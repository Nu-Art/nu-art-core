package com.nu.art.core.utils;

import com.nu.art.core.interfaces.Condition;
import com.nu.art.core.interfaces.Getter;
import com.nu.art.core.tools.ArrayTools;

import static com.nu.art.core.tools.DateTimeTools.Second;

public class ThreadMonitor {

	public static final String DebugFlag = "Debug_" + ThreadMonitor.class.getSimpleName();

	private static final ThreadMonitor ThreadsMonitor = new ThreadMonitor();

	private SynchronizedObject<Monitor> monitors = new SynchronizedObject<>(new Getter<Monitor>() {
		@Override
		public Monitor get() {
			return new Monitor();
		}
	});

	public static class RunnableMonitor
		implements Runnable {

		private final Runnable runnable;
		private final long estimated;

		public RunnableMonitor(Runnable runnable) {
			this(runnable, 5 * Second);
		}

		public RunnableMonitor(Runnable runnable, long estimated) {
			this.runnable = runnable;
			this.estimated = estimated;
		}

		@Override
		public final void run() {
			ThreadsMonitor.getThreadMonitor().started(this);
			runnable.run();
		}
	}

	private Monitor getThreadMonitor() {
		return monitors.get();
	}

	public final class Monitor {

		private final Thread thread;

		private volatile RunnableMonitor runnableMonitor;
		private volatile long started;

		private Monitor() {
			thread = Thread.currentThread();
		}

		private void started(RunnableMonitor runnableMonitor) {
			this.runnableMonitor = runnableMonitor;
			started = System.currentTimeMillis();
		}

		public long getStarted() {
			return started;
		}

		private boolean isDelayed() {
			return runnableMonitor.estimated < System.currentTimeMillis() - started;
		}
	}

	public final Monitor[] monitor() {
		return ArrayTools.asFilteredArray(this.monitors.values(), Monitor.class, new Condition<Monitor>() {
			@Override
			public boolean checkCondition(Monitor monitor) {
				return monitor.isDelayed();
			}
		});
	}
}
