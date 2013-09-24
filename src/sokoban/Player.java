/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private ISearchAlgorithmPath BlockPathSearcher;

    private BoardState CurrentBoardState;
    
    public Player(BoardState initalState, ISearchAlgorithmPath blockPathSearcher,  ISearchAlgorithmPath pathSearcher)
    {
        CurrentBoardState = initalState;
        DefaultPathSearcher = pathSearcher;
        BlockPathSearcher = blockPathSearcher;
    }
    
    public Path pushBlockPath(BoardPosition start, BoardPosition goal)
    {
        Path p = BlockPathSearcher.getPath(CurrentBoardState, start, goal);
        
        if(p == null)
        {
        	ArrayList<BoardPosition> nodes = new ArrayList<BoardPosition>();
        	return new Path(nodes);
        }
        
        return p;
    }
    
    public Path findPath(BoardPosition start, BoardPosition goal)
    {
        Path p = DefaultPathSearcher.getPath(CurrentBoardState, start, goal);
        
        if(p == null)
        {
        	ArrayList<BoardPosition> nodes = new ArrayList<BoardPosition>();
        	return new Path(nodes);
        }
        
        return p;
    }
    
    public Path findPath(BoardPosition start, Set<BoardPosition> destinations)
    {
        Path p = DefaultPathSearcher.getPath(CurrentBoardState, start, destinations);   
        if(p == null)
        {
        	List<BoardPosition> nodes = new ArrayList<BoardPosition>();
        	return new Path(nodes);
        }
        return p;
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
