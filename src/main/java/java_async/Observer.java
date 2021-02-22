package java_async;

@FunctionalInterface
public interface Observer<T> {
	void onChange();
}