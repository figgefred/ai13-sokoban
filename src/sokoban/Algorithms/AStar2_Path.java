package sokoban.Algorithms;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import javax.swing.plaf.basic.BasicOptionPaneUI;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.AlgorithmType;
import sokoban.types.NodeType;

public class AStar2_Path implements ISearchAlgorithmPath{
    private Queue<BoardPosition> openSet;
    private ArrayList<Integer> closedSet;
    
	private final double ConstantWeight;
	private final double EstimateWeight;
    
    private HashMap<BoardPosition, SimpleEntry<Double, Double>> costs;
    private HashMap<BoardPosition, BoardPosition> cameFrom;

    public AStar2_Path()
    {
    	this(AlgorithmType.A_STAR);
    }
    
    public AStar2_Path(AlgorithmType aType)
    {
    	switch(aType)
    	{
	    	case GREEDY_BFS:
	    	{
	    		ConstantWeight = 0;
	    		EstimateWeight = 1;
	    		break;
	    	}
	    	default:
	    	{
	    		ConstantWeight =1;
	    		EstimateWeight =1;
	    		break;
	    	}
    	}
    }
    
	@Override
	public Path getPath(BoardState state, BoardPosition initialPosition, BoardPosition destination)
	{
		openSet = new LinkedList<BoardPosition>();
		closedSet = new ArrayList<Integer>();
		costs = new HashMap<BoardPosition, SimpleEntry<Double, Double>>(); // g sparas som key, f som value
		cameFrom = new HashMap<BoardPosition, BoardPosition>();

		boolean done = false;
		costs.put(initialPosition, new SimpleEntry<>(0.0, getFValue(0.0, cost(initialPosition, destination))));
		cameFrom.put(initialPosition, null);

    	openSet.add(initialPosition);
    	Path path = null;
    	
        while(!openSet.isEmpty() && !done)
        {
        	BoardPosition node = openSet.poll();
        	
        	if(node.equals(destination))
        	{
        		done = true;
        		ArrayList<BoardPosition> nodes = reconstruct_path(destination);
        		nodes.add(0, destination);
        		path = new Path(nodes, true);
        		//System.out.println("LEngth: " + cameFrom.size());
        		return path;
        	}
        	
        	SimpleEntry<Double, Double> nodeCosts = costs.get(node);
        	List<BoardPosition> neighbours = state.getNeighbours(node);
        	
        	for(BoardPosition neighbour: neighbours)
        	{
                        double tentative_g = nodeCosts.getKey() + 1;
        		
        		SimpleEntry<Double, Double> neighbourCosts = costs.get(neighbour);
        		double to_g = (neighbourCosts != null ? neighbourCosts.getKey() : Double.MAX_VALUE);
        		
        		if (closedSet.contains(neighbour.hashCode()) && tentative_g >= to_g)
                	continue;
        		
        		if( isNoneBlockingNode(state, neighbour) && (!openSet.contains(neighbour) || tentative_g < to_g) )
        		{
        			cameFrom.put(neighbour, node);
        			costs.put(neighbour, new SimpleEntry<>(tentative_g, getFValue(tentative_g, cost(neighbour, destination))));
        			openSet.add(neighbour);
        		}
        	}
        	
        	closedSet.add(node.hashCode());
        }
        		
		
		return null;
	}
	
	private double getFValue(double currentCost, double estimateCost) {
		return ConstantWeight*currentCost + EstimateWeight*estimateCost;
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

	@Override
	public Path getPath(BoardState state, BoardPosition initialPosition,
			Set<BoardPosition> destination) {
		// TODO Auto-generated method stub
		return null;
	}
}