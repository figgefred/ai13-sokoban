/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.fredmaster2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import sokoban.BoardPosition;
import sokoban.Direction;

/**
 *
 * @author figgefred
 */
public class Tunnel extends Region {

    private BoardState StartingState;
    private LinkedList<BoardPosition> Positions;
    private Set<BoardPosition> LookUpSet;
    private boolean ContainsGoal = false;
    private boolean IsOneWay = true;
    private Direction lastAddedDirection = Direction.NONE;
    
    private Path Path;
    private Path RevPath;
    
    public Tunnel(BoardState startingState)
    {
        Positions = new LinkedList<>();
        LookUpSet = new HashSet<>();
        StartingState = startingState;
    }
    
    @Override
    public boolean add(BoardPosition p) {
        
        if(Positions.isEmpty())
        {
            ContainsGoal = StartingState.get(p).isGoalNode();
            LookUpSet.add(p);
            return Positions.add(p);
        }
        else if(!LookUpSet.contains(p))
        {
            BoardPosition neighbour = Positions.peekLast();
            if( StartingState.get(p).isTunnelSpaceNode())
            {
                boolean up = ((neighbour.Row-p.Row ) == 1 && (neighbour.Column-p.Column ) == 0 );
                boolean down = ((p.Row-neighbour.Row ) == 1 && (neighbour.Column-p.Column ) == 0 );
                boolean left = ((neighbour.Row-p.Row ) == 0 && (neighbour.Column-p.Column ) == 1 );
                boolean right = ((neighbour.Row-p.Row ) == 0 && (p.Column-neighbour.Column ) == 1 );
                
                if(up || down)
                {
                    BoardPosition leftPos = new BoardPosition(p.Row, p.Column-1);
                    BoardPosition rightPos = new BoardPosition(p.Row, p.Column+1);
                    if(p.equals(leftPos) || p.equals(rightPos))
                    {
                        return false;
                    }
                    if(!StartingState.get(leftPos).isWallNode() || !StartingState.get(rightPos).isWallNode())
                    {
                        return false;
                    }
                }
                else if(left || right)
                {
                    BoardPosition upPos = new BoardPosition(p.Row-1, p.Column);
                    BoardPosition downPos = new BoardPosition(p.Row+1, p.Column);
                    if(p.equals(upPos) || p.equals(downPos))
                    {
                        return false;
                    }
                    if(!StartingState.get(upPos).isWallNode() || !StartingState.get(downPos).isWallNode())
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
                if(!ContainsGoal)
                    ContainsGoal = StartingState.get(p).isGoalNode();

                Direction dir = Positions.peekLast().getDirection(p);
                if(lastAddedDirection == Direction.NONE)
                {
                    lastAddedDirection = dir;
                }
                else if(IsOneWay && dir != lastAddedDirection)
                {
                    IsOneWay = false;
                    lastAddedDirection = dir;
                }
                LookUpSet.add(p);        
                return Positions.add(p);   
            }
        }
        return false;
    }
    
    public boolean contains(BoardPosition p)
    {
        return LookUpSet.contains(p);
    }
    
    public boolean isOneWay()
    {
        return IsOneWay;
    }
    
    public boolean containsGoal()
    {
        return ContainsGoal;
    }
    
    public int count()
    {
        return Positions.size();
    }
    
    public boolean isEmpty()
    {
        return Positions.isEmpty();
    }
    
    public Path getPath(BoardPosition from)
    {
        Iterator<BoardPosition> iter;
        boolean reverse = false;
        if(Positions.peekFirst().equals(from))
        {
            if(Path != null && Path.getPath().size() == Positions.size())
                return Path;
            iter = Positions.iterator();
        }
        else if(Positions.peekLast().equals(from))
        {
            if(RevPath != null && RevPath.getPath().size() == Positions.size())
                return RevPath;
            iter = Positions.descendingIterator();
            reverse = true;
        }
        else 
            return null;
        
        Path path = new Path();
        while(iter.hasNext())
        {
            path.append(iter.next());
        }
        if(reverse)
            this.RevPath = path;
        else
            this.Path = path;
        return path;
    }
    
    
    
    public Iterator<BoardPosition> getTunnelPath(BoardPosition from)
    {
        if(Positions.peekFirst().equals(from))
            return Positions.iterator();
        else if(Positions.peekLast().equals(from))
            return Positions.descendingIterator();
        else 
            return null;
    }

    public BoardState getStartState()
    {
        return StartingState;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for(int r = 0; r < StartingState.getRowsCount(); r++)
        {
            for(int c = 0; c < StartingState.getColumnsCount(r);c++)
            {
                BoardPosition p = new BoardPosition(r, c);
                if(LookUpSet.contains(p))// && Board.getNode(p) != NodeType.WALL ) )
                {
                    sb.append("T");
                }
                else
                {
                    sb.append(StartingState.get(p).getChar());
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    
}
