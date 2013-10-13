package tester;

import java.io.IOException;

import sokoban.Tethik.BoardState;
import sokoban.Tethik.Path;
import sokoban.Tethik.Player;

public class TestJob implements Runnable {

	private String filename;
	public volatile boolean result = false;
	public String message = "";
	public volatile boolean isRunning = true;
	public Player play0r;
	
	public TestJob(String filename) {
		this.filename = filename;
	}
	
	public void StaphPLz() {
		play0r.shouldStop = true;
	}
	
	@Override
	public void run() {
		isRunning = true;
		BoardState board = null;
		try {
			board = BoardState.getBoardFromFile(filename, false);
		} catch (IOException e) {
			result = false;
			message = "File not found: " + filename;
			return;
		}
		
		try {
			play0r = new Player(board);
			Path path = play0r.play();
			if(path != null) {			
				board.movePlayer(path);
				result = true;
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
