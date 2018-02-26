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

public abstract class HeartBeatJob extends BaseJob{
	private static final Logger logger = Logger.getLogger(HeartBeatJob.class);
	
	protected INodeService nodeService;
	
	public void changeHeartBeatStatus(final boolean enabled){
		
		Runnable task = new Runnable(){

			@Override
			public void run() {
				logger.debug("change heart beat status begin...");
				
				int[] nodeIDs = prepareNodeIDs();
				for (int nodeID:nodeIDs){
					NodeDetail nodeDetail = null;
					try{
						nodeDetail = getNodeDetailWithPolicy(nodeService, nodeID);
						
						if (!nodeDetail.isVCMMonitee()) {
							continue;
						}
						
						VCMServiceManager.getInstance().changeHeartBeatStatus(nodeDetail, enabled);
						
						if (enabled)
							generateLog(Severity.Information, Module.VCMHeartBeat, nodeDetail, EdgeCMWebServiceMessages.getMessage("resumeHeartbeatSuccessfully"));
						else
							generateLog(Severity.Information, Module.VCMHeartBeat, nodeDetail, EdgeCMWebServiceMessages.getMessage("pauseHeartbeatSuccessfully"));
					}catch(SOAPFaultException e){
						generateLog(Severity.Error, Module.VCMHeartBeat, nodeDetail, getD2DErrorMessage(e.getFault().getFaultCodeAsQName().getLocalPart()));
					}catch(WebServiceException e){
						generateLog(Severity.Error, Module.VCMHeartBeat, nodeDetail, EdgeCMWebServiceMessages.getMessage("failedtoConnectMonitee", nodeDetail.getHostname()));
					}catch(Exception e){
						generateLog(Severity.Error, Module.VCMHeartBeat, nodeDetail, EdgeCMWebServiceMessages.getMessage("failedToChangeHeartBeatJobStatus"));
					}
				}
				
				logger.debug("change heart beat status ends");
			}
		};
		
		EdgeExecutors.getCachedPool().submit(task);
	}


	protected abstract int[] prepareNodeIDs();
	
	public static class HeartBeatJobForGroup extends HeartBeatJob{
		private int groupID;
		private int groupType;
		
		public HeartBeatJobForGroup(INodeService nodeService, int groupID, int groupType){
			this.nodeService = nodeService;
			this.groupID = groupID;
			this.groupType = groupType;
		}

		@Override
		protected int[] prepareNodeIDs() {
			return this.getNodeIDByGroup(nodeService, groupID, groupType);
		}
	}
	
	public static class HeartBeatJobForNodes extends HeartBeatJob{
		private int[] nodeIDs;
		
		public HeartBeatJobForNodes(INodeService nodeService, int[] nodeIDs){
			this.nodeService = nodeService;
			this.nodeIDs = nodeIDs;
		}
		
		@Override
		protected int[] prepareNodeIDs() {
			return nodeIDs;
		}
		
	}
}
