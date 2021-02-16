package com.antkorwin.ioutils.temp;

import java.io.File;

import com.antkorwin.commonutils.gc.GcUtils;
import com.antkorwin.ioutils.TempFile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TempFileReaperTest {

	@Test
	void deleteAfterGc() {
		// Arrange
		File tmp = TempFile.createFromString("ABC");
		String path = tmp.getPath();
		// Act
		tmp = null;
		GcUtils.fullFinalization();
		// Assert
		File file = new File(path);
		assertThat(file).doesNotExist();
	}

	@Test
	void deleteEmptyAfterGc() {
		// Arrange
		File tmp = TempFile.createEmpty();
		String path = tmp.getPath();
		// Act
		tmp = null;
		GcUtils.fullFinalization();
		// Assert
		File file = new File(path);
		assertThat(file).doesNotExist();
	}
}