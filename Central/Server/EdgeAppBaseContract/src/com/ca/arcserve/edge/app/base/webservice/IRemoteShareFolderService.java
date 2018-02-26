package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface IRemoteShareFolderService {
	
	//browse share folder
	FileFolderItem getFileFolderWithCredentials(String path,String user, String pwd) throws EdgeServiceFault; 
	boolean createFolder(String parentPath, String subDir)throws EdgeServiceFault;
	NetworkPath[] getMappedNetworkPath(String userName) throws EdgeServiceFault;
	long getDestDriveType(String path) throws EdgeServiceFault;
	Volume[] getVolumes() throws EdgeServiceFault;
	String getMntPathFromVolumeGUID(String strGUID) throws EdgeServiceFault;
	long validateDest(String path, String domain, String user, String pwd) throws EdgeServiceFault;
	
	long validateDestForMode(String path, String domain, String user, String pwd,int mode)throws EdgeServiceFault;

}
