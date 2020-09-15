package com.antkorwin.ioutils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.antkorwin.throwable.functions.ThrowableSupplier;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TempFileTest {

	@Test
	void createEmptyOld() throws IOException {
		// Act
		File file = TempFile.create();
		// Assert
		assertThat(file.exists()).isTrue();
		String content = FileUtils.readFileToString(file);
		assertThat(content).isEmpty();
	}

	@Test
	void createEmpty() throws IOException {
		// Act
		File file = TempFile.createEmpty();
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
	void createFromString() throws IOException {
		// Act
		File file = TempFile.createFromString("foo-bar");
		// Assert
		assertThat(file.exists()).isTrue();
		String content = FileUtils.readFileToString(file);
		assertThat(content).isEqualTo("foo-bar");
	}

	@Test
	void testDeleteOnExit() {
		//TODO
	}

	@Test
	void createWithExtensionFromStream() {
		ThrowableSupplier<InputStream> data = () -> new ByteArrayInputStream("12345".getBytes());
		String extension = "doc";
		// Act
		File file = TempFile.createFromInputStream(data, extension);
		// Assert
		assertThat(file.getPath().matches("^*.*\\.doc")).isTrue();
	}

	@Test
	void createWithExtensionFromString() {
		// Act
		File file = TempFile.createFromString("data", "doc");
		// Assert
		assertThat(file.getPath().matches("^*.*\\.doc")).isTrue();
	}

	@Test
	void defaultExtension() {
		// Act
		File file = TempFile.createFromString("data");
		// Assert
		assertThat(file.getPath().matches("^*.*\\.tmp")).isTrue();
	}
}