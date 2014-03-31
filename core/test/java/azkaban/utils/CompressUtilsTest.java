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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CompressUtilsTest {
	private static HashSet<String> expectedFiles;
	private static File tempDir = new File("build/compressTest");
	private File zipFile;
	private File decompressDir = new File("build/decompressTest");
	
	@BeforeClass
	public static void globalSetUp() throws Exception {
		tempDir.mkdirs();
		expectedFiles = new HashSet<String>();
		expectedFiles.add("compressData/testfile1");
		expectedFiles.add("compressData/testfile2");
		expectedFiles.add("compressData/testfolder1/testfile3");
		expectedFiles.add("compressData/testfolder1/testfile4");
	}
	
	@AfterClass
	public static void globalTearDown() throws Exception {
		FileUtils.deleteDirectory(tempDir);
	}
	
	@Before
	public void setup() throws Exception {
		zipFile = new File(tempDir, "zip-create-test.zip");
		decompressDir.mkdirs();
	}
	
	@After
	public void tearDown() throws Exception {
		zipFile.delete();
		FileUtils.deleteDirectory(decompressDir);
	}
	
	@Test
	public void testZipFile() throws IOException {
		File dataDir = new File("test/data/compressData");
		// Compress dir
		CompressUtils.zip(dataDir, zipFile);
		assertTrue(zipFile.length() > 0);
		
		// Test that the zip file contains the expected # of files
		ZipFile zFile = new ZipFile(zipFile);
		zFile.size();
		assertEquals(4, zFile.size());
		
		// Test that all files are expected, and no extras
		HashSet<String> files = new HashSet<String>(expectedFiles);		
		@SuppressWarnings("rawtypes")
		Enumeration iter = zFile.entries();
		while (iter.hasMoreElements()) {
			ZipEntry zobj = (ZipEntry)iter.nextElement();
			assertTrue(files.contains(zobj.getName()));
			files.remove(zobj.getName());
		}
		assertTrue(files.isEmpty());
		
		// Check results of decompressing it.
		CompressUtils.unzip(zFile, decompressDir);
		HashSet<String> expected = new HashSet<String>(expectedFiles);
		for (String unzipFile: expected) {
			File ufile = new File(decompressDir, unzipFile);
			assertTrue(ufile.exists());
		}
	}
	
	@Test
	public void testGzipStr() throws IOException {
		String myString = "Hi my name is Richard. I wrote this test. Here's some weird characters: " +
						"내 이름은 박병호 입니다.";
		byte[] stringBytes = CompressUtils.gzipString(myString, "UTF-8");
		assertTrue(stringBytes.length > myString.length());
		
		String otherString = CompressUtils.unGzipString(stringBytes, "UTF-8");
		assertEquals(myString, otherString);
	}
	
	@Test
	public void testGzipByteRange() throws IOException {
		String myString = "Hi my name is Richard. I wrote this test.";
		byte[] bytes = myString.getBytes("UTF-8");
		byte[] subbytes = CompressUtils.gzipBytes(bytes, 14, 7);
		
		String otherString = CompressUtils.unGzipString(subbytes, "UTF-8");
		assertEquals("Richard", otherString);
	}
}
