/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computa��o - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.metaheuristics.mopsocdrs;


import hidra.gui.MOPSOCDRSJFrame;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import hidra.qualityIndicator.QualityIndicator;
import jmetal.operators.mutation.Mutation;
import jmetal.problems.DTLZ.DTLZ1;
import jmetal.problems.ProblemFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class MOPSOCDRS_main {

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
	    fileHandler_ = new FileHandler("MOPSOCDRS_main.log"); 
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
	     //problem = new ZDT4("Real");
	    	problem = new DTLZ1("Real");
	    	indicators = new QualityIndicator(problem,"paretos/dtlz/DTLZ3.3D.pf") ;
	      //problem = new OKA2("Real") ;
	    } // else
	    
	    boolean debug = false;
	    
	    
	    algorithm = new MOPSOCDRS(problem) ;
	    
	    if(debug){
			MOPSOCDRSJFrame frameMopso_CDR = new MOPSOCDRSJFrame( (MOPSOCDRS) algorithm) ;
			frameMopso_CDR.setTitle("MOPSO_CDRS (" + "ZDT" + ")");
	        frameMopso_CDR.setVisible(true);
	    }
	    
	    // Algorithm parameters
	    algorithm.setInputParameter("swarmSize",100);
	    algorithm.setInputParameter("archiveSize",100);
	    algorithm.setInputParameter("maxIterations",250);
	    
	    parameters = new HashMap() ;
	    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
	    parameters.put("distributionIndex", 20.0) ;
	      
	    
        
	    
	    // Execute the Algorithm 
	    long initTime = System.currentTimeMillis();
	    SolutionSet population = algorithm.execute();
	    long estimatedTime = System.currentTimeMillis() - initTime;
	    
	    // Result messages 
	    logger_.info("Total execution time: "+estimatedTime + "ms");
	    logger_.info("Objectives values have been writen to file FUN");
	    population.printObjectivesToFile("paretos_matlab/FUN");
	    logger_.info("Variables values have been writen to file VAR");
	    population.printVariablesToFile("paretos_matlab/VAR");      
	    
	    if (indicators != null) {
	      
	    	
	    	logger_.info("Quality indicators") ;
		    logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
		    logger_.info("GD         : " + indicators.getGD(population)) ;
	        logger_.info("IGD        : " + indicators.getIGD(population)) ;
	        logger_.info("Generalized Spread     : " + indicators.getGeneralizedSpread(population)) ;
	        logger_.info("Epsilon    : " + indicators.getEpsilon(population)) ;
	        logger_.info("Convergence Measure    : " + indicators.getConvergenceMeasure(population,true)) ;
	    
	    } // if
	    
	    
	  } //main
	
	
}
