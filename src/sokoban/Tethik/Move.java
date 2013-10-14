package sokoban.Tethik;

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
	
	public Move(Analyser analyser, PathFinder pathfinder) {
		this.pathfinder = pathfinder;
		this.analyser = analyser;
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
		heuristic_value = analyser.getHeuristicValue(board);
		return heuristic_value; 
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
			List<BoardPosition> pushPositions = board.getPushingPositions(blockPos);
		
			// now do pathfinding to see if player can reach it..
			for(BoardPosition candidate : pushPositions)
			{
				Path toPush;
				if(candidate.equals(playerPos))
					toPush = new Path(candidate);
				else
					toPush = pathfinder.getPath(board, candidate);		
				
				if(toPush == null) // no path found
					continue;				
				
				toPush.append(blockPos);
				
				BoardState newBoard = (BoardState) board.clone();
				// move the player along the path.
				newBoard.movePlayer(toPush);
				// push the block by moving towards the block.
				newBoard.movePlayerTo(blockPos);
				
				Move move = new Move(analyser, pathfinder);
				move.board = newBoard;
				move.path = path.cloneAndAppend(toPush);
				move.pushes = pushes + 1;
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