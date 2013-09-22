/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import java.util.Collections;
import java.util.List;
import sokoban.types.Direction;

/**
 * A class holding a list of Nodes representing a path.
 * 
 * @author figgefred
 */
public class Path {
    private List<BoardPosition> Nodes;
    private String StringOutput;
    
    public Path(List<BoardPosition> nodes)
    {
        this(nodes, false);
    }
    
    public Path(List<BoardPosition> nodes, boolean reversedList)
    {
        this.Nodes = nodes;
        if(reversedList)
            Collections.reverse(Nodes);
    }
    
    public BoardPosition get(int index)
    {
        if(index >= Nodes.size() || index < 0)
        {
            return null;
        }
        return Nodes.get(index);
    }
    
    public List<BoardPosition> getPath()
    {
        return Nodes;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        BoardPosition firstNode = null;
        
        for(BoardPosition n: Nodes)
        {
            if(firstNode == null)
            {
                firstNode = n;
                continue;
            }
            sb.append(Constants.DirectionToString(getDirection(firstNode, n)));
            
            sb.append(" ");
            firstNode = n;
        }
        return sb.toString();
    }
    
    private Direction getDirection(BoardPosition p1, BoardPosition p2)
    {
        if(p1.Row > p2.Row)
            return Direction.UP;
        if(p1.Row < p2.Row)
            return Direction.DOWN;
        if(p1.Column > p2.Column)
            return Direction.LEFT;
        if(p1.Column < p2.Column)
            return Direction.RIGHT;
        else 
            return Direction.NONE;
    }
    
}

