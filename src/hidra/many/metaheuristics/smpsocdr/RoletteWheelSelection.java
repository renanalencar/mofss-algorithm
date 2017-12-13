/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computa��o - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.many.metaheuristics.smpsocdr;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;



/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class RoletteWheelSelection {

	public Solution execute(SolutionSet solutionSet, double summatory){
		double rouletteValue = 0.0; 
		double partial = 0.0;
		int randomElement = 0;
		int currentSolution = 0;
		rouletteValue = summatory * Math.random();
		
		if(summatory !=0){
			do{
				partial += solutionSet.get(currentSolution).getCrowdingDistance();
				currentSolution++;
			}while(partial < rouletteValue);
			return solutionSet.get(currentSolution-1);
		}else{
			randomElement = (int) (Math.random() * solutionSet.size() );
			return solutionSet.get(randomElement);
		}		
	}
	
}
