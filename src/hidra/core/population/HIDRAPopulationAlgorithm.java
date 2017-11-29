package hidra.core.population;

import hidra.core.util.Util;
import hidra.experiments.GlobalSettings;
import hidra.jmetal.core.Algorithm;
import hidra.jmetal.core.Problem;
import hidra.jmetal.core.SolutionSet;
import hidra.qualityIndicator.QualityIndicator;

import java.util.ArrayList;
import java.util.List;

import jmetal.util.JMException;
import jmetal.util.Ranking;

public abstract class HIDRAPopulationAlgorithm extends Algorithm{
	
	protected int iteration_;
	protected int maxIterations_;	
	protected QualityIndicator qualityIndicator;
	protected double globalTime_ ;	
	
	
	/*private List<Double> timeByIteration;
	private List<Double> hypervolumeByIteration;
	private List<Double> gdByIteration;
	private List<Double> igdByIteration;
	private List<Double> gsByIteration;
	private List<Double> cmByIteration;
	
	*/
	


	protected void initParams(){
		
		this.iteration_ = 0;		
		this.globalTime_ = 0.0;
		this.maxIterations_ = ((Integer) getInputParameter("maxIterations")).intValue();
		
	}
	
	public HIDRAPopulationAlgorithm(Problem problem) {
		super(problem);
		
		/****************************************************
		 * 
		 * @param problem
		 *All parameters must be initialize in initParams!!!!!
		 *
		 *
		 ***************************************************/
		
	}
	
	

	
	
	//convergence and diversity
	double hypervolume;	
	//convergence
	double convergenceMeasureMe;
	double convergenceMeasureAuthor;
	double generationalDistance;
	//double epsilonadd;
	//double ishibuchiMaxSum;
	
	//diversity
	
	double invertedGenerationalDistance;
	double generalizedSpread;
	//double ishibuchiMaxRange;
	double improvedMaximimSpread;
	double diversity;
	double spacing;
	
	double time;	
	double numSolND;
	
	
	
	
	public void printMetrics(String path){
		
		//Util.printData(iteration_,hypervolume, path + "/THV");
		Util.printData(iteration_,time, path + "/TT");
		Util.printData(iteration_,convergenceMeasureMe, path + "/TCM");
		//Util.printData(iteration_,convergenceMeasureAuthor, path + "/TCMA");
		Util.printData(iteration_,invertedGenerationalDistance, path + "/TIGD");
		Util.printData(iteration_,generationalDistance, path + "/TGD");
		Util.printData(iteration_,diversity, path + "/TDM");
		Util.printData(iteration_,spacing, path + "/TS");
		Util.printData(iteration_,generalizedSpread, path + "/TGS");
		/*Util.printData(iteration_,ishibuchiMaxSum, path + "/TIMS");
		Util.printData(iteration_,ishibuchiMaxRange, path + "/TIMR");
		Util.printData(iteration_,epsilonadd, path + "/TEA");*/
		//Util.printData(iteration_,numSolND, path + "/TNSND");
		Util.printData(iteration_,improvedMaximimSpread, path + "/TMS");
	}
	
	
	
	
	public void printMetrics2(String path){
		
		//Util.printData(hypervolume, path + "/HV");
		Util.printData(globalTime_, path + "/T");
		Util.printData(convergenceMeasureMe, path + "/CM");
		Util.printData(convergenceMeasureAuthor, path + "/CMA");
		Util.printData(invertedGenerationalDistance, path + "/IGD");
		Util.printData(generationalDistance, path + "/GD");
		Util.printData(diversity, path + "/DM");
		//Util.printData(spacing, path + "/S");		
		Util.printData(generalizedSpread, path + "/GS");
		/*Util.printData(ishibuchiMaxSum, path + "/IMS");
		Util.printData(ishibuchiMaxRange, path + "/IMR");
		Util.printData(epsilonadd, path + "/EA");*/
		//Util.printData(numSolND, path + "/NSND");
		Util.printData(improvedMaximimSpread, path + "/MS");
	}
	
	
	
	
	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		
		/*hypervolumeByIteration = new ArrayList<Double>();
		timeByIteration = new ArrayList<Double>();
		igdByIteration = new ArrayList<Double>();
		gdByIteration = new ArrayList<Double>();
		gsByIteration = new ArrayList<Double>();
		cmByIteration = new ArrayList<Double>();
		*/
		qualityIndicator = (QualityIndicator) getInputParameter("indicators");
		
		
		String tmpPath = (String) getInputParameter("pathParetoFront");
		String pathDiretoryFinal = (String) getInputParameter("pathDiretoryFinal");
		
