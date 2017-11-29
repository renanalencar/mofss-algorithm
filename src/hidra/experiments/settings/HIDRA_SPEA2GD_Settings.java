//  SPEA2_Settings.java 
//
//  Authors:
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

package hidra.experiments.settings;

import hidra.experiments.GlobalSettings;
import hidra.experiments.util.HIDRASettings;
import hidra.jmetal.core.Algorithm;
import hidra.jmetal.core.Operator;
import hidra.many.metaheuristics.spea2mod.SPEA2GD;
import hidra.metaheuristics.spea2.SPEA2;
import hidra.qualityIndicator.QualityIndicator;

import java.util.HashMap;

import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

/**
 * Settings class of algorithm SPEA2
 */
public class HIDRA_SPEA2GD_Settings extends HIDRASettings {
  
  public int populationSize_           ;
  public int archiveSize_              ;
  public int maxIterations_           ;
  public double mutationProbability_   ;
  public double crossoverProbability_  ;
  public double crossoverDistributionIndex_ ;
  public double mutationDistributionIndex_  ;

  /**
   * Constructor
   */
  public HIDRA_SPEA2GD_Settings(String problem) {
    super(problem) ;
    
    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }      

    //populationSize_           = 100   ;   
   // maxEvaluations_           = 100000 ;
    
    
    populationSize_              = GlobalSettings.populationSize ; 
    maxIterations_              = GlobalSettings.maxIterations ;
    archiveSize_                 = GlobalSettings.archiveSize   ;
    
    mutationProbability_   = 1.0/problem_.getNumberOfVariables() ;
    
    
    crossoverProbability_        = GlobalSettings.crossoverProbability_  ;
    mutationDistributionIndex_   = GlobalSettings.mutationDistributionIndex_ ;
    crossoverDistributionIndex_  = GlobalSettings.crossoverDistributionIndex_ ;

  } // SPEA2_Settings
  
  /**
   * Configure SPEA2 with default parameter settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Operator  crossover ;         // Crossover operator
    Operator  mutation  ;         // Mutation operator
    Operator  selection ;         // Selection operator
    
    QualityIndicator indicators ;
    
    HashMap  parameters ; // Operator parameters

    // Creating the problem
    algorithm = new SPEA2GD(problem_) ;
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxIterations", maxIterations_);
      
    // Mutation and Crossover for Real codification 
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    parameters.put("distributionIndex", crossoverDistributionIndex_) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", mutationDistributionIndex_) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
        
    // Selection operator 
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;                           
    
    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);
    
		// Creating the indicator object
    if ((paretoFrontFile_!=null) && (!paretoFrontFile_.equals(""))) {
			indicators = new QualityIndicator(problem_, paretoFrontFile_);
			algorithm.setInputParameter("indicators", indicators) ;  
		} // if
   
   return algorithm ;
  } // configure
} // SPEA2_Settings
