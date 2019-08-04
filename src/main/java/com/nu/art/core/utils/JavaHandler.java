package com.nu.art.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class JavaHandler {

	private boolean running = true;
	private final ArrayList<Thread> threadPool = new ArrayList<>();
	private final Executable _cache = new Executable(0, null);
	private final Object lock = new Object();
	private final ArrayList<Executable> queue = new ArrayList<>();

	private int minThreads = 1;
	private int maxThreads = 1;
	private int threadTimeoutMs = 10000;

	private String name;

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

	public JavaHandler setThreadTimeoutMs(int threadTimeoutMs) {
		this.threadTimeoutMs = threadTimeoutMs;
		if (this.threadTimeoutMs < 1000)
			this.threadTimeoutMs = 1000;

		return this;
	}

	public JavaHandler setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
		return this;
	}

	public JavaHandler setMinThreads(int minThreads) {
		this.minThreads = minThreads;
		return this;
	}

	public final synchronized JavaHandler start(String name) {
		if (this.name != null)
			throw new RuntimeException("instance already started with name: " + name);

		this.name = name;
		running = true;

		for (int i = 0; i < minThreads; i++) {
			final Thread thread = new Thread(new Runnable() {
				long mark;

				boolean keepAlive = true;

				private void execute() {
					while (running && keepAlive) {
						Executable executable;
						synchronized (lock) {
							if (queue.size() == 0)
								return;

							if (queue.get(0).when > System.currentTimeMillis() + 50)
								return;

							executable = queue.remove(0);
						}

						executable.toExecute.run();
						mark = System.currentTimeMillis();
					}
				}

				@Override
				public void run() {
					while (running && keepAlive) {
						execute();
						if (System.currentTimeMillis() - mark > threadTimeoutMs && isDisposable()) {
							keepAlive = false;
							continue;
						}

						synchronized (lock) {
							try {
								if (queue.size() == 0) {
									lock.wait(threadTimeoutMs);
									continue;
								}

								long sleepInterval = queue.get(0).when - System.currentTimeMillis();
								if (sleepInterval < 30)
									continue;

								lock.wait(sleepInterval - 5);
							} catch (InterruptedException ignore) {}
						}
					}

					synchronized (lock) {
						threadPool.remove(Thread.currentThread());
					}
				}

				private boolean isDisposable() {
					return false;
				}
			}, this.name + "-" + threadPool.size());

			synchronized (lock) {
				threadPool.add(thread);
			}

			thread.start();
		}
		return this;
	}

	public void stop() {
		running = false;
	}

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