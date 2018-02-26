package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class NodeGroup  implements Serializable{

	/**
	 * The group for ungrouped nodes, the value is {@value}.
	 */
	public static final int UNGROUP			=	0;
	/**
	 * The group contains all nodes, the value is {@value}.
	 */
	public static final int ALLGROUP		=	-1;
	/**
	 * The group for nodes that have SQL Server installed, the value is {@value}.
	 */
	public static final int SQLSERVER		=	-2;
	/**
	 * The group for nodes that have Exchange installed, the value is {@value}.
	 */
	public static final int EXCHANGE		= 	-3;
	/**
	 * The group for nodes whose backup status were failed, the value is {@value}.
	 */
	public static final int Failed 			= 	-100;
	/**
	 * The group contains ESX server groups, the value is {@value}.
	 */
	public static final int ESX             =   -4;
	/**
	 * The group contains nodes that have UDP agent installed, the value is {@value}.
	 */
	public static final int D2D             =   -5;
	/**
	 * The group contains nodes that have ARCserver Backup installed, the value is {@value}.
	 */
	public static final int ASBU            =   -6;
	/**
	 * Distinguish group type in restore module, the value is {@value}.
	 */
	public static final int UNESX           =   -7;//distinguish group type in restore module 
	/**
	 * The group for ARCserve Backup Global Dashboard servers, the value is {@value}.
	 */
	public static final int GDB				=	-8;// GDB group type
	/**
	 * Default group type, including All groups/SQL/Exchange/Unassigned, the value is {@value}.
	 */
	public static final int Default			=	-9;
	/**
	 * The group contains RPS servers, the value is {@value}.
	 */
	public static final int RPS				=   -10;//recovery point server
	
	/**
	 * Not used, the value is {@value}.
	 */
	public static final int CpmPolicyGroupType = -11;
	/**
	 * The value is {@value}.
	 */
	public static final int D2DPolicyGroupType = -12;
	/**
	 * Not used, the value is {@value}.
	 */
	public static final int D2DoDPolicyGroupType = -13;
	/**
	 * Not used, the value is {@value}.
	 */
	public static final int VcmPolicyGroupType = -14;
	/**
	 * Not used, the value is {@value}.
	 */
	public static final int VspherePolicyGroupType = -15;
	/**
	 * The group contains nodes imported from RHA, the value is {@value}.
	 */
	public static final int RHAScenarioGroupType = -16; // Scenario group for nodes imported from RHA
	/**
	 * Not used, the value is {@value}.
	 */
	public static final int RemoteNodesGroupType = -17; // Remote Nodes group is the new group, 
														// Right now, RHA group will under this group,
														// other remote groups also will under this group
	/**
	 * The group for VM Backup Proxies, the value is {@value}.
	 */
	public static final int WinProxyGroupType = -18;
	/**
	 * The group contains Hyper-V nodes, the value is {@value}.
	 */
	public static final int HYPERV			= -19;
	/**
	 * distinguish group type in restore module, the value is {@value}.
	 */
	public static final int UNHYPERV			= -20; //distinguish group type in restore module
	/**
	 * The group contains saved node filters, the value is {@value}.
	 */
	public static final int NodeFilterGroupType = -21;
	/**
	 * The group contains nodes that have D2D On Demand installed, the value is {@value}.
	 */
	@Deprecated
	public static final int D2DOD           =   -30;	// (*D2DOD*)
	/**
	 * The group contains nodes that have Linux D2D installed, the value is {@value}.
	 */
	public static final int LinuxD2D        =   -31;
	/**
	 * The group contains nodes that is running a Linux operating system, the value is {@value}.
	 */
	public static final int LinuxNode       =   -32;
	/**
	 * The group contains nodes that were deployed with UDP agent recently, the value is {@value}.
	 */
	public static final int RecentDeployedGroup = -101;
	/**
	 * The group contains nodes that are available for remote deployment, the value is {@value}.
	 */
	public static final int AvailableDeployNodesGroup = -102;
	/**
	 * The group contains nodes that had not been assigned with plan, the value is {@value}.
	 */
	public static final int UnassignedPolicyGroup	=	-103;   // this group contains the node that not policy assigned to it

	
	/**
	 * The group contains virtual conversion nodes, the value is {@value}.
	 */
	public static final int GROUP_TYPE_VSB = -33; // VSB group type
	/**
	 * The group is the 'All Nodes' group under GROUP_TYPE_VSB group, the value is {@value}. 
	 */
	public static final int EMBED_GROUP_ID_VSB_ALL = -34; // All group under VSB group
	/**
	 * Action Required VSB group, action required means both source and standby vm are not running, the value is {@value}.
	 */
	public static final int EMBED_GROUP_ID_VSB_ActionRequired = -35;
	/**
	 * VM Running group under VSB group, vm running means source is not running and standby vm is running, the value is {@value}.
	 */
	public static final int EMBED_GROUP_ID_VSB_VMRunning = -36;
	/**
	 * Source running group under VSB group, means heart beat normal and standby vm not running, the value is {@value}.
	 */
	public static final int EMBED_GROUP_ID_VSB_SourceRunning = -37;
	/**
	 * Source and VM Running under VSB group, means heart beat normal and standby vm also running, the value is {@value}.
	 */
	public static final int EMBED_GROUP_ID_VSB_SourceAndVMRunning = -38;
	
	public static final int VCLOUD = -39;
	public static final int vApp = -40; //vApp group not displayed in left group navigation , just be used when browse vm node.
	
	public static final int IVM_NODE = -41;
	
	private static final long serialVersionUID = -774480725221209657L;
	
	private int id = ALLGROUP;
	private String name;
	private String comments = "";
	private int type = Default;
	private boolean haveChildren=false;
	
	public NodeGroup(){	
	}
	
	public NodeGroup(int id, String name, int type, String comments,boolean haveChildren){
		this.id = id;
		this.name = name;
		this.type = type;
		this.comments = comments;
		this.haveChildren = haveChildren;
	}
	
	/**
	 * Get type of the group. Refer to the constants definitions for available
	 * group types.
	 * 
	 * @return
	 */
	public int getType()
	{
		return this.type;		
	}
	
	/**
	 * Set type of the group. Refer to the constants definitions for available
	 * group types.
	 * 
	 * @param type
	 */
	public void setType(int type)
	{
		this.type = type;
	}
	
	/**
	 * Get ID of the group.
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Set ID of the group.
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Get group name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set group name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get comments of the group.
	 * 
	 * @return
	 */
	public String getComments() {
		return comments;
	}
	
	/**
	 * Set comments of the group.
	 * 
	 * @param comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	public boolean isHaveChildren() {
		return haveChildren;
	}

	public void setHaveChildren(boolean haveChildren) {
		this.haveChildren = haveChildren;
	}
}
