package Cards;
import java.util.LinkedList;

public class Scoring 
{
    public int TypeBonus(char s, LinkedList<Card> stack)
    {
        int bonus = 50;
        
        int count = 0;
        
        for (int i = 1; i < stack.size(); i++)
        {
            if (stack.get(i).GetType() == s)
            {
                count += 1;
            }
            else
            {
                break;
            }
        }
        
        return (bonus * count);
    }
    
    public int ColorBonus(char c, LinkedList<Card> stack)
    {
        int bonus = 25;
        
        int count = 0;
        
        for (int i = 1; i < stack.size(); i++)
        {
            if (stack.get(i).GetColor() == c)
            {
                count += 1;
            }
            else
            {
                break;
            }
        }
        
        return (bonus * count);
    }
    
    public int CardBonus(LinkedList<Card> stack)
    {
        return (10 * stack.size());
    }
    
    public int TotalBonus(int c, int t, int C, int s)
    {
        return (t + c + C + s);
    }
}
