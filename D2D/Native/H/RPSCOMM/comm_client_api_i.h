
#ifndef _COMM_CLIENT_API_
#define _COMM_CLIENT_API_

#include "api_comm.h"
#include "comm_common_api_i.h"


class comm_file_sending_callback_i
{
public:
	virtual void update_file(
						    const char*file_name, 
							__uint64  file_processed_len,
							__uint64* orig_process_len) = 0;

	virtual void handle_received_ack(unsigned int async_id,int err) = 0;

	virtual void add_ref() = 0;
	virtual void release() = 0;

	virtual ~comm_file_sending_callback_i() {}
}; 


/*
	This class provides the abstract interface implemented by the interface
	client and called back by the communication library client side.
*/


class comm_client_callback_i
{
public:

	virtual bool on_command_received (
									unsigned int comm_id,
									const void *received_data,
									const size_t received_data_len,
									comm_reply_api_i* reply
									) = 0;

	virtual bool pre_file_received (
									unsigned int file_id,
									const char *file_path,
									char** new_file_path,
									const void* extra_data,
									const size_t extra_data_len) = 0;

	virtual bool on_file_received (
									unsigned int file_id,
									const char* file_path,
									const void* extra_data,
									const size_t extra_data_len,
									comm_reply_api_i* reply) = 0;

	virtual bool on_reset_connection() = 0;

	virtual void on_error(unsigned int error) = 0;

	virtual void add_ref() = 0;
	virtual void release() = 0;

	virtual	~comm_client_callback_i() {}
};

class comm_client_api_i
{
public:
	virtual bool initialize(comm_global_param* server_param) = 0;

	virtual bool connect(
		transport_n transport_type,
		comm_options_t* comm_options,
		comm_client_callback_i* client_callback) = 0;

	virtual bool send_file(comm_file_msg_s& file_msg) = 0;
	virtual bool send_file_async(comm_file_msg_s& file_msg) = 0;

	virtual bool  recv_command(char * buf,size_t buf_len,size_t *data_len)=0;//RECV
	
	virtual bool send_command(comm_msg_block_s& cmd_msg) = 0;
	virtual bool send_command_async(comm_msg_block_s& cmd_msg) = 0;

	virtual bool close_connection(bool is_forced = false) = 0;

	virtual comm_err_t wait_for_async_ack(
						async_id_t async_id, 
						comm_reply_api_i** reply,
						unsigned time_out = 0) = 0;

	virtual unsigned long last_error_code() const = 0 ;

	virtual bool set_additional_parameters(comm_param_type type, ...) = 0;

	virtual void set_file_process_updater(comm_file_sending_callback_i * sending_process) = 0;
	virtual void add_ref() = 0;
	virtual void release() = 0;
	virtual ~comm_client_api_i() {}
	virtual void get_ssl_info(ssl_info& ssl)const{}
	virtual bool get_peer_d2d_version(comm_d2d_ver_s& d2d_ver) = 0;
};

#ifndef UNIX
#ifdef  RPS_COMMDLL_EXPORTS
#define RPS_COMM_DLL_API __declspec(dllexport)
#else
#define RPS_COMM_DLL_API __declspec(dllimport)
#endif
#endif



#ifdef __cplusplus 
extern "C" { 
#endif

	RPS_COMM_DLL_API comm_client_api_i* create_comm_client();	

#ifdef __cplusplus
}
#endif


#endif