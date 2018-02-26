#pragma once

#define LCK_MSG_LEN 2048
#define DOMAIN_NAME_LEN 64
#define COMPUTER_NAME_LEN 64
#define USER_NAME_LEN 256

#define AFCOMMFUNC_MODULE L"AFCommFunc"

#ifdef __cplusplus
extern "C"{
#endif

typedef enum AF_LCK_MODE
{
    LCK_READ = 0,
    LCK_WRITE,
    LCK_JUST_CREATE, //[Zou, Yu] 2010-11-19(archive), just create that lock file for later use.
    LCK_ARCHIVE,
    LCK_DELETE    //ZZ: [2014/07/21 17:09] Add remove lock to delete node. 
};

typedef struct _AF_LCK_ITEM
{
	DWORD dwErrorCode;   //ZZ: Detail error code, such as D2DCOMM_E_DEST_OR_SESS_UNDER_REMOVING which indicate destiantion is under removing.
    DWORD dwOp; // operation type, like backup, restore & catalog.
    AF_LCK_MODE mode;
    DWORD dwProcessId;
    DWORD dwReserveTime; //[Zou, Yu] 2010-11-19(archive), Valid period for this lock file.
    wchar_t szDomain[DOMAIN_NAME_LEN];
    wchar_t szComputer[COMPUTER_NAME_LEN];
    wchar_t szUser[USER_NAME_LEN];
    wchar_t szDesc[LCK_MSG_LEN];
}AF_LCK_ITEM, *PAF_LCK_ITEM;

typedef enum FILE_OP
{
    FILE_LOCKED = 0,                               //the file should be locked.
    FILE_EXCLUDE,                                  //the file is excluded from lock.
	FILE_EXCLUDE_BY_PATTERN                        //the file is excluded from 
};

#ifdef __cplusplus
}
#endif

typedef struct _LCK_PARAM
{
    DWORD dwOp; // operaton type, like backup, restore & catalog.
    DWORD dwWaitTime; // wait time(ms).
    DWORD dwReserveTime; //the invalid period for this lock. [Zou, Yu] 2010-11-19 for archive.
    AF_LCK_MODE mode;
    wstring strDest; //destination which will be locked.
    wstring strDesc; //lock description.
    vector<wstring> vFile; // files which will be locked, these files are under strDest.
}LCK_PARAM, *PLCK_PARAM;

typedef struct _LCK_FILE_ITEM
{
    DWORD dwAttr;
    DWORD dwFileOp;
    wstring strFile;

}LCK_FILE_ITEM, *PLCK_FILE_ITEM;

typedef struct _LCK_ERR_INFO
{
    AF_LCK_ITEM lckItem;
    wstring strLckFile; //lck file under the backup destination. for archive.
    vector<LCK_FILE_ITEM> vLckFile;
}LCK_ERR_INFO, *PLCK_ERR_INFO;

class IAFLock
{
public:

    virtual void Release() = 0;

    virtual DWORD Lock(const LCK_PARAM &param, std::vector<LCK_ERR_INFO> &vErrInfo) = 0;

    virtual DWORD HoldLock(const wstring &strFile, AF_LCK_MODE mode) = 0;

    virtual void Unlock() = 0;

    virtual DWORD AddLockFile(const LCK_FILE_ITEM &lckFile) = 0;

    virtual BOOL IsThisLock(const wstring &strDest) = 0;

    virtual DWORD GetMutexLock(const wstring &strDest) = 0;

    virtual void ReleaseMutexLock() = 0;
};

DWORD WINAPI CreateIAFLock(IAFLock **ppIAFLock);

DWORD WINAPI GetAFLockListByMode(vector<LCK_ERR_INFO>& vLockLst, const WCHAR* pwzBKDest, AF_LCK_MODE mode);
typedef DWORD (WINAPI *PFNGetAFLockListByMode)(vector<LCK_ERR_INFO>&, const WCHAR*, AF_LCK_MODE);

DWORD WINAPI GetAFLockListByOpType(vector<LCK_ERR_INFO>& vLockLst, const WCHAR* pwzBKDest, DWORD opType);
typedef DWORD (WINAPI *PFNGetAFLockListByOpType)(vector<LCK_ERR_INFO>&, const WCHAR*, DWORD );
