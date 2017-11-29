package hidra.qualityIndicator;

import jmetal.util.comparators.DominanceComparator;

public class Coverage {

	
	public double coverage(double [][] a, double [][] b  ) {
		
		DominanceComparator dominance = new DominanceComparator();
		int count = 0;
		boolean flag = true;
		
		for(int i=0; i < b.length ; i++){
			flag = true;
			for(int j=0; j < a.length && flag; j++){
				
				if(dominance.compare(a, b) == -1){
					count++;
					flag = false;
				}
				
			}
			
		}
		
		double converage = count/b.length;
		
		return converage;
		
	}

	
	
}
