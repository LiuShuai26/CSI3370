package gameserver;

import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class GameServer {

    private static int max = 100;
    private static int party_num = max / 2;
    private static ClientThread[] Clients_arr = new ClientThread[max];
    private static int connected = 0, max_index = 0;
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
    protected static byte[] buffer = new byte[1500];
    protected static byte[] send_data = new byte[1500];

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
                Clients_arr[connected] = new ClientThread(Cli_socket, "", connected);
                connected++;
            }
        }
    }

    public static void check_nm(ClientThread cli) {
        for (int i = 0; i < connected; i++) {
            try {
                if (cli.get_usernm().toLowerCase().equals(Clients_arr[i].get_usernm().toLowerCase()) && cli != Clients_arr[i]) {
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
                            echo_chat(null, "Moderator: " + message_box.getText(), "m");
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
                            echo_chat(null, "Moderator: " + message_box.getText(), "m");
                            message_box.setText("");
                        }
                    } else if (e.getSource() == kick_client) {
                        kick(cli_box.getSelectedItem().toString());
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

    public static void update_clients_box(char add_rem, ClientThread cli) {
        if (add_rem == 'r') {
            if (cli_box.getItemCount() == 1 && !cli_box.getSelectedItem().toString().toLowerCase().equals("no clients")) {
                cli_box.addItem(combo_holder);
            }
            cli_box.removeItem(cli.get_usernm());
        } else {
            if (cli_box.getItemCount() == 1) {
                cli_box.removeItem(combo_holder);
            }
            cli_box.addItem(cli.get_usernm());
        }
    }

    public static void kick(String usernm) { // kicks client
        for (int i = 0; i < connected; i++) {
            try {
                if (Clients_arr[i].get_usernm().equals(usernm)) {
                    //update_clients_box('r', Clients_arr[i]);
                    to_client = new ObjectOutputStream((Clients_arr[i].get_socket().getOutputStream()));
                    to_client.writeBytes("k\n");
                }
            } catch (Exception e) {

            }
        }
    }

    public static void remove_client(int index) {
        int i;
        for (i = index; i < connected; i++) {
            Clients_arr[i] = Clients_arr[i + 1];
            if (connected > 1) {
                try {
                    Clients_arr[i].set_place(i);
                } catch (Exception e) {

                }
            }
        }
        if (i > 0) {
            connected--;
        }
    }

    public static void personal_game_mess(ClientThread cli, String msg) throws IOException {
        DataOutputStream to_cli_per = new DataOutputStream(cli.get_socket().getOutputStream());
        to_cli_per.writeBytes("g" + msg);
    }

    public static void echo_chat(ClientThread client, String message, String joined_chat) throws IOException {
        if (joined_chat.equals("c")) {
            chat_area.append(client.get_usernm() + ": " + message + "\n");
        } else {
            chat_area.append(message + "\n");
        }

        for (int i = 0; i < connected; i++) {
            to_client = new ObjectOutputStream((Clients_arr[i].get_socket().getOutputStream()));
            if (client != Clients_arr[i] || joined_chat.equals("m")) {
                try {
                    if (joined_chat.equals("c")) { // A chat message
//                            send_data = (client.get_usernm() + ": " + message).getBytes();
//                            send_pack = new DatagramPacket(send_data, send_data.length, Clients_arr[i].get_ip(), 387);
//                            ssock.send(send_pack);
                        to_client.writeBytes("c" + client.get_usernm() + ": " + message + "\n");
                    } else if (joined_chat.equals("j")) { //
//                            send_data = message.getBytes();
//                            send_pack = new DatagramPacket(send_data, send_data.length, Clients_arr[i].get_ip(), 387);
//                            ssock.send(send_pack);
                        to_client.writeBytes("c" + message + "\n");
                    } else {
                        to_client.writeBytes("c" + message + "\n");
                    }

                } catch (Exception e) {
                }
            }
        }
    }
}
