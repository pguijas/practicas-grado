package es.udc.redes.tutorial.udp.client;

import java.net.*;

/**
 * This example implements a Echo client using UDP
 */
public class UdpClient {

    public static void main(String argv[]) {
        if (argv.length != 3) {
            System.err.println("Format: UdpClient <server_address> <port_number> <message>");
            System.exit(-1);
        }
        DatagramSocket sDatagram = null;
        try {
            
            // Create a non connection-oriented socket
            // (use any available port)
            sDatagram = new DatagramSocket();
            // Set timeout to 300 secs
            sDatagram.setSoTimeout(300000);
            // Obtain server IP address from first argument
            InetAddress serverAddress = InetAddress.getByName(argv[0]);
            // Obtain server port from second argument
            int serverPort = Integer.parseInt(argv[1]);
            // Obtain message from third argument
            String message = argv[2];
            // Prepare the datagram
            DatagramPacket dgramSent = new DatagramPacket(message.getBytes(),
                    message.getBytes().length, serverAddress, serverPort);
            // Send the datagram
            sDatagram.send(dgramSent);
            System.out.println("CLIENT: Sending " + new String(dgramSent.getData()) + " to "
                + dgramSent.getAddress().toString() + ":" + dgramSent.getPort());
            // Prepare datagram for data reception
            byte array[] = new byte[1024];
            DatagramPacket dgramRec = new DatagramPacket(array, array.length);
            // Receive the message
            sDatagram.receive(dgramRec);
            System.out.println("CLIENT: Received " + new String(dgramRec.getData(), 0, dgramRec.getLength())
                + " from " + dgramRec.getAddress().toString() + ":" + dgramRec.getPort());
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Close socket to release connection
            if (sDatagram!=null) 
                sDatagram.close();
        }
    }
}
