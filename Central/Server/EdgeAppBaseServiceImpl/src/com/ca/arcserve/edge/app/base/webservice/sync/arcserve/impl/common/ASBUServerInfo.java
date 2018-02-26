package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common;

public class ASBUServerInfo implements IASBUServerInfo {
	private int 				_branchID;
	private String 				_serverName;
	private String				_URL;
	private String				_syncPath;
	private int					_serverType;
	private int					_retryTimes;
	private int					_retryInterval;
	
	@Override
	public String get_URL() {
		// TODO Auto-generated method stub
		return _URL;
	}

	@Override
	public int get_branchID() {
		// TODO Auto-generated method stub
		return _branchID;
	}

	@Override
	public int get_retryInterval() {
		// TODO Auto-generated method stub
		return _retryInterval;
	}

	@Override
	public int get_retryTimes() {
		// TODO Auto-generated method stub
		return _retryTimes;
	}

	@Override
	public String get_serverName() {
		// TODO Auto-generated method stub
		return _serverName;
	}

	@Override
	public int get_serverType() {
		// TODO Auto-generated method stub
		return _serverType;
	}

	@Override
	public String get_syncPath() {
		// TODO Auto-generated method stub
		return _syncPath;
	}

	public void set_branchID(int branchID) {
		_branchID = branchID;
	}

	public void set_serverName(String serverName) {
		_serverName = serverName;
	}

	public void set_URL(String url) {
		_URL = url;
	}

	public void set_syncPath(String syncPath) {
		_syncPath = syncPath;
	}

	public void set_serverType(int serverType) {
		_serverType = serverType;
	}

	public void set_retryTimes(int retryTimes) {
		_retryTimes = retryTimes;
	}

	public void set_retryInterval(int retryInterval) {
		_retryInterval = retryInterval;
	}
	
	
}
