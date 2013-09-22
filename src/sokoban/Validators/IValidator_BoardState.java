/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Validators;

import sokoban.BoardState;

/**
 *
 * @author figgefred
 */
public interface IValidator_BoardState {
    public boolean validate(BoardState state);
}
