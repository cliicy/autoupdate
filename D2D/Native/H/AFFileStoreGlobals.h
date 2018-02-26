#pragma once
/////////////Standard Included////////////////////
#include <windows.h>
#include <tchar.h>
#include <vector>
#include <list>
#include <map>
#include "AFJob.h"
#include "ArcCloudInterfaceExport.h"
#include "brandname.h"


using namespace std;
///////////Defined Includes///////////////////////////////////////
#define D2DARC_CCI_PROVIDERTOKEN _T("D2DARCH_CCITOKEN")
#define D2DARC_GUID_ROOT _T("D2DGUID")
#define D2DARC_GUID_FILE _T("GUIDFILE.txt")
#define ARCHIVE_FILE_LAST_CATALOG_UPDATE_TIMESTAMP L"LastArchiveCatalogUpdate.dat"
#define MAX_DIR_PATH_SIZE 512  

// for stub file support
#define FILECOPY_STUBFILE_EXT		L".d2darc"
#define FILECOPY_STUBFILE_TYPE_STR	(CST_PRODUCT_NAME_L L" Stubfile")
#define FILECOPY_STUBFILE_VIEW_CMD	L"ArchiveStubFileViewer.exe"
#define FILECOPY_STUBFILE_ICON_PATH	L"\\TrayIcon\\Resource\\Images\\stub-file.ico,0"

// max size of a GUID is 16bytes or 128 bits or 32 characters
// with hypens and braces it comes to 38 characters max
#define GUID_LEN_IN_TCHAR			40

// MAGIC HEADERS for MASTER and META DATA files
#define METADATAFILE_HEADER_MAGIC					34567
#define MASTERMETADATAFILE_HEADER_MAGIC				76543
#define GUIDFILE_HEADER_MAGIC_FC						93456
#define GUIDFILE_HEADER_MAGIC_FA						93457

// we will use this field to figure out what is the version of the product which created this metadata file
// this will be stamped in the mastermetada and filemetadata files (.d2dm and .d2df).
#define FILECOPY_METADATAFILE_VERSION_16			16

#define FILECOPY_METADATAFILE_VERSION_17			17
#define MasterMetadataPrefix	L".D2DM"
#define MetadataPrefix			L".D2DF"

#define AF_FILE_STOR_DLL_NAME L"AFFileStore.dll"

///////////////Importing and exporting////////////
#ifdef _AFFILESTORE_EXPORTS_
#define AFSTOREAPI __declspec(dllexport)
#else
#define AFSTOREAPI __declspec(dllimport)
#endif

//these are windows defined types we just used the same values.
#define AF_BACKUP_INVALID          0x00000000
#define AF_BACKUP_DATA             0x00000001
#define AF_BACKUP_EA_DATA          0x00000002
#define AF_BACKUP_SECURITY_DATA    0x00000003
#define AF_BACKUP_ALTERNATE_DATA   0x00000004
#define AF_BACKUP_LINK             0x00000005
#define AF_BACKUP_PROPERTY_DATA    0x00000006
#define AF_BACKUP_OBJECT_ID        0x00000007
#define AF_BACKUP_REPARSE_DATA     0x00000008
#define AF_BACKUP_SPARSE_BLOCK     0x00000009

//these are ARCHIVER SPECIFIC TYPES
#define AF_BACKUP_EFS_DATA		   0x000000FF 

#define MAXIMUM_STREAM_LENGTH      256 // potvi02: please don't change without revising the structs which use them
#define DEFAULT_CHUNK_SIZE		(1024*1024)	// 1 MB
#define MIN_CHUNK_SIZE_IN_MB	1			// 1 MB
#define MAX_CHUNK_SIZE_IN_MB	100			// 100 MB
/////////////////Typedefs///////////////////////

/////////////////Enums///////////////////////////
enum JOB_TYPE
{
	JOB_TYPE_STORE = 0,
	JOB_TYPE_RESTORE,
};
enum BACKUP_TYPE
{
	BACKUP_TYPE_FULL_AFFILESTORE = 0,
	BACKUP_TYPE_INCR_AFFILESTORE,
};

