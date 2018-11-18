package com.nu.art.core.test.archiver;

import com.nu.art.core.file.Charsets;
import com.nu.art.core.file.FileCopy;
import com.nu.art.core.tools.FileTools;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class Test_FileCopy {

	private static boolean setUpIsDone = false;
	private File[] testFiles = new File[10];
	private File[] testSubFiles = new File[10];
	private String path = "build/test/file-copy";

	@Before
	public void setUp()
		throws IOException {
		if (setUpIsDone) {
			return;
		}

		for (int i = 1; i < 1 + testFiles.length; i++) {
			testFiles[i - 1] = new File(path + "/data", "temp-file-" + i + ".txt");
			FileTools.writeToFile("test file " + i, testFiles[i - 1], Charsets.UTF_8);
		}
		for (int i = 1; i < 1 + testSubFiles.length; i++) {
			testSubFiles[i - 1] = new File(path + "/data/sub", "temp-sub-file-" + i + ".txt");
			FileTools.writeToFile("test sub file " + i, testSubFiles[i - 1], Charsets.UTF_8);
		}
		for (int i = 1; i < 6; i++) {
			File file = new File(path + "/data/sub/foo", "temp-sub-foo-file-" + i + ".txt");
			FileTools.writeToFile("test sub file " + i, file, Charsets.UTF_8);
		}
		for (int i = 1; i < 6; i++) {
			File file = new File(path + "/data/sub/bar", "temp-sub-bar-file-" + i + ".txt");
			FileTools.writeToFile("test sub file " + i, file, Charsets.UTF_8);
		}

		setUpIsDone = true;
	}

	@Test
	public void test_CopyFiles()
		throws IOException {
		File targetFolder = new File(path, "target");
		FileTools.delete(targetFolder);
		FileTools.mkDir(targetFolder);
		new FileCopy().setTargetFolder(targetFolder)
		              .copy(testFiles[0])
		              .copy(testFiles[1])
		              .copy(new File(path, "/data/sub/foo"), "pah/zevel")
		              .copy(testFiles[4], "pah/zevel");
	}
}
