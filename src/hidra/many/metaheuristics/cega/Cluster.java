package hidra.many.metaheuristics.cega;

import hidra.core.util.Util;
import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.comparators.ObjectiveComparator;



public class Cluster {

	private List<Solution> cluster;
	
	
	public Cluster() {
		this.cluster = new ArrayList<Solution>();
	}
	
	public SolutionSet toSolutionSet(){
		
		SolutionSet solutionSet = new SolutionSet(this.cluster.size());
		
		for(int i=0; i < this.cluster.size() ;i++){
			solutionSet.add(  this.cluster.get(i)   );
		}
		
		return solutionSet;
	}
	
	
	public int size(){
		return cluster.size();
	}
	
	public Solution getSolution(int i){
		return cluster.get(i);
	}

	
	public void remove(int index){
		this.cluster.remove(index);
	}
	
	public int getIndexLeader(){

		if(  (cluster == null) || (cluster.isEmpty()))
			throw new RuntimeException("Cluster empty!!!");

		int numObj = cluster.get(0).numberOfObjectives();
		
		double[] gbest = new double[numObj];

		for(int j=0; j < numObj  ;j++){
			Collections.sort(cluster,new ObjectiveComparator(j));			
			gbest[j] = cluster.get(0).getObjective(j);		    		
		}
		
		
		double minimumDIstance = Double.POSITIVE_INFINITY;
		int indexBest = 0;
		double tempDist = 0.0;

		for(int i=0;i < cluster.size() ;i++){

			tempDist = Util.euclidianDistance(gbest, cluster.get(i));

			if(tempDist < minimumDIstance ){
				indexBest = i;
				minimumDIstance = tempDist;
			}

		}

		return indexBest;
		
	}

	                        
	
	
	public double linkage(Cluster otherCluster){
		
		int count = 0;
		double sum  = 0.0;
		
		for(int i=0; i < cluster.size() ;i++){
			
			for(int j=0; j < otherCluster.size()    ;j++){
				
				try {
					sum += new Distance().distanceBetweenSolutions(cluster.get(i), otherCluster.getSolution(j));
				} catch (JMException e) {
					throw new RuntimeException("Error in Clustering");
				}				
			}
			
		}
		
		count = cluster.size() * otherCluster.size();
		
		double value = sum / count;
		
		return value;
	}
	
	public void union(Cluster cluster){
		
		this.cluster.addAll(cluster.cluster);
		
	}
	
	public void addSolution(Solution s){
		this.cluster.add(s);
	}
	
}