enum _DESTINATION_ERRORS_
{
	ERROR_DESTINATION_DIRECTORY_CREATION_SUCCESS = 100,
	ERROR_DESTINATION_DIRECTORY_ALREADY_EXISTS,
	ERROR_DESTINATION_DIRECTORY_CREATION_FAILED,
	ERROR_DESTINATION_FILE_CREATION_SUCCESS,
	ERROR_DESTINATION_FILE_ALREADY_EXISTS,
	ERROR_DESTINATION_FILE_CREATION_FAILED,
};
enum FILE_ERROR_CODES
{
	ERROR_FILE_CREATED_OK = 100,
	ERROR_FILE_CREATED_FAIL,
	ERROR_FILE_FOUND_OK,
	ERROR_FILE_FOUND_FAIL,
	ERROR_FILE_WRITE_OK,
	ERROR_FILE_WRITE_FAIL,
	ERROR_FILE_DELETE_OK,
	ERROR_FILE_DELETE_FAIL,
	ERROR_FILE_OPEN_OK,
	ERROR_FILE_OPEN_FAIL,
	ERROR_FILE_READ_OK,
	ERROR_FILE_READ_COMPLETED,
	ERROR_FILE_READ_FAIL,
	ERROR_FILE_SETOFFSET_OK,
	ERROR_FILE_SETOFFSET_FAIL
};
#define MAX_SIZE				         260
#define MAX_BLOCK_ALLOC_UNIT_CHUNK_SIZE  256
#define MAX_CRC							 MAX_PATH
#define MAX_VER_SIZE                     50
#define DATAFILE_HEADER_SIZE			 1024*1024 /* BYTES */
///////////////Global Structures//////////////////////////////////
extern "C" 
{
	/* Cloud Session Init Information, This will be received from the AFArchive.exe */
	typedef struct AFCloudSession
	{
		PARCHIVEDISKDESTINFO pDiskDest;
		PARCHIVECLOUDDESTINFO pCloudDest;
		DWORD dwArchiveDestType; 
		/* Chunk size of the Read/Write Operation using CCI interface*/
		unsigned __int64 m_ui64CHUNKsize;
		/*Add encryption information */ 
		UINT m_IsEncrypted;
		UINT m_IsCompressed;
	}AF_CLOUD_SESSION_INIT_;

	/* AF_CLOUD_SESSION_INIT_ will be converted to _CLOUD_SESSION_INIT_, to make it compatible to CCI interface*/
	typedef struct CloudSession
	{
		struct CLOUD_VENDOR_INFO m_cldVendorInfo;
		TCHAR m_CallerCtxappName[MAX_PATH]; 
		TCHAR m_CallerCtxuserName[MAX_PATH]; 
		/* Chunk size of the Read/Write Operation using CCI interface*/
		unsigned __int64 m_ui64CHUNKsize;
		/*Add encryption information */ 
		UINT m_IsEncrypted;
		UINT m_IsCompressed;
		BOOL m_bIsNetWorkShare;
	}_CLOUD_SESSION_INIT_;

	/* Master MetaData Header */
	typedef struct FMMasterHeader
	{
		DWORD m_dwMagicHeader;
		DWORD m_dwVersion;				// we will use this field to store the product version which creation this meta file
	}_FM_MASTER_HEADER_;

	/* Master MetaData Data Block */
	typedef struct FMMasterBlock
	{
		DWORD    m_dwDataFileVersion;
		DWORD    m_dwStartingVersion;
		DWORD	 m_dwIncrementalVersion;
		TCHAR    m_szSourceFileAbsolutePath[MAX_DIR_PATH_SIZE];
	}_FM_MASTERBLOCK_;
	
	enum ARCHIVE_TYPE
	{
		ARCHIVE_TYPE_FULL = 1,
		ARCHIVE_TYPE_INCR = 2,
	};
	 /* Init Writing */
	typedef struct WriteInit
	{
		/* COPY or ARCHIVE */
		DWORD m_dwBackUpType;
		/* Block size in MBs */
		DWORD m_uiBlockSize;
		/*Only source file Dir */
		TCHAR m_szSourceFileAbsolutePath[MAX_DIR_PATH_SIZE];
		/* Version Number*/
		DWORD m_dwVersionNum;
		DWORD m_dwARChiveType;
	}_WRITE_INIT_;

	typedef struct GUIDHeader
	{
		DWORD       m_dwMagicHeader;
		TCHAR       m_szGUID[GUID_LEN_IN_TCHAR];
	}_GUID_HEADER_;
	/*
	Each Metadata File will have header , 
	so that we can identify we are not referring to a junk file.
	*/
typedef struct FMHeader
{
	DWORD		m_dwMagicHeader;
	DWORD		m_dwVersion;		// we will use this field to store the product version which creation this meta file
	DWORD		m_dwBlockSize;
}_FM_HEADER_;

/* 
this is StandardInfo of the block type ,
probably we should move this to somewhere else ?
*/
enum enum_STANDARD_INFO_
{
	STANDARD_INFO_VERSION = 10,
	STANDARD_INFO_FILESIZE_IN_BYTES,
	STANDARD_INFO_MODIFIED_DATE_TIME,
	STANDARD_INFO_ARCHIVED_TIME,
};
	typedef struct StandardInfo
	{
		/* Version Number*/
		DWORD   m_dwDataFileVersion;
		DWORD   m_dwIncrementalVersion;
		DWORD   m_dwFileSizeInBytesHIGH; 
		DWORD   m_dwFileSizeInBytesLOW; 
		FILETIME  m_stCreationTime;
		FILETIME  m_stLaseAccessTime;
		FILETIME  m_stModifiedDateTime;
		FILETIME  m_stArchivedTime;
		ULONG SegmentNumberLowPart;                                   
		USHORT SegmentNumberHighPart;   	
		/* COPY or ARCHIVE */
		DWORD    m_FileType;
		DWORD   dwFileAttributes;
	}_STANDARD_INFO_;
              

/*Each file is divided to blocks*/
enum enum_FILE_METADATA_BLOCK_HEADER_
{
	FILE_METADATA_BLOCK_HEADER_IS_RESIDENT = 20,
	FILE_METADATA_BLOCK_HEADER_BLOCK_TYPE,
	FILE_METADATA_BLOCK_HEADER_IS_ALTERNATE_DATA_STREAM,
	FILE_METADATA_BLOCK_HEADER_BLOCK_SIZE,
	FILE_METADATA_BLOCK_HEADER_BLOCK_NAME,
};
	typedef struct FMBlockHeader
	{
	   ULONGLONG m_uLDataBlockSizeInBytes;
	   /* is the block data can be stored in the meta data or it could be stored in separate data file?   */
	   BYTE m_IsResident;
	   /* type of the block. i.e. Named Stream, DATA, ACL, Standard information etc ...   */
	   BYTE m_BlockType;
	   /* Padding */
	   BYTE Reserved[2];
	   ULONG ulAttrib;
	TCHAR m_szBlockName[MAXIMUM_STREAM_LENGTH];
	ULONGLONG  m_uLDataBlockValidSize;
	}_FILE_METADATA_BLOCK_HEADER_;

/* 
if the block is non-resident then the data will be 
stored in a separate file.
*/
enum enum_FILE_METADATA_BLOCK_DATA_
{
	FILE_METADATA_BLOCK_DATA_SOURCE_BLOCK_NUM = 30,
	FILE_METADATA_BLOCK_DATA_DEST_BLOCK_NUM ,
	FILE_METADATA_BLOCK_DATA_DEST_DATA_FILE_VER,
};
///* This is to handle Sparse Blocks */
typedef struct BlockInfo
{
	DWORD m_dwBlockNumber;
	/* '0' if it is Sparse, '1' if it is DataBlock */
	__int32 m_BlockType;
}__BLOCK_INFO__;
typedef struct StartBlockWrite
{
	/* This is to handle sparse blocks */
	vector< __BLOCK_INFO__ > m_vecBlocksInfo;
	//vector< DWORD > m_vecBlocksInfo;
}_START_BLOCK_WRITE_;

/* Buffer to be written using CCI interface */
typedef struct BufferInfo
{
	__int64 m_lFileSizeToWriteInBYTES;
	BYTE * m_pBuffer;
	/*VENKATTODO: Need to check whether the DWORD is enough or not */
	DWORD * m_pSourceBlockList;
	/*VENKATTODO: Need to check whether the DWORD is enough or not */
	DWORD m_dwSourceBlockCount;
    //avikh01
	unsigned __int64 m_ulActualBytesWritten;
}_BUFFER_INFO_;
typedef struct FMBlockData
{
   /* block number in the source file.   */
   DWORD m_dwSourceBlockNumber; 
   /* block number in the destination files.*/   
   DWORD m_dwDestinationBlockNumber;
   /* Continuous Block Number */
   DWORD m_dwEndBlockNumber;
   /* 
   version of the destination data file, i.e. 
   we use this number to get the name of the data file.
   */
   USHORT m_DestinationDataFileVersion;     
                                                                          
}_FILE_METADATA_BLOCK_DATA_;
   
/* Each Block */
typedef struct FMBlockInfo
{
_FILE_METADATA_BLOCK_HEADER_ m_stMetaDataBlockHeader;
_FILE_METADATA_BLOCK_DATA_ m_stMetaDataBlockData;
}_FILE_METADATA_BLOCK_Info;

/* Each Meta Data File */
typedef struct FileMetaData
{
	_FM_HEADER_        m_stFileMetaDataHeader;
	_STANDARD_INFO_    m_stStandardInfo;
	vector< _FILE_METADATA_BLOCK_Info * > m_vecMetaDataBlocks;
}_FILE_METADATA_;

//avikh01
typedef struct _METADATAJOBRESULT
{
	ULONGLONG actualSizeWrittenToCloud;
}MetadataJobResult, *PMetadataJobResult;

}
//////////////Global Vaiables/////////////////////////////////

