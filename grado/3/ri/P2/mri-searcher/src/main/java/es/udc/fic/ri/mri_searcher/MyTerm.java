package es.udc.fic.ri.mri_searcher;

import java.util.Comparator;

public class MyTerm {
	public static class TermComparator_Inverse implements Comparator<MyTerm> { 
	    @Override
	    public int compare(MyTerm t1, MyTerm t2) {
		   	if (t2.getScore() < t1.getScore()) return -1;
		   	if (t2.getScore() > t1.getScore()) return 1;
		   	return 0;  
	    }
	}
	
	private final String term; 
	private double score;
	private String traza;
		
	MyTerm (String term, double score) {
		this.term=term;
		this.score=score;
		this.traza="";
	}

	public String getTerm() {return this.term;}
	public double getScore() {return this.score;}
	public String getTraza() {return this.traza;}
	public void setScore(double score) {this.score=score;}
	public void sumScore(double score) {this.score+=score;}
	public void setTraza(String traza) {this.traza=traza;}
	public void addTraza(String traza) {this.traza+=traza;}
}