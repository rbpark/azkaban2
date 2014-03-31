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

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;

import org.junit.Test;

/**
 * Test log gobbler
 */
public class LogGobblerTest {
	@Test
	public void testLogGobbler() throws IOException {
		PipedReader reader = new PipedReader();
		PipedWriter writer = new PipedWriter(reader);
		LogGobbler.NullLogWrapper wrapper = new LogGobbler.NullLogWrapper(10);
		
		LogGobbler gobbler = new LogGobbler(reader, wrapper);
		gobbler.start();
		
		PrintWriter p = new PrintWriter(writer);
		p.println("line 1");
		p.println("line 2");
		p.println("line 3");
		
		try {
			synchronized(this) {
				wait(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		p.close();
		reader.close();
		writer.close();
		
		gobbler.shutdown();
		try {
			synchronized(this) {
				wait(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse(gobbler.isAlive());
		
		// Check that everything was properly set
		String messages = wrapper.getRecentMessages();
		String expectedMessage = "line 1\nline 2\nline 3\n";
		assertEquals(expectedMessage, messages);
	}
	
}
