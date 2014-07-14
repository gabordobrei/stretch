package hu.bme.tmit.hsn.stretch;

import hu.bme.tmit.hsn.stretch.interfaces.Simulation;
import hu.bme.tmit.hsn.stretch.simulation.StretchDistribution;

public class Main {

	public static void main(String[] args) {
		Simulation sim = new StretchDistribution(100, true);
		sim.simulate();

		/*-
		Map<Integer, Double> dist = ((StretchDistribution) sim)
				.getDistribution();

		// Statistic stat = new Statistic(dist.values());
		// final int leptek = 20;
		// int[] histogram = new int[leptek];
		// double step = (stat.getMaximum() - stat.getMinimum()) / (double)
		// leptek;

		for (Map.Entry<Integer, Double> distEntry : dist.entrySet()) {

			// int idx = (int) ((distEntry.getValue() - stat.getMinimum()) /
			// step);
			// histogram[Math.max(0, Math.min(idx, leptek - 1))]++;

			// System.out.println(distEntry.getValue() + ";" +
			// distEntry.getKey());
			System.out.println(distEntry.getValue());
		}
		 */
	}
}
