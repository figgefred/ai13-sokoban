/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.fredmaster2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import sokoban.BoardPosition;

/**
 *
 * @author figgefred
 */
public abstract class Region {
    
    List<Set<BoardPosition>> Positions;
    
    public Region()
    {
        Positions = new ArrayList<>();
    }
    
    public abstract boolean add(BoardPosition p);
    
    
    
    
}
