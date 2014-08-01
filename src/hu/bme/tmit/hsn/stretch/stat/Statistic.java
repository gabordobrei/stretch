package hu.bme.tmit.hsn.stretch.stat;

import java.util.Collection;

public class Statistic {

	private final double average;
	private final double minimum;
	private final double maximum;

	public Statistic(Collection<Double> statList) {
		double sum = 0;
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;

		if (!statList.isEmpty()) {
			for (Double d : statList) {
				sum += d;
				if (d < min) {
					min = d;
				}
				if (d > max) {
					max = d;
				}
			}
		}

		average = sum / (double) statList.size();
		minimum = min;
		maximum = max;

	}

	public double getAverage() {
		return average;
	}

	public double getMinimum() {
		return minimum;
	}

	public double getMaximum() {
		return maximum;
	}

	@Override
	public String toString() {
		return "Teljes statisztika: [ átlag= " + average + ", minimum= "
				+ minimum + ", maximum= " + maximum + " ]";
	}

}
