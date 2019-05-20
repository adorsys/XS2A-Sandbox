package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkResolver {
    public static String getLink(Map<String, LinkedHashMap<String, String>> map, String linkToGet) {
        LinkedHashMap<String, String> hashMap = map.get(linkToGet);
        return hashMap.get("href");
    }
}
