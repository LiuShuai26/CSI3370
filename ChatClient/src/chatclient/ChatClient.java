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

    private static Socket client_socket;
    private static ServerThread serv_thread;
    private static String ip = "";
    protected static InetAddress serv_inet;

    public static void main(String[] args) throws Exception {
        String username = "";
        int port = 1234;
        try {
            while (username.equals("") || username.length() < 3 || username.length() > 15) {
                username = JOptionPane.showInputDialog("Please enter a username for the chat.");
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
        } catch (Exception e) {
            System.out.println(e.toString());
            JOptionPane warning = new JOptionPane("Oh No!!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
            JOptionPane.showMessageDialog(warning, "Make sure the IP address is correct and that the server is active!");
            System.exit(1000);
        }
    }

}
