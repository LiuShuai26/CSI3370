/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameclient;

import Arena.TwentyOneSticks;
import HolyCode.HolyCode;
import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import Snake.*;

/**
 *
 * @author mike
 */
public class ServerThread extends JFrame implements Runnable, ActionListener {

    // variables for the thread
    private Thread server_thread;
    private DataOutputStream to_server;
    private Socket serv_socket;
    private BufferedReader from_server;
    private int port;
    public HolyCode holy;
    public TwentyOneSticks arena;
    protected InetAddress serv_ip;
    protected String[] cli_ip;
    protected String my_ip;
    String curr_game = "", game_input = "_";
    protected JOptionPane enter_IP;
    // variables for the GUI
    public String default_game = "Please type 'Start' to select a game: \n";
    protected JTextArea game_text, chat_text, chat_message, game_command;
    protected JButton send_command, send_message;
    protected JLabel game_lbl, chat_lbl, spacer_lbl_recieve, spacer_lbl_send;
    protected JPanel Center, South;
    protected JScrollPane scroll_chat, scroll_game, scroll_send_command, scroll_send_message;

    public ServerThread(Socket sock, String user_nm, InetAddress ip_addr, int port) throws IOException {
        cli_ip = InetAddress.getLocalHost().toString().split("/");
        my_ip = cli_ip[1];
        this.port = port;
        serv_ip = ip_addr;
        server_thread = new Thread(this);
        serv_socket = sock;
        server_thread.start();
        ChatGui(900, 450, "Game and Chat Hub");
        game_text.append(default_game);
    }

    public void ChatGui(int width, int height, String title) {
        DefaultCaret caret_chat_wim;
        // create the window and its properties
        this.setSize(width, height);
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.setTitle("Game and Chat Hub. Your Ip is: " + my_ip);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // defining the different compnents of the JFrame
        //Text areas and scrolling capability
        game_text = new JTextArea(20, 42);
        game_text.setEditable(false);
        game_text.setLineWrap(true);
        scroll_game = new JScrollPane(game_text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
                            send_message_func();
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
        game_command = new JTextArea(3, 35);
        scroll_send_command = new JScrollPane(game_command, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        game_command.setLineWrap(true);
        game_command.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (!game_command.getText().equals("")) {
                            e.consume();
                            send_command_func();
                        } else {
                            e.consume();
                            game_command.setText("");
                        }
                    }
                } catch (Exception er) {

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        // Spacers
        spacer_lbl_recieve = new JLabel("        ");
        spacer_lbl_send = new JLabel("         ");
        // adding buttons
        send_command = new JButton("Send");
        send_message = new JButton("Send");
        send_command.addActionListener(this);
        send_message.addActionListener(this);
        // Jpanels needed
        South = new JPanel();
        Center = new JPanel();
        // adding components to the Jpanels
        // North components
        Center.add(scroll_game);
        Center.add(spacer_lbl_recieve);
        Center.add(scroll_chat);
        // South panel components
        South.add(send_command);
        South.add(scroll_send_command); // Jscrolling for the game command text area
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
        } else if (game_command.isFocusOwner()) {
            this.getRootPane().setDefaultButton(send_command);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message;
        if (e.getSource().equals(send_command)) {
            if (!game_command.getText().equals("")) {
                send_command_func();
            } else {
                game_command.setText("");
            }
        } else if (e.getSource().equals(send_message)) {
            if (!chat_message.getText().equals("")) {
                send_message_func();
            } else {
                chat_message.setText("");
            }
        }
    }

    public void send_message_func() {
        String message;
        //send_data = new byte[1500];
        try {
            message = chat_message.getText();
            to_server.writeBytes("c" + message + "\n");
            chat_text.append(message + "\n");
            chat_message.setText("");
        } catch (Exception e) {
            JOptionPane warning = new JOptionPane("Oh No!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            JOptionPane.showMessageDialog(warning, "The server has closed. Please Reconnect!");
            System.exit(3000);
        }
    }

    public String get_input() {
        return game_input;
    }

    public void set_input(String in) {
        in.trim();
        game_input = in;
    }

    public void set_game(String game) {
        curr_game = "";
    }
    public void send_from_game(String msg) throws IOException{
        to_server.writeBytes(msg);
    }

    public void send_command_func() {
        String command;
        try {
            command = game_command.getText();
            game_text.append(command + "\n");
            if (curr_game.equals("holy")) {
                game_input = command;
            } else {
                to_server.writeBytes("g" + command + "\n");
            }
            game_command.setText("");
        } catch (Exception er) {
            game_text.append(er.toString());
        }
    }

    private void kicked() throws IOException {
        this.setVisible(false);
        serv_socket.close();
        JOptionPane warning = new JOptionPane("You Have been kicked from the server!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
        JOptionPane.showMessageDialog(warning, "You have been kicked");
        System.exit(-1);
    }

    public void handle_gamme_mess(String msg) {
        try {
            switch (msg) {
                case "play_snake":
                    SnakeFrame snake = new SnakeFrame();
                    break;
                case "_reset_":
                    game_text.setText(default_game);
                    break;
                case "_clear_":
                    game_text.setText("");
                    break;
                case "holy":
                    curr_game = "holy";
                    holy = new HolyCode(this);
                    break;
                case "sticks":
                    curr_game = "sticks";
                    arena = new TwentyOneSticks(this);
                default:
                    game_text.append(msg + "\n");
                    break;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void run() {
        String s_mess = "";

        try { // Send your usern ame to the Server to store it 
            from_server = new BufferedReader(new InputStreamReader(serv_socket.getInputStream()));
            to_server = new DataOutputStream((serv_socket.getOutputStream()));
        } catch (Exception ei) {
            System.out.println("Not wroking");
        }

        while (true) {
            try { // Get the messages from the server or from other users

                s_mess = from_server.readLine();
                if (s_mess == null) {
                    kicked();
                }
                if (s_mess.charAt(0) == 'c') {
                    chat_text.append(s_mess.substring(1) + "\n");
                } else if (s_mess.charAt(0) == 'k') {
                    kicked();
                } else if (s_mess.charAt(0) == 'g') {
                    handle_gamme_mess(s_mess.substring(1));
                }
            } catch (Exception e) {
                JOptionPane warning = new JOptionPane("Oh No!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
                JOptionPane.showMessageDialog(warning, "The server has closed. Please Reconnect!");
                System.exit(3000);
            }
        }

    }
}
