/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Tethik;

import java.util.HashSet;
import java.util.Set;

/**
 * A class to temporarily buffer a single Block-type BoardPosition before potentially entering it
 * as a FenceNode into areas. 
 * 
 * The simple idea is:
 *  - Several areas can be related to one position
 *  - If more then one area 'touches' a single block node, then this is with great
 *    probability a fence node.
 * 
 * See what isPartOfCorralAreaFence() returns
 * 
 * @author figgefred
 */
public class CorralFenceCandidate {
    
    private Set<CorralArea> AreasCovered;
    private BoardPosition Position;
    
    /**
     * It is assumed that this BoardPosition points to a BlockNode type BoardPosition
     * @param pos 
     */
    public CorralFenceCandidate(BoardPosition pos)
    {
        Position = pos;
        AreasCovered = new HashSet<>();
    }
    
    /**
     * Add an area that 'touches' this block node BoardPosition
     * @param area 
     */
    public void addCorralArea(CorralArea area)
    {
        AreasCovered.add(area);
    }
    
    public BoardPosition getBoardPosition()
    {
        return Position;
    }
    
    public Set<CorralArea> getAreasCovered()
    {
        return AreasCovered;
    }
    
    public boolean isNodeOf(CorralArea area)
    {
        return AreasCovered.contains(area);
    }
    
    /**
     * If more then one area 'touch' this Candidate, then this is a fence node
     * @return 
     */
    public boolean isPartOfCorralAreaFence()
    {
        return (AreasCovered.size() > 1);
    }
    
}
