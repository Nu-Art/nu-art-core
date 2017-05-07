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

package com.nu.art.software.core.utils;

import java.util.Collections;
import java.util.Vector;

@SuppressWarnings( {"unused", "unchecked"
				   })
public abstract class PoolQueue<Type> {

	private Vector<Type> items = new Vector<>();

	private volatile boolean keepAlive = true;

	private Vector<Thread> threads = new Vector<>();

	private final Runnable queueAction = new Runnable() {

		@Override
		public final void run() {
			threads.add(Thread.currentThread());
			while (keepAlive) {
				Type item = getNextItem();
				if (item == null) {
					continue;
				}
				try {
					executeAction(item);
				} catch (Exception e) {
					onExecutionError(item, e);
				}
			}
			threads.remove(Thread.currentThread());
		}
	};

	protected synchronized Type getNextItem() {
		if (items.size() == 0) {
			try {
				wait();
			} catch (InterruptedException ignore) {}
			return null;
		}

		return items.remove(0);
	}

	protected final Thread[] getThreads() {
		return threads.toArray(new Thread[threads.size()]);
	}

	public final boolean isAlive() {
		return keepAlive;
	}

	protected abstract void onExecutionError(Type item, Throwable e);

	protected abstract void executeAction(Type type)
			throws Exception;

	public synchronized final void kill() {
		keepAlive = false;
		notifyAll();
		clear();
	}

	public synchronized final boolean contains(Type item) {
		return items.contains(item);
	}

	public synchronized final void addItemIfNotInQueue(Type item) {
		if (!contains(item))
			addItem(item);
	}

	public synchronized final void moveToHeadOfQueue(Type... items) {
		for (int i = items.length - 1; i >= 0; i--) {
			removeItem(items[i]);
			addFirst(items[i]);
		}
	}

	public synchronized final void addItem(Type... items) {
		Collections.addAll(this.items, items);
		notify();
	}

	public synchronized final void addFirst(Type item) {
		items.add(0, item);
		notify();
	}

	public final boolean removeItem(Type item) {
		return items.remove(item);
	}

	public final Type removeItem(int index) {
		return items.remove(index);
	}

	public final int getItemsCount() {
		return items.size();
	}

	public final void clear() {
		items.clear();
	}

	public final void createThreads(String name) {
		createThreads(name, 1);
	}

	public final void createThreads(String name, int count) {
		for (int i = 0; i < count; i++) {
			Thread t = new Thread(queueAction, name + " #" + i);
			t.start();
		}
	}
}
