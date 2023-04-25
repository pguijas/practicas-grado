package es.udc.redes.tutorial.tcp.server;

import java.net.*;

/**
 * Multithread TCP echo server.
 * 
 * 1º:start ejecuta un hilo, run es lo que ejecuta el hilo
 * 2º:En el hilo, pprque el bucle while se encarga de aceptar conexiones y luego 
 *  crea un hilo la cual la gestiona, quedando libre el bucle para seguir 
 *  aceptando nuevas conexiones
 * 
 */
public class TcpServerMulti {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: TcpServer <port>");
            System.exit(-1);
        }
        ServerSocket server = null;
        Socket socket = null;
        ServerThread thread = null;
        try {
            // Create a server socket
            server = new ServerSocket(Integer.parseInt(argv[0]));
            // Set a timeout of 300 secs 
            server.setSoTimeout(300000);
            while (true) {
                // Wait for connections
                socket = server.accept();
                // Create a ServerThread object, with the new connection as parameter 
                thread = new ServerThread(socket);
                // Initiate thread using the start() method
                thread.start();
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            //Close the socket
            if (server != null) {
                try {
                    server.close();
                } catch (Exception e) {
                    System.err.println("Error al cerrar el servidor");
                }
            }
        }
    }
}
