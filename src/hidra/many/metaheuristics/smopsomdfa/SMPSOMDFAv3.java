package hidra.many.metaheuristics.smopsomdfa;

import hidra.core.population.BoundArchive;
import hidra.core.population.HIDRAPopulationAlgorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import hidra.many.metaheuristics.comparators.MDFAFitnessComparator;
import hidra.many.metaheuristics.fitness.Fitness;
import hidra.many.metaheuristics.fitness.MDFAFitnessMod;
import hidra.qualityIndicator.QualityIndicator;

import java.util.Comparator;

import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.wrapper.XReal;

public class SMPSOMDFAv3 extends HIDRAPopulationAlgorithm{
	
	 /**
	   * Stores the number of particles_ used
	   */
	  private int swarmSize_;
	  /**
	   * Stores the maximum size for the archive
	   */
	  private int archiveSize_;
	  /**
	   * Stores the maximum number of iteration_
	   */
	 // private int maxIterations_;
	  /**
	   * Stores the current number of iteration_
	   */
	  //private int iteration_;
	  /**
	   * Stores the particles
	   */
	  private SolutionSet particles_;
	  /**
	   * Stores the best_ solutions founds so far for each particles
	   */
	  private Solution[] best_;
	  /**
	   * Stores the leaders_
	   */
	  private BoundArchive leaders_;
	  /**
	   * Stores the speed_ of each particle
	   */
	  private double[][] speed_;
	  /**
	   * Stores a comparator for checking dominance
	   */
	  private Comparator dominance_;
	  /**
	   * Stores a comparator for crowding checking
	   */
	  private Comparator<Solution> comparatorMDFA_;
	  
	 
	  private Fitness mdfaFitness;
	  
	  
	  /**
	   * Stores a operator for non uniform mutations
	   */
	  private Operator polynomialMutation_;

	  QualityIndicator indicators_; // QualityIndicator object

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

	  /** 
	   * Constructor
	   * @param problem Problem to solve
	   */
	  public SMPSOMDFAv3(Problem problem) {
	    super(problem) ;

	    r1Max_ = 1.0;
	    r1Min_ = 0.0;
	    r2Max_ = 1.0;
	    r2Min_ = 0.0;
	    C1Max_ = 2.5;
	    C1Min_ = 1.5;
	    C2Max_ = 2.5;
	    C2Min_ = 1.5;
	    WMax_ = 0.1;
	    WMin_ = 0.1;
	    ChVel1_ = -1;
	    ChVel2_ = -1;
	  } // Constructor



	  private double deltaMax_[];
	  private double deltaMin_[];
	  boolean success_;

	 

	  /**
	   * Initialize all parameter of the algorithm
	   */
	  public void initParams() {
		  
	    swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
	    archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
	    maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();

	    indicators_ = (QualityIndicator) getInputParameter("indicators");

	    polynomialMutation_ = operators_.get("mutation") ; 

	    iteration_ = 0 ;

	    success_ = false;

	    particles_ = new SolutionSet(swarmSize_);
	    best_ = new Solution[swarmSize_];
	    

	    // Create comparators for dominance and crowding distance
	    dominance_ = new DominanceComparator();
	    comparatorMDFA_ = new MDFAFitnessComparator();
	    
	    
	    double[][] weights = generateWeights();
	    
	    this.mdfaFitness = new MDFAFitnessMod(weights);
	    
	    leaders_ = new BoundArchive(archiveSize_, problem_.getNumberOfObjectives(),comparatorMDFA_, mdfaFitness );

	    // Create the speed_ vector
	    speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];


