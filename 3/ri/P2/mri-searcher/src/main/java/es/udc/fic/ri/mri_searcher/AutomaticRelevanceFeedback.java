package es.udc.fic.ri.mri_searcher;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
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
import org.apache.lucene.util.BytesRef;

public class AutomaticRelevanceFeedback {
	private static Similarity similarity = new ClassicSimilarity(); //por defecto
	private static String indexin = null;
	private static int rs = 10;
	private static int exp = 10;
	private static int cut = 100;									 
	private static String metrica = "P";		//por defecto
	private static int top = 10;
	private static Boolean residual = false;	//voluntario
	private static String query = null;		 
	private static List<Integer> relevant_list = null;
	private static double interp = -1;		
	private static Map<Integer,Double> relevantSet = new HashMap<>();
	
	private static void getParams(String[] args) {
		int query_num = -1;
		
		for(int i=0; i < args.length; i++) {
			if ("-retmodel".equals(args[i])) {
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
		    } else if ("-rs".equals(args[i])) {
				rs = Integer.parseInt(args[i+1]);
			    i++;
		    } else if ("-exp".equals(args[i])) {
				exp = Integer.parseInt(args[i+1]);
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
		    } else if ("-query".equals(args[i])) {
		    	query_num = Integer.parseInt(args[i+1]);
			    i++;
		    } else if ("-interp".equals(args[i])) {
		    	interp = Double.parseDouble(args[++i]);
		    } else if ("-residual".equals(args[i])) {
		    	if ("T".equals(args[i+1]))
					residual=true;
				else if ("F".equals(args[i+1])) 
					residual=false;	
				else
					throw new IllegalArgumentException("residual only can be T or F.");
		    	i++;
		    }
		}
		if (interp<=0)
			throw new IllegalArgumentException("Indicar un valor valido para la interpolación.");
		if (query_num<=0)
			throw new IllegalArgumentException("Indicar un número de query válido.");
		else if (indexin==null || !(Files.exists(Paths.get(indexin))))
			throw new IllegalArgumentException("Path given in -indexin argument is not valid, or does not exist.");
		
		Properties prop = new Properties();
		try {
			FileReader r =new FileReader("src/main/resources/config.properties");
			prop.load(r);
		} catch (IOException e1) {
			System.err.println("Error al abrir el fichero properties.");
			e1.printStackTrace();
		}
		
		query 		  = new LoadNPLinfo(prop.getProperty("docs") + "/query-text").get().get(query_num-1);
		relevant_list = new LoadNPLinfo(prop.getProperty("docs") + "/rlv-ass").get_int_list_list().get(query_num-1);

	}
	
