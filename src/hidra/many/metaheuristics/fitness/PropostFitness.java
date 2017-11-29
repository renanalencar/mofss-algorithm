package hidra.many.metaheuristics.fitness;

import hidra.core.util.Util;
import hidra.jmetal.core.SolutionSet;
import hidra.many.metaheuristics.smpsocdr.ParetoFrontUtil;

public class PropostFitness implements Fitness{

	@Override
	public void fitnessAssign(SolutionSet solutionSet, int numObj) {
		
		ParetoFrontUtil.crowdingDistanceAssignmentToMOPSOCDR(solutionSet, numObj);
		
		for(int i=0; i < solutionSet.size() ;i++){
			
			double norm = Util.normEucleadian(solutionSet.get(i));
			
			//double fitness = (solutionSet.get(i).getCrowdingDistance() / norm );
			
			solutionSet.get(i).setFitness(1/norm);
			
		}
		
	}

}
