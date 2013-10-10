/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Tethik;

import java.util.HashMap;


import sokoban.BoardPosition;
import sokoban.NodeType;
import sokoban.Direction;

/**
 *
 * @author figgefred
 */
public class Constants {
    
    private final static Direction[] Directions = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
    private final static String[] DirectionStringValue = { "U", "D", "L", "R" };
    
    private final static HashMap<String,Direction> StringToDirection = getStringToDirection();
    private final static HashMap<Direction, String> DirectionToString = getDirectionToString();
    
    public static Direction[] GetPossibleDirections()
    {
        return Directions;
    }
    
    private static HashMap<Direction, String> getDirectionToString()
    {
        HashMap<Direction, String> tmp = new HashMap<>();
        
        for(int i = 0; i < Directions.length; i++)
        {
            tmp.put(Directions[i], DirectionStringValue[i]);
        }
        return tmp;
    }
    
    private static HashMap<String, Direction> getStringToDirection()
    {
        HashMap<String, Direction> tmp = new HashMap<>();
        
        for(int i = 0; i < Directions.length; i++)
        {
            tmp.put(DirectionStringValue[i], Directions[i]);
        }
        return tmp;
    }
    
    public static String DirectionToString(Direction d)
    {
        return DirectionToString.get(d);
    }
    
    public static Direction StringToDirection(String s)
    {
        return StringToDirection.get(s);
    }
    
    
    
    
}
