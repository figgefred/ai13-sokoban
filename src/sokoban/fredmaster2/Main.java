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
		
                Player.DO_BIPARTITE_MATCHING = true;
                Player.DO_CORRAL_LIVE_DETECTION = true;
                Player.DO_DEADLOCKS_CONSTANTCHECK = true;
                Player.DO_DEADLOCKS_4x4 = true;
                Player.DO_GOAL_SORTING = false;
                
		//System.out.println(board);
		BoardState board = new BoardState(b, true);
		Settings settings = new Settings();
                Player noob = new Player(board, settings);
		System.out.println(noob.play());
	}

}
