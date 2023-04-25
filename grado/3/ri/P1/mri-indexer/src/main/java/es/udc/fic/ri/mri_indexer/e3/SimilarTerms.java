package es.udc.fic.ri.mri_indexer.e3;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class SimilarTerms {
	
	//-----------------------
	//	GETTING SIMILARITIES
	//-----------------------
	
	static TopTerms getTopSimilarities(IndexReader indexReader, String field, String ref_term, int top, String rep) throws IOException{
		  
		//Primero Cargamos El Vector Que vamos a usar como referencia
		RealVector ref_vector = getVector(indexReader, field, ref_term, rep);
		RealVector vector;
		
		//Inicializamos la estructura de datos que nos mantenga los N mejores
		TopTerms top_list = new TopTerms(top);
		
		//Cargamos todos los terms
		final Terms terms = MultiTerms.getTerms(indexReader, field);
		//Si hay, los recorremos
		if (terms != null) {
			//Iteramos
			final TermsEnum termsEnum = terms.iterator();
			while (termsEnum.next() != null) {
				//Comprobamos que sea un término distinto al de referencia, no tiene sentido compararlo consigo mismo
				String termString = termsEnum.term().utf8ToString();
				if (!termString.equals(ref_term)) {
					//Calculamos la similaridad e insertamos
					vector = getVector(indexReader, field, termString, rep);
					top_list.insert(new MyTerm(termString, getSimilarity(ref_vector,vector), vector));
				} 
			}
		}
		return top_list;
	}
	
	static RealVector getVector(IndexReader indexReader, String field, String term, String rep) throws IOException {
		Term t = new Term(field,term);
		PostingsEnum posting = MultiTerms.getTermPostingsEnum(indexReader, field, new BytesRef(term));
		RealVector vector = new ArrayRealVector(indexReader.numDocs());
		
		//Comprobamos que aparezca en algún documento
		if (posting != null) { 
			int docid;
			//Iteramos postings por documento
			while ((docid = posting.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
				int tf = posting.freq(); 
				
				//Calculamos la añadimos a los vectores de términos las representaciones adecuadas
				if ("tf".equals(rep)) { 		// 	frecuencia del término en el documento
					vector.setEntry(docid, tf);
					
				} else if ("bin".equals(rep)){	// 	binario
					if (tf>0) 
						vector.setEntry(docid, 1);
					else 
						vector.setEntry(docid, 0);
					
				} else if ("tfxidf".equals(rep)){	//	tf x idflog10
					float idf = indexReader.numDocs()/(float)indexReader.docFreq(t); // nº docs en la colección / nº docs en los que aparece el término
					double tfxidf = (double)tf*idf;
					vector.setEntry(docid, tfxidf);
					
				}
			}
		}

		return vector;
	}
	
	static double getSimilarity(RealVector v1, RealVector v2) {
		return (v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm());
	}
	
	//-------------
	//	MAIN
	//-------------
	
	public static void main(String[] args) {
		String usage = "java es.udc.fic.ri.mri_indexer.e3.SimilarTerms -index INDEX_PATH -field FIELD_NAME"
				+ "-term TERM [-top TOP] [-rep REP; only valid values: 'bin', 'tf', 'tfxidf']\n\n"
				+ "This gives the list of n most similar terms according to cosine similarity,"
				+ "the list is sorted from highest to lowest."
				+ "\nDefault values: TF representation; top 10 terms.";
		String indexPath = null; 
		String field = null;
		String term = null;
		int top = 10; 			//por defecto
		String rep="tf";		//"bin" o "tf" o "tfxidf". 
		
		// Pilla args
		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-field".equals(args[i])) {
				field = args[i + 1];
				i++;
			} else if ("-term".equals(args[i])) {
				term = args[i + 1];
				i++;
			} else if ("-top".equals(args[i])) {
				top = Integer.parseInt(args[i + 1]);
				i++;
			} else if ("-rep".equals(args[i])) {
				if ("tf".equals(args[i + 1]) || "tfxidf".equals(args[i + 1]) || "bin".equals(args[i + 1])) {
					rep = args[i + 1];
					i++;
				} else {
					System.err.println("Invalid representation.");
					System.out.println("Usage: " + usage);
					System.exit(1);
				}
			}else {
				System.err.println("Invalid order.");
				System.out.println("Usage: " + usage);
				System.exit(1);
			}
		}
		
		//Control de que todo esté inicializado
		if (indexPath==null || field==null || term==null) {
			System.out.println("Usage: " + usage);
			System.exit(1);
		}
		
		//Inicializamos herramientas necesarias 
		Directory dir = null;
		IndexReader indexReader = null;
		TopTerms top_terms = null;
	    
		//Abrimos y obtenemos similaritudes
		try {
			dir = FSDirectory.open(Paths.get(indexPath));
			indexReader = DirectoryReader.open(dir);
			top_terms = getTopSimilarities(indexReader, field, term, top, rep);
		} catch (CorruptIndexException e1) {
			System.out.println("Índice corrupto: " + e1);
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Something has gone wrong :( " + e1);
			e1.printStackTrace();
		}
		
		//Printeamos
		top_terms.print();

		//Cerramos
		try {
			indexReader.close();
			dir.close();
		} catch (IOException e) {
			System.out.println("Something has gone wrong :( " + e);
			e.printStackTrace();
		}
		
	}
}