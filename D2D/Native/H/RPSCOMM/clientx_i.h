
#ifndef _CLIENTX_I_
#define _CLIENTX_I_

#include "api_comm.h"
#ifdef _WINDOWS_
#ifdef  TRANSPORTX_EXPORTS
#define TRANSPORTX_DLL_API __declspec(dllexport)
#else
#define TRANSPORTX_DLL_API __declspec(dllimport)
#endif
#else
#define TRANSPORTX_DLL_API
#endif
//typedef struct {
//   GUID                                   volume_id;//used to identify volume which will be stored in d2d file name
//   UINT64                           capacity;
//   CRECompressOptions         compress_opt;
//   CRECryptOptions                  enc_opt;
//   GUID                       uuid;//used to identify session of this volume which will be stored in d2d header
//   UINT8                            flag;  //open or create;   CREDiskCreationFlag type in libcre.h
//   char szParentDiskFileName[PATH_MAX +1];
//   CRE_DISKIO_CALLBACK diskIOCB;
//   UINT8                            serial_mode;
//      UINT16 blocksize;
//      UINT32 bitmapLen;
//      UINT64 bitmapStart;
//      UINT64 bitmapEnd;
//      UINT8 *bitmap;
//   //void                                 *pSiBatMem;
//   //UINT32                         siBatMemSize;
//} CreateParams;
class conn_para_options
{
public:
	conn_para_options(){};
	~conn_para_options(){};
	comm_options_t comm_options;
	size_t chunk_size;
	transport_n transport_type;
	size_t bandwidth;
	unsigned int stream_num;
	unsigned long auto_reconn_timeout;
	unsigned long timeout_value; // the default unit is second.
};

//struct reply_opt
//{
//	void* ret;
//	int size;
//	unsigned int error_no;	//communication answer
//};
//
//struct param_vhdLib_createDisk_req
//{
//	///
//	//
//	//
//	//
//	//
//
//};
//
//static inline void param_vhdLib_createDisk_req_xdr(param_vhdLib_createDisk_req * , int );
//
//struct param_vhdLib_createDisk_ret
//{
//	int no;
//};
//
//struct param_create_srv_obj_req
//{
//}
//
//struct param_create_session_req
//{
//}
//struct param_create_session_ret
//{
//}
//void param_vhdLib_createDisk_ret_xdr(param_vhdLib_createDisk_ret *);
struct vhd_obj
{
	uint32 obj_id;
	uint64 handle;
	bool operator == (const vhd_obj& obj)
	{
		return (handle == obj.handle && obj_id == obj.obj_id); 
	}
};

struct request_opt_item
{
	vhd_obj vhd_handle;
	__uint64 offset;
	unsigned int request_len;
	char* buffer;
	unsigned int real_len;
	unsigned int com_no;	//command answer
	unsigned int error_no;	//communication answer
	request_opt_item* link;		//link to next item
};

struct answer_head
{
	bool is_continue;
	request_opt_item* link;
};

static const uint64 rps_reserved_unique_int64 = (uint64) -1;
static const uint32 rps_reserved_unique_int32 = (uint32) -1;

class VHD_result_callback_i
{
public:
	virtual void ReleaseResource(int resource_count = 1) = 0;
};

class VHD_client_manager_i
{
public:
	virtual bool connect(const conn_para_options conn_info,__uint64* connect_id) = 0;
	/*param_create_srv_obj_req*/
	virtual bool create_srv_obj(__uint64 connect_id, uint32 flag, const char* param, uint32 param_size, uint32* obj_id,uint32* comm_no) = 0;
	/*param_destory_srv_obj_ret*/
	virtual bool destory_srv_obj(uint32 obj_id, char* ret, uint32* ret_size,uint32* comm_no) = 0;
	/*param_create_session_req*/
	/*param_create_session_ret*/
	virtual bool create_session(uint32 obj_id, const char* param, uint32 param_size, 
		char* ret,uint32* ret_size,uint32* comm_no) = 0;
	/*param_release_session_req*/
	/*param_release_session_ret*/
	virtual bool release_session(uint32 obj_id, const char* param, uint32 param_size, 
		char* ret,uint32* ret_size,uint32* comm_no) = 0;
	/*param_delete_session_req*/
	/*param_delete_session_ret*/ 
	virtual bool delete_session(uint32 obj_id, const char* param, uint32 param_size, 
		char* ret,uint32* ret_size,uint32* comm_no) = 0;
	/*param_vhdLib_createDisk_req*/
	/*param_vhdLib_createDisk_req*/
	virtual bool vhdLib_createDisk(uint32 obj_id, const char* param, uint32 param_size, uint64* handle,
		char* ret, uint32* ret_size,uint32* comm_no) = 0;
	virtual bool vhdLib_readDisk(request_opt_item* item, answer_head* head, bool is_sync) = 0;
	virtual bool vhdLib_writeDisk(request_opt_item* item, answer_head* head) = 0;
	/*param_vhdLib_closeDisk_ret*/ 
	virtual bool vhdLib_closeDisk(vhd_obj vhd_handle, 
		char* ret, uint32* ret_size, uint32* comm_no) = 0;

	
	virtual bool close_connection(bool is_forced = false,__uint64 connect_id = rps_reserved_unique_int64) = 0;

