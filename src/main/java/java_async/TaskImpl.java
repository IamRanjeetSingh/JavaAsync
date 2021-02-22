package java_async;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

public class TaskImpl<T> implements Task<T> {
	private static final String EXCEPTION_INCOMPLETE = "Task is not complete yet";
	private static final String EXCEPTION_FAILED = "Task failed";
	private static final Executor CURRENT_THREAD_EXECUTOR = command -> command.run();
	
	private Queue<EventListener> eventListeners;
	
	private T result;
	private Exception failure;
	private boolean isComplete;
	private boolean isSuccessful;
	private boolean isCanceled;
	
	private Object lock = new Object();
	
	public TaskImpl() {
		this.eventListeners = new ArrayDeque<EventListener>();
	}
	
	@Override
	public Task<T> addOnCompleteListener(OnCompleteListener<T> completeListener) {
		addOnCompleteListener(completeListener, CURRENT_THREAD_EXECUTOR);
		return this;
	}

	@Override
	public Task<T> addOnCompleteListener(OnCompleteListener<T> completeListener, Executor executor) {
		addEventListener(() -> executor.execute(() -> completeListener.onComplete(this)));
		return this;
	}

	@Override
	public Task<T> addOnSuccessListener(OnSuccessListener<T> successListener) {
		addOnSuccessListener(successListener, CURRENT_THREAD_EXECUTOR);
		return this;
	}

	@Override
	public Task<T> addOnSuccessListener(OnSuccessListener<T> successListener, Executor executor) {
		addEventListener(() -> executor.execute(() -> {
			if(isSuccessful)
				successListener.onSuccess(getResult());
		}));
		return this;
	}

	@Override
	public Task<T> addOnFailureListener(OnFailureListener<T> failureListener) {
		addOnFailureListener(failureListener, CURRENT_THREAD_EXECUTOR);
		return this;
	}

	@Override
	public Task<T> addOnFailureListener(OnFailureListener<T> failureListener, Executor executor) {
		addEventListener(() -> executor.execute(() -> {
			if(!isSuccessful && !isCanceled)
				failureListener.onFailure(getFailure());
		}));
		return this;
	}

	@Override
	public Task<T> addOnCanceledListener(OnCanceledListener canceledListener) {
		addOnCanceledListener(canceledListener, CURRENT_THREAD_EXECUTOR);
		return this;
	}

	@Override
	public Task<T> addOnCanceledListener(OnCanceledListener canceledListener, Executor executor) {
		addEventListener(() -> executor.execute(() -> {
			if(isCanceled)
				canceledListener.onCanceled();
		}));
		return this;
	}

	@Override
	public <TContinuationResult> Task<TContinuationResult> continueWith(
			Continuation<T, TContinuationResult> continuation) {
		return continueWith(continuation, CURRENT_THREAD_EXECUTOR);
	}

	@Override
	public <TContinuationResult> Task<TContinuationResult> continueWith(
			Continuation<T, TContinuationResult> continuation, Executor executor) {
		
		TaskImpl<TContinuationResult> newTask = new TaskImpl<TContinuationResult>();
		
		this.addOnCompleteListener(task -> executeContinuation(continuation, newTask, executor), executor);
		
		return newTask;
	}

	@Override
	public <TContinuationResult> Task<TContinuationResult> continueWithTask(
			Continuation<T, Task<TContinuationResult>> continuation) {
		return continueWithTask(continuation, CURRENT_THREAD_EXECUTOR);
	}

	@Override
	public <TContinuationResult> Task<TContinuationResult> continueWithTask(
			Continuation<T, Task<TContinuationResult>> continuation, Executor executor) {
		
		TaskImpl<TContinuationResult> newTask = new TaskImpl<>();
		
		this.addOnCompleteListener(task -> executeContinuationWithTask(continuation, newTask, executor), executor);
		
		return newTask;
	}
	
