package sokoban.Tethik;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


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
	private int distanceMatrix[][];
	
	private BoardState board;
	private int rows;
	private int cols;
	private BlockPathFinder pathfinder = new BlockPathFinder();
	private LiveAnalyser deadlockfinder = new LiveAnalyser();
	
	public Analyser(BoardState board)
	{
		this.board = board;
		constructTableAndWorkbench();
		// Hitta dåliga positions
		analyse();
		// Hitta distanser?
		mapDistancesToGoal(new BoardState(workbench));
	}
	
	private void constructTableAndWorkbench() {
		rows = board.getRowsCount();
		cols = Integer.MIN_VALUE;
		
		for(int row = 0; row < rows; ++row)
			cols = Math.max(cols, board.getColumnsCount(row));
		
		badTable = new boolean[rows][cols];
		workbench = new NodeType[rows][cols];
		distanceMatrix = new int[rows][cols];
		
		for(int row = 0; row < rows; ++row)
			for(int col = 0; col < cols; ++col) {
				NodeType type = board.getNode(row, col);				
				
				// Remove blocks from the map
				if(type == NodeType.BLOCK || type == NodeType.PLAYER)
					type = NodeType.SPACE;
				if(type == NodeType.BLOCK_ON_GOAL)
					type = NodeType.GOAL;
				
				workbench[row][col] = type;
				distanceMatrix[row][col] = Integer.MAX_VALUE;
			}	
	}
	
	private void mapDistancesToGoal(BoardState board)
	{
		for(int row = 0; row < rows; ++row)
			for(int col = 0; col < cols; ++col) {
				distanceMatrix[row][col] = Integer.MAX_VALUE;
			}
		
		Queue<BoardPosition> positions = new LinkedList<>();
		Queue<Integer> distances = new LinkedList<Integer>();
		Set<BoardPosition> visited = new HashSet<>();
		
		for(BoardPosition goal : board.getGoalNodes()) {
		
			positions.clear();
			distances.clear();
			visited.clear();
			
			positions.add(goal);
			distances.add(0);
			visited.add(goal);
			
			if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL) {
				continue;
			}
			
			distanceMatrix[goal.Row][goal.Column] = 0;
			
			// BFS
			while(!positions.isEmpty()) {
			
				BoardPosition pos = positions.poll();
				int distance = distances.poll();
				
				// Uppdatera positions i matrisen
				distanceMatrix[pos.Row][pos.Column] = Math.min(distanceMatrix[pos.Row][pos.Column], distance);
				
				++distance;
				
				/*				
				if(board.getNode(pos) == NodeType.BLOCK || board.getNode(pos) == NodeType.BLOCK_ON_GOAL)
					continue;
				*/
				
				
				for(BoardPosition neighbour : board.getFromNeighbours(pos)) {
					if(visited.contains(neighbour)) 
						continue;
					
					NodeType node = board.getNode(neighbour);
					if(node == NodeType.WALL || node == NodeType.BLOCK_ON_GOAL || node == NodeType.INVALID) 
						continue;
									
					positions.add(neighbour);
					distances.add(distance);
					visited.add(neighbour);
				}
			}	
			
			
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
	 * @deprecated Freds liveanalyser borde ta detta nu. eller inte? för slö :(
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
	
	
	private void printDistanceMatrix(BoardState board) {
		mapDistancesToGoal(board);
		
		StringBuilder builder = new StringBuilder();		
		for(int row = 0; row < rows; ++row) {
			for(int col = 0; col < cols; ++col) {
				NodeType type = workbench[row][col];
				//(badTable[row][col]) ? "x" : 
				String c = distanceMatrix[row][col] > 9 ? " " : "" + distanceMatrix[row][col];
				if(type == NodeType.GOAL)
					c = ".";
				else if(type == NodeType.WALL)
					c = "#";
				builder.append(c).append("");
				
			}
			builder.append("\n");
			
		}
		System.out.println(builder.toString());
	}
	
	public int getHeuristicValue(BoardState board) {
		if(board.isWin()) {				
			return  Integer.MAX_VALUE;
		}
		
		
		if(has4x4Block(board))
		{
			return Integer.MIN_VALUE;
		}
		
		//mapDistancesToGoal(board);
		
		int val = 0;
		List<BoardPosition> blocks = board.getBlockNodes();
		int number_of_unsolved_blocks = 0;
		int number_of_stuck_blocks = 0;
		for(BoardPosition block : blocks)
		{	
			if(board.getNode(block) == NodeType.BLOCK_ON_GOAL) {
				continue;
			}
			
			if(board.getNode(block) == NodeType.BLOCK && board.isInCorner(block))
				return Integer.MIN_VALUE;
			
			Set<BoardPosition> goalNodes = new HashSet<BoardPosition>();
			for(BoardPosition goal : board.getGoalNodes()) {
				if(board.getNode(goal) == NodeType.GOAL)
					goalNodes.add(goal);
			}
			
			if(pathfinder.getPath(board, block, goalNodes) == null) {
				number_of_stuck_blocks++;
			}
			
			/*
			if(deadlockfinder.isBadState(board, block)) {
				return Integer.MIN_VALUE;					
			}
			*/
			
			
			int mindistToGoal = distanceMatrix[block.Row][block.Column];
			if(mindistToGoal == Integer.MAX_VALUE)
				return Integer.MIN_VALUE;
			number_of_unsolved_blocks++;
			val -= mindistToGoal;
		}
		
		if(number_of_stuck_blocks >= number_of_unsolved_blocks)
			return Integer.MIN_VALUE;
		
		return (int) (val * (Math.pow(2, number_of_unsolved_blocks)));
		
	}
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/disttest3");
		System.out.println(board);
		Analyser analyser = new Analyser(board);
		System.out.println(analyser);
		analyser.printDistanceMatrix(board);
		
		/*
		board = BoardState.getBoardFromFile("testing/disttest2");
		System.out.println(board);
		analyser = new Analyser(board);
		System.out.println(analyser);
		analyser.printDistanceMatrix(board);
		*/
	}
}
