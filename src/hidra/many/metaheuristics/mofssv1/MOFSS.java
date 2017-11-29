//  NSGAII.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package hidra.many.metaheuristics.mofssv1;

import java.util.Comparator;

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
 * This class implements the NSGA-II algorithm. 
 */
public class MOFSS extends Algorithm {

  /**
   * Constructor
   * @param problem Problem to solve
   */
	
	private int swarmSize_;
	private int maxIterations_;
	private int iterations_;
	private SolutionSet particles_;
	private Comparator dominance_;
	private Distance distance_;
	private double[] delta_f_;
	private double[] delta_x_;
	private double totFitness_;
	private double lastTotFitness_;
	
	QualityIndicator indicators_;
	
	private double stepini_;
	private double stepfin_;
	private double step_;
	private double maxDeltaF_;
	
	private double trueHypervolume_;
	private Hypervolume hy_;
	private SolutionSet trueFront_;
	boolean success_;
	
	private int count_;
	
	
	
  public MOFSS(Problem problem) {
    super (problem) ;
    
    stepini_ = 1.0;
    stepfin_ = 0.1;
    step_ = stepini_;
    maxDeltaF_ = -9999;
    
  } // MOFSS
  
  public void initParams(){
	  swarmSize_ = ((Integer) getInputParameter("swarmSize")).intValue();
	  maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
	  indicators_ = (QualityIndicator) getInputParameter("indicators");
	  
	  delta_f_ = new double[swarmSize_];
	  delta_x_ = new double[swarmSize_];

	  iterations_ = 1;
	  totFitness_ = 0;

	  success_ = false;

	  particles_ = new SolutionSet(swarmSize_);

	  // Create comparators for dominance and crowding distance
	  dominance_ = new DominanceComparator();
	  distance_ = new Distance();
  }
  
  private void calculateStep(){
	  this.step_ = this.step_ - ((this.stepini_ - this.stepini_) / this.iterations_);
  }
  
