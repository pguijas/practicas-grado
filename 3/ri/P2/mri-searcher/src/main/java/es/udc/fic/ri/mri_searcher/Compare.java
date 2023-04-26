package es.udc.fic.ri.mri_searcher;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

public class Compare {

	private static double[] x = null;
	private static double[] y = null;
	private static Boolean is_t_test = null; 	//si no es t-test es wilcoxon
	private static double alpha = -1; 			
	
	//Parseamos archivo resultante del  TrainingTestNPL
	private static double[] parsefile(String file) {
		ArrayList<Double> list = new ArrayList<Double>();
		try {
			FileReader doc_text_reader = new FileReader(file);
			int i;
			char c;
			String string = "";
			
			//Leemos caracteres
			while((i=doc_text_reader.read())!=-1) {
				c = (char)i;
				if ('\n'==c) {					
					if (string.split(",").length==2) {
						list.add(Double.parseDouble(string.split(",")[1]));
					} else {
						System.out.println("Documento mal formado");
						System.exit(0);
					}
					//System.out.println(string);
					string="";
				} else {
					string=string+c;
				}
			} 
			doc_text_reader.close(); 
		} catch (IOException e1) {
			System.err.println("Error al abrir " + file + ".");
		}
		//pasamos de Double a double
		double[] a = new double[list.size()];
		for (int i = 0; i < list.size(); i++) 
			a[i]=(double)list.get(i);
		return a;
	}
	
	private static void validateArguments() {
		if (x==null)
			throw new IllegalArgumentException("Debe especificarse results1.");
		else if (y==null)
			throw new IllegalArgumentException("Debe especificarse results2.");
		else if (is_t_test==null || alpha==-1)
			throw new IllegalArgumentException("Debe indicarse el tipo de test de significancia estadística con su correspondiente alpha.");
		
	}
	
	private static void loadData(String[] args) {
		
		//--------------------------
		//	Obtención de Parámetros
		//--------------------------
		
		for(int i=0; i < args.length; i++) {
			
			if ("-results".equals(args[i])) {
				x=parsefile(args[++i]);
				y=parsefile(args[++i]);
			} else if ("-test".equals(args[i])) {
				if ("wilcoxon".equals(args[i+1])) {
					is_t_test=false;
				} else if ("t".equals(args[i+1])) {
					is_t_test=true;
				} else {
					System.out.println("Tipo test no válido");
					System.exit(0);				}
				i++;
				alpha=Double.parseDouble(args[++i]);
			}  
		}
		try {
			validateArguments();
		} catch (Exception e) {
			System.out.println("Usage: java es.udc.fic.ri.mri_searcher.Compare "
					+ "-results results1 results2 "
					+ "-test t|wilcoxon alpha\n\n"
					+ e);
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		//Cargamos Argumentos
		loadData(args);
		//Calculamos p_valor
		double p_value;
		if (is_t_test) 
			p_value = new TTest().pairedTTest(x, y);
		else
			p_value = new WilcoxonSignedRankTest().wilcoxonSignedRankTest(x, y, true);
		
		//Concluimos
		if (p_value<=alpha) {
			System.out.println("Rechazamos Hipótesis Nula (hay uno mejor que otro)");
			System.out.println(" p-valor = " + p_value + " <= " + alpha);
		} else {
			System.out.println("No Rechazamos Hipótesis Nula (no hay uno mejor que otro)");
			System.out.println("p-valor = " + p_value + " >= " + alpha);
		}
			
		//Advertimos de un caso con t-test
		if (is_t_test) {
			System.out.println("\n* si el p-valor es NaN es debido a que los resultados son exactamente iguales");
		}
		
		
	}
}
