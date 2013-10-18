package sokoban.Tethik.Combo;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Player {
	
	static final ExecutorService threadPool = Executors.newSingleThreadExecutor();
	
	public volatile Path path = null;
	public volatile boolean done = false;
	public Settings settings = new Settings();
	
	public volatile boolean shouldStop = false;
	
	public void stopThreads() {
		ida.shouldStop = true;
		bfs.shouldStop = true;
	}
	
	
	public IDAPlayer ida;
	public BFSPlayer bfs;
	
	public Player(BoardState initial, Settings settings) {
		this.settings = settings;
		ida = new IDAPlayer(initial, settings);
		bfs = new BFSPlayer(initial, settings);
	}
	
	public void doIdaPlay() {
		Path path = ida.play();	
		if(path != null) {
			bfs.shouldStop = true;
			this.path = path;
		}
			
	}
	
	public void doBfsPlay() {
		Path path = bfs.play();		
		if(path != null) {
			ida.shouldStop = true;
			this.path = path;
		}
	}
	
	public Path play() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				doIdaPlay();				
			}
		};
		
		doBfsPlay();
			
		return path;
	}
	
	
	public static void main(String[] args) throws IOException {
		BoardState board = BoardState.getBoardFromFile("test100/test002.in");
//		BoardState board = BoardState.getBoardFromFile("testing/simpleplaytest4");
		
		long timeStart = System.currentTimeMillis();		
		System.out.println(board);
		Settings settings = new Settings();
//		settings.VERBOSE = true;
		Player noob = new Player(board, settings);		
		Path path = noob.play();
		long timeStop = System.currentTimeMillis();
		System.out.println(path);
		if(path != null)
			board.movePlayer(path);
		System.out.println(board);

		System.out.println("Time: " + (timeStop - timeStart) + " ms");
	}
	
}
