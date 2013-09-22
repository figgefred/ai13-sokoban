/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

/**
 *
 * @author figgefred
 */
public class BoardPosition
{
    public final int Row;
    public final int Column;
    private Integer MyHash = null;
    
    public BoardPosition(int row, int col)
    {
        Row = row;
        Column = col;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof BoardPosition)
        {
            return this.hashCode() == o.hashCode();
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        if(MyHash == null)
        {
            MyHash = 12345*Row*Row + Column;
        }
        return MyHash;
    }
}
