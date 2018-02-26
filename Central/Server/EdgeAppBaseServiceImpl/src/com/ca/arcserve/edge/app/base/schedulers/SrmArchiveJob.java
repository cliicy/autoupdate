package com.ca.arcserve.edge.app.base.schedulers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeSRMArchiveSetting;
import com.ca.arcserve.edge.app.base.appdaos.EdgeScheduler_Schedule;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSchedulerDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSrmDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.scheduler.ISchedulerID2DataMapper;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;

public class SrmArchiveJob {
	public static final int WEEK_DAYS  = 7;
	public static final int YEAR_MONTH = 12;

	public static class SrmArchiveWeeklyJob implements IScheduleCallBack {
		private static Logger _log = Logger.getLogger(SrmArchiveWeeklyJob.class);
		public static String data = "SrmArchive Weekly Job";
		private static SrmArchiveWeeklyJob instance = null;
		@Override
		public ISchedulerID2DataMapper getID2DataMapper() {
			// TODO Auto-generated method stub
			return EdgeDBIDMapper.getInstance();
		}
		public static void init(){
			if(instance==null)
				instance  = new SrmArchiveWeeklyJob();
			try {
				ArrayList<Integer> scheduleIDs = new ArrayList<Integer>();

				// populate SRM weekly event schedule ID
				List<EdgeScheduler_Schedule> schedules = new ArrayList<EdgeScheduler_Schedule>();
				IEdgeSchedulerDao edao = DaoFactory.getDao(IEdgeSchedulerDao.class);
				edao.as_edge_schedule_list(0, schedules);
				for (EdgeScheduler_Schedule schedule : schedules) {
					if (schedule.getActionType() == SrmJob.SRM_JOB_ACTION_TYPE_WEEKLY_EVENT) {
						scheduleIDs.add(schedule.getID());
						break;
					}
				}

				SchedulerUtilsImpl.getInstance().registerIDs(instance, scheduleIDs);
			} catch (EdgeSchedulerException e) {
				_log.debug("[SRM weekly event init Exception]" + e.getMessage());
			}
		}

		public static IScheduleCallBack getInstance(){
			return instance;
		}

		@SuppressWarnings("deprecation")
		@Override
		public int run(ScheduleData scheduleData, Object arg) {
			data = "SRM Weekly Archiving ";
			Date d = new Date(System.currentTimeMillis());
			data += d.toString();
			_log.debug("+++++++++++++" + data + "+++++++++++++++++++++++++");


			// Read the archive setting from DB
			List<EdgeSRMArchiveSetting> settingList = new ArrayList<EdgeSRMArchiveSetting>();
			IEdgeSettingDao edao = DaoFactory.getDao(IEdgeSettingDao.class);
			IEdgeSrmDao srmDao = DaoFactory.getDao(IEdgeSrmDao.class);
			// enum all settings
			int type = 0;
			int action = 0;

			java.util.Date curDate = new java.util.Date();
			edao.as_edge_srm_archive_setting_get(type, action, settingList);
			for(EdgeSRMArchiveSetting setting : settingList) {
				// filter the action which isn't weekly
				if (EdgeSRMArchiveSetting.MapIntegerToAction(setting.getAction())
						!= EdgeSRMArchiveSetting.SrmArchiveAction.WEEKLY) {
					continue;
				}

				if (EdgeSRMArchiveSetting.MapIntegerToType(setting.getType())
						== EdgeSRMArchiveSetting.SrmArchiveType.PKI_TRENDING) {
					// call archive pki function
					_log.debug("PKI: " + setting.getYear() + "," +
							setting.getMonth() + "," +
							setting.getWeek());
					int keepDay = WEEK_DAYS;
					srmDao.spsrmedgearchivePKITrending(keepDay, curDate);
					continue;
				}

				if (EdgeSRMArchiveSetting.MapIntegerToType(setting.getType())
						== EdgeSRMArchiveSetting.SrmArchiveType.APP_TRENDING) {
					// call archive app trending function
					_log.debug("App Trending: " + setting.getYear() + "," +
							setting.getMonth() + "," +
							setting.getWeek());
					int keepDay = setting.getWeek() * WEEK_DAYS;
					srmDao.spsrmedgearchiveAppTrendD2W(keepDay, curDate);
					continue;
				}

				if (EdgeSRMArchiveSetting.MapIntegerToType(setting.getType())
						== EdgeSRMArchiveSetting.SrmArchiveType.VOLUME_TRENDING) {
					// call archive volume trending function
					_log.debug("Volume Trending: " + setting.getYear() + "," +
							setting.getMonth() + "," +
							setting.getWeek());
					int keepDay = setting.getWeek() * WEEK_DAYS;
					srmDao.spsrmedgearchiveVolTrendD2W(keepDay, curDate);
					srmDao.spsrmedgearchiveBackupSizeTrendD2W(keepDay, curDate);
					continue;
				}

			}

			return SrmJob.SRM_JOB_EC_SUCCEED;
		}
	}

