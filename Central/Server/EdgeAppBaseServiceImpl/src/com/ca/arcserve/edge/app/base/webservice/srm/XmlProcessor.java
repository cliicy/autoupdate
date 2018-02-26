package com.ca.arcserve.edge.app.base.webservice.srm;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeSrmDao;
import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.srm.HardwareInformation.CPU;
import com.ca.arcserve.edge.app.base.webservice.srm.HardwareInformation.DiskDrive;
import com.ca.arcserve.edge.app.base.webservice.srm.HardwareInformation.DiskPartition;
import com.ca.arcserve.edge.app.base.webservice.srm.HardwareInformation.FiberCard;
import com.ca.arcserve.edge.app.base.webservice.srm.HardwareInformation.LogicalDisk;
import com.ca.arcserve.edge.app.base.webservice.srm.HardwareInformation.Memory;
import com.ca.arcserve.edge.app.base.webservice.srm.HardwareInformation.NIC;
import com.ca.arcserve.edge.app.base.webservice.srm.HardwareInformation.OS;
import com.ca.arcserve.edge.app.base.webservice.srm.HardwareInformation.Volume;
import com.ca.arcserve.edge.app.base.webservice.srm.ServerPKIInformation.CPUUtil;
import com.ca.arcserve.edge.app.base.webservice.srm.ServerPKIInformation.DiskUtil;
import com.ca.arcserve.edge.app.base.webservice.srm.ServerPKIInformation.MemoryUtil;
import com.ca.arcserve.edge.app.base.webservice.srm.ServerPKIInformation.NetworkUtil;
import com.ca.arcserve.edge.app.base.webservice.srm.SoftwareInformation.AppDataInfo;
import com.ca.arcserve.edge.app.base.webservice.srm.SoftwareInformation.InstalledProgram;

public class XmlProcessor {

	private String m_xmlContent = null;
	private SoftwareInformation m_softInfo = null;
	private HardwareInformation m_hardInfo = null;
	private ServerPKIInformation m_serverPKI = null;
	private String m_rhostname = null;
	private int m_nodeID = -1;

	private static IEdgeSrmDao m_idao = DaoFactory.getDao(IEdgeSrmDao.class);
	private static final Logger xmlProcessorLog = Logger.getLogger(XmlProcessor.class);

	public XmlProcessor( String rhostname, String xmlContent ) {
		if (rhostname != null && xmlContent != null ) {
			m_rhostname = new String(rhostname);
			m_xmlContent = new String(xmlContent);
		}
	}

	public void processHarewareInfo() {
		try {
			m_hardInfo = JAXB.unmarshal(new StringReader(m_xmlContent), HardwareInformation.class);
		} catch (Exception e) {
			xmlProcessorLog.error("[processHarewareInfo] Unmarshal the xml to HardwareInformation failed.", e);
			return;
		}
		
		if (m_hardInfo != null) {
			if ( m_hardInfo.getMachineName() == null || m_hardInfo.getMachineName().length() <=0 ) {
				xmlProcessorLog.error("[processHarewareInfo]There is no machine name attribute for the xml string.");
				return;
			}

			m_nodeID = GetNodeID(m_hardInfo.getMachineName(), m_hardInfo.getVirtualization());
			if ( m_nodeID <= 0 )
				return;

			//OS info
			StoreOSInfo();

			//CPU info
			StoreCPUInfo();

			//Memory info
			StoreMemoryInfo();

			//NIC info
			StoreNICInfo();

			//FiberCard info
			StoreFiberCardInfo();

			//Disk, Volume, Logical Disk info
			StoreDiskVolumeRelatedInfo();
		}
	}

