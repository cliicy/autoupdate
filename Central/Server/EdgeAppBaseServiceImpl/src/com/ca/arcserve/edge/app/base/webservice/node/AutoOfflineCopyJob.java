package com.ca.arcserve.edge.app.base.webservice.node;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.INodeService;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;

public abstract class AutoOfflineCopyJob extends BaseJob{
	private static final Logger logger = Logger.getLogger(AutoOfflineCopyJob.class);
	
	protected INodeService nodeService;
	protected boolean forceSmartCopy = false;
	
	public void changeAutoOfflineCopyStatus(final boolean enabled){
		
		Runnable task = new Runnable(){

			@Override
			public void run() {
				logger.debug("change offline copy status begin...");
				
				int[] nodeIDs = prepareNodeIDs();
				for (int nodeID:nodeIDs){
					NodeDetail nodeDetail = null;
					try{
						nodeDetail = getNodeDetailWithPolicy(nodeService, nodeID);
						
						if (!nodeDetail.isVCMMonitee()) {
							continue;
						}
						
						VCMServiceManager.getInstance().changeAutoOfflieCopyStatus(nodeDetail, enabled, forceSmartCopy);
						
						if (enabled)
							generateLog(Severity.Information, Module.VCMVirtualStandby, nodeDetail, EdgeCMWebServiceMessages.getMessage("resumecoldStandbySuccessfully"));
						else
							generateLog(Severity.Information, Module.VCMVirtualStandby, nodeDetail, EdgeCMWebServiceMessages.getMessage("pausecoldStandbySuccessfully"));
					}catch(SOAPFaultException e){
						generateLog(Severity.Error, Module.VCMVirtualStandby, nodeDetail, getD2DErrorMessage(e.getFault().getFaultCodeAsQName().getLocalPart()));
					}catch(WebServiceException e){
						generateLog(Severity.Error, Module.VCMVirtualStandby, nodeDetail, EdgeCMWebServiceMessages.getMessage("failedtoConnectMonitee", nodeDetail.getHostname()));
					}catch(Exception e){
						// TODO yaoyu01 i18n later
						generateLog(Severity.Error, Module.VCMVirtualStandby, nodeDetail, EdgeCMWebServiceMessages.getMessage("failedToChangeVirtualStandbyJobStatus"));
					}
				}
				
				logger.debug("change heart beat status ends");
			}

		};
		
		EdgeExecutors.getCachedPool().submit(task);
	}
	
	protected abstract int[] prepareNodeIDs();
	
	public static class AutoOfflineCopyJobForGroup extends AutoOfflineCopyJob{
		private int groupID;
		private int groupType;
		
		public AutoOfflineCopyJobForGroup(INodeService nodeService, int groupID, int groupType){
			this.nodeService = nodeService;
			this.groupID = groupID;
			this.groupType = groupType;
		}

		@Override
		protected int[] prepareNodeIDs() {
			return this.getNodeIDByGroup(nodeService, groupID, groupType);
		}
	}
	
	public static class AutoOfflineCopyJobForNodes extends AutoOfflineCopyJob{
		private int[] nodeIDs;
		
		public AutoOfflineCopyJobForNodes(INodeService nodeService, int[] nodeIDs, boolean forceSmartCopy){
			this.nodeService = nodeService;
			this.nodeIDs = nodeIDs;
			this.forceSmartCopy = forceSmartCopy;
		}
		
		@Override
		protected int[] prepareNodeIDs() {
			return nodeIDs;
		}
		
	}
}