  private void calculateIndivOper() throws JMException{
	  lastTotFitness_ = totFitness_;
	  totFitness_ = 0;
	  
	  for (int i=0; i<swarmSize_; i++){ //para cada peixe do cardume
		  Solution particleCopy = new Solution (particles_.get(i));
		  XReal particle = new XReal (particles_.get(i));
		  
		  // S— funciona se o tipo for RealSolutionType
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens‹o de cada peixe
			  //particle.setValue(j, PseudoRandom.randDouble(problem_.getLowerLimit(j), problem_.getUpperLimit(j)));
			  particleCopy.getDecisionVariables()[j].setValue(particleCopy.getDecisionVariables()[j].getValue() + 
					  										  PseudoRandom.randDouble(-1,1) * this.step_);
			  //System.out.println("Particle[" + i + "," + j + "]" + particleCopy.getDecisionVariables()[j].getValue() + " # " + particle.getValue(j));
			}
		  
		  problem_.evaluate(particleCopy);
		  
		  int flag = dominance_.compare(particleCopy, particles_.get(i));
		  if (flag != 1) { // a nova part’cula Ž melhor que a part’cula anterior  
			  Solution olderParticle = new Solution (particles_.get(i));
			  
			  for (int j=0; j<problem_.getNumberOfVariables(); j++){
				  particle.setValue(j, particleCopy.getDecisionVariables()[j].getValue()) ;
			  }
			  problem_.evaluate(particles_.get(i));
			  particles_.get(i).marked();
			  calculateFeedOper(olderParticle, i); // aumentar o peso do peixe
			  
			  count_++;
			}
		  
		  totFitness_ = totFitness_ + particles_.get(i).getFitness();
	  }
  }
  
  private void calculateFeedOper(Solution olderParticle, int index) throws JMException{
	  double f1=0; 
	  double f2=0;
	  
	  double x1=0;
	  double x2=0;
	  
	  for (int i=0; i<problem_.getNumberOfObjectives(); i++){
		  f1 = f1 + particles_.get(index).getObjective(i);
		  f2 = f2 + olderParticle.getObjective(i);
	  }
	  
	  for (int i=0; i<problem_.getNumberOfVariables(); i++){
		  x1 = x1 + particles_.get(index).getDecisionVariables()[i].getValue();
		  x2 = x2 + olderParticle.getDecisionVariables()[i].getValue();
	  }
	  
	  f1 = f1 / particles_.get(index).getDecisionVariables().length;
	  f2 = f2 / olderParticle.getDecisionVariables().length;
	  
	  x1 = x1 / particles_.get(index).getDecisionVariables().length;
	  x2 = x2 / olderParticle.getDecisionVariables().length;
	  
	  delta_f_[index] = Math.abs(f1 - f2);
	  if (delta_f_[index] > maxDeltaF_){
		  maxDeltaF_ = delta_f_[index];
	  }
	  delta_x_[index] = Math.abs(x1 - x2);
	  
	  // atualiza peso do peixe
	  particles_.get(index).setFitness(particles_.get(index).getFitness() + (delta_f_[index] / maxDeltaF_));
  }
  
  private double calculateBarycentre() throws JMException {
	  double sum1 = 0;
	  double sum2 = 0;
	  double position;
	  
	  for (int i=0; i<swarmSize_; i++){
		  position = 0;
		  
		  for (int j=0; j<particles_.get(i).getDecisionVariables().length; j++){
			  position = position + particles_.get(i).getDecisionVariables()[j].getValue();
		  }
		  position = position / particles_.get(i).getDecisionVariables().length;
		  sum1 = sum1 + position + particles_.get(i).getFitness();
		  sum2 = sum2 + particles_.get(i).getFitness();
	  }
	  
	  return sum1/sum2; 
  }
  
  private void calculateColInstOper() throws JMException{
	  double imDirection;
	  double sum1 = 0;
	  double sum2 = 0;
	  
	  for (int i=0; i<swarmSize_; i++){
		  if (particles_.get(i).isMarked()) {
			  sum1 = sum1 + delta_f_[i] + delta_x_[i];
			  sum2 = sum2 + delta_f_[i];
		  }
	  }
	  
	  if (sum2 == 0){
		  imDirection = 0;
	  }else{
		  imDirection = sum1/sum2;
	  }
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){ //para cada dimens‹o de cada peixe
			  particle.setValue(j, particle.getValue(j) + imDirection);
		  }
	  }
  }
  
  private void calculateColVolOper(double barycentre) throws JMException{
	  for (int i=0; i<swarmSize_; i++){
		  XReal particle = new XReal (particles_.get(i));
		  
		  for (int j=0; j<problem_.getNumberOfVariables(); j++){
			  if (totFitness_ > lastTotFitness_){
				  particle.setValue(j, particle.getValue(j) - this.step_ * PseudoRandom.randDouble(0,1) / 
						  			   ((particle.getValue(j) - barycentre)/2));
				  //distance_.distanceBetweenSolutions(solutionI, solutionJ);
			  }else{
				  particle.setValue(j, particle.getValue(j) + this.step_ * PseudoRandom.randDouble(0,1) / 
			  			   ((particle.getValue(j) - barycentre)/2));
			  }
		  }
	  }
}
  
  public SolutionSet execute() throws JMException, ClassNotFoundException {
	  initParams();
	  
	  success_ = false;
	  for (int i = 0; i < swarmSize_; i++) {
		  Solution particle = new Solution(problem_);
	      problem_.evaluate(particle);
	      problem_.evaluateConstraints(particle);
	      particles_.add(particle);
	      particles_.get(i).setFitness(1.0); //peso do peixe
	  }
	  
	  while(iterations_ < maxIterations_){
		  
		  count_ = 0;
		  
		  calculateStep(); //OK
		  calculateIndivOper(); //OK
		  calculateColInstOper(); //OK
		  double barycentre = calculateBarycentre(); //OK
		  calculateColVolOper(barycentre); //OK+-
		  
		  
		  for (int i=0; i<swarmSize_; i++){
			  delta_f_[i] = 0;
			  delta_x_[i] = 0;
		  }
		  
		  System.out.println("Itera‹o " + iterations_ + ": Qtd particulas dominadas: " + count_);
		  iterations_++;
	  }
	  
	  return particles_;
  }

  /**   
   * Runs the NSGA-II algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws JMException 
   */
  /*
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int populationSize;
    int maxEvaluations;
    int evaluations;

    QualityIndicator indicators; // QualityIndicator object
    int requiredEvaluations; // Use in the example of use of the
                                // indicators object (see below)

    SolutionSet population;
    SolutionSet offspringPopulation;
    SolutionSet union;

    Operator mutationOperator;
    Operator crossoverOperator;
    Operator selectionOperator;

    Distance distance = new Distance();

    //Read the parameters
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
    indicators = (QualityIndicator) getInputParameter("indicators");

    //Initialize the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;

    requiredEvaluations = 0;

    //Read the operators
    mutationOperator = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");

    // Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      problem_.evaluate(newSolution);
      problem_.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
    } //for       
    
    // Generations 
    while (evaluations < maxEvaluations) {

      // Create the offSpring solutionSet      
      offspringPopulation = new SolutionSet(populationSize);
      Solution[] parents = new Solution[2];
      for (int i = 0; i < (populationSize / 2); i++) {
        if (evaluations < maxEvaluations) {
          //obtain parents
          parents[0] = (Solution) selectionOperator.execute(population);
          parents[1] = (Solution) selectionOperator.execute(population);
          Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
          mutationOperator.execute(offSpring[0]);
          mutationOperator.execute(offSpring[1]);
          problem_.evaluate(offSpring[0]);
          problem_.evaluateConstraints(offSpring[0]);
          problem_.evaluate(offSpring[1]);
          problem_.evaluateConstraints(offSpring[1]);
          offspringPopulation.add(offSpring[0]);
          offspringPopulation.add(offSpring[1]);
          evaluations += 2;
        } // if                            
      } // for

      // Create the solutionSet union of solutionSet and offSpring
      union = ((SolutionSet) population).union(offspringPopulation);

      // Ranking the union
      Ranking ranking = new Ranking(union);

      int remain = populationSize;
      int index = 0;
      SolutionSet front = null;
      population.clear();

      // Obtain the next front
      front = ranking.getSubfront(index);

      while ((remain > 0) && (remain >= front.size())) {
        //Assign crowding distance to individuals
        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
        //Add the individuals of this front
        for (int k = 0; k < front.size(); k++) {
          population.add(front.get(k));
        } // for

        //Decrement remain
        remain = remain - front.size();

        //Obtain the next front
        index++;
        if (remain > 0) {
          front = ranking.getSubfront(index);
        } // if        
      } // while

      // Remain is less than front(index).size, insert only the best one
      if (remain > 0) {  // front contains individuals to insert                        
        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
        front.sort(new CrowdingComparator());
        for (int k = 0; k < remain; k++) {
          population.add(front.get(k));
        } // for

        remain = 0;
      } // if                               

      // This piece of code shows how to use the indicator object into the code
      // of NSGA-II. In particular, it finds the number of evaluations required
      // by the algorithm to obtain a Pareto front with a hypervolume higher
      // than the hypervolume of the true Pareto front.
      if ((indicators != null) &&
        (requiredEvaluations == 0)) {
        double HV = indicators.getHypervolume(population);
        if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
          requiredEvaluations = evaluations;
        } // if
      } // if
    } // while

    // Return as output parameter the required evaluations
    setOutputParameter("evaluations", requiredEvaluations);

    // Return the first non-dominated front
    Ranking ranking = new Ranking(population);
    return ranking.getSubfront(0);
  } // execute
  */
} // NSGA-II
