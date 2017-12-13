/*
Federal University of Pernambuco - UFPE
Center of Informatics (Cin)

University of Pernambuco - UPE
Engenharia da Computa��o - Ecomp

This code was created in order to study the scalability
of the Multiobjective Evolutionary Algorithms in
problems with many conflicting objectives


*/

package hidra.experiments;

import hidra.core.util.Util;
import hidra.experiments.settings.HIDRA_CEGA_Settings;
import hidra.experiments.settings.HIDRA_CSSMOPSO_Settings;
import hidra.experiments.settings.HIDRA_MDFA_Settings;
import hidra.experiments.settings.HIDRA_MDNPSO_Settings;
import hidra.experiments.settings.HIDRA_MOPSOCDLS_Settings;
import hidra.experiments.settings.HIDRA_MOPSOCDRS_Settings;
import hidra.experiments.settings.HIDRA_MOPSOCDR_Settings;
import hidra.experiments.settings.HIDRA_MOPSOGDR_Settings;
import hidra.experiments.settings.HIDRA_MSOPS_Settings;
import hidra.experiments.settings.HIDRA_NSGAII_Settings;
import hidra.experiments.settings.HIDRA_SMPSOCDRGDR_Settings;
import hidra.experiments.settings.HIDRA_SMPSOCDR_Settings;
import hidra.experiments.settings.HIDRA_SMPSOGDR2_Settings;
import hidra.experiments.settings.HIDRA_SMPSOGDR3_Settings;
import hidra.experiments.settings.HIDRA_SMPSOGDR_Settings;
import hidra.experiments.settings.HIDRA_SMPSOMDFA_Settings;
import hidra.experiments.settings.HIDRA_SMPSOMDFAv2_Settings;
import hidra.experiments.settings.HIDRA_SMPSOMDFAv3_Settings;
import hidra.experiments.settings.HIDRA_SMPSOMS_Settings;
import hidra.experiments.settings.HIDRA_SMPSO_Settings;
import hidra.experiments.settings.HIDRA_SPEA2GD_Settings;
import hidra.experiments.settings.HIDRA_SPEA2_Settings;
import hidra.experiments.util.HIDRAExperiment;
import hidra.experiments.util.HIDRASettings;
import jmetal.core.Algorithm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.util.JMException;

/**
 * @author Elliackin Figueiredo
 * @email  emnf@cin.ufpe.br
 */
/**
 * Example of experiment. In particular four algorithms are compared when solving
 * four constrained problems.
 */


public class ManyObjectivesStudy extends HIDRAExperiment {

	  /**
	   * Configures the algorithms in each independent run
	   * @param problemName The problem to solve
	   * @param problemIndex
	   * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	   */
	  public void algorithmSettings(String problemName, 
	  		                          int problemIndex, 
	  		                          Algorithm[] algorithm) throws ClassNotFoundException {
	    try {
	      int numberOfAlgorithms = algorithmNameList_.length;

	      HashMap[] parameters = new HashMap[numberOfAlgorithms];

	      for (int i = 0; i < numberOfAlgorithms; i++) {
	        parameters[i] = new HashMap();
	      } // for

	      
	      
	      
	      if (!paretoFrontFile_[problemIndex].equals("")) {
	       
		       for (int i = 0; i < numberOfAlgorithms; i++){
		          parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
		          parameters[i].put("pathRefHypervolume_", pathRefHypervolume_);
		          parameters[i].put("patternProblem_", patternProblem_[problemIndex]);
		       } // if
	      
	      }

	  	//protected String pathRefHypervolume_;
		//protected String patternProblem_;
	      
	      
	      	
	      	for(int idxAlg=0; idxAlg< algorithm.length  ; idxAlg++){
	      		
	      		if( algorithmNameList_[idxAlg].equals("NSGAII")  ){
	      			
	      			algorithm[idxAlg] = new HIDRA_NSGAII_Settings(problemName).configure(parameters[idxAlg]);
	      				      			 
	      		}else if(algorithmNameList_[idxAlg].equals("SPEA2")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SPEA2_Settings(problemName).configure(parameters[idxAlg]);
	      			
	      		}else if(algorithmNameList_[idxAlg].equals("CSSMOPSO")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_CSSMOPSO_Settings(problemName).configure(parameters[idxAlg]);
	      			
	      		}else if(algorithmNameList_[idxAlg].equals("MOPSOCDR")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_MOPSOCDR_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("MOPSOCDRS")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_MOPSOCDRS_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("MOPSOCDLS")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_MOPSOCDLS_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SMPSO")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SMPSO_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("OMOPSO")){	      			
	      			
	      			//algorithm[idxAlg] = new HIDRA_OMOPSO_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("M_DNPSO")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_MDNPSO_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("MSOPS")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_MSOPS_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("MDFA")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_MDFA_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("CEGA")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_CEGA_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SMPSOGDR")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SMPSOGDR_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("MOPSOGDR")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_MOPSOGDR_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SMPSOCDR")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SMPSOCDR_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SMPSOGDR2")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SMPSOGDR2_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SMPSOMDFA")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SMPSOMDFA_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SMPSOMDFAv2")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SMPSOMDFAv2_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SMPSOMDFAv3")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SMPSOMDFAv3_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SMPSOCDRGDR")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SMPSOCDRGDR_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SMPSOGDR3")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SMPSOGDR3_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SPEA2GD")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SPEA2GD_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}else if(algorithmNameList_[idxAlg].equals("SMPSOMS")){	      			
	      			
	      			algorithm[idxAlg] = new HIDRA_SMPSOMS_Settings(problemName).configure(parameters[idxAlg]);
	      		
	      		}
	      		
	      		
	      		
	      		
	      	}
	      
	      
	      	
	        //algorithm[1] = new HIDRA_SPEA2_Settings(problemName).configure(parameters[1]);
	        //algorithm[2] = new HIDRA_MOCell_Settings(problemName).configure(parameters[2]);
	        //algorithm[3] = new HIDRA_SMPSO_Settings(problemName).configure(parameters[3]);
	        //algorithm[4] = new HIDRA_GDE3_Settings(problemName).configure(parameters[4]);
	        
	      } catch (IllegalArgumentException ex) {
	      Logger.getLogger(ManyObjectivesStudy.class.getName()).log(Level.SEVERE, null, ex);
	      }
	      catch (IllegalAccessException ex) {
	          Logger.getLogger(ManyObjectivesStudy.class.getName()).log(Level.SEVERE, null, ex);	        
	      } catch  (JMException ex) {
	      Logger.getLogger(ManyObjectivesStudy.class.getName()).log(Level.SEVERE, null, ex);
	      }
	  } // algorithmSettings

