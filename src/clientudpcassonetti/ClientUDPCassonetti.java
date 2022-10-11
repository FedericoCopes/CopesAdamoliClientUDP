/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package clientudpcassonetti;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.nio.*;

/**
 *
 * @author MATTIAADAMOLI
 */
public class ClientUDPCassonetti {

    DatagramSocket socket;
    String IP_address;
    int UDP_port;

    public ClientUDPCassonetti(String host, int port) throws SocketException {
        socket = new DatagramSocket();
        //socket.setSoTimeout(1000);
        IP_address = host;
        UDP_port = port;
    }

    public void close() {
        socket.close();
    }

    public ArrayList open(int scelta, int id) throws UnknownHostException, IOException {
        DatagramPacket datagram;
        ByteBuffer input, input2, output;
        byte[] buffer;
        int result = 0;
        int codice = 0;
        InetAddress address;
        ArrayList valori = new ArrayList();
        output = ByteBuffer.allocate(8);
        output.putInt(scelta);
        output.putInt(id);
        address = InetAddress.getByName(IP_address);
        datagram = new DatagramPacket(output.array(), 8, address, UDP_port);
        socket.send(datagram);
        buffer = new byte[8];
        datagram = new DatagramPacket(buffer, buffer.length);
        socket.receive(datagram);
        if (datagram.getAddress().equals(address) && datagram.getPort() == UDP_port) { //verifica della porta e dell'indirizzo
            input = ByteBuffer.wrap(datagram.getData(), 0, 8);
            result = input.getInt();
            valori.add(result);
            codice = input.getInt();
            valori.add(codice);
        }
        return valori;
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        int uscita = 0;
        ClientUDPCassonetti client;
        client = new ClientUDPCassonetti("127.0.0.1", 12345);
        while (true) {
            try {
                ArrayList answer = new ArrayList();
                int id;
                Scanner input = new Scanner(System.in);
                System.out.print("          MENU\n1) Creare una nuova tessera.\n2) Eliminare una tessera.\n3) Aprire un cassonetto.\n4) Uscire. \nEffettuare una scelta: ");
                int scelta = input.nextInt();
                switch (scelta) {
                    case 1:
                        id = 0;
                        answer = client.open(scelta, id);
                        System.out.println("L'ID della tua tessera è -> " + answer.get(1) + "\n\n");

                        break;
                    case 2:
                        System.out.println("Inserire l'ID della tessera: ");
                        id = input.nextInt();
                        answer = client.open(scelta, id);
                        if ((int) answer.get(0) == 1) {
                            System.out.println("Tessera eliminata con successo\n\n");
                        } else {
                            System.out.println("Tessera non presente nell'archivio\n\n");
                        }
                        break;
                    case 3:
                        System.out.println("Inserie l'ID della tessera: ");
                        id = input.nextInt();
                        answer = client.open(scelta, id);
                        if ((int) answer.get(0) == 1) {
                            System.out.println("Apertura autorizzata\n\n");
                        } else if ((int) answer.get(0) == -2) {
                            System.out.println("Tessera non trovata\n\n");
                        } else {
                            int res = 72 - (int) answer.get(0);
                            System.out.println("Apertura negata, hai già aperto il cassonetto nelle 72 ore precedenti. Mancono " +res+"h all'apertura.\n\n");
                        }
                        break;
                    case 4:
                        System.out.println("Uscita in corso...\n\n");
                        uscita = 1;
                        break;

                }
                if (uscita == 1) {
                    break;
                }
            } catch (SocketException ex) {
            }
        }
        client.close(); //chiusura socket
    }
}
