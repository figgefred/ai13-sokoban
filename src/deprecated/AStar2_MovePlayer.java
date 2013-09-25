/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package deprecated;

import java.util.Set;
import sokoban.Algorithms.AStar2_Impl;
import sokoban.Algorithms.ExploreAction.ExploreAction_Path;
import sokoban.Algorithms.ExploreAction.IExploreAction;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BFS_FindPath;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;
import sokoban.Algorithms.ISearchAlgorithmPath;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.AlgorithmType;

/**
 *
 * @author figgefred
 */
public class AStar2_MovePlayer implements ISearchAlgorithmPath{

    private AStar2_Impl playerSearcher;
    
    public AStar2_MovePlayer() {
        this(new ExploreCondition_BFS_FindPath());
    }
    
    public AStar2_MovePlayer(IExploreCondition cond) {
        this(AlgorithmType.A_STAR, cond, new ExploreAction_Path());
    }
    
    protected AStar2_MovePlayer(AlgorithmType type, IExploreCondition cond, IExploreAction action)
    {
        if(cond == null)
            throw new IllegalArgumentException("Condition is null.");
        if(action == null)
            throw new IllegalArgumentException("Action is null.");
        playerSearcher = new AStar2_Impl(type, cond, action);        
    }

    @Override
    public Path getPath(BoardState state, BoardPosition initialPosition, BoardPosition destination) {
        return playerSearcher.getPath(state, initialPosition, destination);
    }

    @Override
    public Path getPath(BoardState state, BoardPosition initialPosition, Set<BoardPosition> destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    } 
}
