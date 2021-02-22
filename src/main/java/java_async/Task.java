package java_async;

import java.util.concurrent.Executor;

public interface Task<T> {
	public Task<T> addOnCompleteListener(OnCompleteListener<T> completeListener);
	
	public Task<T> addOnCompleteListener(OnCompleteListener<T> completeListener, Executor executor);
	
	public Task<T> addOnSuccessListener(OnSuccessListener<T> successListener);
	
	public Task<T> addOnSuccessListener(OnSuccessListener<T> successListener, Executor executor);
	
	public Task<T> addOnFailureListener(OnFailureListener<T> failureListener);
	
	public Task<T> addOnFailureListener(OnFailureListener<T> failureListener, Executor executor);
	
	public Task<T> addOnCanceledListener(OnCanceledListener canceledListener);
	
	public Task<T> addOnCanceledListener(OnCanceledListener canceledListener, Executor executor);

	public <TContinuationResult> Task<TContinuationResult> continueWith(Continuation<T,TContinuationResult> continuation);
	
	public <TContinuationResult> Task<TContinuationResult> continueWith(Continuation<T,TContinuationResult> continuation, Executor executor);

	public <TContinuationResult> Task<TContinuationResult> continueWithTask(Continuation<T,Task<TContinuationResult>> continuation);
	
	public <TContinuationResult> Task<TContinuationResult> continueWithTask(Continuation<T,Task<TContinuationResult>> continuation, Executor executor);
	
	public <TContinuationResult> Task<TContinuationResult> continueOnCancled(Continuation<T,TContinuationResult> continuation);

	public <TContinuationResult> Task<TContinuationResult> continueOnCanceled(Continuation<T,TContinuationResult> continuation, Executor executor);

	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnCanceled(Continuation<T,Task<TContinuationResult>> continuation);
	
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnCanceled(Continuation<T,Task<TContinuationResult>> continuation, Executor executor);
	
	public 	<TContinuationResult> Task<TContinuationResult> continueOnSuccess(Continuation<T,TContinuationResult> continuation);

	public 	<TContinuationResult> Task<TContinuationResult> continueOnSuccess(Continuation<T,TContinuationResult> continuation, Executor executor);
	
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnSuccess(Continuation<T,Task<TContinuationResult>> continuation);
	
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnSuccess(Continuation<T,Task<TContinuationResult>> continuation, Executor executor);
	
	public <TContinuationResult> Task<TContinuationResult> continueOnFailure(Continuation<T,TContinuationResult> continuation);

	public <TContinuationResult> Task<TContinuationResult> continueOnFailure(Continuation<T,TContinuationResult> continuation, Executor executor);
	
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnFailure(Continuation<T,Task<TContinuationResult>> continuation);
	
	public <TContinuationResult> Task<TContinuationResult> continueWithTaskOnFailure(Continuation<T,Task<TContinuationResult>> continuation, Executor executor);
	
	public boolean isComplete();
	
	public boolean isSuccessful();
	
	public boolean isCanceled();
	
	public void cancel();
	
	public Exception getFailure();
	
	public T getResult() throws IllegalStateException, RuntimeException;
}