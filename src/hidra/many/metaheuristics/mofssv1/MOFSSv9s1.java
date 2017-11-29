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
 *     VERSÃO sem DeltaF (substituido por DeltaW)
 *     
 *     
 * 
 */
public class MOFSSv9s1 extends Algorithm {

	
	private int swarmSize_;
	private int maxIterations_;
	private int archiveSize_;
	private int iterations_;
	private SolutionSet particles_;
	private Solution barycenter_;
	private Comparator dominance_;
	private Distance distance_;
	private Comparator crowdingDistanceComparator_;
	//private double[] delta_f_;
	private double[][] delta_x_;
	private double[] delta_w_;
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
	
	private double countD_;
	private double countND_;
	private double countIND_;
	private double[][] printDominance_;
	
	private PlotManager plotManager_;
	
	private int sleeptime_ = 1;
	
	
	
  public MOFSSv9s1(Problem problem) {
    super (problem) ;
    
    stepini_ = (problem_.getUpperLimit(0)) /2;
    stepfin_ = 0.01;
    maxDeltaF_ = -9999;
    minFitness_ = 1;
    maxFitness_ = 1000;
    
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
	  
	  //delta_f_ = new double[swarmSize_];
	  delta_x_ = new double[swarmSize_][problem_.getNumberOfVariables()];
	  delta_w_ = new double[swarmSize_];
	  
	  printDominance_ = new double[3][maxIterations_];
	  
	  iterations_ = 1;
	  totFitness_ = 0;

	  success_ = false;

	  particles_ = new SolutionSet(swarmSize_);

	  // Create comparators for dominance and crowding distance
	  dominance_ = new DominanceComparator();
	  distance_ = new Distance();
	  crowdingDistanceComparator_ = new CrowdingDistanceComparator();
	  
	  System.out.println(problem_.getName());
	  System.out.println("Quantidade objetivos: " + problem_.getNumberOfObjectives());
	  System.out.println("Quantidade dimens�es: " + problem_.getNumberOfVariables());
	  System.out.println(problem_.getUpperLimit(0) + " | " + problem_.getLowerLimit(0) + " | " + this.stepini_);
  }
  
  private double calculateStep(){
	  return stepini_ - (stepini_ - stepfin_) * ((double) iterations_/maxIterations_);
	  
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
  
  public double euclideanDistance(Solution solutionI, Solution solutionJ) throws JMException {
	  // ->Obtain his decision variables
	  Variable[] decisionVariableI = solutionI.getDecisionVariables();
	  Variable[] decisionVariableJ = solutionJ.getDecisionVariables();

	  double diff; // Auxiliar var
	  double distance = 0.0;
	  // -> Calculate the Euclidean distance
	  for (int i = 0; i < decisionVariableI.length; i++) {
		  diff = decisionVariableI[i].getValue() - decisionVariableJ[i].getValue();
		  distance += Math.pow(diff, 2.0);
	  } // for

	  // -> Return the euclidean distance
	  return Math.sqrt(distance);
  }
  
  private void calculateIndivOper() throws JMException, ClassNotFoundException{
	  lastTotFitness_ = totFitness_;
	  int flag = 0;
	  
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
		  
		  XReal particle = new XReal (particles_.get(i));
		  
		  int leadersRoletteWheel;
		  double euclideanDistance;
		  
		  int loop = 0;
		  do {
			  leadersRoletteWheel = roletteWheelSelection(front, summatory);
			  euclideanDistance = euclideanDistance(olderParticle, front.get(leadersRoletteWheel));
			  loop++;
			  if (loop == 20){
				  this.leaders_.flag = 1;
				  break;
			  }
		  } while (euclideanDistance == 0);
		  
		  double rand = PseudoRandom.randDouble(0,2);
		  
		  //System.out.println("CDMAX["+ leadersRoletteWheel + "] = " + front.get(leadersRoletteWheel).getCrowdingDistance());
		  
		  // S� funciona se o tipo for RealSolutionType
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens�o de cada peixe
			  double movement = (front.get(leadersRoletteWheel).getDecisionVariables()[j].getValue() - particle.getValue(j)) / euclideanDistance;
			  
			  delta_x_[i][j] = movement * step_ * rand;
			  
			  particleNew.getDecisionVariables()[j].setValue(olderParticle.getDecisionVariables()[j].getValue() + delta_x_[i][j]);
			  
			  if (particleNew.getDecisionVariables()[j].getValue() < problem_.getLowerLimit(j)) particleNew.getDecisionVariables()[j].setValue(problem_.getLowerLimit(j));
		      if (particleNew.getDecisionVariables()[j].getValue() > problem_.getUpperLimit(j)) particleNew.getDecisionVariables()[j].setValue(problem_.getUpperLimit(j));
		      
			  //System.out.println("Particle[" + i + "," + j + "]" + particleCopy.getDecisionVariables()[j].getValue() + " # " + particle.getValue(j));
		  }
		  
		  problem_.evaluate(particleNew);
		  flag = dominance_.compare(particleNew, particles_.get(i));
		  if (flag == -1) {        
	    	  countD_++;
	      }else if (flag == 1){
	    	  countND_++;
	      }else{
	    	  countIND_++;
	    	  flag = 0;
	      }
	    	  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  particle.setValue(j, particleNew.getDecisionVariables()[j].getValue()) ;
		  }
	      
	      
		  particles_.get(i).setRank(flag * (-1));
		  problem_.evaluate(particles_.get(i));
		  
