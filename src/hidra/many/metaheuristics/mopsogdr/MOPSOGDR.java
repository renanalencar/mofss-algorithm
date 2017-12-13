/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computa��o - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


 */

package hidra.many.metaheuristics.mopsogdr;







import hidra.core.population.BoundArchive;
import hidra.core.population.HIDRAPopulationAlgorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import hidra.many.metaheuristics.comparators.GDComparator;
import hidra.many.metaheuristics.comparators.GeneralizedRoletteWheelSelection;
import hidra.many.metaheuristics.fitness.GDFitness;
import hidra.metaheuristics.mopsocdr.util.ParetoFrontUtil;
import hidra.metaheuristics.mopsocdr.util.TurbulenceMutation;

import java.util.Comparator;
import java.util.HashMap;

import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.wrapper.XReal;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class MOPSOGDR extends HIDRAPopulationAlgorithm{


	private int swarmSize_;

	private int archiveSize_;
	
	private SolutionSet particles_;

	private Solution[] best_;

	//private BoundArchive leaders_;

	private BoundArchive leaders_;
	
	private double[][] speed_;

	private Comparator dominance_;

	private Comparator<Solution> fitnessComp;

	

	private Operator turbulence_;	

	//private GeneralizedRoletteWheelSelection rollete;
	
	private static final double MUTATION_PERCENTUAL_MOPSO = 0.5; 
	
	private double C1_;
	private double C2_;
	private double wMax_;
	private double wMin_;
	
	
	public MOPSOGDR(Problem problem) {                
		super (problem);		
	}



	public void initParams() {
		
		
		super.initParams();
		
		swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
		archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
		maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
				
		
				
		iteration_ = 0 ;
		
		particles_ = new SolutionSet(swarmSize_);
		best_ = new Solution[swarmSize_];
		//leaders_ = new BoundArchive(archiveSize_, problem_.getNumberOfObjectives(), new GDComparator(),new GDFitness());
		leaders_ = new BoundArchive(archiveSize_, problem_.getNumberOfObjectives(),new GDComparator(),new GDFitness());
		
		// Create comparators for dominance and crowding distance
		dominance_ = new DominanceComparator();
		fitnessComp = new GDComparator();
		
		//distance = new UtilDistance();

		// Create the speed vector
		speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];
		
			
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		
		parameters.put("maxIterations",maxIterations_);
		parameters.put("percMutation", MUTATION_PERCENTUAL_MOPSO);
		
		this.turbulence_ = new TurbulenceMutation(parameters);
		
		
		
		this.C1_ = 1.49445;
		this.C2_ = 1.49445;
		this.wMax_ = 0.4;
		this.wMin_ = 0.0;
	}


	

	@Override
	protected SolutionSet getParetoFront() {
		// TODO Auto-generated method stub
		return this.leaders_;
	}



	@Override
	protected SolutionSet initializationAlgorithm()
			throws ClassNotFoundException, JMException {
		
		
		initParams();

		//->Step 1 (and 3) Create the initial population and evaluate
		for (int i = 0; i < swarmSize_; i++){
			Solution particle = new Solution(problem_);
			problem_.evaluate(particle);
			problem_.evaluateConstraints(particle);
			particles_.add(particle);                   
		}


		//-> Step2. Initialize the speed_ of each particle to 0
		for (int i = 0; i < swarmSize_; i++) {
			for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
				speed_[i][j] = 0.0;
			}
		}

		// Step4 and 5   
		for (int i = 0; i < particles_.size(); i++) {
			Solution particle = new Solution(particles_.get(i));
			leaders_.add(particle);
		}

		//-> Step 6. Initialize the memory of each particle
		for (int i = 0; i < particles_.size(); i++) {
			Solution particle = new Solution(particles_.get(i));
			best_[i] = particle;
		}

		//Crowding the leaders_
		//ParetoFrontUtil.crowdingDistanceAssignmentToMOPSOCDR(leaders_, problem_.getNumberOfObjectives());
		GDFitness gdFitness = new GDFitness();
		gdFitness.fitnessAssign(this.leaders_,problem_.getNumberOfObjectives());
		
		return this.leaders_;
		
	}



	@Override
	protected SolutionSet runIteration() throws JMException {
		
		computeSpeed();
			
		//Compute the new positions for the particles_            
		computeNewPositions();

		//Mutate the particles_          
		mopsoMutation();

		//Evaluate the new particles_ in new positions
		for (int i = 0; i < particles_.size(); i++) {
			Solution particle = particles_.get(i);
			problem_.evaluate(particle);
			problem_.evaluateConstraints(particle);
		}

		//Actualize the archive          
		for (int i = 0; i < particles_.size(); i++) {
			Solution particle = new Solution(particles_.get(i));
			leaders_.add(particle);
		}

		//Assign GD to the leaders_
		GDFitness gdFitness = new GDFitness();
		gdFitness.fitnessAssign(this.leaders_,problem_.getNumberOfObjectives());
		
		iteration_++;

		//Update Particles of the Memory
		for (int i = 0; i < particles_.size(); i++) {
			
			int flag = dominance_.compare(particles_.get(i), best_[i]);
			if (flag == -1) { // the new particle is best than the older pBest        
				Solution particle = new Solution(particles_.get(i));
				best_[i] = particle;
			}else if(flag == 0){

				Solution solNearParticle = 
					ParetoFrontUtil.getNearSolution(this.leaders_, particles_.get(i));

				Solution solNearPBest = 
					ParetoFrontUtil.getNearSolution(this.leaders_, best_[i]);


				if(solNearParticle.getFitness() > solNearPBest.getFitness()){
					best_[i] =new Solution(particles_.get(i));
				}

			}
		}


		return this.leaders_;


	}



	
	// Adaptive inertia 
	private double inertiaWeight() {    	
		double w = wMax_ - (((wMax_-wMin_)*(double)iteration_)/(double)maxIterations_);
		if ( w < 0.0 ) {
            w = 0.0;          
        } 
		return w;
	} // inertiaWeight

	
	
	private void computeSpeed() throws JMException{
		double r1, r2;
		double  W;
		XReal bestGlobal;

		W = inertiaWeight();
		
		
	
		GeneralizedRoletteWheelSelection rollete = 	new GeneralizedRoletteWheelSelection(this.leaders_,this.fitnessComp);
		

		for (int i = 0; i < swarmSize_; i++) {
			
			XReal particle = new XReal(particles_.get(i)) ;
			XReal bestParticle = new XReal(best_[i]) ;

			bestGlobal = new XReal(rollete.execute());

			for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {

				// generate stochastic components for each dimension
				r1 = PseudoRandom.randDouble(0.0, 1.0);
				r2 = PseudoRandom.randDouble(0.0, 1.0);


				//Computing the velocity of this particle 
				speed_[i][var] =  (W *
						speed_[i][var] +
						C1_ * r1 * (bestParticle.getValue(var) -
								particle.getValue(var)) +
								C2_ * r2 * (bestGlobal.getValue(var) -
										particle.getValue(var)));

				double vmax = particle.getUpperBound(var) - particle.getLowerBound(var);

				speed_[i][var] = 
					Math.signum(speed_[i][var]) * 
					Math.min(Math.abs(speed_[i][var]),vmax);




			}
		
		
		}

		
		
	} // computeSpeed

	
	


	/**
	 * Apply a mutation operator to some particles in the swarm
	 * @throws JMException 
	 */
	private void mopsoMutation() throws JMException {

		turbulence_.setParameter("currentIteration", iteration_);

		for (int i = 0; i < particles_.size(); i++) {
			turbulence_.execute(particles_.get(i));
		}
	
	} // mopsoMutation



	/**
	 * Update the position of each particle
	 * @throws JMException 
	 */
	private void computeNewPositions() throws JMException{
		for (int i = 0; i < swarmSize_; i++){
			Variable[] particle = particles_.get(i).getDecisionVariables();
			//particle.move(speed_[i]);
			for (int var = 0; var < particle.length; var++){
				particle[var].setValue(particle[var].getValue()+ speed_[i][var]);
				if (particle[var].getValue() < problem_.getLowerLimit(var)){
					particle[var].setValue(problem_.getLowerLimit(var));                    
					speed_[i][var] = speed_[i][var] * -1.0;    
				}
				if (particle[var].getValue() > problem_.getUpperLimit(var)){
					particle[var].setValue(problem_.getUpperLimit(var));                    
					speed_[i][var] = speed_[i][var] * -1.0;    
				}                                             
			}
		}
	} // computeNewPositions








}
