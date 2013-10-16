package sokoban.fredmaster2;

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
		
		Vector<String> b = new Vector<String>();
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		
		String line;
                line = br.readLine();
		while(line != null) {
                    if(line.equals(""))
                        break;
                    b.add(line);
                    line = br.readLine();
		} // End while
		
                Player.DO_GOAL_SORTING = false;
                Player.DO_DEADLOCKS_4x4 = true;
                Player.DO_DEADLOCKS_CHECKS = true;
                Move.CORRAL_LIVE_DETECTION = true;
                
		//System.out.println(board);
		BoardState board = new BoardState(b, true);
		Player noob = new Player(board);
		System.out.println(noob.play());
	}

}
