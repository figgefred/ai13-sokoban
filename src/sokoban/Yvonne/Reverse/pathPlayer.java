package sokoban.Yvonne.Reverse;

public class pathPlayer {


	private BoardState initialState;
	private AStar_Path search;

	public pathPlayer(BoardState initialState)
	{		
		this.initialState = initialState;

		if(initialState.getBlockNodes().size() != initialState.getGoalNodes().size())
			throw new IllegalArgumentException("Different number of goals than blocks");
	}

	public Move getVictoryPath(Move initial){
		
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
}
