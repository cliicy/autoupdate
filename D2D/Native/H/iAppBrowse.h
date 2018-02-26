#ifndef _iAppBrowse_H
#define _iAppBrowse_H
#pragma once 

#include "GRTMntBrowser.h"
 
#if defined (__cplusplus)
extern "C"
{
#endif // __cplusplus


//File Name iAppBrowse.h
// used by Nativefacade.dll and GRTMntBrowser.dll to enumerate the children from App-Database
#define  APPAD_ROOT_ITEM  0xFFFFFFFF
//

// application type, including Active Directory, Share Point.
typedef enum _app_type
{
	APP_UNKNOWN_TYPE =0,
	APP_GRT_AD	= 0x01,		// AD  GRT
	APP_GRT_SPS	= 0x02		// SPS GRT

}APP_GRT_TYPE, *PAPP_GRT_TYPE;

// the data type 
typedef enum _app_data_type
{
	APP_DATA_TYPE_UNKNOWN	= 0x00,
	APP_DATA_AD_BASE		= 0x01,							// AD data type base
	APP_DATA_AD_PARTTION	= APP_DATA_AD_BASE + 0x00,		// the child items of AD
	APP_DATA_AD_CHILD		= APP_DATA_AD_BASE + 0x01,		// the child items of AD
	APP_DATA_AD_ATTr		= APP_DATA_AD_BASE + 0x02,		// the attributes of current node

	APP_DATA_SPS_BASE		= 0x200,							// SPS data type base

}APP_DATA_TYPE, *PAPP_DATA_TYPE;

typedef enum _app_ad_date_type
{
	AD_DATA_UNKNOWN		= 0x00,
	AD_DATA_PARTITION	= 0x01,
	AD_DATA_CHILD		= 0x02,
	AD_DATA_ATTrs		= 0x03,
}APP_AD_DATA_TYPE, *PAPP_AD_DATA_TYPE;


#define  AD_NODE_TYPE_BASE  0
#define  AD_NODE_GENERAL	AD_NODE_TYPE_BASE +0
#define  AD_NODE_USERS		AD_NODE_TYPE_BASE +1
#define  AD_NODE_USER		AD_NODE_TYPE_BASE +2
#define  AD_NODE_COMPUTER	AD_NODE_TYPE_BASE +3
#define  AD_NODE_OU			AD_NODE_TYPE_BASE +4

#define  AD_NODE_LEAF		0x01	// the current is leaf node
// Active Directory item
typedef struct _appGrt_AD
{
	DWORD dwDnt;			// the id of node
	DWORD dwItemType;		// the type of Node
	DWORD dwFlags;			// more information
	DWORD dwNameLen;		// the length of name of node / attribute
	WCHAR *pszName;			// the name of node / attribute
	DWORD dwValueLen;		// the length of attribute identified by pszName
	WCHAR *pszValue;		// the value of attribute identified by pszName, used to display in the UI. only valid when get the attributes
}AGRTITEM_AD, *PAGRTITEM_AD;
 
// Share point item
typedef struct appGrt_SP
{ 
	DWORD64 id;				// the id of share point item.
}AGRTITEM_SP, *PAGRTITEM_SP;

// the Application items
typedef struct _appGrtItem
{
	int Size;					// sizeof(_appGrtItem)
	union
	{
		AGRTITEM_AD ADItem;		// AD -item
		AGRTITEM_SP SPItem;		// SPS-item
	}item;

	//_appGrtItem * pvNext;

}AGRT_ITEM, *PAGRT_ITEM;
 
//  Active Directory parent item
typedef struct appGrt_Parent_AD
{   
	DWORD dwDNT;				//	for root item, please set the value as APPAD_ROOT_ITEM
	//APP_AD_DATA_TYPE type;		//	the data type needed to be returned by child item
}APPITEM_PARENT_AD, *PAPPITEM_PARENT_AD;


//  Share Point parent item
typedef struct _appGrt_Parent_SPS
{   
	 DWORD64 id;				// the id of share point item.
}APPITEM_PARENT_SPS, *PAPPITEM_PARENT_SPS;

// Application Parent Item
typedef struct _appGrt_Parent
{
	DWORD size;				// sizeof(_appGrtParent)
	APP_GRT_TYPE AppType;	// AppType
	APP_DATA_TYPE DataType;
	SESSION_INFO session;	// Session information
	DWORD dwSubSession;		// Sub session where includes the information related with  application
	union
	{
		APPITEM_PARENT_AD AD_Parent;		//AD Parent
		APPITEM_PARENT_SPS SPS_Parent;		//SPS parent
	}parent;

}AGRT_PARENT, *PAGRT_PARENT;

// the Children Item
typedef struct _appGrtItems
{
	int size;					// sizeof(_appGrtItems)
	APP_DATA_TYPE DataType;		// data type
	int nItemNum;				// the number of pvItem
	PAGRT_ITEM pvItems;			// the list of child item
}AGRT_ITEMS, *PAGRT_ITEMS; 

GRT_MNT_API DWORD  AppGetItems(PAGRT_PARENT pvParent, PAGRT_ITEMS pvItem);
GRT_MNT_API DWORD  AppRleaseItems(PAGRT_ITEMS pvItems);

typedef struct _appGrt_AD_session
{
	DWORD size; // the size of current structure
	SESSION_INFO session;	// Session information
	DWORD dwSubSession;		// Sub session where includes the information related with  application
	DWORD dwJobID;
	DWORD dwRestoreOption;
}AGRT_AD_SESSION, *PAGRT_AD_SESSION;

/*
typedef struct _appGrt_Restore_AD_item
{
	DWORD dwDnt;		// the id of AD item
	BOOL  IsAllChild;	// whether restore all the child items
	BOOL  IsAllATTr;	// whether restore all the attributes
	DWORD dwATTrNum;	// the attribute number
	const WCHAR * pwszATTrNames; // the attributes needed to be restored, each ATTr is separated by ';'
}AGRT_RESTORE_ADITEM, *PAGRT_RESTORE_ADITEM;
*/

typedef struct _appGrt_restore_AD
{
	DWORD size;
	DWORD dwItemNum;
	PAGRT_RESTORE_ADITEM pvItems;
}AGRT_RESTORE_AD, *PAGRT_RESTORE_AD;

#define MSG_ADGRT_BASE								 0x00
#define MSG_ADGRT_JOB_START			MSG_ADGRT_BASE + 0x01
#define MSG_ADGRT_MNT_VOL			MSG_ADGRT_BASE + 0x02	// mount related volume
#define MSG_ADGRT_DISMNT_VOL		MSG_ADGRT_BASE + 0x03	// dismount related volume
#define MSG_ADGRT_OPEN_DB			MSG_ADGRT_BASE + 0x04	// open Active directory database 
#define MSG_ADGRT_CLOSE_DB			MSG_ADGRT_BASE + 0x05	// close Active directory database 
#define MSG_ADGRT_REPORT_PROGRESS	MSG_ADGRT_BASE + 0x06	// report restore progress
#define MSG_ADGRT_FINISHED			MSG_ADGRT_BASE + 0x07
#define MSG_ADGRT_JOB_END			MSG_ADGRT_BASE + 0x08
 /*
MSG_ADGRT_MNT_VOL
Parameter 1 : DWORD	
	0 start mount volume
	1 end volume
Parameter 2 : DWORD
	the return value for each step

MSG_ADGRT_OPEN_DB

Parameter 1: WCHAR *
	the database path in mounted volume
Parameter 2: DWORD 
	the return value 

*/
class IAppCallback
{
public:
	virtual DWORD __stdcall OnMessage(DWORD dwID, void * pParam =NULL, void * pParam2 = NULL ) =0;
};

GRT_MNT_API DWORD AppRestoreForAD(PAGRT_AD_SESSION pvSession, PAGRT_RESTORE_AD pRestore, IAppCallback * pvCallback);

#if defined (__cplusplus)
}
#endif // __cplusplus

#endif //_iAppBrowse_H