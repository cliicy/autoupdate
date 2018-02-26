#ifndef _HA_COMMON_DEF_H_
#define _HA_COMMON_DEF_H_
#pragma once
#include "HATransErrorCode.h"
#include "brandname.h"
#include "hatransjobscript.h"
#include <stdlib.h>
#pragma warning(push, 3)
#include <WinSock2.h>
#pragma warning(pop)

//default port
#define HA_DEFAULT_PORT         4090
#define HA_DEFAULT_MAX_CONN     128 //max concurrent connection number

#define WRITE_SEND_SIZE_DEFAULT 2*1024*1024  // 2M
#define WRITE_SEND_SIZE_MAX     2*1024*1024  // 2M

#define DR_SEND_RECEIVE_SIZE    2*1024*1024  // 2M

#define BUFFER_SIZE_16K         16*1024

#define THUMBPRINT_LENGTH 60	//add by zhepa02 at 2015-05-25

enum
{
    VDDK_MODE_UNKNOWN  = 0,
    VDDK_MODE_SAN      = 1,
    VDDK_MODE_HOTADD   = 2,
    VDDK_MODE_NBD      = 3,
};

typedef enum _e_hadt_trans_type
{
    HADT_SHARE_FOLDER = 0,
    HADT_SOCKET = 1,
} HADT_TRANS_TYPE;

/*
RESUME_COV_RESTART_OBJ is add for resume of VHD format conversion, this means that 
the position to resume is returned by ST_JOB_RESTART_INFO.

remove RESUME_COV_RESTART_OBJ for multistore support.
*/
enum 
{
    RESUME_RESTART    = 0, 
    RESUME_CHECKDATA  = 1, 
    RESUME_POS        = 2, 
    RESUME_SKIP       = 3, 
};


//////////////////////////////////////////////////////////////////////////
//
//the definition for transfer
//
//////////////////////////////////////////////////////////////////////////


//
//the definition for ulCommand in ST_PACKAGE_HEADER
//
#define CMD_UNDEFINED               0x00000000

#define CMD_C_AUTH_USER		        0x00000001 // client ==> server
#define CMD_S_AUTH_OK               0x00000002 // server ==> client

#define CMD_C_JOB_START             0x00000003 // client ==> server
#define CMD_S_JOB_START_OK          0x00000004 // server ==> client

#define CMD_C_FILE_HEADER           0x00000005 // client ==> server
#define CMD_S_FILE_HEADER_OK        0x00000006 // server ==> client

#define CMD_C_FILE_DATA             0x00000007 // client ==> server
#define CMD_S_FILE_DATA_OK          0x00000008 // server ==> client

#define CMD_C_FILE_FINISHED         0x00000009 // client ==> server
#define CMD_C_FILE_FAILED           0x0000000B // client ==> server

#define CMD_C_JOB_ABORT             0x0000000D // client ==> server
#define CMD_S_JOB_EXCEPTION         0x0000000E // server ==> client

#define CMD_C_JOB_FINISHED          0x0000000F // client ==> server
#define CMD_C_JOB_FAILED            0x00000011 // client ==> server

#define CMD_S_SRVDOWN               0x00000012 // server ==> client

#define CMD_S_RESUME_INFO           0x00000014
#define CMD_C_VALIDATE_CODE         0x00000015

#define CMD_C_JOB_RECONNECT         0x00000017 // client ==> server
#define CMD_S_JOB_RECONNECT_OK      0x00000018

#define CMD_S_SRV_STOP_JOB          0x0000001A // server ==> client

#define CMD_C_D2D_GUID              0x0000001B
#define CMD_S_D2D_GUID              0x0000001C


#define CMD_C_LASTSESS_GUID         0x0000001D //client ==> server, get the last sess GUID
#define CMD_S_LASTSESS_GUID         0x0000001E

#define CMD_C_DELDIR                0x0000001F //client ==> server, delete directory
#define CMD_C_DELFILE               0x00000020 //client ==> server, delete file. currently no one use it.

#define CMD_S_VDDKMODE              0x00000021

//<< for test
#define CMD_C_DOWNLOAD              0x00000025
#define CMD_S_TOTALSIZE             0x00000026
#define CMD_S_DOWNLOAD              0x00000027
// for test>>

//<< for DR
#define CMD_DR_C_DISK_OPEN          0x00000040
#define CMD_DR_C_DISK_CLOSE         0x00000041
#define CMD_DR_C_DISK_TOTALSECTOR   0x00000042
#define CMD_DR_S_DISK_TOTALSECTOR   0x00000043
#define CMD_DR_C_DISK_SECTORSIZE    0x00000044
#define CMD_DR_S_DISK_SECTORSIZE    0x00000045
#define CMD_DR_C_DISK_BITMAP        0x00000046
#define CMD_DR_S_DISK_BITMAP        0x00000047
#define CMD_DR_C_DISK_DATA          0x00000048
#define CMD_DR_S_DISK_DATA          0x00000049
#define CMD_DR_C_DISK_DATA_EX       0x00000050      // For ReadEx
#define CMD_DR_S_DISK_DATA_EX       0x00000051      // For ReadEx