	/////////////////////
	///COMPUTE METRICS///
	/////////////////////
	private static void computeMetrics(IndexReader reader, IndexSearcher searcher, QueryParser parser) {
		Query q = null;
		
		System.out.println("QUERY: " + query.replace("\n", "") + "\n");
			
		try {
			q = parser.parse(query.toLowerCase());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		TopDocs topDocs = null;
		int numDocs = Math.max(top, cut);
		try {
			topDocs = searcher.search(q, numDocs);
		} catch (IOException e1) {
			System.out.println("Graceful message: exception " + e1);
			e1.printStackTrace();
		}
		
		//Variables que usamos para las métricas
		int numRelevant = 0;
		Double p = 0.;
		Double r = 0.;
		Double ap = 0.;
			
		//Recorremos Resultados
		long totalHits = topDocs.totalHits.value;
		for (int i = 0; i < Math.min(numDocs, totalHits); i++) {
			try {
				int docID = Integer.parseInt(reader.document(topDocs.scoreDocs[i].doc).get("DocIDNPL"));
				boolean relevant = false;
				if (relevant_list.contains(docID)) {
					relevant = true;
					if (i<cut) {
						numRelevant++;
						ap=+(double)numRelevant/(double)(i+1);
					}
					//Guardamos Relevantes
					if (relevantSet.size() < rs)
						relevantSet.put(docID, (double) topDocs.scoreDocs[i].score);
				}
				//////////////////
				//Printeamos info
				//////////////////
				if (i<top) {
					System.out.println("\t· " + docID + ((relevant) ? "*" : "") +  "\tscore: " + topDocs.scoreDocs[i].score + "\n\t\tFields:");
					// mostramos los campos
					for (final FieldInfo fieldinfo : FieldInfos.getMergedFieldInfos(reader))
						System.out.println("\t\t -"+fieldinfo.name + ": " + reader.document(topDocs.scoreDocs[i].doc).get(fieldinfo.name));
				}		
			} catch (CorruptIndexException e) {
				System.out.println("Error al buscar, indice corrupto.");
				System.exit(1);
			} catch (IOException e) {
				System.out.println("Error en la E/S al buscar.");
				System.exit(1);
			}
		}
		
		p = (double)numRelevant/(double)Math.min(cut, totalHits);
		r = (double)numRelevant/(double)relevant_list.size();
		ap = (double)ap/(double)relevant_list.size();
		System.out.println();
		switch (metrica) {
			case "P":
				System.out.print("\t· Precision: " + p);
				break;
			case "R":
				System.out.print("\t· Recall: " + r);
				break;
			case "MAP":
				System.out.print("\t· AP: " + ap);
				break;
		}
		System.out.println(" (corte=" + Math.min(cut, totalHits) + ").");
	}
	
	// recorremos índice invertido calculando scores de términos en los docs que nos interesen
	private static TopTerms getTopTermsFromRS(IndexReader reader, IndexSearcher searcher, QueryParser parser) throws IOException {
		TopTerms topterms = new TopTerms(exp);
		String field = "Contents";
		int numDocs = reader.numDocs();
		final Terms terms = MultiTerms.getTerms(reader, field);
		if (terms != null) {
			final TermsEnum termsEnum = terms.iterator();
			//Para cada término
			while (termsEnum.next() != null) {
				String termString = termsEnum.term().utf8ToString();
				PostingsEnum posting = MultiTerms.getTermPostingsEnum(reader, field, new BytesRef(termString));
				if (posting != null) { 
					MyTerm term = new MyTerm(termString, 0);
					int docid;
					//Recorremos sus documentos
					while ((docid = posting.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
						//Si el doc es relevante, calculamos su score(para el relevancefeedback)
						if (relevantSet.containsKey(docid)) {
							int tf = posting.freq();
							double df = (double) reader.docFreq(new Term(field, termString))/numDocs;
							double idf = Math.log10(1/df);
							term.sumScore(tf*idf*relevantSet.get(docid));
							term.addTraza(tf+"*"+idf+"*"+relevantSet.get(docid)+"\t+   ");
						}
					}
					if (!term.getTraza().equals("")) {
						term.setTraza(term.getTraza().substring(0, term.getTraza().length()-5));
					}
					if (term.getScore()!=0)
						topterms.insert(term);
				}
			}
		}
		return topterms;
	}
	
	private static void newquery(TopTerms topterm){
		//le añadimos comillas porque en algunos casos había operadores de querys de lucene como OR y cascaba
		String new_query="";
		String[] splitted_og_q = query.split(" ");
		//Aádimos las originales
		for (String string : splitted_og_q) {
			new_query=new_query+"\""+string.replace("\n", "")+"\""+'^'+((double)1/splitted_og_q.length)*interp+" ";
		}
		//Añadimos las expandidas
		topterm.normalize_scores();
		for (MyTerm term : topterm.getList()) {
			new_query=new_query+term.getTerm()+'^'+(term.getScore())*(1-interp)+" ";
		}
		query=new_query;
	}
		

	
	public static void main(String[] args) throws IOException {
		
		getParams(args);
		
		//Inicializamos searcher
		IndexReader reader = null;
		Directory dir = null;
		IndexSearcher searcher = null;
		QueryParser parser;
		
		try {
			dir = FSDirectory.open(Paths.get(indexin));
			reader = DirectoryReader.open(dir);

		} catch (CorruptIndexException e1) {
			System.out.println("Error, índice Corrupto: " + e1);
		} catch (IOException e1) {
			System.out.println("Errorr de entrada salida: " + e1);
		}
		
		searcher = new IndexSearcher(reader);
		searcher.setSimilarity(similarity);
		parser = new QueryParser("Contents", new StandardAnalyzer());		
		
		//////////////
		//1º query
		//////////////
		computeMetrics(reader, searcher, parser); //también nos guarda los relevantes que encuentre en el rs
		System.out.println("\n██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████\n");
		System.out.println("El RS está formado por " + relevantSet.size() + " documentos\n");
		//////////////
		//2º query
		//////////////
		TopTerms topterm = getTopTermsFromRS(reader, searcher, parser);
		topterm.print();
		System.out.println("\n██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████\n");
		newquery(topterm);
		
		//Borramos de la lista de relevantes los que usamos en caso de que se pida
		if (residual) {
			for (int i = 0; i < relevant_list.size(); i++) {
				if (relevantSet.containsKey(relevant_list.get(i))) {
					relevant_list.remove(i);
				}
			}
		}
		
		//lanzamos la 2º q
		computeMetrics(reader, searcher, parser); 
		
	}
	
}
