#pragma once
#include "api_comm.h"

class memory_pointer_i
{
public:
	virtual void free() = 0;
	virtual const void* get_memory(size_t& size) const = 0;
};

// the status returned from CI framework
typedef struct _REP_COMM_STATUS
{
	int         nStatus;            // the job status, 4 means finished
	bool        bHaveErr;
	int         nTotalFileCnt;
	int         nSentFileCnt;
	__int64     llTotalSize;
	__int64     llSentSize;
} REP_COMM_STATUS;

class client_manger_i
{
public:
	virtual bool connect(const char* conf_str, bool dst_ds_deduped, comm_global_param* server_param, size_t* session_id) = 0;
	//if client_info isn't empty, server_info can't be empty
	virtual bool start(size_t session_id, const char* conf_str, memory_pointer_i*& output, const char* client_info = 0, size_t c_len = 0, const char* server_info = 0, size_t s_len = 0) = 0;
	virtual bool begin_send(size_t session_id, const char* conf_str, const char* client_info = 0, size_t c_len = 0, const char* server_info = 0, size_t s_len = 0) = 0;
	virtual bool send(size_t session_id) = 0;
	virtual bool cancel(size_t session_id) = 0;
	virtual bool end_send(size_t session_id, const char* end_ctx, size_t ctx_length) = 0;
	virtual bool stop(size_t session_id, int error_code) = 0;

	virtual bool initialize() = 0;
	virtual bool uninitialize() = 0;
	//basic information: the whole progress, current file progress, hav_error(true/false)
	virtual int query_status(size_t session_id, REP_COMM_STATUS& stat) = 0;
	//detail information: each file progress, error list
	virtual int query_details(size_t session_id, char** result_str,size_t* len) = 0;

	// is canceled from replication server side
	virtual int is_remotely_canceled(size_t session_id, bool* is_remotely_canceled) = 0;

	/// execute command on the remote server, the caller should call free(output->buffer) after calling this function
	virtual int execute_command(size_t session_id, command_parameter* input, command_parameter* output)=0;

	/// get current outbound throttle rate (bits/sec), return true if the value is successfully retrieved
	virtual bool get_current_throttle(long long& bandwidth_throttle)=0;

	virtual bool get_gdd_cfg(size_t session_id, const char *conf_str, char *cfg_buf, size_t *cfg_buf_size)=0;

	virtual ~client_manger_i(){}
	
	virtual void get_ssl_info(size_t session_id, ssl_info&)const{}

	virtual void get_d2d_versions(size_t session_id, comm_d2d_ver_s& d2d_ver, comm_d2d_ver_s& peer_d2d_ver) = 0;
};

class server_manger_i
{
public:
	virtual void set_quit_event(srv_quit_callback_i* evt_quit) = 0;
	virtual void set_named_pipe(const char* named_pipe) = 0;
	virtual bool create_server(transport_n type, unsigned int port) = 0;
	virtual void stop_all() = 0;
	virtual bool initialize(comm_global_param* server_param) = 0;
	virtual bool uninitialize() = 0;

	virtual ~server_manger_i(){}
};

#ifdef  SYNC_UTLDLL_EXPORTS
#define SYNC_UTLDLL_API __declspec(dllexport)
#else
#define SYNC_UTLDLL_API __declspec(dllimport)
#endif


#ifdef __cplusplus 
extern "C" { 
#endif
	SYNC_UTLDLL_API client_manger_i* create_client_manger();	

#ifdef __cplusplus
}
#endif

#ifdef __cplusplus 
extern "C" { 
#endif
	SYNC_UTLDLL_API server_manger_i* create_server_manger();	

#ifdef __cplusplus
}
#endif

/**
* get the text description of error (in UTF8).
* return 0 success, others fail. 
* err- 		        [in]  error code.
* buf         	    [out] pointer to the buffer to receive string.
* buf_size     -    [in]  the input buffer size
*                   [out] the size of the returned message (including the \0).
*                         if return ERROR_INSUFFICIENT_BUFFER, indicates the needed buffer size .
*/

#ifdef __cplusplus 
extern "C" { 
#endif
	// get error message from ASMSG.***.dll from error code of commlib
	// return 0 for success
	SYNC_UTLDLL_API int get_sync_utl_error_message(int error_code, wchar_t* pBuffer, int bufferSize);

#ifdef __cplusplus
}

#ifdef __cplusplus 
extern "C" { 
#endif
	SYNC_UTLDLL_API unsigned long  stop_rep_service();	

#ifdef __cplusplus
}
#endif

#endif