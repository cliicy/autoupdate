package com.ca.arcserve.edge.app.base.serviceexception;

public class EdgeServiceErrorCode {
	/* common */
	public static final long Common_BASE = 0x0000000100000000L;
	/**
	 * this is for code that has only valid message in EdgeServiceFaultBean
	 */
	public static final String Common_Service_General		= String.valueOf(Common_BASE + 1);
	public static final String Common_ErrorOccursInService	= String.valueOf(Common_BASE + 2);

	public static final String Common_Service_FAIL_TO_GETLIST			= String.valueOf(Common_BASE + 10);
	public static final String Common_Service_NOT_FOUND					= String.valueOf(Common_BASE + 11);
	public static final String Common_Service_NOT_LOGIN					= String.valueOf(Common_BASE + 12);
	public static final String Common_Service_NOT_LOGIN_UI				= String.valueOf(Common_BASE + 13);
	public static final String Common_Service_License_ERROR_IN_DB		= String.valueOf(Common_BASE + 14);
	public static final String Common_Service_License_KEY_EXISTED		= String.valueOf(Common_BASE + 15);
	public static final String Common_Service_License_KEY_UNMATCHED		= String.valueOf(Common_BASE + 16);
	public static final String Common_Service_License_KEY_INVALID		= String.valueOf(Common_BASE + 17);
	public static final String Common_Service_License_NO_Report_license	= String.valueOf(Common_BASE + 18); //4294967314
	public static final String Common_Service_License_Error				= String.valueOf(Common_BASE + 19); //4294967315
	public static final String Common_Service_Dao_Execption				= String.valueOf(Common_BASE + 20);
	public static final String Common_Service_Unexcepted_Execption		= String.valueOf(Common_BASE + 21);
	public static final String Common_Service_BadParameter_IsNull		= String.valueOf(Common_BASE + 22);
	public static final String Common_Service_BadParameter_IsEmpty		= String.valueOf(Common_BASE + 23);
	public static final String Common_Service_BadParameter_OutOfRange	= String.valueOf(Common_BASE + 24);
	public static final String Common_Service_Webservice_NotAvailable	= String.valueOf(Common_BASE + 25);
	public static final String Common_Service_Webservice_VersionNotMatch	= String.valueOf(Common_BASE + 26);
	public static final String Common_Service_Webservice_NotReady			= String.valueOf(Common_BASE + 27);
	
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
	public static final String Login_NOTBuildInAdministrator	= String.valueOf(Login_BASE + 10);

