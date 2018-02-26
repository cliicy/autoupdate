#pragma once

#include "AFLic.h"

#define S3_MAX_REGIONS	     10
#define S3_MAX_BUCKETS       100
#define S3_REGION_MAX_LENGTH 256
#define S3_BUCKET_MAX_LENGTH 64

//CCI_ACCESS_RIGHTS
#define CCI_NO_ACCESS      0x00
#define CCI_READ_ACCESS    0x01
#define CCI_WRITE_ACCESS   0x02
  
#define DONT_USE_PROXY     0x01
#define DONT_CREATE_BUCKET 0x02

enum 
MOVEMETHOD
{
	MOVEMETHOD_BEGIN = 0,
	MOVEMETHOD_CURRENT = 1,
	MOVEMETHOD_END = 2
};

enum
FILE_PROPERTIES
{
	FILE_PROPERTIES_FILE_PROPERTY_NONE = 0x00,
    FILE_PROPERTIES_FILE_COMPRESS      = 0x01,
    FILE_PROPERTIES_FILE_ENCRYPTED	   = 0x02,
	FILE_PROPERTIES_FILE_COMPRESS_MAX  = 0x04 //If this flag is set then we take the compression value as 9 check zlib documentation
};
enum
ARCCLD_ERROR_TYPE
{
	ARCCLD_ERROR_LEVEL_ERROR = 1,
	ARCCLD_ERROR_LEVEL_INFORMATION = 2,
	ARCCLD_ERROR_LEVEL_WARNING = 3
};

enum
CCI_E_ENCALG_TYPE
{
    E_ENCALG_TYPE_EEAT_UNKNOWN = 0,
    E_ENCALG_TYPE_EEAT_AES_128BIT,
    E_ENCALG_TYPE_EEAT_AES_192BIT,
    E_ENCALG_TYPE_EEAT_AES_256BIT,
    E_ENCALG_TYPE_EEAT_3DES              // Not support now.
};

enum
CCI_E_CRYPTOAPI_TYPE
{
    E_CRYPTOAPI_TYPE_ECAT_UNKNOWN = 0,
    E_CRYPTOAPI_TYPE_ECAT_BY_OS,
    E_CRYPTOAPI_TYPE_ECAT_CAPI,
    E_CRYPTOAPI_TYPE_ECAT_CNG,
};
enum 
CLOUD_TYPE
{
	CLOUD_TYPE_AMAZON_S3 = 0,
	CLOUD_TYPE_WINDOWS_AZURE_BLOB = 1,
	CLOUD_TYPE_IRON_MOUNTAIN = 2,
	CLOUD_TYPE_I365 = 3,
	CLOUD_TYPE_FILE_SYSTEM = 4,
	CLOUD_TYPE_EUCALYPTUS = 5,
	CLOUD_TYPE_CA_CLOUD = 6,
	CLOUD_TYPE_FUJITSU_CLOUD = 7,
	CLOUD_TYPE_INVALID_TYPE = -1
};

enum 
FILE_ACCESS_PERMISSIONS
{
	FILE_ACCESS_PERMISSIONS_FILE_ACCESS_WRITE = 0,
	FILE_ACCESS_PERMISSIONS_FILE_ACCESS_READ = 1,
	FILE_ACCESS_PERMISSIONS_FILE_ACCESS_APPEND = 2,
	FILE_ACCESS_PERMISSIONS_FILE_ACCESS_INVALID = -1
};

enum 
ARCCLD_FILE_ATTRIBUTE
{
	ARCCLD_FILE_ATTRIBUTE_ARCCLD_DIRECTORY = 0,
	ARCCLD_FILE_ATTRIBUTE_ARCCLD_FILE = 1,
	ARCCLD__FILE_ATTRIBUTE_UNSET = -2
};

enum 
CLOUD_STATISTICS_STATUS
{
	CLOUD_STATISTICS_STATUS_LOCAL = 0,
	CLOUD_STATISTICS_STATUS_OVERALL = 1
};

