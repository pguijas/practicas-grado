package es.udc.fic.ri.mri_searcher;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TrainingTestNPL {	
	private static Boolean is_jm = null;  					
	private static String indexin = null;

	private static int cut = -1;									 
	private static String metrica = "P";				    		 //por defecto P
	private static String outfile = null;			

	private static int train_start = -1;
	private static List<String> trainqueriesNPL = null;
	private static List<List<Integer>> trainrelevantNPL = null;
	private static int test_start = -1;
	private static List<String> testqueriesNPL = null;
	private static List<List<Integer>> testrelevantNPL = null;
	
	private static IndexReader reader = null;
	private static IndexSearcher searcher = null; 
	private static QueryParser parser = null;
	
	
	private static void validateArguments() {
		if (cut<=0)
			throw new IllegalArgumentException("cut o es inválidos o no existe.");
		else if (indexin==null || !(Files.exists(Paths.get(indexin))))
			throw new IllegalArgumentException("Path given in -indexin argument is not valid, or does not exist.");
		else if (is_jm==null)
			throw new IllegalArgumentException("Debe especificarse -evaljm o -evaldirs.");
		else if (outfile==null)
			throw new IllegalArgumentException("Debe especificarse -output.");
	}
	
	private static void loadData(String[] args) {
		
		//--------------------------
		//	Obtención de Parámetros
		//--------------------------
		
		//Properties
		Properties prop = new Properties();
		//Opening file...
		try {
			FileReader reader=new FileReader("src/main/resources/config.properties");
			prop.load(reader);
		} catch (IOException e1) {
			System.err.println("Error al abrir el fichero properties.");
			e1.printStackTrace();
		} 
		
		// Si no tiene docspath, exit
		if (prop.getProperty("docs") == null) {
			//Dejar los errores mas bonitos
			System.err.println("Necesarias ruta de documentos para la ejecución");
			System.err.println("");
			System.exit(1);
		} 

		LoadNPLinfo queriesNPL = new LoadNPLinfo(prop.getProperty("docs") + "/query-text");
		LoadNPLinfo relevantNPL = new LoadNPLinfo(prop.getProperty("docs") + "/rlv-ass");
		
		for(int i=0; i < args.length; i++) {
			
			if ("-evaljm".equals(args[i]) || "-evaldir".equals(args[i])) {
				if ("-evaljm".equals(args[i])) 
					is_jm=true;
				else
					is_jm=false;
				//Cargamos listas de entrenamiento y test
				if (args[i+1].split("-").length==2) {
					int int1 = Integer.parseInt(args[i+1].split("-")[0]);
					int int2 = Integer.parseInt(args[i+1].split("-")[1]);
					train_start = int1;
					trainqueriesNPL  = queriesNPL.get(int1, int2);
					trainrelevantNPL = relevantNPL.get_int_list_list(int1, int2);
					if (args[i+2].split("-").length==2){
						int1 = Integer.parseInt(args[i+2].split("-")[0]);
						int2 = Integer.parseInt(args[i+2].split("-")[1]);
						test_start = int1;
						testqueriesNPL  = queriesNPL.get(int1, int2);
						testrelevantNPL = relevantNPL.get_int_list_list(int1, int2);
						i++;
					} else {
						System.err.println("Mal formato. Formato adecuado: -evaljm int1-int2 int3-int4");
						System.err.println("");
						System.exit(1);
					}
					i++;
				} else {
					System.err.println("Mal formato. Formato adecuado: -evaljm int1-int2 int3-int4");
					System.err.println("");
					System.exit(1);
				}
			} else if ("-cut".equals(args[i])) {
		    	cut = Integer.parseInt(args[i+1]);
		    	i++;
			} else if ("-metrica".equals(args[i])) {
		    	if (!Arrays.asList("P", "R", "MAP").contains(args[i+1]))
		    		throw new IllegalArgumentException("Bad value for -metrica argument.");
		    	metrica = args[i+1];
		    	i++;
			} else if ("-indexin".equals(args[i])) {
				indexin = args[i+1];
			    i++;
		    }  else if ("-outfile".equals(args[i])) {
		    	outfile = args[i+1];
		    	i++;
		    } 
		}
		try {
			validateArguments();
		} catch (Exception e) {
			System.out.println("Usage: java es.udc.fic.ri.mri_searcher.TrainingTestNPL "
					+ "-evaljm int1-int2 int3-int4 "
					+ "-cut n "
					+ "-metrica P | R | MAP (por defecto P) "
					+ "-indexin pathname "
					+ "-outfile results"
					+ "\n\n" + e);
			System.exit(0);
		}
		
	}
	
	private static double compute_metrics(List<String> queries, List<List<Integer>> relevants, PrintStream printer, int start) {

		double raw_score = 0.0;
		int count = 0;
		Query query = null;
		
		//Para todas las queries
		for (int index = 0; index < queries.size(); index++) {
			String query_str = queries.get(index).toLowerCase();
			
			try {
				query = parser.parse(query_str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			TopDocs topDocs = null;
			try {
				topDocs = searcher.search(query, cut);
			} catch (IOException e1) {
				System.out.println("Graceful message: exception " + e1);
			}
		
			List<Integer> lst = relevants.get(index);
			
			// Variables para Métricas
			int numRelevant = 0;
			Double p = 0.;
			Double r = 0.;
			Double ap = 0.;

			// Guardamos el num de docs que son relevantes segun nuestro modelo de IR
			long totalHits = topDocs.totalHits.value;
			for (int j = 0; j < Math.min(cut, totalHits); j++) {
				try {
					int docID = Integer.parseInt(reader.document(topDocs.scoreDocs[j].doc).get("DocIDNPL"));
					if (lst.contains(docID)) {
						numRelevant++;
						ap+=(double)numRelevant/(double)(j+1);
					}
					
				} catch (CorruptIndexException e) {
					System.out.println("Error al buscar, indice corrupto.");
					System.exit(0);
				} catch (IOException e) {
					System.out.println("Error en la E/S al buscar.");
					System.exit(0);
				}
			}

			p  = (double)numRelevant/(double)Math.min(cut, totalHits);
			r  = (double)numRelevant/(double)lst.size();
			ap = (double)ap/(double)lst.size();

			/////////
			//Printeo
			/////////
			if (printer!=null) {
				printer.print((start+index) + ",");
				if ("P".equals(metrica)) {
					printer.println(p);
				} else if ("R".equals(metrica)){
					printer.println(r);
				} if ("MAP".equals(metrica)){
					printer.println(ap);
				} 
			}
			
			/////////
			//MEDIAS
			/////////
			if (numRelevant!=0) {
				if ("P".equals(metrica)) {
					raw_score=raw_score+p;
				} else if ("R".equals(metrica)){
					raw_score=raw_score+r;
				} if ("MAP".equals(metrica)){
					raw_score=raw_score+ap;
				} 
				count++;
			}
		}
		//En caso de que count valga 0, le sumaremos uno para evitar el 0/0 
		if (count==0) 
			count++;
		return (double)raw_score/(double)count;
	}
	
	public static void main(String[] args) throws IOException {
		loadData(args);
	
		//Inicializamos searcher
		Directory dir = null;
				
		try {
			dir = FSDirectory.open(Paths.get(indexin));		
			reader = DirectoryReader.open(dir);

		} catch (CorruptIndexException e1) {
			System.out.println("Error, índice corrupoto. \n\n " + e1);
		} catch (IOException e1) {
			System.out.println("Error en entrada salida. \n\n" + e1);
		}
		
		searcher = new IndexSearcher(reader);
		parser = new QueryParser("Contents", new StandardAnalyzer());
		
		/////////
		//Train
		/////////
		
		float incremento;
		int repeticiones;
		int inicio; //PARA JL 0 no es un valor válido
		if (is_jm) {
			incremento= 0.1f;
			repeticiones = 10;
			inicio=1;
		}
		else {
			incremento=500;
			repeticiones=10;
			inicio=0;
		}

		Double best_score = -1.;
		Double actual_score = -1.;
		float best_param = -1;
		
		//Para todos los parámetros a evaluar 
		for (int i = inicio; i <= repeticiones; i++) {			
			System.out.print("Param: " + i*incremento);
			if (is_jm)  
				searcher.setSimilarity(new LMJelinekMercerSimilarity(i*incremento));
			else
				searcher.setSimilarity(new LMDirichletSimilarity(i*incremento));
			
			//Para todas las queries
			actual_score = compute_metrics(trainqueriesNPL, trainrelevantNPL, null, train_start);

			System.out.println("\t -> score = " + actual_score);
			
			if (best_score<actual_score) {
				best_score=actual_score;
				best_param=i*incremento;
			}
		}
		
		System.out.println("\nBest Score = " + best_score +" (for "+best_param +")");
		
		///////
		//Test
		///////
		if (is_jm) 
			searcher.setSimilarity(new LMJelinekMercerSimilarity(best_param));
		else
			searcher.setSimilarity(new LMDirichletSimilarity(best_param));
		
		//Lanzamos queries test
		actual_score = compute_metrics(testqueriesNPL, testrelevantNPL, new PrintStream(outfile), test_start);
		System.out.println("\nTest Score = " + actual_score);
		System.out.println();


	}
}
