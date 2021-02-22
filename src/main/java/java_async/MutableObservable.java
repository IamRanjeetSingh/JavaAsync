package java_async;

public class MutableObservable<T> extends Observable<T> {
	public MutableObservable(T data) {
		super(data);
	}

	@Override
	public T get() {
		return super.get();
	}

	@Override
	public void set(T data) {
		super.set(data);
	}
	
	
}
