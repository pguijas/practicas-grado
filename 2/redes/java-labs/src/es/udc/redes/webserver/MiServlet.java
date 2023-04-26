package es.udc.redes.webserver;

import java.util.Map;

public class MiServlet implements MiniServlet {
	
	/* Para el correcto funcionamiento es necesario un constructor 
	 * sin parámetros y público */
	public MiServlet(){
		
	}
	
        @Override
	public String doGet (Map<String, String> parameters){
		String nombre = parameters.get("nombre");
		String primerApellido = parameters.get("primerApellido");
		String segundoApellido = parameters.get("segundoApellido");
		String nombreCompleto = nombre + " " + primerApellido + " " + segundoApellido;
		
		return printHeader() + printBody(nombreCompleto) + printEnd();
	}	

	private String printHeader() {
		return "<html><head> <title>Greetings</title> </head> ";
	}

	private String printBody(String nombreCompleto) {
		return "<body> <h1> Hola " + nombreCompleto + "</h1></body>";
	}

	private String printEnd() {
		return "</html>";
	}
}
