package hidra.metaheuristics.mopsocdls;

import hidra.core.population.HIDRAPopulationAlgorithm;
import hidra.jmetal.core.Problem;
import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;
import hidra.jmetal.core.Variable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.wrapper.XReal;



public class MOPSOCDLS extends HIDRAPopulationAlgorithm{

	private int swarmSize_;

	private int archiveSize_;

	/*private int maxIterations_;

	private int iteration_;
*/
	private SolutionSet particles_;

	private Solution[] best_;

	private CDLSArchive leaders_;

	private double[][] speed_;

	private Comparator dominance_;

	private Comparator crowdingDistanceComparator_;

	private Distance distance_;



	private double C1_;
	private double C2_;
	private double wMax_;
	private double wMin_;



	public MOPSOCDLS(Problem problem) {                
		super(problem);
		/*swarmSize_ = MOPSOCDLSConstants.swarmSize;
		maxIterations_ = MOPSOCDLSConstants.maxIterations;*/
	}



	public void initParams() {
		
		swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
		archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
		maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
			
		iteration_ = 0 ;

		particles_ = new SolutionSet(swarmSize_);
		best_ = new Solution[swarmSize_];
		leaders_ = new CDLSArchive(archiveSize_, problem_.getNumberOfObjectives());

		// Create comparators for dominance and crowding distance
		dominance_ = new DominanceComparator();
		crowdingDistanceComparator_ = new CrowdingDistanceComparator();
		distance_ = new Distance();
		//distance = new UtilDistance();

		// Create the speed vector
		speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];


		HashMap<String, Object> parameters = new HashMap<String, Object>();


		parameters.put("maxIterations_",maxIterations_);
		//parameters.put("percMutation_",MOPSOCDRConstants.MUTATION_PERCENTUAL_MOPSO);

	


		this.C1_ = 1.49445;
		this.C2_ = 1.49445;
		this.wMax_ = 0.4;
		this.wMin_ = 0.0;
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





	/*@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {

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

		//Crowding the leaders_
		distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());

		//-> Step 6. Initialize the memory of each particle
		for (int i = 0; i < particles_.size(); i++) {
			Solution particle = new Solution(particles_.get(i));
			best_[i] = particle;
		}


		//-> Step 7. Iterations ..        
		while (iteration_ < maxIterations_) {

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
				Solution particle = new Solution(particles_.get(i));
				leaders_.add(particle);
			}

			//Crowding the leaders_
			distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());


			
			double sigma = (SIGMA_MAX - SIGMA_MIN) * ((maxIterations_ - iteration_)/ ((double)maxIterations_)) +  SIGMA_MIN;
			
			localSearch(sigma);
						
			
			//distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());
			
			//Update Particles of the Memory
			for (int i = 0; i < particles_.size(); i++) {
				int flag = dominance_.compare(particles_.get(i), best_[i]);
				if (flag == -1) { // the new particle is best than the older pBest        
					Solution particle = new Solution(particles_.get(i));
					best_[i] = particle;
				}else if(flag == 0){
					if (Math.random() > 0.5) {
						Solution particle = new Solution(particles_.get(i));
						best_[i] = particle;						
					}
				}
			}


			//Assign crowding distance to the leaders_
			distance_.crowdingDistanceAssignment(leaders_,
					problem_.getNumberOfObjectives());
			
			
			iteration_++;
		}
		return this.leaders_;



	}

*/

	
	
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

