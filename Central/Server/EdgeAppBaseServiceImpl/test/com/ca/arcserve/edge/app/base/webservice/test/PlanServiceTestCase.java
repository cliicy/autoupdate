package com.ca.arcserve.edge.app.base.webservice.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceIdentifier;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

/**
 * plan service test case
 * 
 * @author zhati04
 */
public class PlanServiceTestCase extends AbstractTestCase{
	private static final Logger logger = Logger.getLogger(PlanServiceTestCase.class); 
	private static final PolicyManagementServiceImpl planService = PolicyManagementServiceImpl.getInstance();
	
	@Test
	public void createPlan(){
		UnifiedPolicy plan = new UnifiedPolicy();
		//plan basic information
		plan.setId(0);
		//plan.setUuid(UUID.randomUUID().toString()); //service already set this field
		plan.setName("TestPlan1");
		
		//protected resource identifier information
		List<ProtectedResourceIdentifier> protectedResources = new ArrayList<ProtectedResourceIdentifier>();
		ProtectedResourceIdentifier identifier = new ProtectedResourceIdentifier();
		identifier.setType(ProtectedResourceType.node);
		//if backup node id is node id
		identifier.setId(10);
		protectedResources.add(identifier);
		plan.setProtectedResources(protectedResources);
		
		//default enable plan, this field default value is true
		//plan.setEnable(true);
		
		//task order
		List<Integer> orderList = new ArrayList<Integer>();
		orderList.add(11); //11 identity backup
		orderList.add(14); //14 agent install
		plan.setOrderList(orderList);
		
		//backup configuration
		BackupConfiguration backupConfiguration = new BackupConfiguration();
		backupConfiguration.setD2dOrRPSDestType(true);
		backupConfiguration.setDestination("C:");
		plan.setBackupConfiguration(backupConfiguration);
		
		//advance settings
		PreferencesConfiguration preferencesConfiguration = new PreferencesConfiguration();
		plan.setPreferencesConfiguration(preferencesConfiguration);
		DeployTargetDetail deployD2Dsetting = new DeployTargetDetail();
		plan.setDeployD2Dsetting(deployD2Dsetting);
//		ArchiveToTapeSettings archiveToTapeSettings = new ArchiveToTapeSettings();
		//archiveToTapeSettings.setPlanGlobalUUID(plan.getUuid()); //service already set this field
//		ArchiveToTapeDestinationInfo destinationInfo = new ArchiveToTapeDestinationInfo();
//		destinationInfo.setDomainName(hostName);
//		destinationInfo.setDestServerName(hostName);
//		archiveToTapeSettings.setArchiveToTapeDestinationInfo(destinationInfo);
//		plan.setArchiveToTapeSettings(archiveToTapeSettings);
		try {
			planService.createUnifiedPolicy(plan);
		} catch (EdgeServiceFault e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testDecryptString(){
		logger.debug(nativeFacade.decryptString("TkVGQRAHCSAYAAAAEGYAAEgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAZNb79UI0qDtWbxwL+DfCH6LGMaHrtyXykK5CucRogBOtUWNrkeqy7cEEDi8lg9vRxPyIVgy5VsT5/cZEeGSyOc4E1ekXJPB8eaNW6waQaNg"));
	}
}
