package hidra.many.metaheuristics.MDFA;


import hidra.jmetal.core.Solution;

import java.util.Comparator;



public class MDFAFitnessComparator implements Comparator<Solution>{

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
