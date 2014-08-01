package hu.bme.tmit.hsn.stretch;

import hu.bme.tmit.hsn.stretch.sim.Simulation;
import hu.bme.tmit.hsn.stretch.sim.StretchDistributionSimulation;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Main {

	public static void main(String[] args) {
		 Simulation<Object, DefaultWeightedEdge> sim = new
		 StretchDistributionSimulation(
		 "d29", "d32", "graph/Abvt.graphml");
//		 Simulation<Object, DefaultWeightedEdge> sim = new
//		 StretchDistributionSimulation(
//		 "d11", "d8", "graph/condensed.graphml");
//		Simulation<Object, DefaultWeightedEdge> sim = new StretchDistributionSimulation(
//				100,
//				//*
//				true
//				/*/
//				false
//				// */
//		);

		sim.simulate();

	}
}