		//long time = 0;
		
		
		long t1 = System.currentTimeMillis();
			SolutionSet	solutionSet = initializationAlgorithm();
		long t2 = System.currentTimeMillis();
		
		 
		// timeByIteration.add(((double)time));

		
		if(GlobalSettings.CALCULATE_METRICS){
						
			 time = (t2 - t1);	
			 globalTime_ += time;
			
			 //hypervolume = qualityIndicator.getHypervolume(solutionSet);
			 convergenceMeasureMe = qualityIndicator.getConvergenceMeasure(solutionSet,true);
			 convergenceMeasureAuthor = qualityIndicator.getConvergenceMeasure(solutionSet,false);
			 invertedGenerationalDistance = qualityIndicator.getIGD(solutionSet);
			 generationalDistance  = qualityIndicator.getGD(solutionSet);
			 generalizedSpread  = qualityIndicator.getGeneralizedSpread(solutionSet);
			 
			 spacing = qualityIndicator.getSpacing(solutionSet);
			 diversity = qualityIndicator.getDiversity2(solutionSet);
				
				
			 //ishibuchiMaxSum = qualityIndicator.getIshibuchiMaxSumMetric(solutionSet);
			 //ishibuchiMaxRange = qualityIndicator.getIshibuchiMaxRange(solutionSet);
			 //epsilonadd = qualityIndicator.getEpsilon(solutionSet);
			 //numSolND = solutionSet.size();
			 //improvedMaximimSpread = qualityIndicator.getImprovedMaximumSpread(solutionSet);
			 
			//System.out.println(iteration_ + "/"+hypervolume);
			
			 printMetrics(tmpPath);
			 
		/*	hypervolumeByIteration.add(hypervolume);
			cmByIteration.add(convergenceMeasureMe);
			gdByIteration.add(generationalDistance);
			igdByIteration.add(invertedGenerationalDistance);*/
			
		}
		
		
		
		if(GlobalSettings.PRINT_PARETO){
			
		
				
				
				String pathParetoFront = tmpPath + "/IFUN_" + iteration_;
				String pathParetoSet = tmpPath + "/IVAR_0";
				
				
				solutionSet.printObjectivesToFile(pathParetoFront);
				solutionSet.printObjectivesToFile(pathParetoSet);
				
			
			
		}
	
		
		

