package sokoban.Tethik;

import java.io.IOException;
import java.util.HashSet;
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
    	
        while(!openSet.isEmpty())
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
        	
        	for(Move neighbour: node.getNextMoves())
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
		
		Move initial = new Move();
		initial.board = initialState;
		initial.path = new Path();
		Move.initPreanalyser(initialState);		
		
		
		
		Move win = getVictoryPath(initial);
		if(win != null) {
			//System.out.println(win.board);
			System.out.println(win.path);
			return win.path;
		}			
	
		
		System.out.println();
		return new Path();
		/*
		for(Move nextMove : initial.getNextMoves())
		{
			System.out.println(nextMove.board);
			System.out.println(nextMove.path)4
			System.out.println(nextMove.getHeuristicValue())
		}
		*/
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		BoardState board = BoardState.getBoardFromFile("test100/test098.in");
		
		System.out.println(board);
		Player noob = new Player(board);
		Path path = noob.play();
		board.movePlayer(path);
		System.out.println(board);
	}
}



