package java_async;

@FunctionalInterface
public interface Continuation<T,TContinuationResult> {
	TContinuationResult then(Task<T> ayncResult);
}
