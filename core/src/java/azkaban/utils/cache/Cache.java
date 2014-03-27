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

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple expiring in memory cache.
 * 
 * Internally it store a map of object keys to {@link azkaban.utils.Element}.
 * 
 * The eviction policies can be LRU, and FIFO. The evictions will not be
 * immediate, but instead will be eventual depending on the 
 * {@link azkaban.utils.CacheManager} settings.
 * 
 * This cache shouldn't be constructed by user, but should instead
 * be created using the {@link azkaban.utils.CacheManager}.
 */
public class Cache {
	private long nextUpdateTime = 0;
	private long updateFrequency = 1 * 60 * 1000;
	private int maxCacheSize = -1;

	private long expireTimeToLive = -1; // Never expires
	private long expireTimeToIdle = -1;

	private EjectionPolicy ejectionPolicy = EjectionPolicy.LRU;
	private final CacheManager manager;
	
	private Map<Object, Element<?>> elementMap = new ConcurrentHashMap<Object, Element<?>>();

	/**
	 * Eviction policies
	 */
	public enum EjectionPolicy {
		LRU, FIFO
	}

	/**
	 * Constructor for the Cache.
	 * Caches should only be created through the {@link azkaban.utils.CacheManager}
	 * @param manager
	 */
	/* package */Cache(CacheManager manager) {
		this.manager = manager;
	}

	/**
	 * Returns an object and attempts to cast to T.
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Object key) {
		Element<?> element = elementMap.get(key);
		if (element == null) {
			return null;
		}
		return (T) element.getElement();
	}

	/**
	 * Adds an item to the cache. 
	 * There is no checks for fullness. This may break size boundary conditions.
	 * 
	 * @param key
	 * @param item
	 */
	public <T> void put(Object key, T item) {
		Element<T> elem = new Element<T>(key, item);
		elementMap.put(key, elem);
	}

	/**
	 * Removes the element from the cache
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(Object key) {
		Element<?> elem = elementMap.remove(key);
		if (elem == null) {
			return false;
		}
		
		return true;
	}

	/**
	 * Sets the max size of the cache. It will attempt
	 * to evict older elements if full.
	 * 
	 * The default size is unbounded.
	 * 
	 * @param size The size of the cache, or -1 for unbounded.
	 * @return
	 */
	public Cache setMaxCacheSize(int size) {
		maxCacheSize = size;
		return this;
	}

	/**
	 * Sets the ejection policy for the cache. They can be either
	 * FIFO or LRU
	 * 
	 * @param policy
	 * @return
	 */
	public Cache setEjectionPolicy(EjectionPolicy policy) {
		ejectionPolicy = policy;
		return this;
	}

	/**
	 * Sets a guideline on how frequent to check for element
	 * expiry. The actual update will depend on how often the
	 * CacheManager will trigger an update.
	 * 
	 * @param updateFrequencyMs
	 * @return
	 */
	public Cache setUpdateFrequencyMs(long updateFrequencyMs) {
		this.updateFrequency = updateFrequencyMs;
		return this;
	}

	/**
	 * Sets how long an element can stick around until it is expired.
	 * The actual eviction depends on how often the CacheManager decides
	 * to call an eviction on the Cache.
	 * 
	 * @param time
	 * @return
	 */
	public Cache setExpiryTimeToLiveMs(long time) {
		this.expireTimeToLive = time;
		if (time > 0) {
			manager.update();
		}

		return this;
	}

	/**
	 * Sets the duration that an element can stay untouched before
	 * it is evicted. Actual eviction depends on how often the CacheManager decides
	 * to call an eviction on the Cache.
	 * 
	 * @param time
	 * @return
	 */
	public Cache setExpiryIdleTimeMs(long time) {
		this.expireTimeToIdle = time;
		if (time > 0) {
			manager.update();
		}
		return this;
	}

	/**
	 * Returns the number of elements currently in the cache
	 * @return
	 */
	public int getSize() {
		return elementMap.size();
	}

	/**
	 * Returns the lifespan of element in this cache
	 * @return
	 */
	public long getExpireTimeToLive() {
		return expireTimeToLive;
	}

	/**
	 * Returns how long an element can stay untouched in this cache
	 * @return
	 */
	public long getExpireTimeToIdle() {
		return expireTimeToIdle;
	}

	/**
	 * Inserts element into the cache.
	 * This will also attempt to evict if the element is bounded.
	 * 
	 * @param key
	 * @param item
	 */
	public synchronized <T> void insertElement(Object key, T item) {
		if (maxCacheSize < 0 || elementMap.size() < maxCacheSize) {
			Element<T> elem = new Element<T>(key, item);
			elementMap.put(key, elem);
		} else {
			internalExpireCache();

			Element<T> elem = new Element<T>(key, item);
			if (elementMap.size() < maxCacheSize) {
				elementMap.put(key, elem);
			} else {
				Element<?> element = getNextExpiryElement();
				if (element != null) {
					elementMap.remove(element.getKey());
				}

				elementMap.put(key, elem);
			}
		}
	}

	private Element<?> getNextExpiryElement() {
		if (ejectionPolicy == EjectionPolicy.LRU) {
			long latestAccessTime = Long.MAX_VALUE;
			Element<?> ejectionCandidate = null;
			for (Element<?> elem : elementMap.values()) {
				if (latestAccessTime > elem.getLastUpdateTime()) {
					latestAccessTime = elem.getLastUpdateTime();
					ejectionCandidate = elem;
				}
			}

			return ejectionCandidate;
		} else if (ejectionPolicy == EjectionPolicy.FIFO) {
			long earliestCreateTime = Long.MAX_VALUE;
			Element<?> ejectionCandidate = null;
			for (Element<?> elem : elementMap.values()) {
				if (earliestCreateTime > elem.getCreationTime()) {
					earliestCreateTime = elem.getCreationTime();
					ejectionCandidate = elem;
				}
			}
			return ejectionCandidate;
		}

		return null;
	}

	/**
	 * Evict expired elements from the queue.
	 * If the last time this method was called is less than the
	 * update frequency for this cache, it will do nothing.
	 * 
	 * Usually called by CacheManager.
	 */
	public synchronized void expireCache() {
		long currentTime = System.currentTimeMillis();
		if (nextUpdateTime < currentTime) {
			internalExpireCache();
			nextUpdateTime = currentTime + updateFrequency;
		}
	}

	private synchronized void internalExpireCache() {
		ArrayList<Element<?>> elems = new ArrayList<Element<?>>(elementMap.values());
		
		for (Element<?> elem : elems) {
			if (shouldExpire(elem)) {
				elementMap.remove(elem.getKey());
			}
		}
	}

	private boolean shouldExpire(Element<?> elem) {
		if (expireTimeToLive > -1) {
			if (System.currentTimeMillis() - elem.getCreationTime() > expireTimeToLive) {
				return true;
			}
		}
		if (expireTimeToIdle > -1) {
			if (System.currentTimeMillis() - elem.getLastUpdateTime() > expireTimeToIdle) {
				return true;
			}
		}

		return false;
	}
}
