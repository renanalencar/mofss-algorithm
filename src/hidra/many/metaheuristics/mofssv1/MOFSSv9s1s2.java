package hidra.many.metaheuristics.mofssv1;

import hidra.gui.PlotManager;
import hidra.qualityIndicator.Hypervolume;
import hidra.qualityIndicator.QualityIndicator;
import jmetal.core.*;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.wrapper.XReal;
import org.jfree.chart.ChartPanel;

import java.util.Comparator;

/**
 * This class implements the MOFSS algorithm. 
 * 
 * 
 *     VERSÃO sem DeltaF (substituido por DeltaW)
 *     
 *     
 * 
 */
public class MOFSSv9s1s2 extends Algorithm {

	
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
	
	private int teste = 0;
	private int totalteste = 0;
	
	
	
  public MOFSSv9s1s2(Problem problem) {
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
	  System.out.println("Quantidade dimensões: " + problem_.getNumberOfVariables());
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
  
  private double calculateCDToLeaders(Solution solution) throws JMException{
	  double distance = 9999999;
	  double crowdingDistance;
	  int position = 0;
	  
	  for (int i=0; i<leaders_.size(); i++){
		  double euclideanDistance = euclideanDistance(solution, leaders_.get(i));
		  if (euclideanDistance < distance){
			  distance = euclideanDistance;
			  position = i;
		  }
	  }
	  crowdingDistance = leaders_.get(position).getCrowdingDistance();
	  
	  return crowdingDistance;
  }
  
  
  
  /* S1 */
  private void calculateIndivOperS1() throws JMException, ClassNotFoundException{
	  lastTotFitness_ = totFitness_;
	  int flag = 0;
	  
	  	// RoletteWheel //
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
		// RoletteWheel //
	  
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
		  
		  double rand = PseudoRandom.randDouble(0,1);
		  
		  // S� funciona se o tipo for RealSolutionType
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens�o de cada peixe
			  double movement = (front.get(leadersRoletteWheel).getDecisionVariables()[j].getValue() - particle.getValue(j)) / euclideanDistance;
			  
			  delta_x_[i][j] = movement * step_ * rand;
			  
			  particleNew.getDecisionVariables()[j].setValue(olderParticle.getDecisionVariables()[j].getValue() + delta_x_[i][j]);
			  
			  if (particleNew.getDecisionVariables()[j].getValue() < problem_.getLowerLimit(j)) particleNew.getDecisionVariables()[j].setValue(problem_.getLowerLimit(j));
		      if (particleNew.getDecisionVariables()[j].getValue() > problem_.getUpperLimit(j)) particleNew.getDecisionVariables()[j].setValue(problem_.getUpperLimit(j));
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
	    	  for (int j=0; j<problem_.getNumberOfVariables(); j++){ 
	    		  delta_x_[i][j] = 0;
	    	  }
	      }
	    	  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  particle.setValue(j, particleNew.getDecisionVariables()[j].getValue()) ;
		  }
	      
	      
		  particles_.get(i).setRank(flag * (-1));
		  problem_.evaluate(particles_.get(i));
		  
	  }
	  
	  printDominance_[0][iterations_-1] = countD_;
	  printDominance_[1][iterations_-1] = countND_;
	  printDominance_[2][iterations_-1] = countIND_;
  }
  
  
  /* S2 */
  private void calculateIndivOperS2() throws JMException, ClassNotFoundException{
	  lastTotFitness_ = totFitness_;
	  int flag = 0;
	  
	  	// RoletteWheel //
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
		// RoletteWheel //
	  
	  for (int i=0; i<swarmSize_; i++){ //para cada peixe do cardume
		  Solution particleNew = new Solution (problem_);
		  Solution olderParticle = new Solution (particles_.get(i));
		  
		  XReal particle = new XReal (particles_.get(i));
		  
		  int leadersRoletteWheel = roletteWheelSelection(front, summatory);
		  double euclideanDistance = euclideanDistance(olderParticle, front.get(leadersRoletteWheel));
		  
		  if (euclideanDistance == 0){
			  for (int j=0; j<problem_.getNumberOfVariables(); j++){
				  delta_x_[i][j] = 0;
			  }
			  countIND_++;
			  particles_.get(i).setRank(0);
		  }else{
			  double rand = PseudoRandom.randDouble(0,1);
			  
			  // S� funciona se o tipo for RealSolutionType
			  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens�o de cada peixe
				  double movement = (front.get(leadersRoletteWheel).getDecisionVariables()[j].getValue() - particle.getValue(j)) / euclideanDistance;
				  
				  delta_x_[i][j] = movement * step_ * rand;
				  
				  particleNew.getDecisionVariables()[j].setValue(olderParticle.getDecisionVariables()[j].getValue() + delta_x_[i][j]);
				  
				  if (particleNew.getDecisionVariables()[j].getValue() < problem_.getLowerLimit(j)) particleNew.getDecisionVariables()[j].setValue(problem_.getLowerLimit(j));
			      if (particleNew.getDecisionVariables()[j].getValue() > problem_.getUpperLimit(j)) particleNew.getDecisionVariables()[j].setValue(problem_.getUpperLimit(j));
			      
			  }
			  
			  problem_.evaluate(particleNew);
			  flag = dominance_.compare(particleNew, olderParticle);
			  if (flag == -1) {        
		    	  countD_++;
		      }else if (flag == 1){
		    	  countND_++;
		      }else{
		    	  countIND_++;
		      }
		    	
			  if (flag != 0){
				  for (int j=0; j<problem_.getNumberOfVariables(); j++){
					  particle.setValue(j, particleNew.getDecisionVariables()[j].getValue()) ;
				  }
			  }else{
				  double crowdingDistanceNewPosition = calculateCDToLeaders(particleNew);
				  double crowdingDistanceOldPosition = calculateCDToLeaders(olderParticle);
				  
				  if (crowdingDistanceNewPosition >= crowdingDistanceOldPosition){
					  teste ++;
					  for (int j=0; j<problem_.getNumberOfVariables(); j++){
						  particle.setValue(j, particleNew.getDecisionVariables()[j].getValue()) ;
					  }
				  }else{
					  for (int j=0; j<problem_.getNumberOfVariables(); j++){
						  delta_x_[i][j] = 0;
					  }
				  }
			  }
			  particles_.get(i).setRank(flag * (-1));
			  problem_.evaluate(particles_.get(i));
		  }
	  }
	  
	  printDominance_[0][iterations_-1] = countD_;
	  printDominance_[1][iterations_-1] = countND_;
	  printDominance_[2][iterations_-1] = countIND_;
  }
  
  private void calculateFeedOper() {
	  
	  totFitness_ = 0;
	  
	  for (int i=0; i<swarmSize_; i++){
		  
		  particles_.get(i).setFitness(particles_.get(i).getFitness() + particles_.get(i).getRank());
		  
		  if (particles_.get(i).getFitness() < minFitness_) particles_.get(i).setFitness(minFitness_);
		  if (particles_.get(i).getFitness() > maxFitness_) particles_.get(i).setFitness(maxFitness_);
		  
		  delta_w_[0] += particles_.get(i).getRank(); 
		  
		  totFitness_ += particles_.get(i).getFitness();
	  }
	  
  }
  
  /* S1 */
  private void calculateColInstOperS1() throws JMException{
	  double[] imDirection = new double[problem_.getNumberOfVariables()];
	  double[] sum1 = new double[problem_.getNumberOfVariables()];
	  
	  for (int i=0; i<problem_.getNumberOfVariables(); i++){
		  sum1[i] = 0;
	  }
	  
	  for (int i=0; i<swarmSize_; i++){
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  imDirection[j] += ( delta_x_[i][j] * particles_.get(i).getRank()) / swarmSize_;
		  }
	  }
	  
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens�o de cada peixe
			  particle.setValue(j, particle.getValue(j) + imDirection[j]);
			  
			  if (particle.getValue(j) < problem_.getLowerLimit(j)) particle.setValue(j, problem_.getLowerLimit(j));
		      if (particle.getValue(j) > problem_.getUpperLimit(j))  particle.setValue(j, problem_.getUpperLimit(j));
		  }
	  }
  }
  
  /* S2 */
  private void calculateColInstOperS2() throws JMException{
	  double[] imDirection = new double[problem_.getNumberOfVariables()];
	  double[] sum1 = new double[problem_.getNumberOfVariables()];
	  
	  for (int i=0; i<problem_.getNumberOfVariables(); i++){
		  sum1[i] = 0;
	  }
	  
	  for (int i=0; i<swarmSize_; i++){
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  if (countD_+countND_ != 0){
				  imDirection[j] += ( delta_x_[i][j] * particles_.get(i).getRank()) / (countD_+countND_);
			  }else{
				  imDirection[j] += 0;
			  }
		  }
	  }
	  
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens�o de cada peixe
			  particle.setValue(j, particle.getValue(j) + imDirection[j]);
			  
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
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  sum1[j] += particles_.get(i).getDecisionVariables()[j].getValue() * particles_.get(i).getFitness();
		  }
		  sum2 += particles_.get(i).getFitness();
	  }
	  
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
  
  private void calculateColVolOperS1() throws JMException, ClassNotFoundException{
	  
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  double stepVol = step_ * 2;
		  
		  double rand = PseudoRandom.randDouble(0,1);
		  double euclideanDistance = euclideanDistance(particles_.get(i), barycenter_);
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  
			  particle.setValue(j,particle.getValue(j) - (stepVol * ((countD_-countND_)/(swarmSize_)) * rand * (particle.getValue(j) - barycenter_.getDecisionVariables()[j].getValue())/euclideanDistance));
			  
			  if (particle.getValue(j) < problem_.getLowerLimit(j)) particle.setValue(j, problem_.getLowerLimit(j));
		      if (particle.getValue(j) > problem_.getUpperLimit(j)) particle.setValue(j, problem_.getUpperLimit(j));
		  }
	  }
  }
  
