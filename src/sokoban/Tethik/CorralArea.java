/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Tethik;

import java.util.HashSet;
import java.util.Set;

/**
 * An area is a region of a map where the player can move to. If a player cannot
 * move to an area, then an CorralArea is flagged as being a corral area, which means that
 * isCorralArea() function returns true.
 * 
 * If a map contains no corrals areas then the resp. function creating the areas
 * should only return 1 single area.
 * 
 * This area also features the ability to set its resp. fences. These are block nodes
 * that block players to move into a corral area or block the player to move out of
 * a "play" area.
 * 
 * @author figgefred
 */
public class CorralArea 
{
    private int ID;
    private Set<BoardPosition> Positions = new HashSet<BoardPosition>();
    private Set<BoardPosition> NoFenceBlocks = new HashSet<BoardPosition>();
    private Set<BoardPosition> Fence = new HashSet<BoardPosition>();
    private boolean ContainsGoal;
    private boolean ContainsPlayer;
    private BoardState Board;
    
    
    public CorralArea(int id)
    {
        init(id, null);
    }
    
    public CorralArea(int id, BoardState board)
    {
        init(id, board);
    }
    
    public CorralArea(int id, Set<BoardPosition> pos)
    {
        this(id, pos, null);
    }
    
    public CorralArea(int id, Set<BoardPosition> pos, BoardState board)
    {
        Positions  = pos;
        init(id, board);
    }
    
    private void init(int id, BoardState board)
    {
        if(board != null)
            Board = board;
        if(Fence == null)
        {
            Fence = new HashSet<BoardPosition>();
        }
            //Board = (BoardState) board.clone();
        if(Positions == null)
            Positions = new HashSet<BoardPosition>();
        if(NoFenceBlocks == null)
            NoFenceBlocks = new HashSet<BoardPosition>();
        ID = id;
    }
        
    /**
     * Returns true if a boardposition was added to the area. The boardpositions
     * NodeType is important so that player or goal flags can be set.
     * 
     * @param p
     * @param nodeType
     * @return 
     */
    public boolean add(BoardPosition p, NodeType nodeType)
    {   
        boolean added = Positions.add(p);
        if(added)
        {
            if(nodeType.isPlayerNode())
                ContainsPlayer = true;
            if(nodeType.isGoalNode())
                ContainsGoal = true;
            if(nodeType.isBlockNode())
                NoFenceBlocks.add(p);
        }
        return added;
    }
    
    /**
     * Returns true if a BoardPosition was added as a fence node to the area. The
     * BoardPosition NodeType is checking if the position is actually of a Block type.
     * 
     * BEWARE: You can cheat and actually send in another NodeType, but why would you?
     * 
     * This function will automatically remove the BoardPosition from the area and
     * regard it as a fence node instead.
     * 
     * @param p
     * @param nodeType
     * @return 
     */
    public boolean addAsFenceNode(BoardPosition p, NodeType nodeType)
    {
        if(nodeType.isBlockNode())
        {
            Positions.remove(p);
            return Fence.add(p);
        }
        return false;
    }
    
    /*public void setContainsPlayer(boolean containsPlayer)
    {
        ContainsPlayer = containsPlayer;
    }
    
    public void setContainsGoal(boolean containsGoal)
    {
        ContainsGoal = containsGoal;
    }*/
    
    public Set<BoardPosition> getAreaPositions()
    {
        return Positions;
    }
    
    public Set<BoardPosition> getFencePositions()
    {
        return Fence;
    }
    
    public Set<BoardPosition> getNoFenceBlockPositions()
    {
        return NoFenceBlocks;
    }
    
    /**
     * Returns true if a player cannot reach area without pushing boxes
     * @return 
     */
    public boolean isCorralArea()
    {
        return !ContainsPlayer;
    }
    
    public boolean isGoalArea()
    {
        return ContainsPlayer;
    }
    
    public boolean isMemberOfArea(BoardPosition p)
    {
        return Positions.contains(p);
    }
    
    public int getAreaId()
    {
        return ID;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
       
        if(Board == null)
        {
            if(ContainsPlayer)
            {
                sb.append("Play area AS area " + ID);
            }
            else
            {
                sb.append("Corral Area AS area " + ID);
            }
            if(ContainsGoal)
                sb.append(" (Contains goals)");

            sb.append("\n");
            for(BoardPosition p: Positions)
            {
                sb.append(p).append(" ");
            }
        }
        else
        {
            for(int r = 0; r < Board.getRowsCount(); r++)
            {
                for(int c = 0; c < Board.getColumnsCount(); c++)
                {
                    BoardPosition p = new BoardPosition(r, c);
                    if(Positions.contains(p) && (!Board.get(p).isPlayerNode() ) )// && Board.getNode(p) != NodeType.WALL ) )
                    {
                        sb.append(ID);
                    }
                    else if(Fence.contains(p) )// && Board.getNode(p) != NodeType.WALL ) )
                    {
                        sb.append("F");
                    }
                    else
                    {
                        sb.append(Board.get(p).getChar());
                    }
                }
                sb.append("\n");
            }
            
            System.out.println("No. Positions in area is = " + Positions.size());
            System.out.println("No. Positions in fence is = " + Fence.size());
            
        }
        return sb.toString();
    }
    
    
}
