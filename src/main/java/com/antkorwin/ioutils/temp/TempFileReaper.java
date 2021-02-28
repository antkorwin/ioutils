package com.antkorwin.ioutils.temp;


import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Delete unused files after GC collect the references.
 *
 * @author Anatoliy Korovin
 */
public class TempFileReaper {

	private volatile TempFileReaperThread reaperThread = null;
	private ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
	private Set<TempFileReference> references = Collections.newSetFromMap(new ConcurrentHashMap<>());


	/**
	 * Remove a file from disk after all references to this file will be collected by GC
	 *
	 * @param file temporary file
	 */
	public void deleteWhenUnused(File file) {

		// collect all references
		references.add(new TempFileReference(file, referenceQueue));

		// run the thread to delete unused files
		if (reaperThread == null) {
			synchronized (this) {
				if (reaperThread == null) {
					reaperThread = new TempFileReaperThread();
					reaperThread.start();
				}
			}
		}
	}

	/**
	 * This thread processing unused files
	 */
	class TempFileReaperThread extends Thread {

		public TempFileReaperThread() {
			super("TempFileReaper");
			setDaemon(true);
		}

		@Override
		public void run() {
			while (references.size() > 0) {
				try {
					// wait for a new unused file reference
					TempFileReference unusedReference = (TempFileReference) referenceQueue.remove();
					references.remove(unusedReference);
					// delete file
					unusedReference.delete();
					unusedReference.clear();
				} catch (InterruptedException e) {
					// TODO: use the Slf4j in this library and send an error message in logs here
					continue;
				}
			}
			reaperThread = null;
		}
	}

}
