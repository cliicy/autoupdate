package com.ca.arcflash.webservice.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.arcserve.cserp.entitlements.entitlementReg.beans.UsageDetails;
import com.arcserve.cserp.entitlements.entitlementReg.client.EntitlementRegister;
import com.arcserve.cserp.entitlements.entitlementReg.utility.EntitlementRegisterUtility;
import com.arcserve.cserp.entitlements.entitlementReg.utility.WindowsRegistry;
import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.webservice.data.ProtectionInformation;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.service.AERPService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceContext;

public class AERPJob implements Job {
	private static final Logger logger = Logger.getLogger(AERPJob.class);
	EntitlementRegisterUtility entitlementRegisterUtilty = new EntitlementRegisterUtility();

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.debug("execute(JobExecutionContext) - started at " + new Date() + " " + this.getClass().getName());
		
		try
		{
			HashMap<String, String> entitlementFreq = new EntitlementRegisterUtility().getEntitlementFrequencies();
			
			String uploadFilePath = Boolean.parseBoolean(entitlementFreq.get("uploadFile")) ? getUploadFilePath() : null;
			
			String responseCode = EntitlementRegister.uploadFile(uploadFilePath, getUsageDetailsForStandAloneAgent());
	
			//If uploadFrequency in Policy.xml is changed then terminate existing AERPJob.
			//submit new AERPJob for the new Policy.xml details.
			if(responseCode != null && responseCode.equals("UPLOADUSAGEDETAILS_SUCCESS_UPLOADSETTINGSCHANGED"))
			{
				try {
						logger.info("Resubmitting AERPJob for uploadFrequency change in the Policy.xml");
						entitlementFreq = new EntitlementRegisterUtility().getEntitlementFrequencies();
						AERPService.getInstance().submitAERPJob(Integer.parseInt(entitlementFreq.get("uploadfrequency").trim()), entitlementFreq.get("uploadTimeStamp").trim());
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
	
		logger.info("Ended AERPJob at " + new Date() + " " + this.getClass().getName());
	}
	
	public String getUploadFilePath()
	{
		String jobHistoryPath = "";
		String jobHistory_zip = "";
		try
		{
			EntitlementRegisterUtility entitlementRegisterUtility = new EntitlementRegisterUtility();
			if(entitlementRegisterUtility.getOSType().equals("WINDOWS"))
			{
				jobHistoryPath = WindowsRegistry.readString (WindowsRegistry.HKEY_LOCAL_MACHINE,                           
					    "SOFTWARE\\Arcserve\\Unified Data Protection\\Engine\\InstallPath","Path");
				jobHistory_zip = jobHistoryPath + "database\\D2DJOBHISTORY.zip";
				jobHistoryPath = jobHistoryPath + "database\\D2D.JOBHISTORY";  
			}
			else if(entitlementRegisterUtility.getOSType().equals("LINUX"))
			{
				//jobHistoryPath = System.getenv("D2DSVR_HOME");
				//jobHistoryPath = PROPERTY_FILEPATH + "/EntitlementRegister_Linux.properties";
				jobHistory_zip = jobHistoryPath + "database\\D2DJOBHISTORY.zip";
				jobHistoryPath = "/new1/configFiles/D2D.JOBHISTORY";
			}
			if(new File(jobHistoryPath).exists())
			{
				if(new File(jobHistory_zip).exists())
				{
					new File(jobHistory_zip).delete();
				}
				addToZipFile(jobHistoryPath, new ZipOutputStream(new FileOutputStream (jobHistory_zip)));
				logger.info("D2D.JobBusHistory zip file path" + jobHistory_zip);
			}
		}
		catch(Exception e)
		{
			logger.error("Unable to find JobBusHistory file " + e.getMessage());
		}
		return jobHistory_zip;
	}
	
	/**
	 * This method adds D2D.JobBusHistory file to zip.
	 * @param fileName
	 * @param zos
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	 public static void addToZipFile(String fileName, ZipOutputStream zos) 
	 {
		 try
		 {
			logger.info("Writing '" + fileName + "' to zip file");
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			zos.putNextEntry(new ZipEntry(file.getName()));

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}

			zos.closeEntry();
			fis.close();
			logger.info("Writing to " + fileName + "to zipl file success");
		}
	 	catch(Exception e)
	 	{
	 		logger.error("Exception while zipping D2D.JobBusHistory " + e.getMessage());
	 	}
	}
	/**
	 * This method constructs UsageDetails object with the ProtectionInformation and all other Product Information.
	 */
	 public UsageDetails getUsageDetailsForStandAloneAgent()
	 {
		 UsageDetails usageDetails = new UsageDetails();
		 try
		 {
			 logger.info("Create getUsageDetails "  + new Date() + " " + this.getClass().getName());
			 long dataProtected = 0;
			 long storageUtilized = 0;
			 
			 logger.info("Invoking UDP_CommonService " + new Date());
			 VersionInfo vInfo = CommonService.getInstance().getVersionInfo();
			 logger.info("Invoking UDP_CommonService success " + new Date() +" Version Info " + vInfo);
			 logger.info("Product Name " + ServiceContext.getInstance().getProductNameD2D());
			 logger.info("Product Version " + vInfo.getMajorVersion() + "." +vInfo.getMinorVersion() + "." + vInfo.getBuildNumber());
			 
			 logger.info("Invoking UDP_BackupService " + new Date());
			 ProtectionInformation[] protectionInformation = BackupService.getInstance().getProtectionInformation();
			 if(protectionInformation != null && protectionInformation.length > 0)
			 {
				 for(int count = 0 ; count < protectionInformation.length ; count++)
				 {
					 dataProtected += protectionInformation[count].getTotalLogicalSize();
					 storageUtilized += protectionInformation[count].getSize();
				 }
			 }
			 logger.info("Invoking UDP_BackupService success " + new Date() + " ProtectionInformation " + protectionInformation); 
			 logger.info("Data Protected in bytes " + bytes2String(dataProtected));
			 logger.info("Storage Utilized in bytes " + bytes2String(storageUtilized));
			 
			 //Set ProtectionData to UsageDetails
			 HashMap<String, String> protectionData = new HashMap<String, String>();
			 protectionData.put("RawDataProtected", "N/A");
			 protectionData.put("StorageUtilized", bytes2String(storageUtilized));
			 usageDetails.setProtectionData(protectionData);
			 usageDetails.setProductName(ServiceContext.getInstance().getProductNameD2D());
			 usageDetails.setProductVersion(vInfo.getMajorVersion() + "." +vInfo.getMinorVersion());
			 usageDetails.setProtectedDataSize(bytes2String(dataProtected));
			 usageDetails.setPhysicalServers("1");
			 usageDetails.setVirtualServers("0");
			 usageDetails.setSockets("");
			 
			 //Set OSData to UsageDetails
			 List<HashMap<String, String>> osDataList = new ArrayList<HashMap<String, String>>();
			 HashMap<String, String> osData = new HashMap<String, String>();
			 osData.put("name", System.getProperty("os.name"));
			 osData.put("count", "1");
			 osDataList.add(osData);
			 usageDetails.setOSData(osDataList);
			 
			 //Set AgentData to UsageDetails
			 List<HashMap<String, String>> agentDataList = new ArrayList<HashMap<String, String>>();
			 HashMap<String, String> agentData = new HashMap<String, String>();
		     agentData.put("name",ServiceContext.getInstance().getProductNameD2D());
		     agentData.put("majorVersion", vInfo.getMajorVersion());
			 agentData.put("minorVersion", vInfo.getMinorVersion());
			 agentData.put("buildNumber", vInfo.getBuildNumber());
		     agentData.put("count","1");
		     agentDataList.add(agentData);
		     usageDetails.setAgentData(agentDataList);
			 
		     //Set Empty ApplicationData to UsageDetails
		     //TODO need information on Application Details for StandAlone Agent
		     /*List<HashMap<String, String>> applicationDataList = new ArrayList<HashMap<String, String>>();
			 HashMap<String, String> applicationData = new HashMap<String, String>();
			 applicationData.put("name",ServiceContext.getInstance().getProductNameD2D());
			 applicationData.put("count","1");
			 applicationDataList.add(applicationData);
			 usageDetails.setApplicationData(applicationDataList);*/
			 
			 logger.info("Created UsageDetails for Console Application Data is" + usageDetails.getAgentData() + " + OS Data is " + usageDetails.getOSData() + "Agent Details " + usageDetails.getAgentData()
						+ "ProtectedData " + usageDetails.getProtectionData());
			 
			 logger.info("UsageDetails object created " + new Date() + " " + this.getClass().getName());
		 }
		 catch(Exception e)
		 {
			 logger.error("getUsageDetails exception" + e.getMessage());
		 }
		 return usageDetails;
	 }
	 
	 /**
	  * This method returns data size in KB/MB/GB for bytes.
	  * @param bytes
	  * @return
	  */
	 public static String bytes2String(long bytes){	
		 logger.info("Converting bytes to string " + bytes);
		 String bytesString = "";
		 try
		 {
			 DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(DataFormatUtil.getDateFormatLocale());
		     DecimalFormat number = new DecimalFormat("0.00", formatSymbols);
		     if (bytes <1024)
					bytesString = bytes+" Bytes";
			 else if (bytes<(1024*1024)) {
					String kb = number.format(((double)bytes)/1024);
			 if(kb.startsWith("1024"))
						bytesString = "1 MB";
					
					bytesString = kb+" KB";
				}
			 else if (bytes<(1024*1024*1024)) {
					String mb = number.format(((double)bytes)/(1024*1024));
			 if(mb.startsWith("1024"))
						bytesString = "1 GB";
					
					bytesString = mb + " MB";
				}
			 else
					bytesString = number.format(((double)bytes)/(1024*1024*1024)) + " GB";
		 }
		 catch(Exception e)
		 {
			logger.error("Exception Converting bytes to String " + e.getMessage());
			return bytesString;
		 }
		 logger.info("Coverted bytes to String " + bytesString);
		 return bytesString;
	}
	 
	 /**
	  * This method retuns the socket details used by the Machine.
	  * @return
	  */
	/*public String getSocket()
	{
		String socket_details = "";
	    try
	    {
	    	logger.info("Machine Name" + InetAddress.getLocalHost().getHostName() +  " " + this.getClass().getName());
	    	socket_details = new Socket(InetAddress.getLocalHost().getHostName(), 8014).toString();
	    	logger.info("Socket used " + socket_details + " " + this.getClass().getName());
	    }
	    catch (Exception ex)
	    {
	    	logger.error("Error getSocket for UDP" + ex.getMessage());
	    }
		return socket_details;
	}*/
}
