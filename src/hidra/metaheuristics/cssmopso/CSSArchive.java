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

import hidra.jmetal.core.Solution;
import jmetal.util.archive.CrowdingArchive;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class CSSArchive extends CrowdingArchive{

	public CSSArchive(int maxSize, int numberOfObjectives) {
		super(maxSize, numberOfObjectives);		
	}

	public void remove(Solution solution){
		super.solutionsList_.remove(solution);
	}
	
	public boolean add(Solution solution) {
		
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
	    return true;
		
	}
}
