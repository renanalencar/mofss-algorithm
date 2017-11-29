/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computação - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package jmetal.problems.DTLZ;

import hidra.jmetal.core.Problem;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public abstract class DTLZ extends Problem{

	//protected Double hypervolume;
	
	//public abstract double getRefHypervolume();
	
	
	  public abstract boolean isAnalytic();
		
	  public abstract double[] getMaximumValue();
	  public abstract double[] getMinimumValue();
}
