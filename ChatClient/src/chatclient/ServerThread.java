/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Packet.Packet;
import Packet.Packet.pack_type;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author mike
 */
public class ServerThread extends JFrame implements Runnable {

    // variables for the thread
    private Thread server_thread;
    private Packet lastPack;
    private ObjectOutputStream to_server;
    private ObjectInputStream from_server;
    private Socket serv_socket;
    private int port;
    protected InetAddress serv_ip;
    protected String[] cli_ip;
    protected String my_ip;
    protected JOptionPane enter_IP;
    // variables for the GUI
    private clientGUI gui;

    public ServerThread(Socket sock, String user_nm, InetAddress ip_addr, int port) throws IOException {
        cli_ip = InetAddress.getLocalHost().toString().split("/");
        my_ip = cli_ip[1];
        this.port = port;
        serv_ip = ip_addr;
        serv_socket = sock;
        server_thread = new Thread(this);
        from_server = new ObjectInputStream(serv_socket.getInputStream());
        to_server = new ObjectOutputStream(serv_socket.getOutputStream());
        server_thread.start();
        gui = new clientGUI(this, my_ip, 450, 600);
        outgoingPackets(constructPacket(user_nm, pack_type.connected));
    }

    public Packet constructPacket(String load, pack_type type) {
        Packet pack = new Packet(load, type);
        return pack;
    }


    private void handlePackets(Packet pack) throws IOException {
        pack_type type = pack.getPackType();
        switch (type) {
            // Do things based on the packet type
            case adminMessage:
            case chat_message:
                // display the message
                gui.displayMessage(pack.getPayload());
                break;
            case file_pack:
                // show file popup and try and download it
                break;
            case kick_pack:
                // the user is kicked
                kicked(pack.getPayload());
                break;
            case connected:
                // Shouldn't receive a username packet.
                break;
            case whisper:
                
                break;
            case ban_pack:
                
            default:
            // no packet type Error?
                break;
        }
    }

    public void outgoingPackets(Packet pack) {
        try {
            to_server.writeObject(pack);
        } catch (Exception e) {
            System.out.println(e.toString() + " outgoing");
            JOptionPane warning = new JOptionPane("Oh No!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            JOptionPane.showMessageDialog(warning, "The server has closed. Please Reconnect!");
            System.exit(3000);
        }

    }

    private void kicked(String reason) throws IOException {
        this.setVisible(false);
        serv_socket.close();
        JOptionPane warning = new JOptionPane("You Have been kicked from the server!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
        if(reason.equals("")){
            reason = "You have been kicked.";
        }else{
            reason = "You have been kicked!\nReason: " + reason;
        }
        JOptionPane.showMessageDialog(warning, reason);
        System.exit(-1);
    }

    private void Banned(String reason) throws IOException{
        this.setVisible(false);
        serv_socket.close();
        JOptionPane warning = new JOptionPane("You have been banned from the server!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
        if(reason.equals("")){
            reason = "You have been banned.";
        }else{
            reason = "You have been banned!\nReason: " + reason;
        }
        JOptionPane.showMessageDialog(warning, reason);
        System.exit(-1);
    }
    @Override
    public void run() {

        while (true) {
            Packet inPacket;
            try { // Get the messages from the server or from other users
                inPacket = (Packet) from_server.readObject();
                handlePackets(inPacket);
            }catch(EOFException er){
                // Null
            } 
            catch (Exception e) {
                System.out.println(e.toString());
                JOptionPane warning = new JOptionPane("The Server has closed!!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
                JOptionPane.showMessageDialog(warning, "Server has closed.");
                System.exit(-1);
                break;
            }
        }

    }
}
