package hidra.many.metaheuristics.mofssv1;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.ChartPanel;


import com.sun.tools.internal.xjc.model.Populatable;

import hidra.gui.PlotManager;
import hidra.jmetal.core.*;
import hidra.qualityIndicator.Hypervolume;
import hidra.qualityIndicator.QualityIndicator;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.wrapper.XReal;
import jmetal.util.*;

/**
 * This class implements the MOFSS algorithm. 
 * 
 * 
 *     VERSÌO COM ALTERA‚OES SOLICITADAS POR CARMELO EM 11/12 (adequado ao FSS original)
 *     
 *     
 * 
 */
public class MOFSSv8 extends Algorithm {

  /**
   * Constructor
   * @param problem Problem to solve
   */
	
	private int swarmSize_;
	private int maxIterations_;
	private int archiveSize_;
	private int iterations_;
	private SolutionSet particles_;
	private Solution barycenter_;
	private Comparator dominance_;
	private Distance distance_;
	private Comparator crowdingDistanceComparator_;
	private double[] delta_f_;
	private double[][] delta_x_;
	private double step_;
	private double totFitness_;
	private double lastTotFitness_;
	
	QualityIndicator indicators_;
	
	private double stepini_;
	private double stepfin_;
	private double maxDeltaF_;
	private int minFitness_;
	private int maxFitness_;
	
	private double trueHypervolume_;
	private Hypervolume hy_;
	private SolutionSet trueFront_;
	private CrowdingArchive leaders_;
	boolean success_;
	
	private int countD_;
	private int countND_;
	
	private PlotManager plotManager_;
	
	private int sleeptime_ = 100;
	
	
	
  public MOFSSv8(Problem problem) {
    super (problem) ;
    
    stepini_ = (problem_.getUpperLimit(0)) / 2;
    stepfin_ = 0.000001;
    maxDeltaF_ = -9999;
    minFitness_ = 1;
    maxFitness_ = 500;
    
    this.plotManager_ = new PlotManager();
    this.plotManager_.setupPlotExternalArchive();
    this.plotManager_.setupPlotSwarm();
    
  } // MOFSS
  
  public void initParams(){
	  swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
	  maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
	  archiveSize_ = ((Integer) getInputParameter("archiveSize")).intValue();
	  indicators_ = (QualityIndicator) getInputParameter("indicators");
	  
	  leaders_ = new CrowdingArchive(archiveSize_, problem_.getNumberOfObjectives());
	  
	  delta_f_ = new double[swarmSize_];
	  delta_x_ = new double[swarmSize_][problem_.getNumberOfVariables()];

	  iterations_ = 1;
	  totFitness_ = 0;

	  success_ = false;

	  particles_ = new SolutionSet(swarmSize_);

	  // Create comparators for dominance and crowding distance
	  dominance_ = new DominanceComparator();
	  distance_ = new Distance();
	  crowdingDistanceComparator_ = new CrowdingDistanceComparator();
	  
	  System.out.println(problem_.getUpperLimit(0) + " | " + problem_.getLowerLimit(0) + " | " + this.stepini_);
  }
  
  private double calculateStep(){
	  return stepini_ - (stepini_ - stepfin_) * ((double) iterations_/maxIterations_);
	  
	}
  
  private double calculateStepNonLinear(){
	  double aux = (problem_.getUpperLimit(0) - problem_.getLowerLimit(0)) * 0.1;
	  
	  return stepini_ - Math.sqrt(   ((1 - (Math.pow(iterations_, 2)) / Math.pow(maxIterations_, 2))) * Math.pow(aux, 2)     );
  }
  
  private int getLeadersCrowdingDistanceMax(){
	  int max = 0;
	  
	  for (int i=1; i<leaders_.size(); i++){
		  if ((leaders_.get(max).getCrowdingDistance() != Double.POSITIVE_INFINITY) &&
			  (leaders_.get(i).getCrowdingDistance() != Double.POSITIVE_INFINITY)){
			  if (crowdingDistanceComparator_.compare(leaders_.get(max), leaders_.get(i)) == 1) {
				  max = i;
			  }
		  }
	  }
	  
	  return max;
  }
  
