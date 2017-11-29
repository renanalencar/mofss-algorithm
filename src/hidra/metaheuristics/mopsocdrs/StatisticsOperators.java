package hidra.metaheuristics.mopsocdrs;


import java.util.Arrays;
import java.util.Vector;

public class StatisticsOperators {

	public static double soma(Vector<Double> v){
		if(v.size()==0)System.out.println("ERRO: soma de vetor de tamanho zero???");
		double soma=0.0;
		for(int i=0;i<v.size();i++){
			soma+=new Double(v.get(i).toString());
		}
		return soma;
	}

	public static double media(Vector<Double> v){
		if(v.size()==0)System.out.println("ERRO: media de vetor de tamanho zero???");
		return soma(v)/(double) v.size();
	}

	public static double variancia(Vector<Double> v){
		if(v.size()==0)System.out.println("ERRO: variancia de vetor de tamanho zero???");
		double media=media(v);
		double soma_desvio_quadrado=0;
		for(int i=0;i<v.size();i++){
			double desvio=new Double(v.get(i).toString()) - media;
			double desvio_quadrado = desvio * desvio;
			soma_desvio_quadrado+=desvio_quadrado;
		}		
		return soma_desvio_quadrado/(double) v.size();
	}

	public static double desvio_padrao(Vector<Double> v){
		if(v.size()==0)System.out.println("ERRO: desvio_padrao de vetor de tamanho zero???");
		return Math.sqrt(variancia(v));
	}
	
	public static double[] getBoxPlot(Vector<Double> values){
		Double[] array = new Double[values.size()];
		
		values.toArray(array);
		
		//Double[] array = (Double[]) values.toArray();
		
		Arrays.sort(array);
		double[] ret = new double[5];
		
		//menor valor
		ret[0] = array[0];
		
		//maior valor
		ret[4] = array[array.length-1];
		
		
		int index = Math.round((array.length)/4);
		
		//quartil 1
		ret[1] = array[index];
		//quartil 2 (media)
		ret[2] = media(values);
		//quartil 3
		ret[3] = array[3*index];
		
		return ret;
	}
}
