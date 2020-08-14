package com.antkorwin.ioutils.resourcefile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.antkorwin.ioutils.error.InternalException;
import com.antkorwin.throwable.functions.ThrowableSupplier;
import org.apache.commons.io.IOUtils;

/**
 * Created on 10/08/2020
 * <p>
 * Tools to read files in resources
 *
 * @author Korovin Anatoliy
 */
public class ResourceFile {

	private final String fileName;

	public ResourceFile(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Read a file content to the String value
	 *
	 * @return file content as string
	 * @see ResourceFile#readAsString()
	 */
	@Deprecated
	public String read() {
		try (InputStream inputStream = getResourceStream()) {
			return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new InternalException("Error while reading the data from file: " + fileName, e);
		}
	}

	/**
	 * Read a file content to string value
	 *
	 * @return file content as String
	 */
	public String readAsString() {
		try (InputStream inputStream = getResourceStream()) {
			return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new InternalException("Error while reading the data from file: " + fileName, e);
		}
	}

	/**
	 * Read a file content as byte array
	 *
	 * @return file content as byte array
	 */
	public byte[] readAsByteArray() {
		try (InputStream inputStream = getResourceStream()) {
			return IOUtils.toByteArray(inputStream);
		} catch (Exception e) {
			throw new InternalException("Error while reading the data from file: " + fileName, e);
		}
	}

	/**
	 * Write a file content to the OutputStream
	 *
	 * @param destinationStreamSupplier destination stream supplier
	 */
	public void write(ThrowableSupplier<OutputStream> destinationStreamSupplier) {
		try (OutputStream outputStream = destinationStreamSupplier.get()) {
			IOUtils.write(readAsByteArray(), outputStream);
		} catch (IOException e) {
			throw new InternalException("Error while writing data in the stream", e);
		}
	}

	/**
	 * @return file from resources
	 */
	public File getFile() {
		String filePath = startFilenameWithoutSlash();
		return new File(getClass().getClassLoader()
		                          .getResource(filePath)
		                          .getFile());
	}

	/**
	 * @return InputStream of the file from resources
	 */
	public InputStream getInputStream() {
		try {
			return getResourceStream();
		} catch (IOException e) {
			throw new InternalException(e);
		}
	}

	private InputStream getResourceStream() throws IOException {
		String filePath = startFilenameFromSlash();
		InputStream inputStream = getClass().getResourceAsStream(filePath);
		if (inputStream == null) {
			inputStream = getClass().getResourceAsStream(filePath);
		}
		if (inputStream == null) {
			inputStream = Files.newInputStream(Paths.get(fileName));
		}
		return inputStream;
	}

	private String startFilenameFromSlash() {
		return !fileName.startsWith("/")
		       ? "/" + fileName
		       : fileName;
	}

	private String startFilenameWithoutSlash() {
		return fileName.startsWith("/")
		       ? fileName.replaceFirst("/", "")
		       : fileName;
	}
}
