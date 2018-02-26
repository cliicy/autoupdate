///////////////////////////////////////////////////////////////////////////////////////////
// SETCNVRT.H: header file for setcnvrt.dll

#ifndef _SETCNVRT_H_
#define _SETCNVRT_H_

#ifdef __cplusplus
extern "C" {
#endif  /* __cplusplus */

#define  SETCNVRT_SUCCESS   0
#define  SETCNVRT_FAILED    1

///////////////////////////////////////////////////////////////////////////////////////////
// total size 144 bytes (THIS IS THE OLD BUFFER SIZE & STRUCTURE used in AS661)
// 
typedef struct tagUSERDBDATA_OLD
{
	DWORD  dwObjType;					// Object type
	DWORD  dwObjSubType;				// Object subtype
	char   szObject[64];             	// Object name
	//-- increase buffer size from 24 (in ASNT.61 - ARCServe 6.6, build.795) 
	// to 40 (in ASNT.67 - ARCServe 6.6, build.8xx)
	char   szUserName[40];            	// user name (NT user name 20 chars)
	char   szDomain[16];             	// domain name (NT domain name 15 chars)
	char   szPassword[16];             	// Password (NT password 14 chars)

} USERDBDATA_OLD;

typedef USERDBDATA_OLD *PUSERDBDATA_OLD;

///////////////////////////////////////////////////////////////////////////////////////////
// User Profile

typedef struct _ASUserProfile
{
	TCHAR			szPath[MAX_PATH];
	TCHAR			szSID[MAX_PATH];
	_ASUserProfile* lpNext;
}
ASUSERPROFILE, *LPASUSERPROFILE;

///////////////////////////////////////////////////////////////////////////////////////////
// SETCNVRT.DLL APIs

ULONG __cdecl SetcnvrtCopyRegSubTree(HKEY hRoot,  LPCTSTR sSourcePath, LPCTSTR sDestinationPath, BOOL bDeleteSource);
ULONG __cdecl CnvrtUserDBKey(); // should not be used by setup/upgrade; 
								// replaced by CnvrtUserDBKey2 method

#define USERDB_CNVRT_AS661		1
#define USERDB_CNVRT_ASNW70		2

ULONG __cdecl CnvrtUserDBKey2(int nVersion);

BOOL __cdecl  GetUserProfileFolder(LPASUSERPROFILE* lpProfilePathList);
void  __cdecl DeleteUserProfileList(LPASUSERPROFILE lpProfilePathList);

typedef ULONG (*SETCNVRTCOPYREGSUBTREE)(HKEY, LPCTSTR, LPCTSTR, BOOL);
typedef ULONG (*CNVRTUSERDBKEY)();
typedef ULONG (*CNVRTUSERDBKEY2)(int);
typedef BOOL  (*GETUSERPROFILEFOLDER)(LPASUSERPROFILE*);
typedef void  (*DELETEUSERPROFILELIST)(LPASUSERPROFILE);

#ifdef __cplusplus
}
#endif

#endif //_SETCNVRT_H_