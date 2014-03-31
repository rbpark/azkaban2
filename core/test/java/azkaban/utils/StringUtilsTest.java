package azkaban.utils;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testEscapeCharacters() {
		String test = "\"High my name is 'Richard'\" This is just a test\"";

		String expectedResult = "\\\"High my name is 'Richard'\\\" This is just a test\\\"";
		String result = StringUtils.escapeCharacters(test, '\"');
		assertEquals(expectedResult, result);
	}

	@Test
	public void testJoin() {		
		String[] testData = {
			"booo",
			"urns",
			"join",
			"this"
		};
		List<String> list = Arrays.asList(testData);
		String expected = "booo,urns,join,this";
		String result = StringUtils.join(list, ",");
		assertEquals(expected, result);
	}
	
	@Test
	public void testJoinEmpty() {		
		List<String> list = Collections.emptyList();
		String expected = "";
		String result = StringUtils.join(list, ",");
		assertEquals(expected, result);
	}
	
	@Test
	public void testJoinOne() {		
		String[] testData = {
			"booo"
		};
		List<String> list = Arrays.asList(testData);
		String expected = "booo";
		String result = StringUtils.join(list, ",");
		assertEquals(expected, result);
	}
	
	@Test
	public void testJoinLongDelimiter() {		
		String[] testData = {
			"booo",
			"urns",
			"join",
			"this"
		};
		List<String> list = Arrays.asList(testData);
		String expected = "booo._.urns._.join._.this";
		String result = StringUtils.join(list, "._.");
		assertEquals(expected, result);
	}
}
