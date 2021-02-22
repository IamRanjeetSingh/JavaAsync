package java_async;

@FunctionalInterface
public interface OnSuccessListener<T> {
	void onSuccess(T result);
}
