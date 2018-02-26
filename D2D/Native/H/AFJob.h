/******************************************************************************
 The header file defines the fields package in xml job script and the backend
 structure used to save these fields

 Version Date: June 17, 2009
 *****************************************************************************/

#ifndef _AF_JOB_H
#define _AF_JOB_H
#pragma once
#include <Windows.h>
#include <tchar.h>
#include "asdefs.h"
#include "AFJobClone.h" //<sonmi01>2014-7-15 ###???


// D2D for Exchange GRT. zhazh06 R16. - begin -
//Following is backup options
#define QJDTO_B_ENABLE_EXCHGRTCAT		0x00000001
#define QJDTO_B_DISABLE_EXCHGRTCAT	0x00000002 // GENERATE GRT WHEN BROWING

// new structure for backup option for Exchange 
typedef struct tagAFBACKUPOPTION_EXCH
{
	ULONG ulOptions;

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagAFBACKUPOPTION_EXCH)
	{
		JS_CLONE(ulOptions);
	}

	JS_FREE_FUNCTION(tagAFBACKUPOPTION_EXCH)
	{
		JS_FREE(ulOptions);
	}

}AFBACKUPOPTION_EXCH, *PAFBACKUPOPTION_EXCH;

// new structure for backup option for SharePoint
typedef struct tagAFBACKUPOPTION_SP
{
	ULONG ulOptions;

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagAFBACKUPOPTION_SP)
	{
		JS_CLONE(ulOptions);
	}

	JS_FREE_FUNCTION(tagAFBACKUPOPTION_SP)
	{
		JS_FREE(ulOptions);
	}
}AFBACKUPOPTION_SP, *PAFBACKUPOPTION_SP;

///////////////////////////////////
//Following is restore options

// VOLUME Level options
#define AFDDO_R_EXCH_ALTERNATE_LOCATION 0x00000001
#define AFDDO_R_EXCH_RESTORE_TO_DISK 0x00000002
#define AFDDO_R_EXCH_RESOLVE_NAMECOLLISION_RENAME 0x00010000


// VOLUME Level options for SharePoint
#define AFDDO_R_SP_ALTERNATE_LOCATION 0x00000001
#define AFDDO_R_SP_RESTORE_TO_DISK 0x00000002

#define AFDDO_R_SP_RESOLVE_NAME_COLLISION 0x00010000

#define AFDDO_R_SP_INC_VER_ALL			  0x00000100
#define AFDDO_R_SP_INC_VER_LASTMAJOR      0x00000200
#define AFDDO_R_SP_INC_VER_LASTMAJORMINOR 0x00000400

//<sonmi01>2010-12-24 D2D VM job monitor
//AFJOBSCRIPT::ulJobAttribute
typedef enum tagE_JOB_ATTRIBUTE
{
	EJA_D2D = 0,
	EJA_VM
} E_JOB_ATTRIBUTE;


// new structure for restore options for Exchange 
typedef struct tagAFRESTOREOPTION_EXCH
{
	ULONG ulOptions;
	ULONG ulServerVersion;
	PWCHAR	pwszFolder;	// folder name, when restore to disk
	PWCHAR	pwszAlternateServer;	// the alternate server name
	PWCHAR	pwszUser;		// User name for the alternate server
	PWCHAR	pwszUserPW;		// User password for the alternate server

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagAFRESTOREOPTION_EXCH)
	{
		JS_CLONE(ulOptions);
		JS_CLONE(ulServerVersion);
		JS_CLONE(pwszFolder);
		JS_CLONE(pwszAlternateServer);
		JS_CLONE(pwszUser);
		JS_CLONE(pwszUserPW);
	}

	JS_FREE_FUNCTION(tagAFRESTOREOPTION_EXCH)
	{
		JS_FREE(ulOptions);
		JS_FREE(ulServerVersion);
		JS_FREE(pwszFolder);
		JS_FREE(pwszAlternateServer);
		JS_FREE(pwszUser);
		JS_FREE(pwszUserPW);
	}
}AFRESTOREOPTION_EXCH, *PAFRESTOREOPTION_EXCH;

// new structure for restore options for SharePoint
typedef struct tagAFRESTOREOPTION_SP
{
	ULONG   ulOptions;
	// When restore to disk, this is the destination folder path
	PWCHAR  pwszDestFolder;
	// When restore to farm, this is the destination node info
	DWORD   dwDestNodeType;
	PWCHAR  pwszDestNodeUrl;
	PWCHAR  pwszDestObjId;
	PWCHAR  pwszOwnerLogin;
	PWCHAR  pwszOwnerEmail;
	PWCHAR	pwszUser;		// User name for the alternate server
	PWCHAR	pwszUserPW;		// User password for the alternate server

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagAFRESTOREOPTION_SP)
	{
		JS_CLONE(ulOptions);
		JS_CLONE(pwszDestFolder);
		JS_CLONE(dwDestNodeType);
		JS_CLONE(pwszDestNodeUrl);
		JS_CLONE(pwszDestObjId);
		JS_CLONE(pwszOwnerLogin);
		JS_CLONE(pwszOwnerEmail);
		JS_CLONE(pwszUser);
		JS_CLONE(pwszUserPW);
	}

	JS_FREE_FUNCTION(tagAFRESTOREOPTION_SP)
	{
		JS_FREE(ulOptions);
		JS_FREE(pwszDestFolder);
		JS_FREE(dwDestNodeType);
		JS_FREE(pwszDestNodeUrl);
		JS_FREE(pwszDestObjId);
		JS_FREE(pwszOwnerLogin);
		JS_FREE(pwszOwnerEmail);
		JS_FREE(pwszUser);
		JS_FREE(pwszUserPW);
	}


}AFRESTOREOPTION_SP, *PAFRESTOREOPTION_SP;


// D2D for Active Directory GRT <<
typedef struct tagAFRESTOREOPTION_AD
{
	ULONG options;

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagAFRESTOREOPTION_AD)
	{
		JS_CLONE(options);
	}

	JS_FREE_FUNCTION(tagAFRESTOREOPTION_AD)
	{
		JS_FREE(options);
	}
}AFRESTOREOPTION_AD, *PAFRESTOREOPTION_AD;
// D2D for Active Directory GRT >>

//<XUVNE01> For Exchange GRT restore, subitem type. 2010-04-19
#define AFESI_TYPE_MDB						0x00000001
#define AFESI_TYPE_FOLDER					0x00000002
#define AFESI_TYPE_MESSAGE					0x00000003

//<XUVNE01> For Exchange GRT restore, subitem info. 2010-04-19
typedef struct tagAFExchSubItem
{
	ULONG		ulItemType;		// 1 for MailboxDB, 2 for Folder, 3 for Message
	PWCHAR		pwszItemName;
	PWCHAR		pwszMailboxName;
	PWCHAR		pwszExchangeObjectIDs;
	PWCHAR		pwszDescription;

	JS_CLONE_FUNCTION(tagAFExchSubItem)
	{
		JS_CLONE(ulItemType);
		JS_CLONE(pwszItemName);
		JS_CLONE(pwszMailboxName);
		JS_CLONE(pwszExchangeObjectIDs);
		JS_CLONE(pwszDescription);
	}

	JS_FREE_FUNCTION(tagAFExchSubItem)
	{
		JS_FREE(ulItemType);
		JS_FREE(pwszItemName);
		JS_FREE(pwszMailboxName);
		JS_FREE(pwszExchangeObjectIDs);
		JS_FREE(pwszDescription);
	}

} AFEXCHSUBITEM, *PAFEXCHSUBITEM;

// D2D for Exchange GRT. zhazh06 R16. - end -


typedef struct tagAFSPSubItem
{
	PWCHAR  pwszName;
	PWCHAR  pwszData;

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagAFSPSubItem)
	{
		JS_CLONE(pwszName);
		JS_CLONE(pwszData);
	}

	JS_FREE_FUNCTION(tagAFSPSubItem)
	{
		JS_FREE(pwszName);
		JS_FREE(pwszData);
	}
} AFSPSUBITEM, *PAFSPSUBITEM;

// D2D for Active Directory GRT <<

typedef struct tagAFADSubItem
{
	DWORD dwDnt;
	BOOL  IsAllChild;	// whether restore all the child items
	BOOL  IsAllATTr;	// whether restore all the attributes
	DWORD dwATTrNum;	// the attribute number
	WCHAR * pwszATTrNames; // the attributes needed to be restored, each ATTr is separated by ';' For instance: "cn;objectClass;displayname;"

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagAFADSubItem)
	{
		JS_CLONE(dwDnt);
		JS_CLONE(IsAllChild);	// whe
		JS_CLONE(IsAllATTr);	// whe
		JS_CLONE(dwATTrNum);	// the
		JS_CLONE(pwszATTrNames); // 
	}

	JS_FREE_FUNCTION(tagAFADSubItem)
	{
		JS_FREE(dwDnt);
		JS_FREE(IsAllChild);	// whe
		JS_FREE(IsAllATTr);	// whe
		JS_FREE(dwATTrNum);	// the
		JS_FREE(pwszATTrNames); // 
	}


}AFADSubITEM, *PAFADSubITEM, AGRT_RESTORE_ADITEM, *PAGRT_RESTORE_ADITEM;
// D2D for Active Directory GRT >>
//begin  structure to hold job script converting from xml file
typedef struct tagAFVolItemAppComp
{
	PWCHAR		pwszFileorDir;                 // App Component or FS item path
	ULONG			fOptions;						// Options
	PWCHAR		pwszCompRestPath;				// for SQL alt location restore, save SQL DB alternative location(pVolItemAppCompList)
	PWCHAR		pwszCompRestName;				// for SQL alt location restore, save SQL DB new DB Name(pVolItemAppCompList)

	// D2D for Exchange GRT. zhazh06 R16.
	ULONG				nExchSubItemList;			//<XUVNE01> For Exchange GRT restore, subitem count. 2010-04-19
	PAFEXCHSUBITEM	pExchSubItemList;			//<XUVNE01> For Exchange GRT restore, subitem list. 2010-04-19

	ULONG				nSPSubItem;
	PAFSPSUBITEM	    pSPSubItemList;

	// AD.GRT wanmi12
	ULONG				uADItemNum;				// AD
	PAFADSubITEM		pADItemList;			// AD

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagAFVolItemAppComp)
	{
		JS_CLONE(pwszFileorDir);
		JS_CLONE(fOptions);
		JS_CLONE(pwszCompRestPath);
		JS_CLONE(pwszCompRestName);

		JS_CLONE_LIST(AFEXCHSUBITEM, nExchSubItemList, pExchSubItemList);
		JS_CLONE_LIST(AFSPSUBITEM, nSPSubItem, pSPSubItemList);
		JS_CLONE_LIST(AFADSubITEM, uADItemNum, pADItemList);
	}

	JS_FREE_FUNCTION(tagAFVolItemAppComp)
	{
		JS_FREE(pwszFileorDir);
		JS_FREE(fOptions);
		JS_FREE(pwszCompRestPath);
		JS_FREE(pwszCompRestName);

		JS_FREE_LIST(AFEXCHSUBITEM, nExchSubItemList, pExchSubItemList);
		JS_FREE_LIST(AFSPSUBITEM, nSPSubItem, pSPSubItemList);
		JS_FREE_LIST(AFADSubITEM, uADItemNum, pADItemList);
	}

}AFVOLITEMAPPCOMP, *PAFVOLITEMAPPCOMP;

typedef struct AFRESTVOLAPP
{
	ULONG 				ulFileSystem;				// file system, app type etc
	ULONG					ulSubSessNum;				// subsession #
	PWCHAR				pwszPath;					// writer path
	LONGLONG				lVolAppSize;				// whole volume/whole app size
	ULONG					nVolItemAppComp;			// # of VolItem and AppComp in list			
	PAFVOLITEMAPPCOMP		pVolItemAppCompList;		// List of VolItem & App Components	
	ULONG					nDestItemCount;				// destintaion map to nVolItemAppComp 
	PAFVOLITEMAPPCOMP		pDestItemList;				// destinationlist map to pVolItemAppCompList
	PWCHAR				pDestVolumeName;			// destination volume name
	ULONG					nFilterItems;               // # of Filter items in list		
	PVOID					pvFilterList;               // List of filters	
	ULONG					OnConflictMethod;			// On Conflict Methods -- used by restore job
	ULONG					fOptions;					// Options
	// D2D for Exchange GRT. zhazh06 R16.
	PAFRESTOREOPTION_EXCH pRestoreOption_Exch;
	PAFRESTOREOPTION_SP   pRestoreDest_SP;
	PAFRESTOREOPTION_AD	pRestoreOption_AD;			//AD GRT

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(AFRESTVOLAPP)
	{
		JS_CLONE(ulFileSystem);
		JS_CLONE(ulSubSessNum);
		JS_CLONE(pwszPath);
		JS_CLONE(lVolAppSize);
		JS_CLONE_LIST(AFVOLITEMAPPCOMP, nVolItemAppComp, pVolItemAppCompList);
		JS_CLONE_LIST(AFVOLITEMAPPCOMP, nDestItemCount, pDestItemList);
		JS_CLONE(pDestVolumeName);
		JS_CLONE(nFilterItems);
		//JS_CLONE(pvFilterList); //hmmm... do not use pvoid... do not know what it is statically
		JS_CLONE(OnConflictMethod);
		JS_CLONE(fOptions);
		JS_CLONE_POINTER(AFRESTOREOPTION_EXCH, pRestoreOption_Exch);
		JS_CLONE_POINTER(AFRESTOREOPTION_SP, pRestoreDest_SP);
	}

	JS_FREE_FUNCTION(AFRESTVOLAPP)
	{
		JS_FREE(ulFileSystem);
		JS_FREE(ulSubSessNum);
		JS_FREE(pwszPath);
		JS_FREE(lVolAppSize);
		JS_FREE_LIST(AFVOLITEMAPPCOMP, nVolItemAppComp, pVolItemAppCompList);
		JS_FREE_LIST(AFVOLITEMAPPCOMP, nDestItemCount, pDestItemList);
		JS_FREE(pDestVolumeName);
		JS_FREE(nFilterItems);
		//JS_FREE(pvFilterList); //hmmm... do not use pvoid... do not know what it is statically
		JS_FREE(OnConflictMethod);
		JS_FREE(fOptions);
		JS_FREE_POINTER(AFRESTOREOPTION_EXCH, pRestoreOption_Exch);
		JS_FREE_POINTER(AFRESTOREOPTION_SP, pRestoreDest_SP);
	}


} AFRESTVOLAPP, *PAFRESTVOLAPP;

