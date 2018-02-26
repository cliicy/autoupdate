package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import com.ca.arcserve.edge.app.base.schedulers.IEdgeTaskItem;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

abstract class SyncGlitter implements IEdgeTaskItem {
	private String m_strServerName = null;
	private int m_iBranchID = Integer.MIN_VALUE;
	private String m_strSyncPath = null;
	private String m_syncURL = null;
	private int _retryTimes = ConfigurationOperator.getDefaultretrytimes();
	private int _retryInterval = ConfigurationOperator.getDefaultretryinterval();
	private ASBUJobInfo jobinfo = null;

	public ASBUJobInfo getJobinfo() {
		return jobinfo;
	}

	public void setJobinfo(ASBUJobInfo jobinfo) {
		this.jobinfo = jobinfo;
	}

	public int get_retryTimes() {
		return _retryTimes;
	}

	public void set_retryTimes(int retryTimes) {
		_retryTimes = retryTimes;
	}

	public int get_retryInterval() {
		return _retryInterval;
	}

	public void set_retryInterval(int retryInterval) {
		_retryInterval = retryInterval;
	}

	public String GetSyncURL() {
		return m_syncURL;
	}

	public void SetSyncURL(String syncURL) {
		m_syncURL = syncURL;
	}

	void SetName(String name) {
		m_strServerName = name;
	}

	void SetBranchID(int branchID) {
		m_iBranchID = branchID;
	}

	void SetSyncPath(int branchID) {
		
			// String path = System.getProperty("user.dir");
			SyncEnvChecker checker = new SyncEnvChecker(0, branchID);
			m_strSyncPath = checker.getFolderPath();
			/*
			 * m_strSyncPath = path.substring(0, path
			 * .indexOf(ConfigurationOperater._LibPath)) +
			 * ConfigurationOperater._DefaultSyncPath
			 * .substring(ConfigurationOperater._DefaultSyncPath
			 * .lastIndexOf('\\'));
			 */
	}

	String GetName() {
		return m_strServerName;
	}

	int GetBranchID() {
		return m_iBranchID;
	}

	String GetSyncPath() {
		return m_strSyncPath;
	}

}
