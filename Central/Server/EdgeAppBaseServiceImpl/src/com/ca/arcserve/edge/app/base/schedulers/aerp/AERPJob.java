package com.ca.arcserve.edge.app.base.schedulers.aerp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.arcserve.cserp.entitlements.entitlementReg.beans.UsageDetails;
import com.arcserve.cserp.entitlements.entitlementReg.client.EntitlementRegister;
import com.arcserve.cserp.entitlements.entitlementReg.utility.EntitlementRegisterUtility;
import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.common.EdgeCommonServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.dashboard.RecoveryPointDataItem;
import com.ca.arcserve.edge.app.base.webservice.contract.license.bundled.LicenseInformation;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseLocalWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseLocalWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

public class AERPJob implements Job {
	private static final Logger logger = Logger.getLogger(AERPJob.class);
	
	private static final String logCollectorUtilityPath = EdgeCommonUtil.EdgeInstallPath+ "\\" + CommonUtil.BaseEdgeBIN_DIR + "DiagnosticUtility";
	private String DIAGUTILITY_PROPERTY_FILEPATH = logCollectorUtilityPath + "\\config.properties";
	private String consoleZipFileSuffix = "_console.arcZIP";
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.info("execute(JobExecutionContext) - started at " + new Date()
				+ " " + this.getClass().getName());

		try
		{
			HashMap<String, String> entitlementFreq = new EntitlementRegisterUtility().getEntitlementFrequencies();
			
			//create policy.xml files with the UUIDs by skipping sensitive data.
			createEdgePolicyDataToExport();
			
			String uploadFilePath = Boolean.parseBoolean(entitlementFreq.get("uploadFile")) ? getUploadFilePath() : null;
			
			String responseCode = EntitlementRegister.uploadFile(uploadFilePath, getUsageDetailsForUDPConsole());
			
			//If uploadFrequency in Policy.xml is changed then terminate existing AERPJob.
			//submit new AERPJob for the new Policy.xml details.
			if(responseCode != null && responseCode.equals("UPLOADUSAGEDETAILS_SUCCESS_UPLOADSETTINGSCHANGED"))
			{
				try {
					    logger.info("Resubmitting AERPJob for uploadFrequency change in the Policy.xml");
					    entitlementFreq = new EntitlementRegisterUtility().getEntitlementFrequencies();
						AERPSchedulerService.getInstance().submitAERPJob(Integer.parseInt(entitlementFreq.get("uploadfrequency").trim()), entitlementFreq.get("uploadTimeStamp").trim());
						logger.info("Resubmitting AERPJob successfully for uploadFrequency change in the Policy.xml");
					} catch (Exception e) {
						logger.error("Error submitting AERPJob " + this.getClass().getName() + " " + e.getMessage());
					}
			}
		}
		catch(Exception e)
		{
			logger.error("Exception submit AERPJob " + e.getMessage());
		}

