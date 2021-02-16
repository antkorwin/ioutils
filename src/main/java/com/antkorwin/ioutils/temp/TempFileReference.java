package com.antkorwin.ioutils.temp;

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;


/**
 * Phantom reference to the temporary file
 *
 * @author Anatoliy Korovin
 */
class TempFileReference extends PhantomReference<Object> {

	private final String path;

	TempFileReference(File file, ReferenceQueue<? super Object> queue) {
		super(file, queue);
		this.path = file.getPath();
	}

	/**
	 * Delete a file from disk
	 *
	 * @return true if successful
	 */
	boolean delete() {
		try {
			File file = new File(path);
			if (file == null || !file.exists()) {
				return true;
			}
			return file.delete();
		} catch (Exception ex) {
			return false;
		}
	}
}