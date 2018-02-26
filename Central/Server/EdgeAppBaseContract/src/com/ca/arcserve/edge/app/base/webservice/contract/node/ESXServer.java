package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class ESXServer implements Serializable {
	
	private static final long serialVersionUID = -8185252049439018713L;
	private String esxName = null;
	private String dcName = null;
	private int iTotalVMCount = 0;
	private int iNonWindows = 0;
	private int iNoncompatableHW = 0;
	private int iWindows = 0;
	private boolean skipNode = false;
	private String clusterName = null;
	
	public String getEsxName() {
		return esxName;
	}
	public void setEsxName(String esxServerName) {
		this.esxName = esxServerName;
	}
	public String getDataCenter() {
		return dcName;
	}
	public void setDataCenter(String dataCenterName) {
		this.dcName = dataCenterName;
	}
	public void setiTotalVMCount(int iTotalVMCount) {
		this.iTotalVMCount = iTotalVMCount;
	}
	public int getiTotalVMCount() {
		return iTotalVMCount;
	}
	public void setiNonWindows(int iNonWindows) {
		this.iNonWindows = iNonWindows;
	}
	public int getiNonWindows() {
		return iNonWindows;
	}
	public void setiNoncompatableHW(int iNoncompatableHW) {
		this.iNoncompatableHW = iNoncompatableHW;
	}
	public int getiNoncompatableHW() {
		return iNoncompatableHW;
	}
	public void setiWindows(int iWindows) {
		this.iWindows = iWindows;
	}
	public int getiWindows() {
		return iWindows;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public String getClusterName() {
		return clusterName;
	}
	public boolean isSkipNode() {
		return skipNode;
	}
	public void setSkipNode(boolean skipNode) {
		this.skipNode = skipNode;
	}
}
