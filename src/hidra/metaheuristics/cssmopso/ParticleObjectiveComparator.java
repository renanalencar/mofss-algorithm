/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computação - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.metaheuristics.cssmopso;

import java.util.Comparator;

import jmetal.util.comparators.ObjectiveComparator;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class ParticleObjectiveComparator implements Comparator<Particle>{

	private ObjectiveComparator comp;
	
	
	
	
	public ParticleObjectiveComparator(int obj) {
		this.comp = new ObjectiveComparator(obj);
	}
	
	@Override
	public int compare(Particle o1, Particle o2) {		
		return comp.compare(o1.getSolution(), o2.getSolution());
	}

	
	
	
}
