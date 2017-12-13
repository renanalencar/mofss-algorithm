package hidra.many.metaheuristics.mofssv1;

import java.util.Comparator;

import org.jfree.chart.ChartPanel;


import hidra.gui.PlotManager;
import jmetal.core.*;
import hidra.qualityIndicator.Hypervolume;
import hidra.qualityIndicator.QualityIndicator;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.wrapper.XReal;
import jmetal.util.*;

/**
 * This class implements the MOFSS algorithm. 
 * 
 * 
 *     VERS�O COM ARQUIVO EXTERNO DAS SOLU��ES N�O-DOMINADAS INFLUENCIANDO NO MOVIMENTO INDIVIDUAL
 *     
 *     
 * 
 */
public class MOFSSv5 extends Algorithm {

  /**
   * Constructor
   * @param problem Problem to solve
   */
	
	private int swarmSize_;
	private int maxIterations_;
	private int archiveSize_;
	private int iterations_;
	private SolutionSet particles_;
	private Comparator dominance_;
	private Distance distance_;
	private double[] delta_f_;
	private double[][] delta_x_;
	private double totFitness_;
	private double lastTotFitness_;
	
	QualityIndicator indicators_;
	
	private double stepini_;
	private double stepfin_;
	private double maxDeltaF_;
	
	private double trueHypervolume_;
	private Hypervolume hy_;
	private SolutionSet trueFront_;
	private CrowdingArchive leaders_;
	boolean success_;
	
	private int count_;
	
	private PlotManager plotManager_;
	
	
	
  public MOFSSv5(Problem problem) {
    super (problem) ;
    
    stepini_ = (problem_.getUpperLimit(0) - problem_.getLowerLimit(0)) / 2;
    stepfin_ = 0.000001;
    maxDeltaF_ = -9999;
    
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
	  
	  System.out.println(problem_.getUpperLimit(0) + " | " + problem_.getLowerLimit(0) + " | " + this.stepini_);
  }
  
  private double calculateStep(){
	  return ((stepini_ - stepfin_) / iterations_);
  }
  
  private void calculateIndivOper(double step) throws JMException{
	  lastTotFitness_ = totFitness_;
	  totFitness_ = 0;
	  
	  for (int i=0; i<swarmSize_; i++){ //para cada peixe do cardume
		  Solution particleCopy = new Solution (particles_.get(i));
		  XReal particle = new XReal (particles_.get(i));
		  int leadersRandom = (int) (Math.random()*leaders_.size());
		  
		  // S� funciona se o tipo for RealSolutionType
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens�o de cada peixe
			  //particle.setValue(j, PseudoRandom.randDouble(problem_.getLowerLimit(j), problem_.getUpperLimit(j)));
			  if (leaders_.size() < 1){
				  particleCopy.getDecisionVariables()[j].setValue(particleCopy.getDecisionVariables()[j].getValue() + 
							  									  PseudoRandom.randDouble(-1,1) * step);
			  }else{
				  particleCopy.getDecisionVariables()[j].setValue(particleCopy.getDecisionVariables()[j].getValue() + 
					  										  	  PseudoRandom.randDouble(particle.getValue(j),leaders_.get(leadersRandom).getDecisionVariables()[j].getValue()) 
					  										  	  * step);
			  }
			  
			  if (particleCopy.getDecisionVariables()[j].getValue() < problem_.getLowerLimit(j)) {
				  particleCopy.getDecisionVariables()[j].setValue(problem_.getLowerLimit(j));
		      }
			  if (particleCopy.getDecisionVariables()[j].getValue() > problem_.getUpperLimit(j)) {
				  particleCopy.getDecisionVariables()[j].setValue(problem_.getUpperLimit(j));
		      }
			  //System.out.println("Particle[" + i + "," + j + "]" + particleCopy.getDecisionVariables()[j].getValue() + " # " + particle.getValue(j));
		  }
		  
		  //problem_.evaluate(particleCopy);
		  Solution olderParticle = new Solution (particles_.get(i));
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  particle.setValue(j, particleCopy.getDecisionVariables()[j].getValue()) ;
		  }
		  problem_.evaluate(particles_.get(i));
		  
		  
		  /*
		   * 
		   *  VERIFICAR O PORQUE DO PROBLEMA COM A ALIMENTA��O DO CARDUME!
		   * 
		   */
		  
