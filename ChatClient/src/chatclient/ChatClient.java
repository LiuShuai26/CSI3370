/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import javax.swing.*;
import java.net.*;
import java.io.*;
import Packet.Packet;

class ChatClient {

    private Socket client_socket;
    private ServerThread serv_thread;
    private String ip = "";
    protected InetAddress serv_inet;

    public ChatClient() throws Exception {
        String username = "";
        int port = 1234;
        try {
            while (true) {
                username = JOptionPane.showInputDialog("Please enter a username for the chat.(3-15 Characters with only letters and numbers)");
                if (username.equals("") || username.length() < 3 || username.length() > 15 || !username.matches("[a-zA-Z0-9]*")) {
                    JOptionPane warning = new JOptionPane("Oh No!!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
                    JOptionPane.showMessageDialog(warning, "Make sure your username is valid and doesn't contain '*' characters!");
                }else{
                    break;
                }
            }
            while (ip.equals("")) {
                ip = JOptionPane.showInputDialog("Please enter the IP Address of the Server you wish to connect to.");
                try {
                    serv_inet = InetAddress.getByName(ip);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
            client_socket = new Socket(ip, 1234);
            // Send packet with username
            serv_thread = new ServerThread(client_socket, username, serv_inet, port);
        } catch (ConnectException e) {
            System.out.println(e.toString());
            JOptionPane warning = new JOptionPane("Oh No!!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            JOptionPane.showMessageDialog(warning, "You couldn't connect! The server might be down!");
            System.exit(1000);
        } catch (SocketException se) {
            JOptionPane warning = new JOptionPane("Oh No!!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            JOptionPane.showMessageDialog(warning, "You've entered an invalid IP !");
            System.exit(1000);
        } catch (EOFException eofe) {
            JOptionPane warning = new JOptionPane("Oh No!!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            JOptionPane.showMessageDialog(warning, "You are currently banned from the server.");
            System.exit(1000);
        }
    }

}
