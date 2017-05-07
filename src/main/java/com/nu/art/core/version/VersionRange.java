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

package com.nu.art.core.version;

import com.nu.art.core.utils.RegexAnalyzer;

/**
 * Regex: (?:(?:(?:(\\d+)\\s*-\\s*(\\d+))+)|(\\d+)|(\\*))<br>
 * Example: {x,y-z}.{*}.{*}
 *
 * @author TacB0sS
 */
public final class VersionRange {

	private static final String[] VersionParameter = new String[]{"Release", "Mile-Stone", "Build"};

	private static final String VersionIndexRegex = "(?:(?:(?:(\\d+)\\s*-\\s*(\\d+))+)|(\\d+)|(\\*))";

	public static final String WildCard = "*";

	private static final String RangeSeperator = "-";

	/**
	 * The index regex analyzer, to derive the matching version, and to be used in version compatibility check.
	 */
	private static RegexAnalyzer IndexAnalyzer = new RegexAnalyzer(VersionRange.VersionIndexRegex);

	/**
	 * The releasing index range.
	 */
	private String release;

	/**
	 * The testing index range.
	 */
	private String test;

	/**
	 * The building index range.
	 */
	private String build;

	public VersionRange() {}

	public VersionRange(String string) {
		try {
			setVersion(string);
		} catch (com.nu.art.core.version.BadVersionFormat e) {
			e.printStackTrace();
		}
	}

	public String getVersionAsString() {
		return release + "." + test + "." + build;
	}

	private boolean isInRange(String range, int index) {
		RegexAnalyzer analyzer = new RegexAnalyzer(VersionRange.VersionIndexRegex);
		String[] ranges = analyzer.instances(range);
		for (String indexRange : ranges) {
			if (indexRange.trim().equals(VersionRange.WildCard)) {
				return true;
			}
			if (indexRange.contains(VersionRange.RangeSeperator)) {
				String[] indices = indexRange.split(VersionRange.RangeSeperator);
				if (index >= Integer.parseInt(indices[0].trim()) && index <= Integer.parseInt(indices[1].trim())) {
					return true;
				}
			} else if (index == Integer.parseInt(indexRange.trim())) {
				return true;
			}
		}
		return false;
	}

	public boolean isInRange(Version version) {
		return isInRange(build, version.build) && isInRange(test, version.test) && isInRange(release, version.release);
	}

	public void setVersion(String initialVersion)
			throws com.nu.art.core.version.BadVersionFormat {
		try {
			String[] version = validateVersion(initialVersion);
			release = version[0];
			test = version[1];
			build = version[2];
		} catch (Exception e) {
			throw new com.nu.art.core.version.BadVersionFormat(initialVersion, e);
		}
	}

	@Override
	public String toString() {
		return getVersionAsString();
	}

	private String[] validateVersion(String initialVersion)
			throws com.nu.art.core.version.BadVersionFormat {
		String[] version = initialVersion.split("\\.", 3);
		if (version.length != 3) {
			throw new com.nu.art.core.version.BadVersionFormat("Version syntax should be: {x,y-z}.{*}.{*}, and not: " + initialVersion);
		}
		for (int i = 0; i < version.length; i++) {
			if (VersionRange.IndexAnalyzer.instanceCount(initialVersion) == 0) {
				throw new com.nu.art.core.version.BadVersionFormat(VersionRange.VersionParameter[i] + " index, does not match regex: " + VersionRange.VersionIndexRegex + ". Syntax: {x,y-z}.{*}.{*}");
			}
			version[i] = version[i].trim();
		}
		return version;
	}
}
