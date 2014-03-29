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

/**
 * Immutable Pair of values.
 * 
 * Hashcode and equals implemented for use as a compound key.
 * 
 * @param <F>
 * @param <S>
 */
public class Pair<F, S> {
	private final F first;
	private final S second;

	/**
	 * Constructor
	 * First object and Second object in a pair
	 * 
	 * @param first
	 * @param second
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Retrieve the first element
	 * @return
	 */
	public F getFirst() {
		return first;
	}

	/**
	 * Retrieve the second element
	 * @return
	 */
	public S getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "{" + (first == null? "null" : first.toString()) + "," + (second == null? "null" : second.toString()) + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}
}
