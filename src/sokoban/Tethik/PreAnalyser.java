package sokoban.Tethik;

import java.io.IOException;

import sokoban.BoardPosition;
import sokoban.Path;
import sokoban.types.NodeType;
import sokoban.Constants;

/***
 * PreAnalyser: analyse the board beforehand to calculate positions in which blocks will get stuck in.
 * Usage:
 * 	p = PreAnalyser(board)
 *  
 * @author tethik
 *
 */
public class PreAnalyser {
	
	private boolean badTable[][];
	private NodeType workbench[][];
	private BoardState board;
	private int rows;
	private int cols;
	private BlockPathFinder pathfinder = new BlockPathFinder();
	
	public PreAnalyser(BoardState board)
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
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/level6");
		System.out.println(board);
		PreAnalyser analyser = new PreAnalyser(board);
		System.out.println(analyser);
	}
}
