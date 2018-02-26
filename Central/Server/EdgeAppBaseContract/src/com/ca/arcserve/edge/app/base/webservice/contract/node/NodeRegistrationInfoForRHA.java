/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

/**
 * @author lijwe02
 * 
 */
public class NodeRegistrationInfoForRHA extends NodeRegistrationInfo {
	private static final long serialVersionUID = -4987836227666698047L;
	private RHASourceNode sourceNode;
	private RHAControlService controlService;

	public RHASourceNode getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(RHASourceNode sourceNode) {
		this.sourceNode = sourceNode;
	}

	public RHAControlService getControlService() {
		return controlService;
	}

	public void setControlService(RHAControlService controlService) {
		this.controlService = controlService;
	}

	public String getKey() {
		if (sourceNode != null && controlService != null) {
			return controlService.getServer() + "-" + sourceNode.getScenarioId() + "-" + sourceNode.getNodeName() + "-"
					+ sourceNode.getVmName();
		}
		return null;
	}
}
