package test;

import java.util.ArrayList;
import java.util.List;

import java_async.Task;
import java_async.TaskImpl;

public class TestAsyncClasses {
	private static final long MAIN_THREAD_ID = Thread.currentThread().getId();
	private static BgThread bgThread = new BgThread();
	
	public static void start() {
		System.out.println("main thread id "+Thread.currentThread().getId());
		
		
	}
	
	public static Task<Integer> cancelableTask(boolean shouldFail){
		TaskImpl<Integer> asyncResult = new TaskImpl<>();
		asyncResult.addOnCanceledListener(()-> keepRunningFor = 0);
		
		bgThread.execute(()->{
			while(keepRunningFor-->0);
			if(!asyncResult.isCanceled()) {
				if(!shouldFail)
					asyncResult.setResult(69);
				else
					asyncResult.setFailure(new RuntimeException("some error occurred"));
			}
		});
		
		
		return asyncResult;
	}
	
	public static Task<List<Integer>> getDataFromCloudAsync(boolean shouldFail){
		TaskImpl<List<Integer>> asyncResult = new TaskImpl<>();
		
		bgThread.execute(() -> {
			System.out.print("getting data from cloud on "+(Thread.currentThread().getId()==MAIN_THREAD_ID ? "main" : "background")+" thread("+Thread.currentThread().getId()+")...");
			try {Thread.sleep(3000);} catch(InterruptedException e) {e.printStackTrace();}
			List<Integer> data = new ArrayList<>();
			System.out.println("done");
			if(!shouldFail)
				asyncResult.setResult(data);
			else
				asyncResult.setFailure(new RuntimeException("some error occurred while getting data from cloud"));
		});
		
		return asyncResult;
	}

	public static Task<Void> updateLocalStorageAsync(boolean shouldFail){
		TaskImpl<Void> asyncResult = new TaskImpl<>();
		
		bgThread.execute(()->{
			System.out.print("updating local storage on "+(Thread.currentThread().getId()==MAIN_THREAD_ID ? "main" : "background")+" thread("+Thread.currentThread().getId()+")...");
			try {Thread.sleep(3000);} catch(InterruptedException e) {e.printStackTrace();}
			System.out.println("done");
			if(!shouldFail)
				asyncResult.setResult(null);
			else
				asyncResult.setFailure(new RuntimeException("some error occurred while updating local storage"));
		});
		
		return asyncResult;
	}

	public static Task<List<Integer>> getDataFromCloudSync(boolean shouldFail){
		TaskImpl<List<Integer>> asyncResult = new TaskImpl<>();
		
		System.out.print("getting data from cloud on "+(Thread.currentThread().getId()==MAIN_THREAD_ID ? "main" : "background")+" thread("+Thread.currentThread().getId()+")...");
		try {Thread.sleep(3000);} catch(InterruptedException e) {e.printStackTrace();}
		List<Integer> data = new ArrayList<>();
		System.out.println("done");
		if(!shouldFail)
			asyncResult.setResult(data);
		else
			asyncResult.setFailure(new RuntimeException("some error occurred while getting data from cloud"));
		
		return asyncResult;
	}

	public static Task<Void> updateLocalStorageSync(boolean shouldFail){
		TaskImpl<Void> asyncResult = new TaskImpl<>();
	
		System.out.print("updating local storage on "+(Thread.currentThread().getId()==MAIN_THREAD_ID ? "main" : "background")+" thread("+Thread.currentThread().getId()+")...");
		try {Thread.sleep(3000);} catch(InterruptedException e) {e.printStackTrace();}
		System.out.println("done");
		if(!shouldFail)
			asyncResult.setResult(null);
		else
			asyncResult.setFailure(new RuntimeException("some error occurred while updatin local storage"));
		
		return asyncResult;
	}

	public static Void refreshUISync(boolean shouldFail) {
		System.out.print("refreshing ui on "+(Thread.currentThread().getId()==MAIN_THREAD_ID ? "main" : "background")+" thread("+Thread.currentThread().getId()+")...");
		try {Thread.sleep(3000);} catch(InterruptedException e) {e.printStackTrace();}
		System.out.println("done");
		if(shouldFail)
			throw new RuntimeException("some error occurred while refreshing ui");
		return null;
	}

	//variable to test cancelable task
	private static volatile int keepRunningFor = 500000000;
}