typedef struct AFBACKUPVOL
{
	PWCHAR			pwszVolName;			// Volume - C: D:, when backup app writer, GUI converts writer to volumes
	ULONG 			ulFileSystem;			// file system, app type etc
	ULONG				ulSessionMethod;		// session method - full or inc
	ULONG				nVolItemAppComp;		// it is 0 for backup job
	PAFVOLITEMAPPCOMP	pVolItemAppCompList;	// it is NULl for backup job			
	ULONG				fOptions;				// Options


	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(AFBACKUPVOL)
	{
		JS_CLONE(pwszVolName);
		JS_CLONE(ulFileSystem);
		JS_CLONE(ulSessionMethod);
		JS_CLONE_LIST(AFVOLITEMAPPCOMP, nVolItemAppComp, pVolItemAppCompList);
		JS_CLONE(fOptions);
	}

	JS_FREE_FUNCTION(AFBACKUPVOL)
	{
		JS_FREE(pwszVolName);
		JS_FREE(ulFileSystem);
		JS_FREE(ulSessionMethod);
		JS_FREE_LIST(AFVOLITEMAPPCOMP, nVolItemAppComp, pVolItemAppCompList);
		JS_FREE(fOptions);
	}

} AFBACKUPVOL, *PAFBACKUPVOL;

//<sonmi01>2010-6-24 vsphere support
struct VC_ESX_CREDENTIALS_JobScript
{
	WCHAR * ServerName;
	WCHAR * Username;
	WCHAR * Password;
	LONG  VIport;
	WCHAR * Protocol;
	BOOL ignoreCertificate;

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(VC_ESX_CREDENTIALS_JobScript)
	{
		JS_CLONE(ServerName);
		JS_CLONE(Username);
		JS_CLONE(Password);
		JS_CLONE(VIport);
		JS_CLONE(Protocol);
		JS_CLONE(ignoreCertificate);
	}

	JS_FREE_FUNCTION(VC_ESX_CREDENTIALS_JobScript)
	{
		JS_FREE(ServerName);
		JS_FREE(Username);
		JS_FREE(Password);
		JS_FREE(VIport);
		JS_FREE(Protocol);
		JS_FREE(ignoreCertificate);
	}
};

struct VM_Info_JobScript
{
	WCHAR * vmName;
	WCHAR * vmUUID;
	WCHAR * vmHost;
	WCHAR * vmVMX;
	WCHAR * vmESXHost;
	WCHAR * vmInstUUID;
	WCHAR * vmGuestOS;
	WCHAR * vmIP;
	BOOL powerState;

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(VM_Info_JobScript)
	{
		JS_CLONE(vmName);
		JS_CLONE(vmUUID);
		JS_CLONE(vmHost);
		JS_CLONE(vmVMX);
		JS_CLONE(vmESXHost);
		JS_CLONE(vmInstUUID);
		JS_CLONE(vmGuestOS);
		JS_CLONE(vmIP);
		JS_CLONE(powerState);
	}

	JS_FREE_FUNCTION(VM_Info_JobScript)
	{
		JS_FREE(vmName);
		JS_FREE(vmUUID);
		JS_FREE(vmHost);
		JS_FREE(vmVMX);
		JS_FREE(vmESXHost);
		JS_FREE(vmInstUUID);
		JS_FREE(vmGuestOS);
		JS_FREE(vmIP);
		JS_FREE(powerState);
	}
};

struct GuestHost_Credentials_JobScript
{
	WCHAR * VMUsername;
	WCHAR * VMPassword;

	JS_CLONE_FUNCTION(GuestHost_Credentials_JobScript)
	{
		JS_CLONE(VMUsername);
		JS_CLONE(VMPassword);
	}

	JS_FREE_FUNCTION(GuestHost_Credentials_JobScript)
	{
		JS_FREE(VMUsername);
		JS_FREE(VMPassword);
	}
};
//</sonmi01>

//<sonmi01>2014-8-13 #customize disk thin/thick provision
//- Thick provision lazy zeroed(0)
//- Thin provision(1)
//- Thick provision eager zeroed(2)
CONST LONG VMWARE_VDISK_TYPE_THICK_LAZY = 0;
CONST LONG VMWARE_VDISK_TYPE_THIN = 1;
CONST LONG VMWARE_VDISK_TYPE_THICK_EAGER = 2;
CONST LONG VMWARE_VDISK_TYPE_ORIGINAL = 3;

typedef struct tagVSphereRestore_DiskDataStore
{
	PWCHAR m_diskUrl;
	PWCHAR m_dataStore;
	PWCHAR m_dataStoreId;  //<huvfe01> for vCloud vApp vms
	ULONG  m_ulDiskType; //<huvfe01>2014-6-30 hyper-v dynamic/fixed vhdx -> HYPERV_VDISK_TYPE_DYNAMIC(0); HYPERV_VDISK_TYPE_FIXED(1).
	ULONG  m_ulQuickRecovery; //<huvfe01>2014-6-30 hyper-v for fixed size vhdx-> 0 : restore all data blocks; 1: only restore used blocks.

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagVSphereRestore_DiskDataStore)
	{
		JS_CLONE(m_diskUrl);
		JS_CLONE(m_dataStore);
		JS_CLONE(m_dataStoreId);
		JS_CLONE(m_ulDiskType);
		JS_CLONE(m_ulQuickRecovery);
	}

	JS_FREE_FUNCTION(tagVSphereRestore_DiskDataStore)
	{
		JS_FREE(m_diskUrl);
		JS_FREE(m_dataStore);
		JS_FREE(m_dataStoreId);
		JS_FREE(m_ulDiskType);
		JS_FREE(m_ulQuickRecovery);
	}

}VSphereRestore_DiskDataStore, *PVSphereRestore_DiskDataStore;


//<sonmi01>2013-6-4 #VM NIC and VDS support

/*****************************************************************************************

<?xml version="1.0" encoding="utf-8"?>
<device xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="VirtualE1000e">
<key>4003</key>
<deviceInfo>
<label>Network adapter 4</label>
<summary>VM Network</summary>
</deviceInfo>
<backing xsi:type="VirtualEthernetCardNetworkBackingInfo">
<deviceName>VM Network</deviceName>
<useAutoDetect>false</useAutoDetect>
<network type="Network">network-12</network>
</backing>
</device>

<?xml version="1.0" encoding="utf-8"?>
<device xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="VirtualE1000e">
<key>4000</key>
<deviceInfo>
<label>Network adapter 1</label>
<summary>vm.device.VirtualE1000e.DistributedVirtualPortBackingInfo.summary</summary>
</deviceInfo>
<backing xsi:type="VirtualEthernetCardDistributedVirtualPortBackingInfo">
<port>
<switchUuid>fa eb 2d 50 c0 28 9d d4-dd e6 cf 83 c1 0f ab 36</switchUuid>
<portgroupKey>dvportgroup-55</portgroupKey>
<portKey>103</portKey>
<connectionCookie>650340557</connectionCookie>
</port>
</backing>

</device>

*****************************************************************************************/

struct VSphereRestore_VMNetworkAdapter
{
	PWCHAR m_label;				// VM network adapter name
	PWCHAR m_deviceName;			// if not null, it is standard virtual switch
	PWCHAR m_switchName;			// if not null, it is distributed virtual switch
	PWCHAR m_portgroupName;		// port group name of distributed virtual switch
	PWCHAR m_switchUuid;			// uuid of distributed virtual switch
	PWCHAR m_portgroupKey;		// port group key of distributed virtual switch

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(VSphereRestore_VMNetworkAdapter)
	{
		JS_CLONE(m_label);
		JS_CLONE(m_deviceName);
		JS_CLONE(m_switchName);
		JS_CLONE(m_portgroupName);
		JS_CLONE(m_switchUuid);
		JS_CLONE(m_portgroupKey);
	}

	JS_FREE_FUNCTION(VSphereRestore_VMNetworkAdapter)
	{
		JS_FREE(m_label);
		JS_FREE(m_deviceName);
		JS_FREE(m_switchName);
		JS_FREE(m_portgroupName);
		JS_FREE(m_switchUuid);
		JS_FREE(m_portgroupKey);
	}
};

//<sonmi01>2010-8-18 vm recovery
struct VSphereRestore_JobScript
{
	VC_ESX_CREDENTIALS_JobScript m_VcEsxCredentials;
	PWCHAR m_vcName;
	PWCHAR m_esxName;
	UINT32 m_nVmdkPort;
	PWCHAR m_vmName;
	PWCHAR m_vmResPool; // baide02 for vm resource pool
	PWCHAR m_vmConfigFile;
	PWCHAR m_vmDataStore;
	PWCHAR m_vmDataStoreId; //<huvfe01> for vCloud vApp vms
	BOOL m_bOverwriteVM;
	//BOOL m_bRecoverToOriginal;
	//ULONG m_ulJobID;
	//PWCHAR m_vmDiskList; 
	//PWCHAR m_vmDiskDataStoreList;
	PVSphereRestore_DiskDataStore m_pDiskDataStores;
	UINT m_vmDiskCount;
	BOOL m_PowerOnVM;
	BOOL m_RestoreToOriginal;

	ULONG m_VMNetworkAdapterCount; //<sonmi01>2013-6-4 #VM NIC and VDS support
	VSphereRestore_VMNetworkAdapter * m_pVMNetworkAdapter;

	ULONG  m_ulCPUCount;
	ULONG  m_ulMemSizeInKB;//MB

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(VSphereRestore_JobScript)
	{
		JS_CLONE_OBJECT(VC_ESX_CREDENTIALS_JobScript, m_VcEsxCredentials);
		JS_CLONE(m_vcName);
		JS_CLONE(m_esxName);
		JS_CLONE(m_nVmdkPort);
		JS_CLONE(m_vmName);
		JS_CLONE(m_vmResPool); // baide02 for vm resource pool
		JS_CLONE(m_vmConfigFile);
		JS_CLONE(m_vmDataStore);
		JS_CLONE(m_vmDataStoreId);
		JS_CLONE(m_bOverwriteVM);
		JS_CLONE_LIST(VSphereRestore_DiskDataStore, m_vmDiskCount, m_pDiskDataStores);
		JS_CLONE(m_PowerOnVM);
		JS_CLONE(m_RestoreToOriginal);
		JS_CLONE_LIST(VSphereRestore_VMNetworkAdapter, m_VMNetworkAdapterCount, m_pVMNetworkAdapter);
		JS_CLONE(m_ulCPUCount);
		JS_CLONE(m_ulMemSizeInKB);
	}

	JS_FREE_FUNCTION(VSphereRestore_JobScript)
	{
		JS_FREE_OBJECT(VC_ESX_CREDENTIALS_JobScript, m_VcEsxCredentials);
		JS_FREE(m_vcName);
		JS_FREE(m_esxName);
		JS_FREE(m_nVmdkPort);
		JS_FREE(m_vmName);
		JS_FREE(m_vmResPool); // baide02 for vm resource pool
		JS_FREE(m_vmConfigFile);
		JS_FREE(m_vmDataStore);
		JS_FREE(m_vmDataStoreId);
		JS_FREE(m_bOverwriteVM);
		JS_FREE_LIST(VSphereRestore_DiskDataStore, m_vmDiskCount, m_pDiskDataStores);
		JS_FREE(m_PowerOnVM);
		JS_FREE(m_RestoreToOriginal);
		JS_FREE_LIST(VSphereRestore_VMNetworkAdapter, m_VMNetworkAdapterCount, m_pVMNetworkAdapter);
		JS_FREE(m_ulCPUCount);
		JS_FREE(m_ulMemSizeInKB);
	}
};