	virtual bool set_result_callback(uint32 obj_id, uint64 handle, VHD_result_callback_i * call_back) = 0;

	virtual bool vhdLib_getCompressedSize(vhd_obj vhd_handle, char*ret, uint32* ret_size, uint32* comm_no) = 0;
};

class vhd_file_i
{
public:
	/*param_create_session_req*/
	/*param_create_session_ret*/
	virtual bool create_session(const char* param, uint32 param_size, 
		char* ret,uint32* ret_size) = 0;
	/*param_release_session_req*/
	/*param_release_session_ret*/
	virtual bool release_session(const char* param, uint32 param_size, 
		char* ret,uint32* ret_size) = 0;
	/*param_delete_session_req*/
	/*param_delete_session_ret*/ 
	virtual bool delete_session(const char* param, uint32 param_size, 
		char* ret,uint32* ret_size) = 0;
	/*param_vhdLib_createDisk_req*/
	/*param_vhdLib_createDisk_req*/
	virtual bool vhdLib_createDisk(const char* param, uint32 param_size, uint64* handle,
		char* ret,
		uint32* ret_size) = 0;

	virtual int vhdLib_readDisk(uint64 handle,__uint64 offset, unsigned int read_len, char* pBuffer, unsigned int* real_len) = 0;
	virtual int vhdLib_writeDisk(uint64 handle, __uint64 offset, const char* pBuffer, unsigned int write_len, unsigned int* real_len) = 0;
	/*param_vhdLib_closeDisk_ret*/
	virtual bool vhdLib_closeDisk(uint64 handle, char* ret, uint32* ret_size) = 0;
	virtual bool vhdLib_getCompressedSize(uint64 handle, char* ret, uint32* ret_size) = 0;
	
	enum vhd_cmd_async_result
	{
		async_result_ok,
		async_result_internal_err = min_aync_transfer_err + 1,
		async_result_timeout
	};
};

//class vhd_client_callback_i
//{
//public:
////
//	virtual void vhdlib_readDisk(size_t opt_id,size_t read_size, const char* pBuffer,vhd_client_reply_opt* answer) = 0;
//	virtual void vhdlib_writeDisk(size_t opt_id,size_t write_size, vhd_client_reply_opt* answer) = 0;
//	virtual void on_error(unsigned int error) = 0;
//	virtual void add_ref() = 0;
//	virtual void release() = 0;
//	virtual	~vhd_client_callback_i() {}
//};

class srv_manager_i
{
public:
	virtual bool create_server(transport_n type, unsigned int port) = 0;
	virtual void stop_all() = 0;
	virtual bool initialize(comm_global_param* server_param) = 0;
	virtual bool uninitialize() = 0;

	virtual void set_quit_event(srv_quit_callback_i* evt_quit) = 0;
	virtual void set_named_pipe(const char* named_pipe) = 0;

	virtual ~srv_manager_i(){}
};



#ifdef _WINDOWS_

#ifdef __cplusplus 
extern "C" { 
#endif
	TRANSPORTX_DLL_API srv_manager_i* create_srv_manger();	

#ifdef __cplusplus
}
#endif

#else

#ifdef __cplusplus 
extern "C" { 
#endif
	TRANSPORTX_DLL_API VHD_client_manager_i* create_client_manger(int type = 0);	

#ifdef __cplusplus
}
#endif

#ifdef __cplusplus 
extern "C" {
#endif
	TRANSPORTX_DLL_API void release_client(VHD_client_manager_i* obj,int type = 0);

#ifdef __cplusplus
}
#endif

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
	TRANSPORTX_DLL_API int get_transportx_message(int error_code, wchar_t* pBuffer, int bufferSize);

#ifdef __cplusplus
}


#endif

#endif