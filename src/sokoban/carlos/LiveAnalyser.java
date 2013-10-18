/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 *
 * @author figgefred
 */
public class LiveAnalyser {
   	
	private PathFinder pathfinder;
        private Map<BoardState, Map<CorralArea, List<CorralArea>>> CachedAreas;
        private Map<BoardState, Map<BoardPosition, Integer>> HeuristicCache;
        private Map<BoardState, Map<BoardPosition, Boolean>> DeadlockCache;
        
        
        private static boolean VERBOSE = Player.VERBOSE;
        
	public LiveAnalyser(PathFinder finder)
	{
		this.pathfinder = finder;
                if(Player.DO_CORRAL_CACHING)
                    CachedAreas = new HashMap<>();
                if(Player.DO_HEURISTIC_CACHING)
                {
                    HeuristicCache = new HashMap<>();
                }
                if(Player.DO_EXPENSIVE_DEADLOCK)
                    DeadlockCache = new HashMap<>();
	}
   
	private boolean is4x4BlockTopLeftCorner(BoardState board, BoardPosition pos) {
		if(!board.isBlockingNode(pos))
			return false;
		
		NodeType nodes[] = new NodeType[] {
			board.get(pos.Row, pos.Column),
			board.get(pos.Row, pos.Column+1),
			board.get(pos.Row+1, pos.Column),
			board.get(pos.Row+1, pos.Column+1)
		};		
		
		boolean atLeastOneIsBlock = false;
		for(NodeType node : nodes)
		{
			if(!board.isBlockingNode(node))
				return false;			
			atLeastOneIsBlock = atLeastOneIsBlock || node == NodeType.BLOCK;
		}
		
		return (atLeastOneIsBlock);		
	}
	
	public boolean is4x4Block(BoardState board, BoardPosition block) {
		BoardPosition leftDown = new BoardPosition(block.Row-1, block.Column-1);
		BoardPosition leftTop = new BoardPosition(block.Row, block.Column-1);
		BoardPosition rightDown = new BoardPosition(block.Row-1, block.Column);
		return is4x4BlockTopLeftCorner(board, block) 
				|| is4x4BlockTopLeftCorner(board, leftDown)
				|| is4x4BlockTopLeftCorner(board, rightDown)
				|| is4x4BlockTopLeftCorner(board, leftTop);
	}
    
        public boolean setHeuristic(BoardState state, BoardPosition blockPushed, int val)
        {
            if(Player.DO_HEURISTIC_CACHING)
            {
                Map<BoardPosition, Integer> map = HeuristicCache.get(state);
                if(map == null)
                {
                    map = new HashMap<>();
                    HeuristicCache.put(state, map);
                }
                return (map.put(blockPushed, val)!= null);
            }
            return false;
        }
        
        public Integer getHeuristicCacheVal(BoardState state, BoardPosition blockPushed)
        {
            if(Player.DO_HEURISTIC_CACHING)
            {
                Map<BoardPosition, Integer> map = HeuristicCache.get(state);
                if(map == null)
                    return null;
                else
                    return map.get(blockPushed);
            }
            else
                return null;
        }
        
    private boolean isBlockType(NodeType type)
    {
        return type == NodeType.BLOCK || type == NodeType.BLOCK_ON_GOAL;
    }
   
    private boolean isBlockingType(NodeType type)
    {
        return isBlockType(type) || type == NodeType.WALL;
    }
    
    private List<CorralArea> getCachedArea(BoardState state)
    {
        if(!Player.DO_CORRAL_CACHING)
            return null;
        
        Map<CorralArea, List<CorralArea>> map = CachedAreas.get(state);
        if(map != null)
        {
            for(CorralArea key : map.keySet())
            {
                if(key.hasMember(state.getPlayerNode()))
                {
                    return map.get(key);
                }
            }
        }
        return null;
    }
    
