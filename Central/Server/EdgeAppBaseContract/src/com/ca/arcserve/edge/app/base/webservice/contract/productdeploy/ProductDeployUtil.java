package com.ca.arcserve.edge.app.base.webservice.contract.productdeploy;

import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.ProductType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;

public class ProductDeployUtil {
	
	public static String[] getFinalMessage(int productType, TaskStatus overAllStatus,
			DeployStatus deployStatus, String progressMessage,String warnningMessage,long timeLenthOfConnectWS, String gatewayHostName){
		String finalTitleMessage = "";
    	String finalDetailMessage = "";
		String productReplacer = "";

		if( Integer.parseInt(ProductType.ProductD2D) == productType)
			productReplacer = EdgeCMWebServiceMessages.getResource("productShortNameD2D");
		else
			productReplacer = EdgeCMWebServiceMessages.getResource("productShortNameRPS");
		
		String statusMessage = getMessageByDeployStatus(deployStatus, timeLenthOfConnectWS, productReplacer, gatewayHostName);
		
		switch (overAllStatus) {
		case Error:
			finalTitleMessage = EdgeCMWebServiceMessages.getMessage("deployNewFailed", productReplacer);
	   		if (!StringUtil.isEmptyOrNull(progressMessage)){
	   			finalDetailMessage = progressMessage;
	   		}else {
	   			finalDetailMessage = statusMessage + EdgeCMWebServiceMessages.getMessage("deployErrorDetails",productReplacer);
	   		}
			break;
		case OK:
			finalTitleMessage  = EdgeCMWebServiceMessages.getMessage("deployNewFinish", productReplacer);
			break;
		case Warning:
			finalTitleMessage  = EdgeCMWebServiceMessages.getMessage("deployNewFinish", productReplacer);
			finalDetailMessage = statusMessage + warnningMessage;
   		 	//a special case! in common, when success, detail.getProgressMessage() should always empty; but when new version already exist; we enforce 
			//change inner error status to success status; we should show this "version already exist msg if success without any other warning msg"
			break;
		case WarnningCanContinue:
			finalTitleMessage = EdgeCMWebServiceMessages.getMessage("deployNewHoldOn", productReplacer);
			String continueMessage = EdgeCMWebServiceMessages.getMessage("deployContinue");
	   		if (!StringUtil.isEmptyOrNull(progressMessage)){
	   			finalDetailMessage = progressMessage+" "+continueMessage;
	   		}else {
	   			finalDetailMessage = statusMessage + EdgeCMWebServiceMessages.getMessage("deployErrorDetails",productReplacer);
	   		}
			break;
		default:
			finalTitleMessage = EdgeCMWebServiceMessages.getMessage("deployNewRunning", productReplacer);
			finalDetailMessage = statusMessage;
			break;
		}
		String[] finalMessage = new String[]{finalTitleMessage,finalDetailMessage};
		return finalMessage;
	}
	
	
	private static String getMessageByDeployStatus(DeployStatus status, long timeLenthOfConnectWS,String product, String gatewayHostname) {
		
		if (status == null) {
			return EdgeCMWebServiceMessages.getResource("deployStatusNA");
		}
		switch (status)  {
		
		case DEPLOY_PENDING_FOR_DEPLOY:
			return EdgeCMWebServiceMessages.getResource("deployStatusDeployPendingForDeploy");
			
		case DEPLOY_NA:
			return EdgeCMWebServiceMessages.getResource("deployStatusNA");
			
		case DEPLOY_CONNECTING:
			return EdgeCMWebServiceMessages.getResource("deployStatusDeployConnect");
			
		case DEPLOY_INSTALLING:
			return EdgeCMWebServiceMessages.getResource("deployStatusDeployInProcess");
			
		case DEPLOY_SUCCESS_NEEDREBOOT:
			return EdgeCMWebServiceMessages.getResource("deployStatusDeploySuccessNeedReboot");
			
		case DEPLOY_COMPLETE_REBOOTTIMEOUT:
		case DEPLOY_CONNECT_WS_TIMEOUT:
			return EdgeCMWebServiceMessages.getMessage("deployStatusCompleteRebootTimeOut",
					product,gatewayHostname,String.valueOf(timeLenthOfConnectWS));
			
		case DEPLOY_FAILED:
			return EdgeCMWebServiceMessages.getResource("deployStatusDeployFailed");
			
		case DEPLOY_FAIL_ON_PENDING:
			return EdgeCMWebServiceMessages.getResource("deployStatusFailOnPending");

		case DEPLOY_FAIL_ON_CONNECTING:
			return EdgeCMWebServiceMessages.getResource("deployStatusFailOnConnecting");
			
		case DEPLOY_FAIL_ON_COPYING_IMAGE:
			return EdgeCMWebServiceMessages.getResource("deployStatusFailOnCopying");
			
		case DEPLOY_FAIL_ON_INSTALLING:
			return EdgeCMWebServiceMessages.getResource("deployStatusFailOnInstalling");
			
		case DEPLOY_NOT_STARTED:
			return EdgeCMWebServiceMessages.getResource("deployStatusDeployNotStarted");

		case DEPLOY_THIRD_PARTY:
			return EdgeCMWebServiceMessages.getResource("deployStatusDeployThirdParty");

		case DEPLOY_COPYING_IMAGE:
			return EdgeCMWebServiceMessages.getResource("deployStatusDeployCopyingImage");

		case DEPLOY_PENDING:
			return EdgeCMWebServiceMessages.getResource("deployStatusDeployWaiting");

		case DEPLOY_REBOOTING:
			return EdgeCMWebServiceMessages.getResource("deployStatusDeployReboot");
			
		case DEPLOY_DIRECT_CONNECT_WS:
			return EdgeCMWebServiceMessages.getResource("deployStatusDirectConnectWS");
			
		case DEPLOY_DOWNLOADING_IMAGE:
			return EdgeCMWebServiceMessages.getResource("deployStatusDownloadingImage");
		
		case DEPLOY_DOWNLOADING_IMAGE_FAILED:
			return EdgeCMWebServiceMessages.getResource("deployStatusFailOnDownloadingImage");
			
		default:
			return EdgeCMWebServiceMessages.getResource("deployStatusNA");
		}
	}
}
