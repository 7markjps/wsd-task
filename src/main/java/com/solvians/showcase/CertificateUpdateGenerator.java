package com.solvians.showcase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Multi-threaded certificate update generator using ExecutorService + CompletionService
public class CertificateUpdateGenerator {

	private final int threads;
	private final int quotes;

	public CertificateUpdateGenerator(int threads, int quotes) {
		if (threads < 1) {
			throw new IllegalArgumentException("Number of threads must be >= 1, got: " + threads);
		}
		if (quotes < 1) {
			throw new IllegalArgumentException("Number of quotes must be >= 1, got: " + quotes);
		}
		this.threads = threads;
		this.quotes = quotes;
	}

	public List<String> generateQuotes() {
		ExecutorService executor = Executors.newFixedThreadPool(threads, new CertGenThreadFactory());
		CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

		// all threads wait on this latch, then start simultaneously
		CountDownLatch startLatch = new CountDownLatch(1);

		try {
			for (int i = 0; i < quotes; i++) {
				completionService.submit(new CertificateUpdateCallable(startLatch));
			}

			// fire the starting gun
			startLatch.countDown();

			// collect results in completion order (fastest first)
			List<String> results = new ArrayList<>(quotes);
			for (int i = 0; i < quotes; i++) {
				Future<String> completed = completionService.take();
				results.add(completed.get());
			}

			return results;

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Certificate generation was interrupted", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Certificate generation failed: " + e.getCause().getMessage(), e);
		} finally {
			shutdownExecutor(executor);
		}
	}

	private void shutdownExecutor(ExecutorService executor) {
		executor.shutdown();
		try {
			if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
				executor.shutdownNow();
				if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
					System.err.println("WARNING: thread pool did not terminate cleanly.");
				}
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	public int getThreads() { return threads; }
	public int getQuotes() { return quotes; }

	// Callable that generates one certificate update CSV line
	public static class CertificateUpdateCallable implements Callable<String> {

		private final CountDownLatch startLatch;

		public CertificateUpdateCallable(CountDownLatch startLatch) {
			this.startLatch = startLatch;
		}

		// no-arg constructor for standalone testing (starts immediately)
		public CertificateUpdateCallable() {
			this.startLatch = new CountDownLatch(0);
		}

		@Override
		public String call() throws InterruptedException {
			startLatch.await();
			CertificateUpdate update = new CertificateUpdate();
			return update.toCsvString();
		}
	}

	// Named daemon threads: "cert-gen-worker-1", "cert-gen-worker-2", etc.
	static class CertGenThreadFactory implements ThreadFactory {

		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix = "cert-gen-worker-";

		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable, namePrefix + threadNumber.getAndIncrement());
			thread.setDaemon(true);
			thread.setPriority(Thread.NORM_PRIORITY);
			return thread;
		}
	}
}
