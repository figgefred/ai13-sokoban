/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import java.util.List;

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
        this.Nodes = nodes;
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
        BoardPosition parentNode = null;
        for(BoardPosition n: Nodes)
        {
            if(parentNode == null)
            {
                parentNode = n;
                continue;
            }
            //sb.append(Constants.DirectionToString(n.getDirection(parentNode)));
            
            // TODO print direction from n to parentNode
            
            sb.append(" ");
            parentNode = n;
        }
        return sb.toString();
    }
    
}

