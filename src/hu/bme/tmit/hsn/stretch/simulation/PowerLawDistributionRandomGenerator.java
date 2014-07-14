package hu.bme.tmit.hsn.stretch.simulation;


public class PowerLawDistributionRandomGenerator extends
		AbstractEdgeWeigthRandomGenerator {

	public PowerLawDistributionRandomGenerator() {
		super();
	}

	public double nextEdgeWeight() {
		return nextEdgeWeight(10, 2, 4);
	}

	private double nextEdgeWeight(double n, double x0, double x1) {

		double y = Math.random();
		double X1 = Math.pow(x1, n + 1);
		double X0 = Math.pow(x0, n + 1);
		return Math.pow(((X1 - X0) * y + X0), 1 / (n + 1));

	}
}