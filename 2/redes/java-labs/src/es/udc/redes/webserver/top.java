package es.udc.redes.webserver;

import java.util.Map;

/**
 * Top is a class that processes dynamic requests, so it implements MiniServlet.
 * @author pguijas
 */
public class top implements MiniServlet{

    public top() {
    }

    /**
     * It generate the dynamic html code.
     * @param parameters Ma with arguments
     * @return html code for the response
     */
    @Override
    public String doGet(Map<String, String> parameters){
        String nombretop = parameters.get("nombretop");
        String primero = parameters.get("primero");
        String segundo = parameters.get("segundo");
        String tercero = parameters.get("tercero");

        return apertura() + nombretop(nombretop) + puesto(primero) + puesto(segundo) + puesto(tercero + cierre());

    }
    

    private String apertura() {
        return "<!DOCTYPE html><head><meta charset=utf-8><title>¡TOP!</title></head><body>";
    }

    private String nombretop(String nombre) {
        return "<h1>" + nombre +"</h1><ol>";
    }
    
    private String puesto(String nombre) {
        return "<li>" + nombre +"</li>";
    }

    private String cierre() {
        return "</ol></body></html>";
    }

}
