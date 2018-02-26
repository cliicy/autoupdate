package com.ca.arcflash.ui.server;

import java.util.Date;

import com.ca.arcflash.webservice.BaseWebServiceClientProxy;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;

public interface IFlashRPSSharingService {
	
	String validateUser(BaseWebServiceClientProxy proxy, String username, String password, String domain);
	
	int validateUserByUUID(BaseWebServiceClientProxy proxy, String uuid);
	
	Volume[] getVolumes(BaseWebServiceClientProxy proxy);
	
	boolean createFolder(BaseWebServiceClientProxy proxy, String parentPath, String subDir);

	FileFolderItem getFileFolderWithCredentials(BaseWebServiceClientProxy proxy, String path, String user,
			String pwd);
	
	Volume[] getVolumesWithDetails(BaseWebServiceClientProxy proxy, String backupDest, String usr, String pwd);
	
	public Date getServerTime(BaseWebServiceClientProxy proxy);

}
