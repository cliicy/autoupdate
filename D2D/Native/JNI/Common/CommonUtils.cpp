#include "stdafx.h"
#include <eh.h>
#include "CommonUtils.h"
#include "CommonJNIConv.h"
#include <dbglog.h>


#define STRING(x)		L##x 
#define WSTRING(x)		STRING(x)
#define __WFUNCTION__	WSTRING(__FUNCTION__)

TCHAR	szCAHome[] = _T("SOFTWARE\\Arcserve");

CDbgLog* pLogObj = NULL;
WCHAR* pModuleName = NULL;

void InitialCommonUtils(const WCHAR* moduleName)
{
	pModuleName = const_cast<WCHAR*>(moduleName);
	if(NULL == pLogObj)
		pLogObj = new CDbgLog(moduleName);
}

void UnInitialCommonUtils()
{
	if( pLogObj )
		pLogObj->LogW( LL_INF, 0, L"RPSNativeFacade.dll was detached" );

	delete pLogObj;
	pLogObj = NULL;
}

wstring Utility_JStringToWCHAR( JNIEnv *env, jstring str)
{
	if (str == NULL) return NULL;

	const jchar* jchs = env->GetStringChars(str, NULL);// UCS-2 to Unicode;

	if (jchs == NULL) return NULL;

	wstring wstr((wchar_t*)jchs);

	env->ReleaseStringChars(str, jchs);

	return wstr;

}

jstring WCHARToJString( JNIEnv * env, const wstring& str)
{
	jstring rtn = 0;

	if (!env) 
		return 0;

	if (str.empty()) {
		char sTemp[2] = {0};
		strcpy_s(sTemp, "");
		rtn = env->NewStringUTF(sTemp);
		return rtn;
	}

	rtn = env->NewString((jchar*)str.c_str(), str.length());

	return rtn;
}

void ThrowWSJNIException(JNIEnv * env,jclass klass,jstring message, jint code)
{
	jmethodID methodID=env->GetStaticMethodID(klass, "throwWSJNIException", "(Ljava/lang/String;I)V");
	env->CallStaticVoidMethod(klass, methodID, message, code);	

}
void ThrowWSJNIExceptionEx(JNIEnv * env,jclass klass,jstring message, jint code)
{
	jmethodID methodID=env->GetStaticMethodID(klass, "throwWSJNIExceptionEx", "(Ljava/lang/String;I)V");
	env->CallStaticVoidMethod(klass, methodID, message, code);	

}

