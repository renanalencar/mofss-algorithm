package hidra.experiment.qualityIndicator.calculator;

import hidra.experiments.Paramenters;
import hidra.jmetal.core.Problem;
import hidra.qualityIndicator.QualityIndicator;
import hidra.qualityIndicator.util.MetricsUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

public class IndicatorCalculator {

	
	
	
	
	public static void main(String[] args) {
		
		
		int numObj = 5;
		
		int numRuns = 31;
		
		int numIter = 300;
		
				
		Paramenters.NOBJ = numObj;
	
		//String[] metrics = {"CMT","CV"};
		String[] metrics = {"CMT"};		
		
		String dirbase = "Many_Objective_Study-"+numObj+"D/data";
		
		File dir = new File(dirbase);
		
		String[] algorithms = dir.list();
		String temp = null;
		
		QualityIndicator indicators;
		//System.out.println("PF file: " + paretoFrontFile_[problemId]);
		
		
		Problem problem_ = null;
		
		for(int alg=0; alg < algorithms.length; alg++){
			
			temp = dirbase + "/" +  algorithms[alg]; 
			dir = new File(temp);
			
			
			String[] problems = dir.list();
			
			for(int prob = 0; prob < problems.length ; prob++){
				
				temp = dirbase + "/" +  algorithms[alg] + "/" + problems[prob]; 
				
				Object [] problemParams = {"Real"};
				try {
					problem_ = (new ProblemFactory()).getProblem(problems[prob], problemParams);
				} catch (JMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}      

				String truePareto = "paretos/dtlz-" +  problem_.getNumberOfObjectives() +"d/" +
						problem_.toString() + "." + problem_.getNumberOfObjectives() + "D.pf";
				
				indicators = new QualityIndicator(problem_, truePareto);
				
				dir = new File(temp);
				
				//String[] files = dir.list();
				
				String base_metrics =  dirbase + "/" +  algorithms[alg] + "/" + problems[prob] + "/metrics"; 
				
				File file_metrics = new File(base_metrics);
				
				if(!file_metrics.exists()){
					file_metrics.mkdir();
				}
				
				
				
				for(int run = 0; run < numRuns ; run++){
					
					String aux = temp + "/run" + run; 
					
					//dir = new File(aux);
					//String[] paretos = dir.list();
					//int n = paretos.length;
					
					//List<Double> snapHyper = new Vector<Double>(numIter+1);
					//List<Double> snapConvergence =  new Vector<Double>(numIter+1);
					
					HashMap<String, List<Double>> storeMetrics = new HashMap<String, List<Double>>();
					
					for (int j = 0; j < metrics.length; j++) {
						storeMetrics.put(metrics[j], new Vector<Double>(numIter+1));
					}
					
					
					for(int iteration = 0; iteration <= numIter ; iteration++){
					
						
						System.out.println(iteration);
						String path = aux + "/" + "IFUN_" + iteration;
						
						MetricsUtil m = new MetricsUtil();
						double[][] resultFront = m.readFront(path);
						
						for (int j = 0; j < metrics.length; j++) {
							
							if (metrics[j].equals("HV")) {
								
								String file_temp_metric = base_metrics+"/" + metrics[j];
								File curr_metrics = new File(file_temp_metric);
								
								if(!curr_metrics.exists()){
									curr_metrics.mkdir();
								}
								
								double hypervolume = indicators.getHypervolume(resultFront);								
								//snapHyper.add(hypervolume);
								storeMetrics.get("HV").add(hypervolume);
								
							}
							
							
							if (metrics[j].equals("CMT")) {
								
								String file_temp_metric = base_metrics+"/"  + metrics[j];
								File curr_metrics = new File(file_temp_metric);
								
								if(!curr_metrics.exists()){
									curr_metrics.mkdir();
								}
								
								double convergence = indicators.getConvergenceMeasure(resultFront,true);								
								//snapConvergence.add(convergence);
								storeMetrics.get("CMT").add(convergence);
								
							}
							
							
							/*if (metrics[j].equals("CV")) {
								
								String file_temp_metric = base_metrics+"/"  + metrics[j];
								File curr_metrics = new File(file_temp_metric);
								
								if(!curr_metrics.exists()){
									curr_metrics.mkdir();
								}
								
								double convergence = indicators.getCoverage(a, b)(resultFront,true);								
								//snapConvergence.add(convergence);
								storeMetrics.get("CMT").add(convergence);
								
							}*/
							
							
						}
						
						
						
					}
					
					
					//Finally store the metrics
					
					for (int j = 0; j < metrics.length; j++) {
						
						List<Double> values = storeMetrics.get(metrics[j]);
						
						try {
						FileWriter os;
						os = new FileWriter(base_metrics+"/"  + metrics[j] + "/" + metrics[j] + "_" + run , false);
						
						for(int v=0; v < values.size(); v++){
							double value = values.get(v);
							os.write("" + value + "\n");
						}
						
						
						os.close();
						
						} catch (IOException ex) {
							//Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
						}
						
					}
					
					
					
				}
				
				
				
				HashMap<String, List<Double>> storeMetrics = new HashMap<String, List<Double>>();
				for (int j = 0; j < metrics.length; j++) {
					storeMetrics.put(metrics[j], new Vector<Double>(numIter+1));
				}
			
				
				for(int run = 0; run < numRuns ; run++){
					
					String aux = temp + "/final"; 
				
					String path = aux + "/" + "FUN." + (run+1);
					
					MetricsUtil m = new MetricsUtil();
					double[][] resultFront = m.readFront(path);
					
					for (int j = 0; j < metrics.length; j++) {
						
						if (metrics[j].equals("HV")) {
							
							String file_temp_metric = base_metrics+"/" + metrics[j];
							File curr_metrics = new File(file_temp_metric);
							
							if(!curr_metrics.exists()){
								curr_metrics.mkdir();
							}
							
							double hypervolume = indicators.getHypervolume(resultFront);								
							//snapHyper.add(hypervolume);
							storeMetrics.get("HV").add(hypervolume);
							
						}// end if
					
						
						if (metrics[j].equals("CMT")) {
							
							String file_temp_metric = base_metrics+"/"  + metrics[j];
							File curr_metrics = new File(file_temp_metric);
							
							if(!curr_metrics.exists()){
								curr_metrics.mkdir();
							}
							
							double convergence = indicators.getConvergenceMeasure(resultFront,true);								
							//snapConvergence.add(convergence);
							storeMetrics.get("CMT").add(convergence);
							
						}
					
					} // end for
					
				
				} // end for
				
				
				//Finally store the metrics
				
				for (int j = 0; j < metrics.length; j++) {
					
					List<Double> values = storeMetrics.get(metrics[j]);
					
					try {
					FileWriter os;
					os = new FileWriter(base_metrics+"/"  + metrics[j] + "/" + metrics[j] , false);
					
					for(int v=0; v < values.size(); v++){
						double value = values.get(v);
						os.write("" + value + "\n");
					}
					
					
					os.close();
					
					} catch (IOException ex) {
						//Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
					}
					
				}
				
				
				
				
			}
			
		}
		
		
	}
	
	
}
