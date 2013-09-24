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
public class ExploreCondition_FindPath implements IExploreCondition {

    private BoardState State;
    
    public ExploreCondition_FindPath(BoardState state)
    {
        State = state;
    }
    
    @Override
    public boolean explore(BoardState state, BoardPosition from, BoardPosition to) {
        return State.isNoneBlockingNode(to);
    }

    
}
