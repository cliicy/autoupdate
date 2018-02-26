package com.ca.arcflash.webservice.util;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.BucketNameEncoder;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationConstants;
import com.ca.arcflash.webservice.jni.WSJNI;

public class ArchiveUtil {
	
	private static final Logger logger = Logger.getLogger(ArchiveUtil.class);
	
	public static void  decryptArchiveDestination(ArchiveConfiguration config){
		if(config.isbArchiveToDrive() && !StringUtil.isEmptyOrNull(config.getStrArchiveDestinationPassword())){
			logger.debug("archive to drive with password");
			config.setStrArchiveDestinationPassword(WSJNI.AFDecryptString(config.getStrArchiveDestinationPassword()));
		}		
		if(config.isbArchiveToCloud()){
			logger.debug("archive to cloud");
			if(!StringUtil.isEmptyOrNull(config.getCloudConfig().getcloudVendorPassword()))
				config.getCloudConfig().setcloudVendorPassword(WSJNI.AFDecryptString(config.getCloudConfig().getcloudVendorPassword()));
			if(!StringUtil.isEmptyOrNull(config.getCloudConfig().getcloudCertificatePassword()))
				config.getCloudConfig().setcloudCertificatePassword(WSJNI.AFDecryptString(config.getCloudConfig().getcloudCertificatePassword()));
			if(!StringUtil.isEmptyOrNull(config.getCloudConfig().getcloudProxyPassword()))
				config.getCloudConfig().setcloudProxyPassword(WSJNI.AFDecryptString(config.getCloudConfig().getcloudProxyPassword()));
		}
	}
	
	public static void decryptRpsHost(RpsHost host ){
		if(!StringUtil.isEmptyOrNull(host.getPassword()))
			host.setPassword(WSJNI.AFDecryptString(host.getPassword()));
	}
	
	public static void encryptRpsHost(RpsHost host ){
		if(!StringUtil.isEmptyOrNull(host.getPassword()))
			host.setPassword(WSJNI.AFEncryptString(host.getPassword()));
	}
	
	
	
	public static void updateStrDestination(ArchiveConfiguration config,String destination){
		if(config.isbArchiveToDrive()&&!StringUtil.isEmptyOrNull(config.getStrArchiveToDrivePath())){
			config.setStrArchiveToDrivePath(config.getStrArchiveToDrivePath()+"\\"+destination);
		}
	}
	

	
	public static void encodeCloudName(ArchiveConfiguration config,String hostname){
		if(config.isbArchiveToCloud()&&config.getCloudConfig()!=null){
			String bucketName = config.getCloudConfig().getcloudBucketName();
			String encodedBucketName = config.getCloudConfig().getEncodedCloudBucketName();
			String bucketNamePrefix = ArchiveConfigurationConstants.BUCKET_PREFIX_ARCSERVE ;
			if(!StringUtil.isEmptyOrNull(bucketName))
				config.getCloudConfig().setcloudBucketName(bucketNamePrefix +hostname+"-"+ bucketName);
			
			if(!StringUtil.isEmptyOrNull(encodedBucketName)){
				try{
					config.getCloudConfig().setEncodedCloudBucketName((bucketNamePrefix +  BucketNameEncoder.encodeWithUTF8(hostname+"-"+encodedBucketName)).toLowerCase());
				}
				catch(Exception e){
					logger.info("Error occured on encoding the bucketname");
					logger.debug("encodedBucketName="+encodedBucketName);
					logger.debug("bucketName="+bucketName);
					logger.error(e);
				}
			}else if(!StringUtil.isEmptyOrNull(bucketName)){
				try{
					config.getCloudConfig().setEncodedCloudBucketName((bucketNamePrefix + BucketNameEncoder.encodeWithUTF8(hostname+"-"+bucketName)).toLowerCase());
				}
				catch(Exception e){
					logger.info("Error occured on encoding the bucketname");
					logger.debug("encodedBucketName="+encodedBucketName);
					logger.debug("bucketName="+bucketName);
					logger.error(e);
				}
			}
		}
	}
	
	public static String getDomainName(String fullName) {
		String strDomain = "";
		if (fullName == null || fullName.isEmpty())
			return strDomain;

		int pos = fullName.indexOf("\\");
		if (pos != -1) {
			strDomain = fullName.substring(0, pos);
		}

		return strDomain;
	}
	
	public static String getUserName(String fullName) {
		String strUser = "";
		if (fullName == null || fullName.isEmpty())
			return strUser;

		int pos = fullName.indexOf("\\");
		if (pos == -1) {
			strUser = fullName;
		} else {
			strUser = fullName.substring(pos + 1);
		}
		return strUser;
	}
	
	public static boolean needDeletePendingJobs(ArchiveConfiguration newConfig, ArchiveConfiguration oldConfig){
		boolean enabledInOld = oldConfig != null && oldConfig.isbArchiveAfterBackup();
		boolean disabledInNew = newConfig == null || !newConfig.isbArchiveAfterBackup();
		return enabledInOld && disabledInNew;
	}
}
