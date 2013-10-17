package sokoban.Tethik;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import sokoban.BoardPosition;
import sokoban.NodeType;

public class SingleBlockPlayer {
	
	
	private PathFinder pathfinder;
	private Analyser analyser;
	public static boolean VERBOSE = false;
	
	public SingleBlockPlayer(Analyser analyser) {			
		this.analyser = analyser;
		this.pathfinder = new PathFinder();
	}
	
	public List<Move> findGoalMoves(Move root, BoardPosition block) {
		
		if(root.board.get(block) == NodeType.BLOCK_ON_GOAL)
			return new ArrayList<Move>();
		
		BoardState board = root.board;
		Path path = root.path;
		
//		List<BoardPosition> pushPositions = board.getPushingPositions(block);		
//		boolean canReach = false;		
//		for(BoardPosition pushPos : pushPositions)
//		{
//			if(pathfinder.isReachable(board, pushPos)) {
//				canReach = true;
//				break;
//			}
//		}		
//		if(!canReach)
//			return new ArrayList<Move>();

		
		ArrayList<Move> goalmoves = new ArrayList<Move>();
		BoardState clone = (BoardState) board.clone();
		for(BoardPosition blockToWall : board.getBlockNodes())
		{
			if(blockToWall.equals(block))
				continue;
			
			clone.set(blockToWall, NodeType.WALL);
		}
		
		for(BoardPosition goal : board.getGoalNodes()) {
			if(clone.get(goal) == NodeType.WALL)
				continue; // Har redan ett block på sig.
			
			clone.set(goal, NodeType.SPACE);			
		}
		
		int goalindex = 0;
		for(BoardPosition goal : board.getGoalNodes()) {		
			if(goal.equals(block)) {
				goalindex++;
				continue;
			}
			
			if(clone.get(goal) == NodeType.WALL) {
				goalindex++;
				continue;
			}
	
			clone.set(goal, NodeType.GOAL);
			clone.RecalculateGoalNodes();
			SingleBlockMove initial = new SingleBlockMove(analyser, pathfinder, block, goalindex);
			initial.board = clone;		
			initial.path = new Path();
			
			Move move = getGoalPath(initial);
			
			if(move != null) {
				Move normalised = new Move(root.analyser, root.pathfinder);
				normalised.board = (BoardState) board.clone();
				normalised.board.movePlayer(move.path);
				normalised.path = path.cloneAndAppend(move.path);
				normalised.pushes = root.pushes + move.pushes;
				goalmoves.add(normalised);
			}
			
			clone.set(goal, NodeType.SPACE);
			goalindex++;
		}		
		
		return goalmoves;
	}
	
	public Move getGoalPath(SingleBlockMove initialPosition)
	{
		Queue<Move> openSet = new PriorityQueue<Move>();
		HashSet<Integer> closedSet = new HashSet<Integer>();
    	openSet.add(initialPosition);
    	
        while(!openSet.isEmpty())
        {
        	Move node = openSet.poll();
        	
        	if(VERBOSE) {
	        	System.out.println(openSet.size() + " " + closedSet.size());
	        	System.out.println("Pushes : " + node.pushes);
	        	System.out.println(node.path.getPath().size() + ", " + node.getHeuristicValue() + ", " + closedSet.size() + ", " + node.board.hashCode());
	        	System.out.println(node.board);
        	}
        	
        	if(node.isWin())        	
        		return node;        	
        	
        	List<Move> moves = node.getNextPushMoves();
        	for(Move neighbour : moves)
        	{	            		       		
        		if (closedSet.contains(neighbour.board.hashCode())) {        			
                	continue;
        		}
        		
    			closedSet.add(neighbour.board.hashCode());
    			
    			if(neighbour.getHeuristicValue() > Integer.MIN_VALUE)
    				openSet.add(neighbour);  	
        	}
        }

		return null;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		//BoardState board = BoardState.getBoardFromFile("test100/test099.in");
		BoardState board = BoardState.getBoardFromFile("testing/level3");
		
		System.out.println(board);
		Analyser analyser = new Analyser(board);
		PathFinder pathfinder = new PathFinder();
		SingleBlockPlayer noob = new SingleBlockPlayer(analyser);
		SingleBlockPlayer.VERBOSE = false;
		
//		BoardPosition block = board.getBlockNodes().get(0);
		for(BoardPosition block : board.getBlockNodes())
		{	
			Move initial = new Move(analyser, pathfinder);	
			initial.board = board;
			initial.path = new Path();
			for(Move move : noob.findGoalMoves(initial, block)) {			
				System.out.println(move.path.getPath().size() + ", " + move.getHeuristicValue() + ", " + move.board.hashCode());
	        	System.out.println(move.board);
			}
		}
		
	}

}
