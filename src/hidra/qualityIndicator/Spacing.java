package hidra.qualityIndicator;

import jmetal.problems.DTLZ.DTLZ;
import jmetal.core.Problem;

public class Spacing {

	
	static hidra.qualityIndicator.util.MetricsUtil utils_;  // MetricsUtil provides some 
    // utilities for implementing
    // the metric

	public Spacing() {
		utils_ = new hidra.qualityIndicator.util.MetricsUtil();
	}
	
	public double spacing(double [][] front, 
            double [][] trueParetoFront, Problem problem){
				
		
		int numObj = problem.getNumberOfObjectives();
		
		/**
	     * Stores the maximum values of true pareto front.
	     */
	    double [] maximumValue ;
		    
	    /**
	     * Stores the minimum values of the true pareto front.
	     */
	    double [] minimumValue ;

	    /**
	     * Stores the normalized front.
	     */
	    double [][] normalizedFront ;

	    
	     if(problem instanceof DTLZ && ((DTLZ)problem).isAnalytic()){
	    	 
	    	 maximumValue =  ((DTLZ)problem).getMaximumValue();
	    	 minimumValue =  ((DTLZ)problem).getMinimumValue();
	    			 
	     }else{
	    
	    	// STEP 1. Obtain the maximum and minimum values of the Pareto front
	 	    maximumValue = utils_.getMaximumValues(trueParetoFront, numObj);
	 	    minimumValue = utils_.getMinimumValues(trueParetoFront, numObj);

	     }
	    

	    
	    
	    // STEP 2. Get the normalized front and true Pareto fronts	    
	    normalizedFront = utils_.getNormalizedFront(front, 
	    		                                        maximumValue, 
	    		                                        minimumValue);
	    
	  	
		
				
		double[] distances = new double[normalizedFront.length];
		double dist = 0.0;
		
		for(int i=0;  i < normalizedFront.length ;i++){
			
			double min = Double.POSITIVE_INFINITY;
			
			for(int j=0; j <normalizedFront.length; j++){
				
				if(i != j){				
					dist = distance(normalizedFront[i],normalizedFront[j],numObj);
					min = Math.min(min, dist);
				}
				
			}
			
			distances[i] = min;
		}
		
		
		double sum = 0.0;
		
		for(int i=0; i<distances.length;i++){
			
			sum += distances[i];
			
		}
		
		
		double dbar = sum / distances.length;
		
		
		sum = 0.0;
		
		for(int i=0; i<distances.length;i++){
			
			sum += Math.pow( (dbar - distances[i]) , 2.0);
		}
		
		double spacing = 0.0;
		spacing = sum/(distances.length-1);
		spacing = Math.sqrt(spacing);
			
		return spacing;
		
	}
	
	
	private double distance(double[] a,double[] b,int numObj){
		
		double sum =0.0;
		
		for(int i=0; i < numObj ;i++){
			
			sum += Math.abs( a[i] - b[i] );
			
		}
	
		return sum;
	}
	
	
	
}