    /**
     * Returns a list of corral areas and (or only) the area of where the
     * player is.
     * 
     * A corral area is an area which a player cannot reach except by pushing
     * a block.
     * 
     * CorralArea's will hold a field pointing to a list of blocks that are acting
     * as fences (creating the corrol fence). These should be prioritized so that
     * a corral area can be solved as fast as possible. This is so called 'corrol pruning'.
     * 
     * @param board
     * @return 
     */
    public List<CorralArea> getAreas(BoardState board)
    {
        List<CorralArea> list = null;
        if(Player.DO_CORRAL_CACHING)
        {
            list = getCachedArea(board);
            if(list != null)
            {
                return list;
            }
        }
        // A list containing all the nodes "visited"
        Set<BoardPosition> visited = new HashSet<>();
        list = new ArrayList<>();
        
        int areaCounter = 1;
        
        //**************************//
        // Iterpret areas from map //
        //*********************** //
        
        for(int r = 0; r < board.getRowsCount(); r++)
        {
            for(int c = 0; c < board.getColumnsCount(); c++)
            {
                BoardPosition p = new BoardPosition(r,c);
                NodeType nodeType = board.get(p);
                
                if( !visited.contains(p) && nodeType.isSpaceNode() )
                {
                    CorralArea area = new CorralArea(areaCounter++, board);
                    setCorralArea(board, area, p, visited);
                    list.add(area);
                }
            }
        }

        // If there is only a play area there is no point to continue
        if(list.size() <= 1)
            return list;
        
        //**************************//
        // Find the corral fences  //
        //*********************** //
        
        // !!!!!!!!!!!!!!
        // The following code segments can probably be implemented in a better way.
        // !!!!!!!!!!!!!!        
        
        // A list of block nodes that are fence candidates for the corral areas
        Map<BoardPosition, CorralFenceCandidate> fenceCandidates = new HashMap<>();
        
        // This part is crucial, we must find out which blocks are 'touched'
        // by more then 1 area
        for(CorralArea a: list)
        {
            //for(BoardPosition p: board.getBlockNodes())
            for(BoardPosition p: a.getAreaPositions())
            {
                if(board.get(p).isBlockNode())
                {
                    // For every a try to match every block
                    CorralFenceCandidate c = fenceCandidates.get(p);
                    if(c == null)
                    {
                        c = new CorralFenceCandidate(p);
                        fenceCandidates.put(p, c);
                    }
                    c.addCorralArea(a);
                }
            }
        }
        
        //******************************//
        // Add the fences to the area **//
        //******************************//
        
        // Naive merge
        CorralArea playArea = null;
        for(CorralFenceCandidate f : fenceCandidates.values())
        {
            for(CorralArea a: list)
            {
                if(f.isNodeOf(a))
                {
                    if(f.isPartOfCorralAreaFence())
                    {
                        a.addAsFenceNode(f.getBoardPosition(), board.get(f.getBoardPosition()));
                    }
                    else
                    {
                        a.add(f.getBoardPosition(), board.get(f.getBoardPosition()));
                    }
                }
                if(!a.isCorralArea())
                {
                    playArea = a;
                }
            }
        }
        if(!Player.CHEAT)
        {
            for(CorralArea a: list)
            {
                if(!a.getFencePositions().isEmpty())
                {
                    BoardPosition fenceBlock = a.getFencePositions().iterator().next();
                    exploreAndSetFence(board, a, fenceBlock, new HashSet<BoardPosition>());    
                }
            }
        }
        
        
        if(playArea == null)
            System.err.println("Oooops, no playarea warning.");
        
        if(Player.DO_CORRAL_CACHING)
        {
            Map<CorralArea, List<CorralArea>> map = CachedAreas.get(board);
            if(map == null)
            {
                map = new HashMap<>();
                CachedAreas.put(board, map);
            }
            map.put(playArea, list);  
        }
        return list;
    }
    