	  /**
	   * Main method
	   * @param args
	   * @throws JMException
	   * @throws IOException
	   */
	  public static void main(String[] args) throws JMException, IOException {
	    
		   
		  
			HashMap<String,Object> out = Util.readFile("resource/conf.txt");		  
			List<Integer> num_objs = (List<Integer>) out.get("num_objs");
			GlobalSettings.numRuns = (Integer)  out.get("numRuns");
		      
			
			  
			for(int idxObj = 0; idxObj < num_objs.size() ; idxObj++){  
			  
			    ManyObjectivesStudy exp = new ManyObjectivesStudy();
		
			    
			    
			    exp.algorithmNameList_ = (String[]) out.get("algorithms");
			    exp.problemList_       = (String[]) out.get("problems");
				   
				 
			    int numObj = num_objs.get(idxObj);
			    
			 
			    
			    String[] paretos = new String[exp.problemList_.length];
				
				for(int i=0; i < paretos.length  ;i++){
					paretos[i] = String.format("%s.%dD.pf", exp.problemList_[i],numObj);
				}
			    
			    
			    exp.paretoFrontFile_       = paretos;   		  
			    Paramenters.NOBJ = num_objs.get(idxObj);
			    exp.experimentName_ = String.format("Many_Objective_Study-%dD", numObj); 
				exp.patternProblem_ = exp.paretoFrontFile_.clone();
			    
		
			    int numberOfAlgorithms = exp.algorithmNameList_.length;
		
			    exp.experimentBaseDirectory_ =  exp.experimentName_;
			    
			    exp.paretoFrontDirectory_ = String.format("paretos/dtlz-%dd",numObj);
			    	   
			    
			    exp.pathRefHypervolume_ = "paretos/referenceHypervolume.txt" ;
			    
			    exp.algorithmSettings_ = new HIDRASettings[numberOfAlgorithms];
		
			    exp.independentRuns_ = GlobalSettings.numRuns;
		
			    // Run the experiments
			    
			    exp.runExperiment(1) ;
	
			}
	    
	  } 
	  
	  
	  
	  
	  
	  
	  
	  public void settingFor5Objectives(){
		  
		 // this.experimentName_ = "ManyObjectivesStudyBatch1-5D";
		    		   
		  this.paretoFrontFile_ = new String[]{	                                   
		                                    "DTLZ1.5D.pf","DTLZ2.5D.pf","DTLZ3.5D.pf","DTLZ4.5D.pf","DTLZ5.5D.pf",
		                                    "DTLZ6.5D.pf","DTLZ7.5D.pf"
		                                   };

		   		  
	  }
	  

	  
	  public void settingFor3Objectives(){
		  
		
		    		   
		  this.paretoFrontFile_ = new String[]{	                                   
		                                    "DTLZ1.3D.pf","DTLZ2.3D.pf","DTLZ3.3D.pf","DTLZ4.3D.pf","DTLZ5.3D.pf",
		                                    "DTLZ6.3D.pf", "DTLZ7.3D.pf"
		                                   };

		   		  
	  }

	  
	  
	  public void settingFor4Objectives(){
		  
			
		   
		  this.paretoFrontFile_ = new String[]{	                                   
		                                    "DTLZ1.4D.pf","DTLZ2.4D.pf","DTLZ3.4D.pf","DTLZ4.4D.pf","DTLZ5.4D.pf",
		                                    "DTLZ6.4D.pf", "DTLZ7.4D.pf"
		                                   };

		   		  
	  }

	  
	  
	  public void settingFor2Objectives(){
		  
			
		   
		  this.paretoFrontFile_ = new String[]{	                                   
		                                    "DTLZ1.2D.pf","DTLZ2.2D.pf","DTLZ3.2D.pf","DTLZ4.2D.pf","DTLZ5.2D.pf",
		                                    "DTLZ6.2D.pf", "DTLZ7.2D.pf"
		                                   };

		   		  
	  }
	  
	  
	  
	
	  
	  
	  
	  
	} // ManyObjectivesStudyBatch1


