package sokoban.Tethik;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


/***
 * PreAnalyser: analyse the board beforehand to calculate positions in which blocks will get stuck in.
 * Usage:
 * 	p = Analyser(board)
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
	private HopcroftKarpMatching bipartiteMatcher = new HopcroftKarpMatching();
	private Settings settings;
	
	public Analyser(BoardState board, Settings settings)
	{
		this.settings = settings;
		this.board = board;
		constructTableAndWorkbench();
		// Hitta distanser?
		mapDistancesToGoals(new BoardState(workbench));
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	private void constructTableAndWorkbench() {
		rows = board.getRowsCount();
		cols = board.getColumnsCount();
		
		badTable = new boolean[rows][cols];
		workbench = new NodeType[rows][cols];
		goalDist = new int[board.getGoalNodes().size()];
		blockDist = new int[board.getGoalNodes().size()];
		distanceMatrix = new int[board.getGoalNodes().size()][rows][cols];
		
		for(int row = 0; row < rows; ++row)
			for(int col = 0; col < cols; ++col) {
				NodeType type = board.get(row, col);				
				
				// Remove blocks from the map
				if(type == NodeType.BLOCK || type == NodeType.PLAYER)
					type = NodeType.SPACE;
				if(type == NodeType.BLOCK_ON_GOAL)
					type = NodeType.GOAL;
				
				workbench[row][col] = type;
				badTable[row][col] = true;
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
			
			positions.clear();
			distances.clear();
			visited.clear();
			
			positions.add(goal);
			distances.add(0);
			visited.add(goal);
			
			distanceMatrix[i][goal.Row][goal.Column] = 0;
			
			// BFS
			while(!positions.isEmpty()) {
			
				BoardPosition pos = positions.poll();
				int distance = distances.poll();
				
				// Uppdatera positions i matrisen
				distanceMatrix[i][pos.Row][pos.Column] = distance++;
				if(distance > 1)
					badTable[pos.Row][pos.Column] = false;
				
				for(BoardPosition neighbour : board.getFromNeighbours(pos)) {
					if(visited.contains(neighbour)) 
						continue;
					
					NodeType node = board.get(neighbour);
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
	
	public boolean isBadPosition(int Row, int Col) {		
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
				else 
					if(type == NodeType.WALL)
					c = '#';
				builder.append(c);
				
			}
			builder.append("\n");
			
		}
		return builder.toString();
	}
	
	
	private void printDistanceMatrix(BoardState board) {	
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
					else if(badTable[row][col])
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
		
		for(int i = 0; i < goalDist.length; i++) {
			goalDist[i] = Integer.MAX_VALUE;
			blockDist[i] = Integer.MAX_VALUE;
		}
		
		List<BoardPosition> blocks = board.getBlockNodes();	
		int b = 0; 
		for(BoardPosition block : blocks)
		{			
			 if(board.get(block) == NodeType.BLOCK_ON_GOAL) {
				blockDist[b] = 0;
				if(isBadPosition(block))
					blockDist[b] = -100;
			 } else if(isBadPosition(block) || is4x4Block(board, block)) {
				return Integer.MIN_VALUE;		
			} 
			b++;	
		} 
		
		HashMap<BoardPosition, List<BoardPosition>> reachMap = new HashMap<>(); 
		int i = 0;
		for(BoardPosition goal : board.getGoalNodes())
		{		
			reachMap.put(goal, new ArrayList<BoardPosition>());			
			
			if(board.get(goal) == NodeType.BLOCK_ON_GOAL) {
				goalDist[i] = 0;
			}
			
			b = 0; 
			for(BoardPosition block : blocks)
			{
				int dist = distanceMatrix[i][block.Row][block.Column];
				if(dist < Integer.MAX_VALUE)
				{
					reachMap.get(goal).add(block);
				}
				
				goalDist[i] = Math.min(dist, goalDist[i]);
				
				if(goalDist[i] > 0)
					blockDist[b] = Math.min(dist, blockDist[b++]);
			}			
			++i;
		}			
		
		if(settings.ANALYSER_BIPARTITE_MATCHING && bipartiteMatcher.maxBipartiteMatch(reachMap, board) < board.getGoalNodes().size())
			return Integer.MIN_VALUE;
		
		int val = 0;
		for(i = 0; i < board.getGoalNodes().size(); ++i) {
			if(goalDist[i] == Integer.MAX_VALUE || blockDist[i] == Integer.MAX_VALUE)
			{
				return Integer.MIN_VALUE;
			}
			
			val -= goalDist[i];
			val -= blockDist[i];
		}
		
		return val;		
	}
	
	public int getHeuristicValue(BoardState board, BoardPosition block, int goalindex) {
		if(is4x4Block(board, block) || (board.get(block) != NodeType.BLOCK_ON_GOAL && isBadPosition(block)))
			return Integer.MIN_VALUE;
		
		if(distanceMatrix[goalindex][block.Row][block.Column] == Integer.MAX_VALUE)
			return Integer.MIN_VALUE;
		
		return -distanceMatrix[goalindex][block.Row][block.Column];
	}
	
	public int getHeuristicValue(BoardState board, BoardPosition block) {
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < board.getGoalNodes().size(); ++i)
			min = Math.min(min, distanceMatrix[i][block.Row][block.Column]);
		
		return min;
	}
	
	public int getLowerBound(BoardState board) {
		for(int i = 0; i < goalDist.length; i++) {
			goalDist[i] = Integer.MAX_VALUE;
			blockDist[i] = Integer.MAX_VALUE;
		}
		
		List<BoardPosition> blocks = board.getBlockNodes();
		
		int b = 0; 
		for(BoardPosition block : blocks)
		{			
			 if(board.get(block) == NodeType.BLOCK_ON_GOAL) 
				blockDist[b] = 0;
			b++;	
		} 
		
		int i = 0;
		for(BoardPosition goal : board.getGoalNodes())
		{		
			if(board.get(goal) == NodeType.BLOCK_ON_GOAL) {
				goalDist[i] = 0;
			}
			
			b = 0; 
			for(BoardPosition block : blocks)
			{
				int dist = distanceMatrix[i][block.Row][block.Column];
				
				goalDist[i] = Math.min(dist, goalDist[i]);
				
				if(goalDist[i] > 0)
					blockDist[b] = Math.min(dist, blockDist[b++]);
			}			
			++i;
		}			
		
		int val = 0;
		for(i = 0; i < goalDist.length; ++i) {
			if(goalDist[i] == Integer.MAX_VALUE || blockDist[i] == Integer.MAX_VALUE)
			{
				return Integer.MIN_VALUE;
			}
			
			val += goalDist[i];
			val += blockDist[i];
		}
		
		return val;		
	}
	
	private boolean is4x4BlockTopLeftCorner(BoardState board, BoardPosition pos) {
		if(!board.isBlockingNode(pos))
			return false;
		
		NodeType nodes[] = new NodeType[] {
			board.get(pos.Row, pos.Column),
			board.get(pos.Row, pos.Column+1),
			board.get(pos.Row+1, pos.Column),
			board.get(pos.Row+1, pos.Column+1)
		};		
		
		boolean atLeastOneIsBlock = false;
		for(NodeType node : nodes)
		{
			if(!board.isBlockingNode(node))
				return false;			
			atLeastOneIsBlock = atLeastOneIsBlock || node == NodeType.BLOCK;
		}
		
		return (atLeastOneIsBlock);		
	}
	
	private boolean is4x4Block(BoardState board, BoardPosition block) {
		BoardPosition leftDown = new BoardPosition(block.Row-1, block.Column-1);
		BoardPosition leftTop = new BoardPosition(block.Row, block.Column-1);
		BoardPosition rightDown = new BoardPosition(block.Row-1, block.Column);
		return is4x4BlockTopLeftCorner(board, block) 
				|| is4x4BlockTopLeftCorner(board, leftDown)
				|| is4x4BlockTopLeftCorner(board, rightDown)
				|| is4x4BlockTopLeftCorner(board, leftTop);
	}
	
	@SuppressWarnings("unused")
	private boolean has4x4Block(BoardState board) {		
		for(int row = 0; row < board.getRowsCount() - 1; ++row)			
			for(int col = 0; col < board.getColumnsCount() - 1; ++col) {				
				if(is4x4BlockTopLeftCorner(board, new BoardPosition(row, col)))
					return true;			
				
			}		
		return false;		
	}
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("test100/test001.in");
		System.out.println(board);
		Analyser analyser = new Analyser(board, new Settings());
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