#define CMD_DR_C_VOL_DISKINFO       0x00000052      // Get volume's disk info
#define CMD_DR_S_VOL_DISKINFO       0x00000053
#define CMD_DR_C_VOL_OPEN           0x00000054
#define CMD_DR_C_VOL_CLOSE          0x00000055
#define CMD_DR_C_VOL_BITMAP         0x00000056
#define CMD_DR_S_VOL_BITMAP         0x00000057


#define CMD_DR_C_DRINFO_NOW         0x0000005C      // Get DR info xml for snapshot "NOW"

#define CMD_DR_S_S_VM_DRINFO_NOW    0x0000005D      // server=>VM server, get DR info for snap "NOW"

#define CMD_DR_C_VOLDIFF_OPEN       0x0000005E
#define CMD_DR_C_VOLDIFF_CLOSE      0x0000005F
#define CMD_DR_C_VOLDIFF_BITMAP     0x00000060
#define CMD_DR_S_VOLDIFF_BITMAP     0x00000061
#define CMD_DR_C_DRINFO_SESS        0x00000062      // Get DR info xml for the snapshot based on AF session.
#define CMD_DR_C_DRINFO_DRZ_SESS    0x00000063      // Get AdrInfoC.drz for the snapshot based on AF session.


#define CMD_C_SUBROOT               0x00000080
#define CMD_S_SUBROOT               0x00000081
#define CMD_C_FILE_CANCELED         0x00000082




// for DR>>

#define CMD_S_RET                   0x000000FF


#define CMD_MAX                     0x000000FF //this command should be changed if define new cmd

enum SnapshotType
{
    SnapType_Sess = 0,
    SnapType_Now = 1,
	SnapType_Drz_Sess = 2
};


//
//The definition for ulStat in ST_PACKAGE_HEADER
//
#define STAT_S_JOB_START_NEWOBJ             0x00000001 //server==>client for CMD_S_JOB_START
#define STAT_S_JOB_START_RESUMEOBJ          0x00000002 //server==>client for CMD_S_JOB_START
#define STAT_S_JOB_START_SKIPOBJ            0x00000003 //server==>client for CMD_S_JOB_START

#define STAT_C_JOB_START_CONVFORMAT         0x00000001 //client==>server for CMD_C_JOB_START
#define STAT_C_JOB_START_VHD2VMDK           0x00000002 //client==>server for VHD2VMDK

#define STAT_DR_C_DISK_OPEN_SNAPSHOT        0x00000001 //client==>server for CMD_DR_C_DISK_OPEN
#define STAT_DR_C_DISK_OPEN_STANDARDVHD     0x00000002 //client==>server for CMD_DR_C_DISK_OPEN

//////////////////////////////////////////////////////////////////////////
//
//the structure for transfer
//
//////////////////////////////////////////////////////////////////////////
#pragma pack(push, 1)

typedef struct _t_package_header
{
    unsigned long ulCommand;
    unsigned long ulStat;
    unsigned long ulPackageSize;
}ST_PACKAGE_HEADER, *PST_PACKAGE_HEADER;

typedef struct _t_auth_info
{
    wchar_t wszUsername[64];
    wchar_t wszPassword[64];
} ST_AUTH_INFO, *PSTAUTH_INFO;


typedef struct _t_vmdksession_info
{
    wchar_t pwszEsxHostName[255];       
    wchar_t pwszEsxUserName[255];
    wchar_t pwszEsxPassword[255];
	wchar_t pwszMoref[255];
	unsigned long ulVDDKPort;
	wchar_t pwszSnapshotID[255];
	wchar_t pwszEsxThumbprint[THUMBPRINT_LENGTH];		//add by zhepa02 at 2015-05-25
} VMDKSESSINFO, *PVMDKSESSIONINFO;

#define SESSFLAG_FIRST_INLINK   0x00000001
#define SESSFLAG_SMART_COPY     0x00000002
typedef struct _t_job_header
{
    unsigned long ulVer;
    unsigned long ulSubType;    //the job's subtype
    wchar_t wszJobID[64];       //job's UUID
    wchar_t wszRootPath[260];   //session's dest root path,include the node name, such as D:\dest\ARC
    wchar_t wszLastDesRoot[260];   //the last root path on the destination side.
    wchar_t wszSessGUID[64];    //source session GUID.
    long    nSessNum;           //session number, if not a session, this is 0
    unsigned long ulSessFlag;   //Flag to indicate the session's attributes
    BOOL    bOverwrite;         //clear the existing session to zero, transfer from zero.
    BOOL    bSessNeedConv;
    USHORT  usCmprsAlgOnWire;
    USHORT  usCryptAlgOnWire;
    ST_AUTH_INFO stAuthInfo;    //authenticate 
	BOOL	bSessNeedVmdkConv;
	VMDKSESSINFO VmdkSessInfo;
	long lJobID;
	wchar_t wszInstID[100];  // node guid
}ST_JOB_HEADER, *PST_JOB_HEADER;

typedef struct _t_file_time 
{
    unsigned long dwLowDateTime;
    unsigned long dwHighDateTime;
} ST_FILE_TIME, *PST_FILE_TIME;

