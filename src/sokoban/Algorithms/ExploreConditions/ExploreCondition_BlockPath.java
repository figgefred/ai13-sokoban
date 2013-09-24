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
public class ExploreCondition_BlockPath implements IExploreCondition {
    
    private BoardState State;
    private BoardPosition SpecialNode;
    
    public ExploreCondition_BlockPath(BoardState state)
    {
        State = state;
    }
    
    @Override
    public boolean explore(BoardState state, BoardPosition from, BoardPosition to) {
        return ( State.isNoneBlockingNode(to) && BoardEvaluations.isSpaceToMove(state, from, to, SpecialNode) );
    }

	@Override
	public void setSpecialNode(BoardPosition special) {
		SpecialNode = special;
		
	}
    
}
