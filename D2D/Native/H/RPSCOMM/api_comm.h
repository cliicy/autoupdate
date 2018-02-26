#ifndef _API_CONST_H_
#define _API_CONST_H_

#include <memory.h>
#ifdef _WINDOWS_
#include "../UDPResource.h"
#else
#include <stdint.h>
#endif
//
//transport type
//
enum transport_n
{
	transport_none,  //none transport type
	transport_xonet, //socket transport type
	transport_soap,  //soap transport type
	transport_rest,
	transport_ipc_xonet //socket transport type, but not to listening port.
};

//
//command type
//

#define MAX_CMD_ID	1024
enum comm_cmd_n
{
	comm_cmd_dummy_test = 0,		//dummy for test
	comm_cmd_authenticate=1,		//user authenticate
	comm_cmd_disconnect,			//disconnect the server connection
	comm_cmd_special_disconnect,	//disconnect the server connection by special thread.
	comm_cmd_ping,					//echo reply/request 
	comm_cmd_bosh_get,				//BOSH style request from client 
	comm_cmd_bosh_get_with_response,//BOSH request from client. also it carries the last response from client to server.
	comm_cmd_bosh_test,				//client requests server to queue command
	comm_cmd_handshake,			//get session id and sure that socket has initialized
	comm_cmd_ft_chunk,			//used as the data block channel
	comm_cmd_ct_chunk,			//only test and we don't support it
	comm_cmd_data_stream,
	comm_cmd_rtt,		    //Only for RTT
	comm_cmd_heartbeat,				// heartbeat. if no data is transferred in 6 seconds, send thread will send a heartbeat.
									// If receive thread can't receive data in 18 seconds, it will assume the network connection is broke.

	comm_cmd_file_close,
	comm_cmd_file_flush,
	comm_cmd_file_is_open,
	comm_cmd_file_open,
	comm_cmd_file_read,
	comm_cmd_file_seek,
	comm_cmd_file_size,
	comm_cmd_file_list,
	comm_cmd_file_truncate,
	comm_cmd_file_write,

	comm_cmd_files_job_start,//used to notify server the replication will start.
	comm_cmd_files_job_end,  //used to notify server the replication ends.

	comm_cmd_files_recv,
	comm_cmd_files_recv_status,

	comm_cmd_file_gdd_cfg,
	comm_cmd_file_gdd_cfg_end,
	comm_cmd_file_send_start,
	comm_cmd_file_send_end,

	comm_cmd_files_remote_status,
	comm_cmd_file_execute_command,
	//backup
	comm_check_client_command = 512,
	comm_close_connect_command,
	comm_create_srv_obj,
	comm_destory_srv_obj,
	comm_create_session_command,
	comm_release_session_command,
	comm_delete_session_command,

	comm_vhdlib_createDisk_command, 
	comm_vhdlib_closeDisk_command,
	comm_vhdlib_writeDisk_command,
	comm_vhdlib_readDisk_command,
	comm_vhdlib_getCompressedSize_command,
	comm_cmd_user_id_begin = MAX_CMD_ID,       
	comm_command_max = comm_cmd_user_id_begin + MAX_CMD_ID //do insert commands after this entry
};

// the const global variables for BOSH
#define DUMMY_PAYLOAD "A"
enum
{
	dummy_payload_size = 1,
	bosh_command_timeout  = 27 * 1000,
	bosh_response_timeout = 30 * 1000,

	bosh_reply_retry_on_failure = 30,
	bosh_reply_interval = 2000
};

enum comm_file_type
{
	comm_file_type_dummy = 0,
	comm_file_user_file_type_begin = MAX_CMD_ID,
	comm_file_max = comm_file_user_file_type_begin + MAX_CMD_ID
};

enum proxy_access_type
{
	comm_no_proxy,				//none proxy
	comm_named_proxy			//named proxy
};

#define MAX_PROXY_INFO_LEN 128
struct proxy_info_item_s
{
	proxy_info_item_s()
	{
		server_ip[0] = '\0';
		port = 0;
		username[0] = '\0';
		password[0] = '\0';
	
		is_ssl = false;
	}

	char server_ip[MAX_PROXY_INFO_LEN]; // the proxy server ip address.
	unsigned short	port;				// the proxy server port.
	char username[MAX_PROXY_INFO_LEN];  // the proxy used user password credential
	char password[MAX_PROXY_INFO_LEN];  // the proxy used password credential
	
