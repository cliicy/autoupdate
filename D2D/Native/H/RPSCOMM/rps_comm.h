#ifndef _RPS_COMM_H_
#define _RPS_COMM_H_

#ifndef UNIX
#include <winsock2.h>
#include "..\brandname.h"
#endif
#define KB 1024
#define MB KB*1024
#define GB MB*1024




#pragma warning(disable: 4200)

struct cmd_header_bosh_get_t
{
#ifdef UNIX
	unsigned long long comm_id;
	unsigned long long data_len;
#else
	unsigned _int64 comm_id;
	unsigned _int64 data_len;
#endif
	char data[0]; //warning C4200: nonstandard extension used
};

#pragma  pack(push, 1)
#ifndef UNIX
typedef struct _cmd_socket_t
{
	unsigned long	rem_ip;
	unsigned short	rem_port; 
	unsigned long	local_ip;
	unsigned short  local_port;
	WSAPROTOCOL_INFOA protocol_info;
}cmd_socket_t;
#endif
#pragma pack(pop)

#define SESSION_ID_TAG "SessionId"

#define  IPC_PIPE_GUID              "{3521EF6F-0C8F-4169-A83A-1FBA4CFD0796}"
#define  IPC_IDENTIFY_GUID			"{9F5243F7-FAD9-4db2-9957-389D301208ED}"
#define  IPC_IDENTIFY_PREFIX		"Global\\"
#define  IPC_PIPE_PREFIX			"\\\\.\\pipe\\"
#define  IPC_NOTIFY_EVENT			"Global\\{1B3E0213-8B98-4fdd-A94B-A96086B833A1}"

#define  REG_SOFTWARE_KEY			 CST_REG_ROOT_A 
#define  REG_SOFTWARE_WEBSERVICE_KEY CST_REG_ROOT_A "\\WebService"
#define  REG_SOFTWARE_INSTALL_PATH	 CST_REG_ROOT_A "\\InstallPath"
#define  REG_KEY_SHARED_SOCKET      "IsSharedSocket"
#define  REG_KEY_ENABLED_SSL		"IsEnabledSSL"
#define  REG_KEY_RPS_APAHCE_PORT	"Port"
#define  REG_KEY_PORT_SHARING		"UsePortSharing"
#define  REG_KEY_INSTALL_PATH	    "Path"
#define  APACHE_LISTEN_DIRECTIVE	"Listen"
//Path


#endif //_RPS_COMM_H_
