package com.ca.arcserve.edge.app.base.common;

import org.w3c.dom.Document;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.subscription.SubscriptionConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.rps.webservice.data.policy.RPSConfiguration;
import com.ca.arcflash.rps.webservice.util.RpsConfigXMLParser;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.internal.ArchiveConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.BackupConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.SubscriptionConfigurationXMLDAO;
import com.ca.arcflash.webservice.service.internal.VSphereBackupConfigurationXMLDAO;
import com.ca.arcflash.webservice.util.ArchiveConfigXMLParser;
import com.ca.arcflash.webservice.util.BackupConfigXMLParser;
import com.ca.arcflash.webservice.util.ScheduledExportConfigXMLParser;
import com.ca.arcflash.webservice.util.SubscriptionConfigXMLParser;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.JobScriptCombo4Wan;

public class D2DFacade
{
	private static D2DFacade instance = null;

	private BackupConfigurationXMLDAO gackupConfigurationXMLDAOInstance = null;
	private BackupConfigXMLParser backupConfigXMLParser = null;
	private SubscriptionConfigXMLParser subscriptionConfigXMLParser = null;
	private ArchiveConfigurationXMLDAO archiveConfigXMLDAO = null;
	private SubscriptionConfigurationXMLDAO subScriptionConfigXMLDAO = null;
	private ArchiveConfigXMLParser archiveConfigXMLParser = null;
	private ScheduledExportConfigXMLParser scheduledExportConfigXMLParser = null;
	private VSphereBackupConfigurationXMLDAO vShpereXmlDAOInstance = null;
	private RpsConfigXMLParser rpsConfigXMLParser =null;
	
	//////////////////////////////////////////////////////////////////////////
	
	private D2DFacade()
	{
	}

	//////////////////////////////////////////////////////////////////////////
	
	public static D2DFacade getInstance()
	{
		if (instance == null)
			instance = new D2DFacade();
		
		return instance;
	}
	
	//////////////////////////////////////////////////////////////////////////

	private synchronized BackupConfigurationXMLDAO
		getBackupConfigurationXMLDAOInstance()
	{
		if (gackupConfigurationXMLDAOInstance == null)
			gackupConfigurationXMLDAOInstance = new BackupConfigurationXMLDAO();

		return gackupConfigurationXMLDAOInstance;
	}
	
	//////////////////////////////////////////////////////////////////////////

	private synchronized BackupConfigXMLParser getBackupConfigXMLParser()
	{
		if (backupConfigXMLParser == null)
			backupConfigXMLParser = new BackupConfigXMLParser();
				
		return backupConfigXMLParser;
	}
	
	//////////////////////////////////////////////////////////////////////////

	private synchronized ArchiveConfigurationXMLDAO getArchiveConfigurationXMLDAO()
	{
		if (archiveConfigXMLDAO == null)
			archiveConfigXMLDAO = new ArchiveConfigurationXMLDAO();
				
		return archiveConfigXMLDAO;
	}

	//////////////////////////////////////////////////////////////////////////

	private synchronized ArchiveConfigXMLParser getArchiveConfigXMLParserInstance()
	{
		if (archiveConfigXMLParser == null)
			archiveConfigXMLParser = new ArchiveConfigXMLParser();

		return archiveConfigXMLParser;
	}
	
	//////////////////////////////////////////////////////////////////////////
	private synchronized ScheduledExportConfigXMLParser
		getScheduledExportConfigXMLParserInstance()
	{
		if (scheduledExportConfigXMLParser == null)
			scheduledExportConfigXMLParser = new ScheduledExportConfigXMLParser();

		return scheduledExportConfigXMLParser;
	}

	private synchronized RpsConfigXMLParser
		getRpsConfigXMLParserInstance()
	{
		if (rpsConfigXMLParser == null)
			rpsConfigXMLParser  = new RpsConfigXMLParser();
		
		return rpsConfigXMLParser;
	}

	//////////////////////////////////////////////////////////////////////////

	private synchronized VSphereBackupConfigurationXMLDAO
		getVSphereBackupConfigurationXMLDAO()
	{
		if(vShpereXmlDAOInstance==null)
			vShpereXmlDAOInstance = new VSphereBackupConfigurationXMLDAO();
		
		return vShpereXmlDAOInstance;
	}

	//////////////////////////////////////////////////////////////////////////
	
