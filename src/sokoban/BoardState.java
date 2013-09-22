/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import sokoban.types.Direction;
import sokoban.types.NodeType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import sokoban.Algorithms.ISearchAlgorithmPath;

/**
 *
 * @author figgefred
 */
public class BoardState 
{
    // Game board
    private List<List<NodeType>> Map = new ArrayList<>();
    private Set<BoardPosition> Goals;
    private BoardPosition CurrentNode;
    
    public BoardState(List<String> rows)
    {    
        // Init board
        buildBoard(rows);
    }
    
    private void buildBoard(List<String> rows)
    {
        
        //IDCounter = 1;
        Goals = new HashSet<>();
        Map = new ArrayList<>();
        
        char[] columns = null;
        String tmp ;
        for(int rIndex = 0; rIndex < rows.size(); rIndex++)
        {
            tmp = rows.get(rIndex);
            if(tmp == null || tmp.equals(""))
                break;
            columns = tmp.toCharArray();
            
            Map.add(new ArrayList<NodeType>(columns.length));
            for(int cIndex = 0; cIndex  < columns.length; cIndex++)
            {
				
                BoardPosition p = new BoardPosition(rIndex, cIndex);
                /*
                Node n = new Node(IDCounter++, p);
                n.setNodeType(getNodeType(columns[cIndex]));
                Map.put(p, n);
                */
                NodeType type = Constants.GetNodeType(columns[cIndex]);
                
                if(isGoalType(type))
                {
                    Goals.add(p);
                }
                
                if(isPlayerPosition(type))
                {
                    CurrentNode = p;                    
                }
                Map.get(rIndex).add(type);
            }
        }
    }
    
    private boolean isPlayerPosition(NodeType type) {
        return type == NodeType.PLAYER || type == NodeType.PLAYER_ON_GOAL;
    }
    
    private boolean isGoalType(NodeType type)
    {
        return type == NodeType.GOAL;
    }
    
    public NodeType getNode(int x, int y)
    {
        if(x < 0 || x >= Map.size())
            return NodeType.INVALID;
        if(y < 0 || y >= Map.get(x).size())
            return NodeType.INVALID;
        return Map.get(x).get(y);
    }
	
    public NodeType getNode(BoardPosition pos)
    {
        return getNode(pos.Row, pos.Column);
    }
    
    public List<BoardPosition> getNeighbours(int x, int y)
    {
        List<BoardPosition> positions = new ArrayList<>();
        // UP
        if(x > 0)
         positions.add(new BoardPosition(x-1,y));
        // Down
        if(x < Map.size()-1)
         positions.add(new BoardPosition(x+1,y));
        // LEFT
        if(y > 0)
         positions.add(new BoardPosition(x,y-1));
        //RIGHT
        if(y < Map.get(x).size()-1)
         positions.add(new BoardPosition(x,y+1));
        return positions;
    }
    
    public int getRowsCount()
    {
        return Map.size();
    }
    
    public int getColumnsCount(int r)
    {
        int size = Map.size();
        if(r < 0 || r >= size)
            return -1;
        return Map.get(r).size();
    }
    
    public List<BoardPosition> getNeighbours(BoardPosition pos)
    {
        return getNeighbours(pos.Row, pos.Column);
    }
    
    public BoardPosition getPlayerNode()
    {
        return CurrentNode;
    }
    
    public Set<BoardPosition> getGoalNodes()
    {
        return Goals;
    }
    
    public Direction getDirection(BoardPosition from, BoardPosition to)
    {
        if( from.Row-1 == to.Row && from.Column == to.Column )
        {
            return Direction.UP;
        }
        if(from.Row+1 == to.Row && from.Column == to.Column)
        {
            return Direction.DOWN;
        }
        if(from.Column-1 == to.Column && from.Row == to.Row)
        {
            return Direction.LEFT;
        }
        if(from.Column+1 == to.Column && from.Row == to.Row)
        {
            return Direction.RIGHT;
        }
        return Direction.NONE;
    }

}
