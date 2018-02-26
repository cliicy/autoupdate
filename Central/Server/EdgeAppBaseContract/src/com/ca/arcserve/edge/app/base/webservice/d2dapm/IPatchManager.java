package com.ca.arcserve.edge.app.base.webservice.d2dapm;

import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
//import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface IPatchManager {
    //public PatchInfo CheckUpdates() throws EdgeServiceFault;
    public AutoUpdateSettings GetUpdateSettings() throws EdgeServiceFault;
    public void SetUpdateSettings(AutoUpdateSettings in_UpdateConfig) throws EdgeServiceFault;
    //public AutoUpdateSettings testDownloadServerConnnection(AutoUpdateSettings in_updateSettings) throws EdgeServiceFault;
}
