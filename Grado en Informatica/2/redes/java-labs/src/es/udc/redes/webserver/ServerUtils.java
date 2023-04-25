package es.udc.redes.webserver;

import java.util.Map;

public class ServerUtils {

	public static String processDynRequest(String nombreclase, Map<String, String> parameters) throws Exception {

		MiniServlet servlet;
		Class<?> instancia;

		instancia = Class.forName(nombreclase);
		servlet = (MiniServlet) instancia.newInstance();

		return servlet.doGet(parameters);

	}
}
