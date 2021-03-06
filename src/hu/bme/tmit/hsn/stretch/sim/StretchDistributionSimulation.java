package hu.bme.tmit.hsn.stretch.sim;

import hu.bme.tmit.hsn.stretch.MultiplicativeWeigthedStretch;
import hu.bme.tmit.hsn.stretch.Stretch;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.google.common.collect.Lists;

public class StretchDistributionSimulation extends
		Simulation<Object, DefaultWeightedEdge> {

	Map<Integer, Double> stretchDistribution;

	public StretchDistributionSimulation(String latId, String lonId,
			String fileName) {
		stretchDistribution = new TreeMap<Integer, Double>();
		this.vertexClass = Object.class;
		this.edgeClass = DefaultWeightedEdge.class;

		try {
			readGraph(latId, lonId, fileName);
		} catch (XMLStreamException | FileNotFoundException e) {
			e.printStackTrace();
		}

		/*-
		try {
			(GraphJSONExporter.getInstance()).export(graph,
					"graph/generated.condensed.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//*/

	}

	public StretchDistributionSimulation(int initBase,
			boolean usePowerLawDistribution) {
		
		stretchDistribution = new TreeMap<Integer, Double>();
		sizeRange = 0;
		sizeBase = initBase;
		this.vertexClass = Object.class;
		this.edgeClass = DefaultWeightedEdge.class;
		initRandomGraph(usePowerLawDistribution);
	}

	@Override
	public void simulate() {
		
		/**
		 * véletlen egyenletes (n = 1000) gráfon minden csomópontra root: mennyi
		 * a stretch. eloszlás vitzsgálata: x: stretch, y: hány féle rootból
		 * lett ennyi a stretch.
		 */

		// long prev = System.currentTimeMillis();
		Set<DefaultWeightedEdge> spanningTreeProtocolEdgeSet = new HashSet<DefaultWeightedEdge>();
		DijkstraShortestPath<Object, DefaultWeightedEdge> dsp;
		Stretch<Object, DefaultWeightedEdge> stretch;
		/**
		 * Körbe megyünk, mindenki lesz root
		 */
		for (Object rootVertex : graph.vertexSet()) {

			/**
			 * Minden körben megcsináljuk a STP-t
			 */
			spanningTreeProtocolEdgeSet.clear();
			for (Object endVertex : graph.vertexSet()) {
				dsp = new DijkstraShortestPath<Object, DefaultWeightedEdge>(
						graph, rootVertex, endVertex);
				if (dsp.getPathEdgeList() != null) {
					spanningTreeProtocolEdgeSet.addAll(dsp.getPathEdgeList());
				}
			}

			/**
			 * Megnézzük a stretch-et: az eredeti élhalmaz és az STP
			 * különbségét.
			 */
			stretch = new MultiplicativeWeigthedStretch<Object, DefaultWeightedEdge>(
					(WeightedGraph<Object, DefaultWeightedEdge>) graph,
					graph.edgeSet(), spanningTreeProtocolEdgeSet,
					DefaultWeightedEdge.class);

			stat = stretch.getStatistics();

			stretchDistribution.put(((Integer) rootVertex) + 1,
					stat.getAverage());

			/*************************************/

			/*-
			long curr = System.currentTimeMillis();
			System.err.println("If the rootVertex is #"
					+ ((Integer) rootVertex + 1) + ", then the stretch is "
					+ stat.getAverage() + ". (calculated in " + (curr - prev)
					+ " ms.)");
			prev = curr;
			//*/

			System.err.println(stat.getAverage());

		}
	}

	public Map<Integer, Double> getDistribution() {
		return stretchDistribution;
	}

	@SuppressWarnings("unchecked")
	private void readGraph(String latId, String lonId, String fileName)
			throws XMLStreamException, FileNotFoundException {
		final String LatitudeKey = latId;
		final String LongitudeKey = lonId;
		final String XML_FILE = fileName;
		com.tinkerpop.blueprints.pgm.Graph g = new com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph();
		com.tinkerpop.blueprints.pgm.util.graphml.GraphMLReader reader = new com.tinkerpop.blueprints.pgm.util.graphml.GraphMLReader(
				g);

		InputStream is = new BufferedInputStream(new FileInputStream(XML_FILE));
		reader.inputGraph(is);

		Iterator<com.tinkerpop.blueprints.pgm.Vertex> verticesIterator = g
				.getVertices().iterator();
		Iterator<com.tinkerpop.blueprints.pgm.Edge> edgesIterator = g
				.getEdges().iterator();

		int size = 0;
		while (verticesIterator.hasNext()) {
			verticesIterator.next();
			size++;
		}

		// Create the graph object; it is null at this point
		graph = new SimpleWeightedGraph<Object, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);

		for (Integer i = 0; i < size; i++) {
			graph.addVertex(i);
		}

		final int R = 6371;
		// Set all the weights
		while (edgesIterator.hasNext()) {
			com.tinkerpop.blueprints.pgm.Edge e = edgesIterator.next();
			com.tinkerpop.blueprints.pgm.Vertex vIn = e.getInVertex();
			com.tinkerpop.blueprints.pgm.Vertex vOut = e.getOutVertex();

			if (vIn.getProperty(LatitudeKey) != null
					&& vIn.getProperty(LongitudeKey) != null) {

				if (vOut.getProperty(LatitudeKey) != null
						&& vOut.getProperty(LongitudeKey) != null) {

					Double lat1 = Double.parseDouble((String) vIn
							.getProperty(LatitudeKey));
					Double lon1 = Double.parseDouble((String) vIn
							.getProperty(LongitudeKey));
					Double lat2 = Double.parseDouble((String) vOut
							.getProperty(LatitudeKey));
					Double lon2 = Double.parseDouble((String) vOut
							.getProperty(LongitudeKey));

					Double latDistance = (lat2 - lat1) * Math.PI / 180;
					Double lonDistance = (lon2 - lon1) * Math.PI / 180;
					Double a = Math.sin(latDistance / 2)
							* Math.sin(latDistance / 2)
							+ Math.cos((lat1) * Math.PI / 180)
							* Math.cos((lat2) * Math.PI / 180)
							* Math.sin(lonDistance / 2)
							* Math.sin(lonDistance / 2);
					Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
					Double distance = R * c;

					Double width = 1.0;
					try {
						width = Double.parseDouble((String) e
								.getProperty("d20")) / 1000000000.0;
					} catch (Exception e1) {
					}

					Integer v1 = Integer.parseInt((String) vIn.getId());
					Integer v2 = Integer.parseInt((String) vOut.getId());
					graph.addEdge(v1, v2);
					((WeightedGraph<Object, DefaultWeightedEdge>) graph)
							.setEdgeWeight(graph.getEdge(v1, v2), distance
									* width);
					// System.err.println(v1 + " --"
					// + graph.getEdgeWeight(graph.getEdge(v2, v1))
					// + "--> " + v2);

				} else {
					graph.removeVertex(Integer.parseInt((String) vOut.getId()));
				}
			} else {
				graph.removeVertex(Integer.parseInt((String) vIn.getId()));
			}
		}

		List<DefaultWeightedEdge> removeableEdge = Lists.newLinkedList();
		for (DefaultWeightedEdge e : graph.edgeSet()) {
			if (graph.getEdgeWeight(e) < 1) {
				removeableEdge.add(e);
			}
		}
		graph.removeAllEdges(removeableEdge);

		List<Object> removeableVertex = Lists.newLinkedList();
		for (Object v : graph.vertexSet()) {
			if (((AbstractBaseGraph<Object, DefaultWeightedEdge>) graph).degreeOf(v) == 0) {
				removeableVertex.add(v);
			}
		}
		graph.removeAllVertices(removeableVertex);

	}
}
