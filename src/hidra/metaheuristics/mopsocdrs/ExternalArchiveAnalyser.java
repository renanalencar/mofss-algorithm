/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hidra.metaheuristics.mopsocdrs;




import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;

import java.util.Vector;




public class ExternalArchiveAnalyser
{
        //Stores results at each iteration
        private Vector<Double> spacingResults;
        private Vector<Double> spreadResults;

        private int spacingDesvPad;
        private int spreadingDesvPad;

        //private PerformanceMetrics perfomanceMetrics;

        private Problem problem;

        public ExternalArchiveAnalyser(Problem problem)
        {
            this.spacingResults = new Vector<Double>();
            this.spreadResults = new Vector<Double>();

            this.spacingDesvPad = 0;
            this.spreadingDesvPad = 0;
            
        

            this.problem = problem;
        }

        private double CheckMaxSpread(SolutionSet particles)
        {
            double maxSpread = calculateMaximumSpread(particles, this.problem);
            return maxSpread;
        }

        private double CheckSpacing(SolutionSet particles)
        {
            double spacing = calculateSpacing(particles, this.problem);
            return spacing;
        }

        
        private double calculateDistance(Solution solution, Solution anotherSolution) {
            double distance = 0.0;

            for (int i = 0; i < solution.numberOfObjectives(); i++) {
              distance += 
                Math.abs(solution.getObjective(i) - anotherSolution.getObjective(i));
            }

            return distance;
        }
        
        private double calculateMinDistance(Solution solution, int solutionIndex, SolutionSet solutions) {
            double minDistance = Double.MAX_VALUE;
            double distance;

            for (int i = 0; i < solutions.size(); i++) {
                if (i != solutionIndex) {
                    distance = this.calculateDistance(solution, solutions.get(i));

                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }

            return minDistance;
        }
        
        public double calculateSpacing(SolutionSet solutions, Problem problem) {

            int nSolutions = solutions.size();
            double[] distances = new double[nSolutions];
            double meanDistance;
            double sum = 0.0;
            double[] differenceDistances = new double[nSolutions];
            double sumDifferences = 0.0;

            for (int i = 0; i < nSolutions; i++) {
                distances[i] = this.calculateMinDistance(solutions.get(i), i, solutions);
                sum += distances[i];
            }

            meanDistance = sum / (double) nSolutions;

            for (int i = 0; i < nSolutions; i++) {
                differenceDistances[i] = Math.pow(meanDistance - distances[i], 2);
                sumDifferences += differenceDistances[i];
            }

            return Math.sqrt((1 / ((double) nSolutions - 1)) * sumDifferences);
        }

        
        public double calculateMaximumSpread(SolutionSet solutions, Problem problem) {

            double sumDistances = 0.0;


            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                this.calculateMaxValue(solutions, i);
                sumDistances += Math.pow(this.calculateMaxValue(solutions, i) - this.calculateMinValue(solutions, i), 2);
            }

            return Math.sqrt(sumDistances);
        }
        
        private double calculateMinValue(SolutionSet solutions, int objectiveIndex) {
            double minValue = Double.MAX_VALUE;

            for (int i = 0; i < solutions.size(); i++) {
                if (solutions.get(i).getObjective(objectiveIndex) < minValue) {
                    minValue = solutions.get(i).getObjective(objectiveIndex);
                }
            }

            return minValue;
        }
        
        
        //------------------rever
        private double calculateMaxValue(SolutionSet solutions, int objectiveIndex) {
            double maxValue = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < solutions.size(); i++) {
                if (solutions.get(i).getObjective(objectiveIndex) > maxValue) {
                    maxValue = solutions.get(i).getObjective(objectiveIndex);
                }
            }

            return maxValue;
        }
        
        public SwarmBehavior DecideBehavior(SolutionSet particles, SwarmBehavior behavior)
        {
            
            if (particles.size() > 0)
            {
                double standardDeviation = 0;

                switch (behavior)
                {
                    case UseExternalArchive:
                        double spacing = CheckSpacing(particles);
	                    if(spacingResults.size() >= 1){
	                        if(Math.abs(  spacing -  spacingResults.lastElement() ) < 0.001){
	                        	behavior = SwarmBehavior.Clans;
	                        }	                        
                        }
	                    spacingResults.add(spacing);
                        
                        
                        break;

                    case Clans:
                    	double spreading = CheckMaxSpread(particles);
                    	if(spreadResults.size() >= 1){	                        
	                        if(Math.abs(   spreading  -  spreadResults.lastElement()  ) < 0.001){
	                        	behavior = SwarmBehavior.UseExternalArchive;
	                        }	                        
                    	}
                    	spreadResults.add(spreading);
                        break;
                }
                 
                //behavior = DecideBehaviorAccording(standardDeviation, behavior);
            }
            
            //behavior = SwarmBehavior.Clans;
            return behavior;
        }

        //According standard deviation analysis, it decides which behavior is more suitable.
        private SwarmBehavior DecideBehaviorAccording(double standardDeviation, SwarmBehavior behavior)
        {
            if (behavior == SwarmBehavior.Clans)
            {
                if(standardDeviation <= 0.0001 && standardDeviation > 0.00001)
                {
                    behavior = SwarmBehavior.UseExternalArchive;
                }
            }
            else if(behavior == SwarmBehavior.UseExternalArchive)
            {
                if(standardDeviation <= 0.0001 && standardDeviation > 0.00001)
                {
                    behavior = SwarmBehavior.Clans;
                }
            }

            return behavior;
        }
}
