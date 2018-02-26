#ifndef __BRSTRUCT_H
#define __BRSTRUCT_H
#include <windows.h>
#include <tchar.h>
#include "lic_def.h"
#include "istruct.h"

#include "brandname.h"

#ifdef __cplusplus
extern "C" {
#endif

#define MSG_SIZE				512

//
//	Discovery types:
//
//	Query Network for Cheyenne products
#define	DSCV_QUERY_NETWORK				0x00000001
//	Advertize own set of products
#define	DSCV_ADVERTIZE_OWNSET			0x00000002
//	Serialize responses
#define DSCV_SERIALIZE_RESPONSE			0x00000004
//	Respond to query
#define	DSCV_MAKE_RESPONSE				0x00000008

//	Query Network for Cheyenne products, Listen and Serialize responses		
#define DSCV_PASSIVE_SERIALIZE			(DSCV_QUERY_NETWORK | DSCV_SERIALIZE_RESPONSE)
//	Query Network, Advertize own set of products,
//	Listen and Serialize responses
#define DSCV_ACTIVE						(DSCV_QUERY_NETWORK | DSCV_SERIALIZE_RESPONSE |	DSCV_ADVERTIZE_OWNSET| DSCV_MAKE_RESPONSE	)

//	Listen for Query and Respond 	
#define DSCV_PASSIVE_NOSERIALIZE		(DSCV_MAKE_RESPONSE)

//
//	Response types:
//
//	Broadcast 
#define DSCV_RESP_BROADCAST				0x00010000
//	Point - to - Point
#define	DSCV_RESP_POINT					0x00020000

// Definition of ARCserveNT flags
#define  ARCSERVENT_SERVER              DSCV_ACTIVE

// Definition for the flags in message header
#define NW_HOST_CONVERSION				0x01
#define PHYSICAL_CLUSTER_NODE			0x02
#define VIRTUAL_CLUSTER_NODE			0x04
#define DOMAIN_PRIMARY_SERVER			0x08

//
//Registry keys
//
static TCHAR szASDiscovery[]    = CST_REG_ROOT_T _TEXT("\\Products"); //SECURITY_VULN [false positive]

static TCHAR szASConfigInfo[]	= CST_REG_ROOT_T _TEXT("\\ProductsConfigInfo"); //SECURITY_VULN [false positive]

//
//	Set this Event when service is shut down	
//

#define  szDscSvcShutDownEv		  _TEXT("CheyenneDscSvcShutDown")		
#define  szGlobalDscSvcShutDownEv _TEXT("Global\\CheyenneDscSvcShutDown")		

#define  szCfgUpdateWatcher		 _TEXT("CheyProdCfgUpdate")		
#define  szCfgGlobalUpdateWatcher	_TEXT("Global\\CheyProdCfgUpdate")

 	
#define U_BROADCAST				0
#define U_DELETE_MEMBER			1
#define U_ADD_MEMBER			2
#define U_DELETE_DOMAIN			3
#define U_MODIFY_DOMAIN			4
#define U_CHECK_DOMAIN			5
#define U_CREATE_DOMAIN			6
#define U_SERVERDOWN			7
#define U_SERVICEDOWN			8
#define U_SERVICEPAUSE			9
#define U_SERVICECONTINUE		10
#define U_SCANLOG_CONFIG		11
#define U_ACTIVITYLOG_CONFIG	12
#define U_SERVICE_CONFIG		13
#define U_REALTIME_CONFIG		14

#define U_BCAST_CONFIG		    15
#define U_BCAST_TABLESYNC		16
#define U_BCAST_ALERT		    17

//cheyenne broadcast command
#define U_BCAST_PRODUCT_NONE	0
#define U_BCAST_PRODUCT_INFO	100 //response to query
#define U_BCAST_QUERY_INFO		101	//query network	and serialize on server side
#define U_BCAST_PRODUCT_DOWN	102	//service is down
#define U_BCAST_GETINFO_CLIENT	103	//query network, no serialization on server side
#define U_BCAST_PRODCONFIG_INFO 104	//products config info 
	
#pragma	pack(1)
////////////////////////////////////////////////////////////
/////	For Arcserve current BroadCast info

typedef struct tagProductStruct
{
	DWORD dwProdId;			//4
	DWORD dwProdType;		//4
	DWORD dwProdVersion;	//4
	char  ProdSubType;		//1
	char  Reserve[3];		//3
	DWORD dwUserLevel;		//4
	char  SerialNumber[9];	//9
	DWORD dwLicenceCRC;		//4				
	char  AgentInfo[16];	//16				
}ProductStruct	;			//49

typedef struct tagProductStruct ASProduct;

typedef struct tagProductStructW
{
	DWORD dwProdId;			//4
	DWORD dwProdType;		//4
	DWORD dwProdVersion;	//4
	char  ProdSubType;		//1
	char  Reserve[3];		//3
	DWORD dwUserLevel;		//4
	WCHAR  SerialNumber[9];	//9
	DWORD dwLicenceCRC;		//4				
	WCHAR  AgentInfo[16];	//16				
}ProductStructW	;			//49
typedef struct tagProductStructW ASProductW;
//header structure for broadcasting
typedef struct tagBroadcastHeaderStruct
{
	char 		Command;			//1
	char		ServerName[MAX_COMPUTERNAME_LENGTH + 1];	//16	
	DWORD		BroadcastOwnerId;				//4
	ULONG		ulIP;										//4
	char		DomainName[MAX_COMPUTERNAME_LENGTH + 1];	//16
	USHORT 		usProductQuantity;							//2
	DWORD		CRC;										//4
	UCHAR		FLAGS;										//1
	char		aReserve[2];								//2
} ASBRDCAST_HEADER;									   //50

typedef struct tagBroadcastHeaderStruct ASBRDCAST_HEADER;

typedef struct tagCheyenneStruct
{
	ASBRDCAST_HEADER	BRDHEADER;						//97
	char				aProdInfo[MSG_SIZE-50];								//21
} CheyenneStruct;										//64

typedef struct tagProductConfig
{
	DWORD dwProdId;			//4
	DWORD dwSlotId;			//4
	DWORD dwSlotSize;		//4
}ProductConfig	;			//12

typedef struct tagProductConfig ProdConfigHeader;

//
//	Header's structure for configuration info
//
typedef  struct tagConfigRec
{
	unsigned long ulStreamId;
	unsigned long ulStreamSize;
} ConfigRec;
typedef struct tagConfigRec ConfigStreamHeader;
typedef ConfigStreamHeader* PConfigStreamHeader;
///////////////////////////////////////////////////////////////// 
///// For Normal BroadCast
typedef struct tagServerStruct
{
	char Command;
	char reserve[3];
	char DomainName[48];
	char MasterServerName[48];
	char ServerName[48];
	char  SerialNumber[8];
	DWORD OSVersion;
	DWORD InocuLANVersion;
	DWORD EngineVersion;
	DWORD EngineDate;
	DWORD SigntureVersion;
	DWORD SigntureDate;
	DWORD VolList;
	DWORD MachineStatus;
} ServerStruct;

///// For BroadCast Add Domain
typedef struct tagDomainStruct
{
	char Command;
	char reserve[3];
	char DomainName[48];
	char ReplySlotName[128];
	long Reply;
} DomainStruct;

///// For config activity log broadcast
typedef struct tagActivityLogBroadcastStruct
{
	char Command;
	char reserve[3];
	char DomainName[48];
	unsigned long MaxLog;
	unsigned long PurgeTime;
	unsigned long LogFilter;
} ActivityLogBroadcastStruct;

///// For config scan log broadcast
typedef struct tagScanLogBroadcastStruct
{
	char Command;
	char reserve[3];
	char DomainName[48];
	unsigned long MaxLog;
	unsigned long PurgeTime;
	unsigned long LogFilter;
} ScanLogBroadcastStruct;

///// For Service Config broadcast
typedef struct tagServiceConfigStruct
{
	char Command;
	char reserve[3];
	char DomainName[48];
	unsigned long ulHoldTime;
	unsigned long ulTimeOut;
	unsigned long ServiceType;
} ServiceConfigStruct;

///// For Service Pause,Continue,and Stop BroadCast
typedef struct tagServiceBroadcastStruct
{
	char Command;
	char reserve[3];
	char DomainName[48];
} ServiceBroadcastStruct;

///// For config realtime broadcast
typedef struct tagRealtimeBroadcastStruct
{
	char Command;
	char reserve[3];
	char DomainName[48];
	R_REQUEST RealtimeConfig;
} RealtimeBroadcastStruct;

typedef struct tagBCastBroadcastStruct
{
	char  Command;
	char  reserve[3];
	char  DomainName[48];

    DWORD dwTargets;
    DWORD dwProtocols;
    DWORD dwBcastFreq;
    DWORD dwCacheFreq;
    char  szReserve[20];
} BCastBroadcastStruct;

typedef struct tagTableSyncBroadcastStruct
{
	char  Command;
	char  reserve[3];
	char  DomainName[48];

    DWORD dwTargets;
    DWORD dwTableType;
	char  szSrcServer[48];
    char  szReserve[20];
} TableSyncBroadcastStruct;

typedef struct tagAlertStruct
{
	char  Command;
	char  reserve        [3];

    ULONG ulSrcAddr;                // Source IP Address
    DWORD dwPriority;               // Alert Priority
    DWORD dwFlags;                  // Alert Flags
    char  szMsg          [512-12-4];// Alert Message
} AlertStruct;

#pragma	pack()

#ifdef __cplusplus
}
#endif

#endif
