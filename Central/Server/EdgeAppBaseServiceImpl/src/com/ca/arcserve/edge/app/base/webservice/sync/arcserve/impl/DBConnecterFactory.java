package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class DBConnecterFactory {
	public static IDBConnecter getConnectInstance(){
		return new DBConnecter(ConfigurationOperator.getDefaultDB());
	}
}
