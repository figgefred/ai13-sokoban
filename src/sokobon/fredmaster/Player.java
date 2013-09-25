package sokoban.Tethik;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.Algorithms.AStar_Path;
import sokoban.Algorithms.BaseImpl;
import sokoban.Algorithms.ISearchAlgorithmPath;
import sokoban.types.NodeType;

/***
 * A* variant on boardstate
 * @author tethik
 *
 */
public class Player {	
	private BaseImpl pathfinder = new AStar_Path();
	private Queue<Move> openSet;
    private HashSet<BoardState> closedSet;
    
	private final double ConstantWeight;
	private final double EstimateWeight;
  

	private BoardState initialState;
	
	public Player(BoardState initialState)
	{		
		this.initialState = initialState;
		ConstantWeight = 0;
		EstimateWeight = 0;
	}

	public Move getVictoryPath(Move initialPosition)
	{
		openSet = new PriorityQueue<Move>();
		closedSet = new HashSet<BoardState>();
    	openSet.add(initialPosition);
    	
    	int c = 0;
        while(!openSet.isEmpty())
        {
        	Move node = openSet.poll();
        	System.out.println(openSet.size());
        	System.out.println(node.path.getPath().size() + ", " + node.getHeuristicValue() + ", " + closedSet.size() + ", " + node.board.hashCode());
        	System.out.println(node.board);
        	
        	if(node.board.isWin())
        	{       		
        		return node;
        	}       	        	
        	
        	Integer tentative_g = node.getHeuristicValue() + 1;
        	
        	for(Move neighbour: node.getNextMoves())
        	{	       		                		
        		if(neighbour.board.isWin())
        			return neighbour;
        		Integer to_g = neighbour.getHeuristicValue() - 200;
        		
        		//System.out.println(neighbour.board);
        		//System.out.println(to_g);
        		//System.out.println(tentative_g);
        		
        		
        		if (closedSet.contains(neighbour.board) || to_g > 1000 || to_g < -1000) {        			
                	continue;
        		}
        		
        		// && tentative_g > to_g)
        		        			
    			openSet.add(neighbour);
        		        		
        	}
        	
        	
        	if(closedSet.contains(node.board)) {        		
        		System.err.println("hash collision!");
        	}
        	closedSet.add(node.board);
        	
        	 /*      	
        	if(c == 2)
        		return null;
        	c++; */
        	
        }
        
        System.out.println("No path found?");        		
		
		return null;
	}
		
	
	
	
	public void play() throws InterruptedException {				
		
		Move initial = new Move();
		initial.board = initialState;
		initial.path = new Path();
		
		
		Move win = getVictoryPath(initial);
		if(win != null) {
			System.out.println(win.board);
			System.out.println(win.path);
		}
		
		/*
		for(Move nextMove : initial.getNextMoves())
		{
			System.out.println(nextMove.board);
			System.out.println(nextMove.path);
		}*/
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");
		
		System.out.println(board);
		Player noob = new Player(board);
		noob.play();
	}
	
	public class Move implements Comparable<Move> {
		public BoardState board;
		public Path path;
		private Integer heuristic_value = null;
		
		public int getHeuristicValue() {
			if(heuristic_value != null)
				return heuristic_value;
			
			if(board.isWin()) {				
				heuristic_value = Integer.MAX_VALUE;
				return heuristic_value;
			}
			
			//int val = -path.getPath().size();
			int val = 0;
			List<BoardPosition> blocks = board.getBlockNodes();
			for(BoardPosition block : blocks)
			{
				
				if(board.getNode(block) == NodeType.BLOCK_ON_GOAL) {
					val += 100;
					continue;
				}
				
				if(board.isInCorner(block)) {
					heuristic_value = Integer.MIN_VALUE;
					return heuristic_value;					
				}
				
				int mindistToGoal = Integer.MAX_VALUE;
				for(BoardPosition goal : board.getGoalNodes())					
				{
					if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL)
						continue;
					
					mindistToGoal = Math.min(mindistToGoal, block.DistanceTo(goal));
				}
				val -= mindistToGoal;
			}
			
			return val;
			/*
			int val = 0;
			List<BoardPosition> blocks = board.getBlockNodes();
			for(BoardPosition block : blocks)
			{
				
				if(board.getNode(block) == NodeType.BLOCK_ON_GOAL) {
					val += 100;
					continue;
				}
				
				if(board.isInCorner(block)) {
					heuristic_value = Integer.MIN_VALUE;
					return heuristic_value;					
				}
				
				/*
				// check if we can move this block
				boolean movable = false;
				for(BoardPosition candidate : board.getPushingPositions(block))
				{
					if(candidate.equals(board.getPlayerNode()))
						movable = true;
					else
						movable = pathfinder.getPath(board, board.getPlayerNode(), candidate) == null;		
				
					if(movable)
						break;
				}
				
				if(!movable) {
					val += Integer.MAX_VALUE / blocks.size();
					continue;
				}*/
				
				/*
				for(BoardPosition goal : board.getNeighbours(block))					
				{
					if(board.getNode(goal) == NodeType.WALL)
						val++;
				}
				
				int mindistToGoal = Integer.MAX_VALUE;
				for(BoardPosition goal : board.getGoalNodes())					
				{
					if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL)
						continue;
					
					mindistToGoal = Math.min(mindistToGoal, block.DistanceTo(goal));
				}
				val -= mindistToGoal;
						
				
				
			}
			
			/*
			val += path.getPath().size();
			
			heuristic_value = val; // * number_of_blocks_not_in_goal;
			return heuristic_value;
			*/
		}
		
		public List<Move> getNextMoves() {
			List<Move> possibleMoves = new ArrayList<Player.Move>();
			List<BoardPosition> blocks = board.getBlockNodes();
			BoardPosition playerPos = board.getPlayerNode();			
			
			for(BoardPosition blockPos : blocks)
			{
				// hitta ställen man kan göra förflyttningar av block.
				// skriva om sen..
				List<BoardPosition> pushPositions = board.getPushingPositions(blockPos);
				List<BoardPosition> neighbours = board.getNeighbours(blockPos);
				// srsly, skriva om sen xD
				List<BoardPosition> candidates = new ArrayList<BoardPosition>();
				
				
				for(BoardPosition pushpos : pushPositions) {
					if(neighbours.contains(pushpos))
						candidates.add(pushpos);
				}			
							
				// now do pathfinding to see if player can reach it..
				for(BoardPosition candidate : candidates)
				{
					Path toPush;
					if(candidate.equals(playerPos))
						toPush = new Path(candidate);
					else
						toPush = pathfinder.getPath(board, playerPos, candidate);		
					
					if(toPush == null) // no path found
						continue;				
					
					//toPush.getPath().remove(playerPos); // remove duplicate player pos (was causing null)
					toPush.append(blockPos);
					
					BoardState newBoard = (BoardState) board.clone();
					// move the player along the path.
					newBoard.movePlayer(toPush);
					// push the block by moving towards the block.
					newBoard.movePlayerTo(blockPos);
					
					Move move = new Move();
					move.board = newBoard;
					move.path = path.cloneAndAppend(toPush);
					possibleMoves.add(move);					
				}		
							
			}
			
			return possibleMoves;
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof Move))
				return false;
			
			Move b = (Move) o;			
			return b.board.equals(this.board);
			
		}

		@Override
		public int compareTo(Move o) {			
			return o.getHeuristicValue() - this.getHeuristicValue();
		}
	}

}



