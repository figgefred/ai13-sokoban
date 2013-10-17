package sokoban.fredmaster2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import sokoban.BoardPosition;
import sokoban.Direction;
import sokoban.NodeType;


/***
 * PreAnalyser: analyse the board beforehand to calculate positions in which blocks will get stuck in.
 * Usage:
 * 	p = Analyser(board)
 *  
 * @author tethik
 *
 */
public class Analyser {
	
	private boolean badTable[][];
	private NodeType workbench[][];
	private Map<BoardPosition, int[][]> distanceMatrices;
	private int goalDist[];
	private int blockDist[];
	
	//private DeadlockFinder deadlockerFinder = new DeadlockFinder();
	public LiveAnalyser LiveAnalyser;
	
	private BoardState board;
	private int rows;
	private int cols;
	private HopcroftKarpMatching bipartiteMatcher = new HopcroftKarpMatching();
        
        // Index is the BlockIndex, value is BoardPosition of a matched goal
        private Map<BoardPosition, Integer> GoalToBlockIndexMap;
        // A queue containing guesses of which goals to move blocks to first.
        private Queue<BoardPosition> GoalQueue;
        private BoardPosition LastPushedBlock = null;
	
        
        
	public Analyser(BoardState board)
	{
		this.board = board;
		constructTableAndWorkbench();
		// Hitta distanser?
		mapDistancesToGoals(new BoardState(workbench, false));
		LiveAnalyser = new LiveAnalyser(this, new PathFinder());
                
              //  setGoalToBlockMapping();
                setGoalQueueOrder();
            /*    
                System.out.println("Blocks mapped to goals: ");
                for(BoardPosition goal: GoalToBlockIndexMap.keySet())
                {
                    int index = GoalToBlockIndexMap.get(goal);
                    System.out.println("Goalpos @" + goal + " is matched to block index " + index + " @" + board.getBlockNodes().get(index));
                }                
                System.out.println("Goal queue: ");
                
                for(BoardPosition p: GoalQueue)
                {
                    System.out.println(p);
                }                */
               //try {Thread.sleep(2000000);}catch(InterruptedException ex) {}
	}
	
	private void constructTableAndWorkbench() {
		rows = board.getRowsCount();
		cols = board.getColumnsCount();
		
		badTable = new boolean[rows][cols];
		workbench = new NodeType[rows][cols];
		goalDist = new int[board.getGoalNodes().size()];
		blockDist = new int[board.getGoalNodes().size()];
		distanceMatrices = new HashMap<BoardPosition, int[][]>();//[rows][cols];
		
                for(BoardPosition pos : board.getGoalNodes())
                {
                    distanceMatrices.put(pos, new int[rows][cols]);
                }
                
		for(int row = 0; row < rows; ++row)
			for(int col = 0; col < cols; ++col) {
				NodeType type = board.getNode(row, col);				
				
				// Remove blocks from the map
				if(type == NodeType.BLOCK || type == NodeType.PLAYER)
					type = NodeType.SPACE;
				if(type == NodeType.BLOCK_ON_GOAL)
					type = NodeType.GOAL;
				
				workbench[row][col] = type;
				badTable[row][col] = true;
			}	
	}	
	
	private void mapDistancesToGoals(BoardState board)
	{
		//int i = 0;
		for(@SuppressWarnings("unused") BoardPosition goal : board.getGoalNodes()) {		
			for(int row = 0; row < rows; ++row)
				for(int col = 0; col < cols; ++col) {
					distanceMatrices.get(goal)[row][col] = Integer.MAX_VALUE;
				}
		//	++i;
		}
		
		Queue<BoardPosition> positions = new LinkedList<>();
		Queue<Integer> distances = new LinkedList<Integer>();
		Set<BoardPosition> visited = new HashSet<>();
		
		//i = 0;
		for(BoardPosition goal : board.getGoalNodes()) {
		
			if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL)
				continue;
			
			positions.clear();
			distances.clear();
			visited.clear();
			
			positions.add(goal);
			distances.add(0);
			visited.add(goal);
			
			if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL) {
				continue;
			}
			
			distanceMatrices.get(goal)[goal.Row][goal.Column] = 0;
			
