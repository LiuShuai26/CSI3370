/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import Packet.Packet;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.Box;

import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import javax.swing.text.DefaultCaret;
import java.awt.GridBagConstraints;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Color;

/**
 *
 * @author mike
 */
public class serverGUI extends JFrame implements ActionListener {

    private GridBagConstraints constraints;

    private static ChatServer server;
    protected Object combo_holder = "No Clients";

    protected JPanel west, south, east, mainPanel, optionPanel;
    protected JPanel settings;

    protected JTextArea chat_area;

    protected JComboBox cli_box;

    protected JButton send_message, kick_client, banClient, whisperButton,
            saveButton;

    protected JScrollPane scroll, scroll_box, scroll_clients;

    protected JTextArea message_box;

    protected JTabbedPane serverTabs;

    public serverGUI(ChatServer serv, String ip, int height, int width) {
        server = serv;
        Initialize(height, width);
        chat_area.append("Hosting at address: " + ip + "\n");
        chat_area.append("Listening...\n");
        setTitle("Chat Server: " + ip);
    }

    public void Initialize(int height, int width) {
        setSize(width, height);
        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        serverTabs = new JTabbedPane();

        CreateMainTab();

        constraints = new GridBagConstraints();
        //constraints.fill = GridBagConstraints.BOTH;
        CreateOptionTab();

        add(serverTabs);
        /*
        ActionListener Click = new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                String reason = "";
                try 
                {
                    if (e.getSource() == send_message) 
                    {
                        if (!message_box.getText().equals("")) 
                        {
                            chat_area.append(message_box.getText());
                            server.echo_chat(null, server.constructPacket(message_box.getText(), Packet.pack_type.adminMessage));
                            message_box.setText("");
                        }
                    } 
                    else if (e.getSource() == kick_client) 
                    {
                        reason = JOptionPane.showInputDialog("Give a reason for the kick");
                        server.kick(server.fetchUserbyName(cli_box.getSelectedItem().toString()), reason);
                    } 
                    else if (e.getSource() == banClient) 
                    {
                        server.getBadMacs().banMAC(server.fetchUserbyName(cli_box.getSelectedItem().toString()).get_socket());
                        reason = JOptionPane.showInputDialog("Give a reason for the ban");
                        server.ban(server.fetchUserbyName(cli_box.getSelectedItem().toString()), reason);
                    }
                } 
                catch (Exception er) 
                {
                }
            }
        };
         */

        setVisible(true);

    }

    private void CreateMainTab() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        chat_area = new JTextArea(15, 30);
        chat_area.setEditable(false);
        chat_area.setLineWrap(true);

        DefaultCaret caret_chat;

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

        // Initialize buttons
        send_message = new JButton("Send");
        kick_client = new JButton("Kick");
        banClient = new JButton("Ban");

        // Whisper Button needs to be implemented
        whisperButton = new JButton("Whisper");

        getRootPane().setDefaultButton(send_message);

        send_message.addActionListener(this);
        kick_client.addActionListener(this);
        banClient.addActionListener(this);
        whisperButton.addActionListener(this);

        west = new JPanel();
        south = new JPanel();
        east = new JPanel();

        west.add(scroll);

        south.add(send_message);
        south.add(scroll_box);

        west.add(cli_box);
        west.add(kick_client);
        west.add(banClient);
        west.add(whisperButton);

        mainPanel.add(west, BorderLayout.WEST);
        mainPanel.add(south, BorderLayout.SOUTH);