//////////////Exported Functions/////////////////////////////
/* This should be called before Archiving/Copying/Restoring */
extern "C" AFSTOREAPI void SetArchiveCatalogGUIDFolder(wstring& wsCatalogGUIDFolder);
extern "C" AFSTOREAPI BOOL InitCloudSession(AF_CLOUD_SESSION_INIT_ & in_stCloudSessionInfo,AFARCHIVEJOBSCRIPT * in_pAFARCHIVEJOBSCRIPT, DWORD &dwCCIRetCode);
extern "C" AFSTOREAPI BOOL InitCloudSessionEx(PAFARCHIVEJOBSCRIPT pAFjobscript, DWORD &dwCCIRetCode);
/* This should be called after all the files are Archived/Copied/Restored */
extern "C" AFSTOREAPI BOOL CloseCloudSession();

/////////////File Restore //////////////////////////
/*KEY is Offset of the BlockHeader, Value is the is the BlockHeaderPointer */
typedef struct BlockHeaderMapValue
{
	ULONGLONG m_ullOffSet;
	_FILE_METADATA_BLOCK_HEADER_ * m_p_FILE_METADATA_BLOCK_HEADER_;

}_BLOCK_HEADER_MAP_VALUE_;

typedef std::vector< _BLOCK_HEADER_MAP_VALUE_ * > FileMetaDataBlockHeadersVec;

