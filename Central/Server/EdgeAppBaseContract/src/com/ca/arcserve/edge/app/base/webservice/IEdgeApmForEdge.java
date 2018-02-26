package com.ca.arcserve.edge.app.base.webservice;

import javax.jws.WebService;

import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.PMResponse;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.apm.ApmResponse;
import com.ca.arcserve.edge.app.base.webservice.contract.apm.BIPatchInfoEdge;
import com.ca.arcserve.edge.app.base.webservice.contract.apm.PatchInfoEdge;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author wanwe14
 *This interface involves the web servcie api for updating edge patch 
 */
@WebService(targetNamespace="http://webservice.apm.edge.arcserve.ca.com/")
public interface IEdgeApmForEdge {
	
	public AutoUpdateSettings GetEdgeUpdateSettings() throws EdgeServiceFault;
	
    public void SetEdgeUpdateSettings(AutoUpdateSettings updateConfig) throws EdgeServiceFault;
    
    public AutoUpdateSettings testDownloadServerConnnectionEdge(AutoUpdateSettings updateSettings) throws EdgeServiceFault;
    
     
	public int getPatchManagerStatusEdge() throws EdgeServiceFault;
	
	public PatchInfoEdge[] getPatchInfoesEdge() throws EdgeServiceFault;
	
	public int installPatchEdge() throws EdgeServiceFault;
	
	public ApmResponse[] checkUpdateEdge() throws EdgeServiceFault;
	
	//added by cliicy.luo to add Hotfix menu-item
	public ApmResponse[] checkHotfixEdge() throws EdgeServiceFault;
    public AutoUpdateSettings testDownloadBIServerConnnectionEdge(AutoUpdateSettings updateSettings) throws EdgeServiceFault;
    public void SetEdgeHotfixSettings(AutoUpdateSettings updateConfig) throws EdgeServiceFault;
	public PatchInfoEdge[] getHotfixInfoesEdge() throws EdgeServiceFault;
	public PatchInfoEdge[] getHotfix_Edgine() throws EdgeServiceFault;
	//added by cliicy.luo to add Hotfix menu-item
}
