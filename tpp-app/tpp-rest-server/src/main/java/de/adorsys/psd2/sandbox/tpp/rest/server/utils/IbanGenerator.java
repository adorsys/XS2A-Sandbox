package de.adorsys.psd2.sandbox.tpp.rest.server.utils;

import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;

import java.math.BigInteger;

public class IbanGenerator {
    private static final String BANK_CODE_NISP = "76070024";
    private static final String BANK_CODE_FOR_RANDOM = "76050101";

    public static String generateRandomIban(String tppCode, int bban) {
        return generateIban(tppCode, String.format("%02d", bban), BANK_CODE_FOR_RANDOM);
    }

    public static String generateIbanForNispAccount(String tppCode, String bban) {
        return generateIban(tppCode, bban, BANK_CODE_NISP);
    }

    private static String generateIban(String tppCode, String bban, String bankCode) {
        if (isDigitsAndSize(tppCode, 8) && isDigitsAndSize(bban, 2)) {
            BigInteger totalNr = new BigInteger(bankCode + tppCode + bban + "131400");
            String checkSum = String.format("%02d", 98 - totalNr.remainder(BigInteger.valueOf(97)).intValue());
            return "DE" + checkSum + BANK_CODE_NISP + tppCode + bban;
        }
        throw new TppException(String.format("Inappropriate data for IBAN creation %s %s", tppCode, bban), 400);
    }

    private static boolean isDigitsAndSize(String toCheck, int size) {
        String regex = "\\d+";
        return toCheck.matches(regex) && toCheck.length() == size;
    }
}
