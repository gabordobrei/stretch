package hu.bme.tmit.hsn.stretch.sim.dist;

public class PowerLawDistributionRandomGenerator implements
		EdgeWeigthRandomGenerator {

	public PowerLawDistributionRandomGenerator() {
	}

	public double nextEdgeWeight() {
		return nextEdgeWeight(10, 0, 1);
	}

	private double nextEdgeWeight(double n, double x0, double x1) {

		double y = Math.random();
		double X1 = Math.pow(x1, n + 1);
		double X0 = Math.pow(x0, n + 1);
		return 1 - Math.pow(((X1 - X0) * y + X0), 1 / (n + 1));

	}
}