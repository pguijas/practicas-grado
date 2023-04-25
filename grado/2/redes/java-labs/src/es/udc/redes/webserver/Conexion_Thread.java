package es.udc.redes.webserver;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Conexion_Thread is in charge of processing and answering a server conection.<br>
 * @author pguijas
 */
public class Conexion_Thread extends Thread {

    private Socket socket;
    private WebServer servidor;

    /**
     * Getter of WebServer (class that has the server values)
     * @return servidor 
     */
    private WebServer getServidor() {
        return servidor;
    }

    /**
     * Constructor
     * @param s socket of the petition
     * @param servidor WebServer 
     */
    public Conexion_Thread(Socket s, WebServer servidor) {
        this.socket = s;
        this.servidor = servidor;
    }

    /**
     * It writes the connections in the log files
     * @param peticion
     * @param ip
     * @param code
     * @param frase
     * @param tamano 
     */
    private void tolog(String peticion, InetAddress ip, int code, String frase, long tamano) {
        PrintWriter log;
        if (code < 400) {
            log = new PrintWriter(getServidor().getAccesosLog(), true);
        } else {
            log = new PrintWriter(getServidor().getErroresLog(), true);
        }
        try {
            log.println("Petición: " + peticion);
            log.println("Desde: " + ip.getHostName());
            log.println("Fecha: " + new Date());
            if (code < 400) {
                log.println("Código: " + code);
                log.println("Bytes transferidos: " + tamano);
            } else {
                log.println("Error: " + code + " " + frase);
            }
            log.println();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * It send the response to the client and write it in the log
     * @param peticion
     * @param cachedate cache date of the client
     * @param out where the response is sent
     */
    private void SendResponse(String peticion, Date cachedate, OutputStream out) {
        String[] part_pet = peticion.split(" ");
        Response respuesta;
        int cuerpo = 1;

        if (part_pet.length == 3) {
            switch (part_pet[0]) {
                case "GET":
                    respuesta = new Response(getServidor(), true, part_pet[1], cachedate, out);
                    break;
                case "HEAD":
                    respuesta = new Response(getServidor(), false, part_pet[1], cachedate, out);
                    break;
                default:
                    //Bad Request
                    respuesta = new Response(out);
            }
            respuesta.Send();
        } else {
            //Bad Request
            respuesta = new Response(out);
            respuesta.Send();
        }
        //Registramos la iteracion
        if (!respuesta.isCuerpo()) {
            cuerpo = 0;
        }
        tolog(peticion, socket.getInetAddress(), respuesta.getCode(), respuesta.getFrase(), respuesta.getTamano() * cuerpo);
    }

    /**
     * It processes a server connection.<br>
     * It overrides the run method of Thread beacuse we need that it runs in a different 
     * thread, so the server is always listening.
     */    
    @Override
    public void run() {
        BufferedReader in = null;
        OutputStream out = null;
        String cabecera, peticion;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", new Locale("english"));
        Date cachedate = null;
        int code;

        System.out.println();
        try {
            socket.setSoTimeout(300000);
            // Set the input channel
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Set the output channel
            out = socket.getOutputStream();
            // Recibimos peticion del cliente   
            peticion = in.readLine();
            if (peticion != null) {
                // Recibimos las cabeceras
                System.out.println("" + peticion);
                do {
                    cabecera = in.readLine();
                    System.out.println("" + cabecera);
                    if (cabecera.split(": ").length == 2) {
                        if (cabecera.split(": ")[0].equals("If-Modified-Since")) {
                            try {
                                cachedate = sdf.parse(cabecera.split(": ")[1]);
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                        }
                    }

                } while (!cabecera.isEmpty() && cabecera != null);
                SendResponse(peticion, cachedate, out);
            }

        } catch (SocketTimeoutException e) {
            System.err.println("Nothing in 300 secs");
        } catch (Exception e) {
            System.err.println("Error controlado en thread: " + e.getMessage());
        } finally {
            // Close the socket and streams
            try {
                //Close the Streams
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }

                //Close the SOcket
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
