package hidra.metaheuristics.mDNPSO;

import java.util.Comparator;



public class DistanceFromNeighborComparator implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		
		Double v1 =   ((Double)((DNPSOSolution)o1).getDistanceFromNeighbor());
		Double v2 =   ((Double)((DNPSOSolution)o2).getDistanceFromNeighbor()); 
		
		int result = v1.compareTo(v2);
		
		return result;
	}

	
	
	/*@Override
	public int compare(Object o1, Object o2) {		
		return  ((Double)((DNPSOSolution)o1).getDistanceFromNeighbor()).compareTo( ((DNPSOSolution)o2).getDistanceFromNeighbor() )  ;
	}
	*/
	

}
