package hu.bme.tmit.hsn.stretch.interfaces;

<<<<<<< HEAD
=======
import hu.bme.tmit.hsn.stretch.Statistic;

>>>>>>> origin/master
import com.google.common.collect.Table;

public interface Stretch<V, E> {
	public Table<V, V, Double> getStretches();

<<<<<<< HEAD
	public double getAverageStretch();

	public double getStretchBetween(V fromVertex, V toVertex);
=======
	public double getStretchBetween(V fromVertex, V toVertex);

	public Statistic getStatistics();

>>>>>>> origin/master
}
