package Cards;

/*
Game

Handles the entire game using every class available.

Handles counting the score and checks the slots to 
determine whether the game has finished.

Will have a bonus round if all the slots are solid.

The bonus round will clear the slots, randomize the deck again,
and place a single card in each slot.

There will also be a count of power chips that will
allow the user to skip the current card.

Values:
Card crrntCard

int powerChips

int score

boolean bonus

boolean endGame

Methods:
DisplayGame:
Introduces the game and how it is played.

DisplayBonus:
Introduce the Bonus Round and how it works

CheckSlots:
Checks each of the slots statuses and sets the values of 
endGame to true if slots are at 21 and higher. If each slot is at 21, 
sets bonus to true

AddScore:
Adds 10 points for each card used

AddBonus:
Adds extra points to the score based on the status of the slots
and what is in them.

RestartGame:
Resets the game

EndGame:
Display a Good-Bye and terminate the program.
 */
import java.util.Scanner;
import java.util.*;
import java.util.InputMismatchException;
import gameserver.*;
import java.io.IOException;

public class Cards_Main implements Runnable {

     Scanner input = new Scanner(System.in);
    public GameThread game_thread;
     Slot[] slots;

     Deck deck;

     int score = 0,
            powerChips = 3;

     boolean quit;

    // Output to user
    public void DisplayIntro() throws IOException {
        game_thread.send_game_message("Stockpile 21!\ngIn this game you will be given a card and need to place it in"
                + " any of the slots available.\ngYou need to make it so each slot has a value of 21. BlackJack Rules!\n"
                + "gYou have Power Chips that you can use to skip a card, but they are limited\n"
                + "gYou get a bonus for using the same color/suit that a slot started with in a row(beginning to end)"
                + "\ng\ngSCORING"
                + "\ng10/card no mater what"
                + "\ngSame Color: 25/card\tSame Suit: 50/card"
                + "\ng150 for every slot at 21"
                + "\ng\ngType 'start' to Start Playing\n");

        try {
            // Get input from user, any input that is sent
            while (!game_thread.get_from_client().toLowerCase().equals("start")) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {

        }

        // Clear input cache
    }

    // Output to user
    public void DisplayGame(Card c) throws IOException {
        String game_str = "";
        game_thread.send_game_message("_clear_\n");
        game_thread.send_game_message("Current Card: " + c.GetName() + c.GetType() + "\n");

        game_thread.send_game_message("# of Power Chips: " + powerChips + "\n");

        for (int i = 0; i < slots.length; i++) {
            game_str = "";
            game_str = game_str + ("Slot " + (i + 1) + ": " + slots[i].GetTotal());

            if (slots[i].HasAce()) {
                game_str = game_str + (" / " + slots[i].GetAceTotal());
            }

            if (slots[i].CheckSolid()) {
                game_thread.send_game_message("solid\n");
            } else if (slots[i].CheckBroken()) {
                game_thread.send_game_message("broken\n");
            } else {
                game_thread.send_game_message(game_str + "\n");
            }
            slots[i].DisplaySlot();
        }
    }

    public Cards_Main(GameThread game) throws IOException {
        game_thread = game;
        Thread cards_thread = new Thread(this);
        cards_thread.start();
    }

    // Output to user
    public int DisplayScore() throws IOException {
        int[] scores = new int[slots.length];

        int total = 0;

        for (int i = 0; i < scores.length; i++) {
            scores[i] = slots[i].CalcScore();

            total += scores[i];
        }

        game_thread.send_game_message("\n");

        for (int j = 0; j < scores.length; j++) {
            game_thread.send_game_message("Slot " + (j + 1) + " Bonus: " + scores[j] + "\t");

            if (j % 3 == 0 && j != 0) {
                game_thread.send_game_message("\n");
            }
        }

        game_thread.send_game_message("\nTotal:\t" + total);

        return total;
    }

    // Output to user
    public void DisplayBonus() throws IOException {
        game_thread.send_game_message("BONUS ROUND!!!\ngNow you will have to deal with a random card in each slot.\n"
                + "gSame rules apply, but there are no Power Chips available!\ng\ngPress Enter to Start the Bonus Round\n");

        try {
            // Get input from user, can be any input
            while (!game_thread.get_from_client().toLowerCase().equals("start")) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {

        }
    }

    // Output to user
    public void DisplayOptions() throws IOException {
        game_thread.send_game_message("\ngOPTIONS:\ng0:\tEnd Game\ng1:\tAdd Card to a Slot\ng2:\tUse Power Chip"
                + "\ng\ngWhat do you want to do?(Enter a Number from above)\n");
    }

    // Output to user
    public  void DisplaySlotQ() throws IOException {
        game_thread.send_game_message("Which slot do you want to add the card to? (1-" + (slots.length) + ")\n");
    }

    // Output to user
    public  void DisplayAceQ() throws IOException {
        game_thread.send_game_message("The slot added is now solid based only on ACE rules.\n"
                + "gDo you want to make it officially solid as it is? (Y/N)\n"
                + "gDoing so will make it so no more cards can be added to that slot.\n");
    }

    // Output to user
    public  void DisplayReplayQ() throws IOException {
        game_thread.send_game_message("Do you want to play again? (Y/N)\n");
    }

    public  boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public  int GetAnOption() throws IOException {
        int ans = -2;
        while (ans == -2) {
            try {
                try {
                    while (game_thread.get_from_client().equals("_")) {
                        Thread.sleep(1000);
                    }
                    ans = Integer.parseInt(game_thread.get_from_client());
                    game_thread.in_message("_");
                } catch (Exception er) {

                }
            } catch (InputMismatchException e) {
                // Output to user
                game_thread.send_game_message("Please, input a single NUMBER from the list. (0-2)\n");

                ans = -2;
            }

            if (ans < 0 || ans > 2) {
                // Output to user
                game_thread.send_game_message("Please, input a single NUMBER from the list. (0-2)\n");
                game_thread.in_message("_");
                ans = -2;
            } else if (ans == 2) {
                if (powerChips == 0) {
                    // Output to user
                    game_thread.send_game_message("There are no more Power Chips availabe. You have to use this card.\n"
                            + "gChoose one of the other options. (0-1)\n");

                    ans = -2;
                }
            }
        }

        return ans;
    }

    public  int GetASlot() throws IOException {
        int ans = -2;

        while (ans == -2) {
            try {
                try {
                    while (game_thread.get_from_client().equals("_")) {
                        Thread.sleep(1000);
                    }
                    ans = Integer.parseInt(game_thread.get_from_client());
                    game_thread.in_message("_");
                } catch (Exception er) {
                    game_thread.in_message("_");
                }
            } catch (InputMismatchException e) {
                // Output to user
                game_thread.send_game_message("Please, input a single NUMBER from the avaibable slots. " + (slots.length - 1) + "\n");
                ans = -2;
            }
            if (ans < 1 || ans > slots.length) {
                // Output to user
                game_thread.send_game_message("Please, input a single NUMBER from the avaibable slots. " + (slots.length - 1) + "\n");
                game_thread.in_message("a");
                ans = -2;
            } else if (slots[ans - 1].CheckSolid() || slots[ans - 1].CheckBroken()) {
                // Output to user
                game_thread.send_game_message("Sorry, that slot can no longer accept cards.\n"
                        + "gYou will have to pick a different slot.\n");

                ans = -2;
            }
        }

        // Clear input cache
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }

        return ans;
    }

    public  char GetAceQ() throws IOException {
        String ans = null;

        while (ans == null) {
            try {
                try {
                    while (game_thread.get_from_client().equals("_")) {
                        Thread.sleep(1000);
                    }
                    ans = game_thread.get_from_client();
                    game_thread.in_message("_");
                } catch (Exception er) {

                }
            } catch (InputMismatchException e) {
                // Output to user
                game_thread.send_game_message("Please, input a string or character for Yes or No. (Y/N)\n");

                ans = null;
            }

            if (!ans.equals("Y") && !ans.equals("y") && !ans.equals("N") && !ans.equals("n") && !ans.equals("Yes")
                    && !ans.equals("yes") && !ans.equals("No") && !ans.equals("no") && !ans.equals("n") && !ans.equals("y")) {
                // Output to user
                game_thread.send_game_message("Only these are acceptable inputs: Yes, yes, Y, y, No, no, N, n\n");

                ans = null;
            }
        }

        // Clear input cache
        if (ans.equals("Y") || ans.equals("y") || ans.equals("yes") || ans.equals("Yes")) {
            return 'Y';
        } else {
            return 'N';
        }
    }

    public  char GetReplay() throws IOException {
        String ans = null;

        while (ans == null) {
            try {
                try {
                    while (!game_thread.get_from_client().equals("_")) {
                        Thread.sleep(1000);
                    }
                    ans = game_thread.get_from_client();
                    game_thread.in_message("_");
                } catch (Exception er) {

                }
            } catch (InputMismatchException e) {
                // Output to user
                game_thread.send_game_message("Please, input a string or character for Yes or No. (Y/N)\n");

                ans = null;
            }

            if (!ans.equals("Y") && !ans.equals("y") && !ans.equals("N") && !ans.equals("n") && !ans.equals("Yes")
                    && !ans.equals("yes") && !ans.equals("No") && !ans.equals("no") && !ans.equals("n") && !ans.equals("y")) {
                // Output to user
                game_thread.send_game_message("Only these are acceptable inputs: Yes, yes, Y, y, No, no, N, n\n");

                ans = null;
            }
        }

        // Clear input cache
        if (ans.equals("Y") || ans.equals("y") || ans.equals("yes") || ans.equals("Yes")) {
            return 'Y';
        } else {
            return 'N';
        }
    }

    public  void ResetGame() {
        powerChips = 3;

        deck.ResetDeck();

        quit = false;

        for (int j = 0; j < slots.length; j++) {
            slots[j].EmptySlot();
        }
    }

    public  void StartBonus() {
        Card card;

        ResetGame();

        powerChips = 0;

        for (int i = 0; i < slots.length; i++) {
            card = deck.GiveCard();

            slots[i].AddCard(card);
        }
    }

    public  boolean PlayGame() throws IOException {
        Card current;
        int answer;
        char aceAns;

        boolean gameOver = false;

        current = deck.GiveCard();

        // Call Function to send output to the user
        DisplayGame(current);

        // Call Function to send output to the user
        DisplayOptions();

        // Call Function to send output to the user and get input
        answer = GetAnOption();

        if (answer == 1) {
            // Call Function to send output to the user
            DisplaySlotQ();

            // Call Function to send output to the user and get input
            answer = GetASlot();

            slots[answer - 1].AddCard(current);

            if (slots[answer - 1].CheckAceSolid()) {
                // Call Function to send output to the user and get input
                DisplayAceQ();
                aceAns = GetAceQ();

                if (aceAns == 'Y') {
                    slots[answer - 1].MakeSolid();
                }
            }
        } else if (answer == 2) {
            powerChips--;
        } else {
            // Output to user
            game_thread.send_game_message("\ngQuiting Game.\n");
            quit = true;
            return true;
        }

        for (int i = 0; i < slots.length; i++) {
            if (slots[i].CheckSolid() || slots[i].CheckBroken()) {
                gameOver = true;
            } else {
                return false;
            }
        }

        return gameOver;
    }

    @Override
    public void run() {
        boolean bonus;
        quit = false;
        deck = new Deck();

        slots = new Slot[3];

        Card crrnt;

        char ans = 'Y';
        try {
            DisplayIntro();

            while (ans == 'Y' && !quit) {
                bonus = true;
                score = 0;

                for (int i = 0; i < slots.length; i++) {
                    slots[i] = new Slot(game_thread);
                }

                while (!PlayGame()) {

                }

                if (!quit) {
                    crrnt = deck.GiveCard();

                    DisplayGame(crrnt);

                    score += DisplayScore();

                    for (int i = 0; i < slots.length; i++) {
                        if (!slots[i].CheckSolid()) {
                            bonus = false;
                            break;
                        }
                    }

                    if (bonus) {
                        StartBonus();

                        DisplayBonus();

                        while (!PlayGame()) {

                        }

                        crrnt = deck.GiveCard();

                        DisplayGame(crrnt);

                        score += DisplayScore();
                    }

                    // Output to user
                    game_thread.send_game_message("FINAL SCORE: " + score + "\ng\n");

                    DisplayReplayQ();
                    ans = GetReplay();

                    if (ans == 'Y') {
                        ResetGame();
                    }
                }
            }

            // Output to user
            game_thread.send_game_message("\ngTHANK YOU FOR PLAYING\ng\n");
            game_thread.send_game_message("Sending you back to the game menu!\n");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {

            }
            game_thread.send_game_message("_clear_\n");
            game_thread.send_game_message(game_thread.games);
            game_thread.set_state("select");
        } catch (Exception e) {

        }

    }
}
