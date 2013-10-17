package sokoban.Tethik;

import java.util.ArrayList;
import java.util.List;

import sokoban.BoardPosition;

public class Move implements Comparable<Move> {
	public PathFinder pathfinder = new PathFinder();
	public Analyser analyser = null;
	public BoardState board;
	public Path path;
	private Integer heuristic_value = null;
	public int pushes = 1;
	
	private SingleBlockPlayer singleBlockPlayer;
	private int heuristic_bonus = 0;
	
	public Move(Analyser analyser, PathFinder pathfinder) {
		this.pathfinder = pathfinder;
		this.analyser = analyser;
		singleBlockPlayer = new SingleBlockPlayer(analyser);
	}

	public int getHeuristicValue() {
		if(heuristic_value != null)
			return heuristic_value;
		

		
		heuristic_value = analyser.getHeuristicValue(board);
		
		if(heuristic_value == Integer.MAX_VALUE || heuristic_value == Integer.MIN_VALUE)
			return heuristic_value;
		
		if(Player.DO_CORRAL_LIVE_DETECTION)
        {
            List<CorralArea> l = LiveAnalyser.getAreas(board);
            if(l != null && l.size() > 1)
            {
                for(CorralArea a: l)
                {
                    if(a.isCorralArea())
                    {
                        if(!a.getFencePositions().contains(board.getLastPushedBlock()))
                        {
                        	heuristic_value += 1;
                        }
                    }
                }
            }
        }
		
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
		for(BoardPosition blockPos : blocks)
		{
			// hitta ställen man kan göra förflyttningar av block.
			// skriva om sen..
			List<BoardPosition> pushPositions = board.getPushingPositions(blockPos);
		
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
				
				Path toPush = getThere.cloneAndAppend(blockPos);
				
				BoardState newBoard = (BoardState) board.clone();
				// move the player along the path.
				newBoard.movePlayer(toPush);
				// push the block by moving towards the block.
				newBoard.movePlayerTo(blockPos);
				
				Move move = new Move(analyser, pathfinder);
				move.board = newBoard;
				move.path = path.cloneAndAppend(toPush);
				move.pushes = pushes + 1;
				move.heuristic_bonus = this.heuristic_bonus;
				possibleMoves.add(move);					
			}	
		}
		
		return possibleMoves;
	}
	
	public List<Move> getNextMoves() {
		
		List<BoardPosition> blocks = board.getBlockNodes();
		List<Move> possibleMoves = getNextPushMoves();
		
//		int sum = 0;
		/* Block move based */
		for(BoardPosition blockPos : blocks)
		{
			List<Move> goalPushingMoves = singleBlockPlayer.findGoalMoves(this, blockPos);
			possibleMoves.addAll(goalPushingMoves);
//			sum += goalPushingMoves.size();
		} 
		//System.err.println("Found " + sum + " goalpush moves " + possibleMoves.size());
		
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