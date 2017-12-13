//  SPEA2.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package hidra.many.metaheuristics.spea2mod;

import hidra.core.population.HIDRAPopulationAlgorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.Ranking;

/** 
 * This class representing the SPEA2 algorithm
 */
public class SPEA2GD extends HIDRAPopulationAlgorithm{
          
  /**
   * Defines the number of tournaments for creating the mating pool
   */
  public static final int TOURNAMENTS_ROUNDS = 1;

  private int populationSize, archiveSize;
  
  //private int maxEvaluations, evaluations;
  
  private Operator crossoverOperator, mutationOperator, selectionOperator;
  
  private SolutionSet solutionSet, archive;
  
  /**
  * Constructor.
  * Create a new SPEA2 instance
  * @param problem Problem to solve
  */
  public SPEA2GD(Problem problem) {                
    super(problem) ;
  } // Spea2
   
  /**   
  * Runs of the Spea2 algorithm.
  * @return a <code>SolutionSet</code> that is a set of non dominated solutions
  * as a result of the algorithm execution  
   * @throws JMException 
  */  
  
  protected void initParams(){
	  
	    super.initParams();
	  
	  //Read the params
	    populationSize = ((Integer)getInputParameter("populationSize")).intValue();
	    archiveSize    = ((Integer)getInputParameter("archiveSize")).intValue();
	    //maxEvaluations = ((Integer)getInputParameter("maxEvaluations")).intValue();	    
	    //maxIterations_ = maxEvaluations/populationSize;
	    //maxIterations_ = maxIterations_ - 1;
	    //iteration_ = 0;
	    
	    //Read the operators
	    crossoverOperator = operators_.get("crossover");
	    mutationOperator  = operators_.get("mutation");
	    selectionOperator = operators_.get("selection");        
	        
	    //Initialize the variables
	    solutionSet  = new SolutionSet(populationSize);
	    archive     = new SolutionSet(archiveSize);
	    //evaluations = 0;
	  
  }
  
  @Override
  protected SolutionSet initializationAlgorithm() throws ClassNotFoundException,
  		JMException {
  
	  
    initParams();
	  
    //-> Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      problem_.evaluate(newSolution);            
      problem_.evaluateConstraints(newSolution);
      //evaluations++;
      solutionSet.add(newSolution);
    }  
    
  	return solutionSet;
  }
  
  
  



@Override
protected SolutionSet runIteration() throws JMException {
	
	SolutionSet  offSpringSolutionSet;           

	SolutionSet union = ((SolutionSet)solutionSet).union(archive);
	SpeaGDFitness spea = new SpeaGDFitness(union);
	spea.fitnessAssign();
	archive = spea.environmentalSelection(archiveSize);                       
	// Create a new offspringPopulation
	offSpringSolutionSet= new SolutionSet(populationSize);    
	Solution  [] parents = new Solution[2];
	while (offSpringSolutionSet.size() < populationSize){           
		int j = 0;
		do{
			j++;                
			parents[0] = (Solution)selectionOperator.execute(archive);
		} while (j < SPEA2GD.TOURNAMENTS_ROUNDS); // do-while                    
		int k = 0;
		do{
			k++;                
			parents[1] = (Solution)selectionOperator.execute(archive);
		} while (k < SPEA2GD.TOURNAMENTS_ROUNDS); // do-while

		//make the crossover 
		Solution [] offSpring = (Solution [])crossoverOperator.execute(parents);            
		mutationOperator.execute(offSpring[0]);            
		problem_.evaluate(offSpring[0]);
		problem_.evaluateConstraints(offSpring[0]);            
		offSpringSolutionSet.add(offSpring[0]);
		//evaluations++;
	} // while
	// End Create a offSpring solutionSet
	solutionSet = offSpringSolutionSet;                   

	iteration_++;
	Ranking ranking = new Ranking(archive);
    return ranking.getSubfront(0);
}

@Override
protected SolutionSet getParetoFront() {
	Ranking ranking = new Ranking(archive);
    return ranking.getSubfront(0);
}



} // SPEA2
