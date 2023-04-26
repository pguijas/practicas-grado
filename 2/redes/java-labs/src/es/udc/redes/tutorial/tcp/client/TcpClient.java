package es.udc.redes.tutorial.tcp.client;

import java.net.*;
import java.io.*;

/**
 * Implements an echo client using TCP
 */
public class TcpClient {

    public static void main(String argv[]) {
        if (argv.length != 3) {
            System.err.println("Format: TcpClient <server_address> <port_number> <message>");
            System.exit(-1);
        }
        Socket socket = null;
        try {
            // Obtains the server IP address
            InetAddress serverAddress = InetAddress.getByName(argv[0]);
            // Obtains the server port
            int serverPort = Integer.parseInt(argv[1]);
            // Obtains the message
            String message = argv[2];
            // Creates the socket and establishes connection with the server
            socket = new Socket(serverAddress, serverPort);
            // Set a max. Timeout of 300 secs
            socket.setSoTimeout(300000);
            System.out.println("CLIENT: Connection established with "
                    + serverAddress.toString() + " port " + serverPort);
            // Set the input channel
            BufferedReader sInput = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            // Set the output channel
            PrintWriter sOutput = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("CLIENT: Sending " + message);
            // Send message to the server
            sOutput.println(message);
            // Receive server response
            String recibido = sInput.readLine();
            System.out.println("CLIENT: Received " + recibido);
            // Close streams and release connection
            sOutput.close();
            sInput.close();
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}