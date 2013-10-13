package sokoban.Tethik;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sokoban.BoardPosition;
import sokoban.NodeType;

/**
 * Kommer inte på någon vettig abstraktion för deadlocks än. Så hårdkodade mönster får hjälpa till lite istället...
 * @author tethik
 *
 */
public class DeadlockFinder {

	//private PathFinder pathFinder = new PathFinder();
	
	private List<DeadlockPattern> patterns = new ArrayList<DeadlockPattern>();
	
	public DeadlockFinder() {
		for(int i = 1; i < 14; ++i) {
			try {
				patterns.add(DeadlockPattern.getPatternFromFile("deadlockpatterns/"+i));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public boolean isDeadLock(BoardState board) {
		boolean isDeadlock = false;
		
		int i = 1;
		for(DeadlockPattern patt : patterns) {
			boolean matched = patt.isMatch(board);
			System.out.println(i++ + " " + matched);			
			isDeadlock |= matched;
		}
		
		return isDeadlock;
	}
	
	/***
	 * Letar efter $$
	 *             $$ eller liknande fast med väggar.
	 * @param board
	 * @return
	 */
	/*
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
	*/
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/deadlocktest9");
		System.out.println(board);
		DeadlockFinder analyser = new DeadlockFinder();
		System.out.println(analyser.isDeadLock(board));
	}
	
}
