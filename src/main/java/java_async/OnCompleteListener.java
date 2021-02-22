package java_async;

@FunctionalInterface
public interface OnCompleteListener<T> {
	void onComplete(Task<T> asyncResult);
}
