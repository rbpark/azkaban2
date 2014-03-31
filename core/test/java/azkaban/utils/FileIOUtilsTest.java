package azkaban.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileIOUtilsTest {
	private static File tempDir = new File("build/symlinkTest");
	File sourceDir = new File("test/data/symlinkData");
	
	@Before
	public void setUp() throws Exception {
		tempDir.mkdirs();
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(tempDir);
	}

	@Test
	public void testSymlinkCopy() {
		boolean exception = false;
		try {
			FileIOUtils.createDeepSymlink(sourceDir, tempDir);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Handled this case nicely.");
			exception = true;
		}
		
		assertFalse(exception);
	}
	
	@Test
	public void testSymlinkCopyNonSource() {
		boolean exception = false;
		try {
			FileIOUtils.createDeepSymlink(new File(sourceDir, "idonotexist"), tempDir);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Handled this case nicely.");
			exception = true;
		}
		
		assertTrue(exception);
	}
	
	@Test
	public void testAsciiUTF() throws IOException {
		String foreignText = "abcdefghijklmnopqrstuvwxyz";
		byte[] utf8ByteArray = createUTF8ByteArray(foreignText);
		
		int length = utf8ByteArray.length;
		System.out.println("char length:" + foreignText.length() + " utf8BytesLength:" + utf8ByteArray.length + " for:" + foreignText);
		
		Pair<Integer,Integer> pair = FileIOUtils.getUtf8Range(utf8ByteArray, 1, length - 6);
		System.out.println("Pair :" + pair.toString());
		
		String recreatedString = new String(utf8ByteArray, 1, length - 6, "UTF-8");
		System.out.println("recreatedString:" + recreatedString);
		
		String correctString = new String(utf8ByteArray, pair.getFirst(), pair.getSecond(), "UTF-8");
		System.out.println("correctString:" + correctString);
		
		assertEquals(pair, new Pair<Integer,Integer>(1, 20));
		// Two characters stripped from this.
		assertEquals(correctString.length(), foreignText.length() - 6);
		
	}
	
	@Test
	public void testForeignUTF() throws IOException {
		String foreignText = "안녕하세요, 제 이름은 박병호입니다";
		byte[] utf8ByteArray = createUTF8ByteArray(foreignText);
		
		int length = utf8ByteArray.length;
		System.out.println("char length:" + foreignText.length() + " utf8BytesLength:" + utf8ByteArray.length + " for:" + foreignText);
		
		Pair<Integer,Integer> pair = FileIOUtils.getUtf8Range(utf8ByteArray, 1, length - 6);
		System.out.println("Pair :" + pair.toString());
		
		String recreatedString = new String(utf8ByteArray, 1, length - 6, "UTF-8");
		System.out.println("recreatedString:" + recreatedString);
		
		String correctString = new String(utf8ByteArray, pair.getFirst(), pair.getSecond(), "UTF-8");
		System.out.println("correctString:" + correctString);
		
		assertEquals(new Pair<Integer,Integer>(2, 91), pair);
		// Two characters stripped from this.
		assertEquals(foreignText.length() - 4, correctString.length());
	
		
		// Testing mixed bytes
		String mixedText = "abc안녕하세요, 제 이름은 박병호입니다";
		byte[] mixedBytes = createUTF8ByteArray(mixedText);
		Pair<Integer,Integer> pair2 = FileIOUtils.getUtf8Range(mixedBytes, 1, length - 4);
		correctString = new String(mixedBytes, pair2.getFirst(), pair2.getSecond(), "UTF-8");
		System.out.println("correctString:" + correctString);
		assertEquals(pair2, new Pair<Integer,Integer>(1, 95));
		// Two characters stripped from this.
		assertEquals(mixedText.length() - 4, correctString.length());
		
	}
	
	private byte[] createUTF8ByteArray(String text) {
		byte[] textBytes= null;
		try {
			textBytes = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return textBytes;
	}
}