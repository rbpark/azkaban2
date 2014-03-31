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

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Iterator;

/**
 * Tests the circular buffer
 */
public class CircularBufferTest {
	@Test
	public void testCircularBuffer() {
		Object[] testObjArray = new Object[] {
			"1", 1, 0.1,
			"2", 2, 0.2, 
			"3", 3, 0.3,
			"4", 4, 0.4
		};
		
		CircularBuffer<Object> buffer = new CircularBuffer<Object>(5);
		for (int i = 0; i < 5; ++i) {
			buffer.append(testObjArray[i]);
		}
		
		Iterator<Object> iter = buffer.iterator();
		for (int i = 0; i < 5; ++i) {
			Object obj = iter.next();
			assertEquals(testObjArray[i], obj);
		}
		assertFalse(iter.hasNext());
		
		// Test contents after push
		for (int i = 5; i < 7; ++i) {
			buffer.append(testObjArray[i]);
		}
		// Looking at shifted amounts
		iter = buffer.iterator();
		for (int i = 2; i < 7; ++i) {
			Object obj = iter.next();
			assertEquals(testObjArray[i], obj);
		}
		assertFalse(iter.hasNext());
	}
}
