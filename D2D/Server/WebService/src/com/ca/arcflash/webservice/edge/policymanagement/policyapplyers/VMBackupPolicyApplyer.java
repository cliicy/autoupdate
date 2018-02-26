package com.ca.arcflash.webservice.edge.policymanagement.policyapplyers;



import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.D2DTime;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.SavePolicyWarning;
import com.ca.arcflash.webservice.data.vsphere.VMBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcflash.webservice.edge.policymanagement.ID2DPolicyManagementService;
import com.ca.arcflash.webservice.edge.policymanagement.LogUtility;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyDeploymentCache;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyXmlObject;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VMCopyService;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.VSphereBackupConfigurationXMLDAO;

public class VMBackupPolicyApplyer extends BasePolicyApplyer
{
	private static final Logger logger = Logger.getLogger(VMBackupPolicyApplyer.class);
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected int getResponsiblePolicyType()
	{
		return ID2DPolicyManagementService.PolicyTypes.VMBackup;
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void doApplying()
	{
		applyVMBackupSettings();
	}

	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void doUnApplying()
	{
		unapplyVMBackupSettings();
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void removePolicyRecord()
	{
		// vsp has called detachVSpherePolicy()
	}

	private void addError(int errorType, String vmInstanceUuid, String errorCode, Object[] errorParameters,  int type) {
		PolicyDeploymentError error = new PolicyDeploymentError();
		
		error.setVmInstanceUuid(vmInstanceUuid);
		error.setPolicyType(getResponsiblePolicyType());
		error.setSettingsType(type);
		error.setErrorType(errorType);
		error.setErrorCode(errorCode);
		error.setErrorParameters(errorParameters);
		
		errorList.add(error);
	}
	
	private void addError(String vmInstanceUuid, String errorCode, Object[] errorParameters , int type) {
		addError(PolicyDeploymentError.ErrorTypes.Error, vmInstanceUuid, errorCode, errorParameters, type);
	}
	
	private void addWarning(String vmInstanceUuid, String errorCode, Object[] errorParameters , int type) {
		addError(PolicyDeploymentError.ErrorTypes.Warning, vmInstanceUuid, errorCode, errorParameters, type);
	}
	
	private void unapplyVMBackupSettings()
	{
		long ret = 0;
		VirtualMachine[] vmArray = null;
		
		  try{	
			logger.info("unApplyVMBackupSettings(): enter" );
			
			if(this.policyParameter!=null)
			{
				String[]instanceUUIDArray = this.policyParameter.split(",");
				
				vmArray = new VirtualMachine[instanceUUIDArray.length];
				for(int i=0;i<instanceUUIDArray.length;i++)
				{
					vmArray[i] = new VirtualMachine();
					vmArray[i].setVmInstanceUUID(instanceUUIDArray[i]);
				}
				
				ret = VSphereService.getInstance().detachVSpherePolicy(vmArray);
				unapplyScheduledExportSettings(vmArray);
			}
			
			logger.info("unApplyVMBackupSettings(): Vsphere settings applied ok.ret="+ret );
			}
			catch (Exception e)
			{
				if (e instanceof ServiceException)
				{
					ServiceException svcEx = (ServiceException)e;
					
					logger.error("unApplyVMBackupSettings(): error", e );
				
					if (vmArray != null)
					{
						for (VirtualMachine vm : vmArray)
						{
							addError(vm.getVmInstanceUUID(),
								svcEx.getErrorCode(), svcEx.getMultipleArguments(),ID2DPolicyManagementService.SettingsTypes.VMBackupSettings);
						}
					}
					
					return;
				}
				// - end of the temporary code -
				
				logger.error("unApplyVMBackupSettings(): error", e );
				
				if (vmArray != null)
				{
					for (VirtualMachine vm : vmArray)
					{
						addError(vm.getVmInstanceUUID(),
							ID2DPolicyManagementService.GenericErrors.InternalError,
							null, ID2DPolicyManagementService.SettingsTypes.VMBackupSettings);
					}
				}
			}
			finally
			{
				logUtility.writeLog( LogUtility.LogTypes.Debug,
					"unApplyVMBackupSettings(): exit" );
			}
	}
	
	private  void applyVMBackupSettings()
	{
		BackupVM[] vms = null;
		
	  try{	
		logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyVMBackupSettings(): enter" );
		
		Document settingsDocument =
			this.policyXmlObject.getSettingsSection(
				PolicyXmlObject.PolicyXmlSectionNames.VMBackupSettings );
		VSphereBackupConfiguration configuration= getVSphereBackupConfigurationXMLDAO().VMConfigToVSphereConfig( getVSphereBackupConfigurationXMLDAO().XMLDocumentToVMBackupConfiguration(settingsDocument));
		
		VSphereBackupConfiguration configuration2 = com.ca.arcflash.common.CommonUtil.unmarshal(this.policyParameter,VSphereBackupConfiguration.class);
		vms = configuration2.getBackupVMList();
		
//		// if old policy is same, no need save again
//		vms = removesamepolicy(vms);
//		if(vms==null || vms.length==0)
//			return;
		
		configuration.setBackupVMList(vms);
		
		if(configuration.getStartTime() != null && configuration.getStartTime().getYear() > 1900) {
			D2DTime time = configuration.getStartTime();
			Calendar cal = Calendar.getInstance();
			cal.set(time.getYear(), time.getMonth(), time.getDay(), time.getHourOfday(), time.getMinute(), 0);
			cal.set(Calendar.MILLISECOND, 0);
			configuration.setBackupStartTime(cal.getTimeInMillis());
		}
		
		for (BackupVM vm : vms) {
			PolicyDeploymentCache.getInstance().cacheDeployingVM(vm.getInstanceUUID());
		}
		
		SavePolicyWarning[] warnings =
			VSphereService.getInstance().saveVSphereBackupConfiguration(configuration);
		
		List<SavePolicyWarning> exportWarningList = new LinkedList<SavePolicyWarning>();
		for (BackupVM vm : vms) {
			this.applyScheduledExportSettings(vm, configuration, exportWarningList);
		}
		
		Map<String, String> policyUuids = new HashMap<String, String>();
		for (BackupVM vm : vms) {
			
			policyUuids.put(vm.getInstanceUUID(), policyUuid);
		}

		enumerateWarningErros(exportWarningList.toArray(new SavePolicyWarning[0]), policyUuids, ID2DPolicyManagementService.SettingsTypes.ScheduledExportSettings);
		enumerateWarningErros(warnings, policyUuids, ID2DPolicyManagementService.SettingsTypes.VMBackupSettings);
		
		new D2DEdgeRegistration().SavePolicyUuid2Xml(ApplicationType.vShpereManager, policyUuids);

		for (BackupVM vm : vms) {
			PolicyDeploymentCache.getInstance().clearCachedDeployingVM(vm.getInstanceUUID());
		}
		
		logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyVMBackupSettings(): Vsphere settings applied ok." );
		}
//		catch (ServiceException e)
//		{
//			logUtility.writeLog( LogUtility.LogTypes.Error, e,
//				"applyVCMSettings(): error" );
//			
//			this.addError(
//				ID2DPolicyManagementService.SettingsTypes.VCMSettings,
//				e.getErrorCode() );
//		}
		catch (Exception e)
		{
			// Since the API applyVCMJobPolicy() wrongly uses the super class,
			// Exception, as the checked exception, we have to catch the exception
			// by Exception and then check the exact type of the exception.
			// I'd talked with Cheng, Jian, who wrote this API, and he will
			// change it later. So, before he changes the API, I have to use
			// following temporary code to catch the exception.
			//
			// Pang, Bo (panbo01)
			// 2010-11-08
			if (e instanceof ServiceException)
			{
				ServiceException svcEx = (ServiceException)e;
				
				logUtility.writeLog( LogUtility.LogTypes.Error, e,
					"applyVMBackupSettings(): error" );
			
				if (vms != null)
				{
					for (BackupVM vm : vms)
					{
						addError(vm.getInstanceUUID(),
							svcEx.getErrorCode(), svcEx.getMultipleArguments(), ID2DPolicyManagementService.SettingsTypes.VMBackupSettings);
					}
				}
				
				return;
			}
			// - end of the temporary code -
			
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyVMBackupSettings(): error" );
			
			if (vms != null)
			{
				for (BackupVM vm : vms)
				{
					addError(vm.getInstanceUUID(),
						ID2DPolicyManagementService.GenericErrors.InternalError,
						null, ID2DPolicyManagementService.SettingsTypes.VMBackupSettings);
				}
			}
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyVMBackupSettings(): exit" );
		}
	}
	
	private BackupVM[] removesamepolicy(BackupVM[] vms) {
		if(vms == null || vms.length==0)
			return new BackupVM[0];
		EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.vShpereManager);
		if(edgeRegInfo==null)
			return new BackupVM[0];
		List<BackupVM> newvms=new ArrayList<BackupVM>();
		Map<String, String> map=edgeRegInfo.getPolicyUuids();
		for (BackupVM vm : vms) {
			String key=vm.getInstanceUUID();
			if(!map.containsKey(key))
				newvms.add(vm);
			else{
				if(!policyUuid.equals(map.get(key)))
					newvms.add(vm);
				else{
					VirtualMachine virtualMachine = new VirtualMachine();
					virtualMachine.setVmInstanceUUID(vm.getInstanceUUID());
					try {
						VMBackupConfiguration configuration = getVSphereBackupConfigurationXMLDAO().get(ServiceContext.getInstance().getVsphereBackupConfigurationFolderPath(), virtualMachine);
						BackupVM oldVM = configuration.getBackupVM();
						if (oldVM!=null){
							oldVM.setDestination(vm.getDestination());
							if(!oldVM.equals(vm))
								newvms.add(vm);
						}
					} catch (Exception e) {
						logUtility.writeLog( LogUtility.LogTypes.Error,	e.getMessage() );
					}
				}
			}
		}
		return newvms.toArray(new BackupVM[0]);
	}

	private VSphereBackupConfigurationXMLDAO vShpereXmlDAOInstance = null;
	
	private synchronized VSphereBackupConfigurationXMLDAO getVSphereBackupConfigurationXMLDAO()
	{
		if(vShpereXmlDAOInstance==null)
			vShpereXmlDAOInstance = new VSphereBackupConfigurationXMLDAO();
		return vShpereXmlDAOInstance;
		  
	}
	
	protected void applyScheduledExportSettings(BackupVM vm, VSphereBackupConfiguration backupConfiguration, List<SavePolicyWarning> warnings)
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyScheduledExportSettings(): enter" );

			ScheduledExportConfiguration configuration = getScheduledExportSettings();
			
			if (configuration == null)
			{
				logUtility.writeLog( LogUtility.LogTypes.Debug,
					"applyScheduledExportSettings(): No scheduled export settings." );
				
				unapplyScheduledExportSettings(vm);
				return;
			}
			
			VMCopyService.getInstance().saveScheduledExportConfiguration( vm, configuration, backupConfiguration);
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyScheduledExportSettings(): Backup settings applied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyScheduledExportSettings(): error" );
			
			warnings.add(generatePolicyWarning(vm, e.getErrorCode(), e.getMultipleArguments()));
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"applyScheduledExportSettings(): error" );
			
