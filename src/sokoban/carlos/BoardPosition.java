package sokoban.carlos;

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
    
    public BoardPosition getNeighbouringPosition(Direction direction) {
    	switch (direction) {
			case UP:
				return new BoardPosition(Row-1, Column);
			case DOWN:
				return new BoardPosition(Row+1, Column);
			case RIGHT:
				return new BoardPosition(Row, Column+1);
			case LEFT:
				return new BoardPosition(Row, Column-1);
			default:
				return this;	
		}
    }
    
    public Direction getDirection(BoardPosition to)
	{
    	return BoardPosition.getDirection(this, to);
	}
    
    public static Direction getDirection(BoardPosition from, BoardPosition to)
	{
	    if( from.Row-1 == to.Row && from.Column == to.Column )
	    {
	        return Direction.UP;
	    }
	    if(from.Row+1 == to.Row && from.Column == to.Column)
	    {
	        return Direction.DOWN;
	    }
	    if(from.Column-1 == to.Column && from.Row == to.Row)
	    {
	        return Direction.LEFT;
	    }
	    if(from.Column+1 == to.Column && from.Row == to.Row)
	    {
	        return Direction.RIGHT;
	    }
	    return Direction.NONE;
	}
}
