package sokoban.Algorithms;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.NodeType;

public class Greedy_Path implements ISearchAlgorithmPath{
    private Queue<BoardPosition> openSet;
    private ArrayList<Integer> closedSet;
    
    private HashMap<BoardPosition, SimpleEntry<Double, Double>> costs;
    private HashMap<BoardPosition, BoardPosition> cameFrom;

	@Override
	public Path getPath(BoardState state, BoardPosition initialPosition, BoardPosition destination)
	{
		Set<BoardPosition> position = new HashSet<>();
        position.add(destination);
        return getPath(state, initialPosition, position);
	}

	@Override
	public Path getPath(BoardState state, BoardPosition initialPosition, Set<BoardPosition> destinations)
	{
		openSet = new LinkedList<BoardPosition>();
		closedSet = new ArrayList<Integer>();
		costs = new HashMap<BoardPosition, SimpleEntry<Double, Double>>(); // g sparas som key, f som value
		cameFrom = new HashMap<BoardPosition, BoardPosition>();
		
		BoardPosition destination = destinations.iterator().next();
		costs.put(initialPosition, new SimpleEntry<>(0.0, cost(initialPosition, destination)));
		cameFrom.put(initialPosition, null);

    	openSet.add(initialPosition);
    	Path path = null;
    	
        while(!openSet.isEmpty())
        {
        	BoardPosition node = openSet.poll();
        	System.out.println(node);
        	closedSet.add(node.hashCode());
        	
        	if(node.equals(destination))
        	{
        		ArrayList<BoardPosition> nodes = reconstruct_path(destination);
        		nodes.add(0, destination);
        		path = new Path(nodes, true);
        		break;
        	}
        	
        	SimpleEntry<Double, Double> nodeCosts = costs.get(node);
        	List<BoardPosition> neighbours = state.getNeighbours(node);
        	
        	for(BoardPosition neighbour: neighbours)
        	{
        		
                if(!isNoneBlockingNode(state, neighbour))
                	continue;
                
                double cost = cost(neighbour, destination);
                
                if(!closedSet.contains(neighbour.hashCode()))
               	{
                	costs.put(neighbour, new SimpleEntry<>(0.0, cost));
                	cameFrom.put(neighbour, node);
                	openSet.add(neighbour);
               	}
                else 
               	{
                	
               	}
                
        		//double to_g = (neighbourCosts != null ? neighbourCosts.getKey() : Double.MAX_VALUE);
        		
        	}
        	
        }
        		
		
		return path;

	}
	
	private ArrayList<BoardPosition> reconstruct_path(BoardPosition goal)
	{
		ArrayList<BoardPosition> nodes = new ArrayList<>();
		BoardPosition b;
		
		while((b = cameFrom.get(goal)) != null) {
			goal = b;
			nodes.add(b);
		}
		
		
		return nodes;
	}

	private boolean isNoneBlockingNode(BoardState state, BoardPosition p)
    {
        NodeType type = state.getNode(p);
        if(type == NodeType.INVALID)
            System.err.println("Referring to position " + p + " which refers to INVALID type");
        
        return 
               type != NodeType.INVALID &&
               type != NodeType.WALL &&
               type != NodeType.BLOCK &&
               type != NodeType.BLOCK_ON_GOAL;
    }
	
	private double cost(BoardPosition n, BoardPosition destination)
    {
    	int dx = Math.abs(n.Row - destination.Row);
    	int dy = Math.abs(n.Column - destination.Column);
    	
    	return 1 * (dx + dy);
    //	return Math.sqrt(Math.pow(n.Row - destination.Column, 2) + Math.pow(n.Column - destination.Column, 2));
    }
}