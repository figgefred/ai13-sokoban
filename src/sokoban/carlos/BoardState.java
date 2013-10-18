/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
/**
 *
 * @author figgefred
 */
public class BoardState implements Cloneable
{
    // Game board
    private NodeType[][] Map;
    private List<BoardPosition> Goals;
    private BoardPosition CurrentNode;
    private int rows;
    private int cols;
    private BoardPosition lastPushedBlock;
    private Settings settings;
    private Tunnels Tunnels;
    
    // fult, kanske ska byta interna kartan till samma typ av matris sen?
    public BoardState(NodeType[][] map) {
    	Goals = new ArrayList<BoardPosition>();
    	rows = map.length;
    	cols = Integer.MIN_VALUE;
    	this.Map = map;
    	for(int row = 0; row < rows; ++row) {
    		cols = Math.max(cols, map[row].length);
    		 	
    		for(int col = 0; col < map[row].length; ++col)
    		{    			
    			NodeType type = map[row][col];
    			if(type == NodeType.GOAL || type == NodeType.PLAYER_ON_GOAL || type == NodeType.BLOCK_ON_GOAL)
    				Goals.add(new BoardPosition(row, col));    				
    		}
    	}
        
    }
    
    public BoardState(List<String> rows)
    {
    	this(rows, true);
    }    
    
    public void RecalculateGoalNodes() {
    	Goals = new ArrayList<BoardPosition>();
    	for(int row = 0; row < rows; ++row) {
    		 	
    		for(int col = 0; col < Map[row].length; ++col)
    		{    			
    			NodeType type = Map[row][col];
    			if(type == NodeType.GOAL || type == NodeType.PLAYER_ON_GOAL || type == NodeType.BLOCK_ON_GOAL)
    				Goals.add(new BoardPosition(row, col));    		
    			
    		}
    	}
    }
    
