/*
 * Copyright 2014 LinkedIn Corp.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.utils;

import java.util.Collection;

/**
 * Utils for common Azkaban String manipulations
 *
 */
public class StringUtils {
	public static final char SINGLE_QUOTE = '\'';
	public static final char DOUBLE_QUOTE = '\"';
	public static final char ESCAPE_CHAR = '\\';

	private StringUtils() {
	}
	
	/**
	 * Adds escape characters to a String that has quotes in it.
	 * 
	 * @param s
	 * @param quoteCh
	 * @return
	 */
	public static String escapeCharacters(String s, char quoteCh) {	
		String replace = "" + ESCAPE_CHAR + quoteCh;
		String quote = "" + quoteCh;
		return s.replace(quote, replace);
	}

	/**
	 * Use this when you don't want to include Apache Common's string for
	 * plugins.
	 * 
	 * @param list
	 * @param delimiter
	 * @return
	 */
	public static String join(Collection<String> list, String delimiter) {
		if (list.isEmpty()) {
			return "";
		}
		
		StringBuffer buffer = new StringBuffer();
		for (String str: list) {
			buffer.append(str);
			buffer.append(delimiter);
		}
		
		if (buffer.length() > 0) {
			buffer.setLength(buffer.length() - delimiter.length());
		}
		
		return buffer.toString();
	}

}