//
//the property of VHD disk
//
typedef struct _t_vhd_prop
{
    ULONG diskType; //VHDISK_FULL, VHDISK_INCR
    ULONG diskID;
    LARGE_INTEGER diskSize;
    DWORD dwCmprsLvl;
    GUID  uniqueID;
} ST_VHD_PROP, *PST_VHD_PROP;

typedef struct _t_file_header
{
    unsigned long ulVer;
    wchar_t wszFilePath[512];
    unsigned long ulAttr;
    ST_FILE_TIME ftCreateTime;
    ST_FILE_TIME ftLastAccessTime;
    ST_FILE_TIME ftLastWriteTime;
    unsigned long ulFileSizeLow;
    unsigned long ulFileSizeHigh;
    wchar_t wszPassword[64];
    unsigned char chFileType;       //dir or file
    unsigned char chResumeTransfer; //if resume transfer, if not, always transfer from 0
    unsigned char chCtlFlag;        //The Control flag of transfer
    unsigned char reserved[5];
    unsigned long ulVmfsBlockSize;  //The VMFS block size of ESX
	wchar_t wszVMKDUrl[255];
    //below is for VHD format
    DWORD ulVHDFormat;        //destination VHD format, //{UNKNOWN = 0, VHD_PLAIN = 0x01, VHD_COMPRESS = 0x02}
    ST_VHD_PROP vhdProp; //the property of the current VHD (if this is a vhd file)
} ST_FILE_HEADER,*PST_FILE_HEADER;


typedef struct _t_resume_info
{
    unsigned long ulType; //RESTART=0, CHECKDATA=1, RESUMEPOS=2, RESUME_SKIP = 3
    unsigned long ulPosType;
    unsigned long ulPosLow;
    unsigned long ulPosHigh;
    unsigned long ulLen2Validate; //the length of raw data to generate validate code, default is 4K
} ST_RESUME_INFO, *PST_RESUME_INFO;

typedef struct _t_validate_code
{
    char hashdata[16];
} ST_VALIDATE_CODE, *PST_VALIDATE_CODE;

typedef struct _t_data_range
{
    unsigned long ulOffsetHigh;
    unsigned long ulOffsetLow;
    unsigned long ulLen;
} ST_DATA_RANGE, *PST_DATA_RANGE;

#define WRITE_REQ_FLAG_FINAL    0x00000001
typedef struct _t_write_req_hdr
{
    unsigned long ulFlag;
    unsigned long ulLenOnWire;        //the length of data on wire, which is sent follow this header.
    unsigned long ulRangeCnt;
    struct _t_data_range stRange[1];
} ST_WRITE_REQ_HDR, *PST_WRITE_REQ_HDR;

typedef struct _t_prejob_info
{
    wchar_t wszProductNode[64];
    wchar_t wszSrcSessGuid[64];
    wchar_t wszLastRoot[260];
    long    nSessNum;
    unsigned long    ulSessFlag; //SESSFLAG_FIRST_INLINK
    long    nListLen;           //length of wszDesRootList, in words
    wchar_t wszDesRootList[1];
} ST_PREJOB_INFO, *PST_PREJOB_INFO;

typedef struct _t_dr_snapshot
{
    wchar_t wszVmGuid[64];
    wchar_t wszSnapGuid[64];
} ST_DR_SNAPSHOT_ID, *PST_DR_SNAPSHOT_ID;

typedef struct _t_dr_read_range
{
    unsigned long ulStartLow;
    unsigned long ulStartHigh;
    unsigned long ulLen;           // This should be length
} ST_DR_DATARANGE, *PST_DR_DATARANGE;

typedef struct _t_dr_disk_bitmap
{
    LARGE_INTEGER StartingSec;  // Align with 8
    LARGE_INTEGER BitmapSize;   // Count of sectors
    BYTE Buffer[1];
} ST_DR_DISK_BITMAP, *PST_DR_DISK_BITMAP;

#define DATA_EX_FLAG_FINAL 0x0001     // the final for the input range

typedef struct _t_dr_data_ex
{
    union
    {
        struct
        {
            unsigned short type;     // type of this header
            unsigned short flag;     //
            unsigned long nErrCode;  // error code for this buffer
            unsigned long nSecCnt;   // count of sectors in this buffer
            __int64 llSecBegin;
        } header;
        char padding[512];
    };
    char buffer[0];

} ST_DR_DATA_EX, PST_DR_DATA_EX;


// var for dr volume

#define DR_VOL_FLAG_SNAPSHOT 0x00000001  // IF read FULL image (both parent and child) or only child
#define DR_VOL_FLAG_NO_CTF   0x00000002  // Can NOT get volume bitmap from CTF file

typedef struct _t_dr_vol_iden_ex
{
    wchar_t wszVmGuid[64];               // Virtual machine's GUID
    wchar_t wszSnapshotGuid[64];         // snapshot's GUID
    wchar_t wszVolGuid[64];              // Volume GUID
    unsigned long ulFlag;                //
} ST_DR_VOL_IDEN_EX, *PST_DR_VOL_IDEN_EX;

