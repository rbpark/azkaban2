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

import java.util.Set;
import java.util.HashSet;

/**
 * Manager that creates caches and drives the Cache's expiry code.
 * 
 * This is a singleton object.
 */
public class CacheManager {
	// Thread that expires caches at
	private static final long UPDATE_FREQUENCY = 30000; // Every 30 sec by default.

	private long updateFrequency = UPDATE_FREQUENCY;
	private Set<Cache> caches;

	private static CacheManager manager = new CacheManager();
	private final CacheManagerThread updaterThread;

	private boolean activeExpiry = false;

	/**
	 * Gets the singleton instance of the CacheManager
	 * @return
	 */
	public static CacheManager getInstance() {
		return manager;
	}
	
	private CacheManager() {
		updaterThread = new CacheManagerThread();
		caches = new HashSet<Cache>();

		updaterThread.start();
	}

	/**
	 * Set how often the CacheManager will invoke an eviction call
	 * on its Caches.
	 * 
	 * The default is 30 seconds
	 * 
	 * @param updateFreqMs Milliseconds between eviction calls.
	 */
	public static void setUpdateFrequency(long updateFreqMs) {
		getInstance().internalUpdateFrequency(updateFreqMs);
	}

	/**
	 * Shuts down the CacheManager thread. Once dead, it cannot be restarted.
	 */
	public static void shutdown() {
		getInstance().internalShutdown();
	}

	/**
	 * Creates a cache with a given name. Names must be unique
	 * 
	 * @param name
	 * @return
	 * @throws CacheException A cache with that name already exists.
	 */
	public static Cache createCache() {
		Cache cache = new Cache(getInstance());
		getInstance().internalAddCache(cache);
		return cache;
	}

	/**
	 * Removes cache. 
	 * The cache will still be functional, but it will not auto
	 * evict Elements anymore
	 * 
	 * @param cacheName
	 */
	public static void removeCache(Cache cache) {
		getInstance().internalRemoveCache(cache);
	}

	
	private void internalUpdateFrequency(long updateFreq) {
		updateFrequency = updateFreq;
		updaterThread.interrupt();
	}

	private synchronized void internalAddCache(Cache cache) {
		caches.add(cache);
		updaterThread.interrupt();
	}
	
	private synchronized void internalRemoveCache(Cache cache) {
		caches.remove(cache);
	}

	private synchronized void internalShutdown() {
		updaterThread.shutdown();
	}

	/**
	 * Called by the Cache if its expiry time changes.
	 * 
	 * May invoke early eviction in the Cache
	 */
	/* package */synchronized void update() {
		boolean activeExpiry = false;
		for (Cache cache : caches) {
			if (cache.getExpireTimeToIdle() > 0
					|| cache.getExpireTimeToLive() > 0) {
				activeExpiry = true;
				break;
			}
		}

		if (this.activeExpiry != activeExpiry && activeExpiry) {
			this.activeExpiry = activeExpiry;
			updaterThread.interrupt();
		}
	}

	/**
	 * Thread that handles the update frequency of the Caches
	 */
	private class CacheManagerThread extends Thread {
		private boolean shutdown = false;

		public void run() {
			while (!shutdown) {
				if (activeExpiry) {
					for (Cache cache : caches) {
						cache.expireCache();
					}

					synchronized (this) {
						try {
							wait(updateFrequency);
						} catch (InterruptedException e) {
						}
					}
				} else {
					synchronized (this) {
						try {
							wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}

		public void shutdown() {
			this.shutdown = true;
			updaterThread.interrupt();
		}
	}
}
