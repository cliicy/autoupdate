package com.ca.arcflash.ui.server;

import java.util.Date;

import com.ca.arcflash.rps.webservice.endpoint.IRPSService4CPM;
import com.ca.arcflash.rps.webservice.endpoint.IRPSService4D2D;
import com.ca.arcflash.webservice.BaseWebServiceClientProxy;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;

public class RPSSharingServiceProxy implements IFlashRPSSharingService {

	@Override
	public String validateUser(BaseWebServiceClientProxy proxy,
			String username, String password, String domain) {
		return proxy.getService(IRPSService4D2D.class).validateUser(username,
				password, domain);
	}

	@Override
	public Volume[] getVolumes(BaseWebServiceClientProxy proxy) {
		return proxy.getService(IRPSService4CPM.class).getVolumes();
	}

	@Override
	public int validateUserByUUID(BaseWebServiceClientProxy proxy, String uuid) {
		return proxy.getService(IRPSService4D2D.class).validateUserByUUID(uuid);
	}

	@Override
	public boolean createFolder(BaseWebServiceClientProxy proxy,
			String parentPath, String subDir) {
		return proxy.getService(IRPSService4CPM.class).createFolder(parentPath,
				subDir);
	}

	@Override
	public FileFolderItem getFileFolderWithCredentials(
			BaseWebServiceClientProxy proxy, String path, String user,
			String pwd) {
		return proxy.getService(IRPSService4CPM.class)
				.getFileFolderWithCredentials(path, user, pwd);
	}

	@Override
	public Volume[] getVolumesWithDetails(BaseWebServiceClientProxy proxy,
			String backupDest, String usr, String pwd) {
		return proxy.getService(IRPSService4CPM.class).getVolumesWithDetails(
				backupDest, usr, pwd);
	}

	@Override
	public Date getServerTime(BaseWebServiceClientProxy proxy) {
		return proxy.getService(IRPSService4CPM.class).getServerTime();
	}

}
