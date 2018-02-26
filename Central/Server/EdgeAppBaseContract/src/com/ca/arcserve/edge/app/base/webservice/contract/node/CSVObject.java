package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.List;
/**
 * CSV object for export
 * 
 * @author zhati04
 *
 * @param <T>
 */
public class CSVObject<T> implements Serializable{
	private static final long serialVersionUID = 5664139569699704383L;
	private String[] headers;
	private List<T> CSVNodes;
	public String[] getHeaders() {
		return headers;
	}
	public void setHeaders(String[] headers) {
		this.headers = headers;
	}
	public List<T> getCSVNodes() {
		return CSVNodes;
	}
	public void setCSVNodes(List<T> cSVNodes) {
		CSVNodes = cSVNodes;
	}
}
