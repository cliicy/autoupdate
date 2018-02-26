package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeAD;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHyperV;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeAdDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoverySetting;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryPhase;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class DiscoveryManager {
	private static DiscoveryManager instance = null;

	private ThreadPoolExecutor esxDiscoveryExecutorService;
	private ThreadPoolExecutor hyperVDiscoveryExecutorService;

	private long autoDiscoveryInteralMinutes;
	private static Logger logger = Logger.getLogger(DiscoveryManager.class);
	private Date lastESXDiscoveryDate;
	private Date lastHyperVDiscoveryDate;
	private Object lastESXDiscDateSyncObj = new Object();
	private Object lastHyperVDiscDateSyncObj = new Object();
	private DiscoveryConfiguration config;

	private static IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private static IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private static IEdgeAdDao adDao =DaoFactory.getDao(IEdgeAdDao.class);

	private IActivityLogService activityLogService = new ActivityLogServiceImpl();
	private ActivityLog activityLog = new ActivityLog();
	private static IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);

	private DiscoveryManager() {
		config = DiscoveryConfiguration.getInstance();
		config.saveConfiguration();
		autoDiscoveryInteralMinutes = config.intervalInMinutes;

		esxDiscoveryExecutorService = new ThreadPoolExecutor(config.parellelNumber, config.parellelNumber, 0L,
				TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(config.queueMaxLength), new NamingThreadFactory("esxDiscoveryExecutorService"));
		hyperVDiscoveryExecutorService = new ThreadPoolExecutor(config.parellelNumber, config.parellelNumber, 0L,
				TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(config.queueMaxLength), new NamingThreadFactory("esxDiscoveryExecutorService"));
	}

	public synchronized static DiscoveryManager getInstance() {
		if (instance == null) {
			instance = new DiscoveryManager();
		}

		return instance;
	}
	
	public void shutdownThreadPools(){
		esxDiscoveryExecutorService.shutdownNow();
		hyperVDiscoveryExecutorService.shutdownNow();
	}
	
	public void doAutoDiscoveryForAD() {
		doAutoDiscoveryForAD(getADDiscoveryOptions());
	}
	
	public void doAutoDiscoveryForAD(List<DiscoverySetting> settings) {
		doAutoDiscoveryForAD(getADDiscoveryOptions(settings));
	}
	
	public void doAutoDiscoveryForAD(DiscoveryOption[] adOptions) {
		if (adOptions != null) {
			try {
				DiscoveryService.getInstance().discoverNodeFromAD(adOptions);
			} catch (EdgeServiceFault e) {
				activityLog.setSeverity(Severity.Error);
				activityLog.setTime(new Date(System.currentTimeMillis()));
				activityLog.setMessage(EdgeCMWebServiceMessages.getResource("autoDiscoveryJobRunFail"));
				try {
					activityLogService.addLog(activityLog);
				} catch (EdgeServiceFault e2) {
					logger.error("Add Activity Log Error: "+e2.toString());
				}
				logger.error(e.getMessage(), e);
			}
		} else {
			activityLog.setSeverity(Severity.Information);
			activityLog.setTime(new Date(System.currentTimeMillis()));
			activityLog.setMessage(EdgeCMWebServiceMessages.getResource("autoDiscoveryJobFindNothing"));
			try {
				activityLogService.addLog(activityLog);
			} catch (EdgeServiceFault e) {
				logger.error("Add Activity Log Error: "+e.toString());
			}
			logger.info("null AD discovery option!");
		}
	}
	
	public void doAutoDiscoveryForEsx() {
		Date lastESXUpdateDate = null;
		synchronized (lastESXDiscDateSyncObj) {
			lastESXUpdateDate = lastESXDiscoveryDate;
		}

		boolean esxNeedDiscovery = isNeedDoDiscovery(lastESXUpdateDate);

		if (esxNeedDiscovery) {
			doEsxAutoDiscovery4AutoTrigger();
		} else {
			logger.info("Ignore this vCenter/ESX auto discovery, since from the last update date, the time is less than "
					+ autoDiscoveryInteralMinutes + " minutes");
		}
	}
	
	public void doAutoDiscoveryForHyperV() {
		Date lastHyperVUpdateDate = null;
		synchronized (lastHyperVDiscDateSyncObj) {
			lastHyperVUpdateDate = lastHyperVDiscoveryDate;
		}
		
		boolean hyperVNeedDiscovery = isNeedDoDiscovery(lastHyperVUpdateDate);

		if (hyperVNeedDiscovery) {
			doHyperVAutoDiscovery4AutoTrigger();
		} else {
			logger.info("Ignore this Hyper-V auto discovery, since from the last update date, the time is less than "
					+ autoDiscoveryInteralMinutes + " minutes");
		}
	}

	/**
	 * Do discovery for automatically triggered.
	 */
	public void doAutoDiscovery() {
		doAutoDiscoveryForEsx();
		doAutoDiscoveryForHyperV();
	}

	
	public void doManualDiscovery(List<DiscoverySetting> settings) {
		doEsxAutoDiscovery4ManualTrigger(settings);
		doHyperVAutoDiscovery4ManualTrigger(settings);
	}
	
	/**
	 * Do discovery for manually triggered.
	 */
	public void doManualDiscovery() {
		doEsxAutoDiscovery4ManualTrigger(null);
		doHyperVAutoDiscovery4ManualTrigger(null);
	}

	public void updateLastESXDiscoveryDate() {
		synchronized (lastESXDiscDateSyncObj) {
			lastESXDiscoveryDate = new Date();
		}
	}
	
	public void updateLastHyperVDiscoveryDate() {
		synchronized (lastHyperVDiscDateSyncObj) {
			lastHyperVDiscoveryDate = new Date();
		}
	}

	private void doEsxAutoDiscovery4AutoTrigger() {
		DiscoveryESXOption[] esxOptions = null;
		try {
			esxOptions = getESXDisvoceryOptions();
		} catch (EdgeServiceFault e1) {
			logger.error("[DiscoveryManager] doEsxAutoDiscovery4AutoTrigger() failed to get the auto discovery esx list");
		}
		if(esxOptions == null || esxOptions.length <= 0){
			logger.info("[DiscoveryManager] doEsxAutoDiscovery4AutoTrigger() have not found the esxs which can do auto discovery.");
			return;
		}
		Date lastUpdateDate = null;
		synchronized (lastESXDiscDateSyncObj) {
			lastUpdateDate = lastESXDiscoveryDate;
		}
		boolean isQueueNotFull = esxDiscoveryExecutorService.getQueue().size() < config.queueMaxLength;

		if (isQueueNotFull && isNeedDoDiscovery(lastUpdateDate)) {
			logger.info("Add this ESX auto discovery to thread pool");
			try {
				esxDiscoveryExecutorService.submit(new EsxAutoDiscovery4AutoTriggerRunner(esxOptions));
			} catch (RejectedExecutionException e) {
				logger.info("Ignore this ESX auto discovery, since the waiting queue has arrived the max length "
						+ config.queueMaxLength + " .");
			}
		} else if (!isQueueNotFull) {
			logger.info("Ignore this ESX auto discovery, since the waiting queue has arrived the max length "
					+ config.queueMaxLength + " .");
		} else {
			logger.info("Ignore this ESX auto discovery, since from the last auto discovery, since from the last update, the time is less than "
					+ autoDiscoveryInteralMinutes + " minutes");
		}
	}

	private void doHyperVAutoDiscovery4AutoTrigger() {
		DiscoveryHyperVOption[] hyperOptions = null;
		try {
			hyperOptions = getHyperVDisvoceryOptions();
		} catch (EdgeServiceFault e1) {
			logger.error("[DiscoveryManager] doHyperVAutoDiscovery4AutoTrigger() Failed to get the hypervs to do auto discovery.");
		}
		if(hyperOptions == null || hyperOptions.length <= 0){
			logger.info("[DiscoveryManager] doHyperVAutoDiscovery4AutoTrigger() Have not find the hypervs which can do auto discovery.");
			return;
		}
		Date lastUpdateDate = null;
		synchronized (lastHyperVDiscDateSyncObj) {
			lastUpdateDate = lastHyperVDiscoveryDate;
		}
		boolean isQueueNotFull = (hyperVDiscoveryExecutorService.getQueue().size() < config.queueMaxLength);

		if (isQueueNotFull && isNeedDoDiscovery(lastUpdateDate)) {
			logger.info("Add this Hyper-V auto discovery to thread pool");
			try {
				hyperVDiscoveryExecutorService.submit(new HyperVAutoDiscovery4AutoTriggerRunner(hyperOptions));
			} catch (RejectedExecutionException e) {
				logger.info("Ignore this Hyper-V auto discovery, since the waiting queue has arrived the max length "
						+ config.queueMaxLength + " .");
			}
		} else if (!isQueueNotFull) {
			logger.info("Ignore this Hyper-V auto discovery, since the waiting queue has arrived the max length "
					+ config.queueMaxLength + " .");
		} else {
			logger.info("Ignore this Hyper-V auto discovery, since from the last auto discovery, the time is less than the interval "
					+ autoDiscoveryInteralMinutes + " minutes.");
		}
	}

	public void doEsxAutoDiscovery4ManualTrigger(List<DiscoverySetting> settings) {
		EdgeExecutors.getCachedPool().submit(new EsxAutoDiscovery4MaualTriggerRunner(settings));
	}

	public void doHyperVAutoDiscovery4ManualTrigger(List<DiscoverySetting> settings) {
		EdgeExecutors.getCachedPool().submit(new HyperVAutoDiscovery4MaualTriggerRunner(settings));
	}

	private boolean isNeedDoDiscovery(Date lastDiscoveryDate) {
		if (null == lastDiscoveryDate) {
			return true;
		}

		Date currentDate = new Date();
		return (currentDate.getTime() - lastDiscoveryDate.getTime()) > autoDiscoveryInteralMinutes * 60000;
	}
	
	private static DiscoveryOption[] getADDiscoveryOptions(){
		DiscoveryOption[] adOptions  = null;
		try {
			
			List<EdgeAD> adList = new LinkedList<EdgeAD>();
			adDao.as_edge_ad_get_auto_discovery(adList);
			if(adList.size() > 0) {
				adOptions = new DiscoveryOption[adList.size()];
				int i = 0;
				for(EdgeAD ad : adList) {
					adOptions[i] = new DiscoveryOption();
					adOptions[i].setComputerNameFilter(ad.getFilter());
					adOptions[i].setId(ad.getId());
					adOptions[i].setPassword(ad.getPassword());
					adOptions[i].setUserName(ad.getUsername());
					adOptions[i].setJobType(1); // schedule job
					GatewayEntity gateway = gatewayService.getGatewayByEntityId(ad.getId(), EntityType.AD );
					adOptions[i].setGatewayId(gateway.getId());
					i++;
				}
			}
			
		} catch (Exception e) {
			logger.error("[DiscoveryManager] getADDiscoveryOptions()failed.",e);
		}
		return adOptions;
	}
	
	private static DiscoveryOption[] getADDiscoveryOptions(List<DiscoverySetting> settings){
		if(settings == null || settings.isEmpty()){
			return null;
		}
		DiscoveryOption[] adOptions  = new DiscoveryOption[settings.size()];
		try {
			int i = 0;
			for (DiscoverySetting ds : settings) {
				List<EdgeAD> adList = new LinkedList<EdgeAD>();
				adDao.as_edge_ad_getById(ds.getId(), adList); //select option from AD, because some filed (eg: password) can't be found in UI transfer model
				if(adList.isEmpty()){
					logger.error("[DiscoveryManager] getADDiscoveryOptions() failed. "
							+ "can't find the AD :"+ds.getUsername()+" the id is:"+ds.getId());
				}else {
					EdgeAD ad = adList.get(0);
					adOptions[i] = new DiscoveryOption();
					adOptions[i].setComputerNameFilter(ad.getFilter());
					adOptions[i].setId(ad.getId());
					adOptions[i].setPassword(ad.getPassword());
					adOptions[i].setUserName(ad.getUsername());
					adOptions[i].setJobType(1); // schedule job
					GatewayEntity gateway = gatewayService.getGatewayByEntityId(ad.getId(), EntityType.AD );
					adOptions[i].setGatewayId(gateway.getId());
					i++;
				}
			}
		}catch (Exception e) {
			logger.error("[DiscoveryManager] getADDiscoveryOptions(List<DiscoverySetting> settings)failed.",e);
		}
		return adOptions;
	}


	private static DiscoveryESXOption[] getESXDisvoceryOptions() throws EdgeServiceFault {
		DiscoveryESXOption[] esxOptions = null;
		List<EdgeEsx> esxList = new LinkedList<EdgeEsx>();

		esxDao.as_edge_esx_get_auto_discovery(esxList);
		if (esxList != null && esxList.size() > 0) {
			esxOptions = new DiscoveryESXOption[esxList.size()];
			int i = 0;

			for (EdgeEsx esx : esxList) {
				esxOptions[i] = new DiscoveryESXOption();
				esxOptions[i].setEsxPassword(esx.getPassword());
				esxOptions[i].setEsxUserName(esx.getUsername());
				esxOptions[i].setEsxServerName(esx.getHostname());
				esxOptions[i].setId(esx.getId());
				esxOptions[i].setPort(esx.getPort());
				esxOptions[i].setProtocol(Protocol.values()[esx.getProtocol()]);
				esxOptions[i].setJobType(1); // schedule job
				GatewayEntity gateway = gatewayService.getGatewayByEntityId(esx.getId(), EntityType.VSphereEntity );
				esxOptions[i].setGatewayId(gateway.getId());
				i++;
			}
		}

		return esxOptions;
	}
	
	private static DiscoveryESXOption[] getESXDisvoceryOptions(List<DiscoverySetting> settings) throws EdgeServiceFault {
		if(settings == null || settings.isEmpty())
			return null;
		DiscoveryESXOption[] esxOptions = new DiscoveryESXOption[settings.size()];
		int i = 0;
		for (DiscoverySetting eSetting : settings) {
			List<EdgeEsx> esxList = new LinkedList<EdgeEsx>();
			esxDao.as_edge_esx_getById(eSetting.getId(), esxList);
			if(esxList == null || esxList.isEmpty()){
				logger.error("[DiscoveryManager]getESXDisvoceryOptions(List<DiscoverySetting> "
						+ "settings)failed. can't find the esx:"+eSetting.getHostname()+" which id is:"+eSetting.getId());
			}else {
				EdgeEsx esx = esxList.get(0);
				esxOptions[i] = new DiscoveryESXOption();
				esxOptions[i].setEsxPassword(esx.getPassword());
				esxOptions[i].setEsxUserName(esx.getUsername());
				esxOptions[i].setEsxServerName(esx.getHostname());
				esxOptions[i].setId(esx.getId());
				esxOptions[i].setPort(esx.getPort());
				esxOptions[i].setProtocol(Protocol.values()[esx.getProtocol()]);
				esxOptions[i].setJobType(1); // schedule job
				GatewayEntity gateway = gatewayService.getGatewayByEntityId(esx.getId(), EntityType.VSphereEntity );
				esxOptions[i].setGatewayId(gateway.getId());
				i++;
			}
		}
		return esxOptions;
	}

	private static DiscoveryHyperVOption[] getHyperVDisvoceryOptions() throws EdgeServiceFault {
		DiscoveryHyperVOption[] hyperVOptions = null;
		List<EdgeHyperV> hyperVList = new LinkedList<EdgeHyperV>();

		hyperVDao.as_edge_hyperv_get_auto_discovery(hyperVList);
		if (hyperVList != null && hyperVList.size() > 0) {
			hyperVOptions = new DiscoveryHyperVOption[hyperVList.size()];
			int i = 0;
			for (EdgeHyperV hyperV : hyperVList) {
				hyperVOptions[i] = new DiscoveryHyperVOption();
				hyperVOptions[i].setPassword(hyperV.getPassword());
				hyperVOptions[i].setUsername(hyperV.getUsername());
				hyperVOptions[i].setServerName(hyperV.getHostname());
				hyperVOptions[i].setId(hyperV.getId());
				hyperVOptions[i].setJobType(1); // schedule job
				if (hyperV.getType() == HypervProtectionType.DEFAULT.getValue()) { // defect 175978
					hyperVOptions[i].setHypervProtectionType(HypervProtectionType.STANDALONE);
				} else {					
					hyperVOptions[i].setHypervProtectionType(HypervProtectionType.parse(hyperV.getType()));
				}
				GatewayEntity gateway = gatewayService.getGatewayByEntityId(hyperV.getId(), EntityType.HyperVServer );
				hyperVOptions[i].setGatewayId(gateway.getId());
				i++;
			}
		}

		return hyperVOptions;
	}
	
	private static DiscoveryHyperVOption[] getHyperVDisvoceryOptions(List<DiscoverySetting> settings) throws EdgeServiceFault {
		if(settings == null || settings.isEmpty()){
			return null;
		}
		int i = 0;
		DiscoveryHyperVOption[] hyperVOptions = new DiscoveryHyperVOption[settings.size()];
		for (DiscoverySetting hSetting : settings) {
			List<EdgeHyperV> hyperVList = new LinkedList<EdgeHyperV>();
			hyperVDao.as_edge_hyperv_getById(hSetting.getId(), hyperVList);
			if(hyperVList.isEmpty()){
				logger.error("[DiscoveryManager] getHyperVDisvoceryOptions(List<DiscoverySetting> settings) failed."
						+ " can't find the hyperv: "+hSetting.getHostname()+" the hyperv id is:"+hSetting.getId());
			}else {
				EdgeHyperV hyperV = hyperVList.get(0);
				hyperVOptions[i] = new DiscoveryHyperVOption();
				hyperVOptions[i].setPassword(hyperV.getPassword());
				hyperVOptions[i].setUsername(hyperV.getUsername());
				hyperVOptions[i].setServerName(hyperV.getHostname());
				hyperVOptions[i].setId(hyperV.getId());
				hyperVOptions[i].setJobType(1); // schedule job
				if (hyperV.getType() == HypervProtectionType.DEFAULT.getValue()) { // defect 175978
					hyperVOptions[i].setHypervProtectionType(HypervProtectionType.STANDALONE);
				} else {					
					hyperVOptions[i].setHypervProtectionType(HypervProtectionType.parse(hyperV.getType()));
				}
				GatewayEntity gateway = gatewayService.getGatewayByEntityId(hyperV.getId(), EntityType.HyperVServer );
				hyperVOptions[i].setGatewayId(gateway.getId());
				i++;
			}
		}
		return hyperVOptions;
	}

	private class EsxAutoDiscovery4AutoTriggerRunner implements Runnable {
		private DiscoveryESXOption[] esxOptions = null;
		public EsxAutoDiscovery4AutoTriggerRunner(DiscoveryESXOption[] esxOptions){
			this.esxOptions = esxOptions;
		}
		@Override
		public void run() {
			Date lastUpdateDate = null;
			synchronized (lastHyperVDiscDateSyncObj) {
				lastUpdateDate = lastESXDiscoveryDate;
			}
			if (isNeedDoDiscovery(lastUpdateDate)) {
				EsxDiscoveryService service = EsxDiscoveryService.getInstance();
				try {
					service.startDiscovery(esxOptions);
					while (true) {
						if (service.getDiscoveryMonitor().getDiscoveryPhase() != DiscoveryPhase.DISCOVERY_PHASE_END) {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								break;
							}
						} else {
							break;
						}
					}
				} catch (EdgeServiceFault e) {
					logger.warn("[EsxAutoDiscovery4AutoTriggerRunner]: Faild to do ESX auto discovery, because " + e.getMessage());
				}
			} else {
				logger.info("[EsxAutoDiscovery4AutoTriggerRunner]: Ignore this vCenter/ESX auto discovery interal, because from the last update date less than "
						+ autoDiscoveryInteralMinutes + " minutes.");
			}
		}
	}

	private class HyperVAutoDiscovery4AutoTriggerRunner implements Runnable {
		private DiscoveryHyperVOption[] hyperOptions = null;
		public HyperVAutoDiscovery4AutoTriggerRunner(DiscoveryHyperVOption[] hyperOptions){
			this.hyperOptions = hyperOptions;
		}
		@Override
		public void run() {
			Date lastUpdateDate = null;
			synchronized (lastESXDiscDateSyncObj) {
				lastUpdateDate = lastHyperVDiscoveryDate;
			}
			if (isNeedDoDiscovery(lastUpdateDate)) {
				HyperVDiscoveryService service = HyperVDiscoveryService.getInstance();
				try {
					service.startDiscovery(hyperOptions);
					while (true) {
						if (service.getDiscoveryMonitor().getDiscoveryPhase() != DiscoveryPhase.DISCOVERY_PHASE_END) {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								break;
							}
						} else {
							break;
						}
					}
				} catch (EdgeServiceFault e) {
					logger.warn("[HyperVAutoDiscovery4AutoTriggerRunner]: Faild to do Hyper-V auto discovery, because " + e.getMessage());
				}
			} else {
				logger.info("[HyperVAutoDiscovery4AutoTriggerRunner]: Ignore this Hyper-V auto discovery interal, because from the last update date less than "
						+ autoDiscoveryInteralMinutes + " minutes.");
			}
		}
	}
	
	private class EsxAutoDiscovery4MaualTriggerRunner implements Runnable {
		private List<DiscoverySetting> settings;
		public EsxAutoDiscovery4MaualTriggerRunner(List<DiscoverySetting> settings){
			this.settings = settings;
		}
		
		@Override
		public void run() {
			EsxDiscoveryService service = EsxDiscoveryService.getInstance();
			try {
				DiscoveryESXOption[] esxOptions = null;
				if(settings != null){
					esxOptions = DiscoveryManager.getESXDisvoceryOptions(this.settings);
				}else {
					esxOptions = DiscoveryManager.getESXDisvoceryOptions();
				}
				if(esxOptions != null){
					service.startDiscovery(esxOptions);
				}
			} catch (EdgeServiceFault e) {
				logger.warn("[EsxAutoDiscovery4MaualTriggerRunner]: Faild to do ESX manual discovery, because " + e.getMessage());
			}
		}
	}

	private class HyperVAutoDiscovery4MaualTriggerRunner implements Runnable {
		private List<DiscoverySetting> settings;
		public HyperVAutoDiscovery4MaualTriggerRunner(List<DiscoverySetting> settings){
			this.settings = settings;
		}
		@Override
		public void run() {
			HyperVDiscoveryService service = HyperVDiscoveryService.getInstance();
			try {
				DiscoveryHyperVOption[] hyperOptions =null;
				if(settings == null || settings.isEmpty()){
					hyperOptions = DiscoveryManager.getHyperVDisvoceryOptions();
				}else {
					hyperOptions = DiscoveryManager.getHyperVDisvoceryOptions(this.settings);
				}
				if(hyperOptions != null){
					service.startDiscovery(hyperOptions);
				}
			} catch (EdgeServiceFault e) {
				logger.warn("[HyperVAutoDiscovery4MaualTriggerRunner]: Faild to do Hyper-V manual discovery, because " + e.getMessage());
			}
		}
	}
}