//<sonmi01>2014-3-12 ###???
#define NODE_OPTION_HBBU_NO_LIC		(1 << 0)

typedef struct _AFNODE
{
	PWCHAR			pwszNodeName;					// save both domain and node name here
	PWCHAR			pwszNodeAddr;					// IP address
	PWCHAR			pwszUserName;					// User name
	PWCHAR			pwszUserPW;						// User password
	PWCHAR			pwszSessPath;					// Master Session Path 	
	ULONG				ulSessNum;						// master session #
	ULONG				nVolumeApp;						// # of Volume/App //escape - HBBU backup - number of error string parameters indicated by pRestoreVolumeAppList. now it is CONST 5
	PAFBACKUPVOL		pBackupVolumeList;				// set to NULL if it is restore job. 
	PAFRESTVOLAPP		pRestoreVolumeAppList;			// set to NULL if it is backup job //escape - HBBU backup - contains error string parameters indicated by fOptions
	ULONG				nFilterItems;					// # of Filter items in list		
	PVOID				pvFilterList;					// List of filters	
	ULONG				fOptions;						// Node level options //escape - HBBU backup - it is resource id for license check error
	// D2D for Exchange GRT. zhazh06 R16. 
	PAFBACKUPOPTION_EXCH  pBackupOption_Exch;// backup option for Exchange
	PAFBACKUPOPTION_SP pAFSharePointOption;         // Backup option for SharePoint.

	DWORD			    dwEncryptTypeRestore;			//<sonmi01>2010-5-6 encrypt support
	PWCHAR	        pwszEncryptPasswordRestore;		//<sonmi01>2010-5-6 encrypt support

	//<sonmi01>2010-6-24 vsphere  backup support 
	VC_ESX_CREDENTIALS_JobScript	m_VCESXCredentials;
	VM_Info_JobScript	m_VMInfo;
	GuestHost_Credentials_JobScript m_GuestHostCredentials;

	VSphereRestore_JobScript m_VSphereRestore_JobScript; //<sonmi01>2010-8-18 vm recovery

	ULONG	m_UnderlyingHypervisorCount;						//<sonmi01>2014-6-4 ###???
	VC_ESX_CREDENTIALS_JobScript * m_pUnderlyingHypervisor;	//vCloud attached vCenters <sonmi01>2014-6-4 ###???

	ULONG m_ulChildVMCount; //<huvfe01>added for restore. For vApp node, there are serval underlying vms 
	_AFNODE * m_pChildVMs;

	PWCHAR m_storagePolicyGuid;
	PWCHAR m_storagePolicyName;
	PWCHAR TransportMode; //<sonmi01>2014-8-20 ###???
	PWCHAR strNetworkMapXml;
	ULONG  ulJobId;//<huvfe01>2014-8-28 the job id of the node

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFNODE)
	{
		JS_CLONE(pwszNodeName);					// save both domain and
		JS_CLONE(pwszNodeAddr);					// IP address
		JS_CLONE(pwszUserName);					// User name
		JS_CLONE(pwszUserPW);						// User password
		JS_CLONE(pwszSessPath);					// Master Session Path 
		JS_CLONE(ulSessNum);						// master session #
		//JS_CLONE(ULONG				nVolumeApp);						// # of Volume/App //es
		JS_CLONE_LIST(AFBACKUPVOL, nVolumeApp, pBackupVolumeList);				// set to NULL if it is
		JS_CLONE_LIST(AFRESTVOLAPP, nVolumeApp, pRestoreVolumeAppList);			// set to NULL if it is
		JS_CLONE(nFilterItems);					// # of Filter items in
		//JS_CLONE(PVOID				pvFilterList);					// List of filters	
		JS_CLONE(fOptions);						// Node level options /
		JS_CLONE_POINTER(AFBACKUPOPTION_EXCH, pBackupOption_Exch);// backup option for Exchange
		JS_CLONE_POINTER(AFBACKUPOPTION_SP, pAFSharePointOption);         // Backup option for Shar
		JS_CLONE(dwEncryptTypeRestore);			//<sonmi01>2010-5-6 enc
		JS_CLONE(pwszEncryptPasswordRestore);		//<sonmi01>2010-5-6 enc
		JS_CLONE_OBJECT(VC_ESX_CREDENTIALS_JobScript, m_VCESXCredentials);
		JS_CLONE_OBJECT(VM_Info_JobScript, m_VMInfo);
		JS_CLONE_OBJECT(GuestHost_Credentials_JobScript, m_GuestHostCredentials);
		JS_CLONE_OBJECT(VSphereRestore_JobScript, m_VSphereRestore_JobScript); //<sonmi01>2010-8-18
		//JS_CLONE(ULONG	m_UnderlyingHypervisorCount);						//<sonmi01>2014
		JS_CLONE_LIST(VC_ESX_CREDENTIALS_JobScript, m_UnderlyingHypervisorCount, m_pUnderlyingHypervisor);	//vCloud attach
		JS_CLONE_LIST(_AFNODE, m_ulChildVMCount, m_pChildVMs);
		JS_CLONE(m_storagePolicyGuid);
		JS_CLONE(m_storagePolicyName);
		JS_CLONE(TransportMode);
		JS_CLONE(strNetworkMapXml);
		JS_CLONE(ulJobId);
        JS_CLONE(bRunCommandEvenFailed);
	}

	JS_FREE_FUNCTION(_AFNODE)
	{
		JS_FREE(pwszNodeName);					// save both domain and
		JS_FREE(pwszNodeAddr);					// IP address
		JS_FREE(pwszUserName);					// User name
		JS_FREE(pwszUserPW);						// User password
		JS_FREE(pwszSessPath);					// Master Session Path 
		JS_FREE(ulSessNum);						// master session #
		//JS_FREE(ULONG				nVolumeApp);						// # of Volume/App //es
		JS_FREE_LIST(AFBACKUPVOL, nVolumeApp, pBackupVolumeList);				// set to NULL if it is
		JS_FREE_LIST(AFRESTVOLAPP, nVolumeApp, pRestoreVolumeAppList);			// set to NULL if it is
		JS_FREE(nFilterItems);					// # of Filter items in
		//JS_FREE(PVOID				pvFilterList);					// List of filters	
		JS_FREE(fOptions);						// Node level options /
		JS_FREE_POINTER(AFBACKUPOPTION_EXCH, pBackupOption_Exch);// backup option for Exchange
		JS_FREE_POINTER(AFBACKUPOPTION_SP, pAFSharePointOption);         // Backup option for Shar
		JS_FREE(dwEncryptTypeRestore);			//<sonmi01>2010-5-6 enc
		JS_FREE(pwszEncryptPasswordRestore);		//<sonmi01>2010-5-6 enc
		JS_FREE_OBJECT(VC_ESX_CREDENTIALS_JobScript, m_VCESXCredentials);
		JS_FREE_OBJECT(VM_Info_JobScript, m_VMInfo);
		JS_FREE_OBJECT(GuestHost_Credentials_JobScript, m_GuestHostCredentials);
		JS_FREE_OBJECT(VSphereRestore_JobScript, m_VSphereRestore_JobScript); //<sonmi01>2010-8-18
		//JS_FREE(ULONG	m_UnderlyingHypervisorCount);						//<sonmi01>2014
		JS_FREE_LIST(VC_ESX_CREDENTIALS_JobScript, m_UnderlyingHypervisorCount, m_pUnderlyingHypervisor);	//vCloud attach
		JS_FREE_LIST(_AFNODE, m_ulChildVMCount, m_pChildVMs);
		JS_FREE(m_storagePolicyGuid);
		JS_FREE(m_storagePolicyName);
		JS_FREE(TransportMode);
		JS_FREE(strNetworkMapXml);
		JS_FREE(ulJobId);
        JS_FREE(bRunCommandEvenFailed);
	}

	/// Shared by Hyper-V HBBU and ESX HBBU.
	/// For Hyper-V HBBU: enum VMBackupExpectation{ VM_BACKUP_EXPECTATION_CONSISTENT_ONLINE = 1, VM_BACKUP_EXPECTATION_ONLINE, VM_BACKUP_EXPECTATION_CONSISTENT }
	/// For ESX HBBU:  0 - default value, use VMware snapshot method; 1 - use customized snapshot method (Windows guest VSS snapshot)
    /// 2 - means Don't quiesce VM
	ULONG			  ulSnapshotMethod;

	/// For Hyper-V HBBU usage. FALSE - default value, share Stub backup; TRUE - use dedicated Stub
	BOOL			  bUseDedicatedStub;
    BOOL              bRunCommandEvenFailed;
} AFNODE, *PAFNODE;

//October sprint 
typedef struct _AFSTORAGEAPPLIANCE
{
	//Dec sprint
	PWCHAR			pwszSystemMode;
	PWCHAR			pwszDataIP;
	//PWCHAR			pwszNodeName;					
	PWCHAR			pwszNodeName;					// IP address
	PWCHAR			pwszUserName;					// User name
	PWCHAR			pwszPassword;						// User password
	PWCHAR			pwszProtocol;					// Protocol
	PWCHAR			pwszPort;						// Port


	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFSTORAGEAPPLIANCE)
	{
		//Dec sprint
		JS_CLONE(pwszSystemMode);
		JS_CLONE(pwszDataIP);
		JS_CLONE(pwszNodeName);
		JS_CLONE(pwszUserName);
		JS_CLONE(pwszPassword);
		JS_CLONE(pwszProtocol);
		JS_CLONE(pwszPort);
	}

	JS_FREE_FUNCTION(_AFSTORAGEAPPLIANCE)
	{
		//Dec sprint
		JS_FREE(pwszSystemMode);
		JS_FREE(pwszDataIP);
		JS_FREE(pwszNodeName);
		JS_FREE(pwszUserName);
		JS_FREE(pwszPassword);
		JS_FREE(pwszProtocol);
		JS_FREE(pwszPort);
	}

}AFSTORAGEAPPLIANCE, *PAFSTORAGEAPPLIANCE;

typedef struct _GDDINFORMATION
{
	LONGLONG RawSize;
	LONGLONG CompressedSize;
	DWORD CompressRatio;
	DWORD CompressPercentage;

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_GDDINFORMATION)
	{
		JS_CLONE(RawSize);
		JS_CLONE(CompressedSize);
		JS_CLONE(CompressRatio);
		JS_CLONE(CompressPercentage);
	}

	JS_FREE_FUNCTION(_GDDINFORMATION)
	{
		JS_FREE(RawSize);
		JS_FREE(CompressedSize);
		JS_FREE(CompressRatio);
		JS_FREE(CompressPercentage);
	}

}GDDInformation, pGDDInformation;


// [2/9/2015 zhahu03] File Copy Constant
#define AFFILECOPY_TYPE_DEFAULT			0
#define AFFILECOPY_TYPE_N_BACKUP		1
#define AFFILECOPY_TYPE_ADV_SCHEDULE	2

typedef struct _AFARCHIVE_INFO
{
	BOOL  bFileCopyFeatureEnabled;      // If FileCopy feature enabled or not.
	DWORD dwFileCpySchType;             // File Copy schedule type: Repeat after N or Advance Schedule

	DWORD dwSubmitArchiveAfterNBackups; // For Repeat After N backup. 
	BOOL  bDailyBackup;                 // For Advance Schedule 
	BOOL  bWeeklyBackup;          // For Advance Schedule
	BOOL  bMonthlyBackup;         // For Advance Schedule

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFARCHIVE_INFO)
	{
		JS_CLONE(bFileCopyFeatureEnabled);
		JS_CLONE(dwFileCpySchType);
		JS_CLONE(dwSubmitArchiveAfterNBackups);
		JS_CLONE(bDailyBackup);
		JS_CLONE(bWeeklyBackup);
		JS_CLONE(bMonthlyBackup);
	}

	JS_FREE_FUNCTION(_AFARCHIVE_INFO)
	{
		JS_FREE(bFileCopyFeatureEnabled);
		JS_FREE(dwFileCpySchType);
		JS_FREE(dwSubmitArchiveAfterNBackups);
		JS_FREE(bDailyBackup);
		JS_FREE(bWeeklyBackup);
		JS_FREE(bMonthlyBackup);
	}
} AFARCHIVE_INFO, *PAFARCHIVE_INFO;


