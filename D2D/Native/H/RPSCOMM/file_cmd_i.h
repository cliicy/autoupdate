#pragma once

#include <string.h>
#include <stdlib.h>
#include "api_comm.h"
class file_cmd_i
{
public:
	struct file_entry_s {
	public:
		char*			   file_name;
	
		unsigned short     src_type;
		unsigned short     dst_type;

		bool               is_force;
		uint64			   size;
		unsigned short     error;

	
		explicit file_entry_s(const char* name = 0, const unsigned short src_type = 0, const unsigned short dst_type=0,const bool force = false)
			: src_type(src_type),dst_type(dst_type), is_force(force), size(0), error(0) { 
#ifdef UNIX
		file_name = strdup(name); 
#else
		file_name = _strdup(name); 	
#endif
		}

		file_entry_s& operator=(const file_entry_s& other) {
			src_type  = other.src_type;
			dst_type  = other.dst_type;

			is_force = other.is_force;
			size = other.size;
			error = other.error;
			if (file_name) free(file_name);
#ifdef UNIX
			file_name = strdup(other.file_name);
#else
			file_name = _strdup(other.file_name);
#endif
			return *this;
		}

		virtual ~file_entry_s() { if (file_name) free(file_name); file_name = 0; }
	};

	enum file_cmd_err_e
	{
		unknown_file_cmd_err_t = min_transfer_err + 1,
		server_is_down_or_unvailable_t,
		target_file_bigger_than_source_t,
		file_cmd_user_err_t, // From this one, reserved for users' custom error.
		max_file_cmd_err_t = end_of_file_cmd_err  // Two bytes, unsigned short.
	};

	enum file_cmd_async_result
	{
		async_result_ok,
		async_result_internal_err = min_aync_transfer_err + 1,
		async_result_timeout
	};

	virtual bool close_file(const unsigned file_id) = 0;
	virtual bool flush_file(const unsigned file_id) = 0;
	virtual bool is_file_open(const unsigned file_id) = 0;
	virtual unsigned open_file(const unsigned short file_type, const char* file_name, const char* open_mode, 
		const char* user_data, const unsigned user_data_length) = 0;
	virtual unsigned read_file(const unsigned file_id, char* data, const unsigned data_length) = 0;
	virtual bool seek_file(const unsigned file_id, const uint64 pos) = 0;
	virtual uint64 get_file_size(const unsigned file_id) = 0;
	virtual bool list_file(file_entry_s* file_list, const unsigned file_num, 
		const char* user_data, const unsigned user_data_length) = 0;
	virtual bool truncate_file(const unsigned file_id, const uint64 new_size) = 0;
	
	virtual unsigned write_file(const unsigned file_id, const char* data, const unsigned data_length) = 0;
	virtual unsigned write_file(const char* data, const unsigned data_length, bool do_compress = false) { return 0;}
	
	virtual bool write_file_async(const unsigned file_id, const __int64 packet_seq_no, 
		const char* data, const unsigned data_length, async_id_t& async_id) = 0;
	virtual bool write_file_async(const char* data, const unsigned data_length, async_id_t& async_id, bool do_compress/* = false*/) { return false; }
	
	virtual file_cmd_async_result end_write_file_async(const async_id_t async_id, unsigned& size_written, const unsigned timeout) = 0;
	virtual unsigned long get_last_file_err() const = 0;
#ifdef UNIX
	virtual ~file_cmd_i() {};
#else
	virtual ~file_cmd_i() = 0 {};
#endif

	virtual bool is_async_capable(){return true;};

	virtual void close_all_file()=0;
};

