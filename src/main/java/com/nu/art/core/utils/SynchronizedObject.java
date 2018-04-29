package com.nu.art.core.utils;

import com.nu.art.core.interfaces.Getter;

import java.util.HashMap;

/**
 * Created by tacb0ss on 08/04/2018.
 */

public class SynchronizedObject<Type> {

	private final HashMap<Thread, Type> instanceMap = new HashMap<>();
	private final Getter<Type> getter;

	public SynchronizedObject(Getter<Type> getter) {
		this.getter = getter;
	}

	public final Type get() {
		Thread thread = Thread.currentThread();
		Type object = instanceMap.get(thread);
		if (object == null)
			instanceMap.put(thread, object = getter.get());

		return object;
	}
}