	/* Node management */
	public static final long NODE = 0x0000000300000000L;
	public static final String Node_CantConnectRemoteD2D 			= String.valueOf(NODE + 1);
	public static final String Node_CantObtainNodesFromAD 			= String.valueOf(NODE + 2);
	public static final String Node_InvalidUser		 				= String.valueOf(NODE + 3);
	public static final String Node_UserPrivilegeNotEnough			= String.valueOf(NODE + 4);
	public static final String Node_CantProbeD2DNodesWithUDP		= String.valueOf(NODE + 5);
	public static final String Node_AlreadyExist					= String.valueOf(NODE + 6);
	public static final String Node_AlreadyManagedByMyself			= String.valueOf(NODE + 7);
	public static final String Node_RemoteRegistry_CantConnect		= String.valueOf(NODE + 8);
	public static final String Node_RemoteRegistry_WrongCredential	= String.valueOf(NODE + 9);
	public static final String Node_RemoteRegistry_NoPermission		= String.valueOf(NODE + 10);
	public static final String Node_RemoteRegistry_CantStartService	= String.valueOf(NODE + 11);
	public static final String Node_RemoteRegistry_FailedToRead		= String.valueOf(NODE + 12);
	public static final String Node_FailedToMarkD2DAsManaged		= String.valueOf(NODE + 13);
	public static final String Node_NodeGroupAlreadyExist			= String.valueOf(NODE + 14);
	public static final String Node_D2D_Reg_Fatal_Error				= String.valueOf(NODE + 15);
	public static final String Node_D2D_Reg_Again					= String.valueOf(NODE + 16);
	public static final String Node_D2D_Reg_Duplicate				= String.valueOf(NODE + 17);
	public static final String Node_D2D_UnReg_Not_Exist				= String.valueOf(NODE + 18);
	public static final String Node_D2D_UnReg_Not_Owner				= String.valueOf(NODE + 19);
	public static final String Node_D2D_Reg_connection_refuse		= String.valueOf(NODE + 20);
	public static final String Node_D2D_Reg_InvalidCredential		= String.valueOf(NODE + 21);
	public static final String Node_ESX_InvalidEsxServerOrVCName	= String.valueOf(NODE + 22);
	public static final String Node_ESX_InvalidInformationOrServerNotAvailable	= String.valueOf(NODE + 23);
	public static final String Node_ESX_CantObtainVirtualMachinesFromESXOrVC	= String.valueOf(NODE + 24);
	public static final String Node_ContainsHTMLChars				= String.valueOf(NODE + 25);
	public static final String Node_DomainCannotBeContatced			= String.valueOf(NODE + 26);
	public static final String Node_ADSourceExist					= String.valueOf(NODE + 27);
	public static final String Node_RemoteCmRegistry_CantStartService = String.valueOf(NODE + 28);
	public static final String Node_EsxSourceExist					= String.valueOf(NODE + 29);
	public static final String Node_NodeNameIsSpace					= String.valueOf(NODE + 30);
	public static final String Node_VCM_MACHINE_ISNOT_ESXVM			= String.valueOf(NODE + 31);
	public static final String Node_VCM_VM_DOESNOT_EXIST_ON_ESX		= String.valueOf(NODE + 32);
	public static final String Node_VCM_VM_TOOLS_NOT_INSTALLED		= String.valueOf(NODE + 33);
	public static final String Node_VCM_VM_TOOLS_NOT_RUNNING		= String.valueOf(NODE + 34);
	public static final String Node_VCM_VC_ESX_INVALID_CREDENTIALS	= String.valueOf(NODE + 35);
	public static final String Node_VCM_VC_ESX_CONNECT_ERROR		= String.valueOf(NODE + 36);
	public static final String Node_D2D_Reg_SAAS_NOT_ALLOW_Add		= String.valueOf(NODE + 37);
	public static final String Node_D2D_Reg_SAAS_NOT_ALLOW_Managed	= String.valueOf(NODE + 38);
	public static final String Node_D2D_Reg_D2D_CANNOT_CONNECT_EDGE	= String.valueOf(NODE + 39);
	public static final String Node_InvalidUser_WithName			= String.valueOf(NODE + 40);
	public static final String Node_ESX_ConnectFail_InvalidLoginException	= String.valueOf(NODE + 41);
	public static final String Node_ESX_ConnectFail_NoRouteToHostException	= String.valueOf(NODE + 42);
	public static final String Node_ESX_ConnectFail_SocketException			= String.valueOf(NODE + 43);
	public static final String Node_RemoteRegistry_CantStartService_VCMAPP	= String.valueOf(NODE + 44);
	public static final String Node_D2D_ADD_CANNOT_CONNECT			= String.valueOf(NODE + 45);
	public static final String Node_D2D_ADD_MISMATCH_VERSION		= String.valueOf(NODE + 46);
	public static final String Node_ASBU_ADD_CANNOT_CONNECT			= String.valueOf(NODE + 47);
	public static final String Node_ASBU_ADD_MISMATCH_VERSION		= String.valueOf(NODE + 48);
	public static final String Node_UPDATE_VMWARED2D_ESX_TIMEOUT	= String.valueOf(NODE + 49);
	public static final String Node_NOTFOUND						= String.valueOf(NODE + 50);
	public static final String Node_ESX_NOTFOUND					= String.valueOf(NODE + 51);
	public static final String Node_CANT_QUERY_VM_HOSTNAME			= String.valueOf(NODE + 52);
	public static final String NODE_ESX_SERVER_VERSION_NOT_SUPPORT 	= String.valueOf(NODE + 53);
	public static final String NODE_ESX_SERVER_VERSION_RETRIEVE_FAIL= String.valueOf(NODE + 54);
	public static final String NODE_MULTIPLE_UPDATE_ANOTHER_THREAD_RUNNING = String.valueOf(NODE + 55);
	public static final String NODE_VCM_SOURCE_MACHINE_ADAPTER_NOT_EXIST	= String.valueOf(NODE + 56);
	public static final String NODE_VCM_CANNOT_CONNECT_TO_MONITOR	= String.valueOf(NODE + 57);
	public static final String NODE_VCM_CANNOT_CONNECT_TO_CONVERTER	= String.valueOf(NODE + 58);
	public static final String NODE_VCM_CANNOT_READ_POLICY	= String.valueOf(NODE + 59);
	public static final String NODE_ESX_HTTP_REDIRECT = String.valueOf(NODE+60);
	public static final String Node_D2D_Reg_FailedToConnectD2DService		= String.valueOf(NODE + 61);
	public static final String Node_Filter_NameExist = String.valueOf(NODE + 62);
	public static final String Node_HYPERV_CONNECT_ERR = String.valueOf(NODE + 63);
	public static final String Node_HYPERV_GETVMLIST_ERR = String.valueOf(NODE + 64);
	public static final String Node_Linux_No_D2D_Server = String.valueOf(NODE + 65);
	public static final String Node_Linux_No_Available_D2D_Server = String.valueOf(NODE + 66);
	public static final String Node_Linux_D2D_Server_Having_Plan = String.valueOf(NODE + 67);
	public static final String Node_Linux_D2D_Server_Managed_By_Others = String.valueOf(NODE + 68);
	public static final String Node_Linux_D2D_Server_Not_Reachable = String.valueOf(NODE + 69);
	public static final String Node_Delete_Node_Is_Monitor = String.valueOf(NODE + 70);
	public static final String Node_Delete_Node_Is_Converter = String.valueOf(NODE + 71);
	public static final String Node_Delete_Node_Is_Proxy = String.valueOf(NODE + 72);
	public static final String Node_Delete_Linux_D2D_Server_Linux_Node_Exist = String.valueOf(NODE + 73);
	public static final String Node_HYPERV_InvalidServer = String.valueOf(NODE + 74);
	public static final String Node_HYPERV_ConnectFail_InvalidLoginException = String.valueOf(NODE + 75);
	public static final String Node_HYPERV_ConnectFail_NotAdministratorException = String.valueOf(NODE + 76);
	public static final String Node_HYPERV_InvalidInformationOrServerNotAvailable = String.valueOf(NODE + 77);
	public static final String Node_HYPERV_ConnectFail_Unkown = String.valueOf(NODE + 78);
	public static final String Node_HYPERV_ConnectFail_UserNotEnoghtPrevilege = String.valueOf(NODE + 79);
	public static final String Node_Linux_D2D_Server_Version_Not_Match = String.valueOf(NODE + 80);
	public static final String Node_Linux_D2D_Server_UDP_IP_Not_Reachable = String.valueOf(NODE + 81);
	public static final String Node_RECOVERYPOINT_BROWSER_NOTFOUND = String.valueOf(NODE + 84);
	public static final String Linux_RECOVERYPOINT_BROWSER_NOTFOUND = String.valueOf(NODE + 85);
	public static final String Node_Linux_D2D_Server_Version_Low = String.valueOf(NODE + 86);
	public static final String Node_D2D_Server_Version_Not_Match = String.valueOf(NODE + 87);
	public static final String Node_Delete_Node_Is_Recovery_Server = String.valueOf(NODE + 88);
	
	public static final String Node_HYPERV_Invalid_Cluster_Credential = String.valueOf(NODE + 91);
	public static final String Node_HYPERV_ConnectFail_Cluster_Service = String.valueOf(NODE + 92);
	public static final String Node_HYPERV_ConnectFail_Access_Deny = String.valueOf(NODE + 93);
	public static final String Node_HyperV_Host_Exist = String.valueOf(NODE + 95);
	public static final String Node_Filter_ContainsHTMLChars		= String.valueOf(NODE + 96);
	public static final String Node_HYPERV_CONNECT_Invalidate_In_Other_Node = String.valueOf(NODE + 97);
	public static final String Node_HYPERV_CONNECT_InValidate_In_Other_Node_Permisson = String.valueOf(NODE + 98);
	public static final String Node_HYPERV_Cluster_CONNECT_Local_Account_Format = String.valueOf(NODE + 99);
	
