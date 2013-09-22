/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import sokoban.BoardPosition;
import sokoban.Constants;
import sokoban.Node;
import sokoban.Path;
import sokoban.types.NodeType;

/**
 *
 * @author figgefred
 */
public class BFS_Path implements ISearchAlgorithmPath {
    
    @Override
    public Path getPath(Node initialNode, Set<Node> destinations) {
        if(initialNode == null || destinations.isEmpty())
        {
            return null;
        }
        
        Map<Node, Node> path = new HashMap<Node, Node>();
        Queue<Node> queue = new LinkedList<Node>();

        Map<BoardPosition, Boolean> Visited = new HashMap<BoardPosition, Boolean>();
        
        Boolean visited = Visited.get(initialNode);
        
        if(visited == null || !visited.booleanValue())
        {
            Visited.put(initialNode.Position, true);
        }
        queue.add(initialNode);

        Node goalReached = null;
        
        while(!queue.isEmpty())
        {
            Node n = queue.poll();
            if(destinations.contains(n))
            {
                goalReached = n;
                continue;
            }

            Node[] neighbours = n.getNeighbours();
            for(Node n2: neighbours)
            {
                // SOMETHING
                visited = Visited.get(n2.Position);
                if( visited == null  || !visited.booleanValue())
                {
                    Visited.put(n2.Position, true);
                    if(isNoneBlockingNode(n2))
                    {
                        queue.add(n2);
                        path.put(n2, n);
                    }
                }
            }
        }
        if(goalReached == null)
        {
            return null;
        }

        List<Node> nodes = new ArrayList<>();
        Node n1 = goalReached;
        while(n1 != null)
        {
            nodes.add(n1);
            Node n2 = path.get(n1);
            if(n2 == null)
                break;

            //nodes.add(n2);
            n1 = n2;
        }
        return new Path(nodes);
    }
    
    private boolean isNoneBlockingNode(Node n)
    {
        return n.getNodeType() != NodeType.WALL &&
                n.getNodeType() != NodeType.BLOCK &&
                n.getNodeType() != NodeType.BLOCK_ON_GOAL;
    }
    
}