	@Override
	public <TContinuationResult> Task<TContinuationResult> continueOnCancled(
			Continuation<T, TContinuationResult> continuation) {
		return continueOnCanceled(continuation, CURRENT_THREAD_EXECUTOR);
	}

	
	@Override
	public <TContinuationResult> Task<TContinuationResult> continueOnCanceled(
			Continuation<T, TContinuationResult> continuation, Executor executor) {
		
		TaskImpl<TContinuationResult> newTask = new TaskImpl<>();
		
		this.addOnCanceledListener(()-> executeContinuation(continuation, newTask, executor), executor);
		
		return newTask;
	}

	
	@Override
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnCanceled(
			Continuation<T, Task<TContinuationResult>> continuation) {
		return continueWithTaskOnCanceled(continuation, CURRENT_THREAD_EXECUTOR);
	}

	
	@Override
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnCanceled(
			Continuation<T, Task<TContinuationResult>> continuation, Executor executor) {
		
		TaskImpl<TContinuationResult> newTask = new TaskImpl<>();
		
		this.addOnCanceledListener(()-> executeContinuationWithTask(continuation, newTask, executor), executor);
		
		return newTask;
	}

	
	@Override
	public <TContinuationResult> Task<TContinuationResult> continueOnSuccess(
			Continuation<T, TContinuationResult> continuation) {
		return continueOnSuccess(continuation, CURRENT_THREAD_EXECUTOR);
	}

	
	@Override
	public <TContinuationResult> Task<TContinuationResult> continueOnSuccess(
			Continuation<T, TContinuationResult> continuation, Executor executor) {
		
		TaskImpl<TContinuationResult> newTask = new TaskImpl<>();
		
		this.addOnSuccessListener(task-> executeContinuation(continuation, newTask, executor), executor);
		
		return newTask;
	}

	
	@Override
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnSuccess(
			Continuation<T, Task<TContinuationResult>> continuation) {
		return continueWithTaskOnSuccess(continuation, CURRENT_THREAD_EXECUTOR);
	}
	

	@Override
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnSuccess(
			Continuation<T, Task<TContinuationResult>> continuation, Executor executor) {
		
		TaskImpl<TContinuationResult> newTask = new TaskImpl<>();
		
		this.addOnSuccessListener(task-> executeContinuationWithTask(continuation, newTask, executor), executor);
		
		return newTask;
	}
	

	@Override
	public <TContinuationResult> Task<TContinuationResult> continueOnFailure(
			Continuation<T, TContinuationResult> continuation) {
		return continueOnFailure(continuation, CURRENT_THREAD_EXECUTOR);
	}
	

	@Override
	public <TContinuationResult> Task<TContinuationResult> continueOnFailure(
			Continuation<T, TContinuationResult> continuation, Executor executor) {
		
		TaskImpl<TContinuationResult> newTask = new TaskImpl<>();
		
		this.addOnFailureListener(e-> executeContinuation(continuation, newTask, executor), executor);
		
		return newTask;
	}
	

	@Override
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnFailure(
			Continuation<T, Task<TContinuationResult>> continuation) {
		return continueWithTaskOnFailure(continuation, CURRENT_THREAD_EXECUTOR);
	}
	

	@Override
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnFailure(
			Continuation<T, Task<TContinuationResult>> continuation, Executor executor) {
		
		TaskImpl<TContinuationResult> newTask = new TaskImpl<>();
		
		this.addOnFailureListener(e-> executeContinuationWithTask(continuation, newTask, executor), executor);
		
		return newTask;
	}
	

	@Override
	public boolean isComplete() {
		return isComplete;
	}

	@Override
	public boolean isSuccessful() {
		return isSuccessful;
	}

	@Override
	public boolean isCanceled() {
		return isCanceled;
	}

	@Override
	public void cancel() {
		if(!isComplete)
			isCanceled = true;
		finish();
	}

	@Override
	public Exception getFailure() {
		return this.failure;
	}

	@Override
	public T getResult() throws IllegalStateException, RuntimeException {
		if(!isComplete)
			throw new IllegalStateException(EXCEPTION_INCOMPLETE);
		else if(!isSuccessful)
			throw new RuntimeException(EXCEPTION_FAILED);
		else
			return this.result;
	}

	public void setResult(T result) {
		this.result = result;
		this.isSuccessful = true;
		finish();
	}
	
	public void setFailure(Exception failure) {
		this.failure = failure;
		this.isSuccessful = false;
		finish();
	}
	
	private void addEventListener(EventListener eventListener) {
		this.eventListeners.add(eventListener);
		if(isComplete)
			finish();
	}
	
	private void finish() {
		this.isComplete = true;
		synchronized(lock) {
			while(!eventListeners.isEmpty())
				eventListeners.poll().notifyEvent();
		}
	}
	
	private <TContinuationResult> void executeContinuation(Continuation<T,TContinuationResult> continuation, 
			TaskImpl<TContinuationResult> finalTask, Executor executor) {
		
		executor.execute(()-> {
			try {
				TContinuationResult result = continuation.then(this);
				finalTask.setResult(result);
			} catch(Exception e) {
				finalTask.setFailure(e);
			}
		});
	}
	
	private <TContinuationResult> void executeContinuationWithTask(Continuation<T,Task<TContinuationResult>> continuation, 
			TaskImpl<TContinuationResult> finalTask, Executor executor) {
		
		executor.execute(()-> {
			try {
				continuation.then(this).addOnCompleteListener(continuationTask-> {
					if(continuationTask.isSuccessful())
						finalTask.setResult(continuationTask.getResult());
					else
						finalTask.setFailure(continuationTask.getFailure());
				});
			} catch(Exception e) {
				finalTask.setFailure(e);
			}
		});
	}
	
}
