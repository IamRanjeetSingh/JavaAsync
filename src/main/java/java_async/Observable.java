package java_async;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Observable<T> {
	private T data;
	private ConcurrentHashMap<Integer,WeakReference<Subscription<T>>> subscriptions;
	
	public Observable(T data) {
		this.data = data;
		this.subscriptions = new ConcurrentHashMap<>();
	}
	
	protected T get() {
		return data;
	}
	
	protected void set(T data) {
		this.data = data;
		notifyObservers();
	}
	
	private void notifyObservers() {
		Set<Integer> deadSubscriptions = new HashSet<>();
		
		for(Entry<Integer, WeakReference<Subscription<T>>> entry : subscriptions.entrySet()) {
			if(entry.getValue().get() != null)
				entry.getValue().get().observer.onChange();
			else 
				deadSubscriptions.add(entry.getKey());
		}
		
		for(Integer key : deadSubscriptions)
			subscriptions.remove(key);
	
	}
	
	public Subscription<T> subscribe(Observer<T> observer){
		Subscription<T> subscription = new Subscription<>(this, observer);
		
		subscriptions.put(subscription.id, new WeakReference<>(subscription));	
		
		return subscription;
	}

	public void unsubscribe(Subscription<T> subscription) {
		subscriptions.remove(subscription.id);
	}
	
	public static class Subscription<T>{
		private int id;
		private Observable<T> observable;
		private Observer<T> observer;
		
		private Subscription(Observable<T> observable, Observer<T> observer) {
			this.id = this.hashCode();
			this.observable = observable;
			this.observer = observer;
		}
		
		public T getData() {
			return observable.get();
		}
		
		public void unsubscribe() {
			observable.unsubscribe(this);
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Subscription<?>)
				return this.id == ((Subscription<?>)obj).id;
			else 
				return false;
		}
	}
}