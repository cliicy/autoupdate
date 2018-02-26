package com.ca.arcserve.edge.app.base.webservice;

import javax.jws.WebService;

import com.arcserve.edge.rbac.webservice.IRBACService;
import com.ca.arcflash.listener.service.IFlashListener;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcserve.edge.app.asbu.webservice.IASBUService;
import com.ca.arcserve.edge.app.base.webservice.client.IBaseService;
import com.ca.arcserve.edge.app.base.webservice.d2dapm.IPatchManager;
import com.ca.arcserve.edge.app.msp.webservice.IEdgeMspService;
import com.ca.arcserve.edge.app.rps.webservice.IEdgeRPSService;

@WebService(targetNamespace="http://webservice.edge.arcserve.ca.com/")
public interface IEdgeService extends IBaseService,
	IEdgeSyncService,
	IEdgeSRMService,
	IEdgeD2DService,
	IEdgeABFuncIntegrationService,
	INodeService,
	IActivityLogService,
	IJobHistroryService,
	IEdgeConfigurationService,
	IPolicyManagementService,
	IEdgeD2DReSyncService,
	IEdgeD2DRegService,
	IPatchManager,
	IEdgeCommonService,
	IEdgeApmForEdge,
	IEdgeApmForD2D,
	IEdgeVSphere4EdgeVCM,
	IEdgeCM4D2D,
	IEdgeCM4EdgeReport,
	IEdgeRPSService,
	IFlashListener,
	IEdgeLinuxD2DService,
	IEdgeMspService,
	IEdgeLicenseService,
	IInstantVMService,
	IRecoveryPointService,
	IShareFolderManagementService,
	IASBUService,
	IProductDeployService,
	IDiscoveryService,
	IEdgeGatewayService,
	IStorageApplianceService,
	IEdgeVCMService,
	IRBACService,
	ICloudAccountService,
	IActionCenterService,
	IEdgeAERPService
{
	/**
	 *  don't use oneway method since it isn't able to reply with any thing even the Exception
	 * @param arg
	 */
//	void testVoidMethod(String arg) throws EdgeServiceFault;

}
