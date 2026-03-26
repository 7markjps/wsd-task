package com.solvians.showcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// tests for multi-threaded generation, CSV format, input validation, callable
@DisplayName("CertificateUpdateGenerator Tests")
class CertificateUpdateGeneratorTest {

	@Nested
	@DisplayName("Basic Generation")
	class BasicGenerationTests {

		@Test
		void shouldGenerateExactNumberOfQuotes() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(2, 50);
			List<String> quotes = generator.generateQuotes();
			assertThat(quotes).hasSize(50);
		}

		@Test
		void singleQuoteSingleThread() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(1, 1);
			List<String> quotes = generator.generateQuotes();
			assertThat(quotes).hasSize(1);
		}

		@Test
		void manyQuotesManyThreads() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(10, 1000);
			List<String> quotes = generator.generateQuotes();
			assertThat(quotes).hasSize(1000);
		}

		@Test
		void moreThreadsThanQuotes() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(10, 3);
			List<String> quotes = generator.generateQuotes();
			assertThat(quotes).hasSize(3);
		}
	}

	@Nested
	@DisplayName("CSV Line Format")
	class CsvFormatTests {

		@Test
		void eachLineHas6Fields() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(4, 20);
			List<String> quotes = generator.generateQuotes();

			for (String line : quotes) {
				assertThat(line.split(",")).as("Line: %s", line).hasSize(6);
			}
		}

		@Test
		void timestampIsValidLong() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(2, 10);
			for (String line : generator.generateQuotes()) {
				long timestamp = Long.parseLong(line.split(",")[0]);
				assertThat(timestamp).isGreaterThan(0);
			}
		}

		@Test
		void isinFieldIsValid() {
			IsinGenerator validator = new IsinGenerator();
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(2, 20);

			for (String line : generator.generateQuotes()) {
				String isin = line.split(",")[1];
				assertThat(validator.isValidIsin(isin)).as("ISIN: %s", isin).isTrue();
			}
		}

		@Test
		void bidPriceInRange() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(4, 50);
			for (String line : generator.generateQuotes()) {
				double bidPrice = Double.parseDouble(line.split(",")[2]);
				assertThat(bidPrice).isBetween(100.00, 200.00);
			}
		}

		@Test
		void bidSizeInRange() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(4, 50);
			for (String line : generator.generateQuotes()) {
				int bidSize = Integer.parseInt(line.split(",")[3]);
				assertThat(bidSize).isBetween(1000, 5000);
			}
		}

		@Test
		void askPriceInRange() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(4, 50);
			for (String line : generator.generateQuotes()) {
				double askPrice = Double.parseDouble(line.split(",")[4]);
				assertThat(askPrice).isBetween(100.00, 200.00);
			}
		}

		@Test
		void askSizeInRange() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(4, 50);
			for (String line : generator.generateQuotes()) {
				int askSize = Integer.parseInt(line.split(",")[5]);
				assertThat(askSize).isBetween(1000, 10000);
			}
		}

		@Test
		void lineMatchesCsvPattern() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(2, 10);
			String csvPattern = "\\d+,[A-Z]{2}[A-Z0-9]{9}[0-9],\\d+\\.\\d{2},\\d+,\\d+\\.\\d{2},\\d+";
			for (String line : generator.generateQuotes()) {
				assertThat(line).matches(csvPattern);
			}
		}
	}

	@Nested
	@DisplayName("Multi-Threading")
	class MultiThreadingTests {

		@Test
		void fiftyThreadsDontLoseQuotes() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(50, 500);
			List<String> quotes = generator.generateQuotes();
			assertThat(quotes).hasSize(500);
		}

		@Test
		void isinsShouldBeDiverse() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(4, 100);
			Set<String> uniqueIsins = generator.generateQuotes().stream()
					.map(line -> line.split(",")[1])
					.collect(Collectors.toSet());
			assertThat(uniqueIsins.size()).isGreaterThan(90);
		}

		@Test
		void noNullResults() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(8, 200);
			List<String> quotes = generator.generateQuotes();
			assertThat(quotes).doesNotContainNull();
			for (String line : quotes) {
				assertThat(line).isNotEmpty();
			}
		}
	}

	@Nested
	@DisplayName("Input Validation")
	class InputValidationTests {

		@Test
		void zeroThreadsThrows() {
			assertThatThrownBy(() -> new CertificateUpdateGenerator(0, 10))
					.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("threads");
		}

		@Test
		void negativeThreadsThrows() {
			assertThatThrownBy(() -> new CertificateUpdateGenerator(-1, 10))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		void zeroQuotesThrows() {
			assertThatThrownBy(() -> new CertificateUpdateGenerator(4, 0))
					.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("quotes");
		}

		@Test
		void negativeQuotesThrows() {
			assertThatThrownBy(() -> new CertificateUpdateGenerator(4, -5))
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	@DisplayName("Getters")
	class GetterTests {

		@Test
		void getThreadsReturnsConfigured() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(8, 100);
			assertThat(generator.getThreads()).isEqualTo(8);
		}

		@Test
		void getQuotesReturnsConfigured() {
			CertificateUpdateGenerator generator = new CertificateUpdateGenerator(8, 100);
			assertThat(generator.getQuotes()).isEqualTo(100);
		}
	}

	@Nested
	@DisplayName("Callable")
	class CallableTests {

		@Test
		void callReturnsValidCsv() throws Exception {
			var callable = new CertificateUpdateGenerator.CertificateUpdateCallable();
			String result = callable.call();
			assertThat(result).isNotNull();
			assertThat(result.split(",")).hasSize(6);
		}

		@Test
		void callMatchesPattern() throws Exception {
			var callable = new CertificateUpdateGenerator.CertificateUpdateCallable();
			String csvPattern = "\\d+,[A-Z]{2}[A-Z0-9]{9}[0-9],\\d+\\.\\d{2},\\d+,\\d+\\.\\d{2},\\d+";
			assertThat(callable.call()).matches(csvPattern);
		}

		@Test
		void multipleCallsProduceDifferentResults() throws Exception {
			var callable = new CertificateUpdateGenerator.CertificateUpdateCallable();
			Set<String> results = new java.util.HashSet<>();
			for (int i = 0; i < 50; i++) {
				results.add(callable.call());
			}
			assertThat(results.size()).isGreaterThan(40);
		}
	}
}
