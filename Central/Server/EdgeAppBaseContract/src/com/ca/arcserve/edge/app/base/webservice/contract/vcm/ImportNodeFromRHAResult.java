/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.vcm;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lijwe02
 * 
 */
public class ImportNodeFromRHAResult implements Serializable {
	private static final long serialVersionUID = 7550616662643663631L;

	private Set<Integer> importedNodes = new HashSet<Integer>();
	private Set<Integer> updateNodes = new HashSet<Integer>();
	private Set<Integer> insertNodes = new HashSet<Integer>();
	private Set<Integer> failedNodes = new HashSet<Integer>();
	private Set<Integer> converterIdList = new HashSet<Integer>();

	public Set<Integer> getImportedNodes() {
		return importedNodes;
	}

	public Set<Integer> getFailedNodes() {
		return failedNodes;
	}

	public void addFailedNode(int failedNodes) {
		this.failedNodes.add(failedNodes);
	}

	public Set<Integer> getUpdateNodes() {
		return updateNodes;
	}

	public void addUpdateNode(int updateNodes) {
		this.updateNodes.add(updateNodes);
		importedNodes.add(updateNodes);
	}

	public Set<Integer> getInsertNodes() {
		return insertNodes;
	}

	public void addInsertNode(int insertNodes) {
		this.insertNodes.add(insertNodes);
		importedNodes.add(insertNodes);
	}

	public void addConverterId(int id) {
		converterIdList.add(id);
	}

	public Set<Integer> getConverterIdList() {
		return converterIdList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Deprecated
	// This is for AQA use, when generate client code with wsimport
	public void setImportedNodes(Set<Integer> importedNodes) {
		this.importedNodes = importedNodes;
	}

	@Deprecated
	// This is for AQA use, when generate client code with wsimport
	public void setUpdateNodes(Set<Integer> updateNodes) {
		this.updateNodes = updateNodes;
	}

	@Deprecated
	// This is for AQA use, when generate client code with wsimport
	public void setInsertNodes(Set<Integer> insertNodes) {
		this.insertNodes = insertNodes;
	}

	@Deprecated
	// This is for AQA use, when generate client code with wsimport
	public void setFailedNodes(Set<Integer> failedNodes) {
		this.failedNodes = failedNodes;
	}

	@Deprecated
	// This is for AQA use, when generate client code with wsimport
	public void setConverterIdList(Set<Integer> converterIdList) {
		this.converterIdList = converterIdList;
	}
}
