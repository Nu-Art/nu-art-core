package com.nu.art.core.archiver;

import com.nu.art.core.tools.FileTools;
import com.nu.art.core.tools.StreamTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Created by tacb0ss on 26/04/2018.
 */

public class ArchiveWriter {

	private JarOutputStream jos = null;
	private FileOutputStream fos = null;
	private File outputFile;

	public ArchiveWriter open(File outputFile)
		throws IOException {
		open(outputFile, null);
		return this;
	}

	public ArchiveWriter open(File outputFile, Manifest manifest)
		throws IOException {
		try {
			if (fos != null)
				throw new IOException("Already have open file: " + outputFile.getAbsolutePath());

			this.outputFile = outputFile;
			FileTools.createNewFile(outputFile);

			fos = new FileOutputStream(outputFile);
			if (manifest == null) {
				jos = new JarOutputStream(fos);
			} else {
				jos = new JarOutputStream(fos, manifest);
			}
		} catch (IOException e) {
			dispose();
			throw e;
		}
		return this;
	}

	public ArchiveWriter setCompressionLevel(int compressionLevel)
		throws IOException {
		if (jos == null)
			throw new IOException("MUST open the archive first");

		jos.setLevel(compressionLevel);
		return this;
	}

	public ArchiveWriter setComment(String comment)
		throws IOException {
		if (jos == null)
			throw new IOException("MUST open the archive first");

		jos.setComment(comment);
		return this;
	}

	public ArchiveWriter setMethod(int method)
		throws IOException {
		if (jos == null)
			throw new IOException("MUST open the archive first");

		jos.setMethod(method);
		return this;
	}

	public ArchiveWriter addFiles(File... files)
		throws IOException {
		return addFiles("", files);
	}

	public ArchiveWriter addFiles(String path, File... files)
		throws IOException {
		for (File file : files) {
			addFile(path, file);
		}
		return this;
	}

	public void close()
		throws IOException {
		jos.finish();
		dispose();
	}

	public ArchiveWriter addFile(File file)
		throws IOException {
		addFile("", file);
		return this;
	}

	public ArchiveWriter addFile(String path, File file)
		throws IOException {
		if (path.length() > 0 && !path.endsWith("/"))
			path += "/";

		String fullEntryPath = path + file.getName();
		if (file.isDirectory()) {
			addFiles(fullEntryPath, file.listFiles());
			return this;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			JarEntry jarEntry = new JarEntry(fullEntryPath);
			jarEntry.setTime(file.lastModified());
			jarEntry.setSize(file.length());
			jos.putNextEntry(jarEntry);
			StreamTools.copy(fis, jos);
		} catch (IOException e) {
			dispose();
			throw e;
		} finally {
			if (fis != null)
				fis.close();

			jos.closeEntry();
		}
		return this;
	}

	private void dispose() {
		if (fos != null)
			try {
				fos.close();
			} catch (IOException ignore) {
			}

		if (jos != null)
			try {
				jos.close();
			} catch (IOException ignore) {
			}
	}
}
