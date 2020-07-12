package com.antkorwin.ioutils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TempFileTest {

	@Test
	void createEmpty() throws IOException {
		// Act
		File file = TempFile.create();
		// Assert
		assertThat(file.exists()).isTrue();
		String content = FileUtils.readFileToString(file);
		assertThat(content).isEmpty();
	}

	@Test
	void createFromStream() throws IOException {
		// Act
		File file = TempFile.createFromInputStream(() -> new ByteArrayInputStream("12345".getBytes()));
		// Assert
		assertThat(file.exists()).isTrue();
		String content = FileUtils.readFileToString(file);
		assertThat(content).isEqualTo("12345");
	}

	@Test
	void testDeleteOnExit() {
		//TODO
	}
}