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
    
    public NodeType getNode(int row, int col)
    {
        if(row < 0 || row >= Map.size())
            return NodeType.INVALID;
        if(col < 0 || col >= Map.get(row).size())
            return NodeType.INVALID;
        
        return Map.get(row).get(col);
    }
	
    public NodeType getNode(BoardPosition pos)
    {
        return getNode(pos.Row, pos.Column);
    }
    
    public List<BoardPosition> getNeighbours(int row, int col)
    {
        List<BoardPosition> positions = new ArrayList<>();
        // UP
        if(row > 0)
         positions.add(new BoardPosition(row-1,col));
        // Down
        if(row < Map.size()-1)
         positions.add(new BoardPosition(row+1,col));
        // LEFT
        if(col > 0)
         positions.add(new BoardPosition(row,col-1));
        //RIGHT
        if(col < Map.get(row).size()-1)
         positions.add(new BoardPosition(row,col+1));
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
    
    
    /***
     * Moves the player to target position. Resetting the old position to its normal value.
     * Throws exception if the move is invalid.
     * 
     * Todo: Maybe add function to move player in a direction?
     * @param row
     * @param col
     */
    public void movePlayerTo(int row, int col)
    {
    	if(row < 0 || row >= Map.size())
    		throw new IllegalArgumentException("Position is out of bounds (x = " + row + ")");
    
    	int diff = col - CurrentNode.Column + row - CurrentNode.Row;
    	if(Math.abs(diff) > 1)
    		throw new IllegalArgumentException("Can't move player more than one coordinate");
    	
    	NodeType type = Map.get(row).get(col);
		if(type == NodeType.BLOCK || type == NodeType.BLOCK_ON_GOAL)
		{
			// Check if block can be pushed	(and if so do so)	
			if(diff > 0) // North, west
			{
				if(row > CurrentNode.Row) // North
					pushBlock(row, col, row, col+1);
				else
					pushBlock(row, col, row+1, col);
			}
			else { // south, east
				if(row < CurrentNode.Row) // South
					pushBlock(row, col, row, col-1);
				else
					pushBlock(row, col, row-1, col);
			}
		
		} else if(type != NodeType.SPACE) {
			// Wall or other invalid move
			throw new IllegalArgumentException("Invalid move");			
		}
		
		// Set new position
		Map.get(row).set(col, type == NodeType.GOAL ? NodeType.PLAYER_ON_GOAL : NodeType.PLAYER);    	
    	
		// Reset old position
    	NodeType playerType = Map.get(CurrentNode.Row).get(CurrentNode.Column);
		Map.get(CurrentNode.Row).set(CurrentNode.Column, (playerType == NodeType.PLAYER) ? NodeType.SPACE : NodeType.GOAL);
		CurrentNode = new BoardPosition(row, col);
	
    }
    
    private void pushBlock(int row, int col, int newrow, int newcol) {
    	NodeType orig = Map.get(row).get(col);
    	NodeType dest = Map.get(newrow).get(newcol);
    	
    	if(orig != NodeType.BLOCK && orig != NodeType.BLOCK_ON_GOAL)
    		throw new IllegalArgumentException("Push was called to push non-block: " + orig.toString());
    	if(dest != NodeType.GOAL && dest != NodeType.SPACE)
    		throw new IllegalArgumentException("Can't push block, something is in the way: " + dest.toString());    	
    	
    	Map.get(col).set(row, (orig == NodeType.BLOCK_ON_GOAL) ? NodeType.GOAL : NodeType.SPACE);
    	
    	Map.get(newcol).set(newrow, (dest == NodeType.GOAL) ? NodeType.BLOCK_ON_GOAL : NodeType.BLOCK);
    }
    
    /***
     * Moves the player to target position. Resetting the old position to its normal value
     * @param p
     */
    public void movePlayerTo(BoardPosition p)
    {
    	movePlayerTo(p.Row, p.Column);
    }
    
    /***
     * Basic check if the game is won. Checks if all goals are occupied by a block.
     * @return
     */
    public boolean isWin() {
    	for(BoardPosition goal : Goals)
    	{
    		if(getNode(goal) != NodeType.BLOCK_ON_GOAL)
    			return false;
    	}
    	return true;
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
    
    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	for(List<NodeType> row : Map) {
    		for(NodeType t : row)
    			builder.append(Constants.GetCharFromNodeType(t));
    		builder.append('\n');
    	}
    	
    	return builder.toString();   		
    }
    
  	public List<BoardPosition> getBlockNodes()
    {
        List<BoardPosition> blocks = new ArrayList<>();
        for(int i = 0; i < Map.size(); i++)
        {
            for(int j = 0; j < Map.get(i).size(); j ++)
            {
                NodeType type = getNode(i,j);
                if( type == NodeType.BLOCK || type == NodeType.BLOCK_ON_GOAL )
                {
                    blocks.add(new BoardPosition(i, j));
                }
            }
        }
        return blocks;
    }
    

}
