package sokoban.Algorithms;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.types.NodeType;

/**
 * A*
 * 
 * @author Yvonne Le
 */

public class AStar_Path implements ISearchAlgorithmPath{
        private PriorityQueue<AStar_Node> openSet;
        private Hashtable<BoardPosition,AStar_Node> closedSet;
        private List<List<AStar_Node>> nodeMap;

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
                        openSet=new PriorityQueue<AStar_Node>();
                        nodeMap = new ArrayList<List<AStar_Node>>();
                        closedSet=new Hashtable<BoardPosition, AStar_Node>();

                        //build nodeMap
                        for(int row=0; row<state.getRowsCount(); row++){
                                nodeMap.add(new ArrayList<AStar_Node>());
                                for(int column=0; column <state.getColumnsCount(row); column++){

                                        NodeType nodeType=state.getNode(row, column);
                                        if(nodeType==NodeType.SPACE || nodeType == NodeType.GOAL ||nodeType==NodeType.PLAYER){
                                                nodeMap.get(row).add(column,new AStar_Node(Math.abs((row-goal.Row)+Math.abs(column-goal.Column)), new BoardPosition(row, column)));

                                        }else{
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

                                List<BoardPosition> neighbourPositions = state.getNeighbours(current.bp);
                                for(BoardPosition neighbour : neighbourPositions){
                                        AStar_Node neighbourNode=nodeMap.get(neighbour.Row).get(neighbour.Column);
                                        if(neighbourNode!=null){
                                                int tentative_g_score = current.g+1;
                                                if(closedSet.contains(neighbourNode) &&(tentative_g_score >= neighbourNode.g)){
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

        private class AStar_Node implements Comparable{
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
                public int compareTo(Object o) {
                        AStar_Node n = (AStar_Node) o;
                        return this.f- n.f;
                }

        }
}package sokoban.Yvonne2;

import java.util.ArrayList;
import java.util.List;

import sokoban.BoardPosition;
import sokoban.Direction;

public class Move implements Comparable<Move> {
	private PathFinder pathfinder = new PathFinder();
	private Analyser analyser = null;
	public BoardState board;
	public Path path;
	private Integer heuristic_value = null;
	public int pushes = 1;
	public Integer f=0;
	public static boolean ASTAR = true;

	public Move(Analyser analyser, PathFinder pathfinder) {
		this.pathfinder = pathfinder;
		this.analyser = analyser;
	}


	public int getFValue(){
		return f;
	}

	public void calcF(){
		f=pushes+heuristic_value;
	}


	public int getHeuristicValue() {
		if(heuristic_value != null)
			return heuristic_value;

		//		BoardPosition lastpos = path.get(path.getPath().size() - 2);
		//		BoardPosition pushedBlock = null;
		//		if(lastpos != null) {
		//			Direction pushDirection = lastpos.getDirection(board.getPlayerNode());
		//			pushedBlock = board.getPlayerNode().getNeighbouringPosition(pushDirection);
		//		}
		if(ASTAR){
		heuristic_value = analyser.getHeuristicValue(board)*-1;
		}else{
			heuristic_value = analyser.getHeuristicValue(board);
		}
		//heuristic_value = analyser.getHeuristicValue(board);
		return heuristic_value;
	}

	public List<Move> getNextMoves() {
		List<Move> possibleMoves = new ArrayList<Move>();
		List<BoardPosition> blocks = board.getBlockNodes();
		BoardPosition playerPos = board.getPlayerNode();		

		/* Block move based */
		for(BoardPosition blockPos : blocks)
		{
			// hitta ställen man kan göra förflyttningar av block.
			// skriva om sen..
			List<BoardPosition> pushPositions = board.getPushingPositions(blockPos);

			// now do pathfinding to see if player can reach it..
			for(BoardPosition candidate : pushPositions)
			{
				Path toPush;
				if(candidate.equals(playerPos))
					toPush = new Path(candidate);
				else
					toPush = pathfinder.getPath(board, candidate);		

				if(toPush == null) // no path found
					continue;				

				toPush.append(blockPos);

				BoardState newBoard = (BoardState) board.clone();
				// move the player along the path.
				newBoard.movePlayer(toPush);
				// push the block by moving towards the block.
				newBoard.movePlayerTo(blockPos);

				Move move = new Move(analyser, pathfinder);
				move.board = newBoard;
				move.path = path.cloneAndAppend(toPush);
				move.pushes = pushes + 1;
				possibleMoves.add(move);					
			}		

		} 

		return possibleMoves;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Move))
			return false;

		Move b = (Move) o;			
		return b.board.equals(this.board);

	}


	public int compareTo(Move o) {		
		if(ASTAR){
			if(o.getFValue() > this.getFValue())
				return -1;
			else if(o.getFValue() < this.getFValue())
				return 1;
			return 0;

		}else{		
			if(o.getHeuristicValue() > this.getHeuristicValue())
				return 1;
			else if(o.getHeuristicValue() < this.getHeuristicValue())
				return -1;
			return 0;

		}
	}

}