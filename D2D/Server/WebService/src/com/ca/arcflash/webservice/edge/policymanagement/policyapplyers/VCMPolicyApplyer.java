package com.ca.arcflash.webservice.edge.policymanagement.policyapplyers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.ha.model.VCMSavePolicyWarning;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.data.policy.VCMPolicyDeployParameters;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.LogUtility;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyXmlObject;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceException;

public class VCMPolicyApplyer extends BasePolicyApplyer
{
	private boolean isForRemoteVCM;
	private VCMPolicyDeployParameters deployParameters; // don't use this directly, use getDeployParameters().
	
	//////////////////////////////////////////////////////////////////////////
	
	public VCMPolicyApplyer( boolean isForRemoteVCM )
	{
		this.isForRemoteVCM = isForRemoteVCM;
		this.deployParameters = null;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected int getResponsiblePolicyType()
	{
		return isForRemoteVCM ?
			ID2DPolicyManagementService.PolicyTypes.RemoteVCM :
			ID2DPolicyManagementService.PolicyTypes.VCM;
	}

	//////////////////////////////////////////////////////////////////////////
	
	protected VCMPolicyDeployParameters getDeployParameters() throws Exception
	{
		if (this.deployParameters == null)
		{
			if ((this.policyParameter == null) || this.policyParameter.trim().isEmpty())
			{
				logUtility.writeLog( LogUtility.LogTypes.Error,
					"applyVCMSettings() error: deploy parameter is null or empty." );
				throw new Exception();
			}
			
			this.deployParameters =
				CommonUtil.unmarshal( this.policyParameter, VCMPolicyDeployParameters.class );
			if (this.deployParameters == null)
			{
				logUtility.writeLog( LogUtility.LogTypes.Error,
					"applyVCMSettings() error: unmarshaling deploy parameter failed." );
				throw new Exception();
			}
		}
		
		return this.deployParameters;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void doApplying()
	{
		boolean isPolicyChanged = !checkpolicysame();
		
		// Sometime, although the policy has not been changed, we still need
		// to apply some settings. Such as if the policy is a remote VCM
		// policy, beside applying policy, we need to set the session folder
		// also. These settings are passed in in the policy parameters.
		//
		// Pang, Bo (panbo01)
		// 2012-07-23
		
		try {
			applyVCMSettings( isPolicyChanged );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (isPolicyChanged && !hasError()) // no errors, then save policy uuid
			savePolicyUuid();
	}
	
	private void savePolicyUuid() {
		try
		{
			Map<String, String> policyUuids = new HashMap<String, String>();
			policyUuids.put(getDeployParameters().getInstanceUuid(), policyUuid);
			new D2DEdgeRegistration().SavePolicyUuid2Xml(ApplicationType.VirtualConversionManager, policyUuids);
		}
		catch (Exception e)
		{
		}
	}

	private boolean checkpolicysame() {
		try
		{
			EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.VirtualConversionManager);
			if(edgeRegInfo==null)
				return false;
			String key_vm = "vcm_" + getDeployParameters().getInstanceUuid();
			String oldpolicyUuid=edgeRegInfo.getPolicyUuids().get(key_vm);
			if(oldpolicyUuid==null || oldpolicyUuid.isEmpty())
				return false;
			
			if(oldpolicyUuid.equals(policyUuid))
				return true;
			
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void doUnApplying()
	{
		unapplyVCMSettings();
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void removePolicyRecord()
	{
		try
		{
			List<String> list = new ArrayList<String>();
			list.add(getDeployParameters().getInstanceUuid());
			new D2DEdgeRegistration().RemovePolicyUuidFromXml(ApplicationType.VirtualConversionManager, list );
		}
		catch (Exception e)
		{
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	private String getVCMSettings() throws Exception
	{
		Document settingsDocument =
			this.policyXmlObject.getSettingsSection(
				PolicyXmlObject.PolicyXmlSectionNames.VCMSettings );
		
		String settingsContent = getVCMSettingsFromXmlDocument( settingsDocument );
//		logUtility.writeLog( LogUtility.LogTypes.Debug,
//			"applyVCMSettings(): settingsContent: %s", settingsContent );
		
		return settingsContent;
	}

	//////////////////////////////////////////////////////////////////////////

	private void applyVCMSettings( boolean isPolicyChanged )
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyVCMSettings(): enter" );
			
			String settingsContent = getVCMSettings();
			VCMPolicyDeployParameters deployParameters = getDeployParameters();
			deployParameters.setPlanUUID(policyUuid.split(":")[0]);
			VCMSavePolicyWarning[] warnings = HAService.getInstance().applyVCMJobPolicy(
				settingsContent, deployParameters, this.isForRemoteVCM, isPolicyChanged );
			
			// TODO: check the return value
			//Add the warning message			for (VCMSavePolicyWarning warning : warnings)
			{
				this.addWarning(
					null,
					ID2DPolicyManagementService.SettingsTypes.VCMSettings,
					warning.getWarningCode(), warning.getWarningMessages() );
			}
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyVCMSettings(): VCM settings applied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyVCMSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.VCMSettings,
				e.getErrorCode(), e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyVCMSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.VCMSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyVCMSettings(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////

	private void unapplyVCMSettings()
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyVCMSettings(): enter" );
			
			VCMPolicyDeployParameters deployParameters = getDeployParameters();
			HAService.getInstance().unApplyVCMJobPolicy( deployParameters );
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyVCMSettings(): VCM settings applied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyVCMSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.VCMSettings,
				e.getErrorCode(), e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyVCMSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.VCMSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyVCMSettings(): exit" );
		}
	}

	//////////////////////////////////////////////////////////////////////////
	
	private static final String VCMSETTINGS_ROOTELEMENT			= "VCMSettings";
	private static final String VCMSETTINGS_CONTENTATTRIBUTE	= "Content";
	
	public static String getVCMSettingsFromXmlDocument( Document xmlDocument )
	{
		try
		{
			// get the settings content from the XML
			
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			String path = String.format( "/%s/@%s",
				VCMSETTINGS_ROOTELEMENT, VCMSETTINGS_CONTENTATTRIBUTE );
			String encrypted = xpath.evaluate( path, xmlDocument );
			
			// decrypt the settings content
			
			String settingsContent =
				CommonService.getInstance().getNativeFacade().decrypt( encrypted );
			
			return settingsContent;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
}
