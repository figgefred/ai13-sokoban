package kattisredo;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author figgefred
 */
public enum NodeType
{    
    INVALID(0,'?'), 
    BLOCK(1, '$'), 
    BLOCK_ON_GOAL(2, '*'),
    WALL(3, '#'),
    PLAYER(4, '@'),
    GOAL(5, '.'), 
    PLAYER_ON_GOAL(6, '+'), 
    SPACE(7, ' ');
    
    private final int index;
    private final char c;
    
    private NodeType(int index, char c)
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
    
    public static NodeType parse(char c)
	{
    	for(NodeType type : NodeType.values())
    	{
    		if(type.getChar() == c)
    			return type;
    	}
    	return NodeType.INVALID;
	}
}

