package com.ca.arcflash.webservice.scheduler;

import org.quartz.simpl.SimpleThreadPool;

public class BackupSimpleThreadPool extends SimpleThreadPool {

	@Override
	public int blockForAvailableThreads() {
		return 1;
	}

	/**
	 * Run the job serially. At one time, only allow one backup job are executed. So we don't need multiple threads. Just single thread is enough.  We don't block it and let all the jobs gets executed.
	 * 
	 */
	@Override
	public boolean runInThread(Runnable runnable) {
		runnable.run();
		return true;
	}
}