	bool is_ssl;
};

struct comm_options_t
{
public:
	comm_options_t():
	  server_ip(0),
	  port(0),
	  user_name(0), //utf-8
	  password(0),  //utf-8
	  access_type(comm_no_proxy),
	  proxy_by_pass(0),
	  url(0),
	  user_defined_str(0), //limited to 128 Bytes
	  is_ssl(false),  //enable ssl communication.
	  is_auto_tuning(false),
	  retry_conn_if_failed(true)
	  {
		  memset( &proxy_info, 0, sizeof(proxy_info) );
	  }

	char* server_ip;
	unsigned short port;
	char* user_name; //utf-8
	char* password;  //utf-8
	proxy_access_type access_type;
	char* proxy_by_pass;
	char* url;
	char* user_defined_str; //limited to 128 Bytes
	bool is_ssl;  //enable ssl communication.
	proxy_info_item_s proxy_info;
	proxy_info_item_s port_sharing;

	bool is_auto_tuning; 
	bool retry_conn_if_failed;
};

struct comm_global_param
{
public:
	comm_global_param():
		max_threads_num(50),
		min_threads_num(5),
		max_sleep_time(1000){}

	  size_t max_threads_num; //max thread number in the thread pool.
	  size_t min_threads_num; //minimum thread number in the thread pool;
	  unsigned max_sleep_time; //the time interval of the main dispatcher thread(ms).
};


//
//session parameter type
//
enum comm_param_type
{
	comm_param_none = 0,
	comm_param_bandwidth = 1,	//set connection based bandwidth.
	comm_param_chunk_size,		//set the connection based chunk size unsigned integer
	_NOT_IN_USE_1,
	comm_param_block_recv,		//set the flag indicating if recv_command function is available.
	comm_param_recv_bufsize,	//set the size of receive buffer pool when using block recv_command method.
	_NOT_IN_USE_2,
	_NOT_IN_USE_3,
	comm_param_recv_buf_slot_size, // set the slot buffer length of the receive buffer, the unit is KB 
	_NOT_IN_USE_4,
	comm_param_nic_bound_ip_address //set the bound ip address.
};

//typedef int int32;
#ifdef UNIX
typedef unsigned int uint32;
typedef long long __int64;
typedef unsigned long long uint64;
typedef unsigned long long __uint64;
#elif defined LINUX
typedef unsigned int uint32;
//typedef long long __int64;
typedef unsigned long long uint64;
typedef unsigned long long __uint64;
#else
typedef unsigned int uint32;
typedef unsigned __int64 uint64;
typedef unsigned __int64 __uint64;
#endif
typedef int async_id_t;

#define USER_DEFINED_COMMAND_ID_BASE(id)	(comm_cmd_user_id_begin + (id))
#define USER_DEFINED_COMMAND_ID_MAX			USER_DEFINED_COMMAND_ID_BASE(MAX_CMD_ID) 


#define USER_DEFINFED_FILE_TYPE_BASE(id)   (comm_file_user_file_type_begin + (id))
#define USER_DEFINFED_FILE_TYPE_MAX(id)	   USER_DEFINFED_FILE_TYPE_BASE(MAX_CMD_ID)

/////////////////////////////////////////////////////////////////////////////////////////
////The following is the Replication Server or Replication Utility related Definition////


enum file_type_n
{
	normal_file_t,  
	d2d_file_t, 
	gdd_file_t,  
	file_type_max
};

enum job_type_n
{
	normal_job_t,
	d2d_job_t,
	gdd_job_t,
	job_type_max
};

enum sync_utl_err_n
{
	// xonet errors (compatible with xonet_n::err_t)
	ok = 0,
	sync_utl_err_base = 0xE0010000,    // COMMLIB_ERROR_BASE
	sync_cannot_access_proxy_server = sync_utl_err_base + 81,
	sync_proxy_authentication_required = sync_utl_err_base + 82,
	           
	// sync_utl errors  
	min_sync_utl_error = sync_utl_err_base + 0x400,       
	sync_utl_config,					//config is incorrect
	sync_utl_transport,				    //transport type is incorrect
	sync_utl_file_type,				    //file type is incorrect
	sync_utl_memory,					//unable to create some buffer
	sync_utl_file_interface,			//unable to load file interface dll
	sync_utl_connection,				//connection timeout
	sync_utl_file_size,					//target size > source size
	sync_utl_ctx,						//client or server ctx is incorrect

