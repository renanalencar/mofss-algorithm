package hidra.many.metaheuristics.fitness;

import hidra.core.util.Util;
import jmetal.core.SolutionSet;
import hidra.many.metaheuristics.comparators.CrowdingDistanceComparator;
import hidra.many.metaheuristics.comparators.FitnessComparator;

public class GDCDRFitness implements Fitness{

	@Override
	public void fitnessAssign(SolutionSet solutionSet, int numObj) {
		
		//Crowding the leaders_
		//SolutionSet copy1 = Util.copySolutionSet(solutionSet);
		GDFitness fitness = new GDFitness();
		fitness.fitnessAssign(solutionSet, numObj);

		//SolutionSet copy2 = Util.copySolutionSet(solutionSet);
		CrowdingDistanceFitness crowdingDistanceFitness  = new CrowdingDistanceFitness();
		crowdingDistanceFitness.fitnessAssign(solutionSet, numObj);
		
		if(solutionSet.size()  == 1){
			solutionSet.get(0).setFitness(0.0);
		}
		
		SolutionSet copy1 = Util.copySolutionSet(solutionSet);
		SolutionSet copy2 = Util.copySolutionSet(solutionSet);
		copy1.sort(new FitnessComparator());
		copy2.sort(new CrowdingDistanceComparator());
		
		double lowerGDFitness = copy1.get(0).getFitness();
		double lowerCDFItness = copy2.get(2).getCrowdingDistance();
		
		for(int i=0; i < solutionSet.size() ;i++){
			double fitness1 = solutionSet.get(i).getFitness();
			double fitness2 = solutionSet.get(i).getCrowdingDistance();
			
			
			fitness1 = fitness1 / lowerGDFitness;
			fitness2 = fitness2 / lowerCDFItness;
			
			solutionSet.get(i).setFitness(fitness2);
		}
		
		
	}

}
