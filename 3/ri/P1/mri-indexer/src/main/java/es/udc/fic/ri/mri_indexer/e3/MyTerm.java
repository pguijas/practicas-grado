package es.udc.fic.ri.mri_indexer.e3;

import java.util.Comparator;

import org.apache.commons.math3.linear.RealVector;

public class MyTerm {
	
	public static class TermSimComparator_Inverse implements Comparator<MyTerm> { 
	    @Override
	    public int compare(MyTerm t1, MyTerm t2) {
		   	if (t2.getSimilarity() < t1.getSimilarity()) return -1;
		   	if (t2.getSimilarity() > t1.getSimilarity()) return 1;
		   	return 0;  
	    }
	}
	
	private final String term; 
	private final double similarity;
	private final RealVector vector;
		
	MyTerm (String term, double similarity, RealVector vector) {
		this.term=term;
		this.similarity=similarity;
		this.vector=vector;
	}

	public String getTerm() {return this.term;}
	public double getSimilarity() {return this.similarity;}
	public RealVector getVector() {return this.vector;}
	
}