	private synchronized SubscriptionConfigurationXMLDAO getSubScriptionConfigurationXMLDAO()
	{
		if (subScriptionConfigXMLDAO == null)
			subScriptionConfigXMLDAO = new SubscriptionConfigurationXMLDAO();
				
		return subScriptionConfigXMLDAO;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	private synchronized SubscriptionConfigXMLParser getSubscriptionConfigXMLParser()
	{
		if (subscriptionConfigXMLParser == null)
			subscriptionConfigXMLParser = new SubscriptionConfigXMLParser();
				
		return subscriptionConfigXMLParser;
	}
	//////////////////////////////////////////////////////////////////////////
	
	public Document subScriptionConfigurationToXmlDocument (
			SubscriptionConfiguration configuration) throws Exception
	{
		return getSubScriptionConfigurationXMLDAO().subscriptionConfigToXmlDocument(configuration);
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public SubscriptionConfiguration xmlDocumentToSubScriptionConfiguration (
			Document xmlDocument) throws Exception
	{
		return getSubscriptionConfigXMLParser().loadXML(xmlDocument);
	}
	//////////////////////////////////////////////////////////////////////////	
	
	public Document backupConfigurationToXmlDocument(
		BackupConfiguration configuration ) throws Exception
	{
		return getBackupConfigXMLParser().saveXML( configuration );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public BackupConfiguration xmlDocumentToBackupConfiguration(
		Document xmlDocument ) throws Exception
	{
		return getBackupConfigurationXMLDAOInstance().XmlDocumentToBackupConfig(
			xmlDocument );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public Document archiveConfigurationToXmlDocument(
		ArchiveConfiguration archiveConfig ) throws Exception
	{
		return getArchiveConfigurationXMLDAO().archiveConfigToXmlDocument(
			archiveConfig );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public ArchiveConfiguration xmlDocumentToArchiveConfiguration( Document xmlDocument )
	{
		return getArchiveConfigXMLParserInstance().loadXML( xmlDocument );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public Document ScheduledExportConfigurationToXmlDocument(
		ScheduledExportConfiguration configuration ) throws Exception
	{
		return this.getScheduledExportConfigXMLParserInstance().saveXML( configuration );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public ScheduledExportConfiguration
		xmlDocumentToScheduledExportConfiguration( Document xmlDocument ) throws Exception
	{
		return getScheduledExportConfigXMLParserInstance().loadXML( xmlDocument );
	}

	public Document RpsConfigurationToXmlDocument(
			RPSConfiguration configuration ) throws Exception
			{
		return this.getRpsConfigXMLParserInstance().saveXML( configuration );
			}
	
	public RPSConfiguration	xmlDocumentToRpsConfiguration( Document xmlDocument ) throws Exception
	{
		return getRpsConfigXMLParserInstance().loadXML( xmlDocument );
	}

	//////////////////////////////////////////////////////////////////////////
	
	public Document PreferencesConfigurationToXmlDocument(
		PreferencesConfiguration configuration ) throws Exception
	{
		return getBackupConfigurationXMLDAOInstance().PreferencesSettingsToXmlDocument(
			configuration );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public PreferencesConfiguration
		xmlDocumentToPreferencesSettings( Document xmlDocument )
	{
		return getBackupConfigurationXMLDAOInstance().XmlDocumentToPreferencesSettings(
			xmlDocument );
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public JobScriptCombo4Wan vcmSettingsStringToObject( String vcmSettingsString ) throws Exception
	{
		return CommonUtil.unmarshal( vcmSettingsString, JobScriptCombo4Wan.class );
	}

	//////////////////////////////////////////////////////////////////////////
	
	public String vcmConfigurationToString( JobScriptCombo4Wan configuration ) throws Exception
	{
		return CommonUtil.marshal( configuration );
	}

	//////////////////////////////////////////////////////////////////////////
	
	public Document VSphereBackupConfigurationToXmlDocument(
		VSphereBackupConfiguration configuration ) throws Exception
	{
		return getVSphereBackupConfigurationXMLDAO().VSphereBackupConfigurationToDocument(
			configuration, null, true );
	}

	//////////////////////////////////////////////////////////////////////////
	
	public VMBackupConfiguration xmlDocumentToVMBackupConfiguration(
		Document xmlDocument ) throws Exception
	{
		return getVSphereBackupConfigurationXMLDAO().XMLDocumentToVMBackupConfiguration(
			xmlDocument );
	}
	
	//////////////////////////////////////////////////////////////////////////

	public void validateArchiveSource( ArchiveConfiguration archiveConfig ) throws Exception
	{
		ArchiveService.getInstance().validateArchiveSource( archiveConfig );
	}


}
