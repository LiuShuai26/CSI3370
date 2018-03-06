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

    private static int max = 100;
    private static List<ClientThread> listClients = new ArrayList<ClientThread>();
    private static int connected = 0;
    protected static String[] Inet_addr;
    // protected static DatagramPacket rec_pack, send_pack;
    protected static ObjectOutputStream to_client;
    // protected static DatagramSocket ssock;
    protected static ServerSocket ssock;
    protected static InetAddress client_ip;
    // GUI Variables for the server
    protected static Object combo_holder = "No Clients";
    protected static JPanel west, south, east;
    protected static JTextArea chat_area;
    protected static JComboBox cli_box;
    protected static JButton send_message, kick_client;
    protected static JScrollPane scroll, scroll_box, scroll_clients;
    protected static JFrame Server_GUI = new JFrame();
    protected static JTextArea message_box;

    public static void main(String[] args) throws Exception {
        String client_nm = "";
        Inet_addr = InetAddress.getLocalHost().toString().split("/");
        //ssock = DatagramSocket(387);
        Serv_GUI(400, 550, "Chat Server " + Inet_addr[1]);
        ServerSocket ssock = new ServerSocket(1234);
        chat_area.append("Hosting at address: " + Inet_addr[1] + "\n");
        chat_area.append("Listening...\n");
        while (true) {
            if (connected <= max) {
                // get_username_packet();
                Socket Cli_socket = ssock.accept();
                listClients.add(new ClientThread(Cli_socket, "", connected));
                connected++;
            }
        }
    }

    public static void check_nm(ClientThread cli) {
        for (int i = 0; i < connected; i++) {
            try {
                if (listClients.contains(cli)) {
                    cli.set_usernm(cli.get_usernm() + connected);
                }
            } catch (Exception e) {

            }
        }
    }

    public static void Serv_GUI(int height, int width, String title) {
        DefaultCaret caret_chat, caret_mess;
        Server_GUI.setSize(width, height);
        Server_GUI.setResizable(false);
        Server_GUI.setLayout(new BorderLayout());
        Server_GUI.setTitle(title);
        Server_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chat_area = new JTextArea(15, 30);
        chat_area.setEditable(false);
        chat_area.setLineWrap(true);
        caret_chat = (DefaultCaret) chat_area.getCaret();
        caret_chat.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scroll = new JScrollPane(chat_area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cli_box = new JComboBox();
        cli_box.setEditable(false);
        cli_box.addItem(combo_holder);
        message_box = new JTextArea(3, 30);
        message_box.setLineWrap(true);
        scroll_box = new JScrollPane(message_box, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        message_box.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (!message_box.getText().equals("")) {
                            e.consume();
                            echo_chat(null, new Packet(chat_area.getText(), pack_type.chat_message));
                            message_box.setText("");
                        } else {
                            e.consume();
                            message_box.setText("");
                        }
                    }
                } catch (Exception er) {
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        send_message = new JButton("Send");
        kick_client = new JButton("Kick");
        Server_GUI.getRootPane().setDefaultButton(send_message);
        ActionListener Click = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (e.getSource() == send_message) {
                        if (!message_box.getText().equals("")) {
                            echo_chat(null, new Packet(chat_area.getText(), pack_type.chat_message));
                            message_box.setText("");
                        }
                    } else if (e.getSource() == kick_client) {

                    }
                } catch (Exception er) {
                }
            }
        };
        send_message.addActionListener(Click);
        kick_client.addActionListener(Click);
        west = new JPanel();
        south = new JPanel();
        east = new JPanel();
        west.add(scroll);
        south.add(send_message);
        south.add(scroll_box);
        west.add(cli_box);
        west.add(kick_client);
        Server_GUI.add(west, BorderLayout.WEST);
        Server_GUI.add(south, BorderLayout.SOUTH);
        //Server_GUI.add(east, BorderLayout.EAST);
        Server_GUI.setVisible(true);

    }

    public static void addClient(ClientThread cli) throws IOException {
        listClients.add(cli);
        if (listClients.size() == 1) {
            cli_box.addItem(cli.get_usernm());
            cli_box.removeItem(combo_holder);
        } else {
            cli_box.addItem(cli.get_usernm());
        }
        echo_chat(cli, new Packet("", pack_type.connected));
    }

    public static void removeClient(ClientThread cli) throws IOException{
        listClients.remove(cli);
        if (listClients.size() == 0) {
            cli_box.addItem(combo_holder);
        } else {
            cli_box.removeItem(cli.get_usernm());
        }
        echo_chat(cli, new Packet("", pack_type.disconnected));
    }

    public static void kick(ClientThread cli, String reason) { // kicks client
        for (int i = 0; i < connected; i++) {
            try {
                //update_clients_box('r', Clients_arr[i]);
                to_client = new ObjectOutputStream((cli.get_socket().getOutputStream()));
                constructPacket(reason, pack_type.kick_pack);
                cli.get_socket().close();
            } catch (Exception e) {
                // not sure
            }
        }
    }

    public static Packet constructPacket(String load, pack_type type) {
        Packet pack = new Packet(load, type);
        return pack;
    }

    private static void displayMessage(String message) {
        chat_area.append(message + "\n");
    }

    public static void echo_chat(ClientThread client, Packet pack) throws IOException {
        if (pack.getPackType() == pack_type.connected) {
            displayMessage(pack.getPayload() + " has connected!");
        } else {
            displayMessage(pack.getPayload());
        }

        for (ClientThread cli : listClients) {
            to_client = new ObjectOutputStream((cli.get_socket().getOutputStream()));
            if (client != cli) {
                try {
                    switch (pack.getPackType()) {
                        case chat_message:
                            to_client.writeObject(cli.get_usernm() + ": " + pack);
                            break;
                        case connected:
                            to_client.writeObject(cli.get_usernm() + " has connected!");
                            break;
                        case disconnected:
                            to_client.writeObject(cli.get_usernm() + " has disconnected!");
                            break;
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}
