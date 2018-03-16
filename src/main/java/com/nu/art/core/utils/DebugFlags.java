package com.nu.art.core.utils;

import java.util.HashSet;

public class DebugFlags {

	private static final HashSet<String> debugFlags = new HashSet<>();

	public static void addDebugFlag(String flag) {
		debugFlags.add(flag);
	}

	public static void removeDebugFlag(String flag) {
		debugFlags.remove(flag);
	}

	public static boolean isDebuggableFlag(String flag) {
		return debugFlags.contains(flag);
	}
}
