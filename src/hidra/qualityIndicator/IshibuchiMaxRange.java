package hidra.qualityIndicator;

public class IshibuchiMaxRange {

	
	 static hidra.qualityIndicator.util.MetricsUtil utils_; 
	
	 public IshibuchiMaxRange() {
		 utils_ = new hidra.qualityIndicator.util.MetricsUtil();
	}
	
	public double calculate(double[][] front, int numberOfObjectives){
		
		
		 double[] maximumValue = utils_.getMaximumValues(front,numberOfObjectives);
		 double[] minimumValue = utils_.getMinimumValues(front,numberOfObjectives);
		
	
		 double sum = 0.0;
		 
		 for(int k = 0; k < numberOfObjectives; k++){
			 
			 sum += (maximumValue[k] - minimumValue[k]);
			 
		 }
		 				 
		 return sum;
		 
	}
	
	
	
	
}
