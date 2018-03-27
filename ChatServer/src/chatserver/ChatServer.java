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
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javax.swing.event.*;
import jdk.nashorn.internal.runtime.StoredScript;

public class ChatServer {

    private int max = 100;
    private HashSet<ClientThread> clientsHash = new HashSet<ClientThread>();
    private HashSet<String> storedUsernames = new HashSet<String>();
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
                    clientsHash.add(new ClientThread(Cli_socket, "", connected, badMACS.pullMac(Cli_socket), this));
                } else {
                    Cli_socket.close();
                }
                connected++;
            }
        }
    }

    public bannedMacs getBadMacs() {
        return badMACS;
    }

    public serverGUI getGui() {
        return gui;
    }

    public HashSet<String> getStoredNames() {
        return storedUsernames;
    }

    public HashSet<ClientThread> getHashSet() {
        return clientsHash;
    }

    public void InitializeGui() {
        gui = new serverGUI(this, Inet_addr[1], 400, 600);
    }

    public void check_nm(ClientThread cli, String name) {
        if (storedUsernames.contains(name)) {
            cli.set_usernm(name + connected);
            return;
        }
        cli.set_usernm(name);
        /* for (ClientThread client : listClients) {
            if (client.get_usernm().toLowerCase().equals(name.toLowerCase())) {
                cli.set_usernm(name + connected);
                return;
            }
        }
        cli.set_usernm(name); */
    }

    private boolean isBanned(Socket cli_sock) throws IOException {
        if (badMACS.isMacBanned(badMACS.pullMac(cli_sock))) {
            return true;
        }
        return false;
    }

    public ClientThread fetchUserbyName(String username) {
        for (ClientThread client : clientsHash) {
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

    public void ban(ClientThread cli, String reason) {
        for (int i = 0; i < connected; i++) {
            try {
                cli.getOutputStream().writeObject(constructPacket(reason, pack_type.ban_pack));
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

    private void handleMessages(ClientThread cli, Packet pack) throws IOException {
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
            case whisper:
                privateMessage(cli, pack);
                break;
        }
    }

    public void privateMessage(ClientThread cli, Packet pack) throws IOException {
        String[] split = pack.getPayload().split("@"); // Seperates the username from the message
        ClientThread targetClient = fetchUserbyName(split[0]);
        if (clientsHash.contains(targetClient)) {
            ObjectOutputStream to_client = targetClient.getOutputStream();
            to_client.writeObject(constructPacket(split[1], pack_type.whisper));
        }
    }

    public void echo_chat(ClientThread client, Packet pack) throws IOException {
        handleMessages(client, pack);
        ObjectOutputStream to_client;
        for (ClientThread cli : clientsHash) {
            if (client == null || (!client.get_usernm().equals(cli.get_usernm()))) {
                try {
                    to_client = cli.getOutputStream();
                    switch (pack.getPackType()) {
                        case chat_message:
                            to_client.writeObject(constructPacket(client.get_usernm() + ": " + pack.getPayload(), pack_type.chat_message));
                            break;
                        case connected:
                            to_client.writeObject(constructPacket(client.get_usernm(), pack_type.connected));
                            break;
                        case disconnected:
                            to_client.writeObject(constructPacket(client.get_usernm(), pack_type.disconnected));
                            break;
                        case adminMessage:
                            to_client.writeObject(constructPacket("Administrator: " + pack.getPayload(), pack_type.adminMessage));
                            break;
                    }
                } catch (Exception e) {
                    gui.removeClient(cli);
                    System.out.println(e.toString() + " echo");
                }
            } else if (client.get_usernm().equals(cli.get_usernm())) {
                if (pack.getPackType() == pack_type.connected) {
                    to_client = client.getOutputStream();
                    // sends list of current users
                    List<String> list = new ArrayList<String>(storedUsernames);
                    Packet listPack = constructPacket("", pack_type.listPack);
                    listPack.setClientList(list);
                    to_client.writeObject(listPack);
                }
            }
        }
    }
}
