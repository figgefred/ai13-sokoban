/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Tethik;

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
   	
    private Map<BoardState, Map<CorralArea, List<CorralArea>>> CachedAreas;
//    private Map<BoardState, Map<BoardPosition, Boolean>> DeadlockCache;
    private Settings settings;
        
	public LiveAnalyser(Settings settings)
	{
		this.settings = settings;
        if(settings.DO_CORRAL_CACHING)
            CachedAreas = new HashMap<>();         
//        if(Player.DO_EXPENSIVE_DEADLOCK)
//            DeadlockCache = new HashMap<>();
	}

    private List<CorralArea> getCachedArea(BoardState state)
    {
        if(!settings.DO_CORRAL_CACHING)
            return null;
        
        Map<CorralArea, List<CorralArea>> map = CachedAreas.get(state);
        if(map != null)
        {
            for(CorralArea key : map.keySet())
            {
                if(key.isMemberOfArea(state.getPlayerNode()))
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
        if(settings.DO_CORRAL_CACHING)
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
        
        if(playArea == null)
            System.err.println("Oooops, no playarea warning.");
        
        if(settings.DO_CORRAL_CACHING)
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
		                
		//System.out.println(board);
		BoardState board = new BoardState(b, true);
        PathFinder pFinder = new PathFinder();
        LiveAnalyser liveAnalyser = new LiveAnalyser(new Settings());
        board.setSettings(new Settings());
        Analyser ana = new Analyser(board, new Settings());
        
        List<CorralArea> areas = liveAnalyser.getAreas(board);
        for(CorralArea a: areas)
        {
            System.out.println(a);
        }
                
	//	Player noob = new Player(board);
		//System.out.println(noob.play());
	}
        
//	
//    public boolean isFrozenDeadlockState(BoardState state, Set<BoardPosition> tmpBlock, BoardPosition block)
//    {
//        if(DeadlockCache == null)
//            DeadlockCache = new HashMap<>();
//        Map<BoardPosition, Boolean> map = DeadlockCache.get(state);
//        if(map != null)
//        {
//            Boolean b = map.get(state);
//            if(b != null)
//                return b.booleanValue();
//        }
//        
//        int r = block.Row;
//        int c = block.Column;
//        
//        BoardPosition left = new BoardPosition(r, c-1);
//        BoardPosition right = new BoardPosition(r, c+1);
//        BoardPosition up = new BoardPosition(r-1, c);
//        BoardPosition down = new BoardPosition(r+1, c);
//        
//        //***************//
//        // WALL BLOCKING //
//        //***************//
//        
//        // Check if there are any walls blocking horizontally
//        // Or of course if there is any block already checked that is blocking
//        boolean horizontalWallBlocking = (left.Column >= 0 && state.get(left) == NodeType.WALL)
//			|| (right.Column < state.getColumnsCount() && state.get(right) == NodeType.WALL);
//        
//        horizontalWallBlocking = horizontalWallBlocking || 
//        		((left.Column >= 0 ) && 
//        				(right.Column < state.getColumnsCount() ));
//        
//        
//        
//        
//        // Check same as above just vertical
//        boolean verticalWallBlocking = (up.Row >= 0 && state.get(up) == NodeType.WALL) 
//                || (down.Row < state.getRowsCount() && state.get(down) == NodeType.WALL)   
//                || ((up.Row >= 0 ) 
//                && (down.Row < state.getRowsCount()));
//                
//        
//        // Can block be moved?
//        // Note we have to iterate over all blocks to check one of them
//        // is not a goal, since it could otherwise have been a good win state
//        if(horizontalWallBlocking && verticalWallBlocking)
//        {
//            tmpBlock.add(block);
//            for(BoardPosition pos: tmpBlock)
//            {
//                if(state.get(pos)==NodeType.BLOCK)
//                    return true;
//            }
//            return false;
//        }
//
//        //*****************//
//        // BLOCKS DEADLOCK //
//        //**************** //
//        
//        
//        // If blocking is both horizontal and vertical, well then the block is supposedly frozen
//        // But is it deadlocked?
//
//        // Mark this block as checked - avoid stackoverflow
//        tmpBlock.add(block);
//        boolean deadlockState = 
//            (
//                (verticalWallBlocking)
//                ||
//                (up.Row >= 0 && isBlockType(state.get(up)) && (tmpBlock.contains(up) || isFrozenDeadlockState(state, tmpBlock, up)))
//                ||
//                (down.Row < state.getRowsCount() && isBlockType(state.get(down)) && (tmpBlock.contains(down) || isFrozenDeadlockState(state, tmpBlock, down)))
//            )
//            &&
//            (
//                (horizontalWallBlocking)
//                ||
//                (left.Column >= 0 && isBlockType(state.get(left)) && (tmpBlock.contains(left) || isFrozenDeadlockState(state, tmpBlock, left)))
//                ||
//                (right.Column < state.getColumnsCount() && isBlockType(state.get(right)) && (tmpBlock.contains(right) || isFrozenDeadlockState(state, tmpBlock, right)))
//            );
//        
//        map = DeadlockCache.get(state);
//        if(map == null)
//        {
//            map = new HashMap<>();
//            DeadlockCache.put(state, map);
//        }
//        map.put(block, deadlockState);
//        
//        return deadlockState;
//    }

}