typedef struct _AFJOBSCRIPT
{
	ULONG		   ulVersion;				// xml version
	ULONG        ulShrMemID;				// Unique ID -- used to compose unique sharememory filename	
	ULONG			ulChildJobId;			// if master job, child job is is 0; child job id begin with 1, 2, 3... //<sonmi01>2014-6-4 ###???
	ULONG			ulChildCount;			// how many children jobs in the master job
	ULONG        usJobType;				// backup and restore. Resync is a kind of job method
	ULONG			ulSubJobType;			// <sonmi01>2014-6-4 ###???
	ULONG			MasterJobId;			//<sonmi01>2014-8-20 ###???
	ULONG			MasterJobType;
	ULONG			MasterJobFlag;
	ULONG			MasterJobEnum;
	ULONG        nNodeItems;				// # of node
	PAFNODE 	   pAFNodeList;				// node list, it holds backup/restore source info 
	PWCHAR	   pwszDestPath;			// backup:STOR's destination folder, restore: the altenative location use selected
	PWCHAR	   pwszUserName;			//user name for accessing backup destination;restore and copy source //<sonmi01>2009-8-25 ###???
	PWCHAR       pwszPassword;			//password for accessing backup destination;restore and copy source
	PWCHAR	   pwszUserName_2;			//user name to accessing restore and copy destination
	PWCHAR       pwszPassword_2;			//password to accessing restore and copy destination
	PWCHAR       pwszComments;			// Comments or description
	PWCHAR       pwszBeforeJob;			// Process string before job starts
	PWCHAR	   pwszPostSnapshotCmd;		// Process string run after snapshot is taken
	PWCHAR       pwszAfterJob;			// Process string after job ends
	PWCHAR	   pwszPrePostUser;			// pre-post username																//new adding
	PWCHAR	   pwszPrePostPassword;		// pre-post password																//new adding
	ULONG        usPreExitCode;			// Exit code for Application wich run before
	ULONG        usJobMethod;				// Job Methods -- Incremental/Full/Resync
	ULONG		   usRestPoint;				// restore point
	ULONG        fOptions;				// flags
	DWORD		   dwCompressionLevel;		// VHD compression level //<sonmi01>2009-8-26 compressed VHD
	DWORD        dwJobHistoryDays;       //days to reserve job history<zouyu01>2009-10-14, default is 10 days.
	DWORD        dwSqlLogDays;           //Days limitation to purge sql log files. 0 means doesn't purge.
	DWORD        dwExchangeLogDays;      //Days limitation to purge exchange log files. 0 means doesn't purge.
	DWORD        dwEncryptType;			//<sonmi01>2010-5-6 encrypt support
	PWCHAR	   pwszEncryptPassword;		//<sonmi01>2010-5-6 encrypt support
	PWCHAR	   pwszVDiskPassword;		//<sonmi01>2011-9-13 RPS password management - D2D session password and RPS vdisk data password
	DWORD		   dwEncryptTypeCopySession;			//<sonmi01>2010-5-6 encrypt support
	PWCHAR	   pwszEncryptPasswordCopySession;		//<sonmi01>2010-5-6 encrypt support
	BOOL		bRetainEncryptionAsSource;	// for crp job - retain encyption information as source.
	DWORD        dwThroughoutThrottling;  //ZZ: Backup or restore throughout throttling. MB/min
	ULONG		   ulJobAttribute; //<sonmi01>2010-12-24 D2D VM job monitor
	PWCHAR	   launcherInstanceUUID; //<sonmi01>2011-2-16 per xiang required
	PWCHAR	   pRPSName;
	PWCHAR       pwzRPSSvrSID;        //ZZ: [2013/11/13 13:26] RPS server identity
	PWCHAR	   pPolicyName;
	PWCHAR	   pPolicyGUID;
	PWCHAR       pSourceDataStore;
	PWCHAR       pSourceDataStoreName;
	PWCHAR       pTargetDataStore;
	PWCHAR       pTargetDataStoreName;
	GDDInformation gInfo;
	ULONG        ulPreAllocationSpace;  //<huvfe01>2012-11-7 for defect#102889
	ULONGLONG	   ullScheduledTime;	  // the job scheduled time
	BOOL         bUseHardwareProvider;
	BOOL		   bFallBackToSoftProvider;
	BOOL		   bUseTransportable;
	PWCHAR	   pGeneratedDestinationPath; //<caowe01> 2014/09/12 vApp child VM backup destination path
	//October sprint 

	ULONG        nStorageApplianceItems;				// # of StorageAppliances
	PAFSTORAGEAPPLIANCE 	   pAFStorageApplianceList;
	
	AFARCHIVE_INFO stArchiveInfo;		// archive/filecopy info

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFJOBSCRIPT)
	{
		JS_CLONE(ulVersion);				// xml ve
		JS_CLONE(ulShrMemID);				// Unique
		JS_CLONE(ulChildJobId);			// if mas
		JS_CLONE(ulChildCount);			// how ma
		JS_CLONE(usJobType);				// backup
		JS_CLONE(ulSubJobType);			// <sonmi

		JS_CLONE(MasterJobId);			//<sonmi01>2014-8-20 ###???
		JS_CLONE(MasterJobType);
		JS_CLONE(MasterJobFlag);
		JS_CLONE(MasterJobEnum);

		//JS_CLONE(ULONG        nNodeItems);				// # of n
		JS_CLONE_LIST(AFNODE, nNodeItems, pAFNodeList);				// node l
		JS_CLONE(pwszDestPath);			// backup
		JS_CLONE(pwszUserName);			//user na
		JS_CLONE(pwszPassword);			//passwor
		JS_CLONE(pwszUserName_2);			//user na
		JS_CLONE(pwszPassword_2);			//passwor
		JS_CLONE(pwszComments);			// Commen
		JS_CLONE(pwszBeforeJob);			// Proces
		JS_CLONE(pwszPostSnapshotCmd);		// Proces
		JS_CLONE(pwszAfterJob);			// Proces
		JS_CLONE(pwszPrePostUser);			// pre-po
		JS_CLONE(pwszPrePostPassword);		// pre-po
		JS_CLONE(usPreExitCode);			// Exit c
		JS_CLONE(usJobMethod);				// Job Me
		JS_CLONE(usRestPoint);				// restor
		JS_CLONE(fOptions);				// flags
		JS_CLONE(dwCompressionLevel);		// VHD co
		JS_CLONE(dwJobHistoryDays);       //days to 
		JS_CLONE(dwSqlLogDays);           //Days lim
		JS_CLONE(dwExchangeLogDays);      //Days lim
		JS_CLONE(dwEncryptType);			//<sonmi0
		JS_CLONE(pwszEncryptPassword);		//<sonmi0
		JS_CLONE(pwszVDiskPassword);		//<sonmi0
		JS_CLONE(dwEncryptTypeCopySession);
		JS_CLONE(pwszEncryptPasswordCopySession);
		JS_CLONE(dwThroughoutThrottling);  //ZZ: Bac
		JS_CLONE(ulJobAttribute); //<sonmi01>2010-12
		JS_CLONE(launcherInstanceUUID); //<sonmi01>2
		JS_CLONE(pRPSName);
		JS_CLONE(pwzRPSSvrSID);        //ZZ: [2013/1
		JS_CLONE(pPolicyName);
		JS_CLONE(pPolicyGUID);
		JS_CLONE(pSourceDataStore);
		JS_CLONE(pSourceDataStoreName);
		JS_CLONE(pTargetDataStore);
		JS_CLONE(pTargetDataStoreName);
		JS_CLONE_OBJECT(GDDInformation, gInfo);
		JS_CLONE(ulPreAllocationSpace);  //<huvfe01>
		JS_CLONE(ullScheduledTime);	  // the job 
		JS_CLONE(bUseHardwareProvider);
		JS_CLONE(bFallBackToSoftProvider);
		JS_CLONE(bUseTransportable);
		JS_CLONE(pGeneratedDestinationPath);
		JS_CLONE_LIST(AFSTORAGEAPPLIANCE, nStorageApplianceItems, pAFStorageApplianceList);
		JS_CLONE_OBJECT(AFARCHIVE_INFO, stArchiveInfo);
	}

	JS_FREE_FUNCTION(_AFJOBSCRIPT)
	{
		JS_FREE(ulVersion);				// xml ve
		JS_FREE(ulShrMemID);				// Unique
		JS_FREE(ulChildJobId);			// if mas
		JS_FREE(ulChildCount);			// how ma
		JS_FREE(usJobType);				// backup
		JS_FREE(ulSubJobType);			// <sonmi

		JS_FREE(MasterJobId);			//<sonmi01>2014-8-20 ###???
		JS_FREE(MasterJobType);
		JS_FREE(MasterJobFlag);
		JS_FREE(MasterJobEnum);

		//JS_FREE(ULONG        nNodeItems);				// # of n
		JS_FREE_LIST(AFNODE, nNodeItems, pAFNodeList);				// node l
		JS_FREE(pwszDestPath);			// backup
		JS_FREE(pwszUserName);			//user na
		JS_FREE(pwszPassword);			//passwor
		JS_FREE(pwszUserName_2);			//user na
		JS_FREE(pwszPassword_2);			//passwor
		JS_FREE(pwszComments);			// Commen
		JS_FREE(pwszBeforeJob);			// Proces
		JS_FREE(pwszPostSnapshotCmd);		// Proces
		JS_FREE(pwszAfterJob);			// Proces
		JS_FREE(pwszPrePostUser);			// pre-po
		JS_FREE(pwszPrePostPassword);		// pre-po
		JS_FREE(usPreExitCode);			// Exit c
		JS_FREE(usJobMethod);				// Job Me
		JS_FREE(usRestPoint);				// restor
		JS_FREE(fOptions);				// flags
		JS_FREE(dwCompressionLevel);		// VHD co
		JS_FREE(dwJobHistoryDays);       //days to 
		JS_FREE(dwSqlLogDays);           //Days lim
		JS_FREE(dwExchangeLogDays);      //Days lim
		JS_FREE(dwEncryptType);			//<sonmi0
		JS_FREE(pwszEncryptPassword);		//<sonmi0
		JS_FREE(pwszVDiskPassword);		//<sonmi0
		JS_FREE(dwEncryptTypeCopySession);
		JS_FREE(pwszEncryptPasswordCopySession);
		JS_FREE(dwThroughoutThrottling);  //ZZ: Bac
		JS_FREE(ulJobAttribute); //<sonmi01>2010-12
		JS_FREE(launcherInstanceUUID); //<sonmi01>2
		JS_FREE(pRPSName);
		JS_FREE(pwzRPSSvrSID);        //ZZ: [2013/1
		JS_FREE(pPolicyName);
		JS_FREE(pPolicyGUID);
		JS_FREE(pSourceDataStore);
		JS_FREE(pSourceDataStoreName);
		JS_FREE(pTargetDataStore);
		JS_FREE(pTargetDataStoreName);
		JS_FREE_OBJECT(GDDInformation, gInfo);
		JS_FREE(ulPreAllocationSpace);  //<huvfe01>
		JS_FREE(ullScheduledTime);	  // the job 
		JS_FREE(bUseHardwareProvider);
		JS_FREE(bFallBackToSoftProvider);
		JS_FREE(bUseTransportable);
		JS_FREE(pGeneratedDestinationPath);
		JS_FREE_LIST(AFSTORAGEAPPLIANCE, nStorageApplianceItems, pAFStorageApplianceList);				//October Sprint
		JS_FREE_OBJECT(AFARCHIVE_INFO, stArchiveInfo);
	}

} AFJOBSCRIPT, *PAFJOBSCRIPT;
//end  structure to hold job script converting from xml file

#define IsJobManagedByRPS(pJobScript) \
	((pJobScript)->pRPSName && (pJobScript)->pRPSName[0])


//danri02: Begin for data store purge job
typedef struct _AFPurgeNodeInfo
{
	wchar_t		wszNodeName[MAX_PATH];
	wchar_t		wszNodeID[MAX_PATH];
	BOOL		bIsVMNode;
	wchar_t		wszHostName[MAX_PATH];
}AFPURGENODEINFO, *PAFPURGENODEINFO;

typedef struct _AFPURGEDATAJOBSCRIPT
{
	ULONG		 ulVersion;				// This structure version, default = 1
	ULONG        ulJobNum;				// Unique ID -- used to compose unique share memory filename	
	ULONG        usJobType;				// Delete Job Type			
	ULONG        nNodeItems;			// # of node
	PAFPURGENODEINFO 	 pAFNodeList;	// node list that will be purge.
	PWCHAR		 pwszDestPath;			// backup:STOR's destination folder, restore: the alternative location use selected
	PWCHAR		 pwszUserName;			//user name for accessing backup destination;restore and copy source //<sonmi01>2009-8-25 ###???
	PWCHAR       pwszPassword;			//password for accessing backup destination;restore and copy source
	PWCHAR       pwszComments;			// Comments or description
	ULONG		 fOptions;				// flags
	ULONG		 ulJobAttribute;		//<sonmi01>2010-12-24 D2D VM job monitor
	PWCHAR		 pRPSName;
	PWCHAR       pwzRPSSvrSID;			//ZZ: [2013/11/13 13:26] RPS server identity
	PWCHAR		 pPolicyName;
	PWCHAR		 pPolicyGUID;
	PWCHAR       pDataStore;
	PWCHAR       pDataStoreName;
	PWCHAR		 pwszMonitorEventName;
	ULONGLONG	 ullScheduledTime;	  // the job scheduled time
}AFPURGEDATAJOBSCRIPT, *PAFPURGEDATAJOBSCRIPT;
//danri02: End for data store purge job


