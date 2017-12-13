package hidra.qualityIndicator;

import jmetal.problems.DTLZ.DTLZ1;
import jmetal.problems.DTLZ.DTLZ2;
import jmetal.problems.DTLZ.DTLZ3;
import jmetal.problems.DTLZ.DTLZ4;
import jmetal.problems.DTLZ.DTLZ6;
import jmetal.core.Problem;

public class ConvergenceMeasure {


	static hidra.qualityIndicator.util.MetricsUtil utils_;

	static final double pow_ = 2.0;   

	public ConvergenceMeasure() {
		utils_ = new hidra.qualityIndicator.util.MetricsUtil();
	} 


	public double convergenceMeasure(double [][] nonDominetedSet,
			double [][] trueParetoFront, 
			int numberOfObjectives,Problem problem, boolean correct)  {
     	
		double [] maximumValue ;
		double [] minimumValue ;

		
		
	   boolean analytical =  
			   	   (problem instanceof DTLZ1)
			   	|| (problem instanceof DTLZ2) 
				|| (problem instanceof DTLZ3) 
				|| (problem instanceof DTLZ4)
				|| (problem instanceof DTLZ6);


		// STEP 3. Sum the distances between each point of the front and the 
		// nearest point in the true Pareto front
		double sum = 0.0;
		
		
		if(analytical){
						
			for (int i = 0; i < nonDominetedSet.length; i++) 
				sum +=   distanceToClosedPointAnalytic(nonDominetedSet[i],problem, correct);
			
		}else{
			
			// STEP 1. Obtain the maximum and minimum values of the Pareto front
			maximumValue = utils_.getMaximumValues(trueParetoFront, numberOfObjectives);
			minimumValue = utils_.getMinimumValues(trueParetoFront, numberOfObjectives);

		
			for (int i = 0; i < nonDominetedSet.length; i++) 
				sum +=   distanceToClosedPoint(nonDominetedSet[i] ,
								trueParetoFront, minimumValue, maximumValue);
					
		}
		
		
		// STEP 5. Divide the sum by the maximum number of points of the front
		double generationalDistance = sum / nonDominetedSet.length;

		return generationalDistance;
	
	
	} // generationalDistance

	/*
	public double convergenceMeasure(double [][] nonDominetedSet,  Problem problem)  {
     			
		// STEP 3. Sum the distances between each point of the front and the 
		// nearest point in the true Pareto front
		double sum = 0.0;
				
		for (int i = 0; i < nonDominetedSet.length; i++) 
			sum +=   distanceToClosedPointAnalytic(nonDominetedSet[i],problem);
						
		// STEP 5. Divide the sum by the maximum number of points of the front
		double generationalDistance = sum / nonDominetedSet.length;

		return generationalDistance;
	
	
	} // generationalDistance

	*/


	public double distanceToClosedPoint(double [] point, double [][] front, double[] minimumValue, double[] maximumValue) {
		double minDistance = distance(point, front[0], minimumValue, maximumValue);


		for (int i = 1; i < front.length; i++) {
			double aux = distance(point, front[i], minimumValue, maximumValue);
			if (aux < minDistance) {
				minDistance = aux;
			}
		}

		return minDistance;
	} // distanceToClosedPoint

	
	

	public double distanceToClosedPointAnalytic(double [] point, Problem problem, boolean correct) {
		
		double distance = 0.0;
		
		if(problem instanceof DTLZ1){
			
			double sum = 0.0;
			
			for(int i=0; i < point.length ; i++){
				sum += point[i];
			}
			
			if(correct){				
				distance =    Math.abs(   ( (2 * sum - 1.0) / (2.0 * Math.sqrt(point.length) ) ) )  ;				
			}else{
				distance =    Math.abs(   sum - 0.5    )    ;
			}
			
		}else if ( (problem instanceof DTLZ2) 
				|| (problem instanceof DTLZ3) 
				|| (problem instanceof DTLZ4)
				|| (problem instanceof DTLZ6))  {
			
			distance = norm( point  ) - 1; // d = |r|-1
				
	    }
					
		
		return distance;
		
	} // distanceToClosedPoint

	
	
	

	
	
	public double norm(double [] vector) {
		double distance = 0.0;

		for (int i = 0; i < vector.length; i++) {
			distance += Math.pow(vector[i],2.0);
		}
		return Math.sqrt(distance);
	} // distance

	
	
	
	
	
	public double distance(double [] a, double [] b, double[] minimumValue, double[] maximumValue) {
		double distance = 0.0;
		double temp = 0.0; 

		for (int i = 0; i < a.length; i++) {
			temp = (a[i]-b[i]) / (maximumValue[i] - minimumValue[i]) ;
			distance += Math.pow(temp,2.0);
		}
		return Math.sqrt(distance);
		
	} // distance



	/**
	 * This class can be invoqued from the command line. Two params are required:
	 * 1) the name of the file containing the front, and 2) the name of the file 
	 * containig the true Pareto front
	 * @throws ClassNotFoundException 
	 **/
	public static void main(String args[]) throws ClassNotFoundException {
		/*if (args.length < 2) {
			System.err.println("GenerationalDistance::Main: Usage: java " +
					"GenerationalDistance <FrontFile> " +
					"<TrueFrontFile>  <numberOfObjectives>");
			System.exit(1);
		} // if
*/
		// STEP 1. Create an instance of Generational Distance
		ConvergenceMeasure qualityIndicator = new ConvergenceMeasure();

		// STEP 2. Read the fronts from the files
		double [][] solutionFront = qualityIndicator.utils_.readFront("paretos_matlab/FUN");
		double [][] trueFront     =  null; //qualityIndicator.utils_.readFront("paretos/");

		
		DTLZ1 problem =  new DTLZ1("Real");
		
		// STEP 3. Obtain the metric value
		double value = qualityIndicator.convergenceMeasure(solutionFront, trueFront, 3, problem,true);
				
				
				
				
				

		System.out.println(value); 



	}

}
