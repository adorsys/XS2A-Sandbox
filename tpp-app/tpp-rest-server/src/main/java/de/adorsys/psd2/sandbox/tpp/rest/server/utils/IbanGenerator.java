package de.adorsys.psd2.sandbox.tpp.rest.server.utils;

import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class IbanGenerator {
    private static final int ALLOWED_LENGTH_OF_TPP_CODE = 8;
    private static final int ALLOWED_LENGTH_OF_IBAN_SUFFIX = 2;
    private static final Map<Character, Integer> charToInt = new HashMap<>();

    static {
        char charToFill = 'A';
        for (int i = 10; i < 36; i++) {
            charToInt.put(charToFill, i);
            charToFill++;
        }
    }

    public static String generateIban(String countryCodePrefix, String tppCode, String bankCode, String ibanEnding) {
        return generateIbanInternal(countryCodePrefix, tppCode, ibanEnding, bankCode);
    }

    private static String generateIbanInternal(String countryCodePrefix, String tppCode, String ibanEnding, String bankCode) {
        if (isDigitsAndSize(tppCode, ALLOWED_LENGTH_OF_TPP_CODE) && isDigitsAndSize(ibanEnding, ALLOWED_LENGTH_OF_IBAN_SUFFIX) && isValidCountryPrefix(countryCodePrefix)) {
            BigInteger totalNr = new BigInteger(bankCode + tppCode + ibanEnding + getIntRepresentationOfCountryCode(countryCodePrefix));
            String checkSum = String.format("%02d", 98 - totalNr.remainder(BigInteger.valueOf(97)).intValue());
            return countryCodePrefix + checkSum + bankCode + tppCode + ibanEnding;
        }
        throw new TppException(String.format("Inappropriate data for IBAN creation %s %s", tppCode, ibanEnding), 400);
    }

    private static boolean isDigitsAndSize(String toCheck, int size) {
        String regex = "\\d+";
        return toCheck.matches(regex) && toCheck.length() == size;
    }

    private static boolean isValidCountryPrefix(String countryPrefix) {
        return countryPrefix.length() == 2; //TODO Add Locale check
    }

    private static String getIntRepresentationOfCountryCode(String countryCode) {
        char[] chars = countryCode.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char z : chars) {
            builder.append(charToInt.get(z));
        }
        return builder.append("00").toString();
    }
}