	@SuppressWarnings("unchecked")
	public void processSoftwareInfo() {
		try {
			m_softInfo = JAXB.unmarshal(new StringReader(m_xmlContent), SoftwareInformation.class);
		} catch (Exception e) {
			xmlProcessorLog.error("[processSoftwareInfo] Unmarshal the xml to SoftwareInformation failed.", e);
			return;
		}
		
		if (m_softInfo != null) {
			if ( m_softInfo.getMachineName() == null || m_softInfo.getMachineName().length() <=0 ) {
				xmlProcessorLog.error("[processSoftwareInfo]There is no machine name attribute for the xml string.");
				return;
			}

			//Get NodeID before insert software info into DB
			m_nodeID = GetNodeID(m_softInfo.getMachineName(), m_softInfo.getVirtualization());
			if ( m_nodeID <= 0 )
				return;

			//software info
			List<InstalledProgram> installedProgram = (List<InstalledProgram>) processOneTypeOfNodeList(SoftwareInformation.class,
					InstalledProgram.class, "InstalledProgram");
			if ( installedProgram != null && installedProgram.size() > 0 ) {
				String[] idString = new String[1];
				for ( InstalledProgram program : installedProgram ) {
					String installDate = program.getInstallDate()==null ? "19700101" : program.getInstallDate().getValue().replaceAll("-", "");
					m_idao.spsrmedgeaddSoftwareInfo(idString,
							m_nodeID,
							program.getCategoryID(),
							this.getVersionValue(program.getVersion(), 0),
							this.getVersionValue(program.getVersion(), 1),
							program.getName(),
							program.getVersion(),
							installDate,
							program.getLanguage(),
							program.getSP(),
							program.getEdition()==null ? "" : program.getEdition().getValue());

					//Patches info
					List<InstalledProgram.Patches> patches = program.getPatches();
					if ( patches != null && patches.size() > 0 ) {
						List<InstalledProgram.Patches.ServicePatch> servicePatches = patches.get(0).getServicePatch();
						if ( servicePatches != null && servicePatches.size() > 0 ) {
							for ( InstalledProgram.Patches.ServicePatch patch : servicePatches ) {
								m_idao.spsrmedgeaddSoftwarePatchInfo(idString[0],
										patch.getName(),
										patch.getVersion(),
										patch.getInstallDate());
							}
						}
					}
				}
			}

			// App trending Info
			List<AppDataInfo> appDataInfoList = (List<AppDataInfo>)processOneTypeOfNodeList(SoftwareInformation.class,
					AppDataInfo.class, "AppDataInfo");
			if ( appDataInfoList != null && appDataInfoList.size() > 0 ) {
				for(AppDataInfo adi : appDataInfoList) {
					m_idao.spsrmedgeaddAppDailyData(m_nodeID,
							adi.getCategoryID(),
							adi.getMajorVersion(),
							adi.getMinorVersion(),
							adi.getInstanceName().getValue(),
							adi.getAppDataSizeMB(),
							new Date());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void processServerPKI() {
		try {
			m_serverPKI = JAXB.unmarshal(new StringReader(m_xmlContent), ServerPKIInformation.class);
		} catch (Exception e) {
			xmlProcessorLog.error("[processServerPKI] Unmarshal the xml to ServerPKIInformation failed.", e);
			return;
		}
		
		Date currentDateTime = new Date();

		if (m_serverPKI != null) {
			String nodeName = m_serverPKI.getMachineName();
			if ( nodeName == null ||nodeName.length() <=0 ) {
				xmlProcessorLog.error("[processServerPKI]There is no machine name attribute for the xml string.");
				return;
			}

			//CPU utilization
			List<CPUUtil> cpuUtil = (List<CPUUtil>) processOneTypeOfNodeList(ServerPKIInformation.class,
					CPUUtil.class, "CPUUtil");
			if ( cpuUtil != null && cpuUtil.size() > 0 ) {
				for ( CPUUtil util : cpuUtil ) {
					m_idao.spsrmedgeupdateCPUTrending(nodeName, util.getUtilization(),
							"CPU"+util.getIndex(), currentDateTime);
				}
			}

			//Memory utilization
			MemoryUtil memUtil = (MemoryUtil) processOneTypeOfNodeList(ServerPKIInformation.class,
					MemoryUtil.class, "MemoryUtil");
			if ( memUtil != null ) {
				m_idao.spsrmedgeupdateMemTrending(nodeName,
						memUtil.getPhysicalMemUtil(),
						memUtil.getPhysicalMemCapacity(),
						memUtil.getPageFileUtil(),
						memUtil.getPageFileCapacity(),
						currentDateTime);
			}

			//Disk utilization
			List<DiskUtil> diskUtil = (List<DiskUtil>) processOneTypeOfNodeList(ServerPKIInformation.class,
					DiskUtil.class, "DiskUtil");
			if ( diskUtil != null && diskUtil.size() > 0 ) {
				for ( DiskUtil util : diskUtil ) {
					m_idao.spsrmedgeupdatePhyDskTrending(nodeName,
							util.getIndex(),
							util.getThroughput(),
							currentDateTime);
				}
			}

			//Network utilization
			List<NetworkUtil> networkUtil = (List<NetworkUtil>) processOneTypeOfNodeList(ServerPKIInformation.class,
					NetworkUtil.class, "NetworkUtil");
			if ( networkUtil != null && networkUtil.size() > 0 ) {
				for ( NetworkUtil util : networkUtil ) {
					m_idao.spsrmedgeupdateNicTrending(nodeName,
							util.getUtilization(),
							util.getLinkSpeed(),
							util.getMACAddress(),
							currentDateTime);
				}
			}
		}
	}

	private NodeList getXmlNodeList( String tagName ) {
		if ( m_xmlContent == null || tagName == null )
			return null;

		try{
			//DOM
			DocumentBuilderFactory docBldfact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBlder = docBldfact.newDocumentBuilder();
			Document doc = docBlder.parse(new InputSource(new StringReader(m_xmlContent)));

			NodeList nodeList = doc.getElementsByTagName(tagName);
			if ( nodeList.getLength() > 0 )
			{
				return nodeList;
			}
		}
		catch ( Exception e ) {
			xmlProcessorLog.error(e.getMessage(), e);
		}

		return null;
	}

	private String getRawXmlFromDom(NodeList nodes, int index) {
		if ( nodes == null || index > nodes.getLength() )
			return "";

		try {
			DOMSource domSrc = new DOMSource(nodes.item(index));
			StringWriter strWrt = new StringWriter();
			StreamResult strRst = new StreamResult(strWrt);
			TransformerFactory transFac = TransformerFactory.newInstance();
			Transformer trans = transFac.newTransformer();
			trans.transform(domSrc, strRst);
			return strWrt.toString();
		}
		catch ( Exception e ) {
			xmlProcessorLog.error(e.getMessage(), e);
		}

		return "";
	}

	private <S,T> Object processOneTypeOfNodeList( Class<S> rootType, Class<T> nodeType, String tagName ) {
		String methodName = "get" + tagName;
		Object nodeList = null;
		Method method = null;
		try {
			Object[] param = null;
			Class<?>[] paramType = null;
			method = rootType.getDeclaredMethod( methodName, paramType );

			if ( rootType == SoftwareInformation.class ) {
				nodeList = method.invoke(m_softInfo, param);
			}
			else if ( rootType == HardwareInformation.class ) {
				nodeList = method.invoke(m_hardInfo, param);
			}
			else if ( rootType == ServerPKIInformation.class ) {
				nodeList = method.invoke(m_serverPKI, param);
			}

			return nodeList;
		}
		catch ( Exception e ) {
			xmlProcessorLog.error(e.getMessage(), e);
		}
		return null;
	}

	private AbstractMap<Integer, Long> getDiskUsedSpace(List<Volume> volume) {
		if ( volume == null || volume.size() <= 0 ) {
			return null;
		}
		AbstractMap<Integer, Long> diskUsedSpaceMap = new HashMap<Integer, Long>();
		for ( Volume vl : volume ) {
			Volume.DiskInfo diskInfo = vl.getDiskInfo();
			if ( diskInfo == null )
				continue;

			List<Volume.DiskInfo.Disk> disk = diskInfo.getDisk();
			if (disk == null) continue;

			for ( Volume.DiskInfo.Disk ds : disk ) {
				if ( diskUsedSpaceMap.containsKey(ds.getNumber()) ) {
					Long usedSpace = ds.getExtentLength() + diskUsedSpaceMap.get(ds.getNumber());
					diskUsedSpaceMap.put(ds.getNumber(), usedSpace);
				}
				else {
					diskUsedSpaceMap.put(ds.getNumber(), ds.getExtentLength());
				}
			}
		}

		return diskUsedSpaceMap;
	}

	private String getPartitionNameForLogicalDisk( List<DiskPartition> diskPartition, String logicalDiskName ) {
		if ( diskPartition == null ) return "";

		for ( DiskPartition dp : diskPartition ) {
			List<HardwareInformation.DiskPartition.LogicalDiskName> diskNameLst = dp.getLogicalDiskName();
			if (diskNameLst == null || diskNameLst.size() == 0)
				continue;
			for ( DiskPartition.LogicalDiskName diskName : diskNameLst ) {
				String logicDsNm = diskName.getValue();
				if ( logicDsNm != null && logicDsNm.equals(logicalDiskName) )
					return dp.getName();
			}
		}
		return "";
	}

	private String getVolumeDeviceIDForLogicalDisk( List<Volume> volume, String logicalDiskName ){
		if (volume == null) return "";

		for ( Volume vl : volume ) {
			String name = vl.getName();
			if ( name != null && name.equals(logicalDiskName + "\\") )
				return vl.getDeviceID();
		}
		return "";
	}

	/*
	 * @param version in format of xx.xx.xx.xx <majorversion.minorversion.buildversion.reviseversion>
	 * @param index starts from 0
	 * */
	private int getVersionValue(String version, int index) {
		if ( version == null || index < 0 )
			return -1;

		int curIndex = 0;
		int prevPos = 0;
		int i = 0;
		for ( ; i < version.length(); ++i ) {
			if ( version.charAt(i) < '0' || version.charAt(i) > '9' ) {
				if ( curIndex == index ) {
					break;
				}
			    ++curIndex;
			    prevPos = i + 1;
			}
		}

		if ( prevPos < i )
			return Integer.parseInt(version.substring(prevPos, i));

		return -1;
	}

    private int GetNodeID(String machineName, int virtualType) {
    	//get rhostids
    	List<SrmAshostInfo> rhostids = new ArrayList<SrmAshostInfo>();
    	m_idao.spsrmedgegetRhostID(m_rhostname, rhostids);

    	//Get NodeID before insert SRM info into DB
    	int nodeID[] = new int[1];
    	if ( rhostids.size() == 0 ) {
    		rhostids.add(new SrmAshostInfo());
    	}

    	for ( SrmAshostInfo rhostid : rhostids ) {
			m_idao.spsrmedgeupdatenode(machineName, virtualType, rhostid.getRhostid(), nodeID);
    	}

    	if ( nodeID[0] <= 0 ) {
			xmlProcessorLog.error("Failed to update node table (" +
					nodeID[0] + ") for machine " + machineName);
			return -1;
		}

		return nodeID[0];
    }

	@SuppressWarnings("unchecked")
	private void StoreOSInfo() {
		List<OS> os = (List<OS>) processOneTypeOfNodeList(HardwareInformation.class,
				OS.class, "OS");
		if ( os != null && os.size() > 0 ) {
			NodeList nodes = getXmlNodeList( "OS" );
			for ( int i = 0; i < os.size(); ++i ) {
				String rawXml = getRawXmlFromDom(nodes, i);
				OS osInfo = os.get(i);
				m_idao.spsrmedgeupdateos(osInfo.getBuildNumber(),
						osInfo.getBuildType(),
						osInfo.getCaption(),
						osInfo.getCountryCode(),
						osInfo.getCSDVersion(),
						osInfo.getCSName(),
						osInfo.getManufacturer(),
						osInfo.getManufacturer(),
						osInfo.getOSLanguage() == null ? -1 : osInfo.getOSLanguage(),
						osInfo.getProductType() == null ? -1 : osInfo.getProductType(),
						osInfo.getSerialNumber(),
						osInfo.getServicePackMajorVersion(),
						osInfo.getServicePackMinorVersion(),
						osInfo.getSystemDevice(),
						osInfo.getSystemDirectory(),
						osInfo.getSystemDrive(),
						osInfo.getVersion(),
						osInfo.getWindowsDirectory(),
						rawXml, m_nodeID);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void StoreCPUInfo() {
		List<CPU> cpu = (List<CPU>) processOneTypeOfNodeList(HardwareInformation.class,
				CPU.class, "CPU");
		if ( cpu != null && cpu.size() > 0 ) {
			try {
				m_idao.spsrmedgeupdateCPUBegin();
			} catch (DaoException e) {
			}

			NodeList nodes = getXmlNodeList( "CPU" );
			for ( int i = 0; i < cpu.size(); ++i ) {
				String rawXml = getRawXmlFromDom(nodes, i);
				CPU cpuInfo = cpu.get(i);

				m_idao.spsrmedgeupdateCPUInsert(cpuInfo.getAddressWidth(),
						cpuInfo.getArchitecture(),
						cpuInfo.getAvailability(),
						cpuInfo.getDataWidth(),
						cpuInfo.getDeviceID(),
						cpuInfo.getFamily(),
						cpuInfo.getL2CacheSize(),
						cpuInfo.getManufacturer(),
						cpuInfo.getMaxClockSpeed(),
						cpuInfo.getName(),
						cpuInfo.getProcessorId(),
						cpuInfo.getProcessorType(),
						rawXml,
						m_nodeID);
			}

			m_idao.spsrmedgeupdateCPUUpdate(m_nodeID);
			m_idao.spsrmedgeupdateCPUEnd(m_nodeID);
		}
	}

	@SuppressWarnings("unchecked")
	private void StoreMemoryInfo() {
		List<Memory> mem = (List<Memory>) processOneTypeOfNodeList(HardwareInformation.class,
				Memory.class, "Memory");
		if ( mem != null && mem.size() > 0 ) {
			try {
				m_idao.spsrmedgeupdateMemoryBegin();
			} catch (DaoException e) {
			}

			NodeList nodes = getXmlNodeList( "Memory" );
			for ( int i = 0; i < mem.size(); ++i ) {
				String rawXml = getRawXmlFromDom(nodes, i);
				Memory memInfo = mem.get(i);
				m_idao.spsrmedgeupdateMemoryInsert(memInfo.getCapacity(),
						memInfo.getDataWidth(),
						memInfo.getDeviceLocator(),
						memInfo.getFormFactor(),
						memInfo.getManufacturer(),
						memInfo.getMemoryType(),
						memInfo.getSpeed(),
						memInfo.getTag(),
						memInfo.getTotalWidth(),
						memInfo.getTypeDetail(),
						memInfo.getName(),
						rawXml,
						m_nodeID);
			}

			m_idao.spsrmedgeupdateMemoryUpdate(m_nodeID);
			m_idao.spsrmedgeupdateMemoryEnd(m_nodeID);
		}
	}

	@SuppressWarnings("unchecked")
	private void StoreNICInfo() {
		List<NIC> nic = (List<NIC>) processOneTypeOfNodeList(HardwareInformation.class,
				NIC.class, "NIC");
		if ( nic != null && nic.size() > 0 ) {
			try {
				m_idao.spsrmedgeupdateNICBegin();
			} catch (DaoException e) {
			}

			NodeList nodes = getXmlNodeList( "NIC" );
			for ( int i = 0; i < nic.size(); ++i ) {
				String rawXml = getRawXmlFromDom(nodes, i);
				NIC nicInfo = nic.get(i);

				//IPList IPAddress
				NIC.IPList ipList = nicInfo.getIPList();
				if ( ipList == null )
					continue;

				m_idao.spsrmedgeupdateNICInsert(nicInfo.getAdapterType(),
						nicInfo.getMACAddress(),
						nicInfo.getManufacturer(),
						nicInfo.getName(),
						nicInfo.getSpeed()==null ? -1 : nicInfo.getSpeed(),
						nicInfo.getMTU()==null ? -1 : nicInfo.getMTU(),
						rawXml,
						m_nodeID);
			}

			m_idao.spsrmedgeupdateNICUpdate(m_nodeID);
			m_idao.spsrmedgeupdateNICEnd(m_nodeID);

			for ( int i = 0; i < nic.size(); ++i ) {
				NIC nicInfo = nic.get(i);

				//IPList IPAddress
				NIC.IPList ipList = nicInfo.getIPList();
				if ( ipList == null )
					continue;

				List<NIC.IPList.IPAddress> ipAddrList = ipList.getIPAddress();
				if ( ipAddrList == null || ipAddrList.size() == 0 ) {
					continue;
				}

				for ( NIC.IPList.IPAddress ipAddr : ipAddrList ) {
					m_idao.spsrmedgeupdateIP(m_nodeID,
							nicInfo.getMACAddress(),
							ipAddr.getIP(),
							ipAddr.getSubnet(),
							ipAddr.getMask(),
							ipAddr.getDefaultGateway(),
							ipAddr.getDNSDomain(),
							ipAddr.getDNSHostName(),
							ipAddr.getDHCPServer(),
							-1, //Not implemented by now
							"");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void StoreFiberCardInfo() {
		List<FiberCard> fiberCard = (List<FiberCard>) processOneTypeOfNodeList(HardwareInformation.class,
				FiberCard.class, "FiberCard");
		if ( fiberCard != null && fiberCard.size() > 0 ) {
			try {
				m_idao.spsrmedgeupdateFibercardBegin();
			} catch (DaoException e) {
			}


			NodeList nodes = getXmlNodeList( "FiberCard" );
			for ( int i = 0; i < fiberCard.size(); ++i ) {
				String rawXml = getRawXmlFromDom(nodes, i);
				FiberCard fiberCardInfo = fiberCard.get(i);

				m_idao.spsrmedgeupdateFibercardInsert(fiberCardInfo.getAvailability(),
						fiberCardInfo.getCaption(),
						fiberCardInfo.getDescription(),
						fiberCardInfo.getDriverName(),
						fiberCardInfo.getName(),
						fiberCardInfo.getManufacturer(),
						fiberCardInfo.getProtocolSupported(),
						fiberCardInfo.getHardwareVersion(),
						fiberCardInfo.getDriverVersion()==null ? "" : fiberCardInfo.getDriverVersion().getValue(),
						fiberCardInfo.getMaxDataWidth()==null ? -1 : fiberCardInfo.getMaxDataWidth(),
						fiberCardInfo.getMaxNumberControlled()==null ? -1 : fiberCardInfo.getMaxNumberControlled(),
						fiberCardInfo.getMaxTransferRate()==null ? -1 : fiberCardInfo.getMaxTransferRate(),
						rawXml,
						m_nodeID);
			}

			m_idao.spsrmedgeupdateFibercardUpdate(m_nodeID);
			m_idao.spsrmedgeupdateFibercardEnd(m_nodeID);
		}
	}

	private long getLongValue(Long value) {
		return value == null ? 0 : value;
	}

	private boolean getBooleanValue(Boolean value) {
		return (value == null) ? false : value;
	}
	
	@SuppressWarnings("unchecked")
	private void StoreDiskVolumeRelatedInfo() {

		List<Volume> volume = (List<Volume>) processOneTypeOfNodeList(HardwareInformation.class,
				Volume.class, "Volume");
		if ( volume != null && volume.size() > 0 ) {

			//Volume info
			try {
				m_idao.spsrmedgeupdateVolumeBegin();
			} catch (DaoException e) {
			}

			NodeList nodes = getXmlNodeList( "Volume" );
			for ( int i = 0; i < volume.size(); ++i ) {
				String rawXml = getRawXmlFromDom(nodes, i);
				Volume volumeInfo = volume.get(i);

				m_idao.spsrmedgeupdateVolumeInsert(getLongValue(volumeInfo.getBlockSize()),
						volumeInfo.getCaption(),
						volumeInfo.getName(),
						getLongValue(volumeInfo.getCapacity()),
						getBooleanValue(volumeInfo.isCompressed()),
						volumeInfo.getDriveLetter(),
						volumeInfo.getDriveType()==null ? -1 : volumeInfo.getDriveType(),
						volumeInfo.getFileSystem(),
						volumeInfo.getFreeSpace()==null ? -1 : volumeInfo.getFreeSpace(),
						(int)(getLongValue(volumeInfo.getCapacity())>0 ? volumeInfo.getFreeSpace()*100/volumeInfo.getCapacity() : 0),
						volumeInfo.getDeviceID(),
						volumeInfo.getVolumeType()==null ? -1 : volumeInfo.getVolumeType(),
						rawXml,
						m_nodeID);
			}
			m_idao.spsrmedgeupdateVolumeUpdate(m_nodeID);
			m_idao.spsrmedgeupdateVolumeEnd(m_nodeID);
			m_idao.spsrmedgecollectVolDailyData(m_nodeID, new Date());

			//Fragment info
			for ( int i = 0; i < volume.size(); ++i ) {
				Volume volumeInfo = volume.get(i);
				List<Volume.Fragment> fragment = volumeInfo.getFragment();
				if ( fragment.size() > 0 ) {
					Volume.Fragment fragmentinfo = fragment.get(0);
					m_idao.spsrmedgeupdateFragmentEvent(fragmentinfo.getAverageFileSize()==null ? 0 : fragmentinfo.getAverageFileSize(),
							fragmentinfo.getAverageFragmentsPerFile() == null ? 0.0 : fragmentinfo.getAverageFragmentsPerFile(),
							fragmentinfo.getClusterSize()==null ? 0 : fragmentinfo.getClusterSize(),
							fragmentinfo.getExcessFolderFragments()==null ? 0 : fragmentinfo.getExcessFolderFragments(),
							fragmentinfo.getFilePercentFragmentation()==null ? 0: fragmentinfo.getFilePercentFragmentation(),
							fragmentinfo.getFragmentedFolders()==null ? 0 : fragmentinfo.getFragmentedFolders(),
							fragmentinfo.getFreeSpace()==null ? 0 : fragmentinfo.getFreeSpace(),
							fragmentinfo.getFreeSpacePercent()==null ? 0 : fragmentinfo.getFreeSpacePercent(),
							fragmentinfo.getFreeSpacePercentFragmentation()==null ? 0 : fragmentinfo.getFreeSpacePercentFragmentation(),
							fragmentinfo.getMFTPercentInUse()==null ? 0 : fragmentinfo.getMFTPercentInUse(),
							fragmentinfo.getMFTRecordCount()==null ? 0 : fragmentinfo.getMFTRecordCount(),
							fragmentinfo.getPageFileSize()==null ? 0 : fragmentinfo.getPageFileSize(),
							fragmentinfo.getTotalExcessFragments()==null ? 0 : fragmentinfo.getTotalExcessFragments(),
							fragmentinfo.getTotalFiles()==null ? 0 : fragmentinfo.getTotalFiles(),
							fragmentinfo.getTotalFolders()==null ? 0 : fragmentinfo.getTotalFolders(),
							fragmentinfo.getTotalFragmentedFiles()==null ? 0 : fragmentinfo.getTotalFragmentedFiles(),
							fragmentinfo.getTotalMFTFragments()==null ? 0 : fragmentinfo.getTotalMFTFragments(),
							fragmentinfo.getTotalMFTSize()==null ? 0 : fragmentinfo.getTotalMFTSize(),
							fragmentinfo.getTotalPageFileFragments()==null ? 0 : fragmentinfo.getTotalPageFileFragments(),
							fragmentinfo.getTotalPercentFragmentation()==null ? 0 : fragmentinfo.getTotalPercentFragmentation(),
							fragmentinfo.getUsedSpace()==null ? 0 : fragmentinfo.getUsedSpace(),
							fragmentinfo.getVolumeSize()==null ? 0 : fragmentinfo.getVolumeSize(),
							volumeInfo.getDeviceID(),
							m_nodeID);
				}
			}
		}

		//DiskDrive info
		List<DiskDrive> diskDrive = (List<DiskDrive>) processOneTypeOfNodeList(HardwareInformation.class,
				DiskDrive.class, "DiskDrive");
		if ( diskDrive != null && diskDrive.size() > 0 ) {
			try {
				m_idao.spsrmedgeupdateDiskBegin();
			} catch (DaoException e) {
				//ignore below exception for simple
				//com.ca.arcserve.edge.app.base.dao.DaoException: There is already an object named '##as_edge_srm_temp_diskInfo' in the database.
			}

			NodeList nodes = getXmlNodeList( "DiskDrive" );
			AbstractMap<Integer, Long> diskUsedSpaceMap = getDiskUsedSpace(volume);

			for ( int i = 0; i < diskDrive.size(); ++i ) {
				String rawXml = getRawXmlFromDom(nodes, i);
				DiskDrive diskDriveInfo = diskDrive.get(i);
				long usedSpace = 0L;
				if ( diskUsedSpaceMap != null && diskUsedSpaceMap.containsKey(diskDriveInfo.getIndex()) ) {
					usedSpace = diskUsedSpaceMap.get(diskDriveInfo.getIndex());
				}

				m_idao.spsrmedgeupdateDiskInsert(diskDriveInfo.getCaption(),
						diskDriveInfo.getDescription(),
						diskDriveInfo.getDeviceID(),
						usedSpace,
						0, //Disk throughput is implemented in PKI feature
						diskDriveInfo.getInterfaceType(),
						diskDriveInfo.getManufacturer(),
						diskDriveInfo.getMediaType(),
						diskDriveInfo.getModel(),
						diskDriveInfo.getName(),
						diskDriveInfo.getPartitions()==null ? -1 : diskDriveInfo.getPartitions(),
						diskDriveInfo.getSize()==null ? -1 : diskDriveInfo.getSize(),
						diskDriveInfo.getTotalCylinders()==null ? -1 : diskDriveInfo.getTotalCylinders(),
						diskDriveInfo.getTotalHeads()==null ? -1 : diskDriveInfo.getTotalHeads(),
						diskDriveInfo.getTotalSectors()==null ? -1 : diskDriveInfo.getTotalSectors(),
						diskDriveInfo.getTotalTracks()==null ? -1 : diskDriveInfo.getTotalTracks(),
						diskDriveInfo.getTracksPerCylinder()==null ? -1 : diskDriveInfo.getTracksPerCylinder(),
						diskDriveInfo.getSCSIBus()==null ? -1 : diskDriveInfo.getSCSIBus(),
						diskDriveInfo.getSCSILogicalUnit()==null ? -1 : diskDriveInfo.getSCSILogicalUnit(),
						diskDriveInfo.getSCSIPort()==null ? -1 : diskDriveInfo.getSCSIPort(),
						diskDriveInfo.getSCSITargetId()==null ? -1 : diskDriveInfo.getSCSITargetId(),
						diskDriveInfo.getSignature(),
						diskDriveInfo.getIndex(), //If this field doesn't exist, it should generic errors
						diskDriveInfo.getDiskType()==null ? -1 : diskDriveInfo.getDiskType(),
						rawXml,
						m_nodeID);
			}

			m_idao.spsrmedgeupdateDiskUpdate(m_nodeID);
			m_idao.spsrmedgeupdateDiskEnd(m_nodeID);
		}

		//DiskPartition info
		List<DiskPartition> diskPartition = (List<DiskPartition>) processOneTypeOfNodeList(HardwareInformation.class,
				DiskPartition.class, "DiskPartition");
		if ( diskPartition != null && diskPartition.size() > 0 ) {
			try {
				m_idao.spsrmedgeupdatePartitionBegin();
			} catch (DaoException e) {
			}


			NodeList nodes = getXmlNodeList( "DiskPartition" );
			for ( int i = 0; i < diskPartition.size(); ++i ) {
				String rawXml = getRawXmlFromDom(nodes, i);
				DiskPartition diskPartitionInfo = diskPartition.get(i);

				m_idao.spsrmedgeupdatePartitionInsert(diskPartitionInfo.getBlockSize(),
						diskPartitionInfo.isBootPartition(),
						diskPartitionInfo.getName(),
						diskPartitionInfo.getNumberOfBlocks(),
						diskPartitionInfo.isPrimaryPartition(),
						diskPartitionInfo.getSize(),
						diskPartitionInfo.getType(),
						rawXml,
						m_nodeID,
						diskPartitionInfo.getDiskIndex());
			}

			m_idao.spsrmedgeupdatePartitionUpdate(m_nodeID);
			m_idao.spsrmedgeupdatePartitionEnd(m_nodeID);
		}

		//LogicalDisk info
		List<LogicalDisk> logicalDisk = (List<LogicalDisk>) processOneTypeOfNodeList(HardwareInformation.class,
				LogicalDisk.class, "LogicalDisk");
		if ( logicalDisk != null && logicalDisk.size() > 0 ) {
			try {
				m_idao.spsrmedgeupdateLogicalDiskBegin();
			} catch (DaoException e) {
			}


			NodeList nodes = getXmlNodeList( "LogicalDisk" );
			for ( int i = 0; i < logicalDisk.size(); ++i ) {
				String rawXml = getRawXmlFromDom(nodes, i);
				LogicalDisk logicalDiskInfo = logicalDisk.get(i);
				try {
					m_idao.spsrmedgeupdateLogicalDiskInsert(logicalDiskInfo.getCaption(),
							logicalDiskInfo.isCompressed(),
							logicalDiskInfo.getDescription(),
							logicalDiskInfo.getDeviceID(),
							logicalDiskInfo.getDriveType(),
							logicalDiskInfo.getFileSystem(),
							logicalDiskInfo.getFreeSpace(),
							logicalDiskInfo.getMaximumComponentLength(),
							logicalDiskInfo.getMediaType(),
							logicalDiskInfo.getName(),
							logicalDiskInfo.isQuotasDisabled(),
							logicalDiskInfo.isQuotasIncomplete(),
							logicalDiskInfo.isQuotasRebuilding(),
							logicalDiskInfo.getSize(),
							logicalDiskInfo.isSupportsDiskQuotas(),
							logicalDiskInfo.isSupportsFileBasedCompression(),
							logicalDiskInfo.getVolumeName(),
							rawXml,
							getPartitionNameForLogicalDisk(diskPartition, logicalDiskInfo.getDeviceID()),
							getVolumeDeviceIDForLogicalDisk(volume, logicalDiskInfo.getDeviceID()),
							m_nodeID);
				} catch (Throwable t) {
					xmlProcessorLog.error(t.getMessage(), t);
				}
			}

			m_idao.spsrmedgeupdateLogicalDiskUpdate(m_nodeID);
			m_idao.spsrmedgeupdateLogicalDiskEnd(m_nodeID);
		}

		//DiskVolumeMap info
		if ( volume != null ) {
			for ( int i = 0; i < volume.size(); ++i ) {
				Volume volumeInfo = volume.get(i);
				if ( volumeInfo == null ) {
					continue;
				}

				Volume.DiskInfo diskInfo = volumeInfo.getDiskInfo();
				if ( diskInfo == null ) {
					continue;
				}

				List<Volume.DiskInfo.Disk> disk = diskInfo.getDisk();
				if ( disk == null || disk.size() == 0 ) {
					continue;
				}

				try {
					m_idao.spsrmedgeupdateMapBegin();
				} catch (DaoException e) {
				}

				for ( Volume.DiskInfo.Disk ds : disk ) {
					m_idao.spsrmedgeupdateMapInsert(m_nodeID, volumeInfo.getDeviceID(),
							ds.getNumber()==null ? -1 : ds.getNumber());
				}
				m_idao.spsrmedgeupdateMapUpdate(m_nodeID);
				m_idao.spsrmedgeupdateMapEnd(m_nodeID);
			}
		}
	}
}