private void calculateColVolOperS2() throws JMException, ClassNotFoundException{
	  
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  double stepVol;
		  if (countIND_ != 0){
			  stepVol = step_ * 2 * countIND_/swarmSize_; 
		  }else{
			  stepVol = step_;
		  }
		   
		  
		  double rand = PseudoRandom.randDouble(0,1);
		  double euclideanDistance = euclideanDistance(particles_.get(i), barycenter_);
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  double movement = (stepVol * ((countD_-countND_)/swarmSize_) * rand * (particle.getValue(j) - barycenter_.getDecisionVariables()[j].getValue())/euclideanDistance);
			  
			  particle.setValue(j,particle.getValue(j) - movement);
			  
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
		  
		  System.out.println("############### Iteração " + iterations_ + " ############### ");
		  System.out.println("Tamanho aquivo externo: " + leaders_.size());
		  
		  plotGraph();
		  
		  // Calcula Step
		  step_ = calculateStep();
		  System.out.println("Step = " + step_);
		  
		  // Movimento Individual
		  calculateIndivOperS2(); //OK
		  plotGraphMovements("Individual");
		  
		  if (this.leaders_.flag == 1){
			  return this.leaders_; //Erro do loop infinito
		  }
		  
		  // Alimenta��o
		  calculateFeedOper();
		  		  
		  // Movimento Coletivo Instintivo
		  calculateColInstOperS1(); //OK
		  plotGraphMovements("Instintivo");
		  
		  // Calcula Baricentro
		  barycenter_ = calculateBarycenter();
		  plotGraphMovements("Barycenter");
		  
		  // Movimento Coletivo Volitivo
		  calculateColVolOperS2(); //OK+-
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
		  System.out.println("Qtd dominadas: " + countD_ + " | Qtd Não dominadas: " + countND_ + " | Qtd indiferentes: " + countIND_);
		  if (countD_ >= countND_){
			  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		  }else{
			  System.out.println("-------------------------------------------------------------");
		  }
			  
		  iterations_++;
	  }
	  
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
