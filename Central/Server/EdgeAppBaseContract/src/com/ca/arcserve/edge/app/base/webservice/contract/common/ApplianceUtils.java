package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Properties;

import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcserve.edge.app.base.util.CommonUtil;

public class ApplianceUtils {
	public final static String APPLIANCEFILE = "ApplianceDefaultSetting.properties" ;
	public final static String APPLIANCEFACTORYRESETFILE = "FactoryReset.properties" ;
	public final static String Appliance_Original_Version_Key = "original_version";
	public final static String Appliance_Config_Flag_Key = "isConfigFinished";
	public final static String Appliance_DS_BAUPDESTINATION_Key = "backupDestination";
	public final static String Appliance_DS_CONCURRENT_NODES_Key = "concurrentActiveNodes";
	public final static String Appliance_DS_DATA_DESTINATION_Key = "dataDestination";
	public final static String Appliance_DS_INDEX_DESTINATION_KEY = "indexDestination";
	public final static String Appliance_DS_HASH_DESTINATION_KEY = "hashDestination";
	public final static String Appliance_DS_HASH_ONSSD_KEY = "hashDestinationOnSSD";
	public final static String Appliance_DS_DEDUP_BLOCK_SIZE_KEY = "deduplicationBlockSize";
	public final static String Appliance_DS_HASH_MEMORY_KEY = "hashMemoryAllocation";
	public final static String Appliance_ARCSERVE_BACKUP_PATH_KEY = "arcserveBackupPath";
	public final static String Appliance_LINUX_SERVER_NAME_KEY = "linuxServerName";
	public final static String Appliance_ASBU_PASSWORD_KEY = "ArcserveBackup";
	public final static String Appliance_Another_Console_Url_Key="AnotherConsoleUrl";
	
	public final static String APPLIANCE_LOCAL_USERNAME = "appliance.local.username";
	public final static String APPLIANCE_LOCAL_PASSWORD = "appliance.local.password";
	
