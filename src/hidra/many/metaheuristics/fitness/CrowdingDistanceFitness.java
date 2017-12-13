package hidra.many.metaheuristics.fitness;

import jmetal.core.SolutionSet;
import jmetal.util.comparators.ObjectiveComparator;

public class CrowdingDistanceFitness implements Fitness{

	
	public void fitnessAssign(SolutionSet solutionSet, int nObjs) {
	    int size = solutionSet.size();        
	                
	    if (size == 0)
	      return;
	    
	    if (size == 1) {
	      solutionSet.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
	      return;
	    } // if
	        
	    if (size == 2) {
	      solutionSet.get(0).setCrowdingDistance(0.0);
	      solutionSet.get(1).setCrowdingDistance(0.0);
	      return;
	    } // if       
	        
	    //Use a new SolutionSet to evite alter original solutionSet
	    SolutionSet front = new SolutionSet(size);
	    for (int i = 0; i < size; i++){
	      front.add(solutionSet.get(i));
	    }
	        
	    for (int i = 0; i < size; i++)
	      front.get(i).setCrowdingDistance(0.0);        
	        
	    double objetiveMaxn;
	    double objetiveMinn;
	    double distance;
	                
	    for (int i = 0; i<nObjs; i++) {          
	      // Sort the population by Obj n            
	      front.sort(new ObjectiveComparator(i));
	      objetiveMinn = front.get(0).getObjective(i);      
	      objetiveMaxn = front.get(front.size()-1).getObjective(i);      
	      
	      //Set de crowding distance            
	      front.get(0).setCrowdingDistance(0.0);
	      front.get(size-1).setCrowdingDistance(0.0);                                      
	      
	      for (int j = 1; j < size-1; j++) {
	        distance = front.get(j+1).getObjective(i) - front.get(j-1).getObjective(i);                    
	        distance = distance / (objetiveMaxn - objetiveMinn);        
	        distance += front.get(j).getCrowdingDistance();                
	        front.get(j).setCrowdingDistance(distance);   
	      } // for
	    } // for        
	  } // crowdingDistanceAssing  
	
	
}
