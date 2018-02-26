package com.ca.arcserve.edge.app.msp.webservice;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.msp.dao.IMspCustomerDao;
import com.ca.arcserve.edge.app.msp.webservice.contract.Customer;
import com.ca.arcserve.edge.app.msp.webservice.contract.CustomerDetail;
import com.ca.arcserve.edge.app.msp.webservice.contract.CustomerPagingConfig;
import com.ca.arcserve.edge.app.msp.webservice.contract.CustomerPagingResult;

public class MspCustomerServiceImpl implements IMspCustomerService {
	
	private IMspCustomerDao customerDao = DaoFactory.getDao(IMspCustomerDao.class);

	@Override
	public CustomerPagingResult getCustomers(CustomerPagingConfig config) throws EdgeServiceFault {
		List<Customer> customers = new ArrayList<Customer>();
		customerDao.as_edge_msp_customer_get(0, customers);
		
		CustomerPagingResult result = new CustomerPagingResult();
		result.setTotalCount(customers.size());
		result.setStartIndex(config.getStartIndex());
		
		List<CustomerDetail> customerDetails = new ArrayList<CustomerDetail>();
		int startIndex = config.getStartIndex() > 0 ? config.getStartIndex() : 0;
		int endIndex = startIndex + config.getCount() > 0 ? config.getCount() : 0;
		MspPlanServiceImpl mspPlanService = new MspPlanServiceImpl();
		
		for (int i = startIndex; i < endIndex && i < customers.size(); ++i) {
			CustomerDetail detail = new CustomerDetail();
			
			detail.setCustomer(customers.get(i));
			detail.setPlans(mspPlanService.getCustomerPlans(customers.get(i).getId()));
			
			customerDetails.add(detail);
		}
		
		result.setCustomerDetails(customerDetails);
		
		return result;
	}

	public Customer getCustomerByName(String customerName) throws EdgeServiceFault {
		List<Customer> customers = new ArrayList<Customer>();
		customerDao.as_edge_msp_customer_getByName(customerName, customers);
		
		if (customers.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.MSP_Customer_NotExists, "cannot find the customer, name = " + customerName);
		}
		
		return customers.get(0);
	}

	@Override
	public int addCustomer(Customer customer) throws EdgeServiceFault {
		if (customer == null || customer.getName() == null || customer.getName().isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid customer parameter, the name is null or empty.");
		}
		
		if (customer.getName().length() > 32) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid customer parameter, the length of name is larger than 32.");
		}
		
		if (!WSJNI.isUserExists(customer.getName())) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.MSP_Customer_NotExists, "The Windows Local User [" + customer.getName() + "] does not exist.");
		}
		
		int[] newCustomerId = new int[1];
		customerDao.as_edge_msp_customer_update(0, customer.getName(),customer.getDescription(), newCustomerId);
		return newCustomerId[0];
	}
	
	@Override
	public int modeifyCustomer(Customer customer) throws EdgeServiceFault {
		if (customer == null || customer.getId()<=0) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid customer parameter, the id is null or empty.");
		}
		
		if (!WSJNI.isUserExists(customer.getName())) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.MSP_Customer_NotExists, "The Windows Local User [" + customer.getName() + "] does not exist.");
		}
		
		int[] newCustomerId = new int[1];
		customerDao.as_edge_msp_customer_update(customer.getId(), customer.getName(),customer.getDescription(), newCustomerId);
		return newCustomerId[0];
	}

	@Override
	public void deleteCustomers(List<Integer> customerIds) throws EdgeServiceFault {
		if (customerIds != null) {
			for (int id : customerIds) {
				customerDao.as_edge_msp_customer_delete(id);
			}
		}
	}
	
	public void validateCustomer(Customer customer) throws EdgeServiceFault {
		WSJNI.validateLocalAccount(customer.getName(), customer.getPassword());
		
		String customerLocalGroupName = null;
		
		try {
			customerLocalGroupName = CommonUtil.getApplicationExtentionKey("CustomerLocalGroupName");
		} catch (Exception e) {
		}
		
		if (customerLocalGroupName != null && !customerLocalGroupName.isEmpty()) {
			WSJNI.validateUserLocalGroup(customer.getName(), customerLocalGroupName);
		}
	}

	@Override
	public List<String> getWindowsLocalUsers() throws EdgeServiceFault {
		String customerLocalGroupName = null;
		
		try {
			customerLocalGroupName = CommonUtil.getApplicationExtentionKey("CustomerLocalGroupName");
		} catch (Exception e) {
		}
		
		return WSJNI.getLocalUserNames(customerLocalGroupName);
	}
}
