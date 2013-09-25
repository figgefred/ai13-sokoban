/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms.ExploreConditions;

import sokoban.BoardPosition;
import sokoban.BoardState;


/**
 * These instances are used in combination with Base implementations of search algorithms.
 *
 * This defines a condition that has to be met in order for a child node to be explored.
 * 
 * @author figgefred
 */
public class ExploreCondition_BFS_FindPath implements IExploreCondition {
   
    @Override
    public boolean explore(BoardState state, BoardPosition from, BoardPosition to) {
        return state.isNoneBlockingNode(to);
    }

    
}
