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
 * 	p = PreAnalyser(board)
 *  
 * @author tethik
 *
 */
public class Analyser {
	
	private boolean badTable[][];
	private NodeType workbench[][];
	private int distanceMatrix[][][];
	
	private BoardState board;
	private int rows;
	private int cols;
	private BlockPathFinder pathfinder = new BlockPathFinder();
	
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
		;
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
					
				if(board.getNode(pos) == NodeType.BLOCK || board.getNode(pos) == NodeType.BLOCK_ON_GOAL)
					continue;						
				
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
	
	/***
	 * Hopcroft-Karp maximal bipartite matching
	 * http://en.wikipedia.org/wiki/Hopcroft%E2%80%93Karp_algorithm
	 * @param map
	 * @return
	 */
	private int maxBipartiteMatch(HashMap<BoardPosition, List<BoardPosition>> map)
	{
		BoardPosition nil = new BoardPosition(-1,-1);
		HashMap<BoardPosition, BoardPosition> goal_pairings = new HashMap<BoardPosition, BoardPosition>();
		HashMap<BoardPosition, BoardPosition> block_pairings = new HashMap<BoardPosition, BoardPosition>();
		HashMap<BoardPosition, Integer> dist = new HashMap<BoardPosition, Integer>();
		
		Set<BoardPosition> goalNodes = board.getGoalNodes();
		List<BoardPosition> blockNodes = board.getBlockNodes();
		
		for(BoardPosition goal : goalNodes) {			
			goal_pairings.put(goal, nil);
		}		
		for(BoardPosition block : blockNodes) {
			block_pairings.put(block, nil);
		}
		
		/*
		System.out.println(map);
		System.out.println(goal_pairings);
		System.out.println(block_pairings);
		System.out.println(blockNodes);
		*/
		//goal_pairings.put(nil, nil);
			
		
		int matching = 0;
		boolean bfs = true;
		
		while(bfs) {			
			Queue<BoardPosition> queue = new LinkedList<BoardPosition>();			
			
			
			for(BoardPosition goal : goalNodes) {
				if(goal_pairings.get(goal).equals(nil)) {
					queue.add(goal);
					dist.put(goal, 0);
				} else {
					dist.put(goal, Integer.MAX_VALUE);
				}
			}
			
			
			dist.put(nil, Integer.MAX_VALUE);			
			
			while(!queue.isEmpty()) {
				BoardPosition v = queue.poll();
				
				if(dist.get(v) >= dist.get(nil))
					continue;

				for(BoardPosition u : map.get(v))
				{
					BoardPosition pg2 = block_pairings.get(u);	
					if(dist.get(pg2) == Integer.MAX_VALUE) {
						dist.put(pg2, dist.get(v) + 1);
						queue.add(pg2);
					}
										
				}
			}
			
			bfs = dist.get(nil) != Integer.MAX_VALUE;
			
			if(!bfs) {
				break;
			}
			
			// Dfs part..
			for(BoardPosition v : goalNodes) {				
				if(goal_pairings.get(v).equals(nil) && dfsFindMaxMatch(map, goal_pairings, block_pairings, dist, v))
					matching++;						
				
			}
			//System.out.println(matching);
		}
		
		return matching;
	}
	

	
	private boolean dfsFindMaxMatch(HashMap<BoardPosition, List<BoardPosition>> map,
									HashMap<BoardPosition, BoardPosition> goal_pairings,
									HashMap<BoardPosition, BoardPosition> block_pairings,
									HashMap<BoardPosition, Integer> dist,
									BoardPosition v) {
		
		BoardPosition nil = new BoardPosition(-1,-1);
		if(v.equals(nil))
			return true;
		
		if(map.get(v) == null)
		{
			System.out.println(v);
			System.out.println(map.get(v));
			System.exit(0);
		}
		
		for(BoardPosition u : map.get(v))
		{
			BoardPosition pg2 = block_pairings.get(u);
			/*
			System.out.println(u);
			System.out.println(board.getNode(u));
			System.out.println(pg2);
			System.out.println(board.getNode(pg2));
			*/
			if(dist.get(pg2) == dist.get(v) + 1)
				if(dfsFindMaxMatch(map, goal_pairings, block_pairings, dist, pg2))
				{
					block_pairings.put(u, v);
					goal_pairings.put(v, u);
					return true;
				}
			
		}
		
		dist.put(v, Integer.MAX_VALUE);
		return false;	
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
		
		
		mapDistancesToGoals(board);
		
		int val = 0;
		List<BoardPosition> blocks = board.getBlockNodes();
		int number_of_unsolved_blocks = 0;
		//int number_of_stuck_blocks = 0;
		
		//Set<BoardPosition> goalNodes = new HashSet<BoardPosition>();
		/*
		for(BoardPosition goal : board.getGoalNodes()) {
			if(board.getNode(goal) == NodeType.GOAL)
				goalNodes.add(goal);
		}*/
		
		HashMap<BoardPosition, List<BoardPosition>> reachMap = new HashMap<>(); 
		
		for(BoardPosition goal : board.getGoalNodes())
			reachMap.put(goal, new ArrayList<BoardPosition>());
		
		for(BoardPosition block : blocks)
		{
			if(board.isInCorner(block))
				return Integer.MIN_VALUE;
			
			
			int mindistToGoal = Integer.MAX_VALUE;
			int i = 0;
			for(BoardPosition goal : board.getGoalNodes())
			{
				if(board.getNode(goal) != NodeType.BLOCK_ON_GOAL)
					mindistToGoal = Math.min(distanceMatrix[i][block.Row][block.Column], mindistToGoal);			
				
				if(mindistToGoal != Integer.MAX_VALUE)					
					reachMap.get(goal).add(block);
				
				++i;
			}
					
			if(board.getNode(block) == NodeType.BLOCK_ON_GOAL) {			
				continue;
			}
			
			if(mindistToGoal == Integer.MAX_VALUE)
				return Integer.MIN_VALUE;
			number_of_unsolved_blocks++;
			val -= mindistToGoal;
		}
		
		int matching = maxBipartiteMatch(reachMap);
		//System.out.println(matching);
		if(matching < board.getGoalNodes().size())
			return Integer.MIN_VALUE;
		
		/*
		for(BoardPosition goal : board.getGoalNodes())
		{
			System.out.println(goal.toString() + " || " + reachMap.get(goal).size() + " || " + reachMap.get(goal));
			
			if(reachMap.get(goal).size() == 0)
				return Integer.MIN_VALUE;
		}
		System.out.println();
		
		/*
		if(number_of_stuck_blocks >= number_of_unsolved_blocks)
			return Integer.MIN_VALUE;
		*/
		
		return val * number_of_unsolved_blocks;		
	}
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest51");
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
