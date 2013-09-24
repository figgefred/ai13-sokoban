/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package deprecated;

import java.util.Set;
import sokoban.Algorithms.BFS_BaseImpl;
import sokoban.Algorithms.ExploreAction.ExploreAction_Path;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BFS_FindPath;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;
import sokoban.Algorithms.ISearchAlgorithmPath;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;

/**
 *
 * @author figgefred
 */
public class BFS_MovePlayer implements ISearchAlgorithmPath {

    private BFS_BaseImpl playerSearcher;
    
    public BFS_MovePlayer() {
        this(new ExploreCondition_BFS_FindPath());
    }
    
    public BFS_MovePlayer(IExploreCondition cond, IExploreAction action) {
        if(cond == null)
            throw new IllegalArgumentException("Condition is null.");
        playerSearcher = new BFS_BaseImpl(new ExploreAction_Path(), cond);
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
