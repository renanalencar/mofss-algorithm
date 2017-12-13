package hidra.many.metaheuristics.cega;


import jmetal.core.Solution;
import jmetal.core.SolutionSet;

public class CEGAFitness {

	
	
	public void fitnessAssign(SolutionSet solutionSet) {
			
		for(int i=0; i < solutionSet.size() ;  i++){
			
			Solution XI = solutionSet.get(i);
			double sum = 0.0;
			
			for(int j=0; j < solutionSet.size() ;j++){
				
				Solution XJ = solutionSet.get(j);
				
				if(i != j){
					
					for(int k=0; k < XI.numberOfObjectives() ; k++){
						sum += Math.max(XI.getObjective(k)  - XJ.getObjective(k),0);
					}
				}
				
			}
			
			XI.setFitness(sum);
			
		}
		
		
		
	}
	
	

}
