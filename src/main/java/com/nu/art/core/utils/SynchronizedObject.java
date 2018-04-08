package com.nu.art.core.utils;

import com.nu.art.core.interfaces.Getter;

import java.util.HashMap;

/**
 * Created by tacb0ss on 08/04/2018.
 */

public class SynchronizedObject<Type>
	extends HashMap<Thread, Type> {

	private final Getter<Type> getter;

	public SynchronizedObject(Getter<Type> getter) {
		this.getter = getter;
	}

	public Type get() {
		Thread thread = Thread.currentThread();
		Type object = super.get(thread);
		if (object == null)
			super.put(thread, object = getter.get());

		return object;
	}
}
