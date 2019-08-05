package com.nu.art.core.handler;

import com.nu.art.belog.BeLogged;
import com.nu.art.belog.Logger;
import com.nu.art.core.handler.Test_HandlerCore.PrintRunnable;
import com.nu.art.core.utils.JavaHandler;

import org.junit.Test;

import static com.nu.art.belog.loggers.JavaLogger.Config_FastJavaLogger;

/**
 * Created by TacB0sS on 24/09/2017.
 */

public class Test_SimpleQueue
	extends Test_HandlerCore {

	@Test
	public void test_Handler() {
		BeLogged.getInstance().setConfig(Config_FastJavaLogger);
		JavaHandler.DebugFlag.enable();

		final JavaHandler handler = new JavaHandler();
		handler.setLogger(this);
		handler.setMinThreads(3);
		handler.setThreadTimeoutMs(1000);
		handler.setMaxThreads(7);
		handler.start("test-handler");

		startedAt = System.currentTimeMillis();
		for (int i = 0; i < 20; i++) {
			handler.post(i * 500, new SleepRunnable(i, 2000));
		}

		handler.post(20000, new Runnable() {
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
}