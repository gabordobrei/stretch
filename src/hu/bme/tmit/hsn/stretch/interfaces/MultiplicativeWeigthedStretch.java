package hu.bme.tmit.hsn.stretch.interfaces;

import hu.bme.tmit.hsn.stretch.Statistic;

import java.util.List;
import java.util.Set;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

/**
 * 
 * @author gabor
 * @param <V>
 * 
 * @param <V>
 * @param <E>
 */

public class MultiplicativeWeigthedStretch<V, E> implements Stretch<V, E> {

	private WeightedGraph<Object, DefaultWeightedEdge> defaultGraph,
			edgeSetGraph;
	private Table<V, V, Double> stretches;
	private final Statistic stat;

	public MultiplicativeWeigthedStretch(WeightedGraph<V, E> vertexSetGraph,
			Set<E> defaultEdgeSet, Set<E> alternativeEdgeSet) {

		this.defaultGraph = new SimpleWeightedGraph<Object, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		this.edgeSetGraph = new SimpleWeightedGraph<Object, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);

		for (V v : vertexSetGraph.vertexSet()) {
			defaultGraph.addVertex(v);
			edgeSetGraph.addVertex(v);
		}

		// 1. Minimum Spanning Tree Graph
		/*-
		minimumSpanningTreeGraph.removeAllEdges(g.edgeSet());

		MinimumSpanningTree<V, E> kruskalMinimumSpanningTree = new KruskalMinimumSpanningTree<V, E>(g);
		Set<E> minimumSpanningTreeEdgeList = kruskalMinimumSpanningTree.getMinimumSpanningTreeEdgeSet();

		for (E e : minimumSpanningTreeEdgeList) {
			minimumSpanningTreeGraph.addEdge(g.getEdgeSource(e), g.getEdgeTarget(e), e);
		}
		// */

		// 1. Default Graph
		for (E e : defaultEdgeSet) {
			defaultGraph.addEdge(vertexSetGraph.getEdgeSource(e),
					vertexSetGraph.getEdgeTarget(e), (DefaultWeightedEdge) e);
		}

		// 2. Edge Set Graph
		for (E e : alternativeEdgeSet) {
			edgeSetGraph.addEdge(vertexSetGraph.getEdgeSource(e),
					vertexSetGraph.getEdgeTarget(e), (DefaultWeightedEdge) e);
		}

		// 3. create stretches
		DijkstraShortestPath<Object, DefaultWeightedEdge> defaultDijkstraShortestPath;
		DijkstraShortestPath<Object, DefaultWeightedEdge> alternativeEdgeSetDijkstraShortestPath;

		List<Double> statList = Lists.newLinkedList();
		stretches = HashBasedTable.create();
		for (V sourceVertex : vertexSetGraph.vertexSet()) {
			for (V targetVertex : vertexSetGraph.vertexSet()) {

				if (!sourceVertex.equals(targetVertex)) {
					// Ha egyik irányban már megvan, akkor nem csináljuk meg
					// megint...
					if (!stretches.contains(targetVertex, sourceVertex)) {

						defaultDijkstraShortestPath = new DijkstraShortestPath<Object, DefaultWeightedEdge>(
								defaultGraph, sourceVertex, targetVertex);

						alternativeEdgeSetDijkstraShortestPath = new DijkstraShortestPath<Object, DefaultWeightedEdge>(
								edgeSetGraph, sourceVertex, targetVertex);

						double defaultPathLength = defaultDijkstraShortestPath
								.getPathLength();
						double alternativePathLength = alternativeEdgeSetDijkstraShortestPath
								.getPathLength();
						// System.err
						// .println(sourceVertex
						// + " -> "
						// + targetVertex
						// + ": "
						// + defaultDijkstraShortestPath
						// .getPathEdgeList());
						// System.err.println(defaultPathLength);
						// System.out.println(alternativePathLength);
						// System.out.println(sourceVertex + " -> " +
						// targetVertex
						// + ": " + alternativePathLength
						// / defaultPathLength);

						stretches.put(sourceVertex, targetVertex,
								(alternativePathLength / defaultPathLength));
						statList.add(alternativePathLength / defaultPathLength);
					} else {
						double d = stretches.get(targetVertex, sourceVertex);
						stretches.put(sourceVertex, targetVertex, d);
					}
				}
			}
		}

		stat = new Statistic(statList);

	}

	@Override
	public Table<V, V, Double> getStretches() {
		return stretches;
	}

	@Override
	public double getStretchBetween(V fromVertex, V toVertex) {
		return stretches.get(fromVertex, toVertex);
	}

	@Override
	public Statistic getStatistics() {
		return stat;
	}

}
