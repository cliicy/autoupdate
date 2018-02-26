/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.vcm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAControlService;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHASourceNode;

/**
 * @author lijwe02
 * 
 */
public class ImportNodeFromRHAParameters implements Serializable {
	private static final long serialVersionUID = 2203559810829706424L;

	private boolean importFromFile = false;
	private RHAControlService controlService;
	private List<RHASourceNode> nodeList;

	public RHAControlService getControlService() {
		return controlService;
	}

	public void setControlService(RHAControlService controlService) {
		this.controlService = controlService;
	}

	public List<RHASourceNode> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<RHASourceNode> nodeList) {
		this.nodeList = nodeList;
	}

	public boolean isImportFromFile() {
		return importFromFile;
	}

	public void setImportFromFile(boolean importFromFile) {
		this.importFromFile = importFromFile;
	}

	public void addRHASourceNode(RHASourceNode node) {
		if (node == null) {
			return;
		}
		if (nodeList == null) {
			nodeList = new ArrayList<RHASourceNode>();
		}
		nodeList.add(node);
	}
}