enum 
GL_ERROR
{
	GL_ERROR_NO_ERROR = 0,
	GL_ERROR_INVALID_SIZE = 1,
	GL_ERROR_LIB_INIT_FAILED = 2,
	GL_ERROR_INVALID_HANDLE = 3,
	GL_ERROR_BEYOND_FILE_SIZE = 4,
	GL_ERROR_ERROR_NEGATIVE_SEEK = 5,
	GL_ERROR_ERROR_FILE_OPENED_READ = 6,
	GL_ERROR_DIRECTORY_DOESNOT_EXIST = 7,
	GL_ERROR_PARENT_DIRECTORY_DOESNOT_EXIST = 8,
	GL_ERROR_INODE_CREATION_FAILED = 9,
	GL_ERROR_INVALID_DIRPATH = 10,
	GL_ERROR_ADD_PROVIDER_FAILED = 11,
	GL_ERROR_INVALID_PROVIDER_TOKEN = 12,
	GL_ERROR_SESSION_TOKEN_INVALID = 13,
	GL_ERROR_FILE_IN_USE = 14,
	GL_ERROR_PROVIDER_ERROR = 15,
	GL_ERROR_PLUGIN_LIBRARY_NOT_PRESENT = 16,
	GL_ERROR_NO_MORE_FILE_PRESENT = 17,
	GL_ERROR_CLOUD_OBJECT_NOT_PRESENT = 18,
	GL_ERROR_FILE_ALREADY_EXIST = 19,
	GL_ERROR_CREATE_ROOT_DIRECTORY = 20,
	GL_ERROR_SET_CURRENT_DIRECTORY = 21,
	GL_ERROR_ROOT_DIRECTORY_ALREADY_EXIST = 22,
	GL_ERROR_DIRECTORY_NOT_EMPTY = 23,
	GL_ERROR_ENCRYPTION_FAILED = 24,
	GL_ERROR_CERTIFICATE_DATA_BUFFER_EMPTY = 25,
	GL_ERROR_INVALID_PATH = 26,
	GL_ERROR_ENCRYPTION_PASSWORD_EMPTY = 27,
	GL_ERROR_ENCRYPTION_API_MISSING = 28,
	GL_ERROR_ENCRYPTION_ALG_MISSING = 29,
	GL_ERROR_DECRYPTION_FAILED = 30,
	GL_ERROR_PROVIDER_IN_USE = 31,
	GL_ERROR_CONFIG_FILE_CORRUPTED = 32,
	GL_ERROR_METADATAUPLOAD_FAILED = 33,
	GL_ERROR_CCIFS_CORRUPTED = 34,
	GL_ERROR_FILE_CORRUPTED  = 35,
	GL_ERROR_CCI_FAILED_SRC_DISK_FULL = 36,
	GL_ERROR_CCI_FAILED = 37,
	GL_ERROR_DELETE_FILE_FAILED = 38,
	GL_ERROR_DIRECTORY_ALREADY_EXIST = 100,
	GL_ERROR_FILE_NOT_FOUND = 202,
	GL_ERROR_DRIVE_CREATED = 300,
	GL_ERROR_DRIVE_ALREADY_CREATED = 301,
	GL_ERROR_NO_PROVIDER_AVAILABLE = 400,
	GL_ERROR_NO_MORE_PROVIDER_AVAILABLE = 401,
	GL_ERROR_PROVIDER_NOT_SUPPORTED = 402,
	GL_ERROR_PROVIDER_ALREADY_ADDED = 403,
	GL_ERROR_PROXY_ERROR = 404,
	GL_ERROR_CERTIFICATE_ERROR = 405,
	GL_ERROR_HTTP_ERROR = 406,
	GL_ERROR_ACCESS_DENIED_ERROR = 407,
	GL_ERROR_SERVICE_UNAVAILABLE_ERROR = 408,
	GL_ERROR_SOAP_ERROR = 409,
	GL_ERROR_INTERNAL_ERROR = 410,
	GL_ERROR_COMPRESSION_ERROR = 411,
	GL_ERROR_HTTP_CONNECTION_FAILED = 412,
	GL_ERROR_INVALID_CHUNK_SIZE = 413,
	GL_ERROR_INVALID_PROVIDER_TYPE = 414,
	GL_ERROR_INVALID_BUKET_NAME = 415,
	GL_ERROR_OBJECT_NOT_AVAILABLE = 416,
	GL_ERROR_INVALID_DESTINATION_PATH = 417,
	GL_ERROR_CRYPTOAPI_INITIALISATION_FAILED = 418,
	GL_ERROR_BUCKET_EXISTED = 419,
	GL_ERROR_REQ_TIME_TOOSKEWED = 420,
	GL_ERROR_TOO_MANY_BKTS=421,
	GL_ERROR_CRYPTO_WRONG_ALGTYPE = 422,
	GL_ERROR_CRYPTO_WRONG_CRYPTOAPITYPE = 423,
	GL_ERROR_CRYPTO_WRONG_PWD = 424,
	GL_ERROR_FILE_UNDER_DELETION = 425,
	GL_ERROR_OBJECTNAME_TOOLONG = 426,
	GL_ERROR_OBJECT_VERIFICATION_FAILED = 427,
	GL_ERROR_NOT_ENOUGH_SPACE = 428,
    GL_ERROR_INVALID_STORAGE_KEY = 429,
	GL_ERROR_BUCKET_NOT_EXIST = 432,

	//CGL Internal Errors
	GL_ERROR_SESSION_ENDED = 501,
	GL_ERROR_DEINIT_IN_PROGRESS = 502,
	GL_ERROR_WRONG_VERSION = 503,

	//NTFSPlugin Specific error codes 
	GL_ERROR_PLUGIN_NODISKSPACE = 601,
	GL_ERROR_SESSION_CREDENTIAL_CONFLICT = 602,
	GL_ERROR_BAD_USERNAME = 603,
	GL_ERROR_INVALID_PASSWORD = 604,
	GL_ERROR_NO_NETWORK = 605,
	GL_ERROR_LOGON_FAILURE = 606,

	GL_ERROR_NOT_REGISTERED_WITH_COM = -777,
	GL_ERROR_C__EXCEPTION = -555,
	GL_ERROR_SERIALIZATION_ERROR ,
	GL_ERROR_COM_INITIALIZATION_FAILED
};


