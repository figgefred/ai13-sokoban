package sokoban.Algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.NodeType;

public class AStar_Path implements ISearchAlgorithmPath{
    private Path path;
    private PriorityQueue openSet;
    private Set closedSet;
    private List<List<AStar_Node>> nodeMap;
    private HashMap<BoardPosition,AStar_Node> nMap;

	@Override
	public Path getPath(BoardState state, BoardPosition initialPosition,
			BoardPosition destination) {
		Set<BoardPosition> position = new HashSet<>();
        position.add(destination);
        return getPath(state, initialPosition, position);
	}

	@Override
	public Path getPath(BoardState state, BoardPosition initialPosition,
			Set<BoardPosition> destination) {
		
        for(BoardPosition goal: destination){
        	openSet=new PriorityQueue();
            nodeMap = new ArrayList<>();
            
    		//build nodeMap
    		for(int row=0; row<state.getRowsCount(); row++){
    			for(int column=0; column <state.getColumnsCount(row); column++){
    	            nodeMap.add(new ArrayList<AStar_Node>(state.getColumnsCount(row)));
    				NodeType nodeType=state.getNode(row, column);
    				if(nodeType== NodeType.SPACE || nodeType == NodeType.GOAL); 
    					//nMap.put(arg0, arg1)
    				nodeMap.get(row).add(new AStar_Node(Math.abs((row-goal.Row)+(column-goal.Column))));
    		}
    		}
    		
    		
    		while(!openSet.isEmpty()){
    			AStar_Node current = (AStar_Node) openSet.poll();
    			if(current.bp == destination){
    				//TODO
    			}
    			closedSet.add(current);
    	//		List<BoardPosition> neighbourPositions = state.getNeighbours()
    			
    				
    			
    		}
    		
    		return null;
        }
		return path;
        

	}
	
	public void reconstruct_path(Path came_from, BoardPosition current_node){
		//TODO
	}
	
	private class AStar_Node implements Comparable{
		int g=1; //total cost of getting to this node
		int h; //estimated time to reach the finish from this node
		int f; //g+h
		BoardPosition bp;
		
		private AStar_Node(int hCost){
			
			h=hCost;
			f = g+h;
			
		}
	
		@Override
		public int compareTo(Object o) {
			AStar_Node n = (AStar_Node) o;
			return this.f- n.f;
		}

}
}