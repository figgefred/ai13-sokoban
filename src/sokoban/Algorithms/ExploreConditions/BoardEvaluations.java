/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms.ExploreConditions;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.types.Direction;

/**
 *
 * @author figgefred
 */
public class BoardEvaluations {
    
    public static boolean isSpaceToMove(BoardState state, BoardPosition from, BoardPosition to)
    {
        Direction dir = state.getDirection(from, to);
        boolean hasSpaceArea = false;
        if(dir == Direction.UP)
        {
            int pRow = from.Row+1;
            int pCol = from.Column;
            
            hasSpaceArea = (pRow < state.getRowsCount() &&
                    state.isSpaceNode(pRow, pCol));
            
            return hasSpaceArea;
        }
        else if(dir == Direction.DOWN)
        {
            int pRow = from.Row-1;
            int pCol = from.Column;
            hasSpaceArea = (pRow >= 0 &&
                    state.isSpaceNode(pRow, pCol));
            
            return hasSpaceArea;
        }
        else if(dir == Direction.LEFT)
        {
            int pRow = from.Row;
            int pCol = from.Column+1;
            hasSpaceArea = (pCol < state.getColumnsCount(pRow) &&
                    state.isSpaceNode(pRow, pCol));
            
            return hasSpaceArea;
        }
        else if(dir == Direction.RIGHT)
        {
            int pRow = from.Row;
            int pCol = from.Column-1;
            hasSpaceArea = (pCol >= 0 &&
                    state.isSpaceNode(pRow, pCol));
            return hasSpaceArea;
        }
        
        // ELSE WE REACH INVALID, UNEXPECTED!!!
        else
        {
            throw new IllegalArgumentException("isSpaceToMove(): Evaluated INVALID type, even though this should not be evaluated! UNEXPECTED!!!");
        }
    }
    
}
