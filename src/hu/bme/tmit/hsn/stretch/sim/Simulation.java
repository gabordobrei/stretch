package hu.bme.tmit.hsn.stretch.sim;

import hu.bme.tmit.hsn.stretch.MultiplicativeWeigthedStretch;
import hu.bme.tmit.hsn.stretch.Stretch;
import hu.bme.tmit.hsn.stretch.sim.dist.EdgeWeigthRandomGenerator;
import hu.bme.tmit.hsn.stretch.sim.dist.PowerLawDistributionRandomGenerator;
import hu.bme.tmit.hsn.stretch.sim.dist.UniformDistributionRandomGenerator;
import hu.bme.tmit.hsn.stretch.stat.Statistic;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.alg.interfaces.MinimumSpanningTree;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.google.common.collect.Lists;

public class Simulation<V, E> {

	protected WeightedGraph<V, E> graph;
	protected int numberOfSimulation = 100;
	protected int sizeRange = 10;
	protected int sizeBase = 100;
	protected Statistic stat;
	protected Class<? extends V> vertexClass;
	protected Class<? extends E> edgeClass;

	public Simulation() {
		// initGraph(false);
	}

	@SuppressWarnings("unchecked")
	protected void initRandomGraph(boolean usePowerLawDistribution) {

		EdgeWeigthRandomGenerator edgeWeigthDistributionGenerator;
		if (usePowerLawDistribution) {
			edgeWeigthDistributionGenerator = new PowerLawDistributionRandomGenerator();
		} else {
			edgeWeigthDistributionGenerator = new UniformDistributionRandomGenerator();
		}

		int size;
		if (sizeRange == 0) {
			size = sizeBase;
		} else {
			size = sizeBase + (new Random()).nextInt(2 * sizeRange) - sizeRange;
		}

		// Create the graph object; it is null at this point
		graph = new SimpleWeightedGraph<V, E>(edgeClass);

		for (Integer i = 1; i < size + 1; i++) {
			graph.addVertex((V) i);
		}

		double weight;
		for (Integer i = 1; i < size + 1; i++) {
			for (Integer j = 1; j < size + 1; j++) {
				if (i.compareTo(j) != 0) {
					E e = null;
					try {
						e = edgeClass.newInstance();
					} catch (InstantiationException | IllegalAccessException e1) {
						e1.printStackTrace();
					}

					do {
						weight = edgeWeigthDistributionGenerator
								.nextEdgeWeight() * 100;
					} while (weight < 0.1);

					graph.addEdge((V) i, (V) j, e);
					graph.setEdgeWeight(e, weight);
				}
			}
		}
	}

	public void simulate() {
		List<Double> avgStretches = Lists.newLinkedList();

		long totalTime = System.currentTimeMillis();
		for (int k = 0; k < numberOfSimulation; k++) {

			initRandomGraph(false);

			long now = System.currentTimeMillis();

			// a.) the default edgeset is the STP's edgset: a rooted tree,
			// with
			// minimum path from all vertisies to the root (according to
			// Dijkstra's shortest path)

			// Let be the root vertex is which has the minimum avg of it's
			// edgelist's weights.
			// *-
			V rootVertex = graph.vertexSet().iterator().next();

			double rootVertexAvgWeight = Double.NEGATIVE_INFINITY;
			double avgWeight;
			for (V vertex : graph.vertexSet()) {

				avgWeight = 0;
				Set<E> edgeSet = graph.edgesOf(vertex);
				int numberOfNeighbors = edgeSet.size();

				for (E e : edgeSet) {
					avgWeight += graph.getEdgeWeight(e);
				}

				avgWeight /= (double) numberOfNeighbors;

				if (avgWeight > rootVertexAvgWeight) {
					rootVertexAvgWeight = avgWeight;
					rootVertex = vertex;
				}
			}
			// */

			// Get all the shortest path from rootVertex
			Set<E> spanningTreeProtocolEdgeSet = new HashSet<E>();
			DijkstraShortestPath<V, E> dsp;
			for (V endVertex : graph.vertexSet()) {
				dsp = new DijkstraShortestPath<V, E>(graph, rootVertex,
						endVertex);
				spanningTreeProtocolEdgeSet.addAll(dsp.getPathEdgeList());
			}

			// b.) the alternative edgeset is the Kruskal:
			// Get the minimum spanning tree with Kruskal's algo
			MinimumSpanningTree<V, E> kruskalMinimumSpanningTree = new KruskalMinimumSpanningTree<V, E>(
					graph);
			Set<E> kruskalEdgeSet = kruskalMinimumSpanningTree
					.getMinimumSpanningTreeEdgeSet();

			/*-
			for (DefaultWeightedEdge e : graph.edgeSet()) {
				System.err.println(graph.getEdgeSource(e) + " : "
						+ graph.getEdgeTarget(e) + " => "
						+ graph.getEdgeWeight(e));
			}
			// */

			// System.out.println(graph.edgeSet());
			// System.out.println("kruskal: " + kruskalEdgeSet);
			// System.err.println("spt: " + spanningTreeProtocolEdgeSet);

			// Pass the default graph, the SpanningTreeProtocol's edgeset
			// (as
			// defaultEdgeSet) and the Kruskal's algo's edgeset (as
			// alternativeEdgeSet) to see the stretch.

			Stretch<V, E> stretch = new MultiplicativeWeigthedStretch<V, E>(
					(WeightedGraph<V, E>) graph, graph.edgeSet(),
					kruskalEdgeSet /* spanningTreeProtocolEdgeSet */,
					edgeClass);

			stat = stretch.getStatistics();

			avgStretches.add(stat.getAverage());

			// System.err.println("min: " + stat.getMinimum());
			System.err.println("avg: " + stat.getAverage());
			// System.err.println("max: " + stat.getMaximum());

			System.out.println((System.currentTimeMillis() - now) + " ms...");
		}

		System.err.println("-------------");

		Statistic stat = new Statistic(avgStretches);
		if (numberOfSimulation != 1) {
			System.err.println(stat);
		}

		System.err.println("Összesen "
				+ (System.currentTimeMillis() - totalTime) + " ms...");
	}
}
