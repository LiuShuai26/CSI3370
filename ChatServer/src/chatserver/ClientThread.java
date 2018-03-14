/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import Packet.Packet;
import Packet.Packet.pack_type;

/**
 *
 * @author Michael Muller
 */
public class ClientThread extends JFrame implements Runnable {

    private boolean game_run = false;
    private Socket Cli_socket;
    private int position;
    protected ObjectInputStream from_client;
    private ObjectOutputStream to_client;
    // private DatagramPacket rec_pack;
    protected InetAddress client_ip;
    private String MACAddress;
    private String Username;
    private Thread client_thread;
    private ChatServer chatServer;

    ClientThread(Socket socket, String user_nm, int index, String MAC, ChatServer serv) throws IOException { // populated
        Cli_socket = socket;
        chatServer = serv;
        Username = user_nm;
        MACAddress = MAC;
        client_ip = socket.getInetAddress();
        to_client = new ObjectOutputStream(socket.getOutputStream());
        from_client = new ObjectInputStream(socket.getInputStream());
        client_thread = new Thread(this);
        client_thread.start();
    }

    public Socket get_socket() {
        return Cli_socket;
    }
    
    public String getMAC(){
        return MACAddress;
    }
    public ObjectOutputStream getOutputStream() {
        return to_client;
    }

    public void setOutputStream(ObjectOutputStream out) {
        to_client = out;
    }

    public InetAddress get_ip() {
        return client_ip;
    }

    public String get_usernm() {
        return Username;
    }

    public int get_position() {
        return position;
    }

    public void set_place(int index) {
        position = index;
    }

    public void set_usernm(String usernm) {
        Username = usernm;
    }

    @Override
    public void run() {
        try { // Gets messages from the clients
            Packet inPacket;
            inPacket = (Packet) from_client.readObject(); // initial username
            chatServer.check_nm(this, inPacket.getPayload());
            chatServer.getGui().addClient(this);
            chatServer.echo_chat(this, inPacket);
            while (true) { // handles the constant chat until they disconnect
                try {
                    inPacket = (Packet) from_client.readObject();
                    chatServer.echo_chat(this, inPacket);
                } catch (EOFException ef) {

                } catch (Exception e) {
                    System.out.println(e.toString() + " incomming");
                    chatServer.echo_chat(this, chatServer.constructPacket("", pack_type.disconnected));
                    chatServer.getGui().removeClient(this);
                    this.dispose();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString() + " get name");
        }
    }

}
