/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computação - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.many.metaheuristics.cega;


import hidra.jmetal.core.Solution;

import java.util.Comparator;



/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class NormComparator implements Comparator<Solution>{

	@Override
	public int compare(Solution o1, Solution o2) {		
		if( o1.getFitness()  < o2.getFitness()){
			return -1;
		}else if(o1.getFitness()  > o2.getFitness()){
			return 1;
		}else{
			return 0;
		}
	}

}
