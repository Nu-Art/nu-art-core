package com.nu.art.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class JavaHandler {

	private boolean running = true;
	private Thread thread;

	private class Executable {

		final Runnable toExecute;

		final long when;

		private Executable(Runnable toExecute, long when) {
			this.toExecute = toExecute;
			this.when = when;
		}
	}

	public final synchronized void start(String name) {
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
						} catch (InterruptedException ignore) { }
					}
				}
			}
		}, name);
		thread.start();
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

	public void post(Runnable toExecute) {
		postImpl(toExecute, System.currentTimeMillis());
	}

	public void post(int delay, Runnable toExecute) {
		postImpl(toExecute, System.currentTimeMillis() + delay);
	}

	private void postImpl(Runnable toExecute, long when) {
		synchronized (lock) {
			queue.add(new Executable(toExecute, when));
			sortQueue();
			recalculateSleep();
		}
	}

	public boolean remove(Runnable toRemove) {
		synchronized (lock) {
			boolean remove = queue.remove(new Executable(toRemove, 0));
			if (remove) {
				recalculateSleep();
			}
			return remove;
		}
	}

	private void recalculateSleep() {
		synchronized (lock) {
			lock.notify();
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