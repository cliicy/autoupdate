package com.ca.arcserve.edge.app.base.schedulers.impl;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.schedulers.IEdgeTaskItem;

public class EdgeTask implements Runnable {
	private static final int DEFAULT_EXECUTE_QUEUE_SIZE = 16;
	private static final int DEFAULT_SLEEP_TIME = 20000;
	
	// this is a waiting queue, the item will be pecked by Task Monitor Thread 
	// to execute
	private LinkedBlockingQueue<IEdgeTaskItem> mWaitingQueue;
	private ArrayBlockingQueue<EdgeRunningTask> mExecuteQueue;
	private LinkedBlockingQueue<EdgeRunningTask> mImmediatelyExecuteQueue;
	private boolean mCancelFlag;
	private Boolean mIsRunning = false;
	
	private static Logger logger = Logger.getLogger(EdgeTask.class);
	
	public EdgeTask() {
		mWaitingQueue = new LinkedBlockingQueue<IEdgeTaskItem>();
		mExecuteQueue = new ArrayBlockingQueue<EdgeRunningTask>(DEFAULT_EXECUTE_QUEUE_SIZE);
		mImmediatelyExecuteQueue = new LinkedBlockingQueue<EdgeRunningTask>();
	}
	
	public void setMaxExecuteQueueSize(int size) {
		synchronized (mExecuteQueue) {
			mExecuteQueue = new ArrayBlockingQueue<EdgeRunningTask>(size);
		}
	}
	
	public int getWaitingQueueSize() {
		synchronized (mWaitingQueue) {
			return mWaitingQueue.size();
		}
	}
	
	public int getExecuteQueueSize() {
		synchronized (mExecuteQueue) {
			return mExecuteQueue.size();
		}
	}
	
	public int getImmediatelyExecuteQueue() {
		synchronized(mImmediatelyExecuteQueue) {
			return mImmediatelyExecuteQueue.size();
		}
	}
	
	public void AddToWaitingQueue(IEdgeTaskItem item) throws InterruptedException {
		mWaitingQueue.put(item);
	}
	
	public boolean IsExistedInWaitingQueue(IEdgeTaskItem item) throws InterruptedException {
		return mWaitingQueue.contains(item);
	}
	
	public void AddToExecuteQueue(IEdgeTaskItem item) throws InterruptedException {
		// Check the item whether is executing, if yes, skip adding
		for(EdgeRunningTask task : mExecuteQueue) {
			if (task.getItem() == item)
				return;
		}
		
		// check the item whether is in immediately execute queue
		for(EdgeRunningTask task : mImmediatelyExecuteQueue) {
			if (task.getItem() == item)
				return;
		}
		
		// Check the item whether is staying in waiting queue, if yes, remove it
		if (mWaitingQueue.contains(item)) {
			mWaitingQueue.remove(item);
		}
		
		// start the task
		EdgeRunningTask runningTask = new EdgeRunningTask();
		Future<?> future = EdgeExecutors.getFixedPool().submit(item);
		runningTask.setFuture(future);
		runningTask.setItem(item);
		mImmediatelyExecuteQueue.add(runningTask);
	}
	
	public void CleanupWaitingQueue() {
		mWaitingQueue.clear();
	}
	
	public void Stop() {
		synchronized(mIsRunning) {
			mIsRunning =  false;
			mCancelFlag = true;
		}
	}
	

	@Override
	public void run() {
		synchronized (mIsRunning) {
			if (!mIsRunning) {
				EdgeExecutors.getCachedPool().submit(new EdgeTaskRunner());
				EdgeExecutors.getCachedPool().submit(new EdgeTaskChecker());
				mIsRunning = true;
			}
		}
	}
	
	class EdgeRunningTask {
		private IEdgeTaskItem mItem;
		private Future<?> mFuture;
		
		public IEdgeTaskItem getItem() {
			return mItem;
		}
		
		public void setItem(IEdgeTaskItem item) {
			mItem = item;
		}
		
		public Future<?> getFuture() {
			return mFuture;
		}
		
		public void setFuture(Future<?> f) {
			mFuture = f;
		}
	}
	
	class EdgeTaskRunner implements Runnable {

		@Override
		public void run() {
			// initialize environment
			mCancelFlag = false;
			
			for(;;) {
							
				// check the waiting queue

				// peck an item from waiting queue to execute
				IEdgeTaskItem item = null;
				try {
					item = mWaitingQueue.take();
					if (item != null) {
						if (mCancelFlag) {
							logger.info("EdgeTaskRunner : cancel flag is set");
							break;
						}
						Future<?> f = EdgeExecutors.getFixedPool().submit(item);
						EdgeRunningTask task = new EdgeRunningTask();
						task.setFuture(f);
						task.setItem(item);

						mExecuteQueue.put(task);// if it is full, blocking
					}
				}
				catch (InterruptedException e)
				{
					break;
				} catch (Exception e) {
					logger.error("EdgeTask failed!",e);
				}

			}

			logger.info("EdgeTaskRunner ready exit");
		}
			
	}
	
	class EdgeTaskChecker implements Runnable {

		@Override
		public void run() {
			while (!mCancelFlag) {
				if (!mExecuteQueue.isEmpty()) {
					for (EdgeRunningTask task : mExecuteQueue) {
						if (task.getFuture().isDone() || task.getFuture().isCancelled()) {
							mExecuteQueue.remove(task);
						}
					}
				}
				
				try {
					Thread.sleep(DEFAULT_SLEEP_TIME);
				} catch (InterruptedException e) {
					logger.error("EdgeTask failed!",e);
					break;
				}
			}
			
			logger.info("EdgeTaskChecker ready exit");
		}
		
	}
	
}