        serverTabs.addTab("Chat", null, mainPanel, "");
    }

    private void CreateOptionTab() {
        optionPanel = new JPanel();

        optionPanel.setLayout(new BorderLayout());

        SetOptionWest();

        SetOptionSouth();

        SetOptionEast();

        serverTabs.addTab("Option", null, optionPanel, "");
    }

    private void SetOptionEast() {
        JPanel optEast = new JPanel();
        optEast.setLayout(new GridLayout(3, 1));

        JLabel label = new JLabel("Current Server Settings");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel spacer1 = new JLabel("     ");
        spacer1.setFont(new Font("Times New Roman", Font.BOLD, 30));
        JLabel spacer2 = new JLabel("     ");
        spacer2.setFont(new Font("Times New Roman", Font.BOLD, 30));

        JTextArea display = new JTextArea(25, 25);
        display.setLineWrap(true);
        display.setText("No limit on Allowed Clients\nNo Limit on # of Characters in a Message\n"
                + "No Banned Phrases\nFile Sharing NOT ALLOWED");

        optEast.add(label);
        optEast.add(display);
        optEast.add(spacer1);

        optionPanel.add(optEast, BorderLayout.EAST);
    }

    private void SetOptionSouth() {
        JPanel optSouth = new JPanel();
        optSouth.setLayout(new FlowLayout());

        saveButton = new JButton("Save Settings");

        optSouth.add(saveButton);

        optionPanel.add(optSouth, BorderLayout.SOUTH);
    }

    private void SetOptionWest() {
        JPanel input1 = new JPanel(),
                input2 = new JPanel();
        input1.setLayout(new GridBagLayout());
        input2.setLayout(new GridBagLayout());

        Border border = BorderFactory.createLineBorder(Color.BLACK);

        settings = new JPanel();
        //settings.setLayout(new GridBagLayout());
        settings.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel connUserLimit = new JLabel();
        connUserLimit.setText("# of allowed Clients");
        connUserLimit.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        connUserLimit.setHorizontalAlignment(SwingConstants.CENTER);
        connUserLimit.setVerticalAlignment(SwingConstants.CENTER);

        // Want to control the size of the field, but can't
        JTextArea userLimitInput = new JTextArea(1, 10);
        userLimitInput.setBorder(border);

        JLabel charLimit = new JLabel();
        charLimit.setText("Message Character Limit");
        charLimit.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        charLimit.setHorizontalAlignment(SwingConstants.CENTER);
        charLimit.setVerticalAlignment(SwingConstants.CENTER);

        // Want to control the size of the field, but can't
        JTextArea charLimitInput = new JTextArea(1, 10);
        charLimitInput.setBorder(border);

        JCheckBox fileCheck = new JCheckBox("Allow File Sharing");
        fileCheck.setHorizontalTextPosition(SwingConstants.LEFT);
        fileCheck.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        fileCheck.setHorizontalAlignment(SwingConstants.CENTER);
        fileCheck.setVerticalAlignment(SwingConstants.CENTER);

        JLabel phraseBan = new JLabel();
        phraseBan.setText("Banned Phrases");
        phraseBan.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        phraseBan.setHorizontalAlignment(SwingConstants.CENTER);
        phraseBan.setVerticalAlignment(SwingConstants.CENTER);

        JTextArea bannedInput = new JTextArea();
        bannedInput.setLineWrap(true);

        JScrollPane scrollArea = new JScrollPane(bannedInput,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //constraints.weightx = 500;
        //constraints.weighty = 0;
        input1.add(userLimitInput);
        settings.add(connUserLimit);
        settings.add(input1);

        input2.add(charLimitInput);
        settings.add(charLimit);
        settings.add(input2);

        settings.add(phraseBan);
        settings.add(scrollArea);

        settings.add(fileCheck);

        optionPanel.add(settings, BorderLayout.WEST);
    }

    public void addClient(ClientThread cli) throws IOException {
        if (server.getHashSet().size() >= 1) {
            cli_box.removeItem(combo_holder);
        }
        cli_box.addItem(cli.get_usernm() + "(" + cli.getReportCount() + ")");
        server.getStoredNames().add(cli.get_usernm());
    }

    public void removeClient(ClientThread cli) throws IOException {
        server.getHashSet().remove(cli);
        cli_box.removeItem(cli.get_usernm() + "(" + cli.getReportCount() + ")");
        if (server.getHashSet().size() == 0 && cli_box.getItemCount() == 0) {
            cli_box.addItem(combo_holder);
            cli_box.setSelectedItem(combo_holder);
        }
        cli_box.removeItem(cli.get_usernm());
    }

    public void addReportToClient(ClientThread targetClient, int count, String reportee){
        String userNm = targetClient.get_usernm();
        cli_box.removeItem(userNm + "(" + (targetClient.getReportCount() - 1) + ")");
        cli_box.addItem(userNm + "(" + count + ")");
        displayMessage(reportee + " has reported " + userNm + ".");
    }
    
    public void displayMessage(String message) {
        chat_area.append(message + "\n");
    }

    public void whisperClient() throws IOException {
        String whisperMess = "";
        whisperMess = JOptionPane.showInputDialog("Enter your private message:");
        if (whisperMess.length() <= 0) {
            JOptionPane warning = new JOptionPane("Oh No!!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            JOptionPane.showMessageDialog(warning, "Your message must be atleast 1 character long!");
        } else {
            String Username = cli_box.getSelectedItem().toString();
            if (Username != null) { // prevents initial sending of whisper to noone
                server.privateMessage(null, server.constructPacket(Username + "@" + whisperMess, Packet.pack_type.whisper), true);
                displayMessage("Admin to " + Username + "@" + whisperMess);
            } else {
                displayMessage("No Valid User Selected!");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String reason = "";
        try {
            if (e.getSource() == send_message) {
                if (!message_box.getText().equals("")) {
                    chat_area.append(message_box.getText());
                    server.echo_chat(null, server.constructPacket(message_box.getText(), Packet.pack_type.adminMessage));
                    message_box.setText("");
                }
            } else if (e.getSource() == kick_client) {
                reason = JOptionPane.showInputDialog("Give a reason for the kick");
                server.kick(server.fetchUserbyName(cli_box.getSelectedItem().toString()), reason);
            } else if (e.getSource() == banClient) {
                server.getBadMacs().banMAC(server.fetchUserbyName(cli_box.getSelectedItem().toString()).get_socket());
                reason = JOptionPane.showInputDialog("Give a reason for the ban");
                server.ban(server.fetchUserbyName(cli_box.getSelectedItem().toString()), reason);
            } else if (e.getSource() == whisperButton) {
                whisperClient();
            }
        } catch (Exception er) {
        }
    }
}
