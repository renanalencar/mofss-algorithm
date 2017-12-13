package hidra.many.metaheuristics.smpsoms;


import jmetal.core.Solution;

import java.util.Comparator;


public class MSOPSComparator implements Comparator<Solution>{

	
	/*@Override
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
*/	
	
	private boolean dual = true;
	private int numTargets = 0;
	
	public MSOPSComparator(boolean dual, int targets) {
		this.dual = dual;
		this.numTargets = targets;
	}
	
	
	
	@Override
	public int compare(Solution ind_a, Solution ind_b) {

		int i;
		int target_size;
		if(dual) target_size = 2 * numTargets;
		else target_size = numTargets;		
		for(i = 0; i < target_size; i++)
		{
			if(ind_a.getScores()[i] < ind_b.getScores()[i])
			{
				return 1;
			}
			else if(ind_a.getScores()[i] > ind_b.getScores()[i])
			{
				return -1;
			}
		}
		return 0;
	}

	

	
	
	
	
	
	

	
	
	
	
	
	
}
