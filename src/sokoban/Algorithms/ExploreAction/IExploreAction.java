/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms.ExploreAction;

import sokoban.BoardPosition;
import sokoban.BoardState;

/**
 * These instances are used in combination with Base implementations of search algorithms.
 *
 * This defines an action carried out that has to be successful in order to explore
 * a certain node.
 * 
 * @author figgefred
 */
public interface IExploreAction {
    
    /**
     * Defines an action to be carried out and returning true if successful.
     * @param state
     * @param from
     * @param to
     * @return 
     */
    boolean doAction(BoardState state, BoardPosition from, BoardPosition to);
    
}