			warnings.add(generatePolicyWarning(vm, FlashServiceErrorCode.Common_ErrorOccursInService, null));
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"applyScheduledExportSettings(): exit" );
		}
	}

	protected void unapplyScheduledExportSettings(BackupVM vm)
	{
		try
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyScheduledExportSettings(): enter" );
			
			String configFilePath =
					VMCopyService.getVMScheduledExportRPFile(vm);
			File configFile = new File( configFilePath );
			if (configFile.exists())
			{
				if(tryDeleteFile(configFile))
					VMCopyService.getInstance().clearCachedConfiguration();
			}
			
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyScheduledExportSettings(): Scheduled export settings unapplied ok." );
		}
		catch (ServiceException e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyScheduleExportSettings(): error" );
			
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ScheduledExportSettings,
				e.getErrorCode(),
				e.getMultipleArguments() );
		}
		catch (Exception e)
		{
			logUtility.writeLog( LogUtility.LogTypes.Error, e,
				"unapplyScheduledExportSettings(): error" );
		
			this.addError( null,
				ID2DPolicyManagementService.SettingsTypes.ScheduledExportSettings,
				ID2DPolicyManagementService.GenericErrors.InternalError,
				null );
		}
		finally
		{
			logUtility.writeLog( LogUtility.LogTypes.Debug,
				"unapplyScheduledExportSettings(): exit" );
		}
	}
	
	protected void unapplyScheduledExportSettings(VirtualMachine[] vmArray){
		BackupVM backupVM = new BackupVM();
		for (VirtualMachine vm: vmArray){
			backupVM.setInstanceUUID(vm.getVmInstanceUUID());
			unapplyScheduledExportSettings(backupVM);
		}
	}
	
	private void enumerateWarningErros(SavePolicyWarning[] warnings,
			Map<String, String> policyUuids, int type) {
		for (SavePolicyWarning warning : warnings) {
			BackupVM vm = warning.getVm();
			if (vm == null)
				continue;
			if (warning.getType() == Constants.AFRES_AFALOG_WARNING) {
				addWarning(
						warning.getVm().getInstanceUUID(),
						warning.getWarningCode(),
						warning.getWarningMessages(),
						type);
			} else {
				addError(
						warning.getVm().getInstanceUUID(),
						warning.getWarningCode(),
						warning.getWarningMessages(),
						type);
				policyUuids.remove(warning.getVm().getInstanceUUID());
			}
		}
	}
	
	SavePolicyWarning generatePolicyWarning(BackupVM vm, String errorCode, Object[] args){
		SavePolicyWarning warning = new SavePolicyWarning();
		warning.setVm(vm);
		warning.setType(Constants.AFRES_AFALOG_ERROR);
		warning.setWarningCode(errorCode);
		warning.setWarningMessages(args);
		return warning;
	}
}
