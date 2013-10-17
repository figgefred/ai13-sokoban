package sokoban.FredTethMerge;

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
    public static boolean DO_EXPENSIVE_DEADLOCK = false;
    public static boolean DO_BIPARTITE_MATCHING = true;
    public static boolean DO_CORRAL_LIVE_DETECTION = false;
    
        public static boolean CHEAT;
        public static boolean FALLBACK;
        //public static boolean DO_GOAL_SORTING = true;
        //public static boolean DO_DEADLOCKS_CONSTANTCHECK = true;
        //public static boolean DO_DEADLOCKS_4x4 = true;
        //public static boolean DO_BIPARTITE_MATCHING = true;
        //public static boolean DO_CORRAL_LIVE_DETECTION = true;
        public static boolean DO_TUNNEL_MACRO_MOVE = true;
        public static boolean DO_CORRAL_CACHING = true;
        public static boolean DO_HEURISTIC_CACHING = true;
    
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
		
		PathFinder pathfinder = new PathFinder();
                LiveAnalyser liveAnalyser = new LiveAnalyser( pathfinder);  
                Analyser analyser = new Analyser(liveAnalyser, initialState);
		Move initial = new Move(liveAnalyser, analyser, pathfinder);
		initial.board = initialState;
		initial.path = new Path();
		
		Move win = null;
                if(Player.CHEAT)
                {
                    win = getVictoryPath(initial);
//                    System.out.print("CHEAT");
                    if(win != null)
                        return win.path;
                }
                
                if(Player.CHEAT && Player.FALLBACK)
                {
                    Player.CHEAT = false;
                    System.out.print(" F ");
                    win = getVictoryPath(initial);
                }
                else if(!Player.CHEAT)
                {
                    win = getVictoryPath(initial);
                }
                
		if(win == null)
                    return null;
                else
                    return win.path;		
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
//		BoardState board = BoardState.getBoardFromFile("test100/test099.in");
//		BoardState board = BoardState.getBoardFromFile("test100/test002.in");
//		BoardState board = BoardState.getBoardFromFile("test100/test004.in");
//		BoardState board = BoardState.getBoardFromFile("test100/test016.in");
            
		//BoardState board = BoardState.getBoardFromFile("test100/test069.in");
                //BoardState board = BoardState.getBoardFromFile("test100/test037.in");
                //BoardState board = BoardState.getBoardFromFile("test100/test086.in");

//              BoardState board = BoardState.getBoardFromFile("test100/test059.in");
//		BoardState board = BoardState.getBoardFromFile("test100/test089.in");
//              BoardState board = BoardState.getBoardFromFile("test100/test092.in");
//		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest5");
                
                //BoardState board = BoardState.getBoardFromFile("test100/test025.in");
                //BoardState board = BoardState.getBoardFromFile("test100/test029.in");
            BoardState board = BoardState.getBoardFromFile("test100/test031.in");
		
                Player.VERBOSE = false;
                
                Player.DO_GOAL_SORTING = true;
                Player.DO_DEADLOCKS_CONSTANTCHECK = true;
                Player.DO_DEADLOCKS_4x4 = true;
                Player.DO_EXPENSIVE_DEADLOCK = true;
                Player.DO_BIPARTITE_MATCHING = true;
                Player.DO_CORRAL_LIVE_DETECTION = true;
                Player.DO_CORRAL_CACHING = true;
                Player.DO_HEURISTIC_CACHING = true;

                Player.DO_TUNNEL_MACRO_MOVE = false;

                Player.CHEAT = true;
                Player.FALLBACK = true;
                
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



