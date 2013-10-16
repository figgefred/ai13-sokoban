package carloskattis;
/**
 *
 * @author figgefred
 */
public class BoardPosition
{
    public final int Row;
    public final int Column;
    //private Integer MyHash = null;
    
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
        	BoardPosition b = (BoardPosition) o;
        	return b.Row == Row && b.Column == Column;
        	// nja
            //return this.hashCode() == o.hashCode();
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {    	
        return 171717*(Row+1)*(Row+1) + 13*(Column+2);
    }
    
    @Override
    public String toString() {
    	return "Row: " + Row + ", col: " + Column;
    }
    
    public int DistanceTo(BoardPosition b) {
    	return Math.abs(Row - b.Row) + Math.abs(Column - b.Column);
    }
}
