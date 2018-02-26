#ifndef _RPS_COMMON_H_
#define _RPS_COMMON_H_

#ifdef _WINDOWS_
#include "Winsock2.h"
#include "WinIoCtl.h"
#else
 #include <arpa/inet.h>
#endif

//xdr flag value
#define XDR_FLAG_TO_NETWORK	0
#define XDR_FLAG_TO_HOST	1

//create/open flag
#define	DISK_FLAG_OPEN_READ_ONLY 	1   // open read-only
#define DISK_FLAG_CREATION			3 //create new

#define COMMON_PATH_MAX_RPS 1024
#define ENCRYPTION_ALGO_MAX_RPS			128
#define ENCRYPTION_PWD_MAX_RPS			128
#define COMMON_LEN_RPS					64

//encrypt algo
#define AES128 "AES128"
#define AES192 "AES192"
#define AES256 "AES256"


enum transfer_err_code_e
{
		no_error_t,
		err_in_afstor_t,
		err_in_af_transfer_t,
		err_in_param_t,
		err_max_t
};


#ifdef _WINDOWS_
	typedef unsigned __int64 LARGE_NUMBER;
#else
	typedef unsigned long long	LARGE_NUMBER;
#endif	


//flag(0): covert to net
//flag(1): covert to host
#pragma pack(1)
typedef struct _param_create_srv_obj_req {
	char rootPath[COMMON_PATH_MAX_RPS];//used for create dev object now
	char uuid[COMMON_LEN_RPS];//data store UUID	
	LARGE_NUMBER jobId;
}param_create_srv_obj_req, *p_param_create_srv_obj_req;

typedef struct _param_create_session_req {
	int createFlag;//1 to open for read, 2 to open for write, 3 to create a new
	int sessNo;//used for open
	char rootPath[COMMON_PATH_MAX_RPS];//used for create //duplicated with create_srv_obj, just reserved
	char domain[COMMON_LEN_RPS]; //user for rootPath if it is a shared folder. //maybe this can be deleted when datastore's UUID is used
	char user[COMMON_LEN_RPS]; //user for rootPath if it is a shared folder. //maybe this can be deleted when datastore's UUID is used
	char passwd[COMMON_LEN_RPS]; //passwd for rootPath if it is a shared folder. //maybe this can be deleted when datastore's UUID is used
	int backupFormatType;
	int compressLevel;
	char algo[ENCRYPTION_ALGO_MAX_RPS];
	char encpasswd[ENCRYPTION_PWD_MAX_RPS];
}param_create_session_req, *p_param_create_session_req;

typedef struct _param_create_session_ret {
	int sessHandle;
	int sessNo;//used for delete session when release session failed
	int err_no;
}param_create_session_ret, *p_param_create_session_ret;

#define param_release_session_req param_create_session_ret

typedef struct _param_delete_session_req {
	int sessNo;
}param_delete_session_req, *p_param_delete_session_req;

typedef struct _param_release_session_ret {
	int err_no;  //if 0, success; else fails
	LARGE_NUMBER llLogicalSize;
	LARGE_NUMBER llActualSize;
}param_release_session_ret, *p_param_release_session_ret;


typedef struct _param_getCompressedSize_ret {
	int err_no;   //if 0, success; else fails
	LARGE_NUMBER llWrittenSize;
}param_getCompressedSize_ret, *p_param_getCompressedSize_ret;

#define param_destory_srv_obj_ret param_release_session_ret


#if !defined MEDIA_TYPE_DEF && !defined _WINDOWS_
#define MEDIA_TYPE_DEF
typedef enum _MEDIA_TYPE {
	Unknown,                // Format is unknown
	F5_1Pt2_512,            // 5.25", 1.2MB,  512 bytes/sector
	F3_1Pt44_512,           // 3.5",  1.44MB, 512 bytes/sector
	F3_2Pt88_512,           // 3.5",  2.88MB, 512 bytes/sector
	F3_20Pt8_512,           // 3.5",  20.8MB, 512 bytes/sector
	F3_720_512,             // 3.5",  720KB,  512 bytes/sector
	F5_360_512,             // 5.25", 360KB,  512 bytes/sector
	F5_320_512,             // 5.25", 320KB,  512 bytes/sector
	F5_320_1024,            // 5.25", 320KB,  1024 bytes/sector
	F5_180_512,             // 5.25", 180KB,  512 bytes/sector
	F5_160_512,             // 5.25", 160KB,  512 bytes/sector
	RemovableMedia,         // Removable media other than floppy
	FixedMedia,             // Fixed hard disk media
	F3_120M_512,            // 3.5", 120M Floppy
	F3_640_512,             // 3.5" ,  640KB,  512 bytes/sector
	F5_640_512,             // 5.25",  640KB,  512 bytes/sector
	F5_720_512,             // 5.25",  720KB,  512 bytes/sector
	F3_1Pt2_512,            // 3.5" ,  1.2Mb,  512 bytes/sector
	F3_1Pt23_1024,          // 3.5" ,  1.23Mb, 1024 bytes/sector
	F5_1Pt23_1024,          // 5.25",  1.23MB, 1024 bytes/sector
	F3_128Mb_512,           // 3.5" MO 128Mb   512 bytes/sector
	F3_230Mb_512,           // 3.5" MO 230Mb   512 bytes/sector
	F8_256_128,             // 8",     256KB,  128 bytes/sector
	F3_200Mb_512,           // 3.5",   200M Floppy (HiFD)
	F3_240M_512,            // 3.5",   240Mb Floppy (HiFD)
	F3_32M_512              // 3.5",   32Mb Floppy
} MEDIA_TYPE, *PMEDIA_TYPE;

