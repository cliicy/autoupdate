package com.ca.arcserve.edge.app.base.webservice.productdeploy;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;

public class ProductDeployScheduleJob implements Job{
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		DeployTargetDetail target = (DeployTargetDetail)dataMap.get(ProductDeployServiceImpl.DEPLOYTARGET);
		ProductDeployServiceImpl.getInstance().submitRemoteDeployImmediately(target,false);
	}
}
