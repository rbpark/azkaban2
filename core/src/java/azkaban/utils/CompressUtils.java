package azkaban.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * A util helper class for Compress data
 */
public class CompressUtils {
	
	private CompressUtils() {
	}
	
	/**
	 * Zips an input file to an output file
	 * 
	 * If given a directory, it will attempt to zip the whole directory
	 * 
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static void zip(File input, File output) throws IOException {
		FileOutputStream out = new FileOutputStream(output);
		ZipOutputStream zOut = new ZipOutputStream(out);
		zipFile("", input, zOut);
		zOut.close();
	}

	/**
	 * Attempt to zip every file or directory in a folder individually.
	 * 
	 * @param folder
	 * @param output
	 * @throws IOException
	 */
	public static void zipFolderContent(File folder, File output) throws IOException {
		FileOutputStream out = new FileOutputStream(output);
		ZipOutputStream zOut = new ZipOutputStream(out);
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				zipFile("", f, zOut);
			}
		}
		zOut.close();
	}

	private static void zipFile(String path, File input, ZipOutputStream zOut) throws IOException {
		if (input.isDirectory()) {
			File[] files = input.listFiles();
			if (files != null) {
				for (File f : files) {
					String childPath = path + input.getName()
							+ (f.isDirectory() ? "/" : "");
					zipFile(childPath, f, zOut);
				}
			}
		} else {
			String childPath = path + (path.length() > 0 ? "/" : "")
					+ input.getName();
			ZipEntry entry = new ZipEntry(childPath);
			zOut.putNextEntry(entry);
			InputStream fileInputStream = new BufferedInputStream(
					new FileInputStream(input));
			IOUtils.copy(fileInputStream, zOut);
			fileInputStream.close();
		}
	}

	/**
	 * Unzips a zip file to a destination folder.
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public static void unzip(ZipFile source, File dest) throws IOException {
		Enumeration<?> entries = source.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			File newFile = new File(dest, entry.getName());
			if (entry.isDirectory()) {
				newFile.mkdirs();
			} else {
				newFile.getParentFile().mkdirs();
				InputStream src = source.getInputStream(entry);
				OutputStream output = new BufferedOutputStream(
						new FileOutputStream(newFile));
				IOUtils.copy(src, output);
				src.close();
				output.close();
			}
		}
	}
	
	/**
	 * Returns a byte array of a gzip string
	 * The gzip will be applied on the String's encoded bytes
	 * 
	 * @param str The string to gzip
	 * @param encType The string encoding type to be gzip
	 * @return
	 * @throws IOException
	 */
	public static byte[] gzipString(String str, String encType) throws IOException {
		byte[] stringData = str.getBytes(encType);
		
		return gzipBytes(stringData);
	}
	
	/**
	 * Returns a byte array of the gzipped bytes
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static byte[] gzipBytes(byte[] bytes) throws IOException {
		return gzipBytes(bytes, 0, bytes.length);
	}
	
	/**
	 * Gzip a sub array of the bytes. Returns a new byte array
	 * 
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return
	 * @throws IOException
	 */
	public static byte[] gzipBytes(byte[] bytes, int offset, int length) throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipStream = null;

		gzipStream = new GZIPOutputStream(byteOutputStream);

		gzipStream.write(bytes, offset, length);
		gzipStream.close();
		return byteOutputStream.toByteArray();
	}
	
	/**
	 * Expands a byte array encoded in gzip
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static byte[] unGzipBytes(byte[] bytes) throws IOException {
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
		GZIPInputStream gzipInputStream = new GZIPInputStream(byteInputStream);
		
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		IOUtils.copy(gzipInputStream, byteOutputStream);

		return byteOutputStream.toByteArray();
	}
	
	/**
	 * Expands a byte array encoded in gzip to a String.
	 * The encoding must be correct or the output string may be garbage.
	 * 
	 * @param bytes
	 * @param encType
	 * @return
	 * @throws IOException
	 */
	public static String unGzipString(byte[] bytes, String encType) throws IOException {
		byte[] response = unGzipBytes(bytes);
		return new String(response, encType);
	}
}