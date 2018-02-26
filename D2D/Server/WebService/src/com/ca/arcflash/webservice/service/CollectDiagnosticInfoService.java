package com.ca.arcflash.webservice.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.jobqueue.encrypt.Base64;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.internal.DiagInfoCollectorConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class CollectDiagnosticInfoService {
	
	private static final Logger logger = Logger.getLogger(CollectDiagnosticInfoService.class);
	private static final CollectDiagnosticInfoService instance = new CollectDiagnosticInfoService();
	private static final String logCollectorUtilityPath = ServiceContext.getInstance().getBinFolderPath() + "\\DiagnosticUtility";
	private static final File f = new File(logCollectorUtilityPath);
	

	public static CollectDiagnosticInfoService getInstance() {
		return instance;
	}

	public int collectDiagnosticInfo(DiagInfoCollectorConfiguration config)  {
		try{
			String msg = String.format(WebServiceMessages.getResource("DiagUtilityExecStart"));
			new NativeFacadeImpl().addLogActivity(
					Constants.AFRES_AFALOG_INFO,
					Constants.AFRES_AFJWBS_GENERAL,
					new String[] { msg, "", "",	"", "" });
			
			/*
			 * Step0: get the required informaiton
			 * a. Occupied size of Datastore/shared path
			   b. Free size on the destination
			   c. Output of Dir /S command
				 */
			if(BackupService.getInstance().getBackupConfiguration()!=null)
				createLogFileForAgentDestInfo();
			else{
				logger.warn("Seems the env is not getting protected as Agent");
			}
			
			List<String> vmInstanceUUIDs = getVMInstanceUUIDs();
			if(vmInstanceUUIDs!=null && vmInstanceUUIDs.size()>0){
				createLogFileForVMsDestInfo(vmInstanceUUIDs);
			}else{
				logger.warn("Seems the env is NOT configured as a proxy for VMs.");
			}
						
			//Step1: Create the persistent object (xml) from the given object
			if(config!=null){
				//save the password as encrypted
				/*String originalPwd = config.getPassword();
				if(originalPwd!=null && !originalPwd.isEmpty()){
					config.setPassword(Base64.encode(originalPwd));
				}*/
				DiagInfoCollectorConfigurationXMLDAO xmlDao = new DiagInfoCollectorConfigurationXMLDAO();
				xmlDao.save(ServiceContext.getInstance().getDiagInfoCollectorConfigurationFilePath(), config);
			}
			
			//Step2: Call the given utility
			String logCollectorUtilityBatch = logCollectorUtilityPath + "\\arcserveAgentSupport.bat";
			logCollectorUtilityBatch = "\"" + logCollectorUtilityBatch + "\"";
			if(config!=null){
				logger.warn("The config is not null hence sending xml as argument");
				logCollectorUtilityBatch = logCollectorUtilityBatch +  " -xmlConfig "  + "\"" + ServiceContext.getInstance().getDiagInfoCollectorConfigurationFilePath() + "\"";
				logCollectorUtilityBatch = "\"" + logCollectorUtilityBatch + "\"";
			}
			
			//org code: String cmd= "cmd /c \"" + logCollectorUtilityBatch + "\"";
			
			String cmd= "cmd /c " + logCollectorUtilityBatch ;
			
			int iResult = executeCmd(cmd,null,f,"Y", true);
			
	    	//Step3: Activity log message with the return value. 
			
			if(iResult==0){
				msg = String.format(WebServiceMessages.getResource("DiagUtilityExecSuccess", config.getUploadDestination()));
				new NativeFacadeImpl().addLogActivity(
						Constants.AFRES_AFALOG_INFO,
						Constants.AFRES_AFJWBS_GENERAL,
						new String[] { msg, "", "",	"", "" });
			}
			else if(iResult==2){
				msg = String.format(WebServiceMessages.getResource("DiagUtilityExecSuccessButFailedToCopyToNWshare", config.getUploadDestination()));
				new NativeFacadeImpl().addLogActivity(
						Constants.AFRES_AFALOG_WARNING,
						Constants.AFRES_AFJWBS_GENERAL,
						new String[] { msg, "", "",	"", "" });
			}
			else{
				logger.info("the return value of running the arcserveAgentSupport.bat file is: " + iResult);
				msg = String.format(WebServiceMessages.getResource("DiagUtilityExecFail", config.getUploadDestination()));
				new NativeFacadeImpl().addLogActivity(
						Constants.AFRES_AFALOG_ERROR,
						Constants.AFRES_AFJWBS_GENERAL,
						new String[] { msg, "", "",	"", "" });
			}
	    	return iResult;
	    } catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}  
	}
	
	public DiagInfoCollectorConfiguration getDiagInfoFromXml()
	{
		logger.info("Entering into DiagInfoCollectorConfiguration getDiagInfoFromXml");
		DiagInfoCollectorConfiguration config = null;
		try {
			DiagInfoCollectorConfigurationXMLDAO xmlDao = new DiagInfoCollectorConfigurationXMLDAO();
			WindowsRegistry registry = new WindowsRegistry();
			int handle = 0;
			String REGISTRY_KEY_PATH			=	"Path";
			handle = registry.openKey("SOFTWARE\\Arcserve\\Unified Data Protection\\Management");
			String homeFolder = registry.getValue(handle, REGISTRY_KEY_PATH);
						
			//config = xmlDao.get("C:\\Program Files\\Arcserve\\Unified Data Protection\\Engine\\BIN\\DiagnosticUtility\\DiagInfoCollectorConfig.xml");
			config = xmlDao.get(homeFolder + "BIN\\DiagnosticUtility\\DiagInfoCollectorConfig.xml");
			
			String EecryptedPwd = config.getPassword();
			if(EecryptedPwd!=null && !EecryptedPwd.isEmpty()){
				config.setPassword(Base64.decode(EecryptedPwd));
			}
			
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return config;
	}
	private int executeCmd(String cmd, String[] envp, File dir, String input, boolean ignoreInputErrStreams) throws IOException, InterruptedException 
	{
		Runtime rn = Runtime.getRuntime();   
		Process process = null;   
	    
    	logger.info("ExeCMD: "+ cmd + ", dir: " + dir.getAbsolutePath());
    	
    	process = rn.exec(cmd, envp, dir );
    	
    	OutputStream objOutput = process.getOutputStream();
	    
		if(input!=null && !input.isEmpty()){
		     objOutput.write(input.getBytes());
		     objOutput.flush();
		     objOutput.close();   
		}
		
		BufferedReader cmdInput = new BufferedReader(new InputStreamReader(process
				.getInputStream()));
		
		BufferedReader cmdError = new BufferedReader(new InputStreamReader(process
				.getErrorStream()));
		
		if(ignoreInputErrStreams){
			try{
				cmdInput.close();
				cmdError.close();
			}catch(IOException e){
				logger.error("Exception while closing the process input, error streams");
				logger.error(e);
			}
		}
		
		else{

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
				//return null;
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
    	process=null;
    	return iResult;
    }
	
	public String executeCmd(String cmd) {
		Process p = null;
		BufferedReader cmdInput = null;
		BufferedReader cmdError = null;
		String result = null;
		StringBuffer cmdOutputbuffer = new StringBuffer();
		StringBuffer cmdErrorbuffer = new StringBuffer();
		try {
			
			p = Runtime.getRuntime().exec(cmd);
			
			cmdInput = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			
			cmdError = new BufferedReader(new InputStreamReader(p
					.getErrorStream()));

			String cmdout = "";

			try {

				// Read command output and storing into string buffer
				while ((cmdout = cmdInput.readLine()) != null) {
					cmdOutputbuffer.append(cmdout);
					cmdOutputbuffer.append("\n");
				}

			} catch (Exception err) {
				System.out
						.println("some exception in reading cmd output" + err);
				return null;
			}

			try {
				// Read command output and storing into string buffer
				while ((cmdout = cmdError.readLine()) != null) {
					cmdErrorbuffer.append(cmdout);
					cmdErrorbuffer.append("\n");
				}
			} catch (Exception error) {
				System.out.println("some exception in reading cmd output error"
						+ error);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result = cmdOutputbuffer.toString() + "\n" + cmdErrorbuffer.toString();
		return result;
	}


	private void createLogFileForVMsDestInfo(List<String> vmInstanceUUIDs) throws IOException, InterruptedException {
		for(String vmInstanceUUID: vmInstanceUUIDs){
			createLogFileForVMDestInfo(vmInstanceUUID);
		}
	}
	
	private void createLogFileForVMDestInfo(String vmInstanceUUID) throws IOException, InterruptedException 
	{
		VirtualMachine vm = new VirtualMachine();
		vm.setVmInstanceUUID(vmInstanceUUID);
		
		VMBackupConfiguration vmBackupConfig;
		try {
			vmBackupConfig = VSphereService.getInstance().getVMBackupConfiguration(vm);
			
			if(vmBackupConfig==null){
				logger.warn("The vm: " + vmInstanceUUID + " backup configuration came as NULL");
				return;
			}
				
			long totalFreeSize = VSphereService.getInstance().getBackupInformationSummary(vm).getDestinationCapacity().getTotalFreeSize();
			long totalVolumeSize = VSphereService.getInstance().getBackupInformationSummary(vm).getDestinationCapacity().getTotalVolumeSize();
			
			String dest = vmBackupConfig.getBackupVM().getDestination();
			String destUserName = vmBackupConfig.getUserName();
			String destPassword = vmBackupConfig.getPassword();
			
			createLogFileForDestInfo(dest, destUserName, destPassword, totalFreeSize, totalVolumeSize, vmInstanceUUID, true);
		} catch (ServiceException e) {
			logger.error("Problem in collecting the destination info for the vm: " + vmInstanceUUID + ". Exception occurred.");
			logger.error(e);
			
			String msg = String.format(WebServiceMessages.getResource("DestInfoCollectFailForVM"), vmInstanceUUID);
			new NativeFacadeImpl().addLogActivity(
					Constants.AFRES_AFALOG_WARNING,
					Constants.AFRES_AFJWBS_GENERAL,
					new String[] { msg, "", "",	"", "" });
		}
	}
	
	private void createLogFileForDestInfo(String dest, String destUserName, String destPassword, long totalFreeSize, long totalVolumeSize, String vmInstanceUUID, boolean isVM) throws IOException, InterruptedException
	{	
		//String parent = new File(dest).getParent(); // This is commented as the dir /s is taking long time at DS level
		logger.info("Dest is: " + dest /*+ ", parent: " + parent*/);
		String nwPath = null;
		Lock lock = null;
		try {
		String logFolderPath = ServiceContext.getInstance().getLogFolderPath();
		//String getFolderInfoCmd = ServiceContext.getInstance().getBinFolderPath() + "\\DiagnosticUtility\\getFolderSubPathsInfo.bat \"" + dest + "\" " + totalFreeSize + " " + totalVolumeSize + " " + vmInstanceUUID + " " + logFolderPath;
		//String getFolderInfoCmd = "\"" + ServiceContext.getInstance().getBinFolderPath() + "\\DiagnosticUtility\\getFolderSubPathsInfo.bat\" \"" + dest + "\" " + totalFreeSize + " " + totalVolumeSize + " " + vmInstanceUUID + " \"" + logFolderPath + "\"";
		
		/*String getFolderInfoCmd = "\"C:\\Program Files\\Arcserve\\Unified Data Protection\\Engine\\BIN\\DiagnosticUtility\\getFolderSubPathsInfo.bat\" " + "\"C:\\dest\\JAYSA02-E7440\" " +
		"50839162880 213800448000 JAYSA02-E7440 \"C:\\Program Files\\Arcserve\\Unified Data Protection\\Engine\\Logs\"", null, 
		new File("C:\\Program Files\\Arcserve\\Unified Data Protection\\Engine\\BIN\\DiagnosticUtility"*/
		
				
				
		String getFolderInfoCmd = logCollectorUtilityPath + "\\getFolderSubPathsInfo.bat";
		
		
		
		//String cmd= "cmd /c " + logCollectorUtilityBatch;
		String cmd= "\"" + getFolderInfoCmd + "\" \"" + dest + "\" " + totalVolumeSize + " " + totalFreeSize + " " + vmInstanceUUID + " \"" + logFolderPath + "\"";
		
		if(dest.startsWith("\\\\")){
			
				// get lock and create connection
				lock = RemoteFolderConnCache.getInstance().getLockByPath(dest);
				if (lock != null) {
					lock.lock();
				}
				logger.info("Lock to destination " + dest);
				long ret = -1;
				for (int i = 0; i < 5; i++) {
					try {
						if (destUserName == null)
							destUserName = "";
						if (destPassword == null)
							destPassword = "";
						ret = new NativeFacadeImpl().NetConn(destUserName, destPassword, dest);
						break;
					} catch (ServiceException se) {
						logger.error("Net connetion error " + se.getMessage());
					}
				}
				if (ret != 0) {
					logger.error("Failed to connect to backup destination.");
					return;
				}
				
				//create network share
				nwPath = createNetworkShare(dest, destUserName, destPassword);
	
				if(nwPath == null)
				{
					logger.error("Failed to create network share to backup destination: " + dest);
					
					String msg = String.format(WebServiceMessages.getResource("DestInfoCollectFail"));
					
					if(isVM)
						msg = String.format(WebServiceMessages.getResource("DestInfoCollectFailForVM"), vmInstanceUUID);
					
					
					new NativeFacadeImpl().addLogActivity(
								Constants.AFRES_AFALOG_WARNING,
								Constants.AFRES_AFJWBS_GENERAL,
								new String[] { msg, "", "",	"", "" });
					
					return;
				}
				
				logger.info("Network path is: " + nwPath);
				
				
				
				//run the batch file
				//getFolderInfoCmd = "\"" + ServiceContext.getInstance().getBinFolderPath() + "\\DiagnosticUtility\\getFolderSubPathsInfo.bat\" " + nwPath + " " + totalFreeSize + " " + totalVolumeSize + " " + vmInstanceUUID + " \"" + logFolderPath + "\"";
				cmd= "\"" + getFolderInfoCmd + "\" \"" + nwPath + ":\" " + totalVolumeSize + " " + totalFreeSize + " " + vmInstanceUUID + " \"" + logFolderPath + "\"";
			}
			cmd = "cmd /c " + "\"" + cmd + "\"";
			logger.info("getFolderInfoCmd is: " + cmd);
			
			int iResult = executeCmd(cmd, null, f,null, false);
			
			if(nwPath!=null)
				clearNetworkShares(nwPath);
			
			
	    	//Step3: Activity log message with the return value. 
			if(isVM){
				if(iResult==0){
					String msg = String.format(WebServiceMessages.getResource("DestInfoCollectSuccessForVM"), vmInstanceUUID);
					new NativeFacadeImpl().addLogActivity(
							Constants.AFRES_AFALOG_INFO,
							Constants.AFRES_AFJWBS_GENERAL,
							new String[] { msg, "", "",	"", "" });
				}
				
				else{
					logger.info("the return value of running the getFolderSubPathsInfo.bat file is: " + iResult);
					String msg = String.format(WebServiceMessages.getResource("DestInfoCollectFailForVM"), vmInstanceUUID);
					new NativeFacadeImpl().addLogActivity(
							Constants.AFRES_AFALOG_WARNING,
							Constants.AFRES_AFJWBS_GENERAL,
							new String[] { msg, "", "",	"", "" });
				}
			}
			else{
				if(iResult==0){
					String msg = String.format(WebServiceMessages.getResource("DestInfoCollectSuccess"));
					new NativeFacadeImpl().addLogActivity(
							Constants.AFRES_AFALOG_INFO,
							Constants.AFRES_AFJWBS_GENERAL,
							new String[] { msg, "", "",	"", "" });
				}
				
				else{
					logger.info("the return value of running the getFolderSubPathsInfo.bat file is: " + iResult);
					String msg = String.format(WebServiceMessages.getResource("DestInfoCollectFail"));
					new NativeFacadeImpl().addLogActivity(
							Constants.AFRES_AFALOG_WARNING,
							Constants.AFRES_AFJWBS_GENERAL,
							new String[] { msg, "", "",	"", "" });
				}
			}
		
		} finally {
			if (lock != null) {
				lock.unlock();
				logger.debug("unlock destination " + dest);
			}
			try {
				new NativeFacadeImpl().disconnectRemotePath(dest, "", destUserName,
						destPassword, false);
			} catch (Exception e) {
				logger.debug("Failed to cut connection to " + dest);
			}
		}
	}
	
	
	public int clearNetworkShares(String networkShareLables) {
		if(networkShareLables==null){
			logger.warn("no need to clear network share as the network lable came as NULL");
			return 1;
		}
			
		String[] networkShares = networkShareLables.split(",");
		boolean shareDelFailed = false;
		if (networkShareLables.equalsIgnoreCase("all") == true) {
			String cmd = "net use /DELETE * /Y";
			String result = executeCmd(cmd);
			
			if (result.toLowerCase().indexOf("success") == -1
					&& result.toLowerCase().indexOf(
							"there are no entries in the list") == -1) {
				return 0;
			}

			return 1;
		} else {
			for (int i = 0; i < networkShares.length; i++) {
				if (networkShares[i].trim().endsWith(":") == false) {
					networkShares[i] += ":";
				}

				String cmd = "net use /DELETE " + networkShares[i] + " /Y";
				//System.out.println("Going to delete the network share using the command: " + cmd);
				logger.debug("Going to delete the network share using the command: " + cmd);
				// String result = executecmd(cmd);
				String result = executeCmd(cmd);
				
				if ((result.toLowerCase().indexOf("success") == -1) && (result.toLowerCase().indexOf("successfully.") == -1)) {
					logger.error("Not able to delete share "
							+ networkShares[i]);
					shareDelFailed = true;
				}
			}
			if (shareDelFailed) {
				return 0;
			} else {
				return 1;
			}
		}

	}
	
	private List<String> getVMInstanceUUIDs() {
		return getFilesWithExtn(ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath(), ".xml");
	}
	
	private List<String> getFilesWithExtn(String directory, String extn) {
		  logger.info("Dir for getting the Extn: " + directory);
		  List<String> files = new ArrayList<String>();
		  File dir = new File(directory);
		  if(dir!=null && dir.listFiles()!=null){
			  for (File file : dir.listFiles()) {
				logger.info("file: " + file.getAbsolutePath());
			    if (file.getName().endsWith((extn)) && !file.getName().startsWith("WMConfig")) {
			      files.add(file.getName().substring(0,file.getName().length()-4));
			    }
			  }
		  }
		  return files;
	}
	
	private void createLogFileForAgentDestInfo() throws IOException, InterruptedException {
		
		try{
			BackupConfiguration backupConfig = BackupService.getInstance().getBackupConfiguration();
			
			if(backupConfig==null){
				logger.warn("The backup configuration came as NULL");
				return;
			}
				
			long totalFreeSize = BackupService.getInstance().getBackupInformationSummary().getDestinationCapacity().getTotalFreeSize();
			long totalVolumeSize = BackupService.getInstance().getBackupInformationSummary().getDestinationCapacity().getTotalVolumeSize();
			
			String dest = backupConfig.getDestination();
			String destUserName = backupConfig.getUserName();
			String destPassword = backupConfig.getPassword();
			
			createLogFileForDestInfo(dest, destUserName, destPassword, totalFreeSize, totalVolumeSize, ServiceContext.getInstance().getLocalMachineName(), false);
		} catch (ServiceException e) {
			logger.error("Problem in collecting the destination info for the Agent. Exception occurred.");
			logger.error(e);
			String msg = String.format(WebServiceMessages.getResource("DestInfoCollectFail"));
			new NativeFacadeImpl().addLogActivity(
					Constants.AFRES_AFALOG_WARNING,
					Constants.AFRES_AFJWBS_GENERAL,
					new String[] { msg, "", "",	"", "" });
		}
	}
	
	public String getLablesUsed() {
		String command = "fsutil fsinfo drives";
		
		String result = executeCmd(command);
		
		String Labels_used = result.substring(8);
		Labels_used=Labels_used.replaceAll(":\\\\",",");
		String[] lables = 	Labels_used.split(",");
		String VolLables = "";
		for (int i = 0;i<lables.length; i++)
		{
			VolLables = VolLables + ","+ lables[i].trim();
		}
		return VolLables.substring(1, VolLables.length()-1);
	}
	
	
	public String createNetworkShare(String remotePath, String userName,
			String password) {
		/*
		 * First get all the volume lables got used in the local machine
		 */
		
		if (remotePath != null) {
			if (remotePath.endsWith("\\")) {
				remotePath = remotePath.substring(0, remotePath.length() - 1);
			}
		}

		String volsUsed = getLablesUsed();

		String createdShares = "";

		/*
		 * Now try to create the network shares
		 */
		int anyFailure = 0;
		/*
		 * Starting from label a to s and pick the ones which are not already
		 * used
		 */
		for (int i = 97; i <= 122; i++) {
			String cmd = "";

			Character shareName = (char) i;

			if (volsUsed.toUpperCase().indexOf(
					shareName.toString().toUpperCase()) == -1) {
				// if given values indicates a volumes then append $
				// for example to share C volume of kolve01-2k8-1 the path will
				// become \\kolve01-2k8-1\c$
				cmd = "net use " + shareName.toString() + ": \"" + remotePath
						+ "\" /USER:" + userName + " " + password;
				
				logger.info("command used to cretae network share " + "net use " + shareName.toString() + ": \"" + remotePath
						+ "\" /USER:" + userName);
				
				String result = executeCmd(cmd);

				
				logger.info("Output of creating the share is: " + result);
				if (result.toLowerCase().contains(
						"The specified network resource or device is no longer available"
								.toLowerCase())) {
					logger.error("Could not make connection to the datastore path");
					return null;
				}
			
				if (result.indexOf("The command completed successfully") != -1) {

					createdShares = createdShares + "," + shareName.toString();
					break;
				} else {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}
		if (createdShares.length() != 0)
			return createdShares.substring(1);
		else
			return null;
	}
	
	/*public static void main(String args[])
	{
		CollectDiagnosticInfoService diagService = CollectDiagnosticInfoService.getInstance();
		
			try {
				diagService.executeCmd("\"C:\\Program Files\\Arcserve\\Unified Data Protection\\Engine\\BIN\\DiagnosticUtility\\getFolderSubPathsInfo.bat\" " + "\"C:\\dest\\JAYSA02-E7440\" " +
										"50839162880 213800448000 JAYSA02-E7440 \"C:\\Program Files\\Arcserve\\Unified Data Protection\\Engine\\Logs\"", null, 
										new File("C:\\Program Files\\Arcserve\\Unified Data Protection\\Engine\\BIN\\DiagnosticUtility"));
				
				//diagService.executeCmd("cmd /c " + "\"C:\\Program Files\\Arcserve\\Unified Data Protection\\Engine\\BIN\\DiagnosticUtility\\getFolderSubPathsInfo.bat\" \"C:\\dest\\JAYSA02-E7440\" 50839162880 213800448000 JAYSA02-E7440 \"C:\\Program Files\\Arcserve\\Unified Data Protection\\Engine\\Logs\"\");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}*/

}

