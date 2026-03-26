package com.solvians.showcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// tests for CLI arg validation, constructor, run method, main integration
@DisplayName("App Tests")
class AppTest {

	@Nested
	@DisplayName("Argument Validation")
	class ArgumentValidationTests {

		@Test
		void noArgsShouldThrow() {
			assertThatThrownBy(() -> App.main(new String[] {}))
					.isInstanceOf(RuntimeException.class).hasMessageContaining("2 arguments");
		}

		@Test
		void singleArgShouldThrow() {
			assertThatThrownBy(() -> App.main(new String[] { "10" }))
					.isInstanceOf(RuntimeException.class).hasMessageContaining("2 arguments");
		}

		@Test
		void nonNumericFirstArgThrows() {
			assertThatThrownBy(() -> App.main(new String[] { "xxx", "100" }))
					.isInstanceOf(NumberFormatException.class);
		}

		@Test
		void nonNumericSecondArgThrows() {
			assertThatThrownBy(() -> App.main(new String[] { "10", "zzz" }))
					.isInstanceOf(NumberFormatException.class).hasMessageContaining("zzz");
		}

		@Test
		void bothNonNumericThrows() {
			assertThatThrownBy(() -> App.main(new String[] { "xxx", "zzz" }))
					.isInstanceOf(NumberFormatException.class);
		}
	}

	@Nested
	@DisplayName("Constructor")
	class ConstructorTests {

		@Test
		void validArgsConstruct() {
			App app = new App("4", "100");
			assertThat(app).isNotNull();
		}

		@Test
		void nonNumericThreadsThrows() {
			assertThatThrownBy(() -> new App("abc", "100")).isInstanceOf(NumberFormatException.class);
		}

		@Test
		void nonNumericQuotesThrows() {
			assertThatThrownBy(() -> new App("4", "abc")).isInstanceOf(NumberFormatException.class);
		}
	}

	@Nested
	@DisplayName("Run Method")
	class RunMethodTests {

		@Test
		void runReturnsCorrectCount() {
			App app = new App("2", "25");
			List<String> results = app.run();
			assertThat(results).hasSize(25);
		}

		@Test
		void runResultsAreValidCsv() {
			App app = new App("4", "50");
			String csvPattern = "\\d+,[A-Z]{2}[A-Z0-9]{9}[0-9],\\d+\\.\\d{2},\\d+,\\d+\\.\\d{2},\\d+";
			for (String line : app.run()) {
				assertThat(line).matches(csvPattern);
			}
		}

		@Test
		void minimalParams() {
			App app = new App("1", "1");
			List<String> results = app.run();
			assertThat(results).hasSize(1);
			assertThat(results.get(0).split(",")).hasSize(6);
		}
	}

	@Nested
	@DisplayName("Main Integration")
	class MainMethodTests {

		@Test
		void validArgsRunSuccessfully() {
			// 10 threads, 50 certificates — full demo run
			App.main(new String[] { "10", "50" });
		}

		@Test
		void extraArgsIgnored() {
			App.main(new String[] { "10", "50", "extra", "args" });
		}
	}
}