  private int roletteWheelSelection(SolutionSet solutionSet, double summatory){
	  double rouletteValue = 0.0; 
	  double partial = 0.0;
	  int randomElement = 0;
	  int currentSolution = 0;
	  rouletteValue = summatory * Math.random();
		
	  if(summatory !=0){
		  do{
			  if (solutionSet.get(currentSolution).getCrowdingDistance() != Double.POSITIVE_INFINITY){
				  partial += solutionSet.get(currentSolution).getCrowdingDistance();
			  }
			  currentSolution++;
		  }while(partial < rouletteValue);
		  
		  return (currentSolution-1);
	  }else{
		  randomElement = (int) (Math.random() * solutionSet.size() );
		  return (randomElement);
	  }		
  }
  
  private void calculateIndivOper() throws JMException, ClassNotFoundException{
	  lastTotFitness_ = totFitness_;
	  int flag = 0;
	  
	  SolutionSet olderParticles = new SolutionSet(swarmSize_);
	  
	  	/* RoletteWheel */
	  	double summatory = 0.0;
	  	
	  	SolutionSet front = new SolutionSet(leaders_.size());
		for (int i = 0; i < leaders_.size(); i++){
			front.add(leaders_.get(i));
		}
		
		front.sort(crowdingDistanceComparator_);
		
		for(int i=0; i < front.size() ;i++){
			if (front.get(i).getCrowdingDistance() != Double.POSITIVE_INFINITY){
				summatory = summatory + front.get(i).getCrowdingDistance();
			}
		}
		/* RoletteWheel */
	  
	  for (int i=0; i<swarmSize_; i++){ //para cada peixe do cardume
		  Solution particleNew = new Solution (problem_);
		  Solution olderParticle = new Solution (particles_.get(i));
		  olderParticles.add(olderParticle);
		  
		  XReal particle = new XReal (particles_.get(i));
		  
		  //int leadersRandom = (int) (Math.random()*leaders_.size());
		  int leadersRoletteWheel = roletteWheelSelection(front, summatory);
		  
		  System.out.println("CDMAX["+ leadersRoletteWheel + "] = " + front.get(leadersRoletteWheel).getCrowdingDistance());
		  
		  // S— funciona se o tipo for RealSolutionType
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens‹o de cada peixe
			  //particle.setValue(j, PseudoRandom.randDouble(problem_.getLowerLimit(j), problem_.getUpperLimit(j)));
			  
			  delta_x_[i][j] = PseudoRandom.randDouble(particle.getValue(j),front.get(leadersRoletteWheel).getDecisionVariables()[j].getValue()) 
					  	       * step_;
			  
			  particleNew.getDecisionVariables()[j].setValue(olderParticle.getDecisionVariables()[j].getValue() + delta_x_[i][j]);
			  if (particleNew.getDecisionVariables()[j].getValue() < problem_.getLowerLimit(j)) {
				  particleNew.getDecisionVariables()[j].setValue(problem_.getLowerLimit(j));
		      }
			  if (particleNew.getDecisionVariables()[j].getValue() > problem_.getUpperLimit(j)) {
				  particleNew.getDecisionVariables()[j].setValue(problem_.getUpperLimit(j));
		      }
			  //System.out.println("Particle[" + i + "," + j + "]" + particleCopy.getDecisionVariables()[j].getValue() + " # " + particle.getValue(j));
		  }
		  
		  problem_.evaluate(particleNew);
		  flag = dominance_.compare(particleNew, particles_.get(i));
		  /*if (flag == -1) {
			  //nova part’cula domina part’cula anterior: aumenta peso do peixe
			  for (int j=0; j<problem_.getNumberOfVariables(); j++){
				  particle.setValue(j, particleNew.getDecisionVariables()[j].getValue()) ;
			  }
			  particles_.get(i).marked();
		  }else if (flag == 1){
			  //nova part’cula n‹o domina part’cula anterior: diminui peso do peixe
			  System.out.println("A) " + particles_.get(i).getDecisionVariables()[0].getValue() + ", " + particles_.get(i).getDecisionVariables()[1].getValue());
			  for (int j=0; j<problem_.getNumberOfVariables(); j++){
				  particle.setValue(j, particleNew.getDecisionVariables()[j].getValue()) ;
			  }
			  System.out.println("D) " + particles_.get(i).getDecisionVariables()[0].getValue() + ", " + particles_.get(i).getDecisionVariables()[1].getValue());
			  particles_.get(i).marked();
		  }else{
			  //indifirentes: n‹o altera peso do peixe
			  particles_.get(i).unMarked();
		  }*/
		  //int flag = dominance_.compare(particles_.get(i), best_[i]);
	      if (flag != 1) { // the new particle is best_ than the older remeber        
	    	  for (int j=0; j<problem_.getNumberOfVariables(); j++){
				  particle.setValue(j, particleNew.getDecisionVariables()[j].getValue()) ;
			  }
			  particles_.get(i).marked();
			  countD_++;
	      }else{
	    	  particles_.get(i).unMarked();
	    	  countND_++;
	      }
		  particles_.get(i).setRank(flag);
		  problem_.evaluate(particles_.get(i));
		  
		  //System.out.println("(" + particles_.get(i).getDecisionVariables()[0].getValue() + " / " + olderParticles.get(i).getDecisionVariables()[0].getValue() + " / " + particleNew.getDecisionVariables()[0].getValue() + " / " + flag + ")");
	  }
	  
