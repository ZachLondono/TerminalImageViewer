package imageprocessor.threading;

import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Manager {
	
	private String[] result;
	private ThreadPoolExecutor executor;
	private HashMap<Worker, Future> workerThreads = new HashMap<Worker, Future>();

	public Manager(int[] data, int threadCount) {
		
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
		result = new String[data.length];

		for (int i = 0; i < data.length; i++) {
			Worker worker = new Worker(i, data[i]);
			workerThreads.put(worker, executor.submit(worker));
		}

		executor.shutdown();

		workerThreads.keySet().stream().forEach( (worker) -> {
			
			while (!workerThreads.get(worker).isDone()) {}
			
			result[worker.getPixelIndex()] = worker.getPixelString();
	
		});

	}

	// Returns a value from 0 to 1
	public float getProgress() {
		return ((float) executor.getCompletedTaskCount()) / result.length;
	}

	public String[] getResult() {
		return result;
	}

}
