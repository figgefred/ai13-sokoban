/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import java.util.Set;
import sokoban.Algorithms.ExploreAction.IExploreAction;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.AlgorithmType;

/**
 *
 * @author figgefred
 */
public abstract class BaseImpl  {
    
    protected IExploreCondition Cond;
    protected IExploreAction Action;
    protected AlgorithmType Type;
    
    public abstract Path getPath(BoardState state, BoardPosition initialPosition, BoardPosition destination);
    public abstract Path getPath(BoardState state, BoardPosition initialPosition, Set<BoardPosition> destinations);
    
    public AlgorithmType getAlgorithmType()
    {
        return Type;
    }
    
}
