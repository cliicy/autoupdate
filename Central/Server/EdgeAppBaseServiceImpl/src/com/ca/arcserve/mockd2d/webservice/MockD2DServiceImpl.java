package com.ca.arcserve.mockd2d.webservice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ca.arcflash.common.IDirectWebServiceImpl;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.FlashServiceImpl;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.VSphereBackupConfigurationXMLDAO;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.common.D2DFacade;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.d2dapm.PatchManager;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyEditSession;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyEditSessionManager;

@WebService(endpointInterface="com.ca.arcflash.webservice.IFlashServiceV")
public class MockD2DServiceImpl extends FlashServiceImpl implements IDirectWebServiceImpl
{
	private static Logger logger = Logger.getLogger( MockD2DServiceImpl.class );

	private VSphereBackupConfigurationXMLDAO vShpereXmlDAO;

	//////////////////////////////////////////////////////////////////////////

	public MockD2DServiceImpl()
	{
		edgeFlag = true;
		this.vShpereXmlDAO = new VSphereBackupConfigurationXMLDAO();
		
		setEnableSessionCheckV2( false );
	}


	//////////////////////////////////////////////////////////////////////////
	//
	//  IFlashService Methods
	//
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void checkSession() {
		// Never check session for mock implementation
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public Account getAdminAccount()
	{
		return null;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public Date getServerTime()
	{
		TimeZone timeZone = TimeZone.getTimeZone( "UTC" );
		Calendar calendar = Calendar.getInstance( timeZone );
		calendar.setTimeInMillis( System.currentTimeMillis() );
		return calendar.getTime();
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public VersionInfo getVersionInfo()
	{
		VersionInfo versionInfo = new VersionInfo();

		TimeZone timeZone = Calendar.getInstance().getTimeZone();
		versionInfo.setTimeZoneID( timeZone.getID() );
		versionInfo.setTimeZoneOffset( timeZone.getOffset( System.currentTimeMillis() ) );
		versionInfo.setLocale( System.getProperty( "user.language" ) );
		versionInfo.setCountry(System.getProperty("user.country"));
		versionInfo.setProductType("0"); // no use for edge
		
		versionInfo.setMajorVersion( "16" );
		versionInfo.setMinorVersion( "0" );
		versionInfo.setBuildNumber( "0" );
		versionInfo.setDataFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat());
		
		List<String> volumeList = new ArrayList<String>();
		for (char ch = 'C'; ch <= 'Z'; ch ++)
			volumeList.add( ch + ":" );
		versionInfo.setLocalDriverLetters( volumeList.toArray( new String[0] ) );

		versionInfo.setLocalADTPackage( 0 );

		return versionInfo;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public Volume[] getVolumes()
	{
		return null;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public int validateUserByUUID( String uuid )
	{
		return 0;
	}

	@Override
	public boolean checkBLILic() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public AutoUpdateSettings testDownloadServerConnection(AutoUpdateSettings in_TestSettings) {
		try {
			return PatchManager.getInstance().testDownloadServerConnnectionEdge(in_TestSettings);
		} catch (EdgeServiceFault e) {
			return null;
		}
	}

	//added by cliicy.luo to add Hotfix menu-item
	@Override
	public AutoUpdateSettings testBIDownloadServerConnection(AutoUpdateSettings in_TestSettings) {
		try {
			return PatchManager.getInstance().testDownloadBIServerConnnectionEdge(in_TestSettings);
		} catch (EdgeServiceFault e) {
			return null;
		}
	}
	//added by cliicy.luo to add Hotfix menu-item
	
	private PolicyEditSession getEditSession()
	{
		return PolicyEditSessionManager.getInstance().getSession( this.getSession() );
	}
	
	@Override
	public VMBackupConfiguration getVMBackupConfiguration(VirtualMachine vm) {
		try {
			Document xmlDocument = (Document)getEditSession().getValue(PolicyEditSession.Keys.VMBackupSettings);
			return this.vShpereXmlDAO.XMLDocumentToVMBackupConfiguration(xmlDocument);
		} catch (Exception e) {
			return null;
		}
	}

	//////////////////////////////////////////////////////////////////////////
//	For issue 20088359, following statements are removed
//	@Override
//	public boolean isLocalHost(String host)
//	{
//		// This should always return false. Since the D2D UI is checking
//		// whether the specific host is the host running D2D, obviously,
//		// it's always not.
//		//
//		// Pang, Bo (panbo01)
//		// 2010-11-26
//		
//		return false;
//	}
//	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public long validateBackupConfiguration( BackupConfiguration configuration )
	{
		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public long validatePreferences( PreferencesConfiguration in_ReferencesConfig )
	{
		return 0;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	public long validateArchiveConfiguration( ArchiveConfiguration archiveConfig )
	{
		//FIXME we have no validate 
		try
		{
			D2DFacade.getInstance().validateArchiveSource( archiveConfig );
			return 0;
		}
		catch (ServiceException e)
		{
			throw convertServiceException2AxisFault(e);
		}
		catch (Throwable e)
		{
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault("Unhandled exception in web service",
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	@Override
	public long validateScheduledExportConfiguration(
		ScheduledExportConfiguration configuration )
	{
		return 0;
		//We cannot use the backend validation logics here, because it will create dest path
		//return super.validateScheduledExportConfiguration(configuration);
	}	


	public List<RpsHost>getRpsNodes() {
		List<EdgeConnectInfo> edgeHosts = new ArrayList<EdgeConnectInfo>();
		List<RpsHost> rpsHosts = new ArrayList<RpsHost>();
		IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		hostDao.as_edge_rps_host_list(edgeHosts);
		for(EdgeConnectInfo host : edgeHosts){
			rpsHosts.add(ConvertEdgeHostToRpsHost(host));
		}
		return rpsHosts;
	}
	private RpsHost ConvertEdgeHostToRpsHost(EdgeConnectInfo host){
		RpsHost rpsHost = new RpsHost();
		rpsHost.setRhostname(host.getRhostname());
		rpsHost.setUsername(host.getUsername());
		rpsHost.setPassword(host.getPassword());
		rpsHost.setPort(host.getPort());
		if(host.getProtocol() == Protocol.Http.ordinal())
			rpsHost.setHttpProtocol(true);
		else
			rpsHost.setHttpProtocol(false);
		return rpsHost;
	}
}
