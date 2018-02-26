// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the CBT_CLIENT_EXPORTS
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// CBT_CLIENT_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.
#ifdef CBT_CLIENT_EXPORTS
#define CBT_CLIENT_API __declspec(dllexport)
#else
#define CBT_CLIENT_API __declspec(dllimport)
#endif

#pragma once

enum result_code_e
{
	rc_success_t		= 0x00000000,
	rc_file_not_protected_t		= 0x00000001,

	rc_unknown_t		= 0x80000000,
	rc_network_error_t	= 0x80000001,
	rc_job_not_found_t	= 0x80000002,
	rc_invalid_param_t	= 0x80000003,
	rc_mismtach_version_t		= 0x80000004,
	rc_different_agent_t		= 0x80000005,
};

enum cbt_item_flag_e
{
	iflag_folder_t	= 0x00000001,
	iflag_file_t	= 0x00000002,
};
enum cbt_job_conf_type_e
{
	job_conf_type_normal_t = 0,
	job_conf_type_new_node_t,
	job_conf_type_max_t
};
enum cbt_job_merge_type_e
{
	job_merge_t = 0,
	job_check_t = 1,
	job_max_type_t
};
enum cbt_bitmap_type_e
{
	bit_cbt_t = 0,
	bit_auto_t,
	bit_max_type_t
};
struct cbt_item_s
{
	cbt_item_s* next;
	const char* path;
	const char* spool;
	long flag;
};

struct cbt_job_conf_s
{
	cbt_item_s* item;
	const char* job_set_id;
	const char* agent_id;
	cbt_job_conf_type_e job_conf_type;
};

class cbt_job_i 
{
public:
	virtual const char* get_id() = 0;
	virtual result_code_e prepare_rotation(const cbt_job_conf_s& new_conf) = 0;
	virtual result_code_e finish_rotation(const char* snap_set_id) = 0;
	virtual result_code_e cleanup(const char* snap_set_id) = 0;
	/*
	1.get normal cbt bitmap file,bit_type = bit_cbt_t
	2.get the auto-recovery bitmap file, bit_type = bit_auto_t
	*/
	virtual result_code_e get_bitmap(const char* snap_set_id, const char* file_path, const char* result_path,cbt_bitmap_type_e bit_type) = 0;
	virtual result_code_e get_node_list(char* node_list[],unsigned int &node_num) = 0;
	virtual result_code_e merge_bitmap(char* node_list[],unsigned int node_num,cbt_job_merge_type_e job_merge_type) = 0;
	virtual result_code_e set_psy_bitmap(char* psy_bitmap_result_path[], int file_num) = 0;
	virtual result_code_e get_psy_bitmap(const char* psy_bitmap_result_path,char* phy_list[],unsigned int &phy_num) = 0;
	virtual ~cbt_job_i(){}
};

class cbt_client_i 
{
public:
	virtual result_code_e job_open(const char* job_id, cbt_job_i** job) = 0;
	virtual result_code_e job_close(cbt_job_i* job) = 0;
	virtual result_code_e job_kill(cbt_job_i* job) = 0;
	virtual ~cbt_client_i(){}

};

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

CBT_CLIENT_API result_code_e cbt_client_open(const char* ip_str, unsigned short port, cbt_client_i** client);
	CBT_CLIENT_API void cbt_client_close(cbt_client_i* client);
CBT_CLIENT_API result_code_e get_node_list(const char* ip_str, unsigned short port,char* node_list[],unsigned int &node_num);
#ifdef __cplusplus
}
#endif // __cplusplus
