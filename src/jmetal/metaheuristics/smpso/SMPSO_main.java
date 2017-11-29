//  SMPSO_main.java
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

package jmetal.metaheuristics.smpso;

import hidra.gui.MOPSOCDRSJFrame;
import hidra.jmetal.core.*;
import hidra.metaheuristics.mopsocdr.MOPSOCDR;
import hidra.qualityIndicator.Hypervolume;
import hidra.qualityIndicator.QualityIndicator;

import java.io.IOException;

import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.*;
import jmetal.problems.ZDT.*;
import jmetal.problems.WFG.*;
import jmetal.problems.DTLZ.*;
import jmetal.problems.LZ09.* ;
import jmetal.util.Configuration;
import jmetal.util.JMException ;

import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * This class executes the SMPSO algorithm described in:
 * A.J. Nebro, J.J. Durillo, J. Garcia-Nieto, C.A. Coello Coello, F. Luna and E. Alba
 * "SMPSO: A New PSO-based Metaheuristic for Multi-objective Optimization". 
 * IEEE Symposium on Computational Intelligence in Multicriteria Decision-Making 
 * (MCDM 2009), pp: 66-73. March 2009
 */
public class SMPSO_main {
  public static Logger      logger_ ;      // Logger object
  public static FileHandler fileHandler_ ; // FileHandler object

  /**
   * @param args Command line arguments. The first (optional) argument specifies 
   *             the problem to solve.
   * @throws JMException 
   * @throws IOException 
   * @throws SecurityException 
   * Usage: three options
   *      - jmetal.metaheuristics.mocell.MOCell_main
   *      - jmetal.metaheuristics.mocell.MOCell_main problemName
   *      - jmetal.metaheuristics.mocell.MOCell_main problemName ParetoFrontFile
   */
  public static void main(String [] args) throws JMException, IOException, ClassNotFoundException {
    Problem   problem   ;  // The problem to solve
    Algorithm algorithm ;  // The algorithm to use
    Mutation  mutation  ;  // "Turbulence" operator
    
    QualityIndicator indicators ; // Object to get quality indicators
        
    HashMap  parameters ; // Operator parameters

    // Logger object and file to store log messages
    logger_      = Configuration.logger_ ;
    fileHandler_ = new FileHandler("SMPSO_main.log"); 
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
      //problem = new ZDT1("ArrayReal", 1000);
      //problem = new ZDT4("BinaryReal");
      //problem = new WFG1("Real");
      problem = new DTLZ2("Real", 2);
      //problem = new OKA2("Real") ;
      //problem = new Fonseca("Real");
      //problem = new Schaffer("Real");
    } // else
    
    algorithm = new SMPSO(problem) ;
    indicators = new QualityIndicator(problem, "paretofront/DTLZ1.2D.pf");
    
    // Algorithm parameters
    algorithm.setInputParameter("swarmSize",100);
    algorithm.setInputParameter("archiveSize",100);
    algorithm.setInputParameter("maxIterations",25000);
    
    parameters = new HashMap() ;
    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
    parameters.put("distributionIndex", 20.0) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

    algorithm.addOperator("mutation", mutation);
    
 // Add the indicator object to the algorithm
    algorithm.setInputParameter("indicators", indicators) ;
    
    //graphics
    MOPSOCDRSJFrame frameMopso_CDR = new MOPSOCDRSJFrame( (SMPSO) algorithm) ;
	frameMopso_CDR.setTitle("SMPSO (" + problem.getName() + ")");
    frameMopso_CDR.setVisible(true);

    // Execute the Algorithm 
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    
    // Result messages 
    logger_.info("Total execution time: "+estimatedTime + "ms");
    logger_.info("Objectives values have been writen to file FUN"/*+"-SMPSO-"+problem.getName()*/);
    population.printObjectivesToFile("results/FUN"+"-SMPSO-"+problem.getName());
    logger_.info("Variables values have been writen to file VAR"/*+"-SMPSO-"+problem.getName()*/);
    population.printVariablesToFile("results/VAR"+"-SMPSO-"+problem.getName()); 
    
    int qtdExec = 100;
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
    
    /*for (int i=1; i<qtdExec; i++){
    	initTime = System.currentTimeMillis();
        population = algorithm.execute();
        estimatedTime = System.currentTimeMillis() - initTime;
        
        hypevolumeQI[i] = indicators.getHypervolume(population);
        epsilonQI[i] = indicators.getEpsilon(population);
        spreadQI[i] = indicators.getSpread(population);
        spacingQI[i] = indicators.getSpacing(population);
        
        System.out.println("-----Execução: " + (i+1) + "/" + qtdExec + " Time: " + estimatedTime);
        System.out.println("     HV: " + hypevolumeQI[i] + " | SPA: " + spacingQI[i] + " | SPR: " + spreadQI[i] + " | EPS: " + epsilonQI[i]);
    }
    population.printMultipleQIToFile("results/Graphs/"+"SMPSO-"+problem.getName()+"-HYPERVOLUME", hypevolumeQI);
    population.printMultipleQIToFile("results/Graphs/"+"SMPSO-"+problem.getName()+"-EPSILON", epsilonQI);
    population.printMultipleQIToFile("results/Graphs/"+"SMPSO-"+problem.getName()+"-SPREAD", spreadQI);
    population.printMultipleQIToFile("results/Graphs/"+"SMPSO-"+problem.getName()+"-SPACING", spacingQI);
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
    population.printQualityIndicatorToFile("results/IND"+"-SMPSO-"+problem.getName()+"-"+
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
    } // if                   
  } //main
} // SMPSO_main
