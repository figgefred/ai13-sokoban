package sokoban.Tethik;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import sokoban.BoardPosition;
import sokoban.NodeType;


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
	private int distanceMatrix[][][];
	private int goalDist[];
	private int blockDist[];
	
	private BoardState board;
	private int rows;
	private int cols;
	private BlockPathFinder pathfinder = new BlockPathFinder();
	//private HopcroftKarpMatching bipartiteMatcher = new HopcroftKarpMatching();
	
	public Analyser(BoardState board)
	{
		this.board = board;
		constructTableAndWorkbench();
		// Hitta dåliga positions
		analyse();
		// Hitta distanser?
		mapDistancesToGoals(new BoardState(workbench));
	}
	
	private void constructTableAndWorkbench() {
		rows = board.getRowsCount();
		cols = Integer.MIN_VALUE;
		
		for(int row = 0; row < rows; ++row)
			cols = Math.max(cols, board.getColumnsCount(row));
		
		badTable = new boolean[rows][cols];
		workbench = new NodeType[rows][cols];
		goalDist = new int[board.getGoalNodes().size()];
		blockDist = new int[board.getGoalNodes().size()];
		distanceMatrix = new int[board.getGoalNodes().size()][rows][cols];
		
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
	
	private void mapDistancesToGoals(BoardState board)
	{
		int i = 0;
		for(@SuppressWarnings("unused") BoardPosition goal : board.getGoalNodes()) {		
			for(int row = 0; row < rows; ++row)
				for(int col = 0; col < cols; ++col) {
					distanceMatrix[i][row][col] = Integer.MAX_VALUE;
				}
			++i;
		}
		
		Queue<BoardPosition> positions = new LinkedList<>();
		Queue<Integer> distances = new LinkedList<Integer>();
		Set<BoardPosition> visited = new HashSet<>();
		
		i = 0;
		for(BoardPosition goal : board.getGoalNodes()) {
		
			if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL)
				continue;
			
			positions.clear();
			distances.clear();
			visited.clear();
			
			positions.add(goal);
			distances.add(0);
			visited.add(goal);
			
			if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL) {
				continue;
			}
			
			distanceMatrix[i][goal.Row][goal.Column] = 0;
			
			// BFS
			while(!positions.isEmpty()) {
			
				BoardPosition pos = positions.poll();
				int distance = distances.poll();
				
				// Uppdatera positions i matrisen
				distanceMatrix[i][pos.Row][pos.Column] = distance;
				
				++distance;				

				/*
				if(board.getNode(pos) == NodeType.BLOCK || board.getNode(pos) == NodeType.BLOCK_ON_GOAL)
					continue;						
				*/
				
				for(BoardPosition neighbour : board.getFromNeighbours(pos)) {
					if(visited.contains(neighbour)) 
						continue;
					
					NodeType node = board.getNode(neighbour);
					if(node == NodeType.WALL || node == NodeType.INVALID) 
						continue;
									
					positions.add(neighbour);
					distances.add(distance);
					visited.add(neighbour);
				}
			}	
			
			i++;			
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
	
	
	private void printDistanceMatrix(BoardState board) {
		//mapDistancesToGoals(board);
		
		StringBuilder builder = new StringBuilder();		
		for(int i = 0; i < distanceMatrix.length; i++)
		{
			for(int row = 0; row < rows; ++row) {
				for(int col = 0; col < cols; ++col) {
					NodeType type = workbench[row][col];
					//(badTable[row][col]) ? "x" : 
					String c = distanceMatrix[i][row][col] > 9 ? " " : "" + distanceMatrix[i][row][col];
					if(type == NodeType.WALL)
						c = "#";
					else if(distanceMatrix[i][row][col] == Integer.MAX_VALUE)
						c = "x";
					else if(type == NodeType.GOAL)
						c = ".";					
		
					builder.append(c).append("");
					
				}
				builder.append("\n");				
			}
		}
		System.out.println(builder.toString());
	}
	
	public int getHeuristicValue(BoardState board) {
		this.board = board;
		
		if(board.isWin()) {				
			return Integer.MAX_VALUE;
		}
		
		
		if(has4x4Block(board))
		{
			return Integer.MIN_VALUE;
		}
		
		
		//mapDistancesToGoals(board);
		
		for(int i = 0; i < goalDist.length; i++) {
			goalDist[i] = Integer.MAX_VALUE;
			blockDist[i] = Integer.MAX_VALUE;
		}
		
		
		List<BoardPosition> blocks = board.getBlockNodes();
		
		//int number_of_stuck_blocks = 0;
		
		//Set<BoardPosition> goalNodes = new HashSet<BoardPosition>();
		/*
		for(BoardPosition goal : board.getGoalNodes()) {
			if(board.getNode(goal) == NodeType.GOAL)
				goalNodes.add(goal);
		}*/
		
		/*
		HashMap<BoardPosition, List<BoardPosition>> reachMap = new HashMap<>(); 
		
		for(BoardPosition goal : board.getGoalNodes())
			reachMap.put(goal, new ArrayList<BoardPosition>());
		*/
		
		int b = 0; 
		for(BoardPosition block : blocks)
		{

			if(board.getNode(block) == NodeType.BLOCK_ON_GOAL)
				blockDist[b] = 0;
			else if(board.isInCorner(block)) {// || isBadPosition(block))
				return Integer.MIN_VALUE;		
			}
			b++;
			
		} 
		
		int i = 0;
		for(BoardPosition goal : board.getGoalNodes())
		{				
			if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL) {
				goalDist[i++] = 0;
				continue;
			}
			
			b = 0; 
			for(BoardPosition block : blocks)
			{
				int dist = distanceMatrix[i][block.Row][block.Column];
				goalDist[i] = Math.min(dist, goalDist[i]);
				blockDist[b] = Math.min(dist, blockDist[b++]);
			}
			
				
			++i;
		}	
		
		int val = 0;
		i = 0;
		for(BoardPosition goal : board.getGoalNodes()) {
			if(goalDist[i] == Integer.MAX_VALUE || blockDist[i] == Integer.MAX_VALUE)
			{
				return Integer.MIN_VALUE;
			}
			
			val -= goalDist[i];
			val -= blockDist[i];
			
			System.out.println(goalDist[i] + ", " + blockDist[i]);
			++i;
		}
		
		return val;		
	}
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/disttest");
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
