package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.LinkedList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.VMConnectionContextProvider;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeHyperVHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;

public class BackupNowVMJob extends BackupNowJob {
	private static final Logger logger = Logger.getLogger(BackupNowVMJob.class);
	private static final double REQUIRE_D2D_VERSION_FOR_BACKUP_SET = 16.0;
	private static final double REQUIRE_D2D_UPDATE_NUMBER_FOR_BACKUP_SET = 7;
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	
	protected BackupNowVMJob(){
		
	}
	
	public BackupNowVMJob(int groupID, int groupType, int backupType, String jobName) {
		super(groupID, groupType, backupType, jobName);
	}

	@Override
	protected void submitBackupJob(Node node) throws EdgeServiceFault {
		VirtualMachine vm = new VirtualMachine();
		if (node.isVMwareMachine()){
			List<EdgeEsxVmInfo> vmList = new LinkedList<>();
			esxDao.as_edge_vsphere_vm_detail_getVMByVmHostId(node.getId(), vmList);
			if(vmList.isEmpty()){
				logger.debug("ignore this node since it doesn't have ESX information");
				return;
			}
			vm.setVmName(vmList.get(0).getVmName());
			vm.setVmInstanceUUID(vmList.get(0).getVmInstanceUuid());
			vm.setVmUUID(vmList.get(0).getVmUuid());
		}else{
			List<EdgeHyperVHostMapInfo> hypervHostMap = new LinkedList<EdgeHyperVHostMapInfo>();
			hyperVDao.as_edge_hyperv_host_map_getById(node.getId(), hypervHostMap);
		
			vm.setVmName(hypervHostMap.get(0).getVmName());
			vm.setVmInstanceUUID(hypervHostMap.get(0).getVmInstanceUuid());
			vm.setVmUUID(hypervHostMap.get(0).getVmUuid());
		}
		
		ConnectionContext context = new VMConnectionContextProvider(node.getId()).create();
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
			int regStatus = connection.getService().QueryEdgeMgrStatus(CommonUtil.retrieveCurrentAppUUID(), ApplicationType.vShpereManager, EdgeCommonUtil.getLocalFqdnName());
			if (1 != regStatus) {
				generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsProxyManagedByOthers"));
				return;
			}
			
			// according the version of d2d to invoke different method. 
			double d2dVersion = 0;
			double d2dUpdateVersionNumber = 0;
			
			VersionInfo versionInfo = connection.getService().getVersionInfo();
			
			try {
				d2dVersion = Double.parseDouble(versionInfo.getMajorVersion() + "." + versionInfo.getMinorVersion());
			} catch(Exception e) {
				d2dVersion = 0;
			}
			
			try{
				d2dUpdateVersionNumber = Double.parseDouble(versionInfo.getUpdateNumber());
			} catch(Exception e) {
				d2dUpdateVersionNumber = 0;
			}
			
			logger.debug("D2D version is: "+d2dVersion + "And D2D update number is : "+d2dUpdateVersionNumber);
			
			// if the version after update7 , invoke the new method
			if((d2dVersion > REQUIRE_D2D_VERSION_FOR_BACKUP_SET) ||
					((d2dVersion == REQUIRE_D2D_VERSION_FOR_BACKUP_SET) && (d2dUpdateVersionNumber >= REQUIRE_D2D_UPDATE_NUMBER_FOR_BACKUP_SET))){
				connection.getService().backupVMWithFlag(backupType, jobName, vm, false);
			} else {
				connection.getService().backupVM(backupType, jobName, vm);
			}
			
			generateLog(Severity.Information, node, EdgeCMWebServiceMessages.getResource("submitVMBackupJobsSuccess"));
		} catch (WebServiceException e) {
			generateLog(Severity.Error, node, EdgeCMWebServiceMessages.getResource("submitD2DBackupJobsCantConnectProxy", context.getHost()));
		}
	}

	@Override
	protected boolean isAllow2SubmitBackupJob(Node node) {
		return true;
	}
	
	public static class BackupNowVMJobMultipleNodes extends BackupNowVMJob{
		
		protected int[] nodeIDs; 
		
		public BackupNowVMJobMultipleNodes(int[] nodeIDs, int backupType, String jobName){
			this.nodeIDs = nodeIDs;
			this.backupType = backupType;
			this.jobName = jobName;
		}

		@Override
		public List<Node> getNodeList() {
			return retrieveTargetNodeList(nodeIDs); 
		}
	}
}
