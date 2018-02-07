package Cards;
/*
 Card

The Card.java class is a card object that holds the values 
and aspects of a typical card.

Values are:
face value
card type
card color

All this class will be is a set of set/get functions that are initialized
when the card is created.

Maybe have an overide of the ToString function to display
Card, Face, Type, and Color
 */


public class Card 
{
    private char    type,
                    color;
                    
            
    private String name;
    
    private int face;
    
    public Card(int f, char c)
    {
        if (f >= 10)
        {
            if (f == 14)
            {
                face = 1;
            }
            else
            {
                face = 10;
            }
        }
        else
        {
            face = f;
        }
        
        type = c;
        
        if(c == 'c' || c == 's')
        {
            color = 'b';
        }
        else
        {
            color = 'r';
        }
        
        switch (f)
        {
            case 11:
                name = "J";
                break;
            case 12:
                name = "Q";
                break;
            case 13:
                name = "K";
                break;
            case 14:
                name = "A";
                break;
            default:
                name = Integer.toString(f);
        }
    }
    
    public int GetFace()
    {
        return face;
    }
    
    public char GetType()
    {
        return type;
    }
    
    public char GetColor()
    {
        return color;
    }
    
    public String GetName()
    {
        return name;
    }
}
