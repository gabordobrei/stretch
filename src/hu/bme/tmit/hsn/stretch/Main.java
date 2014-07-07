package hu.bme.tmit.hsn.stretch;

import hu.bme.tmit.hsn.stretch.interfaces.AdditiveWeigthedStretch;
import hu.bme.tmit.hsn.stretch.interfaces.Stretch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.VertexFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.alg.interfaces.MinimumSpanningTree;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.ClassBasedVertexFactory;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class Main {

	private static WeightedGraph<Object, DefaultWeightedEdge> graph;
	private static int numberOfSimulation = 1;
	private static int sizeRange = 0;
	private static int sizeBase = 10;

	public static void main(String[] args) {

		double avgStretches = 0;

		long totalTime = System.currentTimeMillis();
		for (int k = 0; k < numberOfSimulation; k++) {

			int size = sizeBase
					+ (int) Math.round(Math.random() * 2 * sizeRange
							- sizeRange);
			System.out.println(k + 1 + ".\t" + size + " csomópontból");
			long now = System.currentTimeMillis();

			// Create the graph object; it is null at this point
			graph = new SimpleWeightedGraph<Object, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);

			// Create the CompleteGraphGenerator object
			CompleteGraphGenerator<Object, DefaultWeightedEdge> completeGenerator = new CompleteGraphGenerator<Object, DefaultWeightedEdge>(
					size);

			// Create the VertexFactory so the generator can create vertices
			VertexFactory<Object> vFactory = new ClassBasedVertexFactory<Object>(
					Object.class);

			// Use the CompleteGraphGenerator object to make completeGraph a
			// complete graph with [size] number of vertices
			completeGenerator.generateGraph(graph, vFactory, null);

			// Now, replace all the vertices with sequential numbers so we can
			// ID them
			Set<Object> vertices = new HashSet<Object>();
			vertices.addAll(graph.vertexSet());
			Integer counter = 0;
			for (Object vertex : vertices) {
				replaceVertex(vertex, (Object) counter++);
			}

			// Set all the weights
			Iterator<DefaultWeightedEdge> dIterator = graph.edgeSet()
					.iterator();
			while (dIterator.hasNext()) {
				DefaultWeightedEdge e = dIterator.next();
				double weight = Math.rint(Math.random() * 100);
				graph.setEdgeWeight(e, weight);
			}

			/**
			 * --- We've got a Complete Weighted Graph of size @size ---
			 */

			// a.) the default edgeset is the STP's edgset: a rooted tree, with
			// minimum path from all vertisies to the root (according to
			// Dijkstra's shortest path)

			// Let be the root vertex is which has the minimum avg of it's
			// edgelist's weights.
			Object rootVertex = graph.vertexSet().iterator().next();

			double rootVertexAvgWeight = Double.NEGATIVE_INFINITY;
			double avgWeight;
			for (Object vertex : graph.vertexSet()) {

				avgWeight = 0;
				Set<DefaultWeightedEdge> edgeSet = graph.edgesOf(vertex);
				int numberOfNeighbors = edgeSet.size();

				for (DefaultWeightedEdge e : edgeSet) {
					avgWeight += graph.getEdgeWeight(e);
				}

				avgWeight /= (double) numberOfNeighbors;

				if (avgWeight > rootVertexAvgWeight) {
					rootVertexAvgWeight = avgWeight;
					rootVertex = vertex;
				}
			}

			// Get all the shortest path from rootVertex
			Set<DefaultWeightedEdge> spanningTreeProtocolEdgeSet = new HashSet<DefaultWeightedEdge>();
			DijkstraShortestPath<Object, DefaultWeightedEdge> dsp;
			for (Object endVertex : graph.vertexSet()) {
				dsp = new DijkstraShortestPath<Object, DefaultWeightedEdge>(
						graph, rootVertex, endVertex);
				spanningTreeProtocolEdgeSet.addAll(dsp.getPathEdgeList());
			}

			// b.) the alternative edgeset is the Kruskal:
			// Get the minimum spanning tree with Kruskal's algo
			MinimumSpanningTree<Object, DefaultWeightedEdge> kruskalMinimumSpanningTree = new KruskalMinimumSpanningTree<Object, DefaultWeightedEdge>(
					graph);
			Set<DefaultWeightedEdge> kruskalEdgeSet = kruskalMinimumSpanningTree
					.getMinimumSpanningTreeEdgeSet();

			System.out.println(graph.edgeSet());
			System.out.println(kruskalEdgeSet);
			System.err.println(spanningTreeProtocolEdgeSet);

			// Pass the default graph, the SpanningTreeProtocol's edgeset (as
			// defaultEdgeSet) and the Kruskal's algo's edgeset (as
			// alternativeEdgeSet) to see the stretch.
			Stretch<Object, DefaultWeightedEdge> stretch = new AdditiveWeigthedStretch<Object, DefaultWeightedEdge>(
					graph, spanningTreeProtocolEdgeSet, kruskalEdgeSet);

			System.err.println(stretch.getAverageStretch());
			avgStretches += stretch.getAverageStretch();

			/*- Print out the graph to be sure it's really complete
			Iterator<Object> iter = new DepthFirstIterator<Object, DefaultWeightedEdge>(graph);
			Object vertex;
			while (iter.hasNext()) {
				vertex = iter.next();

				System.out.println("Vertex " + vertex.toString() + " is connected to: ");
				for (DefaultWeightedEdge e : graph.edgesOf(vertex)) {

					System.out.print(e.toString() + " - " + graph.getEdgeWeight(e));

				}
				System.out.println();
			}
			// */

			System.out.println((System.currentTimeMillis() - now) + " ms...");
		}

		System.err.println("-------------");

		if (numberOfSimulation != 1) {
			System.out.println(avgStretches / (double) numberOfSimulation);
		}

		System.err.println("Összesen "
				+ (System.currentTimeMillis() - totalTime) + " ms...");
	}

	public static boolean replaceVertex(Object vertex, Object counter) {
		if ((vertex == null) || (counter == null)) {
			return false;
		}

		Set<DefaultWeightedEdge> relatedEdges = graph.edgesOf(vertex);
		graph.addVertex(counter);

		Object sourceVertex;
		Object targetVertex;
		for (DefaultWeightedEdge e : relatedEdges) {
			sourceVertex = graph.getEdgeSource(e);
			targetVertex = graph.getEdgeTarget(e);
			if (sourceVertex.equals(vertex) && targetVertex.equals(vertex)) {
				graph.addEdge(counter, counter);
			} else {
				if (sourceVertex.equals(vertex)) {
					graph.addEdge(counter, targetVertex);
				} else {
					graph.addEdge(sourceVertex, counter);
				}
			}
		}
		graph.removeVertex(vertex);
		return true;
	}

}