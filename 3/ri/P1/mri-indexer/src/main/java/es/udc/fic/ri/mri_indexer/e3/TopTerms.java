package es.udc.fic.ri.mri_indexer.e3;

import java.util.ArrayList;
import java.util.Collections;
import es.udc.fic.ri.mri_indexer.e3.MyTerm.TermSimComparator_Inverse;;

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
			if (list.get(max-1).getSimilarity()<p.getSimilarity()) {
				list.remove(max-1);
				list.add(p);
			}
		}
		
		//Finalmente Ordenamos La lista (reposicionar solo un elemento)
		Collections.sort(this.list,new TermSimComparator_Inverse());	
	}
	
	public void print() {
		System.out.printf("%-40s%-10s\n", "Term", "Similarity");
		System.out.println("--------------------------------------------------------------------------------");
		for (MyTerm p : list) {
			System.out.printf("%-40s%-10s\n", p.getTerm(), p.getSimilarity());
		}
	}
	
	public ArrayList<MyTerm> getList(){ return list;}
	
}