    public BoardState(List<String> rows, boolean initZobrist)
    {    
    	Goals = new ArrayList<BoardPosition>();
        // Init board        
        this.rows = rows.size();
     	cols = Integer.MIN_VALUE;
    	for(String row : rows)
    		cols = Math.max(row.length(), cols);
    	
    	Map = new NodeType[this.rows][this.cols];
    	for(int r = 0; r < this.rows; ++r)
    	{
    		for(int c = 0; c < this.cols; ++c)
    			Map[r][c] = NodeType.INVALID;
    	}
    	buildBoard(rows);
    	
        if(initZobrist) { 
            initZobristTable(rows.size(), cols);
        }
        if(Player.DO_TUNNEL_MACRO_MOVE)
            setAllTunnels();
        if(Player.DO_GOAL_SORTING)
            sortGoals();
        setInvalidFields(Map);
        
    }
    
    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }
    
    public BoardState() {
    	
    }
    
    private void setAllTunnels()
    {
        Tunnels = new Tunnels();
        Set<BoardPosition> markedAsTunnel = new HashSet<BoardPosition>();
        // First we look for VERTICAL entrances
        for(int i = 0; i < getRowsCount(); i++)
        {
            for(int j = 0; j < getColumnsCount(); j++)
            {
                BoardPosition centerP = new BoardPosition(i, j);

                if(get(centerP).isTunnelSpaceNode())
                {
                    boolean horizontal = false;
                    // UP || DOWN
                    BoardPosition p1 = new BoardPosition(i, j-1);
                    BoardPosition p3 = new BoardPosition(i, j+1);
                    if(get(p1).isWallNode() && get(p3).isWallNode())
                    {
                        Tunnel t = new Tunnel(this);
                        expandTunnel(t, centerP, Direction.DOWN, markedAsTunnel);
                        if(t.count() >= 2)
                                Tunnels.add(t);
                    }
                    else
                        horizontal = true;
                    if(horizontal)
                    {
                        p1 = new BoardPosition(i-1, j);
                        p3 = new BoardPosition(i+1, j);    
                        if(get(p1).isWallNode() && get(p3).isWallNode())
                        {
                            Tunnel t = new Tunnel(this);
                            expandTunnel(t, centerP, Direction.RIGHT, markedAsTunnel);    
                            if(!t.isEmpty())
                                Tunnels.add(t);
                        }
                    }
                }
            }
        }
        
        // Now we look for HORIZONTAL entrances
    }
    
    private void expandTunnel(Tunnel tunnel, BoardPosition p, Direction direction, Set<BoardPosition> visited)
    {
        // Add head position
        if(visited.contains(p))
            return;
        tunnel.add(p);
        visited.add(p);
        
        BoardPosition p1;
        BoardPosition p2;
        BoardPosition p3;
        switch(direction)
        {
            case UP:
            {
                p1 = new BoardPosition(p.Row-1, p.Column-1);
                p2 = new BoardPosition(p.Row-1, p.Column);
                p3 = new BoardPosition(p.Row-1, p.Column+1);
                break;
            }
            case DOWN:
            {
                p1 = new BoardPosition(p.Row+1, p.Column-1);
                p2 = new BoardPosition(p.Row+1, p.Column);
                p3 = new BoardPosition(p.Row+1, p.Column+1);
                break;
            }
            case LEFT:
            {
                p1 = new BoardPosition(p.Row-1, p.Column-1);
                p2 = new BoardPosition(p.Row, p.Column-1);
                p3 = new BoardPosition(p.Row+1, p.Column-1);
                break;
            }
            case RIGHT:
            {
                p1 = new BoardPosition(p.Row-1, p.Column+1);
                p2 = new BoardPosition(p.Row, p.Column+1);
                p3 = new BoardPosition(p.Row+1, p.Column+1);
                break;
            }
            default: 
            {
                return;
            }
        }
            
        
        if(!visited.contains(p2) )
        {
            boolean appended = tunnel.add(p2);
            visited.add(p2);
            while(appended)
            {    
                appended = false;
                for(BoardPosition n: this.getNeighbours(p2))
                {
                    if(tunnel.add(n))
                    {
                        p2 = n;
                        visited.add(n);
                        appended = true;
                        break;
                    }
                }   
            }
        }
    }
    
    
    public Tunnels getTunnels()
    {
        return Tunnels;
    }
    
    private void sortGoals()
    {
        java.util.Collections.sort(Goals, new java.util.Comparator<BoardPosition>() {

			@Override
			public int compare(BoardPosition o1, BoardPosition o2) {
				NodeType[] n1 = getNeighbourTypes(o1);
				
				int val1 = 0;
				int val2 = 0;
				
				int w1 = 0;
				int w2 = 0;
				
				NodeType[] n2 = getNeighbourTypes(o2);
				
				for(int i = 0; i < n1.length; i++) {
					switch(n1[i]) {
					case WALL: val1 += 10; break;
					case GOAL: val1 += 8; break;
					default: break;
					}
					
					switch(n2[i]) {
					case WALL: val2 += 10; break;
					case GOAL: val2 += 8; break;
					default: break;
					}
					/*
					if(n1[i] == NodeType.WALL)
						w1++;
					
					if(n2[i] == NodeType.WALL)
						w2++;
						*/
				}
				
				return val1 > val2 ? -1 : val1 < val2 ? 1 : 0;
			}
		});
    }
    
    private void setInvalidFields(NodeType[][] map)
    {
        
    	for(int row = 0; row < rows; ++row) {
            int lastWallIndex = 0;
            boolean firstWall = false;
            for(int col = 0; col < map[row].length; ++col)
            {    			
                NodeType type = map[row][col];
                if(type != NodeType.WALL && !firstWall)
                    map[row][col] = NodeType.INVALID;
                else if(!firstWall)
                {
                    firstWall = true;
                }
                else if(type == NodeType.WALL)
                {
                    lastWallIndex = col;
                }
                
                
                
                //if(firstWall)
//                    break;
            }
            for(int col = lastWallIndex+1; col < map[row].length; ++col)
            {
                map[row][col] = NodeType.INVALID;
            }
    	}
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
        char[] columns = null;
        String tmp ;
        for(int row = 0; row < rows.size(); row++)
        {
            tmp = rows.get(row);
            if(tmp == null || tmp.equals(""))
                break;
            columns = tmp.toCharArray();
                        
            for(int col = 0; col  < columns.length; col++)
            {	
                BoardPosition p = new BoardPosition(row, col);
                NodeType type = NodeType.parse(columns[col]);
                
                if(type == NodeType.GOAL || type == NodeType.BLOCK_ON_GOAL || type == NodeType.PLAYER_ON_GOAL)
                {
                    Goals.add(p);
                }
                
                if(type == NodeType.PLAYER || type == NodeType.PLAYER_ON_GOAL)
                {
                    CurrentNode = p;                    
                }
                
                Map[row][col] = type;
            }
        }
    }
       
    public NodeType get(int row, int col)
    {
        if(row < 0 || row >= rows)
            return NodeType.INVALID;
        if(col < 0 || col >= cols)
            return NodeType.INVALID;
        
        return Map[row][col];
    }
	
    public NodeType get(BoardPosition pos)
    {
        return get(pos.Row, pos.Column);
    }
    
    public void set(BoardPosition pos, NodeType type) {
    	set(pos.Row, pos.Column, type);
    }
    
    public void set(int row, int col, NodeType type) {
    	updateHashCode(row, col, Map[row][col], type);
    	Map[row][col] = type;
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
        if(row < rows - 1)
        	positions.add(new BoardPosition(row+1,col));
        // LEFT
        if(col > 0)
        	positions.add(new BoardPosition(row,col-1));
        //RIGHT
        if(col < cols - 1)
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
        	NodeType type = get(row-2,col);
        	if(type != NodeType.WALL && type != NodeType.INVALID)
        		positions.add(new BoardPosition(row-1,col));
        }
        // Down
        if(row < rows-2) {
        	NodeType type = get(row+2,col);
        	if(type != NodeType.WALL && type != NodeType.INVALID)
        	positions.add(new BoardPosition(row+1,col));
        }
        // LEFT
        if(col > 1) {
        	NodeType type = get(row,col-2);
        	if(type != NodeType.WALL && type != NodeType.INVALID)
        		positions.add(new BoardPosition(row,col-1));
        }
        	
        //RIGHT
        if(col < cols-2) {
        	NodeType type = get(row,col+2);
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
		 List<BoardPosition> positions = new ArrayList<>();
		 // UP and DOWN
		 if(row > 0 && row < rows-1) { 
			BoardPosition down = new BoardPosition(row+1,col);       
			BoardPosition up = new BoardPosition(row-1,col);
			if(!isBlockingNode(down) && !isBlockingNode(up))
			{
				positions.add(up);
				positions.add(down);
			}
		 }
		 
		 // LEFT //RIGHT
		 if(col > 0 && col < cols-1) {
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
    	return isBlockingNode(get(position));
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
    
    public BoardPosition getLastPushedBlock() {
    	return lastPushedBlock;
    }
    
    public List<BoardPosition> getGoalNodes()
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
    	if(CurrentNode.Row == row && CurrentNode.Column == col)
    		return;
    	
    	if(row < 0 || row >= rows)
    		throw new IllegalArgumentException("Position is out of bounds (x = " + row + ")");
    
    	int diff = col - CurrentNode.Column + row - CurrentNode.Row;
    	if(Math.abs(diff) > 1)
    		throw new IllegalArgumentException("Can't move player more than one coordinate");
    	
    	NodeType type = Map[row][col];
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
			set(row,col,type == NodeType.GOAL ? NodeType.PLAYER_ON_GOAL : NodeType.PLAYER);
		}
			
		// Reset old position
    	NodeType playerType = Map[CurrentNode.Row][CurrentNode.Column];
    	set(CurrentNode.Row, CurrentNode.Column, (playerType == NodeType.PLAYER_ON_GOAL) ? NodeType.GOAL : NodeType.SPACE);
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
    
    public void haxMovePlayer(Path path) {
    	if(CurrentNode.equals(path.last()))
    		return;
    	
    	NodeType player = get(CurrentNode);
    	NodeType target = get(path.last());
    	NodeType prevOnPlayer = (player == NodeType.PLAYER_ON_GOAL ? NodeType.GOAL : NodeType.SPACE);
    	NodeType newOnTarget = (target == NodeType.GOAL ? NodeType.PLAYER_ON_GOAL : NodeType.PLAYER);
    	set(CurrentNode, prevOnPlayer);
    	set(path.last(), newOnTarget);
		CurrentNode = path.last();
    }
    
    private void pushBlock(int row, int col, int newrow, int newcol) {
    	NodeType orig = Map[row][col];
    	NodeType dest = Map[newrow][newcol];
    	
    	if(orig != NodeType.BLOCK && orig != NodeType.BLOCK_ON_GOAL)
    		throw new IllegalArgumentException("Push was called to push non-block: " + orig.toString());
    	if(dest != NodeType.GOAL && dest != NodeType.SPACE)
    		throw new IllegalArgumentException("Can't push block, something is in the way: " + dest.toString() + " " + row + "," + col + " " + newrow +"," + newcol);    	
    	
    	set(row, col, (orig == NodeType.BLOCK_ON_GOAL) ? NodeType.PLAYER_ON_GOAL : NodeType.PLAYER);
    	set(newrow, newcol, (dest == NodeType.GOAL) ? NodeType.BLOCK_ON_GOAL : NodeType.BLOCK);
    	
    	lastPushedBlock = new BoardPosition(newrow, newcol);    	
    }
    
    /***
     * Moves the player to target position. Resetting the old position to its normal value
     * @param p
     */
    public void movePlayerTo(BoardPosition p)
    {
    	movePlayerTo(p.Row, p.Column);
    }
    
    public void move(Direction direction)
    {
        BoardPosition p = CurrentNode.getNeighbouringPosition(direction);
        if(p.equals(CurrentNode))
        {
            return;
        }
        movePlayerTo(p);
    }
   
    /***
     * Basic check if the game is won. Checks if all goals are occupied by a block.
     * @return
     */
    public boolean isWin() {
	    for(BoardPosition goal : Goals)
	    {
            if(get(goal) != NodeType.BLOCK_ON_GOAL)
                    return false;
	    }
	    return true;
    }
    
    public boolean isSpaceNode(int r, int c) {
        NodeType type = get(r, c);
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
        NodeType type = get(r, c);
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
    
	public boolean isInCorner(BoardPosition position) {
		
		BoardPosition north = new BoardPosition(position.Row-1, position.Column);
		BoardPosition east = new BoardPosition(position.Row, position.Column+1);
		BoardPosition south = new BoardPosition(position.Row+1, position.Column);
		BoardPosition west = new BoardPosition(position.Row, position.Column-1);
		
		if(get(north) == NodeType.WALL && get(east) == NodeType.WALL)
			return true;
		
		if(get(north) == NodeType.WALL && get(west) == NodeType.WALL)
			return true;
		
		if(get(south) == NodeType.WALL && get(west) == NodeType.WALL)
			return true;
		
		if(get(south) == NodeType.WALL && get(east) == NodeType.WALL)
			return true;
		
		
		return false;
	}
    
    
  	public List<BoardPosition> getBlockNodes()
    {
        List<BoardPosition> blocks = new ArrayList<>();
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j ++)
            {
                NodeType type = get(i,j);
                if( type == NodeType.BLOCK || type == NodeType.BLOCK_ON_GOAL )
                {
                    blocks.add(new BoardPosition(i, j));
                }
            }
        }
        return blocks;
    }
        
    public NodeType[] getNeighbourTypes(BoardPosition pos)
    {
    	return getNeighbourTypes(pos.Row, pos.Column);
    }
    
    public NodeType[] getNeighbourTypes(int row, int col)
    {
    	return new NodeType[] {
    		get(row + 1, col),
    		get(row - 1, col),
    		get(row, col + 1),
    		get(row, col - 1)
    	};
    }
        
    public BoardPosition getNeighbour(BoardPosition p, Direction dir)
    {
        BoardPosition pNew = null;
        int n = -1;
        switch(dir)
        {
            case UP:
                n = p.Row -1;
                if(n < 0)
                    break;
                pNew = new BoardPosition(n, p.Column);
                break;
            case DOWN:
                n = p.Row +1;
                if(n >= getRowsCount())
                    break;
                pNew = new BoardPosition(n, p.Column);
                break;
            case LEFT:
                n = p.Column - 1;
                if(n < 0)
                    break;
                pNew = new BoardPosition(p.Row, n);
                break;
            case RIGHT:
                n = p.Column +1;
                if(n >= getColumnsCount())
                    break;
                pNew = new BoardPosition(p.Row, n);
                break;
        }
        return pNew;
    }

	@Override
  	public Object clone() {
  		BoardState newState = new BoardState();
  		newState.CurrentNode = new BoardPosition(CurrentNode.Row, CurrentNode.Column);
  		newState.zobrist_hash = zobrist_hash;
  		newState.cols = cols;
  		newState.rows = rows;
  		
  		 // yay casts...
  		newState.Goals = Goals;
  		newState.Map = new NodeType[rows][cols];
  		for(int row = 0; row < rows; ++row)
  			System.arraycopy(Map[row], 0, newState.Map[row], 0, cols); 
  			
                newState.Tunnels = Tunnels;
                newState.settings = settings;
                
  		return newState;
  	}


	
	
	/**
	 * Se: http://en.wikipedia.org/wiki/Zobrist_hashing
	 */
	private Integer zobrist_hash = null;
	public static int zobrist_table[][][];

	public static void initZobristTable(int rows, int cols) {		
		NodeType[] vals = NodeType.values();
		zobrist_table = new int[rows][cols][vals.length];
		Random random = new Random();
		
		for(int row = 0; row < rows; ++row)
			for(int col = 0; col < cols; ++col)
				for(int i = 0; i < vals.length; ++i)
					zobrist_table[row][col][i] = random.nextInt();
		
		//System.err.println("zobrist inited.");
	}
	
	@Override
	public int hashCode()
	{		
		if(zobrist_hash != null)
			return zobrist_hash;
		
	
		NodeType[] vals = NodeType.values();
		zobrist_hash = 0;
		for(int row = 0; row < rows; ++row) {
			for(int col = 0; col < cols; ++col)
			{
				NodeType type = get(row, col);
				
				if(!settings.BOARDSTATE_PLAYER_HASHING) {
					if(type == NodeType.PLAYER)
						type = NodeType.SPACE;
					else if(type == NodeType.PLAYER_ON_GOAL)
						type = NodeType.GOAL;
				}
				
				int val = 0;
				typeloop:
				for(; val < vals.length; val++)
					if(type == vals[val]) 						
						break typeloop;					
				
				int[][] table = zobrist_table[row];
				int[] tablerow = table[col];
				zobrist_hash ^= tablerow[val]; 
			}
		}
		
		//System.out.println(zobrist_hash);
		return zobrist_hash;
	}
	
        public void resetHash() {
		zobrist_hash = null;
	}
        
	private void updateHashCode(int row, int col, NodeType oldType, NodeType newType)
	{
		if(zobrist_hash == null)	
			return;
		
		if(!settings.BOARDSTATE_PLAYER_HASHING) {
			if(oldType == NodeType.PLAYER)
				oldType = NodeType.SPACE;
			else if(oldType == NodeType.PLAYER_ON_GOAL)
				oldType = NodeType.GOAL;
			
			if(newType == NodeType.PLAYER)
				newType = NodeType.SPACE;
			else if(newType == NodeType.PLAYER_ON_GOAL)
				newType = NodeType.GOAL;
		}
		
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
		
		for(int row = 0; row < rows; ++row) {
			for(int col = 0; col < cols; ++col)
			{
				if(get(row, col) != b.get(row, col))
					return false;
			}
		}
		return true;		
	}
	
    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	for(int r = 0; r < rows; ++r)
    	{
    		for(int c = 0; c < cols; ++c) {
    			char v = Map[r][c].getChar();
    			builder.append(v);
    		}
    		builder.append("\n");
    	}
    	
    	return builder.toString();   	
    }
	


}
