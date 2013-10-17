package sokoban.FredTethMerge;

import sokoban.Tethik.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import sokoban.BoardPosition;

/***
 * Hopcroft-Karp maximal bipartite matching
 * http://en.wikipedia.org/wiki/Hopcroft%E2%80%93Karp_algorithm
 * @author tethik
 *
 */
public class HopcroftKarpMatching {

	
	/***
	 *  
	 * @param map
	 * @return
	 */
	public int maxBipartiteMatch(HashMap<BoardPosition, List<BoardPosition>> map, BoardState board)
	{
		BoardPosition nil = new BoardPosition(-1,-1);
		HashMap<BoardPosition, BoardPosition> goal_pairings = new HashMap<BoardPosition, BoardPosition>();
		HashMap<BoardPosition, BoardPosition> block_pairings = new HashMap<BoardPosition, BoardPosition>();
		HashMap<BoardPosition, Integer> dist = new HashMap<BoardPosition, Integer>();
		
		List<BoardPosition> goalNodes = board.getGoalNodes();
		List<BoardPosition> blockNodes = board.getBlockNodes();
		
		for(BoardPosition goal : goalNodes) {			
			goal_pairings.put(goal, nil);
		}		
		for(BoardPosition block : blockNodes) {
			block_pairings.put(block, nil);
		}
		
		/*
		System.out.println(map);
		System.out.println(goal_pairings);
		System.out.println(block_pairings);
		System.out.println(blockNodes);
		*/
		//goal_pairings.put(nil, nil);
			
		
		int matching = 0;
		boolean bfs = true;
		
		while(bfs) {			
			Queue<BoardPosition> queue = new LinkedList<BoardPosition>();			
			
			
			for(BoardPosition goal : goalNodes) {
				if(goal_pairings.get(goal).equals(nil)) {
					queue.add(goal);
					dist.put(goal, 0);
				} else {
					dist.put(goal, Integer.MAX_VALUE);
				}
			}
			
			
			dist.put(nil, Integer.MAX_VALUE);			
			
			while(!queue.isEmpty()) {
				BoardPosition v = queue.poll();
				
				if(dist.get(v) >= dist.get(nil))
					continue;

				for(BoardPosition u : map.get(v))
				{
					BoardPosition pg2 = block_pairings.get(u);	
					if(dist.get(pg2) == Integer.MAX_VALUE) {
						dist.put(pg2, dist.get(v) + 1);
						queue.add(pg2);
					}
										
				}
			}
			
			bfs = dist.get(nil) != Integer.MAX_VALUE;
			
			if(!bfs) {
				break;
			}
			
			// Dfs part..
			for(BoardPosition v : goalNodes) {				
				if(goal_pairings.get(v).equals(nil) && dfsFindMaxMatch(map, goal_pairings, block_pairings, dist, v))
					matching++;					
				
			}
			//System.out.println(matching);
		}
		
		return matching;
	}
	

	
	private boolean dfsFindMaxMatch(HashMap<BoardPosition, List<BoardPosition>> map,
									HashMap<BoardPosition, BoardPosition> goal_pairings,
									HashMap<BoardPosition, BoardPosition> block_pairings,
									HashMap<BoardPosition, Integer> dist,
									BoardPosition v) {
		
		BoardPosition nil = new BoardPosition(-1,-1);
		if(v.equals(nil))
			return true;
		
		if(map.get(v) == null)
		{
			System.out.println(v);
			System.out.println(map.get(v));
			System.exit(0);
		}
		
		for(BoardPosition u : map.get(v))
		{
			BoardPosition pg2 = block_pairings.get(u);
			/*
			System.out.println(u);
			System.out.println(board.getNode(u));
			System.out.println(pg2);
			System.out.println(board.getNode(pg2));
			*/
			if(dist.get(pg2) == dist.get(v) + 1)
				if(dfsFindMaxMatch(map, goal_pairings, block_pairings, dist, pg2))
				{
					block_pairings.put(u, v);
					goal_pairings.put(v, u);
					return true;
				}
			
		}
		
		dist.put(v, Integer.MAX_VALUE);
		return false;	
	}

	
}
