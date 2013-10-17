package sokoban.Tethik;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


import sokoban.BoardPosition;

public class IDAPlayer {
	
	private Analyser analyser; 
	private PathFinder pathfinder;
	private BoardState initialState;
	public volatile boolean shouldStop = false;
	public static volatile boolean VERBOSE = true;
	
	public IDAPlayer(BoardState initialState) {
		this.initialState = initialState;
		analyser = new Analyser(initialState);
		pathfinder = new PathFinder();
	}
	
	private Move winMove = null;
	
	/**
	 * Simple manhattan-distance heuristic on the blocks vs goals.
	 * @param move
	 * @return
	 */
	public int heuristicValue(Move move) {		
		BoardState board = move.board;
		int val = 0;
		for(BoardPosition goal : board.getGoalNodes())
		{		
			int min = Integer.MAX_VALUE;
			for(BoardPosition block : board.getBlockNodes())
			{
				min = Math.min(min, goal.DistanceTo(block));
			}			
			val += min;
		}		
		return val;
	}
	
	public int heuristicValue2(Move move) {
		return move.getHeuristicValue() * -1;
	}
	
//	private HashSet<Integer> deadlockStates = new HashSet<Integer>();
	private HashMap<Move, Integer> visitedStates = new HashMap<Move, Integer>();
	
	public int search(Move node, int bound) {
		if(shouldStop)
			return Integer.MIN_VALUE;
		
		
//		if(deadlockStates.contains(node)) {
//			System.out.println("Old Deadlock found..");
//			return Integer.MIN_VALUE;
//		}
		
//		if(node.getHeuristicValue() == Integer.MIN_VALUE) {
////			if(!deadlockStates.contains(node.hashCode()))
////				deadlockStates.add(node.hashCode());
//			return Integer.MIN_VALUE;
//		} 
//		
		if(node.board.isWin()){
			winMove=node;
			return Integer.MAX_VALUE;
		}
		
		int h = node.getHeuristicValue();
		int lb = h * -1;
		
		if(lb > bound)	
			return lb;		
		
		//visitedStates.put(node, h);
		
		if(h == Integer.MAX_VALUE || h == Integer.MIN_VALUE)
			return h;
		
		if(VERBOSE) {
			System.out.println("lb: "+lb+" bound: "+bound);
			System.out.println(node.board);
		}
				
		List<Move> moves = node.getNextMoves();		
		Collections.sort(moves);		
		int min = Integer.MAX_VALUE;
		for(Move child : moves) {		
			
			int t;
			if(visitedStates.containsKey(child))
				t = visitedStates.get(child);
			else {
				t = search(child,bound);
				
				if(t == Integer.MIN_VALUE)
					visitedStates.put(child, t);
			}
			
			if(t == Integer.MAX_VALUE) 
				return t; //return found, go back up the tree

			if(t > Integer.MIN_VALUE)
				min = Math.min(t, min);
		}	
		
		if(min < Integer.MAX_VALUE)
			return min;
		else
			return Integer.MIN_VALUE;			
	}
	
	public Move idaStar(Move root){
		
		int bound = root.getHeuristicValue() * -1;
		
		while(true) {
			int t = search(root,bound);
			
			if(t == Integer.MAX_VALUE) 
				return winMove; //return found			
			
			if(t == Integer.MIN_VALUE) 
				return null; //return not found
			
			bound=t;
			//visitedStates.clear();
		}
	}
	
	public Path play() {				
		Move initial = new Move(analyser, pathfinder);
		initial.board = initialState;
		initial.path = new Path();

		Move win = idaStar(initial);
		if(win != null) {		
			return win.path;
		}	

		return null;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
//		BoardState board = BoardState.getBoardFromFile("test100/test001.in");
		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");
		
		System.out.println(board);
		IDAPlayer noob = new IDAPlayer(board);
		Path path = noob.play();
		System.out.println(path);
		if(path != null)
			board.movePlayer(path);
		System.out.println(board);
	}

}