// For volume diff
typedef struct _t_dr_voldiff_iden
{
    wchar_t wszVmGuid[64];               // Virtual machine's GUID
    wchar_t wszBaseSnapGuid[64];         // base snapshot's GUID
    wchar_t wszCurrSnapGuid[64];         // current snapshot's GUID
    wchar_t wszVolGuid[64];              // Volume GUID
    unsigned long ulFlag;                //
} ST_DR_VOLDIFF_IDEN, *PST_DR_VOLDIFF_IDEN;

typedef struct _t_dr_vol_disk_info 
{
    WCHAR       wszVDiskName[MAX_PATH];  // virtual disk file name for this extent
    ULONG       ulDiskNum;               // BACKUP_BLI, disk number
    ULONG       ulSignature;             // disk signature
    LONGLONG    ulExtentStartPos;        // The offset from the beginning of the disk to 
                                         // the extent, in bytes.
    LONGLONG    ulExtentLength;          // The number of bytes in this extent
    LONGLONG    ulVolumeOffset;          // Volume offset corresponding to the start of 
                                         // this extent (ulExtentStartPos)
    ULONG       ulSectorSize;            // Size of every sector on the disk. VMware & MSFT 
                                         // virtual solutions have it fixed to 512 bytes, kept for 
                                         // future compatibility.
    char        chPadding[4];
}ST_DR_VOL_DISK_INFO, *PST_DR_VOL_DISK_INFO;

#define VOL_ATTR_FLAG_NOTBACKUP 0x00000001
typedef struct _t_dr_vol_attr 
{
    WCHAR wszVolGuid[64];               // volume ID
    WCHAR wszVolName[MAX_PATH];         // drive name or mount point
    ULONG ulOptions;                    // formatted, mountpoint, etc
    ULONG ulFlag;                       // VOL_ATTR_FLAG_NOTBACKUP, etc
    ULONG ulNumOfDisks;                 // number of disks 
    ST_DR_VOL_DISK_INFO vol_disks[1];
}ST_DR_VOL_ATTR, *PST_DR_VOL_ATTR;

typedef struct _t_dr_vol_bitmap
{
    LARGE_INTEGER StartingLcn;  // Align with 8
    LARGE_INTEGER BitmapSize;   // Count of clusters
    BYTE Buffer[1];
} ST_DR_VOL_BITMAP, *PST_DR_VOL_BITMAP;

#pragma pack(pop)




//////////////////////////////////////////////////////////////////////////
//
//the struct we inner use at client and server side
//
//////////////////////////////////////////////////////////////////////////
typedef enum _e_file_item_type
{
    HAFT_UNDEFINED = 0,
    HAFT_FILE = 1,
    HAFT_DIR = 2,
} HA_FILE_ITEM_TYPE;


//
//The bit definition of chCtlFlag in ST_FILE_HEADER
//
#define TRANS_CTL_SPARSE_AS_NORMAL 0x01             //treat sparse file as normal file
#define TRANS_CTL_RELINK_VHD_PARENT 0x02            //explicitly re-link VHD parent path
#define TRANS_CTL_NEED_CONVERT_FORMAT 0x04          //need to convert the VHD format
#define TRANS_CTL_RELINK_VHDX_PARENT 0x08           //explicitly re-link VHDX parent path
#define TRANS_CTL_NEED_CONVERT_FORMAT_VHDX 0x10     //need to convert the VHDX format

//
//compress alg
//
enum {HA_CMPRS_NONE = 0, HA_CMPRS_ZLIB = 1,};

//
//encrypt alg
//
enum {HA_CRYPT_NONE = 0, HA_CRYPT_RC4 = 1, HA_CRYPT_AES128 = 2,};

//
//The VHD disk type
//
enum {VHDISK_FULL = 0x01, VHDISK_INCR = 0x02};

//
// The Registry KEY and VALUE
//
#define MAX_CLI_CACHE_BLOCKSIZE_KB      WRITE_SEND_SIZE_MAX/1024
#define MIN_CLI_CACHE_BLOCKSIZE_KB      32
#define MAX_CLI_CACHE_BLOCKCOUNT        128
#define MIN_CLI_CACHE_BLOCKCOUNT        2

#define REG_KEY_ARC_OFFLINECOPY     CST_REG_HEADER_L L"\\" CST_PRODUCT_REG_NAME_L L"\\OfflineCopy"

static const wchar_t* SC_SESS_BITMAP   = L"VCM-bitmap";
static const wchar_t* ADRCONFIG     = L"AdrConfigure";
static const wchar_t* BACKUP_INFO_XML = L"BackupInfo.XML";
static const wchar_t* JS_XML      = L"JS*_*.xml"; //<huvfe01>2012-11-18
static const wchar_t* SESS_FORMAT = L"\\S%010d";
static const wchar_t* VHD_POSTFIX = L".D2D";
static const wchar_t* XML_POSTFIX = L".xml";
static const wchar_t* DRZ_POSTFIX = L".drz";
static const wchar_t* CTF_POSTFIX = L".ctf";
static const wchar_t* DSC_POSTFIX = L".dsc";
static const wchar_t* MTA_POSTFIX = L".mta";
static const wchar_t* IDX_POSTFIX = L".idx";
static const wchar_t* BITMAP_POSTFIX = L".bitmap";

