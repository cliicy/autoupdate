/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.vcm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lijwe02
 * 
 */
public class ImportNodeFromRPSResult implements Serializable {
	private static final long serialVersionUID = 7550616662643663631L;

	private int importedNodes;
	private int updateNodes;
	private int insertNodes;
	private int failedNodes;
	private List<Integer> converterIdList = new ArrayList<Integer>();

	public int getImportedNodes() {
		return importedNodes;
	}

	public void setImportedNodes(int importedNodes) {
		this.importedNodes = importedNodes;
	}

	public int getFailedNodes() {
		return failedNodes;
	}

	public void setFailedNodes(int failedNodes) {
		this.failedNodes = failedNodes;
	}

	public int getUpdateNodes() {
		return updateNodes;
	}

	public void setUpdateNodes(int updateNodes) {
		this.updateNodes = updateNodes;
	}

	public int getInsertNodes() {
		return insertNodes;
	}

	public void setInsertNodes(int insertNodes) {
		this.insertNodes = insertNodes;
	}

	public void addConverterId(int id) {
		if (!converterIdList.contains(id)) {
			converterIdList.add(id);
		}
	}

	public List<Integer> getConverterIdList() {
		return converterIdList;
	}
}
