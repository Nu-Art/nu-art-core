package com.nu.art.core.test.archiver;

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
		handler.start("Test Handler");

		final PrintRunnable printRunnableNH = new PrintRunnable("N-H", 1200);
		final PrintRunnable printRunnableRH = new PrintRunnable("R-H", 1000);
		PrintRunnable[] items = new PrintRunnable[]{
			new PrintRunnable("A"),
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
		System.out.println(message);
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
			log(string + "(" + (System.currentTimeMillis() - startedAt) + "ms)");
		}
	}
}