	private final static String APPLIANCE_REGISTRY_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
	private final static String APPLIANCE_REGISTRY_FLAG_VALUE = "Arcserve Appliance";
	
	
	private static final String INI_FILE_ENCODING = "UTF-8";
	public static Properties loadApplianceSetting() throws Exception {
		String configFolder = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement);
		String applianceFilePath = configFolder+APPLIANCEFILE;
		return loadPropertiesFromFile(applianceFilePath);
	}
	
	public static void changeWizardFinishedForAppliance() throws Exception {
		String configFolder = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement);
		String applianceFilePath = configFolder+APPLIANCEFILE;
		Properties properties = loadPropertiesFromFile(applianceFilePath);
			
		PrintWriter out = new PrintWriter(applianceFilePath,INI_FILE_ENCODING);
		out.println(Appliance_DS_BAUPDESTINATION_Key+"="+ properties.getProperty(Appliance_DS_BAUPDESTINATION_Key));
		out.println(Appliance_DS_CONCURRENT_NODES_Key+"=" + properties.getProperty(Appliance_DS_CONCURRENT_NODES_Key));
		out.println(Appliance_DS_DATA_DESTINATION_Key+"=" + properties.getProperty(Appliance_DS_DATA_DESTINATION_Key) );// for now, always set this to Yes 
		out.println(Appliance_DS_INDEX_DESTINATION_KEY+"="+properties.getProperty(Appliance_DS_INDEX_DESTINATION_KEY));
		out.println(Appliance_DS_HASH_DESTINATION_KEY+"="+properties.getProperty(Appliance_DS_HASH_DESTINATION_KEY));
		out.println(Appliance_DS_HASH_ONSSD_KEY+"="+properties.getProperty(Appliance_DS_HASH_ONSSD_KEY));
		out.println(Appliance_DS_HASH_MEMORY_KEY+"="+properties.getProperty(Appliance_DS_HASH_MEMORY_KEY));
		out.println(Appliance_DS_DEDUP_BLOCK_SIZE_KEY+"="+properties.getProperty(Appliance_DS_DEDUP_BLOCK_SIZE_KEY));
		out.println(Appliance_ARCSERVE_BACKUP_PATH_KEY+"="+properties.getProperty(Appliance_ARCSERVE_BACKUP_PATH_KEY));
		out.println(Appliance_LINUX_SERVER_NAME_KEY+"="+properties.getProperty(Appliance_LINUX_SERVER_NAME_KEY));
		out.println(Appliance_ASBU_PASSWORD_KEY+"="+properties.getProperty(Appliance_ASBU_PASSWORD_KEY));
		out.println("isConfigFinished=true");
		out.close();
	}
	
	public static void storeAnotherConsoleUrl(String anotherConsoleUrl) throws Exception {
		String configFolder = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement);
		String applianceFilePath = configFolder+APPLIANCEFILE;
		Properties properties = loadPropertiesFromFile(applianceFilePath);
			
		PrintWriter out = new PrintWriter(applianceFilePath,INI_FILE_ENCODING);
		out.println(Appliance_DS_BAUPDESTINATION_Key+"="+ properties.getProperty(Appliance_DS_BAUPDESTINATION_Key));
		out.println(Appliance_DS_CONCURRENT_NODES_Key+"=" + properties.getProperty(Appliance_DS_CONCURRENT_NODES_Key));
		out.println(Appliance_DS_DATA_DESTINATION_Key+"=" + properties.getProperty(Appliance_DS_DATA_DESTINATION_Key) );// for now, always set this to Yes 
		out.println(Appliance_DS_INDEX_DESTINATION_KEY+"="+properties.getProperty(Appliance_DS_INDEX_DESTINATION_KEY));
		out.println(Appliance_DS_HASH_DESTINATION_KEY+"="+properties.getProperty(Appliance_DS_HASH_DESTINATION_KEY));
		out.println(Appliance_DS_HASH_ONSSD_KEY+"="+properties.getProperty(Appliance_DS_HASH_ONSSD_KEY));
		out.println(Appliance_DS_HASH_MEMORY_KEY+"="+properties.getProperty(Appliance_DS_HASH_MEMORY_KEY));
		out.println(Appliance_DS_DEDUP_BLOCK_SIZE_KEY+"="+properties.getProperty(Appliance_DS_DEDUP_BLOCK_SIZE_KEY));
		out.println(Appliance_ARCSERVE_BACKUP_PATH_KEY+"="+properties.getProperty(Appliance_ARCSERVE_BACKUP_PATH_KEY));
		out.println(Appliance_LINUX_SERVER_NAME_KEY+"="+properties.getProperty(Appliance_LINUX_SERVER_NAME_KEY));
		out.println(Appliance_ASBU_PASSWORD_KEY+"="+properties.getProperty(Appliance_ASBU_PASSWORD_KEY));
		out.println(Appliance_Config_Flag_Key+"="+properties.getProperty(Appliance_Config_Flag_Key));
		out.println(Appliance_Another_Console_Url_Key+"="+anotherConsoleUrl);
		out.close();
	}
	
	
	public static Properties loadPropertiesFromFile(String filePath) throws Exception {
		BufferedReader reader = null ;
		try {
			reader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(filePath)));
			String filtedString = filterEscapedChars(reader);
			StringReader filteredReader = new StringReader(filtedString);
			Properties properties = new Properties();
			properties.load(filteredReader);
			return properties;
		}finally{
			if (reader != null)
				reader.close();
		}
	}
	
	public static String filterEscapedChars(BufferedReader reader)throws Exception{
		String str = null;
		StringBuilder resultBuilder = new StringBuilder();
	    while((str = reader.readLine()) != null){
	    	if(str.contains("\\")){
	    		str = str.replace("\\", "\\\\");
	    	}
	    	resultBuilder.append(str);
	    	resultBuilder.append('\n');
	    }
		return resultBuilder.toString();
	}
	
	public static boolean isAppliance(){
		String value = null;
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(APPLIANCE_REGISTRY_KEY);
			value = registry.getValue(handle, APPLIANCE_REGISTRY_FLAG_VALUE);
			registry.closeKey(handle);
			if(value == null)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean applianceConfigFinished()throws Exception{
		if(!isAppliance())
			return true;
		Properties properties = ApplianceUtils.loadApplianceSetting();
		String value = String.valueOf(properties.getProperty(ApplianceUtils.Appliance_Config_Flag_Key));
		if(value.equalsIgnoreCase("true")||value.endsWith("1")){
			return true;
		}
		return false;
	}
	
	public static String getArcserveBackupInstallPath()throws Exception{
		Properties properties = ApplianceUtils.loadApplianceSetting();
		String arcserveBackupPath = properties.getProperty(ApplianceUtils.Appliance_ARCSERVE_BACKUP_PATH_KEY);
		return arcserveBackupPath;
	}
	
	public static String getLinuxServerName()throws Exception{
		Properties properties = ApplianceUtils.loadApplianceSetting();
		String linuxServerName = properties.getProperty(ApplianceUtils.Appliance_LINUX_SERVER_NAME_KEY);
		return linuxServerName;
	}
	
	public static String getASBUPassword()throws Exception{
		Properties properties = ApplianceUtils.loadApplianceSetting();
		String asbuPwd = properties.getProperty(ApplianceUtils.Appliance_ASBU_PASSWORD_KEY);
		return asbuPwd;
	}
	
	public static String getOriginalApplianceVersion(){
		try {
			Properties properties = ApplianceUtils.loadPropertiesFromFile(CommonUtil.BaseEdgeInstallPath+"BIN\\Appliance\\Configuration\\"+ APPLIANCEFACTORYRESETFILE);
			String result = String.valueOf(properties.getProperty(ApplianceUtils.Appliance_Original_Version_Key));
			if(result.compareToIgnoreCase("null") == 0)
				return null;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
