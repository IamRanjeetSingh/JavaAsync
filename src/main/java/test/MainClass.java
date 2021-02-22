package test;

import java.lang.ref.*;

import java_async.Observer;
import java_async.Task;
import java_async.TaskImpl;

public class MainClass {
	public static final String STATIC_FINAL_FIELD = "f";
	
	public static void main(String[] args) {
		Task<Integer> task = doWork(false);
		task.addOnCompleteListener(t -> {
			if(t.isSuccessful()) 
				System.out.println("Task Successful with result "+t.getResult());
			else 
				System.out.println("Task Unsuccessful with result "+t.getResult()+" with reason "+t.getFailure());
		});
	}
	
	public static Task<Integer> doWork(boolean shallSucceed) {
		TaskImpl<Integer> task = new TaskImpl<>();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(shallSucceed) {
			task.setResult(1);
		} else {
			task.setResult(-1);
			task.setFailure(new RuntimeException("Some internal error"));
		}
		
		return task;
	}
}
