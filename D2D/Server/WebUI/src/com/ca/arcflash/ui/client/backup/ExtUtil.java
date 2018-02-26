package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
public class ExtUtil {
	    public static int backupSettingsEditable(boolean isEnabled) {
	    	SettingPresenter.getInstance().getCommonSettings().enableEdit(isEnabled); 
	    	return 0;
	    }
	    public static native void exportStaticMethod() /*-{
	       $wnd.__caagt_bstEditable =
	          $entry(@com.ca.arcflash.ui.client.backup.ExtUtil::backupSettingsEditable(Z));
	    }-*/;
	
}
