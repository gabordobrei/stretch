package hu.bme.tmit.hsn.stretch.interfaces;

import hu.bme.tmit.hsn.stretch.Statistic;

import com.google.common.collect.Table;

public interface Stretch<V, E> {
	public Table<V, V, Double> getStretches();

	public double getStretchBetween(V fromVertex, V toVertex);

	public Statistic getStatistics();

}
