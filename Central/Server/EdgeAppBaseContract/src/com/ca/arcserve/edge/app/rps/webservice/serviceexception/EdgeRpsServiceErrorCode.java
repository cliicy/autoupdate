package com.ca.arcserve.edge.app.rps.webservice.serviceexception;


public class EdgeRpsServiceErrorCode {
	
	/* common */
	public static final long Common_BASE = 0x0000000100000000L;
	public static final String Common_Service_General = String.valueOf(Common_BASE + 1);
	public static final String Common_Service_Database_Error = String.valueOf(Common_BASE + 2);
	
	/* login */
	public static final long Login_BASE = 0x0000000200000000L;
	public static final String Login_WrongCredential 	= String.valueOf(Login_BASE + 1);
	public static final String Login_NotAdministrator 	= String.valueOf(Login_BASE + 2);
	public static final String Login_UsernameRequired 	= String.valueOf(Login_BASE + 3);
	public static final String Login_PasswordRequired 	= String.valueOf(Login_BASE + 4);
	public static final String Login_UUIDRequired 		= String.valueOf(Login_BASE + 5);
	public static final String Login_WrongUUID 			= String.valueOf(Login_BASE + 6);
	public static final String Login_WrongNode 			= String.valueOf(Login_BASE + 7);
	public static final String Login_EmptyPassword		= String.valueOf(Login_BASE + 8);
	public static final String Login_Fail				= String.valueOf(Login_BASE + 9);
	
	/* Rps Node management */
	public static final long NODE = 0x0000000300000000L;
	public static final String Node_CantConnectRemoteRPS 			= String.valueOf(NODE + 1);
	public static final String Node_CantObtainNodesFromAD 			= String.valueOf(NODE + 2);
	public static final String Node_InvalidUser		 				= String.valueOf(NODE + 3);
	public static final String Node_UserPrivilegeNotEnough			= String.valueOf(NODE + 4);
	public static final String Node_CantProbeRPSNodesWithUDP		= String.valueOf(NODE + 5);
	public static final String Node_AlreadyExist					= String.valueOf(NODE + 6);
	public static final String Node_AlreadyManagedByMyself			= String.valueOf(NODE + 7);
	public static final String Node_RemoteRegistry_CantConnect		= String.valueOf(NODE + 8);
	public static final String Node_RemoteRegistry_WrongCredential	= String.valueOf(NODE + 9);
	public static final String Node_RemoteRegistry_NoPermission		= String.valueOf(NODE + 10);
	public static final String Node_RemoteRegistry_CantStartService	= String.valueOf(NODE + 11);
	public static final String Node_RemoteRegistry_FailedToRead		= String.valueOf(NODE + 12);
	public static final String Node_FailedToMarkRPSAsManaged		= String.valueOf(NODE + 13);
	public static final String Node_NodeGroupAlreadyExist			= String.valueOf(NODE + 14);
	public static final String Node_RPS_Reg_Fatal_Error				= String.valueOf(NODE + 15);
	public static final String Node_RPS_Reg_Again					= String.valueOf(NODE + 16);
	public static final String Node_RPS_Reg_Duplicate				= String.valueOf(NODE + 17);
	public static final String Node_RPS_UnReg_Not_Exist				= String.valueOf(NODE + 18);
	public static final String Node_RPS_UnReg_Not_Owner				= String.valueOf(NODE + 19);
	public static final String Node_RPS_Reg_connection_refuse		= String.valueOf(NODE + 20);
	public static final String Node_RPS_Reg_InvalidCredential		= String.valueOf(NODE + 21);
	public static final String Node_ContainsHTMLChars				= String.valueOf(NODE + 25);
	public static final String Node_DomainCannotBeContatced			= String.valueOf(NODE + 26);
	public static final String Node_ADSourceExist					= String.valueOf(NODE + 27);
	public static final String Node_RemoteCmRegistry_CantStartService = String.valueOf(NODE + 28);
	public static final String Node_NodeNameIsSpace					= String.valueOf(NODE + 30);
	public static final String Node_RPS_Reg_SAAS_NOT_ALLOW_Add		= String.valueOf(NODE + 37);
	public static final String Node_RPS_Reg_SAAS_NOT_ALLOW_Managed	= String.valueOf(NODE + 38);
	public static final String Node_RPS_Reg_RPS_CANNOT_CONNECT_EDGE	= String.valueOf(NODE + 39);
	public static final String Node_InvalidUser_WithName			= String.valueOf(NODE + 40);
	public static final String Node_RPS_ADD_CANNOT_CONNECT			= String.valueOf(NODE + 45);
	public static final String Node_RPS_ADD_MISMATCH_VERSION		= String.valueOf(NODE + 46);
	
}
