package hidra.core.population;

import jmetal.core.Solution;
import hidra.many.metaheuristics.cega.DominanceComparator2;
import hidra.many.metaheuristics.fitness.Fitness;

import java.util.Comparator;

import jmetal.util.archive.Archive;
import jmetal.util.comparators.EqualSolutions;

public class BoundArchive extends Archive {

	

	/** 
	 * Stores the maximum size of the archive.
	 */
	protected int maxSize_;

	/**
	 * stores the number of the objectives.
	 */
	protected int objectives_;    

	/**
	 * Stores a <code>Comparator</code> for dominance checking.
	 */
	//private Comparator dominance_;
	protected Comparator dominance_;

	/**
	 * Stores a <code>Comparator</code> for equality checking (in the objective
	 * space).
	 */
	//private Comparator equals_; 
	protected Comparator equals_;

	/**
	 * Stores a <code>Comparator</code> for checking crowding distances.
	 */
	protected Comparator comparator_; 

	/**
	 * Stores a <code>Distance</code> object, for distances utilities
	 */
	protected Fitness fitness_; 

	/**
	 * Constructor. 
	 * @param maxSize The maximum size of the archive.
	 * @param numberOfObjectives The number of objectives.
	 */
	public BoundArchive(int maxSize, int numberOfObjectives, Comparator<Solution> comparator, Fitness fitness ) {
		super(maxSize);
		maxSize_          = maxSize;
		objectives_       = numberOfObjectives;  
		comparator_ 	  = comparator;
		fitness_          = fitness;
		dominance_        = new DominanceComparator2();
		equals_           = new EqualSolutions();		 		
	} // CrowdingArchive


	/**
	 * Adds a <code>Solution</code> to the archive. If the <code>Solution</code>
	 * is dominated by any member of the archive, then it is discarded. If the 
	 * <code>Solution</code> dominates some members of the archive, these are
	 * removed. If the archive is full and the <code>Solution</code> has to be
	 * inserted, the solutions are sorted by crowding distance and the one having
	 * the minimum crowding distance value.
	 * @param solution The <code>Solution</code>
	 * @return true if the <code>Solution</code> has been inserted, false 
	 * otherwise.
	 */
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
			fitness_.fitnessAssign(this , objectives_);      
			remove(indexWorst(comparator_));
		}        
		return true;
	} // add
	
	
	
	/** 
	   * Returns the index of the worst Solution using a <code>Comparator</code>.
	   * If there are more than one occurrences, only the index of the first one is returned
	   * @param comparator <code>Comparator</code> used to compare solutions.
	   * @return The index of the worst Solution attending to the comparator or 
	   * <code>-1<code> if the SolutionSet is empty
	   */
	  public int indexWorst(Comparator comparator){
	    
	   
	    if ((solutionsList_ == null) || (this.solutionsList_.isEmpty())) {
	        return -1;
	    }
	    
	    int index = 0;
	    Solution worstKnown = solutionsList_.get(0), candidateSolution;
	    int flag;
	    for (int i = 1; i < solutionsList_.size(); i++) {        
	        candidateSolution = solutionsList_.get(i);
	        flag = comparator.compare(worstKnown, candidateSolution);
	        if (flag == 1) {
	            index = i;
	            worstKnown = candidateSolution;
	        }
	    }
	    
	    return index;
	        
	  } // indexWorst
	
	
	public void remove(Solution solution){
		super.solutionsList_.remove(solution);
	}
	
	
}
