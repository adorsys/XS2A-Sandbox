package de.adorsys.ledgers.oba.rest.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class NullHeaderInterceptor implements RequestInterceptor {
	
	@Override
	public void apply(RequestTemplate template) {
		Map<String, Collection<String>> headers = template.headers();
		Set<Entry<String,Collection<String>>> entrySet = headers.entrySet();
		for (Entry<String, Collection<String>> e : entrySet) {
			String headerName = e.getKey();
			String nullValue = "{"+e.getKey()+"}";
			Collection<String> valueCol = e.getValue();
			if(valueCol==null) {
				continue;
			}
			List<String> newCol = new ArrayList<>();
			for (String val : valueCol) {
				if(nullValue.equalsIgnoreCase(val)) {
					newCol.add(val);
				}
			}
			if(newCol.size()==valueCol.size()) {
				template.header(headerName);
			}
		}
	}
}
