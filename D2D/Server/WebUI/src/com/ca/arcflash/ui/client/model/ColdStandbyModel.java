package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class ColdStandbyModel extends BaseModelData {
	
	private static final long serialVersionUID = -3003615598341377790L;
	
	public FailoverJobScript getFailoverJobScript() {
		return (FailoverJobScript)get("failoverJobScript");
	}
	public void setFailoverJobScript(FailoverJobScript failoverJobScript) {
		set("failoverJobScript", failoverJobScript);
	}
	public HeartBeatJobScript getHeartBeatJobScript() {
		return (HeartBeatJobScript)get("heartBeatJobScript");
	}
	public void setHeartBeatJobScript(HeartBeatJobScript heartBeatJobScript) {
		set("heartBeatJobScript", heartBeatJobScript);
	}
	public ReplicationJobScript getReplicationJobScript() {
		return (ReplicationJobScript)get("replicationJobScript");
	}
	public void setReplicationJobScript(ReplicationJobScript replicationJobScript) {
		set("replicationJobScript", replicationJobScript);
	}
	
	
	
}
