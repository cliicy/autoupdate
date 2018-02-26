package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.MonitorHyperVInfo;

public interface IEdgeVCMService {
	
	void testMonitorConnection(HostConnectInfo monitorInfo) throws EdgeServiceFault;
	
	MonitorHyperVInfo getMonitorHyperVInfo(HostConnectInfo monitorInfo) throws EdgeServiceFault;
	
	void validateSource(HostConnectInfo monitorInfo, String path, String domain, String user, String pwd, boolean isNeedCreateFolder) throws EdgeServiceFault;
	
	Volume[] getMonitorVolumes(HostConnectInfo monitorInfo) throws EdgeServiceFault;
	
	FileFolderItem getFiles(HostConnectInfo hostInfo, String parentFolder, String user, String password) throws EdgeServiceFault;
	Volume[] getVolumesByHostConnect(HostConnectInfo hostInfo) throws EdgeServiceFault;
	boolean createFolderByHostConnect(HostConnectInfo hostInfo, String parentFolder, String folderName) throws EdgeServiceFault;
}
