package hidra.many.metaheuristics.smpsoms;


import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import hidra.many.metaheuristics.fitness.Fitness;

import java.util.Arrays;
import java.util.List;


public class MSOPSFitness implements Fitness{


	
	private List<List<Double>> target_vectors;
	private int targets;
	private boolean dual = true;
	private static final double q = 100;
	
	
	
	
	
	public MSOPSFitness(List<List<Double>> target_vectors, int targets,
			boolean dual) {
		super();
		this.target_vectors = target_vectors;
		this.targets = targets;
		this.dual = dual;
	}


	public void fitnessAssign(SolutionSet solutionSet, int numObj){
		
		
		int size = targets;
		
		if(dual) size = 2 * targets;
		
		double[][] scores_wminmax = new double[solutionSet.size()][targets]; // Score Matrix n x t of Weighted MinMax		
		double[][] scores_vads = new double[solutionSet.size()][targets]; // Score Matrix n x t of VADS
		int[][]  fitness = new int[solutionSet.size()][size];
		

		
		
		int factor = 1;
		if(dual) factor++;
		
		
		
		for(int i=0; i < solutionSet.size() ;i++){			
			
			
			Solution individual = solutionSet.get(i);
			
			//System.out.println(target_vectors);
			for(int j=0; j <  targets ; j++){
				
				scores_wminmax[i][j] =  weighted_minmax( individual , target_vectors.get(j));
				fitness[i][factor *j] = 1;
				
				
				
				if(dual){
					scores_vads[i][j] = vector_angle_distance_scaling( individual, target_vectors.get(j) );
					fitness[i][factor * j + 1] = 1;					
				}
				
			}						
			
		}
		
		
		
		for(int i=0; i < solutionSet.size() ;i++){
			
			Solution individual = solutionSet.get(i);
			
			for(int j = (i + 1); j < solutionSet.size() ; j++){
								
				for(int k = 0; k < targets; k++)
				{ 				
					if(scores_wminmax[i][k] > scores_wminmax[j][k]) 
						// check which score_wminmax is higher and increment rank
					 {
							fitness[i][factor*k]++;							
					 }
					else if(scores_wminmax[i][k] < scores_wminmax[j][k]){
						
							fitness[j][factor*k]++;							
					}
					if(dual)
					{
						if(scores_vads[i][k] > scores_vads[j][k]) 
						// check which score_vads is higher and increment rank
						{
							fitness[i][factor*k+1]++;													
							
						}
						else if(scores_vads[i][k] < scores_vads[j][k])
						{
							fitness[j][factor*k+1]++;		
						
						}
					}
															
				}
								
			}
			
			Arrays.sort(fitness[i]);
			
			individual.setScores(fitness[i].clone());
						
			/*// and write it into the fitness array
			int size_f;
			if(dual)
			{
				size_f = 2 * targets;
			}
			else
			{
				size_f = targets;
			}
			*/
			
			
			
			
			
		}
		
		
		//return fitness;
		
	}
	
	
	private double  vector_angle_distance_scaling(Solution individual,List<Double> weights){
		
		double score = 0;
		double weight_magnitude = 0;
		double objectives_magnitude = 0;
		int dim = individual.numberOfObjectives();
		double[] unit_weight      = new double[dim]; 
		double[] unit_objectives  = new double[dim];
		
		// Calculate magnitude of vectors
		for(int i = 0; i < individual.numberOfObjectives(); i++)
		{
			weight_magnitude     = weight_magnitude  + Math.pow(1/weights.get(i), 2);
			objectives_magnitude = objectives_magnitude + Math.pow(individual.getObjective(i),2);
		}
		
		weight_magnitude     = Math.sqrt(weight_magnitude);
		objectives_magnitude = Math.sqrt(objectives_magnitude);
		
		//Normalize vectors and calculate dot product of weight- and objective-vector
		for(int i = 0; i < individual.numberOfObjectives(); i++)
		{
			unit_weight[i] = (1/weights.get(i)) / weight_magnitude;
			unit_objectives[i] = individual.getObjective(i) / objectives_magnitude;
			score = score + unit_weight[i] * unit_objectives[i];
		}
		
		
		//Finally calculate score
		score = objectives_magnitude / Math.pow(score, q);
		
		return score;
		
	}
	
	
	
	private double weighted_minmax(Solution individual, List<Double> weights)
	{
		int i;
		double maxValue = -1;
		double actValue;
		
		for(i = 0; i < individual.numberOfObjectives() ; i++)
		{
			actValue = individual.getObjective(i) * weights.get(i);
			
			if(actValue > maxValue)
			{
				maxValue = actValue;
			}
		}
		return maxValue;

	}
	
}













