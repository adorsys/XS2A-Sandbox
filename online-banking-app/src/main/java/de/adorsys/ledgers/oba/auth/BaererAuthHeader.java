package de.adorsys.ledgers.oba.auth;

public class BaererAuthHeader {
	public static final String AUTH_TYPE_PREFIX = "BEARER ";
	
	public static String value(String accessToken) {
		return AUTH_TYPE_PREFIX + accessToken;
	}
}
