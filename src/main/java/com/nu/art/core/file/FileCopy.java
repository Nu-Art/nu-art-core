package com.nu.art.core.file;

import com.nu.art.core.exceptions.runtime.BadImplementationException;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.tools.FileTools;
import com.nu.art.core.utils.RunnableQueue;
import com.nu.art.core.utils.SynchronizedObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCopy {

	public class FileProgress {

		public File sourceFile;
		public File targetFile;
		private long progress;

		public double getProgress() {
			return 1f * progress / sourceFile.length();
		}
	}

	private Throwable error;
	private int bufferSize = 1024;
	private final SynchronizedObject<byte[]> buffers = new SynchronizedObject<>(new Getter<byte[]>() {
		@Override
		public byte[] get() {
			return new byte[bufferSize];
		}
	});
	private final SynchronizedObject<FileProgress> progress = new SynchronizedObject<>(new Getter<FileProgress>() {
		@Override
		public FileProgress get() {
			return new FileProgress();
		}
	});

	private RunnableQueue todo = new RunnableQueue();
	private File targetFolder;

	public FileCopy setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		return this;
	}

	public FileCopy setTargetFolder(File targetFolder) {
		this.targetFolder = targetFolder;
		if (targetFolder.isFile())
			throw new BadImplementationException("Target folder is actually a file: " + targetFolder.getAbsolutePath());

		return this;
	}

	public final FileCopy copy(final File file)
		throws IOException {
		copy(file, "");
		return this;
	}

	public final FileCopy copy(final File file, String relativePath)
		throws IOException {
		if (!file.exists())
			throw new FileNotFoundException("File not found: " + file.getAbsolutePath());

		File targetFolder = new File(this.targetFolder, relativePath);
		if (!targetFolder.exists())
			FileTools.mkDir(targetFolder);

		_copy(file, targetFolder);
		return this;
	}

	private void _copy(final File source, File targetFolder)
		throws IOException {
		if (source.isDirectory())
			_copyFolder(source, targetFolder);
		else
			_copyFile(source, new File(targetFolder, source.getName()));
	}

	private void _copyFile(File sourceFile, File targetFile)
		throws IOException {
		copyFileImpl(sourceFile, targetFile);
	}

	private void _copyFolder(File sourceFolder, File targetFolder)
		throws IOException {
		if (!targetFolder.exists())
			FileTools.mkDir(targetFolder);

		File[] files = sourceFolder.listFiles();
		if (files == null)
			return;

		for (File sourceFile : files) {
			_copy(sourceFile, new File(targetFolder, sourceFolder.getName()));
		}
	}

	private void copyFileImpl(File sourceFile, File targetFile)
		throws IOException {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		FileProgress progress = this.progress.get();
		progress.sourceFile = sourceFile;
		progress.targetFile = targetFile;
		progress.progress = 0;

		try {
			FileTools.createNewFile(targetFile);
			inputStream = new FileInputStream(sourceFile);
			outputStream = new FileOutputStream(targetFile);

			byte[] buffer = buffers.get();
			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				if (this.error != null)
					break;

				outputStream.write(buffer, 0, length);
				progress.progress += length;
			}
			outputStream.flush();

			targetFile.setLastModified(sourceFile.lastModified());
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException ignore) {
			}

			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException ignore) {
			}
		}

		if (error == null)
			return;

		todo.clear();
		try {
			FileTools.delete(targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onError(Throwable e) {
		todo.clear();
		this.error = e;
	}
}
