/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;


import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import javax.swing.event.*;
/**
 *
 * @author mike
 */
public class bannedMacs {
    private List<String> Banned = new ArrayList<String>();
    private Socket cliSock;
    public bannedMacs(){
        // Instatiation
    }
    
    public boolean isMacBanned(String MAC){
        for(String mac : Banned){
            if (MAC.equals(mac)){
                return true;
            }
        }
        return false;
    }
    public void banMAC(Socket cliSocket) throws IOException{
        Banned.add(pullMac(cliSocket));
    }
    public String pullMac(Socket cliSocket) throws IOException{
//        InetAddress inet = cliSocket.getInetAddress();
//        NetworkInterface network = NetworkInterface.getByInetAddress(inet);
//        byte[] mac = network.getHardwareAddress();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < mac.length; i++) {
//            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
//        }
        return null;
    }
}
