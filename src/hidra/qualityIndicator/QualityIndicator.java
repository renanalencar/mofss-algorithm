//  QualityIndicator.java
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

package hidra.qualityIndicator;

import hidra.core.util.Util;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;

/**
 * QualityIndicator class
 */
public class QualityIndicator {
  SolutionSet trueParetoFront_ ;
  double      trueParetoFrontHypervolume_ ;
  Problem     problem_ ; 
  hidra.qualityIndicator.util.MetricsUtil utilities_  ;
  
  /**
   * Constructor
   * @param paretoFrontFile
   */
  public QualityIndicator(Problem problem, String paretoFrontFile) {
    problem_ = problem ;
    utilities_ = new hidra.qualityIndicator.util.MetricsUtil() ;
    trueParetoFront_ = utilities_.readNonDominatedSolutionSet(paretoFrontFile);
    
    //if( !(problem instanceof DTLZ) ){
    
    	trueParetoFrontHypervolume_ = new Hypervolume().hypervolume(
                 trueParetoFront_.writeObjectivesToMatrix(),   
                 trueParetoFront_.writeObjectivesToMatrix(),
                 problem_.getNumberOfObjectives());
    //}else{
    
    	
    	//trueParetoFrontHypervolume_ = Double.NaN;
    	//trueParetoFrontHypervolume_ = ((DTLZ)problem).getRefHypervolume();
    	//trueParetoFrontHypervolume_ = Util.getRefHypervolume(refHypervolume, pattern);
    //}
  } // Constructor 
  
  
  public void readRefHypervolume(String pathRefHyprvolume,String patternProblem){
	  
	  trueParetoFrontHypervolume_ = 
			  Util.getRefHypervolume(pathRefHyprvolume, patternProblem);
	  
  }
  
  
  	/*public QualityIndicator(Problem problem, String paretoFrontFile) {
	    problem_ = problem ;
	    utilities_ = new jmetal.qualityIndicator.util.MetricsUtil() ;
	    trueParetoFront_ = utilities_.readNonDominatedSolutionSet(paretoFrontFile);
	    
	    if( !(problem instanceof DTLZ) ){
	    
	    	trueParetoFrontHypervolume_ = new Hypervolume().hypervolume(
	                 trueParetoFront_.writeObjectivesToMatrix(),   
	                 trueParetoFront_.writeObjectivesToMatrix(),
	                 problem_.getNumberOfObjectives());
	    }else{
	    
	    	//trueParetoFrontHypervolume_ = ((DTLZ)problem).getRefHypervolume();
	    	//trueParetoFrontHypervolume_ = Util.getRefHypervolume(refHypervolume, pattern);
	    }
	  } // Constructor 
  */
  
  
  /**
   * Returns the hypervolume of solution set
   * @param solutionSet
   * @return The value of the hypervolume indicator
   */
  public double getHypervolume(SolutionSet solutionSet) {
    return new Hypervolume().hypervolume(solutionSet.writeObjectivesToMatrix(),
                                         trueParetoFront_.writeObjectivesToMatrix(),
                                         problem_.getNumberOfObjectives());
  } // getHypervolume

    
  
  
  /**
   * Returns the hypervolume of solution set
   * @param solutionSet
   * @return The value of the hypervolume indicator
   */
  public double getHypervolume(double[][] front) {
    return new Hypervolume().hypervolume(front,
                                         trueParetoFront_.writeObjectivesToMatrix(),
                                         problem_.getNumberOfObjectives());
  } // getHypervolume

    
  
  
  
  
  
  /**
   * Returns the hypervolume of the true Pareto front
   * @return The hypervolume of the true Pareto front
   */
  public double getTrueParetoFrontHypervolume() {
    return trueParetoFrontHypervolume_ ;
  }
  
  /**
   * Returns the inverted generational distance of solution set
   * @param solutionSet
   * @return The value of the hypervolume indicator
   */
  public double getIGD(SolutionSet solutionSet) {
    return new InvertedGenerationalDistance().invertedGenerationalDistance(
                    solutionSet.writeObjectivesToMatrix(),
                    trueParetoFront_.writeObjectivesToMatrix(),
                    problem_.getNumberOfObjectives());
  } // getIGD
  