	// Specify hypervisor
	public static final String Node_SpecifyHypervisor_Undetected = String.valueOf(NODE + 100);
	public static final String Node_SpecifyHypervisor_Physical = String.valueOf(NODE + 101);
	public static final String Node_SpecifyHypervisor_TypeMismatch = String.valueOf(NODE + 102);
	public static final String Node_SpecifyHypervisor_vCenterNotAllowed = String.valueOf(NODE + 103);
	public static final String Node_SpecifyHypervisor_Unsupported = String.valueOf(NODE + 104);
	public static final String Node_SpecifyHypervisor_EsxNotBelong = String.valueOf(NODE + 105);
	public static final String Node_SpecifyHypervisor_HyperVNotBelong = String.valueOf(NODE + 106);
	public static final String Node_SpecifyHypervisor_OtherHypervisorIsEsx = String.valueOf(NODE + 107);
	public static final String Node_SpecifyHypervisor_OtherHypervisorIsHyperV = String.valueOf(NODE + 108);

	//vCloud
	public static final String Node_vCloud_Invalid_UserName = String.valueOf(NODE + 109);
	public static final String Node_vCloud_Access_Denied =  String.valueOf(NODE + 110);
	public static final String Node_vCloud_Connect_Failed = String.valueOf(NODE + 111);
	
	public static final String Node_CantConnect_NetWorkNotAvailable = String .valueOf(NODE+112);
	public static final String Node_CantConnect_Admin$Disable = String.valueOf(NODE+113);
	public static final String Node_CantConnect_ServiceDown = String .valueOf(NODE+114);
	public static final String Node_CantConnect_WrongProtocolOrPort = String.valueOf(NODE+115);
	public static final String Node_CantConnect_AccessServiceError = String.valueOf(NODE+116);
	public static final String Node_CantConnect_ServiceInternalError = String.valueOf(NODE+117);
	public static final String Node_CantConnect_ASBUServiceError = String.valueOf(NODE+118);
	
	public static final String NodeExportFailed = String.valueOf(NODE+121);
	
	//hyperv
	public static final String Node_HYPERV_Private_Valid_Account_In_Other_Node = String.valueOf(NODE + 130);
	public static final String Node_HYPERV_Private_Valid_Account_Access_Deny = String.valueOf(NODE + 131);
	public static final String Node_HYPERV_ConnectFail_ServiceNotExist = String.valueOf(NODE + 132);

	/* Configuration */
	public static final long Configuration_BASE = 0x0000000400000000L;
	public static final String Configuration_LoginFailed4User 		= String.valueOf(Configuration_BASE + 1);
	public static final String Configuration_FailedSaveCfg 			= String.valueOf(Configuration_BASE + 2);
	public static final String Configuration_FailedCfgDataSource 	= String.valueOf(Configuration_BASE + 3);
	public static final String Configuration_FailedReadCfg 			= String.valueOf(Configuration_BASE + 4);
	public static final String Configuration_FailedcreateDB 	    = String.valueOf(Configuration_BASE + 5);
	public static final String Configuration_CanotConnectToSpecifiedInst = String.valueOf(Configuration_BASE + 6);
	public static final String Configuration_EnableTCPIP4SpecifiedInst = String.valueOf(Configuration_BASE + 7);
	public static final String Configuration_InvalidASBUSyncFolder = String.valueOf(Configuration_BASE + 8);
	public static final String Configuration_FailConnect_CM			= String.valueOf(Configuration_BASE + 9 );
	public static final String Configuration_FailAuth_CM			= String.valueOf(Configuration_BASE + 10 );
	public static final String Configuration_Report_FailConnectDb	= String.valueOf(Configuration_BASE + 11 );
	public static final String Configuration_Report_FailConnectCMDb = String.valueOf(Configuration_BASE + 12 );
	public static final String Configuration_FailConnectCMDb		= String.valueOf(Configuration_BASE + 13 );
	public static final String Configuration_GL_ERROR		        = String.valueOf(Configuration_BASE + 14 );
	public static final String Configuration_Failed_testMail        = String.valueOf(Configuration_BASE + 15 );
	

	/* arcserve backup function */
	public static final long ABFunc_BASE = 0x0000000500000000L;
	public static final String ABFunc_UserNamePasswordError 	 = String.valueOf(ABFunc_BASE + 1);
	public static final String ABFunc_NoPermission 				 = String.valueOf(ABFunc_BASE + 2);
	public static final String ABFunc_ConnectArcserveFailed      = String.valueOf(ABFunc_BASE + 3);
	public static final String ABFunc_WCFConnectTimeout    		 = String.valueOf(ABFunc_BASE + 4);
	public static final String ABFunc_HaveManagedByAnotherServer = String.valueOf(ABFunc_BASE + 5);
	public static final String ABFunc_UsrPasswordofHost_NotFind	 = String.valueOf(ABFunc_BASE + 6);

	/* arcserve Sync function */
	public static final long ARCserve_Sync_BASE = 0x0000000600000000L;
	public static final String ARCserve_Sync_General					= String.valueOf(ARCserve_Sync_BASE + 1);
	public static final String ARCserve_Sync_UserNamePasswordError 		= String.valueOf(ARCserve_Sync_BASE + 2);
	public static final String ARCserve_Sync_NoPermission 				= String.valueOf(ARCserve_Sync_BASE + 3);
	public static final String ARCserve_Sync_ConnectArcserveFailed     	= String.valueOf(ARCserve_Sync_BASE + 4);
	public static final String ARCserve_Sync_WCFConnectTimeout    		= String.valueOf(ARCserve_Sync_BASE + 5);
	public static final String ARCserve_Sync_FullSyncNotFinished		= String.valueOf(ARCserve_Sync_BASE + 6);

	/* D2D Sync function */
	public static final long D2D_Sync_BASE = 0x0000000700000000L;
	public static final String D2D_Sync_In_Progress				= String.valueOf(D2D_Sync_BASE + 1);
	
