package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SimpleSortPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.ShareFolderDestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.SharedFolderBrowseParam;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.PlanInDestination;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.ProtectedNodeInDestination;

public interface IShareFolderManagementService {
	PagingResult<ShareFolderDestinationInfo> getSharedFolderDestinationList( SimpleSortPagingConfig shareFolderPagingConfig, GatewayId gatewayId) throws EdgeServiceFault;
	ShareFolderDestinationInfo getSharedFolderWithpassword (int destinationId)throws EdgeServiceFault;
	List<PlanInDestination> getPlansFromSharedFolder( SharedFolderBrowseParam param )  throws EdgeServiceFault;
	List<ProtectedNodeInDestination> getNodesDetailFromSharedFolder( SharedFolderBrowseParam param, List<ProtectedNodeInDestination> needUpdates ) throws EdgeServiceFault;

	//browse share folder
	FileFolderItem getFileFolderWithCredentials(GatewayId gateway,String path,String user, String pwd) throws EdgeServiceFault; 
	boolean createFolderOnDestination(GatewayId gateway,String parentPath, String subDir)throws EdgeServiceFault;
	NetworkPath[] getMappedNetworkPathOnDestination(GatewayId gateway, String userName) throws EdgeServiceFault;
	long getDestDriveType(GatewayId gateway, String path) throws EdgeServiceFault;
	Volume[] getVolumesFromDestination(GatewayId gateway) throws EdgeServiceFault;
	String getMntPathFromVolumeGUID(GatewayId gateway, String strGUID) throws EdgeServiceFault;
	long validateDest(GatewayId gateway, String path, String domain, String user, String pwd) throws EdgeServiceFault;
	long validateDestForMode(GatewayId gateway, String path, String domain, String user, String pwd,int mode)throws EdgeServiceFault;
	void deleteShareFolderByid(int destinationId) throws EdgeServiceFault;
	public void updateSharedFolder(String destination, String username, String password, GatewayId gatewayId)  throws EdgeServiceFault;
}
