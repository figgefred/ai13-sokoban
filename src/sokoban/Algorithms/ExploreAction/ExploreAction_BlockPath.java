/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms.ExploreAction;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.types.Direction;

/**
 *
 * @author figgefred
 */
public class ExploreAction_BlockPath implements IExploreAction {

    @Override
    public boolean doAction(BoardState state, BoardPosition from, BoardPosition to) {
        return state.moveBlockTo(from.Row, from.Column, state.getDirection(from, to));
    }
}
