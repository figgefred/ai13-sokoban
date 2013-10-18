package sokoban.fredmaster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import sokoban.BoardPosition;
import sokoban.Direction;
import sokoban.NodeType;

public class Move implements Comparable<Move> {
	public PathFinder pathfinder = new PathFinder();
	public Analyser analyser = null;
	public BoardState board;
	public Path path;
	private Integer heuristic_value = null;
	public int pushes = 1;
        
        private LiveAnalyser liveAnalyser;
	protected Settings settings;
	private SingleBlockPlayer singleBlockPlayer;
	
	public Move(Analyser analyser, Settings settings, PathFinder pathfinder) {
		this.pathfinder = pathfinder;
		this.analyser = analyser;
                this.settings = settings;
                this.liveAnalyser = analyser.liveAnalyser;
		singleBlockPlayer = new SingleBlockPlayer(analyser);
	}

	public int getHeuristicValue() {
		if(heuristic_value != null)
			return heuristic_value;
		
		heuristic_value = analyser.getHeuristicValue(board);
		
//		if(heuristic_value == Integer.MAX_VALUE || heuristic_value == Integer.MIN_VALUE)
//			return heuristic_value;
		
		return heuristic_value; 
	}
	
	private Integer lower_bound = null;
	public int getLowerBound() {
		if(lower_bound == null)
			lower_bound = analyser.getLowerBound(board)*2;
		
		return lower_bound;
	}
	
	public boolean isWin() {
		return board.isWin();
	}
	
	public List<Move> getNextPushMoves() {
		List<Move> possibleMoves = new ArrayList<>();
		List<BoardPosition> blocks = board.getBlockNodes();
		BoardPosition playerPos = board.getPlayerNode();	
		
                
                boolean isRealCorral = false;
                
                if(Player.DO_CORRAL_LIVE_DETECTION)
                {
                    blocks = getCorralFenceBlocks();
                    if(blocks != null)
                    {
                        isRealCorral = true;
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
                                
                                if(Player.DO_EXPENSIVE_DEADLOCK && liveAnalyser.isFrozenDeadlockState(board, new HashSet<BoardPosition>(), newBoard.getLastPushedBlock()))
                                {
                                    continue; // DEADLOCK
                                }
                                
                                Tunnel tunnel = null;
                                if(Player.DO_TUNNEL_MACRO_MOVE)
                                {
                                    tunnel = getTunnel(newBoard);
                                }
                                getThere.append(block);
                                Move move = getMove(tunnel, newBoard, block, getThere);
                                possibleMoves.add(move);
			}	
		}
		
		return possibleMoves;
	}
        
        private Move getMove(Tunnel tunnel, BoardState newBoard, BoardPosition playerPos, Path getThere)
        {
            Move move = new Move(analyser, settings, pathfinder);        
            Path tunnelPath = null;                 
            
            // Tunnel must be oneway
            if(tunnel != null && tunnel.isOneWay())
            {
                tunnelPath = tunnel.getPath(newBoard.getLastPushedBlock());
                if(tunnelPath != null && tunnelPath.size() > 1)
                {
                    BoardPosition initBlockPos = newBoard.getLastPushedBlock();
                    int counter = 1;
                    Path walkedPath = new Path();
                    for(BoardPosition nextStep: tunnelPath.getPath())
                    {
                        boolean blocked = true;
                        
                        // As long as this isn't the last node in the tunnel
                        //if(!nextStep.equals(tunnelPath.last()))
                        //{   
                            Direction dir = newBoard.getPlayerNode().getDirection(nextStep);
                            BoardPosition blockFuture = nextStep.getNeighbouringPosition(dir);
                            
                            // Make sure that the future block position is valid for moving
                            if( tunnel.contains(blockFuture) && !blockFuture.equals(nextStep) && newBoard.get(blockFuture).isSpaceNode())
                            {
                                
                                // push
                                newBoard.movePlayerTo(nextStep);
                                walkedPath.append(nextStep);
                                counter++;
                                
                                //System.out.println(newBoard);
                               // try{Thread.sleep(1500);}catch(InterruptedException ex){};
                                
                                // If block isn't resting on a goal then go on pushing
                                if(newBoard.get(newBoard.getLastPushedBlock()) != NodeType.BLOCK_ON_GOAL)
                                {
                                    blocked = false;
                                }
                            }
                        //}
                        
                        if(blocked)
                            break;
                    }
                    getThere = getThere.cloneAndAppend(walkedPath);
                    move.board = newBoard;
                    move.path = path.cloneAndAppend(getThere);
                    move.pushes = pushes + counter;
                    //move.path.append(playerPos);
                    return move;
                }
            }
            move.board = newBoard;
            move.path = path.cloneAndAppend(getThere);
            //move.path.append(newBoard.getPlayerNode());
            move.pushes = pushes + 1;				
            return move;
        }
        
        private Tunnel getTunnel(BoardState state)
        {
            Tunnel tunnel = null;
            for(Tunnel t: state.getTunnels().get())
            {
                if(t.contains(state.getLastPushedBlock()) && !t.contains(state.getPlayerNode()))
                {
                    tunnel = t;
                    break;
                }
            }
            if(tunnel != null && tunnel.isOneWay())
            {
                /*
                 * Example of tunnel
                 * ########
                 *@$----->$     
                 * ########
                 * 
                 */
            }
            else if(tunnel != null && !tunnel.isOneWay())
            {
                /*
                 * Example of tunnel
                 * ########
                 * @$     #
                 * ###### #
                 * 
                 */
                tunnel = null;
            }
            return tunnel;
        }
        
        private List<BoardPosition> getCorralFenceBlocks()
        {
            List<BoardPosition> blocks = new ArrayList<BoardPosition>();
            List<CorralArea> l = liveAnalyser.getAreas(board);
            if(l != null && l.size() > 1)
            {   
                CorralArea playerArea = null;
                blocks = new ArrayList<>();
                for(CorralArea a: l)
                {
                    //System.out.println(a);
                    if(a.isCorralArea())
                    {
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
                if( !Player.CHEAT && playerArea != null)
                {
                    for(BoardPosition p: playerArea.getNoFenceBlockPositions())
                    {
                        blocks.add(p);
                    }
                }  
                //try{Thread.sleep(60000);}catch(InterruptedException ex){}
            }
            
            if(blocks.size() == 0)
                return null;
            return blocks;
        }
	
	public List<Move> getNextMoves() {
		
		
		List<Move> possibleMoves = getNextPushMoves();
		
                if(settings.MOVE_DO_GOAL_MOVES)
                {
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