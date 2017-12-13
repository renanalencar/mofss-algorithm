/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computa��o - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.many.metaheuristics.comparators;

import java.util.Collections;
import java.util.Comparator;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;



/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class GeneralizedRoletteWheelSelection {

	SolutionSet solutionSet;
	double summatory;
	
	
	
	
	public GeneralizedRoletteWheelSelection(SolutionSet paramSolutionSet,
			Comparator<Solution> fitnessComp) {
		super();
		
		SolutionSet front = new SolutionSet(paramSolutionSet.size());
		
		for (int i = 0; i < paramSolutionSet.size(); i++){
			front.add(paramSolutionSet.get(i));
		}
		
		//Sort in descending order
		front.sort(Collections.reverseOrder(fitnessComp));
		//Collections.reverse(front,fitnessComp);
		
		this.solutionSet = front;
		this.summatory = 0.0;				
		//solutionSet is OK
		for(int i=0; i < this.solutionSet.size() ;i++){
			summatory = summatory + this.solutionSet.get(i).getFitness();
		}
		
	}




	public Solution execute(){
		double rouletteValue = 0.0; 
		double partial = 0.0;
		int randomElement = 0;
		int currentSolution = 0;
		rouletteValue = summatory * Math.random();
		
		if(summatory !=0){
			do{
				partial += solutionSet.get(currentSolution).getFitness();
				currentSolution++;
			}while(partial < rouletteValue);
			return solutionSet.get(currentSolution-1);
		}else{
			randomElement = (int) (Math.random() * solutionSet.size() );
			return solutionSet.get(randomElement);
		}		
	}
	
}
