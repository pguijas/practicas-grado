package es.udc.fic.ri.mri_indexer.e3;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class BestTerms {
	
	//--------------
	//	Term_b Class
	//--------------
	
	public static class Term_b{
		private final String term;
		private final int tf;
		private final float df;
		private final double tfxidflog10;
		
		public Term_b(String term, int tf, float df, double tfxidflog10) {
			this.term=term;
			this.tf=tf;
			this.df=df;
			this.tfxidflog10=tfxidflog10;
		}

		public String getTerm() {return this.term;}
		public int getTf() {return this.tf;}
		public float getDf() {return this.df;}
		public double getTfxidflog10() {return this.tfxidflog10;}
	}
	
	//Comparators -> son reverse debido a que queremos un orden decreciente
	
	public static class TermTFComparator_Reverse implements Comparator<Term_b> { 
	    @Override
	    public int compare(Term_b t1, Term_b t2) {
	        return t2.getTf() - t1.getTf();
	    }
	}
	
	public static class TermDFComparator_Reverse implements Comparator<Term_b> {
	    @Override
	    public int compare(Term_b t1, Term_b t2) {
	    	if (t2.getDf() < t1.getDf()) return -1;
	    	if (t2.getDf() > t1.getDf()) return 1;
	    	return 0;
	    }
	}
	
	public static class TermTfxidflog10Comparator_Reverse implements Comparator<Term_b> {
	    @Override
	    public int compare(Term_b t1, Term_b t2) {
	    	if (t2.getTfxidflog10() < t1.getTfxidflog10()) return -1;
	    	if (t2.getTfxidflog10() > t1.getTfxidflog10()) return 1;
	    	return 0;
	    }
	}
	
	
	//-------------------------------------------------------------------------------------------------------
	//	Top Terms -> Mantiene únicamente los mejores terms en memoria para mejorar el rendimiento en memoria
	//-------------------------------------------------------------------------------------------------------
	
	public static class TopTerms {
		private ArrayList<Term_b> list;
		private int max;
		private Comparator<Term_b> comparator;
		
		TopTerms (int n, Comparator<Term_b> comparator){
			this.max = n;
			this.list = new ArrayList<Term_b>(); 
			this.comparator=comparator;
		}
		
		public void insert(Term_b p) {
			if (list.size()<max) {		//Añadimos si aun no tiene los max
				list.add(p);
			} else {					//machacamos el menor(último)
				if (this.comparator.compare(list.get(max-1), p)>0) {
					list.remove(max-1);
					list.add(p);
				}
			}
			
			//Finalmente Ordenamos La lista (reposicionar solo un elemento)
			Collections.sort(this.list, this.comparator);	
		}
		
		public void print(PrintWriter pw) {
			pw.printf("%-40s%-10s%-20s%-20s\n", "TERM", "TF", "DF", "Tf x idflog10");
			pw.println("--------------------------------------------------------------------------------------------");
			for (Term_b term : this.list) {
				pw.printf("%-40s%-10s%-20s%-20s\n", term.getTerm(), term.getTf(), term.getDf(), term.getTfxidflog10());
			}
		}
	}
	
	//-------------
	//	MAIN
	//-------------
	public static void main(final String[] args) throws IOException{
	
		String usage = "java es.udc.fic.ri.mri_indexer.e3.BestTerms -index INDEX_PATH -docID NUM_DOC -field FIELD_NAME [-top TOP_N_FILES] [-order tf|df|tfxidf] [-outputfile OUTPUT_FILE_PATH]\n\n"
                + "Este programa devolverá los top n terms de ese campo y documento ordenados por tf, df o tf x idflog10 por pantalla o en archivo.\n"
				+ "Valores por defecto: Salida por pantalla, top 10 docs y ordenados por tf decreciente";
		//Inicializamos Variables Con valores por defecto
		String indexPath = null; 	
		int docID = -1; 				
		String field = null;
		int top = 10;												//default 10
		Comparator<Term_b> order = new TermTFComparator_Reverse(); 	//tf (default), df o tfxidf 
		String outputfilePath = null;
		
		
		// Pilla args
		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-docID".equals(args[i])) {
				docID = Integer.parseInt(args[i + 1]);
				i++;
			} else if ("-field".equals(args[i])) {
				field = args[i + 1];
				i++;
			} else if ("-top".equals(args[i])) {
				top = Integer.parseInt(args[i + 1]);
				i++;
			} else if ("-order".equals(args[i])) {
				if ("tf".equals(args[i + 1])) {
					order = new TermTFComparator_Reverse();
				} else if ("df".equals(args[i + 1])) {
					order = new TermDFComparator_Reverse();
				} else if ("tfxidf".equals(args[i + 1])) {
					order = new TermTfxidflog10Comparator_Reverse();
				} else {
					System.err.println("Invalid order.");
					System.out.println("Usage: " + usage);
					System.exit(1);
				}
				i++;
			}else if ("-outputfile".equals(args[i])) {
				outputfilePath = args[i + 1];
				i++;
			}
		}
		
		if (indexPath==null || docID == -1 || field==null) {
			System.out.println("Usage: " + usage);
			System.exit(1);
		}
		
		//Inicializamos herramientas necesarias 
		Directory dir = null;
		DirectoryReader indexReader = null;
		
		try {
			dir = FSDirectory.open(Paths.get(indexPath));
			indexReader = DirectoryReader.open(dir);
		} catch (CorruptIndexException e1) {
			System.out.println("Error: Indice corrupto: " + e1);
			e1.printStackTrace();
		} catch (IOException e1) {
			System.err.println(indexPath);
			System.out.println("Error al abrir el índice: " + e1);
			e1.printStackTrace();
		}
		
		
		float N = indexReader.numDocs();
		TopTerms topterms = new TopTerms(top, order);
		
		//Cargamos los terms
		//	(Si no está guardado en tiempo de indexado debemos obtener todos los terminos)
		//	final Terms terms = MultiTerms.getTerms(indexReader, field);
		final Terms terms = indexReader.getTermVector(docID, field);	
		
		//Si hay, los recorremos
		if (terms != null) {
			//Iteramos
			final TermsEnum termsEnum = terms.iterator();
			while (termsEnum.next() != null) {
				//Para el término obtenemos postings
				String termString = termsEnum.term().utf8ToString();
				PostingsEnum posting = MultiTerms.getTermPostingsEnum(indexReader, field, new BytesRef(termString));

				//Comprobamos que aparezca en algún documento
				if (posting != null) { 
					int docid;
					//Iteramos postings por documento
					while ((docid = posting.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
						//Hasta que lleguemos al documento deseado
						//	Ahora que obtenemos directamente los términos del documento deseado no haría falta, pero nos curamos en salud :)
						if (docid==docID) {
							//Calculasmo y añadimos a la lista de Términos
							int tf = posting.freq(); // frecuencia del término en el documento
							float df = (float) indexReader.docFreq(new Term(field, termString)) / N; // nº docs en los que aparece el término / nº docs en la colección
							double tfxidflog10 = (double)tf*(Math.log10(1/df));//tf x idflog10
							topterms.insert(new Term_b(termString,tf,df,tfxidflog10));
							break;
						}
						
					}
				}
			}

			//Seleccionamas salida
			PrintWriter pw = new PrintWriter(System.out);
			if (outputfilePath==null) 
				pw = new PrintWriter(System.out);
			else
				pw = new PrintWriter(new File(outputfilePath));
			
			//Printeamos
			topterms.print(pw);
			
			//Indicamos que se generó el fichero.
			if (outputfilePath!=null){
				System.out.println("Done!");    
			}
			//Cerramos
			pw.close();
			
			
		} else System.err.println("Error: No terms");
		
		//Cerramos 
		try {
			indexReader.close();
			dir.close();
		} catch (IOException e) {
			System.out.println("Graceful message: exception " + e);
			e.printStackTrace();
		}
	}
}
