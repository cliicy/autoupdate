package com.ca.arcserve.edge.app.msp.webservice.contract;

import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.common.BasePagingResult;

public class CustomerPagingResult extends BasePagingResult {

	private static final long serialVersionUID = 592192046758399384L;
	
	private List<CustomerDetail> customerDetails;

	public List<CustomerDetail> getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetails(List<CustomerDetail> customerDetails) {
		this.customerDetails = customerDetails;
	}

}
