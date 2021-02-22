package test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BgThread implements Executor{
	private Executor executor;
	private int users = 0;
	
	public BgThread() {
		this.executor = Executors.newSingleThreadExecutor();
	}
	
	public synchronized void execute(final Runnable command) {
		users++;
		executor.execute(()->{
			command.run();
			users--;
			if(users < 1) {
				this.executor = null;
				System.gc();
			}
		});
	}
}
