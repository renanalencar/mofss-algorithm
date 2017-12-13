package hidra.many.metaheuristics.MDFA;

import hidra.core.population.HIDRAPopulationAlgorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;

import java.util.HashMap;

import jmetal.operators.selection.BinaryTournament;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.SolutionComparator;

public class MDFA extends HIDRAPopulationAlgorithm{

	int populationSize;
	//int maxEvaluations;
	//int evaluations;

	//QualityIndicator indicators; // QualityIndicator object
	//int requiredEvaluations; // Use in the example of use of the
	// indicators object (see below)

	SolutionSet population;
	//SolutionSet offspringPopulation;
	//SolutionSet union;

	Operator mutationOperator;
	Operator crossoverOperator;
	Operator selectionOperator;

	Distance distance = new Distance();
	
	
	SolutionComparator equalSolutions = new SolutionComparator();
	
	double[][] weights;
	

	public MDFA(Problem problem) {
		super(problem);					
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
			//evaluations++;
			population.add(newSolution);
		} //for
		
		
		generateWeights();
		
		//Assign crowding distance to individuals
		MDFAFitness fitness = new MDFAFitness();
		fitness.fitnessAssign(population,weights);
				
		
		return population;
	}

	
	private void generateWeights(){
		
		for(int i=0; i  < populationSize ; i++){
			
			for(int j = 0; j < problem_.getNumberOfObjectives(); j++){
				
				weights[i][j] = 0.25 + (0.75) * Math.random();
				
			}
			
		}
		
	}
	
	
	protected void initParams() {
		//Read the parameters
		super.initParams();
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
		//maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();			
		//iteration_ = 0;
		
		//Initialize the variables
		population = new SolutionSet(populationSize);
		
		//evaluations = 0;
		//requiredEvaluations = 0;

		//Read the operators
		mutationOperator = operators_.get("mutation");
		crossoverOperator = operators_.get("crossover");
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("comparator", new MDFAFitnessComparator());
	    selectionOperator = new BinaryTournament(parameters);
		// TODO Auto-generated constructor stub
		
		weights = new double[populationSize][problem_.getNumberOfObjectives()];
		//selectionOperator = operators_.get("selection");
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
				
				int count = 0;
				
				while(equalSolutions.compare(parents[0], parents[1]) == 0 && count < 100){				
					parents[1] = (Solution) selectionOperator.execute(population);
					count++;
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
		union = ((SolutionSet) population).union(offspringPopulation);

	
		//Assign crowding distance to individuals
		MDFAFitness fitness = new MDFAFitness();
	    fitness.fitnessAssign(union,weights);
				
		
		
		population.clear();
		
		union.sort( new MDFAFitnessComparator());
		
		int remain = populationSize;
		
		while(remain > 0){			
			population.add(union.get(0));
			union.remove(0);
			remain--;
		}
		
		
		iteration_++;
		//System.out.println(iteration_);
					
		//Ranking ranking = new Ranking(population);
		
		//return ranking.getSubfront(0);

		return population;
	}
	
	
	
	
	
	

	@Override
	protected SolutionSet getParetoFront() {		
		Ranking ranking = new Ranking(population);
	    return ranking.getSubfront(0);
		//return population;
	}
	
	
	
	
}
