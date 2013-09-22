/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Constants;
import sokoban.Path;
import sokoban.types.NodeType;

/**
 *
 * @author figgefred
 */
public class BFS_Path implements ISearchAlgorithmPath {
    
    @Override
    public Path getPath(BoardState state, BoardPosition initialNode, BoardPosition destination) {
        Set<BoardPosition> position = new HashSet<>();
        position.add(destination);
        return getPath(state, initialNode, position);
    }
    
    @Override
    public Path getPath(BoardState state, BoardPosition initialNode, Set<BoardPosition> destinations) {
        if(initialNode == null || destinations.isEmpty())
        {
            return null;
        }
        Map<BoardPosition, BoardPosition> path = new HashMap<BoardPosition, BoardPosition>();
        Queue<BoardPosition> queue = new LinkedList<BoardPosition>();

        Map<BoardPosition, Boolean> Visited = new HashMap<BoardPosition, Boolean>();
        
        Boolean visited = Visited.get(initialNode);
        
        if(visited == null || !visited.booleanValue())
        {
            Visited.put(initialNode, true);
        }
        queue.add(initialNode);

        BoardPosition goalReached = null;
        
        while(!queue.isEmpty())
        {
            BoardPosition p = queue.poll();
            if(destinations.contains(p))
            {
                goalReached = p;
                continue;
            }

            BoardPosition[] neighbours = null;
            
            //TODO find neighbours
           
            
            
            for(BoardPosition p2: neighbours)
            {
                // SOMETHING
                visited = Visited.get(p2);
                if( visited == null  || !visited.booleanValue())
                {
                    Visited.put(p2, true);
                    if(isNoneBlockingNode(p2))
                    {
                        queue.add(p2);
                        path.put(p2, p);
                    }
                }
            }
        }
        if(goalReached == null)
        {
            return null;
        }

        List<BoardPosition> nodes = new ArrayList<>();
        BoardPosition p1 = goalReached;
        while(p1 != null)
        {
            nodes.add(p1);
            BoardPosition p2 = path.get(p1);
            if(p2 == null)
                break;

            //nodes.add(n2);
            p1 = p2;
        }
        return new Path(nodes);
    }
    
    private boolean isNoneBlockingNode(BoardPosition n)
    {
        // TODO 
        
        return true;
        /*return n.getNodeType() != NodeType.WALL &&
                n.getNodeType() != NodeType.BLOCK &&
                n.getNodeType() != NodeType.BLOCK_ON_GOAL;*/
    }
    
}
