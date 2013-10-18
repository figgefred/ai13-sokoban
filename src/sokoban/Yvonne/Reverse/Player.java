package sokoban.Yvonne.Reverse;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
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
	public static boolean VERBOSE = false;
	public volatile boolean shouldStop = false;
	Move winMove=null;
	public static boolean IDA =false;
	public static boolean WIKIDA=false;
	public static boolean ASTAR=false;

	private BoardState initialState;

	public Player(BoardState initialState)
	{		
		this.initialState = initialState;

		if(initialState.getBlockNodes().size() != initialState.getGoalNodes().size())
			throw new IllegalArgumentException("Different number of goals than blocks");
	}




	public Move idaStar (Move root){
		winMove=null;
		int bound = root.getHeuristicValue()*(-1);

		while(bound < Integer.MAX_VALUE){
		//	System.out.println("search("+root.board.getBlockNodes().get(0).Row+ " "+root.board.getBlockNodes().get(0).Column+")");
			bound = search(root,bound);
		}

		return winMove;
	}

	public int search(Move node, int bound){


		if(node.board.isWin()){ //if node is a goal, exit algorithm and return goal
			winMove=node;
			//System.out.println("WIN!");
			return Integer.MAX_VALUE; 
		}

		if(node.getNextMoves().isEmpty()){ //if node has no children, return infinity
			//System.out.println(node.board.getPlayerNode().Row+ " "+node.board.getPlayerNode().Column+ " No children");
			return Integer.MIN_VALUE; 
		}

		int fn = Integer.MIN_VALUE;
		for(Move neighbour : node.getNextMoves()){

			int f=neighbour.pushes+neighbour.getHeuristicValue()*(-1);
			if(f<=bound){		
				//System.out.println(neighbour.board.getPlayerNode().Row+ " "+neighbour.board.getPlayerNode().Column+ " < neighbour low bound");

				fn=Math.min(fn, search(neighbour,bound));				
			}else{
				//System.out.println(neighbour.board.getPlayerNode().Row+ " "+neighbour.board.getPlayerNode().Column+ " < neighbour high bound");

				fn=Math.min(fn,f);
			}
		}
		return fn;
	}
	
//	private HashSet<Move> visitedStates = new HashSet<Move>();

	public int wikiSearch(Move node, int g, int bound){
		winMove=null;
	//	visitedStates.add(node);

		if(node.getHeuristicValue() == Integer.MIN_VALUE)
			return Integer.MIN_VALUE;
		int f = node.getHeuristicValue()-g;
		if(VERBOSE){
			System.out.println("f: "+f+" bound: "+bound);
			System.out.println(node.board);
		}
		if(node.board.isWin()){
			//	System.out.println("Win");
			winMove=node;
			//System.out.println(winMove.board);
			return Integer.MAX_VALUE;
		}
		if(f<bound){

			return f;

		}
		int max=Integer.MIN_VALUE;

		List<Move> nextMoves=node.getNextMoves();
		//Collections.sort(nextMoves, new moveComp());
		Collections.sort(nextMoves);
		for(Move child : nextMoves){
		//for(Move child : node.getNextMoves()){
	//		if(visitedStates.contains(child))
		//		continue;
			int t=wikiSearch(child,g+1,bound);
			//	int t=wikiSearch(child,g,bound);
			//	System.out.println("child with heuristic value: "+t + "move: "+child.hashCode());
			if(t==Integer.MAX_VALUE){ //if found return found
				//	winMove=child;
				//	System.out.println("Found");
				//	System.out.println(winMove.board);
				return Integer.MAX_VALUE; //return found
			}if(t>=max){
				max=t;
			}

		}
		return max;
	}



	public Move wikidaStar(Move root){
		int bound=root.getHeuristicValue();
		while(true){
			int t=wikiSearch(root,0,bound);
			if(t==Integer.MAX_VALUE){ //if found return found
				return winMove; //return found
			}if(t==Integer.MIN_VALUE){
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
		}else if(ASTAR){
			closedSet=new HashSet<Integer>();
			openSet=new PriorityQueue<Move>();
			HashSet<Integer> openSetHash=new HashSet<Integer>();
			openSet.add(initialPosition);
			// HashMap<Integer, Integer> f = new HashMap<Integer,Integer>();
			HashMap<Integer, Integer> g = new HashMap<Integer,Integer>();
			g.put(initialPosition.board.hashCode(), 0);
			// f.put(initialPosition.board.hashCode(), g.get(initialPosition.board.hashCode())+initialPosition.getHeuristicValue());


			while(!openSet.isEmpty()){

				Move current = openSet.poll();
				
				//	System.out.println(current.board);
				


				if(current.board.isWin())
					return current;

				closedSet.add(current.board.hashCode());
				for(Move neighbour : current.getNextMoves()){

					int tentG=current.pushes+1;
					int tentF=tentG+neighbour.getHeuristicValue();

					if(closedSet.contains(neighbour.board.hashCode()) && tentF >=(neighbour.getHeuristicValue()+neighbour.pushes)){
						continue;
					}

					if(!openSetHash.contains(neighbour.board.hashCode()) || tentF <(neighbour.getHeuristicValue()+neighbour.pushes)){
						neighbour.pushes=tentG;
						neighbour.f=tentF;
						if(!openSetHash.contains(neighbour.board.hashCode())){
							openSet.add(neighbour);
							openSetHash.add(neighbour.board.hashCode());
						}
					}
				}
			}
			return null;
		}else{
			System.err.println();
			openSet = new PriorityQueue<Move>();
			closedSet = new HashSet<Integer>();
			openSet.add(initialPosition);
			//initialPosition.print();

			int bound=initialPosition.getDistanceValue();
			while(!openSet.isEmpty() && !shouldStop)
			{
				Move node = openSet.poll();
				if(VERBOSE) {
					System.out.println(openSet.size() + " " + closedSet.size());
					System.out.println("Pushes : " + node.pushes);
					System.out.println(node.path.getPath().size() + ", " + node.getDistanceValue() + ", " + closedSet.size() + ", " + node.board.hashCode());
					System.out.println(node.board);
				}

				if(node.board.isWin()){        	
					return node;        	
				}
				for(Move neighbour: node.getNextMoves())
				{
					int value = neighbour.getDistanceValue();
				
					if (closedSet.contains(neighbour.board.hashCode())) {        			
						continue;
					}

					closedSet.add(neighbour.board.hashCode());
					if(value>Integer.MIN_VALUE){
						openSet.add(neighbour); 
					bound=value;
					}
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
	//	BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");
			BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest");

		System.out.println(board);
		
		board=board.getEndingState();
		System.out.println(board);
		Player noob = new Player(board);
		
		Path path = noob.play();
		   if(path != null)
           {
               board.movePlayer(path);
               System.out.println(board);
           }
	}
}



