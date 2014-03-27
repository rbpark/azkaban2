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

package azkaban.utils.cache;

/**
 * Element that is used in the Cache.
 * It holds creationTime and lastAccessTime to facilitate LRU or FIFO and
 * lifespan eviction
 * 
 * @param <T>
 */
public class Element<T> {
	private Object key;
	private T element;
	private long creationTime = 0;
	private long lastAccessTime = 0;

	/**
	 * Constructor
	 * Called by the Cache
	 * 
	 * @param key
	 * @param element
	 */
	public Element(Object key, T element) {
		this.key = key;
		creationTime = System.currentTimeMillis();
		lastAccessTime = creationTime;
		this.element = element;
	}

	/**
	 * Retrives the key
	 * @return
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * Retrives the Value
	 * @return
	 */
	public T getElement() {
		lastAccessTime = System.currentTimeMillis();
		return element;
	}

	/**
	 * Retrieves the creation time of this Element
	 * @return
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * Retrives the last access time 
	 * @return
	 */
	public long getLastUpdateTime() {
		return lastAccessTime;
	}
}
