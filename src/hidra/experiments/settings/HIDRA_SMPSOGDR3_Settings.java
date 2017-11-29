package hidra.experiments.settings;

import hidra.experiments.GlobalSettings;
import hidra.experiments.util.HIDRASettings;
import hidra.jmetal.core.Algorithm;
import hidra.many.metaheuristics.smpsogdr.SMPSOGDR;
import hidra.many.metaheuristics.smpsogdr2.SMPSOGDR2;
import hidra.many.metaheuristics.smpsogdr3.SMPSOGDR3;
import hidra.qualityIndicator.QualityIndicator;

import java.util.HashMap;

import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

public class HIDRA_SMPSOGDR3_Settings extends HIDRASettings {

	public int    swarmSize_                 ;
	public int    maxIterations_             ;
	public int    archiveSize_               ;
	public double mutationDistributionIndex_ ;
	public double mutationProbability_       ;

	/**
	 * Constructor
	 */
	public HIDRA_SMPSOGDR3_Settings(String problem) {
		super(problem) ;

		Object [] problemParams = {"Real"};
		try {
			problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      

		swarmSize_                   = GlobalSettings.populationSize ; 
		maxIterations_               = (GlobalSettings.maxEvaluations / GlobalSettings.populationSize) - 1;
		archiveSize_                 = GlobalSettings.archiveSize   ;
		
		mutationDistributionIndex_   = GlobalSettings.mutationDistributionIndex_ ;
		mutationProbability_         = 1.0/problem_.getNumberOfVariables() ;
	
	} // SMPSO_Settings

	/**
	 * Configure NSGAII with user-defined parameter settings
	 * @return A NSGAII algorithm object
	 * @throws jmetal.util.JMException
	 */
	public Algorithm configure() throws JMException {
		Algorithm algorithm ;
		Mutation  mutation ;

		QualityIndicator indicators ;

		HashMap  parameters ; // Operator parameters

		// Creating the problem
		algorithm = new SMPSOGDR3(problem_) ;

		// Algorithm parameters
		algorithm.setInputParameter("swarmSize", swarmSize_);
		algorithm.setInputParameter("maxIterations", maxIterations_);
		algorithm.setInputParameter("archiveSize", archiveSize_);

		parameters = new HashMap() ;
		parameters.put("probability", mutationProbability_) ;
		parameters.put("distributionIndex", mutationDistributionIndex_) ;
		mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

		algorithm.addOperator("mutation",mutation);

		// Creating the indicator object
		if ((paretoFrontFile_!=null) && (!paretoFrontFile_.equals(""))) {
			indicators = new QualityIndicator(problem_, paretoFrontFile_);
			algorithm.setInputParameter("indicators", indicators) ;  
		} // if

		return algorithm ;
	} // Configure



}
