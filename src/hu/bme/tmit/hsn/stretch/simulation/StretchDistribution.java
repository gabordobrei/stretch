package hu.bme.tmit.hsn.stretch.simulation;

import hu.bme.tmit.hsn.stretch.interfaces.MultiplicativeWeigthedStretch;
import hu.bme.tmit.hsn.stretch.interfaces.Stretch;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

public class StretchDistribution extends DefaultSimulation {

	Map<Integer, Double> stretchDistribution;

	public StretchDistribution(int initBase, boolean useRandomGraph) {
		stretchDistribution = new TreeMap<Integer, Double>();
		sizeRange = 0;
		sizeBase = initBase;
		if (useRandomGraph) {
			initGraph();
		} else {
			readGraph();
		}
	}

	public void simulate() {
		/**
		 * véletlen egyenletes (n = 1000) gráfon minden csomópontra root: mennyi
		 * a stretch. eloszlás vitzsgálata: x: stretch, y: hány féle rootból
		 * lett ennyi a stretch.
		 */

		long prev = System.currentTimeMillis();
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
				spanningTreeProtocolEdgeSet.addAll(dsp.getPathEdgeList());
			}

			/**
			 * Megnézzük a stretch-et: az eredeti élhalmaz és az STP
			 * különbségét.
			 */
			stretch = new MultiplicativeWeigthedStretch<Object, DefaultWeightedEdge>(
					graph, graph.edgeSet(), spanningTreeProtocolEdgeSet);

			stat = stretch.getStatistics();
			stretchDistribution.put(((Integer) rootVertex) + 1,
					stat.getAverage());
			/*************************************/

			long curr = System.currentTimeMillis();
			System.err.println(((Integer) rootVertex + 1) + ": "
					+ (curr - prev) + ";" + stat.getAverage());
			prev = curr;
		}
	}

	public Map<Integer, Double> getDistribution() {
		return stretchDistribution;
	}

	private void readGraph() {

	}
}
