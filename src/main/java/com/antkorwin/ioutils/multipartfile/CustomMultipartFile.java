package com.antkorwin.ioutils.multipartfile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;

import com.antkorwin.ioutils.error.InternalException;
import com.antkorwin.throwable.functions.ThrowableSupplier;
import lombok.Builder;
import lombok.experimental.Delegate;
import org.apache.commons.fileupload.FileItem;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Created on 12/05/2020
 * <p>
 * {@link MultipartFile} builder to manually creating a {@link MultipartFile}
 * with necessary fields.
 *
 * @author Korovin Anatoliy
 */
public class CustomMultipartFile implements MultipartFile {

	@Delegate
	private final MultipartFile multipartFile;

	/**
	 * Build a {@link CustomMultipartFile} instance.
	 *
	 * @param contentType              Mime type of content
	 * @param dataFieldName            The name of a field(in the multipart file structure)
	 *                                 to transfer a binary data of the file
	 * @param originalFileName         Human-readable name of the file.
	 * @param fileContentAsString      This field provides an ability to set a file content directly
	 *                                 from the string value, instead of the InputStream.
	 * @param fileContentAsBytes       This field provides an ability to set a file content directly
	 *                                 from the byte array value, instead of the InputStream.
	 * @param fileContentAsInputStream You can use this field to set InputStream with a file content
	 */
	@Builder
	public CustomMultipartFile(String contentType,
	                           String dataFieldName,
	                           String originalFileName,
	                           String fileContentAsString,
	                           byte[] fileContentAsBytes,
	                           File fileContentFromFile,
	                           ThrowableSupplier<InputStream> fileContentAsInputStream) {

		if (fileContentAsInputStream == null) {
			fileContentAsInputStream = getInputStreamSupplier(fileContentFromFile,
			                                                  fileContentAsString,
			                                                  fileContentAsBytes);
		}

		String encodedFileName = encode(originalFileName);

		FileItem fileItem = new CustomDiskFileItem(encodedFileName,
		                                           dataFieldName,
		                                           contentType,
		                                           fileContentAsInputStream);

		this.multipartFile = new CommonsMultipartFile(fileItem);
	}

	/**
	 * Retrieves the file content from string or bytes values, if the InputStreamSupplier isn't set.
	 */
	private ThrowableSupplier<InputStream> getInputStreamSupplier(File fileContentFromFile,
	                                                              String fileContentAsString,
	                                                              byte[] fileContentAsBytes) {

		if(fileContentFromFile != null){
			return () -> new FileInputStream(fileContentFromFile);
		}

		if (fileContentAsBytes != null) {
			return () -> new ByteArrayInputStream(fileContentAsBytes);
		}

		if (fileContentAsString != null) {
			return () -> new ByteArrayInputStream(fileContentAsString.getBytes());
		}

		throw new InternalException("Multipart file creating error.");
	}

	/**
	 * Use to (url)encode of the file name
	 */
	private String encode(String name) {
		try {
			return URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException exception) {
			throw new InternalException("Filename encoding error.", exception);
		}
	}
}
