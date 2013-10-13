package sokoban.Yvonne.Reverse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Main {
long startTime;
	/**
	 * Read the board.
	 * Add the lines to vector b.
	 * 
	 * @param args
	 */

	public static void main(String[] args) throws IOException, InterruptedException {
//		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");
		long start = System.currentTimeMillis();

		Vector<String> b = new Vector<String>();
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		
		String line;
		
		while(br.ready()) {
			line = br.readLine();
			b.add(line);
		} // End while
		
		//System.out.println(board);
		BoardState board = new BoardState(b);
		
		Player player = new Player(board.getEndingState());
		player.play();
	}

}
