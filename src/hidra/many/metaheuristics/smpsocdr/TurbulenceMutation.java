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

import java.util.HashMap;
import java.util.Random;

import jmetal.operators.mutation.Mutation;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;



/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
public class TurbulenceMutation extends Mutation {

	private double percMutation;
	private double numMaxCycles;
	

	public TurbulenceMutation(HashMap<String, Object> parameters) {		
		super(parameters);
		
		if (parameters.get("percMutation") != null)
			percMutation = (Double) parameters.get("percMutation") ;
		
		if (parameters.get("maxIterations") != null)
			numMaxCycles = (Integer) parameters.get("maxIterations") ; 
		
	}
	
	public Object execute(Object object) throws JMException {
		
		Solution solution = (Solution) object;
		Integer currentIteration_ =  null;
		
		if (getParameter("currentIteration") != null)
	  		currentIteration_ = (Integer) getParameter("currentIteration") ; 
		
		XReal xReal = new XReal(solution);
		
		Random random = new Random();
		double mutRange = 0.0;
		double ub = 0.0,lb=0.0;

		int numDim = solution.numberOfVariables();
		int choosedDim = 0;
		
		int currIteration = currentIteration_.intValue();
		
		double value = Math.pow(1.0-(((double)currIteration)/((double)numMaxCycles)), (5.0/percMutation));

		double temp = 0.0;

		if(flip(value)){
			choosedDim = random.nextInt(numDim);
			mutRange = (xReal.getUpperBound(choosedDim)-xReal.getLowerBound(choosedDim)) * value;
			ub =  xReal.getValue(choosedDim) + mutRange;
			lb =  xReal.getValue(choosedDim) - mutRange;
			ub = (ub > xReal.getUpperBound(choosedDim)) ? xReal.getUpperBound(choosedDim): ub; 
			lb = (lb < xReal.getLowerBound(choosedDim)) ? xReal.getLowerBound(choosedDim) :lb;
			temp =lb + ( ub  - lb ) * random.nextDouble();
			xReal.setValue(choosedDim, temp);							
		}
		return solution;


	}
	
	private boolean flip(double value){
		if(Math.random() <= value){
			return true;
		}
		return false;
	}

	
	
}