	sync_utl_out_of_sequence,   
	sync_utl_async_timeout,             //aysnc_timeout or send command timeout
	sync_utl_internal_err,
	sync_utl_gdd_cfg_too_large,

	// compatible with file_cmd_i::file_cmd_err_e
	min_transfer_err = sync_utl_err_base + 0x500,           // place holder of the start
	unknown_file_cmd_err_t,             // this error code is the corresponding to the file command
	server_is_down_or_unvailable_t,
	target_file_bigger_than_source_t,
	file_cmd_user_err_t,                // From this one, reserved for users' custom error.
	end_of_file_cmd_err = sync_utl_err_base + 0x5FF,        //place holder
	
	// compatible with file_cmd_i::file_cmd_async_result 
	min_aync_transfer_err = sync_utl_err_base + 0x600,              // asynchronous sending corresponding error.
	async_result_internal_err,
	async_result_timeout,

	//
	min_file_err = sync_utl_err_base + 0x700,

	//backup
	transportx_auth_incorrect = sync_utl_err_base+0x1000,
	transportx_obj_incorrect,
	transportx_cmd_incorrect,
	transportx_cmd_buffer_more,
	transportx_cmd_buffer_empty,
	transportx_no_ack
};

// from "winerror.h"
//   3 3 2 2 2 2 2 2 2 2 2 2 1 1 1 1 1 1 1 1 1 1
//   1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0
//  +---+-+-+-----------------------+-------------------------------+
//  |Sev|C|R|     Facility          |               Code            |
//  +---+-+-+-----------------------+-------------------------------+
//
//  where
//
//      Sev - is the severity code
//
//          00 - Success
//          01 - Informational
//          10 - Warning
//          11 - Error
//
//      C - is the Customer code flag
//
//      R - is a reserved bit
//
//      Facility - is the facility code
//
//      Code - is the facility's status code
//
//

// macros for Communication Library error code
#define  COMMLIB_ERROR_BASE								(0xE0010000)
#define  MAKE_COMMLIB_ERROR(err)                        (err)         //( (err)|COMMLIB_ERROR_BASE )       
#define  IS_COMMLIB_ERROR(err)                          ( ((err) & COMMLIB_ERROR_BASE) == COMMLIB_ERROR_BASE )
#define  IS_D2D_ERROR(err)                              ( ((err) & D2D_STANDARD_ERR_BASE) == D2D_STANDARD_ERR_BASE )
#define  HAS_CUSTOMER_CODE_FLAG(err)                    ( ((err) & 0x20000000) == 0x20000000 )

// macros for Communication Library resource ID, IDs are defined in ..\..\RCModules\AFMSG\afmsg_res.h
#define  COMMLIB_RESOURCE_BASE							IDS_COMMLIB_XONET_OK
#define  COMMLIB_RESOURCE_MAX                           AFRES_COMMLIB_MAX
#define  IS_COMMLIB_RESOURCE(id)                        ( (id)>=COMMLIB_RESOURCE_BASE && (id)<=COMMLIB_RESOURCE_MAX )

#define  COMMLIB_RESOURCE_ERROR(err)				    (COMMLIB_RESOURCE_BASE + ((err)&(0x0000ffff))) //error code is lower 16bit. 

// the following error code is used by GDD or D2D
#define D2D_STANDARD_ERR_BASE							0xA00E0000                                   // error from CID2Dplugin

/////End of the Definition for the Replication Server or Replication Utility related Definition///
//////////////////////////////////////////////////////////////////////////////////////////////////

//the following error is compatible with xonet communication library (xonet_n::err_t).

enum comm_err_t
{
	comm_ok_t = 0,
	comm_err_base = 0xE0010000,              // COMMLIB_ERROR_BASE
	comm_unknown,									// these
	comm_core_error,								// use only in core part of code for unexpected cases
	comm_bad_appl_params = comm_err_base + 5,					// errors
	comm_lost_acknowledgement = comm_err_base + 23,
	comm_connection_broken = comm_err_base + 27,					//
	comm_connection_closed_by_peer = comm_err_base + 28,			// these errors sre set by both
	comm_timeout = comm_err_base + 29,	

