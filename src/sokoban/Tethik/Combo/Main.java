package sokoban.Tethik.Combo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Main {

	/**
	 * @param args
	 */

	public static void main(String[] args) throws IOException, InterruptedException {
//		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");
//		
		Vector<String> b = new Vector<String>();
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		
		String line;
		while(br.ready()) {
			line = br.readLine();
			b.add(line);
		} // End while
		
		BoardState board = new BoardState(b);
		
		Player noob = new Player(board, new Settings());
		System.out.println(noob.play());
	}

}