typedef struct _AFCATALOGJOB
{
	ULONG        ulShrMemID;				// Unique ID -- used to compose unique sharememory filename	
	ULONG        ulSessNum;				// Session number
	ULONG        ulSubSessNum;			// Sub session number
	PWCHAR	   pwszDestPath;			// backup:STOR's destination folder, restore: the altenative location use selected
	PWCHAR	   pwszUserName;			//user name for accessing backup destination;restore and copy source //<sonmi01>2009-8-25 ###???
	PWCHAR       pwszPassword;			//password for accessing backup destination;restore and copy source

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFCATALOGJOB)
	{
		JS_CLONE(ulShrMemID);
		JS_CLONE(ulSessNum);
		JS_CLONE(ulSubSessNum);
		JS_CLONE(pwszDestPath);
		JS_CLONE(pwszUserName);
		JS_CLONE(pwszPassword);
	}

	JS_FREE_FUNCTION(_AFCATALOGJOB)
	{
		JS_FREE(ulShrMemID);
		JS_FREE(ulSessNum);
		JS_FREE(ulSubSessNum);
		JS_FREE(pwszDestPath);
		JS_FREE(pwszUserName);
		JS_FREE(pwszPassword);
	}


}AFCATALOGJOB, *PAFCATALOGJOB;

//begin  structure to hold Archive job script converting from xml file

typedef struct _AFARCHIVECLOUDDESTINFO
{
	//Proxy details

	BOOL        bUseProxy;
	PWCHAR      pwszProxyServerName;
	DWORD       dwProxyServerPort;
	BOOL        bproxyRequiresAuth;
	PWCHAR      pwszProxyPassword;
	PWCHAR      pwszproxyUserName;

	//destination path in the cloud
	PWCHAR      pwszCloudBucketName;   // only for S3.as of now user can't change destination for
	// IRM .with out changing vendor info.
	PWCHAR      pwszCloudDisplayBucketName;
	PWCHAR      pwszCloudBucketRegionName;

	//Cloud vendor information
	DWORD       dwCloudVendorType;	  // CCI data type use enums as defined in CCI inteface H
	PWCHAR      pwszVendorHostname;	  // only for I365   
	PWCHAR      pwszVendorUsername;   // Access Key for IRM
	PWCHAR      pwszVendorPassword;    // Secert for IRM
	PWCHAR		pwszVendorCertificatePath;//only for IRM 
	PWCHAR		pwszCertificatePassword; // only for IRM
	PWCHAR		pwszCloudVendorURL;  // Applicable for both S3 and IRM	
	DWORD		dwVendorPort;    // as of now it's value is 80, may change in future.
	DWORD		dwDestinationProperties;	// will have destination properties eg: RRS flag for amazon.
	DWORD		dwCloudVendorSubType;  //This will contain vendor subtype for Azure like Fuzitsu


	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFARCHIVECLOUDDESTINFO)
	{
		JS_CLONE(bUseProxy);
		JS_CLONE(pwszProxyServerName);
		JS_CLONE(dwProxyServerPort);
		JS_CLONE(bproxyRequiresAuth);
		JS_CLONE(pwszProxyPassword);
		JS_CLONE(pwszproxyUserName);
		JS_CLONE(pwszCloudBucketName);   // onl
		JS_CLONE(pwszCloudDisplayBucketName);
		JS_CLONE(pwszCloudBucketRegionName);
		JS_CLONE(dwCloudVendorType);	  // CCI 
		JS_CLONE(pwszVendorHostname);	  // only
		JS_CLONE(pwszVendorUsername);   // Acce
		JS_CLONE(pwszVendorPassword);    // Sec
		JS_CLONE(pwszVendorCertificatePath);//o
		JS_CLONE(pwszCertificatePassword); // o
		JS_CLONE(pwszCloudVendorURL);  // Appli
		JS_CLONE(dwVendorPort);    // as of now
		JS_CLONE(dwDestinationProperties);
		JS_CLONE(dwCloudVendorSubType);  //This
	}

	JS_FREE_FUNCTION(_AFARCHIVECLOUDDESTINFO)
	{
		JS_FREE(bUseProxy);
		JS_FREE(pwszProxyServerName);
		JS_FREE(dwProxyServerPort);
		JS_FREE(bproxyRequiresAuth);
		JS_FREE(pwszProxyPassword);
		JS_FREE(pwszproxyUserName);
		JS_FREE(pwszCloudBucketName);   // onl
		JS_FREE(pwszCloudDisplayBucketName);
		JS_FREE(pwszCloudBucketRegionName);
		JS_FREE(dwCloudVendorType);	  // CCI 
		JS_FREE(pwszVendorHostname);	  // only
		JS_FREE(pwszVendorUsername);   // Acce
		JS_FREE(pwszVendorPassword);    // Sec
		JS_FREE(pwszVendorCertificatePath);//o
		JS_FREE(pwszCertificatePassword); // o
		JS_FREE(pwszCloudVendorURL);  // Appli
		JS_FREE(dwVendorPort);    // as of now
		JS_FREE(dwDestinationProperties);
		JS_FREE(dwCloudVendorSubType);  //This
	}

}ARCHIVECLOUDDESTINFO, *PARCHIVECLOUDDESTINFO;

typedef struct _AFARCHIVEDISKDESTINFO
{
	PWCHAR pwszDestpath; // destination location for FS plugin
	PWCHAR pwszUserName; // destination user name for FS plugin
	PWCHAR pwszPassword; // destination password  for FS plugin

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFARCHIVEDISKDESTINFO)
	{
		JS_CLONE(pwszDestpath);
		JS_CLONE(pwszUserName);
		JS_CLONE(pwszPassword);
	}

	JS_FREE_FUNCTION(_AFARCHIVEDISKDESTINFO)
	{
		JS_FREE(pwszDestpath);
		JS_FREE(pwszUserName);
		JS_FREE(pwszPassword);
	}


}ARCHIVEDISKDESTINFO, *PARCHIVEDISKDESTINFO;

typedef struct _AFARCHIVERESTOREVOLITEMAPPCOMP
{
	PWCHAR		pwszFileorDir;                 // FS item path
	DWORD			fOptions;						// Options (File or Dir refer Enums for more info)
	DWORD         ulFileVersion;					// Version of the file to restore.

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFARCHIVERESTOREVOLITEMAPPCOMP)
	{
		JS_CLONE(pwszFileorDir);
		JS_CLONE(fOptions);
		JS_CLONE(ulFileVersion);
	}

	JS_FREE_FUNCTION(_AFARCHIVERESTOREVOLITEMAPPCOMP)
	{
		JS_FREE(pwszFileorDir);
		JS_FREE(fOptions);
		JS_FREE(ulFileVersion);
	}

}AFARCHIVEVOLITEMAPPCOMP, *PAFARCHIVEVOLITEMAPPCOMP;

typedef struct _AFARCHIVERESTVOLAPP
{
	DWORD							fOptions;					// Options    		
	DWORD							OnConflictMethod;			// On Conflict Methods -- used by restore job
	DWORD							ulSubSessNum;				// subsession # reserved not used.
	DWORD 						ulFileSystem;				// file system, app type etc
	LONGLONG						lVolAppSize;				// whole volume/whole app size
	PWCHAR						pDestVolumeName;			// destination volume name
	PWCHAR						pwszPath;					// writer path
	DWORD							nDestItemCount;				// destintaion map to nVolItemAppComp   
	PAFARCHIVEVOLITEMAPPCOMP		pDestItemList;				// destinationlist map to pVolItemAppCompList
	DWORD							nVolItemAppComp;			// # of VolItem and AppComp in list	
	PAFARCHIVEVOLITEMAPPCOMP		pVolItemAppCompList;		// List of VolItem & App Components	
	PVOID							pvFilterList;               // List of filters	
	DWORD							nFilterItems;               // # of Filter items in list

	JS_CLONE_FUNCTION(_AFARCHIVERESTVOLAPP)
	{
		JS_CLONE(fOptions);
		JS_CLONE(OnConflictMethod);
		JS_CLONE(ulSubSessNum);
		JS_CLONE(ulFileSystem);
		JS_CLONE(lVolAppSize);
		JS_CLONE(pDestVolumeName);
		JS_CLONE(pwszPath);
		//JS_CLONE(DWORD							nDestItemCount);
		JS_CLONE_LIST(AFARCHIVEVOLITEMAPPCOMP, nDestItemCount, pDestItemList);
		//JS_CLONE(DWORD							nVolItemAppComp);
		JS_CLONE_LIST(AFARCHIVEVOLITEMAPPCOMP, nVolItemAppComp, pVolItemAppCompList);
		//JS_CLONE(PVOID							pvFilterList);
		JS_CLONE(nFilterItems);
	}

	JS_FREE_FUNCTION(_AFARCHIVERESTVOLAPP)
	{
		JS_FREE(fOptions);
		JS_FREE(OnConflictMethod);
		JS_FREE(ulSubSessNum);
		JS_FREE(ulFileSystem);
		JS_FREE(lVolAppSize);
		JS_FREE(pDestVolumeName);
		JS_FREE(pwszPath);
		//JS_FREE(DWORD							nDestItemCount);
		JS_FREE_LIST(AFARCHIVEVOLITEMAPPCOMP, nDestItemCount, pDestItemList);
		//JS_FREE(DWORD							nVolItemAppComp);
		JS_FREE_LIST(AFARCHIVEVOLITEMAPPCOMP, nVolItemAppComp, pVolItemAppCompList);
		//JS_FREE(PVOID							pvFilterList);
		JS_FREE(nFilterItems);
	}

} AFARCHIVERESTVOLAPP, *PAFARCHIVERESTVOLAPP;



typedef struct _AFARCHIVEBACKUPVOLAPP
{
	DWORD			fOptions;						// Options
	PWCHAR		pwszFileorDir;                 // App Component or FS item path
	PVOID			pvFilterList;					// List of filters	
	DWORD			nFilterItems;					// # of Filter items in list		  

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFARCHIVEBACKUPVOLAPP)
	{
		JS_CLONE(fOptions);
		JS_CLONE(pwszFileorDir);
		//JS_CLONE(PVOID			pvFilterList);
		JS_CLONE(nFilterItems);
	}

	JS_FREE_FUNCTION(_AFARCHIVEBACKUPVOLAPP)
	{
		JS_FREE(fOptions);
		JS_FREE(pwszFileorDir);
		//JS_FREE(PVOID			pvFilterList);
		JS_FREE(nFilterItems);
	}

}AFARCHIVEBACKUPVOLAPP, *PAFARCHIVEBACKUPVOLAPP;


typedef struct _AFARCHIVESOURCE
{
	DWORD						fOptions;				// Options
	DWORD						nVolItemAppComp;		// it is 0 for backup job
	PWCHAR					pwszVolName;			// Volume - C: D:, when backup app writer, GUI converts writer to volumes
	PAFARCHIVEBACKUPVOLAPP	pVolItemAppCompList;	// it is NULl for backup job			 

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFARCHIVESOURCE)
	{
		JS_CLONE(fOptions);
		//JS_CLONE(DWORD						nVolItemAppComp);
		JS_CLONE(pwszVolName);
		JS_CLONE_LIST(AFARCHIVEBACKUPVOLAPP, nVolItemAppComp, pVolItemAppCompList);
	}

	JS_FREE_FUNCTION(_AFARCHIVESOURCE)
	{
		JS_FREE(fOptions);
		//JS_FREE(DWORD						nVolItemAppComp);
		JS_FREE(pwszVolName);
		JS_FREE_LIST(AFARCHIVEBACKUPVOLAPP, nVolItemAppComp, pVolItemAppCompList);
	}

} AFARCHIVESOURCE, *PAFARCHIVESOURCE;

typedef struct _AFARCHIVEAFNODE
{
	PWCHAR					pwszNodeName;					// save both domain and node name here
	PWCHAR					pwszNodeAddr;					// IP address
	PWCHAR					pwszUserName;					// User name
	PWCHAR					pwszUserPW;						// User password
	PWCHAR					pwszSessPath;					// Master Session Path 	
	DWORD						ulSessNum;						// master session #
	DWORD						nVolumeApp;						// # of Volume/App	
	PAFARCHIVESOURCE			pArchiveVolumeAppList;				// set to NULL if it is restore job.			
	PAFARCHIVERESTVOLAPP		pRestoreVolumeAppList;			// set to NULL if it is backup job
	DWORD						nFilterItems;					// # of Filter items in list		
	PVOID						pvFilterList;					// List of filters	
	DWORD						fOptions;						// Node level options

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFARCHIVEAFNODE)
	{
		JS_CLONE(pwszNodeName);
		JS_CLONE(pwszNodeAddr);
		JS_CLONE(pwszUserName);
		JS_CLONE(pwszUserPW);
		JS_CLONE(pwszSessPath);
		JS_CLONE(ulSessNum);
		//JS_CLONE(DWORD						nVolumeApp);
		JS_CLONE_LIST(AFARCHIVESOURCE, nVolumeApp, pArchiveVolumeAppList);
		JS_CLONE_LIST(AFARCHIVERESTVOLAPP, nVolumeApp, pRestoreVolumeAppList);
		JS_CLONE(nFilterItems);
		//JS_CLONE(PVOID						pvFilterList);
		JS_CLONE(fOptions);

	}

	JS_FREE_FUNCTION(_AFARCHIVEAFNODE)
	{
		JS_FREE(pwszNodeName);
		JS_FREE(pwszNodeAddr);
		JS_FREE(pwszUserName);
		JS_FREE(pwszUserPW);
		JS_FREE(pwszSessPath);
		JS_FREE(ulSessNum);
		//JS_FREE(DWORD						nVolumeApp);
		JS_FREE_LIST(AFARCHIVESOURCE, nVolumeApp, pArchiveVolumeAppList);
		JS_FREE_LIST(AFARCHIVERESTVOLAPP, nVolumeApp, pRestoreVolumeAppList);
		JS_FREE(nFilterItems);
		//JS_FREE(PVOID						pvFilterList);
		JS_FREE(fOptions);

	}

} AFARCHIVENODE, *PAFARCHIVENODE;


