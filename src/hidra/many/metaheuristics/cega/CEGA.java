package hidra.many.metaheuristics.cega;

import hidra.core.population.HIDRAPopulationAlgorithm;
import hidra.core.util.Util;
import hidra.jmetal.core.Operator;
import hidra.jmetal.core.Problem;
import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;

import java.util.HashMap;
import java.util.List;

import jmetal.operators.selection.BinaryTournament;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.comparators.ObjectiveComparator;
import jmetal.util.comparators.SolutionComparator;

public class CEGA extends HIDRAPopulationAlgorithm{
	
	private SolutionSet population;
	private int populationSize; 
	
	private Operator mutationOperator;
	private Operator crossoverOperator;
	private Operator selectionOperator;
	
	
	
	Distance distance = new Distance();
	SolutionComparator equalSolutions = new SolutionComparator();
	
	

	public CEGA(Problem problem) {
		super(problem);				
	}

	
	protected void initParams() {
		
		super.initParams();
				
		this.populationSize = ((Integer) getInputParameter("populationSize")).intValue();		
		this.population = new SolutionSet(populationSize);
		
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("comparator", new CEGAComparator());	    
		selectionOperator = new BinaryTournament(parameters);		
		mutationOperator = operators_.get("mutation");
		crossoverOperator = operators_.get("crossover");		
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
			//super.evaluations_++;
			population.add(newSolution);
		} //for       		
		
		//Assign crowding distance to individuals
		CEGAFitness fitness = new CEGAFitness();
		fitness.fitnessAssign(population);
		
		
		return population;
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
				//super.evaluations_ += 2;
			                            
		} // for

		// Create the solutionSet union of solutionSet and offSpring
		union = ((SolutionSet) population).union(offspringPopulation);

		//Assign crowding distance to individuals
		CEGAFitness fitness = new CEGAFitness();
		fitness.fitnessAssign(union);
		
		
		
		// Clustering the union
		Clustering clustering = new Clustering(union,populationSize/2);
	
		population.clear();
		
		
		// Obtain the next front
		List<Cluster> clusters = clustering.getClusters();
		
		//SolutionSet  solutions = new SolutionSet( clusters.size() );
		SolutionSet tempPop = new SolutionSet(2*populationSize);
		
		for(int c=0; c < clusters.size();c++){
			
			int indexBest = clusters.get(c).getIndexLeader();
			population.add(clusters.get(c).getSolution(indexBest));
			clusters.get(c).remove(indexBest);							
			SolutionSet aux = clusters.get(c).toSolutionSet();
			tempPop = tempPop.union(aux);
		
		}
		
		tempPop.sort(new CEGAComparator());
		//int indexBest = tempPop.size()-1;
		
		int remain = populationSize - population.size();
		
		
		while (  remain > 0  ) {

			population.add(  tempPop.get(0)  );
			tempPop.remove(0);
			remain--;
			
		} // while

				
		iteration_++;
		//System.out.println(iteration_);
					
		//Ranking ranking = new Ranking(population);
		
		//return ranking.getSubfront(0);

		return population;
	}
	
	
	
	public void filter2(){
					
		
		for(int obj=0; obj < problem_.getNumberOfObjectives() ;obj++){
			
			this.population.sort(new ObjectiveComparator(obj));
			
			int thirdQuat  = (int)(0.75 * population.size());
			int firstQuat = (int)(0.25 * population.size());
			
			double range = 
				population.get(thirdQuat).getObjective(obj)
								- population.get(firstQuat).getObjective(obj);
		
			range = 1.5 * range;
			double limit = population.get(thirdQuat).getObjective(obj);
			limit = limit + range;
			
			double value = 0.0;
			
			for(int j = thirdQuat+1;  j < population.size()  ; ){				
				value = population.get(j).getObjective(obj);				
				if( value > limit){
					population.remove(j);	
				}else{
					j++;
				}								
		   }

			
			
		}
		
		
	}
	
	

	
	public void filter(){
		
		
		
		for(int i=0;  i < population.size();i++){		
			double norm = Util.normEucleadian(population.get(i));			
			population.get(i).setFitness(norm);			
		}
		
		population.sort(new NormComparator());
		
		int thirdQuat  = (int)(0.75 * population.size());
		int firstQuat = (int)(0.25 * population.size());
		
		double range = 
			population.get(thirdQuat).getFitness()
							- population.get(firstQuat).getFitness();
	
		range = 1.5 * range;
		double limit = population.get(thirdQuat).getFitness();
		limit = limit + range;
		double norm = 0.0;
		
		
		for(int i = thirdQuat+1;  i < population.size()  ; ){
			
			norm = population.get(i).getFitness();
			
			if( norm > limit){
				population.remove(i);	
			}else{
				i++;
			}
			
			
	   }
		
	}

	
	
	

	@Override
	protected SolutionSet getParetoFront() {
		//filter2();
		Ranking2 ranking = new Ranking2(population);
		population = ranking.getSubfront(0);
		filter2();		
		return population;
		//return population;
	}
	
	
	
	
}
