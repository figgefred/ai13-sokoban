package sokoban.Tethik;

import java.io.IOException;
import java.util.List;

import sokoban.BoardPosition;
import sokoban.Path;
import sokoban.types.NodeType;

/***
 * PreAnalyser: analyse the board beforehand to calculate positions in which blocks will get stuck in.
 * Usage:
 * 	p = PreAnalyser(board)
 *  
 * @author tethik
 *
 */
public class Analyser {
	
	private boolean badTable[][];
	private NodeType workbench[][];
	private BoardState board;
	private int rows;
	private int cols;
	private BlockPathFinder pathfinder = new BlockPathFinder();
	
	public Analyser(BoardState board)
	{
		this.board = board;
		constructTableAndWorkbench();
		analyse();
	}
	
	private void constructTableAndWorkbench() {
		rows = board.getRowsCount();
		cols = Integer.MIN_VALUE;
		
		for(int row = 0; row < rows; ++row)
			cols = Math.max(cols, board.getColumnsCount(row));
		
		badTable = new boolean[rows][cols];
		workbench = new NodeType[rows][cols];
		
		for(int row = 0; row < rows; ++row)
			for(int col = 0; col < cols; ++col) {
				NodeType type = board.getNode(row, col);				
				
				// Remove blocks from the map
				if(type == NodeType.BLOCK || type == NodeType.PLAYER)
					type = NodeType.SPACE;
				if(type == NodeType.BLOCK_ON_GOAL)
					type = NodeType.GOAL;
				
				workbench[row][col] = type;
			}
		
	}
	
	private void analyse() {
		for(int row = 0; row < rows; ++row)
			for(int col = 0; col < cols; ++col) {
				NodeType type = workbench[row][col];
				
				if(type == NodeType.WALL || type == NodeType.INVALID) {
					badTable[row][col] = true;
					continue;
				}
				
				if(type == NodeType.GOAL)
				{
					badTable[row][col] = false;
					continue;
				}				
				
				workbench[row][col] = NodeType.PLAYER;
				BoardState testBoard = new BoardState(workbench);
				//System.out.println(row + " " + col);
				//System.out.println(testBoard);				
				Path path = pathfinder.getPath(testBoard, new BoardPosition(row, col), board.getGoalNodes());
				badTable[row][col] = path == null;
				// reset
				workbench[row][col] = type;
			}
	}
	
	public boolean isBadPosition(int Row, int Col)
	{
		return badTable[Row][Col];
	}
	
	public boolean isBadPosition(BoardPosition pos) {
		return isBadPosition(pos.Row, pos.Column);
	}
	
	/***
	 * Kanske borde abstraheras till vissa mönster?
	 * @param board
	 * @return
	 */
	private boolean has4x4Block(BoardState board) {
		
		for(int row = 0; row < rows - 1; ++row)
			mainloop:
			for(int col = 0; col < cols - 1; ++col) {
				
				if(!board.isBlockingNode(new BoardPosition(row, col)))
					continue;
				
				NodeType nodes[] = new NodeType[] {
					board.getNode(row, col),
					board.getNode(row, col+1),
					board.getNode(row+1, col),
					board.getNode(row+1, col+1)
				};
				
				boolean atLeastOneIsBlock = false;
				for(NodeType node : nodes)
				{
					if(!board.isBlockingNode(node))
						continue mainloop;
					
					atLeastOneIsBlock = atLeastOneIsBlock || node == NodeType.BLOCK;
				}
				
				if(atLeastOneIsBlock) {
					//System.out.println("found 4x4 block at " + row + " " + col);					
					return true;
				}
				
			}
		
		return false;		
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(int row = 0; row < rows; ++row) {
			for(int col = 0; col < cols; ++col) {
				NodeType type = workbench[row][col];
				char c = (badTable[row][col]) ? 'x' : ' ';
				if(type == NodeType.GOAL)
					c = '.';
				else if(type == NodeType.WALL)
					c = '#';
				builder.append(c);
				
			}
			builder.append("\n");
			
		}
		return builder.toString();
	}
	
	public int getHeuristicValue(BoardState board) {
		if(board.isWin()) {				
			return  Integer.MAX_VALUE;
		}
		
		if(has4x4Block(board))
		{
			return Integer.MIN_VALUE;
		}
		
		int val = 0;
		List<BoardPosition> blocks = board.getBlockNodes();
		for(BoardPosition block : blocks)
		{		
			if(board.getNode(block) == NodeType.BLOCK_ON_GOAL) {					
				continue;
			}
			
			if(isBadPosition(block)) {
				return Integer.MIN_VALUE;					
			}
			
			int mindistToGoal = Integer.MAX_VALUE;
			for(BoardPosition goal : board.getGoalNodes())					
			{
				if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL)
				{						
					continue;
				}
				
				mindistToGoal = Math.min(mindistToGoal, block.DistanceTo(goal));
			}
			val -= mindistToGoal;
		}
		return val;
		
	}
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/pushtest2");
		System.out.println(board);
		Analyser analyser = new Analyser(board);
		System.out.println(analyser);
	}
}
