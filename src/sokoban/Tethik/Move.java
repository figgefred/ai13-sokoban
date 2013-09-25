package sokoban.Tethik;

import java.util.ArrayList;
import java.util.List;

import sokoban.BoardPosition;
//import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.NodeType;

public class Move implements Comparable<Move> {
	private static PathFinder pathfinder = new PathFinder();
	private static PreAnalyser analyser = null;
	public BoardState board;
	public Path path;
	private Integer heuristic_value = null;
	
	public static void initPreanalyser(BoardState board) {
		analyser = new PreAnalyser(board);
	}
	
	public int getHeuristicValue() {
		if(heuristic_value != null)
			return heuristic_value;
		
		if(board.isWin()) {				
			heuristic_value = Integer.MAX_VALUE;
			return heuristic_value;
		}
		
		//int val = -path.getPath().size();
		int val = 0;
		List<BoardPosition> blocks = board.getBlockNodes();
		for(BoardPosition block : blocks)
		{
			
			if(board.getNode(block) == NodeType.BLOCK_ON_GOAL) {					
				continue;
			}
			
			//board.isInCorner(block) || 
			if(analyser.isBadPosition(block)) {
				heuristic_value = Integer.MIN_VALUE;
				return heuristic_value;					
			}
			
			int mindistToGoal = Integer.MAX_VALUE;
			for(BoardPosition goal : board.getGoalNodes())					
			{
				if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL)
				{						
					continue;
				}
				
				mindistToGoal = Math.min(mindistToGoal, block.DistanceTo(goal));
			}
			val -= mindistToGoal;
		}
		heuristic_value = val;
		return heuristic_value;
		/*
		int val = 0;
		List<BoardPosition> blocks = board.getBlockNodes();
		for(BoardPosition block : blocks)
		{
			
			if(board.getNode(block) == NodeType.BLOCK_ON_GOAL) {
				val += 100;
				continue;
			}
			
			if(board.isInCorner(block)) {
				heuristic_value = Integer.MIN_VALUE;
				return heuristic_value;					
			}
			
			/*
			// check if we can move this block
			boolean movable = false;
			for(BoardPosition candidate : board.getPushingPositions(block))
			{
				if(candidate.equals(board.getPlayerNode()))
					movable = true;
				else
					movable = pathfinder.getPath(board, board.getPlayerNode(), candidate) == null;		
			
				if(movable)
					break;
			}
			
			if(!movable) {
				val += Integer.MAX_VALUE / blocks.size();
				continue;
			}*/
			
			/*
			for(BoardPosition goal : board.getNeighbours(block))					
			{
				if(board.getNode(goal) == NodeType.WALL)
					val++;
			}
			
			int mindistToGoal = Integer.MAX_VALUE;
			for(BoardPosition goal : board.getGoalNodes())					
			{
				if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL)
					continue;
				
				mindistToGoal = Math.min(mindistToGoal, block.DistanceTo(goal));
			}
			val -= mindistToGoal;
					
			
			
		}
		
		/*
		val += path.getPath().size();
		
		heuristic_value = val; // * number_of_blocks_not_in_goal;
		return heuristic_value;
		*/
	}
	
	public List<Move> getNextMoves() {
		List<Move> possibleMoves = new ArrayList<Move>();
		List<BoardPosition> blocks = board.getBlockNodes();
		BoardPosition playerPos = board.getPlayerNode();			
		
		for(BoardPosition blockPos : blocks)
		{
			// hitta ställen man kan göra förflyttningar av block.
			// skriva om sen..
			List<BoardPosition> pushPositions = board.getPushingPositions(blockPos);
			List<BoardPosition> neighbours = board.getNeighbours(blockPos);
			// srsly, skriva om sen xD
			List<BoardPosition> candidates = new ArrayList<BoardPosition>();
			
			
			for(BoardPosition pushpos : pushPositions) {
				if(neighbours.contains(pushpos))
					candidates.add(pushpos);
			}			
						
			// now do pathfinding to see if player can reach it..
			for(BoardPosition candidate : candidates)
			{
				Path toPush;
				if(candidate.equals(playerPos))
					toPush = new Path(candidate);
				else
					toPush = pathfinder.getPath(board, playerPos, candidate);		
				
				if(toPush == null) // no path found
					continue;				
				
				//toPush.getPath().remove(playerPos); // remove duplicate player pos (was causing null)
				toPush.append(blockPos);
				
				BoardState newBoard = (BoardState) board.clone();
				// move the player along the path.
				newBoard.movePlayer(toPush);
				// push the block by moving towards the block.
				newBoard.movePlayerTo(blockPos);
				
				Move move = new Move();
				move.board = newBoard;
				move.path = path.cloneAndAppend(toPush);
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