/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computação - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.metaheuristics.mopsocdr.util;






import hidra.jmetal.core.Solution;
import hidra.jmetal.core.SolutionSet;

import java.math.BigDecimal;

import jmetal.util.JMException;
import jmetal.util.comparators.ObjectiveComparator;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class ParetoFrontUtil {
		
	

	
	public static Solution getNearSolution(SolutionSet solutionSet, Solution solution){
		
		double[][] front = getRawFront(solutionSet);		
		if(front == null){
			return null;
		}		
		int numObj = solution.numberOfObjectives(); 
		double[] solutionFitness = new double[numObj];
		
		for(int i=0; i < numObj ; i++){						
			solutionFitness[i] = solution.getObjective(i);
		}
		
		double[] normalizedPositionParticle = new double[numObj];
		double[] normalizedPositionSolution = new double[numObj];
		
		double fitnessByObj = 0;
		double currentEuclidianDistance = 0;
		
		//double minEuclidianDistance = Double.MAX_VALUE;
		double minEuclidianDistance = Double.POSITIVE_INFINITY;
		
		Solution nearSolution = null;
		
		
		double[] maximumValue = getMaximumValues(front, numObj);
		double[] minimumValue = getMinimumValues(front, numObj);
				
		for(int currentObj = 0; currentObj < numObj; currentObj++){			
			fitnessByObj = solutionFitness[currentObj];
			minimumValue[currentObj] = (minimumValue[currentObj] > fitnessByObj) ?  fitnessByObj : minimumValue[currentObj];
			maximumValue[currentObj] = (maximumValue[currentObj] < fitnessByObj) ?  fitnessByObj : maximumValue[currentObj];
			normalizedPositionParticle[currentObj] = (fitnessByObj - minimumValue[currentObj]) / (maximumValue[currentObj] - minimumValue[currentObj]);
			if((maximumValue[currentObj] - minimumValue[currentObj]) == 0)
				normalizedPositionParticle[currentObj] = 1;
		}
		
		//nearSolution = archive.get(0);
		for (int i=0; i< solutionSet.size(); i++) {			
			Solution currentSolution = solutionSet.get(i);
			if(!isEqualSolutions(solution, currentSolution)){
				for(int currentObj = 0; currentObj < numObj; currentObj++){
					fitnessByObj = currentSolution.getObjective(currentObj);
					normalizedPositionSolution[currentObj] = 
						(fitnessByObj-minimumValue[currentObj])/ ((maximumValue[currentObj] - minimumValue[currentObj]));
					if((maximumValue[currentObj] - minimumValue[currentObj]) == 0)
						normalizedPositionSolution[currentObj] = 1;
				}
				currentEuclidianDistance = 
					euclidianDistance(normalizedPositionParticle, normalizedPositionSolution);
				if(currentEuclidianDistance < minEuclidianDistance){
					minEuclidianDistance = currentEuclidianDistance;
					nearSolution = currentSolution;
				}
			}
		}
		
		if(solutionSet.size() == 0) throw new RuntimeException("Archive is empty");
		if(solutionSet.size() == 1){
			nearSolution = solutionSet.get(0);
		}
		
		return nearSolution;
	}
	
	
	 public static double[][] getRawFront(SolutionSet solutionSet){

		 if (solutionSet.size() == 0) {
			 return null;
		 }

		 double [][] front;

		 int dimObj = solutionSet.get(0).numberOfObjectives();
		 int numSol = solutionSet.size();

		 front = new double[numSol][dimObj];


		 for (int currSol = 0; currSol < numSol; currSol++) {
			 for (int currDim = 0; currDim < dimObj; currDim++) {
				 front[currSol][currDim] = solutionSet.get(currSol).getObjective(currDim);
			 }
		 }
		 return front;

	 }
	
	
	
	//Verificar depois
	private static boolean  isEqualSolutions(Solution solution, Solution anotherSolution){
		boolean equal = true;
		int numDim = solution.numberOfVariables();
		int numObj = solution.numberOfObjectives();
		
		for(int dimension = 0; (dimension < numDim) && equal; dimension++){
			try {
				if(solution.getDecisionVariables()[dimension].getValue() 
				                  != anotherSolution.getDecisionVariables()[dimension].getValue()){
					equal = false;
				}
			} catch (JMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int currentObj=0; (currentObj < numObj) && equal; currentObj++){
			if(solution.getObjective(currentObj) != anotherSolution.getObjective(currentObj)){
				equal = false;
			}
		}
		
		return equal;
	}
	
	
	
public static void crowdingDistanceAssignmentToMOPSOCDR(SolutionSet solutionSet, int numObj){
		
		int size = solutionSet.size();        		
		double delta = 0.0;								
		//Use a new SolutionSet to avoid alter original solutionSet
		SolutionSet front = new SolutionSet(size);
		for (int i = 0; i < size; i++){
			front.add(solutionSet.get(i));
		}
					
		double beforeSolution = 0,currentSolution=0,afterSolution=0,lastSolution=0,firstSolution=0;
		double crowdingDistance = 0.0;
		
		
		
		for (int i = 0; i < size; i++){
			front.get(i).setCrowdingDistance(0.0);        
		}
		
		for(int currentObj=0; currentObj < numObj; currentObj++){
			front.sort(new ObjectiveComparator(currentObj));
			lastSolution = front.get(size-1).getObjective(currentObj);
			firstSolution = front.get(0).getObjective(currentObj);
			
			delta = lastSolution - firstSolution; 
			
			if(delta < 0.0){
				throw new RuntimeException("Delta Menor que Zero");
			}
			
			for(int solution=0; solution < front.size() ;solution++){
				if(delta != 0.0 && front.size() >= 2){
					if(solution == 0){
						currentSolution = front.get(solution).getObjective(currentObj);
						afterSolution = front.get(solution+1).getObjective(currentObj);			
						crowdingDistance = ((afterSolution-currentSolution) + (lastSolution-firstSolution))/delta;
					}else if(solution == front.size()-1){
						beforeSolution = front.get(solution-1).getObjective(currentObj);
						currentSolution = front.get(solution).getObjective(currentObj);
						crowdingDistance = ((currentSolution-beforeSolution) + (lastSolution-firstSolution))/delta;
					}else{
					 beforeSolution = front.get(solution-1).getObjective(currentObj);
					 currentSolution = front.get(solution).getObjective(currentObj);
					 afterSolution = front.get(solution+1).getObjective(currentObj);
					 crowdingDistance = (afterSolution-beforeSolution)/delta;
					}
					//crowdingDistances[solution] +=  result;
					
			   }else{
				   crowdingDistance = 0.0;
			   }
				crowdingDistance = front.get(solution).getCrowdingDistance() + crowdingDistance;
				front.get(solution).setCrowdingDistance(crowdingDistance);
			}
		}

	}

	
	
	
/*	public static Solution getNearSolution(SolutionSet solutionSet, Solution solution){
				
		double [][] front = solutionSet.getRawFront();
		
		
		
		if(front == null){
			return null;
		}
		
		int noObjectives = front[0].length; 
		
		double[] maximumValue = getMaximumValues(front, noObjectives);
		double[] minimumValue = getMinimumValues(front, noObjectives);
		
		double[] normRefSolution  = new double[noObjectives];
		
		for(int currentObj = 0; currentObj < noObjectives; currentObj++){
			double fitnessByObj = solution.getObjectives()[currentObj];
			minimumValue[currentObj] = (minimumValue[currentObj] > fitnessByObj) ?  fitnessByObj : minimumValue[currentObj];
			maximumValue[currentObj] = (maximumValue[currentObj] < fitnessByObj) ?  fitnessByObj : maximumValue[currentObj];				
			normRefSolution[currentObj] = (fitnessByObj - minimumValue[currentObj]) / (maximumValue[currentObj] - minimumValue[currentObj]);
		}
		
		double[][] normParetoFront = normalizeFront(front, maximumValue, minimumValue);
		
		
		double currentEuclidianDistance = 0.0;
		double minEuclidianDistance = Double.POSITIVE_INFINITY;
		Solution nearSolution = null;
		
		
		for(int i=0; i< solutionSet.size(); i++){
			
			if(!isEqualSolutions(solution,solutionSet.get(i))){
				
				currentEuclidianDistance = euclidianDistance(normRefSolution,normParetoFront[i]);
				
				if(currentEuclidianDistance < minEuclidianDistance){
					minEuclidianDistance = currentEuclidianDistance;
					nearSolution = solutionSet.get(i);
				}
				
			}
			
		}
		
		
	
		if(solutionSet.size() == 0) throw new RuntimeException("Archive is empty");
		if(solutionSet.size() == 1){
			nearSolution = solutionSet.get(0);
		}
		
		return nearSolution;
		
	}*/
	
	
	/************************************************************
	 * 
	 * @param solution
	 * @param anotherSolution
	 * @param numDim
	 * @param numObj
	 * @return
	 *************************************************************/	
	/*private static boolean isEqualSolutions(Solution solution, Solution anotherSolution){
		boolean equal = true;
		
		int numDim,  numObj;
		
		if(solution == null || anotherSolution == null){
			return false;
		}
		
		
		numDim = solution.getNumberVariables();
		numObj = solution.getNumberObjectives();
		
		
		for(int currentObj=0; (currentObj < numObj) && equal; currentObj++){
			
			BigDecimal objSol        = new BigDecimal(solution.getObjectives()[currentObj]); 
			BigDecimal anotherObjSol = new BigDecimal(anotherSolution.getObjectives()[currentObj]);
			
			if( objSol.compareTo(anotherObjSol) != 0 ){
				equal = false;
			}
		}
		
		
		
		
		for(int dimension = 0; (dimension < numDim) && equal; dimension++){
			
			BigDecimal varSol = 	   new BigDecimal(solution.getDecisionVariables()[dimension]);
			BigDecimal anotherVarSol = new BigDecimal(anotherSolution.getDecisionVariables()[dimension]);
			
			if(varSol.compareTo(anotherVarSol) != 0){
				equal = false;
			}
		}
		
		
		
		return equal;
	}*/
	
	
	
	//private final static double EPSILON = 0.000000000000000000001;
	
	public static double [][] normalizeFront(double [][] front, 
			double [] maximumValue,
			double [] minimumValue) {

		if(front == null) return null;
		
		double [][] normalizedFront = new double[front.length][];
		
		int numObj = front[0].length;
		
		//double[] delta = new double[numObj];
		
		for(int j=0; j < numObj ; j++){
			BigDecimal maxVal = new BigDecimal(maximumValue[j]);
			BigDecimal minVal = new BigDecimal(minimumValue[j]);
			
			if(maxVal.compareTo(minVal) == 0){
				throw new RuntimeException("Division By Zero");
			}
		}
		
		for (int i = 0; i < front.length;i++) {
			normalizedFront[i] = new double[front[i].length];						
			
			for (int j = 0; j < front[i].length; j++) {
				normalizedFront[i][j] = (front[i][j] - minimumValue[j]) /
						(maximumValue[j] - minimumValue[j]);
			}
		}
		return normalizedFront;
	} 

	
	public static double [] getMinimumValues(double [][] front, int noObjectives) {
		double [] minimumValue = new double[noObjectives];
		for (int i = 0; i < noObjectives; i++)
			minimumValue[i] = Double.POSITIVE_INFINITY;

		for (int i = 0;i < front.length; i++) {
			for (int j = 0; j < front[i].length; j++) {
				if (front[i][j] < minimumValue[j]) 
					minimumValue[j] = front[i][j];
			}
		}
		return minimumValue;
	}
	
	
	public static double [] getMaximumValues(double[][] front, int noObjectives) {
		double [] maximumValue = new double[noObjectives];
		for (int i = 0; i < noObjectives; i++)
			maximumValue[i] =  Double.NEGATIVE_INFINITY;


		for (int i =0; i < front.length;i++ ) {
			for (int j = 0; j < front[i].length; j++) {
				if (front[i][j] > maximumValue[j])
					maximumValue[j] = front[i][j];
			}
		}

		return maximumValue;
	}

	
	 public static double distanceToNearestPoint(double [] point, double [][] front) {
		    double minDistance = Double.MAX_VALUE;
		    
		    for (int i = 0; i < front.length; i++) {
		      double aux = euclidianDistance(point,front[i]);
		      if ((aux < minDistance) && (aux > 0.0)) {
		        minDistance = aux;
		      }
		    }
		    
		    return minDistance;
    }
	
	
	
	  public static double distanceToClosedPoint(double [] point, double [][] front) {
		    double minDistance = euclidianDistance(point,front[0]);
		    
		    
		    for (int i = 1; i < front.length; i++) {
		      double aux = euclidianDistance(point,front[i]);
		      if (aux < minDistance) {
		        minDistance = aux;
		      }
		    }
		    
		    return minDistance;
	 } 
	
	  
	  
	  public static double euclidianDistance(double [] a, double [] b) {
		    double distance = 0.0;
		    
		    for (int i = 0; i < a.length; i++) {
		      distance += Math.pow(a[i]-b[i],2.0);
		    }
		    return Math.sqrt(distance);
	 }
	  
	  
/*	  public double[][] readNonDominatedSolutionSet(String path) {
		    try {
		       Open the file 
		      FileInputStream fis   = new FileInputStream(path)     ;
		      InputStreamReader isr = new InputStreamReader(fis)    ;
		      BufferedReader br      = new BufferedReader(isr)      ;
		      
		      // Create  an unbounded SolutionSet 
		     List<double[]> 
		      
		      String aux = br.readLine();
		      while (aux!= null) {
		        StringTokenizer st = new StringTokenizer(aux);
		        int i = 0;
		        Solution solution = new Solution(st.countTokens());
		        while (st.hasMoreTokens()) {
		          double value = (new Double(st.nextToken())).doubleValue();
		          solution.setObjective(i,value);
		          i++;
		        }
		        solutionSet.add(solution);
		        aux = br.readLine();
		      }
		      br.close();
		      return solutionSet;
		    } catch (Exception e) {
		      System.out.println("jmetal.qualityIndicator.util.readNonDominatedSolutionSet: "+path);
		      e.printStackTrace();
		    }
		    return null;
	 } // readNonDominatedSolutionSet 
	  */
	
}