static const wchar_t* VHD_CATALOG              = L"Catalog";
static const wchar_t* VHD_DATA_FOLDER          = L"VStore";
static const wchar_t* VHD_NAME_PATTERN         = L"disk%010u.D2D";
static const wchar_t* VHD_NAME_ONLY_WITHDOT    = L"disk%010u.";
static const wchar_t* VHD_NAME_NOPOSTFIX       = L"disk%010u";
static const wchar_t* VHD_AFSESS_BLOCK1        = L"block0000000001.ctf";
static const wchar_t* VHD_AFSESS_BLOCK2        = L"block0000000002.ctf";
static const wchar_t* VHD_AFSESS_DRCFGXML      = L"AdrConfigure.xml";
static const wchar_t* VHD_AFSESS_ADRINFOCDRZ   = L"AdrInfoC.drz";
static const wchar_t* VHD_AFSESS_DRCFGXML_NOW  = L"AdrConfigure_now.xml";

static const wchar_t* DLL_AFCOREINTERFACE           = L"AFCoreInterface.dll";
static const wchar_t* DLL_REPOSITORYKIT             = L"HARepositoryKit.dll";
static const wchar_t* DLL_HACOMMONFUNC              = L"HACommonFunc.dll";
static const char* FNNAME_AFDecryptString           = "AFDecryptString";
static const char* FNNAME_CreateRepositoryKit       = "CreateRepositoryKit";
static const char* FNNAME_ReadCopyInfoXml           = "ReadOfflineCopyInfoXml";
static const char* FNNAME_WriteCopyInfoXml          = "WriteOfflineCopyInfoXml";
static const wchar_t* DLL_VHDCONV                   = L"HaVhdUtility.dll";
static const wchar_t* COPY_INFO_XML                 = L"OfflineCopyInfo.XML";
static const wchar_t* CA_AVHD_GUID                  = L"00000000-0000-0000-0000-000000000000";


//
//definition for interaction between Proxy.dll and exe
//
static const wchar_t* s_wszTransServerAppName       = L"HATransServer.exe";
static const wchar_t* s_wszNewMsgComing_SRV         = L"HADT_NewMsgComing_SERVER";
static const wchar_t* s_wszNewMsgSpaced_SRV         = L"HADT_NewMsgSpaced_SERVER";
static const wchar_t* s_wszShmName_SRV              = L"HADT_SHM_SRV_AB131DC9-58F5-4263-BC03-571434038EFD";

static const wchar_t* s_wszNewMsgComing_CLI         = L"HADT_NewMsgComing_CLIENT";
static const wchar_t* s_wszNewMsgSpaced_CLI         = L"HADT_NewMsgSpaced_CLIENT";
static const wchar_t* s_wszTransClientAppName       = L"HATransClient.exe";
static const wchar_t* s_wszShmName_CLI              = L"HADT_SHM_CLI_EE1A9116-D749-4a82-AAE3-F7CC2398692F";

//
//definition for interaction function call between Proxy.dll and exe
static const wchar_t* s_wszNewFnCallComing_SRV      = L"HADT_FnCallNewComing_SERVER";
static const wchar_t* s_wszFnCallFinish_SRV         = L"HADT_FnCallFinish_SERVER";
static const wchar_t* s_wszShmName_FNC_SRV          = L"SHM_FNC_SRV_9DB90835-CEB6-498d-A237-9C09949E9A75";

static const wchar_t* s_wszStopServerEvent          = L"HADT_StopServerEvent_SERVER"; //<huvfe01>2012-11-10 for defect#126017




// For multi_datastore support
#define SIG_HEADER_ROOT         L"header_root.ini"
#define SIG_LEAF_ROOT           L"leaf_root.ini"
#define PROFILE_APP_COUNT       L"LEAF COUNT"
#define PROFILE_APP_HEADERLOC   L"HEADER LOCATION"
#define PROFILE_APP_LEAFLOC     L"LEAF LOCATION"
#define PROFILE_KEY_COUNT       L"Count"
#define PROFILE_KEY_HEADER      L"Header"




//================================================
// Method for ntoh and hton
//================================================
namespace HADT
{

    inline bool IsLittleEndian()
    {
        unsigned long a = 0x10000000;
        return *(char*)(&a) == 0;
    }

    inline unsigned __int64 Hton(unsigned __int64 llIn)
    {
        if (IsLittleEndian())
        {
            unsigned __int64 llOut = 0;

            ((char *) &llOut)[0] = ((char *) &llIn)[7];
            ((char *) &llOut)[1] = ((char *) &llIn)[6];
            ((char *) &llOut)[2] = ((char *) &llIn)[5];
            ((char *) &llOut)[3] = ((char *) &llIn)[4];
            ((char *) &llOut)[4] = ((char *) &llIn)[3];
            ((char *) &llOut)[5] = ((char *) &llIn)[2];
            ((char *) &llOut)[6] = ((char *) &llIn)[1];
            ((char *) &llOut)[7] = ((char *) &llIn)[0];        

            return llOut;
        }

        return llIn;

    }