		  //System.out.println("(" + particles_.get(i).getDecisionVariables()[0].getValue() + " / " + olderParticles.get(i).getDecisionVariables()[0].getValue() + " / " + particleNew.getDecisionVariables()[0].getValue() + " / " + flag + ")");
	  }
	  
	  printDominance_[0][iterations_-1] = countD_;
	  printDominance_[1][iterations_-1] = countND_;
	  printDominance_[2][iterations_-1] = countIND_;
  }
  
  private void calculateFeedOper() {
	  
	  totFitness_ = 0;
	  
	  for (int i=0; i<swarmSize_; i++){
		  
		  if (particles_.get(i).getRank() != 0){
			  particles_.get(i).setFitness(particles_.get(i).getFitness() + particles_.get(i).getRank());
		  }else{
			  particles_.get(i).setRank(1);
		  }
		  
		  if (particles_.get(i).getFitness() < minFitness_) particles_.get(i).setFitness(minFitness_);
		  if (particles_.get(i).getFitness() > maxFitness_) particles_.get(i).setFitness(maxFitness_);
		  
		  delta_w_[0] += particles_.get(i).getRank(); 
		  
		  totFitness_ += particles_.get(i).getFitness();
	  }
	  
  }
  
  private void calculateColInstOper() throws JMException{
	  double[] imDirection = new double[problem_.getNumberOfVariables()];
	  double[] sum1 = new double[problem_.getNumberOfVariables()];
	  
	  for (int i=0; i<problem_.getNumberOfVariables(); i++){
		  sum1[i] = 0;
	  }
	  
	  for (int i=0; i<swarmSize_; i++){
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  imDirection[j] += ( delta_x_[i][j] * particles_.get(i).getRank()) / Math.max(1.0, Math.abs(delta_w_[0]));
		  }
		  //((countIND_/(countD_+countND_)) *
	  }
	  
	  
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens�o de cada peixe
			  //System.out.println(particles_.get(i).getDecisionVariables()[j].getValue());
			  particle.setValue(j, particle.getValue(j) + imDirection[j]);
			  //System.out.println(particle.getValue(j));
			  if (particle.getValue(j) < problem_.getLowerLimit(j)) particle.setValue(j, problem_.getLowerLimit(j));
		      if (particle.getValue(j) > problem_.getUpperLimit(j))  particle.setValue(j, problem_.getUpperLimit(j));
		      
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
		  sum1[j] = sum1[j];
		  barycenter.getDecisionVariables()[j].setValue((sum1[j]/sum2));
		  System.out.print(barycenter.getDecisionVariables()[j].getValue() + ", ");
	  }
	  System.out.println();
	  problem_.evaluate(barycenter);
	  return barycenter; 
  }
  
  private void calculateColVolOper() throws JMException, ClassNotFoundException{
	  
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  double stepVol = step_ * 2;
		  
		  double rand = PseudoRandom.randDouble(0,1);
		  double euclideanDistance = euclideanDistance(particles_.get(i), barycenter_);
		  //euclideanDistance = 1;
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  //if (countD_ >= countND_){
			  	  
				  particle.setValue(j,particle.getValue(j) - (stepVol * ((countD_-countND_)/(swarmSize_)) * rand * (particle.getValue(j) - barycenter_.getDecisionVariables()[j].getValue())/euclideanDistance));
			  /*}
			  if ((countND_ < countD_) || (countIND_ == swarmSize_)){
				  particle.setValue(j,particle.getValue(j) + (stepVol * rand * (particle.getValue(j) - barycenter_.getDecisionVariables()[j].getValue())/euclideanDistance));
			  }*/
			  
			  //System.out.println("-> stelVol= " + stepVol + " Rank= " + particles_.get(i).getRank() + " Position= " + particle.getValue(j) + " Barycenter= " + barycenter_.getDecisionVariables()[j].getValue());
			  //System.out.println("!!!!" + stepVol * particles_.get(i).getRank() * (particle.getValue(j) - barycenter_.getDecisionVariables()[j].getValue()));
			  
			  if (particle.getValue(j) < problem_.getLowerLimit(j)) particle.setValue(j, problem_.getLowerLimit(j));
		      if (particle.getValue(j) > problem_.getUpperLimit(j)) particle.setValue(j, problem_.getUpperLimit(j));
		  }
	  }
  }
  
  private void plotGraph () throws JMException{
	  plotManager_.plotExternalArchive(this.leaders_, iterations_);
      //plotManager_.plotSwarmBary(this.particles_, iterations_, barycenter_);
      plotManager_.plotSwarmMOFSS(this.particles_, iterations_, barycenter_);
      try {
          Thread.sleep(sleeptime_ / 10);
      } catch (InterruptedException ex) {
          //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
  
  private void plotGraphMovements (String name) throws JMException{
	  plotManager_.plotExternalArchive(this.leaders_, iterations_);
	  
	  if (name == "Individual"){
		  plotManager_.plotMOFSSMovInd(this.particles_, iterations_);
	      try {
	          Thread.sleep(sleeptime_ * 10);
	      } catch (InterruptedException ex) {
	          //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
	      }
	  }
	  if (name == "Instintivo"){
		  plotManager_.plotMOFSSMovCoI(this.particles_, iterations_);
	      try {
	          Thread.sleep(sleeptime_);
	      } catch (InterruptedException ex) {
	          //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
	      }
	  }
	  if (name == "Volitivo"){
		  plotManager_.plotMOFSSMovCoV(this.particles_, iterations_);
	      try {
	          Thread.sleep(sleeptime_ * 10);
	      } catch (InterruptedException ex) {
	          //Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
	      }
	  }
	  if (name == "Barycenter"){
			plotManager_.plotMOFSSBarycenter(barycenter_);
			try {
				Thread.sleep(sleeptime_ * 10);
			} catch (InterruptedException ex) {
				// Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE,
				// null, ex);
			}
	  }
	  if (name == "Search Space Boundaries"){
		  	plotManager_.plotLimitsProblem(problem_);
			try {
				Thread.sleep(sleeptime_ * 10);
			} catch (InterruptedException ex) {
				// Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE,
				// null, ex);
			}
	  }
  }
  
  public SolutionSet execute() throws JMException, ClassNotFoundException {
	  initParams();
	  //step_ = stepini_;
	  
	  success_ = false;
	  
	  // Inicia cardume aleatoriamente
	  for (int i = 0; i < swarmSize_; i++) {
		  Solution particle = new Solution(problem_);
	      problem_.evaluate(particle);
	      problem_.evaluateConstraints(particle);
	      particles_.add(particle);
	      //Seta o peso de todos os peixes para 1
	      particles_.get(i).setFitness(maxFitness_/10); //peso do peixe
	  }
	  
	  // Inicializa Arquivo Externo
	  for (int i = 0; i < particles_.size(); i++) {
	      Solution particle = new Solution(particles_.get(i));
	      leaders_.add(particle);
	  }
	  distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());
	  
	  plotGraphMovements("Search Space Boundaries");
	  
	  while(iterations_ <= maxIterations_){
		  countD_ = countND_ = countIND_ = 0;
		  
		  System.out.println("############### Itera��o " + iterations_ + " ############### ");
		  System.out.println("Tamanho aquivo externo: " + leaders_.size());
		  
		  plotGraph();
		  
		  // Calcula Step
		  step_ = calculateStep();
		  System.out.println("Step = " + step_);
		  
		  // Movimento Individual
		  calculateIndivOper(); //OK
		  plotGraphMovements("Individual");
		  
		  if (this.leaders_.flag == 1){
			  return this.leaders_; //Erro do loop infinito
		  }
		  
		  // Alimenta��o
		  calculateFeedOper();
		  		  
		  // Movimento Coletivo Instintivo
		  calculateColInstOper(); //OK
		  plotGraphMovements("Instintivo");
		  
		  // Calcula Baricentro
		  barycenter_ = calculateBarycenter();
		  plotGraphMovements("Barycenter");
		  
		  // Movimento Coletivo Volitivo
		  calculateColVolOper(); //OK+-
		  plotGraphMovements("Volitivo");
		  
		  
		  // Atualiza Arquivo Externo
		  for (int i = 0; i < particles_.size(); i++) {
			  Solution particle = new Solution(particles_.get(i));
		      leaders_.add(particle);
		  }
		  distance_.crowdingDistanceAssignment(leaders_, problem_.getNumberOfObjectives());
		  
		  // Plota o gr�fico geral, sem ser por movimento em separado
		  //plotGraph();
		  
		  System.out.println("Peso Cardume Passado: " + lastTotFitness_ + " | Peso Cardume: " + totFitness_);
		  System.out.println("Qtd dominadas: " + countD_ + " | Qtd N�o dominadas: " + countND_ + " | Qtd indiferentes: " + countIND_);
		  if (countD_ >= countND_){
			  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		  }else{
			  System.out.println("-------------------------------------------------------------");
		  }
			  
		  iterations_++;
	  }
	  
	  //particles_.printFitnessToFile("results/PESO");
	  //particles_.printObjectivesToFile("results/FUN-POP-MOFSS-"+problem_.getName()+"-"+iterations_);
	  particles_.printDominanceToFile("results/DOMINANCE-MOFSS-"+problem_.getName(), iterations_-1, printDominance_);
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
