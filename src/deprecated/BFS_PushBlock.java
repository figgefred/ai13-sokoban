/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package deprecated;

import java.awt.print.Book;
import java.util.*;
import sokoban.Algorithms.BFS_BaseImpl;
import sokoban.Algorithms.ExploreAction.ExploreAction_BlockPath;
import sokoban.Algorithms.ExploreAction.ExploreAction_Path;
import sokoban.Algorithms.ExploreAction.IExploreAction;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.Algorithms.ExploreConditions.BoardEvaluations;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BFS_BlockPath;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BFS_FindPath;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;
import sokoban.Algorithms.ISearchAlgorithmPath;
import sokoban.types.Direction;
import static sokoban.types.Direction.DOWN;
import static sokoban.types.Direction.LEFT;
import static sokoban.types.Direction.RIGHT;
import static sokoban.types.Direction.UP;
import sokoban.types.NodeType;

/**
 * 
 * @author figgefred
 */
public class BFS_PushBlock implements ISearchAlgorithmPath {

	private ISearchAlgorithmPath PlayerPathSearch;
	private BFS_BaseImpl BlockPathSearch;

        public BFS_PushBlock(ISearchAlgorithmPath playerPathSearch) {
            this(playerPathSearch, new ExploreCondition_BFS_BlockPath(), new ExploreAction_BlockPath());
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
                BoardState newState = (BoardState) state.clone();
		do {
                    Path blockPath = BlockPathSearch.getPath(state, initialPosition, destination);
                    if (blockPath == null || blockPath.getPath() == null || blockPath.getPath().size() == 0) // Ok cant find block path..
                        break;
                    System.out.println("DEBUG: Found path " + blockPath);
                    path = getPlayerPushPath(blockPath, state);
                    if (path != null) // YEA we found a path for the player to push block
                    {
                        System.out.println("DEBUG: Found path incl. player path " + path);
                    }
                    else {
                        newState.setNodeType(NodeType.WALL, blockPath.get(0));
                    }
		} while (path == null);
		return path;
	}

	@Override
	public Path getPath(BoardState state, BoardPosition pStart, Set<BoardPosition> destinations) {
            throw new UnsupportedOperationException("Not yet supported!");	
        }
        
        public Path getPlayerPushPath(Path blockPath, BoardState state){
            
            Path playerToBlockPath=new Path();
            Direction dir = null;

            for(int i = 0; i < blockPath.getPath().size(); i++){
                BoardPosition playerPos = state.getPlayerNode();
                Path playerToBlockSegment = new Path();
                int nextIndex=i+1;
                if(nextIndex==blockPath.getPath().size())
                        break;
                Direction nextDir = state.getDirection(blockPath.get(i), blockPath.get(nextIndex));
                if(dir!=nextDir){ //If a change in direction
                        BoardPosition playerNewPos=null;
                        if(i!=0){ 
                                playerNewPos=blockPath.get(i-1);

                                Path straight = PlayerPathSearch.getPath(state, playerPos, playerNewPos);
                                playerToBlockPath=playerToBlockPath.cloneAndAppend(straight);

                        //	state.movePlayer(playerToBlockSegment);
                        //	playerToBlockPath=playerToBlockPath.cloneAndAppend(playerToBlockSegment);
                        //	System.out.println(playerToBlockPath);
                        //	System.out.println("append segment: "+playerToBlockPath);

                                playerToBlockSegment=new Path();
                                playerPos = state.getPlayerNode();
                        }
                        playerToBlockSegment = getPlayerPath(nextDir, state, blockPath.get(i), playerPos);
                }
                dir=nextDir;

                if(playerToBlockSegment!=null){
                        state.movePlayer(playerToBlockSegment);
                        playerToBlockPath=playerToBlockPath.cloneAndAppend(playerToBlockSegment);
                }	
                playerToBlockPath.append(blockPath.last());
            }

        return playerToBlockPath;
    }
        
        private Path getPlayerPath(Direction dir, BoardState state, BoardPosition currentBlockFragment, BoardPosition playerPosition) {

            BoardPosition newPlayerPos = null;
            Path playerToBlockSegment = null;

            switch(dir){
                case UP:
                {
                    newPlayerPos=new BoardPosition(currentBlockFragment.Row+1, currentBlockFragment.Column);
                    playerToBlockSegment = PlayerPathSearch.getPath(state, playerPosition, newPlayerPos);
                    break;
                }
                case DOWN:
                {
                    newPlayerPos=new BoardPosition(currentBlockFragment.Row-1, currentBlockFragment.Column);
                    playerToBlockSegment = PlayerPathSearch.getPath(state, playerPosition, newPlayerPos);

                    break;
                }
                case LEFT:
                {
                    newPlayerPos=new BoardPosition(currentBlockFragment.Row, (currentBlockFragment.Column)+1);
                    playerToBlockSegment = PlayerPathSearch.getPath(state, playerPosition, newPlayerPos);
                    break;
                }
                case RIGHT:
                {
                    newPlayerPos=new BoardPosition(currentBlockFragment.Row-1, currentBlockFragment.Column-1);
                    playerToBlockSegment = PlayerPathSearch.getPath(state, playerPosition, newPlayerPos);
                    break;
                }default:
                {
                    System.out.println("Ingen match");
                }
            }
            return playerToBlockSegment;
        }
}
