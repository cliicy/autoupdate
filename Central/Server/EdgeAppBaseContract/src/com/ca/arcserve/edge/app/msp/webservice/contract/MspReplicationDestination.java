package com.ca.arcserve.edge.app.msp.webservice.contract;

import java.io.Serializable;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.MspPlanSettings;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;

public class MspReplicationDestination implements Serializable {

	private static final long serialVersionUID = -6069102658303918083L;
	
	private MspPlanSettings mspPlanSettings;
	private RPSPolicy replicationRpsPolicy;
	private RpsHost replicationServer;
	private DataStoreSettingInfo dataStoreInfo;
	
	public MspPlanSettings getMspPlanSettings() {
		return mspPlanSettings;
	}
	public void setMspPlanSettings(MspPlanSettings mspPlanSettings) {
		this.mspPlanSettings = mspPlanSettings;
	}
	public RPSPolicy getReplicationRpsPolicy() {
		return replicationRpsPolicy;
	}
	public void setReplicationRpsPolicy(RPSPolicy replicationRpsPolicy) {
		this.replicationRpsPolicy = replicationRpsPolicy;
	}
	public RpsHost getReplicationServer() {
		return replicationServer;
	}
	public void setReplicationServer(RpsHost replicationServer) {
		this.replicationServer = replicationServer;
	}
	public DataStoreSettingInfo getDataStoreInfo()
	{
		return dataStoreInfo;
	}
	public void setDataStoreInfo( DataStoreSettingInfo dataStoreInfo )
	{
		this.dataStoreInfo = dataStoreInfo;
	}

}
