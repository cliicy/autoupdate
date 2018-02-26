#pragma once

#include <Windows.h>
#include <tchar.h>
#include <malloc.h>

#include "AFJob.h"


//#define AF_JOBTYPE_RPS_REPLICATION				17	//<sonmi01>2011-9-14 ###???
//#define AF_JOBTYPE_RPS_REPLICATION_STRING       L"RPS Replication Job"

#define  REP_AFTER_BACKUP_JOB_END		(ULONG)(1 << 0)
#define  REP_AFTER_CATALOG_JOB_END		(ULONG)(1 << 1)
#define  REP_FLAG_MAKEUP_JOB            (ULONG)(1 << 2) // make up job
#define  REP_FLAG_GDD_D2D_FILES         (ULONG)(1 << 3) 

#define REP_DS_CMPRS_MAX        1       // the compress alg type
#define REP_DS_CMPRS_STANDARD   2


#define REP_DS_ATTR_FLAGS_GDD_ENABLED	(ULONG)(1 << 0)		//RPSDsAttr::Flags

struct RPSDsAttr
{
    ULONG   EnableEncrypt;
    ULONG   EncyptAlg;
    WCHAR*  DataPassword;
    ULONG   EnableCmprs;
    ULONG   CmprsAlg;
	ULONG	Flags;	//<sonmi01>2012-2-16 #block GDD to non-GDD replication
	WCHAR*	DSName;
	WCHAR*	DisplayName;
};

struct RPSRepProxy
{
    WCHAR*  SrvName;		// proxy server host name
    WCHAR*  UserName;		// credentials on proxy server
    WCHAR*  Password;		// credentials on proxy server
    ULONG   Index;			// !**** it should be removed ****!
    ULONG   Port;			// proxy listening port
    BOOL    IsSSL;			// true: SSL is enabled
    ULONG   CypherType;		// !**** it should be removed ****!
    BOOL    IsSharedSocket;	// !**** it should be removed ****!
};

struct RPSMigrationRPInfo
{
	WCHAR* sessionGuid;			// session guid
	WCHAR* sessionPwd;			// original session pwd
	WCHAR* targetSessionPwd;	//target session pwd
};

struct RPSRepJobScript
{
	ULONG Version;
	ULONG JobID;

	ULONG JobType; //constant AF_JOBTYPE_RPS_REPLICATION				17
	WCHAR * JobTypeString;
	WCHAR * JobName;

	WCHAR * D2DNodeName;
	WCHAR * D2DNodeGUID;
	WCHAR * PolicyGUID; 
	WCHAR * pRPSName;  //zouju01-12/1/2011

	WCHAR * RPSNodeID;    //local RPS ID
	WCHAR * RemoteRPSNodeSID; //Remote RPS ID

	WCHAR * DestinationRootPath; //local rps ...
	WCHAR * DestinationUserName; //local rps ...
	WCHAR * DestinationPassword; //local rps ...

	ULONG BeginSession;
	ULONG EndSession; //not used
	ULONG JobEndFlag; //bitwise fields to indicate rep occur after which job finished, backup job or catalog job, REP_AFTER_XXX_JOB_END
	ULONG MaxMackupRetryTimes; //max retry count for makeup jobs

	WCHAR * RemotePolicyGUID;  //remote rps ... used ?
	WCHAR * RemoteTransportMode; //<sonmi01>2011-10-20 ###??? //remote rps ...
	WCHAR * RemoteServer;  //remote rps ...
	ULONG	RemotePort; //remote rps ...

	WCHAR * RemoteDestinationRootPath;
	WCHAR * RemoteDestinationUserName; //not used ?
	WCHAR * RemoteDestinationPassword; //not used ?

	//<sonmi01>2011-10-20 ###???
	ULONG StreamNumber;
	ULONG BandWidthThrotting;

	//<sonmi01>2011-10-20 ###??? currently reserved for later use
	ULONG UseSSL;
	ULONG AccessType; 
	ULONG CypherType;
	ULONG ReconnTimeout;

    RPSDsAttr SrcDs;            // source data store
    RPSDsAttr DesDs;            // destination data store

    ULONG        ProxyCnt;      // the proxy count			!**** only one proxy server here ***!
    RPSRepProxy* ProxyList;     // the list of the proxy	!**** only one proxy server here ***!
    WCHAR * PolicyName;
	WCHAR * RemotePolicyName;

	WCHAR * PlanUUID;
	WCHAR * TargetPlanUUID; //<huvfe01>2013-11-1 plan UUID
	WCHAR * TargetPlanName;
	ULONG   TargetJobType;

	//<huvfe01>2013-11-29 support MSP authentication
	ULONG   IsMspUser; 
	WCHAR * UserName;
	WCHAR * Password;
	WCHAR * Domain;

