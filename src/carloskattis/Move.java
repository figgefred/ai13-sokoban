package carloskattis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Move implements Comparable<Move> {
	private PathFinder pathfinder = new PathFinder();
	private Analyser analyser = null;
	public BoardState board;
	public Path path;
	private Integer heuristic_value = null;
	public int pushes = 1;
	
	// Testa spara path
	private static int TEST = 0;
	// Save Moves - BoardState.hashCode to List->Move
	private static HashMap<Integer, HashMap<BoardPosition, Path>> movesMap
		= new HashMap<Integer, HashMap<BoardPosition, Path>>();


	public Move(Analyser analyser, PathFinder pathfinder) {
		this.pathfinder = pathfinder;
		this.analyser = analyser;
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
		heuristic_value = analyser.getHeuristicValue(board);
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
				boolean isInMap = false;
				Path toPush = null;

				// Check map
				if(movesMap.containsKey(board.hashCode())) {
					toPush = movesMap.get(board.hashCode()).get(candidate);
					if(toPush != null) {
//						System.out.println("Hämtade: " + toPush + ", för candidate: " + candidate );

						isInMap = true;
					}
				} else {
					movesMap.put(board.hashCode(), new HashMap<BoardPosition, Path>());
				}
				
				if(candidate.equals(playerPos)) {
					toPush = new Path(candidate);
				}
				else {
					if(!isInMap) {
						toPush = pathfinder.getPath(board, candidate);
						
						if(toPush == null)
							continue;
						
						//TODO var appenda?
						//toPush.append(blockPos);
						movesMap.get(board.hashCode()).put(candidate, toPush);
//						System.out.println(movesMap);
//						System.out.println("Lägger till: " + toPush + ", för candidate: " + candidate);
					}
				}

				if(toPush == null) // no path found
					continue;
				
				//if(!isInMap)
					//toPush.append(blockPos);
				
				BoardState newBoard = (BoardState) board.clone();
				// move the player along the path.
				newBoard.movePlayer(toPush);
				// push the block by moving towards the block.
				newBoard.movePlayerTo(blockPos);

				Move move = new Move(analyser, pathfinder);
				move.board = newBoard;
				move.path = path.cloneAndAppend(toPush.cloneAndAppend(blockPos));
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

	@Override
	public int compareTo(Move o) {			
		if(o.getHeuristicValue() > this.getHeuristicValue())
			return 1;
		else if(o.getHeuristicValue() < this.getHeuristicValue())
			return -1;
		return 0;
	}
}
