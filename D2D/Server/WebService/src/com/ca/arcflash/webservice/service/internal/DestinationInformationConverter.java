package com.ca.arcflash.webservice.service.internal;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.jni.model.JBackupDestinationInfo;

public class DestinationInformationConverter {
	
	public DestinationCapacity convert(JBackupDestinationInfo source){
		DestinationCapacity result = new DestinationCapacity();
		result.setCatalogSize(StringUtil.string2Long(source.getCatalogSize(), 0));
		result.setFullBackupSize(StringUtil.string2Long(source.getFullBackupSize(), 0));
		result.setIncrementalBackupSize(StringUtil.string2Long(source.getIncrementalBackupSize(), 0));
		result.setResyncBackupSize(StringUtil.string2Long(source.getResyncBackupSize(), 0));
		result.setTotalFreeSize(StringUtil.string2Long(source.getTotalFreeSize(), 0));
		result.setTotalVolumeSize(StringUtil.string2Long(source.getTotalSize(), 0));
		return result;
	}


}
