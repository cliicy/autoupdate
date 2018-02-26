/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

/**
 * @author lijwe02
 * 
 */
@SuppressWarnings("serial")
public class FieldFilter implements Serializable {
	private String comparison;
	private String field;
	private Object value;

	public String getComparison() {
		return comparison;
	}

	public void setComparison(String comparison) {
		this.comparison = comparison;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
