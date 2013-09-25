package sokoban.Tethik;

import java.util.LinkedList;
import sokoban.types.NodeType;
import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;

// kan replaceas sen med ordentlig som tex a*
public class PathFinder {
    
    private BoardState board;
	
    public PathFinder(BoardState board) {
    	this.board = board;
    }
    
    // ful portning av python kod
    // bör dock fungera..
    public Path findPath(BoardPosition from, BoardPosition to) {
    	LinkedList<Path> queue = new LinkedList<>();    	
    	Path startpos = new Path(from);
    	queue.add(startpos);
    	
    	while(queue.size() > 0) {
    		Path path = queue.pop();
    		BoardPosition lastpos = path.last();
    		
    		for(BoardPosition nextStep : board.getNeighbours(lastpos))
    		{
    			if(nextStep.equals(to))
    				return path.cloneAndAppend(nextStep);
    			
    			if(path.contains(nextStep))
    				continue; // already visited
    			
    			NodeType type = board.getNode(nextStep);
    			if(type != NodeType.GOAL || type != NodeType.SPACE)
    				continue; // everything else is assumed to be something that blocks the path.
    			
    			queue.add(path.cloneAndAppend(nextStep));    			    			
    		}
    	}    	
    	
    	return null;
    }
    

}
