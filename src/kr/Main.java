package kr;

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
		
                Player.VERBOSE = false;
                Player.DO_GOAL_SORTING = false;
                Player.DO_EXPENSIVE_DEADLOCK = true;
                Player.DO_CORRAL_LIVE_DETECTION = true;
                Player.DO_CORRAL_CACHING = true;
                Player.DO_TUNNEL_MACRO_MOVE = true;
                Player.DO_MOVE_CACHING = true;
                Player.CHEAT = true;
		//System.out.println(board);
		BoardState board = new BoardState(b, true);
                Settings settings = new Settings();
                settings.MOVE_DO_GOAL_MOVES = false;
                
                Player noob = new Player(board, settings);
		System.out.println(noob.play());
	}

}
