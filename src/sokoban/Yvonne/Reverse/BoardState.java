/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Yvonne.Reverse;

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
	private List<BoardPosition> Goals;
	private BoardPosition CurrentNode;
	private int rows;
	private int cols;
	private BoardPosition initPlayerNode;


	// fult, kanske ska byta interna kartan till samma typ av matris sen?
	public BoardState(NodeType[][] map) {
		Goals = new ArrayList<BoardPosition>();
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

		//initZobristTable(rows, cols);
		//System.err.println("zorbi!");
	}

	public BoardState(List<String> rows)
	{
		this(rows, true);
	}    
	
	public BoardPosition getInitPlayerNode(){
		return initPlayerNode;
	}

	public BoardState(List<String> rows, boolean initZobrist)
	{    
		// Init board
		buildBoard(rows);
		this.rows = rows.size();
		cols = Integer.MIN_VALUE;
		for(List<NodeType> row : Map)
			cols = Math.max(row.size(), cols);
		if(initZobrist) { 
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
		Goals = new ArrayList<>();
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

	public List<BoardPosition> getPullingPositions(BoardPosition pos) {
		return getPullingPositions(pos.Row, pos.Column);
	}



	public boolean isBlockBehind(Direction dir, int row, int col){
		switch(dir)
		{
		case UP:
			if(Map.get(row+2).get(col)==NodeType.BLOCK ||Map.get(row+2).get(col)==NodeType.BLOCK_ON_GOAL )
				return true;
			break;
		case DOWN:
			if(Map.get(row-2).get(col)==NodeType.BLOCK ||Map.get(row-2).get(col)==NodeType.BLOCK_ON_GOAL)
				return true;    		
			break;
		case LEFT:
			if(Map.get(row).get(col+2)==NodeType.BLOCK ||Map.get(row).get(col+2)==NodeType.BLOCK_ON_GOAL )
				return true;
			break;
		case RIGHT:
			if(Map.get(row).get(col-2)==NodeType.BLOCK ||Map.get(row).get(col-2)==NodeType.BLOCK_ON_GOAL )
				return true;
			break;
		default:
			break;
		}
		return false;
	}



	/***
	 * Returns neighbour positions which can be used to push this (assumed) block. 
	 * @param row
	 * @param col
	 * @return
	 */
	public List<BoardPosition> getPullingPositions(int row, int col)
	{


		List<BoardPosition> positions = new ArrayList<>();
		// UP and DOWN
		if(row > 0 && row < Map.size()-1) { 
			BoardPosition down = new BoardPosition(row+1,col);       
			BoardPosition up = new BoardPosition(row-1,col);
			if(!isBlockingNode(down)&&!isBlockingNode(new BoardPosition(row+2,col)))
				positions.add(down);
			if(!isBlockingNode(up)&&!isBlockingNode(new BoardPosition(row-2,col)))	
				positions.add(up);
		}

		// LEFT //RIGHT
		if(col > 0 && col < Map.get(row).size()-1) {
			BoardPosition left = new BoardPosition(row,col-1);
			BoardPosition right = new BoardPosition(row,col+1);	


			if(!isBlockingNode(left)&&!isBlockingNode(new BoardPosition(row,col-2)))
				positions.add(left);
			if(!isBlockingNode(right)&&!isBlockingNode(new BoardPosition(row,col+2)))	
				positions.add(right);		 	
		}
		
		return positions;
	}

    
    public void RecalculateGoalNodes() {
    	Goals = new ArrayList<BoardPosition>();
    	for(int row = 0; row < rows; ++row) {
    		 	
    		for(int col = 0; col < getColumnsCount(); ++col)
    		{    			
    			NodeType type = get(row, col);
    			if(type == NodeType.GOAL || type == NodeType.PLAYER_ON_GOAL || type == NodeType.BLOCK_ON_GOAL)
    				Goals.add(new BoardPosition(row, col));    		
    			
    		}
    	}
    }    
    
    
public NodeType get(int row, int col)
{
    if(row < 0 || row >= Map.size())
        return NodeType.INVALID;
    if(col < 0 || col >= Map.get(row).size())
        return NodeType.INVALID;
    
    return Map.get(row).get(col);
}

public NodeType get(BoardPosition pos)
{
    return get(pos.Row, pos.Column);
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

	public List<BoardPosition> getGoalNodes()
	{
		return Goals;
	}




	public boolean isValidPosition(int row, int col){
		if(row < 0 || row >= Map.size())
			throw new IllegalArgumentException("Position is out of bounds (x = " + row + ")");

		int diff = col - CurrentNode.Column + row - CurrentNode.Row;
		if(Math.abs(diff) > 1)
			throw new IllegalArgumentException("Can't move player more than one coordinate");

		NodeType type = Map.get(row).get(col);
		if(type == NodeType.BLOCK || type == NodeType.BLOCK_ON_GOAL)
		{
			throw new IllegalArgumentException ("Invalid destination");


		} else if(type != NodeType.SPACE && type != NodeType.GOAL) {
			// Wall or other invalid move
			//throw new IllegalArgumentException("Invalid move: \n" + CurrentNode + "\n  " + row + " " + col + " is " + type);
			return false;
		}
		return true;
	}



	public void movePlayer(Path path) {
		//	System.out.println("last pos: "+path.last().Row+" "+path.last().Column);
		//	path.getPath().remove(path.last());
		//System.out.println("last pos: "+path.last().Row+" "+path.last().Column);
		for(BoardPosition pos : path.getPath())
		{
			//	System.out.println("pos "+pos.Row+" "+pos.Column);
			//    		if(pos != null && pos.equals(CurrentNode))
			if(pos.equals(CurrentNode))
				continue;
			if(Map.get(pos.Row).get(pos.Column)==NodeType.BLOCK ||Map.get(pos.Row).get(pos.Column)==NodeType.BLOCK_ON_GOAL)
				continue;
			//                else if(pos != null)
			movePlayerTo(pos);
		}
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

	public void playerWalk(Direction dir,int row, int col){
		NodeType destType=Map.get(row).get(col);
		int oldRow=row;
		int oldCol=col;
		switch(dir)
		{
		case UP:
			oldRow++;
			break;
		case DOWN:
			oldRow--;   	
			break;
		case LEFT:
			oldCol++;
			break;
		case RIGHT:
			oldCol--;
			break;
		}

		NodeType oldPosType=Map.get(oldRow).get(oldCol);


		switch(destType)
		{
		case SPACE:
			Map.get(row).set(col, NodeType.PLAYER);
			updateHashCode(row, col, NodeType.SPACE, NodeType.PLAYER);
			break;
		case GOAL:
			Map.get(row).set(col, NodeType.PLAYER_ON_GOAL);
			updateHashCode(row, col, NodeType.SPACE, NodeType.PLAYER);   		
			break;
		case PLAYER:
			return;

		}

		if(oldPosType==NodeType.PLAYER_ON_GOAL){
			Map.get(oldRow).set(oldCol, NodeType.GOAL);
			updateHashCode(oldRow, oldCol, NodeType.PLAYER_ON_GOAL, NodeType.GOAL);
		}else{
			Map.get(oldRow).set(oldCol, NodeType.SPACE);
			updateHashCode(oldRow, oldCol, NodeType.PLAYER, NodeType.SPACE);
		}
		
		CurrentNode = new BoardPosition(row, col);	

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

		if(isValidPosition(row,col)){
			Direction dir = getDirection(CurrentNode, new BoardPosition(row,col));
			//	moveBlockTo(CurrentNode.Row, CurrentNode.Column, dir);
			if(isBlockBehind(dir,row,col)){
				pullBlock(dir,row,col);
			}else{

				playerWalk(dir,row,col);
			}
			/*	
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
			}*/

		//	CurrentNode = new BoardPosition(row, col);	
		}

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



	public List<BoardPosition> getGNodes()
	{
		List<BoardPosition> Gs = new ArrayList<>();
		for(int i = 0; i < Map.size(); i++)
		{
			for(int j = 0; j < Map.get(i).size(); j ++)
			{
				NodeType type = getNode(i,j);
				if( type == NodeType.GOAL || type ==NodeType.PLAYER_ON_GOAL)
				{
					Gs.add(new BoardPosition(i, j));
				}
			}
		}
		return Gs;
	}   


	public List<BoardPosition> getBlockNodes()
	{
		List<BoardPosition> blocks = new ArrayList<>();
		for(int i = 0; i < Map.size(); i++)
		{
			for(int j = 0; j < Map.get(i).size(); j ++)
			{
				NodeType type = getNode(i,j);
				if( type == NodeType.BLOCK || type == NodeType.BLOCK_ON_GOAL)
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


	public void pullBlock(Direction dir, BoardPosition to) {
		pullBlock(dir, to.Row, to.Column);
	}

	public void pullBlock(Direction dir, int row, int col) {
		NodeType dest = Map.get(row).get(col);
		int playerOriginRow=row;
		int playerOriginCol=col;
		int blockOriginRow=row;
		int blockOriginCol = col;
		NodeType playerOrigin;


		switch(dir)
		{
		case UP:
			playerOriginRow++;
			blockOriginRow = playerOriginRow+1;
			break;
		case DOWN:
			playerOriginRow--;   	
			blockOriginRow = playerOriginRow-1;
			break;
		case LEFT:
			playerOriginCol++;
			blockOriginCol=playerOriginCol+1;
			break;
		case RIGHT:
			playerOriginCol--;
			blockOriginCol=playerOriginCol-1;
			break;
		}


		playerOrigin=Map.get(playerOriginRow).get(playerOriginCol);	
		NodeType blockOrigin=Map.get(blockOriginRow).get(blockOriginCol);



		if(dest != NodeType.SPACE && dest != NodeType.GOAL)
			throw new IllegalArgumentException("Can't move player to that pos");

	

		//update the new pos player is moving to
		if(dest == NodeType.SPACE) { //if player is moving to space
			Map.get(row).set(col, NodeType.PLAYER);
			updateHashCode(row, col, NodeType.SPACE, NodeType.PLAYER);
		}else{//(dest == NodeType.GOAL)
			Map.get(row).set(col, NodeType.PLAYER_ON_GOAL);
			updateHashCode(row, col, NodeType.GOAL, NodeType.PLAYER_ON_GOAL);
		}

		//update the new pos block is moving to
	
		
		if(playerOrigin==NodeType.PLAYER){
			Map.get(playerOriginRow).set(playerOriginCol, NodeType.BLOCK);
			updateHashCode(playerOriginRow, playerOriginCol, NodeType.PLAYER, NodeType.BLOCK);
		}else{ //if player came from goal
			Map.get(playerOriginRow).set(playerOriginCol, NodeType.BLOCK_ON_GOAL);
			updateHashCode(playerOriginRow, playerOriginCol, NodeType.PLAYER_ON_GOAL, NodeType.BLOCK_ON_GOAL);
		}
		//update the old block position
		if(blockOrigin==NodeType.BLOCK){
			Map.get(blockOriginRow).set(blockOriginCol, NodeType.SPACE);
			updateHashCode(blockOriginRow, blockOriginCol, NodeType.BLOCK, NodeType.SPACE);
		}else{
			Map.get(blockOriginRow).set(blockOriginCol, NodeType.GOAL);
			updateHashCode(blockOriginRow, blockOriginCol, NodeType.BLOCK_ON_GOAL, NodeType.GOAL);
		}
		RecalculateGoalNodes();
		
			CurrentNode = new BoardPosition(row, col);	

	}






public BoardState getEndingState()
{

	BoardState endState = (BoardState) this.clone();

	// First remove player from map
//	initPlayerNode=endState.getPlayerNode();
	//sSystem.out.println("endstate initplayernode: "+initPlayerNode);
	NodeType t = endState.Map.get(endState.CurrentNode.Row).get(endState.CurrentNode.Column);
	if(t == NodeType.PLAYER)
	{
		endState.Map.get(endState.CurrentNode.Row).set(endState.CurrentNode.Column, NodeType.SPACE);
	}
	else if(t == NodeType.PLAYER_ON_GOAL)
	{
		endState.Map.get(endState.CurrentNode.Row).set(endState.CurrentNode.Column, NodeType.GOAL);
	}

	// Place all blocks on goal positions

	for(int r = 0; r < getRowsCount(); r++)
	{
		for(int c = 0; c < getColumnsCount(); c++)
		{
			NodeType type = endState.Map.get(r).get(c);
			if(type == NodeType.BLOCK)
			{
				endState.Map.get(r).set(c, NodeType.GOAL);
			}
			else if(type == NodeType.GOAL)
			{
				endState.Map.get(r).set(c, NodeType.BLOCK);
			}
		}
	}

	// Find out where the players end position can be
	// north, south, west and east positions are were the player can stand
	// next to block
	
	BoardPosition playerPos = null;

	for(BoardPosition p: endState.Goals)
	{
		BoardPosition north = new BoardPosition(p.Row-1, p.Column);
		if(endState.isOnlySpaceNode(north) && endState.isOnlySpaceNode(new BoardPosition(north.Row-1, north.Column)))
		{
			playerPos = north;
		}
		BoardPosition south = new BoardPosition(p.Row+1, p.Column);
		if(playerPos == null && endState.isOnlySpaceNode(south) && endState.isOnlySpaceNode(new BoardPosition(south.Row+1, south.Column)))
		{
			playerPos = south;
		}
		BoardPosition west = new BoardPosition(p.Row, p.Column-1);
		if(playerPos == null && endState.isOnlySpaceNode(west) && endState.isOnlySpaceNode(new BoardPosition(west.Row, west.Column-1)))
		{
			playerPos = west;
		}
		BoardPosition east = new BoardPosition(p.Row, p.Column+1);
		if(playerPos == null && endState.isOnlySpaceNode(east) && endState.isOnlySpaceNode(new BoardPosition(east.Row, east.Column+1)))
		{
			playerPos = east;
		}

		if(playerPos != null)
			break;
	}

	//   Position the player on resp. position        
	endState.Map.get(playerPos.Row).set(playerPos.Column, NodeType.PLAYER);
	endState.CurrentNode = playerPos;
	sortGoals();
	return endState;
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
	for(int row = 0; row < Map.size(); ++row) {
		for(int col = 0; col < Map.get(row).size(); ++col)
		{
			NodeType type = getNode(row, col);
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
public boolean isOnlySpaceNode(int r, int c)
{
	NodeType type = getNode(r, c);
	if(type == NodeType.INVALID)
		System.err.println("Referring to position " + r + ", " + c + " which refers to INVALID type");

	return 
			type == NodeType.SPACE;
}
public boolean isOnlySpaceNode(BoardPosition p)
{
	return isOnlySpaceNode(p.Row, p.Column);
}




}
