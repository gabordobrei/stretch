package hu.bme.tmit.hsn.stretch.interfaces;

import com.google.common.collect.Table;

public interface Stretch<V, E> {
	public Table<V, V, Double> getStretches();

	public double getAverageStretch();

	public double getStretchBetween(V fromVertex, V toVertex);
}
