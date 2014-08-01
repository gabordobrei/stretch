package hu.bme.tmit.hsn.stretch;

import hu.bme.tmit.hsn.stretch.stat.Statistic;

import org.jgrapht.WeightedGraph;

import com.google.common.collect.Table;

public abstract class Stretch<V, E> {

	protected WeightedGraph<V, E> defaultGraph;
	protected WeightedGraph<V, E> alternativeGraph;
	protected Table<V, V, Double> stretches;
	protected Statistic stat;
	protected Class<?extends V> vertexClass;
	protected Class<?extends E> edgeClass;

	public Table<V, V, Double> getStretches() {
		return stretches;
	}

	public double getStretchBetween(V fromVertex, V toVertex) {
		return stretches.get(fromVertex, toVertex);
	}

	public Statistic getStatistics() {
		return stat;
	}
	
}
