package Cards;

/*
Slot

A single slot that takes a received Card.java object
adds its face value to its overall count and checks for whether
the state of the slot is okay, solid, or broken(depends on Black Jack
rules)

The slot can take a card and add its value to its total count. It
will need to check for an Ace being added and have an alternate count
that uses the Ace value and is a separate check.

Functions:
addCard

checkStatus

Variables:
boolean solid

boolean ace_Solid

boolean broken

LinkedList cards

int total       will use the 1 point Ace

int ace_Total   will use the 11 point Ace
 */
import java.util.LinkedList;
import gameserver.*;
import java.io.IOException;

public class Slot {

    private boolean solid,
            ace_Solid,
            broken,
            ace;
    public String game_string = "";
    protected GameThread game_thread;

    private int total,
            ace_Total;

    private LinkedList<Card> cards;

    private char color, type;

    private Scoring score = new Scoring();

    public Slot(GameThread game) {
        total = 0;
        ace_Total = 0;
        this.game_thread = game;
        solid = false;
        ace_Solid = false;

        broken = false;

        ace = false;

        cards = new LinkedList<Card>();
    }

    public void AddCard(Card c) {
        if (cards.isEmpty()) {
            color = c.GetColor();
            type = c.GetType();
        }

        cards.add(c);

        total += c.GetFace();

        if ("A".equals(c.GetName())) {
            ace = true;
            ace_Total += 11;
        } else {
            ace_Total += c.GetFace();
        }

        if (total > 21) {
            broken = true;
        } else if (total == 21) {
            solid = true;
        }

        if (ace_Total == 21 && ace_Total != total) {
            ace_Solid = true;
        } else {
            ace_Solid = false;
        }
    }

    public boolean CheckBroken() {
        if (broken) {
            return broken;
        } else {
            return false;
        }
    }

    public boolean CheckSolid() {
        if (solid) {
            return solid;
        } else {
            return false;
        }
    }

    public boolean CheckAceSolid() {
        if (ace_Solid) {
            return true;
        } else {
            return false;
        }
    }

    public int GetTotal() {
        return total;
    }

    public int GetAceTotal() {
        return ace_Total;
    }

    public boolean HasAce() {
        return ace;
    }

    public void MakeSolid() {
        solid = true;
    }

    public void EmptySlot() {
        cards.clear();

        total = 0;
        ace_Total = 0;

        solid = false;
        ace_Solid = false;

        broken = false;

        ace = false;
    }

    public int CalcScore() {
        if (solid) {
            return score.TotalBonus(score.CardBonus(cards), score.TypeBonus(type, cards), score.ColorBonus(color, cards),
                    150);
        }
        return score.TotalBonus(score.CardBonus(cards), score.TypeBonus(type, cards), score.ColorBonus(color, cards),
                0);

    }

    // Output to the user
    public void DisplaySlot() throws IOException {
        Card c;
        int i;
        String cards_str = "";
        for (i = 0; i < cards.size(); i++) {
            //Can't do this for the actual game, removes the element from the list
            c = cards.get(i);
            if (cards.size() == 1) {
                game_thread.send_game_message(c.GetName() + c.GetType() + "\n");
            } else {
                cards_str = cards_str + (c.GetName() + c.GetType()) + ", ";
            }
        }
        if (i >= 2) {
            System.out.println(cards_str.substring(0, cards_str.length() - 2));
            game_thread.send_game_message(cards_str.substring(0, cards_str.length() - 2) + "\n");
        }
    }
}
