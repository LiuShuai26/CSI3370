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
    
    public bannedMacs(){
        // Instatiation
    }
    
    public boolean checkMAC(String MAC){
        for(String mac : Banned){
            if (MAC.equals(mac)){
                return true;
            }
        }
        return false;
    }
    public void banMAC(String MAC){
        Banned.add(MAC);
    }
}
