#ifndef HA_DATA_TRANS_JOB_SCRIPT_H
#define HA_DATA_TRANS_JOB_SCRIPT_H
#pragma once
#include <Windows.h>

#define HAJS_PROTOCOL_SHAREFOLEDR   0x01
#define HAJS_PROTOCOL_SOCKET        0x02

#define HAJS_DES_VHD_ORIGIN         0x00        //does not change the format
#define HAJS_DES_VHD_PLAIN          0x01        //des format is plain VHD
#define HAJS_DES_VHD_COMPRESS       0x02        //des format is compressed VHD
#define HAJS_DES_VHDX_ORIGIN        0x05        //does not change the format
#define HAJS_DES_VHDX_PLAIN         0x06        //des format is plain VHDX
#define HAJS_DES_VHDX_COMPRESS      0x07        //des format is compressed VHDX

//ulReplicaConvType
#define REP_TYPE_D2D_D2D			0
#define REP_TYPE_D2D_VMDK			1

// ulSubType
#define REP_SUBTYPE_D2DVMDK_NBD     0
#define REP_SUBTYPE_D2DVMDK_SAN     1


//TODO: file level credential info

typedef struct _t_ha_item_file
{
    wchar_t* pwszPath;			// Path of the .D2D file inside the session
    wchar_t* pwszDesRoot;       // Destination root for the .D2D file
	wchar_t* pwszVMDKPath;		// Corresponding VMDK disk URL to which D2D2VMDK conversion should happen
    unsigned long ulVmfsBlockSize;  // The VMFS block size of the datastore that this .vmdk belongs to.
} HA_ITEM_FILE, PHA_ITEM_FILE;

typedef struct _t_ha_src_item
{
    wchar_t* pwszPath;
    wchar_t* pwszSFUsername;    // If path is share folder, the user name to access it
    wchar_t* pwszSFPassword;    // If path is share folder, the user password to access it

// TODO: session level credential info
   
	unsigned long ulD2DFileCnt;    // The count of file to be replicated
    HA_ITEM_FILE* pD2DFileList;	// represents each D2D file and its related information inside given session
} HA_SRC_ITEM, PHA_SRC_ITEM;

typedef struct _t_ha_job_script
{
    unsigned long           ulVer;              //Job script version.
    unsigned long           ulJobType;          //Can only be Data Transfer.
    wchar_t*                pwszJobID;          //job UUID.
    unsigned long           ulProtocol;         //share folder or socket.

    wchar_t*                pwszVmInstID;       //the UUID of VM instance

    wchar_t*                pwszLocalUsername;
    wchar_t*                pwszLocalPassword;
    wchar_t*                pwszProductNode;

    //des
    wchar_t*                pwszDesHostName;       //Dest host name
    wchar_t*                pwszDesPort;           //Dest port
    wchar_t*                pwszDesFolder;         //dest folder
    wchar_t*                pwszUserName;
    wchar_t*                pwszPassword;

    //src
    unsigned long           ulSrcItemCnt;
    HA_SRC_ITEM*            pSrcItemList;

    //control
    unsigned long           ulDesVHDFormat;
    unsigned long           ulThrottling;
    unsigned long           ulCtlFlag;
    BOOL                    bCompressOnWire;
    BOOL                    bEncryptOnWire;
    BOOL                    bOverwriteExist;
    wchar_t*                pwszCryptPassword;
	
	//ESX/VC Credentials
    wchar_t                *pwszEsxHostName;       
    wchar_t                *pwszEsxUserName;
    wchar_t                *pwszEsxPassword;
	wchar_t                *pwszMoref;
	wchar_t				   *pwszSnapshotID;
    unsigned long			ulVDDKPort;
	wchar_t				   *pwszEsxThumbprint;			//add by zhepa02 at 2015-05-25


	unsigned long			ulReplicaConvType;
    unsigned long           ulSubType;

    //smart copy
    BOOL                    bSmartCopy;
    unsigned long           ulScSessBegin;
    unsigned long           ulScSessEnd;

    //the latest dest root. This is recorded at the monitor side in previous.
    wchar_t*                pwszLastDesRoot;

    unsigned long           ulBackupDescType; //Backup Session destination type: 0 - backup to share folder; 1 - backup to RPS datastore
	long					lJobID;
} HA_JOBSCRIPT, PHA_JOBSCRIPT;



#endif //HA_DATA_TRANS_JOB_SCRIPT_H
