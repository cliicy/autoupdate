package com.ca.arcserve.edge.app.base.initialization.common;

import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;


public abstract class BaseComponentsAssembler extends BaseAppInitializer
{

	@Override
	protected void doInitialization()
	{
		doAppSpecificInitialization();
		
		EdgeFactory.setAssemblingFinished( true );
	}

	@Override
	protected void doUninitialization()
	{
		doAppSpecificUninitialization();
	}

	protected abstract void doAppSpecificInitialization();
	protected abstract void doAppSpecificUninitialization();
}
