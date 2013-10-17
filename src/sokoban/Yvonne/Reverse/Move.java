package sokoban.Yvonne.Reverse;

import java.util.ArrayList;
import java.util.List;

import sokoban.BoardPosition;
import sokoban.Direction;

public class Move implements Comparable<Move> {
	private PathFinder pathfinder = new PathFinder();
	private Analyser analyser = null;
	public BoardState board;
	public Path path;
	private Integer heuristic_value = null;
	public int pushes = 1;
	public Integer f=0;
	public static boolean ASTAR = true;

	public Move(Analyser analyser, PathFinder pathfinder) {
		this.pathfinder = pathfinder;
		this.analyser = analyser;
	}


	public int getFValue(){
		return f;
	}

	public void calcF(){
		f=pushes+heuristic_value;
	}


	public int getHeuristicValue() {
		if(heuristic_value != null)
			return heuristic_value;

		//		BoardPosition lastpos = path.get(path.getPath().size() - 2);
		//		BoardPosition pushedBlock = null;
		//		if(lastpos != null) {
		//			Direction pushDirection = lastpos.getDirection(board.getPlayerNode());
		//			pushedBlock = board.getPlayerNode().getNeighbouringPosition(pushDirection);
		//		}
		if(ASTAR){
		heuristic_value = analyser.getHeuristicValue(board)*-1;
		}else{
			heuristic_value = analyser.getHeuristicValue(board);
		}
		//heuristic_value = analyser.getHeuristicValue(board);
		return heuristic_value;
	}
	
	public void print(){
		analyser.printDistanceMatrix(board);
	}
	
	//Distance between block and player
	public int getDistanceValue(){
		BoardPosition goal = board.getBlockNodes().get(0);
		BoardPosition w = board.getGNodes().get(0);
		
		return (Math.abs(w.Row-goal.Row)+Math.abs(w.Column-goal.Column));
	//	return analyser.getDistanceValue(board.getPlayerNode().Row,board.getPlayerNode().Column);
	}

	public List<Move> getNextMoves() {
		List<Move> possibleMoves = new ArrayList<Move>();
		List<BoardPosition> blocks = board.getBlockNodes();
		BoardPosition playerPos = board.getPlayerNode();		

		/* Block move based */
		for(BoardPosition blockPos : blocks)
		{
			// hitta ställen man kan göra förflyttningar av block.
			// skriva om sen..
			List<BoardPosition> pullPositions = board.getPullingPositions(blockPos);

			// now do pathfinding to see if player can reach it..
			for(BoardPosition candidate : pullPositions)
			{
				Path toPush;
				if(candidate.equals(playerPos))
					toPush = new Path(candidate);
				else{
					toPush = pathfinder.getPath(board, candidate);		
					//toPush.getPath().remove(toPush.last());
				}
				if(toPush == null) // no path found
					continue;				

				//toPush.append(blockPos);

				BoardState newBoard = (BoardState) board.clone();
				// move the player along the path.
				Direction relation = newBoard.getDirection(candidate, blockPos);
				int row=candidate.Row;
				int col=candidate.Column;
			  	switch(relation)
		     	{
		     	case UP:
		     		row++;
		     		break;
		     	case DOWN:
		     		row--;   	
		     		break;
		     	case LEFT:
		     		col++;
		     		break;
		     	case RIGHT:
		     		col--;
		     		break;
		     	}
			  	
			  	
				toPush.append(new BoardPosition(row,col));
				newBoard.movePlayer(toPush);
				// push the block by moving towards the block.

				Move move = new Move(analyser, pathfinder);
				move.board = newBoard;
				move.path = path.cloneAndAppend(toPush);
				move.pushes = pushes + 1;
				possibleMoves.add(move);					
			}		

		} 
	/*	for(Move mive : possibleMoves){
			System.out.println("moves i en path:");
			System.out.println(mive.board);
		}
	*/

		return possibleMoves;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Move))
			return false;

		Move b = (Move) o;			
		return b.board.equals(this.board);

	}


	public int compareTo(Move o) {		
		if(ASTAR){
			if(o.getFValue() > this.getFValue())
				return -1;
			else if(o.getFValue() < this.getFValue())
				return 1;
			return 0;

		}else{		
			if(o.getHeuristicValue() > this.getHeuristicValue())
				return 1;
			else if(o.getHeuristicValue() < this.getHeuristicValue())
				return -1;
			return 0;

		}
	}

}