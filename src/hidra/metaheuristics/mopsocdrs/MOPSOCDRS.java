/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computação - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


 */

package hidra.metaheuristics.mopsocdrs;







import hidra.core.population.HIDRAPopulationAlgorithm;
import hidra.gui.PlotManager;
import hidra.jmetal.core.Operator;
import hidra.jmetal.core.Problem;
import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;
import hidra.jmetal.core.Variable;
import hidra.metaheuristics.mopsocdr.util.ParetoFrontUtil;
import hidra.metaheuristics.mopsocdr.util.RoletteWheelSelection;
import hidra.metaheuristics.mopsocdr.util.TurbulenceMutation;

import java.util.Comparator;
import java.util.HashMap;

import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.wrapper.XReal;

import org.jfree.chart.ChartPanel;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class MOPSOCDRS extends HIDRAPopulationAlgorithm{

	private int swarmSize_;

	private int archiveSize_;

	/*private int maxIterations_;

	private int iteration_;
*/
	private SolutionSet particles_;

	private Solution[] best_;

	private CrowdingArchive leaders_;

	private double[][] speed_;

	private Comparator dominance_;

	private Comparator crowdingDistanceComparator_;

	private Distance distance_;

	private Operator turbulence_;	

	private RoletteWheelSelection rollete;

	private SwarmBehavior swarmBehavior;
	private ExternalArchiveAnalyser externalArchiveAnalyser_;

	private static final double MUTATION_PERCENTUAL_MOPSO = 0.5;
	
	private PlotManager plotManager_;
	
	private double C1_;
	private double C2_;
	private double wMax_;
	private double wMin_;


	public MOPSOCDRS(Problem problem) {                
		super (problem);
		//swarmSize_ = 20;
		//maxIterations_ = 10000;
		
	    this.plotManager_ = new PlotManager();
	    this.plotManager_.setupPlotExternalArchive();
	    this.plotManager_.setupPlotSwarm();
	}



	public void initParams() {	
		
		swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
		archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
		maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
	
		
	
		this.rollete = new RoletteWheelSelection();

		swarmBehavior = SwarmBehavior.UseExternalArchive;
		externalArchiveAnalyser_ = new ExternalArchiveAnalyser(problem_);
	
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


		HashMap<String, Object> parameters = new HashMap<String, Object>();


		parameters.put("maxIterations",maxIterations_);
		parameters.put("percMutation",MUTATION_PERCENTUAL_MOPSO);

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
		
		//Crowding the leaders_
				ParetoFrontUtil.crowdingDistanceAssignmentToMOPSOCDR(leaders_, problem_.getNumberOfObjectives());

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

		//System.out.println(iteration_);

		//Compute the new positions for the particles_            
		computeNewPositions();

		//Mutate the particles_          
		mopsoMutation(iteration_, maxIterations_);

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
		

		switch(this.swarmBehavior.ordinal())
		{
			case 0: updatePbest();
			break;

			case 1: updatePbestUsingClanId();
				//System.out.println("Clan 1");
				
				
			break;
		}


		//This part of the code is necessary to control the computational cost of the algorithm
		 if((iteration_ % 1) == 0)
         {
                this.swarmBehavior = externalArchiveAnalyser_.DecideBehavior(this.leaders_, swarmBehavior);
         }

		
		 if ((iteration_ % 50) == 0)
         {
             plotManager_.plotExternalArchive(this.leaders_, iteration_ + 1);
             plotManager_.plotSwarm(this.particles_, iteration_ + 1);
         }
		
		
		iteration_++;

		return this.leaders_;
		
	}
	


	/*@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {

				

		//-> Step 7. Iterations ..        
		while (iteration_ < maxIterations_) {

			computeSpeed();

			System.out.println(iteration_);

			//Compute the new positions for the particles_            
			computeNewPositions();

			//Mutate the particles_          
			mopsoMutation(iteration_, maxIterations_);

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
			

			switch(this.swarmBehavior.ordinal())
			{
				case 0: updatePbest();
				break;
	
				case 1: updatePbestUsingClanId();
					//System.out.println("Clan 1");
					
					
				break;
			}


			//This part of the code is necessary to control the computational cost of the algorithm
			 if((iteration_ % 1) == 0)
	         {
	                this.swarmBehavior = externalArchiveAnalyser_.DecideBehavior(this.leaders_, swarmBehavior);
	         }

			
			 if ((iteration_ % 50) == 0)
             {
                 plotManager_.plotExternalArchive(this.leaders_, iteration_ + 1);
                 plotManager_.plotSwarm(this.particles_, iteration_ + 1);
             }
			
			
			iteration_++;
		}
		
		
		plotManager_.plotExternalArchive(this.leaders_, iteration_ + 1);
        plotManager_.plotSwarm(this.particles_, iteration_ + 1);
        plotManager_.getJFreeChartExternalArchive().setTitle("MOPSO-CDRS");
		
		
		return this.leaders_;

	}

*/	
	public void updatePbestUsingClanId(){
					
		for (int i = 0; i < particles_.size(); i++) {
			
			if(i % (problem_.getNumberOfObjectives()+1) == problem_.getNumberOfObjectives()){
								
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
				
									
			}else{
				int currObj = (i % (problem_.getNumberOfObjectives()+1));
				if( particles_.get(i).getObjective(currObj) < this.best_[i].getObjective(currObj)){
					 this.best_[i] = new Solution(particles_.get(i));
				}
			}
												
		}
			
	}

	
	
	public void updatePbest(){		
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
		XReal bestGlobal = null;


		wmax = wMax_;
		wmin = wMin_;

		W = inertiaWeight();

		double summatory = 0.0;


		SolutionSet front = new SolutionSet(leaders_.size());
		for (int i = 0; i < leaders_.size(); i++){
			front.add(leaders_.get(i));
		}

		front.sort(crowdingDistanceComparator_);

		for(int i=0; i < front.size() ;i++){
			summatory = summatory + front.get(i).getCrowdingDistance();
		}

		Solution[] candLeaders = getCandidateLeaders();

		for (int currPart = 0; currPart < swarmSize_; currPart++) {
			
			XReal particle = new XReal(particles_.get(currPart)) ;
			XReal bestParticle = new XReal(best_[currPart]) ;

			//bestGlobal = new XReal(rollete.execute(front, summatory));

			switch(this.swarmBehavior.ordinal())
			{
				case 0: bestGlobal = 
						new XReal(rollete.execute(front, summatory));
				break;
	
				case 1: 
	
					if(currPart % (problem_.getNumberOfObjectives()+1) == problem_.getNumberOfObjectives()){
						bestGlobal = new XReal(rollete.execute(front, summatory));
					}else{					
						Solution leaderObj = 
								new Solution(selectLeaderAccording(particles_.get(currPart),candLeaders,currPart));
						bestGlobal = 	new XReal(leaderObj);
					}
					break;
			}


			for (int var = 0; var < particle.getNumberOfDecisionVariables(); var++) {

				// generate stochastic components for each dimension
				r1 = PseudoRandom.randDouble(0.0, 1.0);
				r2 = PseudoRandom.randDouble(0.0, 1.0);


				//Computing the velocity of this particle 
				speed_[currPart][var] =  (W *
						speed_[currPart][var] +
						C1_ * r1 * (bestParticle.getValue(var) -
								particle.getValue(var)) +
								C2_ * r2 * (bestGlobal.getValue(var) -
										particle.getValue(var)));

				double vmax = particle.getUpperBound(var) - particle.getLowerBound(var);

				speed_[currPart][var] = 
						Math.signum(speed_[currPart][var]) * 
						Math.min(Math.abs(speed_[currPart][var]),vmax);




			}
		}

	} // computeSpeed



	public Solution selectLeaderAccording(Solution solution,Solution[] candLeaders,int index)
	{

		//throw new RuntimeException("No Problem");
		
		int obj = index % (problem_.getNumberOfObjectives()+1);

		if(solution.getObjective(obj) < candLeaders[obj].getObjective(obj)){
			return solution;
		}else{
			return candLeaders[obj];
		}

	}


	public Solution[] getCandidateLeaders(){

		int numObj = problem_.getNumberOfObjectives();
		double minObj = Double.POSITIVE_INFINITY;

		Solution[] candLeaders = new Solution[numObj];

		for(int j=0; j < numObj; j++){

			minObj = Double.POSITIVE_INFINITY;
			
			for(int i=0; i  < this.leaders_.size()  ; i++){

				if(minObj > this.leaders_.get(i).getObjective(j) ){
					minObj =  this.leaders_.get(i).getObjective(j);
					candLeaders[j] = this.leaders_.get(i);
				}

			}

		}

		return candLeaders;

	}



	/**
	 * Apply a mutation operator to some particles in the swarm
	 * @throws JMException 
	 */
	private void mopsoMutation(int actualIteration, int totalIterations) throws JMException {

		turbulence_.setParameter("currentIteration",actualIteration);

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



	public ChartPanel getChartPanelExternalArchive()
    {
        return plotManager_.getChartPanelExternalArchive();
    }

    public void setChartPanelExternalArchive(ChartPanel chartPanelExternalArchive)
    {
    	plotManager_.setChartPanelExternalArchive(chartPanelExternalArchive);
    }

    public ChartPanel getChartPanelSwarm()
    {
        return plotManager_.getChartPanelSwarm();
    }

    public void setChartPanelSwarm(ChartPanel chartPanelSwarm)
    {
    	plotManager_.setChartPanelSwarm(chartPanelSwarm);
    }



	
	

}
