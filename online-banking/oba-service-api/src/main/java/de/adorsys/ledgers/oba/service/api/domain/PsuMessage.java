/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.ledgers.oba.service.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

public class PsuMessage {
	@JsonProperty("category")
	private PsuMessageCategory category = null;
	@JsonProperty("code")
	private String code = null;
	@JsonProperty("path")
	private String path = null;
	@JsonProperty("text")
	private String text = null;

	public PsuMessage category(PsuMessageCategory category) {
		this.category = category;
		return this;
	}

	/**
	 * Get category
	 *
	 * @return category
	 **/
	@Schema(required = true)
	public PsuMessageCategory getCategory() {
		return category;
	}

	public void setCategory(PsuMessageCategory category) {
		this.category = category;
	}

	public PsuMessage code(String code) {
		this.code = code;
		return this;
	}

	/**
	 * Get code
	 *
	 * @return code
	 **/
	@Schema(required = true)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public PsuMessage path(String path) {
		this.path = path;
		return this;
	}

	/**
	 * Get path
	 *
	 * @return path
	 **/
	@Schema
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public PsuMessage text(String text) {
		this.text = text;
		return this;
	}

	/**
	 * Get text
	 *
	 * @return text
	 **/
	@Schema
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PsuMessage psuMessage = (PsuMessage) o;
		return Objects.equals(this.category, psuMessage.category)
				&& Objects.equals(this.code, psuMessage.code)
				&& Objects.equals(this.path, psuMessage.path)
				&& Objects.equals(this.text, psuMessage.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, code, path, text);
	}

	@Override
	public String toString() {
        return "class PsuMessage {\n" +
                        "    category: " + toIndentedString(category) + "\n" +
                        "    code: " + toIndentedString(code) + "\n" +
                        "    path: " + toIndentedString(path) + "\n" +
                        "    text: " + toIndentedString(text) + "\n" +
                        "}";
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
