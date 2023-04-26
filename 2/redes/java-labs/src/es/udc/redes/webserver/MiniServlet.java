package es.udc.redes.webserver;

import java.util.Map;

public interface MiniServlet {
	
	public String doGet (Map<String, String> parameters) throws Exception;

}
