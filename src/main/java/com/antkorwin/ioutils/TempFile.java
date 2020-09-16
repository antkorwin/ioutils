package com.antkorwin.ioutils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.antkorwin.throwable.functions.ThrowableSupplier;
import com.antkorwin.throwable.functions.ThrowableWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

/**
 * Created on 30/06/2020
 * <p>
 * Tools to work with temporary files
 *
 * @author Korovin Anatoliy
 */
@RequiredArgsConstructor
public class TempFile {

	/**
	 * create a temporary file from InputStream
	 *
	 * @param inputStreamSupplier supplier for the InputStream
	 * @param extension           target file extension
	 * @return created temporary file
	 */
	public static File createFromInputStream(ThrowableSupplier<InputStream> inputStreamSupplier, String extension) {
		return ThrowableWrapper.get(() -> {
			final File tempFile = File.createTempFile("ioutils-", "." + extension);
			tempFile.deleteOnExit();
			try (InputStream inputStream = inputStreamSupplier.get();
			     FileOutputStream out = new FileOutputStream(tempFile)) {
				// copy data from stream to file
				IOUtils.copy(inputStream, out);
			}
			return tempFile;
		});
	}

	/**
	 * create a temporary file from InputStream
	 *
	 * @param inputStreamSupplier supplier for the InputStream
	 * @return created temporary file
	 */
	public static File createFromInputStream(ThrowableSupplier<InputStream> inputStreamSupplier) {
		return createFromInputStream(inputStreamSupplier, "tmp");
	}

	/**
	 * Create a temp file with the content from string argument value
	 *
	 * @param fileContent string value of the file content
	 * @return created temp file
	 */
	public static File createFromString(String fileContent) {
		return TempFile.createFromInputStream(() -> new ByteArrayInputStream(fileContent.getBytes()));
	}

	/**
	 * Create a temp file with the content from string argument value
	 *
	 * @param fileContent string value of the file content
	 * @param extension   target file extension
	 * @return created temp file
	 */
	public static File createFromString(String fileContent, String extension) {
		return TempFile.createFromInputStream(() -> new ByteArrayInputStream(fileContent.getBytes()),
		                                      extension);
	}

	/**
	 * Create an empty temporary file
	 *
	 * @return created file
	 */
	public static File createEmpty() {
		return ThrowableWrapper.get(() -> {
			final File tempFile = File.createTempFile("ioutils-", ".tmp");
			tempFile.deleteOnExit();
			return tempFile;
		});
	}
}
