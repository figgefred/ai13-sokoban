/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import java.util.HashSet;
import java.util.Set;
import sokoban.Algorithms.ISearchAlgorithmPath;
import sokoban.types.NodeType;

/**
 *
 * @author figgefred
 */
public class Player {
    
    // Vanlig push
    private ISearchAlgorithmPath DefaultPathSearcher;
    
    // Kolla om man kan vinna , kanske greedy bfs?
    private ISearchAlgorithmPath BlockPathChecker;

    private BoardState CurrentBoardState;
    
    public Player(BoardState initalState, ISearchAlgorithmPath pathSearcher)
    {
        CurrentBoardState = initalState;
        DefaultPathSearcher = pathSearcher;
    }
    
    public String findPath(BoardPosition start, BoardPosition goal)
    {
        Set<BoardPosition> destination = new HashSet<>();
        destination.add(goal);
        return findPath(start, destination);
    }
    
    public String findPath(BoardPosition start, Set<BoardPosition> destinations)
    {
        if(CurrentBoardState.getNode(start) == NodeType.PLAYER_ON_GOAL)
            return "";
        Path p = DefaultPathSearcher.getPath(CurrentBoardState, start, destinations);
        if(p == null)
            return "no path";
        return p.toString();
    }  
    
    public void setCurrentBoardState(BoardState state)
    {
        CurrentBoardState = state;
    }
    
    public BoardState getCurrentBoardState()
    {
        return CurrentBoardState;
    }
    
    
}
