#pragma once
#include "ArcUpdate.h"
//
// *********************************************************
// definations of the pipe communication
// *********************************************************
//
#define  COMM_PIPE_NAME				 L"\\\\.\\pipe\\8A413EF2_152D_41D1_99C6_06BA669DEF0C"
#define  COMM_BUFFER_SIZE_IN         (1<<21)		// 2MB
#define  COMM_BUFFER_SIZE_OUT        (1<<21)		// 2MB

#define REQ_UPDATE_SESSION			1000						// for update related commands
#define REQ_UPDATE_TRIGGER_JOB		REQ_UPDATE_SESSION + 1		// trigger update job
#define REQ_UPDATE_QUERY_STAQTUS	REQ_UPDATE_SESSION + 2		// query update job status
#define REQ_UPDATE_CANCEL_JOB		REQ_UPDATE_SESSION + 3		// cancel update job

#define REQ_PLUGIN_MANAGER_SESSION	2000						// for plug in related commands
#define REQ_PLUGIN_INSTALL			REQ_PLUGIN_MANAGER_SESSION + 1		// request to install plug in
#define REQ_PLUGIN_REMOVE			REQ_PLUGIN_MANAGER_SESSION + 2		// request to remove plug in

#define REQ_QUERY_SESSION			3000						// for the commands to query sth
#define REQ_QUERY_NODE_INFO			REQ_QUERY_SESSION + 1		// query the node information
#define REQ_QUERY_INSTALLED_PATCHES	REQ_QUERY_SESSION + 2		// query the patches installed

#define REG_FILE_MGR_SESSION		4000		// for the commands to manage files
#define REQ_FILE_MGR_DELETE_FILE	REG_FILE_MGR_SESSION + 1	// command to delete file
#define REQ_FILE_MGR_DELETE_FOLDER	REG_FILE_MGR_SESSION + 2	// command to delete folder
#define REQ_FILE_MGR_CREATE_FOLDER	REG_FILE_MGR_SESSION + 3	// command to create folder
#define REQ_FILE_MGR_DOWNLOAD_FILE	REG_FILE_MGR_SESSION + 4	// command to download file
#define REQ_FILE_MGR_UPLOAD_FILE	REG_FILE_MGR_SESSION + 5	// command to upload file
#define REQ_FILE_MGR_QUERY_FILE_MD5	REG_FILE_MGR_SESSION + 6	// command to query md5 of file
#define REQ_FILE_MGR_FIND_FILES		REG_FILE_MGR_SESSION + 7	// command to find files
#define REQ_FILE_MGR_RUN_COMMAND	REG_FILE_MGR_SESSION + 8	// command to run a command line

#define COM_BLOCK_DATA_SIZE			65536
typedef struct _packet_req_
{
	char		magic[8];		// U.D.P
	DWORD		cmd;			// command code
	DWORD		chkSum;			// check sum of data
	DWORD		dataSize;		// the parameter's size
}packet_req, *ppacket_req;

typedef struct _packet_ack_
{
	DWORD		cmd;			// command code
	LONG		errCode;		// the error code
	DWORD		chkSum;			// check sum of data
	DWORD		dataSize;		// the parameter's size
}packet_ack, *ppacket_ack;

//
// the request to start a update job
//
typedef struct _req_param_start_job_
{
	DWORD		dwProdFor;
	DWORD		dwProdOn;
	BOOL		bSvrSpecified;
	BOOL		bHotfix;//added by cliicy.luo
	ARCUPDATE_SERVER_INFO svrInfo;
}req_param_start_job, *preq_param_start_job;

//
// the request to cancel a update job
//
typedef struct _req_param_cancel_job_
{
	DWORD		dwProduct;
}req_param_cancel_job, *preq_param_cancel_job;

//
// the request to query job status
//
typedef struct _req_param_query_job_status_
{
	DWORD		dwProduct;
}req_param_query_job_status, *preq_param_query_job_status;

//
// the request install plugin
//
typedef struct _req_param_install_plugin_
{
	DWORD		dwCommand;			// the command this plugin will handle
	WCHAR		szModuleName[32];   // the module name (dll) of this plugin
}req_param_install_plugin, *preq_param_install_plugin;


//
// the request remove plugin
//
typedef struct _req_param_remove_plugin_
{
	DWORD		dwCommand;			// the command this plugin will handle
}req_param_remove_plugin, *preq_param_remove_plugin;

//
// the request to run a command line
//
typedef struct _req_param_run_command_
{
	int			nSync;					// run the command in sync mode or not
	WCHAR		szCommandLine[2048];	// the command to run
}req_param_run_command, *preq_param_run_command;

//
// the ack message param of node info
//
typedef struct _udp_product_
{
	DWORD		dwCode;
	DWORD		dwMajorVersion;
	DWORD		dwMinorVersion;
	DWORD		dwSpVersion;
	DWORD		dwMajorBuild;
	DWORD		dwUpdateVersion;
	DWORD		dwUpdateBuild;
}udp_product, *pudp_product;
typedef struct _ack_param_node_info_
{
	WCHAR		szHostname[64];
	DWORD		dwCpu;		
	DWORD		dwOsMajorVersion;
	DWORD		dwOsMinorVersion;
	DWORD		dwNumberOfProductsInstalled;
	udp_product productsInstalled[1];
}ack_param_node_info, *pack_param_node_info;

inline void init_packet_req(packet_req& req)
{
	ZeroMemory(&req, sizeof(req));
	req.magic[0] = 'u';
	req.magic[1] = '.';
	req.magic[2] = 'd';
	req.magic[3] = '.';
	req.magic[4] = 'p';
	req.chkSum = 0;
	req.cmd = 0;
	req.dataSize = 0;
}

inline void init_packet_ack(packet_ack& ack)
{
	ZeroMemory(&ack, sizeof(ack));
	ack.chkSum = 0;
	ack.cmd = 0;
	ack.dataSize = 0;
	ack.errCode = 0;
}

inline bool valid_req(const packet_req& req)
{
	if (strcmp(req.magic, "u.d.p") == 0)
		return true;
	return false;
}
