package hidra.many.metaheuristics.cega;


import hidra.jmetal.core.Solution;

import java.util.Comparator;



public class CEGAComparator implements Comparator<Solution>{

	@Override
	public int compare(Solution o1, Solution o2) {
		
		if( o1.getFitness() < o2.getFitness()){
			return -1;
		}else{
			
			if( o1.getFitness() > o2.getFitness()){
				return 1;
			}else{
				return 0;
			}
		}
		
	}

}
