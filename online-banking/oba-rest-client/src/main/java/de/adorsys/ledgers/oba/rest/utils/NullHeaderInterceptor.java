/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

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
