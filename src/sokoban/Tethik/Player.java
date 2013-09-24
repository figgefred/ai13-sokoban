package sokoban.Tethik;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import sokoban.BoardPosition;
import sokoban.BoardState;
import sokoban.Path;
import sokoban.Algorithms.AStar_Path;
import sokoban.Algorithms.ISearchAlgorithmPath;

public class Player {
	
	private ISearchAlgorithmPath pathfinder = new AStar_Path();
	private int BEGIN_DEPTH = 1; // for iterative deepening	
	private int MAX_DEPTH = 50;
	private BoardState initialState;
	
	public Player(BoardState initialState)
	{		
		this.initialState = initialState;
	}
	
	public Path searchForGlory(BoardState board, Path currentPath, int depthleft) {
		//System.out.println(board);
		//System.out.println(currentPath);
		
		if(board.isWin()) {
			System.out.println(board);		
			System.out.println(board.getGoalNodes().size());
			return currentPath;
		}
	
		if(depthleft < 0)
			return null;
		
		List<BoardPosition> blocks = board.getBlockNodes();
		BoardPosition playerPos = board.getPlayerNode();
		
		for(BoardPosition blockPos : blocks)
		{
			// hitta ställen man kan göra förflyttningar av block.
			// skriva om sen..
			List<BoardPosition> pushPositions = board.getPushingPositions(blockPos);
			List<BoardPosition> neighbours = board.getNeighbours(blockPos);
			// srsly, skriva om sen xD
			List<BoardPosition> candidates = new ArrayList<BoardPosition>();
			
			for(BoardPosition pushpos : pushPositions) {
				if(neighbours.contains(pushpos))
					candidates.add(pushpos);
			}
			
			// now do pathfinding to see if player can reach it..
			for(BoardPosition candidate : candidates)
			{
				Path toPush;
				if(candidate.equals(playerPos))
					toPush = new Path();
				else
					toPush = pathfinder.getPath(board, playerPos, candidate);		
				
				if(toPush == null) // no path found
					continue;				
				
				toPush.getPath().remove(playerPos); // remove duplicate player pos (was causing null)
				toPush.append(blockPos);
				
				BoardState newBoard = (BoardState) board.clone();
				// move the player along the path.
				newBoard.movePlayer(toPush);
				// push the block by moving towards the block.
				newBoard.movePlayerTo(blockPos);
				
				
				Path result = searchForGlory(newBoard, currentPath.cloneAndAppend(toPush), depthleft-1);
				if(result != null) {
					return result;
				}
			}			
			
		}
		
		return null;
	}
	
	public void play() {
	
		for(int depth = BEGIN_DEPTH; depth < MAX_DEPTH; depth++)
		{
			System.out.println("Searching at depth " + depth + "...");
			Path answer = searchForGlory(initialState, new Path(), depth);
			if(answer != null) {			
				System.out.println(answer);
				break;
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest2");
		System.out.println(board);
		Player noob = new Player(board);
		noob.play();
		
	
		
	}

}
