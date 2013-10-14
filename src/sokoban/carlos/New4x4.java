package sokoban.carlos;

import java.io.IOException;
import java.util.List;

import sokoban.BoardPosition;
import sokoban.NodeType;

public class New4x4 {
	
	public New4x4(BoardState board) {
		System.out.println(this.has4x4Block(board));
		System.out.println(this.has4x4Block2(board));
	}
	
	private boolean has4x4Block(BoardState board, BoardPosition block) {
		
		int row = block.Row;
		int col = block.Column;
		
		NodeType nodes[] = new NodeType[] {
			//board.getNode(row, col), We know this one is block
			board.getNode(row, col+1),
			board.getNode(row+1, col),
			board.getNode(row+1, col+1)
		};		
		
		boolean atLeastOneIsBlock = false;
		for(NodeType node : nodes)
		{
			if(!board.isBlockingNode(node))
				return false;
			
			atLeastOneIsBlock = atLeastOneIsBlock || node == NodeType.BLOCK;
		}
		
		if(atLeastOneIsBlock) {
			System.out.println("found 4x4 block at " + row + " " + col);
			return true;
		}
		
		return false;
	}
	
	private boolean has4x4Block2(BoardState board) {
		List<BoardPosition> blocks = board.getBlockNodes();
		
		for(BoardPosition block: blocks) {
			if(has4x4Block(board, block))
				return true;
		}
		/*
		List<BoardPosition> blocks = board.getBlockNodes();
		
		mainloop:
		for(BoardPosition block: blocks) {
			int row = block.Row;
			int col = block.Column;
			
			NodeType nodes[] = new NodeType[] {
				//board.getNode(row, col), We know this one is block
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
		}*/
		
		return false;
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
					System.out.println("found 4x4 block at " + row + " " + col);					
					return true;
				}
				
			}
		
		return false;		
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/deadlocktest1");
		System.out.println(board);
		New4x4 analyser = new New4x4(board);

	}

}
