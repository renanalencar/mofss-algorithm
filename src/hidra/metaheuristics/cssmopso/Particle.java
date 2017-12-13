/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computa��o - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.metaheuristics.cssmopso;

import jmetal.core.Solution;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class Particle{

	
	Solution solution;
	int index;
		
	
	
	
	public Particle(Solution solution, int index) {
		super();
		this.solution = solution;
		this.index = index;
	}
	
	
	public Solution getSolution() {
		return solution;
	}
	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		return this.solution.toString();
	}
	
	

}