	WCHAR * SourceDataStore;
	WCHAR * SourceDataStoreName;
	WCHAR * TargetDataStore;
	WCHAR * TargetDataStoreName;
	WCHAR * ExtendsInfo;
	GDDInformation gInfo;
	ULONG				 MigrationRPCount;			// the migration recovery point info
	RPSMigrationRPInfo * MigrationRPInfo;	// the list of migration recovery point info
	BOOL    IsMigration;	// if migration


	// only used by afcoreinterface to fee allocated memory, please make sure JNI allocating memory use malloc(...)
	VOID FreeMembers()
	{
#define RPSREPJS_FREE_MEM(x) {if(x) {free(x); x = NULL;}}

		RPSREPJS_FREE_MEM(JobTypeString);
		RPSREPJS_FREE_MEM(JobName);
		RPSREPJS_FREE_MEM(D2DNodeName);
		RPSREPJS_FREE_MEM(D2DNodeGUID);
		RPSREPJS_FREE_MEM(PolicyGUID);
        RPSREPJS_FREE_MEM(pRPSName);
		RPSREPJS_FREE_MEM(RPSNodeID);
		RPSREPJS_FREE_MEM(RemoteRPSNodeSID);
		RPSREPJS_FREE_MEM(DestinationRootPath);
		RPSREPJS_FREE_MEM(DestinationUserName);
		RPSREPJS_FREE_MEM(DestinationPassword);
		RPSREPJS_FREE_MEM(RemotePolicyGUID);
		RPSREPJS_FREE_MEM(RemoteTransportMode); //<sonmi01>2011-10-20 ###???
		RPSREPJS_FREE_MEM(RemoteServer);
		RPSREPJS_FREE_MEM(RemoteDestinationRootPath);
		RPSREPJS_FREE_MEM(RemoteDestinationUserName);
		RPSREPJS_FREE_MEM(RemoteDestinationPassword);
        RPSREPJS_FREE_MEM(SrcDs.DataPassword);
		RPSREPJS_FREE_MEM(SrcDs.DSName); //<sonmi01>2012-2-16 #block GDD to non-GDD replication
        RPSREPJS_FREE_MEM(DesDs.DataPassword);
		RPSREPJS_FREE_MEM(DesDs.DSName);
		RPSREPJS_FREE_MEM(SourceDataStore);
		RPSREPJS_FREE_MEM(SourceDataStoreName);
		RPSREPJS_FREE_MEM(TargetDataStore);
		RPSREPJS_FREE_MEM(TargetDataStoreName);
		RPSREPJS_FREE_MEM(PolicyName);
		RPSREPJS_FREE_MEM(RemotePolicyName);
		RPSREPJS_FREE_MEM(PlanUUID);
		RPSREPJS_FREE_MEM(TargetPlanUUID);
		RPSREPJS_FREE_MEM(TargetPlanName);
		RPSREPJS_FREE_MEM(UserName);
		RPSREPJS_FREE_MEM(Password);
		RPSREPJS_FREE_MEM(Domain);
        RPSREPJS_FREE_MEM(ProxyList);
		RPSREPJS_FREE_MEM(MigrationRPInfo);
		RPSREPJS_FREE_MEM(ExtendsInfo);
	}

    static void FreeProxyItem(RPSRepProxy& item)
    {
        RPSREPJS_FREE_MEM(item.SrvName);
        RPSREPJS_FREE_MEM(item.UserName);
        RPSREPJS_FREE_MEM(item.Password);
    }

    static void DeepCopy(const RPSRepProxy& src, RPSRepProxy& des)
    {
        des = src;
        des.SrvName     = _wcsdup(src.SrvName);
        des.UserName    = _wcsdup(src.UserName);
        des.Password    = _wcsdup(src.Password);
    }

	static void FreeMigrationRPInfoItem(RPSMigrationRPInfo& item)
	{
		RPSREPJS_FREE_MEM(item.sessionGuid);
		RPSREPJS_FREE_MEM(item.sessionPwd);
		RPSREPJS_FREE_MEM(item.targetSessionPwd);
	}


	static void DeepCopy(const RPSMigrationRPInfo& src, RPSMigrationRPInfo& des)
	{
		des.sessionGuid			= _wcsdup(src.sessionGuid);
		des.sessionPwd			= _wcsdup(src.sessionPwd);
		des.targetSessionPwd	= _wcsdup(src.targetSessionPwd);
	}

