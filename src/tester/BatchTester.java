package tester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import sokoban.Tethik.BoardState;
import sokoban.Tethik.IDAPlayer;
import sokoban.Tethik.Player;


public class BatchTester {
	
	private static int TIMELIMIT = 1000;
	//private static int NUMBER_OF_THREADS = 8;
	private static int NUMBER_OF_TESTS = 100;
	static final ExecutorService threadPool = Executors.newFixedThreadPool(4);
	

	public static void main(String[] args) throws InterruptedException {		
		
		List<TestJob> jobs = new ArrayList<TestJob>();
		List<TimingRunner> timers = new ArrayList<TimingRunner>();
		
		// Some statics..
		sokoban.fredmaster2.BoardState.initZobristTable(50, 50);
		BoardState.initZobristTable(50, 50);
		sokoban.fredmaster2.Player.VERBOSE = false;
		IDAPlayer.VERBOSE = false;
		Player.VERBOSE = false;
		
//		TestJob job = new TestJob("test100/test099.in");
//		TimingRunner runner = new TimingRunner(job, TIMELIMIT);
//		runner.run();
		

		for(int i = 0; i < NUMBER_OF_TESTS; ++i) {
			String filename = "test100/test0";
			if(i < 10)
				filename += "0";			
			filename += i + ".in";
			
			
			TestJob job =  new TestJob(filename);
			jobs.add(job);
			timers.add(new TimingRunner(job, TIMELIMIT));			
		}		
		
		for(Runnable command : timers)
			threadPool.execute(command);
		
		threadPool.shutdown();
		threadPool.awaitTermination(5000 * NUMBER_OF_TESTS, TimeUnit.MILLISECONDS);
		
		System.out.println();
		System.out.println();
		System.out.println();
		int sum_passed = 0;
		long sum_timetaken = 0;
		for(int i = 0; i < NUMBER_OF_TESTS; ++i) {
			TestJob job = jobs.get(i);
			TimingRunner timer = timers.get(i);
			
			String result = "#" + i + " " + (job.result ? "[PASS] " : "[FAIL] ");
			result += timer.getTimeTaken() + "ms ";
			
			if(job.result) {				
				sum_passed++;
			} else {
				result += job.message;
			}
			
			sum_timetaken += timer.getTimeTaken();

			System.out.println(result);								
		}
		
		System.out.println("Passed " + sum_passed + " out of " + NUMBER_OF_TESTS);
		System.out.println("Total time: " + sum_timetaken + "ms");
		System.out.println("Average time: " + sum_timetaken / NUMBER_OF_TESTS + "ms");
		
		
	}

}
