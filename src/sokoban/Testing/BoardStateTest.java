package sokoban.Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import sokoban.Tethik.BoardState;
import sokoban.types.NodeType;

public class BoardStateTest {
	


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
		BoardState board = BoardState.getBoardFromFile("testing/parsetest");
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
		BoardState board = BoardState.getBoardFromFile("testing/movetest1");
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
		BoardState board = BoardState.getBoardFromFile("testing/pushtest1");		
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
	
	@Test
	public void testHashMovePlayerFunction() throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/hash/test1");
		BoardState board2 = BoardState.getBoardFromFile("testing/hash/test2", false);
		
		System.out.println("Flyttar spelaren till 3,3");
		board.movePlayerTo(3, 3);
		
		System.out.println("Flytta spelaren till 3,2");
		board.movePlayerTo(3, 2);
		
		assertEquals(board.hashCode(), board2.hashCode());
		
	}
	
	@Test
	public void testHashPushBlockFunction() throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/hash/test1");
		BoardState board2 = BoardState.getBoardFromFile("testing/hash/test3", false);
		BoardState board3 = BoardState.getBoardFromFile("testing/hash/test4", false);
		BoardState board4 = BoardState.getBoardFromFile("testing/hash/test5", false);
		BoardState board5 = BoardState.getBoardFromFile("testing/hash/test6", false);
		
		board.movePlayerTo(4, 4);
		
		System.out.println("Board 1 : " + board.hashCode());
		System.out.println("Board 2 : " + board2.hashCode());
		
		assertEquals(board.hashCode(), board2.hashCode());
		
		board.movePlayerTo(3, 4);
		assertEquals(board.hashCode(), board3.hashCode());
		
		board.movePlayerTo(2, 4);
		assertEquals(board.hashCode(), board4.hashCode());
		
		board.movePlayerTo(3, 4);
		board.movePlayerTo(3, 5);
		assertEquals(board.hashCode(), board5.hashCode());
	}
	
	@Test(expected= IllegalArgumentException.class) 
	public void testBadMovementWall() throws IOException
	{		
		BoardState board = BoardState.getBoardFromFile("testing/badmovetest");
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		board.movePlayerTo(2, 3);
		System.out.println(board.toString());
		fail("should have thrown exception");
	}
	
	@Test(expected= IllegalArgumentException.class) 
	public void testBadMovementBlockPush() throws IOException
	{		
		BoardState board = BoardState.getBoardFromFile("testing/badmovetest");
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		board.movePlayerTo(3, 4);
		System.out.println(board.toString());
		fail("should have thrown exception");
	}
	
	@Test(expected= IllegalArgumentException.class) 
	public void testBadMovementBlockPush2() throws IOException
	{		
		BoardState board = BoardState.getBoardFromFile("testing/badmovetest");
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		board.movePlayerTo(1, 4);
		System.out.println(board.toString());
		fail("should have thrown exception");
	}
	
	@Test(expected= IllegalArgumentException.class) 
	public void testBadMovementBlockPush3() throws IOException
	{		
		BoardState board = BoardState.getBoardFromFile("testing/badmovetest");
		
		board.movePlayerTo(2, 5);
		System.out.println(board.toString());
		fail("should have thrown exception");
	}
	
	@Test
	public void testClone() throws IOException {
		BoardState board = BoardState.getBoardFromFile("testing/movetest1");
		assertEquals(NodeType.PLAYER, board.getNode(2, 4));
		
		BoardState board2 = (BoardState) board.clone();
		assertEquals(board.hashCode(),board2.hashCode());
		assertEquals(board.getGoalNodes(), board2.getGoalNodes());
		board2.movePlayerTo(2, 3);
		
		System.out.println(board2.getPlayerNode());
		System.out.println(board.getPlayerNode());
		System.out.println(board2);
		System.out.println(board);
		assertTrue(!board.toString().equals(board2.toString()));
		assertNotSame(board2.getPlayerNode(), board.getPlayerNode());
		assertNotSame(board.hashCode(),board2.hashCode());
		
	}
}