		  int flag = dominance_.compare(particleCopy, particles_.get(i));
		  if (flag != 0) { // a nova part�cula � melhor que a part�cula anterior  
			  //calculateFeedOper(olderParticle, i, flag); // aumentar o peso do peixe
			  particles_.get(i).marked();
			  count_++;
			  calculateFeedOper(olderParticle, i, flag);
		  }
		   // aumentar o peso do peixe
		  //particles_.get(i).marked();
		  
		  totFitness_ = totFitness_ + particles_.get(i).getFitness();
	  }
  }
  
  private void calculateFeedOper(Solution olderParticle, int index, int flag) throws JMException{
	  double f1=0; 
	  double f2=0;
	  
	  double[] x1 = new double[problem_.getNumberOfVariables()];
	  double[] x2 = new double[problem_.getNumberOfVariables()];
	  
	  for (int i=0; i<problem_.getNumberOfObjectives(); i++){
		  f1 += particles_.get(index).getObjective(i);
		  f2 += olderParticle.getObjective(i);
	  }
	  //f1 = f1/problem_.getNumberOfObjectives();
	  //f2 = f2/problem_.getNumberOfObjectives();
	  
	  for (int i=0; i<problem_.getNumberOfVariables(); i++){
		  x1[i] = particles_.get(index).getDecisionVariables()[i].getValue();
		  x2[i] = olderParticle.getDecisionVariables()[i].getValue();
	  }
	  
	  delta_f_[index] = f1 - f2;
	  if (delta_f_[index] > maxDeltaF_){
		  maxDeltaF_ = delta_f_[index];
	  }
	  for (int i=0; i<problem_.getNumberOfVariables(); i++){
		  delta_x_[index][i] = x1[i] - x2[i];
	  }
	  
	  // atualiza peso do peixe
	  if (flag == 1){
		  particles_.get(index).setFitness(particles_.get(index).getFitness() + (delta_f_[index] / maxDeltaF_));
	  }else if (flag == -1){
		  particles_.get(index).setFitness(particles_.get(index).getFitness() - (delta_f_[index] / maxDeltaF_));
	  }
  }
  
  private void calculateColInstOper() throws JMException{
	  double[] imDirection = new double[problem_.getNumberOfVariables()];
	  double[] sum1 = new double[problem_.getNumberOfVariables()];
	  double sum2 = 0;
	  
	  for (int i=0; i<swarmSize_; i++){
		  if (particles_.get(i).isMarked()) {
			  for (int j=0; j<problem_.getNumberOfVariables(); j++){
				  sum1[j] += delta_f_[i] * delta_x_[i][j];
			  }
			  //sum1[j] = sum1[j]/problem_.getNumberOfVariables();
			  sum2 += delta_f_[i];
			  particles_.get(i).unMarked();
		  }
	  }
	  
	  if (sum2 == 0){
		  for (int i=0; i<problem_.getNumberOfVariables(); i++){
			  imDirection[i] = 0;
		  }
	  }else{
		  for (int i=0; i<problem_.getNumberOfVariables(); i++){
			  imDirection[i] = sum1[i]/sum2;
		  }
	  }
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens�o de cada peixe
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
  
  private Solution calculateBarycentre() throws JMException {
	  double[] sum1 = new double[problem_.getNumberOfVariables()];
	  double sum2;
	  
	  Solution barycentre = new Solution (particles_.get(0));
	  sum2 = 0;
	  for (int i=0; i<swarmSize_; i++){
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  //System.out.println(particles_.get(i).getDecisionVariables()[j].getValue());
			  
			  sum1[j] = sum1[j] + particles_.get(i).getDecisionVariables()[j].getValue() * particles_.get(i).getFitness();
		  }
		  //System.out.println(particles_.get(i).getFitness());
		  sum2 = sum2 + particles_.get(i).getFitness();
	  }
	  
	  //System.out.println(sum2);
	  //System.out.println(problem_.getNumberOfVariables());
	  System.out.print("Baricentro: ");
	  for (int j=0; j<problem_.getNumberOfVariables(); j++){
		  barycentre.getDecisionVariables()[j].setValue((sum1[j]/sum2));
		  System.out.print(barycentre.getDecisionVariables()[j].getValue() + ", ");
	  }
	  System.out.println();
	  
	  return barycentre; 
  }
  
  private void calculateColVolOper(double step) throws JMException{
	  Solution barycentre = calculateBarycentre();
	  
	  System.out.println("Peso Cardume Passado: " + lastTotFitness_ + " | Peso Cardume: " + totFitness_);
	  
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  double euclideanDistance = distance_.distanceBetweenSolutions(particles_.get(i), barycentre);
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  if (totFitness_ > lastTotFitness_){
				  particle.setValue(j,particle.getValue(j) - step/2 * PseudoRandom.randDouble(0,1) / 
						  			   ((particle.getValue(j) - barycentre.getDecisionVariables()[j].getValue())/
						  				 euclideanDistance));
				  if (particle.getValue(j) < problem_.getLowerLimit(j)) {
					  particle.setValue(j, problem_.getLowerLimit(j));
			      }
				  if (particle.getValue(j) > problem_.getUpperLimit(j)) {
					  particle.setValue(j, problem_.getUpperLimit(j));
			      }
			  }else{
				  particle.setValue(j, particle.getValue(j) + step/2 * PseudoRandom.randDouble(0,1) / 
			  			   ((particle.getValue(j) - barycentre.getDecisionVariables()[j].getValue())/
			  				 euclideanDistance));
				  if (particle.getValue(j) < problem_.getLowerLimit(j)) {
					  particle.setValue(j, problem_.getLowerLimit(j));
			      }
				  if (particle.getValue(j) > problem_.getUpperLimit(j)) {
					  particle.setValue(j, problem_.getUpperLimit(j));
			      }
			  }
		  }
	  }
	  /*plotManager_.plotBarycentre(barycentre);
	  try {
          Thread.sleep(1000);
      } catch (InterruptedException ex) {
          //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
      }*/
  }
  
  public SolutionSet execute() throws JMException, ClassNotFoundException {
	  initParams();
	  double step = stepini_;
	  
	  success_ = false;
	  for (int i = 0; i < swarmSize_; i++) {
		  Solution particle = new Solution(problem_);
	      problem_.evaluate(particle);
	      problem_.evaluateConstraints(particle);
	      particles_.add(particle);
	      //Seta o peso de todos os peixes para 1
	      particles_.get(i).setFitness(1.0); //peso do peixe
	  }
	  
	  for (int i = 0; i < particles_.size(); i++) {
	      Solution particle = new Solution(particles_.get(i));
	      leaders_.add(particle);
	  }
	  
	  distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());

	  while(iterations_ <= maxIterations_){
		  
		  count_ = 0;
		  step = calculateStep() * (-1); //OK
		  System.out.println("Step = " + step);
		  calculateIndivOper(step); //OK
		  /*plotManager_.plotSwarmMOFSS(this.particles_, iterations_, "Mov Indiv");
	      try {
              Thread.sleep(1000);
          } catch (InterruptedException ex) {
              //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
          }*/
		  calculateColInstOper(); //OK
		  /*plotManager_.plotSwarmMOFSS(this.particles_, iterations_, "Mov ColInstint");
	      try {
              Thread.sleep(1000);
          } catch (InterruptedException ex) {
              //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
          }*/
		  //double barycentre = calculateBarycentre(); //OK
		  calculateColVolOper(step); //OK+-
		  
		  
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
		  
		  
		  if ((iterations_ == 1) || (iterations_ == maxIterations_/2)){
			  leaders_.printObjectivesToFile("results/FUN-EAR-MOFSS-"+problem_.getName()+"-"+iterations_);
			  particles_.printObjectivesToFile("results/FUN-POP-MOFSS-"+problem_.getName()+"-"+iterations_);
		  }
		  
		  plotManager_.plotExternalArchive(this.leaders_, iterations_);
	      plotManager_.plotSwarm(this.particles_, iterations_);
	      try {
              Thread.sleep(100);
          } catch (InterruptedException ex) {
              //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
          }
		  
		  System.out.println("Tamanho aquivo externo: " + leaders_.size());
		  System.out.println("############### Itera��o " + iterations_ + " ############### \nQtd particulas dominadas: " + count_);
		  iterations_++;
	  }
	  
	  
	  
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