/**********************************STRUCT DEFINITIONS************************************************
****************************************************************************************************/

#pragma pack(push, 4)
struct ENCRYPTION_PARAM
{
    enum CCI_E_ENCALG_TYPE enAlgType;
    enum CCI_E_CRYPTOAPI_TYPE cryptoApiType;
};
#pragma pack(pop)

#pragma pack(push, 8)
struct 
CLOUD_FILE
{
	wchar_t relativePathToRoot[1024];
	enum FILE_ACCESS_PERMISSIONS filePermissions;
	__int64 chunkSize;
	__int64 distanceToMove;
	unsigned __int64 newFilePointer;
	enum MOVEMETHOD enumMoveMethod;
	unsigned __int64 numberOfChunks;
	__int64 fileSize;
	__int64 fileSizeOnCloud;
	enum FILE_PROPERTIES fileOptions;
	wchar_t encryptionPassword[1024];
	struct ENCRYPTION_PARAM encryptionParam;
};
#pragma pack(pop)


#pragma pack(push, 4)
struct 
CALLER_CONTEXT_INFO
{
	LPWSTR appName;
	LPWSTR userName;
};
#pragma pack(pop)


	
#pragma pack(push, 4)
struct ARCCloudErrorDetails
{
	wchar_t e_Message[1024];
	enum ARCCLD_ERROR_TYPE level;
	enum GL_ERROR errorID;
};
#pragma pack(pop)


