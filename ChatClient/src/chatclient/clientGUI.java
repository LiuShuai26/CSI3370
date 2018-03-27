/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import Packet.Packet;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.print.attribute.standard.Severity;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author mike
 */
public class clientGUI extends JFrame 
{

    private ServerThread client;
    protected JTextArea chat_text, chat_message;
    protected JButton send_message;
    protected JLabel chat_lbl, spacer_lbl_recieve, spacer_lbl_send;
    protected JPanel Center, South;
    protected JScrollPane scroll_chat, scroll_send_message;

    public clientGUI(ServerThread client, String ip, int height, int width) 
    {
        this.client = client;
        Initialize(width, height);
        chat_text.append("Welcome to the Chat!\n");
        setTitle("Chat Client. Your Ip is: " + ip);
    }

    public void Initialize(int width, int height) 
    {
        DefaultCaret caret_chat_wim;
        // create the window and its properties
        setSize(width, height);
        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // defining the different compnents of the JFrame
        //Text areas and scrolling capability
        chat_text = new JTextArea(20, 33);
        chat_text.setEditable(false);
        chat_text.setLineWrap(true);
        caret_chat_wim = (DefaultCaret) chat_text.getCaret();
        caret_chat_wim.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scroll_chat = new JScrollPane(chat_text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chat_message = new JTextArea(3, 27);
        chat_message.addKeyListener(new KeyListener() 
        {
            @Override
            public void keyTyped(KeyEvent e) 
            {
            }

            @Override
            public void keyPressed(KeyEvent e) 
            {
                try 
                {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) 
                    {
                        if (!chat_message.getText().equals("")) 
                        {
                            e.consume();
                            displayMessage(chat_message.getText());
                            client.outgoingPackets(client.constructPacket(chat_message.getText(), Packet.pack_type.chat_message));
                            chat_message.setText("");
                        } 
                        else 
                        {
                            e.consume();
                            chat_message.setText("");
                        }
                    }
                } 
                catch (Exception er) 
                {

                }
            }

            @Override
            public void keyReleased(KeyEvent e) 
            {
            }
        });
        
        scroll_send_message = new JScrollPane(chat_message, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chat_message.setLineWrap(true);
        
        // Spacers
        spacer_lbl_recieve = new JLabel("        ");
        spacer_lbl_send = new JLabel("         ");
        
        // adding buttons
        send_message = new JButton("Send");
        
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
        if (chat_message.isFocusOwner()) 
        {
            this.getRootPane().setDefaultButton(send_message);
        }
        
        ActionListener Click = new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                String message;
                if (e.getSource().equals(send_message)) 
                {
                    if (!chat_message.getText().equals("")) 
                    {
                        displayMessage(chat_message.getText());
                        client.outgoingPackets(client.constructPacket(chat_message.getText(), Packet.pack_type.chat_message));
                        chat_message.setText("");
                    } 
                    else 
                    {
                        chat_message.setText("");
                    }
                }
            }
        };
    }

    public void displayMessage(String message) 
    {
        chat_text.append(message + "\n");
    }
}