	    deltaMax_ = new double[problem_.getNumberOfVariables()];
	    deltaMin_ = new double[problem_.getNumberOfVariables()];
	    for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
	      deltaMax_[i] = (problem_.getUpperLimit(i) -
	        problem_.getLowerLimit(i)) / 2.0;
	      deltaMin_[i] = -deltaMax_[i];
	    } // for
	  } // initParams 

	  // Adaptive inertia 
	  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
	    return wma; // - (((wma-wmin)*(double)iter)/(double)miter);
	  } // inertiaWeight

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

	  
	  
	  
	  
	  private double[][] generateWeights(){			
		  double[][] weights = new double[this.archiveSize_][this.problem_.getNumberOfObjectives()];

		  for(int i=0; i  < this.swarmSize_ ; i++){

			  for(int j = 0; j < problem_.getNumberOfObjectives(); j++){

				  weights[i][j] =  Math.random();

			  }

		  }			
		  return weights;			
	  }
	  
	  
	  
	  
	  

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

	  /**
	   * Update the speed of each particle
	   * @throws JMException 
	   */
	  private void computeSpeed(int iter, int miter) throws JMException {
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

	      if (comparatorMDFA_.compare(one, two) > 0 ) {
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

	  /**
	   * Update the position of each particle
	   * @throws JMException 
	   */
	  private void computeNewPositions() throws JMException {
	    for (int i = 0; i < swarmSize_; i++) {
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

	  /**
	   * Apply a mutation operator to some particles in the swarm
	   * @throws JMException 
	   */
	  private void mopsoMutation(int actualIteration, int totalIterations) throws JMException {
	    for (int i = 0; i < particles_.size(); i++) {
	      if ( (i % 6) == 0)
	        polynomialMutation_.execute(particles_.get(i)) ;
	      //if (i % 3 == 0) { //particles_ mutated with a non-uniform mutation %3
	      //  nonUniformMutation_.execute(particles_.get(i));
	      //} else if (i % 3 == 1) { //particles_ mutated with a uniform mutation operator
	      //  uniformMutation_.execute(particles_.get(i));
	      //} else //particles_ without mutation
	      //;
	    }
	  } // mopsoMutation

	  /**   
	   * Runs of the SMPSO algorithm.
	   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	   * as a result of the algorithm execution  
	   * @throws JMException 
	   */
	  /*public SolutionSet execute() throws JMException, ClassNotFoundException {
	    initParams();

	    success_ = false;
	    //->Step 1 (and 3) Create the initial population and evaluate
	    for (int i = 0; i < swarmSize_; i++) {
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
	    distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());

	    //-> Step 7. Iterations ..        
	    while (iteration_ < maxIterations_) {
	      try {
	        //Compute the speed_
	        computeSpeed(iteration_, maxIterations_);
	      } catch (IOException ex) {
	        Logger.getLogger(SMPSO.class.getName()).log(Level.SEVERE, null, ex);
	      }

	      //Compute the new positions for the particles_            
	      computeNewPositions();

	      //Mutate the particles_          
	      mopsoMutation(iteration_, maxIterations_);

	      //Evaluate the new particles_ in new positions
	      for (int i = 0; i < particles_.size(); i++) {
	        Solution particle = particles_.get(i);
	        problem_.evaluate(particle);
	      }

	      //Actualize the archive          
	      for (int i = 0; i < particles_.size(); i++) {
	        Solution particle = new Solution(particles_.get(i));
	        leaders_.add(particle);
	      }

	      //Actualize the memory of this particle
	      for (int i = 0; i < particles_.size(); i++) {
	        int flag = dominance_.compare(particles_.get(i), best_[i]);
	        if (flag != 1) { // the new particle is best_ than the older remeber        
	          Solution particle = new Solution(particles_.get(i));
	          best_[i] = particle;
	        }
	      }

	      //Assign crowding distance to the leaders_
	      distance_.crowdingDistanceAssignment(leaders_,
	        problem_.getNumberOfObjectives());
	      iteration_++;
	    }
	    return this.leaders_;
	  } // execute

	*/  
	  
	  @Override
	  protected SolutionSet getParetoFront() {
	  	// TODO Auto-generated method stub
	  	return this.leaders_;
	  }

	  @Override
	  protected SolutionSet initializationAlgorithm() throws ClassNotFoundException,
	  		JMException {
	  	
		  initParams();

		    success_ = false;
		    //->Step 1 (and 3) Create the initial population and evaluate
		    for (int i = 0; i < swarmSize_; i++) {
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

		    
		    mdfaFitness.fitnessAssign(this.leaders_, problem_.getNumberOfObjectives());

		    return this.leaders_;
		  
		  
	  }

	  @Override
	  protected SolutionSet runIteration() throws JMException {
	  			  		
		        //Compute the speed_
		      computeSpeed(iteration_, maxIterations_);
		     
		      //Compute the new positions for the particles_            
		      computeNewPositions();

		      //Mutate the particles_          
		      mopsoMutation(iteration_, maxIterations_);

		      //Evaluate the new particles_ in new positions
		      for (int i = 0; i < particles_.size(); i++) {
		        Solution particle = particles_.get(i);
		        problem_.evaluate(particle);
		      }

		      //Actualize the archive          
		      for (int i = 0; i < particles_.size(); i++) {
		        Solution particle = new Solution(particles_.get(i));
		        leaders_.add(particle);
		      }
		      
		 
			  mdfaFitness.fitnessAssign(this.leaders_, problem_.getNumberOfObjectives());
			     

		      //Actualize the memory of this particle
		      for (int i = 0; i < particles_.size(); i++) {
		        int flag = comparatorMDFA_.compare(particles_.get(i), best_[i]);
		        if (flag == 1) { // the new particle is best_ than the older remeber        
		          Solution particle = new Solution(particles_.get(i));
		          best_[i] = particle;
		        }
		      }

		      
		      
		      iteration_++;

		      return this.leaders_;
		  
	  }

	  
	  
	  
	  
	  /** 
	   * Gets the leaders of the SMPSO algorithm
	   */
	  public SolutionSet getLeader() {
	    return leaders_;
	  }  // getLeader   


	
	
}
