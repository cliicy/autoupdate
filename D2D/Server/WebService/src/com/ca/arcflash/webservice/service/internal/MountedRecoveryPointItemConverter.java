package com.ca.arcflash.webservice.service.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.restore.MountedRecoveryPointItem;
import com.ca.arcflash.webservice.jni.model.JMountedRecoveryPointItem;

public class MountedRecoveryPointItemConverter {
	private int getTimeZoneOffset(Date date) {
		TimeZone tz = Calendar.getInstance().getTimeZone();
		int offset = tz.getOffset(date.getTime());
		return offset;
	}
	
	public MountedRecoveryPointItem convert(JMountedRecoveryPointItem jMountedItem){
		MountedRecoveryPointItem mountedItem = new MountedRecoveryPointItem();
		mountedItem.setMountDiskSignature(jMountedItem.getMountDiskSignature());
		mountedItem.setMountPath(removeSlash(jMountedItem.getMountPath()));
		mountedItem.setMountFlag(jMountedItem.getMountFlag());
		mountedItem.setRecoveryPointPath(jMountedItem.getRecoveryPointPath());
		//mountedItem.setRecoveryPointDate(BackupConverterUtil.string2Date(jMountedItem.getDate()+" "+jMountedItem.getTime()));
		mountedItem.setSessionID(jMountedItem.getSessionID());
		mountedItem.setSessionGuid(jMountedItem.getSesionGuid());
		//mountedItem.setTimeZoneOffset(getTimeZoneOffset(mountedItem.getRecoveryPointDate()));
		mountedItem.setVolumeGuid(jMountedItem.getVolumeGuid());
		mountedItem.setVolumePath(removeSlash(jMountedItem.getVolumePath()));
		mountedItem.setVolumeSize(jMountedItem.getVolumeSize());
		mountedItem.setReadOnly(true);
		
		if((StringUtil.isEmptyOrNull(jMountedItem.getDate())) || (StringUtil.isEmptyOrNull(jMountedItem.getTime()))){
			mountedItem.setRecoveryPointDate(null);
			mountedItem.setTimeZoneOffset(0);
		}
		else{
			mountedItem.setRecoveryPointDate(BackupConverterUtil.string2Date(jMountedItem.getDate()+" "+jMountedItem.getTime()));
			mountedItem.setTimeZoneOffset(getTimeZoneOffset(mountedItem.getRecoveryPointDate()));
		}
		return mountedItem;
	}
	
	public String removeSlash(String source){
		if(source.endsWith("\\"))
			return source.substring(0, source.length()-1);
		else 
			return source;
	}
}