extern "C" AFSTOREAPI LONG ValidateBucket (PARCHIVECLOUDDESTINFO pARCHCloudDestInfo, BOOL &bValid);
extern "C" AFSTOREAPI LONG CreateBucket(PARCHIVECLOUDDESTINFO pARCHCloudDestInfo);
extern "C" AFSTOREAPI LONG GetBucketList (PARCHIVECLOUDDESTINFO pARCHCloudDestInfo, LPWSTR** listOfContainers, long* itemCount);
extern "C" AFSTOREAPI LONG FreeBucketList (LPWSTR** listOfContainers, long itemCount);
extern "C" AFSTOREAPI LONG GetRegionList (PARCHIVECLOUDDESTINFO pARCHCloudDestInfo, LPWSTR** listOfRegions, long * itemCount);
extern "C" AFSTOREAPI LONG TestCloudConnection(PARCHIVECLOUDDESTINFO pARCHCloudDestInfo, int nProductType);
extern "C" AFSTOREAPI LONG GetRegionForBucket (PARCHIVECLOUDDESTINFO pARCHCloudDestInfo, LPWSTR * szRegion);
extern "C" AFSTOREAPI LONG FreeRegionForBucket (LPWSTR szRegion);
// define function interfaces for async metdata upload thread
extern "C" AFSTOREAPI BOOL InitAsyncMetdataUploadMgr(wstring& szLocalCatalogPath);
extern "C" AFSTOREAPI BOOL DeInitAsyncMetdataUploadMgr();
//avikh01
//extern "C" AFSTOREAPI BOOL WaitForAsyncMetadataUploadThread();
extern "C" AFSTOREAPI BOOL WaitForAsyncMetadataUploadThread(MetadataJobResult* metadataJobResult);

