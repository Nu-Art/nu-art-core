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

package com.nu.art.software.core.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

public final class ExceptionTools {

	private ExceptionTools() {}

	private final static ByteArrayOutputStream bos = new ByteArrayOutputStream();

	private final static DataOutputStream dos = new DataOutputStream(bos);

	public static final String getStackTrace(Throwable e) {
		e.printStackTrace(new PrintStream(dos));
		String stackTrace = new String(bos.toByteArray());
		bos.reset();
		return stackTrace;
	}

	public static final Throwable getCause(Throwable e) {
		while (e.getCause() != null) { e = e.getCause(); }
		return e;
	}

	public static final StackTraceElement[] getLastStackTrace(Throwable e, int count) {
		Throwable cause = ExceptionTools.getCause(e);
		ArrayList<StackTraceElement> trace = new ArrayList<>();
		StackTraceElement[] causeStackTrace = cause.getStackTrace();
		if (count > causeStackTrace.length) {
			count = causeStackTrace.length;
		}
		trace.addAll(Arrays.asList(causeStackTrace).subList(0, count));
		return trace.toArray(new StackTraceElement[trace.size()]);
	}

	public static final String parseStackTrace(StackTraceElement[] stackTrace) {
		StringBuilder builder = new StringBuilder();
		for (StackTraceElement stackTraceElement : stackTrace) {
			builder.append(stackTraceElement.toString()).append("\n");
		}
		return builder.toString();
	}
}
