package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.service.internal.VSphereBackupConfigurationXMLDAO;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.BackupPolicySummary;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyContentForSave;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;

public class PolicyUtil {

	private static final PolicyUtil instance = new PolicyUtil();
	private static final Logger logger = Logger.getLogger(PolicyUtil.class);
	private static IEdgeHostMgrDao hostManagerDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private VSphereBackupConfigurationXMLDAO vShpereXmlDAOInstance = new VSphereBackupConfigurationXMLDAO();
//	private EdgeCommonServiceImpl commonService = new EdgeCommonServiceImpl();
	
	//Save d2d 15.0, 16.0, 16.5 and 17 for handle backward compatibility of d2d
	private int[] agentsBeforeOolong = new int[]{15,16,17};
	

	public static PolicyUtil getInstance() {
		return instance;
	}

	public Logger getLogger()
	{
		return logger;
	}

	public Document xmlStringToDocument( String string )
	{
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			Document xmlDocument = docBuilder.parse( new InputSource( new StringReader( string ) ) );

			return xmlDocument;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public Document getSectionDocument( Document xmlDoc, String sectionName )
	{
		try
		{
			// find the root node of the section

			XPathFactory pathFactory = XPathFactory.newInstance();
			XPath path = pathFactory.newXPath();

			NodeList nodeList = (NodeList)
				path.evaluate( "/BackupPolicy/" + sectionName, xmlDoc, XPathConstants.NODESET );

			NodeList childNodeList = nodeList.item( 0 ).getChildNodes();
			Node rootNode = null;
			for (int i = 0; i < childNodeList.getLength(); i ++)
			{
				Node node = childNodeList.item( i );
				if (node instanceof Element)
				{
					rootNode = node;
					break;
				}
			}

			if (rootNode == null)
				return null;

			// create a new document and import the section

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			Document xmlDocument = docBuilder.newDocument();
			xmlDocument.appendChild( xmlDocument.importNode( rootNode, true ) );

			return xmlDocument;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public VMBackupConfiguration getVMBackupConfiguration(String policyXml, String sectionName) throws Exception {
		Document policyDocument = xmlStringToDocument(policyXml);
		Document subDocument = getSectionDocument(policyDocument, sectionName);
		return vShpereXmlDAOInstance.XMLDocumentToVMBackupConfiguration(subDocument);
	}
	
//	public void checkD2DVersion( IFlashService d2dService ) throws
//		FailedToGetD2DVersionException,
//		UnsatisfiedD2DVersionException
//	{
//		int d2dMajorVersion, d2dMinorVersion;
//		
//		try
//		{
//			VersionInfo d2dVersionInfo = d2dService.getVersionInfo();
//			d2dMajorVersion = Integer.parseInt( d2dVersionInfo.getMajorVersion() );
//			d2dMinorVersion = Integer.parseInt( d2dVersionInfo.getMinorVersion() );
//		}
//		catch (Exception e)
//		{
//			logger.error( "Failed to get D2D version.", e );
//			throw new FailedToGetD2DVersionException();
//		}
//		
//		if(isAgentBeforeOolong(d2dMajorVersion)){
//			
//			throw new UnsatisfiedD2DVersionException(
//					d2dMajorVersion, d2dMinorVersion,
//					5, 0 );
//			
//		}
//		
//	}
//
//	private boolean isAgentBeforeOolong(int majaorVersion){
//		
//		boolean result = false;
//		for(int version: agentsBeforeOolong){
//			result =  version == majaorVersion ? true: false;
//			if(result){
//				break;
//			}
//		}
//		
//		return result;
//		
//	}

	public String getLocalEdgeID(){
		return CommonUtil.retrieveCurrentAppUUID();
	}
	
	public PolicyContentForSave convertUnifiedPolicy2VMPolicyContenForSave(UnifiedPolicy plan) {
		VSphereBackupConfiguration vsBConf = plan.getVSphereBackupConfiguration();
		if (vsBConf == null) {
			return null;
		}
		
		PolicyContentForSave lagacyPolicyContent = new PolicyContentForSave();
//		lagacyPolicyContent.setArchiveConfiguration(plan.getFileCopyConfiguration());
		lagacyPolicyContent.setBackupSettings(plan.getBackupConfiguration());
		lagacyPolicyContent.setContentFlag(BackupPolicySummary.PolicyContentFlag.VMBackup);
		//lagacyPolicyContent.setNodeId();
		//lagacyPolicyContent.setPolicyGuid();
		//lagacyPolicyContent.setPolicyProductType();
		lagacyPolicyContent.setPolicyName(plan.getName());
		lagacyPolicyContent.setPolicyType(PolicyTypes.Unified);
		lagacyPolicyContent.setPreferencesSettings(plan.getPreferencesConfiguration());
		//lagacyPolicyContent.setRpsSettings();
		lagacyPolicyContent.setScheduledExportConfiguration(plan.getExportConfiguration());
		//lagacyPolicyContent.setSubscriptionSettings();
		//lagacyPolicyContent.setVcmConfiguration();
		lagacyPolicyContent.setVmBackupSettings(vsBConf);
		
		return lagacyPolicyContent; 
	}

	public static String getNodeNameOfError(PolicyDeploymentTask task, PolicyDeploymentError error) {
		if (error.getNodeName() != null) {
			return error.getNodeName();
		}

		return getNodeName(task.getHostId());
	}

	public static String getNodeName(int nodeId) {
		List<EdgeHost> nodeList = new ArrayList<EdgeHost>();
		int isVisible = 1; // true
		hostManagerDao.as_edge_host_list(nodeId, isVisible, nodeList);

		String nodeName = null;
		EdgeHost node = null;
		if (nodeList.size() > 0) {
			nodeName = nodeList.get(0).getRhostname();
			node = nodeList.get(0);
		} else {
			logger.error("getNodeName(): Node ID not found.");
		}

		if (nodeName == null || nodeName.isEmpty()) {
			if (node != null) {
				int hostType = node.getRhostType();
				if (HostTypeUtil.isVMWareVirtualMachine(node.getRhostType())
						|| HostTypeUtil.isHyperVVirtualMachine(hostType)) {
					nodeName = EdgeCMWebServiceMessages.getMessage("unknown_vm", node.getVmname());
				}
			} else {
				nodeName = EdgeCMWebServiceMessages.getResource("policyDeployment_UnknownNode");
			}
		}

		return nodeName;
	}

	public static String getFixedOsDescription(String osDesc) {
		if (StringUtil.isEmptyOrNull(osDesc)) {
			return EdgeCMWebServiceMessages.getResource("policyDeployment_UnknownOS");
		}
		return osDesc;
	}
}
