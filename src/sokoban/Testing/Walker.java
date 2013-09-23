package sokoban.Testing;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.NodeType;
import sokoban.Testing.exceptions.*;

public class Walker {
	
	private BoardState state;
	
	public Walker(BoardState state)
	{
		this.state = state;
	}
	
	public void walk(Path path) throws InvalidPositionException {		
		for(BoardPosition pos : path.getPath())
		{
			NodeType type = state.getNode(pos);
			
			if(type == NodeType.WALL)
				throw new InvalidPositionException(pos, state);
			
		}	
	}
	


}
