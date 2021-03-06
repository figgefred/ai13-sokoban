package kattisredo;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;


public class PathFinder {
    
	private int playerDistMatrix[][];
	private int board_hash = 0;
	public static int COUNT = 0;
	
    public PathFinder() {
        
    }

	private void mapPlayerDistance(BoardState board) {
		playerDistMatrix = new int[board.getRowsCount()][board.getColumnsCount()];
		Queue<BoardPosition> positions = new LinkedList<>();
		Queue<Integer> distances = new LinkedList<Integer>();
		Set<BoardPosition> visited = new HashSet<>();

		BoardPosition player = board.getPlayerNode();

		positions.add(player);
		distances.add(0);
		visited.add(player);

		playerDistMatrix[player.Row][player.Column] = 0;

		for(int i = 0; i < board.getRowsCount(); ++i)
			for(int y = 0; y < board.getColumnsCount(); ++y)
				playerDistMatrix[i][y] = Integer.MAX_VALUE;

		// BFS
		while(!positions.isEmpty()) {

			BoardPosition pos = positions.poll();
			int distance = distances.poll();

			// Uppdatera positions i matrisen
			playerDistMatrix[pos.Row][pos.Column] = distance;

			++distance;				

			for(BoardPosition neighbour : board.getNeighbours(pos)) {
				if(visited.contains(neighbour)) 
					continue;

				NodeType node = board.getNode(neighbour);
				visited.add(neighbour);

				if(node == NodeType.WALL || node == NodeType.INVALID || node == NodeType.BLOCK || node == NodeType.BLOCK_ON_GOAL) 
					continue;

				positions.add(neighbour);
				distances.add(distance);
			}
		}

		/*
		for(int i = 0; i < board.getRowsCount(); ++i) {
			for(int j = 0; j < board.getColumnsCount(); j++) {
				System.out.print((playerDistMatrix[i][j] == Integer.MAX_VALUE) ? "#" : playerDistMatrix[i][j] % 10);
			}
			System.out.print("\n");
		}
		*/
		
		this.board_hash = board.hashCode();
	}
	
	public boolean isReachable(BoardState board, BoardPosition goal) {
		if(board_hash != board.hashCode())
			mapPlayerDistance(board);
		
		return (playerDistMatrix[goal.Row][goal.Column] < Integer.MAX_VALUE);			
	}

	public Path getPath(BoardState board, BoardPosition goal) {
		if(board_hash != board.hashCode())
			mapPlayerDistance(board);
		
		if(!isReachable(board, goal))
			return null;		
		
		//System.err.println("watassdsa");
		
		Deque<BoardPosition> positionStack = new ArrayDeque<BoardPosition>();		
		positionStack.add(goal);
		
		BoardPosition pos;
		traversal:
		while(!(pos = positionStack.peek()).equals(board.getPlayerNode())) {
			
			int distance = playerDistMatrix[pos.Row][pos.Column];
			
			for(BoardPosition neighbour : board.getNeighbours(pos))
			{			
				if(playerDistMatrix[neighbour.Row][neighbour.Column] < distance)
				{
					positionStack.push(neighbour);					
					continue traversal;					
				}				
			}	
			
			return null; // Should not happen!
		}
		
		//positionStack.pop();
		
		return new Path(positionStack);
	}
		
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest");
		System.out.println(board);
		PathFinder pathfinder = new PathFinder();
		Path path = pathfinder.getPath(board, new BoardPosition(2, 1));	
		System.out.println(path);
		board.movePlayer(path);
		System.out.println(board);
	}
    

}
