/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.fredmaster2;

import java.util.ArrayList;
import java.util.List;
import sokoban.BoardPosition;

/**
 *
 * @author figgefred
 */
public class Tunnels {
    
    private List<Tunnel> Tunnels;
    
    public Tunnels()
    {
        Tunnels = new ArrayList<Tunnel>();
    }
    
    public void add(Tunnel t)
    {
     //   if(t.count() > 1)
            Tunnels.add(t);
    }
    
    public void set(int index, Tunnel val)
    {
        if(index < 0 || index >= Tunnels.size())
            return;
        Tunnels.set(index, val);
    }
    
    public Tunnel get(int index)
    {
        if(index < 0 || index >= Tunnels.size())
            return null;
        return Tunnels.get(index);
    }
    
    public List<Tunnel> get()
    {
        return Tunnels;
    }
    
    public boolean isEmpty()
    {
        return Tunnels.isEmpty();
    }
    
    public int count()
    {
        return Tunnels.size();
    }
   
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        BoardState InitState = Tunnels.get(0).getStartState();
        
        for(int r = 0; r < InitState.getRowsCount(); r++)
        {
            for(int c = 0; c < InitState.getColumnsCount(r);c++)
            {
                BoardPosition p = new BoardPosition(r, c);
                boolean noTunnel = true;
                for(Tunnel t: Tunnels)
                {

                    if(t.contains(p))// && Board.getNode(p) != NodeType.WALL ) )
                    {
                        sb.append("T");
                        noTunnel= false;
                        break;
                    }
                }
                if(noTunnel)
                    sb.append(InitState.get(p).getChar());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
