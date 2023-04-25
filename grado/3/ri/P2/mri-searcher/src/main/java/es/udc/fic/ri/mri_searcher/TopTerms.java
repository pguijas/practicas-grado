package es.udc.fic.ri.mri_searcher;

import java.util.ArrayList;
import java.util.Collections;
import es.udc.fic.ri.mri_searcher.MyTerm.TermComparator_Inverse;;;

public class TopTerms {
	private ArrayList<MyTerm> list;
	private int max;
	
	TopTerms (int n){
		this.max = n;
		this.list = new ArrayList<MyTerm>(); 
	}
	
	public void insert(MyTerm p) {
		if (list.size()<max) {		//Añadimos si aun no tiene los max
			list.add(p);
		} else {					//machacamos el menor(último)
			if (list.get(max-1).getScore()<p.getScore()) {
				list.remove(max-1);
				list.add(p);
			}
		}
		
		//Finalmente Ordenamos La lista (reposicionar solo un elemento)
		Collections.sort(this.list,new TermComparator_Inverse());	
	}
	
	public void print() {
		System.out.printf("%-40s%-30s%-100s\n", "Top " + max + " terms from the RS", "Score", "Trazado del score (∑doc in RS tf(term, doc) x idf (term) x score (doc))");
		System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		for (MyTerm p : list) {
			System.out.printf("%-40s%-30s%-100s\n", p.getTerm(), p.getScore(), p.getTraza());
		}
	}
	
	public void normalize_scores() {
		double total=0;
		for (MyTerm myTerm : list) 
			total=total+myTerm.getScore();
		for (MyTerm myTerm : list) 
			myTerm.setScore(myTerm.getScore()/total);
		
	}
	
	public ArrayList<MyTerm> getList(){ return list;}
	
}