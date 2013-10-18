package sokoban.Tethik.Combo;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class IDAPlayer {
	
	private Analyser analyser; 
	private PathFinder pathfinder;
	private BoardState initialState;
	public volatile boolean shouldStop = false;
	
	public Settings settings;
	
	public IDAPlayer(BoardState initialState, Settings settings) {
		this.settings = settings;
		this.initialState = initialState;
		analyser = new Analyser(initialState, settings);
		pathfinder = new PathFinder();
		cachedMoves = new HashMap<BoardState, Map<BoardPosition,List<Move>>>();
	}
	
	private Move winMove = null;
	
	private HashMap<Integer, Integer> visitedStates = new HashMap<Integer, Integer>();
	private Map<BoardState, Map<BoardPosition, List<Move>>> cachedMoves;
	
	public int search(Move node, int bound) {
		if(shouldStop)
			return Integer.MIN_VALUE;
		
		if(node.getHeuristicValue() == Integer.MIN_VALUE)
			return Integer.MIN_VALUE;	
		
		int lb = node.getLowerBound() + node.pushes;	
		
		if(settings.VERBOSE) {
			System.out.println("lb: "+lb+" bound: "+bound + " h:" + node.getHeuristicValue());
			System.out.println(node.board);
		}
		
		if(lb > bound)	{
			return lb;	
		}		
				
		Map<BoardPosition, List<Move>> map = cachedMoves.get(node.board);
        List<Move> moves = null;
        if(settings.DO_MOVE_CACHING && map != null)
        {
            moves = map.get(node.board.getPlayerNode());
        }
        if(moves == null)
        {
            moves = node.getNextMoves();	
            if(settings.DO_MOVE_CACHING) {
	            if(map == null)
	            {
	                map = new HashMap<>();
	            }
	            map.put(node.board.getPlayerNode(), moves);
            }
        }
//		
		int min = Integer.MAX_VALUE;
		for(Move child : moves) {				
			int t;
			if(visitedStates.containsKey(child.hashCode()) &&
					(visitedStates.get(child.hashCode()) > bound ||
							visitedStates.get(child.hashCode()) == Integer.MIN_VALUE))
				// Transition table
				t = visitedStates.get(child.hashCode());
			else if(child.isWin()) {
				// Goalcut
				winMove=child;
				t = Integer.MAX_VALUE;
			} else {
				// Normal
				t = search(child, bound);
				visitedStates.put(child.hashCode(), t);				
			}
			
			if(t == Integer.MAX_VALUE) 
				return t; //win found, go back up the tree

			if(t == Integer.MIN_VALUE)
				continue; //deadlock found
			
			min = Math.min(t, min);
		}	
		
		if(min < Integer.MAX_VALUE)
			return min;
		else
			return Integer.MIN_VALUE;			
	}
	
	public Move idaStar(Move root) {		
		visitedStates = new HashMap<Integer, Integer>();
		winMove = null;
		int bound = analyser.getLowerBound(root.board);
		
		while(true) {
			int t = search(root,bound);
			
			if(t == Integer.MAX_VALUE) 
				return winMove; //return found			
			
			if(t == Integer.MIN_VALUE) 
				return null; //return not found
			
			bound = t;
		}
	}
	
	public Path play() {				
		Move initial = new Move(analyser, pathfinder);
		initialState.setSettings(settings);
		initial.board = initialState;		
		initial.path = new Path();

		Move win = idaStar(initial);
		if(win != null) {
			return win.path;
		} else {			
			settings.BOARDSTATE_PLAYER_HASHING = true;
			//settings.VERBOSE = true;
			initialState.resetHash();
			
			win = idaStar(initial);			
			if(win != null) 
				return win.path;
		}
		
		return null;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		BoardState board = BoardState.getBoardFromFile("test100/test003.in");
//		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");
		
		long timeStart = System.currentTimeMillis();		
		System.out.println(board);
		Settings settings = new Settings();
//		settings.VERBOSE = true;
		IDAPlayer noob = new IDAPlayer(board, settings);		
		Path path = noob.play();
		long timeStop = System.currentTimeMillis();
		System.out.println(path);
		if(path != null)
			board.movePlayer(path);
		System.out.println(board);

		System.out.println("Time: " + (timeStop - timeStart) + " ms");
	}

}
