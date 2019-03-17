package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import org.apache.commons.lang3.StringUtils;

public class QuerryParser {

	public static String param(String url, String paramName) {
		String result = StringUtils.substringAfter(url, "?");
		result = StringUtils.substringAfter(result, paramName+"=");
		return StringUtils.substringBefore(result, "&");
	}
}
