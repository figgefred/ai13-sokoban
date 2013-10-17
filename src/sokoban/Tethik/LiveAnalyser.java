/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban.Tethik;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import sokoban.BoardPosition;
import sokoban.NodeType;

/**
 *
 * @author figgefred
 */
public class LiveAnalyser {
  
    
    /**
     * Returns a list of corral areas and (or only) the area of where the
     * player is.
     * 
     * A corral area is an area which a player cannot reach except by pushing
     * a block.
     * 
     * CorralArea's will hold a field pointing to a list of blocks that are acting
     * as fences (creating the corrol fence). These should be prioritized so that
     * a corral area can be solved as fast as possible. This is so called 'corrol pruning'.
     * 
     * @param board
     * @return 
     */
    public static List<CorralArea> getAreas(BoardState board)
    {
        // A list containing all the nodes "visited"
        Set<BoardPosition> visited = new HashSet<>();
        List<CorralArea> list = new ArrayList<>();
        
        int areaCounter = 1;
        
        //**************************//
        // Iterpret areas from map //
        //*********************** //
        
        for(int r = 0; r < board.getRowsCount(); r++)
        {
            for(int c = 0; c < board.getColumnsCount(); c++)
            {
                BoardPosition p = new BoardPosition(r,c);
                NodeType nodeType = board.get(p);
                
                if( !visited.contains(p) && nodeType.isSpaceNode() )
                {
                    CorralArea area = new CorralArea(areaCounter++, board);
                    setCorralArea(board, area, p, visited);
                    list.add(area);
                }
            }
        }

        // If there is only a play area there is no point to continue
        if(list.size() <= 1)
            return list;
        
        //**************************//
        // Find the corral fences  //
        //*********************** //
        
        // !!!!!!!!!!!!!!
        // The following code segments can probably be implemented in a better way.
        // !!!!!!!!!!!!!!        
        
        // A list of block nodes that are fence candidates for the corral areas
        Map<BoardPosition, CorralFenceCandidate> fenceCandidates = new HashMap<>();
        
        // This part is crucial, we must find out which blocks are 'touched'
        // by more then 1 area
        for(CorralArea a: list)
        {
            //for(BoardPosition p: board.getBlockNodes())
            for(BoardPosition p: a.getAreaPositions())
            {
                if(board.get(p).isBlockNode())
                {
                    // For every a try to match every block
                    CorralFenceCandidate c = fenceCandidates.get(p);
                    if(c == null)
                    {
                        c = new CorralFenceCandidate(p);
                        fenceCandidates.put(p, c);
                    }
                    c.addCorralArea(a);
                }
            }
        }
        
        //******************************//
        // Add the fences to the area **//
        //******************************//
        
        // Naive merge
        for(CorralFenceCandidate f : fenceCandidates.values())
        {
            for(CorralArea a: list)
            {
                if(f.isNodeOf(a))
                {
                    if(f.isPartOfCorralAreaFence())
                    {
                        a.addAsFenceNode(f.getBoardPosition(), board.get(f.getBoardPosition()));
                    }
                    else
                    {
                        a.add(f.getBoardPosition(), board.get(f.getBoardPosition()));
                    }
                }
            }
        }
        return list;
    }
    
    private static void setCorralArea(BoardState board, CorralArea area, BoardPosition spaceNode, Set<BoardPosition> visited)
    {
        NodeType nodeType = board.get(spaceNode);
        area.add(spaceNode, nodeType);
        visited.add(spaceNode);
        
        for(BoardPosition neighbour: board.getNeighbours(spaceNode))
        {
            NodeType neighbourType = board.get(neighbour);
            if( !visited.contains(neighbour) && ( neighbourType.isSpaceNode()) )
            {
                // If unvisited expand!
                setCorralArea(board, area, neighbour, visited);
            }
            else if(neighbourType.isBlockNode())
            {
                //area.add(neighbour, neighbourType);
                visitNeighbouringBlocks(board, area, neighbour, new HashSet<BoardPosition>());
            }
        }
    }
    
    private static void visitNeighbouringBlocks(BoardState board, CorralArea area, BoardPosition blockNode, Set<BoardPosition> visited)
    {
        NodeType nodeType = board.get(blockNode);
        area.add(blockNode, nodeType);
        visited.add(blockNode);
        for(BoardPosition blockNeighbour: board.getNeighbours(blockNode))
        {
            NodeType neighbourType = board.get(blockNeighbour);
            if( !visited.contains(blockNeighbour) && ( neighbourType.isBlockNode()) )
            {
                // If unvisited expand!
                visitNeighbouringBlocks(board, area, blockNeighbour, visited);
            }
        }
    }
   
    
	public static void main(String[] args) throws IOException, InterruptedException {
//		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");
		
		Vector<String> b = new Vector<String>();
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		
		String line;
                line = br.readLine();
		while(line != null) {
                    if(line.equals(""))
                        break;
                    b.add(line);
                    line = br.readLine();
		} // End while
		
//                Player.DO_BIPARTITE_MATCHING = true;
//                Player.DO_CORRAL_LIVE_DETECTION = true;
//                Player.DO_DEADLOCKS_CONSTANTCHECK = true;
//                Player.DO_DEADLOCKS_4x4 = true;
//                Player.DO_GOAL_SORTING = false;
                
		//System.out.println(board);
		BoardState board = new BoardState(b, true);       
        
        List<CorralArea> areas = LiveAnalyser.getAreas(board);
        for(CorralArea a: areas)
        {
            System.out.println(a);
        }
                
	//	Player noob = new Player(board);
		//System.out.println(noob.play());
	}
}
