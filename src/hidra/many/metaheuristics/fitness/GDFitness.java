package hidra.many.metaheuristics.fitness;

import hidra.core.util.HIDRADistanceUtil;
import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;

public class GDFitness implements Fitness{


	/*public void fitnessAssign(SolutionSet solutionSet, int numObj) {


		int size = solutionSet.size();        

		if (size == 0)
			return;

		if (size == 1) {
			solutionSet.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
			return;
		} // if

		// Take extreme values to normalize
		double[] maximumValues = HIDRADistanceUtil.getMaximumValues(solutionSet, numObj);
		double[] minimumValues = HIDRADistanceUtil.getMinimumValues(solutionSet, numObj);
		double distance = 0.0;
		
		for(int i=0; i < solutionSet.size() ;  i++){

			Solution XI = solutionSet.get(i);
			double sum = 0.0;

			for(int j=0; j < solutionSet.size() ;j++){

				Solution XJ = solutionSet.get(j);

				if(i != j){

					for(int k=0; k < numObj ; k++){
						
						if( (maximumValues[k]-minimumValues[k]) != 0.0){
							distance = (XI.getObjective(k)  - XJ.getObjective(k)) / (maximumValues[k]-minimumValues[k]) ;							
							sum += Math.max(distance,0);
						}
						
						
					}
				}

			}

			double fitness = 100.0/sum;
			XI.setFitness(fitness);

		}			

	}
*/
	
	
	public double  globalDetritment(Solution XI, Solution XJ,  int numObj){
		double sum = 0.0;
		
		for(int k=0; k < numObj ; k++){
			sum += Math.max(XI.getObjective(k)  - XJ.getObjective(k),0);
		}
							
		return sum;
	}
	
	
	public void fitnessAssign(SolutionSet solutionSet, int numObj) {
		
		for(int i=0; i < solutionSet.size() ;  i++){
			
			Solution XI = solutionSet.get(i);
			double sum = 0.0;
			
			for(int j=0; j < solutionSet.size() ;j++){
				
				Solution XJ = solutionSet.get(j);
				
				if(i != j){
					
					for(int k=0; k < numObj ; k++){
						sum += Math.max(XI.getObjective(k)  - XJ.getObjective(k),0);
					}
				}
				
			}
			
			double fitness = 1000.0/sum;			
			XI.setFitness(fitness);
			
		}			
	
	}
	
}
