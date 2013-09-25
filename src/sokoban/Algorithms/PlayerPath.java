/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import java.util.Set;
import sokoban.Algorithms.ExploreAction.ExploreAction_Path;
import sokoban.Algorithms.ExploreAction.IExploreAction;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BFS_FindPath;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.AlgorithmType;

/**
 *
 * @author figgefred
 */
public class PlayerPath implements ISearchAlgorithmPath {
 
    private BaseImpl playerSearcher;
    
    public PlayerPath(AlgorithmType type) {       
        this(type, null, null);
    }
    
    public PlayerPath(AlgorithmType type, IExploreCondition cond, IExploreAction action) {
        switch(type)
        {
            case A_STAR:
            {
                if(cond == null)
                    cond = new ExploreCondition_BFS_FindPath();
                if(action == null)
                    action = new ExploreAction_Path();
                
                playerSearcher = new AStar2_Impl(type, cond, action);
                break;
            }
            case BFS:
            {
                if(cond == null)
                    cond = new ExploreCondition_BFS_FindPath();
                if(action == null)
                    action = new ExploreAction_Path();
                
                playerSearcher = new BFS_BaseImpl(cond, action);
                break;
            }
            case GREEDY_BFS:
            {
                if(cond == null)
                    cond = new ExploreCondition_BFS_FindPath();
                if(action == null)
                    action = new ExploreAction_Path();
                playerSearcher = new AStar2_Impl(type, cond, action);
                break;
            }
        }
    }
    
    @Override
    public Path getPath(BoardState state, BoardPosition initialPosition, BoardPosition destination) {
        return playerSearcher.getPath(state, initialPosition, destination);
    }

    @Override
    public Path getPath(BoardState state, BoardPosition initialPosition, Set<BoardPosition> destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    } 

    @Override
    public AlgorithmType[] getAlgorithmType() {
        AlgorithmType[] types = {playerSearcher.getAlgorithmType()};
        return types;
    }
    
}
