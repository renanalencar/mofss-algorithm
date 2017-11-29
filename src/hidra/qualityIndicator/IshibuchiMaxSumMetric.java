package hidra.qualityIndicator;


public class IshibuchiMaxSumMetric {

	
	
	public double calculate(double[][] front){
		
		double min = Double.POSITIVE_INFINITY;
		
		for(int i=0; i < front.length ; i++){
			min = Math.min(min, calculateSum(front[i]));
		}
				
		return min;
	
	}
	
	
	private double calculateSum(double[] x){
		
		double sum  = 0.0;
		
		for(int k=0; k < x.length; k++){
			
			sum += x[k] ;
		}
		
		return sum;
		
	}
	
}
