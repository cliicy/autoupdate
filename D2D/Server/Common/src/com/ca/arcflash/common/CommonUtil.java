package com.ca.arcflash.common;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPathConstants;

//added by cliicy.luo
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
//added by cliicy.luo

import org.apache.log4j.Logger;

import com.ca.arcflash.common.xml.PMXPathReader;
import com.ca.arcflash.webservice.data.PM.BIPatchInfo;
import com.ca.arcflash.webservice.data.PM.PatchInfo;

public class CommonUtil {
	//static PatchInfo[] aryPInfo = null;//added by cliicy.luo
	
	private static final Logger logger = Logger.getLogger(CommonUtil.class);

	private static final String D2DInstallPathKey = CommonRegistryKey.getD2DRegistryRoot()+"\\InstallPath";
	
	private static final String FAILOVER_VM_KEY = "FailoverVMType";
	private static final String FAILOVER_JOB_SCRIPT = "FailoverJobScript";
	private static final String Backkup_JOBID_VALUE = "BackupJobID";
	public static final String D2DSrvName = "CASAD2DWebSvc";
	private static final String FAILOVER_REBOOT_KEY = "FailoverVMRebootFlag";
	private static final String FAILOVER_ONLINEDISK_KEY = "FailoverVMOnlineDiskFlag";

	public static String D2DInstallPath = "C:\\Program Files\\arcserve\\Unified Data Protection\\Engine\\";
	public static String D2DHAInstallPath = "..\\..\\";
	public static final String D2DFailoverJobScript = "\\arcserve\\Unified Data Protection\\Engine";
	
	public static String VDDKInstallPath = "C:\\Program Files\\arcserve\\Unified Data Protection\\Engine\\BIN\\VDDK\\";
	
	public static String FailoverVM = ""; 
	public static String FailoverVM_HYPERV = "HyperV"; 
	public static String FailoverVM_VMWARE = "VMWare";
	
	
	// Below numbers are less than the limitation of VMware/HyperV by 2
	// This reserved count is used for "DR from now"
	public static final int MAX_VMWARE_SNAPSHOT = 29;
	public static final int MAX_HYPERV_SNAPSHOT = 48;
		
	public static final String SNAPSHOT_XML_FILE = "VMSnapshotsModel.xml";
	public static final String SNAPSHOT_XML_LOCATION_FILE = "VMSnapshotsModel.loc";
	public static final String ADRCONFIG_XML_FILE = "AdrConfigure.xml";
	public static final String PARTIAL_ADRCONFIG_File = "VMDiskInfo.xml";
	public static final String BLOCKCTF_FILE = "block0000000002.ctf";
	public static final String ADRINFOC_DRZ_FILE = "AdrInfoC.drz";
	
	private static final String REP_JOBID_VALUE = "RepJobID";
	
	public static final int VM_CREATED = 1;
	public static final int VM_EXIST = 2;
	public static final int VM_CREATE_CANCELED = 3;
	public static final int VM_CREATE_FAILED = 4;
	
	// Smart Copy
	// Indicate whether enable multiple full sessions replication
	private static boolean bReplicateMultipleFullSessions = false;
	// Replication whether requires backup entire machine
	private static boolean bReplicateBackupEntireMachine = false;
	// Maximum snapshot number
	private static int	iReplicateMaximumSnapshotCount = 0;
	// Delete how many snapshots each time
	private static int	iReplicateDeleteSnapshotCount = 0;
	// Smart Copy
	
	private static boolean failoverRebootFlag = false;
	
	//the Hyper-V host name if this machine is a VM and running in a Hyper-V server.
	private static String hostNameOfHypervisor = null;
	private static Boolean isEsxVM;
	
	//empty sting object used to indicate a string has initialized
	private static final String EmptyString = "";
	
	//the information stored in VM running on Hyper-V.
	private final static String HYPERV_INFO_IN_VM_KEY= "Software\\Microsoft\\Virtual Machine\\Guest\\Parameters";
	//host name of Hyper-v in which VM runs
	private static final String HYPERV_HOSTNAME_KEY = "PhysicalHostName";
	
	private final static String ESX_INFO_IN_VM_KEY= "Hardware\\Description\\System\\BIOS";
	private static final String SYSTEM_MANUFACTURER = "SystemManufacturer";
	private static final String SYSTEM_PRODUCT_NAME = "SystemProductName";
	private static final String VMWARE_PRODUCT_MANUFACTURER = "VMware, Inc.";
	private static final String VMWARE_SYSTEM_PRODUCT_NAME = "VMware Virtual Platform";
	
	public static final String VCM_EVENT_HISTORY_DIR = "VCMEventHistory";
	
	public static final String VCM_EVENT_HISTORY_FILE = "VCMEvents";
	
	private static final String PAUSE_HEART_BEAT_4_UPGRADE = "Pause_Heartbeat_For_Upgrade";
	
	private static final String DEBUG_MODE_KEY = "debugMode";
	private static boolean DEBUG_MODE = false;
	private static final String CACHED_LICENSE_VALID_TIME_IN_HOURS_KEY = "cachedLicenseValidTimeInHours";
	private static int CACHED_LICENSE_VALID_TIME_IN_HOURS = 24;
	private static int DEFAULT_CACHED_LICENSE_VALID_TIME_IN_HOURS = 24;
	
	private static String VCM_REG_SUBMIT_INCREMENTAL_FLAG = "SubmitIncrementalFlag";

	static {
		getAllRegistryItems_();
		D2DHAInstallPath = D2DInstallPath;
		loadVirtualConversionPolicy();
		initConfigureValues();
	}

