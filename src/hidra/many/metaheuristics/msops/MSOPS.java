package hidra.many.metaheuristics.msops;

import hidra.core.population.HIDRAPopulationAlgorithm;
import hidra.core.util.Util;
import hidra.jmetal.core.Operator;
import hidra.jmetal.core.Problem;
import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;
import hidra.many.metaheuristics.cega.CEGAComparator;
import hidra.many.metaheuristics.cega.CEGAFitness;
import hidra.many.metaheuristics.cega.Cluster;
import hidra.many.metaheuristics.cega.Clustering;
import hidra.many.metaheuristics.cega.NormComparator;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import jmetal.operators.selection.BinaryTournament;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.comparators.SolutionComparator;

public class MSOPS extends HIDRAPopulationAlgorithm{
	
	int populationSize;
	SolutionSet population;
	
	Operator mutationOperator;
	Operator crossoverOperator;
	Operator selectionOperator;

	Distance distance = new Distance();
	SolutionComparator equalSolutions = new SolutionComparator();
	
	
	private boolean dual = true;
	private int numTargets;
	private List<List<Double>> weights;
	
	
	public MSOPS(Problem problem) {
		super(problem);
		
		// TODO Auto-generated constructor stub
	}

	
	
	
	private void initializeWeights(){
		
		this.weights = new Vector<List<Double>>();
		this.numTargets = 100;
		int size = numTargets;
		
		/*if(dual){
			size = 2 * numTargets;
		}
		*/
		for(int i=0; i <  size ;i++){
			
			Vector<Double> temp = new Vector<Double>();
			
			for(int j=0; j < problem_.getNumberOfObjectives() ; j++){
				
				double value = Math.random();
				temp.add(value);
				
			}
			
			
			/*double sum = 0.0;
			
			for(int j=0; j < problem_.getNumberOfObjectives() ; j++){				
				sum += temp.get(j);				
			}
			
			for(int j=0; j < problem_.getNumberOfObjectives() ; j++){
				double value = temp.get(j);
				value = value/sum;
				temp.set(j, value);
			}
			*/
			this.weights.add(temp);
		}
		
		
	}
	
	
	
	//Its necessary initialize 	this.numTargets 
	
	private void initializeWeights2(String path){
		
		
		this.weights = new Vector<List<Double>>();
		
		double[][] weights =  Util.readMatrix(path); 
		
		this.numTargets = weights.length;
		
		for(int i=0; i <  weights.length ;i++){
			
			Vector<Double> temp = new Vector<Double>();
			
			for(int j=0; j < problem_.getNumberOfObjectives() ; j++){
				
				double value = weights[i][j];
				temp.add(value);
				
			}
			
			
			this.weights.add(temp);
		}
		
		
	}
	
	
	
	
	@Override
	protected SolutionSet initializationAlgorithm() throws ClassNotFoundException,JMException {
		initParams();
		// Create the initial solutionSet
		Solution newSolution;
		for (int i = 0; i < populationSize; i++) {
			newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);		
			population.add(newSolution);
		} //for       		
		
		
		//Assign crowding distance to individuals
	    MSOPSFitness fitness = new MSOPSFitness(this.weights,numTargets,dual);
		fitness.fitnessAssign(population);
							
		return population;
	}

	
	protected void initParams() {
		super.initParams();
		//Read the parameters
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
		//maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
		//dual = ((Integer) getInputParameter("dual")).intValue();
		//t = ((Integer) getInputParameter("maxIterations")).intValue();
		
		//iteration_ = 0;
		
		//Initialize the variables
		population = new SolutionSet(populationSize);
		//evaluations = 0;

		//requiredEvaluations = 0;
		this.weights = new Vector<List<Double>>();
		//initializeWeights();
		String path  = "resource/msops/weights_" + problem_.getNumberOfObjectives() + ".txt";
		//initializeWeights2(path);
		initializeWeights();
		
		//Read the operators
		mutationOperator = operators_.get("mutation");
		crossoverOperator = operators_.get("crossover");
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("comparator", new MSOPSComparator(dual,numTargets));
	    selectionOperator = new BinaryTournament(parameters);
		
		
	}

	@Override
	protected SolutionSet runIteration() throws JMException {
		
		SolutionSet offspringPopulation;
		SolutionSet union;

						
		// Create the offSpring solutionSet      
		offspringPopulation = new SolutionSet(populationSize);
		Solution[] parents = new Solution[2];
		for (int i = 0; i < (populationSize / 2); i++) {
			
			//obtain parents
			parents[0] = (Solution) selectionOperator.execute(population);
			parents[1] = (Solution) selectionOperator.execute(population);


			if(equalSolutions.compare(parents[0], parents[1]) == 0){				
				parents[1] = (Solution) selectionOperator.execute(population);
			}


			Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
			mutationOperator.execute(offSpring[0]);
			mutationOperator.execute(offSpring[1]);
			problem_.evaluate(offSpring[0]);
			problem_.evaluateConstraints(offSpring[0]);
			problem_.evaluate(offSpring[1]);
			problem_.evaluateConstraints(offSpring[1]);
			offspringPopulation.add(offSpring[0]);
			offspringPopulation.add(offSpring[1]);
			//evaluations += 2;
			                            
		} // for

		// Create the solutionSet union of solutionSet and offSpring
		union =  population.union(offspringPopulation);

	
		//Assign crowding distance to individuals
		MSOPSFitness fitness = new MSOPSFitness(this.weights,numTargets,dual);
//		System.out.println("before");
		fitness.fitnessAssign(union);
		//System.out.println("after");		
		
		population.clear();
							
		//Environmental Selection
		union.sort(new MSOPSComparator(dual,numTargets));		
		
		int remain = populationSize; 
		
		
		while (  remain > 0  ) {
			//System.out.println("true"); 
			population.add(  union.get(0)  );
			union.remove(0);
			remain--;
			
		} // while

				
		iteration_++;
		//System.out.println(iteration_);
					
		//Ranking ranking = new Ranking(population);
		
		//return ranking.getSubfront(0);

		return population;
	}
	
	
	
	
	

	@Override
	protected SolutionSet getParetoFront() {		
		//Ranking ranking = new Ranking(population);
		//return ranking.getSubfront(0);
		return population;
	}
	
	
	
	
	
}
