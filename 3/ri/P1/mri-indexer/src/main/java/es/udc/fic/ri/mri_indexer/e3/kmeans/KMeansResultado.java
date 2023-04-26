/**
* Este código es una adaptación para nuestro uso del codigo original del usuario "xetorthio" de github.
* Enlace al repositorio con el código original:
* https://github.com/xetorthio/kmeans
*/
package es.udc.fic.ri.mri_indexer.e3.kmeans;

import java.util.ArrayList;
import java.util.List;

public class KMeansResultado {
	private List<Cluster> clusters = new ArrayList<Cluster>();
	private Double ofv;

	public KMeansResultado(List<Cluster> clusters, Double ofv) {
		super();
		this.ofv = ofv;
		this.clusters = clusters;
	}

	public List<Cluster> getClusters() { return clusters;}
	public Double getOfv() { return ofv; } 					//Distancia de los puntos a los clusters (lo usé para debuguear)
	
	public void print() {
	    System.out.println("Cluster (" + clusters.size() + ")\t\t\t\tTerm");
		System.out.println("--------------------------------------------------------------------------------");
		int i = 0;
		for (Cluster cluster : this.clusters) {
			i++;
			System.out.println("Cluster " + i);
			for (Punto punto : cluster.getPuntos()) {
				System.out.println("\t\t\t\t\t" + punto.getName());
			}
			System.out.print("\n\n");
		}
	}
}