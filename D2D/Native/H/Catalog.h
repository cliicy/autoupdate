#ifndef _CATBROWSE_H
#define _CATBROWSE_H

#pragma warning(disable: 4819)

#include "windows.h"
#include "asdefs.h"
#include <string>
using namespace std;

#define VOLGUID_LENGTH 48	
#define MAX_PATH_WITH_VOLGUID MAX_NT_PATH+VOLGUID_LENGTH

typedef struct tyDetailW 
{
	unsigned long  LongNameID;
	unsigned long  PathID;
	unsigned short SessType;
	unsigned short DataType;
	unsigned long  FileDate;
	unsigned long  FileSizeHigh;
	unsigned long  FileSize;
	unsigned long  SessionNumber;
	unsigned long  SubSessionNumber;
    unsigned long  ulFullSessNum;       //ZZ: Full session number, if current session is full, it equals to SessionNumber.
    unsigned long  ulEncryptInfo;       //ZZ: Encryption information. Currently non-zero mean encrypted session.
    unsigned long  ulBKTime;            //ZZ: Current recovery point time.
    wchar_t        wzBKDest[MAX_PATH];  //ZZ: Current backup destination for current session.
    wchar_t        wzJobName[MAX_PATH]; //ZZ: Current backup job number.
    wchar_t        wzPWDHash[MAX_PATH]; //ZZ: Hash value string for current session password
//	unsigned long  QFAChunkNum;
//	unsigned long  QFAChunkOffset;
	wchar_t        DisplayName[MAX_PATH];
	wchar_t        LongName[MAX_PATH_WITH_VOLGUID];//bccma01 long path - to support long path for session not having catalog
    wchar_t        wzSessGUID[MAX_PATH];
    wchar_t        wzFullSessGUID[MAX_PATH];
	DWORD	dwFlags;//					// 0x01 ,has driver letter
	//wchar_t Path[512];
}DetailW, *PDetailW;

//dwFlags of struct tyDetailW 
#define	DT_FLAG_HAVE_DRIVER_LETTER			0x01

//#define MAX_EM_GETDETAILLIST              40	// 100-->40  (4/5/2007) : Fix (15808709)

// [3/24/2014 zhahu03]
// change return value type from BOOL to DWORD(with error code).
DWORD GenerateIndexFiles(wchar_t *Catalogfile);

HANDLE OpenCatalogFile(wchar_t *Catalogname);
UINT GetChildrenCount(HANDLE handle, unsigned long LongNameID);
PDetailW GetChildren(HANDLE handle, unsigned long LongNameID, UINT *Cnt);
PDetailW GetChildrenEx(HANDLE handle, unsigned long LongNameID, UINT nStart, UINT nRequest, UINT *Cnt);
wchar_t *GetFullPath(HANDLE handle, unsigned long PathID, wchar_t *lpString, UINT sBufSize);
void CloseCatalogFile(HANDLE handle);

//HANDLE SearchCatalogFile(wchar_t *sDestination, SYSTEMTIME begintime, SYSTEMTIME endtime, wchar_t *sDir, BOOL bCaseSensitive, BOOL bIncludeSubDir, wchar_t *pattern, UINT *TotalCnt);
HANDLE SearchCatalogFile(wchar_t *sDestination, unsigned long begin_sesstime, unsigned long end_sesstime, wchar_t *sDir, BOOL bCaseSensitive, BOOL bIncludeSubDir, wchar_t *pattern, UINT *TotalCnt);
UINT FindNextCatalogFile(HANDLE handle, UINT nRequest, PDetailW *pDetail, UINT *nFound);
wchar_t *GetFindFullPath(HANDLE handle, unsigned long PathID, wchar_t *lpString, UINT sBufSize);
void FindCloseCatalogFile(HANDLE handle);

//pidma02:
//Below api's are for searching in a single session
HANDLE SearchCatalogFileEx(wchar_t *sCatalogSession, unsigned long begin_sesstime, unsigned long end_sesstime, wchar_t *sDir, BOOL bCaseSensitive, BOOL bIncludeSubDir, wchar_t *pattern, UINT *TotalCnt);
UINT FindNextCatalogFileEx(HANDLE handle, UINT nRequest, PDetailW *pDetail, UINT *nFound);

//The below are added by Luo Cao
//The struct to store Msg object data

typedef struct _tag_MsgRecW
{
	/*long sessid;
	long qfachunknum;
	long qfachunkoffset;*/
	long objtype;
	long objdate;
	long objflags;
	long lobjsize; //the low 4 bytes of size
	long hobjsize; //the high 4 bytes
	wchar_t objname[256];
	wchar_t objinfo[64];
	long lobjselfid; //the low 4 bytes of self id
	long hobjselfid; //the high 4 bytes of self id
	long lobjparentid;
	long hobjparentid;
	long lobjbody;
	long hobjbody;
	//long objaux;
	//long reserved;
	unsigned int cp_flag;

	//new fields
	wchar_t* sender;
    wchar_t* receiver;  // receiver may be very long?
    DWORD64 senttime;
    DWORD64 receivedtime;
    unsigned long flag;  //store attchement and importance
	unsigned long itemSize;

}MsgRecW, *PMsgRecW;

