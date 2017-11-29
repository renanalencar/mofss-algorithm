/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computação - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.metaheuristics.mopsocdls;

import hidra.core.population.HIDRACrowdingArchive;
import hidra.jmetal.core.Solution;
import jmetal.util.archive.CrowdingArchive;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class CDLSArchive extends HIDRACrowdingArchive{

	public CDLSArchive(int maxSize, int numberOfObjectives) {
		super(maxSize, numberOfObjectives);		
	}

	
	public boolean add(Solution solution){
		int flag = 0;
		int i = 0;
		Solution aux; //Store an solution temporally
		while (i < solutionsList_.size()){
			aux = solutionsList_.get(i);            

			flag = dominance_.compare(solution,aux);
			if (flag == 1) {               // The solution to add is dominated
				return false;                // Discard the new solution
			} else if (flag == -1) {       // A solution in the archive is dominated
				solutionsList_.remove(i);    // Remove it from the population            
			} else {
				if (equals_.compare(aux,solution)==0) { // There is an equal solution 
					// in the population
					return false; // Discard the new solution
				}  // if
				i++;
			}
		}
		// Insert the solution into the archive
		solutionsList_.add(solution);        
		if (size() > maxSize_) { // The archive is full
			distance_.crowdingDistanceAssignment(this,objectives_);      
			remove(indexWorst(crowdingDistance_));
		}        
		return true;
	} // add
}
