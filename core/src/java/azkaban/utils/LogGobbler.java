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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * Gobbler for Buffer
 * Reads the input and logs every line. 
 * 
 * Useful for attaching to stderr or stdout of a process
 */
public class LogGobbler extends Thread {
	private final BufferedReader inputReader;
	private final LogWrapper logWrapper;
	private boolean isShutdown = false;
	
	/**
	 * LogGobbler that takes log4j settings
	 * 
	 * @param inputReader
	 * @param logger
	 * @param level
	 */
	public LogGobbler(
			final Reader inputReader, 
			final org.apache.logging.log4j.Logger logger, 
			final org.apache.logging.log4j.Level level) 
	{
		this.inputReader = new BufferedReader(inputReader);
		logWrapper = new Log4jLogWrapper(logger, level);
	}
	
	/**
	 * Allow you to attach your own logger. The logger shouldn't block
	 * 
	 * @param inputReader
	 * @param logWrapper
	 */
	public LogGobbler(
			final Reader inputReader, 
			final LogWrapper logWrapper) 
	{
		this.inputReader = new BufferedReader(inputReader);
		this.logWrapper = logWrapper;
	}
	
	@Override
	public void run() {
		try {
			while (!isShutdown) {
				String line = inputReader.readLine();
				if (line == null) {
					return;
				}
				logWrapper.log(line);
			}
		} catch (IOException e) {
			logWrapper.error("Error reading from logging stream:", e);
		}
	}
	
	/**
	 * Shutsdown this gobbler.
	 * Ensure that the Reader is properly closed before this is called.
	 */
	public void shutdown() {
		this.isShutdown = true;
		synchronized(this) {
			this.interrupt();
		}
	}
	
	/**
	 * A log wrapper to abstract away logger implementations
	 */
	public static interface LogWrapper {
		public void error(String message, Exception e);
		
		public void info(String message, Exception e);
		
		public void log(String message);
	}

	/**
	 * Log4j wrapper for the log gobbler
	 */
	public static class Log4jLogWrapper implements LogWrapper {
		private final org.apache.logging.log4j.Logger logger;
		private final org.apache.logging.log4j.Level level;
		
		public Log4jLogWrapper(
				org.apache.logging.log4j.Logger logger, 
				org.apache.logging.log4j.Level level ) 
		{
			this.logger = logger;
			this.level = level;
		}
		
		@Override
		public void error(String message, Exception e) {
			logger.error(message, e);
		}

		@Override
		public void info(String message, Exception e) {
			logger.info(message, e);
		}

		@Override
		public void log(String message) {
			logger.log(level, logger);
		}
	}
	
	/**
	 * Null logger backed by circular buffer to see most recent logs
	 */
	public static class NullLogWrapper implements LogWrapper {
		private final CircularBuffer<String> buffer;
		
		public NullLogWrapper() {
			buffer = null;
		}
		
		public NullLogWrapper(int numLines) {
			buffer = new CircularBuffer<String>(numLines);
		}
		
		@Override
		public void error(String message, Exception e) {
			buffer.append(message);
		}

		@Override
		public void info(String message, Exception e) {
			buffer.append(message);
		}

		@Override
		public void log(String message) {
			buffer.append(message);
		}
		
		public String getRecentMessages() {
			StringBuffer strBuffer = new StringBuffer();
			Iterator<String> iter = buffer.iterator();
			while (iter.hasNext()) {
				String val = iter.next();
				strBuffer.append(val);
				strBuffer.append("\n");
			}
			
			return strBuffer.toString();
		}
	}
}
