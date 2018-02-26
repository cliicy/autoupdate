package com.ca.arcserve.edge.app.base.initialization;

import java.util.List;

import com.ca.arcserve.edge.app.base.common.udpapplication.UDPApplication;

public interface IAppConfigurations
{
	String getLogsConfigFileName();
	List<IAppInitializer> getInitializationList();
	UDPApplication createApplicationObject();
}
