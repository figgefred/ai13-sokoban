package sokoban.FredTethMerge;

import sokoban.Tethik.*;
import java.util.ArrayList;
import java.util.List;

import sokoban.BoardPosition;
import sokoban.NodeType;

public class Move implements Comparable<Move> {
	public PathFinder pathfinder = new PathFinder();
	public Analyser analyser = null;
        public LiveAnalyser liveAnalyser;
	public BoardState board;
	public Path path;
	private Integer heuristic_value = null;
	public int pushes = 1;
	
	private SingleBlockPlayer singleBlockPlayer;
	
	public Move(LiveAnalyser liveAnalyser, Analyser analyser, PathFinder pathfinder) {
		this.pathfinder = pathfinder;
		this.analyser = analyser;
                this.liveAnalyser = liveAnalyser;
		singleBlockPlayer = new SingleBlockPlayer(liveAnalyser, analyser);
	}

	public int getHeuristicValue() {
		if(heuristic_value != null)
			return heuristic_value;
		
		heuristic_value = analyser.getHeuristicValue(board);
		
//		if(heuristic_value == Integer.MAX_VALUE || heuristic_value == Integer.MIN_VALUE)
//			return heuristic_value;
		
		return heuristic_value; 
	}
	
	public boolean isWin() {
		return board.isWin();
	}
	
	public List<Move> getNextPushMoves() {
		List<Move> possibleMoves = new ArrayList<Move>();
		List<BoardPosition> blocks = board.getBlockNodes();
		BoardPosition playerPos = board.getPlayerNode();	

                boolean isRealCorral = false;
                
                /*
                Tunnel tunnel = null;
                for(BoardPosition block: board.getBlockNodes())
                {
                    for(Tunnel t: board.getTunnels().get())
                    {
                        if(t.contains(block))
                            tunnel = t;
                    }
                }
                
                if(tunnel != null)
                {
                    tunnel.getPath(playerPos)
                }
                
                Move move = new Move(pathfinder, analyser);
                move.board = newBoard;
                move.path = path.cloneAndAppend(toPush);
                 */
                
                if(Player.DO_CORRAL_LIVE_DETECTION)
                {
                    List<CorralArea> l = liveAnalyser.getAreas(board);
                    if(l != null && l.size() > 1)
                    {   
                        CorralArea playerArea = null;
                        blocks = new ArrayList<>();
                        for(CorralArea a: l)
                        {
                            if(a.isCorralArea())
                            {
                                if(a.getFencePositions().size() > 0)
                                    isRealCorral = true;
                                for(BoardPosition fencePos: a.getFencePositions())
                                {
                                    blocks.add(fencePos);
                                }
                            }
                            else
                            {
                                playerArea = a;
                            }
                        }
                        if(!Player.CHEAT && playerArea != null)
                        {
                            for(BoardPosition p: playerArea.getNoFenceBlockPositions())
                            {
                                blocks.add(p);
                            }
                        }   
                    }
                }
                
                if(!isRealCorral)
                    blocks = board.getBlockNodes();
                
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
				
				Move move = new Move(liveAnalyser, analyser, pathfinder);
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
		
		List<BoardPosition> blocks = board.getBlockNodes();
		for(BoardPosition block : blocks)
		{
			if(board.get(block) == NodeType.BLOCK_ON_GOAL)
				continue;
			
			List<Move> goalPushingMoves = singleBlockPlayer.findGoalMoves(this, block);
			possibleMoves.addAll(goalPushingMoves);
			
			if(goalPushingMoves.size() > 0)
				break;
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