    public Map<CorralArea, List<BoardPosition>> getBlockAreas(BoardState board, List<CorralArea> corrals, BoardPosition movedBlock)
    {
        boolean hasAllBoxesOnGoals = true;
        boolean isACombinedCorral = false;
        
        for(CorralArea cArea: corrals)
        {
            // This is the "Play area", lets skip it
            if(!cArea.isCorralArea())
                break;
            
            for(BoardPosition box: board.getBlockNodes())
            {
                Direction[] dirs = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
                for(Direction d : dirs)
                {
                    BoardPosition N = board.getNeighbour(box, d);
                    BoardPosition O = board.getNeighbour(box, d.opposite());

//                    if(cArea.)


                }
            }
        }
        
        
        return null;
    }
    
    /**
     * Explore neighbouring blocks that could be part of fences. The input parameter
     * must be of block type and part of the fence in the corral area.
     * @param board
     * @param area
     * @param block
     * @param visited 
     */
    private void exploreAndSetFence(BoardState board, CorralArea area, BoardPosition block, Set<BoardPosition> visited)
    {
        if(!board.get(block).isBlockNode() || !area.getFencePositions().contains(block))
            return;
        
        if(!visited.contains(block))
        {
            visited.add(block);
            for(BoardPosition candidate: board.getNeighbours(block))
            {
                // If a block
                if(board.get(candidate).isBlockNode())
                {
                    // Is already part of post
                    if(area.getFencePositions().contains(candidate))
                    {
                        exploreAndSetFence(board, area, candidate, visited);
                    }
                    else if(area.getNoFenceBlockPositions().contains(candidate))
                    {
                        // This is fence node, remove from old set and add to fence positions
                        area.getNoFenceBlockPositions().remove(candidate);
                        area.getFencePositions().add(candidate);
                        exploreAndSetFence(board, area, candidate, visited);
                    }
                }
                else
                {
                    // Its skipped. Not interesting for this corral area's fence
                }
                

            }
        }
        
    }
    
    private void setCorralArea(BoardState board, CorralArea area, BoardPosition spaceNode, Set<BoardPosition> visited)
    {
        NodeType nodeType = board.get(spaceNode);
        area.add(spaceNode, nodeType);
        visited.add(spaceNode);
        
        for(BoardPosition neighbour: board.getNeighbours(spaceNode))
        {
            NodeType neighbourType = board.get(neighbour);
            if( !visited.contains(neighbour) && ( neighbourType.isSpaceNode()) )
            {
                // If unvisited expand!
                setCorralArea(board, area, neighbour, visited);
            }
            else if(neighbourType.isBlockNode())
            {
                //area.add(neighbour, neighbourType);
                visitNeighbouringBlocks(board, area, neighbour, new HashSet<BoardPosition>());
            }
        }
    }
    
    private void visitNeighbouringBlocks(BoardState board, CorralArea area, BoardPosition blockNode, Set<BoardPosition> visited)
    {
        NodeType nodeType = board.get(blockNode);
        area.add(blockNode, nodeType);
        visited.add(blockNode);
        for(BoardPosition blockNeighbour: board.getNeighbours(blockNode))
        {
            NodeType neighbourType = board.get(blockNeighbour);
            if( !visited.contains(blockNeighbour) && ( neighbourType.isBlockNode()) )
            {
                // If unvisited expand!
                visitNeighbouringBlocks(board, area, blockNeighbour, visited);
            }
        }
    }
    
	public static void main(String[] args) throws IOException, InterruptedException {
//		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");
		
		Vector<String> b = new Vector<String>();
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		
		String line;
                line = br.readLine();
		while(line != null) {
                    if(line.equals(""))
                        break;
                    b.add(line);
                    line = br.readLine();
		} // End while
		
                Player.DO_BIPARTITE_MATCHING = true;
                Player.DO_CORRAL_LIVE_DETECTION = true;
                Player.DO_DEADLOCKS_CONSTANTCHECK = true;
                Player.DO_DEADLOCKS_4x4 = true;
                Player.DO_GOAL_SORTING = false;
                
		//System.out.println(board);
		BoardState board = new BoardState(b, true);
                Settings s = new Settings();
                board.setSettings(s);
                PathFinder pFinder = new PathFinder();
                LiveAnalyser liveAnalyser = new LiveAnalyser(pFinder);
                Analyser ana = new Analyser(board, new Settings(), liveAnalyser);
                
                List<CorralArea> areas = liveAnalyser.getAreas(board);
                for(CorralArea a: areas)
                {
                    System.out.println(a);
                }
                
	//	Player noob = new Player(board);
		//System.out.println(noob.play());
	}
        
	
    public boolean isBadState(BoardState state, BoardPosition block)
    {
        return  
            is4x4Block(state, block)
            ||
            isFrozenDeadlockState(state, new HashSet<BoardPosition>(),block)
            ;
    }
    