	public static class SrmArchiveMonthlyJob implements IScheduleCallBack {
		private static Logger _log = Logger.getLogger(SrmArchiveWeeklyJob.class);
		public static String data = "SrmArchive Monthly Job";
		private static SrmArchiveMonthlyJob instance = null;
		@Override
		public ISchedulerID2DataMapper getID2DataMapper() {
			// TODO Auto-generated method stub
			return EdgeDBIDMapper.getInstance();
		}
		public static void init(){
			if(instance==null)
			instance  = new SrmArchiveMonthlyJob();
			try {
				ArrayList<Integer> scheduleIDs = new ArrayList<Integer>();

				// populate SRM monthly event schedule ID
				List<EdgeScheduler_Schedule> schedules = new ArrayList<EdgeScheduler_Schedule>();
				IEdgeSchedulerDao edao = DaoFactory.getDao(IEdgeSchedulerDao.class);
				edao.as_edge_schedule_list(0, schedules);
				for (EdgeScheduler_Schedule schedule : schedules) {
					if (schedule.getActionType() == SrmJob.SRM_JOB_ACTION_TYPE_MONTHLY_EVENT) {
						scheduleIDs.add(schedule.getID());
						break;
					}
				}

				SchedulerUtilsImpl.getInstance().registerIDs(instance, scheduleIDs);
			} catch (EdgeSchedulerException e) {
				_log.debug("[SRM monthly event init Exception]" + e.getMessage());
			}
		}

		public static IScheduleCallBack getInstance(){
			return instance;
		}

		@SuppressWarnings("deprecation")
		@Override
		public int run(ScheduleData scheduleData, Object arg) {
			data = "SRM Monthly Archiving ";
			Date d = new Date(System.currentTimeMillis());
			data += d.toString();
			_log.debug("+++++++++++++" + data + "+++++++++++++++++++++++++");

			java.util.Date curDate = new java.util.Date();


			// Read the archive setting from DB
			List<EdgeSRMArchiveSetting> settingList = new ArrayList<EdgeSRMArchiveSetting>();
			IEdgeSettingDao edao = DaoFactory.getDao(IEdgeSettingDao.class);
			IEdgeSrmDao srmDao = DaoFactory.getDao(IEdgeSrmDao.class);


			// enum all settings
			int type = 0;
			int action = 0;
			edao.as_edge_srm_archive_setting_get(type, action, settingList);
			for(EdgeSRMArchiveSetting setting : settingList) {
				// filter the action which isn't Monthly
				if (EdgeSRMArchiveSetting.MapIntegerToAction(setting.getAction())
						!= EdgeSRMArchiveSetting.SrmArchiveAction.MONTHLY) {
					continue;
				}


				if (EdgeSRMArchiveSetting.MapIntegerToType(setting.getType())
						== EdgeSRMArchiveSetting.SrmArchiveType.APP_TRENDING) {
					// call archive app trending function
					_log.debug("App Trending: " + setting.getYear() + "," +
							setting.getMonth() + "," +
							setting.getWeek());
					int keepMonth = setting.getMonth();
					srmDao.spsrmedgearchiveAppTrendW2M(keepMonth, curDate);
					continue;
				}

				if (EdgeSRMArchiveSetting.MapIntegerToType(setting.getType())
						== EdgeSRMArchiveSetting.SrmArchiveType.VOLUME_TRENDING) {
					// call archive volume trending function
					_log.debug("Volume Trending: " + setting.getYear() + "," +
							setting.getMonth() + "," +
							setting.getWeek());
					int keepMonth = setting.getMonth();
					srmDao.spsrmedgearchiveVolTrendW2M(keepMonth, curDate);
					srmDao.spsrmedgearchiveBackupSizeTrendW2M(keepMonth, curDate);
					continue;
				}

			}

			// Deal with Yearly records
			_log.debug("---Yearly process---");
			for(EdgeSRMArchiveSetting setting : settingList) {
				// filter the action which isn't Yearly
				if (EdgeSRMArchiveSetting.MapIntegerToAction(setting.getAction())
						!= EdgeSRMArchiveSetting.SrmArchiveAction.YEARLY) {
					continue;
				}

				if (EdgeSRMArchiveSetting.MapIntegerToType(setting.getType())
						== EdgeSRMArchiveSetting.SrmArchiveType.APP_TRENDING) {
					// call archive app trending function
					_log.debug("App Trending: " + setting.getYear() + "," +
							setting.getMonth() + "," +
							setting.getWeek());
					int keepYear = setting.getYear();
					srmDao.spsrmedgecleanupSoftwareInfo(keepYear);
					srmDao.spsrmedgecleanupAppTrending(keepYear, curDate);
					continue;
				}

				if (EdgeSRMArchiveSetting.MapIntegerToType(setting.getType())
						== EdgeSRMArchiveSetting.SrmArchiveType.VOLUME_TRENDING) {
					// call archive volume trending function
					_log.debug("Volume Trending: " + setting.getYear() + "," +
							setting.getMonth() + "," +
							setting.getWeek());
					int keepYear = setting.getYear();
					srmDao.spsrmedgecleanupVolTrending(keepYear, curDate);
					srmDao.spsrmedgecleanupBackupSizeTrending(keepYear, curDate);
					continue;
				}
			}

			return SrmJob.SRM_JOB_EC_SUCCEED;
		}
	}

}
