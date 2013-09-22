/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import sokoban.types.Direction;
import sokoban.types.NodeType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author figgefred
 */
public class Node {
    
    public final int ID;
    public final BoardPosition Position;
    private NodeType Type;
    private Map<Direction, Node> Neighbours;
    private Node[] NeighboursList;
    
    private Integer MyHash = null;
    
    public Node(int ID, BoardPosition position)
    {
        this.ID = ID;
        this.Position = position;
        Type = NodeType.WALL;
        Neighbours = new HashMap<>();
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Node)
        {
            return hashCode() == ((Node)o).hashCode();
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        if(MyHash == null)
        {
            MyHash = Position.hashCode()*Position.hashCode() + ID; 
        }
        return MyHash;
        
    }
    
    public Node[] getNeighbours()
    {
        if(NeighboursList == null)
        {
            NeighboursList = new Node[Neighbours.size()];
            int i = 0;
            for(Node n: Neighbours.values())
            {
                NeighboursList[i++] = n;
            }
        }
        return NeighboursList;
    }
    
    public void setNodeType(NodeType type)
    {
        Type = type;
    }
    
    public NodeType getNodeType()
    {
        return Type;
    }
    
    public boolean addNeighbour(Direction d, Node n)
    {
        if(Neighbours.containsKey(d))
        {
            return false;
        }
        NeighboursList = null;
        return (Neighbours.put(d, n) != null ? true: false);
    }

    public Node removeNeighbour(Direction d)
    {
        NeighboursList = null;
        return Neighbours.remove(d);
    }
    
    public Direction getDirection(Node n)
    {
        Node n2 = null;
        for(Direction d: Constants.GetPossibleDirections())
        {
            n2 = Neighbours.get(d);
            if(n.equals(n2))
            {
                return d;
            }
        }
        return Direction.NONE;
    }
    
    public Node getNeighbour(Direction d)
    {
        return Neighbours.get(d);
    }
}
