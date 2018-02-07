/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Arena;

import java.util.Scanner;
import gameclient.*;

/**
 *
 * @author ryman
 */
public class TwentyOneSticks {

    /**
     * @param args the command line arguments
     */
    public String input = "_";

    public TwentyOneSticks(ServerThread serv_thread) {
        int numSticks = 21;
        serv_thread.handle_gamme_mess("Would you like to be first? (y/n)");
        int numToTake = 0;

        while (input.equals("_")) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
            while (numSticks > 0) {
                if (input.equals("y") || input.equals("Y")) {
                    serv_thread.handle_gamme_mess("There are " + numSticks + " stick(s).");
                    serv_thread.handle_gamme_mess("How many sticks do you want to take? (1 or 2)");
                    while (input.equals("_")) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {

                        }
                    }
                    numToTake = Integer.parseInt(input);

                    if (numToTake > 2) {
                        numToTake = 2;
                    } else if (numToTake < 1) {
                        numToTake = 1;
                    }
                    numSticks = numSticks - numToTake;

                    if (numSticks <= 0) {
                        serv_thread.handle_gamme_mess("You Loose!! :( Thanks For Playing!");
                    } else {

                        if ((numSticks - 2) % 3 == 0 || numSticks - 2 == 0) {
                            numToTake = 1;
                        } else {
                            numToTake = 2;
                        }
                        serv_thread.handle_gamme_mess("Computer takes " + numToTake + " stick(s).");
                        numSticks = numSticks - numToTake;

                        if (numSticks <= 0) {
                            serv_thread.handle_gamme_mess("You Win!! Thanks For Playing!");
                        }
                    }
                } else {

                    if ((numSticks - 2) % 3 == 0 || numSticks - 2 == 0) {
                        numToTake = 1;
                    } else {
                        numToTake = 2;
                    }
                    serv_thread.handle_gamme_mess("Computer takes " + numToTake + " stick(s).");
                    numSticks = numSticks - numToTake;

                    if (numSticks <= 0) {
                        serv_thread.handle_gamme_mess("You Win!! Thanks For Playing!");
                    } else {
                        serv_thread.handle_gamme_mess("There are " + numSticks + " stick(s).");
                        serv_thread.handle_gamme_mess("How many sticks do you want to take? (1 or 2)");
                        numToTake = Integer.parseInt(input);

                        if (numToTake > 2) {
                            numToTake = 2;
                        } else if (numToTake < 1) {
                            numToTake = 1;
                        }
                        numSticks = numSticks - numToTake;

                        if (numSticks <= 0) {
                            serv_thread.handle_gamme_mess("You Lose!! :( Thanks For Playing!");
                        }
                    }
                }
            }

        }
    }
}
