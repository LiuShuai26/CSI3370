/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Snake;

//import gameclient.snakeentities.Apple;
//import gameclient.snakeentities.SnakeBody;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import java.util.logging.Logger;
import javax.swing.JPanel;
import SnakeEnts.*;

/**
 *
 * @author steve
 */
public class Screen extends JPanel implements Runnable {

    public static final int WIDTH = 800, HEIGHT = 800;
    private Thread thread;
    private boolean running = false;

    private Snakebody b;
    private ArrayList<Snakebody> snake;

    private Apple apple;
    private ArrayList<Apple> apples;
    
    private SnakeFrame snake_frame;
    private Random r;

    private int xCoor = 10, yCoor = 10;
    private int size = 5;

    private boolean right = true, left = false, up = false, down = false;

    private int ticks = 0;

    private Key key;

    public Screen(SnakeFrame sf) {
        snake_frame = sf;
        setFocusable(true);
        key = new Key();
        addKeyListener(key);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        r = new Random();

        snake = new ArrayList<Snakebody>();
        apples = new ArrayList<Apple>();

        start();
    }

    public void tick() {
        if (snake.size() == 0) {
            b = new Snakebody(xCoor, yCoor, 10);
            snake.add(b);
        }

        if (apples.size() == 0) {
            int xCoor = r.nextInt(79);
            int yCoor = r.nextInt(79);

            apple = new Apple(xCoor, yCoor, 10);
            apples.add(apple);
        }

        for (int i = 0; i < apples.size(); i++) {
            if (xCoor == apples.get(i).getxCoor() && yCoor == apples.get(i).getyCoor()) {
                size++;
                apples.remove(i);
                i--;
            }
        }

        //stops the game when collision occurs
        for (int i = 0; i < snake.size(); i++) {
            if (xCoor == snake.get(i).getxCoor() && yCoor == snake.get(i).getyCoor()) {
                if (i != snake.size() - 1) {
                    snake_frame.stop();
                    running = false;
                    break;
                }
            }
        }

        if (xCoor < 0 || xCoor > 79 || yCoor < 0 || yCoor > 79) {
            snake_frame.stop();
            running = false;
        }

        ticks++;

        if (ticks > 300000) {
            if (right) {
                xCoor++;
            }
            if (left) {
                xCoor--;
            }
            if (up) {
                yCoor--;
            }
            if (down) {
                yCoor++;
            }

            ticks = 0;

            b = new Snakebody(xCoor, yCoor, 10);
            snake.add(b);

            if (snake.size() > size) {
                snake.remove(0);
            }
        }

    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.BLACK);
        for (int i = 0; i < WIDTH / 10; i++) {
            g.drawLine(i * 10, 0, i * 10, HEIGHT);
        }

        for (int i = 0; i < HEIGHT / 10; i++) {
            g.drawLine(0, i * 10, WIDTH, i * 10);
        }

        for (int i = 0; i < snake.size(); i++) {
            snake.get(i).draw(g);
        }
        for (int i = 0; i < apples.size(); i++) {
            apples.get(i).draw(g);
        }

    }

    public void start() {
        running = true;
        thread = new Thread(this, "Game Loop");
        thread.start();
    }


    public void run() {
        while (running) {
            tick();
            repaint();
        }
    }

    //This whole class involves the user input of the arrow keys. 
    //moves the snake.
    private class Key implements KeyListener {

        @Override
        public void keyPressed(KeyEvent ke) {
            int key = ke.getKeyCode();

            //right arrow key
            if (key == KeyEvent.VK_RIGHT && !left) {
                up = false;
                down = false;
                right = true;
            }

            //left arrow key
            if (key == KeyEvent.VK_LEFT && !right) {
                up = false;
                down = false;
                left = true;
            }

            //up arrow key
            if (key == KeyEvent.VK_UP && !down) {
                right = false;
                left = false;
                up = true;
            }

            //down arrow key
            if (key == KeyEvent.VK_DOWN && !up) {
                right = false;
                left = false;
                down = true;
            }

        }

        @Override
        public void keyTyped(KeyEvent ke) {

        }

        @Override
        public void keyReleased(KeyEvent ke) {

        }

    }

}