//<sonmi01>2009-7-7 ###???
//AFJOBSCRIPT::usJobType
#define AF_JOBTYPE_BACKUP					0	//backup job
#define AF_JOBTYPE_RESTORE					1	//restore job
#define AF_JOBTYPE_COPY						2	//copy job - recovery point management
#define AF_JOBTYPE_BACKUP_VMWARE			3	//VSphere vmware backup
#define AF_JOBTYPE_BACKUP_HYPERV			4	//VSphere hyperv backup
#define AF_JOBTYPE_RESTORE_VMWARE			5
//#define AF_JOBTYPE_RESTORE_HYPERV			6
#define AF_JOBTYPE_FILECOPY_BACKUP   		8	//filecopy job
#define AF_JOBTYPE_ARCHIVE_PURGE			9	//archive purge job
#define AF_JOBTYPE_ARCHIVE_RESTORE			10	//archive
#define AF_JOBTYPE_FS_CATALOG_GEN           11
#define AF_JOBTYPE_APP_CATALOG_GEN          12
#define AF_JOBTYPE_EXCH_GRT_CATALOG_GEN     13
#define AF_JOBTYPE_ARCHIVE_CATALOGRESYNC	14	// catalog resync job
#define AF_JOBTYPE_FS_CATALOG_GEN_VM        15
#define AF_JOBTYPE_FS_CATALOG_DISABLE       16
#define AF_JOBTYPE_DIRECT_RESTORE_VM_PROXY	17
#define AF_JOBTYPE_DIRECT_RESTORE_VM_STUB	18
#define AF_JOBTYPE_FILECOPY_SOURCEDELETE    19

#define AF_JOBTYPE_FS_CATALOG_DISABLE_VM	20

#define AF_JOBTYPE_INTER_MERGE				21	//<sonmi01>2011-8-4 ###???
#define AF_JOBTYPE_RPS_REPLICATION			22	//<sonmi01>2011-9-14 ###???
#define AF_JOBTYPE_RPS_REPLICATION_IN       24  // <huvfe01>for replication in job
#define AF_JOBTYPE_BACKUP_VMWARE_APP		25	//<sonmi01>2014-6-20 ###??? //VSphere VMware vCloud vApp backup
#define AF_JOBTYPE_RESTORE_VMWARE_APP       26  //<huvfe01>2014-7-21
#define AF_JOBTYPE_MERGE                    30  // for merge job
#define AF_JOBTYPE_VM_MERGE                 31  // for merge job
#define AF_JOBTYPE_MERGE_RPS				32  // rps merge job
#define AF_JOBTYPE_CONVERSION				40  // virtual conversion
#define AF_JOBTYPE_RPS_CONVERSION			41  // rps virtual conversion

#define AF_JOBTYPE_BMR						42  //for bmr job

#define AF_JOBTYPE_RPS_DATA_SEEDING			43  //for data seeding job
#define AF_JOBTYPE_RPS_DATA_SEEDING_IN		44	//for data seeding in bound job

#define AF_JOBTYPE_RESTORE_HYPERV			50   // for hyperv restore
#define AF_JOBTYPE_PURGE_DATASTORE			51	 // for purge data of node or whole data store
#define AF_JOBTYPE_START_INSTANTVM			60	// For start instant vm job
#define AF_JOBTYPE_STOP_INSTANTVM			61	// For stop instant vm job
#define AF_JOBTYPE_ASSURED_RECOVERY			62	// For assured recovery

#define AF_JOBTYPE_START_INSTANT_VHD	    63	// For start instant VHD
#define AF_JOBTYPE_STOP_INSTANT_VHD	        64	// For stop instant VHD

#define AF_JOBTYPE_ARCHIVE					70 // filecopy archive
#define AF_JOBTYPE_ARCHIVE_TO_TAPE			71 // archive to tape

#define AF_JOBTYPE_CHECK_RECOVERY_POINT			    80 // check point

#define AF_JOBTYPE_INTER_MERGE_STRING       L"Intermediate Merge Job"
#define AF_JOBTYPE_RPS_REPLICATION_STRING   L"RPS Replication Job"
//AFJOBSCRIPT::usJobMethod related to AFJOBSCRIPT::usJobType AF_JOBTYPE_BACKUP
#define AF_JOBMETHOD_FULL		0		//full backup
#define AF_JOBMETHOD_INCR		1		//incremental backup
#define AF_JOBMETHOD_RESYNC		2		//resync backup
//</sonmi01>

#define NS_NT           5               // Windows NT file

// On Conflict Methods (for Archive and Restore)
#define ONCONFLICT_SKIP         1          // On conflict skip
#define ONCONFLICT_RENAME       2          // On conflict rename
#define ONCONFLICT_REPLACE      3          // On conflict replace (overwrite)
#define ONCONFLICT_REPLNEW      4          // On conflict replace if newer
#define ONCONFLICT_REPLCONFIRM  5          // On conflict confirm replace (overwrite)

//ZZ: [2013/10/10 17:06] Added for advanced schedule to mark daily, weekly and monthly backup.
#define QJDTO_B_REAPEAT_BACKUP			0x00000000  //wanje04: Backup will be repeat
#define QJDTO_B_DAILY_BACKUP			0x00000001  //ZZ: Backup will be marked as daily 
#define QJDTO_B_WEEKLY_BACKUP			0x00000002  //ZZ: Backup will be marked as weekly
#define QJDTO_B_MONTHLY_BACKUP			0x00000004  //ZZ: Backup will be marked as monthly

// Job Level Options for Backup
#define QJDTO_B_PURGELOG_SQL			0x00000100 // Purge SQL's log files when backup job succeed
#define QJDTO_B_PURGELOG_EXCH			0x00000200 // Purge Exchange's log files when backup job succeed
#define QJDTO_B_DISABLE_CATALOG			0x00000400 // Set catalog status as disabled
#define QJDTO_B_RUN_JOB					0x00001000 // Run job on certain exit code
#define QJDTO_B_FAIL_JOB				0x00002000 // Fail job on certain exit code
#define QJDTO_B_CHECK_RECOVERYPOINT		0x00004000 // check recovery point/disk when backup job succeed

///ZZ: Added for supported backup set. I shall use AFJOBSCRIPT::fOptions to save this flag.
#define QJDTO_B_MARK_BACKUP_SET         0x00010000
//
//wanje04 2013-08-26, fOptions in job script. Below values was used to identify the backup session format
//
#define QJDTO_B_DISK_FORMAT_D2D         0x01000000 // CA VHD format
#define QJDTO_B_DISK_FORMAT_D2D2        0x02000000 // CA new VHD fomrat, internal call it merge phase 2 format
#define QJDTO_B_DISK_FORMAT_VHD         0x04000000 // Microsoft VHD format
#define QJDTO_B_DISK_FORMAT_GDD         0x10000000 // CA Dedup format

#define IS_OLD_D2D_FORMAT_ENABLED(Options)   (Options&QJDTO_B_DISK_FORMAT_D2D)
#define IS_MPII_D2D_FORMAT_ENABLED(Options)  (Options&QJDTO_B_DISK_FORMAT_D2D2)
#define IS_GDD_D2D_FORMAT_ENABLED(Options)  (Options&QJDTO_B_DISK_FORMAT_GDD)
//end wanje04


// Job Level Options
#define QJDTO_R_ONCONFLICT_REPLACE		0x00000001	// Overwrite existing file
#define QJDTO_R_REPLACE_ACTIVE			0x00000002	// Replace active file
#define QJDTO_R_ONCONFLICT_RENAME       0x00000004  // rename when conflict
#define QJDTO_FULLDTLRPTFILE			0x00000010	// Include all msg info in report file
#define QJDTO_R_CREATEVOLUMENAME        0x00200000         // create volume name in restore path.
#define QJDTO_R_CREATEWHOLEPATH			0x00400000	// Create whole path for destination
#define QJDTO_R_CREATETOPDIRS			0x00800000	// Create top directories
#define	QJDTO_R_ALTLOCATION_APP_DUMPFILEONLY		0x02000000	//app restore dumpfile only
#define	QJDTO_R_ALTLOCATION_EXCH_RSGRDB				0x04000000	//exchange RSG(RDB)
#define	QJDTO_R_ALTLOCATION_EXCH_NON_EXCHFOLDER		0x08000000	//exchange Non-Exchange Folder
#define QJDTO_R_ALTLOCATION_SQL_LOADAPP	0x10000000	// SQL restore to alternative location, load APP by Writer
#define QJDTO_R_ORGLOCATION				0x20000000	// Restore back to original location
#define QJDTO_R_MULTI_VOLUME				0x40000000	// Restore Multiple Volumes

//<huvfe01>2014-6-18 Job AF_JOBTYPE_RESTORE_HYPERV (50) options
#define HYPERV_VM_R_REGISTER2CLUSTER    0x00000001    //register to cluster
#define OPTION_GENERATE_NEW_VM_ID       0x00000002

#define ASTIO_TRAVERSEDIR                       0x00000001  // Traverse directories
#define ASTIO_FILEENTRY                         0x00000002  // Entry is a file		


//  ASFILTER_TIMERANGE File System Fields
//
#define ASTMOBJ_CREATETIME    0         // Creation Time
#define ASTMOBJ_MODIFYTIME    1         // Modify Time
#define ASTMOBJ_LASTACCESS    2         // Last Access Time

//
//  ASFILTER_TIMERANGE Time Range Types
//
#define ASTMRT_BEFORE         0         // Check before start time
#define ASTMRT_ONORBEFORE     0         // Check On or before start time
#define ASTMRT_AFTER          1         // Check On or after start time
#define ASTMRT_ONORAFTER      1         // Check before start time
#define ASTMRT_BETWEEN        2         // Check between start and end times
#define ASTMRT_WITHIN         3         // Check within n Days/Months/Years
#define ASTMRT_BEFOREXDAYS    4         // Check before n Days/Months/Years
#define ASTMRT_AFTERXDAYS     5         // Check after n Days/Months/Years

//
//  ASTMRT_WITHIN Time Units
//
#define ASTMRU_DAYS           0         // Time unit is in Days
#define ASTMRU_MONTHS         1         // Time unit is in Months
#define ASTMRU_YEARS          2         // Time unit is in Years

//
//  ASFILTER_SIZE Types
//
#define ASFST_EQ            0         // Check if file == size
#define ASFST_LT         	1         // Check if file < size
#define ASFST_GT            2         // Check if file > size
#define ASFST_IN         	3         // Check if size < file < size2

//
//  Filter Types
//
#define ASFT_VOLUME           1         // Volume (or Drive) filter
#define ASFT_FILEPATTERN      2         // File pattern filter
#define ASFT_DIRPATTERN       3         // Directory pattern filter
#define ASFT_TIMERANGE        4         // Time range filter
#define ASFT_ATTRIBUTE        5         // Attribute filter
#define ASFT_SIZE             7         // Size filter - New in Python

//
//  Include/Exclude
//
#define QJIE_INCLUDE          0         // Include files or dirs in list
#define QJIE_EXCLUDE          1         // Exclude files or dirs in list

#define  AF_SIZE_TYPE_LESS  0
#define  AF_SIZE_TYPE_GREATER  1
#define  AF_SIZE_TYPE_BETWEEN  2
#define  AF_SIZE_TYPE_EQUAL  3

//
//  Snapshot method
//
#define SNAPSHOT_METHOD_VMWARE_TOOLS            0       // VMware Tools
#define SNAPSHOT_METHOD_VSS_INSIDE              1       // Microsoft VSS inside VM
#define SNAPSHOT_METHOD_DONOT_QUIESCE_VM        2       // Don't quiesce VM

