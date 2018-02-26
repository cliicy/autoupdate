package com.ca.arcserve.edge.app.msp.webservice.contract;

import java.io.Serializable;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.extjs.gxt.ui.client.data.BeanModelTag;

public class CustomerDetail implements Serializable, BeanModelTag{

	private static final long serialVersionUID = -136676543851695084L;
	private int id;
	private String description;
	private Customer customer;
	private List<PolicyInfo> plans;
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public List<PolicyInfo> getPlans() {
		return plans;
	}
	public void setPlans(List<PolicyInfo> plans) {
		this.plans = plans;
	}
	public int getId() {
		if (customer != null) {
			return customer.getId();
		} else {
			return 0;
		}
	}
	public String getDescription() {
		if (customer != null) {
			return customer.getDescription();
		} else {
			return "";
		}
	}
}
