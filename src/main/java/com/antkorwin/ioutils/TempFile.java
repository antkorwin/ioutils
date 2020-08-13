package com.antkorwin.ioutils;

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
 * Заворачивает стрим во временный файл
 *
 * @author Korovin Anatoliy
 */
@RequiredArgsConstructor
public class TempFile {


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

	public static File createEmpty() {
		return ThrowableWrapper.get(() -> {
			final File tempFile = File.createTempFile("ioutils-", ".tmp");
			tempFile.deleteOnExit();
			return tempFile;
		});
	}
}