    inline unsigned __int64 Ntoh(unsigned __int64 llIn)
    {
        return Hton(llIn);
    }
    /*
    ST_AUTH_INFO
    */
    inline void Ntoh(const ST_AUTH_INFO& src, ST_AUTH_INFO& des)
    {
        for (int i = 0; i<_countof(src.wszUsername); i++)
            des.wszUsername[i] = ntohs(src.wszUsername[i]);
        for (int i = 0; i<_countof(src.wszPassword); i++)
            des.wszPassword[i] = ntohs(src.wszPassword[i]);
    }
    inline void Ntoh(ST_AUTH_INFO& src)
    {
        Ntoh(src, src);
    }
    inline void Hton(const ST_AUTH_INFO& src, ST_AUTH_INFO& des)
    {
        for (int i = 0; i<_countof(src.wszUsername); i++)
            des.wszUsername[i] = htons(src.wszUsername[i]);
        for (int i = 0; i<_countof(src.wszPassword); i++)
            des.wszPassword[i] = htons(src.wszPassword[i]);
    }
    inline void Hton(ST_AUTH_INFO& src)
    {
        Hton(src, src);
    }

    /*
    ST_PACKAGE_HEADER
    */
    inline void Ntoh(const ST_PACKAGE_HEADER& src, ST_PACKAGE_HEADER& des)
    {
        des.ulCommand = ntohl(src.ulCommand);
        des.ulPackageSize = ntohl(src.ulPackageSize);
        des.ulStat = ntohl(src.ulStat);
    }

    inline void Ntoh(ST_PACKAGE_HEADER& src)
    {
        Ntoh(src, src);
    }

    inline void Hton(const ST_PACKAGE_HEADER& src, ST_PACKAGE_HEADER& des)
    {
        des.ulCommand = htonl(src.ulCommand);
        des.ulPackageSize = htonl(src.ulPackageSize);
        des.ulStat = htonl(src.ulStat);
    }

    inline void Hton(ST_PACKAGE_HEADER& src)
    {
        Hton(src, src);
    }

    /*
    ST_RESUME_INFO
    */
    inline void Ntoh(const ST_RESUME_INFO& src, ST_RESUME_INFO& des)
    {
        des.ulType          = ntohl(src.ulType);
        des.ulPosType       = ntohl(src.ulPosType);
        des.ulPosLow        = ntohl(src.ulPosLow);
        des.ulPosHigh       = ntohl(src.ulPosHigh);
        des.ulLen2Validate  = ntohl(src.ulLen2Validate);
    }

    inline void Ntoh(ST_RESUME_INFO& src)
    {
        Ntoh(src, src);
    }

    inline void Hton(const ST_RESUME_INFO& src, ST_RESUME_INFO& des)
    {
        des.ulType          = htonl(src.ulType);
        des.ulPosType       = htonl(src.ulPosType);
        des.ulPosLow        = htonl(src.ulPosLow);
        des.ulPosHigh       = htonl(src.ulPosHigh);
        des.ulLen2Validate  = htonl(src.ulLen2Validate);
    }

    inline void Hton(ST_RESUME_INFO& src)
    {
        Hton(src, src);
    }

    /*
    ST_DR_SNAPSHOT_ID
    */
    inline void Ntoh(const ST_DR_SNAPSHOT_ID& src, ST_DR_SNAPSHOT_ID& des)
    {
        for (int i = 0; i<_countof(src.wszVmGuid); i++)
            des.wszVmGuid[i] = ntohs(src.wszVmGuid[i]);
        for (int i = 0; i<_countof(src.wszSnapGuid); i++)
            des.wszSnapGuid[i] = ntohs(src.wszSnapGuid[i]);
    }

    inline void Ntoh(ST_DR_SNAPSHOT_ID& src)
    {
        Ntoh(src, src);
    }

    inline void Hton(const ST_DR_SNAPSHOT_ID& src, ST_DR_SNAPSHOT_ID& des)
    {
        for (int i = 0; i<_countof(src.wszVmGuid); i++)
            des.wszVmGuid[i] = htons(src.wszVmGuid[i]);
        for (int i = 0; i<_countof(src.wszSnapGuid); i++)
            des.wszSnapGuid[i] = htons(src.wszSnapGuid[i]);
    }

    inline void Hton(ST_DR_SNAPSHOT_ID& src)
    {
        Hton(src, src);
    }

    /*
    ST_DR_DATARANGE
    */
    inline void Ntoh(const ST_DR_DATARANGE& src, ST_DR_DATARANGE& des)
    {
        des.ulStartHigh = ntohl(src.ulStartHigh);
        des.ulStartLow  = ntohl(src.ulStartLow);
        des.ulLen    = ntohl(src.ulLen);
    }
    inline void Ntoh(ST_DR_DATARANGE& src)
    {
        Ntoh(src, src);
    }

    inline void Hton(const ST_DR_DATARANGE& src, ST_DR_DATARANGE& des)
    {
        des.ulStartHigh = htonl(src.ulStartHigh);
        des.ulStartLow  = htonl(src.ulStartLow);
        des.ulLen    = htonl(src.ulLen);
    }
    inline void Hton(ST_DR_DATARANGE& src)
    {
        Hton(src, src);
    }

