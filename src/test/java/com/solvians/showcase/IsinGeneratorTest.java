package com.solvians.showcase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// tests for ISIN generation, check digit calculation, validation
@DisplayName("IsinGenerator Tests")
class IsinGeneratorTest {

	private IsinGenerator generator;

	@BeforeEach
	void setUp() {
		generator = new IsinGenerator();
	}

	@Nested
	@DisplayName("ISIN Format Validation")
	class IsinFormatTests {

		@Test
		void generatedIsinShouldBe12Characters() {
			String isin = generator.generateIsin();
			assertThat(isin).hasSize(12);
		}

		@Test
		void firstTwoCharsShouldBeUppercaseLetters() {
			String isin = generator.generateIsin();
			assertThat(isin.substring(0, 2)).matches("[A-Z]{2}");
		}

		@Test
		void middleNineCharsShouldBeAlphanumeric() {
			String isin = generator.generateIsin();
			assertThat(isin.substring(2, 11)).matches("[A-Z0-9]{9}");
		}

		@Test
		void lastCharShouldBeDigit() {
			String isin = generator.generateIsin();
			assertThat(Character.isDigit(isin.charAt(11))).isTrue();
		}

		@Test
		void shouldMatchFullIsinPattern() {
			String isin = generator.generateIsin();
			assertThat(isin).matches("[A-Z]{2}[A-Z0-9]{9}[0-9]");
		}

		@RepeatedTest(50)
		void repeatedGenerationAlwaysValidFormat() {
			String isin = generator.generateIsin();
			assertThat(isin).matches("[A-Z]{2}[A-Z0-9]{9}[0-9]");
		}
	}

	@Nested
	@DisplayName("Check Digit Calculation")
	class CheckDigitTests {

		@Test
		void readmeExample_DE123456789_shouldGive6() {
			int checkDigit = generator.calculateCheckDigit("DE123456789");
			assertThat(checkDigit).isEqualTo(6);
		}

		@ParameterizedTest(name = "{0} -> check digit {1}")
		@CsvSource({ "US037833100, 5", // Apple Inc
				"DE000BAY001, 7", // Bayer AG
				"GB000256570, 2", // Vodafone
				"FR000012057, 8", // LVMH
		})
		void knownIsins(String body, int expectedDigit) {
			int checkDigit = generator.calculateCheckDigit(body);
			assertThat(checkDigit).isEqualTo(expectedDigit);
		}

		@Test
		void checkDigitAlwaysBetween0And9() {
			for (int i = 0; i < 100; i++) {
				String isin = generator.generateIsin();
				int checkDigit = generator.calculateCheckDigit(isin.substring(0, 11));
				assertThat(checkDigit).isBetween(0, 9);
			}
		}

		@RepeatedTest(100)
		void generatedCheckDigitSelfValidates() {
			String isin = generator.generateIsin();
			int computed = generator.calculateCheckDigit(isin.substring(0, 11));
			int embedded = Character.getNumericValue(isin.charAt(11));
			assertThat(computed).isEqualTo(embedded);
		}

		@Test
		void allLettersBody() {
			int checkDigit = generator.calculateCheckDigit("AAAAAAAAAAA");
			assertThat(checkDigit).isBetween(0, 9);
		}

		@Test
		void allDigitsBody() {
			int checkDigit = generator.calculateCheckDigit("AB000000000");
			assertThat(checkDigit).isBetween(0, 9);
		}
	}

	@Nested
	@DisplayName("Character Conversion")
	class ConversionTests {

		@Test
		void aShouldConvertTo10() {
			assertThat(generator.convertToDigitString("A")).isEqualTo("10");
		}

		@Test
		void zShouldConvertTo35() {
			assertThat(generator.convertToDigitString("Z")).isEqualTo("35");
		}

		@Test
		void digitsShouldRemainUnchanged() {
			assertThat(generator.convertToDigitString("0123456789")).isEqualTo("0123456789");
		}

		@Test
		void readmeExampleConversion() {
			// D=13, E=14, then 123456789
			String result = generator.convertToDigitString("DE123456789");
			assertThat(result).isEqualTo("1314123456789");
		}

		@Test
		void mixedConversion() {
			// A=10, B=11, 1 stays 1, C=12
			String result = generator.convertToDigitString("AB1C");
			assertThat(result).isEqualTo("1011112");
		}

		@Test
		void lowercaseShouldThrow() {
			assertThatThrownBy(() -> generator.convertToDigitString("ab")).isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("Invalid character");
		}
	}

	@Nested
	@DisplayName("ISIN Validation")
	class ValidationTests {

		@Test
		void validGeneratedIsinPasses() {
			String isin = generator.generateIsin();
			assertThat(generator.isValidIsin(isin)).isTrue();
		}

		@RepeatedTest(50)
		void repeatedValidationAlwaysPasses() {
			String isin = generator.generateIsin();
			assertThat(generator.isValidIsin(isin)).isTrue();
		}

		@Test
		void knownValidIsinPasses() {
			assertThat(generator.isValidIsin("DE1234567896")).isTrue();
		}

		@Test
		void wrongCheckDigitFails() {
			assertThat(generator.isValidIsin("DE1234567897")).isFalse();
		}

		@ParameterizedTest(name = "\"{0}\" should fail")
		@ValueSource(strings = { "", "DE12345", "DE12345678901234", "12345678901X", "de1234567890" })
		void invalidIsinsFail(String invalidIsin) {
			assertThat(generator.isValidIsin(invalidIsin)).isFalse();
		}

		@Test
		void nullIsinFails() {
			assertThat(generator.isValidIsin(null)).isFalse();
		}
	}

	@Nested
	@DisplayName("Error Handling")
	class ErrorHandlingTests {

		@Test
		void nullInputThrows() {
			assertThatThrownBy(() -> generator.calculateCheckDigit(null)).isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("11 characters");
		}

		@Test
		void tooShortInputThrows() {
			assertThatThrownBy(() -> generator.calculateCheckDigit("DE1234"))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		void tooLongInputThrows() {
			assertThatThrownBy(() -> generator.calculateCheckDigit("DE1234567890"))
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	@DisplayName("Uniqueness")
	class UniquenessTests {

		@Test
		void hundredIsinsShouldMostlyBeUnique() {
			long uniqueCount = java.util.stream.IntStream.range(0, 100).mapToObj(i -> generator.generateIsin())
					.distinct().count();
			assertThat(uniqueCount).isGreaterThanOrEqualTo(90);
		}
	}
}
