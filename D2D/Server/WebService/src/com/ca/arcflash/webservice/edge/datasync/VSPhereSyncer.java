package com.ca.arcflash.webservice.edge.datasync;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.webservice.data.edge.datasync.vsphere.VM;
import com.ca.arcflash.webservice.data.edge.datasync.vsphere.VMInfoLst;
import com.ca.arcflash.webservice.data.vsphere.TempVMHostInfo;
import com.ca.arcflash.webservice.data.vsphere.TempVMHostList;
import com.ca.arcflash.webservice.data.vsphere.VMItem;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dstatus.D2DStatusServiceImpl;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class VSPhereSyncer extends BaseDataSyncer {
	private static final Logger logger = Logger.getLogger(ActiveLogSyncer.class);

	public VSPhereSyncer() {
		FullSyncFinishMarkName = "vsPhereFullSyncFinished";
	}

	public int transferXML2Edge() throws EdgeServiceFault {
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		String CacheFileName = nativeFacade.GetCachedVmInfo4Trans();
		logger.debug("D2DSync(VmInfo) - nativeFacade.GetCachedVmInfo4Trans() = "+CacheFileName);

		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String wsdl = edgeRegInfo.GetEdgeWSDL();
		if(wsdl == null)
		{
			logger.error("D2DSync(VmInfo) - doesn't managed by an Edge server yet!!");
			writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
			return 1;
		}

		String edgeUUID = edgeRegInfo.GetEdgeUUID();
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();
		
		BufferedReader br;
		String xmlContent = "";

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(CacheFileName)));

			String data = null;
			while((data = br.readLine())!=null)
			{
				xmlContent += data;
			}

			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_VM_DATA_FAILURE);
			return -1;
		}

		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(wsdl,IEdgeCM4D2D.class);
		if(proxy == null)
		{
			logger.error("D2DSync(VmInfo) - Failed to get proxy handle!!\n");
			return 1;
		}
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			proxy.validateUserByUUID(edgeUUID);
		}catch(EdgeServiceFault e) {
			logger.error("D2DSync(VmInfo) - Failed to establish connection to Edge Server(login failed)\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE, edgeHostName);
			return 1;
		}
		
		int result = proxy.D2DSyncVMInfo(getEdgeTaskId(), xmlContent, UUID, false);

		if (result == 0)
		{
			logger.debug("D2DSync(VmInfo) - Sync Backup Data to Edge Server - succeeded!!\n");
			nativeFacade.DeleteVmInfoTransFile();
			//Extend for sync data , sync managed vm by HBBU to CPM report.
			result = transferHBBUManagedVmInfo2Edge(getCorrespondingTempVMHost(xmlContent),false);
			if(result == 0)
				return 0;
		}
		if( result == 1)
			logger.error("D2DSync(VmInfo) - Sync Backup Data to Edge Server - XML parser failed!!\n");
		else if (result == 2 )
			logger.error("D2DSync(VmInfo) - Sync Backup Data to Edge Server - SQL operation failed!!\n");
		else
			logger.error("D2DSync(VmInfo) - Sync Backup Data to Edge Server - Other error!! result = " + result + "\n");
		writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_VM_TO_EDGE_FAILURE, edgeHostName);
		return -1;
	}

	public int transferFullData2Edge() throws EdgeServiceFault {
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		String CacheFileName = nativeFacade.GetAllVmInfo4Trans();

		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String wsdl = edgeRegInfo.GetEdgeWSDL();
		if(wsdl == null)
		{
			logger.error("D2DSync(VmInfo) - doesn't managed by an Edge server yet!!");
			writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
			return 1;
		}

		String edgeUUID = edgeRegInfo.GetEdgeUUID();
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();
		
		BufferedReader br;
		String xmlContent = "";

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(CacheFileName)));

			String data = null;
			while((data = br.readLine())!=null)
			{
				xmlContent += data;
			}

			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_VM_DATA_FAILURE);
			return -1;
		}

		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(wsdl,IEdgeCM4D2D.class);
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			proxy.validateUserByUUID(edgeUUID);
		}catch(EdgeServiceFault e) {
			logger.error("D2DSync(VmInfo) - Failed to establish connection to Edge Server(login failed)\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE,edgeHostName);
			return 1;
		}
		
		int result = proxy.D2DSyncVMInfo(getEdgeTaskId(), xmlContent, UUID, true);

		if (result == 0)
		{
			logger.debug("D2DSync(VmInfo) - Sync Backup Data to Edge Server - succeeded!!\n");
			nativeFacade.DeleteAllVmInfoTransFile();
			return 0;
		}
		else if( result == 1)
			logger.error("D2DSync(VmInfo) - Sync Backup Data to Edge Server - XML parser failed!!\n");
		else if (result == 2 )
			logger.error("D2DSync(VmInfo) - Sync Backup Data to Edge Server - SQL operation failed!!\n");
		else
			logger.error("D2DSync(VmInfo) - Sync Backup Data to Edge Server - Other error!! result = " + result + "\n");
		writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_VM_TO_EDGE_FAILURE,edgeHostName);
		return -1;
	}

	protected boolean process(boolean isFullSync) throws EdgeServiceFault {
		int result = -1;

		if(isFullSync){
			result = transferFullData2Edge();
			//Extend for sync data , sync managed vm by HBBU to CPM report.
			
			transferHBBUManagedVmInfo2Edge(getVMListManagedByHBBU(),true);
			
		}
		else
			result = transferXML2Edge();

		if(result != 0)
			return false;
		else
			return true;
	}
	
	public int transferHBBUManagedVmInfo2Edge(TempVMHostList tempvmhostList , boolean isFullSync){
		
		if(tempvmhostList == null || tempvmhostList.getVmList().size()==0){
			return 0;
		}
		try{
			String xmlContent = CommonUtil.marshal(tempvmhostList);
			D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
			String wsdl = edgeRegInfo.GetEdgeWSDL();
			if(wsdl == null)
			{
				logger.error("D2DSync(VmInfo) - doesn't managed by an Edge server yet!!");
				writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
				return 1;
			}

			String edgeUUID = edgeRegInfo.GetEdgeUUID();
			String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();
			IEdgeD2DService proxy = WebServiceFactory.getEdgeService(wsdl,IEdgeCM4D2D.class);
			String UUID = CommonService.getInstance().getNodeUUID();

			try {
				proxy.validateUserByUUID(edgeUUID);
			}catch(EdgeServiceFault e) {
				logger.error("D2DSync(VmInfo) - Failed to establish connection to Edge Server(login failed)\n");
				writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE,edgeHostName);
				return 1;
			}
			
			int result = proxy.D2DSyncTempVMHost(getEdgeTaskId(), xmlContent, UUID, isFullSync);
			return result;
		} catch (JAXBException ex) {
			logger.error(ex.getMessage(), ex);
			return -1;
		}
		catch (Exception e) {
			logger.error("transferHBBUManagedVmInfo2Edge()", e);
			return -1;
		}
	}
	
	private TempVMHostList getVMListManagedByHBBU(){
		TempVMHostList vmtemphostList = new TempVMHostList();
		VMItem[] vmItems = null;
		List<TempVMHostInfo>  vmHostInfos = new ArrayList<TempVMHostInfo>(); 
		try {
			vmItems = VSphereService.getInstance().getConfiguredVMByGenerateType(0);
			if(vmItems==null)
				return null;
			for(VMItem vmitem : vmItems ){
				TempVMHostInfo vmHost = new TempVMHostInfo();
				vmHost.setVmInstanceUUID(vmitem.getVmInstanceUUID());
				vmHost.setVmHostName(vmitem.getVmHostName());
				vmHost.setVmName(vmitem.getVmName());
				D2DStatusInfo d2DStatusInfo = D2DStatusServiceImpl.getInstance().getVSphereVMStatusInfo(vmitem.getVmInstanceUUID());
				if(d2DStatusInfo != null)
					vmHost.setOverallStatus(d2DStatusInfo.getOverallStatus().ordinal());
				else
					vmHost.setOverallStatus(0);
				vmHostInfos.add(vmHost);
			}
			vmtemphostList.setVmList(vmHostInfos);
		} catch (Exception e) {
			logger.error("D2DSync(VmInfo) - Failed to get the managed vm by HBBU",e);
			return null;
		}
		return vmtemphostList;		
	}
	
	/**
	 * Get the overallstatus for the vm which have done the backup job
	 */
	private TempVMHostList getCorrespondingTempVMHost(String xmlContent ) {
		//get instance uuid of vm from the xmlcontent
		if(xmlContent == null || xmlContent==""){
			return null;
		}
		VMInfoLst vms = null;
		//vms = unmashallVMInfoLst(xmlContent);
		try {
			vms = CommonUtil.unmarshal(xmlContent, VMInfoLst.class);
		} catch (JAXBException e) {
			logger.error("getCorrespondingTempVMHost - Failed to get vm status",e);
			return null;
		}
		TempVMHostList vmtemphostList = null;
		if (vms != null){
			vmtemphostList = new TempVMHostList();
			List<TempVMHostInfo>  vmHostInfos = new ArrayList<TempVMHostInfo>(); 
			TempVMHostInfo vmHost = new TempVMHostInfo();
			VM vm = vms.getVMInfoXml().get(0).getVM();
			String vmInstUUID = vm.getVmInstUUID();
			vmHost.setVmInstanceUUID(vmInstUUID);
			D2DStatusInfo d2DStatusInfo = D2DStatusServiceImpl.getInstance().getVSphereVMStatusInfo(vmInstUUID);
			if(d2DStatusInfo != null)
				vmHost.setOverallStatus(d2DStatusInfo.getOverallStatus().ordinal());
			else
				vmHost.setOverallStatus(0);
			vmHost.setVmHostName(vm.getVmHost());
			vmHost.setVmName(vm.getVmName());
			vmHostInfos.add(vmHost);
			vmtemphostList.setVmList(vmHostInfos);
		}
		return vmtemphostList;
	}
}
