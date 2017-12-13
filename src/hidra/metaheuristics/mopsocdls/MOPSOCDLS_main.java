package hidra.metaheuristics.mopsocdls;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import hidra.qualityIndicator.QualityIndicator;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import jmetal.operators.mutation.Mutation;
import jmetal.problems.ProblemFactory;
import jmetal.problems.DTLZ.DTLZ1;
import jmetal.util.Configuration;
import jmetal.util.JMException;




public class MOPSOCDLS_main {

	  public static Logger      logger_ ;      // Logger object
	  public static FileHandler fileHandler_ ; // FileHandler object
	
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
		      //problem = new ZDT6("Real");
		    	problem = new DTLZ1("Real");
		    	indicators = new QualityIndicator(problem,"paretos/dtlz/DTLZ1.3D.pf") ;
		      //problem = new OKA2("Real") ;
		    } // else
		    
		    algorithm = new MOPSOCDLS(problem) ;
		    
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
