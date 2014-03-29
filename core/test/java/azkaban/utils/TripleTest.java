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

public class TripleTest {
	@Test
	public void testTriple() {
		Triple<Integer, String, String> triple1 = new Triple<Integer, String, String>(0, "I'm A String", "boo");

		Assert.assertEquals(new Integer(0), triple1.getFirst());
		Assert.assertEquals(new String("I'm A String"), triple1.getSecond());
		Assert.assertEquals(new String("boo"), triple1.getThird());
		Assert.assertEquals(triple1, triple1);
	}

	@Test
	public void testHashcodeEqualsTriple() {
		// triple 1 and triple 2 should be same in every way.
		Triple<Integer, String, String> triple1 = new Triple<Integer, String, String>(0, "I'm A String", "boo");
		Triple<Integer, String, String> triple2 = new Triple<Integer, String, String>(0, new String("I'm A String"), new String("boo"));
		Assert.assertEquals(triple1, triple2);
		Assert.assertEquals(triple1.hashCode(), triple2.hashCode());

		Triple<Integer, String, String> triple3 = new Triple<Integer, String, String>(
				1, 
				new String("I'm A String"),
				new String("boo")
		);
		Assert.assertNotEquals(triple1, triple3);

		Triple<Integer, String, String> triple4 = new Triple<Integer, String, String>(
				0,
				new String("I'm A Str1ng"),
				new String("boo")
		);
		Assert.assertNotEquals(triple1, triple4);
		
		Triple<Integer, String, String> triple5 = new Triple<Integer, String, String>(
				0,
				new String("I'm A String"),
				new String("boon")
		);
		Assert.assertNotEquals(triple1, triple5);
	}
	
	@Test
	public void testTripleNull() {
		Triple<Integer, Integer, Integer> triple1 = new Triple<Integer, Integer, Integer>(0, 0, null);
		Triple<Integer, Integer, Integer> triple2 = new Triple<Integer, Integer, Integer>(0, 0, null);
		Assert.assertEquals(triple1, triple2);
		Assert.assertEquals(triple1.hashCode(), triple2.hashCode());
		
		Triple<Integer, Integer, Integer> triple3 = new Triple<Integer, Integer, Integer>(0, null, 0);
		Triple<Integer, Integer, Integer> triple4 = new Triple<Integer, Integer, Integer>(0, null, 0);
		Assert.assertEquals(triple3, triple4);
		Assert.assertEquals(triple3.hashCode(), triple4.hashCode());

		Triple<Integer, Integer, Integer> triple5 = new Triple<Integer, Integer, Integer>(null, 0, 0);
		Triple<Integer, Integer, Integer> triple6 = new Triple<Integer, Integer, Integer>(null, 0, 0);
		Assert.assertEquals(triple5, triple6);
		Assert.assertEquals(triple5.hashCode(), triple6.hashCode());
	}
}