package tester;

public class TimingRunner implements Runnable {
	
	private long timetorun;
	private TestJob runnableToTime;
	private long timetaken;
	
	public TestJob getTimedRunnable() {
		return runnableToTime;
	}
	
	public long getTimeTaken() {
		return timetaken;
	}
	
	public TimingRunner(TestJob runnableToTime, long timetorun) {
		this.runnableToTime = runnableToTime;
		this.timetorun = timetorun;
	}

	@Override
	public void run() {
		long starttime = System.currentTimeMillis();
		Thread thread = new Thread(runnableToTime);
		thread.start();
		while((timetaken = System.currentTimeMillis() - starttime) < timetorun) {			
			if(!runnableToTime.isRunning)
			{
				System.out.print("+");
				return;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("- " + timetaken + " " + runnableToTime.message);
		runnableToTime.StaphPLz();
	}
	
}