extern "C" AFSTOREAPI BOOL SignalAsyncMetadataUploadThread (DWORD dwStatus);
extern "C" AFSTOREAPI BOOL ProcessAllPendingMetadataFileRecords ();
extern "C" AFSTOREAPI BOOL AFSTOR_DeInitCCI();

extern "C" AFSTOREAPI  DWORD AFCatalogSync(PTCHAR szD2DHomePath,
																wstring szHostName, 
																BOOL bAlternateLocation, 
																BOOL bRecursive, 
																DWORD jobNumber, 
																wstring  szLocalCatalogRootPath,
																wstring szCCICatalogPath);
enum ASYNCTHREAD_MSGS{
	ASYNCTHREAD_MSG_DEFAULT = 0,			// this will be the default value
	ASYNCTHREAD_MSG_NEW_REC_ADDED = 1,		// this will be signalled when the archive job has processed one new entry
	ASYNCTHREAD_MSG_JOB_CANCELLED = 2,		// this will be signalled when the archive job has received a CANCEL job request
	ASYNCTHREAD_MSG_JOB_COMPLETE = 3,		// this will be signalled when the archive job has completed processing all the files and awaiting the async thread
	ASYNCTHREAD_MSG_SEVERE_ERROR = 4		// this will be signalled when the archive job would have encountered a severe error
};

extern "C" AFSTOREAPI BOOL UpdateLastArchiveCatalogUpdateTime(wstring& szHostName);
extern "C" AFSTOREAPI BOOL GetLastArchiveCatalogUpdateTime(wstring &szHostName, FILETIME &stLastUpdateTimeStamp, DWORD &dwStatus);
extern "C" AFSTOREAPI BOOL GetGUIDFromArchiveDest(wstring& szDestGUID);
extern "C" AFSTOREAPI BOOL UpdateLastArchiveCatalogUpdateTimeInDestination(wstring& szHostName);




// declarations to support CCI destination encryption
#define ARCHIVE_FILE_DESTINATION_ENCRYPTION_SETTINGS	L"FilecopyEncryptionSettings.dat"
extern "C" AFSTOREAPI LONG IsFileCopyDestinationInitedForEncryption(DWORD& dwCCIRetCode, BOOL& bDestInited, BOOL& bDestEncrypted);
extern "C" AFSTOREAPI LONG InitFileCopyDestinationForEncryption(PTCHAR szComputerName, DWORD& dwCCIRetCode, BOOL& bDestEncrypted, wstring& szEncrPassword, DWORD enAlgType, DWORD cryptoApiType);
extern "C" AFSTOREAPI LONG ValidateFileCopyDestEncrPassword(wstring& szEncrPassword, DWORD& dwCCIRetCode, DWORD enAlgType, DWORD cryptoApiType);
extern "C" AFSTOREAPI LONG AFCCICloudDisableASYNCMODE(bool bASYNCMode);
extern "C" AFSTOREAPI LONG AFCCIIsFilePathValid(wstring& filePath, BOOL& bIsValid, ReasonTypes &reason);
extern "C" AFSTOREAPI DWORD GetJobDestination();
extern "C" AFSTOREAPI DWORD GetArchChunkSize();

