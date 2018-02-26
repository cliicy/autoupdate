package com.ca.arcserve.edge.app.base.schedulers.policymanagement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.scheduler.EdgeSchedulerException;
import com.ca.arcserve.edge.app.base.scheduler.IScheduleCallBack;
import com.ca.arcserve.edge.app.base.scheduler.ISchedulerID2DataMapper;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentScheduler;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData;
import com.ca.arcserve.edge.app.base.webservice.contract.scheduler.ScheduleData.RepeatUnitlType;

public class PolicyDeploymentJob implements IScheduleCallBack
{
	class InMemorySchedulerIdDataMap implements ISchedulerID2DataMapper
	{
		private Map<Integer, ScheduleData> idDataMap;
		
		//////////////////////////////////////////////////////////////////////
		public InMemorySchedulerIdDataMap()
		{
			this.idDataMap = new HashMap<Integer, ScheduleData>();
		}

		//////////////////////////////////////////////////////////////////////
		@Override
		public ScheduleData getSchedule( Integer scheduleID )
		{
			return this.idDataMap.get( scheduleID );
		}

		//////////////////////////////////////////////////////////////////////
		@Override
		public List<ScheduleData> getSchedules( List<Integer> scheduleIDs )
		{
			List<ScheduleData> dataList = new LinkedList<ScheduleData>();
			for (Integer id : scheduleIDs)
			{
				ScheduleData data = this.idDataMap.get( id );
				if (data != null)
				{
					dataList.add( data );
				}
				else
				{
					logger.error(
						"PolicyDeploymentJob.InMemorySchedulerIdDataMap: No such schedule ID." );
				}
			}
			
			return dataList;
		}

		//////////////////////////////////////////////////////////////////////
		@Override
		public void putSchedules( Integer id, ScheduleData scheduleData )
		{
			this.idDataMap.put( id, scheduleData );
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private static PolicyDeploymentJob instance = null;
	private static Logger logger = Logger.getLogger( PolicyDeploymentJob.class );
	
	private ISchedulerID2DataMapper schedulerIdDataMap;
	
	//////////////////////////////////////////////////////////////////////////
	
	private PolicyDeploymentJob()
	{
		this.schedulerIdDataMap = new InMemorySchedulerIdDataMap();
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public static synchronized PolicyDeploymentJob getInstance()
	{
		if (instance == null)
			instance = new PolicyDeploymentJob();
		
		return instance;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public void initialize()
	{
		try
		{
			PolicyDeploymentScheduler.getInstance().initializate();
			
//			int scheduleId = 1;
//			
//			ScheduleData scheduleData = new ScheduleData();
//			scheduleData.setScheduleID( scheduleId );
//			scheduleData.setScheduleDescription( "Policy deployment job" );
//			scheduleData.setRepeatUntilType( RepeatUnitlType.forever );
//			
//			this.schedulerIdDataMap.putSchedules( scheduleId, scheduleData );
//			
//			SchedulerUtilsImpl.getInstance().registerIDs( this, new LinkedList<Integer>() );
			
			logger.info( "PolicyDeploymentJob: Initilization OK." );
		}
		catch (Exception e)
		{
			logger.error( "PolicyDeploymentJob: Initilization failed.", e );
		}
	}
	
	public void destroy(){
		PolicyDeploymentScheduler.getInstance().destroy();
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public ISchedulerID2DataMapper getID2DataMapper()
	{
		return this.schedulerIdDataMap;
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public int run( ScheduleData scheduleData, Object args )
	{
		PolicyDeploymentScheduler.getInstance().doDeploymentNow();
		return 0;
	}

}
