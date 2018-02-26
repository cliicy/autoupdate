package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import java.util.List;

public class EmailUtils {

	public static int mergeEmailSettings(IEmailConfigModel orgEmailConfigModel, IEmailConfigModel edgeEmailconfigModel){
		if(orgEmailConfigModel==null){
			return -1;
		}
		
		if(edgeEmailconfigModel==null){
			return 1;
		}
		
		String orgSmtp = orgEmailConfigModel.getSMTP();
		if((orgSmtp == null)||(orgSmtp.trim().isEmpty())){
			orgEmailConfigModel.setEnableHTMLFormat(edgeEmailconfigModel.getEnableHTMLFormat());
			orgEmailConfigModel.setEnableMailAuth(edgeEmailconfigModel.isEnableMailAuth());
			orgEmailConfigModel.setEnableProxy(edgeEmailconfigModel.isEnableProxy());
			orgEmailConfigModel.setEnableProxyAuth(edgeEmailconfigModel.isEnableProxyAuth());
			orgEmailConfigModel.setEnableSsl(edgeEmailconfigModel.isEnableSsl());
			orgEmailConfigModel.setEnableTls(edgeEmailconfigModel.isEnableTls());
			orgEmailConfigModel.setFromAddress(edgeEmailconfigModel.getFromAddress());
			orgEmailConfigModel.setMailPwd(edgeEmailconfigModel.getMailPwd());
			orgEmailConfigModel.setMailService(edgeEmailconfigModel.getMailService());
			orgEmailConfigModel.setMailUser(edgeEmailconfigModel.getMailUser());
			orgEmailConfigModel.setProxyAddress(edgeEmailconfigModel.getProxyAddress());
			orgEmailConfigModel.setProxyPassword(edgeEmailconfigModel.getProxyPassword());
			orgEmailConfigModel.setProxyPort(edgeEmailconfigModel.getProxyPort());
			orgEmailConfigModel.setProxyUsername(edgeEmailconfigModel.getProxyUsername());
			
			List<String> edgeRecipients = edgeEmailconfigModel.getRecipients();
			if((edgeRecipients!=null)&&(edgeRecipients.size()>0)){
				orgEmailConfigModel.setRecipients(edgeEmailconfigModel.getRecipients());
			}
			
			orgEmailConfigModel.setSMTP(edgeEmailconfigModel.getSMTP());
			orgEmailConfigModel.setSmtpPort(edgeEmailconfigModel.getSmtpPort());
			orgEmailConfigModel.setSubject(edgeEmailconfigModel.getSubject());
		}
		
		return 0;
	}
}