    /*
    ST_DR_DISK_BITMAP
    */
    inline void Ntoh(const ST_DR_DISK_BITMAP& src, ST_DR_DISK_BITMAP& des)
    {
        des.StartingSec.HighPart = ntohl(src.StartingSec.HighPart);
        des.StartingSec.LowPart = ntohl(src.StartingSec.LowPart);
        des.BitmapSize.HighPart = ntohl(src.BitmapSize.HighPart);
        des.BitmapSize.LowPart = ntohl(src.BitmapSize.LowPart);
    }
    inline void Ntoh(ST_DR_DISK_BITMAP& src)
    {
        Ntoh(src, src);
    }
    inline void Hton(const ST_DR_DISK_BITMAP& src, ST_DR_DISK_BITMAP& des)
    {
        des.StartingSec.HighPart = htonl(src.StartingSec.HighPart);
        des.StartingSec.LowPart = htonl(src.StartingSec.LowPart);
        des.BitmapSize.HighPart = htonl(src.BitmapSize.HighPart);
        des.BitmapSize.LowPart = htonl(src.BitmapSize.LowPart);
    }
    inline void Hton(ST_DR_DISK_BITMAP& src)
    {
        Hton(src, src);
    }

    /*
    ST_DR_VOL_IDEN_EX
    */
    inline void Ntoh(const ST_DR_VOL_IDEN_EX& src, ST_DR_VOL_IDEN_EX& des)
    {
        for (int i = 0; i<_countof(src.wszVmGuid); i++)
            des.wszVmGuid[i] = ntohs(src.wszVmGuid[i]);
        for (int i = 0; i<_countof(src.wszSnapshotGuid); i++)
            des.wszSnapshotGuid[i] = ntohs(src.wszSnapshotGuid[i]);
        for (int i = 0; i<_countof(src.wszVolGuid); i++)
            des.wszVolGuid[i] = ntohs(src.wszVolGuid[i]);
        des.ulFlag = ntohl(src.ulFlag);

    }
    inline void Ntoh(ST_DR_VOL_IDEN_EX& src)
    {
        Ntoh(src, src);
    }
    inline void Hton(const ST_DR_VOL_IDEN_EX& src, ST_DR_VOL_IDEN_EX& des)
    {
        for (int i = 0; i<_countof(src.wszVmGuid); i++)
            des.wszVmGuid[i] = htons(src.wszVmGuid[i]);
        for (int i = 0; i<_countof(src.wszSnapshotGuid); i++)
            des.wszSnapshotGuid[i] = htons(src.wszSnapshotGuid[i]);
        for (int i = 0; i<_countof(src.wszVolGuid); i++)
            des.wszVolGuid[i] = htons(src.wszVolGuid[i]);
        des.ulFlag = htonl(src.ulFlag);
    }
    inline void Hton(ST_DR_VOL_IDEN_EX& src)
    {
        Hton(src, src);
    }

    /*
    ST_DR_VOLDIFF_IDEN
    */
        inline void Ntoh(const ST_DR_VOLDIFF_IDEN& src, ST_DR_VOLDIFF_IDEN& des)
    {
        for (int i = 0; i<_countof(src.wszVmGuid); i++)
            des.wszVmGuid[i] = ntohs(src.wszVmGuid[i]);
        for (int i = 0; i<_countof(src.wszBaseSnapGuid); i++)
            des.wszBaseSnapGuid[i] = ntohs(src.wszBaseSnapGuid[i]);
        for (int i = 0; i<_countof(src.wszCurrSnapGuid); i++)
            des.wszCurrSnapGuid[i] = ntohs(src.wszCurrSnapGuid[i]);
        for (int i = 0; i<_countof(src.wszVolGuid); i++)
            des.wszVolGuid[i] = ntohs(src.wszVolGuid[i]);
        des.ulFlag = ntohl(src.ulFlag);

    }
    inline void Ntoh(ST_DR_VOLDIFF_IDEN& src)
    {
        Ntoh(src, src);
    }
    inline void Hton(const ST_DR_VOLDIFF_IDEN& src, ST_DR_VOLDIFF_IDEN& des)
    {
        for (int i = 0; i<_countof(src.wszVmGuid); i++)
            des.wszVmGuid[i] = htons(src.wszVmGuid[i]);
        for (int i = 0; i<_countof(src.wszBaseSnapGuid); i++)
            des.wszBaseSnapGuid[i] = htons(src.wszBaseSnapGuid[i]);
        for (int i = 0; i<_countof(src.wszCurrSnapGuid); i++)
            des.wszCurrSnapGuid[i] = htons(src.wszCurrSnapGuid[i]);
        for (int i = 0; i<_countof(src.wszVolGuid); i++)
            des.wszVolGuid[i] = htons(src.wszVolGuid[i]);
        des.ulFlag = htonl(src.ulFlag);
    }
    inline void Hton(ST_DR_VOLDIFF_IDEN& src)
    {
        Hton(src, src);
    }

