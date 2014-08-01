package hu.bme.tmit.hsn.stretch.sim.dist;

public interface EdgeWeigthRandomGenerator {

	/**
	 * Return random value between 0.0 and 1.0 with a specified distribution.
	 * 
	 * @return
	 */
	public abstract double nextEdgeWeight();
}