	  //calcula o DeltaF e o DeltaX das part’culas
	  calculateDelta(olderParticles);
	  
	  totFitness_ = 0;
	  for (int i=0; i<swarmSize_; i++){
		  //if (particles_.get(i).isMarked()){
			  calculateFeedOper(i, particles_.get(i).getRank());
		  //}
		  totFitness_ += particles_.get(i).getFitness();
		  //System.out.println(totFitness_ + "/" + particles_.get(i).getFitness());
	  }
  }
  
  private void calculateDelta(SolutionSet olderParticles) throws JMException{
	  double f1=0; 
	  double f2=0;
	  
	  double[] x1 = new double[problem_.getNumberOfVariables()];
	  double[] x2 = new double[problem_.getNumberOfVariables()];
	  
	  maxDeltaF_ = -9999;
	  
	  for (int index=0; index<swarmSize_; index++){
		  f1 = f2 = 0;
		  
		  for (int i=0; i<problem_.getNumberOfObjectives(); i++){
			  f1 += Math.abs(particles_.get(index).getObjective(i));
			  f2 += Math.abs(olderParticles.get(index).getObjective(i));
		  }
		  f1 = f1/problem_.getNumberOfObjectives();
		  f2 = f2/problem_.getNumberOfObjectives();
		  
		  delta_f_[index] = (f1 - f2);
		  if (delta_f_[index] > maxDeltaF_){
			  maxDeltaF_ = delta_f_[index];
		  }
	  }
  }
  
  private void calculateFeedOper(int index, int flag) throws JMException{
	  // atualiza peso do peixe
	  
	  
	  /*System.out.println("(" + maxDeltaF_ + " * " + delta_f_[index] + ")");*/
	  
	  if (flag == 1){
		  particles_.get(index).setFitness(particles_.get(index).getFitness() - (delta_f_[index] / maxDeltaF_));
	  }else{
		  particles_.get(index).setFitness(particles_.get(index).getFitness() + (delta_f_[index] / maxDeltaF_));
	  }
	  
	  if (particles_.get(index).getFitness() < minFitness_) particles_.get(index).setFitness(minFitness_);
	  if (particles_.get(index).getFitness() > maxFitness_) particles_.get(index).setFitness(maxFitness_);
	  
	  
	  particles_.get(index).setRank(0);
  }
  
  private void calculateColInstOper() throws JMException{
	  double[] imDirection = new double[problem_.getNumberOfVariables()];
	  double[] sum1 = new double[problem_.getNumberOfVariables()];
	  double sum2;
	  
	  for (int i=0; i<problem_.getNumberOfVariables(); i++){
		  sum1[i] = 0;
	  }
	  sum2 = 0;
	  
	  for (int i=0; i<swarmSize_; i++){
		  if (particles_.get(i).isMarked()) {
			  for (int j=0; j<problem_.getNumberOfVariables(); j++){
				  sum1[j] += delta_x_[i][j] * (delta_f_[i] / maxDeltaF_);
			  }
			  sum2 += delta_f_[i] / maxDeltaF_;
			  particles_.get(i).unMarked();
		  }
	  }
	  
	  if (sum2 == 0){
		  for (int i=0; i<problem_.getNumberOfVariables(); i++){
			  imDirection[i] = 0;
		  }
	  }else{
		  for (int i=0; i<problem_.getNumberOfVariables(); i++){
			  sum1[i] = sum1[i]/problem_.getNumberOfVariables();
			  imDirection[i] = sum1[i]/sum2;
		  }
	  }
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens‹o de cada peixe
			  particle.setValue(j, particle.getValue(j) + imDirection[j]);
			  
			  if (particle.getValue(j) < problem_.getLowerLimit(j)) {
				  particle.setValue(j, problem_.getLowerLimit(j));
		      }
			  if (particle.getValue(j) > problem_.getUpperLimit(j)) {
				  particle.setValue(j, problem_.getUpperLimit(j));
		      }
		  }
	  }
  }
  
  private Solution calculateBarycenter() throws JMException, ClassNotFoundException {
	  double[] sum1 = new double[problem_.getNumberOfVariables()];
	  double sum2;
	  
	  Solution barycenter = new Solution (problem_);
	  sum2 = 0;
	  
	  for (int i=0; i<problem_.getNumberOfVariables(); i++){
		  sum1[i] = 0;
	  }
	  
	  for (int i=0; i<swarmSize_; i++){
		  //if (particles_.get(i).isMarked()) {
			  for (int j=0; j<problem_.getNumberOfVariables(); j++){
				  //System.out.println(particles_.get(i).getDecisionVariables()[j].getValue());
				  
				  sum1[j] += particles_.get(i).getDecisionVariables()[j].getValue() * particles_.get(i).getFitness();
			  }
			  //System.out.println(particles_.get(i).getFitness());
			  sum2 += particles_.get(i).getFitness();
			  //particles_.get(i).unMarked();
		  //}
	  }
	  
	  //System.out.println(sum2);
	  //System.out.println(problem_.getNumberOfVariables());
	  System.out.print("Baricentro: ");
	  for (int j=0; j<problem_.getNumberOfVariables(); j++){
		  sum1[j] = sum1[j] /problem_.getNumberOfVariables();
		  barycenter.getDecisionVariables()[j].setValue((sum1[j]/sum2));
		  System.out.println("{" + sum1[j] + "|" + sum2 + "}");
		  System.out.print(barycenter.getDecisionVariables()[j].getValue() + ", ");
	  }
	  System.out.println();
	  problem_.evaluate(barycenter);
	  return barycenter; 
  }
  
  private void calculateColVolOper() throws JMException, ClassNotFoundException{
	  barycenter_ = calculateBarycenter();
	  
	  //plotManager_.plotBarycentre(barycentre);
	  
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  double euclideanDistance = (distance_.distanceBetweenSolutions(particles_.get(i), barycenter_));
		  double stepVol = step_ / (particles_.get(i).getFitness() * 8);
		  
		  //System.out.println(euclideanDistance);
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  if (countD_ > countND_){
				  particle.setValue(j,particle.getValue(j) - (stepVol * PseudoRandom.randDouble(0,1) / 
						  			   (particle.getValue(j) - barycenter_.getDecisionVariables()[j].getValue()))/
						  				 euclideanDistance);
				  if (particle.getValue(j) < problem_.getLowerLimit(j)) {
					  particle.setValue(j, problem_.getLowerLimit(j));
			      }
				  if (particle.getValue(j) > problem_.getUpperLimit(j)) {
					  particle.setValue(j, problem_.getUpperLimit(j));
			      }
			  }else{
				  particle.setValue(j,particle.getValue(j) + (stepVol * PseudoRandom.randDouble(0,1) / 
			  			   (particle.getValue(j) - barycenter_.getDecisionVariables()[j].getValue()))/
			  				 euclideanDistance);
				  if (particle.getValue(j) < problem_.getLowerLimit(j)) {
					  particle.setValue(j, problem_.getLowerLimit(j));
			      }
				  if (particle.getValue(j) > problem_.getUpperLimit(j)) {
					  particle.setValue(j, problem_.getUpperLimit(j));
			      }
			  }
		  }
	  }
  }
  
  public SolutionSet execute() throws JMException, ClassNotFoundException {
	  initParams();
	  //step_ = stepini_;
	  
	  success_ = false;
	  for (int i = 0; i < swarmSize_; i++) {
		  Solution particle = new Solution(problem_);
	      problem_.evaluate(particle);
	      problem_.evaluateConstraints(particle);
	      particles_.add(particle);
	      //Seta o peso de todos os peixes para 1
	      particles_.get(i).setFitness(maxFitness_/2); //peso do peixe
	  }
	  
	  for (int i = 0; i < particles_.size(); i++) {
	      Solution particle = new Solution(particles_.get(i));
	      leaders_.add(particle);
	  }
	  distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());
	  
	  while(iterations_ <= maxIterations_){
		  //step_ = calculateStepNonLinear(); //OK
		  step_ = calculateStep() * (-1);
		  //step_ = (step_ / 1.2);
		  
		  countD_ = countND_ = 0;
		  System.out.println("Step = " + step_ * (-1));
		  calculateIndivOper(); //OK
		  System.out.println("Peso Cardume Passado: " + lastTotFitness_ + " | Peso Cardume: " + totFitness_);
		  /*plotManager_.plotSwarmMOFSS(this.particles_, iterations_, "Mov Indiv");
	      try {
              Thread.sleep(sleeptime_);
          } catch (InterruptedException ex) {
              //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
          }*/
		  calculateColInstOper(); //OK
		  /*plotManager_.plotSwarmMOFSS(this.particles_, iterations_, "Mov ColInstint");
	      try {
              Thread.sleep(sleeptime_);
          } catch (InterruptedException ex) {
              //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
          }*/
		  //double barycentre = calculateBarycentre(); //OK
		  calculateColVolOper(); //OK+-
		  
		  
		  for (int i=0; i<swarmSize_; i++){
			  delta_f_[i] = 0;
			  for (int j=0; j<problem_.getNumberOfVariables(); j++){
				  delta_x_[i][j] = 0;
			  }
		  }
		  
		  for (int i = 0; i < particles_.size(); i++) {
			  Solution particle = new Solution(particles_.get(i));
		      leaders_.add(particle);
		  }
		  
		  distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());
		  /*for (int i=0; i<leaders_.size(); i++){
			  System.out.println("CD["+ i + "] = " + leaders_.get(i).getCrowdingDistance());
		  }*/
		  
		  
		  /* Gerar arquivo com o resultado dos objetivos da populacao e do AR */
		  /*if ((iterations_ == 1) || (iterations_ == maxIterations_/2)){
			  leaders_.printObjectivesToFile("results/FUN-EAR-MOFSS-"+problem_.getName()+"-"+iterations_);
			  particles_.printObjectivesToFile("results/FUN-POP-MOFSS-"+problem_.getName()+"-"+iterations_);
		  }*/
		  
		  plotManager_.plotExternalArchive(this.leaders_, iterations_);
	      plotManager_.plotSwarmBary(this.particles_, iterations_, barycenter_);
	      try {
              Thread.sleep(sleeptime_);
          } catch (InterruptedException ex) {
              //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
          }
		  
		  System.out.println("Tamanho aquivo externo: " + leaders_.size());
		  System.out.println("############### Itera‹o " + iterations_ + " ############### " +
		  					 "\nQtd dominadas: " + countD_ + " | Qtd N‹o dominadas: " + countND_);
		  iterations_++;
	  }
	  
	  particles_.printFitnessToFile("results/PESO");
	  
	  particles_.printObjectivesToFile("results/FUN-POP-MOFSS-"+problem_.getName()+"-"+iterations_);
	  //return particles_;
	  return this.leaders_;
  }
  
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
