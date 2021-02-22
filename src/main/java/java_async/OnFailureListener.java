package java_async;

@FunctionalInterface
public interface OnFailureListener<T> {
	void onFailure(Exception e);
}
