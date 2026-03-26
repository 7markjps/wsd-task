package com.solvians.showcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.assertThat;

// tests for certificate update CSV format, value ranges, ISIN validity, timestamps
@DisplayName("CertificateUpdate Tests")
class CertificateUpdateTest {

	@Nested
	@DisplayName("CSV Output Format")
	class CsvFormatTests {

		@Test
		void explicitConstructorProducesExactCsv() {
			CertificateUpdate update = new CertificateUpdate(1352122280502L, "DE1234567896", 101.23, 1000, 103.45, 1000);
			String csv = update.toCsvString();
			assertThat(csv).isEqualTo("1352122280502,DE1234567896,101.23,1000,103.45,1000");
		}

		@Test
		void csvHas6Fields() {
			CertificateUpdate update = new CertificateUpdate();
			String[] fields = update.toCsvString().split(",");
			assertThat(fields).hasSize(6);
		}

		@Test
		void pricesHave2DecimalPlaces() {
			CertificateUpdate update = new CertificateUpdate(1000L, "DE1234567896", 150.10, 2000, 160.00, 3000);
			String[] fields = update.toCsvString().split(",");
			assertThat(fields[2]).matches("\\d+\\.\\d{2}");
			assertThat(fields[4]).matches("\\d+\\.\\d{2}");
		}

		@Test
		void sizesHaveNoDecimals() {
			CertificateUpdate update = new CertificateUpdate(1000L, "DE1234567896", 150.00, 2500, 160.00, 7500);
			String[] fields = update.toCsvString().split(",");
			assertThat(fields[3]).matches("\\d+");
			assertThat(fields[3]).doesNotContain(".");
			assertThat(fields[5]).matches("\\d+");
			assertThat(fields[5]).doesNotContain(".");
		}

		@Test
		void noThousandSeparators() {
			CertificateUpdate update = new CertificateUpdate(1352122280502L, "DE1234567896", 101.23, 5000, 103.45, 10000);
			assertThat(update.toCsvString().split(",")).hasSize(6);
		}

		@Test
		void toStringEqualsToCsvString() {
			CertificateUpdate update = new CertificateUpdate(1000L, "DE1234567896", 150.00, 2000, 160.00, 3000);
			assertThat(update.toString()).isEqualTo(update.toCsvString());
		}
	}

	@Nested
	@DisplayName("Value Ranges")
	class ValueRangeTests {

		@RepeatedTest(100)
		void bidPriceBetween100And200() {
			CertificateUpdate update = new CertificateUpdate();
			assertThat(update.getBidPrice()).isBetween(100.00, 200.00);
		}

		@RepeatedTest(100)
		void askPriceBetween100And200() {
			CertificateUpdate update = new CertificateUpdate();
			assertThat(update.getAskPrice()).isBetween(100.00, 200.00);
		}

		@RepeatedTest(100)
		void bidSizeBetween1000And5000() {
			CertificateUpdate update = new CertificateUpdate();
			assertThat(update.getBidSize()).isBetween(1000, 5000);
		}

		@RepeatedTest(100)
		void askSizeBetween1000And10000() {
			CertificateUpdate update = new CertificateUpdate();
			assertThat(update.getAskSize()).isBetween(1000, 10000);
		}

		@RepeatedTest(50)
		void bidPriceHasAtMost2Decimals() {
			CertificateUpdate update = new CertificateUpdate();
			double price = update.getBidPrice();
			assertThat(price).isEqualTo(Math.round(price * 100.0) / 100.0);
		}

		@RepeatedTest(50)
		void askPriceHasAtMost2Decimals() {
			CertificateUpdate update = new CertificateUpdate();
			double price = update.getAskPrice();
			assertThat(price).isEqualTo(Math.round(price * 100.0) / 100.0);
		}
	}

	@Nested
	@DisplayName("ISIN in Update")
	class IsinTests {

		@RepeatedTest(50)
		void isinShouldBeValid() {
			CertificateUpdate update = new CertificateUpdate();
			IsinGenerator validator = new IsinGenerator();
			assertThat(validator.isValidIsin(update.getIsin())).isTrue();
		}

		@Test
		void isinIs12Chars() {
			CertificateUpdate update = new CertificateUpdate();
			assertThat(update.getIsin()).hasSize(12);
		}

		@Test
		void isinIsSecondFieldInCsv() {
			CertificateUpdate update = new CertificateUpdate(1000L, "DE1234567896", 101.23, 1000, 103.45, 1000);
			String[] fields = update.toCsvString().split(",");
			assertThat(fields[1]).isEqualTo("DE1234567896");
		}
	}

	@Nested
	@DisplayName("Timestamps")
	class TimestampTests {

		@Test
		void timestampIsCloseToCurrentTime() {
			long before = System.currentTimeMillis();
			CertificateUpdate update = new CertificateUpdate();
			long after = System.currentTimeMillis();
			assertThat(update.getTimestamp()).isBetween(before, after);
		}

		@Test
		void explicitTimestampIsPreserved() {
			CertificateUpdate update = new CertificateUpdate(1352122280502L, "DE1234567896", 101.23, 1000, 103.45, 1000);
			assertThat(update.getTimestamp()).isEqualTo(1352122280502L);
		}

		@Test
		void timestampIsFirstFieldInCsv() {
			CertificateUpdate update = new CertificateUpdate(1352122280502L, "DE1234567896", 101.23, 1000, 103.45, 1000);
			String[] fields = update.toCsvString().split(",");
			assertThat(fields[0]).isEqualTo("1352122280502");
		}
	}

	@Nested
	@DisplayName("Getters")
	class GetterTests {

		@Test
		void allGettersReturnCorrectValues() {
			CertificateUpdate update = new CertificateUpdate(1352122280502L, "DE1234567896", 155.50, 3000, 160.75, 8000);
			assertThat(update.getTimestamp()).isEqualTo(1352122280502L);
			assertThat(update.getIsin()).isEqualTo("DE1234567896");
			assertThat(update.getBidPrice()).isEqualTo(155.50);
			assertThat(update.getBidSize()).isEqualTo(3000);
			assertThat(update.getAskPrice()).isEqualTo(160.75);
			assertThat(update.getAskSize()).isEqualTo(8000);
		}
	}
}
