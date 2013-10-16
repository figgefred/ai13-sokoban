package carloskattis;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


/***
 * A* variant on boardstate
 * @author tethik
 *
 */
public class Player {	
	
	private Queue<Move> openSet;
    private HashSet<Integer> closedSet;
    public static boolean VERBOSE = true;
    public volatile boolean shouldStop = false;
    
	private BoardState initialState;
	
	
	public Player(BoardState initialState)
	{		
		this.initialState = initialState;
		
		if(initialState.getBlockNodes().size() != initialState.getGoalNodes().size())
			throw new IllegalArgumentException("Different number of goals than blocks");
	}

	public Move getVictoryPath(Move initialPosition)
	{
		System.err.println();
		openSet = new PriorityQueue<Move>();
		closedSet = new HashSet<Integer>();
    	openSet.add(initialPosition);
    	
        while(!openSet.isEmpty() && !shouldStop)
        {
        	Move node = openSet.poll();
        	//System.out.println(node);
        	
        	if(VERBOSE) {
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

        	List<Move> nextMoves = node.getNextMoves();
        	
        	for(Move neighbour: nextMoves)
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
	
	
	public Path play() {				
		
		Analyser analyser = new Analyser(initialState);
		PathFinder pathfinder = new PathFinder();
		Move initial = new Move(analyser, pathfinder);
		initial.board = initialState;
		initial.path = new Path();
		
		Move win = getVictoryPath(initial);
		if(win != null) {		
			System.out.println(win.path);
			//return win.path;
		}			
	
		
		//System.out.println();
		return null;
		/*
		for(Move nextMove : initial.getNextMoves())
		{
			System.out.println(nextMove.board);
			System.out.println(nextMove.path)
			System.out.println(nextMove.getHeuristicValue())
		}
		*/
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		long timeStart = System.currentTimeMillis();
		BoardState board = BoardState.getBoardFromFile("test100/test003.in");
		BoardState orig = (BoardState) board.clone();
		
		System.out.println(board);
		Player noob = new Player(board);
		Path path = noob.play();
		System.out.println(path);
		board.movePlayer(path);
		System.out.println(board);
		
		orig.movePlayer(path);
		System.out.println(orig);
		
		long timeStop = System.currentTimeMillis();
		
		System.out.println("Time: " + (timeStop - timeStart) + " ms");
	}
}




