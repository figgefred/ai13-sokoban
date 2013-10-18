package sokoban.Tethik.Combo;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


/***
 * A* variant on boardstate
 * @author tethik
 */
public class BFSPlayer {	
	

    
    public volatile boolean shouldStop = false;
    public Settings settings;
    public Analyser analyser;
    public PathFinder pathfinder; 
    
	private BoardState initialState;
	
	public BFSPlayer(BoardState initialState, Settings settings)
	{		
		this.settings = settings;
		this.initialState = initialState;
		this.analyser = new Analyser(initialState, settings);
		this.pathfinder = new PathFinder();
		
		if(initialState.getBlockNodes().size() != initialState.getGoalNodes().size())
			throw new IllegalArgumentException("Different number of goals than blocks");
	}

	public Move getVictoryPath(Move initialPosition)
	{	
		HashSet<Integer> closedSet = new HashSet<Integer>();    	
		
    	for(int boundMultiplier = initialPosition.board.getRowsCount() / 2; boundMultiplier < 32768 && !shouldStop; boundMultiplier = boundMultiplier << 1)
    	{
    		Queue<Move> openSet = new PriorityQueue<Move>();
    		HashSet<Integer> visitedSet = new HashSet<Integer>();
    		openSet.add(initialPosition);
    		
	    	int bound = analyser.getLowerBound(initialState) * boundMultiplier;
	    	if(settings.VERBOSE)
	    		System.out.println("Now at bound: " + bound);
	    	
	    	if(bound < 0)
	    		throw new IllegalArgumentException("Bound is lower than 0!");	   
	    	
	        while(!openSet.isEmpty() && !shouldStop)
	        {
	        	Move node = openSet.poll();
	        	
	        	if(settings.VERBOSE) {
		        	System.out.println(openSet.size() + " " + closedSet.size());
		        	System.out.println("Pushes : " + node.pushes);
		        	System.out.println(node.path.getPath().size() + ", " + node.getHeuristicValue() + ", " + closedSet.size() + ", " + node.board.hashCode());
		        	System.out.println(node.board);
	        	}
	        	
	        	if(node.board.isWin())        	
	        		return node;    	        		
	        	
	        	List<Move> moves = node.getNextMoves();
	        	int maxval = Integer.MIN_VALUE;
	        	for(Move neighbour : moves)
	        	{	
	        		if(visitedSet.contains(neighbour.hashCode()))
        				continue;
	        		
	        		if(closedSet.contains(neighbour.hashCode())) 
	                	continue;

	    			if(neighbour.getHeuristicValue() == Integer.MIN_VALUE) {
	    				closedSet.add(neighbour.hashCode());
	    				continue;
	    			}
	    			
	    			maxval = Math.max(maxval, neighbour.getHeuristicValue());
	    			
	    			
	    			if(analyser.getLowerBound(neighbour.board) + neighbour.pushes > bound) {		        		
		        		continue;
		        	}
	    			
	    			visitedSet.add(neighbour.hashCode());	
	    			openSet.add(neighbour);  
	        	}
	        	
	        	// All moves lead to deadlock..
	        	if(maxval == Integer.MIN_VALUE)
	        		closedSet.add(node.hashCode());
	        }
    	}

		return null;
	}
	
	
	public Path play() {
		
		Move initial = new Move(analyser, pathfinder);
		initial.board = initialState;
		initialState.setSettings(settings);
		initial.path = new Path();
		
		Move win = getVictoryPath(initial);
		if(win != null) {
			return win.path;
		} else {						
			settings.BOARDSTATE_PLAYER_HASHING = true;
			win = getVictoryPath(initial);
			if(win != null) 
				return win.path;
		}
				
		return null;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
//		BoardState board = BoardState.getBoardFromFile("test100/test099.in");
		BoardState board = BoardState.getBoardFromFile("test100/test093.in");
//		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest5");
		
		long timeStart = System.currentTimeMillis();
		
		System.out.println(board);
		Settings settings = new Settings();
//		settings.MOVE_DO_GOAL_MOVES = true;
		settings.VERBOSE = true;
		BFSPlayer noob = new BFSPlayer(board, settings);
		Path path = noob.play();
		long timeStop = System.currentTimeMillis();
		System.out.println(path);
		if(path != null)
			board.movePlayer(path);
		System.out.println(board);
		
		System.out.println("Time: " + (timeStop - timeStart) + " ms");
	}
}



