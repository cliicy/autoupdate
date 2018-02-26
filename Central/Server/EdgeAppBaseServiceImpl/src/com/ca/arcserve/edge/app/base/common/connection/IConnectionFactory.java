package com.ca.arcserve.edge.app.base.common.connection;

import com.ca.arcserve.edge.app.base.appdaos.EdgeD2DHost;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;

public interface IConnectionFactory {
	
	D2DConnection createD2DConnection(IConnectionContextProvider contextProvider);
	D2DConnection createD2DConnection(int nodeId);
	D2DConnection createVMBackupProxyConnection(EdgeD2DHost proxy);
	
	RPSConnection createRPSConnection(IConnectionContextProvider contextProvider);
	RPSConnection createRPSConnection(int rpsNodeId);
	
	LinuxD2DConnection createLinuxD2DConnection(IConnectionContextProvider contextProvider);
	LinuxD2DConnection createLinuxD2DConnection(int nodeId);
	
	ASBUConnection createASBUConnection(IConnectionContextProvider contextProvider);
	ASBUConnection createASBUConnection(int nodeId);
	
	OldASBUConnection createOldASBUConnection(IConnectionContextProvider contextProvider);
	
	InstantVMConnection createInstantVMConnection(IConnectionContextProvider contextProvider);
	InstantVMConnection createInstantVMConnection(int nodeId);

	ConsoleConnection createConsoleConnection( IConnectionContextProvider contextProvider );
	ConsoleConnection createConsoleConnection( ConnectionContext context );
	
}
