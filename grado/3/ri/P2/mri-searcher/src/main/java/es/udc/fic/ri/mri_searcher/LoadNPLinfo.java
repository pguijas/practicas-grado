package es.udc.fic.ri.mri_searcher;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadNPLinfo {
	
	private List<String> list;
	
	public LoadNPLinfo(String recurso) {
		List<String> lista = new ArrayList<String>();
		try {
			FileReader doc_text_reader = new FileReader(recurso);
			int i;
			char c;
			boolean text=false; //primero que leeremos será un id
			String string = "";
			
			//Leemos caracteres
			while((i=doc_text_reader.read())!=-1) {
				c = (char)i;
				
				if (text) {
					//en cuanto recibamos / indicará que acaba el documento (inmediatamente despues viene un salto de linea)
					if ('/'==c) {
						doc_text_reader.read();
						lista.add(string);
						text=false;
						string="";
					} else 
						string=string+c;
				} else {
					//en cuanto acabe la linea que contiene el id, leemos el texto
					if ('\n'==c) {
						text=true;
					}
				}
			} 
			doc_text_reader.close(); 
		} catch (IOException e1) {
			System.err.println("Error al abrir " + recurso + ".");
		}
		this.list = lista;
	}
	
	public List<String> get() {
		return this.list;
	}
	
	public List<String> get(int int1, int int2) {
		return this.list.subList(int1-1,int2);
	}
	
	public List<List<Integer>> get_int_list_list() {
		List<List<Integer>> int_list_list = new ArrayList<List<Integer>>();
		List<Integer> int_list = null;
		
		for (String element : list) {
			int_list = new ArrayList<Integer>();
			for (String numbers : element.split(" ")) {
				if (!numbers.equals("")) {
					int_list.add(Integer.parseInt(numbers.replace("\n", "")));
				}
			}
			int_list_list.add(int_list);
		}
		return int_list_list;
	}
	
	public List<List<Integer>> get_int_list_list(int int1, int int2) {
		return this.get_int_list_list().subList(int1-1,int2);
	}
	
}