    /*
    ST_DR_VOL_BITMAP
    */
    inline void Ntoh(const ST_DR_VOL_BITMAP& src, ST_DR_VOL_BITMAP& des)
    {
        des.StartingLcn.HighPart = ntohl(src.StartingLcn.HighPart);
        des.StartingLcn.LowPart = ntohl(src.StartingLcn.LowPart);
        des.BitmapSize.HighPart = ntohl(src.BitmapSize.HighPart);
        des.BitmapSize.LowPart = ntohl(src.BitmapSize.LowPart);
    }
    inline void Ntoh(ST_DR_VOL_BITMAP& src)
    {
        Ntoh(src, src);
    }
    inline void Hton(const ST_DR_VOL_BITMAP& src, ST_DR_VOL_BITMAP& des)
    {
        des.StartingLcn.HighPart = htonl(src.StartingLcn.HighPart);
        des.StartingLcn.LowPart = htonl(src.StartingLcn.LowPart);
        des.BitmapSize.HighPart = htonl(src.BitmapSize.HighPart);
        des.BitmapSize.LowPart = htonl(src.BitmapSize.LowPart);
    }
    inline void Hton(ST_DR_VOL_BITMAP& src)
    {
        Hton(src, src);
    }

    /*
    ST_PREJOB_INFO
    */
    inline void Ntoh(const ST_PREJOB_INFO* pSrc, ST_PREJOB_INFO* pDes)
    {
        for (size_t i = 0; i<_countof(pSrc->wszProductNode); i++)
            pDes->wszProductNode[i] = ntohs(pSrc->wszProductNode[i]);
        for (size_t i = 0; i<_countof(pSrc->wszSrcSessGuid); i++)
            pDes->wszSrcSessGuid[i] = ntohs(pSrc->wszSrcSessGuid[i]);
        for (size_t i = 0; i<_countof(pSrc->wszLastRoot); i++)
            pDes->wszLastRoot[i] = ntohs(pSrc->wszLastRoot[i]);
        pDes->nSessNum = ntohl(pSrc->nSessNum);
        pDes->ulSessFlag = ntohl(pSrc->ulSessFlag);
        pDes->nListLen = ntohl(pSrc->nListLen);
        long nLen = pDes->nListLen;
        for (long i = 0; i<nLen; i++)
            pDes->wszDesRootList[i] = ntohs(pSrc->wszDesRootList[i]);
    }
    inline void Ntoh(ST_PREJOB_INFO* pSrc)
    {
        Ntoh(pSrc, pSrc);
    }
    inline void Hton(const ST_PREJOB_INFO* pSrc, ST_PREJOB_INFO* pDes)
    {
        for (size_t i = 0; i<_countof(pSrc->wszProductNode); i++)
            pDes->wszProductNode[i] = htons(pSrc->wszProductNode[i]);
        for (size_t i = 0; i<_countof(pSrc->wszSrcSessGuid); i++)
            pDes->wszSrcSessGuid[i] = htons(pSrc->wszSrcSessGuid[i]);
        for (size_t i = 0; i<_countof(pSrc->wszLastRoot); i++)
            pDes->wszLastRoot[i] = htons(pSrc->wszLastRoot[i]);
        pDes->nSessNum = htonl(pSrc->nSessNum);
        pDes->ulSessFlag = htonl(pSrc->ulSessFlag);
        long nLen = pSrc->nListLen;
        pDes->nListLen = htonl(pSrc->nListLen);
        for (long i = 0; i<nLen; i++)
            pDes->wszDesRootList[i] = htons(pSrc->wszDesRootList[i]);
    }
    inline void Hton(ST_PREJOB_INFO* pSrc)
    {
        Hton(pSrc, pSrc);
    }

    /*
    ST_DR_DATA_EX
    */
    inline void Ntoh(ST_DR_DATA_EX* pSrc, ST_DR_DATA_EX* pDes)
    {
        pDes->header.type       = ntohs(pSrc->header.type);
        pDes->header.flag       = ntohs(pSrc->header.flag);
        pDes->header.nErrCode   = ntohl(pSrc->header.nErrCode);
        pDes->header.nSecCnt    = ntohl(pSrc->header.nSecCnt);
        pDes->header.llSecBegin = Ntoh(pSrc->header.llSecBegin);
    }
    inline void Ntoh(ST_DR_DATA_EX* pSrc)
    {
        Ntoh(pSrc, pSrc);
    }
    inline void Hton(ST_DR_DATA_EX* pSrc, ST_DR_DATA_EX* pDes)
    {
        pDes->header.type       = htons(pSrc->header.type);
        pDes->header.flag       = htons(pSrc->header.flag);
        pDes->header.nErrCode   = htonl(pSrc->header.nErrCode);
        pDes->header.nSecCnt    = htonl(pSrc->header.nSecCnt);
        pDes->header.llSecBegin = Hton(pSrc->header.llSecBegin);
    }
    inline void Hton(ST_DR_DATA_EX* pSrc)
    {
        Hton(pSrc, pSrc);
    }

} //namespace HADT

#endif //_HA_COMMON_DEF_H_