    public boolean isBadState(BoardState board)
    {
    	for(BoardPosition block : board.getBlockNodes())
    		if(board.get(block) == NodeType.BLOCK && isBadState(board, block)) {
    			return true;
    		}
    	return false;
    }
    
    public boolean isFrozenDeadlockState(BoardState state, Set<BoardPosition> tmpBlock, BoardPosition block)
    {
        if(DeadlockCache == null)
            DeadlockCache = new HashMap<>();
        Map<BoardPosition, Boolean> map = DeadlockCache.get(state);
        if(map != null)
        {
            Boolean b = map.get(block);
            if(b != null)
                return b.booleanValue();
        }
        
        int r = block.Row;
        int c = block.Column;
        
        BoardPosition left = new BoardPosition(r, c-1);
        BoardPosition right = new BoardPosition(r, c+1);
        BoardPosition up = new BoardPosition(r-1, c);
        BoardPosition down = new BoardPosition(r+1, c);
        
        //***************//
        // WALL BLOCKING //
        //***************//
        
        // Check if there are any walls blocking horizontally
        // Or of course if there is any block already checked that is blocking
        boolean horizontalWallBlocking = (left.Column >= 0 && state.get(left) == NodeType.WALL)
			|| (right.Column < state.getColumnsCount() && state.get(right) == NodeType.WALL);
        
        
        
        
        // Check same as above just vertical
        boolean verticalWallBlocking = (up.Row >= 0 && state.get(up) == NodeType.WALL) 
                || (down.Row < state.getRowsCount() && state.get(down) == NodeType.WALL);
                
        
        // Can block be moved?
        // Note we have to iterate over all blocks to check one of them
        // is not a goal, since it could otherwise have been a good win state
        if(horizontalWallBlocking && verticalWallBlocking)
        {
            tmpBlock.add(block);
            for(BoardPosition pos: tmpBlock)
            {
                if(state.get(pos)==NodeType.BLOCK)
                    return true;
            }
            return false;
        }

        //*****************//
        // BLOCKS DEADLOCK //
        //**************** //
        
        
        // If blocking is both horizontal and vertical, well then the block is supposedly frozen
        // But is it deadlocked?

        // Mark this block as checked - avoid stackoverflow
        tmpBlock.add(block);
        boolean deadlockState = 
            (
                (verticalWallBlocking)
                ||
                (up.Row >= 0 && isBlockType(state.get(up)) && (tmpBlock.contains(up) || isFrozenDeadlockState(state, tmpBlock, up)))
                ||
                (down.Row < state.getRowsCount() && isBlockType(state.get(down)) && (tmpBlock.contains(down) || isFrozenDeadlockState(state, tmpBlock, down)))
            )
            &&
            (
                (horizontalWallBlocking)
                ||
                (left.Column >= 0 && isBlockType(state.get(left)) && (tmpBlock.contains(left) || isFrozenDeadlockState(state, tmpBlock, left)))
                ||
                (right.Column < state.getColumnsCount() && isBlockType(state.get(right)) && (tmpBlock.contains(right) || isFrozenDeadlockState(state, tmpBlock, right)))
            );
        
        map = DeadlockCache.get(state);
        if(map == null)
        {
            map = new HashMap<>();
            DeadlockCache.put(state, map);
        }
        map.put(block, deadlockState);
        
        return deadlockState;
    }

}
