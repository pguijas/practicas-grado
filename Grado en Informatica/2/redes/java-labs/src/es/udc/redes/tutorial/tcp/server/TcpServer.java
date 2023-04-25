package es.udc.redes.tutorial.tcp.server;

import java.net.*;
import java.io.*;

/**
 * Monothread TCP echo server.
 */
public class TcpServer {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: TcpServer <port>");
            System.exit(-1);
        }
        ServerSocket server = null;
        Socket csocket = null;
        BufferedReader in;
        PrintWriter out;
       
        try {
            // Create a server socket
            server = new ServerSocket(Integer.parseInt(argv[0]));
            // Set a timeout of 300 secs
            server.setSoTimeout(300000);
            while (true) {
                // Wait for connections
                csocket = server.accept();
                System.out.println("SERVER: Connection established with " + csocket.getInetAddress().toString() + " port " + csocket.getPort());
                // Set the input channel
                in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
                // Set the output channel
                out = new PrintWriter(csocket.getOutputStream(), true);
                // Receive the client message           
                String recibido = in.readLine();                
                System.out.println("SERVER: Received " + recibido);
                // Send response to the client
                System.out.println("CLIENT: Sending " + recibido);
                out.println(recibido);
                // Close the streams
                in.close();
                out.close();
                csocket.close();
            }          
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            //Close the socket           
            try {
                if (server!=null) 
                    server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
