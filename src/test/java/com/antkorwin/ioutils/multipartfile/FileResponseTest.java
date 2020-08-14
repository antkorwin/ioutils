package com.antkorwin.ioutils.multipartfile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.antkorwin.ioutils.resourcefile.ResourceFile;
import org.junit.jupiter.api.Test;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

class FileResponseTest {

	@Test
	void buildWithContentDisposition() throws UnsupportedEncodingException {
		// Arrange
		String content = "1234567";
		String mimeType = "application/json";
		String filename = "123.txt";
		ContentDisposition contentDisposition = ContentDisposition.parse("attachment; filename*=UTF-8''123.txt");
		InputStream stream = new ByteArrayInputStream(content.getBytes());
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		// Act
		FileResponse.builder()
		            .file(() -> stream)
		            .filename(filename)
		            .contentDisposition(contentDisposition)
		            .mimeType(mimeType)
		            .response(mockResponse)
		            .build();
		// Assert
		assertThat(mockResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(mockResponse.getContentType()).isEqualTo(mimeType);
		assertThat(mockResponse.getHeader(CONTENT_DISPOSITION)).isEqualTo("attachment;filename*=UTF-8''123.txt");
		assertThat(mockResponse.getContentAsString()).isEqualTo(content);
	}

	@Test
	void buildWithFilename() throws UnsupportedEncodingException {
		// Arrange
		String content = "1234567";
		String mimeType = "application/json";
		String filename = "123.txt";
		InputStream stream = new ByteArrayInputStream(content.getBytes());
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		// Act
		FileResponse.builder()
		            .file(() -> stream)
		            .filename(filename)
		            .mimeType(mimeType)
		            .response(mockResponse)
		            .build();
		// Assert
		assertThat(mockResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(mockResponse.getContentType()).isEqualTo(mimeType);
		assertThat(mockResponse.getHeader(CONTENT_DISPOSITION)).isEqualTo("attachment;filename*=UTF-8''123.txt");
		assertThat(mockResponse.getContentAsString()).isEqualTo(content);
	}

	@Test
	void buildWithFilenameAndContentDisposition() throws UnsupportedEncodingException {
		// Arrange
		String content = "1234567";
		String mimeType = "application/json";
		String filename = "123.txt";
		InputStream stream = new ByteArrayInputStream(content.getBytes());
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		// Act
		FileResponse.builder()
		            .file(() -> stream)
		            .filename(filename)
		            .contentDisposition("attachment; filename*=UTF-8''file.txt")
		            .mimeType(mimeType)
		            .response(mockResponse)
		            .build();
		// Assert
		assertThat(mockResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(mockResponse.getContentType()).isEqualTo(mimeType);
		assertThat(mockResponse.getHeader(CONTENT_DISPOSITION)).isEqualTo("attachment;filename*=UTF-8''123.txt");
		assertThat(mockResponse.getContentAsString()).isEqualTo(content);
	}

	@Test
	void buildFromFile() throws UnsupportedEncodingException {
		// Arrange
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		// Act
		FileResponse.builder()
		            .file(new ResourceFile("test.txt").getFile())
		            .response(mockResponse)
		            .build();
		// Assert
		assertThat(mockResponse.getContentAsString()).isEqualTo("q1w2e3r4t5\n" +
		                                                        "1234566789");
	}

	@Test
	void buildFromByteArray() throws UnsupportedEncodingException {
		// Arrange
		String content = "1234567";
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		// Act
		FileResponse.builder()
		            .file(content.getBytes())
		            .response(mockResponse)
		            .build();
		// Assert
		assertThat(mockResponse.getContentAsString()).isEqualTo(content);
	}
}
