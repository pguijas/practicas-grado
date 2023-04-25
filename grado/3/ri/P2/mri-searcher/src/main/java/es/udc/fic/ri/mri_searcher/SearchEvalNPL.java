package es.udc.fic.ri.mri_searcher;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchEvalNPL {
	private static Similarity similarity = new ClassicSimilarity();  //por defecto TFxIDF
	private static String indexin = null;

	private static int cut = -1;									 
	private static String metrica = "";				    		 
	private static int top = -1;									

	private static List<String> queriesNPL = null;
	private static List<List<Integer>> relevantNPL = null;
	
	
	private static void validateArguments() {
		if (cut<=0 || top<0)
			throw new IllegalArgumentException("cut o top o son inválidos o no existen.");
		else if (indexin==null || !(Files.exists(Paths.get(indexin))))
			throw new IllegalArgumentException("Path given in -indexin argument is not valid, or does not exist.");
	}
	
	private static void readProperties() {
		Properties prop = new Properties();
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
		// leemos de la carpeta NPL las querys y los juicios de relevancia
		queriesNPL = new LoadNPLinfo(prop.getProperty("docs") + "/query-text").get();
		relevantNPL = new LoadNPLinfo(prop.getProperty("docs") + "/rlv-ass").get_int_list_list();
	}
	
	private static void getParams(String[] args) {
		for(int i=0; i < args.length; i++) {
			if ("-search".equals(args[i])) {
				if ("tfidf".equals(args[i+1])) {			//tfidf
					similarity = new ClassicSimilarity();
					System.err.println("tfidf");
				} else if ("jm".equals(args[i+1])){
					try {
				    	Float lambda = Float.parseFloat(args[i+2]);
				    	similarity = new LMJelinekMercerSimilarity(lambda);
	    			} catch (NumberFormatException e) {
	    				throw new IllegalArgumentException("Value for -jm is not a number.");
	    			}
					i++;
				} else if ("dir".equals(args[i+1])){
					try {
						Float mu = Float.parseFloat(args[i+2]);
				    	similarity = new LMDirichletSimilarity(mu);
	    			} catch (NumberFormatException e) {
	    				throw new IllegalArgumentException("Value for -dir is not a number.");
	    			}
					i++;
				} else {
					System.err.print("modelo de RI para la búsqueda no válido.");
					System.exit(1);
				}
		    	i++;
		    } else if ("-indexin".equals(args[i])) {
				indexin = args[i+1];
			    i++;
		    } else if ("-cut".equals(args[i])) {
		    	cut = Integer.parseInt(args[i+1]);
		    	i++;
		    } else if ("-metrica".equals(args[i])) {
		    	if (!Arrays.asList("P", "R", "MAP").contains(args[i+1]))
		    		throw new IllegalArgumentException("Bad value for -metrica argument.");
		    	metrica = args[i+1];
		    	i++;
		    }  else if ("-top".equals(args[i])) {
		    	top = Integer.parseInt(args[i+1]);
		    	i++;
		    } else if ("-queries".equals(args[i])) {
    			try {
			    	if (!"all".equals(args[i+1])) {
			    		if (!args[i+1].contains("-")) {
			    			Integer aux = Integer.parseInt(args[i+1]);
			    			queriesNPL = queriesNPL.subList(aux-1,aux);
			    			relevantNPL = relevantNPL.subList(aux-1,aux);
			    		} else {
			    			Integer aux1 = Integer.parseInt(args[i+1].split("-")[0]);
			    			Integer aux2 = Integer.parseInt(args[i+1].split("-")[1]);
			    			queriesNPL = queriesNPL.subList(aux1-1,aux2);
			    			relevantNPL = relevantNPL.subList(aux1-1,aux2);
			    		}
			    	}
    			} catch (NumberFormatException e) {
    				throw new IllegalArgumentException("Bad value for -queries argument.");
    			}
		    	i++;
		    }
		}
	}
	
	private static void computeMetrics(IndexReader reader, IndexSearcher searcher, QueryParser parser) {
		Query query = null;
		double meanP = 0.0;
		double meanRecall = 0.0;
		double MAP = 0.0;
		int count = 0;
		int numDocs = Math.max(top, cut);
		
		for (int index = 0; index < queriesNPL.size(); index++) {
			String query_str = queriesNPL.get(index).toLowerCase();

			System.out.println("---------------------------------------------------------------------------------------------------");
			System.out.println("QUERY -> " + query_str);
			
			try {
				query = parser.parse(query_str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			TopDocs topDocs = null;
			try {
				topDocs = searcher.search(query, numDocs);
			} catch (IOException e1) {
				System.out.println("Graceful message: exception " + e1);
				e1.printStackTrace();
			}
		
			List<Integer> lst = relevantNPL.get(index);
			
			// variable para guardar el número de documentos relevantes para la query
			int numRelevant = 0;
			// variable auxiliar para computar la suma de la precisión en cada documento (AP).
			double aux = 0;
			// Guardamos el num de docs que son relevantes segun nuestro modelo de IR
			long totalHits = topDocs.totalHits.value;
			//Recorremos Resultados
			for (int i = 0; i < Math.min(numDocs, totalHits); i++) {
				try {
					int docID = Integer.parseInt(reader.document(topDocs.scoreDocs[i].doc).get("DocIDNPL"));
					boolean relevant = false;
					if (lst.contains(docID)) {
						relevant = true;
						// computamos las métricas hasta el valor "cut"
						if (i<cut) {
							numRelevant++;
							aux += (double) numRelevant/(i+1);	// cada vez que aparece un doc relevante, sumamos la precisión en los "i" documentos procesados para el AP
						}
					}
					if (i<top) {
						// documento más score
						System.out.println("\t· " + docID + ((relevant) ? "*" : "") +  "\tscore: " + topDocs.scoreDocs[i].score + "\n\t\tFields:");
						// mostramos los campos
						for (final FieldInfo fieldinfo : FieldInfos.getMergedFieldInfos(reader))
							System.out.println("\t\t -"+fieldinfo.name + ": " + reader.document(topDocs.scoreDocs[i].doc).get(fieldinfo.name));
					}
				} catch (CorruptIndexException e) {
					//System.out.println("Error al buscar, indice corrupto.");
					System.exit(1);
				} catch (IOException e) {
					System.out.println("Error en la E/S al buscar.");
					System.exit(1);
				}
			}
			double precision = (double) numRelevant/(Math.min(cut, totalHits));
			double recall = (double) numRelevant/lst.size();
			double ap = (double) aux/lst.size(); // a veces da infinito 
			System.out.println();
			System.out.println("\tPrecision: " + precision + ".");
			System.out.println("\tRecall: " + recall + ".");
			System.out.println("\tAP: " + ap + ".");
			if (numRelevant!=0) {
				count++;
				meanP += precision;
				meanRecall += recall;
				MAP += ap;
			}
		}
		System.out.println();
		System.out.println();
	
		//En caso de que no haya ningún doc relevante sumamos 1 para que no haga un 0/0 y haga un 0/1 el cual sigue siendo 0
		if (count==0) {
			count=1;
		}
		//Finalmente printeamos la métrica adecuada
		switch (metrica) {
			case "P":
				System.out.println("Mean Precision for " + queriesNPL.size() + " queries: " + (double) meanP/count + ".");
				break;
			case "R":
				System.out.println("Mean Recall for " + queriesNPL.size() + " queries: " + (double) meanRecall/count + ".");
				break;
			case "MAP":
				System.out.println("MAP for " + queriesNPL.size() + " queries: " + (double) MAP/count + ".");
				break;
			default:
				System.out.println("Mean Precision for " + queriesNPL.size() + " queries: " + (double) meanP/count + ".");
				System.out.println("Mean Recall for " + queriesNPL.size() + " queries: " + (double) meanRecall/count + ".");
				System.out.println("MAP for " + queriesNPL.size() + " queries: " + (double) MAP/count + ".");
				break;
		}
	}
	
	public static void main(String[] args) throws IOException {
		//Pillamos Parámtros
		readProperties();
		getParams(args);
		validateArguments();
		
		//Inicializamos searcher
		IndexReader reader = null;
		Directory dir = null;
		IndexSearcher searcher = null;
		QueryParser parser;
		
		try {
			dir = FSDirectory.open(Paths.get(indexin));
			reader = DirectoryReader.open(dir);

		} catch (CorruptIndexException e1) {
			System.out.println("Error, índice corrupto.\n " + e1);
			System.exit(0);
		} catch (IOException e1) {
			System.out.println("Error en la e/s.\n " + e1);
			System.exit(0);
		}
		
		searcher = new IndexSearcher(reader);
		searcher.setSimilarity(similarity);
		parser = new QueryParser("Contents", new StandardAnalyzer());
		//Buscamos
		computeMetrics(reader,searcher,parser);	
	}
}
