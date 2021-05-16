package imageprocessor.threading;

import java.lang.Math;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Manager {
	
	private String[] result;
	private ThreadPoolExecutor executor;
	private HashMap<Worker, Future> workerThreads = new HashMap<Worker, Future>();

	public Manager(int[] data, int threadCount, int imageWidth) {
		
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
		result = new String[threadCount];

		int divisionSize = (int) Math.floor(((float) data.length) / threadCount); 
		for (int i = 0; i < threadCount; i++) {
		
			int start = i*divisionSize;
			int end = i == threadCount - 1 ? data.length : start + divisionSize;
			Integer[] subarray = IntStream.range(start, end) 
						.mapToObj(index -> data[index])
						.toArray(Integer[]::new);

			Worker worker = new Worker(i, start, imageWidth, subarray);
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
