package com.ca.arcserve.edge.app.base.common;

import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;

public class LinuxNodeUtil {
	
	public static Severity getMessageType(String[] result){
		Severity ret = Severity.Error;
		if(result !=null ){
			if(result[0] != null){
				int errorCode0 = Integer.valueOf(result[0]);
				if(errorCode0 == 20){
					if(result[1] != null){
						int errorCode1 = Integer.valueOf(result[1]);
						if ( (errorCode1 & 0x2) != 0 || (errorCode1 & 0x4) != 0 ) {//no perl or no parted
		  					ret =  Severity.Error;
		  				}
		  				
		  				ret =  Severity.Warning;//no nfs or cifs
					}
				}else if(errorCode0 == 30){
					ret = Severity.Warning;
				}else{
					ret = Severity.Error;
				}
			}
		}
		return ret;
	}
	
	public static boolean isSSHKeyAuthentication(String username, String password){
		if(StringUtil.isEmptyOrNull(username) && StringUtil.isEmptyOrNull(password)){
			return true;
		}
		return false;
	}
	
	public static String getLinuxMessage(RegistrationNodeResult result,String nodeName){
		String mesg = "";
		if(result.getErrorCodes() !=null ){
			if(result.getErrorCodes()[0] != null){
				int errorCode = Integer.valueOf(result.getErrorCodes()[0]).intValue();

	  			switch ( errorCode )
	  			{
	  			case 1:
	  			case 3:
	  			case 9:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedWrongNetwork",nodeName);
	  				break;
	  			case 2:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedWrongUserAccount");
	  				break;
	  			case 22:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedWithSSHKey");
	  				break;
	  			case 4:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedExecuteCheckMachine",nodeName);
	  				break;
	  			case 5:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedNotSupportedPlatform");
	  				break;
	  			case 6:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedNotSupportedLinuxPlatform");
	  				break;
	  			case 7:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedNotSupportedArch");
	  				break;
	  			case 8:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedNotSupportedOSVersion");
	  				break;
	  			case 20:
	  			{
	  				String extErrMsg = "";
	  				String extWarnMsg = "";
	  				if ( result.getErrorCodes()[1] != null )
	  				{
	  					int errorCodeExt = Integer.valueOf(result.getErrorCodes()[1]).intValue();
	  					
	  					if ( (errorCodeExt&0x01) != 0 )
	  					{
	  						extWarnMsg = EdgeCMWebServiceMessages.getMessage("connectFailedNotRootUser");
	  					}
	  					if ( (errorCodeExt&0x02) != 0 )
	  					{
	  						if ( !extErrMsg.isEmpty() )
	  						{
	  							extErrMsg += ", ";
	  						}
	  						extErrMsg += EdgeCMWebServiceMessages.getMessage("connectFailedNotRootUser");
	  					}
	  					if ( (errorCodeExt&0x08) != 0 )
	  					{
	  						if ( !extWarnMsg.isEmpty() )
	  						{
	  							extWarnMsg += ", ";
	  						}
	  						extWarnMsg += EdgeCMWebServiceMessages.getMessage("connectFailedNotRootUser");
	  					}  					
	  				}
	  				if ( !extErrMsg.isEmpty() ) {
	  					mesg = EdgeCMWebServiceMessages.getMessage("connectFailedNoModules") + extErrMsg;
	  				} else if ( !extWarnMsg.isEmpty() ) {
	  					mesg = EdgeCMWebServiceMessages.getMessage("connectWarnningNoModules") + extWarnMsg;
	  				}
	  				break;
	  			}
	  			case 30:
	  				mesg = EdgeCMWebServiceMessages.getMessage("failedToManageD2DByAnotherServe",  nodeName, result.getErrorCodes()[2]);
	  				break;
	  			case 31:
	  				mesg = EdgeCMWebServiceMessages.getMessage("forceManageItFailedWithRunningJob");
	  				break;
	  			case 40:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedNotRootUser");
	  				break;
	  			case 41:
	  				mesg = EdgeCMWebServiceMessages.getMessage("invalidRootPasswd");
	  				break;
	  			case 42:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedExecutePwdExpired",nodeName);
	  				break;
	  			case 100:
	  				mesg = EdgeCMWebServiceMessages.getMessage("connectFailedUndefinedReason");
	  				break;  
	  			}
			}
		}
		return mesg;
	}

}
