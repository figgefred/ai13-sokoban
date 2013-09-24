/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import java.awt.print.Book;
import java.util.*;
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

	private ISearchAlgorithmPath playerPathSearch;
	private ISearchAlgorithmPath blockPathSearch;
	
	private BoardPosition CurrentPstart;
	
	public BFS_PushBlock(IExploreCondition blockPathFindingCond, IExploreCondition playerPathFindingCond) {
		this(blockPathFindingCond, playerPathFindingCond, null);
	}

	public BFS_PushBlock(IExploreCondition blockPathFindingCond, IExploreCondition playerPathFindingCond, ISearchAlgorithmPath tmp) {
		if (tmp == null)
			playerPathSearch = new BFS_Path(playerPathFindingCond);
		playerPathSearch = tmp;
		blockPathSearch = new BFS_Path(blockPathFindingCond);
	}
	
	@Override
	public Path getPath(BoardState state, BoardPosition initialPosition, BoardPosition destination) {
		
		if (!state.isBlock(initialPosition)) {
			throw new IllegalArgumentException(
					"Is not a pushable block on this node: " + initialPosition);
		}
		Path path = null;

		CurrentPstart = initialPosition;
		do {
			path = blockPathSearch.getPath(state, initialPosition, destination);
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
		
		if (!state.isBlock(pStart)) {
			throw new IllegalArgumentException(
					"Is not a pushable block on this node: " + pStart);
		}
		Path path = null;
		CurrentPstart = pStart;
		do {
			Path blockPath = blockPathSearch.getPath(state, pStart, destinations);
			System.out.println("Blockpath: " + blockPath);
			return blockPath;
			//break;
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
		
		//return path;
	}

	

	private boolean isSpaceNode(BoardState state, int pRow, int pCol) {
		// TODO Auto-generated method stub
		return !isNoneBlockingNode(state, new BoardPosition(pRow, pCol));
	}

	private boolean isNoneBlockingNode(BoardState state, BoardPosition p)
    {
        NodeType type = state.getNode(p);
        if(type == NodeType.INVALID)
            System.err.println("Referring to position " + p + " which refers to INVALID type");
        
        return 
               type != NodeType.INVALID &&
               type != NodeType.WALL &&
               type != NodeType.BLOCK &&
               type != NodeType.BLOCK_ON_GOAL;
    }
}
