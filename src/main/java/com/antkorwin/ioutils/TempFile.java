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
	 * @return created temporary file
	 */
	public static File createFromInputStream(ThrowableSupplier<InputStream> inputStreamSupplier) {
		return ThrowableWrapper.get(() -> {

			final File tempFile = File.createTempFile("ioutils-", ".tmp");
			tempFile.deleteOnExit();

			try (InputStream inputStream = inputStreamSupplier.get();
			     FileOutputStream out = new FileOutputStream(tempFile)) {

				IOUtils.copy(inputStream, out);
			}
			return tempFile;
		});
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
	 * see {@link TempFile#createEmpty()} method
	 */
	@Deprecated
	public static File create() {
		return ThrowableWrapper.get(() -> {
			final File tempFile = File.createTempFile("ioutils-", ".tmp");
			tempFile.deleteOnExit();
			return tempFile;
		});
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
