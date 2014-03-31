package azkaban.utils.cache;

import static org.junit.Assert.*;
import org.junit.Test;

import azkaban.utils.cache.Cache.EjectionPolicy;

public class CacheManagerTest {
	@Test
	public void testLRU() {
		Cache cache = CacheManager.createCache();
		cache.setEjectionPolicy(EjectionPolicy.LRU);
		cache.setMaxCacheSize(4);

		cache.insertElement("key1", "val1");
		cache.insertElement("key2", "val2");
		cache.insertElement("key3", "val3");
		cache.insertElement("key4", "val4");

		assertEquals(cache.get("key2"), "val2");
		assertEquals(cache.get("key3"), "val3");
		assertEquals(cache.get("key4"), "val4");
		assertEquals(cache.get("key1"), "val1");
		assertEquals(4, cache.getSize());

		cache.insertElement("key5", "val5");
		assertEquals(4, cache.getSize());
		assertEquals(cache.get("key3"), "val3");
		assertEquals(cache.get("key4"), "val4");
		assertEquals(cache.get("key1"), "val1");
		assertEquals(cache.get("key5"), "val5");
		assertNull(cache.get("key2"));
	}

	@Test
	public void testFIFO() {
		Cache cache = CacheManager.createCache();
		cache.setEjectionPolicy(EjectionPolicy.FIFO);
		cache.setMaxCacheSize(4);

		cache.insertElement("key1", "val1");
		synchronized (this) {
			try {
				wait(10);
			} catch (InterruptedException e) {
			}
		}
		cache.insertElement("key2", "val2");
		cache.insertElement("key3", "val3");
		cache.insertElement("key4", "val4");

		assertEquals(cache.get("key2"), "val2");
		assertEquals(cache.get("key3"), "val3");
		assertEquals(cache.get("key4"), "val4");
		assertEquals(cache.get("key1"), "val1");
		assertEquals(4, cache.getSize());

		cache.insertElement("key5", "val5");
		assertEquals(4, cache.getSize());
		assertEquals(cache.get("key3"), "val3");
		assertEquals(cache.get("key4"), "val4");
		assertEquals(cache.get("key2"), "val2");
		assertEquals(cache.get("key5"), "val5");
		assertNull(cache.get("key1"));
	}

	@Test
	public void testTimeToLiveExpiry() {
		CacheManager.setUpdateFrequency(200);
		Cache cache = CacheManager.createCache();
		
		cache.setUpdateFrequencyMs(200);
		cache.setEjectionPolicy(EjectionPolicy.FIFO);
		cache.setExpiryTimeToLiveMs(4500);
		cache.insertElement("key1", "val1");

		synchronized (this) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
			}
		}
		assertEquals(cache.get("key1"), "val1");
		cache.insertElement("key2", "val2");
		synchronized (this) {
			try {
				wait(4000);
			} catch (InterruptedException e) {
			}
		}
		assertNull(cache.get("key1"));
		assertEquals("val2", cache.get("key2"));

		synchronized (this) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
			}
		}

		assertNull(cache.get("key2"));
	}

	@Test
	public void testIdleExpireExpiry() {
		CacheManager.setUpdateFrequency(250);
		Cache cache = CacheManager.createCache();
		
		cache.setUpdateFrequencyMs(250);
		cache.setEjectionPolicy(EjectionPolicy.FIFO);
		cache.setExpiryIdleTimeMs(4500);
		cache.insertElement("key1", "val1");
		cache.insertElement("key3", "val3");
		synchronized (this) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
			}
		}
		assertEquals(cache.get("key1"), "val1");
		cache.insertElement("key2", "val2");
		synchronized (this) {
			try {
				wait(4000);
			} catch (InterruptedException e) {
			}
		}
		assertEquals("val1", cache.get("key1"));
		assertNull(cache.get("key3"));
		synchronized (this) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
			}
		}

		assertNull(cache.get("key2"));
	}
}