#pragma pack(push, 8)
struct	ARCCLD_DIR_STATISTICS
{
	unsigned __int64 dirCount;
	unsigned __int64 fileCount;
	unsigned __int64 dirSize;
	unsigned __int64 dirSizeOnCloud;
};
#pragma pack(pop)

#pragma pack(push, 4)

	struct 
	CLOUD_VENDOR_INFO
	{
		enum CLOUD_TYPE cldVendorType;
		wchar_t vendorURL[256];
		wchar_t userName[256];
		wchar_t password[256];
		wchar_t certificateName[256];
		BYTE* certDatabuffer;
		long certDataBufferSize;
		wchar_t certificatePwd[256];
		wchar_t rHostName[256];  //used as reserved fields
		wchar_t servicePort[254]; //used as reserved fields 
		UINT   flags;
		wchar_t proxyServerName[256];
		wchar_t proxyPort[256];
		wchar_t proxyUserName[256];
		wchar_t proxyPassword[256];
		wchar_t userDomain[256];
		wchar_t bucketName[256];
		wchar_t vendorDescription[1024];
		wchar_t certificateFilePath[1024];
		BOOL bRRS;
		wchar_t szRegion[256];
		wchar_t ownerName[1024];
	};

    struct CLOUD_CACLOUD_USER_INFO
    {
        wchar_t pwszAccountIndentity[256];
        wchar_t pwszAccountType[256];
        wchar_t pwszDisplayName[256];
        wchar_t pwszEmailAddress[256];
        __int64 i64UsedSizeInByteTotal;
        __int64 i64RemainedSizeInByteTotal;
    };

	#pragma pack(pop)



#pragma pack(push, 8)
struct 
ARCCloudDirectoryMetaEntry
{
	wchar_t name[1024];
	ULONG64 size;
	ULONG64 sizeOnCloud;
	enum ARCCLD_FILE_ATTRIBUTE fileAttrib;
};
#pragma pack(pop)


#pragma pack(push, 8)
struct 
CLOUD_PROVIDER_STATS
{
	wchar_t cloudAccountName[1024];
	unsigned __int64 storedDataSize;
	unsigned __int64 downloadedDataSize;
	unsigned __int64 uploadedDataSize;
    double downloadThroughput;
	double uploadThroughput;
};

#pragma pack(pop)


struct CProvider
{
   enum CLOUD_TYPE cloudType; // CLOUD_PROVIDER_AMAZONS3, CLOUD_PROVIDER_EUCALYPTUS
   wchar_t providerName[255];   //"Amazon S3", "Eucalyptus Walrus"   
};

struct CProviderInformation
{
	wchar_t providerURL[1024];  //http://s3.amazon.com, http://ip:8773/services/walrus };
};

#define DISABLEASYNCDIRUPLOAD 0x01
#define ENABLESYNCFILEDELETE  0x02

struct CCCIParams
{	
	bool bDisableASyncDirUpload;
	unsigned int validValuesBit;
	unsigned int cciParamValues;	
};

typedef struct tagCWriteFileData
{
	unsigned int actualBytesWritten;
	unsigned int actualBytesRead;
}CWriteFileData;

typedef struct tagCReadFileData
{
	unsigned int actualBytesRead;
}CReadFileData;

//Global parameters for CCI
#define GLOBAL_DISABLE_DELETE  0x01  

struct CCIGlobalParamExternal
{
	ULONGLONG validValuesBit;
	BOOL disableDelete;    //For diabling the DeleteManager to start....
};

enum THROTTLE_TYPE
{
	DOWNLOAD,
	UPLOAD
};
typedef struct tagThrottleParam
{
	enum THROTTLE_TYPE type;
	unsigned int value;  //In KB/Secs
}ThrottleParam;

typedef struct _CACLOUDJOBINFO
{
	LONGLONG		jobNumber;
	LONGLONG		jobType;
	LONGLONG		startTime;
	LONGLONG		endTime;
	LONGLONG		sizeUploadInByte;
	LONGLONG		status;
}CACLOUDJOBINFO, *pCACLOUDJOBINFO;

