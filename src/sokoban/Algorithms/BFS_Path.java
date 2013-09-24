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
    public Path getPath(BoardState state, BoardPosition pStart, BoardPosition destination) {
        if(pStart == null || destination == null)
        {
            return null;
        }
        Map<BoardPosition, BoardPosition> path = new HashMap<BoardPosition, BoardPosition>();
        Queue<BoardPosition> queue = new LinkedList<BoardPosition>();
        
        boolean[][] visited = new boolean[state.getRowsCount()][];
        for(int i = 0; i < visited.length; i++)
        {
            int size = state.getColumnsCount(i);
            /*if(size < 0)
                size = 0;*/
            visited[i] = new boolean[size];
        }
        
        //System.out.println("Test: pos= " + pStart);
        //System.out.println("Test: coordinates pointing at= " + state.getNode(pStart));
        //System.out.println("Test: visited has= " + visited[pStart.Row].length + " columns");
        //System.out.println("Test: map has= " + state.getColumnsCount(pStart.Row) + " columns");
        
        if( !visited[pStart.Row][pStart.Column])
        {
            visited[pStart.Row][pStart.Column] = true;
        }
        
        queue.add(pStart);
        BoardPosition goalReached = null;
        
        while(!queue.isEmpty())
        {
            BoardPosition p = queue.poll();
            if(p.equals(destination))
            {
                goalReached = p;
                break;
//                continue;
            }

            List<BoardPosition> neighbours = state.getNeighbours(p);
            for(BoardPosition p2: neighbours)
            {
                // SOMETHING
                if( !visited[p2.Row][p2.Column] )
                {
                    visited[p2.Row][p2.Column] = true;
                    if(isNoneBlockingNode(state, p2))
                    {
                        queue.add(p2);
                        // p2 came from p
                        path.put(p2, p);
                    }
                }
            }
        }
        if(goalReached == null)
        {
            return new Path(null, false);
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
        boolean reversedList = true;
        return new Path(nodes, reversedList);
    }
    
    @Override
    public Path getPath(BoardState state, BoardPosition pStart, Set<BoardPosition> destinations) {
        if(pStart == null || destinations.isEmpty())
        {
            return null;
        }
        Map<BoardPosition, BoardPosition> path = new HashMap<BoardPosition, BoardPosition>();
        Queue<BoardPosition> queue = new LinkedList<BoardPosition>();
        
        boolean[][] visited = new boolean[state.getRowsCount()][];
        for(int i = 0; i < visited.length; i++)
        {
            int size = state.getColumnsCount(i);
            /*if(size < 0)
                size = 0;*/
            visited[i] = new boolean[size];
        }
        
        //System.out.println("Test: pos= " + pStart);
        //System.out.println("Test: coordinates pointing at= " + state.getNode(pStart));
        //System.out.println("Test: visited has= " + visited[pStart.Row].length + " columns");
        //System.out.println("Test: map has= " + state.getColumnsCount(pStart.Row) + " columns");
        
        if( !visited[pStart.Row][pStart.Column])
        {
            visited[pStart.Row][pStart.Column] = true;
        }
        
        queue.add(pStart);
        BoardPosition goalReached = null;
        
        while(!queue.isEmpty())
        {
            BoardPosition p = queue.poll();
            if(destinations.contains(p))
            {
                goalReached = p;
                break;
//                continue;
            }

            List<BoardPosition> neighbours = state.getNeighbours(p);
            for(BoardPosition p2: neighbours)
            {
                // SOMETHING
                if( !visited[p2.Row][p2.Column] )
                {
                    visited[p2.Row][p2.Column] = true;
                    if(isNoneBlockingNode(state, p2))
                    {
                        queue.add(p2);
                        // p2 came from p
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
        boolean reversedList = true;
        return new Path(nodes, reversedList);
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