typedef struct tagASFILTER_TIMERANGE
{
	USHORT usFilterType;                  // See Filter Types
	USHORT nInclExcl;                     // See Include/Exclude
	USHORT nFSFields;                     // See File System Fields
	USHORT nTRType;                       // See Time Range Types
	ULONG  ulStartTime;                   // Time range start time
	ULONG  ulEndTime;                     // Time range end time
	USHORT nTimeUnit;                     // See ASTMRT_WITHIN Time Units
	USHORT nPadding1;                     // Used as time zone between ARCserve and UNIX agent
	// to support rotation differential/incremental job.
	// The value should be 0 in regular time filter
	UCHAR  aucReserved[12];               // Reserved for padding to 32 bytes

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagASFILTER_TIMERANGE)
	{
		JS_CLONE(usFilterType);
		JS_CLONE(nInclExcl);
		JS_CLONE(nFSFields);
		JS_CLONE(nTRType);
		JS_CLONE(ulStartTime);
		JS_CLONE(ulEndTime);
		JS_CLONE(nTimeUnit);
		JS_CLONE(nPadding1);
		//JS_CLONE(UCHAR  aucReserved[12];
	}

	JS_FREE_FUNCTION(tagASFILTER_TIMERANGE)
	{
		JS_FREE(usFilterType);
		JS_FREE(nInclExcl);
		JS_FREE(nFSFields);
		JS_FREE(nTRType);
		JS_FREE(ulStartTime);
		JS_FREE(ulEndTime);
		JS_FREE(nTimeUnit);
		JS_FREE(nPadding1);
		//JS_FREE(UCHAR  aucReserved[12];
	}

} ASFILTER_TIMERANGE;

typedef struct tagASFILTER_SIZE
{
	USHORT usFilterType;                  // See Filter Types
	USHORT nInclExcl;                     // See Include/Exclude
	USHORT nSizeType;                     // See Size Types
	UCHAR	 nMultipleType;         		// See multiple types
	UCHAR	 nMultipleType2;         		// See multiple types
	ULONG  ulLowSize;                     // Low size
	ULONG  ulHighSize;                    // High size
	ULONG  ulLowSize2;                    // Low size #2
	ULONG  ulHighSize2;                   // High size #2
	UCHAR  aucReserved[8];                // Reserved for padding to 32 bytes

	//////////////////////////////////////////////////////////////////////////JS_CLONE_FUNCTION(xxx)
	JS_CLONE_FUNCTION(tagASFILTER_SIZE)
	{
		JS_CLONE(usFilterType);
		JS_CLONE(nInclExcl);
		JS_CLONE(nSizeType);
		JS_CLONE(nMultipleType);
		JS_CLONE(nMultipleType2);
		JS_CLONE(ulLowSize);
		JS_CLONE(ulHighSize);
		JS_CLONE(ulLowSize2);
		JS_CLONE(ulHighSize2);
		//JS_CLONE(UCHAR  aucReserved[8]);
	}

	JS_FREE_FUNCTION(tagASFILTER_SIZE)
	{
		JS_FREE(usFilterType);
		JS_FREE(nInclExcl);
		JS_FREE(nSizeType);
		JS_FREE(nMultipleType);
		JS_FREE(nMultipleType2);
		JS_FREE(ulLowSize);
		JS_FREE(ulHighSize);
		JS_FREE(ulLowSize2);
		JS_FREE(ulHighSize2);
		//JS_FREE(UCHAR  aucReserved[8]);
	}

} ASFILTER_SIZE;

typedef struct tagASFILTER_ATTRIBUTE
{
	USHORT usFilterType;                  // See Filter Types
	USHORT nInclExcl;                     // See Include/Exclude
	FLAG32 fAttribute;                    // 32 bit Attribute
	UCHAR  aucReserved[24];               // Reserved for padding to 32 bytes

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagASFILTER_ATTRIBUTE)
	{
		JS_CLONE(usFilterType);
		JS_CLONE(nInclExcl);
		JS_CLONE(fAttribute);
		//JS_CLONE(UCHAR  aucReserved[24]);
	}

	JS_FREE_FUNCTION(tagASFILTER_ATTRIBUTE)
	{
		JS_FREE(usFilterType);
		JS_FREE(nInclExcl);
		JS_FREE(fAttribute);
		//JS_FREE(UCHAR  aucReserved[24]);
	}

} ASFILTER_ATTRIBUTE;

typedef struct tagASFILTER_FILE
{
	USHORT usFilterType;                  // See Filter Types
	USHORT nInclExcl;                     // See Include/Exclude
	ULONG  nItems;                        // Number of Incl/Excl items in list
	PTSZ   pszNameList;                   // Names to match (wildcards allowed)
	USHORT usDataSize;                    // Buffer size of pszNameList
	UCHAR  aucReserved[18];               // Reserved for padding to 32 bytes

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagASFILTER_FILE)
	{
		JS_CLONE(usFilterType);
		JS_CLONE(nInclExcl);
		JS_CLONE(nItems);
		JS_CLONE(pszNameList);
		JS_CLONE(usDataSize);
		//JS_CLONE(UCHAR  aucReserved[18]);
	}

	JS_FREE_FUNCTION(tagASFILTER_FILE)
	{
		JS_FREE(usFilterType);
		JS_FREE(nInclExcl);
		JS_FREE(nItems);
		JS_FREE(pszNameList);
		JS_FREE(usDataSize);
		//JS_FREE(UCHAR  aucReserved[18]);
	}

} ASFILTER_FILE;

typedef struct tagASFILTER_DIR
{
	USHORT usFilterType;                  // See Filter Types
	USHORT nInclExcl;                     // See Include/Exclude
	ULONG  nItems;                        // Number of Incl/Excl items in list
	PTSZ   pszNameList;                   // Names to match (wildcards allowed)
	USHORT usDataSize;                    // Buffer size of pszNameList
	UCHAR  aucReserved[18];               // Reserved for padding to 32 bytes

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagASFILTER_DIR)
	{
		JS_CLONE(usFilterType);
		JS_CLONE(nInclExcl);
		JS_CLONE(nItems);
		JS_CLONE(pszNameList);
		JS_CLONE(usDataSize);
		//JS_CLONE(UCHAR  aucReserved[18]);
	}

	JS_FREE_FUNCTION(tagASFILTER_DIR)
	{
		JS_FREE(usFilterType);
		JS_FREE(nInclExcl);
		JS_FREE(nItems);
		JS_FREE(pszNameList);
		JS_FREE(usDataSize);
		//JS_FREE(UCHAR  aucReserved[18]);
	}


} ASFILTER_DIR;

//originally defined in CSTOOL
DWORD  WINAPI FileTimeToPackedTime(PFILETIME);
USHORT WINAPI _dos_packdate(USHORT year, USHORT month, USHORT day);
USHORT WINAPI _dos_packtime(USHORT hour, USHORT minute, USHORT second);

PWCHAR WINAPI AnsiToUnicode(PCHAR s, PWCHAR ws, UINT limit);
PCHAR  WINAPI UnicodeToAnsi(PWCHAR ws, PCHAR s, UINT limit);
/*
//The following will be removed later after made change for APP restore repackage
typedef struct tagASTAPEITEM
{
PTSZ   pszFileorDir;                  // Fully qualified file or directory
FLAG32 fOptions;
}ASTAPEITEM,*PASTAPEITEM;

typedef struct tagASDTAPE
{
PTSZ   pszTapeName;                   // Tape name
USHORT ulTapeID;                      // unsigned short Tape ID (Random ID)
USHORT usTapeSeq;                     // Tape sequence number
ULONG  nTapeItems;                    // # of ASTAPEITEM items in list
PASTAPEITEM pASTapeItemList;          // List of ASTAPEITEM items
PTSZ   pszPath;                       // Session Path ie SYS: or SYS:\PUBLIC
ULONG  nFilterItems;                  // # of Filter items in list		//yvette
PVOID  pvFilterList;                  // List of filters				//yvette
}ASDTAPE, *PASDTAPE;

typedef struct tagASQJDATATRANSFER
{
FLAG32       fOptions;               // See Global, B, A, R Options
USHORT       OnConflictMethod;       // See On Conflict Methods (A, R)
}ASQJDATATRANSFER ,*PASQJDATATRANSFER;

typedef struct tagASNODE
{
ULONG  nFilterItems;                  // # of Filter items in list
PVOID  pvFilterList;                  // List of filters
} ASNODE, *PASNODE;

typedef struct tagASDISKITEM
{
PTSZ   pszPath;                       // Fully qualified path
}ASDISKITEM,*PASDISKITEM;

typedef struct tagASDDISK
{
PTSZ   pszDiskName;                   // Volume or Drive Name
ULONG  nDiskItems;                    // # of ASDISKITEM items in list
PASDISKITEM pASDiskItemList;          // List of ASDISKITEM items
}ASDDISK ,*PASDDISK;
*/
typedef ASFILTER_DIR *PASFILTER_DIR;
typedef ASFILTER_FILE *PASFILTER_FILE;
typedef ASFILTER_ATTRIBUTE *PASFILTER_ATTRIBUTE;
typedef ASFILTER_SIZE *PASFILTER_SIZE;
typedef ASFILTER_TIMERANGE *PASFILTER_TIMERANGE;

typedef struct tagARCHIVE_FILTER_FILEFOLDER
{
	USHORT nInclExcl;                     // See Include/Exclude
	PTSZ   pszFilterName;                 // Names to match (wildcards allowed)
	USHORT pszFilterNameSize;             // Buffer size of pszFilterName
	PTSZ   pszFilterDisplayName;          // Display name of the filter (office files), reserved for time being.
	USHORT pszFilterDisplayNameSize;      // Buffer size of pszFilterName , reserved for time being.

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(tagARCHIVE_FILTER_FILEFOLDER)
	{
		JS_CLONE(nInclExcl);
		JS_CLONE(pszFilterNameSize);
		JS_CLONE(pszFilterDisplayName);
		JS_CLONE(pszFilterDisplayNameSize);		
	}

	JS_FREE_FUNCTION(tagARCHIVE_FILTER_FILEFOLDER)
	{
		JS_FREE(nInclExcl);
		JS_FREE(pszFilterNameSize);
		JS_FREE(pszFilterDisplayName);
		JS_FREE(pszFilterDisplayNameSize);
	}

} ARCHIVE_FILTER_FILEFOLDER, *PARCHIVE_FILTER_FILEFOLDER;

typedef struct tagARCHIVE_ASFILTER_SIZE
{
	USHORT nInclExcl;                     // See Include/Exclude
	USHORT nCompareType;                     // See Size Types
	LONGLONG FileSize1; // size of the file
	LONGLONG FileSize2; // second param for size of the file if used between .

	//////////////////////////////////////////////////////////////////////////JS_CLONE_FUNCTION(xxx)
	JS_CLONE_FUNCTION(tagARCHIVE_ASFILTER_SIZE)
	{
		JS_CLONE(nInclExcl);
		JS_CLONE(nCompareType);
		JS_CLONE(FileSize1);
		JS_CLONE(FileSize2);
	}

	JS_FREE_FUNCTION(tagARCHIVE_ASFILTER_SIZE)
	{
		JS_FREE(nInclExcl);
		JS_FREE(nCompareType);
		JS_FREE(FileSize1);
		JS_FREE(FileSize2);
	}

} ARCHIVE_ASFILTER_SIZE, *PARCHIVE_ASFILTER_SIZE;

typedef struct tagARCHIVE_ASFILTER_TIME
{
	USHORT    nInclExcl;                  // See Include/Exclude
	USHORT	  nDateType;                  // See Date Types (modifation time,creation time, access time)
	USHORT	  nCompareType;               // See Time Range Types
	USHORT    nTimeUnit;                  // reserved : Time unit is in Days, months or year ???, for time being only pass
										  // days to backend.
	LONGLONG  ulStartTime;                // reserved : Time range start time reserved for time beging
	LONGLONG  ulEndTime;                  // reserved : Time range end time reserved for time beging
	LONGLONG  llTimeElapsed;              //  100 days or 265 days etc.... 
	
	//////////////////////////////////////////////////////////////////////////JS_CLONE_FUNCTION(xxx)
	JS_CLONE_FUNCTION(tagARCHIVE_ASFILTER_TIME)
	{
		JS_CLONE(nInclExcl);
		JS_CLONE(nTimeUnit);
		JS_CLONE(ulStartTime);
		JS_CLONE(ulEndTime);
		JS_CLONE(llTimeElapsed);
	}

	JS_FREE_FUNCTION(tagARCHIVE_ASFILTER_TIME)
	{
		JS_FREE(nInclExcl);
		JS_FREE(nTimeUnit);
		JS_FREE(ulStartTime);
		JS_FREE(ulEndTime);
		JS_FREE(llTimeElapsed);
	}

} ARCHIVE_ASFILTER_TIME, *PARCHIVE_ASFILTER_TIME;
typedef struct _AFARCHIVEPOLICY
{
	PWCHAR								szSourceFolder;
	PARCHIVE_FILTER_FILEFOLDER			pFileFilters;
	DWORD								nFileFilters;
	PARCHIVE_FILTER_FILEFOLDER			pFolderFilters;
	DWORD								nFolderFilters;
	PARCHIVE_ASFILTER_SIZE				pFileSizeFilters;
	DWORD								nFileSizeFilters;
	PARCHIVE_ASFILTER_TIME				pFileTimeFilters;
	DWORD								nFileTimeFilters;

	JS_CLONE_FUNCTION(_AFARCHIVEPOLICY)
	{
		JS_CLONE(szSourceFolder);
		JS_CLONE_LIST(ARCHIVE_FILTER_FILEFOLDER, nFileFilters, pFileFilters);
		JS_CLONE(nFileFilters);
		JS_CLONE_LIST(ARCHIVE_FILTER_FILEFOLDER, nFolderFilters, pFolderFilters);
		JS_CLONE(nFolderFilters);
		JS_CLONE_LIST(ARCHIVE_ASFILTER_SIZE, nFileSizeFilters, pFileSizeFilters);
		JS_CLONE(nFileSizeFilters);
		JS_CLONE_LIST(ARCHIVE_ASFILTER_TIME, nFileTimeFilters, pFileTimeFilters);
		JS_CLONE(nFileTimeFilters);
	}
	JS_FREE_FUNCTION(_AFARCHIVEPOLICY)
	{
		JS_FREE(szSourceFolder);
		JS_FREE_LIST(ARCHIVE_FILTER_FILEFOLDER, nFileFilters, pFileFilters);
		JS_FREE(nFileFilters);
		JS_FREE_LIST(ARCHIVE_FILTER_FILEFOLDER, nFolderFilters, pFolderFilters);
		JS_FREE(nFolderFilters);
		JS_FREE_LIST(ARCHIVE_ASFILTER_SIZE, nFileSizeFilters, pFileSizeFilters);
		JS_FREE(nFileSizeFilters);
		JS_FREE_LIST(ARCHIVE_ASFILTER_TIME, nFileTimeFilters, pFileTimeFilters);
		JS_FREE(nFileTimeFilters);
	}

}AFARCHIVEPOLICY, *PAFARCHIVEPOLICY;

