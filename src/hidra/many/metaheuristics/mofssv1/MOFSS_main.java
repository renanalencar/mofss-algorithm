//  NSGAII_main.java
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

package hidra.many.metaheuristics.mofssv1;

import hidra.gui.MOPSOCDRSJFrame;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import hidra.qualityIndicator.QualityIndicator;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.DTLZ.DTLZ1;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;


/** 
 * Class implementing the NSGA-II algorithm.  
 * This implementation of NSGA-II makes use of a QualityIndicator object
 *  to obtained the convergence speed of the algorithm. This version is used
 *  in the paper:
 *     A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba 
 *     "A Study of Convergence Speed in Multi-Objective Metaheuristics." 
 *     To be presented in: PPSN'08. Dortmund. September 2008.
 *     
 *   Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 *   included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba 
 *                  "On the Effect of the Steady-State Selection Scheme in 
 *                  Multi-Objective Genetic Algorithms"
 *                  5th International Conference, EMO 2009, pp: 183-197. 
 *                  April 2009)
 */

public class MOFSS_main {
  public static Logger      logger_ ;      // Logger object
  public static FileHandler fileHandler_ ; // FileHandler object

  /**
   * @param args Command line arguments.
   * @throws JMException 
   * @throws IOException 
   * @throws SecurityException 
   * Usage: three options
   *      - jmetal.metaheuristics.nsgaII.NSGAII_main
   *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName
   *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName paretoFrontFile
   */
  public static void main(String [] args) throws 
                                  JMException, 
                                  SecurityException, 
                                  IOException, 
                                  ClassNotFoundException {
    Problem   problem   ; // The problem to solve
    Algorithm algorithm ; // The algorithm to use
    Operator  crossover ; // Crossover operator
    Operator  mutation  ; // Mutation operator
    Operator  selection ; // Selection operator
    
    HashMap  parameters ; // Operator parameters
    
    QualityIndicator indicators ; // Object to get quality indicators
    //hidra.qualityIndicator.util.MetricsUtil utilities_ = new hidra.qualityIndicator.util.MetricsUtil() ;
    //SolutionSet trueParetoFront_; 

    // Logger object and file to store log messages
    logger_      = Configuration.logger_ ;
    fileHandler_ = new FileHandler("log/MOFSS_main.log");
    logger_.addHandler(fileHandler_) ;
    
    indicators = null ;
    if (args.length == 1) {
      Object [] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0],params);
    } // if
    else if (args.length == 2) {
      Object [] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0],params);
      indicators = new QualityIndicator(problem, args[1]) ;
    } // if
    else { // Default problem
    	//problem = new Kursawe("Real", 3); 
        //problem = new Water("Real");
        //problem = new ZDT3("Real", 2);
        //problem = new ZDT4("BinaryReal");
        //problem = new WFG1("Real");
        problem = new DTLZ1("Real", 2);
        //problem = new OKA2("Real") ;
        //problem = new ConstrEx("Real");
    	//problem = new Fonseca("Real");
    	//problem = new Schaffer("Real");
    } // else
    
    /*
     * 
     *  JMetal Pareto Front Problems: http://jmetal.sourceforge.net/problems.html
     * 
     * 
     */
    
    algorithm = new MOFSSv9s1s2(problem);
    String paretoFrontFile = new String("paretofront/DTLZ1.2D.pf");
    
    indicators = new QualityIndicator(problem, paretoFrontFile);
    //trueParetoFront_ = utilities_.readNonDominatedSolutionSet(paretoFrontFile);
    
    // Algorithm parameters
    algorithm.setInputParameter("swarmSize",100);
    algorithm.setInputParameter("archiveSize",100);
    algorithm.setInputParameter("maxIterations",2500);

    // Mutation and Crossover for Real codification 
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    parameters.put("distributionIndex", 20.0) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
    parameters.put("distributionIndex", 20.0) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

    // Selection Operator 
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;                           

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);

    // Add the indicator object to the algorithm
    algorithm.setInputParameter("indicators", indicators) ;
    
    //graphics
    MOPSOCDRSJFrame frameMopso_CDR = new MOPSOCDRSJFrame( (MOFSSv9s1s2) algorithm) ;
	frameMopso_CDR.setTitle("MOFSS (" + problem.getName() + ")");
    frameMopso_CDR.setVisible(true);
    
    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    
    // Result messages 
    logger_.info("Total execution time: "+estimatedTime + "ms");
    logger_.info("Variables values have been written to file VAR"/*+"-MOFSS-"+problem.getName()*/);
    population.printVariablesToFile("results/VAR"+"-MOFSS-"+problem.getName()/*+"-"+
    								algorithm.getInputParameter("swarmSize").toString()+"|"+
    								algorithm.getInputParameter("maxIterations").toString()*/);    
    logger_.info("Objectives values have been written to file FUN"/*+"-MOFSS-"+problem.getName()*/);
    population.printObjectivesToFile("results/FUN"+"-MOFSS-"+problem.getName()/*+"-"+
									algorithm.getInputParameter("swarmSize").toString()+"|"+
									algorithm.getInputParameter("maxIterations").toString()*/);
    
    /*int qtdExec = 100;
    double[] hypevolumeQI = new double[qtdExec];
    double[] epsilonQI = new double[qtdExec];
    double[] spreadQI = new double[qtdExec];
    double[] spacingQI = new double[qtdExec];
    
    hypevolumeQI[0] = indicators.getHypervolume(population);
    epsilonQI[0] = indicators.getEpsilon(population);
    spreadQI[0] = indicators.getSpread(population);
    spacingQI[0] = indicators.getSpacing(population);
    
    System.out.println("-----Execução: 1/" + qtdExec + " Time: " + estimatedTime);
    System.out.println("     HV: " + hypevolumeQI[0] + " | SPA: " + spacingQI[0] + " | SPR: " + spreadQI[0] + " | EPS: " + epsilonQI[0]);
    
    for (int i=1; i<qtdExec; i++){
    	initTime = System.currentTimeMillis();
        population = algorithm.execute();
        if (population.flag == 1){
        	i--;
        }else{
	        estimatedTime = System.currentTimeMillis() - initTime;
	        
	        hypevolumeQI[i] = indicators.getHypervolume(population);
	        epsilonQI[i] = indicators.getEpsilon(population);
	        spreadQI[i] = indicators.getSpread(population);
	        spacingQI[i] = indicators.getSpacing(population);
	        
	        System.out.println("-----Execução: " + (i+1) + "/" + qtdExec + " Time: " + estimatedTime);
	        System.out.println("     HV: " + hypevolumeQI[i] + " | SPA: " + spacingQI[i] + " | SPR: " + spreadQI[i] + " | EPS: " + epsilonQI[i]);
        }
    }
    population.printMultipleQIToFile("results/Graphs/"+"MOFSS-"+problem.getName()+"-HYPERVOLUME", hypevolumeQI);
    population.printMultipleQIToFile("results/Graphs/"+"MOFSS-"+problem.getName()+"-EPSILON", epsilonQI);
    population.printMultipleQIToFile("results/Graphs/"+"MOFSS-"+problem.getName()+"-SPREAD", spreadQI);
    population.printMultipleQIToFile("results/Graphs/"+"MOFSS-"+problem.getName()+"-SPACING", spacingQI);
    */
    
    String[] name = new String[5];
    double[] value = new double[5];
    name[0] = "Hypervolume";
    value[0] = indicators.getHypervolume(population);
    name[1] = "Spread";
    value[1] = indicators.getSpread(population);
    name[2] = "Spacing";
    value[2] = indicators.getSpacing(population);
    name[3] = "Convergence";
    value[3] = indicators.getConvergenceMeasure(population, true);
    name[4] = "Hypervolume PF";
    value[4] = indicators.getTrueParetoFrontHypervolume();
    logger_.info("Quality indicators values have been writen to file IND");
    population.printQualityIndicatorToFile("results/IND"+"-MOFSS-"+problem.getName()+"-"+
											algorithm.getInputParameter("swarmSize").toString()+"|"+
											algorithm.getInputParameter("maxIterations").toString(), name, value);
    
    
    
    if (indicators != null) {
      logger_.info("Quality indicators") ;
      logger_.info("Hypervolume    : " + indicators.getHypervolume(population)) ;
      logger_.info("HypervolumePF  : " + indicators.getTrueParetoFrontHypervolume());
      logger_.info("GD             : " + indicators.getGD(population)) ;
      logger_.info("IGD            : " + indicators.getIGD(population)) ;
      logger_.info("Spread         : " + indicators.getSpread(population)) ;
      logger_.info("Epsilon        : " + indicators.getEpsilon(population)) ;
      logger_.info("Spacing        : " + indicators.getSpacing(population));
      //logger_.info("Coverage(ND,PF): " + indicators.getCoverage(population.writeObjectivesToMatrix(), utilities_.readNonDominatedSolutionSet(paretoFrontFile).writeObjectivesToMatrix()));
      //logger_.info("Coverage(PF,ND): " + indicators.getCoverage(utilities_.readNonDominatedSolutionSet(paretoFrontFile).writeObjectivesToMatrix(), population.writeObjectivesToMatrix()));
      
      //int evaluations = ((Integer)algorithm.getOutputParameter("evaluations")).intValue();
      //logger_.info("Speed      : " + evaluations + " evaluations") ;      
    } // if
  } //main
} // NSGAII_main
