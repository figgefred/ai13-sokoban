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
import sokoban.Algorithms.ExploreAction.IExploreAction;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.Algorithms.ExploreConditions.IExploreCondition;

/**
 *
 * @author figgefred
 */
public class BFS_BaseImpl {
    
    private IExploreCondition Cond;
    private IExploreAction Action;

    public BFS_BaseImpl(IExploreAction action, IExploreCondition cond)
    {
        Cond = cond;
        Action = action;
    }
	
    public Path getPath(BoardState state, BoardPosition pStart, BoardPosition destination) {
        if(pStart == null || destination == null)
        {
            return null;
        }
        Map<BoardPosition, BoardPosition> path = new HashMap<BoardPosition, BoardPosition>();
        
        // BFS to find all sub paths
        BoardPosition goalReached = doBfs(path, new LinkedList<BoardPosition>(), state, pStart,  destination);
        
        if(goalReached == null)
        {
            return new Path(new ArrayList<BoardPosition>(), false);
        }
        
        // Evaluate the path from goal to start node
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

    private BoardPosition doBfs(Map<BoardPosition, BoardPosition> path, Queue<BoardPosition> queue, BoardState state, BoardPosition pStart, BoardPosition destination)
    {
        HashMap<BoardPosition, BoardState> oldStates = new HashMap<BoardPosition, BoardState>();
        Set<Integer> repeatedStates = new HashSet<Integer>();
        BoardPosition goalReached = null;
        
        oldStates.put(pStart, state);
        repeatedStates.add(state.hashCode());
        queue.add(pStart);
        
      //  System.out.println("CURRENT STATE: ");
       // System.out.println(state);
        
        while(!queue.isEmpty() )
        {
            BoardPosition p = queue.poll();
            BoardState s = oldStates.get(p);
        
            //System.out.println("VIEWED STATE: ");
            //System.out.println(s);
            
            int previousHash = s.hashCode();
            if(p.equals(destination))
            {
                goalReached = p;
                break;
            }
            List<BoardPosition> neighbours = s.getNeighbours(p);
            for(BoardPosition p2: neighbours)
            {
                BoardState newState = (BoardState)s.clone();
                if(Cond.explore(s, p, p2))
                {
                    boolean successfulAction = Action.doAction(newState, p, p2);
                    boolean repeatedState = repeatedStates.contains(newState.hashCode());
                    if(successfulAction && !repeatedState)
                    {
                        repeatedStates.add(newState.hashCode());
                        oldStates.put(p2, newState);
                        queue.add(p2);
                        path.put(p2, p);
                    }
                }
            }
        }
        return goalReached;
    }
    
}