typedef struct _AFFILECOPYPOLICY
{
	PWCHAR								szSourceFolder;
	PARCHIVE_FILTER_FILEFOLDER			pFileFilters;
	DWORD								nFileFilters;
	PARCHIVE_FILTER_FILEFOLDER			pFolderFilters;
	DWORD								nFolderFilters;
	PARCHIVE_ASFILTER_SIZE				pFileSizeFilters;
	DWORD								nFileSizeFilters;

	JS_CLONE_FUNCTION(_AFFILECOPYPOLICY)
	{
		JS_CLONE(szSourceFolder);
		JS_CLONE_LIST(ARCHIVE_FILTER_FILEFOLDER, nFileFilters, pFileFilters);
		JS_CLONE(nFileFilters);
		JS_CLONE_LIST(ARCHIVE_FILTER_FILEFOLDER, nFolderFilters, pFolderFilters);
		JS_CLONE(nFolderFilters);
		JS_CLONE_LIST(ARCHIVE_ASFILTER_SIZE, nFileSizeFilters, pFileSizeFilters);
		JS_CLONE(nFileSizeFilters);
	}
	JS_FREE_FUNCTION(_AFFILECOPYPOLICY)
	{
		JS_FREE(szSourceFolder);
		JS_FREE_LIST(ARCHIVE_FILTER_FILEFOLDER, nFileFilters, pFileFilters);
		JS_FREE(nFileFilters);
		JS_FREE_LIST(ARCHIVE_FILTER_FILEFOLDER, nFolderFilters, pFolderFilters);
		JS_FREE(nFolderFilters);
		JS_FREE_LIST(ARCHIVE_ASFILTER_SIZE, nFileSizeFilters, pFileSizeFilters);
		JS_FREE(nFileSizeFilters);
	}
}AFFILECOPYPOLICY, *PAFFILECOPYPOLICY;


typedef struct _AFARCHIVEJOBSCRIPT
{
	DWORD					ulVersion;					// xml version
	DWORD					ulShrMemID;					// Unique ID -- used to compose unique sharememory filename	
	DWORD					usJobType;					// bacup and restore. Resync is a kind of job method				
	DWORD					nNodeItems;					// # of node
	PWCHAR				pwszBackupSessionPath;		// Backup  Session path which is source data for Archive 
	PAFARCHIVENODE 		pAFNodeList;				// node list, it holds backup/restore source info   
	PWCHAR				pwszBackupDestUserName;		//user name for accessing backup destination;restore and copy source //<sonmi01>2009-8-25 ###???
	PWCHAR				pwszBackupDestPassword;		//password for accessing backup destination;restore and copy source
	PARCHIVEDISKDESTINFO  pDiskDest;
	PARCHIVECLOUDDESTINFO pCloudDest;
	DWORD					dwDestType;					// Refer AF Cloud Interface Export Header File
	// AMAZON_S3 = 0,
	// WINDOWS_AZURE_BLOB = 1,
	// IRON_MOUNTAIN = 2,
	// I365 = 3,
	// FILE_SYSTEM = 4,

	PWCHAR				pwszComments;				// Comments or description
	PWCHAR				pwszBeforeJob;				// Process string before job starts
	PWCHAR				pwszAfterJob;				// Process string after job ends
	PWCHAR				pwszPrePostUser;			// pre-post username																//new adding
	PWCHAR				pwszPrePostPassword;		// pre-post password																//new adding
	PWCHAR				pwszEncrptionPassword;
	DWORD					usPreExitCode;				// Exit code for Application wich run before
	DWORD					usJobMethod;				// Job Methods -- Incremental/Full/Resync
	DWORD					usRestPoint;				// reserved ???
	DWORD					fOptions;					// flags
	DWORD					dwCompressionLevel;			// compression level
	DWORD					dwJobHistoryDays;			// days to reserve job history<zouyu01>2009-10-14, default is 10 days.
	DWORD					dwArchiveLogDays;           // Days limitation to purge Archive log files. 0 means doesn't purge.  
	LONGLONG				ftPurgeFileBeforeThisDate;  // As of now this is reserved we may use this for purge job.
	DWORD					dwEncryptionEnabled;

	DWORD					dwproductType;
	PWCHAR	            pwszvmInstanceUUID;

	LONGLONG				ftFileMergeDate;
	PWCHAR                pwszCatalogDirPath;
	PAFARCHIVEPOLICY      pArchivePolicy;
	DWORD					nArchivePolicyCount;
	PAFFILECOPYPOLICY     pFileCopyPolicy;
	DWORD					nFileCopyPolicyCount;

	DWORD					dwDestinationAccountId;
	PWCHAR					pwszBackupEncrptionPassword;	
	DWORD					dwBackupEncryptionMethod;
	PWCHAR				  pwszCatalogDirUserName;
	PWCHAR				  pwszCatalogDirPassword;
	PWCHAR                pwszPlanName;
	PWCHAR                pwszArchiveStubFileExtension;
	PWCHAR                pwszArchiveStubFileComments;
	DWORD                 dwDatastoreType;
	PWCHAR				  rpsServerName;
	PWCHAR				  rpsDatastoreName;

	//////////////////////////////////////////////////////////////////////////
	JS_CLONE_FUNCTION(_AFARCHIVEJOBSCRIPT)
	{
		JS_CLONE(ulVersion);					// xml versi
		JS_CLONE(ulShrMemID);					// Unique ID
		JS_CLONE(usJobType);					// bacup and
		//JS_CLONE(DWORD					nNodeItems);					// # of node
		JS_CLONE(pwszBackupSessionPath);		// Backup  S
		JS_CLONE_LIST(AFARCHIVENODE, nNodeItems, pAFNodeList);				// node list
		JS_CLONE(pwszBackupDestUserName);		//user name 
		JS_CLONE(pwszBackupDestPassword);		//password f
		JS_CLONE_POINTER(ARCHIVEDISKDESTINFO, pDiskDest);
		JS_CLONE_POINTER(ARCHIVECLOUDDESTINFO, pCloudDest);
		JS_CLONE(dwDestType);					// Refer AF 
		JS_CLONE(pwszComments);				// Comments 
		JS_CLONE(pwszBeforeJob);				// Process s
		JS_CLONE(pwszAfterJob);				// Process s
		JS_CLONE(pwszPrePostUser);			// pre-post 
		JS_CLONE(pwszPrePostPassword);		// pre-post 
		JS_CLONE(pwszEncrptionPassword);
		JS_CLONE(usPreExitCode);				// Exit code
		JS_CLONE(usJobMethod);				// Job Metho
		JS_CLONE(usRestPoint);				// reserved 
		JS_CLONE(fOptions);					// flags
		JS_CLONE(dwCompressionLevel);			// compressi
		JS_CLONE(dwJobHistoryDays);			// days to r
		JS_CLONE(dwArchiveLogDays);           // Days limi
		JS_CLONE(ftPurgeFileBeforeThisDate);  // As of now
		JS_CLONE(dwEncryptionEnabled);
		JS_CLONE(dwproductType);
		JS_CLONE(pwszvmInstanceUUID);
		JS_CLONE(ftFileMergeDate);
		JS_CLONE(pwszCatalogDirPath);
		JS_CLONE_LIST(AFARCHIVEPOLICY, nArchivePolicyCount, pArchivePolicy);
		JS_CLONE(nArchivePolicyCount);
		JS_CLONE_LIST(AFFILECOPYPOLICY, nFileCopyPolicyCount, pFileCopyPolicy);
		JS_CLONE(nFileCopyPolicyCount);
		JS_CLONE(dwDestinationAccountId);
		JS_CLONE(pwszBackupEncrptionPassword);
		JS_CLONE(dwBackupEncryptionMethod);
		JS_CLONE(pwszCatalogDirUserName);
		JS_CLONE(pwszCatalogDirPassword);
		JS_CLONE(pwszPlanName);
		JS_CLONE(pwszArchiveStubFileExtension);
		JS_CLONE(pwszArchiveStubFileComments);
		JS_CLONE(dwDatastoreType);
		JS_CLONE(rpsServerName);
		JS_CLONE(rpsDatastoreName);
	}

	JS_FREE_FUNCTION(_AFARCHIVEJOBSCRIPT)
	{
		JS_FREE(ulVersion);					// xml versi
		JS_FREE(ulShrMemID);					// Unique ID
		JS_FREE(usJobType);					// bacup and
		//JS_FREE(DWORD					nNodeItems);					// # of node
		JS_FREE(pwszBackupSessionPath);		// Backup  S
		JS_FREE_LIST(AFARCHIVENODE, nNodeItems, pAFNodeList);				// node list
		JS_FREE(pwszBackupDestUserName);		//user name 
		JS_FREE(pwszBackupDestPassword);		//password f
		JS_FREE_POINTER(ARCHIVEDISKDESTINFO, pDiskDest);
		JS_FREE_POINTER(ARCHIVECLOUDDESTINFO, pCloudDest);
		JS_FREE(dwDestType);					// Refer AF 
		JS_FREE(pwszComments);				// Comments 
		JS_FREE(pwszBeforeJob);				// Process s
		JS_FREE(pwszAfterJob);				// Process s
		JS_FREE(pwszPrePostUser);			// pre-post 
		JS_FREE(pwszPrePostPassword);		// pre-post 
		JS_FREE(pwszEncrptionPassword);
		JS_FREE(usPreExitCode);				// Exit code
		JS_FREE(usJobMethod);				// Job Metho
		JS_FREE(usRestPoint);				// reserved 
		JS_FREE(fOptions);					// flags
		JS_FREE(dwCompressionLevel);			// compressi
		JS_FREE(dwJobHistoryDays);			// days to r
		JS_FREE(dwArchiveLogDays);           // Days limi
		JS_FREE(ftPurgeFileBeforeThisDate);  // As of now
		JS_FREE(dwEncryptionEnabled);
		JS_FREE(dwproductType);
		JS_FREE(pwszvmInstanceUUID);
		JS_FREE(ftFileMergeDate);
		JS_FREE(pwszCatalogDirPath);
		JS_FREE_LIST(AFARCHIVEPOLICY, nArchivePolicyCount, pArchivePolicy);
		JS_FREE(nArchivePolicyCount);
		JS_FREE_LIST(AFFILECOPYPOLICY, nFileCopyPolicyCount, pFileCopyPolicy);
		JS_FREE(nFileCopyPolicyCount);
		JS_FREE(dwDestinationAccountId);
		JS_FREE(pwszBackupEncrptionPassword);
		JS_FREE(dwBackupEncryptionMethod);
		JS_FREE(pwszCatalogDirUserName);
		JS_FREE(pwszCatalogDirPassword);
		JS_FREE(pwszPlanName);
		JS_FREE(pwszArchiveStubFileExtension);
		JS_FREE(pwszArchiveStubFileComments);
		JS_FREE(dwDatastoreType);
		JS_FREE(rpsServerName);
		JS_FREE(rpsDatastoreName);
	}

} AFARCHIVEJOBSCRIPT, *PAFARCHIVEJOBSCRIPT;
//node level option value:

//volume level option value
//backup option:

//restore option:
#define AFVO_R_DISMOUNTDB      0x00000001	//request backend dismount/mount db during restore

#endif  //_AF_JOB_H
