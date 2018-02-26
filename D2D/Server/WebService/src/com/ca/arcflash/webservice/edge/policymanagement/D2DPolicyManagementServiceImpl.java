package com.ca.arcflash.webservice.edge.policymanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.D2DConfiguration;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.policymanagement.policyapplyers.BackupAndArchivingPolicyApplyerEx;
import com.ca.arcflash.webservice.edge.srmagent.SrmAgentServerImpl;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BaseService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.util.BackupConfigXMLParser;

public class D2DPolicyManagementServiceImpl extends BaseService implements ID2DPolicyManagementService
{
	private static D2DPolicyManagementServiceImpl instance = null;

	public static D2DPolicyManagementServiceImpl getInstance()
	{
		if (instance == null)
			instance = new D2DPolicyManagementServiceImpl();

		return instance;
	}

//	class PolicyXmlSectionNames
//	{
//		private static final String BackupSettings				= "BackupSettings";
//		private static final String ArchivingSettings			= "ArchivingSettings";
//		private static final String VirtualConversionSettings	= "VirtualConversionSettings";
//	}

	private static final Logger logger = Logger.getLogger(D2DPolicyManagementServiceImpl.class);
	private static final LogUtility logUtility = new LogUtility( logger );

//	private Document xmlStringToDocument( String string )
//	{
//		try
//		{
//			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
//			Document xmlDocument = docBuilder.parse( new InputSource( new StringReader( string ) ) );
//
//			return xmlDocument;
//		}
//		catch (Exception e)
//		{
//			return null;
//		}
//	}

//	private Document fileToDocument( File file )
//	{
//		try
//		{
//			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
//			Document xmlDocument = docBuilder.parse(file);
//
//			return xmlDocument;
//		}
//		catch (Exception e)
//		{
//			return null;
//		}
//	}

	private  void saveXmlDocument( Document xmlDocument, String name ) throws Exception
	{

		if (xmlDocument == null)
		{
			throw new Exception("Invalid Parameter");
		}

		Transformer t = TransformerFactory.newInstance().newTransformer();
		//t.setOutputProperty( OutputKeys.DOCTYPE_SYSTEM, SystemIdentifier );
		//t.setOutputProperty( OutputKeys.DOCTYPE_PUBLIC, PublicIdentifier );
		t.setOutputProperty( OutputKeys.INDENT, "yes" );
		t.setOutputProperty( OutputKeys.METHOD, "xml" );
		t.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
		t.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );

		t.transform( new DOMSource( xmlDocument ), new StreamResult( name ) );

	}

