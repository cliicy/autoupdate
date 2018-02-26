package com.ca.arcserve.edge.app.base.webservice.node.exportimport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHyperV;
import com.ca.arcserve.edge.app.base.appdaos.EdgeSiteInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeAdDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeGatewayDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHypervisorDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.resources.messages.MessageReader;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.CsvParser;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.AdEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.EsxEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.HypervEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.NodeExportEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.exportimport.SiteEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;
import com.ca.arcserve.edge.app.base.webservice.gateway.EdgeGatewayBean;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.taskmonitor.TaskMonitor;

public class NodeImporter implements Runnable {
	private String importFile;
	private String unzipFilePath;
	private final int BUFFER = 2048;
	private static SimpleDateFormat formatter = new SimpleDateFormat(
			MessageReader.getDateFormat("timeDateFormat"));

	private NodeServiceImpl nodeService;

	private EdgeGatewayBean gateWayService = new EdgeGatewayBean();
	private IEdgeGatewayDao gatewayDao = DaoFactory
			.getDao(IEdgeGatewayDao.class);
	private IEdgeAdDao adDao = DaoFactory.getDao(IEdgeAdDao.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao hypervDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private IEdgeHostMgrDao hostMgrDao = DaoFactory
			.getDao(IEdgeHostMgrDao.class);
	IEdgeConnectInfoDao connectionInfoDao = DaoFactory
			.getDao(IEdgeConnectInfoDao.class);
	private IEdgeHypervisorDao hypervisorDao = DaoFactory
			.getDao(IEdgeHypervisorDao.class);

	private static final Logger logger = Logger.getLogger(NodeImporter.class);

	private Map<Integer, Integer> gatewayIdMap = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> adMap = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> esxMap = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> hypervMap = new HashMap<Integer, Integer>();

	private int taskId;
	private TaskDetail<String> detail;

	public NodeImporter(String importFile, NodeServiceImpl nodeService) {
		this.importFile = importFile;
		this.nodeService = nodeService;
		this.unzipFilePath = importFile.substring(0,
				importFile.lastIndexOf("\\"));
		detail = new TaskDetail<String>();
		detail.setCode(0);
		String message = EdgeCMWebServiceMessages.getMessage("importing");
		detail.setMessage(message);
		detail.setRawData("");
		taskId = TaskMonitor.registerNewTask(Module.ImportNodesFromFile,
				"importNodeFromFile", detail);
	}

	@Override
	public void run() {
		TaskMonitor.setTaskStarted(taskId);
		File unzipFileDir = null;
		try {
			unzipFileDir = new File(unzipFilePath);
			detail.setCode(10);
			String message = EdgeCMWebServiceMessages.getMessage("importing");
			detail.setMessage(message);
			TaskMonitor.updateTaskStatus(taskId, TaskStatus.InProcess, detail);
			if (!unZipFile()) {
				return;
			}
			if (unzipFileDir.exists()) {
				importGateWays();
				
				importADs();
				
				importEsxs();
				
				importHypervs();
				
				ImportNodes();
				
				message = EdgeCMWebServiceMessages.getMessage("importNodes_End");
				detail.setMessage(message);
				detail.setCode(100);
				TaskMonitor.updateTaskStatus(taskId, TaskStatus.OK, detail);
				
			} else {
				message = EdgeCMWebServiceMessages.getMessage("noNodesImport");
				detail.setMessage(message);
				detail.setCode(100);
				TaskMonitor.updateTaskStatus(taskId, TaskStatus.InProcess, detail);
				logger.error("[NodeImporter] Have no file to import.");
				return;
			}
		} catch (Exception e) {
			String message = EdgeCMWebServiceMessages.getMessage("importnodefailed");
			detail.setMessage(message);
			detail.setCode(100);
			TaskMonitor.updateTaskStatus(taskId, TaskStatus.Error, detail);
			logger.error("[NodeImporter] import node failed.", e);
		} finally {
			 if( unzipFileDir !=null && unzipFileDir.exists() ){
				 CommonUtil.recursiveDelFolder( unzipFileDir.getAbsolutePath() );
			 }
		}
	}

	private void importGateWays() {
		BufferedReader br = null;
		try {
			String message = EdgeCMWebServiceMessages.getMessage("importing");
			detail.setMessage(message);
			detail.setCode(20);
			TaskMonitor.updateTaskStatus(taskId, TaskStatus.InProcess,
					detail);
			
			File gatewayFile = new File(unzipFilePath+"\\"
					+ NodeExporter.exportGatewayName+".csv");
			if (!gatewayFile.exists()) {
				logger.error("[NodeImporter] importGateWays() Have no gateway file to import."+gatewayFile.getAbsolutePath());
				return;
			}
			br = new BufferedReader(new FileReader(gatewayFile));
			String line = null;
			line = br.readLine();
			if(line == null){
				logger.error("[NodeImporter] gateway csv have no content. "+gatewayFile.getAbsolutePath());
				return;
			}
			while ((line = br.readLine()) != null) {
				CsvParser parser = new CsvParser(line);
				List<String> csvRow = parser.getNextRow();
				if(csvRow.size() < 1){
					logger.error("[NodeImporter] gateway csv have no content. "+gatewayFile.getAbsolutePath());
					break;
				}
				if (csvRow.contains("\"")) {
					logger.error("[NodeImporter] importGateWays() failed, invalid csv content.");
					failedTheTask();
				}
				SiteEntity siteEntity = parseGatewayCsvRow(csvRow);
				if(siteEntity == null){
					failedTheTask();
				}
				if (siteEntity.getIsLocal() == 1) {
					GatewayEntity gatewayEntity = gateWayService
							.getLocalGateway();
					if (!gatewayIdMap.containsKey(siteEntity.getGatewayId())) {
						gatewayIdMap.put(siteEntity.getGatewayId(),
								gatewayEntity.getId().getRecordId());
					}
				} else {
					String siteName = siteEntity.getSiteName();
					List<EdgeSiteInfo> siteInfoList = new ArrayList<>();
					gatewayDao.querySites(siteName, siteInfoList);
					if (siteInfoList.size() > 0) {
						EdgeSiteInfo siteInfo = siteInfoList.get(0);
						if (!gatewayIdMap
								.containsKey(siteEntity.getGatewayId())) {
							gatewayIdMap.put(siteEntity.getGatewayId(),
									siteInfo.getGatewayId());
						}
					} else {
						// insert site
						UUID gatewayUuid = UUID.randomUUID();
						int[] idArray = new int[1];
						int isLocal = 0; // false
						this.gatewayDao.createSite(siteEntity.getSiteName(),
								siteEntity.getSiteDescription(),
								gatewayUuid.toString(), isLocal,
								siteEntity.getAddress(), siteEntity.getEmail(),
								siteEntity.getHeartBeatInterval(), idArray);
						siteInfoList.clear();
						gatewayDao.querySites(siteName, siteInfoList);
						if (siteInfoList.size() > 0) {
							EdgeSiteInfo siteInfo = siteInfoList.get(0);
							if (!gatewayIdMap.containsKey(siteEntity
									.getGatewayId())) {
								gatewayIdMap.put(siteEntity.getGatewayId(),
										siteInfo.getGatewayId());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("[NodeImporter] importGateWays() import gateway failed.", e);
			failedTheTask();
		}finally{
			try {
				if(br != null){
					br.close();
				}
			} catch (Exception e2) {}
		}
	}

	private void importADs() {
		String message = EdgeCMWebServiceMessages.getMessage("importing");
		detail.setMessage(message);
		detail.setCode(30);
		TaskMonitor.updateTaskStatus(taskId, TaskStatus.InProcess,
				detail);
		BufferedReader br = null;
		try {
			File adFile = new File(unzipFilePath +"\\"+ NodeExporter.exportAdName+".csv");
			if (!adFile.exists()) {
				logger.info("[NodeImporter] Have no ad file to import.");
				return;
			}
			br = new BufferedReader(new FileReader(adFile));
			String line = br.readLine();
			if(line == null){
				logger.info("[NodeImporter]: importADs() ad csv file have no content. "+adFile.getAbsolutePath());
				return;
			}
			while ((line = br.readLine()) != null) {
				CsvParser parser = new CsvParser(line);
				List<String> csvRow = parser.getNextRow();
				if (csvRow.size() < 1) {
					logger.info("[NodeImporter]: importADs() ad csv file have no content. "+adFile.getAbsolutePath());
					break;
				}
				if (csvRow.contains("\"")) {
					logger.error("[NodeImporter] importGateWays() failed, invalid csv content.");
					failedTheTask();
				}
				AdEntity adEntity = parseAdCsvRow(csvRow);
				if(adEntity == null){
					failedTheTask();
				}
				int[] output = new int[1];
				adDao.as_edge_ad_isExist(adEntity.getUsername(),
						adEntity.getFilter(), output);
				if (output[0] > 0) {
					if (!adMap.containsKey(adEntity.getId())) {
						adMap.put(adEntity.getId(), output[0]);
					}
				} else {
					output[0] = 0;
					adDao.as_edge_ad_update(0, adEntity.getUsername(),
							adEntity.getPassword(), adEntity.getFilter(),
							adEntity.getDomainControler(), output);
					if (output[0] > 0) {
						gatewayDao.as_edge_gateway_entity_map_addOrUpdate(
								gatewayIdMap.get(adEntity.getGatewayId()),
								output[0], EntityType.AD);
						if (!adMap.containsKey(adEntity.getId())) {
							adMap.put(adEntity.getId(), output[0]);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("[NodeImporter] importADs() import ad failed.", e);
			failedTheTask();
		}finally{
			try {
				if(br != null && br.ready()){
					br.close();
				}
			} catch (Exception e2) {}
		}
	}

	private void importEsxs() {
		String message = EdgeCMWebServiceMessages.getMessage("importing");
		detail.setMessage(message);
		detail.setCode(40);
		TaskMonitor.updateTaskStatus(taskId, TaskStatus.InProcess,
				detail);
		
		BufferedReader br = null;
		try {
			File esxFile = new File(unzipFilePath +"\\"+ NodeExporter.exportESsxName+".csv");
			if (!esxFile.exists()) {
				logger.info("[NodeImporter] Have no esx file to import.");
				return;
			}
			br = new BufferedReader(new FileReader(esxFile));
			String line = br.readLine();
			if(line == null){
				logger.info("[NodeImporter] importEsxs() esx file have no content."+esxFile.getAbsolutePath());
				return;
			}
			while ((line = br.readLine()) != null) {
				CsvParser parser = new CsvParser(line);
				List<String> csvRow = parser.getNextRow();
				if(csvRow.size() < 1){
					logger.info("[NodeImporter] importEsxs() esx file have no content."+esxFile.getAbsolutePath());
					break;
				}
				if (csvRow.contains("\"")) {
					logger.error("[NodeImporter] importEsxs() failed, invalid csv content.");
					// throw
				}
				EsxEntity esxEntity = parseEsxCsvRow(csvRow);
				if(esxEntity == null){
					failedTheTask();
				}
				List<EdgeEsx> esxList = new ArrayList<EdgeEsx>();
				esxDao.as_edge_esx_getByName(esxEntity.getGatewayId(), esxEntity.getHostName(), esxList);
				if (esxList.size() > 0) {
					EdgeEsx esx = esxList.get(0);
					if (!esxMap.containsKey(esxEntity.getId())) {
						esxMap.put(esxEntity.getId(), esx.getId());
					}
				} else {
					// insert
					int[] output = new int[1];
					esxDao.as_edge_esx_update(0, esxEntity.getHostName(),
							esxEntity.getUserName(), esxEntity.getPassword(),
							esxEntity.getProtocol(), esxEntity.getPort(),
							esxEntity.getServerType(),esxEntity.getVisible(),
							esxEntity.getDescription(), esxEntity.getUuid(),
							output);
					if (output[0] > 0) {
						gatewayDao.as_edge_gateway_entity_map_addOrUpdate(
								gatewayIdMap.get(esxEntity.getGatewayId()),
								output[0], EntityType.VSphereEntity);
						if (!esxMap.containsKey(esxEntity.getId())) {
							esxMap.put(esxEntity.getId(), output[0]);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("[NodeImporter] importEsxs() import esx failed.", e);
			failedTheTask();
		} finally{
			try {
				if(br != null){
					br.close();
				}
			} catch (Exception e2) {}
		}
	}

	private void importHypervs() {
		String message = EdgeCMWebServiceMessages.getMessage("importing");
		detail.setMessage(message);
		detail.setCode(50);
		TaskMonitor.updateTaskStatus(taskId, TaskStatus.InProcess,
				detail);
		BufferedReader br = null;
		try {
			File hypervFile = new File(unzipFilePath +"\\"
					+ NodeExporter.exportHypervName + ".csv");
			if (!hypervFile.exists()) {
				logger.info("[NodeImporter] Have no hyperv file to import.");
				return;
			}
			br = new BufferedReader(new FileReader(hypervFile));
			String line = br.readLine();
			if(line == null){
				logger.error("[NodeImporter] hyperv file have no content."+hypervFile.getAbsolutePath());
				return;
			}
			while ((line = br.readLine()) != null) {
				CsvParser parser = new CsvParser(line);
				List<String> csvRow = parser.getNextRow();
				if(csvRow.size() < 1){
					logger.error("[NodeImporter] hyperv file have no content."+hypervFile.getAbsolutePath());
					break;
				}
				if (csvRow.contains("\"")) {
					logger.error("[NodeImporter] importHypervs() failed, invalid csv content.");
					failedTheTask();
				}
				HypervEntity hypervEntity = parseHypervCsvRow(csvRow);
				if(hypervEntity == null){
					failedTheTask();
				}
				List<EdgeHyperV> hypervList = new ArrayList<EdgeHyperV>();
				hypervDao.as_edge_hyperv_getByName(hypervEntity.getGatewayId(),
						hypervEntity.getHostName(), hypervList);
				if (hypervList.size() > 0) {
					if (!hypervMap.containsKey(hypervEntity.getId())) {
						hypervMap.put(hypervEntity.getId(), hypervList.get(0)
								.getId());
					}
				} else {
					// insert
					int[] output = new int[1];
					hypervDao.as_edge_hyperv_update(0,
							hypervEntity.getHostName(),
							hypervEntity.getUserName(),
							hypervEntity.getPassword(),
							hypervEntity.getProtocol(), hypervEntity.getPort(),
							hypervEntity.getVisible(), hypervEntity.getType(),
							output);
					if (output[0] > 0) {
						gatewayDao.as_edge_gateway_entity_map_addOrUpdate(
								gatewayIdMap.get(hypervEntity.getGatewayId()),
								output[0], EntityType.HyperVServer);
						if (!hypervMap.containsKey(hypervEntity.getId())) {
							hypervMap.put(hypervEntity.getId(),
									output[0]);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("[NodeImporter] importHypervs() import hyperv failed.", e);
			failedTheTask();
		}finally{
			try {
				if(br != null){
					br.close();
				}
			} catch (Exception e2) {}
		}
	}

	private void ImportNodes() {
		BufferedReader br = null;
		try {
			String message = EdgeCMWebServiceMessages.getMessage("importing");
			detail.setMessage(message);
			detail.setCode(80);
			TaskMonitor.updateTaskStatus(taskId, TaskStatus.InProcess,
					detail);
			File nodeFile = new File(unzipFilePath + "\\"
					+ NodeExporter.exportNodeName + ".csv");
			if (!nodeFile.exists()) {
				logger.error("[NodeImporter] Have no node file to import.");
				return;
			}
			br = new BufferedReader(new FileReader(nodeFile));
			String line = br.readLine();
			if(line == null){
				logger.error("[NodeImporter] ImportNodes() Node file have no content. "+nodeFile.getAbsolutePath());
				return;
			}
			while ((line = br.readLine()) != null) {
				CsvParser parser = new CsvParser(line);
				List<String> csvRow = parser.getNextRow();
				if(csvRow.size() < 1){
					logger.error("[NodeImporter] ImportNodes() Node file have no content. "+nodeFile.getAbsolutePath());
					break;
				}
				if (csvRow.contains("\"")) {
					logger.error("[NodeImporter] ImportNodes() failed, invalid csv content.");
					failedTheTask();
				}
				NodeExportEntity nodeEntity = parseNodeCsvRow(csvRow);
				if(nodeEntity == null){
					failedTheTask();
				}
				if (HostTypeUtil.isPhysicsMachine(nodeEntity.getHostType())) {
					List<EdgeHost> hosts = new ArrayList<EdgeHost>();
					hostMgrDao.as_edge_host_pyhsical_list_byHostname(
							nodeEntity.getHostName(), hosts);
					if (hosts.isEmpty()) {
						insertNodeToDB(nodeEntity);
					}
				} else if (HostTypeUtil.isVMWareVirtualMachine(nodeEntity
						.getHostType())) {
					int[] output = new int[1];
					hostMgrDao.as_edge_host_vm_by_instanceUUID(
							nodeEntity.getEsxvmInstanceUuid(), output);
					if (output[0] < 1) {
						insertNodeToDB(nodeEntity);
					}
				} else if (HostTypeUtil.isHyperVVirtualMachine(nodeEntity
						.getHostType())) {
					int[] output = new int[1];
					hostMgrDao.as_edge_host_vm_by_instanceUUID(
							nodeEntity.getHypervVmInstanceUuid(), output);
					if (output[0] < 1) {
						insertNodeToDB(nodeEntity);
					}
				} else {// linux server / linux node / remote node
					List<EdgeHost> hosts = new ArrayList<EdgeHost>();
					hostMgrDao.as_edge_host_pyhsical_list_byHostname(
							nodeEntity.getHostName(), hosts);
					if (hosts.isEmpty()) {
						insertNodeToDB(nodeEntity);
					}
				}
			}
		} catch (Exception e) {
			logger.error("[NodeImporter] ImportNodes() import nodes failed.", e);
			failedTheTask();
		} finally{
			try {
				if(br != null){
					br.close();
				}
			} catch (Exception e2) {}
		}
	}

	private void insertNodeToDB(NodeExportEntity nodeEntity) {
		// insert
		int[] output = new int[1];
		hostMgrDao.as_edge_host_update(0, nodeEntity.getLastUpdated(),
				nodeEntity.getHostName(), nodeEntity.getNodeDescription(),
				nodeEntity.getIpAddress(), nodeEntity.getOsDescription(),
				nodeEntity.getOsType(), nodeEntity.getVisible(),
				nodeEntity.getAppStatus(), nodeEntity.getServerPrincipalName(),
				nodeEntity.getHostType(), nodeEntity.getProtectionTypeBitmap(),
				nodeEntity.getFqdnNames(),
				output);
		connectionInfoDao.as_edge_connect_info_update(output[0],
				nodeEntity.getUserName(), nodeEntity.getPassword(),
				nodeEntity.getUuid(), nodeEntity.getProtocol(),
				nodeEntity.getPort(), nodeEntity.getType(),
				nodeEntity.getMajorVersion(), nodeEntity.getMinorVersion(),
				nodeEntity.getUpdateNumber(), nodeEntity.getBuildNumber(),
				NodeManagedStatus.Unmanaged.ordinal());
		connectionInfoDao.as_edge_arcserve_connect_info_update(output[0],
				nodeEntity.getCaUser(), nodeEntity.getCaPassword(),
				nodeEntity.getAuthMode(), nodeEntity.getArcserveProtocol(),
				nodeEntity.getArcservePort(), nodeEntity.getArcserveType(),
				nodeEntity.getArcserveVersion(),
				NodeManagedStatus.Unmanaged.ordinal());
		gatewayDao.as_edge_gateway_entity_map_addOrUpdate(
				gatewayIdMap.get(nodeEntity.getGatewayId()), output[0],
				EntityType.Node);

		if (nodeEntity.getAdId() > 0) {
			adDao.as_edge_ad_host_map_add(adMap.get(nodeEntity.getAdId()),
					output[0], nodeEntity.getAdStatus());
		}

		if (nodeEntity.getEsxId() > 0) {// if physical , specify hypervisor
			nodeService.saveVMToDB(nodeEntity.getGatewayId(), esxMap.get(nodeEntity.getEsxId()),
					output[0], nodeEntity.getEsxvmInstanceUuid(),
					nodeEntity.getHostName(), nodeEntity.getEsxVmName(),
					nodeEntity.getEsxVmUuid(), nodeEntity.getEsxHost(),
					nodeEntity.getEsxVmXPath(), nodeEntity.getEsxVmGuestOS(),
					nodeEntity.getUserName(), nodeEntity.getPassword(),
					nodeEntity.getProtocol(), nodeEntity.getPort(), 0,
					nodeEntity.getNodeDescription(),true);
		}

		if (nodeEntity.getHypervId() > 0) {// if physical, specify hypervisor
			hypervDao
					.as_edge_hyperv_host_map_add(
							hypervMap.get(nodeEntity.getHypervId()), output[0],
							IEdgeHyperVDao.HYPERV_HOST_STATUS_VISIBLE,
							nodeEntity.getHypervVmName(),
							nodeEntity.getHypervVmUuid(),
							nodeEntity.getHypervVmInstanceUuid(),
							nodeEntity.getHypervHost(),
							nodeEntity.getHypervVmGuestOS());
		}

		if (!StringUtil.isEmptyOrNull(nodeEntity.getOtherHypervisorHostName())) { // other
																					// type
																					// hypervisor
			hypervisorDao.as_edge_hypervisor_vm_update(output[0],
					nodeEntity.getOtherHypervisorHostName(),
					nodeEntity.getOtherHypervisorSocketCount());
		}
	}

	private boolean unZipFile() {
		try {
			File f = new File(importFile);
			if (f.length() > 0) {
				BufferedOutputStream dest = null;
				FileInputStream fis = new FileInputStream(f);
				ZipInputStream zis = new ZipInputStream(
						new BufferedInputStream(fis));
				ZipEntry zipEntry;
				while ((zipEntry = zis.getNextEntry()) != null) {
					int count;
					byte data[] = new byte[BUFFER];
					// write the files to the disk
					FileOutputStream fos = new FileOutputStream(unzipFilePath+"\\"+zipEntry.getName());
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = zis.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
					fos.close();
				}
				fis.close();
				zis.close();
			}
			f.delete();
			return true;
		} catch (Exception e) {
			logger.error("[NodeImporter] unzip file: " + importFile
					+ " failed.", e);
			return false;
		}
	}

	private SiteEntity parseGatewayCsvRow(List<String> gateway) {
		try {
			if (gateway.size() < 1) {
				logger.error("[NodeImporter] parseGatewayCsvRow() failed. gateway row size is not 24");
				return null;
			}
			SiteEntity siteEntity = new SiteEntity();
			siteEntity.setGatewayId(parseInt(gateway.get(0)));
			siteEntity.setGatewayName(gateway.get(1));
			siteEntity.setGatewayUuid(gateway.get(2));
			siteEntity.setGatewayHostUuid(gateway.get(3));
			siteEntity.setGatewayHostName(gateway.get(4));
			siteEntity.setIsLocal(Integer.parseInt(gateway.get(5)));
			if (!StringUtil.isEmptyOrNull(gateway.get(6))) {
				Date lastContactTime = formatter.parse(gateway.get(6));
				siteEntity.setLastContactTime(lastContactTime);
			}
			siteEntity.setHeartBeatInterval(Integer.parseInt(gateway.get(7)));
			if (!StringUtil.isEmptyOrNull(gateway.get(8))) {
				Date gatewayCreateTime = formatter.parse(gateway.get(8));
				siteEntity.setGatewayCreateTime(gatewayCreateTime);
			}
			if (!StringUtil.isEmptyOrNull(gateway.get(9))) {
				Date gatewayUpdateTime = formatter.parse(gateway.get(9));
				siteEntity.setUpdateTime(gatewayUpdateTime);
			}
			siteEntity.setConsoleHostName(gateway.get(10));
			siteEntity.setConsoleProtocol(Integer.parseInt(gateway.get(11)));
			siteEntity.setConsolePort(parseInt(gateway.get(12)));
			siteEntity.setGatewayProtocol(parseInt(gateway.get(13)));
			siteEntity.setGatewayPort(parseInt(gateway.get(14)));
			siteEntity.setGatewayUserName(gateway.get(15));
			siteEntity.setGatewayPassword(BackupService.getInstance()
					.getNativeFacade().decrypt(gateway.get(16)));
			siteEntity.setRegistrationText(gateway.get(17));
			siteEntity.setSiteName(gateway.get(18));
			siteEntity.setSiteDescription(gateway.get(19));
			if (!StringUtil.isEmptyOrNull(gateway.get(20))) {
				Date siteCreatTime = formatter.parse(gateway.get(20));
				siteEntity.setSiteCreateTime(siteCreatTime);
			}
			if (!StringUtil.isEmptyOrNull(gateway.get(21))) {
				Date siteUpdateTime = formatter.parse(gateway.get(21));
				siteEntity.setSiteUpdateTime(siteUpdateTime);
			}
			siteEntity.setAddress(gateway.get(22));
			siteEntity.setEmail(gateway.get(23));
			return siteEntity;
		} catch (Exception e) {
			logger.error("[NodeImporter] parseGatewayCsvRow failed", e);
			return null;
		}
	}

	private int parseInt(String value) {
		if (StringUtil.isEmptyOrNull(value)) {
			return 0;
		} else {
			return Integer.parseInt(value);
		}
	}

	private long parseLong(String value) {
		if (StringUtil.isEmptyOrNull(value)) {
			return 0;
		} else {
			return Long.parseLong(value);
		}
	}

	private AdEntity parseAdCsvRow(List<String> csvRow) {
		try {
			if (csvRow.size() < 1) {
				logger.error("[NodeImporter] parseAdCsvRow() failed. ad csv row size is empty");
				return null;
			}
			AdEntity adEntity = new AdEntity();
			adEntity.setId(parseInt(csvRow.get(0)));
			adEntity.setUsername(csvRow.get(1));
			adEntity.setPassword(BackupService.getInstance().getNativeFacade()
					.decrypt(csvRow.get(2)));
			adEntity.setFilter(csvRow.get(3));
			adEntity.setDomainControler(csvRow.get(4));
			adEntity.setGatewayId(parseInt(csvRow.get(5)));
			return adEntity;
		} catch (Exception e) {
			logger.error("[NodeImporter] parseAdCsvRow failed", e);
			return null;
		}
	}

	private EsxEntity parseEsxCsvRow(List<String> csvRow) {
		try {
			if (csvRow.size() < 1) {
				logger.error("[NodeImporter] parseEsxCsvRow() failed. Esx row size is wrong");
				return null;
			}
			EsxEntity esxEntity = new EsxEntity();
			esxEntity.setId(parseInt(csvRow.get(0)));
			esxEntity.setHostName(csvRow.get(1));
			esxEntity.setUserName(csvRow.get(2));
			esxEntity.setPassword(BackupService.getInstance().getNativeFacade()
					.decrypt(csvRow.get(3)));
			esxEntity.setProtocol(parseInt(csvRow.get(4)));
			esxEntity.setPort(parseInt(csvRow.get(5)));
			esxEntity.setServerType(parseInt(csvRow.get(6)));
			esxEntity.setVisible(parseInt(csvRow.get(7)));
			esxEntity.setEssential(parseInt(csvRow.get(8)));
			esxEntity.setSocketCount(parseInt(csvRow.get(9)));
			esxEntity.setDescription(csvRow.get(10));
			esxEntity.setUuid(csvRow.get(11));
			esxEntity.setGatewayId(parseInt(csvRow.get(12)));
			return esxEntity;
		} catch (Exception e) {
			logger.error("[NodeImporter] parseEsxCsvRow failed", e);
			return null;
		}
	}

	private HypervEntity parseHypervCsvRow(List<String> csvRow) {
		try {
			if (csvRow.size() < 1) {
				logger.error("[NodeImporter] parseHypervCsvRow() failed. hyperv row size wrong");
				return null;
			}
			HypervEntity hypervEntity = new HypervEntity();
			hypervEntity.setId(parseInt(csvRow.get(0)));
			hypervEntity.setHostName(csvRow.get(1));
			hypervEntity.setUserName(csvRow.get(2));
			hypervEntity.setPassword(BackupService.getInstance()
					.getNativeFacade().decrypt(csvRow.get(3)));
			hypervEntity.setProtocol(parseInt(csvRow.get(4)));
			hypervEntity.setPort(parseInt(csvRow.get(5)));
			hypervEntity.setVisible(parseInt(csvRow.get(6)));
			hypervEntity.setSocketCount(parseInt(csvRow.get(7)));
			hypervEntity.setType(parseInt(csvRow.get(8)));
			hypervEntity.setGatewayId(parseInt(csvRow.get(9)));
			return hypervEntity;
		} catch (Exception e) {
			logger.error("[NodeImporter] parseHypervCsvRow failed", e);
			return null;
		}
	}

	private NodeExportEntity parseNodeCsvRow(List<String> csvRow) {
		try {
			if (csvRow.size() < 1) {
				logger.error("[NodeImporter] parseNodeCsvRow() failed. node row size is wrong");
				return null;
			}
			int adId = parseInt(csvRow.get(47));
			int esxId = parseInt(csvRow.get(49));
			int hypervId = parseInt(csvRow.get(50));
			
			NodeExportEntity nodeEntity = new NodeExportEntity();
			
			nodeEntity.setNodeId(parseInt(csvRow.get(0)));
			nodeEntity.setNodeDescription(csvRow.get(1));
			if (!StringUtil.isEmptyOrNull(csvRow.get(2))) {
				Date lastUpdated = formatter.parse(csvRow.get(2));
				nodeEntity.setLastUpdated(lastUpdated);
			}
			nodeEntity.setHostName(csvRow.get(3));
			nodeEntity.setDomainName(csvRow.get(4));
			nodeEntity.setIpAddress(csvRow.get(5));
			nodeEntity.setOsDescription(csvRow.get(6));
			nodeEntity.setOsType(csvRow.get(7));
			nodeEntity.setVisible(parseInt(csvRow.get(8)));
			nodeEntity.setAppStatus(parseInt(csvRow.get(9)));
			
			nodeEntity.setServerPrincipalName(csvRow.get(10));
			nodeEntity.setHostType(parseInt(csvRow.get(11)));
			nodeEntity.setTimezone(parseInt(csvRow.get(12)));
			nodeEntity.setJobPhase(parseLong(csvRow.get(13)));
			nodeEntity.setProtectionTypeBitmap(parseInt(csvRow.get(14)));
			nodeEntity.setRawMachineType(parseInt(csvRow.get(15)));
			nodeEntity.setUserName(csvRow.get(16));
			nodeEntity.setPassword(BackupService.getInstance()
					.getNativeFacade().decrypt(csvRow.get(17)));
			nodeEntity.setUuid(csvRow.get(18));
			nodeEntity.setAuthUuid(csvRow.get(19));
			
			nodeEntity.setProtocol(parseInt(csvRow.get(20)));
			nodeEntity.setPort(parseInt(csvRow.get(21)));
			nodeEntity.setType(parseInt(csvRow.get(22)));
			nodeEntity.setMajorVersion(csvRow.get(23));
			nodeEntity.setMinorVersion(csvRow.get(24));
			nodeEntity.setBuildNumber(csvRow.get(25));
			nodeEntity.setUpdateNumber(csvRow.get(26));
			nodeEntity.setStatus(parseInt(csvRow.get(27)));
			nodeEntity.setManaged(parseInt(csvRow.get(28)));
			nodeEntity.setCaUser(csvRow.get(29));
			
			nodeEntity.setCaPassword(BackupService.getInstance()
					.getNativeFacade().decrypt(csvRow.get(30)));
			nodeEntity.setAuthMode(parseInt(csvRow.get(31)));
			nodeEntity.setArcserveProtocol(parseInt(csvRow.get(32)));
			nodeEntity.setArcservePort(parseInt(csvRow.get(33)));
			nodeEntity.setArcserveType(parseInt(csvRow.get(34)));
			nodeEntity.setArcserveVersion(csvRow.get(35));
			nodeEntity.setArcserveManaged(parseInt(csvRow.get(36)));
			nodeEntity.setArcserveUuid(csvRow.get(37));
			if (esxId > 0) {
				nodeEntity.setEsxVmStatus(parseInt(csvRow.get(38)));
				nodeEntity.setEsxVmName(csvRow.get(39));
				
				nodeEntity.setEsxvmInstanceUuid(csvRow.get(40));
				nodeEntity.setEsxVmUuid(csvRow.get(41));
				nodeEntity.setEsxHost(csvRow.get(42));
				nodeEntity.setEsxEssential(parseInt(csvRow.get(43)));
				nodeEntity.setEsxSocketCount(parseInt(csvRow.get(44)));
				nodeEntity.setEsxVmXPath(csvRow.get(45));
				nodeEntity.setEsxVmGuestOS(csvRow.get(46));
			} else if (hypervId > 0) {
				nodeEntity.setHypervVmStatus(parseInt(csvRow.get(38)));
				nodeEntity.setHypervVmName(csvRow.get(39));
				nodeEntity.setHypervVmInstanceUuid(csvRow.get(40));
				nodeEntity.setHypervVmUuid(csvRow.get(41));
				nodeEntity.setHypervHost(csvRow.get(42));
				nodeEntity.setHypervSocketCount(parseInt(csvRow.get(44)));
				nodeEntity.setHypervVmGuestOS(csvRow.get(46));
			}
			if (adId > 0) {
				nodeEntity.setAdId(parseInt(csvRow.get(47)));
				nodeEntity.setAdStatus(parseInt(csvRow.get(48)));
			}
			nodeEntity.setEsxId(parseInt(csvRow.get(49)));
			
			nodeEntity.setHypervId(parseInt(csvRow.get(50)));
			if (!StringUtil.isEmptyOrNull(csvRow.get(51))) {
				nodeEntity.setOtherHypervisorHostName(csvRow.get(51));
				nodeEntity.setOtherHypervisorSocketCount(parseInt(csvRow
						.get(52)));
			}
			nodeEntity.setGatewayId(parseInt(csvRow.get(53)));
			nodeEntity.setFqdnNames(csvRow.get(54));
			
			return nodeEntity;
		} catch (Exception e) {
			logger.error("[NodeImporter] parseNodeCsvRow failed", e);
			return null;
		}
	}

	public int getTaskId() {
		return taskId;
	}
	
	private void failedTheTask(){
		String message = EdgeCMWebServiceMessages.getMessage("importnodefailed");
		detail.setMessage(message);
		detail.setCode(100);
		TaskMonitor.updateTaskStatus(taskId, TaskStatus.Error, detail);
	}
}
