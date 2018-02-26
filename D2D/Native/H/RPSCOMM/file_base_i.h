#pragma once
#include "api_comm.h"
class FILE_base_i
{
public:
	  //destruct myself.
	  virtual void release() = 0;
      
	  // mode can be "w", "r", "w+", "r+" or other;
      virtual bool      open(const char* filename, const char* mode,
		  const char* user_info = 0, size_t info_len = 0)=0;
      
      // buf_len is the length of plain file. derived classes need to caculate real length.
      //return value should be less than or equal buf_len.
      virtual size_t                read(char* buf, size_t buf_len)=0;
      
      // buf_len is the length of plain file. derived classes need to caculate real length.
      // return value should be equal buf_len.
      virtual size_t                write(const char* buf, size_t buf_len)=0;

      //If is_real is true, return the real file offest. Otherwise, it is the offest of plain file.
      //is_real is false in re-send case.
      //is_real is true in statistics case.
      virtual __int64               tell(bool is_real = false)=0;

      //If is_real is false, offest is offset of plain file. derived classes need to caculate real offset.
      virtual bool                  seek(__int64 offset, bool is_real = false)=0;

      virtual bool                  close(bool bCancel = false)=0;
      //if is_real is false, return the size of plain file.
      virtual __int64               size(bool is_real = false)=0;

      virtual bool                  is_open()=0;

      //it is reserved
      virtual void                  flush()=0;
      //it is reserved
      virtual bool                  truncate(__int64 offset)=0;
	  
	  virtual bool					get_rsize_from_lsize(__int64 lsize,  __int64 * rsize)=0;

	  virtual bool                  check_gdd_optimization(void** gdd_reader) = 0;//<huvfe01>2013-8-5

	  virtual int                   enable_gdd_optimization(void* gdd_reader) = 0;//<huvfe01>2013-8-5

	  virtual int                   get_gdd_file_size(__uint64& total_size, __uint64& unique_size, __uint64& compressed_size) = 0;
	  virtual int					get_gdd_header(char* pBuf,unsigned long* pBufSize) = 0;
};
class JOB_base_i
{
public:
	//destruct myself.
	virtual void release() = 0;
	
	// Call this in the beginning of job
    // @user_info - the one input in client_manger_i::start()
    // @info_len  - the length of user_info
    // return value - 0 success, others fail   
	virtual int on_job_start(const char* user_info, size_t info_len) = 0;

	virtual int rep_verification_check(comm_rep_check_s& info) = 0;

	//call this to get session list info after on_job_start(), to reply job start cmd 
	virtual int get_session_list_info(unsigned char** buffer, int* length) = 0;

	//call this to free session list info
	virtual void free_session_list_info(unsigned char* buffer) = 0;

	// Call this at the end of job
    // @error_code - error code.
    // return value - 0 success, others fail
	virtual int on_job_end(int job_status, int error_code = 0) = 0;
	

	// Call this at the begin of session
    //@user_info - the one input in client_manger_i::begin_send()
    // @bSuccess - if the job is successful.
    // return value - 0 success, others fail 
    virtual int on_session_start(const char* user_info, size_t info_len) = 0;


	// Call this at the end of session
    // @error_code - error code.
    // return value - 0 success, others fail 
    virtual int on_session_end(const char* end_ctx, size_t ctx_length) = 0;
	
	/* BEGIN: Added by <huvfe01>, 2013/4/9   PN:target job monitor & cancel job */
	virtual bool is_job_canceled() = 0;

    virtual int execute_command(command_parameter* input, command_parameter* output) = 0;
	/* END:   Added by <huvfe01>, 2013/4/9   PN:target job monitor & cancel job */

};

#ifdef UNIX
#define FILE_BASE_API
#else
#ifdef  FILE_BASE_EXPORTS
#define FILE_BASE_API __declspec(dllexport)
#else
#define FILE_BASE_API __declspec(dllimport)
#endif
#endif


#ifdef __cplusplus 
extern "C" { 
#endif
	FILE_BASE_API FILE_base_i* create_file();	
	FILE_BASE_API JOB_base_i*  create_job();
#ifdef __cplusplus
}
#endif