package es.udc.fic.ri.mri_indexer.e3.kmeans;

import org.apache.commons.math3.linear.RealVector;

public class Punto {
    private double[] data;
    private String name;

    //Constructor sin nombre para los centroides
    public Punto(double[] data) {
    	this.data = data;
    	this.name = "soy un centroide";
    }
    
    //Constructor para términos
    public Punto(RealVector vector, String name) {
    	this.data = vector.toArray();
    	this.name = name;
    }

    public double get(int dimension) { return this.data[dimension];}
    public int getGrado() { return this.data.length;}
    public String getName() { return this.name;}

    //Lo use para debuguear
    @Override
    public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(data[0]);
		for (int i = 1; i < data.length; i++) {
		    sb.append(", ");
		    sb.append(data[i]);
		}
		return "(" + sb.toString()+") -> " + name;
    }

    public Double distanciaEuclidiana(Punto destino) {
		Double d = 0d;
		for (int i = 0; i < data.length; i++) {
		    d += Math.pow(data[i] - destino.get(i), 2);
		}
		return Math.sqrt(d);
    }

    @Override
    public boolean equals(Object obj) {
	Punto other = (Punto) obj;
	for (int i = 0; i < data.length; i++) {
	    if (data[i] != other.get(i)) {
		return false;
	    }
	}
	return true;
    }
}