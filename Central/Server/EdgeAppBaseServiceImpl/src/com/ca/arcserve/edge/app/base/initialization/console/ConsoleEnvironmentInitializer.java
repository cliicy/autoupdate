package com.ca.arcserve.edge.app.base.initialization.console;

import java.util.concurrent.TimeUnit;

import com.ca.arcserve.edge.app.base.initialization.common.BaseEnvironmentInitializer;
import com.ca.arcserve.edge.app.base.schedulers.EdgeDeleteApacheLogTask;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;

public class ConsoleEnvironmentInitializer extends BaseEnvironmentInitializer
{

	@Override
	protected void doAppSpecificInitialization()
	{
		// clean apache log every day
		EdgeExecutors.getSchedulePool().scheduleAtFixedRate(EdgeDeleteApacheLogTask.getInstance(), 0, 86400000, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void doAppSpecificUninitialization()
	{
	}

}
