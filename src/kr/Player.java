package kr;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
	
	private Analyser analyser; 
	private PathFinder pathfinder;
        private LiveAnalyser liveAnalyser;
	private BoardState initialState;
	public volatile boolean shouldStop = false;
        private Settings settings;
	public static volatile boolean VERBOSE = true;
        
        private Map<BoardState, Map<BoardPosition, List<Move>>> MyCachedMoves;
        public static boolean CHEAT;
        public static boolean HALF_CHEAT = true;
        public static boolean DO_MOVE_CACHING = true;
        //public static boolean FALLBACK;
        public static boolean DO_GOAL_SORTING = false;
        public static boolean DO_DEADLOCKS_CONSTANTCHECK = true;
        public static boolean DO_DEADLOCKS_4x4 = true;
        public static boolean DO_EXPENSIVE_DEADLOCK = true;
        public static boolean DO_BIPARTITE_MATCHING = true;
        public static boolean DO_CORRAL_LIVE_DETECTION = true;
        public static boolean DO_TUNNEL_MACRO_MOVE = true;
        public static boolean DO_CORRAL_CACHING = true;
        public static boolean DO_HEURISTIC_CACHING = true;
        
	public Player(BoardState initialState, Settings settings) {
		this.initialState = initialState;
		pathfinder = new PathFinder();
                this.settings = settings;
                this.liveAnalyser = new LiveAnalyser(pathfinder);
                analyser = new Analyser(initialState, settings, this.liveAnalyser);
                MyCachedMoves = new HashMap<>();
                
		
	}
	
	private Move winMove = null;
	
	private HashMap<Integer, Integer> visitedStates = new HashMap<Integer, Integer>();
	
	public int search(Move node, int bound) {
		if(shouldStop)
			return Integer.MIN_VALUE;
		
		if(node.getHeuristicValue() == Integer.MIN_VALUE)
			return Integer.MIN_VALUE;
		
		int lb = node.getLowerBound() + node.pushes;		
		
		if(lb > bound)	{
			return lb;	
		}
		
		if(VERBOSE) {
			System.out.println("lb: "+lb+" bound: "+bound + " h:" + node.getHeuristicValue());
			System.out.println(node.board);
                        try {Thread.sleep(200);}catch(InterruptedException ex) {};
		}
				
                
                // SOME CACHING STUFF
                Map<BoardPosition, List<Move>> map = MyCachedMoves.get(node.board);
                List<Move> moves = null;
                if(DO_MOVE_CACHING && map != null)
                {
                    moves = map.get(node.board.getPlayerNode());
                }
                if(DO_MOVE_CACHING && moves == null)
                {
                    moves = node.getNextMoves();
                    Collections.sort(moves);		
                    if(map == null)
                    {
                        map = new HashMap<>();
                    }
                    map.put(node.board.getPlayerNode(), moves);
                } 
                else if(moves == null)
                {
                    moves = node.getNextMoves();
                    Collections.sort(moves);		
                }
		int min = Integer.MAX_VALUE;
		for(Move child : moves) {				
			int t;
			if(visitedStates.containsKey(child.hashCode()) && (visitedStates.get(child.hashCode()) > bound || visitedStates.get(child.hashCode()) == Integer.MIN_VALUE))
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
		int bound = analyser.getLowerBound(root.board);
		long initTime = (new Date().getTime());
                settings.MOVE_DO_GOAL_MOVES = false;
		while(true) {
                    
                        if((new Date().getTime() - initTime) >= 4000)
                        {
                            settings.MOVE_DO_GOAL_MOVES = true;
                        }
                    
			int t = search(root,bound);
			
			if(t == Integer.MAX_VALUE) 
				return winMove; //return found			
			
			if(t == Integer.MIN_VALUE) 
				return null; //return not found
			
			bound=t;
		}
	}
	
	public Path play() {				

		Move initial = new Move(analyser, settings, pathfinder);
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
		//BoardState board = BoardState.getBoardFromFile("test100/test031.in");
            //BoardState board = BoardState.getBoardFromFile("test100/test098.in");
            //BoardState board = BoardState.getBoardFromFile("test100/test004.in");
            //BoardState board = BoardState.getBoardFromFile("test100/test050.in");
         //   BoardState board = BoardState.getBoardFromFile("test100/test059.in");
            //            BoardState board = BoardState.getBoardFromFile("test100/test069.in");
            BoardState board = BoardState.getBoardFromFile("test100/test099.in");
	//	BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest5");
          //BoardState board = BoardState.getBoardFromFile("testing/tunnelmap");
		
		long timeStart = System.currentTimeMillis();
		
                Player.VERBOSE = false;
                Player.DO_GOAL_SORTING = true;
                Player.DO_EXPENSIVE_DEADLOCK = false;
                Player.DO_CORRAL_LIVE_DETECTION = true;
                Player.DO_CORRAL_CACHING = true;
                Player.DO_TUNNEL_MACRO_MOVE = true;
                Player.DO_MOVE_CACHING = true;
                
                //Player.DO_DEADLOCKS_CONSTANTCHECK = true;
                //Player.DO_DEADLOCKS_4x4 = true;
                //Player.DO_BIPARTITE_MATCHING = true;
                
                //Player.DO_HEURISTIC_CACHING = true;
                
                Settings settings = new Settings();
                settings.MOVE_DO_GOAL_MOVES = false;
                
		System.out.println(board);
		
                Player noob = new Player(board, settings);
                board.setSettings(settings);
		Path path = noob.play();
		long timeStop = System.currentTimeMillis();
		System.out.println(path);
		if(path != null)
                {
                    for(int i = 0; i< path.size(); i++)
                    {
                        try {
                            board.movePlayerTo(path.get(i));
                        } catch(IllegalArgumentException ex)
                        {
                            System.err.println("Oooops, invalid move.");
                        }
                    }
                    
                    //board.movePlayer(path);
                }
		System.out.println(board);

		System.out.println("Time: " + (timeStop - timeStart) + " ms");
	}

}
