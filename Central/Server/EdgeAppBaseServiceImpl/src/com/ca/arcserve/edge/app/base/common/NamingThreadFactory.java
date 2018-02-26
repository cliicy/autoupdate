package com.ca.arcserve.edge.app.base.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * A thread factory used for create thread pool which will give all generated threads
 * a name prefix. By default, it will use the default thread factory to create
 * thread. Anyway, you can specify another thread factory to do it.
 * 
 * @author panbo01
 *
 */
public class NamingThreadFactory implements ThreadFactory
{
	private String namePrefix = "";
	private ThreadFactory threadFactory = null;
	
	public NamingThreadFactory( String namePrefix )
	{
		this( namePrefix, null );
	}
	
	public NamingThreadFactory( String namePrefix, ThreadFactory threadFactory )
	{
		assert namePrefix != null : "namePrefix should not be null.";
		if (namePrefix == null)
			throw new IllegalArgumentException();
		
		this.namePrefix = namePrefix;
		this.threadFactory = threadFactory;
	}
	
	private ThreadFactory getThreadFactory()
	{
		if (this.threadFactory == null)
			this.threadFactory = Executors.defaultThreadFactory();
		
		return this.threadFactory;
	}

	@Override
	public Thread newThread( Runnable runnable )
	{
		Thread thread = getThreadFactory().newThread( runnable );
		thread.setName( "(" + namePrefix + ") " + thread.getName() );
		return thread;
	}

}
