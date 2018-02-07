/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Snake;

/**
 *
 * @author steve
 */
import javax.swing.JFrame;
import Snake.Screen;

import java.awt.GridLayout;
import javax.swing.JOptionPane;

public class SnakeFrame extends JFrame{
    
    public SnakeFrame() {
        setTitle("Snake Game");
        setResizable(false);
        
        init();
    }
    
    public void stop() {
       JOptionPane warning = new JOptionPane("Game over!", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION);
        JOptionPane.showMessageDialog(warning, "Game over!");
        
        this.dispose();
    }
    public void init() {
        setLayout(new GridLayout(1, 1, 0, 0));
        
        Screen s = new Screen(this);
        add(s);
        
        pack();
        
        setLocationRelativeTo(null);
        setVisible(true);
        
        
    }
    
    public static void main(String[] args) {
        new SnakeFrame();
    }
    
}
