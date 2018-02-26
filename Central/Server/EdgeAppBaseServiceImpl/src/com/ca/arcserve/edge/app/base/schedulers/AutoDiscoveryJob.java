package com.ca.arcserve.edge.app.base.schedulers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeDiscoverySetting;
import com.ca.arcserve.edge.app.base.appdaos.EdgeDiscoverySettingModel;
import com.ca.arcserve.edge.app.base.appdaos.EdgeScheduler_Schedule;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.scheduler.ISchedulerID2DataMapper;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ASBUSetting.ASBUSettingStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AutoDiscoverySetting;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.discovery.DiscoveryManager;

public class AutoDiscoveryJob implements IScheduleCallBack {
	private static Logger _log = Logger.getLogger(AutoDiscoveryJob.class);
	private static AutoDiscoveryJob instance = null;
	private IActivityLogService _iSyncActivityLog = new ActivityLogServiceImpl();
	private ActivityLog activity_log = new ActivityLog();
	private static IEdgeSettingDao edao = DaoFactory.getDao(IEdgeSettingDao.class);
	public static IScheduleCallBack getInstance() {
		return instance;
	}
	public static void init() {
		if (instance == null)
			instance = new AutoDiscoveryJob();
		ArrayList<Integer> scheduleIDs = new ArrayList<Integer>();
		AutoDiscoverySetting.SettingType[] settingTypes = AutoDiscoverySetting.SettingType.values();
		
		try {
			for (AutoDiscoverySetting.SettingType settingType:settingTypes){
				try {
					List<EdgeDiscoverySetting> settings = new ArrayList<EdgeDiscoverySetting>();
					List<EdgeScheduler_Schedule> schedules = new ArrayList<EdgeScheduler_Schedule>();
					IEdgeSettingDao edao = DaoFactory.getDao(IEdgeSettingDao.class);
					edao.as_edge_discovery_setting_get(settingType.ordinal(), settings, schedules);
					if (settings.size() > 0) {
						EdgeDiscoverySettingModel model = EdgeDiscoverySetting.getModel(settings.get(0).getXMLContent());
						if (model.getStatus() == ASBUSettingStatus.enabled
								&& (model.getaDStatus() == ASBUSettingStatus.enabled 
									|| model.getuDPStatus() == ASBUSettingStatus.enabled)) {
							scheduleIDs.add(settings.get(0).getScheduleid());
						}
					}
	
				} catch (JAXBException e) {
					_log.error("[Discovery Job init Exception,continue with empty IDs:]" + e.getMessage());
				}
			}
			
			SchedulerUtilsImpl.getInstance().registerIDs(instance, scheduleIDs);
		} catch (EdgeSchedulerException e) {
			_log.error("[Discovery Job init Exception]" + e.getMessage());
		}
	}

	@Override
	public int run(ScheduleData scheduleData, Object arg) {
		_log.debug("[Discovery Job is running]");
		// Add the activity log
		activity_log.setModule(Module.Common);
		
		try {
			List<EdgeDiscoverySetting> settings = new ArrayList<EdgeDiscoverySetting>();
			List<EdgeScheduler_Schedule> schedules = new ArrayList<EdgeScheduler_Schedule>();
			
			edao.as_edge_discovery_setting_get(AutoDiscoverySetting.SettingType.AD.ordinal(), settings, schedules);
				if (settings.size() > 0) {
					EdgeDiscoverySettingModel model = EdgeDiscoverySetting.getModel(settings.get(0).getXMLContent());
					if (model.getStatus() == ASBUSettingStatus.enabled
							&& (model.getaDStatus() == ASBUSettingStatus.enabled || model.getuDPStatus() == ASBUSettingStatus.enabled)) {
								DiscoveryManager.getInstance().doAutoDiscoveryForAD();
								// For test
								DiscoveryManager.getInstance().doAutoDiscoveryForEsx();
								DiscoveryManager.getInstance().doAutoDiscoveryForHyperV();
				}
			}

		} catch (JAXBException e) {
			activity_log.setSeverity(Severity.Error);
			activity_log.setTime(new Date(System.currentTimeMillis()));
			activity_log.setMessage(EdgeCMWebServiceMessages.getResource("autoDiscoveryJobRunFail"));
			try {
				_iSyncActivityLog.addLog(activity_log);
			} catch (EdgeServiceFault e2) {
				_log.error("Add Activity Log Error: "+e2.toString());
			}
			_log.error("[Discovery Job Run Error:]" + e.getMessage());
		}

		return 0;
	}
	@Override
	public ISchedulerID2DataMapper getID2DataMapper() {
		return EdgeDBIDMapper.getInstance();
	}

}
