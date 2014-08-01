package hu.bme.tmit.hsn.stretch.sim.dist;

public class UniformDistributionRandomGenerator implements
		EdgeWeigthRandomGenerator {

	public UniformDistributionRandomGenerator() {
		super();
	}

	public double nextEdgeWeight() {
		return Math.random();
	}
}
