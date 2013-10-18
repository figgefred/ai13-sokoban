/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Tethik.Combo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * A class holding a list of Nodes representing a path.
 * 
 * @author figgefred
 */
public class Path {
    private List<BoardPosition> Nodes;    
    
    public Path() {
    	this(new ArrayList<BoardPosition>(), false);
    }
    
    // Lazyconstructor för paths med endast en position (dvs ett steg)
    public Path(BoardPosition start) {
    	this(new ArrayList<BoardPosition>(), false);
    	this.Nodes.add(start);
    }    
    
    public Path(List<BoardPosition> nodes)
    {
        this(nodes, false);
    }
    
    // Lite konstigt med att reversea listan här? eller förekommer det ofta? idk
    public Path(List<BoardPosition> nodes, boolean reversedList)
    {
        if(nodes == null)
            throw new IllegalArgumentException("Path list with nodes is null");
        this.Nodes = nodes;
        if(reversedList && Nodes != null)
            Collections.reverse(Nodes);        
    } 
    
    public Path(Deque<BoardPosition> nodes) {
    	Nodes = new ArrayList<BoardPosition>();
    	
    	for(BoardPosition node : nodes)
    		Nodes.add(node);
    }
   
    
    
    public BoardPosition get(int index)
    {
        if(index >= Nodes.size() || index < 0)
        {
            return null;
        }
        return Nodes.get(index);
    }
    
    public BoardPosition last() {
    	return Nodes.get(Nodes.size() -1);    
    }
    
    public BoardPosition first() {
    	return get(0);
    }
    
    public int size() {
    	return Nodes.size();
    }
    
    public boolean contains(BoardPosition pos) {
    	return Nodes.contains(pos);
    }
    
    public List<BoardPosition> getPath()
    {
        return Nodes;
    }
    
    /**
     * Clones this path and appends given position to it.
     * @param pos
     * @return
     */
    public Path cloneAndAppend(BoardPosition pos)
    {
    	ArrayList<BoardPosition> steps = (ArrayList<BoardPosition>)((ArrayList<BoardPosition>) this.Nodes).clone();
    	steps.add(pos);
    	return new Path(steps);
    }
    
    /***
     * Clones this path and appends given position to it.
     * @param path
     * @return
     */
    public Path cloneAndAppend(Path path)
    {
    	if(this.Nodes == null)
    		return new Path(path.Nodes);
    	ArrayList<BoardPosition> steps = (ArrayList<BoardPosition>)((ArrayList<BoardPosition>) this.Nodes).clone();
    	List<BoardPosition> toAppend = path.getPath();
    	if(steps.size() > 0 && toAppend.size() > 0 && steps.get(steps.size()-1).equals(toAppend.get(0)))
    		steps.remove(steps.size()-1);
    	steps.addAll(path.getPath());
    	return new Path(steps);
    }
    
    public void append(BoardPosition pos) {
    	this.Nodes.add(pos);
    }
    
    @Override
    public String toString()
    {
        String noPath = "no path";
    	if(Nodes == null || Nodes.size() == 0)
    		return noPath;
    	
        StringBuilder sb = new StringBuilder();
        BoardPosition firstNode = null;
        
        for(BoardPosition n: Nodes)
        {
            if(firstNode == null)
            {
                firstNode = n;
                continue;
            }
            sb.append(getDirection(firstNode, n).getChar());
            
            sb.append(" ");
            firstNode = n;
        }
        return sb.toString();
    }
   
    
    private Direction getDirection(BoardPosition p1, BoardPosition p2)
    {
        if(p1.Row > p2.Row)
            return Direction.UP;
        if(p1.Row < p2.Row)
            return Direction.DOWN;
        if(p1.Column > p2.Column)
            return Direction.LEFT;
        if(p1.Column < p2.Column)
            return Direction.RIGHT;
        else 
            return Direction.NONE;
    }
    
}

