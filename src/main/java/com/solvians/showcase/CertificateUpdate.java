package com.solvians.showcase;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

// Represents one certificate update line: timestamp,ISIN,bidPrice,bidSize,askPrice,askSize
public class CertificateUpdate {

	private static final double MIN_PRICE = 100.00;
	private static final double MAX_PRICE = 200.00;
	private static final int MIN_BID_SIZE = 1000;
	private static final int MAX_BID_SIZE = 5000;
	private static final int MIN_ASK_SIZE = 1000;
	private static final int MAX_ASK_SIZE = 10000;

	private final long timestamp;
	private final String isin;
	private final double bidPrice;
	private final int bidSize;
	private final double askPrice;
	private final int askSize;

	// Random generation with defaults
	public CertificateUpdate() {
		this(ThreadLocalRandom.current(), new IsinGenerator());
	}

	// Allows injecting random source and ISIN generator for testing
	public CertificateUpdate(ThreadLocalRandom random, IsinGenerator isinGenerator) {
		this.timestamp = System.currentTimeMillis();
		this.isin = isinGenerator.generateIsin();
		this.bidPrice = roundToTwoDecimals(MIN_PRICE + (MAX_PRICE - MIN_PRICE) * random.nextDouble());
		this.bidSize = random.nextInt(MIN_BID_SIZE, MAX_BID_SIZE + 1);
		this.askPrice = roundToTwoDecimals(MIN_PRICE + (MAX_PRICE - MIN_PRICE) * random.nextDouble());
		this.askSize = random.nextInt(MIN_ASK_SIZE, MAX_ASK_SIZE + 1);
	}

	// Explicit values for unit testing
	public CertificateUpdate(long timestamp, String isin, double bidPrice, int bidSize, double askPrice, int askSize) {
		this.timestamp = timestamp;
		this.isin = isin;
		this.bidPrice = bidPrice;
		this.bidSize = bidSize;
		this.askPrice = askPrice;
		this.askSize = askSize;
	}

	// e.g. "1352122280502,DE1234567896,101.23,1000,103.45,1000"
	public String toCsvString() {
		return String.format(Locale.US, "%d,%s,%.2f,%d,%.2f,%d", timestamp, isin, bidPrice, bidSize, askPrice, askSize);
	}

	private static double roundToTwoDecimals(double value) {
		return Math.round(value * 100.0) / 100.0;
	}

	public long getTimestamp() { return timestamp; }
	public String getIsin() { return isin; }
	public double getBidPrice() { return bidPrice; }
	public int getBidSize() { return bidSize; }
	public double getAskPrice() { return askPrice; }
	public int getAskSize() { return askSize; }

	@Override
	public String toString() {
		return toCsvString();
	}
}
