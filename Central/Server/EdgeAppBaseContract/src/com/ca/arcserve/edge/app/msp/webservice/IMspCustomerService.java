package com.ca.arcserve.edge.app.msp.webservice;

import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.msp.webservice.contract.Customer;
import com.ca.arcserve.edge.app.msp.webservice.contract.CustomerPagingConfig;
import com.ca.arcserve.edge.app.msp.webservice.contract.CustomerPagingResult;

public interface IMspCustomerService {
	
	CustomerPagingResult getCustomers(CustomerPagingConfig config) throws EdgeServiceFault;
	int addCustomer(Customer customer) throws EdgeServiceFault;
	int modeifyCustomer(Customer customer) throws EdgeServiceFault;
	void deleteCustomers(List<Integer> customerIds) throws EdgeServiceFault;
	List<String> getWindowsLocalUsers() throws EdgeServiceFault;

}
