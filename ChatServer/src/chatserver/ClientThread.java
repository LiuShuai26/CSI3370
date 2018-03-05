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
    protected ObjectInputStream from_client;
    // private DatagramPacket rec_pack;
    protected InetAddress client_ip;
    private String Username;
    private int place;
    private Thread client_thread;
    private ChatServer chatServer = new ChatServer();

    ClientThread(Socket socket, String user_nm, int index) { // populated
        Cli_socket = socket;
        place = index;
        Username = user_nm;
        client_thread = new Thread(this);
        client_thread.start();
    }

    public Socket get_socket() {
        return Cli_socket;
    }

    public InetAddress get_ip() {
        return client_ip;
    }

    public String get_usernm() {
        return Username;
    }

    public int get_place() {
        return place;
    }

    public void set_place(int index) {
        place = index;
    }

    public void set_usernm(String usernm) {
        Username = usernm;
    }
    public void handleMessages(Packet pack){
        
    }
    @Override
    public void run() {
        try { // Gets messages from the clients
            from_client = new ObjectInputStream(Cli_socket.getInputStream());
            // reads in the username if they join the chat
            
            chatServer.check_nm(this);
            chatServer.echo_chat(this, Username + " has joined the Chat", "j");
            chatServer.update_clients_box('a', this);
            while (true) { // handles the constant chat until they disconnect
                try {
                    c_mess = from_client.readLine();
                } catch (Exception e) {
                    game_serv.echo_chat(this, Username + " has disconnected.", "j");
                    game_serv.update_clients_box('r', this);
                    game_serv.remove_client(place);
                    break;
                }
            }
        } catch (Exception e) {

        }
    }

}
