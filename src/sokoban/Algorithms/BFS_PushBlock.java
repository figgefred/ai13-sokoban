/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import java.awt.print.Book;
import java.util.*;
import sokoban.Algorithms.ExploreAction.ExploreAction_BlockPath;
import sokoban.Algorithms.ExploreAction.ExploreAction_Path;
import sokoban.Algorithms.ExploreAction.IExploreAction;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.Algorithms.ExploreConditions.BoardEvaluations;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BlockPath;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_FindPath;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;
import sokoban.types.Direction;
import sokoban.types.NodeType;

/**
 * 
 * @author figgefred
 */
public class BFS_PushBlock implements ISearchAlgorithmPath {

	private ISearchAlgorithmPath PlayerPathSearch;
	private BFS_BaseImpl BlockPathSearch;

        public BFS_PushBlock(ISearchAlgorithmPath playerPathSearch) {
            this(playerPathSearch, new ExploreCondition_BlockPath(), new ExploreAction_BlockPath());
        }
        
        public BFS_PushBlock(ISearchAlgorithmPath playerPathSearch, IExploreCondition blockPathCond) {
            this(playerPathSearch, blockPathCond, new ExploreAction_BlockPath());
        }
        
	public BFS_PushBlock(ISearchAlgorithmPath playerPathSearch, IExploreCondition blockPathCond, IExploreAction action) {
	
            if(playerPathSearch == null)
                throw new IllegalArgumentException("Player path searcher algorithm is null.");
            if(blockPathCond == null)
                throw new IllegalArgumentException("Condition of block path searcher is null.");
            
            PlayerPathSearch = playerPathSearch;
            BlockPathSearch = new BFS_BaseImpl(action, blockPathCond);
	}
	
	@Override
	public Path getPath(BoardState state, BoardPosition initialPosition, BoardPosition destination) {
		
		if (!state.isBlock(initialPosition)) {
			throw new IllegalArgumentException(
					"Is not a pushable block on this node: " + initialPosition);
		}
		Path path = null;
		do {
			path = BlockPathSearch.getPath(state, initialPosition, destination);
			break;
			/*
			if (blockPath == null) // Ok cant find block path..
				break;
			System.out.println("DEBUG: Found path " + blockPath);
			path = getPlayerPushPath(blockPath, state);
			if (path != null) // YEA we found a path for the player to push
								// block
			{
				System.out.println("DEBUG: Found path incl. player path "
						+ path);
				break;
			} else {
				visited = initVisitedMatrix(state);
				int iEnd = blockPath.getPath().size() - 1;
				for (int i = 1; i < iEnd; i++) {
					visited[blockPath.get(i).Row][blockPath.get(i).Column] = true;
				}
			}
			*/
		} while (true);
		
		return path;
	}

	@Override
	public Path getPath(BoardState state, BoardPosition pStart, Set<BoardPosition> destinations) {
            throw new UnsupportedOperationException("Not yet supported!");	
        }
        


/*	private boolean isNoneBlockingNode(BoardState state, BoardPosition p)
    {
        NodeType type = state.getNode(p);
        if(type == NodeType.INVALID)
            System.err.println("Referring to position " + p + " which refers to INVALID type");
        
        return 
               type != NodeType.INVALID &&
               type != NodeType.WALL &&
               type != NodeType.BLOCK &&
               type != NodeType.BLOCK_ON_GOAL;
    }*/
}