    static void DeepCopy(const RPSRepJobScript& src, RPSRepJobScript& des)
    {
        ZeroMemory(&des, sizeof(RPSRepJobScript));

        des.Version             = src.Version;
        des.JobID               = src.JobID;

        des.JobType             = src.JobType;
        des.JobTypeString       = _wcsdup(src.JobTypeString);
        des.JobName             = _wcsdup(src.JobName);

        des.D2DNodeName         = _wcsdup(src.D2DNodeName);
        des.D2DNodeGUID         = _wcsdup(src.D2DNodeGUID);
        des.PolicyGUID          = _wcsdup(src.PolicyGUID );
        des.pRPSName            = _wcsdup(src.pRPSName);

		des.RPSNodeID           = _wcsdup(src.RPSNodeID);
		des.RemoteRPSNodeSID    = _wcsdup(src.RemoteRPSNodeSID);

        des.DestinationRootPath = _wcsdup(src.DestinationRootPath);
        des.DestinationUserName = _wcsdup(src.DestinationUserName);
        des.DestinationPassword = _wcsdup(src.DestinationPassword);

        des.BeginSession        = src.BeginSession;
        des.EndSession          = src.EndSession;
        des.JobEndFlag          = src.JobEndFlag;
        des.MaxMackupRetryTimes = src.MaxMackupRetryTimes;

        des.RemotePolicyGUID    = _wcsdup(src.RemotePolicyGUID);
		des.RemotePolicyName    = _wcsdup(src.RemotePolicyName);
		des.PolicyName			= _wcsdup(src.PolicyName);
        des.RemoteTransportMode = _wcsdup(src.RemoteTransportMode);
        des.RemoteServer        = _wcsdup(src.RemoteServer);
        des.RemotePort          = src.RemotePort;

        des.RemoteDestinationRootPath = _wcsdup(src.RemoteDestinationRootPath);
        des.RemoteDestinationUserName = _wcsdup(src.RemoteDestinationUserName);
        des.RemoteDestinationPassword = _wcsdup(src.RemoteDestinationPassword);

        des.StreamNumber        = src.StreamNumber;
        des.BandWidthThrotting  = src.BandWidthThrotting;

        des.UseSSL              = src.UseSSL;
        des.AccessType          = src.AccessType; 
        des.CypherType          = src.CypherType;
        des.ReconnTimeout       = src.ReconnTimeout;

        des.SrcDs               = src.SrcDs;
        des.SrcDs.DataPassword  = _wcsdup(src.SrcDs.DataPassword);
		des.SrcDs.DSName		= _wcsdup(src.SrcDs.DSName); //<sonmi01>2012-2-16 #block GDD to non-GDD replication

        des.DesDs               = src.DesDs;
        des.DesDs.DataPassword  = _wcsdup(src.DesDs.DataPassword);
		des.DesDs.DSName		= _wcsdup(src.DesDs.DSName);

		des.PlanUUID            = _wcsdup(src.PlanUUID);
		des.TargetPlanUUID      = _wcsdup(src.TargetPlanUUID);
		des.TargetPlanName      = _wcsdup(src.TargetPlanName);
		des.TargetJobType		= src.TargetJobType;
		des.IsMspUser			= src.IsMspUser;

		des.UserName            = _wcsdup(src.UserName);
		des.Password            = _wcsdup(src.Password);
		des.Domain              = _wcsdup(src.Domain);

		des.SourceDataStore     = _wcsdup(src.SourceDataStore);
		des.SourceDataStoreName = _wcsdup(src.SourceDataStoreName);
		des.TargetDataStore     = _wcsdup(src.TargetDataStore);
		des.TargetDataStoreName = _wcsdup(src.TargetDataStoreName);

		des.gInfo.RawSize = src.gInfo.RawSize;
		des.gInfo.CompressedSize = src.gInfo.CompressedSize;
		des.gInfo.CompressRatio = src.gInfo.CompressRatio;
		des.gInfo.CompressPercentage = src.gInfo.CompressPercentage;

		des.ExtendsInfo  = _wcsdup(src.ExtendsInfo);

        des.ProxyCnt            = src.ProxyCnt;
        if (des.ProxyCnt != 0)
            des.ProxyList           = (RPSRepProxy*)malloc(des.ProxyCnt * sizeof(RPSRepProxy));
        else
            des.ProxyList           = NULL;
        for (ULONG i = 0; i<des.ProxyCnt; i++)
            DeepCopy(src.ProxyList[i], des.ProxyList[i]);

		des.MigrationRPCount	= src.MigrationRPCount;
		if(des.MigrationRPCount != 0)
			des.MigrationRPInfo		= (RPSMigrationRPInfo*)malloc(des.MigrationRPCount * sizeof(RPSMigrationRPInfo));
		else
			des.MigrationRPInfo		= NULL;

		for (ULONG i = 0; i < des.MigrationRPCount; i++)
			DeepCopy(src.MigrationRPInfo[i], des.MigrationRPInfo[i]);

		des.IsMigration = src.IsMigration;
    }

};