		logger.info("Ended AERPJob at " + new Date() + " "
				+ this.getClass().getName());
	}

	/**
	 * This method Calls the EdgeAppDBImportExport tool to Export/Backup the UDP
	 * database. It reads the ExportPath from DiagnosticUtilty
	 * config/properties. C:\Program Files\Arcserve\Unified Data
	 * Protection\Management\BIN\DiagnosticUtility\config.properties"
	 * 
	 * @return
	 */
	public String getUploadFilePath() {
		String exportDBPath = "";
		try {
				if(exportDBForEdge())
				{
					exportDBPath = logCollectorUtilityPath + "/" + getHostName() + consoleZipFileSuffix;
					logger.info("Exported DB Path is " +exportDBPath );
				}
		} catch (Exception e) {
			logger.error("Unable to find ExportDB file " + exportDBPath + " exception details " + e.getMessage());
		}
		return exportDBPath;
	}

	/**
	 * This method will invoke the EdgeAppDBImportExport and export the DB to
	 * the location in the DiagnisticUitliy config.propeties/exportPath.
	 * 
	 * @return
	 */
	public boolean exportDBForEdge() {
		boolean dbExported = false;
		String msg = "";
		try {
			// Run EdgeAppDBImportExport
			updateDiagConfigProperties(true);
			String logCollectorUtilityBatch = logCollectorUtilityPath
					+ "\\arcserveConsoleSupport.bat";
			logCollectorUtilityBatch = "\"" + logCollectorUtilityBatch + "\"";

			String cmd = "cmd /c " + logCollectorUtilityBatch;

			File f = new File(logCollectorUtilityPath);

			int iResult = -1;
			try {
				iResult = executeCmd(cmd, null, f, "Y", true);
			} catch (IOException | InterruptedException e) {
				logger.error(e.getMessage());
			}

			if (iResult == 0) {
				dbExported = true;
				msg = String.format(EdgeCMWebServiceMessages
						.getResource("AERPExportDBUtilityExecSuccess"));
				logger.info(msg);
			}

			else {
				msg = String.format(EdgeCMWebServiceMessages
						.getResource("AERPExportDBUtilityExecFail"));
				logger.info(msg);
			}
			updateDiagConfigProperties(false);
		} catch (Exception e) {
			updateDiagConfigProperties(false);
			logger.error("Exception reading config.properties");
		}
		return dbExported;
	}

	/**
	 * Load config.propeties from C:\Program Files\Arcserve\Unified Data
	 * Protection\Management\BIN\DiagnosticUtility\config.properties"
	 */
	public void updateDiagConfigProperties(boolean addProperties) {
		logger.info("Updating Diag config.properties " +addProperties);
		try
		{
			Properties prop = loadDiagProperties();
			if(prop != null)
			{
				if(addProperties)
				{
					prop.setProperty("isAERPService", "true");
				}
				else
				{
					prop.remove("isAERPService");
				}
				File file = new File(DIAGUTILITY_PROPERTY_FILEPATH);
				FileOutputStream fileOut = new FileOutputStream(file);		
				prop.store(fileOut, "Favorite Things");		
				fileOut.close();
				logger.info("save Diagnostic config properties success");
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("Exception in updateDiagConfigProperties " +e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Exception in updateDiagConfigProperties " +e.getMessage());
		}
		logger.info("Updated Diag config.properties");
	}

	public Properties loadDiagProperties()
	{
		Properties diagnosticproperties =  null;
		logger.info("Loading Diag config.properties");
		try {
			InputStream inputStream = null;
			inputStream = new FileInputStream(DIAGUTILITY_PROPERTY_FILEPATH);
			if (inputStream != null) {
				try {
					diagnosticproperties = new Properties();
					diagnosticproperties.load(inputStream);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Exception in loadDiagProperties"
							+ e.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error("Exception reading DiagnisticUtility properties file"
					+ e.getMessage());
		}
		logger.info("Loaded Diag config.properties");
		return diagnosticproperties;
	}
	
	/*
	 * The usagaDetails object contains information regarding how many nodes and
	 * how much data is protected by console
	 */
	public UsageDetails getUsageDetailsForUDPConsole() {
		logger.info("Creating UsageDetails for Console");
		IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		UsageDetails usageDetails = new UsageDetails();
		long protectedDataSize = 0;
		long dataSizeOnStorage = 0;
		long rawDataProtected = 0;
		long rps_protectedDataSize = 0;
		long rps_dataSizeOnStorage = 0;
		long rps_rawDataProtected = 0;
		int physicalServers = 0;
		int virtualServers = 0;
		try {
			// set the version of console from where the usagedetails are picked
			EdgeVersionInfo versionInfo = new EdgeCommonServiceImpl()
					.getVersionInformation();
			usageDetails.setProductVersion(versionInfo.getMajorVersion() + "."
					+ versionInfo.getMinorVersion());
			logger.info("product version is "
					+ versionInfo.getMajorVersion() + "."
					+ versionInfo.getMinorVersion());
			//Get D2D Backup Data
			List<RecoveryPointDataItem> d2dBackupData = new NodeServiceImpl().getD2DBackupData();
			if(d2dBackupData!=null && d2dBackupData.size()>0)
			{
				for(RecoveryPointDataItem dataItem : d2dBackupData)
				{
					protectedDataSize += dataItem.getRestorableInKB();
					dataSizeOnStorage +=dataItem.getCompressedInKB();
					rawDataProtected += dataItem.getRawInKB();
				}
			}
			// we just need one recovery point which is latest snapshot of 
			// amount of data recovered by all nodes
			// in incremental backup scenario the latest recovery point shows			
			// the amount of data
			//RPS  Data
			List<RecoveryPointDataItem> recoveryPoints = new NodeServiceImpl()
			.getRecoveryPointData();
			RecoveryPointDataItem item = new RecoveryPointDataItem();
			if(recoveryPoints!=null && recoveryPoints.size()>0)
			{
				item=recoveryPoints.get(recoveryPoints.size()-1);
				rps_protectedDataSize=item.getRestorableInKB();
				rps_dataSizeOnStorage=item.getCompressedInKB();
				rps_rawDataProtected = item.getRawInKB();
			}				
			// Set ProtectionData to UsageDetails
			usageDetails.setProductName(EdgeCMWebServiceMessages.getMessage("productNameUPM"));
			usageDetails.setProtectedDataSize(bytes2String(protectedDataSize * 1024));
			logger.info("ProtectedDataSize " + protectedDataSize);
			HashMap<String, String> protectionData = new HashMap<String, String>();
			protectionData.put("RawDataProtected", bytes2String(rawDataProtected * 1024));
			protectionData.put("StorageUtilized", bytes2String(dataSizeOnStorage * 1024));
			protectionData.put("RPS_RawDataProtected", bytes2String(rps_rawDataProtected * 1024));
			protectionData.put("RPS_StorageUtilized", bytes2String(rps_dataSizeOnStorage * 1024));
			protectionData.put("RPS_ProtectedDataSize", bytes2String(rps_protectedDataSize * 1024));
			usageDetails.setProtectionData(protectionData);
			logger.info("RawDataProtected" + protectionData.get("RawDataProtected") + " StorageUtilized " +protectionData.get("StorageUtilized"));
			logger.info("RPS_RawDataProtected" + protectionData.get("RPS_RawDataProtected") + " RPS_StorageUtilized " +protectionData.get("RPS_StorageUtilized") 
					+ " RPS_ProtectedDataSize" + protectionData.get("RPS_ProtectedDataSize"));
			usageDetails.setSockets(Integer.toString(getLicenseSocketCountForCosole()));

			// get all the nodes connected to this console no matter they have
			// recovery points or not
			final List<EdgeHost> hosts = new LinkedList<EdgeHost>();
			hostMgrDao.as_edge_host_list(0, 1, hosts); // get all visible nodes
			Map<String, Integer> osTypes = new HashMap<String, Integer>();
			HashMap<String, Integer> agentDetails = new HashMap<String, Integer>();
			//since only two application types are supported --sql server and exchange
			int noOfExchApps=0, noOfSQLApps=0;
			if(hosts != null)
			{
				logger.info("Hosts " + hosts.size());
				for (EdgeHost h : hosts) // group all nodes by OS type and count per
											// OS type
				{
					String agentDtlsString = "";
					if (!osTypes.containsKey(h.getOsdesc())) {
						osTypes.put(h.getOsdesc(), 1);
					} else {
						osTypes.put(h.getOsdesc(), osTypes.get(h.getOsdesc()) + 1);
					}
					//count application types
					if(ApplicationUtil.isSQLInstalled(h.getAppStatus()))
					{
						noOfSQLApps++;
					}
					if(ApplicationUtil.isExchangeInstalled(h.getAppStatus()))
					{
						noOfExchApps++;
					}
					logger.info("Machine Type " + h.getRhostType());
					if(HostTypeUtil.isPhysicsMachine(h.getRhostType()))//PhysicalServers
						physicalServers++;
					else 
						virtualServers++;
					
					agentDtlsString = h.getD2DMajorversion() + "." + h.getD2dMinorversion() + "." + h.getD2dBuildnumber();
					if(agentDetails.containsKey(agentDtlsString))
					{
						int count = agentDetails.get(agentDtlsString);
						agentDetails.put(agentDtlsString, ++count);
					}
					else
					{
						agentDetails.put(agentDtlsString, 1);
					}
				}
			}
			
			logger.info("Physical Servers " + physicalServers);
			logger.info("Virtual Servers " + virtualServers);
			
			usageDetails.setPhysicalServers(Integer.toString(physicalServers));
			usageDetails.setVirtualServers(Integer.toString(virtualServers));
			
			List<HashMap<String, String>> osDataList = new ArrayList<HashMap<String, String>>();
			for (Map.Entry<String, Integer> entry : osTypes.entrySet()) {
				HashMap<String, String> osData = new HashMap<String, String>();
				osData.put("name", entry.getKey());
				osData.put("count", entry.getValue() + "");// count of each
																// ostypes in
																// list of hosts
				osDataList.add(osData);
				logger.info("OSDATA Name " +entry.getKey() + "Count " +  entry.getValue());
			}
			usageDetails.setOSData(osDataList);
			// Set osData to UsageDetails
			
			// set Application information 
			if(noOfSQLApps > 0 || noOfExchApps > 0)
			{
				List<HashMap<String, String>> applicationDataList = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> applicationData = null;
				if(noOfSQLApps > 0)
				{
					applicationData = new HashMap<String, String>();
					applicationData.put("name", "SQLSERVER");
					applicationData.put("count", noOfSQLApps+"");
					applicationDataList.add(applicationData);	
				}
				if(noOfExchApps > 0)
				{
					applicationData = new HashMap<String, String>();
					applicationData.put("name", "MSEXCHANGE");
					applicationData.put("count", noOfExchApps+"");
					applicationDataList.add(applicationData);
				}
				usageDetails.setApplicationData(applicationDataList);
			}
			
			//Set AgentData to UsageDetails -
			 List<HashMap<String, String>> agentDataList = new ArrayList<HashMap<String, String>>();
			 if(agentDetails != null && agentDetails.size() > 0)
			 {
				 for(Entry<String, Integer> data : agentDetails.entrySet())
				 {
					 HashMap<String, String> agentData = new HashMap<String, String>();
					 String[] agentVersionDetails = data.getKey().split("\\.");
					 logger.info("Adding agentDetails to usageDetails " + agentVersionDetails);
					 logger.info("agentDetails count " + data.getValue());
					 agentData.put("name",ServiceContext.getInstance().getProductNameD2D());
					 agentData.put("majorVersion", agentVersionDetails[0]);
					 agentData.put("minorVersion", agentVersionDetails[1]);
					 agentData.put("buildNumber", agentVersionDetails[2]);
					 agentData.put("count", Integer.toString(data.getValue()));
				     agentDataList.add(agentData);
				 }
			 }
		     usageDetails.setAgentData(agentDataList);
			logger.info("Created UsageDetails for Console Application Data is" + usageDetails.getAgentData() + " + OS Data is " + usageDetails.getOSData() + "Agent Details " + usageDetails.getAgentData()
					+ "ProtectedData " + usageDetails.getProtectionData() + " Sockets count " + usageDetails.getSockets());

		} catch (Exception e) {
			logger.error("getUsageDetails exception" + e.getMessage());
		}
		// return usageDetails;

		return usageDetails;
	}

	/**
	 * This method returns data size in KB/MB/GB for bytes.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytes2String(long bytes) {
		logger.info("Converting bytes to string " + bytes);
		String bytesString = "";
		try {
			DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(
					DataFormatUtil.getDateFormatLocale());
			DecimalFormat number = new DecimalFormat("0.00", formatSymbols);
			if (bytes < 1024)
				bytesString = bytes + " Bytes";
			else if (bytes < (1024 * 1024)) {
				String kb = number.format(((double) bytes) / 1024);
				if (kb.startsWith("1024"))
					bytesString = "1 MB";

				bytesString = kb + " KB";
			} else if (bytes < (1024 * 1024 * 1024)) {
				String mb = number.format(((double) bytes) / (1024 * 1024));
				if (mb.startsWith("1024"))
					bytesString = "1 GB";

				bytesString = mb + " MB";
			} else
				bytesString = number.format(((double) bytes)
						/ (1024 * 1024 * 1024))
						+ " GB";
		} catch (Exception e) {
			logger.error("Exception Converting bytes to String "
					+ e.getMessage());
			return bytesString;
		}
		logger.info("Coverted bytes to String " + bytesString);
		return bytesString;
	}

	private int executeCmd(String cmd, String[] envp, File dir, String input,
			boolean ignoreInputErrStreams) throws IOException,
			InterruptedException {
		logger.info("Execute command for Export DB");
		Runtime rn = Runtime.getRuntime();
		Process process = null;

		logger.info("ExeCMD: " + cmd + ", dir: " + dir.getAbsolutePath());

		process = rn.exec(cmd, envp, dir);

		OutputStream objOutput = process.getOutputStream();

		if (input != null && !input.isEmpty()) {
			objOutput.write(input.getBytes());
			objOutput.flush();
			objOutput.close();
		}

		BufferedReader cmdInput = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

		BufferedReader cmdError = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));

		if (ignoreInputErrStreams) {
			try {
				cmdInput.close();
				cmdError.close();
			} catch (IOException e) {
				logger.error("Exception while closing the process input, error streams");
				logger.error(e);
			}
		}

		else {

			StringBuffer cmdOutputbuffer = new StringBuffer();
			StringBuffer cmdErrorbuffer = new StringBuffer();

			String cmdout = "";

			try {

				// Read command output and storing into string buffer
				while ((cmdout = cmdInput.readLine()) != null) {
					cmdOutputbuffer.append(cmdout);
					cmdOutputbuffer.append("\n");
				}

			} catch (Exception err) {
				logger.error("some exception in reading cmd output" + err);
				// return null;
			}

			try {
				// Read command output and storing into string buffer
				while ((cmdout = cmdError.readLine()) != null) {
					cmdErrorbuffer.append(cmdout);
					cmdErrorbuffer.append("\n");
				}
			} catch (Exception error) {
				logger.error("some exception in reading cmd output error"
						+ error);
			}
		}

		process.waitFor();
		int iResult = process.exitValue();
		logger.info("cmd: " + cmd + "exit code=" + iResult);

		process.destroy();
		process = null;
		return iResult;
	}
	
	public String getHostName() throws UnknownHostException
	{
        InetAddress iAddress = InetAddress.getLocalHost();
        String hostName = iAddress.getHostName();
        //String canonicalHostName = iAddress.getCanonicalHostName();
        logger.info("HostName:" + hostName);
        return hostName;
	}
	
	public int getLicenseSocketCountForCosole()
	{
		int socketCount = 0;
		try
		{
			LicenseServiceImpl licenseServiceImpl = new LicenseServiceImpl();
			ILicenseLocalWrapper localWrapper=LicenseLocalWrapper.getInstance();
			List<LicenseInformation> licenseList = licenseServiceImpl.getLicenses();
			if(licenseList  != null && licenseList.size() > 0)
			{
				int count = 0;
				for(LicenseInformation licenseInformation : licenseList)
				{
					String code = licenseInformation.getCode();
					count = localWrapper.getUsed(code);
					logger.info("socket licsense code " + code + " count is " + count);
					socketCount = socketCount + count;
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Exception getLicenseSocketCountForConsole " + e.getMessage());
		}
		
		return socketCount;
	}
	
	public void createEdgePolicyDataToExport()
	{
		try
		{
			logger.info("In createEdgePolicyDataToExport");
			IEdgePolicyDao edgePolicyDao = DaoFactory.getDao(IEdgePolicyDao.class);
			List<EdgePolicy> policys = new LinkedList<EdgePolicy>();
			edgePolicyDao.as_edge_policy_list(0, 1, policys); 
			logger.info("In createEdgePolicyDataToExport policyList size " + policys != null ? policys.size() : null);
			if(policys != null && policys.size() > 0)
			{
				for(EdgePolicy edgePolicy : policys)
				{
					createPolicyXMLForUUID(edgePolicy.getUuid(), edgePolicy.getPolicyxml());
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Exception in createEdgePolicyDataToExport " + e.getMessage());
		}
	}
	/**
	 * This method create Policy.xml and removes the sensitive data in the XML like username and password before export.
	 */
	public void createPolicyXMLForUUID(String uuid, String policyXML)
	{
		String filePath = "";
		try
		{
			logger.info("Create as_edge_policy Policy.xml for uuid " + uuid);
			filePath = logCollectorUtilityPath + "\\as_edge_policy";	
			policyXML = modifyPolicyXML(uuid, policyXML);
	  		logger.info("Create as_edge_policy Policy.xml for uuid " + uuid +" to UDP install path  " + filePath);
	  		File file = new File(filePath);
	  		if(!file.exists())
	  		{
	  			file.mkdir();
	  		}
	  		filePath= filePath + "\\" + uuid + ".xml";
	  		logger.info("Create as_edge_policy Policy.xml in path " + filePath);
	  		file = new File(filePath);
	  		if(file.exists())
	  		{
	  				file.delete();
	  		}
	  		BufferedWriter out = null;
	  		try 
	  		{
	  			out = new BufferedWriter(new FileWriter(filePath));
	  			out.write(policyXML);
	  			logger.info("Created as_edge_policy Policy.xml for uuid " + uuid +" to UDP install path  " + filePath);
	  		} catch (IOException e) {
	  			    throw new RuntimeException(e);    
	  			} finally {
	  			    if (out != null) {
	  			        try { out.close(); } catch (IOException e) {}
	  			    }
	  			}
		}
		catch(Exception e)
		{
			logger.error("Exception while creating edgePolicyData for uuid " + uuid +" to UDP install path  " + filePath + " Exception details" +e.getMessage());
		}
	}
	
	public String modifyPolicyXML(String uuid, String policyXML)
	{
		try
		{
			logger.debug("Modifying PolicyXML " + policyXML + " for UUID " + uuid);
			String[] exprs = { "password", "Password", "username", "hostname", "UserName"};
		    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		    Document doc = domFactory.newDocumentBuilder().parse(new StringBufferInputStream(policyXML));
		    for(int i = 0 ; i < exprs.length ; i++)
		    {
		    	 String XPATH_EXPRESSION = "//*[substring(name(),string-length(name())-7) = '" + exprs[i] + "']";
			     XPath xpath = XPathFactory.newInstance().newXPath();
				 XPathExpression expr = xpath.compile(XPATH_EXPRESSION);
			     Object result = expr.evaluate(doc, XPathConstants.NODESET);
			     NodeList nodes = (NodeList) result;
			     logger.debug("Nodes Count" + nodes.getLength());
			     for(int count = 0 ; count < nodes.getLength() ; count++)
			     {
			    	Node node = nodes.item(count);
			    	logger.debug("Node name" + node.getNodeName());
			    	logger.debug("old Node value" + node.getTextContent());
			    	node.setNodeValue("");
			    	node.setTextContent("");
			    	logger.debug("New Node value" + node.getTextContent());
			     }
		    }
		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
		    StringWriter writer = new StringWriter();
		    transformer.transform(new DOMSource(doc), new StreamResult(writer));
		    policyXML = writer.getBuffer().toString();
		    logger.debug("Modified PolicyXML" + policyXML);
		}
		catch(Exception e)
		{
			logger.error("Exception while modifying XML for UUID " +  uuid);
		}
		return policyXML;
	}
}