typedef struct _DISK_GEOMETRY {
	LARGE_NUMBER		Cylinders;
	MEDIA_TYPE MediaType;
	unsigned int TracksPerCylinder;
	unsigned int SectorsPerTrack;
	unsigned int BytesPerSector;
} DISK_GEOMETRY, *PDISK_GEOMETRY;
#endif

typedef struct _param_vhdLib_createDisk_req {
	unsigned int sessHandle;//session id retured by create_session
	unsigned int diskId;
	unsigned int parentDiskId;
	int parentSessNo;
	DISK_GEOMETRY			stGeometry;	
	LARGE_NUMBER		liDiskSize;
	int	        			flag;  //open or create;  1 to open for read, 2 to open for write, 3 to create a new
	int						dwCompressRatio;
	char					disk_name[COMMON_PATH_MAX_RPS]; //for open disk
}param_vhdLib_createDisk_req, *p_param_vhdLib_createDisk_req;

typedef struct _param_vhdLib_createDisk_ret {	
	LARGE_NUMBER		size;     //For common file use
	int err_no; //0 means success
}param_vhdLib_createDisk_ret, *p_param_vhdLib_createDisk_ret;

#pragma pack()

#define param_vhdLib_removeDisk_ret param_release_session_ret
#define param_vhdLib_closeDisk_ret param_release_session_ret
#define param_delete_session_ret param_release_session_ret

static inline int param_create_srv_obj_req_xdr(param_create_srv_obj_req* req, int flag)
{
	//no need consider char buffer
	if (flag == XDR_FLAG_TO_NETWORK)
	{
		unsigned int low = (int)(req->jobId);
		unsigned int high = (int)(req->jobId >> 32);
		low = htonl(low);
		high = htonl(high);
		req->jobId = (((LARGE_NUMBER)high) << 32) + low;
		return 0;
	}
	else if (flag == XDR_FLAG_TO_HOST)
	{
		unsigned int low = (int)(req->jobId);
		unsigned int high = (int)(req->jobId >> 32);
		low = ntohl(low);
		high = ntohl(high);
		req->jobId = (((LARGE_NUMBER)high) << 32) + low;
		return 0;
	}
	return -1;
}
static inline int param_create_session_req_xdr(param_create_session_req* req, int flag)
{
	if ( flag == XDR_FLAG_TO_NETWORK )
	{
		req->createFlag = htonl(req->createFlag);
		req->sessNo = htonl(req->sessNo);
		req->backupFormatType = htonl(req->backupFormatType);
		req->compressLevel = htonl(req->compressLevel);
		return 0;
	} else if ( flag == XDR_FLAG_TO_HOST )
	{
		req->createFlag = ntohl(req->createFlag);
		req->sessNo = ntohl(req->sessNo);
		req->backupFormatType = ntohl(req->backupFormatType);
		req->compressLevel = ntohl(req->compressLevel);
		return 0;
	}
	return -1;
}

static inline int param_create_session_ret_xdr(param_create_session_ret* req, int flag)
{
	if ( flag == XDR_FLAG_TO_NETWORK )
	{
		req->sessHandle = htonl(req->sessHandle);
		req->err_no = htonl(req->err_no);
		req->sessNo = htonl(req->sessNo);
		return 0;
	} else if ( flag == XDR_FLAG_TO_HOST )
	{
		req->sessHandle = ntohl(req->sessHandle);
		req->err_no = ntohl(req->err_no);
		req->sessNo = ntohl(req->sessNo);
		return 0;
	}
	return -1;
}

static inline int param_release_session_req_xdr(param_release_session_req* req, int flag)
{
	return param_create_session_ret_xdr(req, flag);
}


static inline int param_release_session_ret_xdr(param_release_session_ret* req, int flag)
{
	if (flag == XDR_FLAG_TO_NETWORK)
	{
		req->err_no = htonl(req->err_no);

		unsigned int low = (int)(req->llLogicalSize);
		unsigned int high = (int)(req->llLogicalSize >> 32);
		low = htonl(low);
		high = htonl(high);
		req->llLogicalSize = (((LARGE_NUMBER)high) << 32) + low;


		low = (int)(req->llActualSize);
		high = (int)(req->llActualSize >> 32);
		low = htonl(low);
		high = htonl(high);
		req->llActualSize = (((LARGE_NUMBER)high) << 32) + low;

		return 0;
	}
	else if (flag == XDR_FLAG_TO_HOST)
	{
		req->err_no = ntohl(req->err_no);

		unsigned int low = (int)(req->llLogicalSize);
		unsigned int high = (int)(req->llLogicalSize >> 32);
		low = ntohl(low);
		high = ntohl(high);
		req->llLogicalSize = (((LARGE_NUMBER)high) << 32) + low;


		low = (int)(req->llActualSize);
		high = (int)(req->llActualSize >> 32);
		low = ntohl(low);
		high = ntohl(high);
		req->llActualSize = (((LARGE_NUMBER)high) << 32) + low;

		return 0;
	}

	return -1;
}