	comm_bad_socket = comm_err_base + 30,							// OS

	comm_would_block,						// following errors are set by OS
	comm_no_more_sockets,
	comm_permission_denied,
	comm_network_down,
	comm_no_more_bufs,
	comm_none_reentr,
	comm_winsock_not_initialized,
	comm_winsock_ver_not_supported,
	comm_server_not_initialized,
	comm_listen_was_not_called,
	comm_gethostbyname_failed,
	comm_connection_refused_by_OS,
	comm_connection_reset,
	comm_system_timeout,
	comm_bad_OS_params_params,
	comm_connection_shut_down,
	comm_address_already_in_use,
	comm_routing_problem,
	comm_unknown_address,
	comm_UNIX_signal_catch,

	comm_non_secure_peer = comm_err_base + 51,
	comm_init_ctx_error,
	comm_init_ssl_error,
	comm_null_ctx,
	comm_null_ssl,
	comm_null_key,
	comm_null_ca,
	comm_bad_key_config,
	comm_bad_key_file,
	comm_bad_ca_file,
	comm_bad_bio,
	comm_bad_key,
	comm_io_error,
	comm_io_eof_error,
	comm_bad_parameter,
	comm_handshake_error,
	comm_clean_close,
	comm_repeat,
	comm_protocol_error,
	comm_syscall_error,// 
	comm_xx_error = comm_err_base + 71,
	comm_cannot_access_proxy_server = comm_err_base + 81,
	comm_proxy_authentication_required = comm_err_base + 82,
};


//the following structure is used by GDDClient to query portsharing configruation.
struct proxy_server_confg
{
	proxy_server_confg():
	is_port_sharing(0),
	proxy_port(0),
	is_ssl(0),
	proxy_server_ip(0),
	is_shared_socket(0)
	{}

	unsigned is_port_sharing;
	unsigned short proxy_port;
	unsigned is_ssl;
	unsigned is_shared_socket;    
	unsigned long proxy_server_ip;//not used currently.	
	unsigned size;//used for sanity check, the size of this structure.
};

// a common command parameter for client_manger_i.execute_command()
struct command_parameter
{
	int		type;          // user defined command type	
	int		length;        // buffer length
	void*	buffer;        // buffer
};

struct ssl_info
{
	bool			isSSL;
	char			protocol[MAX_PROXY_INFO_LEN];
	char			cipher[MAX_PROXY_INFO_LEN];
	int				cipherBits;

	ssl_info()
	{
		isSSL = false;
		memset(&protocol, 0, sizeof(protocol));
		memset(&cipher, 0, sizeof(cipher));
		cipherBits = 0;
	}
};

struct comm_d2d_ver_s
{
	unsigned _major;
	unsigned _minor;
	unsigned build;
//	unsigned incremental;

	unsigned update_ver;
	unsigned update_build;

	// like: 5.0.1897, update4.1086
	comm_d2d_ver_s() : _major(0), _minor(0), build(0), update_ver(0), update_build(0) {}
};

struct comm_rep_check_s
{
	comm_d2d_ver_s src_d2d_ver;
	comm_d2d_ver_s dst_d2d_ver;
};

class comm_reply_api_i
{
public:
	virtual bool  append(const void* buf, size_t buf_len) = 0;
	virtual const void  *data() = 0;
	virtual size_t	 data_len() = 0;
	virtual ~comm_reply_api_i(){};
};

class srv_quit_callback_i
{
public:
	virtual void srv_quit() = 0;
};

struct comm_msg_block_s
{
public:
	comm_msg_block_s() : msg_id(0), session_id(0), need_reply(false), reply(0), timeout(0), do_compress(false), msg_data(0), msg_data_len(0), async_id(0) {}
	~comm_msg_block_s() {}

	unsigned int msg_id; // file or command id

	unsigned long session_id;

	bool need_reply;
	comm_reply_api_i** reply; // for sync only

	unsigned int timeout;  // in seconds
	bool do_compress;

	void* msg_data;
	size_t msg_data_len;

	async_id_t async_id; // output, for async only
};

struct comm_file_msg_s : public comm_msg_block_s
{
public:
	comm_file_msg_s() : comm_msg_block_s(), local_file_path(0), remote_file_path(0) {}
	~comm_file_msg_s() {}

	const char *local_file_path;
	const char *remote_file_path;
};

#endif
