package tester;

import java.io.IOException;

import sokoban.Tethik.BoardState;
import sokoban.Tethik.Path;
import sokoban.Tethik.Player;
import sokoban.Tethik.IDAPlayer;


public class TestJob implements Runnable {

	private String filename;
	public volatile boolean result = false;
	public String message = "";
	public volatile boolean isRunning = true;
//	public Player play0r;
//	public IDAPlayer play0r;
	public sokoban.fredmaster2.Player play0r;
	
	public TestJob(String filename) {
		this.filename = filename;
	}
	
	public void StaphPLz() {
		play0r.shouldStop = true;
	}
	
	@Override
	public void run() {
		isRunning = true;
		sokoban.fredmaster2.BoardState board = null;
//		BoardState board = null;
		try {
			board = sokoban.fredmaster2.BoardState.getBoardFromFile(filename, false);
//			board = BoardState.getBoardFromFile(filename, false);
		} catch (IOException e) {
			result = false;
			message = "File not found: " + filename;
			return;
		}
		
		try {
//			play0r = new Player(board);
//			play0r = new IDAPlayer(board);
			play0r = new sokoban.fredmaster2.Player(board);
			sokoban.fredmaster2.Path path = play0r.play();
//			Path path = play0r.play();
			if(path != null) {			
				board.movePlayer(path);
				result = board.isWin();
				if(!result)
					message = "Finished, but wrong path: " + path.toString();
			} else if(!play0r.shouldStop) {
				result = board.isWin();
				message = "Path is null?!";
			} else {
				result = false;
				message = "Timeout!";
			}
		} catch(IllegalArgumentException ex) {
			result = false;			
			message = "Wrong path?";
		} catch (Exception e) {
			result = false;
			message = "Error: " + e.toString();
			e.printStackTrace();
		}
		 
		isRunning = false;
	}
	

}
