package com.ca.arcserve.edge.app.base.webservice.recoverypoints;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeService_Oolong;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationBrowser;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlanDestinationType;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;

public class SessionPasswordHandler {
	private static final Logger logger = Logger.getLogger( SessionPasswordHandler.class );
	public boolean validateRecoveryPointPassword(RecoveryPointInformationForCPM rpWithNode) throws EdgeServiceFault{
		DestinationBrowser browser = rpWithNode.getBrowser();
		//node protected by windows agent: include linux node backup by hbbu;
		if( rpWithNode.isBackByWindowsAgent() ) {
			try(D2DConnection conn = RecoveryPointBrowseUtil.getInstance().getDestinationBrowserAgentService(rpWithNode.getBrowser()))  {
				conn.connect();
				ID2D4EdgeService_Oolong service = conn.getService();
				RecoveryPoint rp = rpWithNode.getRecoveryPoint();
				return service.validateSessionPasswordByHash( rpWithNode.getSessionPassword(), rpWithNode.getSessionPassword().length(), 
						rp.getEncryptPasswordHash() , rp.getEncryptPasswordHash().length() );
			}
		}
		//linux agent 
		else {  //linux agent backup to nfs and cifs;
			
			try(LinuxD2DConnection linuxConn = RecoveryPointBrowseUtil.getInstance().getLinuxDestinationBrowser(browser)){
				ILinuximagingService linuxServer = linuxConn.getService();
//				if( browser.getDestinationType() == PlanDestinationType.SharedFolder ) {  //it can cover datastore
					BackupLocationInfo backupLocation =  RecoveryPointBrowseUtil.getInstance().getDestinationWithPasswordForLinux( browser);
					com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint linuxRP = RecoveryPointBrowseUtil.getInstance().convertToLinuxRecoveryPoint( rpWithNode.getRecoveryPoint() );
					linuxRP.setEncryptionPassword( rpWithNode.getSessionPassword() );
					return linuxServer.checkRecoveryPointPasswd( backupLocation, getMachineName(rpWithNode), linuxRP , null );
//				}
			}
			
		}
//		logger.info( "validateRecoveryPointPassword(): incorrect node or recoverypoint information! ");
//		return false;
	}
	
	public String getMachineName(RecoveryPointInformationForCPM rpWithNode){
		if(rpWithNode.getBrowser().getDestinationType() == PlanDestinationType.RPS){
//			return rpWithNode.getRecoveryPoint().getNodeUuid();
			int index = rpWithNode.getProtectedNode().getDestination().lastIndexOf("\\");
			String machineName = rpWithNode.getProtectedNode().getDestination().substring(index+1);
			return machineName;
		}else{
			return rpWithNode.getProtectedNode().getNodeName();
		}
	}
}
