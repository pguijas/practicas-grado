package es.udc.fic.ri.mri_indexer.e3;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class StatsField {
	
	static void printStats(String fieldName, IndexSearcher searcher) throws IOException {
		//Getting Stats
		CollectionStatistics collectionStats = searcher.collectionStatistics(fieldName);
	    System.out.println("Statistics for field \"" + fieldName + "\":");

		if (collectionStats!=null) {
		    System.out.println("  MaxDoc:\t\t"+ 		collectionStats.maxDoc());
		    System.out.println("  DocCount:\t\t"+		collectionStats.docCount());
		    System.out.println("  SumDocFreq:\t\t"+ 	collectionStats.sumDocFreq());
		    System.out.println("  SumTotalTermFreq:\t"+	collectionStats.sumTotalTermFreq());
		} else {
			System.out.println("  No stats for the field (maybe not indexed)");
		}
	}
	
	
	public static void main(String[] args) {
		String usage = "java es.udc.fic.ri.mri_indexer.e3.StatsField" + " -index INDEX_PATH [-field FIELD_NAME] \n\n"
				+ "This returns stats for a especific field in a index."
				+ "If the field is not specified, returns the stats for all the fields";
		
		String indexPath = null;
		String fieldName = null;
	
		// Pilla args
		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-field".equals(args[i])) {
				fieldName = args [i + 1];
				i++;				
			}
		}
		
		Directory index=null;
		IndexReader indexReader = null;
		IndexSearcher searcher = null;
		
		if ((indexPath!=null)) {
			//Inicializando Index Reader
			try {
				index = FSDirectory.open(Paths.get(indexPath));
				indexReader = DirectoryReader.open(index);
			} catch (IOException e) {
				System.err.println("No se ha podido crear el indexReader");
			}
			//Inicializamos Searcher
			searcher = new IndexSearcher(indexReader);
			//Comprobamos si se indicó el fieldname
			if (fieldName != null) {
				//Statistics para un campo
				try {
					printStats(fieldName, searcher);
				} catch (IOException e) {
					System.err.println("Fallo al intentar obtener las Statistics");
					e.printStackTrace();
				}
			} else {
				//Statistics para todos los campos
				final FieldInfos fieldinfos = FieldInfos.getMergedFieldInfos(indexReader);
				try {
					for (FieldInfo fieldInfo : fieldinfos) {
						printStats(fieldInfo.name, searcher);
						System.out.println();
					}
				}catch (IOException e) {					
					System.err.println("Fallo al intentar obtener las Statistics");
					e.printStackTrace();
				}
			}
		} else {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}  
		try {
			index.close();
			indexReader.close();
		} catch (IOException e) {
			System.err.println("Error al intentar cerrar el índice");
		}
	}
}
