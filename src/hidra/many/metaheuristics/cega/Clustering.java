package hidra.many.metaheuristics.cega;


import jmetal.core.SolutionSet;

import java.util.ArrayList;
import java.util.List;



public class Clustering {

	private List<Cluster> clusters;
	private int numClusters;
	
	public Clustering(SolutionSet solutionSet, int numClusters) {
		this.clusters = new ArrayList<Cluster>();
		this.numClusters = numClusters;
		clustering(solutionSet);		
	}
	
	
	
	/*public SolutionSet  getSolutions(){
		
		SolutionSet  solutions = new SolutionSet( clusters.size() );
		
		for(int i=0; i< clusters.size();i++){
			
			Solution s = clusters.get(i).getLeader();
			solutions.add(s);
		}
		return solutions;
	}*/
	
	public List<Cluster> getClusters(){
		return this.clusters;
	}
	
	
	private void clustering(SolutionSet solutionSet){
						
		for(int i=0; i <  solutionSet.size(); i++){
			 Cluster c = new Cluster();
			 c.addSolution( solutionSet.get(i) );
			 clusters.add(  c  );
		}
				
		clustering(clusters);
	}
	
	
	private void clustering(List<Cluster> clusters){
		
		
		if(clusters.size() <= this.numClusters){
			return;
		}
		
		int a = 0,b = 0;
		double minDist = Double.POSITIVE_INFINITY;
		Double tempDist = null;
		
		for(int i=0; i < clusters.size() - 1 ;i++){
			
			for(int j=i+1; j< clusters.size() ; j++){
			
				tempDist = clusters.get(i).linkage(clusters.get(j));
				if(tempDist < minDist ){
					a = i;
					b = j;
					minDist = tempDist;
				}
			}
		}
	   	
		
		clusters.get(a).union(clusters.get(b));
		clusters.remove(b);
		
		clustering(clusters);
	}
	
	
}
