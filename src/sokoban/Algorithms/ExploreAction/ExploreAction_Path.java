/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms.ExploreAction;

import sokoban.BoardPosition;
import sokoban.BoardState;

/**
 *
 * @author figgefred
 */
public class ExploreAction_Path implements IExploreAction {
    
    @Override
    public boolean doAction(BoardState state, BoardPosition from, BoardPosition to) {
        try {
            state.movePlayerTo(to);
        } catch(IllegalArgumentException ex)
        {
            return false;
        }
        return true;
    }
    
    
    
    
    
}