 /**
   * Returns the generational distance of solution set
   * @param solutionSet
   * @return The value of the hypervolume indicator
   */
  public double getGD(SolutionSet solutionSet) {
    return new GenerationalDistance().generationalDistance(
                    solutionSet.writeObjectivesToMatrix(),
                    trueParetoFront_.writeObjectivesToMatrix(),
                    problem_.getNumberOfObjectives());
  } // getGD
  
  /**
   * Returns the spread of solution set
   * @param solutionSet
   * @return The value of the hypervolume indicator
   */
  public double getSpread(SolutionSet solutionSet) {
    return new Spread().spread(solutionSet.writeObjectivesToMatrix(),
                               trueParetoFront_.writeObjectivesToMatrix(),
                               problem_.getNumberOfObjectives());
  } // getGD
  
  
  
  public double getGeneralizedSpread(SolutionSet solutionSet) {
	    return new GeneralizedSpread().generalizedSpread(solutionSet.writeObjectivesToMatrix(),
	                               trueParetoFront_.writeObjectivesToMatrix(),
	                               problem_.getNumberOfObjectives());
  }
  
  
  
  
  
    /**
   * Returns the epsilon indicator of solution set
   * @param solutionSet
   * @return The value of the hypervolume indicator
   */
  public double getEpsilon(SolutionSet solutionSet) {
    return new Epsilon().epsilon(solutionSet.writeObjectivesToMatrix(),
                                 trueParetoFront_.writeObjectivesToMatrix(),
                                 problem_.getNumberOfObjectives());
  } // getEpsilon
  
  
  public double calculateHypervolumeRatio(double hypervolume) {
	    return (hypervolume/trueParetoFrontHypervolume_);
  }

  
  
  
  
  
  //=========================================== included by Elliakcin ==============================
  
  
  public double getConvergenceMeasure(SolutionSet solutionSet,boolean correct) {
	    return new ConvergenceMeasure().convergenceMeasure(
	                    solutionSet.writeObjectivesToMatrix(),
	                    trueParetoFront_.writeObjectivesToMatrix(),
	                    problem_.getNumberOfObjectives(),problem_,correct);
	    
	    
  } 
  
  
  
  public double getConvergenceMeasure(double[][] front,boolean correct) {
	    return new ConvergenceMeasure().convergenceMeasure(
	                   front,
	                    trueParetoFront_.writeObjectivesToMatrix(),
	                    problem_.getNumberOfObjectives(),problem_,correct);
	    
	    
} 

  
  
  public double getIshibuchiMaxSumMetric(SolutionSet solutionSet){
	  return new IshibuchiMaxSumMetric().calculate(solutionSet.writeObjectivesToMatrix());
  }
  
  
  public double getIshibuchiMaxRange(SolutionSet solutionSet){

	  return new IshibuchiMaxRange().calculate(solutionSet.writeObjectivesToMatrix(), problem_.getNumberOfObjectives());
	  
  }
  
  
  public double getImprovedMaximumSpread(SolutionSet solutionSet){
	  return new ImprovedMaximumSpread().calculate(solutionSet.writeObjectivesToMatrix(),
			  trueParetoFront_.writeObjectivesToMatrix(), problem_, problem_.getNumberOfObjectives());
  }
  
  
  
  public double getCoverage(double[][] a, double[][] b){
	return new Coverage().coverage(a, b);
	  
  }
  

  public double getDiversity2(SolutionSet solutionSet){
	return new Diversity2().diversity(solutionSet.writeObjectivesToMatrix(), problem_);	  
  }
  
  public double getSpacing(SolutionSet solutionSet){
	  return new Spacing().spacing(solutionSet.writeObjectivesToMatrix(), trueParetoFront_.writeObjectivesToMatrix(), problem_);
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
} // QualityIndicator
