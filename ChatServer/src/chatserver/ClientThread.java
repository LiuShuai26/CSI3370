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
    private String Username;
    private Thread client_thread;
    private ChatServer chatServer = new ChatServer();

    ClientThread(Socket socket, String user_nm, int index) throws IOException { // populated
        Cli_socket = socket;
        Username = user_nm;
        from_client = new ObjectInputStream(socket.getInputStream());
        client_thread = new Thread(this);
        client_thread.start();
        position = index;
        to_client = new ObjectOutputStream(socket.getOutputStream());
    }

    public Socket get_socket() {
        return Cli_socket;
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
            while (true) { // handles the constant chat until they disconnect
                try {
                    inPacket =  (Packet)from_client.readObject();
                    chatServer.echo_chat(this, inPacket);
<<<<<<< HEAD
                } catch (EOFException ef) {

=======
                    from_client.close();
>>>>>>> 6a1eec89c417a930e592876c8e7e8fca27773327
                } catch (Exception e) {
                    System.out.println(e.toString());
                    chatServer.echo_chat(this, new Packet("", pack_type.disconnected));
                    chatServer.removeClient(this);
                    this.dispose();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}