package com.ca.arcserve.edge.app.msp.webservice;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.MspPlanSettings;
import com.ca.arcflash.rps.webservice.data.policy.RPSDataStoreSettings;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.msp.dao.IMspCustomerDao;
import com.ca.arcserve.edge.app.msp.webservice.contract.Customer;
import com.ca.arcserve.edge.app.msp.webservice.contract.MspReplicationDestination;
import com.ca.arcserve.edge.app.msp.webservice.contract.PlanPagingConfig;
import com.ca.arcserve.edge.app.msp.webservice.contract.PlanPagingResult;
import com.ca.arcserve.edge.app.rps.webservice.datastore.RPSDataStoreServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.rps.IRPSDataStoreService;

public class MspPlanServiceImpl implements IMspPlanService, IMspPlan4ClientService {
	
	private IMspCustomerDao customerDao = DaoFactory.getDao(IMspCustomerDao.class);
	private IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	
	private ICustomerContext customerContext;
	private IRPSDataStoreService dataStoreService = null;
	
	public MspPlanServiceImpl() {
		this(null);
	}
	
	public MspPlanServiceImpl(ICustomerContext customerContext) {
		this.customerContext = customerContext;
	}
	
	private IRPSDataStoreService getDataStoreService()
	{
		if (this.dataStoreService == null)
			this.dataStoreService = new RPSDataStoreServiceImpl();
		
		return this.dataStoreService;
	}

	@Override
	public List<MspReplicationDestination> getMspReplicationDestinations() throws EdgeServiceFault {
		List<MspReplicationDestination> mspReplicationDestinations = new ArrayList<MspReplicationDestination>();
		int customerId = customerContext.getCustomerId();
		
		List<EdgePolicy> policies = new ArrayList<EdgePolicy>();
		customerDao.as_edge_msp_customer_getAssignedPlans(customerId, policies);
		
		IRPSDataStoreService dataStoreService = getDataStoreService();
		
		PolicyManagementServiceImpl policyService = new PolicyManagementServiceImpl();
		for (EdgePolicy policy : policies) {
			String[] planGlobalUuid = new String[1];
			policyDao.as_edge_policy_getGlobalUuid(policy.getId(), planGlobalUuid);
			if (planGlobalUuid[0] == null || planGlobalUuid[0].isEmpty()) {
				continue;
			}
			
			UnifiedPolicy plan = policyService.loadUnifiedPolicyById(policy.getId());
			
			RPSPolicy firstReplicationRpsPolicy = plan.getRpsPolices().get(0).getRpsPolicy();
			firstReplicationRpsPolicy.getRpsSettings().setRpsReplicationSettings(new RPSReplicationSettings());
			
			RPSReplicationSettings mspReplication = plan.getMspServerReplicationSettings();
			
			RPSDataStoreSettings dataStoreSettings = firstReplicationRpsPolicy.getRpsSettings().getRpsDataStoreSettings();
			DataStoreSettingInfo dataStoreInfo = dataStoreService.getDataStoreByGuid(
				mspReplication.getHostId(), dataStoreSettings.getDataStoreName() );
			
			MspReplicationDestination destination = new MspReplicationDestination();
			destination.setMspPlanSettings(new MspPlanSettings(planGlobalUuid[0], policy.getUuid(), policy.getName()));
			destination.setReplicationServer(new RpsHost(mspReplication.getHostName(), mspReplication.getPort(), 
					mspReplication.getProtocol() == 0, "", ""));
			destination.setReplicationRpsPolicy(firstReplicationRpsPolicy);
			destination.setDataStoreInfo( dataStoreInfo );
			mspReplicationDestinations.add(destination);
		}
		
		return mspReplicationDestinations;
	}
	
