package sokoban.Yvonne.Reverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


public class BlockPathFinder {
	private PriorityQueue<AStar_Node> openSet;
	private HashMap<BoardPosition,AStar_Node> closedSet;
	private List<List<AStar_Node>> nodeMap;
	
	private int ConstantWeight;
	private int EstimateWeight;

    
    public BlockPathFinder() {
    	ConstantWeight = 0;
        EstimateWeight = 1;
    }

	public Path getPath(BoardState board, BoardPosition initialPosition, BoardPosition goal) {

        openSet=new PriorityQueue<AStar_Node>();
        nodeMap = new ArrayList<List<AStar_Node>>();
        closedSet=new HashMap<BoardPosition, AStar_Node>();

        //build nodeMap
        for(int row=0; row<board.getRowsCount(); row++){
            nodeMap.add(new ArrayList<AStar_Node>());
            for(int column=0; column <board.getColumnsCount(row); column++){

                NodeType nodeType=board.getNode(row, column);
                if(nodeType==NodeType.SPACE || nodeType == NodeType.GOAL 
                		|| nodeType==NodeType.PLAYER || nodeType==NodeType.PLAYER_ON_GOAL) {
                    nodeMap.get(row).add(column,new AStar_Node(Math.abs((row-goal.Row)+Math.abs(column-goal.Column)), new BoardPosition(row, column)));

                } else {
                    nodeMap.get(row).add(column,null);
                }
            }
        }
        openSet.add(nodeMap.get(initialPosition.Row).get(initialPosition.Column));

        while(!openSet.isEmpty()){
            AStar_Node current = (AStar_Node) openSet.poll();
            if(current.bp.Row == goal.Row && current.bp.Column==goal.Column){
                    return reconstruct_path(current);
            }
            closedSet.put(current.bp, current);

            List<BoardPosition> neighbourPositions = board.getDraggingPositions(current.bp);
            for(BoardPosition neighbour : neighbourPositions){
                AStar_Node neighbourNode=nodeMap.get(neighbour.Row).get(neighbour.Column);
                if(neighbourNode!=null){
	                int tentative_g_score = current.g+1;
	                if(closedSet.containsValue(neighbourNode) &&(tentative_g_score >= neighbourNode.g)){
	                    continue;
	                }
	                if(!openSet.contains(neighbourNode) || (tentative_g_score < neighbourNode.g)){
	                    neighbourNode.parent=current;
	                    neighbourNode.g=tentative_g_score;
	                    neighbourNode.f = getFValue(neighbourNode.g, neighbourNode.h);
	                    if(!openSet.contains(neighbourNode)){
	                        openSet.add(neighbourNode);
	                    }
	
	                }
                }
            }
        }
        
        return null;
	}

	
	public Path getPath(BoardState state, BoardPosition initialPosition, Set<BoardPosition> destination) {
	
		for(BoardPosition goal: destination){
			openSet=new PriorityQueue<AStar_Node>();
			nodeMap = new ArrayList<List<AStar_Node>>();
			closedSet=new HashMap<BoardPosition, AStar_Node>();
	
			//build nodeMap
			for(int row=0; row<state.getRowsCount(); row++){
				nodeMap.add(new ArrayList<AStar_Node>());
				for(int column=0; column <state.getColumnsCount(row); column++){
	
					NodeType nodeType=state.getNode(row, column);
					if(nodeType!=NodeType.WALL && nodeType != NodeType.INVALID) {
						/*|| nodeType == NodeType.GOAL 
							||nodeType==NodeType.PLAYER || nodeType==NodeType.PLAYER_ON_GOAL) {*/
						nodeMap.get(row).add(column,new AStar_Node(Math.abs((row-goal.Row)+Math.abs(column-goal.Column)), new BoardPosition(row, column)));
	
					}else{
						nodeMap.get(row).add(column,null);
	
					}
				}
			}
		}
	
		openSet.add(nodeMap.get(initialPosition.Row).get(initialPosition.Column));


		while(!openSet.isEmpty()){
			AStar_Node current = (AStar_Node) openSet.poll();
			for(BoardPosition goal: destination){
				if(current.bp.Row == goal.Row && current.bp.Column==goal.Column){
					return reconstruct_path(current);
				}
			}
			closedSet.put(current.bp, current);

			List<BoardPosition> neighbourPositions = state.getDraggingPositions(current.bp);
			for(BoardPosition neighbour : neighbourPositions){
				AStar_Node neighbourNode=nodeMap.get(neighbour.Row).get(neighbour.Column);
				if(neighbourNode!=null){
					int tentative_g_score = current.g+1;
					if(closedSet.containsValue(neighbourNode) &&(tentative_g_score >= neighbourNode.g)){
						continue;
					}if(!openSet.contains(neighbourNode) || (tentative_g_score < neighbourNode.g)){
						neighbourNode.parent=current;
						neighbourNode.g=tentative_g_score;
						neighbourNode.f=neighbourNode.g+neighbourNode.h;
						if(!openSet.contains(neighbourNode)){
							openSet.add(neighbourNode);
						}

					}

				}
			}

		}


		return null;
	
	
	}

	public Path reconstruct_path(AStar_Node current_node){
		List<BoardPosition> pathList=new ArrayList<BoardPosition>();
		while(current_node.parent!=null){
			pathList.add(current_node.bp);
			current_node=current_node.parent;
		}
		pathList.add(current_node.bp);
		return new Path(pathList,true);
	}

    private int getFValue(int currentCost, int estimateCostLeft)
    {
        return ConstantWeight*currentCost + EstimateWeight*estimateCostLeft;
    }
    
	private class AStar_Node implements Comparable<AStar_Node> {
		int g=1; //total cost of getting to this node
		int h; //estimated time to reach the finish from this node
		int f; //g+h
		AStar_Node parent;
		BoardPosition bp;
	
		private AStar_Node(int hCost,BoardPosition boPo){
	
			h=hCost;
			f = g+h;
			bp=boPo;
	
		}
	
		@Override
		public int compareTo(AStar_Node n) {
			return this.f- n.f;
		}
	}
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/pushtest2");
		System.out.println(board);
		BlockPathFinder pathfinder = new BlockPathFinder();
		Path path = pathfinder.getPath(board, board.getPlayerNode(), board.getGoalNodes());	
		System.out.println(path);
	}
	
}
