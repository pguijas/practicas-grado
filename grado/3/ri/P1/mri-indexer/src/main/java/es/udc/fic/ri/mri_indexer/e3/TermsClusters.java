
package es.udc.fic.ri.mri_indexer.e3;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import es.udc.fic.ri.mri_indexer.e3.kmeans.KMeans;
import es.udc.fic.ri.mri_indexer.e3.kmeans.KMeansResultado;
import es.udc.fic.ri.mri_indexer.e3.kmeans.Punto;

/**
 * 
 * Implemetación de kmeans usada: https://github.com/xetorthio/kmeans/
 * 
 * He hecho algunos cambios en la clase que representa un punto en el espacio y en la clase resultado.
 * 
 * 
 * Observación: La clusterización debe hacerse con una gran colección de documentos que le aporten mucha información al sistema.
 * En caso de tener pocos documentos y de distintas índoles, poca información tendrá. También es importante elegir la representación mas adecuada.
 * 
 * Ejemplo: (rep: tf) en una colección en el que aparece mucho un término (50 veces) y el resto aparecen pocas veces, 
 * va a meter los terms que aparecen poco en el mismo cluster y el que aparece mucho dado que la distancia euclidea va a ser muy 
 * dispar entre los elementos de estos clusters. 
 * En casos como este, sería mas idóneo una representación binaria para que en caso de que un único término tenga una TF muy alta, no haga que 
 * las distancias entre términos que no tienen demasiado que ver sea despreciable en comparación con la distancia al término de TF exageradamente alta.
 * 
 * @author pguijas
 *
 */

public class TermsClusters {

	public static void main(String[] args) {
		String usage = "java es.udc.fic.ri.mri_indexer.e3.TermsClusters -index INDEX_PATH -field FIELD_NAME"
				+ "-term TERM [-top TOP] [-rep REP; only valid values: 'bin', 'tf', 'tfxidf'] -k K\n\n"
				+ "This gives the list of n most similar terms according to cosine similarity,"
				+ "the list is sorted from highest to lowest. Then using the k-means algorithm, k clusters are made."
				+ "\nDefault values: TF representation; top 10 terms.";
		String indexPath = null; 
		String field = null;
		String term = null;
		int top = 10;		//por defecto
		String rep="tf"; 	//"bin" o "tf" o "tfxidf".
		int k= -1;			//por defecto
		
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
			}else if ("-k".equals(args[i])) {
				k = Integer.parseInt(args[i + 1]);
				if (k<=0) {
					System.out.println("K must be 1 or higher. Tampoco tiene sentido clasificar en 1 posible cluster, pero se permite :)");
					System.exit(1);
				}
				i++;
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
		
		Directory dir = null;
		IndexReader indexReader = null;
		TopTerms top_terms = null;

		//Abrimos y obtenemos similaritudes
		try {
			dir = FSDirectory.open(Paths.get(indexPath));
			indexReader = DirectoryReader.open(dir);
			top_terms = SimilarTerms.getTopSimilarities(indexReader, field, term, top, rep);
			
		} catch (CorruptIndexException e1) {
			System.out.println("Índice corrupto: " + e1);
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Something has gone wrong :( " + e1);
			e1.printStackTrace();
		}
		
		//Printeamos
		top_terms.print();
		
		//Extraemos los puntos para el Cluster
		ArrayList<Punto> puntos = new ArrayList<Punto>();
		for (MyTerm t : top_terms.getList()) {
			puntos.add(new Punto(t.getVector(), t.getTerm()));
		}
		
		//Ejecutamos kmeans y printeamos los resultados
		KMeans kmeans = new KMeans();
		KMeansResultado resultado = kmeans.calcular(puntos, k);
		System.out.println();
		System.out.println();
		resultado.print();
		System.out.println(" * puede que algún cluster se quede vacío, eso es debido a la implementación de k-means elegida. (Pero si los vectores de términos están bien repartidos en el espacio no debería pasar nada. Si pasa pruebe a cambiar de representación)");
	    
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
