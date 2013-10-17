package sokoban.Yvonne.Reverse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import sokoban.BoardPosition;
import sokoban.NodeType;


/**
 * A*
 * 
 * @author Yvonne Le
 */

public class AStar_Path{
        private PriorityQueue<AStar_Node> openSet;
        private Hashtable<BoardPosition,AStar_Node> closedSet;
        private List<List<AStar_Node>> nodeMap;

        public Path getPath(BoardState state, BoardPosition initialPosition,
                        BoardPosition destination) {
                Set<BoardPosition> position = new HashSet<>();
                position.add(destination);
                return getPath(state, initialPosition, position);
        }

        public Path getPath(BoardState state, BoardPosition initialPosition,
                        Set<BoardPosition> destination) {

                for(BoardPosition goal: destination){
                        openSet=new PriorityQueue<AStar_Node>();
                        nodeMap = new ArrayList<List<AStar_Node>>();
                        closedSet=new Hashtable<BoardPosition, AStar_Node>();

                        //build nodeMap
                        for(int row=0; row<state.getRowsCount(); row++){
                                nodeMap.add(new ArrayList<AStar_Node>());
                                for(int column=0; column <state.getColumnsCount(); column++){

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
}