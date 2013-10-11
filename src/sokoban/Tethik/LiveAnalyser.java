/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Tethik;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import sokoban.BoardPosition;
import sokoban.NodeType;

/**
 *
 * @author figgefred
 */
public class LiveAnalyser {
   	
    
	public LiveAnalyser()
	{}
	
        public boolean isBadState(BoardState state, BoardPosition block)
        {
            return  
                    isQuadBlocking(state, block)
                    ||
                    isDeadlockState(state, new HashSet<BoardPosition>(),block)
                    ;
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
            boolean horizontalWallBlocking = 
                    (left.Column >= 0 && state.getNode(left) == NodeType.WALL) 
                    ||
                    (right.Column < state.getColumnsCount() && state.getNode(right) == NodeType.WALL)
                    ;
            
            // Check same as above just vertical
            boolean verticalWallBlocking = 
                    (up.Row >= 0 && state.getNode(up) == NodeType.WALL) 
                    ||
                    (down.Row < state.getRowsCount() && state.getNode(down) == NodeType.WALL)   
                    ;
            
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
                
                LiveAnalyser analyser = new LiveAnalyser();
                System.out.println(board);
                Set<BoardPosition> visitedBlock = new HashSet<BoardPosition>();
		System.out.println("Analyser verdict: State deadlock? - " + analyser.isDeadlockState(board, visitedBlock, p));
                System.out.println("Visited: " + visitedBlock);
            }
        }
}
