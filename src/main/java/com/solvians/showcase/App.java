package com.solvians.showcase;

import java.util.List;

// Entry point: takes <threads> <quotes> as args, generates certificate updates in parallel
public class App {

	private final int threads;
	private final int quotes;

	public App(String threads, String quotes) {
		this.threads = Integer.parseInt(threads);
		this.quotes = Integer.parseInt(quotes);
	}

	public List<String> run() {
		CertificateUpdateGenerator generator = new CertificateUpdateGenerator(threads, quotes);
		return generator.generateQuotes();
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			throw new RuntimeException(
					"Expected at least 2 arguments: <threads> <quotes>. Got " + args.length + " argument(s).");
		}

		App app = new App(args[0], args[1]);

		System.out.println("=".repeat(80));
		System.out.println("  CERTIFICATE FEED GENERATOR");
		System.out.println("  Threads: " + app.threads + " | Quotes: " + app.quotes + " | Available Processors: "
				+ Runtime.getRuntime().availableProcessors());
		System.out.println("=".repeat(80));

		long startTime = System.nanoTime();
		List<String> results = app.run();
		long endTime = System.nanoTime();

		results.forEach(System.out::println);

		// print performance stats
		long durationMs = (endTime - startTime) / 1_000_000;
		double durationSec = durationMs / 1000.0;
		double throughput = durationSec > 0 ? results.size() / durationSec : results.size();

		System.out.println("=".repeat(80));
		System.out.println("  PERFORMANCE METRICS");
		System.out.println("  Total generated : " + results.size() + " certificate updates");
		System.out.println("  Threads used    : " + app.threads);
		System.out.println("  Execution time  : " + durationMs + " ms");
		System.out.printf("  Throughput      : %.0f quotes/sec%n", throughput);
		System.out.println("=".repeat(80));
	}
}
