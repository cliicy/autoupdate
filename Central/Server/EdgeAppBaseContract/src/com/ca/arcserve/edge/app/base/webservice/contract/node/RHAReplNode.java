/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lijwe02
 * 
 */
public class RHAReplNode implements Serializable {
	private static final long serialVersionUID = 7645849797170206743L;

	private String host;
	private String ip;
	private RHAReplNodeRole role;
	private List<RHARootDir> rootDirList = null;
	private List<RHAReplNode> subReplNodeList = null;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public RHAReplNodeRole getRole() {
		return role;
	}

	public void setRole(RHAReplNodeRole role) {
		this.role = role;
	}

	public void addRootDir(RHARootDir rootDir) {
		if (rootDir == null) {
			return;
		}
		if (rootDirList == null) {
			rootDirList = new ArrayList<RHARootDir>();
		}
		rootDirList.add(rootDir);
	}

	public List<RHARootDir> getRootDirList() {
		return rootDirList;
	}

	public void addSubReplNode(RHAReplNode replNode) {
		if (replNode == null) {
			return;
		}
		if (subReplNodeList == null) {
			subReplNodeList = new ArrayList<RHAReplNode>();
		}
		subReplNodeList.add(replNode);
	}

	public List<RHAReplNode> getSubReplNodeList() {
		return subReplNodeList;
	}
}
