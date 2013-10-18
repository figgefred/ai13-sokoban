package sokoban.carlos;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


/***
 * A* variant on boardstate
 * @author tethik
 */
public class Player {	
	
	private Queue<Move> openSet;
    private HashSet<Integer> closedSet;
    
    public volatile boolean shouldStop = false;
    public Settings settings;
    public Analyser analyser;
    public PathFinder pathfinder; 
    
	private BoardState initialState;
	
	public Player(BoardState initialState, Settings settings)
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
		openSet = new PriorityQueue<Move>();
		closedSet = new HashSet<Integer>();
    	openSet.add(initialPosition);
    	
        while(!openSet.isEmpty() && !shouldStop)
        {
        	Move node = openSet.poll();
        	
        	if(settings.VERBOSE) {
	        	System.out.println(openSet.size() + " " + closedSet.size());
	        	System.out.println("Pushes : " + node.pushes);
	        	System.out.println(node.path.getPath().size() + ", " + node.getHeuristicValue() + ", " + closedSet.size() + ", " + node.board.hashCode());
	        	System.out.println(node.board);
	        	
	        	try {
	        		System.in.read();
	        	} catch(IOException e) {
	        		
	        	}
        	}
        	
        	if(node.board.isWin())        	
        		return node;        	
        	
        	List<Move> moves = node.getNextMoves();
        	for(Move neighbour : moves)
        	{	
        		if(closedSet.contains(neighbour.board.hashCode())) {        			
                	continue;
        		}
        		
    			closedSet.add(neighbour.board.hashCode());    			
    		
    			if(neighbour.getHeuristicValue() > Integer.MIN_VALUE)
    				openSet.add(neighbour);
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
		BoardState board = BoardState.getBoardFromFile("test100/test006.in");
//		BoardState board = BoardState.getBoardFromFile("testing/deadlocktest1");
		
		long timeStart = System.currentTimeMillis();
		
		System.out.println(board);
		Settings settings = new Settings();
		settings.VERBOSE = false;
		settings.ANALYSER_BIPARTITE_MATCHING = false;
//		settings.MOVE_DO_GOAL_MOVES = false;
		Player noob = new Player(board, settings);
		Path path = noob.play();
		long timeStop = System.currentTimeMillis();
		System.out.println(path);
		if(path != null)
			board.movePlayer(path);
		System.out.println(board);
		
		System.out.println("Time: " + (timeStop - timeStart) + " ms");
	}
}



