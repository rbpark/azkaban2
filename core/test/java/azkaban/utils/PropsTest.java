package azkaban.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

public class PropsTest {

	@Test
	public void testBasic() {
		Props expected = createSampleProps();

		assertEquals("goodbye", expected.getString("hello"));
		assertEquals("test", expected.getString("my"));
		assertEquals("mystring", expected.getString("string"));
		assertEquals(1, expected.getInt("int"));
		assertEquals(1l, expected.getLong("long"));
		assertEquals(1.0d, expected.getDouble("double"), 0.0001);
		assertEquals(true, expected.getBoolean("bool"));
		
		// Test non existing key. get() should work. getType() shouldn't
		boolean exception = false;
		try {
			expected.getString("KEY-DOESNT-EXIST");
		}
		catch (UndefinedPropertyException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(expected.get("KEY-DOESNT-EXIST"));
		
		// Test incorrect type coercion
		exception = false;
		try {
			expected.getDouble("string");
		}
		catch (NumberFormatException e) {
			exception = true;
		}
		assertTrue(exception);
		
		// Test default values
		assertEquals(2, expected.getInt("notint", 2));
		assertEquals(1.1, expected.getDouble("notdouble", 1.1), 0.0001);
		assertEquals("momo", expected.getString("notString", "momo"));
		//These exist, so the default shouldn't be expected
		assertEquals("mystring", expected.getString("string", "moo"));
		assertEquals(1, expected.getInt("int", 2));
		assertEquals(1l, expected.getLong("long", 2));
		assertEquals(1.0d, expected.getDouble("double", 2.1), 0.0001);
	}

	@Test
	public void testFileConstructor() throws IOException {
		// Sample created on the base test/data/propsTest/test.properties
		Props base = createSampleProps();
		assertNull(base.getSource());
		
		// Test the string file path constructor
		final String filePath = "test/data/propsTest/test.properties";
		Props other = new Props(base.getParent(), "test/data/propsTest/test.properties");
		assertEquals("goodbye", other.getString("hello"));
		assertEquals("test", other.getString("my"));
		assertEquals("mystring", other.getString("string"));
		assertEquals(1, other.getInt("int"));
		assertEquals(1l, other.getLong("long"));
		assertEquals(1.0d, other.getDouble("double"), 0.0001);
		assertEquals(true, other.getBoolean("bool"));
		assertTrue(base.equalsProps(other));
		// If created by file/path, the source of the props should be set
		assertEquals("test/data/propsTest/test.properties", other.getSource());
		
		// Test File constructor
		File propsFile = new File(filePath);
		Props other2 = new Props(base.getParent(), propsFile);
		assertTrue(base.equalsProps(other2));
		assertEquals("test/data/propsTest/test.properties", other2.getSource());
		
		// Test Input stream 
		FileInputStream stream = new FileInputStream(propsFile);
		Props other3 = new Props(base.getParent(), stream);
		stream.close();
		assertTrue(base.equalsProps(other3));
		other3.setSource("test/data/propsTest/test.properties");
		assertEquals("test/data/propsTest/test.properties", other3.getSource());
	}
	
	@Test
	public void testOtherConstructors() throws IOException {
		// Sample created on the base test/data/propsTest/test.properties
		Props base = createSampleProps();
		
		// Test map input creation
		Map<String,String> propsMap = new HashMap<String, String>();
		for (String key : base.getLocalKeySet()) {
			propsMap.put(key, base.get(key));
		}
		@SuppressWarnings("unchecked")
		Props other = new Props(base.getParent(), propsMap);
		assertTrue(base.equalsProps(other));
		
		// Test out properties constructor
		Properties props1 = new Properties();
		Properties props2 = new Properties();
		props1.put("hello", "goodbye");
		props1.put("my", "test");
		props1.put("int", "1");
		props2.put("long", "1");
		props2.put("double", "1.0");
		props2.put("bool", "true");
		Props other2 = new Props(base.getParent(), props1, props2);
		assertTrue(base.equalsProps(other2));
		
		// Test Props Props constructor
		Props mainProps = new Props();
		mainProps.put("hello", "goodbye");
		mainProps.put("my", "test");
		mainProps.put("int", "1");
		mainProps.put("long", "1");
		mainProps.put("double", "1.0");
		mainProps.put("bool", "true");
		Props other3 = new Props(base.getParent(), mainProps);
		assertTrue(base.equalsProps(other3));
	}
	
	@Test
	public void testGetSetEarliestAncestors() {
		// Sample created on the base test/data/propsTest/test.properties
		Props base = createSampleProps();
		assertTrue(base.getEarliestAncestor() == base.getParent());
		
		Props newAncestor = new Props();
		newAncestor.put("ancestor", "me");
		base.setEarliestAncestor(newAncestor);
		assertTrue(base.getEarliestAncestor() == newAncestor);
		assertEquals("me", base.getString("ancestor"));
	}
	
	@Test
	public void testGetClearLocal() {
		// Sample created on the base test/data/propsTest/test.properties
		Props base = createSampleProps();
		
		Set<String> set = base.getLocalKeySet();
		assertEquals(6, set.size());
		assertEquals("goodbye", base.getString("hello"));
		assertEquals("test", base.getString("my"));
		
		base.clearLocal();
		set = base.getLocalKeySet();
		assertEquals(0, set.size());
		assertEquals("yolo", base.getString("hello"));
		assertFalse(base.containsKey("my"));
	}
	
	@Test
	public void testSetGetParents() {
		// Sample created on the base test/data/propsTest/test.properties
		Props base = createSampleProps();
		assertEquals("goodbye", base.getString("hello"));
		assertEquals("mystring", base.getString("string"));
		assertEquals(7, base.size());
		
		Props parent = base.getParent();
		assertEquals("yolo", parent.getString("hello"));
		assertEquals("mystring", parent.getString("string"));
		assertEquals(2, parent.size());
		
		// Remove the parents
		base.setParent(null);
		assertNull(base.getParent());
		assertNull(base.getString("string", null));
		assertEquals(6, base.size());
		
		// Reset the parents, should be back to normal
		base.setParent(parent);
		assertEquals("goodbye", base.getString("hello"));
		assertEquals("mystring", base.getString("string"));
		assertEquals(7, base.size());
	}
	
	@Test
	public void testClone() {
		// Sample created on the base test/data/propsTest/test.properties
		Props base = createSampleProps();
		Props clone = Props.clone(base);
		
		assertTrue(base.equalsProps(clone));
		assertFalse(base == clone);
	}
	
	// @TODO Write test for writing out Props files.
	
	private static Props createSampleProps() {
		Props parent = new Props();
		parent.put("hello", "yolo");
		parent.put("string", "mystring");
		
		// Mirrors data/propTest/test.properties
		Props props = new Props(parent);
		props.put("hello", "goodbye");
		props.put("my", "test");
		props.put("int", "1");
		props.put("long", "1");
		props.put("double", "1.0");
		props.put("bool", "true");
		return props;
	}
}
