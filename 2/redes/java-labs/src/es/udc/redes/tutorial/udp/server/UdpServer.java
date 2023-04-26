package es.udc.redes.tutorial.udp.server;

import java.net.*;

/**
 * Implements an UDP Echo Server.
 * 
 * Pretuntas:
 *      1º:Se lanza una excecpción, no puede haber dos servicios corriendo en el mismo puerto
 *      2º:No pasa nada raro. No
 *      3º:Nada, se sobreescribe
 */
public class UdpServer {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: UdpServer <port_number>");
            System.exit(-1);
        }
        DatagramSocket ServerSocket = null;
        DatagramPacket cPacket, sPacket = null;
        byte array[] = new byte[1024];
        try {
            // Create a server socket
            ServerSocket = new DatagramSocket(Integer.parseInt(argv[0]));
            // Set max. timeout to 300 secs
            ServerSocket.setSoTimeout(300000);
            while (true) {
                // Prepare datagram for reception
                cPacket = new DatagramPacket(array, array.length);
                // Receive the message
                ServerSocket.receive(cPacket);
                System.out.println("CLIENT: Received " + new String(cPacket.getData(), 0, cPacket.getLength())
                    + " from " + cPacket.getAddress().toString() + ":" + cPacket.getPort());
                // Prepare datagram to send response
                sPacket = new DatagramPacket(array, array.length, cPacket.getAddress(), cPacket.getPort());
                // Send response
                ServerSocket.send(sPacket);
                System.out.println("CLIENT: Sending " + new String(sPacket.getData()) + " to "
                    + sPacket.getAddress().toString() + ":" + sPacket.getPort());
            }
        } catch (SocketTimeoutException e) {
            System.err.println("No requests received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (ServerSocket!=null) 
                ServerSocket.close();
        }
    }
}
