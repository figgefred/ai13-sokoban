/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.types;

/**
 *
 * @author figgefred
 */
public enum NodeType
{
    
    INVALID(0), BLOCK(1), BLOCK_ON_GOAL(2), WALL(3), PLAYER(4), GOAL(5), PLAYER_ON_GOAL(6), SPACE(7);
    
    private final int index;
    
    private NodeType(int index)
    {
    	this.index = index;
    }
    
    public int getIndex() {
    	return index;
    }
}

