package de.adorsys.ledgers.oba.rest.server.auth;

public class BaererAuthHeader {
	public static final String AUTH_TYPE_PREFIX = "BEARER ";
	
	public static String value(String accessToken) {
		return AUTH_TYPE_PREFIX + accessToken;
	}
}
