/*
 * The core of the core of all my projects!
 *
 * Copyright (C) 2017  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.core.archiver;

import com.nu.art.core.tools.FileTools;
import com.nu.art.core.tools.StreamTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * An object that archives files and folders.
 *
 * @author TacB0sS
 */
public class Archiver {

	public final void archiveToFile(String archiveFile, Manifest manifest, String... filesToArchive)
		throws IOException {
		archiveToFile(new File(archiveFile), manifest, filesToArchive);
	}

	private void archiveToFile(File archiveFile, Manifest manifest, String... filesToArchive)
		throws IOException {
		File[] files = new File[filesToArchive.length];
		for (int i = 0; i < files.length; i++) {
			files[i] = new File(filesToArchive[i]);
		}
		archiveToFile(archiveFile, manifest, files);
	}

	public final void archiveToFile(File archiveFile, Manifest manifest, File... filesToArchive)
		throws IOException {
		if (filesToArchive.length == 0)
			throw new IOException("Files NOT Specified");

		JarOutputStream jos = null;
		FileOutputStream fos = null;
		try {
			FileTools.createNewFile(archiveFile);

			fos = new FileOutputStream(archiveFile);
			if (manifest == null) {
				jos = new JarOutputStream(fos);
			} else {
				jos = new JarOutputStream(fos, manifest);
			}

			File parentFile = filesToArchive[0].getParentFile();
			for (int i = 1; i < filesToArchive.length; i++) {
				if (filesToArchive[i].getParentFile().equals(parentFile))
					continue;

				throw new IllegalArgumentException("Multiple files with multiple parents are not allowed!");
			}

			writeFilesIntoArchive(jos, parentFile, filesToArchive);
			jos.finish();
		} finally {
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

	private void writeFilesIntoArchive(JarOutputStream jos, File parentFile, File... files)
		throws IOException {
		for (File file : files) {
			if (file.isDirectory()) {
				writeFilesIntoArchive(jos, parentFile, file.listFiles());
				continue;
			}
			writeIntoJar(jos, parentFile, file);
		}
	}

	private void writeIntoJar(JarOutputStream jos, File parentFile, File toArchive)
		throws IOException {
		String relativePath = toArchive.getAbsolutePath().replace(parentFile.getAbsolutePath(), "").replace(File.separator, "/");
		relativePath = relativePath.substring(1);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(toArchive);
			JarEntry jarEntry = new JarEntry(relativePath);
			jarEntry.setTime(toArchive.lastModified());
			jarEntry.setSize(toArchive.length());
			jos.putNextEntry(jarEntry);
			StreamTools.copy(fis, jos);
		} finally {
			if (fis != null)
				fis.close();

			jos.closeEntry();
		}
	}

	public final void extractToFolder(String archiveFile, String openInFolder)
		throws IOException {
		extractToFolder(new File(archiveFile), new File(openInFolder));
	}

	public final void extractToFolder(File archiveFile, File openInFolder)
		throws IOException {
		FileInputStream fis = null;
		try {
			if (!archiveFile.exists())
				throw new IOException("Error while creating new Archive file: " + archiveFile.getAbsolutePath());

			fis = new FileInputStream(archiveFile);
			extractToFolder(fis, openInFolder);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException ignore) {
				}
		}
	}

	public final void extractToFolder(InputStream archiveStream, File openInFolder)
		throws IOException {
		JarInputStream jis = null;
		try {
			if (!openInFolder.exists())
				if (!openInFolder.mkdirs())
					throw new IOException("Error while extracting archive to: " + openInFolder.getAbsolutePath());

			jis = new JarInputStream(archiveStream, false);
			readFilesFromArchive(jis, openInFolder);
		} finally {
			if (jis != null)
				try {
					jis.close();
				} catch (IOException ignore) {
				}
		}
	}

	private void readFilesFromArchive(JarInputStream jis, File outputFolder)
		throws IOException {
		JarEntry entry;
		while ((entry = jis.getNextJarEntry()) != null) {
			if (entry.isDirectory()) {
				continue;
			}
			writeEntryToFile(entry, jis, outputFolder);
		}
	}

	private void writeEntryToFile(JarEntry entry, JarInputStream jis, File outputFolder)
		throws IOException {
		FileOutputStream fos = null;
		File file = new File(outputFolder, entry.getName());
		try {
			if (file.exists())
				if (!file.delete())
					throw new IOException("Could not delete older file: " + file.getAbsolutePath());

			if (!file.getParentFile().exists())
				if (!file.getParentFile().mkdirs())
					throw new IOException("Could not create path to file: " + file.getParentFile().getAbsolutePath());

			if (!file.createNewFile())
				throw new IOException("Could not create new file: " + file.getAbsolutePath());

			fos = new FileOutputStream(file);
			StreamTools.copy(jis, fos);
		} finally {
			if (fos != null)
				fos.close();

			if (entry.getTime() != -1)
				file.setLastModified(entry.getTime());
		}
	}

	public static void main(String[] args)
		throws IOException {
		Archiver archiver = new Archiver();
		File targetFile = new File(args[0]);
		File[] sources = new File[args.length - 1];
		for (int i = 0; i < sources.length; i++) {
			sources[i] = new File(args[i + 1]);
		}

		FileTools.delete(targetFile);
		archiver.archiveToFile(targetFile, null, sources);
	}
}
