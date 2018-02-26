package com.ca.arcflash.webservice.service;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.subscription.SubscriptionConfiguration;
import com.ca.arcflash.webservice.service.internal.SubscriptionConfigurationXMLDAO;

public class SubscriptionService extends BaseService {
	private static final Logger logger = Logger
			.getLogger(SubscriptionService.class);
	private static final SubscriptionService subscriptionInstance = new SubscriptionService();
	private SubscriptionConfiguration subscriptionConfig = null;
	private SubscriptionConfigurationXMLDAO subscriptionConfigurationXMLDAO = new SubscriptionConfigurationXMLDAO();
	private Object lock = new Object();

	public static SubscriptionService getInstance() {
		return subscriptionInstance;
	}

	public long saveSubscriptionConfiguration(
			SubscriptionConfiguration in_subscriptionConfig) throws Exception {
		subscriptionConfigurationXMLDAO.Save(ServiceContext.getInstance().getSubscriptionConfigurationFilePath(), in_subscriptionConfig);
		logger.debug("save Subscription Configuration");
		subscriptionConfig = in_subscriptionConfig;
		return 0;
	}

	public SubscriptionConfiguration getSubscriptionConfiguration()
			throws ServiceException {
		logger.debug("getSubscriptionConfiguration - start");
		try {
			synchronized (lock) {
				if (subscriptionConfig == null) {
					if (!StringUtil.isExistingPath(ServiceContext.getInstance().getSubscriptionConfigurationFilePath()))
						return null;

					subscriptionConfig = subscriptionConfigurationXMLDAO.get(ServiceContext.getInstance().getSubscriptionConfigurationFilePath());
				}
			}

			logger.debug("getSubscriptionConfiguration - end :"+ subscriptionConfig);
			return subscriptionConfig;
		} catch (Throwable e) {
			logger.error("getSubscriptionConfiguration()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public String getCACloudStorageKey(ArchiveCloudDestInfo cloudDestInfo,String userName, String password) throws ServiceException {
		return getNativeFacade().getCACloudStorageKey(cloudDestInfo, userName,password);
	}
	
	/*public long consumeCACloudLicense(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException {
		return getNativeFacade().consumeCACloudLicense(cloudDestInfo);
	}*/

/*	public long canBackup(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException {
		return getNativeFacade().canBackup(cloudDestInfo);
	}*/

	public void updateStorageKey(){
		try {
			SubscriptionConfiguration subConfig=SubscriptionService.getInstance().getSubscriptionConfiguration();
			if(subConfig==null){//current version is not d2d on demand
				return;
			}
			String oldStorageKey=subConfig.getStorageKey();
			String newStorageKey = getNativeFacade().updateStorageKey(ConvertToCloudConfig(subConfig));
			if(newStorageKey.equals(oldStorageKey)){
				logger.info("no change about storage key!");
			}else if(newStorageKey.startsWith("Error_")){
				logger.error("updateStorageKey occur error :" + newStorageKey);
			}else{
				subConfig.setStorageKey(newStorageKey);
				SubscriptionService.getInstance().saveSubscriptionConfiguration(subConfig);
				ArchiveConfiguration archiveConfig=ArchiveService.getInstance().getArchiveConfiguration();
				if(archiveConfig!=null&&archiveConfig.isbArchiveToCloud()&&archiveConfig.getCloudConfig().getcloudVendorType()==6){
					archiveConfig.getCloudConfig().setcloudVendorPassword(newStorageKey);
					ArchiveService.getInstance().saveArchiveConfiguration(archiveConfig);
				}
				logger.info("Update storage key from "+oldStorageKey+" to "+newStorageKey);
			}
		} catch (Exception e) {
			logger.error("updateStorageKey occur exception :",e);
		}
	}
	
	private ArchiveCloudDestInfo ConvertToCloudConfig(SubscriptionConfiguration subConfig) {
		ArchiveCloudDestInfo CloudConfig = null;
		
		if(subConfig != null){
			CloudConfig = new ArchiveCloudDestInfo();
			CloudConfig.setcloudUseProxy(subConfig.isCloudUseProxy());
			if(subConfig.isCloudUseProxy()){
				CloudConfig.setcloudProxyServerName(subConfig.getCloudProxyServerName());
				CloudConfig.setcloudProxyPort(subConfig.getCloudProxyPort());
				
				CloudConfig.setcloudProxyRequireAuth(subConfig.isCloudProxyRequireAuth());
				if(subConfig.isCloudProxyRequireAuth()){
					CloudConfig.setcloudProxyUserName(subConfig.getCloudProxyUserName());
					CloudConfig.setcloudProxyPassword(subConfig.getCloudProxyPassword());
				}
			}
			
			CloudConfig.setcloudVendorType(subConfig.getCloudVendorType());
			CloudConfig.setcloudVendorURL(subConfig.getCloudVendorURL());
			CloudConfig.setcloudVendorPassword(subConfig.getStorageKey());
			CloudConfig.setcloudVendorUserName(subConfig.getUserName());
			CloudConfig.setcloudCertificatePassword(subConfig.getPassword());
			CloudConfig.setcloudBucketName(subConfig.getServerName());
			CloudConfig.setcloudBucketRegionName(subConfig.getRegion());
		}
		return CloudConfig;
	}

	public String getGeminarePortalURL(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException {
		return getNativeFacade().getGeminarePortalURL(cloudDestInfo);
	}

	public void clearCachedConfiguration() {
		logger.debug("clearCachedConfiguration - start");

		synchronized (lock) {
			subscriptionConfig = null;
		}

		logger.debug("clearCachedConfiguration - end");

		
	}
}
