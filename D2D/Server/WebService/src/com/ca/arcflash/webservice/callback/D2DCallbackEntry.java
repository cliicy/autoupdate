package com.ca.arcflash.webservice.callback;

import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.callback.MergeFailureInfo;
import com.ca.arcflash.webservice.jni.model.JJobContext;
import com.ca.arcflash.webservice.service.CallbackService;

public class D2DCallbackEntry
{
	private static final Logger logger = Logger.getLogger(D2DCallbackEntry.class);
	
	public static long mergeFailureCallback(MergeFailureInfo info)
	{
		logger.debug("D2D callback for merge job failed. ");
		
		try
		{
			if (logger.isDebugEnabled())
			{
				logger.debug(StringUtil.convertObject2String(info));
			}
			
			CallbackService.getInstance().notifyMergeFailure(info);
		}
		catch (Exception e)
		{
			logger.error("Exception caught when notifyMergeFailed", e);
		}	
		
		return 0;
	}
	
	public static long submitVAppChildVMBackupCallback(List<JJobContext> jobs) {
		logger.debug("VApp child VM backup call back is submitted");
		return CallbackService.getInstance().submitVAppChildVMBackup(jobs);
	}
}
