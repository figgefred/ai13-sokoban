/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import java.util.Set;
import sokoban.Algorithms.ExploreAction.ExploreAction_BlockPath;
import sokoban.Algorithms.ExploreAction.IExploreAction;
import sokoban.Algorithms.ExploreConditions.ExploreCondition_BFS_BlockPath;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.AlgorithmType;
import static sokoban.types.AlgorithmType.A_STAR;
import static sokoban.types.AlgorithmType.BFS;
import static sokoban.types.AlgorithmType.GREEDY_BFS;
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
public class BlockPath implements ISearchAlgorithmPath {
    	private ISearchAlgorithmPath PlayerPathSearch;
	private BaseImpl BlockPathSearch;
        
        public BlockPath(AlgorithmType type, ISearchAlgorithmPath playerPath)
        {
            this(type, null, null, playerPath);
        }
        
	public BlockPath(AlgorithmType type, IExploreCondition cond, IExploreAction action, ISearchAlgorithmPath playerPath) {
	
            if(playerPath == null)
                throw new IllegalArgumentException("Player path searcher algorithm is null.");
            PlayerPathSearch = playerPath;
            
            switch(type)
            {
                case A_STAR:
                {
                    if(cond == null)
                        cond = new ExploreCondition_BFS_BlockPath();
                    if(action == null)
                        action = new ExploreAction_BlockPath();

                    BlockPathSearch = new AStar2_Impl(type, cond, action);
                    break;
                }
                case BFS:
                {
                    if(cond == null)
                        cond = new ExploreCondition_BFS_BlockPath();
                    if(action == null)
                        action = new ExploreAction_BlockPath();

                    BlockPathSearch = new BFS_BaseImpl(cond, action);
                    break;
                }
                case GREEDY_BFS:
                {
                    if(cond == null)
                        cond = new ExploreCondition_BFS_BlockPath();
                    if(action == null)
                        action = new ExploreAction_BlockPath();
                    BlockPathSearch = new AStar2_Impl(type, cond, action);
                    break;
                }
            }            
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
                    path = BlockPathSearch.getPath(newState, initialPosition, destination);
                    if (path == null || path.getPath() == null || path.getPath().size() == 0) // Ok cant find block path..
                        break;
                    System.out.println("DEBUG: Found path " + path);
                    path = getPlayerPushPath(path, newState);
                    if (path == null || path.getPath() == null || path.getPath().size() == 0) // YEA we found a path for the player to push block
                    {
                        System.out.println("DEBUG: Found path incl. player path " + path);
                    }
                    else {
                        newState.setNodeType(NodeType.WALL, path.get(0));
                    }
		} while (path == null);
		return path;
        }
        
        @Override
        public Path getPath(BoardState state, BoardPosition initialPosition, Set<BoardPosition> destination) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public AlgorithmType[] getAlgorithmType() {
            AlgorithmType[] types = new AlgorithmType[2];
            types[0] = BlockPathSearch.Type;
            types[1] = PlayerPathSearch.getAlgorithmType()[0];
            return types;
        }
        
        public Path getPlayerPushPath(Path blockPath, BoardState state){
            
            Path playerToBlockPath=new Path();
            Direction dir = null;

            for(int i = 0; i < blockPath.getPath().size(); i++){
                BoardPosition playerPos = state.getPlayerNode();
                Path playerToBlockSegment = new Path();
                int nextIndex=i+1;
                if(nextIndex==blockPath.getPath().size())
                {
                        break;
                }
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
                //playerToBlockPath.append(blockPath.last());
                playerToBlockPath.append(blockPath.getPath().get(blockPath.getPath().size()-2));
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
                newPlayerPos=new BoardPosition(currentBlockFragment.Row, currentBlockFragment.Column-1);
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
