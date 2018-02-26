package com.ca.arcserve.edge.app.base.webservice.contract.log;

public enum Module {
	ReportAll(-2),
	All(-1),
	Common(0),
	ImportNodesFromAD(1),
	ImportNodesFromHypervisor(2),
	ImportNodesFromFile(3),
	PolicyManagement(4),
	ArcserveSync(5),
	D2DSync(6),
	D2DAPM(7),
	EdgeAPM(8),
	ReportCommon(9),
	ReportEmail(10),
	ReportEmailScheduler(11),
	RpsManagement(12),
	RpsDataStoreSetting(13),
	RpsPolicyManagement(14),
	RpsImportNodesFromAD(15),
	RpsImportNodesFromFile(16),
	SubmitD2DJob(17), 
	VerifyVMsJob(18),
	VCMHeartBeat(19),
	VCMVirtualStandby(20),
	UpdateMutipleNode(21),
	VCMRecoverPointSnapshots(22),
	ImportNodesFromRHA(23),
	MergeJob(24), 
	RemoteDeploy(25), 
	RecoveryPointSummarySync(26),
	InstantVM(27),
	ImportNodesFromVCloud(28),
	ASBUConnectServer(29),
	Discovery(37),
	AuditLogger(38),
	CancelMutipleJob(39),
	UpdateNode(40),
	ManageMultipleNodes(41),
	GatewayManagement(42),
	SendRegistrationEmails(43),
	DeleteNode(44);
		
	private int value;
	
	private Module(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	public static Module parse(int moduleId) {
		Module retval = null;
		
		for (Module m : Module.values()) {
			if (m.getValue() == moduleId) {
				retval = m;
			}
		}
		
		return retval;
	}
	
}
