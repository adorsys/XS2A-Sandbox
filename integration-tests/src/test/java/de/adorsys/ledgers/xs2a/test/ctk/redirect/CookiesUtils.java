package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import java.net.HttpCookie;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CookiesUtils {
	public String readCookie(List<String> cookieHeaders, String cookieName) {
		for (String httpCookie : cookieHeaders) {
			if(StringUtils.startsWithIgnoreCase(httpCookie.trim(), cookieName)){
				return httpCookie.trim();
			}
		}
		return null;
	}
	
	
	public String resetCookies(List<String> cookieStrings) {
		String result = null;
		for (String cookieString : cookieStrings) {
			List<HttpCookie> parse = HttpCookie.parse(cookieString);
			for (HttpCookie httpCookie : parse) {
				if(StringUtils.isNoneBlank(httpCookie.getValue())) {
					String cookie = httpCookie.getName()+"="+httpCookie.getValue();
					if(result==null) {
						result = cookie;
					} else {
						result = result + " ; " + cookie;
					}
				}
			}
		}
		return result;
	}

}