	/* Policy Management */
	public static final long PolicyManagement_Base = 0x0000000800000000L;
	public static final String PolicyManagement_BadParameters					= String.valueOf( PolicyManagement_Base + 1 );
	public static final String PolicyManagement_BadPolicyName					= String.valueOf( PolicyManagement_Base + 2 );
	public static final String PolicyManagement_NameDuplicated					= String.valueOf( PolicyManagement_Base + 3 );
	public static final String PolicyManagement_PolicyNotFound					= String.valueOf( PolicyManagement_Base + 4 );
	public static final String PolicyManagement_BadPolicyContent				= String.valueOf( PolicyManagement_Base + 5 );
	public static final String PolicyManagement_EditSessionNotFound				= String.valueOf( PolicyManagement_Base + 6 );
	public static final String PolicyManagement_NodeNotBeenManaged				= String.valueOf( PolicyManagement_Base + 7 );
	public static final String PolicyManagement_BadBackupConfiguration			= String.valueOf( PolicyManagement_Base + 8 );
	public static final String PolicyManagement_BadArchiveConfiguration			= String.valueOf( PolicyManagement_Base + 9 );
	public static final String PolicyManagement_BadScheduledExportConfiguration	= String.valueOf( PolicyManagement_Base + 10 );
	public static final String PolicyManagement_BadPreferencesConfiguration		= String.valueOf( PolicyManagement_Base + 11 );
	public static final String PolicyManagement_BadVcmConfiguration				= String.valueOf( PolicyManagement_Base + 12 );
	public static final String PolicyManagement_BadVMBackupConfiguration		= String.valueOf( PolicyManagement_Base + 13 );
	public static final String PolicyManagement_Linux_Destination_Not_Found		= String.valueOf( PolicyManagement_Base + 14 );
	public static final String PolicyManagement_Linux_Destination_Wrong_Credentials		= String.valueOf( PolicyManagement_Base + 15 );
	public static final String PolicyManagement_Linux_Destination_Common_ERROR		= String.valueOf( PolicyManagement_Base + 16 );
	public static final String PolicyManagement_NameInvalidChar = String.valueOf(PolicyManagement_Base + 17);
	public static final String PolicyManagement_BrowserNoWritePermission = String.valueOf(PolicyManagement_Base + 18);
	public static final String policyManagement_Replication_TargetRps_SmallerThan_SourceRps = String.valueOf(PolicyManagement_Base + 19);
	public static final String policyManagement_Backup_TargetRps_SmallerThan_SourceNode = String.valueOf(PolicyManagement_Base + 20);
	public static final String policyManagement_Pause_Rps_Version_Low = String.valueOf(PolicyManagement_Base + 21);
	public static final String PolicyManagement_NameDuplicatedInSite			= String.valueOf( PolicyManagement_Base + 22 );
	public static final String PolicyManagement_Deploy_VMBackupJob_Running			= String.valueOf( PolicyManagement_Base + 23 );

	/* Email */
	public static final long Email_Base = 0x0000000900000000L;
	public static final String Email_AuthenticationFailed			= String.valueOf( Email_Base + 1 );
	public static final String Email_FolderError					= String.valueOf( Email_Base + 2 );
	public static final String Email_MethodNotSupport				= String.valueOf( Email_Base + 3 );
	public static final String Email_SendFailed						= String.valueOf( Email_Base + 4 );
	public static final String Email_NoSuchProvider					= String.valueOf( Email_Base + 5 );
	public static final String Email_WrongFromFormat				= String.valueOf( Email_Base + 6 );
	public static final String Email_SendFailed_RecipientAddresses	= String.valueOf( Email_Base + 7 );
    
	public static final String TestMailFolderError                  = String.valueOf( Email_Base + 8 );
	public static final String TestMailUserError                    = String.valueOf( Email_Base + 9 );
	public static final String TestMailFromError                    = String.valueOf( Email_Base + 10 );
	public static final String TestMailRecError                     = String.valueOf( Email_Base + 11 ); 
	public static final String TestMailHostError                    = String.valueOf( Email_Base + 12 );
	public static final String TestMailPortError                    = String.valueOf( Email_Base + 13 );
	public static final String TestMailProxyError                   = String.valueOf( Email_Base + 14 );  
	public static final String TestMailCommonError                  = String.valueOf( Email_Base + 15 );
	public static final String TestMailSSLError						= String.valueOf( Email_Base + 16 );
	
	/* Patch Manager */
	public static final long APM_Base = 0x0000000A00000000L;
	public static final String AutoUpdateConfig_InvalidServerName            = String.valueOf(APM_Base + 1);
	public static final String AutoUpdateConfig_InvalidPort            = String.valueOf(APM_Base + 2);
	public static final String AutoUpdateConfig_Edge_Fail_Load		= String.valueOf(APM_Base + 3);
	public static final String AutoUpdateConfig_Edge_Fail_Save		= String.valueOf(APM_Base + 4);
	public static final String AutoUpdateConfig_Edge_Fail_InstallPatch = String.valueOf(APM_Base + 5);
	public static final String AutoUpdateConfig_Edge_Fail_TestConnect = String.valueOf(APM_Base + 6);
	public static final String APM_EDGE_Fail_PatchManagerStatus = String.valueOf(APM_Base + 7);
	public static final String APM_FailCheckUpdate = String.valueOf(APM_Base + 8);
	public static final String APM_BACKEND_BUSY = String.valueOf(APM_Base + 9);
	public static final String APM_BACKEND_DEAD = String.valueOf(APM_Base + 10);
	public static final String APM_FAIL_GET_PATCHINFO = String.valueOf(APM_Base + 11);

	/* Edge VCM */
	public static final long EdgeVCM_Base = 0x0000000B00000000L;
	public static final String EdgeVCM_FailedToConnectEdgeVSphere		= String.valueOf( EdgeVCM_Base + 1 );
	public static final String EdgeVCM_FailedToImportVMFromEdgeVSphere	= String.valueOf( EdgeVCM_Base + 2 );
	public static final String EdgeVCM_FailedToConnectRHAControlService	= String.valueOf( EdgeVCM_Base + 3 );
	public static final String EdgeVCM_FailedToParseRHAData				= String.valueOf( EdgeVCM_Base + 4 );
	public static final String EdgeVCM_FailedToConnectRHACredentialError= String.valueOf( EdgeVCM_Base + 5 );
	public static final String EdgeVCM_ConverterNotFound				= String.valueOf( EdgeVCM_Base + 6 );
	
	/* D2D Backup */
	public static final long Backup_BASE = 0x0000000C00000000L;
	public static final String Backup_InvalidBackupType 	=  String.valueOf(Backup_BASE + 1);
	public static final String Backup_NoBackupName			=  String.valueOf(Backup_BASE + 2);
	public static final String Backup_NoBackupConfiguration	=  String.valueOf(Backup_BASE + 3);
	public static final String Backup_AnotherJobRunning 	=  String.valueOf(Backup_BASE + 4);
	public static final String Backup_NodeManagedByOthers 	=  String.valueOf(Backup_BASE + 5);
	public static final String Backup_ProxyManagedByOthers	=  String.valueOf(Backup_BASE + 6);
	public static final String Backup_ProxyNotFound			=  String.valueOf(Backup_BASE + 7);
	public static final String Backup_CantConnect2Proxy		=  String.valueOf(Backup_BASE + 8);
	/* cancel VM backup job*/
	public static final String CancelVMBackupJob_CantConnect2Proxy	=  String.valueOf(Backup_BASE + 9);
	public static final String CancelVMBackupWaitingJobFailed		= String.valueOf( Backup_BASE + 10 );
	public static final String LinuxBackup_NodeManagedByOthers		= String.valueOf( Backup_BASE + 11 );

