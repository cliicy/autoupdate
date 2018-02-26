#ifndef _COMM_SERVER_API_I_
#define _COMM_SERVER_API_I_

#include "api_comm.h"
#include "comm_common_api_i.h"

class comm_session_ctx_i
{
public:
	virtual void*	get_retained_object()= 0;
	virtual void	set_retained_object(void* obj) = 0;
	virtual unsigned long	session_id() = 0 ;
	virtual void add_ref() = 0;
	virtual void release() = 0;
	virtual unsigned long remote_ip() = 0;
	virtual void set_active_disconnect(bool flag) = 0;
	virtual bool is_active_disconnect() = 0;
	virtual comm_err_t get_last_err() { return comm_ok_t;}
	virtual void set_last_err(comm_err_t error) {}
	virtual ~comm_session_ctx_i() {};
};


/*
The abstract class is implemented by the interface client and will be related with the 
connection. When the connection receives the client network data, the callback method 
in the abstract interface will be called.
*/
class comm_session_callback_i
{
public:

	virtual bool on_sessioin_created(comm_session_ctx_i* ctx) = 0;

	virtual bool on_command_received (
		comm_session_ctx_i* ctx,
		unsigned int comm_id,
		const void *received_data,
		const size_t received_data_len,
		comm_reply_api_i* reply) = 0; 

	virtual bool pre_file_received(
		comm_session_ctx_i* ctx,
		unsigned int file_id,
		const char *file_path,
		char** new_file_path,
		void* extra_data,
		size_t extra_data_len) = 0;

	virtual bool on_file_received( 
		comm_session_ctx_i* ctx,
		unsigned int file_id,
		const char* file_path,
		void* extra_data,
		size_t extra_data_len,
		comm_reply_api_i* reply) = 0;

	virtual bool on_reset_connection(
		comm_session_ctx_i* ctx) = 0;

	virtual bool on_error(
		comm_session_ctx_i* ctx,
		uint64 err) = 0;

	virtual void add_ref() = 0;

	virtual void release() = 0;

	virtual ~comm_session_callback_i() {}
};

class comm_server_callback_i
{
public:
	virtual comm_session_callback_i* on_new_session( unsigned long session_id, unsigned long rem_ip,
		const char* authentication, const char* connection_str, const char* reserved_str) = 0;

	virtual void add_ref() = 0;
	virtual void release() = 0;

	virtual ~comm_server_callback_i() {}
};


struct comm_transporter
{
	comm_transporter(const transport_n trans_type, comm_server_callback_i* svr_callback, const char* svr_str, const unsigned short svr_port, bool trans_block_mode = false)
		: transport_type(trans_type), server_callback(svr_callback), server_str(svr_str), port(svr_port), block_mode(trans_block_mode), quit_evt_handler(0) {}
	~comm_transporter() {}

	transport_n transport_type;
	comm_server_callback_i* server_callback;
	const char* server_str;
	unsigned short port;
	bool block_mode;
	srv_quit_callback_i* quit_evt_handler; // for quit event
};

/*
This abstract class provides the interface method implemented by the 
interface client when the server accept new session.
*/
class comm_server_api_i
{
public:
	virtual bool initialize(comm_global_param* server_param) = 0;

	virtual bool add_transport(comm_transporter& transporter) = 0;
	virtual bool remove_transport(const comm_server_callback_i* server_callback) = 0;

	virtual void set_named_pipe(const char* pszNamedPipe) = 0;
	virtual bool is_port_bound(unsigned short port) {return false;} //<huvfe01>2013-10-30
	virtual bool stop_server(const unsigned short port) = 0;
	virtual bool stop_all_server() = 0;

	virtual bool start_server(const unsigned short port) = 0;
	virtual bool start_all_server() = 0;

	virtual bool send_file(comm_file_msg_s& file_msg) = 0;

	virtual bool send_command(comm_msg_block_s& cmd_msg) = 0;
	virtual bool send_command_async(comm_msg_block_s& cmd_msg) = 0;

	virtual comm_server_callback_i* get_server_callback(const unsigned short port) = 0;

	virtual void add_ref() = 0;
	virtual void release() = 0;
	virtual ~comm_server_api_i() {}

	virtual comm_err_t wait_for_async_ack(
						const unsigned long session_id,
						async_id_t async_id, 
						comm_reply_api_i** reply,
						unsigned time_out = 0) = 0;

	virtual bool get_peer_d2d_version(const unsigned long session_id, comm_d2d_ver_s& d2d_ver) = 0;
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
	RPS_COMM_DLL_API comm_server_api_i* create_comm_server();	

#ifdef __cplusplus
}
#endif

#endif