package hu.bme.tmit.hsn.stretch.simulation;

public abstract class AbstractEdgeWeigthRandomGenerator {

	/**
	 * Return random value between 0.0 and 1.0 with a specified distribution.
	 * 
	 * @return
	 */
	public abstract double nextEdgeWeight();
}
