/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms.ExploreConditions;

import sokoban.BoardPosition;
import sokoban.BoardState;

/**
 *
 * @author figgefred
 */
public class ExploreCondition_BFS_FindPath implements IExploreCondition {
   
    @Override
    public boolean explore(BoardState state, BoardPosition from, BoardPosition to) {
        return state.isNoneBlockingNode(to);
    }

    
}