		//Crowding the leaders_
		distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());

		//-> Step 6. Initialize the memory of each particle
		for (int i = 0; i < particles_.size(); i++) {
			Solution particle = new Solution(particles_.get(i));
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
			Solution particle = new Solution(particles_.get(i));
			leaders_.add(particle);
		}

		//Crowding the leaders_
		distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());


		
		double sigma = (SIGMA_MAX - SIGMA_MIN) * ((maxIterations_ - iteration_)/ ((double)maxIterations_)) +  SIGMA_MIN;
		
		localSearch(sigma);
					
		
		//distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());
		
		//Update Particles of the Memory
		for (int i = 0; i < particles_.size(); i++) {
			int flag = dominance_.compare(particles_.get(i), best_[i]);
			if (flag == -1) { // the new particle is best than the older pBest        
				Solution particle = new Solution(particles_.get(i));
				best_[i] = particle;
			}else if(flag == 0){
				if (Math.random() > 0.5) {
					Solution particle = new Solution(particles_.get(i));
					best_[i] = particle;						
				}
			}
		}


		//Assign crowding distance to the leaders_
		distance_.crowdingDistanceAssignment(leaders_,
				problem_.getNumberOfObjectives());
		
		
		iteration_++;

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
		double wmax, wmin, W;
		XReal bestGlobal;


		wmax = wMax_;
		wmin = wMin_;

		W = inertiaWeight();

		
		SolutionSet aux      = new SolutionSet(this.leaders_.size());
		SolutionSet archive  = new SolutionSet(this.leaders_.size());
		
		
		for(int i=0; i < this.leaders_.size(); i++){
			aux.add(this.leaders_.get(i));
		}
		
		aux.sort(new CrowdingDistanceComparator());
		

		/*for(int i = this.leaders_.size()-1; i >= 0; i--){
			archive.add(aux.get(i));
		}
		*/
		
		archive = aux;
		
		//SolutionSet archive =  this.leaders_;

		for (int i = 0; i < swarmSize_; i++) {
			XReal particle = new XReal(particles_.get(i)) ;
			XReal bestParticle = new XReal(best_[i]) ;

			bestGlobal = new XReal(selectLeaderCD(particles_.get(i),archive ) ); 

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


	public static final double SIGMA = 0.2;
	
	public static final double SIGMA_MAX = 0.3;
	public static final double SIGMA_MIN = 0.1;
	
	
	
	public void localSearch(double sigma) throws JMException{
		
		SolutionSet aux      = new SolutionSet(this.leaders_.size());
		SolutionSet archive  = new SolutionSet(this.leaders_.size());
	
		
		
		for(int i=0; i < this.leaders_.size(); i++){
			aux.add(this.leaders_.get(i));
		}
		
		aux.sort(crowdingDistanceComparator_);
		

		/*for(int i = this.leaders_.size()-1; i >= 0; i--){
			archive.add(aux.get(i));
		}*/
		
		
		archive = aux;
		
		ArrayList<Solution> tenPercentlessCrowdedExternalArchive = new ArrayList<Solution>();
		int tenPercentExternalArchiveSize = (int) (archive.size() * 0.1);
		int count = 0;
		int index = 0;
		
		
		if (tenPercentExternalArchiveSize > 0) {
			
			for(int i=0; i < tenPercentExternalArchiveSize  ; i++){
				tenPercentlessCrowdedExternalArchive.add( archive.get(i) );
			}
			
		}else{
			return;
		}
		
		
		double step = Double.NEGATIVE_INFINITY;
		
		for(int var = 0; var < problem_.getNumberOfVariables(); var++){
			step =  Math.max(step, problem_.getUpperLimit(var)-problem_.getLowerLimit(var));			
		}
		
		step = step * sigma;
		
		for(int i=0; i < tenPercentlessCrowdedExternalArchive.size()  ; i++){
			
			Solution x = tenPercentlessCrowdedExternalArchive.get(i);			
			Solution s = new Solution(x);
			XReal xreal = new XReal( s );
			
			for(int var = 0; var <  s.numberOfVariables();  var++){
															
				double value = xreal.getValue(var);
				
				double lambda1 = Math.random();
				double lambda2 = Math.random();
				
				if(lambda1 > 0.5){										
					value = value + lambda2 * step;					
				}else{					
					value = value - lambda2 * step;					
				}
				
				value = Math.min(problem_.getUpperLimit(var), value);
				value = Math.max(problem_.getLowerLimit(var), value);
				
				xreal.setValue(var, value);
			}
			
			problem_.evaluate(s);
			problem_.evaluateConstraints(s);
			int dominance = dominance_.compare( s, x);
			
			if(dominance < 1){
				
				boolean inserted = this.leaders_.add(s);
				
				if(inserted){
					this.leaders_.remove(x);
				}
				
			}
			
		}
				
		
	}


	public Solution selectLeaderCD(Solution particle, SolutionSet archive)
	{
		ArrayList<Solution> tenPercentlessCrowdedExternalArchive;
		int tenPercentExternalArchiveSize, index, toSort;
		Comparator dominanceOperator = new DominanceComparator();
		Random r;

		Solution leader = null;

		tenPercentlessCrowdedExternalArchive = new ArrayList<Solution>();
		tenPercentExternalArchiveSize = (int) (archive.size() * 0.1);
		toSort = tenPercentExternalArchiveSize;
		index = 0;
		r = new Random();

		if (tenPercentExternalArchiveSize > 0) {

			while ((toSort > 0) && (index < archive.size())) {

				int dominance = dominanceOperator.compare(particle, archive.get(index));

				if (dominance == 1) {
					tenPercentlessCrowdedExternalArchive.add(   new Solution( archive.get(index) )       );
					toSort--;
				}
				index++;
			}

			if (toSort == 0) {
				leader = tenPercentlessCrowdedExternalArchive.get(r.nextInt(tenPercentExternalArchiveSize));
			} else {
				leader = archive.get(r.nextInt(archive.size()));
			}
		} else {
			leader = archive.get(r.nextInt(archive.size()));
		}

		return leader;
	}



	@Override
	protected SolutionSet getParetoFront() {
		// TODO Auto-generated method stub
		return this.leaders_;
	}



	












}
