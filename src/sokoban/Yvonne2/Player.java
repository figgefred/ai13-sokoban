package sokoban.Yvonne2;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
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
	public volatile boolean shouldStop = false;
	Move winMove=null;
	public static boolean IDA =false;
	public static boolean WIKIDA=true;

	private BoardState initialState;

	public Player(BoardState initialState)
	{		
		this.initialState = initialState;

		if(initialState.getBlockNodes().size() != initialState.getGoalNodes().size())
			throw new IllegalArgumentException("Different number of goals than blocks");
	}



	public Move idaStar (Move root){
		winMove=null;
		int bound = root.getHeuristicValue();
	
		while(bound < Integer.MAX_VALUE){
			System.out.println("search("+root.board.getBlockNodes().get(0).Row+ " "+root.board.getBlockNodes().get(0).Column+")");
			bound = search(root,bound);
		}

		return winMove;
	}

	public int search(Move node, int bound){


		if(node.board.isWin()){ //if node is a goal, exit algorithm and return goal
			winMove=node;
			System.out.println("WIN!");
			return Integer.MAX_VALUE; 
		}

		if(node.getNextMoves().isEmpty()){ //if node has no children, return infinity
			System.out.println(node.board.getPlayerNode().Row+ " "+node.board.getPlayerNode().Column+ " No children");
			return Integer.MAX_VALUE; 
		}

		int fn = Integer.MAX_VALUE;
		for(Move neighbour : node.getNextMoves()){

			int f=+neighbour.getHeuristicValue();
			if(f<=bound){		
				System.out.println(neighbour.board.getPlayerNode().Row+ " "+neighbour.board.getPlayerNode().Column+ " < neighbour low bound");

				fn=Math.min(fn, search(neighbour,bound));				
			}else{
				System.out.println(neighbour.board.getPlayerNode().Row+ " "+neighbour.board.getPlayerNode().Column+ " < neighbour high bound");

				fn=Math.min(fn,f);
			}
		}
		return fn;
	}

	public int wikiSearch(Move node, int g, int bound){
		int f=node.getHeuristicValue();
		if(f>bound)
			return f;
		if(node.board.isWin())
			return Integer.MIN_VALUE;
		int min=Integer.MIN_VALUE;
		for(Move neighbour : node.getNextMoves()){
			int t=wikiSearch(neighbour,g+node.getHeuristicValue(),bound);
			if(t==Integer.MIN_VALUE){ //if found return found
				winMove=neighbour;
				return Integer.MIN_VALUE; //return found
			}if(t<min){
				min=t;
			}
		}
		return min;
	}
	public Move wikidaStar(Move root){
		int bound=root.getHeuristicValue();
		while(true){
		int t=wikiSearch(root,0,bound);
		if(t==Integer.MIN_VALUE){ //if found return found
			return winMove; //return found
		}if(t==Integer.MAX_VALUE){
			return null; //not found
		}
		bound=t;
		}
	}

	public Move getVictoryPath(Move initialPosition)
	{
		if(IDA){
			return idaStar(initialPosition);

		}else if(WIKIDA){
			return wikidaStar(initialPosition);
		}else{
			System.err.println();
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
	}


	public Path play() {				

		Analyser analyser = new Analyser(initialState);
		PathFinder pathfinder = new PathFinder();
		Move initial = new Move(analyser, pathfinder);
		initial.board = initialState;
		initial.path = new Path();

		Move win = getVictoryPath(initial);
		if(win != null) {		
			//System.out.println(win.path);
			return win.path;
		}			


		//System.out.println();
		return null;
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
		//	BoardState board = BoardState.getBoardFromFile("test100/test001.in");
		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest");

		System.out.println(board);
		Player noob = new Player(board);
		Path path = noob.play();
		System.out.println(path);
		board.movePlayer(path);
		System.out.println(board);
	}
}



