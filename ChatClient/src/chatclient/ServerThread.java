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
public class ServerThread extends JFrame implements Runnable, ActionListener {

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
    protected JTextArea chat_text, chat_message;
    protected JButton send_message;
    protected JLabel chat_lbl, spacer_lbl_recieve, spacer_lbl_send;
    protected JPanel Center, South;
    protected JScrollPane scroll_chat, scroll_send_message;

    public ServerThread(Socket sock, String user_nm, InetAddress ip_addr, int port) throws IOException {
        cli_ip = InetAddress.getLocalHost().toString().split("/");
        my_ip = cli_ip[1];
        this.port = port;
        serv_ip = ip_addr;
        serv_socket = sock;
        server_thread = new Thread(this);
        to_server = new ObjectOutputStream(serv_socket.getOutputStream());
        server_thread.start();
        ChatGui(900, 450, "Chat Hub");
        outgoingPackets(constructPacket(user_nm, pack_type.connected));
        System.out.println(user_nm);
    }

    public void ChatGui(int width, int height, String title) {
        DefaultCaret caret_chat_wim;
        // create the window and its properties
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.setTitle("Chat Hub. Your Ip is: " + my_ip);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // defining the different compnents of the JFrame
        //Text areas and scrolling capability
        chat_text = new JTextArea(20, 33);
        chat_text.append("Welcome to the Chat!\n");
        chat_text.setEditable(false);
        chat_text.setLineWrap(true);
        caret_chat_wim = (DefaultCaret) chat_text.getCaret();
        caret_chat_wim.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scroll_chat = new JScrollPane(chat_text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chat_message = new JTextArea(3, 27);
        chat_message.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (!chat_message.getText().equals("")) {
                            e.consume();
                            outgoingPackets(constructPacket(chat_message.getText(), pack_type.chat_message));
                            chat_message.setText("");
                        } else {
                            e.consume();
                            chat_message.setText("");
                        }
                    }
                } catch (Exception er) {

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        scroll_send_message = new JScrollPane(chat_message, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chat_message.setLineWrap(true);
        // Spacers
        spacer_lbl_recieve = new JLabel("        ");
        spacer_lbl_send = new JLabel("         ");
        // adding buttons
        send_message = new JButton("Send");
        send_message.addActionListener(this);
        // Jpanels needed
        South = new JPanel();
        Center = new JPanel();
        // adding components to the Jpanels
        // North components
        Center.add(spacer_lbl_recieve);
        Center.add(scroll_chat);
        // South panel components
        South.add(spacer_lbl_send);
        South.add(send_message);
        South.add(scroll_send_message); // JScrolling for the chat message window

        // West.add(chat_lbl);
        // adding the panels to the JFrame
        this.add(South, BorderLayout.SOUTH);
        this.add(Center, BorderLayout.CENTER);
        this.setVisible(true);
        chat_message.requestFocus();
        if (chat_message.isFocusOwner()) {
            this.getRootPane().setDefaultButton(send_message);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message;
        if (e.getSource().equals(send_message)) {
            if (!chat_message.getText().equals("")) {
                outgoingPackets(constructPacket(chat_message.getText(), pack_type.chat_message));
            } else {
                chat_message.setText("");
            }
        }
    }

    private Packet constructPacket(String load, pack_type type) {
        Packet pack = new Packet(load, type);
        return pack;
    }

    private void displayMessage(Packet pack) {
        chat_message.append(pack.getPayload());
    }

    private void handlePackets(Packet pack) throws IOException {
        pack_type type = pack.getPackType();
        switch (type) {
            // Do things based on the packet type
            case chat_message:
                // display the message
                displayMessage(pack);
                break;
            case file_pack:
                // show file popup and try and download it
                break;
            case kick_pack:
                // the user is kicked
                kicked();
                break;
            case connected:
                // Shouldn't receive a username packet.
                break;
            case connectionLoss:

                break;
            default:
            // no packet type Error?
        }
    }

    private void outgoingPackets(Packet pack) {
        try {
            to_server.writeObject(pack);
        } catch (Exception e) {
            System.out.println(e.toString());
            JOptionPane warning = new JOptionPane("Oh No!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            JOptionPane.showMessageDialog(warning, "The server has closed. Please Reconnect!");
            System.exit(3000);
        }

    }

    private void kicked() throws IOException {
        this.setVisible(false);
        serv_socket.close();
        JOptionPane warning = new JOptionPane("You Have been kicked from the server!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
        JOptionPane.showMessageDialog(warning, "You have been kicked");
        System.exit(-1);
    }

    @Override
    public void run() {
        try { // Send your usern ame to the Server to store it 
            from_server = new ObjectInputStream(serv_socket.getInputStream());
        } catch (Exception ei) {
            System.out.println("Not wroking");
        }
        while (true) {
            Packet inPacket;
            try { // Get the messages from the server or from other users
                inPacket = (Packet) from_server.readObject();
                handlePackets(inPacket);
            } catch (Exception e) {

            }
        }

    }
}
