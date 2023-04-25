package es.udc.redes.tutorial.tcp.server;

import java.io.*;
import java.net.*;

/**
 *
 * @author pguijas
 */
public class ServerThread extends Thread{
    private Socket socket;
    
    public ServerThread(Socket s){
        socket=s;
    }
    
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            // Set the input channel
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Set the output channel
            out = new PrintWriter(socket.getOutputStream(), true);
            // Receive the message from the client                
            String recibido = in.readLine();                
            System.out.println("SERVER: Received " + recibido);
            // Sent the echo message to the client               
            System.out.println("SERVER: Sending " + recibido);
            out.println(recibido);
          } catch (SocketTimeoutException e) { 
              System.err.println("Nothing received in 300 secs");
          } catch (Exception e) { 
              System.err.println("Error: " + e.getMessage());
          } finally {
            // Close the socket and streams
            try {
                //Close the Streams
                if (in!=null) 
                    in.close();   
                if (out!=null) 
                    out.close();
        
                //Close the SOcket
                if (socket!=null) 
                    socket.close();
            } catch (Exception e) {    
                System.err.println("Error: " + e.getMessage());
            }
          }
    }
}
