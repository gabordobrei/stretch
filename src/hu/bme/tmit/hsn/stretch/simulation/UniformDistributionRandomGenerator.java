package hu.bme.tmit.hsn.stretch.simulation;


public class UniformDistributionRandomGenerator extends AbstractEdgeWeigthRandomGenerator {

	public UniformDistributionRandomGenerator() {
		super();
	}

	public double nextEdgeWeight() {
		return Math.random();
	}
}
