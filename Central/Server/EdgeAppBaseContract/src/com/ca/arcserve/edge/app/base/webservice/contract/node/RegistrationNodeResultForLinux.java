package com.ca.arcserve.edge.app.base.webservice.contract.node;

public class RegistrationNodeResultForLinux extends RegistrationNodeResult {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6760822272703064662L;
	private boolean existLinuxBackupServer;

	public boolean isExistLinuxBackupServer() {
		return existLinuxBackupServer;
	}

	public void setExistLinuxBackupServer(boolean existLinuxBackupServer) {
		this.existLinuxBackupServer = existLinuxBackupServer;
	}
	
}
