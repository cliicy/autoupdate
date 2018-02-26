
#ifndef UTILS_H
#define UTILS_H


#include <jni.h>
#include "FacadeVMWareMgr.h"
#include "AFCoreAPIInterface.h"
#include <dbghelp.h>

#define ERR_DLL_LACK 1
#define ERR_FUN_LACK 2
#define ERR_SEH 3

#define INVALID_DYNAMIC_DISK 5 

class CDllException
{
public:
	int m_type;
	wstring m_dllName;
	string m_funName;
	wstring m_message;

	//for the SEH
	EXCEPTION_POINTERS* m_pExp;
	unsigned int m_errCode;
public:
	CDllException();
	CDllException(int _errcode, const wstring &_dllName, const string &_funName);
	CDllException(int _errcode, const wstring &_dllName, const string &_funName, const wstring &_message);
};

wstring Utility_JStringToWCHAR( JNIEnv *env, jstring str);

jstring WCHARToJString( JNIEnv * env, const wstring& str);

void ThrowHyperVException(JNIEnv * env, jclass klass, jstring message, jint code);

void JStrListToCVector(JNIEnv *env,jclass klass, jobject jList,vector<wstring>& strVec);

void JIntListToCVector(JNIEnv *env,jclass klass,jobject jList,vector<unsigned short>& uShortVec);

typedef std::map<std::wstring, std::wstring> HyperVObjectMap;
jobject CMapToJMap(JNIEnv *env,jclass klass, HyperVObjectMap hyperVMap);

void HandleDllException(const CDllException &ex);

int CreateDumpFile( PEXCEPTION_POINTERS pInfo, wstring dicPath, wstring &savePath);

int CreatePathFold( wstring dicPath );

HANDLE DynGetProcAddress( const WCHAR* dllName, const char* procName );

void HandleDllMissed( const WCHAR* _dllName );

void HandleProcMissed( const WCHAR* _dllName, const char* _procName );

void HandleSEH( const WCHAR* _dllName, const char* _procName, const EXCEPTION_POINTERS* _pEx, unsigned int errCode);

void ThrowVMwareException(JNIEnv * env,jstring message, jint code);
void ThrowWSJNIException(JNIEnv * env,jclass klass,jstring message, jint code);

DWORD SetStringValue2Field(JNIEnv *env, jobject object, char* name, std::wstring targetValue);

DWORD SetBoolValue2Field(JNIEnv *env, jobject object, char* name, bool bValue);

DWORD SetLongValue2Field(JNIEnv *env, jobject object, char* name, __int64 targetValue);

DWORD NewJavaObject(JNIEnv *env, char* className, jobject &obj);

DWORD AddJobject2Vector(JNIEnv *env, jobject jvec, jobject obj);

DWORD ConstructGetSubRootParams(JNIEnv* env,jobject& javaParam,ST_HASRV_INFO* pstSrvInfo,
								wstring& wstrProductNode,wstring& pwszLastDesRoot,HA_SRC_ITEM* pwszSrcItem,wchar_t**& ppwszDesRootList,int &destCount);

void  GenDumpFile(WCHAR *szDumpFileName, const EXCEPTION_POINTERS *pExcept);

void setExceptionHandler();
void revertExceptionHandler();

NET_CONN_INFO getConnectionInfo(JNIEnv *env, jstring dest,jstring domain,jstring user,jstring pwd);

DWORD getAvailableMountDriveLetters(std::vector<wstring>& avaliableLetters);

DWORD getWindowsTempDir(wstring& strTempDir);

DWORD readRegDWORDValue(const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg, DWORD& dwValue);

DWORD writeRegDWORDValue(const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg, DWORD dwValue);

DWORD readRegMultiStringValue(const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg,vector<wstring>& vecValue);

DWORD writeRegMultiStringValue(const WCHAR* pwzRegKeyName, const WCHAR* pwzRegValName, const WCHAR* pwzD2DRootReg, vector<wstring> vecValue);

BOOL isAMD64Platform();


DWORD OpenRegistryKey(PTCHAR szKey, HKEY* phKey, BOOL bCreate = TRUE);


DWORD GetRegistryValue(PTCHAR szKey,  PTCHAR szValName, PDWORD  pdwValue,
					 BOOL bCreate, DWORD dwDefaultValue);

DWORD QueryRegistryValue( PTCHAR szKey,
						  PTCHAR szSubKey,
						  PTCHAR szValName,
						  PDWORD pdwValue,
						  DWORD	 dwDefaultValue);

DWORD QueryVDDKRegKey(PTCHAR szKeyName);




wstring getApacheParametersRegPath();

template <typename TLockee>
class TLocker
{
public:
	TLocker()
	{
		TLockee *pLockee = TLockee::Instance();
		pLockee->Lock();
	}
	~TLocker()
	{
		TLockee *pLockee = TLockee::Instance();
		pLockee->UnLock();
	}
};


class AutoCS
{
	friend class TLocker<AutoCS>;
private:
	AutoCS()
	{
		InitializeCriticalSection(&m_CS);
	}
	~AutoCS()
	{
		DeleteCriticalSection(&m_CS);
	}
	operator CRITICAL_SECTION* ()
	{
		return &m_CS;
	}
	void Lock()
	{
		::EnterCriticalSection(&m_CS);
	}
	void UnLock()
	{
		::LeaveCriticalSection(&m_CS);
	}

	static AutoCS * Instance()
	{
		static AutoCS cs;
		return &cs;
	}
private:
	CRITICAL_SECTION m_CS;
};

//<sonmi01>2012-11-29 #edit vm in a editable VM state
BOOL IsHypervVMEditable(LONG State, LONG HealthState, CONST vector<LONG>& vOperationalStatus);

//<huvfe01>2012-12-7 #VM operational status
BOOL IsOK_MSVM_OperationalStatus(CONST vector<LONG>& vOperationalStatus);

BOOL CheckHyperVMEditableWithRetry(VOID* pVirtualMachine, DWORD dwRetryInterval, DWORD dwLoopCount);


BOOL IsMsVmSnapshotApplicable(LONG state);
BOOL IsHyperVMSnapshotApplicable(LONG State, LONG HealthState, CONST vector<LONG>& vOperationalStatus);
BOOL CheckHyperVmSnapshotApplicableWithRetry(VOID* pVirtualMachine, DWORD dwRetryInterval, DWORD dwLoopCount);

BOOL DeleteDirectoryHelper(CString strPath);

BOOL isExchangeWriter(BACKUP_ITEM bakItem);
BOOL hasExchangeDB(BACKUP_ITEM bakItem);

NET_CONN_INFO CreateNetConnInfo(const WCHAR* pszSource, const WCHAR* pszUsrName, const WCHAR* pszUsrPwd);

#endif // UTILS_H

