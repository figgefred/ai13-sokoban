package sokoban.Tethik;

import java.util.ArrayList;
import java.util.List;

import sokoban.BoardPosition;
import sokoban.NodeType;

public class Move implements Comparable<Move> {
	public PathFinder pathfinder = new PathFinder();
	public Analyser analyser = null;
	public BoardState board;
	public Path path;
	private Integer heuristic_value = null;
	public int pushes = 1;
	
	private SingleBlockPlayer singleBlockPlayer;
	
	public Move(Analyser analyser, PathFinder pathfinder) {
		this.pathfinder = pathfinder;
		this.analyser = analyser;
		singleBlockPlayer = new SingleBlockPlayer(analyser);
	}

	public int getHeuristicValue() {
		if(heuristic_value != null)
			return heuristic_value;
		
		heuristic_value = analyser.getHeuristicValue(board);		
		return heuristic_value; 
	}
	
	public boolean isWin() {
		return board.isWin();
	}
	
	public List<Move> getNextPushMoves() {
		List<Move> possibleMoves = new ArrayList<Move>();
		List<BoardPosition> blocks = board.getBlockNodes();
		BoardPosition playerPos = board.getPlayerNode();	
		
		/* Block move based */
		for(BoardPosition block : blocks)
		{
			// hitta ställen man kan göra förflyttningar av block.
			// skriva om sen..
			List<BoardPosition> pushPositions = board.getPushingPositions(block);
		
			// now do pathfinding to see if player can reach it..
			for(BoardPosition candidate : pushPositions)
			{
				Path getThere;
				if(candidate.equals(playerPos))
					getThere = new Path(candidate);
				else
					getThere = pathfinder.getPath(board, candidate);		
				
				if(getThere == null) // no path found
					continue;				
				
				BoardState newBoard = (BoardState) board.clone();
				// move the player along the path.
				newBoard.haxMovePlayer(getThere);
				// push the block by moving towards the block.
				newBoard.movePlayerTo(block);
				
				Move move = new Move(analyser, pathfinder);
				move.board = newBoard;
				move.path = path.cloneAndAppend(getThere);
				move.path.append(block);
				move.pushes = pushes + 1;
				possibleMoves.add(move);					
			}	
		}
		
		return possibleMoves;
	}
	
	public List<Move> getNextMoves() {
		
		
		List<Move> possibleMoves = getNextPushMoves();
		
		if(analyser.getSettings().MOVE_DO_GOAL_MOVES) {		
			List<BoardPosition> blocks = board.getBlockNodes();
			for(BoardPosition block : blocks)
			{
				if(board.get(block) == NodeType.BLOCK_ON_GOAL)
					continue;
				
				List<Move> goalPushingMoves = singleBlockPlayer.findGoalMoves(this, block);
				possibleMoves.addAll(goalPushingMoves);
				
	//			if(goalPushingMoves.size() > 0)
	//				break;
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
	
	@Override
	public int hashCode() {
		return board.hashCode();
	}
}