	private static void getAllRegistryItems_() {

		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;

		//get installpath monitor and D2D
		{
			String installPath = "";
			try {

				handle = registry.openKey(D2DInstallPathKey);
				installPath = registry.getValue(handle, "Path");
				registry.closeKey(handle);
				
				if (installPath != null && !installPath.isEmpty()) {
					int len = installPath.length();
					if (installPath.charAt(len - 1) != '\\')
						installPath += "\\";
					D2DInstallPath = installPath;
				}
			} catch (Exception e) {

			}
		}
		
		//get the vddk installed path
		try {
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			VDDKInstallPath = registry.getValue(handle, "VDDKDirectory");
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if(handle != 0)
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
		}
		
		//get failover VM type
		{
			String rebootFlagString = "";
			try {
				handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
				FailoverVM = registry.getValue(handle, FAILOVER_VM_KEY);
				rebootFlagString = registry.getValue(handle, FAILOVER_REBOOT_KEY);
				registry.closeKey(handle);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(FailoverVM==null) 
				FailoverVM = "";
			else 
				FailoverVM = FailoverVM.trim();
			if ("1".equals(rebootFlagString)) 
				failoverRebootFlag = true;
			else 
				failoverRebootFlag = false;
		}

	}
	public static String getProductionServerURL(){
		String key = CommonRegistryKey.getD2DRegistryRoot()+"\\WebService";
		WindowsRegistry registry = new WindowsRegistry();
		try {
			int handle = registry.openKey4Read(key);
			String value = registry.getValue(handle, "URL");
			registry.closeKey(handle);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getProductionServerPort(String URL){
		String value = null;
		try {
			if(URL != null){
				String[] values = URL.split(":");
				value = values[2];
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getProductionServerProtocol(String URL){
		String value = null;
		try {
			if(URL != null){
				String[] values = URL.split(":");
				value = values[0];
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> T unmarshal(String source,Class<T> type) throws JAXBException{
		if(StringUtil.isEmptyOrNull(source)) return null;
		return JAXB.unmarshal(new StringReader(source), type);	
	}
	
	public static <T> T unmarshal(InputStream source,Class<T> type) throws JAXBException{
		if(source == null) return null;
		return JAXB.unmarshal(source, type);	
	}
	
	public static <T> T unmarshal(File source,Class<T> type) throws JAXBException{		
		return JAXB.unmarshal(source, type);	
	}
	public static String marshal(Object script) throws JAXBException{
		if(script == null) return null;
		StringWriter buffer = new StringWriter();
		JAXB.marshal(script, buffer);
		return buffer.toString();
	}
/**
 * 
 * @param vmType
 * @throws Exception
 */
	public static void setFailoverMode(String vmType) throws Exception {

		WindowsRegistry registry = new WindowsRegistry();

		int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		registry.setValue(handle, FAILOVER_VM_KEY, vmType);
		registry.closeKey(handle);

		return;
	}

	public static boolean isFailoverMode(){
	
		if (FailoverVM.isEmpty())
			return false;
		else 
			return true;

	}
	
	public static boolean isFailoverReboot(){
		return failoverRebootFlag;
	}
	
	public static void removeFailoverRebootFlag() throws Exception{
		WindowsRegistry registry = new WindowsRegistry();

		int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		registry.setValue(handle, FAILOVER_REBOOT_KEY, "0");
		registry.closeKey(handle);

		return;
	}
	
	public static void setFailoverOnlineDiskFlag(String flag) throws Exception{
		WindowsRegistry registry = new WindowsRegistry();

		int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		registry.setValue(handle, FAILOVER_ONLINEDISK_KEY, flag);
		registry.closeKey(handle);

	}
	
	public static String getFailoverOnlineDiskFlag() throws Exception{
		String value ="";
		WindowsRegistry registry = new WindowsRegistry();
		int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		value = registry.getValue(handle, FAILOVER_ONLINEDISK_KEY);
		registry.closeKey(handle);

		return value;
	}
	
	public static boolean isNeedOnlineDisk(){
		try {
			String value = getFailoverOnlineDiskFlag();
			if(StringUtil.isEmptyOrNull(value)){
				return true;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		
	}

	public static void saveBackupJobID(long jobID) throws Exception {
		WindowsRegistry registry = new WindowsRegistry();

		int handle;
		handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		registry.setValue(handle, Backkup_JOBID_VALUE, "" + jobID);
		registry.closeKey(handle);

		return;
	}
	public static long getBackupJobID() throws Exception {
		WindowsRegistry registry = new WindowsRegistry();

		int handle;
		handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		String value = registry.getValue(handle, Backkup_JOBID_VALUE);
		registry.closeKey(handle);
		if(value!=null){
			try{
				return Long.parseLong(value);
			}catch(NumberFormatException  ne){
				
			}
		}
		return 0;
	}
	
	public static void saveRepJobID(long jobID) throws Exception {
		WindowsRegistry registry = new WindowsRegistry();

		int handle;
		handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		registry.setValue(handle, REP_JOBID_VALUE, "" + jobID);
		registry.closeKey(handle);

		return;
	}
	public static long getRepJobID() throws Exception {
		WindowsRegistry registry = new WindowsRegistry();

		int handle;
		handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		String value = registry.getValue(handle, REP_JOBID_VALUE);
		registry.closeKey(handle);
		if(value!=null){
			try{
				return Long.parseLong(value);
			}catch(NumberFormatException  ne){
				
			}
		}
		return 0;
	}
	
	public static boolean isRemote(String path) {
		if (path != null) {
			if (path.startsWith("\\\\?\\")) {
				String pattenStr = "^[\\\\]{2}[?]{1}[\\\\]{1}[A-Za-z]{1}[:]{1}.*";
				Pattern localP = Pattern.compile(pattenStr);
				// Pattern remoteP = Pattern
				// .compile("^[\\]{2}[?][\\]{1}U|uN|nC|c\\[^:`~!@#\\$\\^&*()=+[]{}\|;\\'\\\\",<>/?]+[\\]{1}");
				// // "\\?\UNC\server\share"
				// "\\?\D:\<long path>"
				Matcher m = localP.matcher(path);
				if (!m.matches()) {
					return true;
				}
			} else if (path.startsWith("\\\\")) {
				return true;
			}

		}
		return false;
	}
	
	public static String readFileAsString(String filePath) throws Exception{
	    byte[] buffer = new byte[(int) new File(filePath).length()];
	    BufferedInputStream f  = null;
	    try{
	    	f = new BufferedInputStream(new FileInputStream(filePath));
	    	f.read(buffer);
	    }finally{
	    	
	    	if(f!=null){
	    		try{f.close();}catch(Exception e){}
	    	}
	    }
	    
	    return new String(buffer,"utf-8");
	}
	
	public static String getExceptionStackMessage(StackTraceElement[] errorElements){
		
		if(errorElements == null || errorElements.length == 0)
			return "";
		String message = "";
		String tmp = "";
		for(StackTraceElement element : errorElements){
			tmp = element.toString();
			message += tmp + "" + System.getProperty("line.separator");
		}
		return message;
	}
	
	public static String getFailoverJobScript(){
		
		String value = null;	
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle;
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			value = registry.getValue(handle, FAILOVER_JOB_SCRIPT);
			registry.setValue(handle, FAILOVER_JOB_SCRIPT, "");
			registry.closeKey(handle);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
		
	}
	
	public static int getCustomizedVDDKPort(){
		final String VDDK_PORT = "VDDKPort";
		int vddkPort = 443;
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle;
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String value = registry.getValue(handle, VDDK_PORT);
			registry.closeKey(handle);
			vddkPort = Integer.valueOf(value).intValue();
			return vddkPort;
		} catch (Exception e) {
			return vddkPort;
		}
		
	}

	public static void prepareTrustAllSSLEnv() throws NoSuchAlgorithmException,
			KeyManagementException, KeyStoreException {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");

		sc
				.init(
						new javax.net.ssl.X509KeyManager[] {},
						new TrustManager[] { new com.ca.arcflash.webservice.EasyX509TrustManager(
								null) }, new java.security.SecureRandom());

		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());

	}
	public  static synchronized PatchInfo getPatchInfo ()throws Exception
	{
		PatchInfo objPatchInfo = new PatchInfo();
		return objPatchInfo;
	
	}
	public static PatchInfo getPatchInfo(String StatusXmlFilePath) throws Exception
	{
		PatchInfo objPatchInfo = new PatchInfo();
		File file = new File(StatusXmlFilePath);
		if (!file.exists())
		{
			objPatchInfo.setError_Status(PatchInfo.ERROR_NONEW_PATCHES_AVAILABLE);
			return objPatchInfo;
		}
		try
		{
		PMXPathReader objXPathReader = new PMXPathReader(StatusXmlFilePath);

			if(objXPathReader.Initialise() == true)
			{
				// Get  Package ID
				String sXPath = "Product/Release/Package/@Id";
				Object obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				if(obj == null)
				{
					objPatchInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
					return objPatchInfo;
				}
				objPatchInfo.setPackageID(obj.toString());
				//Get Major Version
				sXPath = "/Product/Release/@MajorVersion";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setMajorversion(Integer.parseInt(obj.toString()));
				// Get Minor Version
				sXPath = "/Product/Release/@MinorVersion";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				if(obj.toString().isEmpty()== false)
				{
					objPatchInfo.setMinorVersion(Integer.parseInt(obj.toString()));
				}
				// Get Published Date
				sXPath = "/Product/Release/Package/@PublishedDate";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setPublishedDate(obj.toString());
				// Get  Description
				sXPath = "/Product/Release/Package/Desc"+getSuffix4Language();
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setDescription(obj.toString());
				// Get  Downloadedlocation
				sXPath = "/Product/Release/Package/Downloadedlocation";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setPatchDownloadLocation(obj.toString());

				String strDownloadLocation = obj.toString();
				// Get  PatchURL
				sXPath = getPatchURL();
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setPatchURL(obj.toString());
				sXPath = "/Product/Release/Package/RebootRequired";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setRebootRequired(Integer.parseInt(obj.toString()));
				// Get Patch Size
				sXPath = "/Product/Release/Package/Size";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setSize(Integer.parseInt(obj.toString()));
				// Get UpdateBuild
				sXPath = "/Product/Release/Package/UpdateBuild";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				String[] objString = obj.toString().split("\\.");
				objPatchInfo.setBuildNumber(Integer.valueOf(objString[0]));
				// Get PatchVersionNumber
				sXPath = "/Product/Release/Package/UpdateVersionNumber";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setPatchVersionNumber(Integer.parseInt(obj.toString()));
				// Get  Available Status
				sXPath = "/Product/Release/Package/AvailableStatus";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setAvailableStatus(Integer.parseInt(obj.toString()));
				// Get  DownloadStatus
				sXPath = "/Product/Release/Package/DownloadStatus";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setDownloadStatus(Integer.parseInt(obj.toString()));

				if(objPatchInfo.getDownloadStatus() == 1)
				{
					File downloadFile = new File(strDownloadLocation);
					if(!downloadFile.exists())
					{
						objPatchInfo.setDownloadStatus(0);
					}
				}

				// Get  InstallStatus
				sXPath = "/Product/Release/Package/InstallStatus";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setInstallStatus(Integer.parseInt(obj.toString()));
				// Error Message
				sXPath = "/Product/Release/Package/ErrorMessage";
				obj = objXPathReader.readXPath(sXPath,XPathConstants.STRING);
				objPatchInfo.setErrorMessage(obj.toString());
				if(obj.toString().length()>0)
				{
					objPatchInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
				}
				else
				{
					objPatchInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_SUCCESS);
				}
				return objPatchInfo;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			objPatchInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
			return objPatchInfo;
		}

		objPatchInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
		return objPatchInfo;
	}
	
	//added by cliicy.luo
	public static BIPatchInfo getBIPatchInfo(String StatusXmlFilePath)
	{logger.info("oooo commonUtil BI status xml="+StatusXmlFilePath);
		BIPatchInfo biPInfo = new BIPatchInfo();
		File file = new File(StatusXmlFilePath);
		if (!file.exists())
		{
			biPInfo.setError_Status(PatchInfo.ERROR_NONEW_PATCHES_AVAILABLE);
			return biPInfo;
		}
		try 
		{
		    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		    Document doc = docBuilder.parse (new File(StatusXmlFilePath));

		    // normalize text representation
		    doc.getDocumentElement().normalize();
		    //System.out.println ("Root element of the Status.xml is " + doc.getDocumentElement().getNodeName());
		    NodeList listOfPkgs = null;
		    int iPnode = 0 , itotalPkgs = 0;
		    String sPackageFlag;
		    //get the total number of Packagexx. like Package0 Package1 Package2
		    do {
		    	sPackageFlag = "Package" + Integer.toString(iPnode);
		    	listOfPkgs = doc.getElementsByTagName(sPackageFlag);
		    	itotalPkgs = listOfPkgs.getLength();
		    	if ( itotalPkgs == 0 ) break;
		    	iPnode++;
		    } while (true);
		    //get the total number of Packagexx. like Package0 Package1 Package2
		    biPInfo.aryPatchInfo = new PatchInfo[iPnode] ;
		    iPnode = 0;
		    
		    do {
		    	sPackageFlag = "Package" + Integer.toString(iPnode);
		    	listOfPkgs = doc.getElementsByTagName(sPackageFlag);
		    	itotalPkgs = listOfPkgs.getLength();    		
		    	if ( itotalPkgs == 0 ) break;   	
		    	
				for (int i = 0; i < itotalPkgs ; i++) {
					if ( i >= 1 ) break;//make sure the Package name is not the same, is different. 
					Node firstPatchNode = listOfPkgs.item(i);
					if (firstPatchNode.getNodeType() == Node.ELEMENT_NODE) {
						i = iPnode;
						biPInfo.aryPatchInfo[i] = new PatchInfo();
						// ------- get update ID name
						Element firstElement = (Element) firstPatchNode;
						String sPID = firstElement.getAttribute("Id");
						biPInfo.aryPatchInfo[i].setPackageID(sPID);
	
						// ------- get update PublishedDate
						String sPdate = firstElement.getAttribute("PublishedDate");
						biPInfo.aryPatchInfo[i].setPublishedDate(sPdate.toString());
	
						// System.out.println("Id : " + sPID);
						// System.out.println("PublishedDate : " + sPdate);
	
						// ------- get update patch name
						NodeList firstNameList = firstElement.getElementsByTagName("Update");
						Element firstNameElement = (Element) firstNameList.item(0);
						NodeList textFNList = firstNameElement.getChildNodes();
						String pckname = ((Node) textFNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setPackageUpdateName(pckname);
						// System.out.println("Update Patch Name : " + pckname);
	
						// ------- get Dependency update patch name
						NodeList dyNameList = firstElement.getElementsByTagName("Dependency");
						Element dyNameElement = (Element) dyNameList.item(0);
						NodeList dyNList = dyNameElement.getChildNodes();
						String dyname = ((Node) dyNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setPackageDepy(dyname);
						
						// ------- get Size
						NodeList secNameList = firstElement.getElementsByTagName("Size");
						Element secNameElement = (Element) secNameList.item(0);
						NodeList textSNList = secNameElement.getChildNodes();
						String ssize = ((Node) textSNList.item(0)).getNodeValue().trim();
						int nsize = Integer.parseInt(ssize.toString());
						biPInfo.aryPatchInfo[i].setSize(nsize);
						// System.out.println("Size of Patch : " + ssize);
	
						// ------- get Checksum
						NodeList chsNameList = firstElement
								.getElementsByTagName("Checksum");
						Element chsNameElement = (Element) chsNameList.item(0);
						NodeList chsNList = chsNameElement.getChildNodes();
						String schs = ((Node) chsNList.item(0)).getNodeValue().trim().toString();		
						// System.out.println("Checksum of Patch : " + schs);
	
						// ------- get Downloadedlocation
						NodeList dlocNameList = firstElement.getElementsByTagName("Downloadedlocation");
						Element dlocNameElement = (Element) dlocNameList.item(0);
	
						NodeList dlocNList = dlocNameElement.getChildNodes();
						String sloc = ((Node) dlocNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setPatchDownloadLocation(sloc.toString());
						System.out.println("Downloadedlocation of Patch : " +sloc);
	
						// ------- get DownloadStatus
						NodeList dlsNameList = firstElement.getElementsByTagName("DownloadStatus");
						Element dlsNameElement = (Element) dlsNameList.item(0);
	
						NodeList dlsNList = dlsNameElement.getChildNodes();
						String sls = ((Node) dlsNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setDownloadStatus(Integer.parseInt(sls.toString()));
						biPInfo.setDownloadStatus(Integer.parseInt(sls.toString()));
	
						if (biPInfo.aryPatchInfo[i].getDownloadStatus() == 1) {
							File downloadFile = new File(sloc.toString());
							if (!downloadFile.exists()) {
								biPInfo.aryPatchInfo[i].setDownloadStatus(0);
								biPInfo.setDownloadStatus(0);
							} else {
								biPInfo.aryPatchInfo[i].setDownloadStatus(1);
								biPInfo.setDownloadStatus(1);
							}
						}
						// System.out.println("DownloadStatus of Patch : " + sls);
	
						// ------- get AvailableStatus
						NodeList avsNameList = firstElement
								.getElementsByTagName("AvailableStatus");
						Element avsNameElement = (Element) avsNameList.item(0);
	
						NodeList avsNList = avsNameElement.getChildNodes();
						String savs = ((Node) avsNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setAvailableStatus(Integer.parseInt(savs.toString()));
						biPInfo.setAvailableStatus(Integer.parseInt(savs.toString()));
						// System.out.println("AvailableStatus of Patch : " + savs);
	
						// ------- get UpdateBuild
						NodeList upbNameList = firstElement.getElementsByTagName("UpdateBuild");
						Element upbNameElement = (Element) upbNameList.item(0);
	
						NodeList upbNList = upbNameElement.getChildNodes();
						String supb = ((Node) upbNList.item(0)).getNodeValue().trim();
						String[] supbx = supb.toString().split("\\.");
						biPInfo.aryPatchInfo[i].setBuildNumber(Integer.parseInt(supbx[0]));
						biPInfo.setBuildNumber(Integer.parseInt(supbx[0]));
						// System.out.println("UpdateBuild of Patch : " + supb);
	
						// ------- get UpdateVersionNumber
						NodeList upvNameList = firstElement
								.getElementsByTagName("UpdateVersionNumber");
						Element upvNameElement = (Element) upvNameList.item(0);
	
						NodeList upvNList = upvNameElement.getChildNodes();
						String supv = ((Node) upvNList.item(0)).getNodeValue()
								.trim();
						biPInfo.aryPatchInfo[i].setPatchVersionNumber(Integer
								.parseInt(supv.toString()));
						// System.out.println("UpdateVersionNumber of Patch : " +
						// supv);
	
						// ------- get RebootRequired
						NodeList rbNameList = firstElement
								.getElementsByTagName("RebootRequired");
						Element rbNameElement = (Element) rbNameList.item(0);
	
						NodeList rbNList = rbNameElement.getChildNodes();
						String srb = ((Node) rbNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setRebootRequired(Integer
								.parseInt(srb.toString()));
						// System.out.println("RebootRequired of Patch : " + srb);
	
						// ------- get LastRebootableUpdateVersion
						NodeList lrbNameList = firstElement
								.getElementsByTagName("LastRebootableUpdateVersion");
						Element lrbNameElement = (Element) lrbNameList.item(0);
	
						NodeList lrbNList = lrbNameElement.getChildNodes();
						// System.out.println("LastRebootableUpdateVersion of Patch : "
						// + ((Node)lrbNList.item(0)).getNodeValue().trim());
	
						// ------- get RequiredVersionOfAutoUpdate
						NodeList vaupNameList = firstElement
								.getElementsByTagName("RequiredVersionOfAutoUpdate");
						Element vaupNameElement = (Element) vaupNameList.item(0);
	
						NodeList vaupNList = vaupNameElement.getChildNodes();
						// System.out.println("RequiredVersionOfAutoUpdate of Patch : "
						// + ((Node)vaupNList.item(0)).getNodeValue().trim());
	
						// ------- get InstallStatus
						NodeList itsNameList = firstElement
								.getElementsByTagName("InstallStatus");
						Element itsNameElement = (Element) itsNameList.item(0);
	
						NodeList itsNList = itsNameElement.getChildNodes();
						String sits = ((Node) itsNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setInstallStatus(Integer.parseInt(sits.toString()));
						biPInfo.setInstallStatus(Integer.parseInt(sits.toString()));
						// System.out.println("InstallStatus of Patch : " + sits);
	
						// ------- get Desc
						String sdec = "Desc" + getSuffix4Language();
						NodeList desNameList = firstElement.getElementsByTagName(sdec);
						Element desNameElement = (Element) desNameList.item(0);
	
						NodeList desNList = desNameElement.getChildNodes();
						String sdes = ((Node) desNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setDescription(sdes.toString());
						// System.out.println("Desc of Patch : " + sdes);
						//
						// ------- get UpdateURL
						String surl = "UpdateURL" + getSuffix4Language();
						NodeList urlNameList = firstElement.getElementsByTagName(surl);
						Element urlNameElement = (Element) urlNameList.item(0);
	
						NodeList urlNList = urlNameElement.getChildNodes();
						String surlV = ((Node) urlNList.item(0)).getNodeValue().trim();
						biPInfo.aryPatchInfo[i].setPatchURL(surlV.toString());
						// System.out.println("UpdateURL of Patch : " + surlV);
	
						// Error Message
						NodeList errorMNameList = firstElement.getElementsByTagName("ErrorMessage");
						biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_SUCCESS);
						if (errorMNameList.item(0) != null) {
							Element errorMElement = (Element) errorMNameList.item(0);
	
							NodeList errorMNList = errorMElement.getChildNodes();
							String serrorV = ((Node) errorMNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setErrorMessage(serrorV.toString());
							biPInfo.setErrorMessage(serrorV.toString());
	
							if (serrorV.toString().length() > 0) {
								biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
							} else {
								biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_SUCCESS);
							}
						}
					}
				}//end of for loop with s var
				iPnode++;
		    }while ( true );
		    return biPInfo;
		} catch (Exception err) {
			err.printStackTrace ();
			biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
		}
		biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
	    return biPInfo;
	}
	//added by cliicy.luo
	
	private static String getSuffix4Language() {
		String result="";
		String language = DataFormatUtil.getServerLocale().getLanguage();
		String country = DataFormatUtil.getServerLocale().getCountry();
		if ("ja".equals(language))
			result = "_JPN";
		else if ("fr".equals(language))
			result = "_FRN";
		else if ("de".equals(language))
			result = "_GRM";
		else if ("pt".equals(language))
			result = "_PRB";
		else if ("es".equals(language))
			result = "_SPA";
		else if ("it".equals(language))
			result = "_ITA";
		else if (language.equals("zh")) {
			if (country.equalsIgnoreCase("CN"))
				result = "_CHS";
			else
				result = "_CHT";
		}
		return result;
	}
	private static String getPatchURL() {
		// TODO Auto-generated method stub
		String path;
		Locale locale = Locale.getDefault(Category.FORMAT);
		String language = locale.getLanguage();
		if (language.equals("ja"))
			path = "/Product/Release/Package/UpdateURL_JPN";
		else if (language.equals("de"))
			path = "/Product/Release/Package/UpdateURL_GRM";
		else if (language.equals("it"))
			path = "/Product/Release/Package/UpdateURL_ITA";
		else if (language.equals("es"))
			path = "/Product/Release/Package/UpdateURL_SPA";
		else if (language.equals("fr"))
			path = "/Product/Release/Package/UpdateURL_FRN";
		else if (language.equals("pt"))
			path = "/Product/Release/Package/UpdateURL_PRB";
		else if (language.equals("zh")){
			String country = locale.getCountry();
			if (country.equals("CN") || country.equals("SG"))
				path = "/Product/Release/Package/UpdateURL_CHS";
			else
				path = "/Product/Release/Package/UpdateURL_CHT";
		} else
			path = "/Product/Release/Package/UpdateURL";
		return path;
	}
	
	// This registry key is used to indicate the 1st configuration of VCM setting
	private static final String VCM_FIRST_SETTING = "FirstVirtualConversionSetting";

	public static void setFirstVCMSettingFlag(boolean flag) throws Exception {
		WindowsRegistry registry = new WindowsRegistry();
		
		int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		if (flag == true) {
			registry.setValue(handle, VCM_FIRST_SETTING, "1");
		} else {
			registry.setValue(handle, VCM_FIRST_SETTING, "0");
		}
		registry.closeKey(handle);
	}
	
	public static int getFirstVCMSettingFlag() throws Exception {
		WindowsRegistry registry = new WindowsRegistry();
		
		int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		String value = registry.getValue(handle, VCM_FIRST_SETTING);
		registry.closeKey(handle);
		
		if (value != null && value.equals("1")) {
			return	1;
		}
		
		return 0;
		
	}
	
	// This registry key is used to indicate the 1st configuration of VCM setting
	

	public static void setSubmitIncrementalFlag(String afGuid, String flag, boolean useGUID) throws Exception {
		
		String submitIncrementalKeyString = VCM_REG_SUBMIT_INCREMENTAL_FLAG;
		if (useGUID)
			submitIncrementalKeyString += "_" + afGuid;
		
		WindowsRegistry registry = new WindowsRegistry();
		
		int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		registry.setValue(handle, submitIncrementalKeyString, flag);
		registry.closeKey(handle);
	}
	
	public static String getSubmitIncrementalFlag(String afGuid, boolean useGUID) throws Exception {
		
		String submitIncrementalKeyString = VCM_REG_SUBMIT_INCREMENTAL_FLAG;
		if (useGUID)
			submitIncrementalKeyString += "_" + afGuid;

		WindowsRegistry registry = new WindowsRegistry();
		
		int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		
		String value = registry.getValue(handle, submitIncrementalKeyString);
		
		registry.closeKey(handle);
		
		return value;
		
	}
	public static void clearSubmitIncrementalFlag(String afGuid, boolean useGUID) throws Exception {
		
		String submitIncrementalKeyString = VCM_REG_SUBMIT_INCREMENTAL_FLAG;
		if (useGUID)
			submitIncrementalKeyString += "_" + afGuid;

		WindowsRegistry registry = new WindowsRegistry();
		int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
		
		registry.deleteValue(handle, submitIncrementalKeyString);
		
		registry.closeKey(handle);
	}
	
	
	public static final int getMaxSnapshotCountForVMware() {
		if (iReplicateMaximumSnapshotCount >= MAX_VMWARE_SNAPSHOT) {		
			return MAX_VMWARE_SNAPSHOT;
		} else if (iReplicateMaximumSnapshotCount > 0) {
			return iReplicateMaximumSnapshotCount;
		} else {
			return MAX_VMWARE_SNAPSHOT;
		}
	}
	
	public static final int getMaxSnapshotCountForHyperV() {
		int maxSnapShot;
		
		// For HyperV, we need half this value for Bootable Snapshot
		int maxSnapshotInTheory = MAX_HYPERV_SNAPSHOT/2;
		if (iReplicateMaximumSnapshotCount > 0 && iReplicateMaximumSnapshotCount <= maxSnapshotInTheory) {		
			maxSnapShot = iReplicateMaximumSnapshotCount;
		} else 
			maxSnapShot = maxSnapshotInTheory;
		
		return maxSnapShot;
	}

	public static void loadVirtualConversionPolicy() {
		String	VCM_POLICY_MULTIPLE_FULL_SESSIONS = "ReplicateMultipleFullSessions";
		String	VCM_POLICY_BACKUP_ENTIRE_MACHINE  = "ReplicateRequireBackupEntireMachine";
		String	VCM_POLICY_MAXIMUM_SNAPSHOT_COUNT = "ReplicateMaximumSnapshotCount";
		String	VCM_POLICY_DELETE_SNAPSHOT_COUNT  = "ReplicateDeleteSnapshotCount";
		
		// Set default values
		bReplicateMultipleFullSessions = false;
		bReplicateBackupEntireMachine = true;
		iReplicateMaximumSnapshotCount = 0;
		iReplicateDeleteSnapshotCount = 1;
		
		try {
			WindowsRegistry registry = new WindowsRegistry();
			
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String value;	
			value = registry.getValue(handle, VCM_POLICY_MULTIPLE_FULL_SESSIONS);
			if (value != null && (value.equalsIgnoreCase("True") || value.equals("1"))) {
				bReplicateMultipleFullSessions = true;
			}
			
			value = registry.getValue(handle, VCM_POLICY_BACKUP_ENTIRE_MACHINE);
			if (value != null && (value.equalsIgnoreCase("False") || value.equals("0"))) {
				bReplicateBackupEntireMachine = false;
			} else {
				bReplicateBackupEntireMachine = true;
			}
			
			value = registry.getValue(handle, VCM_POLICY_MAXIMUM_SNAPSHOT_COUNT);
			if (value != null) {
				iReplicateMaximumSnapshotCount = Integer.parseInt(value);
			}
			
			value = registry.getValue(handle, VCM_POLICY_DELETE_SNAPSHOT_COUNT);
			if (value != null) {
				iReplicateDeleteSnapshotCount = Integer.parseInt(value);
				if (iReplicateDeleteSnapshotCount <= 0) {
					iReplicateDeleteSnapshotCount = 1;
				}
			}
			
			registry.closeKey(handle);
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	public static final boolean ifReplicateMultipleFullSessions() {
		return bReplicateMultipleFullSessions;
	}
	
	public static final  boolean ifReplicateRequiresBackupEntireMachine() {
		return bReplicateBackupEntireMachine;
	}
	
	public static final int getCountForDeletingSnapshot() {
		return iReplicateDeleteSnapshotCount;
	}

	public static double roundUp(double num){
		double result = num;
		double tmp = Math.round(result);
		if(result > tmp){
			tmp += 1;
		}
		return tmp;
	}
	
	public static String getRepositoryConfPath(){
		return CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
	}
	
	/**
	 * Return the Hyper-V host name if this machine is a VM and running in a Hyper-V server. 
	 * @return Return the Hyper-V host name if this machine is a VM and running in a Hyper-V server; null otherwise.
	 */
	public static String hyperVHostNameIfVM() {
		if(hostNameOfHypervisor == null) {
			hostNameOfHypervisor = EmptyString;
			
			WindowsRegistry registry = new WindowsRegistry();
			int handle = 0;
			try {
				handle = registry.openKey(HYPERV_INFO_IN_VM_KEY);
				String hyperVHostName = registry.getValue(handle, HYPERV_HOSTNAME_KEY);
				if(!StringUtil.isEmptyOrNull(hyperVHostName))
					hostNameOfHypervisor = hyperVHostName;
			} catch (Exception e) {
				logger.info("Fails to get key " + HYPERV_HOSTNAME_KEY + " in " + HYPERV_INFO_IN_VM_KEY, e);
			} finally {
				if(handle != 0) {
					try {
						registry.closeKey(handle);
					}catch(Exception e) {};
					
				}
			}
			
		}
		
		return hostNameOfHypervisor == EmptyString ? null : hostNameOfHypervisor;
	}
		
	/**
	 * Return the Hyper-V host name if this machine is a VM and running in a Hyper-V server. 
	 * @deprecated as it does not work if the machine is an ESX VM with XP installed 
	 * @return Return the Hyper-V host name if this machine is a VM and running in a Hyper-V server; null otherwise.
	 */
	@Deprecated
	public static Boolean isESXServerVM() {
		if(isEsxVM == null) {
			isEsxVM = Boolean.FALSE;
			
			WindowsRegistry registry = new WindowsRegistry();
			int handle = 0;
			try {
				handle = registry.openKey(ESX_INFO_IN_VM_KEY);
				String manufacturer = registry.getValue(handle, SYSTEM_MANUFACTURER);
				String productName = registry.getValue(handle, SYSTEM_PRODUCT_NAME);
				
				if(!StringUtil.isEmptyOrNull(manufacturer) && !StringUtil.isEmptyOrNull(productName)
					&& manufacturer.toLowerCase().indexOf(VMWARE_PRODUCT_MANUFACTURER.toLowerCase()) >= 0
					&& productName.toLowerCase().indexOf(VMWARE_SYSTEM_PRODUCT_NAME.toLowerCase()) >= 0)
					isEsxVM = Boolean.TRUE;
			} catch (Exception e) {
				logger.info("Fails to get key " + SYSTEM_MANUFACTURER + " and " + SYSTEM_PRODUCT_NAME
						+ " in " + ESX_INFO_IN_VM_KEY, e);
			} finally {
				if(handle != 0) {
					try {
						registry.closeKey(handle);
					}catch(Exception e) {};
				}
			}
		}
		return isEsxVM;
	}
	
	public static String getStringKeyValueFromD2DRoot(String key) {
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String value = registry.getValue(handle, key);
			return value;
		} catch (Exception e) {
			logger.error("Fails to get key " + key + " in " + CommonRegistryKey.getD2DRegistryRoot(), e);
		}
		finally {
			if(handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
				}
			}
		}
		
		return null;
	}
	
	public static String getNewGuestOSName(){
		
		WindowsRegistry registry = new WindowsRegistry();
		int regHandle = 0;
		String osName = "";
		try{
			regHandle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot() + "\\VCM");
			if (regHandle != 0) {
				String automation = registry.getValue(regHandle, "VCMAutomation");
				if ("1".equals(automation)) {
					String newHostnameString = registry.getValue(regHandle,
							"FailoverOsName");
					if (!StringUtil.isEmptyOrNull(newHostnameString)) {
						osName = newHostnameString;
					}
				}
			}
		}catch(Exception e){
			
		}finally{
			if(regHandle != 0){
				try {
					registry.closeKey(regHandle);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		
		return osName;
		
	}
	
	public static boolean isPrintScriptEnabled(){
	
		WindowsRegistry registry = new WindowsRegistry();
		int regHandle = 0;
		try{
			regHandle = registry.openKey(CommonRegistryKey.getVSBRegistryRoot());
			if (regHandle != 0) {
				String print = registry.getValue(regHandle, "PrintJobScript");
				return "1".equals(print);
			}
			return false;
		}catch(Exception e){
			return false;
		}finally{
			if(regHandle != 0){
				try {
					registry.closeKey(regHandle);
				} catch (Exception e) {
				}
			}
		}
	}
	
	public static void setD2DUpgradeFlag(String flag) throws Exception {
		
		WindowsRegistry registry = new WindowsRegistry();
		String key = CommonRegistryKey.getD2DRegistryRoot() + "\\Version";
		int handle = 0;
		try {
			handle = registry.openKey(key);
			if(handle == 0){
				handle = registry.createKey(key);
			}
			registry.setValue(handle, PAUSE_HEART_BEAT_4_UPGRADE, flag);	
		}finally {
			if(handle != 0) {
				try {
					registry.closeKey(handle);
				}catch(Exception e) {};
			}
		}
	}
	
	public static String getD2DUpgradeFlag() throws Exception {
		
		WindowsRegistry registry = new WindowsRegistry();
		String key = CommonRegistryKey.getD2DRegistryRoot() + "\\Version";
		int handle = 0; 
		try {
			handle = registry.openKey(key);
			String value =  registry.getValue(handle, PAUSE_HEART_BEAT_4_UPGRADE);
			return value;
		}finally {
			if(handle != 0) {
				try {
					registry.closeKey(handle);
				}catch(Exception e) {};
			}
		}
	}

	private static void initConfigureValues() {
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try {
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String debugMode = registry.getValue(handle, DEBUG_MODE_KEY);
			if (!StringUtil.isEmptyOrNull(debugMode)) {
				debugMode = debugMode.trim();
				if ("1".equalsIgnoreCase(debugMode) || "yes".equalsIgnoreCase(debugMode)
						|| "true".equalsIgnoreCase(debugMode)) {
					DEBUG_MODE = true;
				}
			}
		} catch (Exception e) {
			logger.error("Read registry for key:" + DEBUG_MODE_KEY + " failed.", e);
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
					logger.error("Close registry key failed.", e);
				}
			}
		}
		CACHED_LICENSE_VALID_TIME_IN_HOURS = getCachedLicenseValidInHourFromRegistry();
	}

	private static int getCachedLicenseValidInHourFromRegistry() {
		WindowsRegistry registry = new WindowsRegistry();
		int cachedLicenseValidTimeInHours = DEFAULT_CACHED_LICENSE_VALID_TIME_IN_HOURS;
		int handle = 0;
		try {
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String strCachedLicenseValidTimeInHours = registry.getValue(handle, CACHED_LICENSE_VALID_TIME_IN_HOURS_KEY);
			if (!StringUtil.isEmptyOrNull(strCachedLicenseValidTimeInHours)) {
				try {
					cachedLicenseValidTimeInHours = Integer.parseInt(strCachedLicenseValidTimeInHours);
				} catch (Exception e) {
					logger.error("The value:" + strCachedLicenseValidTimeInHours + " in rgistry for key:"
							+ CACHED_LICENSE_VALID_TIME_IN_HOURS_KEY + " is not value.", e);
					cachedLicenseValidTimeInHours = DEFAULT_CACHED_LICENSE_VALID_TIME_IN_HOURS;
				}
			}
		} catch (Exception e) {
			logger.error("Read registry for key:" + CACHED_LICENSE_VALID_TIME_IN_HOURS_KEY + " failed.", e);
		} finally {
			if (handle != 0) {
				try {
					registry.closeKey(handle);
				} catch (Exception e) {
					logger.error("Close registry key failed.", e);
				}
			}
		}
		if (cachedLicenseValidTimeInHours <= 0) {
			cachedLicenseValidTimeInHours = DEFAULT_CACHED_LICENSE_VALID_TIME_IN_HOURS;
		}
		return cachedLicenseValidTimeInHours;
	}

	public static int getCachedLicenseValidInHour() {
		if (!DEBUG_MODE) {
			return CACHED_LICENSE_VALID_TIME_IN_HOURS;
		} else {
			return getCachedLicenseValidInHourFromRegistry();
		}
	}
	
	public static boolean isGuestOSLinux(String VmGuestOS) {
		if(VmGuestOS == null || VmGuestOS.isEmpty()) {
			return false;
		}
		VmGuestOS = VmGuestOS.toLowerCase();
		
		if(VmGuestOS.contains("linux") || VmGuestOS.contains("centos") || VmGuestOS.contains("asianux")) {
			return true;
		}

		return false;
	}
	
	public static void removeFailoverTag() {
		WindowsRegistry registry = new WindowsRegistry();
		try {
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			
			registry.deleteValue(handle, FAILOVER_VM_KEY);
			logger.info("Delete registry value " + FAILOVER_VM_KEY);

			registry.deleteValue(handle, FAILOVER_JOB_SCRIPT);
			logger.info("Delete registry value " + FAILOVER_JOB_SCRIPT);

			registry.deleteValue(handle, FAILOVER_REBOOT_KEY);
			logger.info("Delete registry value " + FAILOVER_REBOOT_KEY);
			
			registry.deleteValue(handle, FAILOVER_ONLINEDISK_KEY);
			logger.info("Delete registry value " + FAILOVER_ONLINEDISK_KEY);
			
			registry.closeKey(handle);
		} catch (Exception e) {
			logger.error("Failed to delete registry value");
			logger.error(e);
		}
	}
	
	protected static final int RETRY_TIMES = 20;
	protected static final int RETRY_INTERVAL = 500;
	
	public static boolean tryDeleteFile(File fileToDelete) throws Exception {
		for(int i = 0; i < RETRY_TIMES; i ++) {
			if(fileToDelete.delete()) {
				return true;
			}
			Thread.sleep(RETRY_INTERVAL);
		}
		return false;
	}
	
	public static String detailString(Object o) {

		java.lang.reflect.Field[] fields = o.getClass().getDeclaredFields();

		StringBuffer buffer = new StringBuffer();
		buffer.append(o.getClass().getCanonicalName() + ".detailString[ ");
		for (java.lang.reflect.Field f : fields) {
			try {
				f.setAccessible(true);
				buffer.append(f.getName() + "=" + f.get(o) + ", ");
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		buffer.append(" ]");

		return buffer.toString();
	}
	
}