	/* D2D Backup */
	public static final long D2D_Error = 0x0000000D00000000L;
	public static final String D2D_Error_Identifier	=  String.valueOf(D2D_Error + 1);
	
	// RPS
	public static final long RPS_Base = 0x0000000E00000000L;
	public static final String RPS_CannotConnectServer 		= String.valueOf(RPS_Base + 1);
	public static final String RPS_DATASTORE_STARTFAILED 	= String.valueOf(RPS_Base + 2);
	public static final String RPS_DATASTORE_STOPFAILED 	= String.valueOf(RPS_Base + 3);
	public static final String RPS_DATASTORE_INVALIDPATH 	= String.valueOf(RPS_Base + 4);
	public static final String RPS_AlreadyExist					= String.valueOf(RPS_Base + 5);
	public static final String RPS_Server_Version_Not_Match		= String.valueOf(RPS_Base + 6);
	
	/* Rps Policy management */
	public static final long POLICY = 0x0000000E10000000L;
	public static final String POLICY_RPS_CANNOT_CONNECT			= String.valueOf(POLICY + 1);
	public static final String POLICY_RPS_DELETE_FAILED				= String.valueOf(POLICY + 2);
	public static final String POLICY_RPS_DELETE_FAILED_USED		= String.valueOf(POLICY + 3);
	public static final String POLICY_RPS_SAVE_DB_FAILED			= String.valueOf(POLICY + 4);
	public static final String POLICY_RPS_RETRIEVE_FAILED			= String.valueOf(POLICY + 5);
	public static final String POLICY_RPS_WrongCredential			= String.valueOf(POLICY + 6);
	public static final String POLICY_RPS_Node_DELETE_FAILED_USED		= String.valueOf(POLICY + 7);
	public static final String POLICY_RPS_MANAGED_BY_ANOTHER_CONSOLE		= String.valueOf(POLICY + 8);
	
	// EdgeRHA
	public static final long EdgeRHA_Base = 0x0000000F00000000L;
	public static final String EdgeRHA_ErrorGettingScenarioList					= String.valueOf( EdgeRHA_Base + 1 );
	public static final String EdgeRHA_FailedToConnectRHAControlService			= String.valueOf( EdgeRHA_Base + 2 );
	public static final String EdgeRHA_FailedToParseRHAData						= String.valueOf( EdgeRHA_Base + 3 );
	public static final String EdgeRHA_NotInitializedSoapService				= String.valueOf( EdgeRHA_Base + 4 );
	public static final String EdgeRHA_WrongCredentialToRHAControlService		= String.valueOf( EdgeRHA_Base + 5 );
	public static final String EdgeRHA_WrongCredentialToHost					= String.valueOf( EdgeRHA_Base + 6 );
	public static final String EdgeRHA_WrongCredentialOrInvalidSessionIdToHost	= String.valueOf( EdgeRHA_Base + 7 );
	public static final String EdgeRHA_ErrorGettingCSList						= String.valueOf( EdgeRHA_Base + 8 );
	public static final String EdgeRHA_ErrorGettingSGList						= String.valueOf( EdgeRHA_Base + 9 );
	public static final String EdgeRHA_ErrorGroupNotExist						= String.valueOf( EdgeRHA_Base + 10 );
	public static final String EdgeRHA_ErrorToSaveCSSettings					= String.valueOf( EdgeRHA_Base + 11 );
	public static final String EdgeRHA_ErrorToGetCSSettings						= String.valueOf( EdgeRHA_Base + 12 );
	public static final String EdgeRHA_FailToConnectHost						= String.valueOf( EdgeRHA_Base + 13 );
	public static final String EdgeRHA_FailToValidateCredentialWithUnknowError	= String.valueOf( EdgeRHA_Base + 14 );
	public static final String EdgeRHA_ErrorGettingHostList						= String.valueOf( EdgeRHA_Base + 15 );
	public static final String EdgeRHA_FailedToParseHostData					= String.valueOf( EdgeRHA_Base + 16 );

	
	public static final String EdgeRHA_FailToSaveScenarioProperty               = String.valueOf( EdgeRHA_Base + 20 );
		
	public static final String EdgeRHA_ErrorAddingControlService				= String.valueOf( EdgeRHA_Base + 22 );
	public static final String EdgeRHA_ErrorUpdatingControlService				= String.valueOf( EdgeRHA_Base + 23 );
	public static final String EdgeRHA_ErrorDeletingControlService				= String.valueOf( EdgeRHA_Base + 24 );
	public static final String EdgeRHA_ErrorControlServiceNameDuplicated		= String.valueOf( EdgeRHA_Base + 25 );
	public static final String EdgeRHA_ErrorQueryingControlService				= String.valueOf( EdgeRHA_Base + 26 );
	
	public static final String EdgeRHA_FailToAddHost							= String.valueOf( EdgeRHA_Base + 27 );
	public static final String EdgeRHA_FailToRemoveHost							= String.valueOf( EdgeRHA_Base + 28 );
	public static final String EdgeRHA_FailToRenameHost							= String.valueOf( EdgeRHA_Base + 29 );
	
	public static final String EdgeRHA_FailToGetSnapshotList					= String.valueOf( EdgeRHA_Base + 30 );
	public static final String EdgeRHA_FailToGetEngineInfo						= String.valueOf( EdgeRHA_Base + 31 );
	public static final String EdgeRHA_FailToSetEngineInfo						= String.valueOf( EdgeRHA_Base + 32 );
	public static final String EdgeRHA_FailToSetDiskMapping						= String.valueOf( EdgeRHA_Base + 33 );
	public static final String EdgeRHA_FailToGetDiskMapping						= String.valueOf( EdgeRHA_Base + 34 );
	public static final String EdgeRHA_ErrorQueryingRemoteInstallHosts			= String.valueOf( EdgeRHA_Base + 35 );
	public static final String EdgeRHA_ErrorAddingRemoteInstallHosts			= String.valueOf( EdgeRHA_Base + 36 );
	public static final String EdgeRHA_ErrorUpdatingRemoteInstallHost			= String.valueOf( EdgeRHA_Base + 37 );
	public static final String EdgeRHA_ErrorRemovingRemoteInstallHosts			= String.valueOf( EdgeRHA_Base + 38 );
	public static final String EdgeRHA_ErrorGettingRemoteInstallSettings		= String.valueOf( EdgeRHA_Base + 39 );
	public static final String EdgeRHA_ErrorSavingRemoteInstallSettings			= String.valueOf( EdgeRHA_Base + 40 );
	public static final String EdgeRHA_ErrorDoingRemoteInstallAction			= String.valueOf( EdgeRHA_Base + 41 );
	public static final String EdgeRHA_ErrorGettingRemoteInstallLogs			= String.valueOf( EdgeRHA_Base + 42 );
	
