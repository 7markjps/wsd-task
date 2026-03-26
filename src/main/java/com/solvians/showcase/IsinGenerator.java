package com.solvians.showcase;

import java.util.concurrent.ThreadLocalRandom;

// Generates valid ISIN strings: 2 letters + 9 alphanumeric + 1 check digit
public class IsinGenerator {

	private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public String generateIsin() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		StringBuilder isinBody = new StringBuilder(11);

		// 2 random uppercase letters
		for (int i = 0; i < 2; i++) {
			isinBody.append(UPPERCASE_LETTERS.charAt(random.nextInt(UPPERCASE_LETTERS.length())));
		}

		// 9 random alphanumeric chars
		for (int i = 0; i < 9; i++) {
			isinBody.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
		}

		int checkDigit = calculateCheckDigit(isinBody.toString());
		return isinBody.toString() + checkDigit;
	}

	// Luhn-based check digit: convert letters to numbers, double every other from right, sum digits
	public int calculateCheckDigit(String isinWithoutCheckDigit) {
		if (isinWithoutCheckDigit == null || isinWithoutCheckDigit.length() != 11) {
			throw new IllegalArgumentException(
					"ISIN body must be exactly 11 characters, got: " + isinWithoutCheckDigit);
		}

		String digitString = convertToDigitString(isinWithoutCheckDigit);

		int sum = 0;
		boolean doubleDigit = true;
		for (int i = digitString.length() - 1; i >= 0; i--) {
			int digit = Character.getNumericValue(digitString.charAt(i));
			if (doubleDigit) {
				digit *= 2;
				if (digit > 9) {
					sum += (digit / 10) + (digit % 10);
				} else {
					sum += digit;
				}
			} else {
				sum += digit;
			}
			doubleDigit = !doubleDigit;
		}

		return (10 - (sum % 10)) % 10;
	}

	// A=10, B=11, ..., Z=35, digits stay as-is
	String convertToDigitString(String isin) {
		StringBuilder digits = new StringBuilder();
		for (char c : isin.toCharArray()) {
			if (Character.isUpperCase(c)) {
				digits.append(c - 'A' + 10);
			} else if (Character.isDigit(c)) {
				digits.append(c);
			} else {
				throw new IllegalArgumentException("Invalid character in ISIN: " + c);
			}
		}
		return digits.toString();
	}

	// Checks full 12-char ISIN: format + check digit match
	public boolean isValidIsin(String isin) {
		if (isin == null || isin.length() != 12) {
			return false;
		}
		if (!isin.substring(0, 2).matches("[A-Z]{2}")) {
			return false;
		}
		if (!isin.substring(2, 11).matches("[A-Z0-9]{9}")) {
			return false;
		}
		if (!Character.isDigit(isin.charAt(11))) {
			return false;
		}

		String body = isin.substring(0, 11);
		int expectedCheckDigit = calculateCheckDigit(body);
		int actualCheckDigit = Character.getNumericValue(isin.charAt(11));
		return expectedCheckDigit == actualCheckDigit;
	}
}
