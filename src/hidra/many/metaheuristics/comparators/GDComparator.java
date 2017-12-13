package hidra.many.metaheuristics.comparators;

import java.util.Comparator;

import jmetal.core.Solution;

public class GDComparator implements Comparator<Solution>{

	
	public GDComparator() {
		// TODO Auto-generated constructor stub
	}
	
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
