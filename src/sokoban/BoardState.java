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
    private Map<BoardPosition, Node> Map;
    private List<List<NodeType>> newMap = new ArrayList<>();
    private Set<BoardPosition> Goals;
    private BoardPosition CurrentNode;
    private boolean StartingOnGoal = false;
    private int IDCounter = 1;
    
    private ISearchAlgorithmPath PathSearcher;
    
    
    public BoardState(ISearchAlgorithmPath pathSearch, List<String> rows)
    {    
        PathSearcher = pathSearch;
        
        // Init board
        buildBoard(rows);
    }
    
    private void buildBoard(List<String> rows)
    {
        StartingOnGoal = false;
        IDCounter = 1;
        Goals = new HashSet<>();
        Map = new HashMap<>();
        
        char[] columns = null;
        String tmp ;
        for(int rIndex = 0; rIndex < rows.size(); rIndex++)
        {
            tmp = rows.get(rIndex);
            if(tmp == null || tmp.equals(""))
                break;
            columns = tmp.toCharArray();
            
            newMap.add(new ArrayList<NodeType>(columns.length));
            for(int cIndex = 0; cIndex  < columns.length; cIndex++)
            {
				
                BoardPosition p = new BoardPosition(rIndex, cIndex);
                /*
                Node n = new Node(IDCounter++, p);
                n.setNodeType(getNodeType(columns[cIndex]));
                Map.put(p, n);
                */
                NodeType type = getNodeType(columns[cIndex]);
                
                if(isGoalType(type))
                {
                    Goals.add(p);
                }
                
                if(isPlayerPosition(type))
                {
                    CurrentNode = p;                    
                }
                
                newMap.get(rIndex).add(type);
                                
                /*
                if(n.getNodeType() == NodeType.PLAYER_ON_GOAL)
                {
                    StartingOnGoal = true;
                }
                * */
            }
        }
        
        /*
        BoardPosition[] positions = new BoardPosition[4];
        Direction[] directions = Constants.GetPossibleDirections();
        
        for(Node n : Map.values())
        {   
            if(n.getNodeType() == NodeType.WALL)
            {
                continue;
            }
            int row = n.Position.Row;
            int col = n.Position.Column;
            // up
                positions[0] = new BoardPosition(row-1, col);
            //down
                positions[1] = new BoardPosition(row+1, col);
            //left
                positions[2] = new BoardPosition(row, col-1);
            // right
                positions[3] = new BoardPosition(row, col+1);
            for(int i = 0; i < positions.length; i++)
            {
                Node neighbour = Map.get(positions[i]);
                if(neighbour != null)
                {
                    n.addNeighbour(directions[i], neighbour);
                }
            }            
        }
        */
    }
    
    public String findPath(Node dest)
    {
        Set<Node> destination = new HashSet<>();
        destination.add(dest);
        return findPath(destination);
    }
    
    public String findPath(Set<Node> destinations)
    {
        if(StartingOnGoal)
            return "";
        Path p = PathSearcher.getPath(CurrentNode, destinations);
        if(p == null)
            return "no path";
        
        return p.toString();
    }    
    
    private boolean isPlayerPosition(NodeType n) {
        return n.getNodeType() == NodeType.PLAYER || n.getNodeType() == NodeType.PLAYER_ON_GOAL;
    }
    
    private boolean isGoalType(NodeType n)
    {
        return n.getNodeType() == NodeType.GOAL;
    }
    
    private NodeType getNodeType(char c)
	{
		switch(c)
		{
			case ' ':
			{       
				return NodeType.SPACE;
			}
			case '$':
			{
				return NodeType.BLOCK;
			}
			case '*':
			{
				return NodeType.BLOCK_ON_GOAL;
			}
			case '+':
			{
				return NodeType.PLAYER_ON_GOAL;
			}
			case '.':
			{
				return NodeType.GOAL;
			}
			case '@':
			{
				return NodeType.PLAYER;
			}
			case '#':
			default:
			{
				return NodeType.WALL;
			}
		}
	}
    
	public NodeType getNode(int x, int y)
	{
		return newMap.get(x).get(y);
	}
	
    public NodeType getNode(BoardPosition pos)
    {
        return newMap.get(pos.Row).get(pos.Column);
    }
    
    public BoardPosition getPlayerNode()
    {
        return ;
    }
    
    public Set<BoardPosition> getGoalNodes()
    {
        return Goals;
    }

}
