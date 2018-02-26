#ifndef _COMM_REPLY_API_I_
#define _COMM_REPLY_API_I_

#include "api_comm.h"

#ifdef UNIX
#define RPS_COMM_DLL_API
#else
#ifdef  RPS_COMMDLL_EXPORTS
#define RPS_COMM_DLL_API __declspec(dllexport)
#else
#define RPS_COMM_DLL_API __declspec(dllimport)
#endif
#endif


#ifdef __cplusplus 
extern "C" { 
#endif

	RPS_COMM_DLL_API bool   init_proxy_config(const char* dest_ip, proxy_info_item_s* proxy_options, size_t proxy_num);
	RPS_COMM_DLL_API bool   set_client_nic_bind(const char* dest_server_ip, const char* client_nic_ip);
	RPS_COMM_DLL_API unsigned long	get_proxy_server_config(proxy_server_confg* server_config);

	// get error message from ASMSG.***.dll from error code of commlib
	// return 0 for success
	RPS_COMM_DLL_API int get_error_message(int error_code, wchar_t* pBuffer, int bufferSize);

	RPS_COMM_DLL_API int set_job_level_port(int port);

#ifdef __cplusplus
}
#endif
#endif