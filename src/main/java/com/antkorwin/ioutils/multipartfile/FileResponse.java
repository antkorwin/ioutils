package com.antkorwin.ioutils.multipartfile;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;

import com.antkorwin.ioutils.error.InternalException;
import com.antkorwin.throwable.functions.ThrowableSupplier;
import org.apache.commons.io.IOUtils;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;


/**
 * Build a file response for apache tomcat
 * with necessary fields.
 *
 * @author Korovin Anatoliy
 */
public class FileResponse {

	private ThrowableSupplier<InputStream> fileInputStreamSupplier;
	private String filename;
	private String mimeType;
	private HttpServletResponse response;
	private ContentDisposition contentDisposition;
	private HttpStatus responseStatus;

	public static FileResponse builder() {
		return new FileResponse();
	}

	/**
	 * Entry point to build a response with the MultipartFile
	 */
	public HttpServletResponse build() {
		// The order of operations in this method is importance
		// if you change this please check a result with a real tomcat server
		// TODO: write integration tests on this flavor

		// mime-type
		if (!StringUtils.isEmpty(mimeType)) {
			response.setContentType(mimeType);
		}

		// content-disposition
		if (contentDisposition != null) {
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
		}

		// filename
		if (!StringUtils.isEmpty(filename)) {
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
			                   ContentDispositionFactory.getWithUtf8Filename(filename));
		}

		// file content
		try (InputStream inputStreamContent = fileInputStreamSupplier.get()) {
			IOUtils.copy(inputStreamContent, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			throw new InternalException(e);
		}

		// response status
		response.setStatus(responseStatus == null ? HttpServletResponse.SC_OK : responseStatus.value());
		return response;
	}


	//region boilerplate
	public FileResponse file(ThrowableSupplier<InputStream> fileInputStream) {
		this.fileInputStreamSupplier = fileInputStream;
		return this;
	}

	public FileResponse file(File file) {
		this.fileInputStreamSupplier = () -> new FileInputStream(file);
		return this;
	}

	public FileResponse file(byte[] fileContent) {
		this.fileInputStreamSupplier = () -> new ByteArrayInputStream(fileContent);
		return this;
	}

	public FileResponse filename(String filename) {
		this.filename = filename;
		return this;
	}

	public FileResponse response(HttpServletResponse response) {
		this.response = response;
		return this;
	}

	public FileResponse mimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}

	public FileResponse contentDisposition(ContentDisposition contentDisposition) {
		this.contentDisposition = contentDisposition;
		return this;
	}

	public FileResponse contentDisposition(String contentDisposition) {
		this.contentDisposition = ContentDisposition.parse(contentDisposition);
		return this;
	}

	public FileResponse status(HttpStatus status) {
		this.responseStatus = status;
		return this;
	}
	//endregion boilerplate
}