//  SPEA2_main.java
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

package jmetal.metaheuristics.spea2;

import hidra.gui.MOPSOCDRSJFrame;
import hidra.jmetal.core.*;
import hidra.qualityIndicator.QualityIndicator;

import java.io.IOException;

import jmetal.metaheuristics.smpso.SMPSO;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.selection.*;
import jmetal.problems.*                  ;
import jmetal.problems.ZDT.*;
import jmetal.problems.WFG.*;
import jmetal.problems.DTLZ.*;
import jmetal.problems.LZ09.* ;

import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Class for configuring and running the SPEA2 algorithm
 */
public class SPEA2_main {
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
    Problem   problem   ;         // The problem to solve
    Algorithm algorithm ;         // The algorithm to use
    Operator  crossover ;         // Crossover operator
    Operator  mutation  ;         // Mutation operator
    Operator  selection ;         // Selection operator
        
    QualityIndicator indicators ; // Object to get quality indicators

    HashMap  parameters ; // Operator parameters

    // Logger object and file to store log messages
    logger_      = Configuration.logger_ ;
    fileHandler_ = new FileHandler("SPEA2.log"); 
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
      problem = new Kursawe("Real", 8); 
      //problem = new Water("Real");
      //problem = new ZDT1("ArrayReal", 1000);
      //problem = new ZDT2("Real");
      //problem = new ZDT4("BinaryReal");
      //problem = new WFG1("Real");
      //problem = new DTLZ1("Real", 2);
      //problem = new OKA2("Real") ;
      //problem = new Fonseca("Real");
    } // else
       
    algorithm = new SPEA2(problem);
    indicators = new QualityIndicator(problem, "paretofront/Kursawe.pf");
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize",100);
    algorithm.setInputParameter("archiveSize",100);
    algorithm.setInputParameter("maxEvaluations",25000);
      
    
 // Algorithm parameters
    //algorithm.setInputParameter("populationSize",20);
    //algorithm.setInputParameter("archiveSize",200);
    //algorithm.setInputParameter("maxEvaluations",200020);
    
    
    
    // Mutation and Crossover for Real codification 
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    parameters.put("distributionIndex", 20.0) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
    parameters.put("distributionIndex", 20.0) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
        
    // Selection operator 
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;                           
    
    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);
    
    //graphics
    //MOPSOCDRSJFrame frameMopso_CDR = new MOPSOCDRSJFrame( (SPEA2) algorithm) ;
	//frameMopso_CDR.setTitle("SPEA2 (" + problem.getName() + ")");
    //frameMopso_CDR.setVisible(true);
    
    // Execute the algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;

    // Result messages 
    logger_.info("Total execution time: "+estimatedTime + "ms");
    logger_.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("results/FUN"+"-SPEA2-"+problem.getName());
    logger_.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("results/VAR"+"-SPEA2-"+problem.getName());      
    
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
    
    for (int i=1; i<qtdExec; i++){
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
    population.printMultipleQIToFile("results/Graphs/"+"SPEA2-"+problem.getName()+"-HYPERVOLUME", hypevolumeQI);
    population.printMultipleQIToFile("results/Graphs/"+"SPEA2-"+problem.getName()+"-EPSILON", epsilonQI);
    population.printMultipleQIToFile("results/Graphs/"+"SPEA2-"+problem.getName()+"-SPREAD", spreadQI);
    population.printMultipleQIToFile("results/Graphs/"+"SPEA2-"+problem.getName()+"-SPACING", spacingQI);
    String[] name = new String[4];
    double[] value = new double[4];
    name[0] = "Hypervolume";
    value[0] = indicators.getHypervolume(population);
    name[1] = "Spread";
    value[1] = indicators.getSpread(population);
    name[2] = "Spacing";
    value[2] = indicators.getSpacing(population);
    name[3] = "Convergence";
    value[3] = indicators.getConvergenceMeasure(population, true);
    logger_.info("Quality indicators values have been writen to file IND");
    population.printQualityIndicatorToFile("results/IND"+"-SPEA2-"+problem.getName()+"-"+
											algorithm.getInputParameter("populationSize").toString()+"|"+
											algorithm.getInputParameter("maxEvaluations").toString(), name, value);
    
    if (indicators != null) {
      logger_.info("Quality indicators") ;
      logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
      logger_.info("GD         : " + indicators.getGD(population)) ;
      logger_.info("IGD        : " + indicators.getIGD(population)) ;
      logger_.info("Spread     : " + indicators.getSpread(population)) ;
      logger_.info("Epsilon    : " + indicators.getEpsilon(population)) ;
      logger_.info("Espacing   : " + indicators.getSpacing(population));
    } // if 
  }//main
} // SPEA2_main.java
