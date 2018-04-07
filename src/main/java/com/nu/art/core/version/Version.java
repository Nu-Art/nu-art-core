/*
 * The core of the core of all my projects!
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
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

import java.util.Vector;

/**
 * X.X.XXXX
 *
 * @author TacB0sS
 */
public final class Version
	implements VersionUpdateListener {

	private static final long serialVersionUID = -5627960243757371642L;

	public static boolean isNewerThen(String versionInQuestion, String versionToCompareTo) {
		Version inQuestion = new Version(versionInQuestion);
		Version toCompareWith = new Version(versionToCompareTo);
		return inQuestion.isNewerThen(toCompareWith);
	}

	short release;

	short test;

	short build;

	private Vector<VersionUpdateListener> listeners = new Vector<>();

	public Version() {}

	public Version(String string) {
		try {
			setVersion(string);
		} catch (BadVersionFormat e) {
			e.printStackTrace();
		}
	}

	public void addVersionUpdateListener(VersionUpdateListener versionUpdateListener) {
		listeners.add(versionUpdateListener);
	}

	public void build() {
		build = increment(build);
		updateListeners();
	}

	public String getVersionAsString() {
		return release + "." + test + "." + build;
	}

	private short increment(short toIncrement) {
		if (toIncrement < Short.MAX_VALUE) {
			return (short) (toIncrement + 1);
		}
		return 0;
	}

	public boolean isMatchingTo(Version version) {
		if (build == version.build) {
			return true;
		} else if (build < version.build) {
			return false;
		}

		if (test > version.test) {
			return true;
		} else if (test < version.test) {
			return false;
		}

		if (release > version.release) {
			return true;
		} else if (release < version.release) {
			return false;
		}

		return false;
	}

	public boolean isNewerThen(Version version) {
		if (build > version.build) {
			return true;
		} else if (build < version.build) {
			return false;
		}

		if (test > version.test) {
			return true;
		} else if (test < version.test) {
			return false;
		}

		if (release > version.release) {
			return true;
		} else if (release < version.release) {
			return false;
		}

		return false;
	}

	public boolean isOlderThen(Version version) {
		if (build > version.build) {
			return true;
		} else if (build < version.build) {
			return false;
		}

		if (test > version.test) {
			return true;
		} else if (test < version.test) {
			return false;
		}

		if (release > version.release) {
			return true;
		} else if (release < version.release) {
			return false;
		}

		return false;
	}

	public void release() {
		release = increment(release);
		test = 0;
		build = 0;
		updateListeners();
	}

	public void setVersion(String initialVersion)
		throws BadVersionFormat {
		try {
			int index = initialVersion.indexOf(".");
			release = Short.parseShort(initialVersion.substring(0, index));
			test = Short.parseShort(initialVersion.substring(index + 1, index = initialVersion.indexOf(".", index + 1)));
			build = Short.parseShort(initialVersion.substring(index + 1));
			updateListeners();
		} catch (Exception e) {
			throw new BadVersionFormat(initialVersion, e);
		}
	}

	public void test() {
		test = increment(test);
		build = 0;
		updateListeners();
	}

	@Override
	public String toString() {
		return getVersionAsString();
	}

	private void updateListeners() {
		for (VersionUpdateListener listener : listeners) {
			listener.updateVersion(this);
		}
	}

	@Override
	public void updateVersion(Version version) {
		this.build = version.build;
		this.test = version.test;
		this.release = version.release;
		updateListeners();
	}
}