			// BFS
			while(!positions.isEmpty()) {
			
				BoardPosition pos = positions.poll();
				int distance = distances.poll();
				
				// Uppdatera positions i matrisen
				distanceMatrices.get(goal)[pos.Row][pos.Column] = distance;
				badTable[pos.Row][pos.Column] = false;
				
				++distance;				

				/*
				if(board.getNode(pos) == NodeType.BLOCK || board.getNode(pos) == NodeType.BLOCK_ON_GOAL)
					continue;						
				*/
				
				for(BoardPosition neighbour : board.getFromNeighbours(pos)) {
					if(visited.contains(neighbour)) 
						continue;
					
					NodeType node = board.getNode(neighbour);
					if(node == NodeType.WALL || node == NodeType.INVALID) 
						continue;
									
					positions.add(neighbour);
					distances.add(distance);
					visited.add(neighbour);
				}
			}	
			
			//i++;			
		}
	}
	
	public boolean isBadPosition(int Row, int Col) {		
		return badTable[Row][Col];
	}
	
	public boolean isBadPosition(BoardPosition pos) {
		return isBadPosition(pos.Row, pos.Column);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(int row = 0; row < rows; ++row) {
			for(int col = 0; col < cols; ++col) {
				NodeType type = workbench[row][col];
				char c = (badTable[row][col]) ? 'x' : ' ';
				if(type == NodeType.GOAL)
					c = '.';
				else if(type == NodeType.WALL)
					c = '#';
				builder.append(c);
				
			}
			builder.append("\n");
			
		}
		return builder.toString();
	}
	
        
        /**
         * Sets the global GoalQueue field. THis field will contain after
         * execution of function the order of which the goals should be populated
         * by blocks.
         * 
         * This ordering implements the idea that goals having more walls surrounding
         * them should be prioritized as these have higher risk of being blocked and a
         * cause for deadlock if left for too long.
         * 
         */
        public void setGoalQueueOrder()
        {
            BoardState boardCopy = (BoardState) board.clone();
            GoalQueue = new LinkedList<BoardPosition>();
            
            // Copy blocks
            BoardPosition[] goals = new BoardPosition[boardCopy.getGoalNodes().size()];
            int i = 0;
            for(BoardPosition g: boardCopy.getGoalNodes())
            {
                goals[i++] = g;
            }
            
            for(int wallNum = 3; wallNum >= 0; wallNum--)
            {
                while(true)
                {
                    boolean manipulatedBoard = false;
                    Set<BoardPosition> justAdded = new HashSet<BoardPosition>();
                    for(i = 0; i < goals.length; i++)
                    {
                        if(goals[i] == null)
                                continue;
                        if(hasSurroundingWalls(boardCopy, goals[i], wallNum))
                        {
                            GoalQueue.add(goals[i]);
                            justAdded.add(goals[i]);
                            goals[i] = null;
                        }   
                    }
                    for(BoardPosition p: justAdded)
                    {
                        boardCopy.manipulate(p, NodeType.WALL);
                        manipulatedBoard = true;
                    }
                    if(!manipulatedBoard)
                        break;
                }
            }
            
                if(GoalQueue.isEmpty())
                    System.err.println("QUeue is empty though it is expected not to be!!!");
                
        }
        
        /**
         * Returns true if a position 'pos' is surrounded by at least 'num' WALL nodes in
         * the neighbouring nodes of UP, DOWN, LEFT or RIGHT directions.
         * @param state
         * @param pos
         * @param num
         * @return 
         */
        private boolean hasSurroundingWalls(BoardState state, BoardPosition pos, int num)
        {            
            BoardPosition[] positions = 
            {
                pos.getNeighbouringPosition(Direction.UP), 
                pos.getNeighbouringPosition(Direction.DOWN), 
                pos.getNeighbouringPosition(Direction.LEFT), 
                pos.getNeighbouringPosition(Direction.RIGHT)
            };
            
            int count = 0;
            for(BoardPosition p: positions)
            {
                if(state.getNode(p) == NodeType.WALL)
                {
                    count++;
                }
            }
            
            return count >= num;
        }
        
        /**
         * Sets an array of goals. The index represents a block at boardposition
         * found in the BlockIndex register of the BoardState
         */
        public void setGoalToBlockMapping()
        {
		List<BoardPosition> blocks = board.getBlockNodes();	
		
		HashMap<BoardPosition, List<BoardPosition>> reachMap = new HashMap<>(); 
		
                int b = 0;
		int i = 0;
		for(BoardPosition goal : board.getGoalNodes())
		{		
			reachMap.put(goal, new ArrayList<BoardPosition>());			
			
			if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL) {
				goalDist[i] = 0;
			}
			
			b = 0; 
			for(BoardPosition block : blocks)
			{
				int dist = distanceMatrices.get(goal)[block.Row][block.Column];
				if(dist < Integer.MAX_VALUE)
				{
					reachMap.get(goal).add(block);
				}
				
				goalDist[i] = Math.min(dist, goalDist[i]);
				
				if(goalDist[i] > 0)
					blockDist[b] = Math.min(dist, blockDist[b++]);
			}			
			++i;
		}
            
                HashMap<BoardPosition, BoardPosition> pos = bipartiteMatcher.maxBipartiteMatch(reachMap, board);
                
                GoalToBlockIndexMap = new HashMap<>();
                blocks = board.getBlockNodes();
                for(BoardPosition goal: pos.keySet())
                {
                    BoardPosition blockPos = pos.get(goal);
                    for(int j = 0; j < blocks.size(); j++)
                    {
                        if(blocks.get(j).equals(blockPos))
                        {
                            GoalToBlockIndexMap.put(goal, j);
                        }
                    }
                }
                

                for(BoardPosition goal : GoalToBlockIndexMap.keySet())
                {
                    if(GoalToBlockIndexMap.get(goal) == null)
                    {
                        System.err.println("A goal is mapped to a null index. WARNING!");
                    }
                }
                
                
        }
        
        /**
         * Returns a mapping of the goals to a resp. BlockIndex. 
         * 
         * The index represents a block at a boardposition found in the BlockIndex 
         * register of the BoardState.
         * 
         * 
         * @return 
         */
        public Map<BoardPosition, Integer> getGoalToBlockMapping()
        {
            return GoalToBlockIndexMap;
        }
        
	
	private void printDistanceMatrix(BoardState board) {
		//mapDistancesToGoals(board);
		
		StringBuilder builder = new StringBuilder();		
		for(BoardPosition goal: board.getGoalNodes())
		{
			for(int row = 0; row < rows; ++row) {
				for(int col = 0; col < cols; ++col) {
					NodeType type = workbench[row][col];
					//(badTable[row][col]) ? "x" : 
					String c = distanceMatrices.get(goal)[row][col] > 9 ? " " : "" + distanceMatrices.get(goal)[row][col];
					if(type == NodeType.WALL)
						c = "#";
					else if(distanceMatrices.get(goal)[row][col] == Integer.MAX_VALUE)
						c = "x";
					else if(type == NodeType.GOAL)
						c = ".";					
		
					builder.append(c).append("");
					
				}
				builder.append("\n");				
			}
		}
		System.out.println(builder.toString());
	}
	
	public int getHeuristicValue(BoardState board, BoardPosition pushedBlock) {
		this.board = board;
		
		if(board.isWin()) {				
			return Integer.MAX_VALUE;
		}		
		
                if(Player.DO_DEADLOCKS_CONSTANTCHECK)
                {
                    if(pushedBlock != null)
                    {
                        if(isBadPosition(pushedBlock))
                        {
                            return Integer.MIN_VALUE;
                        }
                        else if(Player.DO_DEADLOCKS_4x4 && LiveAnalyser.is4x4Block(board, pushedBlock))
                        {
                            return Integer.MIN_VALUE;
                        }
                    }
                }
                
		//mapDistancesToGoals(board);
		
		for(int i = 0; i < goalDist.length; i++) {
                    goalDist[i] = Integer.MAX_VALUE;
                    blockDist[i] = Integer.MAX_VALUE;
		}
                
		List<BoardPosition> blocks = board.getBlockNodes();	
		HashMap<BoardPosition, List<BoardPosition>> reachMap = new HashMap<>(); 
                int b = 0; 
		
		for(BoardPosition block : blocks)
		{			
			if(board.getNode(block) == NodeType.BLOCK_ON_GOAL)
                            blockDist[b] =-50;
			else if(board.isInCorner(block)) {
                            return Integer.MIN_VALUE;		
			}
			b++;	
		}
		
		int i = 0;
		for(BoardPosition goal : board.getGoalNodes())
		{		
			reachMap.put(goal, new ArrayList<BoardPosition>());			
			
			if(board.getNode(goal) == NodeType.BLOCK_ON_GOAL) {
				goalDist[i] = 0;
			}
			
			b = 0; 
			for(BoardPosition block : blocks)
			{
				int dist = distanceMatrices.get(goal)[block.Row][block.Column];
				if(dist < Integer.MAX_VALUE)
				{
					reachMap.get(goal).add(block);
				}
				
				goalDist[i] = Math.min(dist, goalDist[i]);
				
				if(goalDist[i] > 0)
					blockDist[b] = Math.min(dist, blockDist[b++]);
			}			
			++i;
		}	
                
		
		if(Player.DO_BIPARTITE_MATCHING && bipartiteMatcher.maxBipartiteMatchCount(reachMap, board) < board.getGoalNodes().size())
                    return Integer.MIN_VALUE;
                
		int val = 0;
		for(i = 0; i < board.getGoalNodes().size(); ++i) {
			if(goalDist[i] == Integer.MAX_VALUE || blockDist[i] == Integer.MAX_VALUE)
			{
				return Integer.MIN_VALUE;
			}
			
			val -= goalDist[i];
			val -= blockDist[i];
		}

             /*   if(Player.DO_CORRAL_LIVE_DETECTION)
                {
                    List<CorralArea> l = LiveAnalyser.getAreas(board);
                    if(l != null && l.size() > 1)
                    {
                        blocks = new ArrayList<>();
                        for(CorralArea a: l)
                        {
                            if(a.isCorralArea())
                            {
                                if(!a.getFencePositions().contains(pushedBlock))
                                {
                                    val -= 1000;
                                }
                            }
                        }
                    }
                }*/
		
                //int val = 0;
                /*List<BoardPosition> goals = (List) GoalQueue;
                
                int weightIncrement = 100;
                int penaltyWeight = goals.size()*weightIncrement;
                for(BoardPosition goal: goals)
                {
                    //BoardPosition block = board.getBlockNodes().get(GoalToBlockIndexMap.get(goal));
                    if(board.getNode(goal) != NodeType.BLOCK_ON_GOAL)
                    {
                        penaltyWeight -= penaltyWeight;
                    }
                    penaltyWeight -= weightIncrement;
                }
                */
                
                int blockLastPushedIndex = board.getBlockLastMovedIndex();
                // If not -1, then a block has been pushed sometime ago - lets prioritize it!
                if(blockLastPushedIndex != -1)
                {
                    BoardPosition block = board.getBlockNodes().get(blockLastPushedIndex);
                    if(!pushedBlock.equals(block))
                    {
                        val -= 500;
                    }
                }

                //try {Thread.sleep(1000000);}catch(InterruptedException ex){}
                
		return val;
	}
        
	
	private boolean has4x4Block(BoardState board) {
		
		for(int row = 0; row < board.getRowsCount() - 1; ++row)
			mainloop:
			for(int col = 0; col < board.getColumnsCount() - 1; ++col) {
				
				if(!board.isBlockingNode(new BoardPosition(row, col)))
					continue;
				
				NodeType nodes[] = new NodeType[] {
					board.getNode(row, col),
					board.getNode(row, col+1),
					board.getNode(row+1, col),
					board.getNode(row+1, col+1)
				};
				
				
				
				boolean atLeastOneIsBlock = false;
				for(NodeType node : nodes)
				{
					if(!board.isBlockingNode(node))
						continue mainloop;
					
					atLeastOneIsBlock = atLeastOneIsBlock || node == NodeType.BLOCK;
				}
				
				if(atLeastOneIsBlock) {
					//System.out.println("found 4x4 block at " + row + " " + col);					
					return true;
				}
				
			}
		
		return false;		
	}
       
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");
		System.out.println(board);
		Analyser analyser = new Analyser(board);
		System.out.println(analyser);
		analyser.printDistanceMatrix(board);
		
		/*
		board = BoardState.getBoardFromFile("testing/disttest2");
		System.out.println(board);
		analyser = new Analyser(board);
		System.out.println(analyser);
		analyser.printDistanceMatrix(board);
		*/
	}
}
