/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computa��o - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.qualityIndicator;

import jmetal.core.Problem;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class Diversity2 {

	
	
	static hidra.qualityIndicator.util.MetricsUtil utils_; 
	
	 public Diversity2() {
		 utils_ = new hidra.qualityIndicator.util.MetricsUtil();
	 }
	
	public double diversity(double[][] front, Problem problem){
		
		double sumMetric = 0.0;
		double metric = 0;
					
		double[] maximumValueFront = utils_.getMaximumValues(front,problem.getNumberOfObjectives());
		double[] minimumValueFront = utils_.getMinimumValues(front,problem.getNumberOfObjectives());
		
		
		for(int obj=0; obj < problem.getNumberOfObjectives() ;obj++){
					
			double[] h = new double[front.length+2];
			
			double delta 
				= (maximumValueFront[obj] - minimumValueFront[obj]) ;
			
			delta = delta / front.length;
			
			if(delta != 0){
			
				for(int j=0;  j < front.length ; j++){
					int index = 
						((int)((front[j][obj]-minimumValueFront[obj])/delta));
					
					h[index+1] = 1;   
				}
				
				h[0] = 1;
				h[front.length+1] = 1;
				
				double sum = 0;
				double value = 0.0;
				
				for(int i=1; i <= front.length ; i++){
					value = this.lookupTable(h, i);
					sum = sum + value;
				}
				
				metric = (sum-2)/ (front.length-2);
				
			}else{
				metric= 0;
			}
			
			
			sumMetric += metric;
		}
		
		sumMetric = sumMetric/problem.getNumberOfObjectives();
		
		return sumMetric;
	}
	
	
	
	private double lookupTable(double[] h, int i){
		
		if(h[i-1] == 0 && h[i] == 0 && h[i+1] == 0){
			return 0;
		}else if(h[i-1] == 0 && h[i] == 1 && h[i+1] == 0){
			return 0.75;
		}else if(h[i-1] == 1 && h[i] == 0 && h[i+1] == 1){
			return 0.75;
		}else if(h[i-1] == 1 && h[i] == 0 && h[i+1] == 0){
			return 0.50;
		}else if(h[i-1] == 0 && h[i] == 0 && h[i+1] == 1){
			return 0.50;
		}else if(h[i-1] == 0 && h[i] == 1 && h[i+1] == 1){
			return 0.67;
		}else if(h[i-1] == 1 && h[i] == 1 && h[i+1] == 0){
			return 0.67;
		}else{
			return 1;
		}
		
	}
	
	
}
