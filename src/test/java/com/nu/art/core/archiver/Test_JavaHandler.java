package com.nu.art.core.archiver;

import com.nu.art.core.utils.JavaHandler;

import org.junit.Test;

/**
 * Created by TacB0sS on 24/09/2017.
 */

public class Test_JavaHandler {

	long startedAt;

	@Test
	public void test_Handler() {

		final JavaHandler handler = new JavaHandler();
		handler.setMinThreads(3);
		handler.start("test-handler");

		final PrintRunnable printRunnableNH = new PrintRunnable("N-H", 1200);
		final PrintRunnable printRunnableRH = new PrintRunnable("R-H", 1000);
		PrintRunnable[] items = new PrintRunnable[]{
			new PrintRunnable("A0"),
			new PrintRunnable("A1"),
			new PrintRunnable("A2"),
			new PrintRunnable("A3"),
			new PrintRunnable("A4"),
			new PrintRunnable("A5"),
			new PrintRunnable("A6"),
			new PrintRunnable("A7"),
			new PrintRunnable("B", 1000),
			new PrintRunnable("C"),
			new PrintRunnable("D", 500),
			new PrintRunnable("E", 200),
			new PrintRunnable("F", 100),
			printRunnableRH,
			new PrintRunnable("G")
		};

		startedAt = System.currentTimeMillis();

		for (PrintRunnable runnable : items) {
			handler.post(runnable.delay, runnable);
		}
		handler.post(800, new Runnable() {
			@Override
			public void run() {
				log("removing R-H");
				handler.remove(printRunnableRH);
				handler.post(printRunnableNH.delay, printRunnableNH);
			}
		});
		handler.post(5000, new Runnable() {
			@Override
			public void run() {
				log("Terminate");
				handler.stop();

				synchronized (handler) {
					handler.notify();
				}
			}
		});
		synchronized (handler) {
			try {
				handler.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void log(String message) {
		System.out.println(String.format("%6dms: ", (System.currentTimeMillis() - startedAt)) + Thread.currentThread().getName() + " - " + message);
	}

	private void logError(String message) {
		System.err.println(message);
	}

	private class PrintRunnable
		implements Runnable {

		final String string;

		final int delay;

		private PrintRunnable(String string) {
			this(string, 0);
		}

		private PrintRunnable(String string, int delay) {
			this.string = string;
			this.delay = delay;
		}

		@Override
		public void run() {
			log(string);
		}
	}
}
