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
public interface IExploreAction {
    
    boolean doAction(BoardState state, BoardPosition from, BoardPosition to);
    
}
