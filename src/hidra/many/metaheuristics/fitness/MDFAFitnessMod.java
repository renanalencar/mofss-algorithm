package hidra.many.metaheuristics.fitness;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;

public class MDFAFitnessMod implements Fitness{


	private double[][] weights;
	private static double q = 1; 
	

	public MDFAFitnessMod(double[][] weights) {
		this.weights = weights;
	}


	@Override
	public void fitnessAssign(SolutionSet solutionSet, int numObj) {


		for(int i=0; i < solutionSet.size() ; i++){
			solutionSet.get(i).setFitness(Double.POSITIVE_INFINITY);
		}




		for(int w = 0; w < weights.length; w++){

			double minWSum = Double.POSITIVE_INFINITY;
			int indexBest = 0;

			for(int i=0; i < solutionSet.size() ; i++){
				double currWSum = getVDS( solutionSet.get(i) , weights[w] );
				if( currWSum < minWSum){
					indexBest = i;
					minWSum = currWSum;
				}

			}

			double fitness = getVDS( solutionSet.get(indexBest) , weights[w] ); 
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
				double fitness = getMaxVDS(solutionSet.get(i), weights);
				fitness = fitness + worst_stage1;
				solutionSet.get(i).setFitness(fitness);
			}

		}

		for(int i=0; i < solutionSet.size();  i++){
			double fitness = solutionSet.get(i).getFitness();
			solutionSet.get(i).setFitness( -1 *  fitness);
		}
		

	}


	private double getMaxVDS(Solution x, double[][] weights) {

		double maxVDS = Double.NEGATIVE_INFINITY;

		for(int w=0; w < weights.length; w++){
			double tempWsum = getVDS(x,weights[w]);
			maxVDS = Math.max(maxVDS, tempWsum);
		}

		return maxVDS;
	}

	
	
	

	private double getVDS(Solution x, double[] weight){

		double weight_magnitude = 0.0;
		double objectives_magnitude = 0.0;
		int dim = x.numberOfObjectives();
		double[] unit_weight      = new double[dim]; 
		double[] unit_objectives  = new double[dim];
			
		// Calculate magnitude of vectors
		for(int i = 0; i < dim ; i++)
		{
			weight_magnitude     = weight_magnitude     + Math.pow(1/weight[i], 2);
			objectives_magnitude = objectives_magnitude + Math.pow(x.getObjective(i),2);
		}
				
	
		weight_magnitude     = Math.sqrt(weight_magnitude);
		objectives_magnitude = Math.sqrt(objectives_magnitude);
	    double score = 0.0;

		//Normalize vectors and calculate dot product of weight- and objective-vector
		for(int i = 0; i < x.numberOfObjectives(); i++)
		{
			unit_weight[i] = (1/weight[i]) / weight_magnitude;
			unit_objectives[i] = x.getObjective(i) / objectives_magnitude;
			score = score + unit_weight[i] * unit_objectives[i];
		}

		score = score + 0.000000001;

		//Finally calculate score
		score = objectives_magnitude / Math.pow(score, q);
		return score;
	
	}







}
