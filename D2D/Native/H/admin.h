#ifndef _ADMIN_SVC_
#define _ADMIN_SVC_

#include "brandname.h"

#ifdef __cplusplus
extern "C" {
#endif

#ifdef _WINDOWS
#include <bebenabled.h>
#endif

// Add "Communication Foundation Service" to Server Admin
#define SA_COMMUNICATION_FOUNDATION

/*These macros are used by caller to start \ stop \ query services*/
typedef enum
{
	AS6RPC_QUEUE_INDEX = 0       /*CASJobEngine*/
	,AS6RPC_TAPESVR_INDEX        /*CASTapeEngine*/
	,AS6RPC_DBSERVER_INDEX       /*CASDBEngine*/
	,AS6RPC_LOGGER_INDEX         /**/
	,AS6RPC_DISCOVER_INDEX       /*CASUnivDomainSvr*/
	,AS6RPC_WINDISCOVERY_INDEX   /*CASDiscovery*/
	,AS6RPC_MGMTSVC_INDEX        /*CASMgmtSvc*/
	,AS6RPC_MSGENG_INDEX         /*CASMessageEngine*/
	,AS6RPC_ARCSERVE_INDEX       /*CASSvcControlSvr*/
#ifdef SA_COMMUNICATION_FOUNDATION
	,AS6RPC_COMMUNICATIONFOUNDATION_INDEX /*CA ARCserve Communication Foundation*/
#endif
	,AS6RPC_RPCSVC_INDEX         /*CASportmapper*/
}	SERVICES_INDEX;

/*These macros are used by services to register start time*/
#define JOB_SERVER			0
#define TAPE_SERVER			1
#define DATABASE_SERVER		2
#define MESSAGE_SERVER		3
#define DISCOVERY_SERVER	4	
#define SANAGENT_SERVER		5
#define QUEUE_SERVER		6
#ifdef BACKEND_CHANGES_FOR_SERVICE_ADMIN
#define UNI_DISCOVERY_SERVER 7
#define WIN_DISCOVERY_SERVER 8
#define SVC_CONTROLLER 9
#define MGMT_SERVER 10
#define RPC_SERVER 11
#endif /*BACKEND_CHANGES_FOR_SERVICE_ADMIN*/

//Service name
#define JOB_ENGINE			_T("CASJobEngine")
#define TAPE_ENGINE			_T("CASTapeEngine")
#define DATABASE_ENGINE		_T("CASDBEngine")
#define MESSAGE_ENGINE		_T("CASMessageEngine")
#define DISCOVERY_SERVICE	_T("CASDiscovery")
#define ALERT_SERVICE		_T("Cheyenne Alert Notification Server")
#define SANAGENT_SERVICE	_T("CA SAN Agent")
#define QUEUE_SERVICE		_T("CASUnivQueueServer")
//Servcie display name
#ifdef _ASO
#define SZ_DISCOVERYSVC_DISPLAYNAPE		_T("ASO Discovery Service")				// displayed name of the Discovery Service
#define SZ_JOBENGSVC_DISPLAYNAPE		_T("ASO Job Engine")
#define SZ_TAPEENGSVC_DISPLAYNAPE		_T("ASO Tape Engine")
#define SZ_DATABASEENGSVC_DISPLAYNAPE	_T("ASO Database Engine")
#define SZ_MESSAGEENGSVC_DISPLAYNAPE	_T("ASO Message Engine")
#define SZ_DOMAINSVC_DISPLAYNAPE		_T("CA BrightStor Domain Server")
#define SZ_SVCCONTROLSVC_DISPLAYNAPE	_T("CA BrightStor Service Controller")
#define SZ_CARPCSVC_DISPLAYNAPE			_T("CA Remote Procedure Call Server")
#define SZ_ALERTSVC_DISPLAYNAPE			_T("Cheyenne Alert Notification Server")
#define SZ_QSERVERSVC_DISPLAYNAPE		_T("ASO Universal Queue Server")

#else //regular ARCserve
#define SZ_DISCOVERYSVC_DISPLAYNAPE		_T("CA ") CST_BRAND_NAME_T _T(" Discovery Service")		// displayed name of the Discovery Service
#define SZ_JOBENGSVC_DISPLAYNAPE		_T("CA ") CST_BRAND_NAME_T _T(" Job Engine")
#define SZ_TAPEENGSVC_DISPLAYNAPE		_T("CA ") CST_BRAND_NAME_T _T(" Tape Engine")
#define SZ_DATABASEENGSVC_DISPLAYNAPE	_T("CA ") CST_BRAND_NAME_T _T(" Database Engine")
#define SZ_MESSAGEENGSVC_DISPLAYNAPE	_T("CA ") CST_BRAND_NAME_T _T(" Message Engine")
#define SZ_DOMAINSVC_DISPLAYNAPE		_T("CA ") CST_BRAND_NAME_T _T(" Domain Server")
#define SZ_SVCCONTROLSVC_DISPLAYNAPE	_T("CA ") CST_BRAND_NAME_T _T(" Service Controller")
#define SZ_CARPCSVC_DISPLAYNAPE			_T("CA Remote Procedure Call Server")
#define SZ_ALERTSVC_DISPLAYNAPE			_T("Cheyenne Alert Notification Server")
#define SZ_QSERVERSVC_DISPLAYNAPE		_T("ARCserveIT Universal Queue Server")
#define SZ_MGMTSVC_DISPLAYNAPE			_T("CA ") CST_BRAND_NAME_T _T(" Management Service")

#ifdef SA_COMMUNICATION_FOUNDATION
#define SZ_COMMUNICATIONFOUNDATION_DISPLAYNAPE _T("CA ") CST_BRAND_NAME_T _T(" Communication Foundation")
#endif

#endif
#define SZ_SANAGENTSVC_DISPLAYNAPE		_T("CA SAN Topology Service")

//Service module
#define DISCOVERYSVC_FILENAME			_T("CASDSCSVC.EXE")
#define JOBENGSVC_FILENAME				_T("JOBENG.EXE")
#define TAPEENGSVC_FILENAME				_T("TAPEENG.EXE")
#define DATABASEENGSVC_FILENAME			_T("DBENG.EXE")
#define MESSAGEENGSVC_FILENAME			_T("MSGENG.EXE")
#define ALERTSVC_FILENAME				_T("ALERT.EXE")
#define SANAGENTSVC_FILENAME			_T("SANAGNT.EXE")
#define QSERVERSVC_FILENAME				_T("QSERVER.EXE")

#ifdef BACKEND_CHANGES_FOR_SERVICE_ADMIN
struct _ADMIN_SVC_INFO
{
	DWORD status;
	ULONG uptime;
};
typedef struct _ADMIN_SVC_INFO ADMIN_SVC_INFO;
#endif /*BACKEND_CHANGES_FOR_SERVICE_ADMIN*/

/* DWORD QueryARCserveServerStatus()
 *
 *  machine_name    pass NULL if local machine
 *  server_index    JOB_SERVER, TAPE_SERVER or DATABASE_SERVER
 *  status          on return has the WIN32 defined server status:
 *
 *                             SERVICE_STOPPED
 *                             SERVICE_START_PENDING
 *                             SERVICE_STOP_PENDING
 *                             SERVICE_RUNNING
 *                             SERVICE_CONTINUE_PENDING
 *                             SERVICE_PAUSE_PENDING
 *                             SERVICE_PAUSED
 *  return DWORD    WIN32 error code
 */

DWORD QueryARCserveServerStatusCUI(char * machine_name,
								DWORD nComputerType,
								DWORD nUserHandle,
                                DWORD server_index,
                                DWORD * status);

// Oripin: UNICODE_JIS Support yelsu01

/**
* @brief            
* @retval   DWORD: WIN32 error code
* @param	char * machine_name[IN]:	machine name in unicode. pass NULL if local machine
* @param	DWORD nComputerType:	
* @param	DWORD userHandle:	
* @param	DWORD server_index:	JOB_SERVER, TAPE_SERVER or DATABASE_SERVER
* @param	DWORD * status[OUT]:	on return has the WIN32 defined server status:
*
*                             SERVICE_STOPPED
*                             SERVICE_START_PENDING
*                             SERVICE_STOP_PENDING
*                             SERVICE_RUNNING
*                             SERVICE_CONTINUE_PENDING
*                             SERVICE_PAUSE_PENDING
*                             SERVICE_PAUSED
* @todo	
* @exception		
*/
DWORD QueryARCserveServerStatusCUIW(
								wchar_t * machine_nameW, 
								DWORD nComputerType, 
								DWORD userHandle, 
								DWORD server_index, 
								DWORD * status);
DWORD QueryARCserveServerStatus(char * machine_name,
                                DWORD server_index,
                                DWORD * status);

DWORD QueryARCserveServerStatusW(
								WCHAR * machine_nameW, 
								DWORD server_index, 
								DWORD * status);
#ifdef BACKEND_CHANGES_FOR_SERVICE_ADMIN
DWORD QueryARCserveServerStatusCUIExW(WCHAR * machine_name,
									 DWORD nComputerType,
									 DWORD userHandle,
									 DWORD server_index,
									 ADMIN_SVC_INFO * status);
DWORD QueryARCserveServerStatusCUIEx(char * machine_name,
									 DWORD nComputerType,
									 DWORD userHandle,
									 DWORD server_index,
									 ADMIN_SVC_INFO * status);
#endif /*BACKEND_CHANGES_FOR_SERVICE_ADMIN*/


/* DWORD StartARCserveServer()
 *
 *  machine_name    pass NULL if local machine
 *  server_index    JOB_SERVER, TAPE_SERVER or DATABASE_SERVER
 *  return DWORD    WIN32 error code
 */

DWORD StartARCserveServerCUI(char * machine_name,
						  DWORD nComputerType,
						  DWORD nUserHandle,
                          DWORD service_index);
// Oripin: UNICODE_JIS Support dayra01
DWORD StartARCserveServerCUIW(wchar_t * machine_name,
							 DWORD nComputerType,
							 DWORD nUserHandle,
							 DWORD service_index);

DWORD StartARCserveServer(char * machine_name,
                          DWORD service_index);



/* DWORD ContinueARCserveServer()
 *
 *  machine_name    pass NULL if local machine
 *  server_index    JOB_SERVER, TAPE_SERVER or DATABASE_SERVER
 *  return DWORD    WIN32 error code
 */

DWORD ContinueARCserveServerCUI(char * machine_name,
							 DWORD nComputerType,
							 DWORD nUserHandle,
                             DWORD server_index);

DWORD ContinueARCserveServerCUIW(wchar_t * machine_name,
								DWORD nComputerType,
								DWORD nUserHandle,
								DWORD server_index);
DWORD ContinueARCserveServer(char * machine_name,
                             DWORD server_index);



/* DWORD PauseARCserveServer()
 *
 *  machine_name    pass NULL if local machine
 *  server_index    JOB_SERVER, TAPE_SERVER or DATABASE_SERVER
 *  return DWORD    WIN32 error code
 */

DWORD PauseARCserveServerCUI(char * machine_name,
						  DWORD nComputerType,
						  DWORD nUserHandle,
                          DWORD server_index);
// Oripin: UNICODE_JIS Support dayra01
DWORD PauseARCserveServerCUIW(wchar_t * machine_name,
							 DWORD nComputerType,
							 DWORD nUserHandle,
							 DWORD server_index);

DWORD PauseARCserveServer(char * machine_name,
                          DWORD server_index);



/* DWORD StopARCserveServer()
 *
 *  machine_name    pass NULL if local machine
 *  server_index    JOB_SERVER, TAPE_SERVER or DATABASE_SERVER
 *  return DWORD    WIN32 error code
 */

DWORD StopARCserveServerCUI(char * machine_name,
						 DWORD nComputerType,
						 DWORD nUserHandle,
                         DWORD server_index);

// Oripin: UNICODE_JIS Support dayra01
DWORD StopARCserveServerCUIW(wchar_t * machine_name,
							DWORD nComputerType,
							DWORD nUserHandle,
							DWORD server_index);


DWORD StopARCserveServer(char * machine_name,
                         DWORD server_index);



/* DWORD RegisterARCserveStartTime()
 *
 *  server_index    JOB_SERVER, TAPE_SERVER or DATABASE_SERVER
 *  return DWORD    WIN32 error code
 */

DWORD RegisterARCserveStartTime(DWORD service_index);

#ifdef __cplusplus
}
#endif

#endif //_ADMIN_SVC_