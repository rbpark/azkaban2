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

import org.junit.Assert;

import org.junit.Test;

public class PairTest {
	@Test
	public void testPair() {
		Pair<Integer, String> pair1 = new Pair<Integer,String>(0, "I'm A String");
		Assert.assertEquals("{0,I'm A String}", pair1.toString());
		Assert.assertEquals(new Integer(0), pair1.getFirst());
		Assert.assertEquals(new String("I'm A String"), pair1.getSecond());
		Assert.assertEquals(pair1, pair1);
	}
	
	@Test
	public void testHashcodeEqualsPair() {
		// Pair 1 and pair 2 should be same in every way.
		Pair<Integer, String> pair1 = new Pair<Integer,String>(0, "I'm A String");
		Pair<Integer, String> pair2 = new Pair<Integer,String>(0, new String("I'm A String"));
		Assert.assertEquals(pair1, pair2);
		Assert.assertEquals(pair1.hashCode(), pair2.hashCode());
		
		Pair<Integer, String> pair3 = new Pair<Integer,String>(1, new String("I'm A String"));
		Assert.assertNotEquals(pair1, pair3);
		
		Pair<Integer, String> pair4 = new Pair<Integer,String>(0, new String("I'm A Strong"));
		Assert.assertNotEquals(pair1, pair4);
	}
	
	@Test
	public void testNulls() {
		// Pair 1 and pair 2 should be same in every way.
		Pair<Integer, String> pair1 = new Pair<Integer,String>(0, null);
		Pair<Integer, String> pair2 = new Pair<Integer,String>(0, null);
		Assert.assertEquals(pair1, pair2);
		Assert.assertEquals(pair1.hashCode(), pair2.hashCode());
		Assert.assertNull(pair1.getSecond());
		Assert.assertEquals("{0,null}", pair1.toString());
		
		Pair<Integer, String> pair3 = new Pair<Integer,String>(null, new String("I'm A String"));
		Pair<Integer, String> pair4 = new Pair<Integer,String>(null, new String("I'm A String"));
		Assert.assertEquals(pair3, pair4);
		Assert.assertEquals(pair3.hashCode(), pair4.hashCode());
		Assert.assertNull(pair3.getFirst());
		Assert.assertEquals("{null,I'm A String}", pair3.toString());
	}
}
