package com.antkorwin.ioutils.resourcefile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceFileTest {

	@Nested
	class ReadTests {

		@Test
		void readTextFile() {
			String content = new ResourceFile("/test.txt").readAsString();
			assertThat(content).containsSubsequence("q1w2e3r4t5",
			                                        "1234566789");
		}

		@Test
		void readTextFileWithoutDash() {
			String content = new ResourceFile("test.txt").readAsString();
			assertThat(content).containsSubsequence("q1w2e3r4t5",
			                                        "1234566789");
		}

		@Test
		void readTextFileAsByteArray() {
			byte[] content = new ResourceFile("/test.txt").readAsByteArray();
			assertThat(new String(content)).containsSubsequence("q1w2e3r4t5",
			                                                    "1234566789");
		}
	}

	@Nested
	class GetFileTests {

		@Test
		void getFile() {
			// Act
			File file = new ResourceFile("folder/nestedfile").getFile();
			// Assert
			assertThat(file).isNotNull();
			assertThat(getContent(file)).containsSubsequence("start",
			                                                 "content",
			                                                 "end");
		}

		@Test
		void getNestedFileWithSlash() {
			// Act
			File file = new ResourceFile("/folder/nestedfile").getFile();
			// Assert
			assertThat(file).isNotNull();
			assertThat(getContent(file)).containsSubsequence("start",
			                                                 "content",
			                                                 "end");

		}

		@Test
		void getStreamTest() throws IOException {
			try (InputStream inputStream = new ResourceFile("test.txt").getInputStream()) {
				assertThat(inputStream).isNotNull();
				assertThat(getContent(inputStream)).containsSubsequence("q1w2e3r4t5",
				                                                        "1234566789");
			}
		}

		private String getContent(File file) {
			try {
				return FileUtils.readFileToString(file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private String getContent(InputStream inputStream) {
			try {
				byte[] bytes = new byte[256];
				inputStream.read(bytes);
				return new String(bytes);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}


	@Nested
	class WriteTests {

		@Test
		void writeInStreamTest() {
			// Arrange
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			// Act
			new ResourceFile("test.txt").write(() -> outputStream);
			// Assert
			byte[] content = outputStream.toByteArray();
			assertThat(new String(content)).containsSubsequence("q1w2e3r4t5",
			                                                    "1234566789");
		}
	}


}