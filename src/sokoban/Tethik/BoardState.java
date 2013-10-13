/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Tethik;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


import sokoban.BoardPosition;
import sokoban.NodeType;
import sokoban.Direction;
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
    private int rows;
    private int cols;
    
    // fult, kanske ska byta interna kartan till samma typ av matris sen?
    public BoardState(NodeType[][] map) {
    	Goals = new HashSet<BoardPosition>();
    	rows = map.length;
    	cols = Integer.MIN_VALUE;
    	for(int row = 0; row < rows; ++row) {
    		cols = Math.max(cols, map[row].length);
    		Map.add(new ArrayList<NodeType>(map[row].length));    	
    		for(int col = 0; col < map[row].length; ++col)
    		{    			
    			NodeType type = map[row][col];
    			Map.get(row).add(type);
    			if(type == NodeType.GOAL || type == NodeType.PLAYER_ON_GOAL || type == NodeType.BLOCK_ON_GOAL)
    				Goals.add(new BoardPosition(row, col));
    		}
    	}
    	
    	initZobristTable(rows, cols);
    }
    
    public BoardState(List<String> rows)
    {
    	this(rows, true);
    }    
    
    public BoardState(List<String> rows, boolean initZobrist)
    {    
        // Init board
        buildBoard(rows);
        this.rows = rows.size();
        if(initZobrist) {
        	cols = Integer.MIN_VALUE;
        	for(List<NodeType> row : Map)
        		cols = Math.max(row.size(), cols);
        		
        	initZobristTable(Map.size(), cols);
        }
    }
    
    public BoardState() {
    	
    }
    
  	public static BoardState getBoardFromFile(String filename) throws IOException
  	{
  		return getBoardFromFile(filename, true);
  	}
  	
	public static BoardState getBoardFromFile(String filename, boolean initHash) throws IOException
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
	
		
		return new BoardState(buffer, initHash);
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
                NodeType type = NodeType.parse(columns[cIndex]);
                
                if(type == NodeType.GOAL || type == NodeType.BLOCK_ON_GOAL || type == NodeType.PLAYER_ON_GOAL)
                {
                    Goals.add(p);
                }
                
                if(type == NodeType.PLAYER || type == NodeType.PLAYER_ON_GOAL)
                {
                    CurrentNode = p;                    
                }
                Map.get(rIndex).add(type);
            }
        }
    }
    
	public boolean isInCorner(BoardPosition position) {
		
		BoardPosition north = new BoardPosition(position.Row-1, position.Column);
		BoardPosition east = new BoardPosition(position.Row, position.Column+1);
		BoardPosition south = new BoardPosition(position.Row+1, position.Column);
		BoardPosition west = new BoardPosition(position.Row, position.Column-1);
		
		if(getNode(north) == NodeType.WALL && getNode(east) == NodeType.WALL)
			return true;
		
		if(getNode(north) == NodeType.WALL && getNode(west) == NodeType.WALL)
			return true;
		
		if(getNode(south) == NodeType.WALL && getNode(west) == NodeType.WALL)
			return true;
		
		if(getNode(south) == NodeType.WALL && getNode(east) == NodeType.WALL)
			return true;
		
		
		return false;
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
    
    public List<BoardPosition> getFromNeighbours(BoardPosition pos) {
    	return getFromNeighbours(pos.Row, pos.Column);
    }
    
    public List<BoardPosition> getFromNeighbours(int row, int col)
    {
        List<BoardPosition> positions = new ArrayList<>();
        // UP
        if(row > 1) {
        	NodeType type = getNode(row-2,col);
        	if(type != NodeType.WALL && type != NodeType.INVALID)
        		positions.add(new BoardPosition(row-1,col));
        }
        // Down
        if(row < Map.size()-2) {
        	NodeType type = getNode(row+2,col);
        	if(type != NodeType.WALL && type != NodeType.INVALID)
        	positions.add(new BoardPosition(row+1,col));
        }
        // LEFT
        if(col > 1) {
        	NodeType type = getNode(row,col-2);
        	if(type != NodeType.WALL && type != NodeType.INVALID)
        		positions.add(new BoardPosition(row,col-1));
        }
        	
        //RIGHT
        if(col < Map.get(row).size()-2) {
        	NodeType type = getNode(row,col+2);
        	if(type != NodeType.WALL && type != NodeType.INVALID)
        		positions.add(new BoardPosition(row,col+1));
        
        }
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
    	//NodeType node = getNode(row, col);
    	/* 
    	if(node != NodeType.BLOCK && node != NodeType.BLOCK_ON_GOAL)
    		 throw new IllegalArgumentException("Cant get pushing positions for non blocks: ("+row+" "+col+ " " + node + ")");
    	*/
    	
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
    
    public boolean isBlockingNode(NodeType type) {
    	return (type != NodeType.INVALID && type != NodeType.GOAL && type != NodeType.SPACE && type != NodeType.PLAYER && type != NodeType.PLAYER_ON_GOAL);
    }
    
    public boolean isBlockingNode(BoardPosition position) {    	
    	return isBlockingNode(getNode(position));
    }   
    
    public int getRowsCount()
    {
        return rows;
    }
    
    public int getColumnsCount()
    {
        return cols;
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
		} else {			
			// Update hash
			if(type == NodeType.GOAL)
			{
				updateHashCode(row, col, NodeType.GOAL, NodeType.PLAYER_ON_GOAL);
			}
			else
			{
				updateHashCode(row, col, NodeType.SPACE, NodeType.PLAYER);
			}
			
			// Set new position (otherwise push will)
			Map.get(row).set(col, type == NodeType.GOAL ? NodeType.PLAYER_ON_GOAL : NodeType.PLAYER);
		}
		
    	
		// Reset old position
    	NodeType playerType = Map.get(CurrentNode.Row).get(CurrentNode.Column);
		Map.get(CurrentNode.Row).set(CurrentNode.Column, (playerType == NodeType.PLAYER_ON_GOAL) ? NodeType.GOAL : NodeType.SPACE);

		// Update hash
		if(playerType == NodeType.PLAYER_ON_GOAL)
		{
			updateHashCode(CurrentNode.Row, CurrentNode.Column, NodeType.PLAYER_ON_GOAL, NodeType.GOAL);
		}
		else
		{
			updateHashCode(CurrentNode.Row, CurrentNode.Column, NodeType.PLAYER, NodeType.SPACE);
		}
		
		CurrentNode = new BoardPosition(row, col);	
    }
    
    public void movePlayer(Path path) {
    	for(BoardPosition pos : path.getPath())
    	{
//    		if(pos != null && pos.equals(CurrentNode))
            if(pos.equals(CurrentNode))
    			continue;
//                else if(pos != null)
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
    	
    	if(orig == NodeType.BLOCK_ON_GOAL)
    	{
    		updateHashCode(row, col, NodeType.BLOCK_ON_GOAL, NodeType.PLAYER_ON_GOAL);
    	}
    	else // Is block
    	{
    		updateHashCode(row, col, NodeType.BLOCK, NodeType.PLAYER);
    	}
    	
    	if(dest == NodeType.GOAL)
    	{
    		updateHashCode(newrow, newcol, NodeType.GOAL, NodeType.BLOCK_ON_GOAL);
    	}
    	else // Is space
    	{
    		updateHashCode(newrow, newcol, NodeType.SPACE, NodeType.BLOCK);
    	}
    	
    	Map.get(row).set(col, (orig == NodeType.BLOCK_ON_GOAL) ? NodeType.PLAYER_ON_GOAL : NodeType.PLAYER);
    	
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
  	
  	@SuppressWarnings("unchecked")
	@Override
  	public Object clone() {
  		BoardState newState = new BoardState();
  		newState.CurrentNode = new BoardPosition(CurrentNode.Row, CurrentNode.Column);
  		newState.zobrist_hash = zobrist_hash;
  		newState.cols = cols;
  		newState.rows = rows;
  		
  		 // yay casts...
  		newState.Goals = Goals;
  		for(List<NodeType> row : Map)
  			newState.Map.add((List<NodeType>) ((ArrayList<NodeType>) row).clone()); 	
  		
  		return newState;
  	}


	
	
	/**
	 * Se: http://en.wikipedia.org/wiki/Zobrist_hashing
	 */
	private Integer zobrist_hash = null;
	private static int zobrist_table[][][];

	public static void initZobristTable(int rows, int cols) {		
		NodeType[] vals = NodeType.values();
		zobrist_table = new int[rows][cols][vals.length];
		Random random = new Random();
		
		for(int row = 0; row < rows; ++row)
			for(int col = 0; col < cols; ++col)
				for(int i = 0; i < vals.length; ++i)
					zobrist_table[row][col][i] = random.nextInt();
		
	}
	
	@Override
	public int hashCode()
	{		
		if(zobrist_hash != null)
			return zobrist_hash;
		
	
		NodeType[] vals = NodeType.values();
		zobrist_hash = 0;
		for(int row = 0; row < Map.size(); ++row) {
			for(int col = 0; col < Map.get(row).size(); ++col)
			{
				NodeType type = getNode(row, col);
				int val = 0;
				typeloop:
				for(; val < vals.length; val++)
					if(type == vals[val]) 						
						break typeloop;					
				
				zobrist_hash ^= zobrist_table[row][col][val]; 
			}
		}
		
		//System.out.println(zobrist_hash);
		return zobrist_hash;
	}
	
	private void updateHashCode(int row, int col, NodeType oldType, NodeType newType)
	{
		if(zobrist_hash == null)
			return;
//		System.out.println(Arrays.toString(zobrist_table[row][col]));
		// XOra ut oldType
		zobrist_hash ^= zobrist_table[row][col][oldType.getIndex()];
		// XOra in newType
		zobrist_hash ^= zobrist_table[row][col][newType.getIndex()];
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof BoardState))
			return false;
		
		BoardState b = (BoardState) o;
		
		for(int row = 0; row < Map.size(); ++row) {
			for(int col = 0; col < Map.get(row).size(); ++col)
			{
				if(getNode(row, col) != b.getNode(row, col))
					return false;
			}
		}
		return true;		
	}
	
    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	for(List<NodeType> row : Map) {
    		for(NodeType t : row)
    			builder.append(t.getChar());
    		builder.append('\n');
    	}
    	
    	return builder.toString();   		
    }
	


}
