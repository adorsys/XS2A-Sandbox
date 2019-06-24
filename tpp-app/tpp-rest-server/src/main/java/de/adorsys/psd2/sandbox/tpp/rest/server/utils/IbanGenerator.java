package de.adorsys.psd2.sandbox.tpp.rest.server.utils;

import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;

import java.math.BigInteger;

public class IbanGenerator {
    private static final int ALLOWED_LENGTH_OF_TPP_CODE = 8;
    private static final int ALLOWED_LENGTH_OF_IBAN_SUFFIX = 2;
    private static final String COUNTRY_CODE_PREFIX = "DE";
    private static final String BANK_CODE_NISP = "76070024";
    private static final String BANK_CODE_FOR_RANDOM = "76050101";

    private IbanGenerator() {}

    public static String generateRandomIban(String tppCode, int ibanEnding) {
        return generateIban(tppCode, String.format("%02d", ibanEnding), BANK_CODE_FOR_RANDOM);
    }

    public static String generateIbanForNispAccount(String tppCode, String ibanEnding) {
        return generateIban(tppCode, ibanEnding, BANK_CODE_NISP);
    }

    private static String generateIban(String tppCode, String ibanEnding, String bankCode) {
        if (isDigitsAndSize(tppCode, ALLOWED_LENGTH_OF_TPP_CODE) && isDigitsAndSize(ibanEnding, ALLOWED_LENGTH_OF_IBAN_SUFFIX)) {
            BigInteger totalNr = new BigInteger(bankCode + tppCode + ibanEnding + "131400");
            String checkSum = String.format("%02d", 98 - totalNr.remainder(BigInteger.valueOf(97)).intValue());
            return COUNTRY_CODE_PREFIX + checkSum + BANK_CODE_NISP + tppCode + ibanEnding;
        }
        throw new TppException(String.format("Inappropriate data for IBAN creation %s %s", tppCode, ibanEnding), 400);
    }

    private static boolean isDigitsAndSize(String toCheck, int size) {
        String regex = "\\d+";
        return toCheck.matches(regex) && toCheck.length() == size;
    }
}
