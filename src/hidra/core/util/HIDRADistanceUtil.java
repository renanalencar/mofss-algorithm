package hidra.core.util;

import jmetal.core.SolutionSet;

public class HIDRADistanceUtil {


	public static double [] getMaximumValues(SolutionSet front, int noObjectives) {
		
		double[][] rawFront = front.writeObjectivesToMatrix();
		
		double [] maximumValue = new double[noObjectives];
		for (int i = 0; i < noObjectives; i++)
			maximumValue[i] =  Double.NEGATIVE_INFINITY;


		for (int i =0; i < rawFront.length;i++ ) {
			for (int j = 0; j < rawFront[i].length; j++) {
				if (rawFront[i][j] > maximumValue[j])
					maximumValue[j] = rawFront[i][j];
			}
		}

		return maximumValue;
	} // getMaximumValues


	
	public static double [] getMinimumValues(SolutionSet front, int noObjectives) {
		
		double[][] rawFront = front.writeObjectivesToMatrix();
		
		double [] minimumValue = new double[noObjectives];
		for (int i = 0; i < noObjectives; i++)
			minimumValue[i] = Double.POSITIVE_INFINITY;

		for (int i = 0;i < rawFront.length; i++) {
			for (int j = 0; j < rawFront[i].length; j++) {
				if (rawFront[i][j] < minimumValue[j]) 
					minimumValue[j] = rawFront[i][j];
			}
		}
		return minimumValue;
	} // getMinimumValues



}
