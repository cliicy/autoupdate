package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;

public class RestoreConstants {
	//catalog status
	public static final int FSCAT_DISABLED = 0x03;
	public static final int FSCAT_PENDING = 0x02;
	public static final int FSCAT_FINISH =  0x01;
	public static final int FSCAT_FAIL = 0x00;
	public static final int FSCAT_NOTCREATE =  -0x01;
	
	//job type
	public final static int Unknown = -1;
	public final static int Full = 0;
	public final static int Incremental = 1;
	public final static int Resync = 2;
	
	public static String getBackupJobType(final int type) {
		String ret = UIContext.Constants.backupTypeUnknown(); 
		switch(type) {
		case Unknown:
			ret = UIContext.Constants.backupTypeUnknown();
			break;
		case Full:
			ret = UIContext.Constants.backupTypeFull();
			break;
		case Incremental:
			ret = UIContext.Constants.backupTypeIncremental();
			break;
		case Resync:
			ret = UIContext.Constants.backupTypeResync();
			break;
		}
		return ret;
	}
	
	public static String getFSCatalogStatusMsg(int fsCatalogStatus){
		if(fsCatalogStatus == FSCAT_FAIL){
			return UIContext.Constants.restoreCatalogFailed();
		}
		else if(fsCatalogStatus == FSCAT_NOTCREATE){
			return UIContext.Constants.restoreCatalogNotCreatedVMPowerOff();
		}
		else if(fsCatalogStatus == FSCAT_FINISH){
			return UIContext.Constants.restoreCatalogCreated();
		}
		else if(fsCatalogStatus == FSCAT_PENDING){
			return UIContext.Constants.restoreCatalogPending();
		}
		else if(fsCatalogStatus ==  FSCAT_DISABLED){
			return UIContext.Constants.restoreCatalogDisabled();
		}
		else{
			return UIContext.Constants.NA();
		}
		
	}
	
	public static String getFSCatalogStatusMsgForVSphere(int fsCatalogStatus){
		if(fsCatalogStatus == FSCAT_FAIL){
			return UIContext.Constants.restoreCatalogFailed();
		}
		else if(fsCatalogStatus == FSCAT_FINISH){
			return UIContext.Constants.restoreCatalogCreated();
		}
		else if(fsCatalogStatus == FSCAT_PENDING){
			return UIContext.Constants.restoreCatalogPending();
		}
		else if(fsCatalogStatus == FSCAT_NOTCREATE){
			return UIContext.Constants.restoreCatalogNotCreatedVMPowerOff();
		}
		else if(fsCatalogStatus == FSCAT_DISABLED){
			return UIContext.Constants.restoreCatalogDisabled();
		}
		else{
			return UIContext.Constants.NA();
		}
		
	}
}
