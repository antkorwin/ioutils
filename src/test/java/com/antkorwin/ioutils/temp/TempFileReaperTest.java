package com.antkorwin.ioutils.temp;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.antkorwin.commonutils.gc.GcUtils;
import com.antkorwin.ioutils.TempFile;
import com.jupiter.tools.stress.test.concurrency.ExecutionMode;
import com.jupiter.tools.stress.test.concurrency.StressTestRunner;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import static java.nio.file.Files.exists;
import static org.assertj.core.api.Assertions.assertThat;

class TempFileReaperTest {

	private static int ITERATIONS = 10000;

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

	@Test
	void concurrentTest() {
		List<Path> filenames = new CopyOnWriteArrayList<>();
		// making tmp files in 8 threads:
		StressTestRunner.test()
		                .timeout(1, TimeUnit.MINUTES)
		                .mode(ExecutionMode.EXECUTOR_MODE)
		                .threads(8)
		                .iterations(10)
		                .run(() -> {
			                // act:
			                File tmp = TempFile.createEmpty();
			                filenames.add(tmp.toPath());
		                });
		// await
		GcUtils.fullFinalization();
		// assert
		filenames.forEach(f -> {
			System.out.println(f.toString());
			assertThat(exists(f)).isFalse();
		});
	}

	@Test
	void createTempFilesCleanAndCreateAgain() throws InterruptedException {
		List<Path> filenames = new CopyOnWriteArrayList<>();

		// first iteration:
		StressTestRunner.test()
		                .timeout(1, TimeUnit.MINUTES)
		                .mode(ExecutionMode.EXECUTOR_MODE)
		                .threads(8)
		                .iterations(ITERATIONS)
		                .run(() -> {
			                // act:
			                File tmp = TempFile.createEmpty();
			                filenames.add(tmp.toPath());
		                });
		// await
		GcUtils.fullFinalization();
		Awaitility.await()
		          .atMost(5, TimeUnit.SECONDS)
		          .untilAsserted(() -> {
			          filenames.forEach(f -> assertThat(exists(f)).isFalse());
		          });

		// wait until thread reaper terminated
		filenames.clear();
		Thread.sleep(100);

		// Second iteration:
		StressTestRunner.test()
		                .timeout(1, TimeUnit.MINUTES)
		                .mode(ExecutionMode.EXECUTOR_MODE)
		                .threads(8)
		                .iterations(ITERATIONS)
		                .run(() -> {
			                // act:
			                File tmp = TempFile.createEmpty();
			                filenames.add(tmp.toPath());
		                });
		// await
		GcUtils.fullFinalization();
		Awaitility.await()
		          .atMost(5, TimeUnit.SECONDS)
		          .untilAsserted(() -> {
			          filenames.forEach(f -> assertThat(exists(f)).isFalse());
		          });
	}
}