	@Override
	public PlanPagingResult getAvailableMspPlans(int customerId, PlanPagingConfig config) throws EdgeServiceFault {
		ensureWindowsUserExist(customerId);
		
		List<EdgePolicy> daoPolicies = new ArrayList<EdgePolicy>();
		customerDao.as_edge_msp_customer_getAvailablePlans(customerId, daoPolicies);
		
		PlanPagingResult result = new PlanPagingResult();
		result.setTotalCount(daoPolicies.size());
		result.setStartIndex(config.getStartIndex());
		
		List<PolicyInfo> plans = new ArrayList<PolicyInfo>();
		int startIndex = config.getStartIndex() > 0 ? config.getStartIndex() : 0;
		int endIndex = startIndex + config.getCount() > 0 ? startIndex + config.getCount() : 0;
		for (int i = startIndex; i < endIndex && i < daoPolicies.size(); ++i) {
			PolicyInfo plan = new PolicyInfo();
			
			plan.setPolicyId(daoPolicies.get(i).getId());
			plan.setPolicyName(daoPolicies.get(i).getName());
			
			plans.add(plan);
		}
		
		result.setPlans(plans);
		
		return result;
	}
	
	private void ensureWindowsUserExist(int customerId) throws EdgeServiceFault {
		if (customerId <= 0) {
			return;
		}
		
		List<Customer> customers = new ArrayList<Customer>();
		customerDao.as_edge_msp_customer_get(customerId, customers);
		
		if (customers.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.MSP_Customer_NotExists, "Cannot find the customer, id = " + customerId);
		}
		
		if (!WSJNI.isUserExists(customers.get(0).getName())) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.MSP_Customer_NotExists, "The Windows Local User [" + customers.get(0).getName() + "] does not exist.");
		}
	}

	@Override
	public void assignMspPlans(int customerId, List<Integer> mspPlanIds) throws EdgeServiceFault {
		if (customerId <= 0) {
			return;
		}
		
		List<EdgePolicy> daoPolicies = new ArrayList<EdgePolicy>();
		customerDao.as_edge_msp_customer_getAssignedPlans(customerId, daoPolicies);
		
		List<Integer> assignedPlanIds = new ArrayList<Integer>();
		for (EdgePolicy policy : daoPolicies) {
			assignedPlanIds.add(policy.getId());
		}
		
		customerDao.as_edge_msp_customer_unassignPlan(customerId, 0);
		
		if (mspPlanIds != null && !mspPlanIds.isEmpty()) {
			List<Integer> newAssignedPlanIds = new ArrayList<Integer>();
			
			for (Integer id : mspPlanIds) {
				if (assignedPlanIds.contains(id)) {
					customerDao.as_edge_msp_customer_assignPlan(customerId, id);
				} else {
					newAssignedPlanIds.add(id);
				}
			}
			
			if (!newAssignedPlanIds.isEmpty()) {
				ensureWindowsUserExist(customerId);
				
				for (Integer id : newAssignedPlanIds) {
					customerDao.as_edge_msp_customer_assignPlan(customerId, id);
				}
			}
		}
	}
	
	public List<PolicyInfo> getCustomerPlans(int customerId) {
		List<EdgePolicy> daoPolicies = new ArrayList<EdgePolicy>();
		customerDao.as_edge_msp_customer_getAssignedPlans(customerId, daoPolicies);
		
		List<PolicyInfo> plans = new ArrayList<PolicyInfo>();
		
		for (EdgePolicy daoPolicy : daoPolicies) {
			PolicyInfo plan = new PolicyInfo();
			
			plan.setPolicyId(daoPolicy.getId());
			plan.setPolicyName(daoPolicy.getName());
			
			plans.add(plan);
		}
		
		return plans;
	}
	
	public boolean isPlanAssigned(int customerId, String planUuid) throws EdgeServiceFault {
		boolean[] exist = new boolean[1];
		customerDao.as_edge_msp_customer_isPlanAssigned(customerId, planUuid, exist);
		return exist[0];
	}
	
	@Override
	public void validateisRemoteConsole(String localConsoleFQDNName) throws EdgeServiceFault {
		if(localConsoleFQDNName.equalsIgnoreCase(CommonUtil.getFQDN())){
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.MSP_NotRemoteMSP, "Not a remote Console.");
		}
	}

}
