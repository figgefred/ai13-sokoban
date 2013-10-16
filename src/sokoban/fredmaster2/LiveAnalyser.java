/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.fredmaster2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sokoban.BoardPosition;
import sokoban.NodeType;
import sokoban.Direction;

/**
 *
 * @author figgefred
 */
public class LiveAnalyser {
   	
	private Analyser analyser;
	private PathFinder pathfinder;
    
        public static boolean VERBOSE = true;
        
	public LiveAnalyser(Analyser analyser, PathFinder finder)
	{
		this.analyser = analyser;
		this.pathfinder = finder;
	}
	
    public boolean isBadState(BoardState state, BoardPosition block)
    {
        return  
            isQuadBlocking(state, block)
            ||
            isDeadlockState(state, new HashSet<BoardPosition>(),block)
            ;
    }
    
    public boolean isBadState(BoardState board)
    {
    	for(BoardPosition block : board.getBlockNodes())
    		if(board.getNode(block) == NodeType.BLOCK && isBadState(board, block)) {
    			return true;
    		}
    	
    	return false;
    }
    
    private boolean isDeadlockState(BoardState state, Set<BoardPosition> tmpBlock, BoardPosition block)
    {
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
        boolean horizontalWallBlocking = (left.Column >= 0 && state.getNode(left) == NodeType.WALL)
			|| (right.Column < state.getColumnsCount() && state.getNode(right) == NodeType.WALL);
        
        horizontalWallBlocking = horizontalWallBlocking || 
        		((left.Column >= 0 && analyser.isBadPosition(left)) && 
        				(right.Column < state.getColumnsCount() && analyser.isBadPosition(right)));
        
        
        
        
        // Check same as above just vertical
        boolean verticalWallBlocking = (up.Row >= 0 && state.getNode(up) == NodeType.WALL) 
                || (down.Row < state.getRowsCount() && state.getNode(down) == NodeType.WALL)   
                || ((up.Row >= 0 && analyser.isBadPosition(up)) 
                && (down.Row < state.getRowsCount() && analyser.isBadPosition(down)));
                
        
        // Can block be moved?
        // Note we have to iterate over all blocks to check one of them
        // is not a goal, since it could otherwise have been a good win state
        if(horizontalWallBlocking && verticalWallBlocking)
        {
            tmpBlock.add(block);
            for(BoardPosition pos: tmpBlock)
            {
                if(state.getNode(pos)==NodeType.BLOCK)
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
                (up.Row >= 0 && isBlockType(state.getNode(up)) && (tmpBlock.contains(up) || isDeadlockState(state, tmpBlock, up)))
                ||
                (down.Row < state.getRowsCount() && isBlockType(state.getNode(down)) && (tmpBlock.contains(down) || isDeadlockState(state, tmpBlock, down)))
            )
            &&
            (
                (horizontalWallBlocking)
                ||
                (left.Column >= 0 && isBlockType(state.getNode(left)) && (tmpBlock.contains(left) || isDeadlockState(state, tmpBlock, left)))
                ||
                (right.Column < state.getColumnsCount() && isBlockType(state.getNode(right)) && (tmpBlock.contains(right) || isDeadlockState(state, tmpBlock, right)))
            );
        return deadlockState;
    }
    
   private boolean isQuadBlocking(BoardState state, BoardPosition block) {
        
        int rmin = block.Row-1;
        int rmax = block.Row+1;
        int cmin = block.Column-1;
        int cmax = block.Column+1;
        
        NodeType[] top = new NodeType[3];
        NodeType[] middle = new NodeType[3];
        NodeType[] bottom = new NodeType[3];
        NodeType[][] segments = {top, middle, bottom};
        for(int r = rmin; r <= rmax; r++)
        {
            for(int c = cmin; c <= cmax; c++)    
            {
                if( (r >= 0 && r < state.getRowsCount()) && (c >= 0 && c < state.getColumnsCount()) )
                {
                    segments[r-rmin][c-cmin] = state.getNode(r, c);
                }
                else
                {
                    segments[r-rmin][c-cmin] = NodeType.INVALID;
                }
            }
        }
        boolean leftIsUnMovable = false;
        boolean rightIsUnMovable = false;
        
        leftIsUnMovable = 
                (
                    ( isBlockingType(segments[0][0]) && isBlockingType(segments[0][1]) )
                    &&
                    ( isBlockingType(segments[1][0]) && isBlockingType(segments[1][1]) )
                ) 
                ||
                (
                    ( isBlockingType(segments[1][0]) && isBlockingType(segments[1][1]) )
                    &&
                    ( isBlockingType(segments[2][0]) && isBlockingType(segments[2][1]) )
                );
        
        rightIsUnMovable = 
                (
                    ( isBlockingType(segments[0][0]) && isBlockingType(segments[0][1]) )
                    &&
                    ( isBlockingType(segments[1][0]) && isBlockingType(segments[1][1]) )
                ) 
                ||
                (
                    ( isBlockingType(segments[1][0]) && isBlockingType(segments[1][1]) )
                    &&
                    ( isBlockingType(segments[2][0]) && isBlockingType(segments[2][1]) )
                );
        
        
        return leftIsUnMovable || rightIsUnMovable;
    }
    
    private boolean isBlockType(NodeType type)
    {
        return type == NodeType.BLOCK || type == NodeType.BLOCK_ON_GOAL;
    }
   
    private boolean isBlockingType(NodeType type)
    {
        return isBlockType(type) || type == NodeType.WALL;
    }
    
    /**
     * Returns a list of corral areas and (or only) the area of where the
     * player is.
     * 
     * A corral area is an area which a player cannot reach except by pushing
     * a block.
     * 
     * Area's will hold a field pointing to a list of blocks that are acting
     * as fences (creating the corrol fence). These should be prioritized so that
     * a corral area can be solved as fast as possible. This is so called 'corrol pruning'.
     * 
     * @param board
     * @return 
     */
    public List<Area> getAreas(BoardState board)
    {
        // A list containing all the nodes "visited"
        Set<BoardPosition> visited = new HashSet<>();
        List<Area> list = new ArrayList<>();
        
        int areaCounter = 1;
        
        //**************************//
        // Iterpret areas from map //
        //*********************** //
        
        for(int r = 0; r < board.getRowsCount(); r++)
        {
            for(int c = 0; c < board.getColumnsCount(r); c++)
            {
                BoardPosition p = new BoardPosition(r,c);
                NodeType nodeType = board.getNode(p);
                
                if( !visited.contains(p) && nodeType.isSpaceNode() )
                {
                    Area area = new Area(areaCounter++, board);
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
        for(Area a: list)
        {
            for(BoardPosition p: board.getBlockNodes())
            {
                // For every a try to match every block
                if(board.getNode(p).isBlockNode())
                {
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
        for(CorralFenceCandidate f : fenceCandidates.values())
        {
            for(Area a: list)
            {
                if(f.isNodeOf(a))
                {
                    if(f.isPartOfCorralAreaFence())
                    {
                        a.addAsFenceNode(f.getBoardPosition(), board.getNode(f.getBoardPosition()));
                    }
                    else
                    {
                        a.add(f.getBoardPosition(), board.getNode(f.getBoardPosition()));
                    }
                }
            }
        }
        return list;
    }
    
    public Map<Area, List<BoardPosition>> getBlockAreas(BoardState board, List<Area> corrals, BoardPosition movedBlock)
    {
        boolean hasAllBoxesOnGoals = true;
        boolean isACombinedCorral = false;
        
        for(Area cArea: corrals)
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
    
    private void setCorralArea(BoardState board, Area area, BoardPosition spaceNode, Set<BoardPosition> visited)
    {
        NodeType nodeType = board.getNode(spaceNode);
        area.add(spaceNode, nodeType);
        visited.add(spaceNode);
        
        for(BoardPosition neighbour: board.getNeighbours(spaceNode))
        {
            NodeType neighbourType = board.getNode(neighbour);
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
    
    private void visitNeighbouringBlocks(BoardState board, Area area, BoardPosition blockNode, Set<BoardPosition> visited)
    {
        NodeType nodeType = board.getNode(blockNode);
        area.add(blockNode, nodeType);
        visited.add(blockNode);
        for(BoardPosition blockNeighbour: board.getNeighbours(blockNode))
        {
            NodeType neighbourType = board.getNode(blockNeighbour);
            if( !visited.contains(blockNeighbour) && ( neighbourType.isBlockNode()) )
            {
                // If unvisited expand!
                visitNeighbouringBlocks(board, area, blockNeighbour, visited);
            }
        }
    }
    
    public static void main(String args[]) throws IOException
    {
        
        String[] files = 
        {
            "testing/deadlocktest1",
            "testing/deadlocktest2",
            "testing/deadlocktest3",
            "testing/deadlocktest4",
            "testing/deadlocktest5",
            "testing/deadlocktest6",
            "testing/deadlocktest7",
            "testing/deadlocktest9",
            "testing/deadlocktest10",
        };
        for(String file: files)
        {
            BoardState board = BoardState.getBoardFromFile(file);
            BoardPosition p = null;
            for(int r = 0; r < board.getRowsCount(); r++)
            {
                p = null;
                for(int c = 0; c < board.getColumnsCount(); c++)
                {
                    if(board.getNode(r, c) == NodeType.BLOCK_ON_GOAL)
                    {
                        p = new BoardPosition(r,c);
                        break;
                    }
                }
                if( p != null)
                    break;
            }
            if(p == null)
                System.out.println("ERROR! Expected a BLOCK_ON_GOAL ('*') node to investigate.");
            
            Analyser bestanalyser = new Analyser(board);
            PathFinder finder = new PathFinder();
            LiveAnalyser analyser = new LiveAnalyser(bestanalyser, finder);
            System.out.println(board);
            Set<BoardPosition> visitedBlock = new HashSet<BoardPosition>();
            System.out.println("Analyser verdict: State deadlock? - " + analyser.isDeadlockState(board, visitedBlock, p));
            System.out.println("Visited: " + visitedBlock);
        }
    }
}