static inline int param_common_ret_xdr(param_release_session_ret* req, int flag)
{
	if ( flag == XDR_FLAG_TO_NETWORK )
	{
		req->err_no = htonl(req->err_no);
		return 0;
	} else if ( flag == XDR_FLAG_TO_HOST )
	{
		req->err_no = ntohl(req->err_no);
		return 0;
	}
	return -1;
}



static inline int param_destory_srv_obj_ret_xdr(param_destory_srv_obj_ret*ret, int flag)
{
	return param_common_ret_xdr(ret, flag);
}

static inline int param_delete_session_req_xdr(param_delete_session_req* req, int flag)
{
	if ( flag == XDR_FLAG_TO_NETWORK )
	{
		req->sessNo = htonl(req->sessNo);
		return 0;
	} else if ( flag == XDR_FLAG_TO_HOST )
	{
		req->sessNo = ntohl(req->sessNo);
		return 0;
	}
	return -1;
}
static inline int param_delete_session_ret_xdr(param_delete_session_ret* req, int flag)
{
	return param_common_ret_xdr(req, flag);
}

static inline int param_vhdLib_createDisk_req_xdr(param_vhdLib_createDisk_req* req, int flag)
{
	if ( flag == XDR_FLAG_TO_NETWORK )
	{
		req->sessHandle = htonl(req->sessHandle);
		req->diskId = htonl(req->diskId);
		req->parentDiskId = htonl(req->parentDiskId);
		req->parentSessNo = htonl(req->parentSessNo);
		//req->stGeometry not used in linux d2d
		req->flag = htonl(req->flag);
		unsigned int low = (int)(req->liDiskSize);
		unsigned int high = (int)(req->liDiskSize >> 32);
		low = htonl(low);
		high = htonl(high);
		req->liDiskSize = (((LARGE_NUMBER)high)<<32) + low;
		return 0;
	} else if ( flag == XDR_FLAG_TO_HOST )
	{
		req->sessHandle = ntohl(req->sessHandle);
		req->diskId = ntohl(req->diskId);
		req->parentDiskId = ntohl(req->parentDiskId);
		req->parentSessNo = ntohl(req->parentSessNo);
		//req->stGeometry not used in linux d2d
		req->flag = ntohl(req->flag);
		unsigned int low = (int)(req->liDiskSize);
		unsigned int high = (int)(req->liDiskSize >> 32);
		low = ntohl(low);
		high = ntohl(high);
		req->liDiskSize = (((LARGE_NUMBER)high)<<32) + low;
		return 0;
	}
	return -1;
}

static inline int param_vhdLib_createDisk_ret_xdr(param_vhdLib_createDisk_ret* req, int flag)
{
	if ( flag == XDR_FLAG_TO_NETWORK )
	{
		unsigned int low = (int)(req->size);
		unsigned int high = (int)(req->size >> 32);
		low = htonl(low);
		high = htonl(high);
		req->size = (((LARGE_NUMBER)high) << 32) + low;

		req->err_no = htonl(req->err_no);
		return 0;
	} else if ( flag == XDR_FLAG_TO_HOST )
	{
		unsigned int low = (int)(req->size);
		unsigned int high = (int)(req->size >> 32);
		low = ntohl(low);
		high = ntohl(high);
		req->size = (((LARGE_NUMBER)high) << 32) + low;

		req->err_no = ntohl(req->err_no);
		return 0;
	}
	return -1;
}

/*static inline int param_vhdLib_closeDisk_req_xdr(param_vhdLib_closeDisk_req* req, int flag)
{
	return param_vhdLib_createDisk_ret_xdr(req, flag);
}*/


static inline int param_getCompressedSize_ret_xdr(param_getCompressedSize_ret *req, int flag)
{
	if (flag == XDR_FLAG_TO_NETWORK)
	{
		req->err_no = htonl(req->err_no);

		unsigned int low = (int)(req->llWrittenSize);
		unsigned int high = (int)(req->llWrittenSize >> 32);
		low = htonl(low);
		high = htonl(high);
		req->llWrittenSize = (((LARGE_NUMBER)high) << 32) + low;
		return 0;
	}
	else if (flag == XDR_FLAG_TO_HOST)
	{
		req->err_no = ntohl(req->err_no);

		unsigned int low = (int)(req->llWrittenSize);
		unsigned int high = (int)(req->llWrittenSize >> 32);
		low = ntohl(low);
		high = ntohl(high);
		req->llWrittenSize = (((LARGE_NUMBER)high) << 32) + low;
		return 0;
	}

	return -1;
}


static inline int param_vhdLib_closeDisk_ret_xdr(param_vhdLib_closeDisk_ret* ret, int flag)
{
	return param_common_ret_xdr(ret, flag);
}

#endif