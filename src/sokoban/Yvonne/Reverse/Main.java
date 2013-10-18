package sokoban.Yvonne.Reverse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Vector;

import sokoban.BoardPosition;

public class Main {
	private static BoardPosition initPlayerNode;

	/**
	 * @param args
	 */

	public static void main(String[] args) throws IOException, InterruptedException {
		//		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");

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
		initPlayerNode=board.getPlayerNode();			
		board=board.getEndingState();
		Player noob = new Player(board);

		Path path = noob.play();



		if(path != null)
		{
			board.movePlayer(path);
			//System.out.println("path "+path);
			//  System.out.println("toPlayerPath "+toPlayerPath);
			Path rightPath=path.cloneAndAppend(noob.toPlayerPath);

			Collections.reverse(rightPath.getPath());
			//Path rightPath = new Path(path.getPath(),true);
			//    System.out.println(board);
			System.out.println(rightPath);
		}
	}
}
