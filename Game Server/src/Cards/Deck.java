package Cards;
/*

Deck

Creates the deck of cards using the Card.java class.
Uses an array of Cards for the deck.

Will create the deck in order at first and then randomize the deck.
This class can cylce through the deck and deal a card.

This Deck.java class will hold no other values than the array of Card.java
objects.

Methods:
CreateDeck:
Creates an array of Card.java objects.

RandomizeDeck:
Randomizes the array.

SendCard:
Returns the current card that would be at the top of the deck.

ResetDeck:
Resets the deck. Re-randomizes the deck.
*/

import java.util.Random;

public class Deck 
{
    private Card[] deck = new Card[52];
    
    private int index = 0;
    
    public Deck()
    {
        index = 0;
        CreateDeck();
        
        //DisplayDeck();
        
        ShuffleDeck();
    }
    
    private void CreateDeck()
    {
        char[] types = {'h','c','d','s'};
        
        int cardNum = 0;
        
        for (int i = 0; i < 4; i++)
        {
            for(int j = 2; j <= 14; j++)
            {
                deck[cardNum] = new Card(j, types[i]);
                
                cardNum++;
            }
        }
    }

    public Card GiveCard()
    {   
        Card c = deck[index];
        
        index++;
        
        return c;
    }
    
    private void Swap(int i, int j)
    {
        Card temp = deck[i];
        
        deck[i] = deck[j];
        
        deck[j] = temp;
    }
    
    private void ShuffleDeck()
    {
        int n = deck.length;
        index = 0;
        Random random = new Random();
        
        random.nextInt();
        for (int i = 0; i < n; i++)
        {
            int j = random.nextInt(n - 1);
            
            Swap(i, j);
        }
    }
    
    //Debug Output for checking what the deck contatins
    private void DisplayDeck()
    {
        System.out.println("Deck:");
        System.out.print("{");
        for(int i = 0; i < deck.length; i++)
        {
            if( i > 0)
            {
                System.out.print(", ");
                
                if ( i % 13 == 0)
                {
                    System.out.println();
                }
            }
            
            System.out.printf("%s%s - %d",  deck[i].GetName(), 
                                            deck[i].GetType(),
                                            deck[i].GetFace());
        }
        
        System.out.print("}\n\n");
    }
    
    public void ResetDeck()
    {
        ShuffleDeck();
    }
}

