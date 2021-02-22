package test;

import java.lang.ref.WeakReference;

import java_async.MutableObservable;
import java_async.Observable;
import java_async.Observer;
import java_async.Observable.Subscription;

public class ObservableTest {;
	public static void start(){
		DataGenerator dataGenerator = new DataGenerator();
		DataObserver dataObserver = new DataObserver();
		WeakReference<DataObserver> dataObserverWR = new WeakReference<>(dataObserver);
		
		dataGenerator.startUpdating();
		
		dataObserver.observe(dataGenerator);
		
		dataObserver = null;
		System.gc();
		if(dataObserverWR.get() != null)
			System.out.println("memory leak");
		else 
			System.out.println("all good");
	}
	
	public static class DataObserver implements Observer<Integer>{
		private Subscription<Integer> subscription;
		
		public void observe(DataGenerator dataGenerator) {
			subscription = dataGenerator.getObservable().subscribe(()-> {
				System.out.println("new data: "+subscription.getData());
			});
			
			dataGenerator.getObservable().subscribe(this);
		}

		@Override
		public void onChange() {
			System.out.println("new data: "+subscription.getData());
		}
	}
	
	public static class DataGenerator{
		private MutableObservable<Integer> liveData;
		private BgThread bgThread;
		private volatile boolean keepUpdating = false;
		
		public DataGenerator() {
			liveData = new MutableObservable<Integer>(0);
		}
		
		public void startUpdating() {
			keepUpdating = true;
			if(bgThread == null)
				bgThread = new BgThread();
			updateData();
		}
		
		private void updateData() {
			bgThread.execute(()-> {
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
				liveData.set(liveData.get()+2);
				if(keepUpdating)
					updateData();
			});
		}
		
		public void stopUpdating() {
			keepUpdating = false;
			bgThread = null;
			System.gc();
		}
		
		public Observable<Integer> getObservable(){
			return liveData;
		}
	}
}
