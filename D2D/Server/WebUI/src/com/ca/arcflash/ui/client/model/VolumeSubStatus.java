package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.UIContext;

public final class VolumeSubStatus {
  public static final int EVSS_UNKNOWN = 0;              // Unknown Type
  public static final int EVSS_APP_SQL = 0x00000001;
  public static final int EVSS_APP_EXCH = 0x00000002;
  public static final int EVSS_SYSTEM = 0x00010000;
  public static final int EVSS_BOOT = 0x00020000;
  public static final int EVSS_PAGEFILE = 0x00040000;
  public static final int EVSS_ACTIVE = 0x00080000;
  public static final int EVSS_REMOVABLE = 0x00100000;
  public static final int EVSS_MOUNTEDFROMVHD = 0x00200000;
  public static final int EVSS_VOLUMEON2TDISK = 0x00400000;
  
  public static String getDisplayName(int type) {
	  
	    StringBuilder str = new StringBuilder();
		if((type & EVSS_APP_SQL) > 0)
			str.append(UIContext.Constants.volumeSubStatusSql()).append(", ");
			
		if((type & EVSS_APP_EXCH) > 0)
			str.append(UIContext.Constants.volumeSubStatusExchange()).append(", ");
		
		if((type & EVSS_SYSTEM) > 0)
			str.append(UIContext.Constants.volumeSubStatusSystem()).append(", ");
		
		if((type & EVSS_BOOT) > 0)
			str.append(UIContext.Constants.volumeSubStatusBoot()).append(", ");
		
		if((type & EVSS_PAGEFILE) > 0)
			str.append(UIContext.Constants.volumeSubStatusPagefile()).append(", ");
		
		if((type & EVSS_ACTIVE) > 0)
			str.append(UIContext.Constants.volumeSubStatusActive()).append(", ");
		
		if((type & EVSS_REMOVABLE) > 0)
			str.append(UIContext.Constants.volumeSubStatusRemovable()).append(", ");
		
		if((type & EVSS_MOUNTEDFROMVHD) > 0)
			str.append(UIContext.Constants.volumeSubStatusVHD()).append(", ");
		
		if((type & EVSS_VOLUMEON2TDISK) > 0)
			str.append(UIContext.Constants.volumeSubStatus2TDisk()).append(", ");
		
		if(str.length() > 0)
			return str.substring(0, str.length() - 2);
		
		return null;
	}
  
  public static boolean isMountedFrom2TDisk(VolumeModel model) {
	  return (model.getSubStatus() & EVSS_VOLUMEON2TDISK) > 0;
  }
  
  public static boolean isBootVolume(int type){
	  if((type & EVSS_BOOT) > 0){
		  return true;
	  }
	  else {
		return false;
	}
  }
  
  public static boolean isSystemVolume(int type){
	  if((type & EVSS_SYSTEM) > 0){
		  return true;
	  }
	  else{
		  return false;
	  }
  }
}
