/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HolyCode;

import gameclient.*;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Joe
 */
public class HolyCode implements Runnable {

    static String name;
    public String input = "_";
    public boolean playing = true;
    static String[] outcomes = {
        "You pull the Lever,and the floor opens up from underneath your feet, and you fall to your death ",
        "You died.....alone in a cave",
        "You put the key in the keyhole, and the door opens. infront of you is a long corridor that leads to freedom!"
    };
    static String[] story = {"You wake up in a dark room, and you can barely see. \nYou can 'look around', or 'call out for help' \n"
        + "What will you do?",
        "You look around, and see a see a pile of hay with something shiny in it, and you see a lever on the wall next to you. \nYou may 'search the pile' or 'pull the lever'",
        "Inside the pile, and you find a key would you like to put it in the keyhole? \n'yes' or 'no'",
        "You can 'pull the lever' or 'die of boredom' ",
        "You call out for help, and a man screams from the room next to you. He says 'you will never leave. I have been here for years!!'\nYou may 'look around' or 'die of boredom'",
        "You look around, and see a see a pile of hay with something shiny in it, and you see a lever on the wall next to you. \nYou may 'search the pile' or 'pull the lever'",
        "Inside the pile, and you find a key would you like to put it in the keyhole? \n'yes' or 'no'",
        "You can 'pull the lever' or 'die of boredom' "
    };
    static String[] options = {"look around", "call out for help", "search the pile", "pull the lever", "yes", "no", "pull the lever", "die of boredom",
        "look around", "die of boredom", "pull the lever", "search the pile", "yes", "no", "pull the lever", "die of boredom"

    };
    public ServerThread serv_thread;

    public HolyCode(ServerThread serv) throws IOException {
        Thread holy_thread = new Thread(this);
        holy_thread.start();
        serv_thread = serv;
    }

    public void hold() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
    }

    @Override
    public void run() {
        serv_thread.handle_gamme_mess("Welcome to THE QUEST FOR THE HOLY CODE    ");
        serv_thread.handle_gamme_mess("Enter your name: ");
        while (input.equals("_")) {
            try {
                input = serv_thread.get_input();
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
        name = input;
        input = "_";
        serv_thread.set_input("_");
        serv_thread.handle_gamme_mess("Welcome " + name + " lets start our journey together");
        try {
            input = serv_thread.get_input();
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        int count = 0;

        while (true) {

            serv_thread.handle_gamme_mess(story[count]);
            while (input.equals("_")) {
                try {
                    input = serv_thread.get_input();
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            }
            if (input.equals(options[2 * count])) {
                if (input.equals("yes")) {
                    serv_thread.handle_gamme_mess(outcomes[2]);
                    break;
                } else if (input.equals("pull the lever")) {
                    serv_thread.handle_gamme_mess(outcomes[0]);
                    break;
                } else if (input.equals("die of boredom")) {
                    serv_thread.handle_gamme_mess(outcomes[1]);
                    break;
                }
                count++;

            } else if (input.equals(options[2 * count + 1])) {
                if (input.equals("call out for help")) {
                    count = 4;
                } else if (input.equals("yes")) {
                    serv_thread.handle_gamme_mess(outcomes[2]);
                    break;
                } else if (input.equals("pull the lever")) {
                    serv_thread.handle_gamme_mess(outcomes[0]);
                    break;
                } else if (input.equals("die of boredom")) {
                    serv_thread.handle_gamme_mess(outcomes[1]);
                    break;
                } else {

                    count++;
                }

            } else {
                serv_thread.handle_gamme_mess("_clear_");
                serv_thread.handle_gamme_mess("please make a valid selection");
            }
            serv_thread.handle_gamme_mess("_clear_");
            input = "_";
            serv_thread.set_input("_");
        }
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
        hold();
        try {
            serv_thread.send_from_game("g999\n");
        } catch (Exception e) {

        }
    }
}
