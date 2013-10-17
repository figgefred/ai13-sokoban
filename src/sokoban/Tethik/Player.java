package sokoban.Tethik;

import java.io.IOException;
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
    
    public static boolean VERBOSE = true;
    public static boolean DO_GOAL_SORTING = true;
    public static boolean DO_DEADLOCKS_CONSTANTCHECK = true;
    public static boolean DO_DEADLOCKS_4x4 = true;
    public static boolean DO_BIPARTITE_MATCHING = true;
    public static boolean DO_CORRAL_LIVE_DETECTION = false;
    
	private BoardState initialState;
	
	public Player(BoardState initialState)
	{		
		this.initialState = initialState;
		
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
        	
        	if(VERBOSE) {
	        	System.out.println(openSet.size() + " " + closedSet.size());
	        	System.out.println("Pushes : " + node.pushes);
	        	System.out.println(node.path.getPath().size() + ", " + node.getHeuristicValue() + ", " + closedSet.size() + ", " + node.board.hashCode());
	        	System.out.println(node.board);
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
    			else if(neighbour.isWin())
    				return neighbour;
        	}
        }

		return null;
	}
	
	
	public Path play() {			
		
		Analyser analyser = new Analyser(initialState);
		PathFinder pathfinder = new PathFinder();
		Move initial = new Move(analyser, pathfinder);
		initial.board = initialState;
		initial.path = new Path();
		
		Move win = getVictoryPath(initial);
		if(win != null) {
			return win.path;
		}				
				
		return null;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
//		BoardState board = BoardState.getBoardFromFile("test100/test099.in");
		BoardState board = BoardState.getBoardFromFile("test100/test002.in");
//		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest5");
		
		long timeStart = System.currentTimeMillis();
		
		System.out.println(board);
		Player noob = new Player(board);
		Path path = noob.play();
		long timeStop = System.currentTimeMillis();
		System.out.println(path);
		if(path != null)
			board.movePlayer(path);
		System.out.println(board);
		
		System.out.println("Time: " + (timeStop - timeStart) + " ms");
	}
}



