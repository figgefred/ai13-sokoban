package sokoban.fredmaster2;

import java.util.ArrayList;
import java.util.List;

import sokoban.BoardPosition;
import sokoban.Direction;

public class Move implements Comparable<Move> {
	private PathFinder pathfinder;
	private Analyser analyser = null;
	public BoardState board;
	public Path path;
	private Integer heuristic_value = null;
        
        private LiveAnalyser LiveAnalyser;
	
        public Move(PathFinder pathfinder, Analyser analyser)
        {
            //super();
            this.pathfinder = pathfinder;
            this.analyser = analyser;
            
            this.LiveAnalyser = analyser.LiveAnalyser;
        }
        
	/*public static void initPreanalyser(BoardState board) {
		analyser = new Analyser(board);
	}*/
	

	public int getHeuristicValue() {
		if(heuristic_value != null)
			return heuristic_value;
		
		BoardPosition lastpos = path.get(path.getPath().size() - 2);
		BoardPosition pushedBlock = null;
		if(lastpos != null) {
			Direction pushDirection = lastpos.getDirection(board.getPlayerNode());
			pushedBlock = board.getPlayerNode().getNeighbouringPosition(pushDirection);
		}
                
                if(Player.DO_HEURISTIC_CACHING)
                {
                    Integer val = LiveAnalyser.getHeuristicCacheVal(board, pushedBlock);
                    if(val != null)
                        return val.intValue();
                }
		heuristic_value = analyser.getHeuristicValue(board, pushedBlock);
                if(Player.DO_HEURISTIC_CACHING)
                {
                    LiveAnalyser.setHeuristic(board, pushedBlock, heuristic_value);
                }
		return heuristic_value; 
	}
	
	public List<Move> getNextMoves() {
		List<Move> possibleMoves = new ArrayList<Move>();
		List<BoardPosition> blocks = null;
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
                    List<CorralArea> l = LiveAnalyser.getAreas(board);
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
                    else 
                        blocks = board.getBlockNodes();
                }
                
                if(!isRealCorral)
                    blocks = board.getBlockNodes();
		
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
				
				Move move = new Move(pathfinder, analyser);
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