	public static final String EdgeRHA_ErrorConnectingControlService			= String.valueOf( EdgeRHA_Base + 43 );
	public static final String EdgeRHA_WrongCredentialForControlService			= String.valueOf( EdgeRHA_Base + 44 );
	public static final String EdgeRHA_ErrorGettingCSVersion					= String.valueOf( EdgeRHA_Base + 45 );
	public static final String EdgeRHA_FailToSetAdditionalCommnand			    = String.valueOf( EdgeRHA_Base + 46 );
	public static final String EdgeRHA_FailedToGetLicenseData		    		= String.valueOf( EdgeRHA_Base + 47 );
	
	
	public static final String EdgeRHA_WebServiceReadDataTimeout		    	= String.valueOf( EdgeRHA_Base + 48 );
	public static final String EdgeRHA_FailedToStartVM		    				= String.valueOf( EdgeRHA_Base + 49 );
	public static final String EdgeRHA_FailedToStopVM		    				= String.valueOf( EdgeRHA_Base + 50 );
	public static final String EdgeRHA_FailedToRecoverActiveServer		    	= String.valueOf( EdgeRHA_Base + 51 );
	public static final String EdgeRHA_FailedToRequestDifferentReport		    = String.valueOf( EdgeRHA_Base + 52 );
	public static final String EdgeRHA_FailedToSetRewindBook		    		= String.valueOf( EdgeRHA_Base + 53 );
	public static final String EdgeRHA_FailedToStartSync		    			= String.valueOf( EdgeRHA_Base + 54 );
	public static final String EdgeRHA_FailedToUpdateP2VRootDir		    		= String.valueOf( EdgeRHA_Base + 55 );
	public static final String EdgeRHA_FailedToRegisterCsKey	    			= String.valueOf( EdgeRHA_Base + 56 );
	public static final String EdgeRHA_FailedToGetLicenseKey	    			= String.valueOf( EdgeRHA_Base + 57 );
	public static final String EdgeRHA_FailedToStartStopIsAliveCheck	    	= String.valueOf( EdgeRHA_Base + 58 );
	public static final String EdgeRHA_FailedToStartSupspendReplication	    	= String.valueOf( EdgeRHA_Base + 59 );
	public static final String EdgeRHA_FailedToGetCriticalEvents    			= String.valueOf( EdgeRHA_Base + 60 );
	public static final String EdgeRHA_FailedToGetEvents    					= String.valueOf( EdgeRHA_Base + 61 );
	public static final String EdgeRHA_FailedToAddUserCredentials    			= String.valueOf( EdgeRHA_Base + 62 );
	public static final String EdgeRHA_FailedToVerifyScenarioState    			= String.valueOf( EdgeRHA_Base + 63 );
	public static final String EdgeRHA_FailedToValidateScenario  				= String.valueOf( EdgeRHA_Base + 64 );
	
	public static final String EdgeRHA_FailedToGetHostSummaryStatistics  		= String.valueOf( EdgeRHA_Base + 65 );
	
	
	public static final String	EdgeRHA_FailedToStopScenario						= String.valueOf(EdgeRHA_Base + 66);
	public static final String	EdgeRHA_FailedToGetScenarioProperties				= String.valueOf(EdgeRHA_Base + 67);
	public static final String	EdgeRHA_FailedToSetScenarioProperties				= String.valueOf(EdgeRHA_Base + 68);
	public static final String	EdgeRHA_FailedToGetScenarioHostStatistics			= String.valueOf(EdgeRHA_Base + 69);
	public static final String	EdgeRHA_FailedToCreateScenario						= String.valueOf(EdgeRHA_Base + 70);
	public static final String	EdgeRHA_FailedToCreateScenarioGroup					= String.valueOf(EdgeRHA_Base + 71);
	public static final String	EdgeRHA_FailedToRemoveScenario						= String.valueOf(EdgeRHA_Base + 72);
	public static final String	EdgeRHA_FailedToRemoveScenarioGroup					= String.valueOf(EdgeRHA_Base + 73);
	public static final String	EdgeRHA_FailedToGetWindowsServiceList				= String.valueOf(EdgeRHA_Base + 74);
	public static final String	EdgeRHA_FailedToRunScenario							= String.valueOf(EdgeRHA_Base + 75);
	public static final String	EdgeRHA_FailedToCheckVMStatus						= String.valueOf(EdgeRHA_Base + 76);
	public static final String	EdgeRHA_FailedToCheckScenarioStatus					= String.valueOf(EdgeRHA_Base + 77);
	public static final String	EdgeRHA_FailedToRemoveVMResources					= String.valueOf(EdgeRHA_Base + 78);
	public static final String	EdgeRHA_FailedToCreateRestoreScenario				= String.valueOf(EdgeRHA_Base + 79);
	public static final String	EdgeRHA_FailedToGetP2VDestinationInfo				= String.valueOf(EdgeRHA_Base + 80);
	public static final String	EdgeRHA_FailedToSwitchover							= String.valueOf(EdgeRHA_Base + 81);
	public static final String	EdgeRHA_FailedToStartAR								= String.valueOf(EdgeRHA_Base + 82);
	public static final String	EdgeRHA_FailedToStopAR								= String.valueOf(EdgeRHA_Base + 83);
	public static final String	EdgeRHA_FailedToResumeReplication					= String.valueOf(EdgeRHA_Base + 84);
	public static final String	EdgeRHA_FailedToCheckAuth							= String.valueOf(EdgeRHA_Base + 85);
	public static final String	EdgeRHA_FailedToCleanupP2VEnv						= String.valueOf(EdgeRHA_Base + 86);
	public static final String	EdgeRHA_FailedToGetBasicInfo						= String.valueOf(EdgeRHA_Base + 87);
	public static final String	EdgeRHA_FailedToSaveScenarioGroupInfo				= String.valueOf(EdgeRHA_Base + 88);
	public static final String	EdgeRHA_FailedRetrieveSwitchoverMasterDisconnected	= String.valueOf(EdgeRHA_Base + 89);
	
