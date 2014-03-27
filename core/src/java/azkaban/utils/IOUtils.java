package azkaban.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * Random utils used for IO and data conversion
 *
 */
public class IOUtils {
	private static final Random RANDOM = new Random();
	
	private IOUtils() {
	}
	
	/**
	 * Equivalent to Object.equals except that it handles nulls. If a and b are
	 * both null, true is returned.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(Object a, Object b) {
		if (a == null || b == null) {
			return a == b;
		}

		return a.equals(b);
	}

	/**
	 * Return the object if it is non-null, otherwise throw an exception
	 * 
	 * @param <T>
	 *            The type of the object
	 * @param t
	 *            The object
	 * @return The object if it is not null
	 * @throws IllegalArgumentException
	 *             if the object is null
	 */
	public static <T> T nonNull(T t) {
		if (t == null) {
			throw new IllegalArgumentException("Null value not allowed.");
		} else {
			return t;
		}
	}
	

	/**
	 * Print the message and then exit with the given exit code
	 * 
	 * @param message
	 *            The message to print
	 * @param exitCode
	 *            The exit code
	 */
	public static void croak(String message, int exitCode) {
		System.err.println(message);
		System.exit(exitCode);
	}

	/**
	 * Creates a tempory directory in the directory defined in the 
	 * property 'java.io.tmpdir'
	 * 
	 * @return
	 */
	public static File createTempDir() {
		return createTempDir(new File(System.getProperty("java.io.tmpdir")));
	}

	/**
	 * Creates a temporary file within the specified directory
	 * 
	 * @param parent
	 * @return
	 */
	public static File createTempDir(File parent) {
		File temp = new File(parent,
				Integer.toString(Math.abs(RANDOM.nextInt()) % 100000000));
		temp.delete();
		temp.mkdir();
		temp.deleteOnExit();
		return temp;
	}

	/**
	 * Move data from one stream to another. 
	 * Probably should create channels if we can
	 * 
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static void copyStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	/**
	 * Attempts to convert an object (string or number) to a Double
	 * @param obj
	 * @return
	 */
	public static Double convertToDouble(Object obj) {
		if (obj instanceof String) {
			return Double.parseDouble((String) obj);
		}

		return (Double) obj;
	}
}
