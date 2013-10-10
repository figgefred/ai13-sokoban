/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

/**
 *
 * @author figgefred
 */
public enum Direction {
    NONE(-1, '?'), 
    UP(0, 'U'), 
    DOWN(1, 'D'),
    LEFT(2, 'L'),
    RIGHT(3, 'R');
    
    private final int index;
    private final char c;
    
    private Direction(int index, char c)
    {
    	this.index = index;
    	this.c = c;
    }
    
    public int getIndex() {    	
		return this.index;		
    }
    
    public char getChar() {
    	return this.c;
    }
    
    public static Direction parse(char c)
	{
    	for(Direction type : Direction.values())
    	{
    		if(type.getChar() == c)
    			return type;
    	}
    	return Direction.NONE;
	}
}