		while (iteration_ < maxIterations_){


			t1 = System.currentTimeMillis();
			solutionSet = runIteration();			
			t2 = System.currentTimeMillis();


			 
			// timeByIteration.add(((double)time));


			if(GlobalSettings.CALCULATE_METRICS){

				time = (t2 - t1);	
				globalTime_ += time;				
				//hypervolume = qualityIndicator.getHypervolume(solutionSet);
				convergenceMeasureMe = qualityIndicator.getConvergenceMeasure(solutionSet,true);
				convergenceMeasureAuthor = qualityIndicator.getConvergenceMeasure(solutionSet,false);
				invertedGenerationalDistance = qualityIndicator.getIGD(solutionSet);
				generationalDistance  = qualityIndicator.getGD(solutionSet);
				generalizedSpread  = qualityIndicator.getGeneralizedSpread(solutionSet);
				
				spacing = qualityIndicator.getSpacing(solutionSet);
				diversity = qualityIndicator.getDiversity2(solutionSet);
				
				//ishibuchiMaxSum = qualityIndicator.getIshibuchiMaxSumMetric(solutionSet);
				//ishibuchiMaxRange = qualityIndicator.getIshibuchiMaxRange(solutionSet);
				//epsilonadd = qualityIndicator.getEpsilon(solutionSet);
				numSolND = solutionSet.size();
				improvedMaximimSpread = qualityIndicator.getImprovedMaximumSpread(solutionSet);
				
				//System.out.println(iteration_ + "/"+hypervolume);
				//System.out.println(iteration_);

				printMetrics(tmpPath); 

				/*timeByIteration.add(((double)time));
				hypervolumeByIteration.add(hypervolume);
				cmByIteration.add(convergenceMeasureMe);
				gdByIteration.add(generationalDistance);
				igdByIteration.add(invertedGenerationalDistance);*/


			}


			System.out.println(iteration_);
			
			if(GlobalSettings.PRINT_PARETO){

				if(   (iteration_ % GlobalSettings.resolution) == 0    ){

					String pathParetoFront = tmpPath + "/IFUN_" + iteration_ ;				
					String pathParetoSet = tmpPath + "/IVAR_" + iteration_;
					
					//solutionSet = getParetoFront();
					solutionSet.printObjectivesToFile(pathParetoFront);
					solutionSet.printVariablesToFile(pathParetoSet);

				}

			}


		}

		
		if(GlobalSettings.PRINT_PARETO){
				
				
			
				String pathParetoFront = tmpPath + "/IFUN_" + iteration_ ;				
				String pathParetoSet = tmpPath + "/IVAR_" + iteration_;
				
				//solutionSet = getParetoFront();
				solutionSet.printObjectivesToFile(pathParetoFront);
				solutionSet.printVariablesToFile(pathParetoSet);
			
		}
		
		
		
		/*setOutputParameter("HV",hypervolumeByIteration);
		setOutputParameter("CM",cmByIteration);
		setOutputParameter("GD", gdByIteration);
		setOutputParameter("IGD", igdByIteration);
		setOutputParameter("GS", gsByIteration);
		*/
		
		
		//Util.printList(timeByIteration, pathMetric + "/T_" + run);
		
		//Util.printData(globalTime, pathMetric + "/T");
		
		SolutionSet finalSolutionSet = this.getParetoFront();
		
		if(GlobalSettings.CALCULATE_METRICS){			
			
			//hypervolume = qualityIndicator.getHypervolume(finalSolutionSet);
			convergenceMeasureMe = qualityIndicator.getConvergenceMeasure(finalSolutionSet,true);
			convergenceMeasureAuthor = qualityIndicator.getConvergenceMeasure(finalSolutionSet,false);
			invertedGenerationalDistance = qualityIndicator.getIGD(finalSolutionSet);
			generationalDistance  = qualityIndicator.getGD(finalSolutionSet);
			spacing = qualityIndicator.getSpacing(solutionSet);
			diversity = qualityIndicator.getDiversity2(solutionSet);
			
			generalizedSpread  = qualityIndicator.getGeneralizedSpread(solutionSet);
			/*ishibuchiMaxSum = qualityIndicator.getIshibuchiMaxSumMetric(solutionSet);
			ishibuchiMaxRange = qualityIndicator.getIshibuchiMaxRange(solutionSet);
			epsilonadd = qualityIndicator.getEpsilon(solutionSet);*/
			numSolND = solutionSet.size();
			improvedMaximimSpread = qualityIndicator.getImprovedMaximumSpread(solutionSet);		
			printMetrics2(pathDiretoryFinal); 
		}
		
		
		
		/*setOutputParameter("HV",hypervolume);
		setOutputParameter("CM",convergenceMeasureMe);
		setOutputParameter("GD", gdByIteration);
		setOutputParameter("IGD", igdByIteration);
		setOutputParameter("GS", gsByIteration);
		
		*/
		return finalSolutionSet;
		//return 
	}

	

	protected abstract SolutionSet initializationAlgorithm() throws ClassNotFoundException,
	JMException;
	protected abstract SolutionSet runIteration() throws JMException;
	protected abstract SolutionSet getParetoFront();
	
}
