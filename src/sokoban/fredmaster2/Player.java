package sokoban.fredmaster2;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import sokoban.BoardPosition;


/***
 * A* variant on boardstate
 * @author tethik
 *
 */
public class Player {	
    

	
	private Queue<Move> openSet;
        private HashSet<BoardState> closedSet;
        private HashSet<BoardState> toVisitSet;
        public static boolean VERBOSE = false;
        public volatile boolean shouldStop;

        private Map<BoardState, Map<BoardPosition, List<Move>>> MyCachedMoves;
        public static boolean DO_MOVE_CACHING = true;
        public static boolean CHEAT;
        public static boolean FALLBACK;
        public static boolean DO_GOAL_SORTING = true;
        public static boolean DO_DEADLOCKS_CONSTANTCHECK = true;
        public static boolean DO_DEADLOCKS_4x4 = true;
        public static boolean DO_EXPENSIVE_DEADLOCK = true;
        public static boolean DO_BIPARTITE_MATCHING = true;
        public static boolean DO_CORRAL_LIVE_DETECTION = true;
        public static boolean DO_TUNNEL_MACRO_MOVE = true;
        public static boolean DO_CORRAL_CACHING = true;
        public static boolean DO_HEURISTIC_CACHING = true;
    
	private BoardState initialState;
        private Settings settings;
	
	public Player(BoardState initialState, Settings settings)
	{		
		this.initialState = initialState;
                this.settings = settings;
		this.MyCachedMoves = new HashMap<>();
		if(initialState.getBlockNodes().size() != initialState.getGoalNodes().size())
			throw new IllegalArgumentException("Different number of goals than blocks");
	}

	public Move getVictoryPath(Move initialPosition)
	{
		openSet = new PriorityQueue<Move>();
		closedSet = new HashSet<BoardState>();
		toVisitSet = new HashSet<BoardState>();
                openSet.add(initialPosition);
    	
        while(!openSet.isEmpty() && !shouldStop)
        {
        	Move node = openSet.poll();
        	
        	if(VERBOSE) {
	        	System.out.println(openSet.size() + " " + toVisitSet.size());
	        	System.out.println(node.path.getPath().size() + ", " + node.getHeuristicValue() + ", " + closedSet.size() + ", " + node.board.hashCode());
	        	System.out.println(node.board);
        	}
        	
        	if(node.board.isWin())        	
        		return node;        	
        	
        	if(node.getHeuristicValue() == Integer.MIN_VALUE)
        		return null;
        	
        	//Integer tentative_g = node.getHeuristicValue() + 100;
        	
                
                // SOME CACHING STUFF
                Map<BoardPosition, List<Move>> map = MyCachedMoves.get(node.board);
                List<Move> moves = null;
                if(Player.DO_MOVE_CACHING && map != null)
                {
                    moves = map.get(node.board.getPlayerNode());
                }
                if( Player.DO_MOVE_CACHING && moves == null)
                {
                    moves = node.getNextMoves();
                    if(map == null)
                    {
                        map = new HashMap<>();
                    }
                    map.put(node.board.getPlayerNode(), moves);
                }      
                else if(moves == null)
                {
                    moves = node.getNextMoves();
                }
        	for(Move neighbour: moves)
        	{	       		                    
        		if(neighbour.board.isWin())
        			return neighbour;
        		
        		Integer to_g = neighbour.getHeuristicValue();        		
        		
        		//System.out.println(neighbour.board);
        		//System.out.println(to_g);
        		//System.out.println(tentative_g);
        		
        		
        		if (closedSet.contains(neighbour.board) || to_g == Integer.MIN_VALUE) {        			
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

        }

		return null;
	}
	
	
	public Path play() throws InterruptedException {				
		
                PathFinder p = new PathFinder();
		Move initial = new Move( new LiveAnalyser(p), new Analyser(initialState), new Settings(), p);
		initial.board = initialState;
                initial.board.setSettings(settings);
		initial.path = new Path();
		//Move.initPreanalyser(initialState);		
		
		Move win = null;
                win = getVictoryPath(initial);
                if(Player.CHEAT)
                    System.out.print(" C ");
                if(win != null)
                    return win.path;
                
                if(Player.FALLBACK)
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
		BoardState board;
                //board = BoardState.getBoardFromFile("testing/simpleplaytest5");
                //board = BoardState.getBoardFromFile("testing/level3");
              //  board = BoardState.getBoardFromFile("test100/test092.in");
                //board = BoardState.getBoardFromFile("test100/test031.in");
                //board = BoardState.getBoardFromFile("test100/test016.in");
                
            // Can solve
                //board = BoardState.getBoardFromFile("test100/test059.in");
               // board = BoardState.getBoardFromFile("test100/test069.in");
               // board = BoardState.getBoardFromFile("test100/test092.in");
                
            // Cant't solve
                //board = BoardState.getBoardFromFile("test100/test089.in");
                
                // IDA dålig
                //board = BoardState.getBoardFromFile("test100/test036.in");
                //board = BoardState.getBoardFromFile("test100/test069.in");
                
                
                //board = BoardState.getBoardFromFile("test100/test009.in");
                //board = BoardState.getBoardFromFile("test100/test019.in");
                //board = BoardState.getBoardFromFile("test100/test029.in");
                //board = BoardState.getBoardFromFile("test100/test029.in");
                //board = BoardState.getBoardFromFile("test100/test039.in");
                //board = BoardState.getBoardFromFile("test100/test049.in");
                board = BoardState.getBoardFromFile("test100/test093.in");
                //board = BoardState.getBoardFromFile("test100/test069.in");
                //board = BoardState.getBoardFromFile("test100/test079.in");
                //board = BoardState.getBoardFromFile("test100/test089.in");
                //board = BoardState.getBoardFromFile("test100/test099.in");
                Player.VERBOSE = false;
        
        Player.DO_MOVE_CACHING = true;
                
        Player.DO_GOAL_SORTING = false;
        Player.DO_DEADLOCKS_CONSTANTCHECK = true;
        Player.DO_DEADLOCKS_4x4 = true;
        Player.DO_EXPENSIVE_DEADLOCK = true;
        Player.DO_BIPARTITE_MATCHING = true;
        Player.DO_CORRAL_LIVE_DETECTION = true;
        Player.DO_TUNNEL_MACRO_MOVE = true;
        Player.DO_CORRAL_CACHING = true;
        Player.DO_HEURISTIC_CACHING = false;
    
                
        sokoban.fredmaster2.Player.CHEAT = false;
        sokoban.fredmaster2.Player.FALLBACK = false;
        
		System.out.println(board);
//                System.out.println(board.getTunnels());
            //    try{Thread.sleep(500000);}catch(InterruptedException ex){}
                
                
                Settings settings = new Settings();
		Player noob = new Player(board, settings);
                settings.MOVE_DO_GOAL_MOVES =false;
		Path path = noob.play();
                
                System.out.println( (path == null?"no path": path.toString()) );
                
                if(path != null)
                {
                    board.movePlayer(path);
                    System.out.println(board);
                }
	}
}