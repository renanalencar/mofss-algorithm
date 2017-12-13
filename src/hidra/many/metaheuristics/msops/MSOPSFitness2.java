package hidra.many.metaheuristics.msops;


import jmetal.core.Solution;
import jmetal.core.SolutionSet;

import java.util.Collections;
import java.util.Vector;


public class MSOPSFitness2{

	//vector< vector<double> > scores_wminmax; // Score Matrix n x t of Weighted MinMax
	//vector<double> act_score_wminmax;
	//vector< vector<double> > scores_vads; // Score Matrix n x t of VADS
	//vector<double> act_score_vads;
	//vector< vector<int> > fitness; //Dual Optimization Matrix
	//vector< int > act_fitness;
	
	private Vector<Vector<Double>> target_vectors;
	private int targets;
	private boolean dual = true;
	private static final double q = 100;
	
	public void fitnessAssign(SolutionSet solutionSet){
		
		Vector<Vector<Double>> scores_wminmax = new Vector<Vector<Double>>(); // Score Matrix n x t of Weighted MinMax
		Vector<Double> act_score_wminmax = new Vector<Double>();
		Vector<Vector<Double>> scores_vads = new Vector<Vector<Double>>(); // Score Matrix n x t of VADS
		Vector<Double> act_score_vads = new Vector<Double>();
		Vector<Vector<Integer>> fitness = new Vector<Vector<Integer>>();
		Vector<Integer> act_fitness = new Vector<Integer>();
		double value = 0.0;
							
		
		for(int i=0; i < solutionSet.size() ;i++){
			
			act_score_wminmax.clear();			
			if(dual) act_score_vads.clear();					
			act_fitness.clear();
			
			Solution individual = solutionSet.get(i);
			
			for(int j=0; j <  targets ; j++){
				
				value =  weighted_minmax( individual , target_vectors.get(j));
				act_score_wminmax.add(j,value);
				act_fitness.add(1);
				
				if(dual){
					value = vector_angle_distance_scaling( individual, target_vectors.get(j) );
					act_score_vads.add(j,value);
					act_fitness.add(1);
				}
				
			}
			
			scores_wminmax.add(i,act_score_wminmax);
			if(dual) scores_vads.add(i,act_score_vads);
			fitness.add(act_fitness);
			
		}
		
		int factor = 1;
		if(dual) factor++;
		
		
		for(int i=0; i < solutionSet.size() ;i++){
			
			Solution individual = solutionSet.get(i);
			
			for(int j = (i + 1); j < solutionSet.size() ; j++){
								
				for(int k = 0; k < targets; k++)
				{ 				
					if(scores_wminmax.get(i).get(k) > scores_wminmax.get(j).get(k)) 
						// check which score_wminmax is higher and increment rank
					 {
							int temp = fitness.get(i).get(factor*k);
							temp++;
							fitness.get(i).set(factor * k, temp);
							
					 }
					else if(scores_wminmax.get(i).get(k) < scores_wminmax.get(j).get(k)){
						
							int temp = fitness.get(j).get(factor*k);
							temp++;
							fitness.get(j).set(factor * k, temp);
						
					}
					if(dual)
					{
						if(scores_vads.get(i).get(k) > scores_vads.get(j).get(k)) 
						// check which score_vads is higher and increment rank
						{
							int temp = fitness.get(i).get(factor*k+1);
							temp++;
							fitness.get(i).set(factor * k + 1, temp);
							
						}
						else if(scores_vads.get(i).get(k) < scores_vads.get(j).get(k))
						{
							int temp = fitness.get(j).get(factor*k+1);
							temp++;
							fitness.get(j).set(factor * k + 1, temp);
						
						}
					}
															
				}
								
			}
			
			Collections.sort(fitness.get(i));
			
			
			// and write it into the fitness array
			int size_f;
			if(dual)
			{
				size_f = 2 * targets;
			}
			else
			{
				size_f = targets;
			}
			
			Vector<Integer> tempFitness = new Vector<Integer>();
			
			for(int k = 0; k < size_f; k++)
			{
				
				tempFitness.set(k, fitness.get(i).get(k));
				
			}
			
			//((MSOPSSolution)individual).setScores(tempFitness);
			
			
		}							
		
	}
	
	
	private double  vector_angle_distance_scaling(Solution individual,Vector<Double> weights){
		
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
	
	
	
	private double weighted_minmax(Solution individual, Vector<Double> weights)
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













