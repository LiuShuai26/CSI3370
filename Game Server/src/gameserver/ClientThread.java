/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import Packet.Packet;
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
    private Thread client_thread, game_thread;
    private GameServer game_serv = new GameServer();

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
    public void handleMessages(){
        
    }
    @Override
    public void run() {
        String c_mess, s_mess;
        try { // Gets messages from the clients
            from_client = new ObjectInputStream(Cli_socket.getInputStream());
            // reads in the username if they join the chat
            
            game_serv.check_nm(this);
            game_serv.echo_chat(this, Username + " has joined the Chat", "j");
            game_serv.update_clients_box('a', this);
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