//	private Document getSectionDocument( Document xmlDoc, String sectionName )
//	{
//		try
//		{
//			// find the root node of the section
//
//			XPathFactory pathFactory = XPathFactory.newInstance();
//			XPath path = pathFactory.newXPath();
//
//			NodeList nodeList = (NodeList)
//				path.evaluate( "/BackupPolicy/" + sectionName, xmlDoc, XPathConstants.NODESET );
//
//			NodeList childNodeList = nodeList.item( 0 ).getChildNodes();
//			Node rootNode = null;
//			for (int i = 0; i < childNodeList.getLength(); i ++)
//			{
//				Node node = childNodeList.item( i );
//				if (node instanceof Element)
//				{
//					rootNode = node;
//					break;
//				}
//			}
//
//			if (rootNode == null)
//				return null;
//
//			// create a new document and import the section
//
//			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
//			Document xmlDocument = docBuilder.newDocument();
//			xmlDocument.appendChild( xmlDocument.importNode( rootNode, true ) );
//
//			return xmlDocument;
//		}
//		catch (Exception e)
//		{
//			return null;
//		}
//	}

	int applyD2DPolicy(BackupConfiguration backupConfig, String fullConfigName) {
		if (backupConfig == null) {
			logger.error("BackupConfiguration is null");
			return -1;
		}

		long result = -1;

		try {
			result = BackupService.getInstance().saveBackupConfiguration(backupConfig);
		} catch(Exception e) {
			logger.warn("Save backup configuration failed <" + e.getMessage() + ">");
		}

		if (result == 0) {
			return 0;
		}

		BackupConfigXMLParser backupConfigXMLParser = new BackupConfigXMLParser();

		try {
			Document xmlDocument = backupConfigXMLParser.saveXML(backupConfig);
			saveXmlDocument(xmlDocument, fullConfigName);
		} catch (Exception e) {
			logger.error("Save backup configuration to XML file failed");
			return -1;
		}

		try {
			BackupService.getInstance().reloadBackupConfiguration();
		} catch (Exception e) {
			logger.error("Reload backup configuration failed after saving XML file <" + e.getMessage() + ">");
		}

		return 0;
	}

	@Override
	public int DeplyD2DPolicy(BackupConfiguration configuration) {
		int res = 0;

		String fullConfigName = ServiceContext.getInstance().getBackupConfigurationFilePath();
		File fileConfig = new File(fullConfigName);
		boolean valid = BackupService.getInstance().checkBackupConfigurationValid();

		if (!valid && fileConfig.exists()) {
			try {
				BackupService.getInstance().reloadBackupConfiguration(); //load configuration file content to memory
			} catch (Exception e) {
				logger.error("Reload backup configuration failed <" + e.getMessage() + ">");
				return -1;
			}
		}

		res = applyD2DPolicy(configuration, fullConfigName);
		if (res != 0) {
			return res;
		}

		try {
			if (configuration != null && configuration.getSrmPkiAlertSetting() != null) {
				res = SrmAgentServerImpl.SaveAlertSetting(configuration.getSrmPkiAlertSetting());
			}
		} catch (Exception e) {
			logger.error("Save SRM alert setting failed <" + e.getMessage() + ">");
			return -1;
		}

		return res;

	}

	@Override
	public int RemoveD2DPolicy() {
		int res = 0;
		try
		{
			String fullConfigName = ServiceContext.getInstance().getBackupConfigurationFilePath();
			File configFile = new File(fullConfigName);
			if(configFile.exists())
			{
				configFile.delete();
				BackupService.getInstance().cleanBackupConfiguration();
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage() == null ? e : e.getMessage());
			Date time = new Date();
			logger.error(time.toString() + ": Exception when RemoveD2DPolicy <" + e.getMessage() + ">");
			res = -1;
		}

		return res;
	}

	//////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////

	@Override
	public List<PolicyDeploymentError> deployPolicy(
		int policyType, String policyUuid, String policyXml, String edgeID, ApplicationType appType, String parameter){
		try {
			logUtility.writeLog(LogUtility.LogTypes.Info,
					"deployPolicy(): enter, policyType = " + policyType + ", appType = " + appType);

			List<PolicyDeploymentError> errorList = new ArrayList<PolicyDeploymentError>();
			
			if(!precheck(policyType,edgeID,appType,errorList,policyXml)){
				return errorList;
			}

			IPolicyApplyer policyApplyer = PolicyApplyerFactory
					.createPolicyApplyer(policyType);
			if (policyApplyer == null) {
				logUtility
						.writeLog(
								LogUtility.LogTypes.Error,
								"deployPolicy(): Create IPolicyApplyer failed. (policyType: %d)",
								policyType);

				addGeneralError(errorList, policyType, GenericErrors.UnknownPolicyType);
				return errorList;
			}

			policyApplyer.applyPolicy(errorList, policyUuid, policyXml, parameter);
			return errorList;
		} catch (Exception e) {
			logUtility.writeLog(LogUtility.LogTypes.Error, e,
					"deployPolicy(): error");
			return null;
		} finally {
			logUtility.writeLog(LogUtility.LogTypes.Info,
					"deployPolicy(): exit");
		}
	}
	
	
	

	//////////////////////////////////////////////////////////////////////////

	@Override
	public List<PolicyDeploymentError> removePolicy(
		int policyType, boolean keepCurrentSettings, String edgeID, ApplicationType appType , String parameter )
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Info,
				"removePolicy(): enter, policyType = " + policyType + ", appType = " + appType + ", keepCurrentSettings = " + keepCurrentSettings );

			List<PolicyDeploymentError> errorList =
				new ArrayList<PolicyDeploymentError>();

			D2DEdgeRegistration _register = new D2DEdgeRegistration();
			if (_register.getRegStatus(edgeID, appType) == 2){
				logger.debug("appType:"+appType);
				logger.debug("This D2D has been managed by another Edge App");
				addGeneralError( errorList, policyType,
					FlashServiceErrorCode.EDGE_MANAGED_BY_ANOTHER_EDGE,
					_register.getEdgeRegInfo(appType).getEdgeHostName());
				return errorList;
			}

			IPolicyApplyer policyApplyer =
				PolicyApplyerFactory.createPolicyApplyer( policyType );
			if (policyApplyer == null)
			{
				addGeneralError( errorList, policyType, GenericErrors.UnknownPolicyType);
				return errorList;
			}

			policyApplyer.unapplyPolicy( errorList, keepCurrentSettings,parameter );

			return errorList;
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"removePolicy(): error" );
			return null;
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Info,
				"removePolicy(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////

	private void addGeneralError(
		List<PolicyDeploymentError> errorList, int policyType, String errorCode )
	{
		addGeneralError(errorList, policyType, errorCode, (Object[]) null);
	}
	
	private void addGeneralError(
			List<PolicyDeploymentError> errorList, int policyType, String errorCode, Object... errorParameters )
		{
			PolicyDeploymentError error = new PolicyDeploymentError();
			error.setPolicyType( policyType );
			error.setSettingsType( SettingsTypes.WholePolicy );
			error.setErrorType( PolicyDeploymentError.ErrorTypes.Error );
			error.setErrorCode( errorCode );
			error.setErrorParameters( errorParameters );
			errorList.add( error );
		}
	
	//////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isUsingEdgePolicySettings( int settingsType )
	{
		return PolicyUsageMarker.getInstance().isUsingEdgePolicySettings( settingsType );
	}

	
	private boolean precheck(int policyType, String edgeID, ApplicationType appType , List<PolicyDeploymentError> errorList,Object policyObject){
		
		D2DEdgeRegistration _register = new D2DEdgeRegistration();
		int regStatus = _register.getRegStatus(edgeID, appType);
		if (regStatus == 2) {
			logger.debug("appType:" + appType);
			logger.debug("This D2D has been managed by another Edge App");
			addGeneralError(errorList, policyType,
					FlashServiceErrorCode.EDGE_MANAGED_BY_ANOTHER_EDGE,
					_register.getEdgeRegInfo(appType).getEdgeHostName());
			return false;
		}
		else if(regStatus == 0) {
			logger.debug("appType:" + appType);
			logger.debug("This D2D has not been managed by any Edge App");
			addGeneralError(errorList, policyType,
					FlashServiceErrorCode.EDGE_NOT_MANAGED_BY_ANY_EDGE);
			return false;
		}

		if (policyObject == null) {
			logUtility.writeLog(LogUtility.LogTypes.Error,"precheck(): Invalid parameter policy is null.");
			addGeneralError(errorList, policyType, GenericErrors.InvalidParameter);
			return false;
		}
		return true;
		
	}
	

	public List<PolicyDeploymentError> deployPolicy(D2DConfiguration configuration,String policyUuid,String edgeID ){
		List<PolicyDeploymentError> errorList = new ArrayList<PolicyDeploymentError>();
		int policyType=ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving;
		ApplicationType appType=ApplicationType.CentralManagement;
		try {
			logUtility.writeLog(LogUtility.LogTypes.Info,"deployPolicy(): enter, policyType = " + policyType + ", appType = " + appType);
			if(!precheck(policyType,edgeID,appType,errorList,configuration)){
				return errorList;
			}
			BackupAndArchivingPolicyApplyerEx applyer=new BackupAndArchivingPolicyApplyerEx(configuration);
			applyer.applyPolicy(errorList, policyUuid);
			return errorList;
		} catch (Exception e) {
			logUtility.writeLog(LogUtility.LogTypes.Error, e,
					"deployPolicy(): error");
			return null;
		} finally {
			logUtility.writeLog(LogUtility.LogTypes.Info,
					"deployPolicy(): exit");
		}

	}

}
