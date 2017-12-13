/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computa��o - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.experiments.settings;

import hidra.experiments.GlobalSettings;
import hidra.experiments.util.HIDRASettings;
import jmetal.core.Algorithm;
import hidra.metaheuristics.mopsocdr.MOPSOCDR;
import hidra.qualityIndicator.QualityIndicator;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class HIDRA_MOPSOCDR_Settings extends HIDRASettings{

	
	  public int    swarmSize_                 ;
	  public int    maxIterations_             ;
	  public int    archiveSize_               ;
	  public double mutationDistributionIndex_ ;
	  public double mutationProbability_       ;
	

	  /**
	   * Constructor
	   */
	  public HIDRA_MOPSOCDR_Settings(String problem) {
		  
	    super(problem) ;
	    
	    Object [] problemParams = {"Real"};
	    try {
		    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
	    } catch (JMException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
	    }      

	    // Default settings
	    //swarmSize_                 = 100 ;
	    //maxIterations_             = 1000 ;
	    //archiveSize_               = 200 ;
	    
	    swarmSize_                   = GlobalSettings.populationSize ; 
	    maxIterations_               = (GlobalSettings.maxEvaluations / GlobalSettings.populationSize) - 1;
	    archiveSize_                 = GlobalSettings.archiveSize   ;
	    
	    
	    
	    mutationDistributionIndex_ = 20.0 ;
	    mutationProbability_       = 1.0/problem_.getNumberOfVariables() ;
	  } // SMPSO_Settings
	  
	  /**
	   * Configure NSGAII with user-defined parameter settings
	   * @return A NSGAII algorithm object
	   * @throws jmetal.util.JMException
	   */
	  public Algorithm configure() throws JMException {
	    Algorithm algorithm ;

	    
	    QualityIndicator indicators ;
	    
	   

	    // Creating the problem
	    algorithm = new MOPSOCDR(problem_) ;
	    
	    // Algorithm parameters
	    algorithm.setInputParameter("swarmSize", swarmSize_);
	    algorithm.setInputParameter("maxIterations", maxIterations_);
	    algorithm.setInputParameter("archiveSize", archiveSize_);
	    //algorithm.setInputParameter("pathParetoFront", pathParetoFront_);	
	    
	    
	    // Creating the indicator object
	    if ((paretoFrontFile_!=null) && (!paretoFrontFile_.equals(""))) {
	 	   //System.out.println(paretoFrontFile_);
	       indicators = new QualityIndicator(problem_, paretoFrontFile_);
	       indicators.readRefHypervolume(pathRefHypervolume_, patternProblem_);
	       algorithm.setInputParameter("indicators", indicators) ;  
	    } // if
	    
	    
	    
		return algorithm ;
	  } // Configure
	
	

	
	
}
