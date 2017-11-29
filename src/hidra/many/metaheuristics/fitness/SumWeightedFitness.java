package hidra.many.metaheuristics.fitness;

import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;

public class SumWeightedFitness implements Fitness{

	@Override
	public void fitnessAssign(SolutionSet solutionSet, int numObj) {
		
		double sum = 0.0;
		
		for(int i=0; i < solutionSet.size()  ;i++){
			Solution solution = solutionSet.get(i);
			sum = 0.0;
			for(int j=0; j < numObj  ;j++){
				sum += solution.getObjective(j);
			}
			solution.setFitness(-sum);
		}
		
		
	}

}
