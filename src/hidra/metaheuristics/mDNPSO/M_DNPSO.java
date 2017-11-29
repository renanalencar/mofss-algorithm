package hidra.metaheuristics.mDNPSO;

import hidra.core.population.HIDRAPopulationAlgorithm;
import hidra.jmetal.core.Problem;
import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;
import hidra.jmetal.core.Variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.EqualSolutions;
import jmetal.util.comparators.ObjectiveComparator;
import jmetal.util.wrapper.XReal;



public class M_DNPSO extends HIDRAPopulationAlgorithm{

	private int swarmSize_;

	private int archiveSize_;

	/*private int maxIterations_;

	private int iteration_;*/

	private SolutionSet particles_;

	private DNPSOSolution[] best_;

	private CrowdingArchive leaders_;

	private double[][] speed_;

	private Comparator dominance_;




	private double C1_;
	private double C2_;

	
	
	
	
	public M_DNPSO(Problem problem) {
		super(problem);
		//swarmSize_ = 100;
		//maxIterations_ = 10000;
	}

	public void initParams() {		 
		
		swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
		archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
		maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
		
	
		iteration_ = 0 ;

		particles_ = new SolutionSet(swarmSize_);
		best_ = new DNPSOSolution[swarmSize_];
		leaders_ = new CrowdingArchive(archiveSize_, problem_.getNumberOfObjectives());

		// Create comparators for dominance and crowding distance
		dominance_ = new DominanceComparator();

		// Create the speed vector
		speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];

		this.C1_ = 1.49445;
		this.C2_ = 1.49445;
		
	}





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



	
	

	


	
	
	/* public double updateRandomInertia() {
	     return 0.5 + (PseudoRandom.randDouble(-1, 1) * 0.5);
     }    
*/
	 
	 public double updateRandomInertia() {
	     return 0.5 + (PseudoRandom.randDouble(0, 1)  * 0.5);
     }  
	 
	 

	private void computeSpeed() throws JMException{
		double r1, r2;
		double W;
		XReal bestGlobal;


		

		W = updateRandomInertia();

	
		for (int i = 0; i < swarmSize_; i++) {
			XReal particle = new XReal(particles_.get(i)) ;
			XReal bestParticle = new XReal(best_[i]) ;

			bestGlobal = new XReal(   selectLeader_DNPSO(particles_.get(i), DNPSOConstants.F2, DNPSOConstants.MNEARESTNEIGHBORS)   ); 

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


	
	
	
    public Solution selectLeader_DNPSO(Solution particle, int F2, int MNEARESTNEIGHBORS) {

        ArrayList<Solution> neighbors;

        neighbors = new ArrayList<Solution>();


        Comparator equal =  new EqualSolutions();
        
        for (int i = 0; i < this.leaders_.size(); i++) {
        	
            if (   equal.compare(particle, this.leaders_.get(i))      != 0  ) {
                neighbors.add(this.leaders_.get(i));
            }
            
        }

        if (neighbors.size() > 0) {
            
        	for (int j = 0; j < neighbors.size(); j++) {
            	
            	if(neighbors.get(j) instanceof DNPSOSolution){
            		((DNPSOSolution) neighbors.get(j)).setDistanceFromNeighbor(this.calculateDistanceNeighbor(particle, neighbors.get(j), F2));
            	}else{
            		throw new RuntimeException("Error of implementation exception!!!!");
            	}            	            	            
               
            }

            Collections.sort(neighbors, new DistanceFromNeighborComparator());
            neighbors = this.trunkNeighbors(neighbors, MNEARESTNEIGHBORS);        
            Collections.sort(neighbors, new ObjectiveComparator(F2));
           
            return new Solution(neighbors.get(0));
            
        }
        
        else {
            return  new Solution(particle);
        }

   
    }

    
    
    private ArrayList<Solution> trunkNeighbors(ArrayList<Solution> neighbors, int MNEARESTNEIGHBORS) {

        // se o tamanho do arquivo externo for maior que o máximo, remove o excesso
        if (neighbors.size() > MNEARESTNEIGHBORS) {

            for (int i = neighbors.size() - 1; i >= MNEARESTNEIGHBORS; i--) {
                neighbors.remove(i);
            }
        }
        return neighbors;
    
    }
    
    
    private double calculateDistanceNeighbor(Solution particle, Solution neighbor, int F1) {
		 return this.distanceBetweenObjectivesWithoutOne(particle, neighbor, F1);
	}

	
	
	/** Returns the distance between two solutions in objective space.
	  *  @param solutionI The first <code>Solution</code>.
	  *  @param solutionJ The second <code>Solution</code>.
	  *  @return the distance between solutions in objective space.
	  */
	  private double distanceBetweenObjectivesWithoutOne(Solution solutionI, Solution solutionJ, int F1){                
	    double diff;    //Auxiliar var
	    double distance = 0.0;
	    //-> Calculate the euclidean distance
	    for (int nObj = 0; nObj < solutionI.numberOfObjectives();nObj++){
	      
	      if(nObj != F1){	
		      diff = solutionI.getObjective(nObj) - solutionJ.getObjective(nObj);
		      distance += Math.pow(diff,2.0);
	      }
	    
	    } // for   
	        
	    //Return the euclidean distance
	    return Math.sqrt(distance);
	  } // distanceBetweenObjectives.

	@Override
	protected SolutionSet initializationAlgorithm()
			throws ClassNotFoundException, JMException {
		
		initParams();

		//->Step 1 (and 3) Create the initial population and evaluate
		for (int i = 0; i < swarmSize_; i++){
			Solution particle = new DNPSOSolution(problem_);
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
			if(particles_.get(i) instanceof DNPSOSolution){
				DNPSOSolution particle = new DNPSOSolution((DNPSOSolution)particles_.get(i));
				leaders_.add(particle);
			}else{
				throw new RuntimeException("Error of implementation ");
			}
		}


		//-> Step 6. Initialize the memory of each particle
		for (int i = 0; i < particles_.size(); i++) {
			DNPSOSolution particle = new DNPSOSolution((DNPSOSolution)particles_.get(i));
			best_[i] = particle;
		}
		
		return this.leaders_;

	}

	@Override
	protected SolutionSet runIteration() throws JMException {
		
		computeSpeed();

		System.out.println(iteration_);

		//Compute the new positions for the particles_            
		computeNewPositions();

		//Evaluate the new particles_ in new positions
		for (int i = 0; i < particles_.size(); i++) {
			Solution particle = particles_.get(i);
			problem_.evaluate(particle);
		}

		//Actualize the archive          
		for (int i = 0; i < particles_.size(); i++) {
			DNPSOSolution particle = new DNPSOSolution((DNPSOSolution)particles_.get(i));
			leaders_.add(particle);
		}		

		
		//Update Particles of the Memory
		for (int i = 0; i < particles_.size(); i++) {
			int flag = dominance_.compare(particles_.get(i), best_[i]);
			if (flag == -1) { // the new particle is best than the older pBest        
				DNPSOSolution particle = new DNPSOSolution((DNPSOSolution)particles_.get(i));
				best_[i] = particle;
			}
		}
	
		iteration_++;
		
		return this.leaders_;
	}

	@Override
	protected SolutionSet getParetoFront() {
		
		return this.leaders_;
	}
	
	

	 

	
	
}
