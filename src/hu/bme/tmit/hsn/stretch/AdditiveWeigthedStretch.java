package hu.bme.tmit.hsn.stretch;

import hu.bme.tmit.hsn.stretch.stat.Statistic;

import java.util.List;
import java.util.Set;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;

/**
 * 
 * @author gabor
 * @param <V>
 * 
 * @param <V>
 * @param <E>
 */

public class AdditiveWeigthedStretch<V, E> extends Stretch<V, E> {

	public AdditiveWeigthedStretch(WeightedGraph<V, E> vertexSetGraph,
			Set<E> defaultEdgeSet, Set<E> alternativeEdgeSet,
			Class<? extends V> vertexClass, Class<? extends E> edgeClass) {

		this.defaultGraph = (WeightedGraph<V, E>) new SimpleWeightedGraph<V, E>(
				edgeClass);
		this.alternativeGraph = (WeightedGraph<V, E>) new SimpleWeightedGraph<V, E>(
				edgeClass);

		for (V v : vertexSetGraph.vertexSet()) {
			defaultGraph.addVertex(v);
			alternativeGraph.addVertex(v);
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
					vertexSetGraph.getEdgeTarget(e), (E) e);
		}

		// 2. Edge Set Graph
		for (E e : alternativeEdgeSet) {
			alternativeGraph.addEdge(vertexSetGraph.getEdgeSource(e),
					vertexSetGraph.getEdgeTarget(e), (E) e);
		}

		// 3. create stretches
		DijkstraShortestPath<V, E> defaultDijkstraShortestPath;
		DijkstraShortestPath<V, E> alternativeDijkstraShortestPath;

		List<Double> statList = Lists.newLinkedList();
		stretches = HashBasedTable.create();
		for (V sourceVertex : vertexSetGraph.vertexSet()) {
			for (V targetVertex : vertexSetGraph.vertexSet()) {

				if (!sourceVertex.equals(targetVertex)) {
					// Ha egyik irányban már megvan, akkor nem csináljuk meg
					// megint...
					if (!stretches.contains(targetVertex, sourceVertex)) {

						defaultDijkstraShortestPath = new DijkstraShortestPath<V, E>(
								defaultGraph, sourceVertex, targetVertex);

						alternativeDijkstraShortestPath = new DijkstraShortestPath<V, E>(
								alternativeGraph, sourceVertex, targetVertex);

						double defaultPathLength = defaultDijkstraShortestPath
								.getPathLength();
						double alternativePathLength = alternativeDijkstraShortestPath
								.getPathLength();
						// System.err
						// .println(sourceVertex
						// + " -> "
						// + targetVertex
						// + ": "
						// + defaultDijkstraShortestPath
						// .getPathEdgeList());
						System.err.println("defaultPathLength: "
								+ defaultPathLength);
						System.out.println("alternativePathLength: "
								+ alternativePathLength);
						System.out.println("arány: " + sourceVertex + " -> "
								+ targetVertex + ": " + alternativePathLength
								/ defaultPathLength);

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

}
