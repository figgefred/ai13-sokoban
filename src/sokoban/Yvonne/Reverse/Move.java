package sokoban.Yvonne.Reverse;

import java.util.ArrayList;
import java.util.List;

public class Move implements Comparable<Move> {
	private static PathFinder pathfinder = new PathFinder();
	private static Analyser analyser = null;
	public BoardState board;
	public Path path;
	private Integer heuristic_value = null;
	
	public static void initPreanalyser(BoardState board) {
		analyser = new Analyser(board);
	}
	

	public int getHeuristicValue() {
		if(heuristic_value != null)
			return heuristic_value;
		
		heuristic_value = analyser.getHeuristicValue(board);
		return heuristic_value; 
	}
	
	public List<Move> getNextMoves() {
		List<Move> possibleMoves = new ArrayList<Move>();
		List<BoardPosition> blocks = board.getBlockNodes();
		BoardPosition playerPos = board.getPlayerNode();			
		
		for(BoardPosition blockPos : blocks)
		{
			

			// hitta ställen man kan göra förflyttningar av block.
			// skriva om sen..
			List<BoardPosition> pushPositions = board.getDraggingPositions(blockPos);
		
			// now do pathfinding to see if player can reach it..
			for(BoardPosition candidate : pushPositions)
			{
				Path toDrag;
				if(candidate.equals(playerPos))
					toDrag = new Path(candidate);
				else
					toDrag = pathfinder.getPath(board, playerPos, candidate);		
				
				if(toDrag == null) // no path found
					continue;				
				
				//toPush.getPath().remove(playerPos); // remove duplicate player pos (was causing null)
				toDrag.append(blockPos);
				
				BoardState newBoard = (BoardState) board.clone();
				// move the player along the path.
				newBoard.movePlayer(toDrag);
				// push the block by moving towards the block.
				newBoard.movePlayerTo(blockPos);
				
				Move move = new Move();
				move.board = newBoard;
				move.path = path.cloneAndAppend(toDrag);
				possibleMoves.add(move);					
			}		
						
		}
		
		return possibleMoves;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Move))
			return false;
		
		Move b = (Move) o;			
		return b.board.equals(this.board);
		
	}

	@Override
	public int compareTo(Move o) {			
		if(o.getHeuristicValue() > this.getHeuristicValue())
			return 1;
		else if(o.getHeuristicValue() < this.getHeuristicValue())
			return -1;
		return 0;
	}
}