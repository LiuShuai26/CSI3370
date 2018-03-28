/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import Packet.Packet;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.javafx.scene.control.skin.VirtualFlow.ArrayLinkedList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.print.attribute.standard.Severity;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author mike
 */
public class clientGUI extends JFrame implements ActionListener {

    private ServerThread client;

    private List<String> clientList = new ArrayList<String>(); // stores the clients currently in the Chat for client to message

    // Linked List of users that gets updated whenever a new user connects to the server
    // When that happens will convert to an array that is diplayed by the JList connected users selection display
    protected JTextArea chat_text, chat_message;

    protected JButton sendButton, fileButton, whisperButton,
            reportButton, connectionButton;

    protected JLabel chat_lbl, spacer_lbl_recieve, spacer_lbl_send;

    protected JPanel Right, South, Left, Center;

    protected JScrollPane scroll_chat, scroll_send_message, clientScroll;

    private JList<String> userList;

    private DefaultListModel<String> model = new DefaultListModel<String>();

    private String disconnText = "Disconnect";
    private String connText = "Connect";

    public clientGUI(ServerThread client, String ip, int height, int width) {
        this.client = client;
        Initialize(width, height);
        chat_text.append("Welcome to the Chat!\n");
        setTitle("Chat Client. Username/IP: " + client.getUsername() + "/" + ip );
    }

    public void Initialize(int width, int height) {
        setSize(width, height);
        setResizable(false);
        setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SetLeft();

        SetRight();

        SetBottom();

        SetCenter();

        this.setVisible(true);

        chat_message.requestFocus();
        if (chat_message.isFocusOwner()) {
            this.getRootPane().setDefaultButton(sendButton);
        }

    }

    private void SetLeft() {
        JLabel listTitle = new JLabel("Connected Users", JLabel.CENTER);
        listTitle.setFont(new Font("Times New Roman", Font.PLAIN, 16));

        JLabel spacer = new JLabel("     ");
        spacer.setFont(new Font("Times New Roman", Font.BOLD, 30));

        userList = new JList<String>(model);
        userList.setVisibleRowCount(5);
        userList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        userList.setModel(model);
        // Used for Multiple selection
        // Not entirely sure how it works
        //userList.setSelectionInterval(0, users.size());
        Left = new JPanel();
        Left.setLayout(new BorderLayout());//new BoxLayout(Left, BoxLayout.Y_AXIS));

        clientScroll = new JScrollPane(userList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        clientScroll.setPreferredSize(new Dimension(200, 100));

        whisperButton = new JButton("Whisper");
        whisperButton.addActionListener(this);

        reportButton = new JButton("Report");
        reportButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        buttonPanel.add(whisperButton);
        buttonPanel.add(spacer, SwingConstants.CENTER);
        buttonPanel.add(reportButton);

        Left.add(listTitle, BorderLayout.NORTH);
        Left.add(spacer, BorderLayout.WEST);
        Left.add(clientScroll, BorderLayout.CENTER);
        Left.add(buttonPanel, BorderLayout.SOUTH);

        add(Left, BorderLayout.WEST);
    }

    private void SetRight() {
        Right = new JPanel();

        DefaultCaret caret_chat_wim;

        chat_text = new JTextArea(15, 30);
        //chat_text.append("Welcome to the Chat!\n");
        chat_text.setEditable(false);
        chat_text.setLineWrap(true);

        caret_chat_wim = (DefaultCaret) chat_text.getCaret();
        caret_chat_wim.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scroll_chat = new JScrollPane(chat_text,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        Right.add(scroll_chat);

        add(Right, BorderLayout.EAST);
    }

    private void SetCenter() {
        JLabel spacer = new JLabel("      ");
        spacer.setFont(new Font("Times New Roman", Font.BOLD, 95));

        Center = new JPanel();
        Center.setLayout(new BorderLayout());

        JPanel panel = new JPanel();

        connectionButton = new JButton(disconnText);
        connectionButton.addActionListener(this);

        panel.add(connectionButton);

        Center.add(spacer, BorderLayout.NORTH);
        Center.add(panel, BorderLayout.CENTER);

        add(Center, BorderLayout.CENTER);
    }

    private void SetBottom() {
        South = new JPanel();

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
                            displayMessage(chat_message.getText());
                            client.outgoingPackets(client.constructPacket(chat_message.getText(), Packet.pack_type.chat_message));
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

        JScrollPane scroll_box = new JScrollPane(chat_message,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        chat_message.setLineWrap(true);

        fileButton = new JButton("Select File");
        fileButton.addActionListener(this);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);

        //fileButton.addActionListener(this);
        South.add(sendButton);
        South.add(scroll_box);
        South.add(fileButton);

        add(South, BorderLayout.SOUTH);
    }

    public void displayMessage(String message) {
        chat_text.append(message + "\n");
    }

    public void addClient(String Username) {
        // Adds clients username to the list in the combobox
        displayMessage(Username + " has connected!");
        clientList.add(Username);
        modifyJList(Username, true);
    }

    private void modifyJList(String Username, boolean add) {
        if (add) {
            model.addElement(Username);
        } else {
            model.remove(model.indexOf(Username));
        }
    }

    public void removeClient(String Username) {
        // Removes client from combobox list
        displayMessage(Username + " has disconnected");
        clientList.remove(Username);
        modifyJList(Username, false);
    }

    public void whisperClient() {
        String whisperMess = "";
        whisperMess = JOptionPane.showInputDialog("Enter your private message:");
        if (whisperMess.length() <= 0) {
            JOptionPane warning = new JOptionPane("Oh No!!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            JOptionPane.showMessageDialog(warning, "Your message must be atleast 1 character long!");
        } else {
            String selectedUser = getSelectedUsername();
            if (selectedUser != null) { // prevents initial sending of whisper to noone
                client.outgoingPackets(client.constructPacket(selectedUser + "@" + whisperMess, Packet.pack_type.whisper));
                displayMessage("To " + selectedUser + "@" + whisperMess);
            }else{
                displayMessage("No Valid User Selected!");
            }
        }
    }

    public void setList(List<String> list) {
        for (String user : list) {
            model.addElement(user);
        }
    }

    private String getSelectedUsername() {
        String user = userList.getSelectedValue();

        return user;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(sendButton)) {
            if (!chat_message.getText().equals("")) {
                displayMessage(chat_message.getText());
                client.outgoingPackets(client.constructPacket(chat_message.getText(), Packet.pack_type.chat_message));
                chat_message.setText("");
            } else {
                chat_message.setText("");
            }
        } else if (e.getSource().equals(fileButton)) {
            // Needs to get a file to send
        } else if (e.getSource().equals(whisperButton)) {
            // Continue to work on Friday
            try {

                whisperClient();
            } catch (Exception ex) {
                // no user selected or none in list
            }
        } else if (e.getSource().equals(reportButton)) {

        } else if (e.getSource().equals(connectionButton)) {
            if (connectionButton.getText().equals(disconnText)) {
                connectionButton.setText(connText);
            } else if (connectionButton.getText().equals(connText)) {
                connectionButton.setText(disconnText);
            }
        }
    }
}
