package com.ca.arcserve.edge.app.base.webservice.dataSync;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.RPMScheduleData;

public class RecoveryPointSyncManager implements Job {
	private static RPMScheduleData rpmConfig;
	private static int repeatIntervalSecond; 
	private static String RPMConfigFileName ="node_recoverypoint_summary_sync_configure.xml" ;
	private static Logger logger = Logger.getLogger(RecoveryPointSyncManager.class); 

	private static String JobTriggerName = "recovery-point-datasummary-manager";
	private static RecoveryPointSyncManager rpManager = new RecoveryPointSyncManager();
	public static RecoveryPointSyncManager getInstance() {
		return rpManager;
	}


	@SuppressWarnings("deprecation")
	public void init() {
		try {
			getConfigure();
			repeatIntervalSecond = 24*3600/rpmConfig.getRepeatEachDay();
			
			Calendar cstart = Calendar.getInstance();
			Date current = new Date();
			Date startDate = new Date( current.getYear(), current.getMonth(),  current.getDate(), rpmConfig.getStartHour(), rpmConfig.getStartMinute() ); 
			cstart.setTime( startDate );
			
			while( true ) {
				if( current.after( cstart.getTime() ) ) {
					cstart.add(Calendar.SECOND, repeatIntervalSecond);
				}
				else  {
					break;
				}
			}
			cstart.add(Calendar.SECOND, 1);
			setTrigger( cstart.getTime() );
			
			//RecoveryPointHandler.getInstance().syncRecoveryPointTotalSize();//test only
		} 
		catch (Throwable e) {
			logger.error("rpm service " +  e);
		}
	}
	private void getConfigure() {
		
		String configFolder = CommonUtil.getConfigurationFolder(EdgeApplicationType.CentralManagement);
		File rpmFile = new File(configFolder + RPMConfigFileName );
		
		if (!rpmFile.exists()) {
			logger.debug("rpm service - rpm configure file does not exist, path = " + rpmFile.getPath());
			rpmConfig = new RPMScheduleData();
			
			try {
				JAXB.marshal(rpmConfig, rpmFile);
			} catch (Exception e) {
				logger.error("rpm service -  generate default rpm configuration failed.", e);
			}
		} 
		else {
			try {
				rpmConfig = JAXB.unmarshal(rpmFile, RPMScheduleData.class);
			} catch (Exception e) {
				logger.error("rpm service -  unmarshal rpm configuration from xml file failed.", e);
				rpmConfig = new RPMScheduleData();
			}
		}
		if( rpmConfig !=null && rpmConfig.getRepeatEachDay() > 100 ) {
			rpmConfig.setRepeatEachDay(100);
			logger.warn( "rpm service -  cannot set too frequent recovery point sync! " );
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.info("rpm service -job fire time: " + context.getFireTime().toGMTString() );
		adjustSchedule( context );
		RecoveryPointHandler.getInstance().syncRecoveryPointTotalSize();
	}
	
	private void setTrigger( Date startTime ){
		try {
			Scheduler scheduler = SchedulerUtilsImpl.getScheduler();
			scheduler.unscheduleJob(new TriggerKey(JobTriggerName, null));
			SimpleTrigger trigger = new  SimpleTriggerImpl( JobTriggerName , null, startTime, null,
					SimpleTrigger.REPEAT_INDEFINITELY , repeatIntervalSecond * 1000L	);
			JobDetail jobDetail = new JobDetailImpl( JobTriggerName , null, this.getClass() );
			scheduler.scheduleJob(jobDetail, trigger);
		} 
		catch (Exception e) {
			logger.error("rpm service - set trigger time fail!", e);
		}
	}
	/**
	 * after run some times ; the schedule may not guarantee to run in the start time specified in configuration file; because of the
	 * repeat time cannot divisible by day-time without remainder; so we adjust the start time if the offset more than 3 minute; 
	 */
	private void adjustSchedule( JobExecutionContext context ) {
		if( rpmConfig != null ) {
			Date nextFireTime = context.getNextFireTime();
			Date current = new Date();
			Date todayStartTime = new Date( current.getYear(), current.getMonth(),  current.getDate(), rpmConfig.getStartHour(), rpmConfig.getStartMinute() ); 
			Calendar cstart = Calendar.getInstance();
			
			cstart.setTime( todayStartTime );
			cstart.add( Calendar.DATE, 1 ); 
			Date nextDayStartTime = cstart.getTime();

			if( 	Math.abs(nextFireTime.getTime()- todayStartTime.getTime() ) <  repeatIntervalSecond/3 && 
					Math.abs(nextFireTime.getTime()- todayStartTime.getTime() ) > 3*60*1000 ) {
				setTrigger( todayStartTime );
			}
			else if ( 	Math.abs(nextFireTime.getTime()- nextDayStartTime.getTime() ) <  repeatIntervalSecond/3 && 
					Math.abs(nextFireTime.getTime()- nextDayStartTime.getTime() ) > 3*60*1000 ) {
				setTrigger( nextDayStartTime );
			} 
		}
	}
}
