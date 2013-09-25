package sokoban.Tethik;

import java.io.IOException;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

//import sokoban.BoardState;
import sokoban.Path;

/***
 * A* variant on boardstate
 * @author tethik
 *
 */
public class Player {	
	
	private Queue<Move> openSet;
    private HashSet<BoardState> closedSet;
    private HashSet<BoardState> toVisitSet;
    
	private BoardState initialState;
	
	public Player(BoardState initialState)
	{		
		this.initialState = initialState;
	}

	public Move getVictoryPath(Move initialPosition)
	{
		openSet = new PriorityQueue<Move>();
		closedSet = new HashSet<BoardState>();
		toVisitSet = new HashSet<BoardState>();
    	openSet.add(initialPosition);
    	
        while(!openSet.isEmpty())
        {
        	Move node = openSet.poll();
        	System.out.println(openSet.size() + " " + toVisitSet.size());
        	System.out.println(node.path.getPath().size() + ", " + node.getHeuristicValue() + ", " + closedSet.size() + ", " + node.board.hashCode());
        	System.out.println(node.board);
        	
        	if(node.board.isWin())
        	{       		
        		return node;
        	}       	        	
        	
        	Integer tentative_g = node.getHeuristicValue() + 10;
        	
        	for(Move neighbour: node.getNextMoves())
        	{	       		                		
        		if(neighbour.board.isWin())
        			return neighbour;
        		Integer to_g = neighbour.getHeuristicValue();
        		
        		//System.out.println(neighbour.board);
        		//System.out.println(to_g);
        		//System.out.println(tentative_g);
        		
        		
        		if (closedSet.contains(neighbour.board) || to_g > tentative_g) {        			
                	continue;
        		}
        		
        		
        		if(!toVisitSet.contains(neighbour.board))
        		{        		        			
        			openSet.add(neighbour);
        			toVisitSet.add(neighbour.board);
        		}
        		        		
        	}
        	
        	
        	if(closedSet.contains(node.board)) {        		
        		System.err.println("hash collision!");
        	}
        	toVisitSet.remove(node.board);
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
		Move.initPreanalyser(initialState);
		
		
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
}



