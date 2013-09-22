/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Node;
import sokoban.types.Direction;
import sokoban.types.NodeType;

/**
 *
 * @author figgefred
 */
public class BFS_PushBlock implements ISearchPushAlgorithm {

    @Override
    public BoardState pushBlock(BoardState state, BoardPosition blockPos) {
        Node node = state.getNode(blockPos);
        if(isBlock(node))
        {
            return null;
        }
        
        // Check surroundings, if block is at all pushable
        boolean sidewaysPushable = false;
        boolean verticalPushable = false;
        Node nLeft = node.getNeighbour(Direction.LEFT);
        Node nRight = node.getNeighbour(Direction.RIGHT);
        Node nUp = node.getNeighbour(Direction.UP);
        Node nDown = node.getNeighbour(Direction.DOWN);
        if(isNoWall(nLeft) && isNoWall(nRight))
            sidewaysPushable = true;
        if(isNoWall(nUp) && isNoWall(nDown))
            verticalPushable = true;
        
        if( !sidewaysPushable && !verticalPushable)
            return null;
        
        // Now lets push!
        
        // PUsh algorithm or something
        // Single push and check Boardstate?
        // Push whole way?
        
        
        
        return null;
    }
    
    private boolean isBlock(Node n)
    {
        return n.getNodeType() != NodeType.BLOCK || n.getNodeType() != NodeType.BLOCK_ON_GOAL;
    }
    
    private boolean isNoWall(Node n)
    {
        return n.getNodeType() == NodeType.SPACE ||
                n.getNodeType() == NodeType.PLAYER || 
                n.getNodeType() == NodeType.PLAYER_ON_GOAL;
    }
    
}