/*
#ifndef _DEFINE_ARCFLASH_LIC_INFO
#define _DEFINE_ARCFLASH_LIC_INFO
typedef struct _ARCFLASH_LIC_INFO
{
	TCHAR			pwszOSName[128];			//OS Name
	BOOL			bAFSupportOS;				//if AF support the OS
	BOOL			bWorkStation;				//work station or not
	BOOL			bServer;					//server machine
	BOOL			bSBS;						//SBS or EBS not
	BOOL			bFoundationServer;			//Foundation server
	BOOL			bStorageServer;				//Storage server
	BOOL			bVMGuest;					//VM guest machine or not
	BOOL			bVMHost;					//VM host machine or not
	BOOL			bAllowBMR;					//Allow BMR
	BOOL			bAllowBMRAlt;				//Allow BMR to alternative location
	BOOL			bProtectSQL;				//protect SQL APP
	BOOL			bProtectEXCH;				//protect Exchange APP
	BOOL            bExchDBRecovery;            //Support restore DB level restore.
	BOOL            bExchGRTRecovery;           //Support Exchange GRT restore.
	BOOL			bProtectHyperVM;			//protect HyperV VM
	BOOL			bWithBLI;					//with BLI license
	BOOL            bEncryption;                //Support encryption.
	BOOL            bManualExport;              //Support export manually
	BOOL            bScheduleExport;            //Support schedule export
	BOOL			bD2D2D;						//Support file copy to local disk

	OSVERSIONINFOEX osvi;						//version info
	SYSTEM_INFO		si;							//system info
}ARCFLASH_LIC_INFO, *PARCFLASH_LIC_INFO;

#endif
*/
#define PROXY_URL_MAX_PATH  1024
#define PROXY_MAX_SIZE      256

typedef struct tagPROXY_AUTO_DETECT
{
	BOOL bAutoDetect;
	int preferenceOrder;
	TCHAR proxyDomain[PROXY_MAX_SIZE];
	TCHAR proxyUserName[PROXY_MAX_SIZE];
	TCHAR proxyUserPwd[PROXY_MAX_SIZE];
}PROXY_AUTO_DETECT;

typedef struct tagPROXY_AUTO_CONFIG_SCRIPT
{
	BOOL bAutoConfigScript;
	int preferenceOrder;
	TCHAR proxyAutoConfigScriptURL[PROXY_URL_MAX_PATH];
	TCHAR proxyDomain[PROXY_MAX_SIZE];
	TCHAR proxyUserName[PROXY_MAX_SIZE];
	TCHAR proxyUserPwd[PROXY_MAX_SIZE];
}PROXY_AUTO_CONFIG_SCRIPT;

typedef struct tagPROXY_MANUAL_SERVER
{
	BOOL bManualProxyServer;
	int preferenceOrder;
	TCHAR proxyServerURL[PROXY_URL_MAX_PATH];
	TCHAR proxyServerPort[PROXY_MAX_SIZE];
	TCHAR proxyDomain[PROXY_MAX_SIZE];
	TCHAR proxyUserName[PROXY_MAX_SIZE];
	TCHAR proxyUserPwd[PROXY_MAX_SIZE];
}PROXY_MANUAL_SERVER;

typedef struct tagPROXY_DETAILS
{
	PROXY_AUTO_DETECT autoDetect;
	PROXY_AUTO_CONFIG_SCRIPT autoConfigScript;
	PROXY_MANUAL_SERVER	manualServer;	
}PROXY_DETAILS;

enum REASONTYPES
{
	REASON_TYPE_UNKNOWN = 0,
	REASON_TYPE_MAX_FILEPATH_LENGTH,
	REASON_TYPE_MAX_FILENAME_LENGTH
};

typedef struct _REASONTYPES
{
	REASONTYPES	reasonType;
	DWORD		reasonBuffer;
}ReasonTypes, *PReasonTypes;