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
public interface IExploreCondition {
    
	public void setSpecialNode(BoardPosition special);
    public boolean explore(BoardState state, BoardPosition from, BoardPosition to);
    
}