typedef struct _tag_MsgSearchRecW
{
	_tag_MsgRecW msgRec;
	unsigned long  SessionNumber;
    unsigned long  SubSessionNumber;
    wchar_t* mailboxOrSameLevelName;
    wchar_t* edbLogicalPath;//edb real restore path
    //long edbType;// // Root Public folder (254)or edb(255)
    //wchar_t* edbDisplayName;// edb display full path name
	wchar_t* mailFullDisplayPath;// the full display path for the mail like ech writer\Server\EDB\Inbox etc.

	unsigned long  ulFullSessNum;       //ZZ: Full session number, if current session is full, it equals to SessionNumber.
    unsigned long  ulEncryptInfo;       //ZZ: Encryption information. Currently non-zero mean encrypted session.
    unsigned long  ulBKTime;            //ZZ: Current recovery point time.
    wchar_t        wzBKDest[MAX_PATH];  //ZZ: Current backup destination for current session.
    wchar_t        wzJobName[MAX_PATH]; //ZZ: Current backup job number.
    wchar_t        wzPWDHash[MAX_PATH]; //ZZ: Hash value string for current session password
    wchar_t        wzSessGUID[MAX_PATH];
    wchar_t        wzFullSessGUID[MAX_PATH];
}MsgSearchRecW, *PMsgSearchRecW;

//ZZ: Everyone should only call this API in web service side because there is impersonation in this API. 
//    If this API is called in back end, it will cause a new logon session created. Sth like connection to remote 
//    folder will not be available any more after this calling. There is API AFGetDatabaseCatalogInformation 
//    in AFCorefuntion to achieve the same target.

//return 0 mean success, the first parameter is out parameter, others are in parameter.
long GetMsgCatalogPath(wstring& path, const WCHAR* pwzDBIdentity, const WCHAR* pwzBackupDest, DWORD dwSessNum, DWORD dwSubSessNum, bool bDoImpersonate = true);

//return 0 mean success, the first parameter is out parameter, others are in parameter.
long GetMsgDBIdentity(wstring& dbIdentity,HANDLE handle, unsigned long LongNameID);

BOOL MsgGenerateIndexFiles(wchar_t *Catalogfile);

HANDLE MsgOpenCatalogFile(wchar_t *Catalogname);

/*
lselfid - the low 4 bytes of self id
heslfid - the high 4 bytes of self id
when lselfid==hselfid == 0, it means the root.
The first 3 param are in paramerters, the last one is out parameter, it return the count of PMsgRecW
*/
PMsgRecW MsgGetChildren(HANDLE handle, long lselfid, long hselfid, UINT *nCnt);

PMsgRecW MsgGetChildrenEx(HANDLE handle, long lselfid, long hselfid, UINT nStart, UINT nRequest, UINT *nCnt);

/*The three APIs below are for get children with filters, the fillter should be in querystring;
as the format of query string, please check the end of the file
Parameters: same as the old API;
nRequest: if it is 0, will return all records
resultIndex: the index of the result, should >0 and <8, the index of results, for multiple result sets
*/
PMsgRecW MsgGetChildrenByFilterFirst(HANDLE handle, long lselfid, long hselfid, wchar_t* queryString,  
									 UINT nRequest, UINT *nCnt, int resultIndex, UINT *totalCount);
PMsgRecW MsgGetChildrenByFilterNext(HANDLE handle,
								UINT nStart, UINT nRequest, UINT *nCnt, int resultIndex);
int MsgSortChildren(HANDLE handle, wchar_t* queryString, int resultIndex);
/*end*/

// to realse the msg rec allocated by the functions above
void ReleaseMsgRecW(PMsgRecW pDetail, UINT count);

//if failed, return -1, otherwise return the count
long MsgGetChildrenCount(HANDLE handle, long lselfid, long hselfid);
//functions to search the catalog

//search API for MSG Catalog
UINT FindNextMsgCatalogFile(HANDLE handle, UINT nRequest, PMsgSearchRecW *pDetail, UINT *nFound);
void ReleaseMsgSearchRecW(MsgSearchRecW *pDetail, UINT count);

//new search APIs for exchange
HANDLE Exch_BeginSearch(wchar_t *sDestination, wchar_t * queryString, UINT *TotalCnt);
int Exch_GetSearchResults(HANDLE handle, UINT nRequest, PMsgSearchRecW *pDetail, UINT *nFound);
void Exch_EndSearch(HANDLE handle);

//key word for query string
/*int value */
#define BEGIN_TIME L"begintime"
#define END_TIME L"endtime"
//string values
#define PATTERN L"pattern"
//bool values bool vaule: 0 or 1
#define IGNORE_CASE L"ignorecase"
#define INCLUDE_SUBDIR L"includesubdir"

//string value
#define SORT_FIELD L"sortfield"
//bool vaule: 0 or 1
#define DESCENTANT L"descentant"
//enum values
#define CONTENT L"content"
//end of the key word

//if the search string already contains "#", please use "##" to indicate one "#"
#define SEPARATOR L'#'

//the vaule of the sort field
#define SORT_SUBJECT L"subject"
#define SORT_SENDER L"sender"
#define SORT_RECEVER L"receiver"
#define SORT_SENTTIME L"senttime"
#define SORT_RECEIVEDTIME L"receivedtime"
#define SORT_IMPORTANCE L"importance"
#define SORT_SIZE L"size"
#define SORT_ATTACHMENT L"attachment"

//the values for the content
#define MAIL L"mail"
#define FOLDER L"folder"
#define CALENDAR L"calendar"
#define CONTACT	L"contact"
/* the query string will be like this:

#begintime#:#12222678# #endtime#:#123231# #includesubdir#:#0# #pattern#:#*dfdfdf# #ignorecase#:"0"

#sortfield#:#subject# #descentant#:#1# #content#:#mail#

*/

#endif