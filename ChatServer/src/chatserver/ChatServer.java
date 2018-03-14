package chatserver;

import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import Packet.Packet;
import Packet.Packet.pack_type;
import javax.swing.event.*;

public class ChatServer {

    private int max = 100;
    private List<ClientThread> listClients = new ArrayList<ClientThread>();
    private bannedMacs badMACS;
    private int connected = 0;
    protected String[] Inet_addr;
    // protected static DatagramPacket rec_pack, send_pack;
    // protected static DatagramSocket ssock;
    protected ServerSocket ssock;
    protected InetAddress client_ip;
    // GUI Variables for the server
    private serverGUI gui;

    public ChatServer() throws Exception {
        Inet_addr = InetAddress.getLocalHost().toString().split("/");
        InitializeGui();
        ssock = new ServerSocket(1234);
        badMACS = new bannedMacs();
        while (true) {
            if (connected <= max) {
                // get_username_packet();
                Socket Cli_socket = ssock.accept();
                if (!badMACS.isMacBanned(badMACS.pullMac(Cli_socket))) {
                    listClients.add(new ClientThread(Cli_socket, "", connected, badMACS.pullMac(Cli_socket), this));
                }
                connected++;
            }
        }
    }
    public bannedMacs getBadMacs(){
        return badMACS;
    }
    public serverGUI getGui(){
        return gui;
    }
    public List<ClientThread> getClientsList(){
        return listClients;
    }
    public void InitializeGui(){
        gui = new serverGUI(this, Inet_addr[1],400, 600);
    }
    public void check_nm(ClientThread cli, String name) {
        for (ClientThread client : listClients) {
            if (client.get_usernm().toLowerCase().equals(name.toLowerCase())) {
                cli.set_usernm(name + connected);
                return;
            }
        }
        cli.set_usernm(name);
    }

    private boolean isBanned(Socket cli_sock) throws IOException {
        if (badMACS.isMacBanned(badMACS.pullMac(cli_sock))) {
            return true;
        }
        return false;
    }


    public ClientThread fetchUserbyName(String username) {
        for (ClientThread client : listClients) {
            if (client.get_usernm().equals(username)) {
                return client;
            }
        }
        return null; // should never be reached
    }

    public void kick(ClientThread cli, String reason) { // kicks client
        for (int i = 0; i < connected; i++) {
            try {
                //update_clients_box('r', Clients_arr[i]);
                cli.getOutputStream().writeObject(constructPacket(reason, pack_type.kick_pack));
                cli.get_socket().close();
                gui.removeClient(cli);
                echo_chat(null, constructPacket(cli.get_usernm(), pack_type.disconnected));
            } catch (Exception e) {
                // not sure
            }
        }
    }

    public Packet constructPacket(String load, pack_type type) {
        Packet pack = new Packet(load, type);
        return pack;
    }

    private void handleMessages(ClientThread cli, Packet pack) {
        switch (pack.getPackType()) {
            case connected:
                gui.displayMessage(cli.get_usernm() + " has connected!");
                break;
            case chat_message:
               gui.displayMessage(cli.get_usernm() + ": " + pack.getPayload());
                break;
            case disconnected:
                gui.displayMessage(cli.get_usernm() + " has disconnected!");
                break;
        }
    }

    public void echo_chat(ClientThread client, Packet pack) throws IOException {
        handleMessages(client, pack);
        ObjectOutputStream to_client;
        for (ClientThread cli : listClients) {
            if (client == null || (!client.get_usernm().equals(cli.get_usernm()))) {
                try {
                    to_client = cli.getOutputStream();
                    switch (pack.getPackType()) {
                        case chat_message:
                            to_client.writeObject(constructPacket(client.get_usernm() + ": " + pack.getPayload(), pack_type.chat_message));
                            break;
                        case connected:
                            to_client.writeObject(constructPacket(client.get_usernm() + " has connected!", pack_type.chat_message));
                            break;
                        case disconnected:
                            to_client.writeObject(constructPacket(client.get_usernm() + " has disconnected!", pack_type.chat_message));
                            break;
                        case adminMessage:
                            to_client.writeObject(constructPacket("Administrator: " + pack.getPayload(), pack_type.adminMessage));
                    }
                } catch (Exception e) {
                    gui.removeClient(cli);
                    System.out.println(e.toString() + " echo");
                }
            }
        }
    }
}
