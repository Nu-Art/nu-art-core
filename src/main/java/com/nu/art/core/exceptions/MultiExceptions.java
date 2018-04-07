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

package com.nu.art.core.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Vector;

public final class MultiExceptions
	extends InternalException {

	private static final long serialVersionUID = 4868165699725328140L;

	private static Field messageField;

	static {
		try {
			messageField = Throwable.class.getDeclaredField("detailMessage");
			messageField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	private Vector<Throwable> exceptions = new Vector<>();

	private Vector<Object> exceptionKey = new Vector<>();

	public MultiExceptions(String reason) {
		super(reason);
	}

	public void addException(Throwable exception) {
		addException(exception, null);
	}

	public void addException(String tag, Throwable exception) {
		addException(exception, null);
		addPrefixToException(tag, exception);
	}

	public void addException(Throwable exception, Object key) {
		exceptions.add(exception);
		exceptionKey.add(key);
	}

	public void addException(String tag, Throwable exception, Object key) {
		exceptions.add(exception);
		exceptionKey.add(key);
		addPrefixToException(tag, exception);
	}

	public void addMessagesPostfix(String messagePostfix) {
		for (Throwable e : exceptions) {
			addPostfixToException(e, messagePostfix);
		}
	}

	public void addMessagesPrefix(String messagePrefix) {
		for (Throwable e : exceptions) {
			addPrefixToException(messagePrefix, e);
		}
	}

	private void addPostfixToException(Throwable e, String messagePostfix) {
		try {
			messageField.set(e, e.getMessage() + messagePostfix);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void addPrefixToException(String messagePrefix, Throwable e) {
		try {
			messageField.set(e, messagePrefix + e.getMessage());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void clear() {
		exceptions.clear();
	}

	public Throwable[] getCauses() {
		return exceptions.toArray(new Throwable[exceptions.size()]);
	}

	@Override
	public void printStackTrace() {
		int index = 0;
		for (Throwable e : exceptions) {
			System.err.print(index++ + ": ");
			e.printStackTrace();
			System.err.println();
		}
	}

	@Override
	public void printStackTrace(PrintStream s) {
		int index = 0;
		for (Throwable e : exceptions) {
			System.err.print(index++ + ": ");
			e.printStackTrace(s);
			System.err.println();
		}
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		int index = 0;
		for (Throwable e : exceptions) {
			System.err.print(index++ + ": ");
			e.printStackTrace(s);
			System.err.println();
		}
	}

	/**
	 * If there are any exception added to this Exception container, then <b>this</b> MultiException instance is thrown!
	 *
	 * @throws MultiExceptions If there are any exception in this exception container.
	 */
	public void process()
		throws MultiExceptions {
		if (exceptions.size() > 0) {
			throw this;
		}
	}

	public int size() {
		return exceptions.size();
	}
}
