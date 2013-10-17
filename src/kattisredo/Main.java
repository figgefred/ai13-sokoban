package kattisredo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Main {

	/**
	 * @param args
	 */

	public static void main(String[] args) throws IOException, InterruptedException {
		//BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest");

		Vector<String> b = new Vector<String>();

		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		
		boolean STATS = true;

		String line;
		line = br.readLine();
		
		/*
		while(line != null)
		{
			if(line.equals(""))
				continue;
			b.add(line);
			line = br.readLine();
		}*/
		
		while(br.ready()) {
			line = br.readLine();
			b.add(line);
		}

		BoardState board = new BoardState(b, true);
		Player noob = new Player(board);
		
		if(STATS) {
			long startTime = System.currentTimeMillis();
			noob.play();
			long endTime = System.currentTimeMillis();
			
			System.out.println("Time: " + (endTime - startTime) + " ms");
			
		} else {
			noob.play();
		}
	}
}