class ICloudAPIs
{
public:
	ICloudAPIs() {}
	virtual ~ICloudAPIs() {}

	virtual BOOL InitCloudSession(AF_CLOUD_SESSION_INIT_ & in_stCloudSessionInfo, AFARCHIVEJOBSCRIPT * in_pAFARCHIVEJOBSCRIPT, DWORD &dwCCIRetCode, HANDLE& handle)=0;
	virtual BOOL InitCloudSessionEx(PAFARCHIVEJOBSCRIPT pAFjobscript, DWORD &dwCCIRetCode, HANDLE& handle, bool bDontCreateBucket) = 0;
	virtual BOOL CloseCloudSession(HANDLE handle)=0;
	virtual BOOL CheckForNwShareWritabe(HANDLE handle, AFARCHIVEJOBSCRIPT * pAFjobscript)=0;
	virtual LONG ValidateBucket(PARCHIVECLOUDDESTINFO pARCHCloudDestInfo, BOOL &bValid)=0;
	virtual LONG CreateBucket(PARCHIVECLOUDDESTINFO pARCHCloudDestInfo)=0;
	virtual LONG GetBucketList(PARCHIVECLOUDDESTINFO pARCHCloudDestInfo, LPWSTR** listOfContainers, long* itemCount)=0;
	virtual LONG GetRegionForBucket( PARCHIVECLOUDDESTINFO pARCHCloudDestInfo, LPWSTR * szRegion)=0;
	virtual LONG GetRegionList(PARCHIVECLOUDDESTINFO pARCHCloudDestInfo, LPWSTR** listOfRegions, long * itemCount)=0;
	virtual LONG TestCloudConnection(PARCHIVECLOUDDESTINFO pARCHCloudDestInfo)=0;

	virtual BOOL GetLastArchiveCatalogUpdateTime(HANDLE handle, wstring &pCatalogBaseDirPath, wstring &szHostName, FILETIME &stLastUpdateTimeStamp, DWORD &dwStatus) = 0;
	virtual BOOL PrepareArchiveCatalogUpdateFilePath(HANDLE handle, wstring &szArchiveCatalogUpdateFilePath, wstring &szHostName)=0;
	virtual BOOL GetGUIDFromArchiveDest(HANDLE handle, wstring& szDestGUID)=0;
	virtual BOOL AFSTOR_DeInitCCI(HANDLE handle)=0;

	virtual LONG IsFileCopyDestinationInitedForEncryption(HANDLE handle, DWORD& dwCCIRetCode, BOOL& bDestInited, BOOL& bDestEncrypted)=0;
	virtual BOOL GetCCIHostName(HANDLE handle, wstring& szDestHostName)=0;

	virtual LONG InitFileCopyDestinationForEncryption(HANDLE handle, PTCHAR szComputerName, DWORD& dwCCIRetCode, BOOL& bDestEncrypted, wstring& szEncrPassword, DWORD enAlgType, DWORD cryptoApiType) = 0;
	virtual LONG ValidateFileCopyDestEncrPassword(HANDLE handle, wstring& szEncrPassword, DWORD& dwCCIRetCode, DWORD enAlgType, DWORD cryptoApiType)=0;
	virtual LONG AFCCICloudDisableASYNCMODE(HANDLE handle, bool bASYNCMode)=0;

	virtual LONG FreeRegionForBucket(LPWSTR szRegion)=0;
	virtual LONG FreeBucketList(LPWSTR** listOfContainers, long itemCount)=0;

	virtual void Release()=0;
	virtual void Release(HANDLE handle)=0;
};

extern "C" AFSTOREAPI ICloudAPIs* CreateCldAPIsPtr();