	public static final String	EdgeRHA_FailedToGetReportData						= String.valueOf(EdgeRHA_Base + 90);
	public static final String	EdgeRHA_ErrorCSNotSupported							= String.valueOf(EdgeRHA_Base + 91);
	public static final String	EdgeRHA_FailedUpdateProperites						= String.valueOf(EdgeRHA_Base + 92);
	// MSP customer support
	public static final long MSP_Customer_Base = 0x0000001000000000L;
	public static final String MSP_Customer_NotExists = String.valueOf(MSP_Customer_Base + 1);
	public static final String MSP_CannotConnectMSP = String.valueOf(MSP_Customer_Base + 2);
	public static final String MSP_NotRemoteMSP = String.valueOf(MSP_Customer_Base + 3);
	public static final String RemoteConsole_Version_Not_Match = String.valueOf(MSP_Customer_Base + 4);
	
	public static final long PolicyValidation_Base = 0x0000002000000000L;
	public static final String PolicyValidation_NoTasks								= String.valueOf( PolicyValidation_Base + 1 );
	public static final String PolicyValidation_WrongFirstTask						= String.valueOf( PolicyValidation_Base + 2 );
	public static final String PolicyValidation_WrongSubsequentTaskForMSPServerRep	= String.valueOf( PolicyValidation_Base + 3 );
	public static final String PolicyValidation_WrongSubsequentTaskForRemoteConvRHA	= String.valueOf( PolicyValidation_Base + 4 );
	public static final String PolicyValidation_BackupConfigIsNull					= String.valueOf( PolicyValidation_Base + 5 );
	public static final String PolicyValidation_InvalidBackupDest					= String.valueOf( PolicyValidation_Base + 6 );
	public static final String PolicyValidation_NoRpsDestSettings					= String.valueOf( PolicyValidation_Base + 7 );
	public static final String PolicyValidation_InvalidRpsInfo						= String.valueOf( PolicyValidation_Base + 8 );
	public static final String PolicyValidation_NoRpsSettingsForBackup				= String.valueOf( PolicyValidation_Base + 9 );
	public static final String PolicyValidation_NoSessionPassword					= String.valueOf( PolicyValidation_Base + 10 );
	public static final String PolicyValidation_BackupDestIsNotRps					= String.valueOf( PolicyValidation_Base + 11 );
	public static final String PolicyValidation_InvalidVSphereProxyInfo				= String.valueOf( PolicyValidation_Base + 12 );
	public static final String PolicyValidation_LinuxBackupConfigIsNull				= String.valueOf( PolicyValidation_Base + 13 );
	public static final String PolicyValidation_ArchiveConfigIsNull					= String.valueOf( PolicyValidation_Base + 14 );
	public static final String PolicyValidation_InvalidArchiveSources				= String.valueOf( PolicyValidation_Base + 15 );
	public static final String PolicyValidation_InvalidArchiveDest					= String.valueOf( PolicyValidation_Base + 16 );
	public static final String PolicyValidation_FSCatalogGenerationNotEnabled		= String.valueOf( PolicyValidation_Base + 17 );
	public static final String PolicyValidation_ExportConfigIsNull					= String.valueOf( PolicyValidation_Base + 18 );
	public static final String PolicyValidation_InvalidExportDest					= String.valueOf( PolicyValidation_Base + 19 );
	public static final String PolicyValidation_ExportDestIsSameWithBackupDest		= String.valueOf( PolicyValidation_Base + 20 );
	public static final String PolicyValidation_ConversionConfigIsNull				= String.valueOf( PolicyValidation_Base + 21 );
	public static final String PolicyValidation_ConversionJobScriptIsNull			= String.valueOf( PolicyValidation_Base + 22 );
	public static final String PolicyValidation_ReplicationJobScriptIsNull			= String.valueOf( PolicyValidation_Base + 23 );
	public static final String PolicyValidation_InvalidReplicationDestination		= String.valueOf( PolicyValidation_Base + 24 );
	public static final String PolicyValidation_HeartbeatJobScriptIsNull			= String.valueOf( PolicyValidation_Base + 25 );
	public static final String PolicyValidation_InvalidHeartbeatMonitorInfo			= String.valueOf( PolicyValidation_Base + 26 );
	public static final String PolicyValidation_MSPServerReplicationSettingsIsNull	= String.valueOf( PolicyValidation_Base + 27 );
	public static final String PolicyValidation_IncorrectRpsPolicyCount				= String.valueOf( PolicyValidation_Base + 28 );
	public static final String PolicyValidation_InvalidReplicationSettings			= String.valueOf( PolicyValidation_Base + 29 );
	public static final String PolicyValidation_RpsPolicyIsNull						= String.valueOf( PolicyValidation_Base + 30 );
	public static final String PolicyValidation_RpsSettingsIsNull					= String.valueOf( PolicyValidation_Base + 31 );
	public static final String PolicyValidation_DataStoreSettingsIsNull				= String.valueOf( PolicyValidation_Base + 32 );
	public static final String PolicyValidation_InvalidDataStoreInfo				= String.valueOf( PolicyValidation_Base + 33 );
	public static final String PolicyValidation_RpsIsUsed							= String.valueOf( PolicyValidation_Base + 34 );
	public static final String PolicyValidation_NoRpsSettingsForMSPServerRep		= String.valueOf( PolicyValidation_Base + 35 );
	public static final String PolicyValidation_NoMspRpsDestSettings				= String.valueOf( PolicyValidation_Base + 36 );
	public static final String PolicyValidation_InvalidMspHost						= String.valueOf( PolicyValidation_Base + 37 );
	public static final String PolicyValidation_InvalidMspPlan						= String.valueOf( PolicyValidation_Base + 38 );
	public static final String PolicyValidation_PlanInfoDiffThanMspPlanSettings		= String.valueOf( PolicyValidation_Base + 39 );

	//ASBU
	public static final long ASBU_BASE = 0x0000004000000000L;
	public static final String ASBU_MemberServerCannotUse =  String.valueOf(ASBU_BASE + 1);
	public static final String ASBU_PortOrHostNameError =  String.valueOf(ASBU_BASE + 2);
	public static final String ASBU_Version_Not_Match =  String.valueOf(ASBU_BASE + 3);
	
