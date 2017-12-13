package hidra.many.metaheuristics.msops;


import jmetal.core.Problem;
import jmetal.core.Solution;


public class MSOPSSolution extends Solution{

	//private List<Integer> scores;
	
		
	public MSOPSSolution(Problem problem_) throws ClassNotFoundException {
		super(problem_);
		this.scores = null;
	}
	
	

	/*public List<Integer> getScores(){
		return this.scores;
	}
	
	public void setScores(List<Integer> scores){
		 this.scores = scores;
	}*/

	
	public MSOPSSolution(Solution solution) {
		super(solution);
		if(((MSOPSSolution)solution).getScores() != null){
			this.scores = ((MSOPSSolution)solution).getScores().clone();
		}
	}
	
	
	private int[] scores;
	
	public int[] getScores(){
		return this.scores;
	}
	
	public void setScores(int[] scores){
		 this.scores = scores;
	}


	
}
