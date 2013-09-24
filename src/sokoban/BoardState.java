/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import sokoban.types.Direction;
import sokoban.types.NodeType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
public class BoardState implements Cloneable
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
    
    public BoardState() {
    	
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
        return type == NodeType.GOAL || type == NodeType.BLOCK_ON_GOAL;
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
    
    public List<BoardPosition> getNeighbours(BoardPosition pos)
    {
        return getNeighbours(pos.Row, pos.Column);
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
    
    public List<BoardPosition> getPushingPositions(BoardPosition pos) {
    	return getPushingPositions(pos.Row, pos.Column);
    }
    
    /***
     * Returns neighbour positions which can be used to push this (assumed) block. 
     * @param row
     * @param col
     * @return
     */
    public List<BoardPosition> getPushingPositions(int row, int col)
    {
    	NodeType node = getNode(row, col);
    	 if(node != NodeType.BLOCK && node != NodeType.BLOCK_ON_GOAL)
    		 throw new IllegalArgumentException("Cant get pushing positions for non blocks: ("+row+" "+col+ " " + node + ")");
    	 
		 List<BoardPosition> positions = new ArrayList<>();
		 // UP and DOWN
		 if(row > 0 && row < Map.size()-1) { 
			BoardPosition down = new BoardPosition(row+1,col);       
			BoardPosition up = new BoardPosition(row-1,col);
			if(!isBlockingNode(down) && !isBlockingNode(up))
			{
				positions.add(up);
				positions.add(down);
			}
		 }
		 
		 // LEFT //RIGHT
		 if(col > 0 && col < Map.get(row).size()-1) {
			BoardPosition left = new BoardPosition(row,col+1);
			BoardPosition right = new BoardPosition(row,col-1);	
			
			if(!isBlockingNode(left) && !isBlockingNode(right))
			{
				positions.add(left);
				positions.add(right);
			}
		 	
		 }
		 
		 return positions;
    }
    
    public boolean isBlockingNode(BoardPosition position) {
    	NodeType type = getNode(position);    	
    	return (type != NodeType.INVALID && type != NodeType.GOAL && type != NodeType.SPACE && type != NodeType.PLAYER); //&& type != NodeType.PLAYER_ON_GOAL);
    }
    
    public boolean isBlock(int r, int c)
    {
    	NodeType type = getNode(r, c);
        if(type == NodeType.INVALID)
            System.err.println("Referring to position " + r + ", " + c + " which refers to INVALID type");
        
        return 
               type == NodeType.BLOCK ||
               type == NodeType.BLOCK_ON_GOAL;
    }
    
	public boolean isBlock(BoardPosition b) {
		
		return isBlock(b.Row, b.Column);
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

    
    public BoardPosition getPlayerNode()
    {
        return CurrentNode;
    }
    
    public Set<BoardPosition> getGoalNodes()
    {
        return Goals;
    }
    
    public boolean moveBlockTo(int fromRow, int fromCol, Direction dir)
    {
    	try
    	{
	    	switch(dir)
	    	{
	    	case UP:
	    		pushBlock(fromRow, fromCol, fromRow - 1, fromCol);
	    		break;
	    	case DOWN:
	    		pushBlock(fromRow, fromCol, fromRow + 1, fromCol);
	    		break;
	    	case LEFT:
	    		pushBlock(fromRow, fromCol, fromRow, fromCol - 1);
	    		break;
	    	case RIGHT:
	    		pushBlock(fromRow, fromCol, fromRow, fromCol + 1);
	    		break;
			default:
				break;
	    	}
    	} catch(IllegalArgumentException ex)
    	{
    		return false;
    	}
    	return true;
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
    	if(new BoardPosition(row, col).equals(CurrentNode))
    		return;
    	
    	if(row < 0 || row >= Map.size())
    		throw new IllegalArgumentException("Position is out of bounds (x = " + row + ")");
    
    	int diff = col - CurrentNode.Column + row - CurrentNode.Row;
    	if(Math.abs(diff) > 1)
    		throw new IllegalArgumentException("Can't move player more than one coordinate");
    	
    	NodeType type = Map.get(row).get(col);
		if(type == NodeType.BLOCK || type == NodeType.BLOCK_ON_GOAL)
		{
			// Check if block can be pushed	(and if so do so)	
			if(diff > 0) 
			{
				if(row > CurrentNode.Row) // South
					pushBlock(row, col, row+1, col);					
				else
					pushBlock(row, col, row, col+1); // East
			}
			else { 
				if(row < CurrentNode.Row) // North
					pushBlock(row, col, row-1, col);
				else
					pushBlock(row, col, row, col-1); // West
					
			}
		
		} else if(type != NodeType.SPACE && type != NodeType.GOAL) {
			// Wall or other invalid move
			throw new IllegalArgumentException("Invalid move: \n" + CurrentNode + "\n  " + row + " " + col + " is " + type);			
		}
		
		// Set new position
		Map.get(row).set(col, type == NodeType.GOAL ? NodeType.PLAYER_ON_GOAL : NodeType.PLAYER);    	
    	
		// Reset old position
    	NodeType playerType = Map.get(CurrentNode.Row).get(CurrentNode.Column);
		Map.get(CurrentNode.Row).set(CurrentNode.Column, (playerType == NodeType.PLAYER_ON_GOAL) ? NodeType.GOAL : NodeType.SPACE);
		CurrentNode = new BoardPosition(row, col);	
    }
    
    public void movePlayer(Path path) {
    	for(BoardPosition pos : path.getPath())
    	{
    		if(pos.equals(CurrentNode))
    			continue;
    		movePlayerTo(pos);
    	}
    }
    
    private void pushBlock(int row, int col, int newrow, int newcol) {
    	NodeType orig = Map.get(row).get(col);
    	NodeType dest = Map.get(newrow).get(newcol);
    	
    	if(orig != NodeType.BLOCK && orig != NodeType.BLOCK_ON_GOAL)
    		throw new IllegalArgumentException("Push was called to push non-block: " + orig.toString());
    	if(dest != NodeType.GOAL && dest != NodeType.SPACE)
    		throw new IllegalArgumentException("Can't push block, something is in the way: " + dest.toString());    	
    	
    	Map.get(row).set(col, (orig == NodeType.BLOCK_ON_GOAL) ? NodeType.GOAL : NodeType.SPACE);
    	
    	Map.get(newrow).set(newcol, (dest == NodeType.GOAL) ? NodeType.BLOCK_ON_GOAL : NodeType.BLOCK);
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
    
    public boolean isSpaceNode(int r, int c) {
        NodeType type = getNode(r, c);
        if(type == NodeType.INVALID)
            System.err.println("Referring to position " + r + ", " + c + " which refers to INVALID type");
        
        return 
               type == NodeType.GOAL ||
                type == NodeType.SPACE;
    }

    public boolean isSpaceNode(BoardPosition p) {
        return isSpaceNode(p.Row, p.Column);
    }
    
    public boolean isNoneBlockingNode(int r, int c)
    {
        NodeType type = getNode(r, c);
        if(type == NodeType.INVALID)
            System.err.println("Referring to position " + r + ", " + c + " which refers to INVALID type");
        
        return 
               type != NodeType.INVALID &&
               type != NodeType.WALL &&
               type != NodeType.BLOCK &&
               type != NodeType.BLOCK_ON_GOAL;
    }
    
    public boolean isNoneBlockingNode(BoardPosition p)
    {
        return isNoneBlockingNode(p.Row, p.Column);
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
  	
  	@Override
  	public Object clone() {
  		BoardState newState = new BoardState();
  		newState.CurrentNode = new BoardPosition(CurrentNode.Row, CurrentNode.Column);
  		 // yay casts...
  		newState.Goals = Goals;
  		for(List<NodeType> row : Map)
  			newState.Map.add((List<NodeType>) ((ArrayList<NodeType>) row).clone()); 	
  		
  		return newState;
  	}

  	
	public static BoardState getBoardFromFile(String filename) throws IOException
	{
		FileReader rawInput = new FileReader(filename);
		BufferedReader br = new BufferedReader(rawInput);
		
		List<String> buffer = new ArrayList<>();
		
		while(true)
		{
			String tmp = br.readLine();
			if(tmp == null)
				break;
			buffer.add(tmp);			
		}
		br.close();
	
		
		return new BoardState(buffer);
	}
	
	public int hashCode()
	{
		List<BoardPosition> positions = getBlockNodes();
		//int weight = 1;
		int incrementedWeight = 1;
		int val = 0;
		for(BoardPosition pos: positions)
		{
			val += incrementedWeight*pos.hashCode();
			incrementedWeight *= 4;
		}
		return val;
	}


}
