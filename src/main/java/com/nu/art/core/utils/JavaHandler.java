package com.nu.art.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class JavaHandler {

	private boolean running = true;
	private Thread thread;
	private final Executable _cache = new Executable(0, null);

	private class Executable {

		Runnable toExecute;

		final long when;

		private Executable(long when, Runnable toExecute) {
			this.toExecute = toExecute;
			this.when = when;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;

			if (o == null || getClass() != o.getClass())
				return false;

			Executable that = (Executable) o;

			return toExecute.equals(that.toExecute);
		}

		@Override
		public int hashCode() {
			return toExecute.hashCode();
		}
	}

	public final synchronized JavaHandler start(String name) {
		if (thread != null)
			throw new RuntimeException("instance already started with name: " + name);

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				running = true;
				while (running) {
					execute();
					synchronized (lock) {
						try {
							if (queue.size() == 0) {
								lock.wait();
								continue;
							}

							long sleepInterval = queue.get(0).when - System.currentTimeMillis();
							if (sleepInterval < 30)
								continue;

							lock.wait(sleepInterval - 5);
						} catch (InterruptedException ignore) {}
					}
				}
			}
		}, name);
		thread.start();
		return this;
	}

	public void stop() {
		running = false;
	}

	private void execute() {
		while (running) {
			Executable executable;
			synchronized (lock) {
				if (queue.size() == 0)
					return;

				if (queue.get(0).when > System.currentTimeMillis() + 50)
					return;

				executable = queue.remove(0);
			}

			executable.toExecute.run();
		}
	}

	private final Object lock = new Object();

	private ArrayList<Executable> queue = new ArrayList<>();

	public int getItemsCount() {
		synchronized (lock) {
			return queue.size();
		}
	}

	public void post(Runnable action) {
		post(0, action);
	}

	public void post(int delay, Runnable action) {
		postAt(System.currentTimeMillis() + delay, action);
	}

	public void postAt(long when, Runnable action) {
		postImpl(when, action);
	}

	private void postImpl(long when, Runnable action) {
		synchronized (lock) {
			queue.add(new Executable(when, action));
			sortQueue();
			lock.notify();
		}
	}

	public boolean remove(Runnable action) {
		synchronized (lock) {
			_cache.toExecute = action;
			boolean remove = queue.remove(_cache);
			if (remove)
				lock.notify();

			return remove;
		}
	}

	public void clear() {
		synchronized (lock) {
			queue.clear();
			lock.notify();
		}
	}

	public boolean removeAndPost(Runnable action) {
		return removeAndPost(0, action);
	}

	public boolean removeAndPost(int delay, Runnable action) {
		return removeAndPostAt(System.currentTimeMillis() + delay, action);
	}

	public boolean removeAndPostAt(long when, Runnable action) {
		synchronized (lock) {
			_cache.toExecute = action;

			boolean remove = queue.remove(_cache);
			queue.add(new Executable(when, action));
			sortQueue();
			lock.notify();

			return remove;
		}
	}

	private void sortQueue() {
		Collections.sort(queue, new Comparator<Executable>() {
			@Override
			public int compare(Executable o1, Executable o2) {
				return Long.compare(o1.when, o2.when);
			}
		});
	}
}