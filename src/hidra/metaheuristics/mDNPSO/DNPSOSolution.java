package hidra.metaheuristics.mDNPSO;

import jmetal.core.Problem;
import jmetal.core.Solution;

public class DNPSOSolution extends Solution{
	
	private double distanceFromNeighbor;
	
	public DNPSOSolution(Problem problem_) throws ClassNotFoundException {
		super(problem_);
	}

	public DNPSOSolution(DNPSOSolution solution) {
		super(solution);
		this.distanceFromNeighbor = solution.getDistanceFromNeighbor();
	}

	public void setDistanceFromNeighbor(double distanceFromNeighbor) {
		this.distanceFromNeighbor = distanceFromNeighbor;
	}
	
	 public double getDistanceFromNeighbor() {
	        return distanceFromNeighbor;
	 }

	
}
