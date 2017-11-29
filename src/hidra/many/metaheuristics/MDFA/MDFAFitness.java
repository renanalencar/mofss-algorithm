package hidra.many.metaheuristics.MDFA;

import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;

public class MDFAFitness {

	
	public void fitnessAssign(SolutionSet solutionSet, double[][] weights) {
		
		
		for(int i=0; i < solutionSet.size() ; i++){
			solutionSet.get(i).setFitness(Double.POSITIVE_INFINITY);
		}
		
		
		
		
		for(int w = 0; w < weights.length; w++){
			
			double minWSum = Double.POSITIVE_INFINITY;
			int indexBest = 0;
			
			for(int i=0; i < solutionSet.size() ; i++){
				double currWSum = wsum( solutionSet.get(i) , weights[w] );
				if( currWSum < minWSum){
					indexBest = i;
				    minWSum = currWSum;
				}
				
			}
			
			double fitness = wsum( solutionSet.get(indexBest) , weights[w] ); 
			if(  fitness < solutionSet.get(indexBest).getFitness()){
				 solutionSet.get(indexBest).setFitness(fitness);
			}
			
		}
		
		
		double worst_stage1 = Double.NEGATIVE_INFINITY;
		
		for(int i=0; i < solutionSet.size();  i++){
			
			
			if( solutionSet.get(i).getFitness() != Double.POSITIVE_INFINITY ){
				worst_stage1 = Math.max(worst_stage1, solutionSet.get(i).getFitness());
			}
			
		}
		
		for(int i=0; i < solutionSet.size();  i++){
						
			if( solutionSet.get(i).getFitness() == Double.POSITIVE_INFINITY ){
				double fitness = getMaxWSum(solutionSet.get(i), weights);
				fitness = fitness + worst_stage1;
				solutionSet.get(i).setFitness(fitness);
			}
			
		}
		
		
	}
	
	
	private double getMaxWSum(Solution x, double[][] weights) {
		
		double maxWSum = Double.NEGATIVE_INFINITY;
		
		for(int w=0; w < weights.length; w++){
			double tempWsum = wsum(x,weights[w]);
			maxWSum = Math.max(maxWSum, tempWsum);
		}
		
		return maxWSum;
	}
	
	
	private double wsum(Solution x, double[] v){
		
		double sum = 0.0;
		
		for(int i=0; i < x.numberOfObjectives();    i++){
			sum += x.getObjective(i) * v[i];
		}
	
		return sum;
	}
	
	
	
	
}
