/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computa��o - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


 */

package hidra.metaheuristics.cssmopso;

import hidra.core.population.HIDRAPopulationAlgorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.ObjectiveComparator;
import jmetal.util.wrapper.XReal;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class CSSMOPSO extends HIDRAPopulationAlgorithm{

	private int swarmSize_;

	private int archiveSize_;

	//private int maxIterations_;

	//private int iteration_;

	private SolutionSet particles_;

	private Solution[] best_1_;
	private Solution[] best_2_;
	private double[] datumPoint_;
	
	
	private CSSArchive leaders_;
	private double[][] speed_;	
	private Distance distance_;
	private double inertia_;
	

	public CSSMOPSO(Problem problem) {
		super(problem);
		/*swarmSize_ = 20;
		maxIterations_ = 10000;*/
	}

	public void initParams() {
		
		swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
		archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
		maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
	
		
		
		
		iteration_ = 0 ;

		particles_ = new SolutionSet(swarmSize_);

		best_1_ = new Solution[swarmSize_];
		best_2_ = new Solution[swarmSize_];

		leaders_ 
			= new CSSArchive(archiveSize_,problem_.getNumberOfObjectives());

		// Create comparators for dominance and crowding distance
		
		distance_ = new Distance();
		//distance = new UtilDistance();

		// Create the speed vector
		speed_ = new double[swarmSize_][problem_.getNumberOfVariables()];
	
		
		inertia_ = CSSMOPSOConstants.MAXINERTIA;
		
	}


/*	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		
		initializationAlgorithm();

	
		if(GlobalSettings.DEBUG){
			
		
				
				String tmpPath = (String) getInputParameter("pathParetoFront");
				String pathParetoFront = tmpPath + "/IFUN_0";
				String pathParetoSet = tmpPath + "/IVAR_0";
				
				
				this.leaders_.printObjectivesToFile(pathParetoFront);
				this.leaders_.printObjectivesToFile(pathParetoSet);
				
			
			
		}
	
		
		
		while (iteration_ < maxIterations_){

			SolutionSet	solutionSet = runIteration();
			
			if(GlobalSettings.DEBUG){
			
				if(   (iteration_ % GlobalSettings.resolution) == 0){
					
					String tmpPath = (String) getInputParameter("pathParetoFront");
					String pathParetoFront = tmpPath + "/IFUN_" + iteration_ ;
					String pathParetoSet   = tmpPath + "/IVAR_" + iteration_ ;
					
					
					solutionSet.printObjectivesToFile(pathParetoFront);
					solutionSet.printObjectivesToFile(pathParetoSet);
					
				}
				
			}
		
			
		}

		
		if(GlobalSettings.DEBUG){
				String tmpPath = (String) getInputParameter("pathParetoFront");
				String pathParetoFront = tmpPath + "/IFUN_" + iteration_ ;				
				String pathParetoSet = tmpPath + "/IVAR_" + iteration_;
				
				this.leaders_.printObjectivesToFile(pathParetoFront);
				this.leaders_.printObjectivesToFile(pathParetoSet);
			
		}
		
		
		return this.leaders_;
	}*/

	protected SolutionSet initializationAlgorithm() throws ClassNotFoundException,JMException {
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
		
		return this.leaders_;
	}

	
	@Override
	protected SolutionSet getParetoFront() {
		// TODO Auto-generated method stub
		return this.leaders_;
	}

	
	protected CSSArchive runIteration() throws JMException {
		selectGBest2();
		datumPoint_ = calculateDatumPoint();
		computeSpeed();
		turbulence_CSS();
		
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
		
		if(this.leaders_.size() > archiveSize_ ){
			executeClusterMethod();
		}
		
		
		if(this.leaders_.size() > archiveSize_ ){
			trunkExternalArchive();
		}
		
		updateLinearInertia(CSSMOPSOConstants.MAXINERTIA, 
				CSSMOPSOConstants.MININERTIA, 
				iteration_, 
					maxIterations_);
		
		
		iteration_++;
		    		
		//System.out.println(iteration_);
		
		return this.leaders_;
	
	}

	
/*	
	@Override
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

	
		
		while (iteration_ < maxIterations_){

			selectGBest2();
			datumPoint_ = calculateDatumPoint();
			computeSpeed();
			turbulence_CSS();
			
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
			
			if(this.leaders_.size() > archiveSize_ ){
				executeClusterMethod();
			}
			
			
			if(this.leaders_.size() > archiveSize_ ){
				trunkExternalArchive();
			}
			
			updateLinearInertia(CSSMOPSOConstants.MAXINERTIA, 
					CSSMOPSOConstants.MININERTIA, 
					iteration_, 
						maxIterations_);
			
			
			iteration_++;
	            
	        System.out.println(iteration_);
			
		}

		return this.leaders_;
	}
*/

	
	public void updateLinearInertia(double maxInertia, double minInertia, int currentIteration, int maxIteration) {
                
        inertia_ = maxInertia - ((maxInertia - minInertia) * (  ((double)currentIteration)  / ((double) maxIteration)));
        
        if ( inertia_ < 0.0 ) {
            inertia_ = 0.0;
        }
        
        
    }
 
	

	private void computeSpeed() throws JMException{


		for (int i = 0; i < swarmSize_; i++) {

			Solution solution = particles_.get(i);

			selectGBest1(solution,i);

			updatePosition(solution,i,inertia_);

		}


	} // computeSpeed







	public void executeClusterMethod() {
		double radiusCluster;
		ArrayList<Solution> externalArchiveClone = new ArrayList<Solution>();
		Solution selectedParticle;
		Solution current;
		double distance;

		Random r = new Random();

		radiusCluster = calculateRadiusCluster();

		for (int i = 0; i < this.leaders_.size(); i++) {
			externalArchiveClone.add(this.leaders_.get(i));
		}

		while (externalArchiveClone.size() > 0) {
			int index = r.nextInt(externalArchiveClone.size());
			selectedParticle = externalArchiveClone.get(index);
			externalArchiveClone.remove(selectedParticle);

			for (int i = 0; i < externalArchiveClone.size(); i++) {	
				current = externalArchiveClone.get(i);
				distance = 
					distance_.distanceBetweenObjectives(selectedParticle, current);

				if (distance < radiusCluster) {
					externalArchiveClone.remove(i);
					this.leaders_.remove(current);
				}
			}
		}

	}


	private void trunkExternalArchive(){

		
	    Comparator crowdingDistance_ = new CrowdingDistanceComparator();
	    Distance distance_         = new Distance();
		distance_.crowdingDistanceAssignment(this.leaders_,problem_.getNumberOfObjectives());
		
		/*ArrayList<Solution> archive = new ArrayList<Solution>();
		
		for(int i=0; i < this.leaders_.size() ; i++){
			archive.add(this.leaders_.get(i));
		}
		 */
		
		this.leaders_.sort(crowdingDistance_);
		
		while(this.leaders_.size() > archiveSize_){
			
			this.leaders_.remove(this.leaders_.size()-1);
			
		}
		 
		 
	}
	

	private double calculateRadiusCluster() {
		double[] minValues = new double[problem_.getNumberOfObjectives()];
		double[] maxValues = new double[problem_.getNumberOfObjectives()];
		double[] d1 = new double[2];
		double[] d2 = new double[2];

		double sumDistances = 0.0;

		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			minValues[i] = getDimensionMinValue_CSS(i);
			maxValues[i] = getDimensionMaxValue_CSS(i);
		}

		for (int j = 0; j < problem_.getNumberOfObjectives() - 1; j++) {
			d1[0] = minValues[j];
			d1[1] = maxValues[j + 1];

			d2[0] = maxValues[j];
			d2[1] = minValues[j + 1];

			sumDistances += this.calculateEuclidianDistance(d1, d2);
		}

		return sumDistances / (2 * this.leaders_.size());
	}


	public double getDimensionMinValue_CSS(int dimension) {
		double minValue = Double.POSITIVE_INFINITY;

		for (int j = 0; j < this.leaders_.size(); j++) {
			if (this.leaders_.get(j).getObjective(dimension) < minValue) {
				minValue = this.leaders_.get(j).getObjective(dimension);
			}
		}

		return minValue;
	}


	private double calculateEuclidianDistance(double[] d1, double[] d2) {
		double sum = 0.0;

		for ( int i = 0; i < d1.length; i++ ) {
			sum += Math.pow( d1[i] - d2[i], 2);
		}

		return Math.sqrt(sum);
	}



	public void turbulence_CSS() {
		ArrayList<Integer> indexes;
		Random r;
		Integer index;

		indexes = new ArrayList<Integer>();
		r = new Random();

		for (int i = 0; i < particles_.size(); i++) {
			indexes.add(i);
		}

		int k = r.nextInt(particles_.size());

		while (k > 0) {
			index = indexes.get(r.nextInt(indexes.size()));
			indexes.remove(index);
			speed_[index] = this.applyTurbulence(speed_[index]);                        
			k--;
		}

	}


	private double[] applyTurbulence(double[] velocities) {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		Random r = new Random();
		Integer index;

		for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
			indexes.add(i);
		}

		int k = r.nextInt(velocities.length);

		while (k > 0) {
			index = indexes.get(r.nextInt(indexes.size()));
			indexes.remove(index);
			velocities[index] = this.applyGaussianMutation(velocities[index]);
			k--;
		}
		return velocities;
	}


	private double applyGaussianMutation(double value) {
		Random r = new Random();

		value += (CSSMOPSOConstants.STANDARDDEVIATIONMUTATIONGAUSSIAN * r.nextGaussian());

		return value;
	}




	public void updatePosition(Solution particle_CSS, int index, double inertia) throws JMException {
		double[] velocities = speed_[index].clone();
		
		XReal xReal = new XReal(particle_CSS);

		double r1;
		double r2;
		double g1d;
		double g2d;
		double minBound;
		double maxBound;

		double position = 0.0;

		double cognitionLearningRate = CSSMOPSOConstants.COGNITION_LEARNIN_GRATE;
		double socialLearningRate = CSSMOPSOConstants.SOCIAL_LEARNING_RATE;

		for (int i = 0; i < problem_.getNumberOfVariables(); i++) {
			r1 = Math.random();
			r2 = Math.random();

			g1d = new XReal(best_1_[index]).getValue(i);
			g2d = new XReal(best_2_[index]).getValue(i);             	            

			minBound = xReal.getLowerBound(i);
			maxBound = xReal.getUpperBound(i);


			velocities[i] = (inertia * velocities[i]) +
			(cognitionLearningRate * r1 * (g1d - xReal.getValue(i))) +
			(socialLearningRate * r2 *    (g2d - xReal.getValue(i)));

			if (Math.abs(velocities[i]) > (maxBound - minBound)) {
				velocities[i] = Math.signum(velocities[i]) * (maxBound - minBound);
			}

			
			position = xReal.getValue(i);
			position = position + velocities[i]; 


			if (position < minBound) {
				position = minBound;
				velocities[i] *= -1;
			} else if (position > maxBound) {
				position = maxBound;
				velocities[i] *= -1;
			}


			xReal.setValue(i, position);
			speed_[index][i] = velocities[i];

		}


	}



	public void selectGBest1(Solution solution, int indexSol) throws JMException {
		int indexMaxExternalArchiveCosAngle = 0;
		double angleValue;
		double maxCosAngle = -Double.MAX_VALUE;

		for (int i = 0; i <  this.leaders_.size(); i++) {
			angleValue = this.getCosAngleValue(solution,this.leaders_.get(i));

			if (angleValue > maxCosAngle) {
				maxCosAngle = angleValue;
				indexMaxExternalArchiveCosAngle = i;
			}
		}

		best_1_[indexSol] = this.leaders_.get(indexMaxExternalArchiveCosAngle);

	}

	private double getCosAngleValue(Solution particle_CSS, Solution aExternalArchiveParticle) throws JMException {

		int numObj = problem_.getNumberOfObjectives();

		double[] datumExternalArchiveLine = new double[numObj];
		double[] particleExternalArchiveLine = new double[numObj];
		double cosAngleValue;

		for (int i = 0; i < numObj; i++) {
			datumExternalArchiveLine[i] = datumPoint_[i] - aExternalArchiveParticle.getObjective(i);
			
			particleExternalArchiveLine[i] = 
				Math.abs(particle_CSS.getObjective(i) - aExternalArchiveParticle.getObjective(i));
			
			//particleExternalArchiveLine[i] = 
				//particle_CSS.getObjective(i) - aExternalArchiveParticle.getObjective(i);
			
		}

		if (!this.equalParticles(particle_CSS, aExternalArchiveParticle)) {
			cosAngleValue = this.getCosAngleValue(datumExternalArchiveLine, particleExternalArchiveLine);
		} else {
			cosAngleValue = 1.0;
		}

		return cosAngleValue;
	}


	public double[] calculateDatumPoint() {
		double[] datumPoint = new double[problem_.getNumberOfObjectives()];

		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			datumPoint[i] = this.getDimensionMaxValue_CSS(i);
		}

		return datumPoint;
	}




	public boolean equalParticles(Solution particle1, Solution particle2) throws JMException {
		boolean isEquals = true;
		XReal xReal1 = new XReal(particle1);
		XReal xReal2 = new XReal(particle2);


		for (int i = 0; i < xReal1.getNumberOfDecisionVariables(); i++) {
			if (xReal1.getValue(i) != xReal2.getValue(i)) {
				isEquals = false;
				i = xReal1.getNumberOfDecisionVariables();
			}
		}

		return isEquals;
	}


	private double getCosAngleValue(double[] datumExternalArchiveLine, double[] particleExternalArchiveLine) {
		double innerProduct = 0.0;
		double datumExternalArchiveLineNorm;
		double particleExternalArchiveLineNorm;

		for (int i = 0; i < datumExternalArchiveLine.length; i++) {
			innerProduct += (datumExternalArchiveLine[i] * particleExternalArchiveLine[i]);
		}

		datumExternalArchiveLineNorm = this.calculateNorm(datumExternalArchiveLine);
		particleExternalArchiveLineNorm = this.calculateNorm(particleExternalArchiveLine);

		return (innerProduct / (datumExternalArchiveLineNorm * particleExternalArchiveLineNorm));
	}

	private double calculateNorm(double[] vector) {
		double innerProduct = 0;

		for (int i = 0; i < vector.length; i++) {
			innerProduct += (vector[i] + vector[i]);
		}

		return Math.sqrt(innerProduct);
	}


	public double getDimensionMaxValue_CSS(int dimension) {
		double maxValue = Double.NEGATIVE_INFINITY;
		//maxValue  = Double.MIN_VALUE;
		
		for (int j = 0; j < this.leaders_.size(); j++) {
			if (this.leaders_.get(j).getObjective(dimension) > maxValue) {
				maxValue = this.leaders_.get(j).getObjective(dimension);
			}
		}

		return maxValue;
	}


	public void selectGBest2() {
		
		Random r = new Random();
		int selecetedObjective = r.nextInt(problem_.getNumberOfObjectives());

		List<Particle> swarm =  new ArrayList<Particle>();
		
		for(int i = 0; i < particles_.size()  ;i++){			
			swarm.add(new Particle(particles_.get(i), i));
		}
		
		
		Collections.sort(swarm, new ParticleObjectiveComparator(selecetedObjective));
			
		
		List<Solution> archive2 =  new ArrayList<Solution>();
		
		for(int i = 0; i < this.leaders_.size() ;i++){			
			archive2.add(this.leaders_.get(i));
		}
		
		Collections.sort(archive2, new ObjectiveComparator(selecetedObjective));
							
		List<Solution> archive =  new ArrayList<Solution>();
				
		
		for(int i = archive2.size()-1; i >= 0 ;i--){			
			archive.add(archive2.get(i));
		}
				
		for (int j = 0; j < swarm.size(); j = j + 2) {

			boolean leaderFinded = false;
			
			Solution s   = swarm.get(j).getSolution(); 
			
			for (int k = 0; k < archive.size(); k++) {

				if (s.getObjective(selecetedObjective) >= 
					archive.get(k).getObjective(selecetedObjective)) {
					best_2_[swarm.get(j).getIndex()] = archive.get(k);					
					leaderFinded = true;
					k = archive.size();
				}
			}

			if (!leaderFinded) {
				best_2_[swarm.get(j).getIndex()] = archive.get(archive.size() - 1);				
			}
		}
		
		archive = archive2;
		
		for (int j = 1; j < swarm.size(); j = j + 2) {

			boolean leaderFinded = false;

			for (int k = 0; k < archive.size(); k++) {

				Solution s   = swarm.get(j).getSolution(); 
				if (s.getObjective(selecetedObjective) 
						<= archive.get(k).getObjective(selecetedObjective)) {					
					best_2_[swarm.get(j).getIndex()] = archive.get(k);
					leaderFinded = true;
					k = archive.size();
				}
			}

			if (!leaderFinded) {
				best_2_[swarm.get(j).getIndex()] = archive.get(archive.size() - 1);
			}
		}


	}

	

	


}
