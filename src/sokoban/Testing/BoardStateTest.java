package sokoban.Testing;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import sokoban.BoardState;
import sokoban.types.NodeType;

public class BoardStateTest {
	
	private BoardState getBoardFromFile(String filename) throws IOException
	{
		FileReader rawInput = new FileReader(filename);
		BufferedReader br = new BufferedReader(rawInput);
		
		List<String> buffer = new ArrayList<>();
		
		while(true)
		{
			String tmp = br.readLine();
			if(tmp == null)
				break;
			buffer.add(tmp);			
		}
		br.close();
	
		
		return new BoardState(buffer);
	}

	/*
	 * Testing using the following board:
	 * ########
	   #   #+.#
	   #   *$.#
	   ####   #
   	      #@ ##
   	      ####
	 */	
	@Test
	public void testBasicParsing() throws IOException {
		BoardState board = getBoardFromFile("testing/parsetest");
		assertEquals(NodeType.WALL,board.getNode(0, 0));
		assertEquals(NodeType.SPACE, board.getNode(1, 1));
		assertEquals(NodeType.BLOCK_ON_GOAL, board.getNode(2, 4));
		assertEquals(NodeType.BLOCK, board.getNode(2, 5));
		assertEquals(NodeType.PLAYER_ON_GOAL, board.getNode(1, 5));
		assertEquals(NodeType.GOAL, board.getNode(2, 6));
		assertEquals(NodeType.PLAYER, board.getNode(4, 4));		
	}
	
	@Test
	public void testBasicMovement() throws IOException {		
		BoardState board = getBoardFromFile("testing/movetest1");
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		System.out.println(board.toString());
		
		board.movePlayerTo(3,4);
		assertEquals(NodeType.PLAYER, board.getNode(3, 4));
		assertEquals(NodeType.SPACE, board.getNode(2, 4));
		
		System.out.println(board.toString());
		
		board.movePlayerTo(3,3);
		System.out.println(board.toString());
		assertEquals(NodeType.PLAYER, board.getNode(3, 3));
		assertEquals(NodeType.SPACE, board.getNode(3, 4));
		
		board.movePlayerTo(3,4);
		System.out.println(board.toString());
		assertEquals(NodeType.PLAYER, board.getNode(3, 4));
		assertEquals(NodeType.SPACE, board.getNode(3, 3));		
		
		board.movePlayerTo(2,4);
		System.out.println(board.toString());
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		assertEquals(NodeType.SPACE, board.getNode(3, 4));	
	}
	
	@Test
	public void testPushMovement() throws IOException {
		BoardState board = getBoardFromFile("testing/pushtest1");		
		assertEquals(NodeType.PLAYER, board.getNode(3, 4));
		System.out.println(board.toString());
		
		board.movePlayerTo(2,4);
		System.out.println(board.toString());
		assertEquals(NodeType.BLOCK_ON_GOAL, board.getNode(1, 4));
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		assertEquals(NodeType.SPACE, board.getNode(3, 4));
		
		board.movePlayerTo(3,4);
		System.out.println(board.toString());
		board.movePlayerTo(4,4);
		System.out.println(board.toString());
		assertEquals(NodeType.BLOCK, board.getNode(5, 4));
		assertEquals(NodeType.PLAYER, board.getNode(4, 4));
		assertEquals(NodeType.SPACE, board.getNode(3, 4));
		assertEquals(NodeType.SPACE, board.getNode(2, 4));
		
		board.movePlayerTo(3,4);
		System.out.println(board.toString());
		board.movePlayerTo(3,3);
		System.out.println(board.toString());
		assertEquals(NodeType.BLOCK, board.getNode(3, 2));
		assertEquals(NodeType.PLAYER, board.getNode(3, 3));
		assertEquals(NodeType.SPACE, board.getNode(3, 4));
		assertEquals(NodeType.SPACE, board.getNode(2, 4));
		assertEquals(NodeType.SPACE, board.getNode(4, 4));
		
		board.movePlayerTo(3,4);
		System.out.println(board.toString());
		board.movePlayerTo(3,5);
		System.out.println(board.toString());
		assertEquals(NodeType.BLOCK_ON_GOAL, board.getNode(3, 6));
		assertEquals(NodeType.PLAYER, board.getNode(3, 5));
		assertEquals(NodeType.SPACE, board.getNode(3, 4));
		assertEquals(NodeType.SPACE, board.getNode(2, 4));
		assertEquals(NodeType.SPACE, board.getNode(4, 4));
		assertEquals(NodeType.SPACE, board.getNode(3, 3));

	}
	
	@Test(expected= IllegalArgumentException.class) 
	public void testBadMovementWall() throws IOException
	{		
		BoardState board = getBoardFromFile("testing/badmovetest");
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		board.movePlayerTo(2, 3);
		System.out.println(board.toString());
		fail("should have thrown exception");
	}
	
	@Test(expected= IllegalArgumentException.class) 
	public void testBadMovementBlockPush() throws IOException
	{		
		BoardState board = getBoardFromFile("testing/badmovetest");
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		board.movePlayerTo(3, 4);
		System.out.println(board.toString());
		fail("should have thrown exception");
	}
	
	@Test(expected= IllegalArgumentException.class) 
	public void testBadMovementBlockPush2() throws IOException
	{		
		BoardState board = getBoardFromFile("testing/badmovetest");
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		board.movePlayerTo(1, 4);
		System.out.println(board.toString());
		fail("should have thrown exception");
	}
	
	@Test(expected= IllegalArgumentException.class) 
	public void testBadMovementBlockPush3() throws IOException
	{		
		BoardState board = getBoardFromFile("testing/badmovetest");
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		board.movePlayerTo(2, 5);
		System.out.println(board.toString());
		fail("should have thrown exception");
	}
}