//
// please use HandleSEH instead of below API
//
int CreateDumpFile( PEXCEPTION_POINTERS pInfo, wstring dicPath, wstring &savePath )
{
	time_t tm;
	HANDLE hFile;
	MINIDUMP_EXCEPTION_INFORMATION ExInfo;
	WCHAR path[MAX_PATH] = {0};
	int ret = 0;

	time(&tm);
	_snwprintf_s( path, MAX_PATH, L"%s\\%d.dmp", dicPath.c_str(), tm );
	hFile=CreateFile(path, GENERIC_WRITE, FILE_SHARE_READ, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
	if (hFile == INVALID_HANDLE_VALUE)
	{
		if(GetLastError() == ERROR_PATH_NOT_FOUND)
		{
			if(CreatePathFold(dicPath) < 0 )
				return -1;
			hFile=CreateFile(path, GENERIC_WRITE, FILE_SHARE_READ, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
			if (hFile == INVALID_HANDLE_VALUE)
				return -1;
		}
		else
			return -1;
	}

	ExInfo.ThreadId = GetCurrentThreadId();
	ExInfo.ExceptionPointers = pInfo;
	ExInfo.ClientPointers = FALSE;

	if (MiniDumpWriteDump( GetCurrentProcess(), GetCurrentProcessId(), hFile, MiniDumpNormal, &ExInfo, NULL, NULL ))
	{
		printf( "success to write dump file\n" );
		ret = -1;
	}
	else
		printf( "failed to write dump file\n" );
	CloseHandle(hFile);
	savePath = path;
	return ret;
}

void HandleSEH( const WCHAR* _dllName, const char* _procName, const EXCEPTION_POINTERS* _pEx, unsigned int errCode)
{
	// stack overflow exception can not be handled
	TLocker<AutoCS> csLocker;

	if (_pEx->ExceptionRecord->ExceptionCode == EXCEPTION_STACK_OVERFLOW)
	{
		pLogObj->LogW(LL_ERR, -1, __WFUNCTION__  L": stack overflow caught @ [%S] in DLL: [%s]", _procName, _dllName);
		return;
	}

	WCHAR szEptName[MAX_PATH * 2] = {0}, szDllName[MAX_PATH] = {0}, *backslash = NULL;

	pLogObj->LogW(LL_ERR, -1, __WFUNCTION__  L": exception caught @ [%S] in DLL: [%s]", _procName, _dllName);

	if(_dllName && wcslen(_dllName))
		wcscpy_s(szDllName, _dllName);
	else
		wcscpy_s(szDllName, pModuleName);
	DWORD cnt = _countof(szEptName);
	if(0 != CDbgLog::GetDefaultLogFolder(szEptName, &cnt))
	{
		GetModuleFileNameW(NULL, szEptName, _countof(szEptName));
		if (szEptName[0] == 0)
			return;

		backslash = wcsrchr(szEptName, L'\\');
		if (backslash == NULL)
			return;

		*backslash = L'\0';
	}

	wcscat_s(szEptName, L"\\");
	wcscat_s(szEptName, szDllName);

	WCHAR szTempPath[MAX_PATH * 2] = {0};

	int iIndex = 0;

	// name form: dllname0000x.mdp
	do
	{
		swprintf_s(szTempPath, L"%s_%05d.dmp", szEptName, iIndex);
		iIndex++;
	}
	while(PathFileExistsW(szTempPath));

	GenDumpFile(szTempPath, _pEx);
}

int CreatePathFold( wstring dicPath )
{
	wstring path;
	size_t idx = 0, c = 0;

	dicPath += L'\\';
	while( (idx = dicPath.find(L'\\', idx)) != wstring::npos )
	{
		path = dicPath.substr(0, idx);
		if( _waccess( path.c_str(), 0 ) != 0 )
		{
			if( 0 == CreateDirectoryW( path.c_str(), NULL ) )
				return -1;
		}
		idx++;
	}
	return 0;
}

void HandleDllMissed( const WCHAR* _dllName)
{
	DWORD dwErr = GetLastError(); // this is only valid when HandleDllMissed is called right after LoadLibrary
	pLogObj->LogW(LL_ERR, dwErr, __WFUNCTION__ L": fail to load DLL: [%s]", _dllName);
}

void HandleProcMissed( const WCHAR* _dllName, const char* _procName )
{
	DWORD dwErr = GetLastError(); // this is only valid when HandleProcMissed is called right after GetProcAddress
	pLogObj->LogW(LL_ERR, dwErr, __WFUNCTION__  L": fail to find proc [%S] in DLL: [%s]", _procName, _dllName);
}

HANDLE DynGetProcAddress( const WCHAR* dllName, const char* procName )
{
	HMODULE dll = GetModuleHandle( dllName );
	HANDLE proc = NULL;
	if( dll == NULL )
	{
		dll = LoadLibrary( dllName );
		if( dll == NULL )
		{
			HandleDllMissed( dllName );
			return NULL;
		}
	}
	proc = GetProcAddress( dll, procName);
	if( proc == NULL )
	{
		HandleProcMissed( dllName, procName );
		return NULL;
	}
	return proc;

}

CDllException::CDllException()
{

}

CDllException::CDllException(int _type, const wstring &_dllName, const string &_funName):m_type(_type), m_dllName(_dllName), m_funName(_funName)
{

}

CDllException::CDllException(int _type, const wstring &_dllName, const string &_funName, const wstring &_message):m_type(_type), m_dllName(_dllName), m_funName(_funName), m_message(_message)
{

}


DWORD SetStringValue2Field(JNIEnv *env, jobject object, char* name, std::wstring targetValue){

	jclass class_value= env->GetObjectClass(object);
	jfieldID field = env->GetFieldID(class_value, name, "Ljava/lang/String;");
	jstring value = WCHARToJString(env, (wchar_t*)targetValue.c_str());
	env->SetObjectField(object, field, value);

	if ( value!=NULL) env->DeleteLocalRef(value);
	if (class_value != NULL) env->DeleteLocalRef(class_value);
	return 0;
}


DWORD SetBoolValue2Field(JNIEnv *env, jobject object, char* name, bool bValue)
{
	jclass class_value= env->GetObjectClass(object);
	jmethodID id_setBoolValue = env->GetMethodID(class_value, name, "(Z)V");
	env->CallVoidMethod(object, id_setBoolValue, bValue);

	if (class_value != NULL) env->DeleteLocalRef(class_value);
	return 0;
}


DWORD SetLongValue2Field( JNIEnv *env, jobject object, char* name, __int64 targetValue )
{
	jclass class_value= env->GetObjectClass(object);
	jfieldID field = env->GetFieldID(class_value, name, "J");

	env->SetLongField(object, field, targetValue);

	if (class_value != NULL) env->DeleteLocalRef(class_value);
	return 0;
}

DWORD NewJavaObject( JNIEnv *env, char* className, jobject &obj )
{
	jclass jcls = NULL;
	jmethodID jmethod = NULL;
	obj = NULL;

	jcls = env->FindClass(className);
	if (NULL == jcls)
		return -1;

	jmethod = env->GetMethodID(jcls, "<init>", "()V");
	if (NULL == jmethod)
		return -1;

	obj = env->NewObject(jcls, jmethod);
	return 0;
}

DWORD AddJobject2Vector( JNIEnv *env, jobject jvec, jobject obj )
{
	jclass jcls = NULL;
	jmethodID jadd = NULL;

	jcls = env->FindClass("java/util/Vector");
	jadd = env->GetMethodID(jcls, "add", "(Ljava/lang/Object;)Z");
	if(JNI_FALSE == env->CallBooleanMethod(jvec, jadd, obj))
		return -1;
	else
		return 0;
}

typedef BOOL (WINAPI * PFN_MINIDUMPWRITEDUMP)( \
											  IN HANDLE hProcess, \
											  IN DWORD ProcessId, \
											  IN HANDLE hFile, \
											  IN MINIDUMP_TYPE DumpType, \
											  IN CONST PMINIDUMP_EXCEPTION_INFORMATION ExceptionParam, OPTIONAL \
											  IN CONST PMINIDUMP_USER_STREAM_INFORMATION UserStreamParam, OPTIONAL \
											  IN CONST PMINIDUMP_CALLBACK_INFORMATION CallbackParam OPTIONAL \
											  );

void  GenDumpFile(WCHAR *szDumpFileName, const EXCEPTION_POINTERS *pExcept)
{
	DWORD dwExceptCode = 0;
	DWORD dwProcId = GetCurrentProcessId();
	HANDLE hProc = INVALID_HANDLE_VALUE;
	HANDLE hFile = INVALID_HANDLE_VALUE;
	MINIDUMP_EXCEPTION_INFORMATION exceptInfo;

	HMODULE	hDbgHelp = NULL;
	PFN_MINIDUMPWRITEDUMP pfnMiniDumpWriteDump = NULL;

	hDbgHelp = LoadLibraryW(L"dbghelp.dll");
	if(NULL != hDbgHelp)
	{
		pfnMiniDumpWriteDump = (PFN_MINIDUMPWRITEDUMP)GetProcAddress(hDbgHelp, "MiniDumpWriteDump");
		if(NULL != pfnMiniDumpWriteDump)
		{
			hProc = ::OpenProcess(PROCESS_ALL_ACCESS,FALSE,GetCurrentProcessId());
			if(NULL == hProc)
				hProc = GetCurrentProcess(); // hanl got here is a pseudo handle to current process

			hFile = CreateFileW(szDumpFileName, GENERIC_WRITE, FILE_SHARE_READ, NULL,
				CREATE_ALWAYS, 0, NULL);
			if(INVALID_HANDLE_VALUE != hFile)

			{
				//static AutoCS AutoSEHCS;
				//EnterCriticalSection(AutoSEHCS);
				exceptInfo.ThreadId = GetCurrentThreadId();
				exceptInfo.ExceptionPointers = (EXCEPTION_POINTERS *)pExcept;
				exceptInfo.ClientPointers = TRUE;
				MINIDUMP_TYPE DumpType = MiniDumpNormal;
				pfnMiniDumpWriteDump( hProc, dwProcId, hFile, DumpType, 
					&exceptInfo, NULL, NULL);
				CloseHandle(hFile);
				//LeaveCriticalSection(AutoSEHCS);
			}
		}
		FreeLibrary(hDbgHelp);
	}
}

NET_CONN_INFO getConnectionInfo(JNIEnv *env, jstring dest,jstring domain,jstring user,jstring pwd)
{
	wchar_t* pDest = JStringToWCHAR(env, dest);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);

	NET_CONN_INFO info;
	memset(&info, 0, sizeof(NET_CONN_INFO));
	if (pDest!=NULL)
	{
		wcscpy_s(info.szDir, pDest);
		free(pDest);
	}
	if (pDomain != NULL)
	{
		wcscpy_s(info.szDomain, pDomain);
		free(pDomain);
	}
	if (pUser != NULL)
	{
		wcscpy_s(info.szUsr, pUser);
		free(pUser);
	}
	if (pPwd != NULL)
	{
		wcscpy_s(info.szPwd, pPwd);
		free(pPwd);
	}
	return info;
}

DWORD getAvailableMountDriveLetters(std::vector<wstring>& avaliableLetters)
{
	DWORD dwRet = GetLogicalDrives();
	if(dwRet == 0){
		return GetLastError();
	}
	
	for(int i=0; i<26; i++)
	{
		if(!(dwRet & (1<<i)))
		{
			wchar_t szChar[3] = {0};
			szChar[0] = L'A' + i;
			szChar[1] = L':';
			avaliableLetters.push_back(szChar);
		}
	}
	return dwRet;
}

DWORD getWindowsTempDir(wstring& strTempDir)
{
	WCHAR szBuf[1024+1] = {0};
	DWORD dwRet = 0;

	dwRet = GetTempPath(1024, szBuf);
	if(dwRet == 0)
	{
		return GetLastError();
	}

	strTempDir.append(szBuf);
	return 0;
}

/////////////////////////////////////////////////////////////////////////////////////
DWORD OpenRegistryKey(PTCHAR szKey, HKEY* phKey, BOOL bCreate)
{
	DWORD	rc = ERROR_SUCCESS;
	DWORD	dwDisposition;

	if (bCreate)
		rc = RegCreateKeyEx(HKEY_LOCAL_MACHINE,
		szKey,
		0,
		NULL,
		REG_OPTION_NON_VOLATILE,
		KEY_ALL_ACCESS,
		NULL,
		phKey,
		&dwDisposition);
	else
		rc = RegOpenKeyEx(HKEY_LOCAL_MACHINE,
		szKey,
		0,
		KEY_ALL_ACCESS,
		phKey);

	return rc;	
}


DWORD GetRegistryValue(PTCHAR szKey,  PTCHAR szValName, PDWORD  pdwValue,
					 BOOL bCreate, DWORD dwDefaultValue)
{
	HKEY	hKey;
	DWORD	dwValueType, dwBufSize;
	DWORD	rc = ERROR_SUCCESS;

	rc = OpenRegistryKey(szKey, &hKey, bCreate);
	if (rc != ERROR_SUCCESS) 
	{
		*pdwValue = dwDefaultValue;
		return rc;
	}

	// no matter if the key exists, query the value, if failed, create the value

	dwBufSize = sizeof(DWORD);
	rc = RegQueryValueEx(hKey,
		szValName,
		0,
		&dwValueType,
		(LPBYTE) pdwValue,
		&dwBufSize);

	if (rc != ERROR_SUCCESS && bCreate) 
	{
		rc = RegSetValueEx(hKey,
			szValName,
			0,
			REG_DWORD,
			(LPBYTE) &dwDefaultValue,
			sizeof(dwDefaultValue));

		*pdwValue = dwDefaultValue;
	}

	RegCloseKey(hKey);
	return rc;
}

DWORD QueryRegistryValue( PTCHAR szKey,
						  PTCHAR szSubKey,
						  PTCHAR szValName,
						  PDWORD pdwValue,
						  DWORD	 dwDefaultValue)
{
	TCHAR	szTempKey[MAX_PATH];
	_sntprintf_s(szTempKey,_countof(szTempKey),_TRUNCATE, _T("%s\\%s"), szKey, szSubKey);
	return GetRegistryValue(szTempKey, szValName, pdwValue, TRUE, dwDefaultValue);
}

LONG globalExceptionHandler(EXCEPTION_POINTERS *ExInfo)
{
	HandleSEH(pModuleName, "tomcat7.exe", ExInfo, 0);

	return( EXCEPTION_CONTINUE_SEARCH );
}

LPTOP_LEVEL_EXCEPTION_FILTER oldExceptionHandler = NULL;
void setExceptionHandler()
{
	oldExceptionHandler = SetUnhandledExceptionFilter( (LPTOP_LEVEL_EXCEPTION_FILTER)globalExceptionHandler );
}

void revertExceptionHandler()
{
	SetUnhandledExceptionFilter(oldExceptionHandler);
}