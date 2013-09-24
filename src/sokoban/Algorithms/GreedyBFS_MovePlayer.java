/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import deprecated.AStar2_MovePlayer;
import sokoban.Algorithms.ExploreAction.ExploreAction_Path;
import sokoban.Algorithms.ExploreAction.IExploreAction;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BFS_FindPath;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;
import sokoban.types.AlgorithmType;

/**
 *
 * @author figgefred
 */
public class GreedyBFS_MovePlayer extends AStar2_MovePlayer {
    
    public GreedyBFS_MovePlayer(IExploreCondition cond, IExploreAction action) {
        super(AlgorithmType.A_STAR, cond, action);
    }
    
}