	// Console gateway
	public static final long GATEWAY_BASE = 0x0000008000000000L;
	public static final String GATEWAY_AlreadyExist						= String.valueOf( GATEWAY_BASE + 1 );
	public static final String GATEWAY_NotFound							= String.valueOf( GATEWAY_BASE + 2 );
	public static final String GATEWAY_InvalidSiteId					= String.valueOf( GATEWAY_BASE + 3 );
	public static final String GATEWAY_InvalidSiteInfo					= String.valueOf( GATEWAY_BASE + 4 );
	public static final String GATEWAY_SiteNameIsInUse					= String.valueOf( GATEWAY_BASE + 5 );
	public static final String GATEWAY_SiteIsInUse						= String.valueOf( GATEWAY_BASE + 6 );
	public static final String GATEWAY_InvalidRegInfo					= String.valueOf( GATEWAY_BASE + 7 );
	public static final String GATEWAY_InvalidLoginInfo					= String.valueOf( GATEWAY_BASE + 8 );
	public static final String GATEWAY_GatewayHostLoginFailed			= String.valueOf( GATEWAY_BASE + 9 );
	public static final String GATEWAY_GatewayNotFound					= String.valueOf( GATEWAY_BASE + 10 );
	public static final String GATEWAY_GatewayHostNotRegistered			= String.valueOf( GATEWAY_BASE + 11 );
	public static final String GATEWAY_GatewayRegisteredToAnotherHost	= String.valueOf( GATEWAY_BASE + 12 );
	public static final String GATEWAY_SiteNotFound						= String.valueOf( GATEWAY_BASE + 13 );
	public static final String GATEWAY_ErrorEnsuringLocalSite			= String.valueOf( GATEWAY_BASE + 14 );
	public static final String GATEWAY_CannotRegisterToLocalGateway		= String.valueOf( GATEWAY_BASE + 15 );
	public static final String GATEWAY_InvalidUnregInfo					= String.valueOf( GATEWAY_BASE + 16 );
	public static final String GATEWAY_InvalidHeartbeatParam			= String.valueOf( GATEWAY_BASE + 17 );
	public static final String GATEWAY_GatewayHostVersionLow			= String.valueOf( GATEWAY_BASE + 18 );
	public static final String GATEWAY_CannotRegisterWhenUpgrading		= String.valueOf( GATEWAY_BASE + 19 );
	public static final String GATEWAY_ErrorLaunchingAutoUpdateExe		= String.valueOf( GATEWAY_BASE + 20 );
	
	//WSO2
	public static final long WSO2_BASE = 0x0000010000000000L;
	public static final String WSO2_CreateStubError = String.valueOf(WSO2_BASE + 1);
	public static final String WSO2_RemoteException = String.valueOf(WSO2_BASE + 2);
	public static final String WSO2_LoginAuthenticationException = String.valueOf(WSO2_BASE + 3);
	public static final String WSO2_LogoutAuthenticationException = String.valueOf(WSO2_BASE + 4);
	public static final String WSO2_UserAdminException = String.valueOf(WSO2_BASE + 5);
	public static final String WSO2_GetUserPermissionsFail = String.valueOf(WSO2_BASE + 6);
	
	//RBAC
	public static final long RBAC_BASE = 0x0000100000000000L;
	public static final String RBAC_CreateServiceFail = String.valueOf(RBAC_BASE + 1);
	
	//Appliance Factory Reset
	public static final long FactoryReset_BASE = 0x0000110000000000L;
	public static final String FactoryReset_Failed = String.valueOf(FactoryReset_BASE + 1);
	
	// Remote deployment
	public static final long REMOTEDEPLOY_BASE = 0x0000080000000000L;
	public static final String REMOTEDEPLOY_FailedToStartDeployProcess	= String.valueOf( REMOTEDEPLOY_BASE + 1 );
	public static final String REMOTEDEPLOY_FailedToGetDeployStatus		= String.valueOf( REMOTEDEPLOY_BASE + 2 );
	
	//Instant VM
	public static final long INSTANTVM_BASE = 0x0000090000000000L;
	public static final String INSTANTVM_RPSAGENT_DONT_EXIST_CURRENT_CONSLE	= String.valueOf( INSTANTVM_BASE + 1 );
	public static final String INSTANTVM_HYPERVAGENT_DONT_EXIST_CURRENT_CONSLE = String.valueOf( INSTANTVM_BASE + 2 );
	public static final String INSTANTVM_WINDOWS_AGENT_MANAGED_OTHERS = String.valueOf( INSTANTVM_BASE + 3 );
	public static final String INSTANTVM_D2D_DONT_INSTALL= String.valueOf( INSTANTVM_BASE + 4 );
	public static final String INSTANTVM_AGENT_UPPER_SIX_VERSION= String.valueOf( INSTANTVM_BASE + 5 );
	public static final String INSTANTVM_REMOTE_NODE_NULL= String.valueOf( INSTANTVM_BASE + 6 );
	public static final String INSTANTVM_UPDATE_NODE_ERROR= String.valueOf( INSTANTVM_BASE + 7 );
	public static final String INSTANTVM_WINDOWS_2008_R2= String.valueOf( INSTANTVM_BASE + 8 );
	public static final String INSTANTVM_NFS_NOT_INSTALL= String.valueOf( INSTANTVM_BASE + 9 );
	public static final String INSTANTVM_WINDOWS_AGENT_NOT_REGISTER= String.valueOf( INSTANTVM_BASE + 10 );
	
	public static final String INSTANTVM_HYPERV_CREDENTIAL = String.valueOf( INSTANTVM_BASE + 11 );
	public static final String INSTANTVM_HYPERV_SERVICE = String.valueOf( INSTANTVM_BASE + 12 );
	public static final String INSTANTVM_HYPERV_FIREWALL = String.valueOf( INSTANTVM_BASE + 13 );
	public static final String INSTANTVM_HYPERV_OTHER_ERROR = String.valueOf( INSTANTVM_BASE + 14 );
	
	//Method NotSupport
	public static final long NOTSUPPORT_BASE = 0x00000A0000000000L;
	public static final String METHODNOTSUPPORT_D2DAGENT = String.valueOf( NOTSUPPORT_BASE + 1 );
	public static final String METHODNOTSUPPORT_LINUXD2DAGENT = String.valueOf( NOTSUPPORT_BASE + 2 );
	public static final String METHODNOTSUPPORT_RPS = String.valueOf( NOTSUPPORT_BASE + 3 );
	public static final String METHODNOTSUPPORT_ASBU = String.valueOf( NOTSUPPORT_BASE + 4 );
	
}