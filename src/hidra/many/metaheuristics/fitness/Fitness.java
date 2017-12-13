package hidra.many.metaheuristics.fitness;

import jmetal.core.SolutionSet;

public interface Fitness {

	public void fitnessAssign(SolutionSet solutionSet, int numObj);
	
}
