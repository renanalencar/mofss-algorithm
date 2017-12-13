package hidra.core.util;


import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.experiments.Experiment;
import jmetal.util.Configuration;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

	
	
	
	
	public static double [][] readMatrix(String path) {
	    try {
	      // Open the file
	      FileInputStream fis   = new FileInputStream(path)     ;
	      InputStreamReader isr = new InputStreamReader(fis)    ;
	      BufferedReader br      = new BufferedReader(isr)      ;
	      
	      List<double []> list = new ArrayList<double []>();
	      int numberOfObjectives = 0;
	      String aux = br.readLine();
	      while (aux!= null) {
	        StringTokenizer st = new StringTokenizer(aux);
	        int i = 0;
	        numberOfObjectives = st.countTokens();
	        double [] vector = new double[st.countTokens()];
	        while (st.hasMoreTokens()) {
	          double value = (new Double(st.nextToken())).doubleValue();
	          vector[i] = value;
	          i++;
	        }
	        list.add(vector);
	        aux = br.readLine();
	      }
	            
	      br.close();
	      
	      double [][] front = new double[list.size()][numberOfObjectives];
	      for (int i = 0; i < list.size(); i++) {
	        front[i] = list.get(i);
	      }
	      return front;
	      
	    } catch (Exception e) {
	      System.out.println("InputFacilities crashed reading for file: "+path);
	      e.printStackTrace();
	    }
	    return null;
	  } // readFront
	  
	
	
	

	public static double normEucleadian(Solution solution){			
		double temp = 0;
		double dist = 0;
		double sum = 0.0;				
		
		for(int i=0; i < solution.numberOfObjectives()  ; i++){
			
			temp = Math.pow(  solution.getObjective(i) ,2.0 );
			sum += temp;
		}
		
		dist = Math.sqrt(sum);
		return dist;					
	}
	
	
	public static double euclidianDistance(double[] refPoint, Solution solution){
				
		double dist = 0.0;
		double diffsqd = 0;
		double sum = 0.0;
				
		
		for(int i=0; i < refPoint.length ; i++){
			
			diffsqd = Math.pow(refPoint[i] - solution.getObjective(i),2.0);
			sum += diffsqd;
		}
		
		dist = Math.sqrt(sum);
		return dist;
		
	}
	
	
	public static double getRefHypervolume(String path,String pattern){
		
		Scanner s = new Scanner(path);
		
		while(s.hasNext()){
			
			String line = s.nextLine();
			
			if(line.startsWith(pattern)){
				
				StringTokenizer st = new StringTokenizer(" ");
				st.nextToken();
				double hv = Double.parseDouble(st.nextToken());								
				return hv;
			}
		}

		return Double.NaN;
	}
	
	
	
public static SolutionSet copySolutionSet(SolutionSet solutionSet){
	
	SolutionSet copy = new SolutionSet(solutionSet.size());
	
	for(int i=0; i <  solutionSet.size() ; i++){
		
		copy.add(  new Solution(solutionSet.get(i))  );
		
	}
	
	return copy;
}
	
	
	
	
	
	
public static HashMap<String,Object> readFile(String conf) throws FileNotFoundException{
		
		Scanner read = new Scanner(new File(conf));
		
		
			
		
		int numRuns  = Integer.parseInt(  read.nextLine()     ); 
				
		
		
		String line  = read.nextLine();
		
		StringTokenizer st = new StringTokenizer(line);			
		List<Integer> num_objs = new ArrayList<Integer>();
		
		while(st.hasMoreTokens()){
			num_objs.add(  Integer.parseInt(st.nextToken())   );			
		}
		
		
				
				
		line  = read.nextLine();		
		st = new StringTokenizer(line);			
		List<String> alg = new ArrayList<String>();
		
		while(st.hasMoreTokens()){
			alg.add(st.nextToken());			
		}
		
		
		 line  = read.nextLine();
		
		st = new StringTokenizer(line);			
		List<String> prob = new ArrayList<String>();
		
		while(st.hasMoreTokens()){
			prob.add(st.nextToken());			
		}
												
		
		
		
		String[] algorithms = new String[alg.size()];
		String[] problems = new String[prob.size()];
		
		algorithms = alg.toArray(algorithms);
		problems   = prob.toArray(problems);
		
		
		
		
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		
		map.put("num_objs", num_objs);
		map.put("numRuns", numRuns);
		map.put("algorithms", algorithms);
		map.put("problems", problems);
		
		
		return map;
		
		
	}
	
	
	public static void printData(int iteration, Object data, String path){
		
		FileWriter os = null;
		try {			
			os = new FileWriter(path, true);
			os.write(iteration + " " + data.toString() + "\n");
			os.close();
		} catch (IOException ex) {
			Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				os.close();
			} catch (IOException ex) {
				Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
	}
	
public static void printData(Object data, String path){
		
		FileWriter os = null;
		try {			
			os = new FileWriter(path, true);
			os.write(data.toString() + "\n");
			os.close();
		} catch (IOException ex) {
			Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				os.close();
			} catch (IOException ex) {
				Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
	}
	
	
	
	public static void printList(List<Double> list,String path){
	
		try {
		      /* Open the file */
		      FileOutputStream fos   = new FileOutputStream(path)     ;
		      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
		      BufferedWriter bw      = new BufferedWriter(osw)        ;
		                        
		      for (int i = 0; i < list.size(); i++) {
		        //if (this.vector[i].getFitness()<1.0) {
		        bw.write(list.get(i).toString());
		        bw.newLine();
		        //}
		      }
		      
		      /* Close the file */
		      bw.close();
		    }catch (IOException e) {
		      Configuration.logger_.severe("Error acceding to the file");
		      e.printStackTrace();
		    }
		
	
	}
	
	
	
	
	
	
	public static boolean removeDirectory(File directory) {

		  // System.out.println("removeDirectory " + directory);

		  if (directory == null)
		    return false;
		  if (!directory.exists())
		    return true;
		  if (!directory.isDirectory())
		    return false;

		  String[] list = directory.list();

		  // Some JVMs return null for File.list() when the
		  // directory is empty.
		  if (list != null) {
		    for (int i = 0; i < list.length; i++) {
		      File entry = new File(directory, list[i]);

		      //        System.out.println("\tremoving entry " + entry);

		      if (entry.isDirectory())
		      {
		        if (!removeDirectory(entry))
		          return false;
		      }
		      else
		      {
		        if (!entry.delete())
		          return false;
		      }
		    }
		  }

		  return directory.delete();
		}
}
