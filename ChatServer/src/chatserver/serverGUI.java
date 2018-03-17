/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import Packet.Packet;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author mike
 */
public class serverGUI extends JFrame {

    private static ChatServer server;
    protected Object combo_holder = "No Clients";
    protected JPanel west, south, east;
    protected JTextArea chat_area;
    protected JComboBox cli_box;
    protected JButton send_message, kick_client, banClient;
    protected JScrollPane scroll, scroll_box, scroll_clients;
    protected JTextArea message_box;

    public serverGUI(ChatServer serv, String ip, int height, int width) {
        server = serv;
        Initialize(height, width);
        chat_area.append("Hosting at address: " + ip + "\n");
        chat_area.append("Listening...\n");
        setTitle("Chat Server: " + ip);
    }

    public void Initialize(int height, int width) {
        DefaultCaret caret_chat;
        setSize(width, height);
        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                            displayMessage("Administrator: " + message_box.getText());
                            server.echo_chat(null, server.constructPacket(message_box.getText(), Packet.pack_type.adminMessage));
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
        banClient = new JButton("Ban");
        getRootPane().setDefaultButton(send_message);
        ActionListener Click = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (e.getSource() == send_message) {
                        if (!message_box.getText().equals("")) {
                            chat_area.append(message_box.getText());
                            server.echo_chat(null, server.constructPacket(message_box.getText(), Packet.pack_type.adminMessage));
                            message_box.setText("");
                        }
                    } else if (e.getSource() == kick_client) {
                        server.kick(server.fetchUserbyName(cli_box.getSelectedItem().toString()), "Just a kick message holder");
                    } else if (e.getSource() == banClient) {
                        server.getBadMacs().banMAC(server.fetchUserbyName(cli_box.getSelectedItem().toString()).get_socket());
                        server.kick(server.fetchUserbyName(cli_box.getSelectedItem().toString()), "Just a kick message holder");
                    }
                } catch (Exception er) {
                }
            }
        };
        send_message.addActionListener(Click);
        kick_client.addActionListener(Click);
        banClient.addActionListener(Click);
        west = new JPanel();
        south = new JPanel();
        east = new JPanel();
        west.add(scroll);
        south.add(send_message);
        south.add(scroll_box);
        west.add(cli_box);
        west.add(kick_client);
        west.add(banClient);
        add(west, BorderLayout.WEST);
        add(south, BorderLayout.SOUTH);
        setVisible(true);

    }

    public void addClient(ClientThread cli) throws IOException {
        if (server.getClientsList().size() >= 1) {
            cli_box.removeItem(combo_holder);
        }
        cli_box.addItem(cli.get_usernm());
    }

    public void removeClient(ClientThread cli) throws IOException {
        server.getClientsList().remove(cli);
        cli_box.removeItem(cli.get_usernm());
        if (server.getClientsList().size() == 0 && cli_box.getItemCount() == 0) {
            cli_box.addItem(combo_holder);
            cli_box.setSelectedItem(combo_holder);
        }
        cli_box.removeItem(cli.get_usernm());
    }

    public void displayMessage(String message) {
        chat_area.append(message + "\n");
    }
}
