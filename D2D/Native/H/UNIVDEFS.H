#include <time.h>
#ifdef   _NETWARE_NLM
#include <rpc/newnames.h>
#endif
#include <rpc/rpc.h>
#include <rpc/pmap_cln.h>

#ifdef   _NETWARE_NLM
#include <netinet/in.h>
#endif

#ifndef _UNIV_DEFS_H_
#define	_UNIV_DEFS_H_

#ifdef __cplusplus
extern "C" {
#endif

/*
  Definitions for Universal ARCserve macros 
*/
#define  UNIV_PATH_LEN           1024
#define  UNIV_FILENAME_LEN       256
#define  UNIV_QUEUENAME_LEN      514
#define  UNIV_USERNAME_LEN       514
#define  UNIV_JOBDESC_LEN        256
#define  UNIV_SERVERNAME_LEN     48


/* Definitions for RPC Client-connection collection */

typedef struct   {
   CLIENT      *clnt;               //RPC client structure
   u_long      nService;
   u_long      nVersion;
} ASRPC_ServConn;

typedef struct   {
   char              szServerName[UNIV_SERVERNAME_LEN];
   char              bIsSPXInstalledOnServer;
   char              bIsTCPInstalledOnServer;
   u_short           nConns;          //Total Service connections for each server
   ASRPC_ServConn    **pServConn;     //Service list 
}ASRPC_ServConnList;

typedef struct   {
   u_short             nServConns;        //# of Server connections
   ASRPC_ServConnList  **pServConnList;   //List of Server connections
}ASRPC_ServList;



/* Definitions for RPC services collection */
typedef struct   {
   u_long      nService;
   u_long      nVersion;
} ASRPC_ServiceInfo;

typedef struct   {
   u_short               nProtocol;
   SVCXPRT              *transp;       // RPC Transport Structure
   u_short               nServices;    // # of Services using the protocol
   ASRPC_ServiceInfo    **pSInfoList;   // list of services
}ASRPC_ServiceList;

typedef struct   {
   u_short               nProtocols;
   ASRPC_ServiceList    **pProtocolList;
} ASRPC_SProtocolList;

typedef void (ASRPC_CB) (struct svc_req *rqstp, register SVCXPRT *transp);


/* Definitions for RPC Client Callback connection list.Cross references above
 * connection list 
 */

typedef struct   {
   char              szServerName[UNIV_SERVERNAME_LEN];
   u_short           nClients;        
   u_long            *pulClientData;
   u_long            *pulServerData;
} ASRPC_CBClientDataList;

typedef struct   {
   u_short                   nFunctionType;
   u_short                   nConns;
   ASRPC_CBClientDataList    **pCBConnList;
} ASRPC_CBFunctionList;

typedef struct   {
   u_short                 nFunctions;
   ASRPC_CBFunctionList    **pFunctionList;
} ASRPC_CBConnectionList;


/*
  definitions for buffer allocations   
*/

#define BUFR_8            8
#define BUFR_16         16
#define BUFR_32         32
#define BUFR_64         64
#define BUFR_128       128
#define BUFR_256       256
#define BUFR_512       512
#define BUFR_1024     1024


/* Definitions for global RPC functions */

u_short  UniInitASRPCClient();
u_short  UniTerminateASRPCClient(void);

u_short  RegisterASQueueClientCallbackService();
u_short  RegisterASQueueService();
u_short  RegisterASDBService();
u_short  RegisterASService();
u_short  RegisterASTapeService();
u_short  RegisterAllRPCServices();

u_short  UniStartASRPCServer();
u_short  UniRunASRPCServices();
u_short  UniTerminateASRPCServices();
u_short  UniInitASRPCServices();

u_short  UniStartASRPCClientCallbackServer();

/* RPC Service side Utility API */

CLIENT   *connectToRPCService (ASRPC_ServList *gRPCConns,  char *pszServerName,
                         u_long nService,u_long nVersion);

u_short  freeRPCServiceConnection ( ASRPC_ServList  *gRPCConns,
                                      char *pszServerName, u_long nService ,
                                      u_long nVersion);

void     freeAllRPCServiceConnections (ASRPC_ServList  *gRPCConns);
void     freeAllRPCServiceConnectionsByType (ASRPC_ServList  *gRPCConns,
                                      u_long nService , u_long nVersion);

/* RPC Client Utility API */

CLIENT   *GetNextRPCClientConnection(ASRPC_ServList *gRPCConns,int   index,
               u_long   nService,u_long   nVersion);

CLIENT   *CreateRPCClientConnection (  char *pszServerName, u_long nService, 
                                       u_long nVersion);
CLIENT   *GetRPCClientConnection (  char *pszServerName, u_long nService,
                                       u_long nVersion);
u_short  DestroyRPCClientConnection ( char *pszServerName, u_long nService,
                                       u_long nVersion);
void     DestroyAllRPCClientConnections (void);

CLIENT *GetNextRPCConnection(  u_long nService,u_long   nVersion, int   index);
CLIENT   *CreateRPCClientConnectionByAddr (  char *pszServerName, u_long nService,u_long nVersion,SVCXPRT  *transp);

#define  GetRPCClientConnection CreateRPCClientConnection
CLIENT *CreateClntConnection(char *pszServerName, char *bIsTCPInstalledOnServer,
                             char   *bIsSPXInstalledOnServer, u_long nService,u_long nVersion);

/* RPC Services Utility API */

u_short  StartASService ( u_long nService, u_long nVersion, u_long nProtocol,
                           ASRPC_CB fnPtr);
u_short  StopASService ( u_long nService, u_long nVersion, u_long nProtocol);
void     StopAllASServices (void);
u_short  ASServerLogin (char  *pszServerName);
u_short  ASServerLogout (char  *pszServerName);
u_short  ASServerLogin (char  *pszServerName);
u_short  UniIsLocalHost(char	*pszServerName);

/* RPC error log API */
void  ASRPCLog (int  bScreenDump, const char *fmt,...);


/* UTC   Date & time conversion API */

u_long UniUTCToLocalTime(time_t localTimeT);
u_long UniLocalToUTCTime(time_t localTimeT);

/* RPC Client Callback utility API  */
u_short  addToRPCCBServiceList (ASRPC_CBConnectionList *gCBConns,  char *pszServerName,
                     u_short  nFunctionType, u_long   ulClientData, u_long ulServerData);

u_short  freeRPCCBServiceConnection ( ASRPC_CBConnectionList  *gCBConns,
                                      char *pszServerName, 
                                      u_short  nFunctionType, u_long   ulClientData,
                                      u_long ulServerData);

void  freeAllRPCCBServiceConnections (ASRPC_CBConnectionList  *gCBConns);

void  freeAllRPCCBServiceConnectionsByType (ASRPC_CBConnectionList  *gCBConns, 
                                                u_short nFunctionType);

u_short  GetNextRPCCBClientItem(ASRPC_CBConnectionList *gCBConns,u_short   zeroBasedIndex,
                                 u_short  nFunctionType,char   *szServerName,u_long *pulClientData,u_long *pulServerData );

u_short  GetRPCCBClientItemServerData(ASRPC_CBConnectionList *gCBConns,
                                 u_short  nFunctionType,
                                 char     *szServerName,u_long ulClientData,
                                 u_long   *pulServerData);

void     freeAllClientCBConnections ();


void	ConvertIPAddrToNameAndViceVersa(char *pszHostServer,char	*pszOutput );
//pszOutput should be at least 128 bytes

#ifdef __cplusplus
}
#endif

#endif /* !_UNIV_DEFS_H_ */
