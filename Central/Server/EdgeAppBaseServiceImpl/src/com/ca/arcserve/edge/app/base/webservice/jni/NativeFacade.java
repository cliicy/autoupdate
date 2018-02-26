package com.ca.arcserve.edge.app.base.webservice.jni;

import java.util.List;

import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.webservice.jni.model.ComputerNameType;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;
import com.ca.arcserve.edge.webservice.jni.model.HttpDownloadResult;
import com.ca.arcserve.edge.webservice.jni.model.HttpProxySettings;
import com.ca.arcserve.edge.webservice.jni.model.IDownloadStatusCallback;
import com.ca.arcserve.edge.webservice.jni.model.IHttpDownloadCallback;

public interface NativeFacade extends IRemoteNativeFacade {
	
	List<String> getDcList(String domainName) throws EdgeServiceFault;
	
	int validateUser(String username, String password, String domain) throws EdgeServiceFault;
	
	int deploy(String serverListFile);
	
	String AFEncryptString(String str);
	String AFDecryptString(String str);
	
	String getWindowsDirectory();
	
	EdgeAccount getEdgeAccount();
	void saveEdgeAccount(EdgeAccount account) throws EdgeServiceFault;

	int createMailSlot( String mailSlotString ) throws EdgeServiceFault;
	
	PatchInfo loadD2DPatchInfoFromDll(String dllFilePath) throws EdgeServiceFault;
	
	int isDeployingD2D();
	
	
	public int getD2DDeployProcessStatus( String path );
	public int updateASBUDomainName(String cmdDir,String newName, String usrName, String usrPwd, String carootPwd);
	public int updateASBUPrimaryServerName(String cmdDir,String usrName, String usrPwd, String carootPwd);
	
	public static int S_OK = 0;
	
	long urlDownloadToFile( String url, String saveAs, IDownloadStatusCallback statusCallback );
	
	boolean httpDownload( String server, int port, boolean isHttps, String serverPath, String saveAs,
		IHttpDownloadCallback callback, HttpDownloadResult result );
	
	HttpProxySettings getIEProxySettings( String username, String password ) throws Exception;
	
	public long getAccountDomain(
		String accountName, String domain, String password, StringBuffer normalizedDomain );
	
	public boolean getComputerName( ComputerNameType nameType, StringBuffer name );
}
