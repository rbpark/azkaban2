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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * Gobbler for Buffer
 * Reads the input and logs every line. 
 * 
 * Useful for attaching to stderr or stdout of a process
 */
public class LogGobbler extends Thread {
	private final BufferedReader inputReader;
	private final Logger logger;
	private final Level loggingLevel;

	public LogGobbler(final Reader inputReader, final Logger logger, final Level level) {
		this.inputReader = new BufferedReader(inputReader);
		this.logger = logger;
		this.loggingLevel = level;
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				String line = inputReader.readLine();
				if (line == null) {
					return;
				}
				log(line);
			}
		} catch (IOException e) {
			error("Error reading from logging stream:", e);
		}
	}

	private void log(String message) {
		if (logger != null) {
			logger.log(loggingLevel, message);
		}
	}
	
	private void error(String message, Exception e) {
		if (logger != null) {
			logger.error(message, e);
		}
	}
	
	private void info(String message, Exception e) {
		if (logger != null) {
			logger.info(message, e);
		}
	}
	
	public void awaitCompletion(final long waitMs) {
		try {
			join(waitMs);
		} catch (InterruptedException e) {
			info("I/O thread interrupted.", e);
		}
	}
}
