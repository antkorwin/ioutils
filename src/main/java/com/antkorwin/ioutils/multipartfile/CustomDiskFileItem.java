package com.antkorwin.ioutils.multipartfile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.antkorwin.throwable.functions.ThrowableSupplier;
import com.antkorwin.throwable.functions.WrappedException;
import lombok.experimental.Delegate;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;


/**
 * Created on 12/05/2020
 * <p>
 * The FileItem implementation based on the {@link DiskFileItem}
 * which reads a file content form an {@link InputStream}.
 *
 * @author Korovin Anatoliy
 */
public class CustomDiskFileItem implements FileItem {

	private final static int IN_MEMORY_BUFFER_SIZE = 1024 * 1024;

	@Delegate
	private final FileItem fileItem;


	public CustomDiskFileItem(String originalFileName,
	                          String dataFieldName,
	                          String contentType,
	                          ThrowableSupplier<InputStream> inputStreamSupplier) {

		fileItem = new DiskFileItem(dataFieldName,
		                            contentType,
		                            true,
		                            originalFileName,
		                            IN_MEMORY_BUFFER_SIZE,
		                            null);

		prepareFileItemContent(fileItem, inputStreamSupplier);
	}

	/**
	 * Move a file content from the InputStream to the outputStream of FileItem
	 */
	private void prepareFileItemContent(FileItem fileItem,
	                                    ThrowableSupplier<InputStream> inputStreamSupplier) {

		try (OutputStream fileItemOutputStream = fileItem.getOutputStream();
		     InputStream fileInputStream = new BufferedInputStream(inputStreamSupplier.get())) {

			IOUtils.copy(fileInputStream, fileItemOutputStream);

		} catch (IOException e) {
			throw new WrappedException(e);
		}
	}
}
