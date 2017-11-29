/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computação - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


 */

package hidra.many.metaheuristics.smpsocdr;







import hidra.core.population.HIDRAPopulationAlgorithm;
import hidra.jmetal.core.Operator;
import hidra.jmetal.core.Problem;
import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;

import java.util.Comparator;
import java.util.HashMap;

import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.wrapper.XReal;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class SMPSOCDR extends HIDRAPopulationAlgorithm{


	private int swarmSize_;

	private int archiveSize_;

	private SolutionSet particles_;

	private Solution[] best_;

	private CrowdingArchive leaders_;

	private double[][] speed_;

	private Comparator dominance_;

	private Comparator crowdingDistanceComparator_;

	private Distance distance_;

	private Operator turbulence_;	

	private RoletteWheelSelection rollete;	

	double r1Max_;
	double r1Min_;
	double r2Max_;
	double r2Min_;
	double C1Max_;
	double C1Min_;
	double C2Max_;
	double C2Min_;
	double WMax_;
	double WMin_;
	double ChVel1_;
	double ChVel2_;
	
	private double deltaMax_[];
	private double deltaMin_[];
    
	private static final double MUTATION_PERCENTUAL_MOPSO = 0.5;    


	public SMPSOCDR(Problem problem) {                
		super (problem);
		
		r1Max_ = 1.0;
		r1Min_ = 0.0;
		r2Max_ = 1.0;
		r2Min_ = 0.0;
		C1Max_ = 2.5;
		C1Min_ = 1.5;
		C2Max_ = 2.5;
		C2Min_ = 1.5;
		WMax_ = 0.5;
		WMin_ = 0.1;
		ChVel1_ = -1;
		ChVel2_ = -1;
		
	}



	public void initParams() {
		
		super.initParams();
		
		swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
		archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
		maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
				
		this.rollete = new RoletteWheelSelection();
		
		
		iteration_ = 0 ;
		
		particles_ = new SolutionSet(swarmSize_);
		best_ = new Solution[swarmSize_];
		leaders_ = new CrowdingArchive(archiveSize_, problem_.getNumberOfObjectives());

		// Create comparators for dominance and crowding distance
		dominance_ = new DominanceComparator();
		crowdingDistanceComparator_ = new CrowdingDistanceComparator();
		distance_ = new Distance();
		//distance = new UtilDistance();

		// Create the speed vector
		speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];
		
		deltaMax_ = new double[problem_.getNumberOfVariables()];
	    deltaMin_ = new double[problem_.getNumberOfVariables()];
		
	    for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
	        deltaMax_[i] = (problem_.getUpperLimit(i) -
	          problem_.getLowerLimit(i)) / 2.0;
	        deltaMin_[i] = -deltaMax_[i];
	     } // for
			
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		
		parameters.put("maxIterations",maxIterations_);
		parameters.put("percMutation", MUTATION_PERCENTUAL_MOPSO);
		
		this.turbulence_ = new TurbulenceMutation(parameters);
		
	
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
		ParetoFrontUtil.crowdingDistanceAssignmentToMOPSOCDR(leaders_, problem_.getNumberOfObjectives());

		return this.leaders_;
		
	}



	@Override
	protected SolutionSet runIteration() throws JMException {
		
		computeSpeed();	

		//Compute the new positions for the particles_            
		computeNewPositions();

		//Mutate the particles_          
		turbulenceMOPSO();

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

		//Assign crowding distance to the leaders_
		ParetoFrontUtil.crowdingDistanceAssignmentToMOPSOCDR(leaders_,
				problem_.getNumberOfObjectives());
		
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


				if(solNearParticle.getCrowdingDistance() > solNearPBest.getCrowdingDistance()){
					best_[i] =new Solution(particles_.get(i));
				}

			}
		}


		return this.leaders_;


	}

	
	
	
	
	
	/*
	 *//**
	   * Update the speed of each particle
	   * @throws JMException 
	   *//*
	  private void computeSpeedSMPSO(int iter, int miter) throws JMException, IOException {
	    double r1, r2, W, C1, C2;
	    double wmax, wmin, deltaMax, deltaMin;
	    XReal bestGlobal;

	    for (int i = 0; i < swarmSize_; i++) {
	    	XReal particle = new XReal(particles_.get(i)) ;
	    	XReal bestParticle = new XReal(best_[i]) ;

	      //Select a global best_ for calculate the speed of particle i, bestGlobal
	      Solution one, two;
	      int pos1 = PseudoRandom.randInt(0, leaders_.size() - 1);
	      int pos2 = PseudoRandom.randInt(0, leaders_.size() - 1);
	      one = leaders_.get(pos1);
	      two = leaders_.get(pos2);

	      if (crowdingDistanceComparator_.compare(one, two) < 1) {
	        bestGlobal = new XReal(one);
	      } else {
	        bestGlobal = new XReal(two);
	      //Params for velocity equation
	      }
	      r1 = PseudoRandom.randDouble(r1Min_, r1Max_);
	      r2 = PseudoRandom.randDouble(r2Min_, r2Max_);
	      C1 = PseudoRandom.randDouble(C1Min_, C1Max_);
	      C2 = PseudoRandom.randDouble(C2Min_, C2Max_);
	      W = PseudoRandom.randDouble(WMin_, WMax_);
	      //
	      wmax = WMax_;
	      wmin = WMin_;

	      for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
	        //Computing the velocity of this particle 
	        speed_[i][var] = velocityConstriction(constrictionCoefficient(C1, C2) *
	          (inertiaWeight(iter, miter, wmax, wmin) *
	          speed_[i][var] +
	          C1 * r1 * (bestParticle.getValue(var) -
	          particle.getValue(var)) +
	          C2 * r2 * (bestGlobal.getValue(var) -
	          particle.getValue(var))), deltaMax_, //[var],
	          deltaMin_, //[var], 
	          var,
	          i);
	      }
	    }
	  } // computeSpeed
*/
	
	  
	// constriction coefficient (M. Clerc)
	  private double constrictionCoefficient(double c1, double c2) {
	    double rho = c1 + c2;
	    //rho = 1.0 ;
	    if (rho <= 4) {
	      return 1.0;
	    } else {
	      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
	    }
	  } // constrictionCoefficient
  
	  

	  // velocity bounds
	  private double velocityConstriction(double v, double[] deltaMax,
	                                      double[] deltaMin, int variableIndex,
	                                      int particleIndex)  {


	    double result;

	    double dmax = deltaMax[variableIndex];
	    double dmin = deltaMin[variableIndex];

	    result = v;

	    if (v > dmax) {
	      result = dmax;
	    }

	    if (v < dmin) {
	      result = dmin;
	    }

	    return result;
	  } // velocityConstriction
  
	  
	
	private void computeSpeed() throws JMException{		
		double r1, r2, W, C1, C2;
		double wmax;
		XReal bestGlobal;	

		double summatory = 0.0;

		
		SolutionSet front = new SolutionSet(leaders_.size());
		for (int i = 0; i < leaders_.size(); i++){
			front.add(leaders_.get(i));
		}
		
		front.sort(crowdingDistanceComparator_);

		for(int i=0; i < front.size() ;i++){
			summatory = summatory + front.get(i).getCrowdingDistance();
		}

		

		for (int i = 0; i < swarmSize_; i++) {
			
			XReal particle = new XReal(particles_.get(i)) ;
			XReal bestParticle = new XReal(best_[i]) ;
			bestGlobal = new XReal(rollete.execute(front, summatory));

			// generate stochastic components for each dimension
			r1 = PseudoRandom.randDouble(r1Min_, r1Max_);
		    r2 = PseudoRandom.randDouble(r2Min_, r2Max_);
		    C1 = PseudoRandom.randDouble(C1Min_, C1Max_);
		    C2 = PseudoRandom.randDouble(C2Min_, C2Max_);
		    W = PseudoRandom.randDouble(WMin_, WMax_);


		 
		   

			
			for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {							      
				//Computing the velocity of this particle 
		        speed_[i][var] = velocityConstriction(constrictionCoefficient(C1, C2) *
		          ( inertiaWeight(iteration_, maxIterations_, WMax_, WMin_) *
		          speed_[i][var] +
		          C1 * r1 * (bestParticle.getValue(var) -
		          particle.getValue(var)) +
		          C2 * r2 * (bestGlobal.getValue(var) -
		          particle.getValue(var))), deltaMax_, //[var],
		          deltaMin_, //[var], 
		          var,
		          i);
								
			}
		}
		

	} // computeSpeed

	
	
	
	// Adaptive inertia 
	  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
	    return wma; // - (((wma-wmin)*(double)iter)/(double)miter);
	  } // inertiaWeight

	

	/**
	 * Apply a mutation operator to some particles in the swarm
	 * @throws JMException 
	 */
	private void turbulenceMOPSO() throws JMException {

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
			XReal particle = new XReal(particles_.get(i)) ;
			for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {
				particle.setValue(var, particle.getValue(var) +  speed_[i][var]) ;

				if (particle.getValue(var) < problem_.getLowerLimit(var)) {
					particle.setValue(var, problem_.getLowerLimit(var));
					speed_[i][var] = speed_[i][var] * ChVel1_; //    
				}
				if (particle.getValue(var) > problem_.getUpperLimit(var)) {
					particle.setValue(var, problem_.getUpperLimit(var));
					speed_[i][var] = speed_[i][var] * ChVel2_; //   
				}
			}
		}
	} // computeNewPositions









}
