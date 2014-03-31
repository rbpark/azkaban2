package azkaban.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class ReflectUtilsTest {
	public static String myStaticFunction() {
		return "called()";
	}
	
	public static String myStaticFunction(String a, Integer b) {
		return "called(" + a + "," + b + ")";
	}
	
	@Test
	public void testGetTypes() {
		Object[] args = {
			"I'm A String",
			1, // Integer
			new ArrayList<Double>(),
			new HashMap<String,String>()
		};
		
		Class<?>[] argClasses = ReflectUtils.getTypes(args);
		Class<?>[] expected = {
			String.class,
			Integer.class,
			ArrayList.class,
			HashMap.class
		};
		
		assertArrayEquals(expected, argClasses);
	}

	@Test
	public void testGetTypesSingle() {
		Object[] args = {
			"I'm A String"
		};
		
		Class<?>[] argClasses = ReflectUtils.getTypes(args);
		Class<?>[] expected = {
			String.class
		};
		
		assertArrayEquals(expected, argClasses);
	}
	
	@Test
	public void testCallConstructorBase() {
		// Base constructor called
		TestClass retBase = (TestClass)ReflectUtils.callConstructor(TestClass.class);
		TestClass expectBase = new TestClass();
		assertEquals(retBase, expectBase);
	}
	
	@Test
	public void testCallConstructorNotFound() {
		// Base constructor called
		try {
			ReflectUtils.callConstructor(TestClass.class, 1);
		} catch(Exception e) {
			return;
		}
		
		fail("An exception should've been thrown but has not been");
	}
	
	
	@Test
	public void testCallConstructorOneArg() {
		// Base constructor called
		TestClass retBase = (TestClass)ReflectUtils.callConstructor(TestClass.class,"hello");
		TestClass expectBase = new TestClass("hello");
		assertEquals(expectBase, retBase);
	}
	
	@Test
	public void testCallConstructorMultiArg() {
		// Base constructor called
		TestClass retBase = (TestClass)ReflectUtils.callConstructor(TestClass.class, 1, "hello");
		TestClass expectBase = new TestClass(1, "hello");
		assertEquals(expectBase, retBase);
	}
	
	@Test
	public void testCallConstructorComplexArgs() {
		// Base constructor called
		ArrayList<String> complex = new ArrayList<String>();
		complex.add("2");
		complex.add("goodbye");
		
		Class<?>[] argTypes = {List.class};
		Object[] args = {complex};
		TestClass retBase = (TestClass)ReflectUtils.callConstructor(TestClass.class, argTypes, args);
		TestClass expectBase = new TestClass(2, "goodbye");
		assertEquals(expectBase, retBase);
	}
	
	@Test
	public void testInvokeStatic() throws Exception {
		String result = (String)ReflectUtils.invokeStaticMethod(
				this.getClass().getClassLoader(), 
				"azkaban.utils.ReflectUtilsTest", 
				"myStaticFunction"
			);
		
		assertEquals("called()", result);
		
		result = (String)ReflectUtils.invokeStaticMethod(
				this.getClass().getClassLoader(), 
				"azkaban.utils.ReflectUtilsTest", 
				"myStaticFunction",
				"bee",
				0
			);
		assertEquals("called(bee,0)", result);
	}
	
	/**
	 * Class used to test Constructor calls
	 */
	public static class TestClass {
		private final Integer val;
		private final String str;
		
		public TestClass() {
			this(null, null);
		}
		
		public TestClass(String str) {
			this(null, str);
		}
		
		public TestClass(Integer val, String str) {
			this.val = val;
			this.str = str;
		}

		public TestClass(List<String> complex) {
			this(Integer.parseInt(complex.get(0)),complex.get(1));
		}
		
		@Override
		public String toString() {
			return val + ":" + str;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((str == null) ? 0 : str.hashCode());
			result = prime * result + ((val == null) ? 0 : val.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestClass other = (TestClass) obj;
			if (str == null) {
				if (other.str != null)
					return false;
			} else if (!str.equals(other.str))
				return false;
			if (val == null) {
				if (other.val != null)
					return false;
			} else if (!val.equals(other.val))
				return false;
			return true;
		}
	}
	
}