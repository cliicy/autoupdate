package com.ca.arcserve.edge.app.base.webservice.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;

public class CollectDeadLockInfo {
	private static CollectDeadLockInfo instance = new CollectDeadLockInfo();
	private static Logger logger = Logger.getLogger("DeadLockInfologout");
	private IEdgeSettingDao settingDao = DaoFactory.getDao(IEdgeSettingDao.class);
	private CollectDeadLockInfo(){}
	
	public static CollectDeadLockInfo getInstance(){
		return instance;
	}
	
	public void CollectDeadLockInfoToLogFile(){
		try {
			while(true){
				List<DeadLockInfo> deadLockInfos = new ArrayList<DeadLockInfo>();
				settingDao.as_edge_getDeadLockInfo(deadLockInfos);
				if(!deadLockInfos.isEmpty()){
					logger.info("===================================================================================================================");
					logger.info("Have "+deadLockInfos.size() +" Blocking Infos");
					int i=1;
					for (DeadLockInfo deadLockInfo : deadLockInfos) {
						logger.info("Blocking Info "+i);
						logger.info("BlockingSid is :" + deadLockInfo.getBlockingSpid() + "Blocking Text is "+deadLockInfo.getBlockingText());
						logger.info("BlockedSid is :" + deadLockInfo.getBlockedSpid() + "Blocked Text is "+deadLockInfo.getBlockedText());
						logger.info("Blocking Info "+i +" end");
					}
					logger.info("===================================================================================================================");
				}
				Thread.sleep(60*1000);// sleep 3 seconds to sync result
			}
		} catch (Exception e) {
			logger.error("[CollectDeadLockInfo] failed.",e);
		}
	}
}
