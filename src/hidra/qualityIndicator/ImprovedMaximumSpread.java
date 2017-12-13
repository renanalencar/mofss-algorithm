package hidra.qualityIndicator;

import jmetal.problems.DTLZ.DTLZ1;
import jmetal.problems.DTLZ.DTLZ2;
import jmetal.problems.DTLZ.DTLZ3;
import jmetal.problems.DTLZ.DTLZ4;
import jmetal.core.Problem;

public class ImprovedMaximumSpread {

	
	 static hidra.qualityIndicator.util.MetricsUtil utils_; 
		
	 public ImprovedMaximumSpread() {
		 utils_ = new hidra.qualityIndicator.util.MetricsUtil();
	 }
	
	public double calculate(double[][] front, double [][] trueParetoFront, Problem problem, int numberOfObjectives){
		
		
		 
		
		 boolean analytical =  
			   	   (problem instanceof DTLZ1)
			   	|| (problem instanceof DTLZ2) 
				|| (problem instanceof DTLZ3) 
				|| (problem instanceof DTLZ4);

		 
		 double[] maximumValueTrueFront = new double[numberOfObjectives];
		 double[] minimumValueTrueFront = new double[numberOfObjectives];
		 
		 if(analytical){
			 
			 double max = Double.NaN;
			 
			 if(problem instanceof DTLZ1){ 
				  max = 0.5;	 
			 }else{
				  max = 1.0;
			 }
			 
			 for(int i=0; i < numberOfObjectives;  i++){
				 maximumValueTrueFront[i]  = max;
				 minimumValueTrueFront[i]  = 0.0;
			 }
			 
		 }else{
			 maximumValueTrueFront = utils_.getMaximumValues(trueParetoFront, numberOfObjectives);
			 minimumValueTrueFront = utils_.getMinimumValues(trueParetoFront, numberOfObjectives);			 
		 }
		 
		 

		 
		 
		 double[] maximumValueFront = utils_.getMaximumValues(front,numberOfObjectives);
		 double[] minimumValueFront = utils_.getMinimumValues(front,numberOfObjectives);
			
		 double sum = 0.0;
		 double temp = 0.0;
		 
		 
		 
		 
		 for(int k = 0; k < numberOfObjectives; k++){
			 temp = (maximumValueFront[k] - minimumValueFront[k])/(maximumValueTrueFront[k]-minimumValueTrueFront[k]);
			 sum += Math.pow( temp , 2.0);
			 
		 }
		 
		 double MSF = Math.sqrt(sum);
		 
		 
		 
		 
		
		 sum = 0.0;
		 
		 for(int k = 0; k < numberOfObjectives; k++){
			 
			 sum += Math.pow( maximumValueTrueFront[k] - minimumValueTrueFront[k] , 2.0);
			 
		 }
		 
		 
		 double MST = Math.sqrt(sum);
		
		 double ratio = MSF/MST;
		 
		 				 
		 return ratio;
		 
	}
	
}
