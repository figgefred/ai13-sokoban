package sokoban.Tethik;

import java.util.ArrayList;
import java.util.List;


public class SingleBlockMove extends Move {

	private Integer heuristic_value = null;
	public BoardPosition block;
	public int goalindex;
	
	public SingleBlockMove(Analyser analyser, PathFinder pathfinder, BoardPosition block, int goalindex) {
		super(analyser, pathfinder);
		this.block = block;
		this.goalindex = goalindex;
	}
	
	public int getHeuristicValue() {
		if(heuristic_value != null)
			return heuristic_value;
	
		heuristic_value = analyser.getHeuristicValue(board, block, goalindex);
		return heuristic_value; 
	}

	public boolean isWin() {
		return board.get(block) == NodeType.BLOCK_ON_GOAL;
	}
	
	public List<Move> getNextPushMoves() {
		List<Move> possibleMoves = new ArrayList<Move>();
		BoardPosition playerPos = board.getPlayerNode();	

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
			
			SingleBlockMove move = new SingleBlockMove(analyser, pathfinder, newBoard.getLastPushedBlock(), goalindex);
			move.board = newBoard;
			move.path = path.cloneAndAppend(getThere);
			move.path.append(block);
			move.pushes = pushes + 1;
			
//			if(move.getHeuristicValue() < this.getHeuristicValue())
//				continue; 
			
			possibleMoves.add(move);					
		}	
		
		return possibleMoves;
	}

}
