
#include "stdafx.h"
#include <LM.h>
#include "com_ca_arcflash_webservice_jni_WSJNI.h"
#include "HaCommonFunc.h"
#include "JNIConv.h"
#include "brandname.h"

extern HMODULE g_hModule;

//////////////////////////////////////////////////////////////////////////////

bool JbooleanToBool( jboolean value )
{
	return (value != JNI_FALSE);
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_createVssManager
  ( JNIEnv *, jclass )
{
	IVcmVssMgr * pVssManager = NULL;

	if (MVCM_CreateVssMgr( &pVssManager ) != 0)
		return 0;

	return (jlong)pVssManager;
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_vssManager_1Init
  ( JNIEnv *, jclass, jlong vssManagerHandle, jint flag, jboolean isPersistent )
{
	IVcmVssMgr * pVssManager = (IVcmVssMgr *)vssManagerHandle;
	if (pVssManager == NULL)
		return -1;

	if (pVssManager->Init( flag, JbooleanToBool( isPersistent ) ) != 0)
		return -1;

	return 0;
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_vssManager_1CreateSnapshotSet
  (JNIEnv * pEnv, jclass, jlong vssManagerHandle, jobject volumes)
{
	IVcmVssMgr * pVssManager = (IVcmVssMgr *)vssManagerHandle;
	if (pVssManager == NULL)
		return NULL;

	vector<wstring> vVolumes;

	jclass classList = pEnv->GetObjectClass( volumes );
	jmethodID midListSize = pEnv->GetMethodID( classList, "size", "()I" );
	jmethodID midListGet = pEnv->GetMethodID( classList, "get", "(I)Ljava/lang/Object;" );

	jint nListSize = pEnv->CallIntMethod( volumes, midListSize );
	for (jint i = 0; i < nListSize; i ++)
	{
		jstring volume = (jstring)pEnv->CallObjectMethod( volumes, midListGet, i );
		vVolumes.push_back( JStringToWString( pEnv, volume ) );
	}

	wstring snapshotSetId;
	if (pVssManager->CreateSnapshotSet( vVolumes, snapshotSetId ) != 0)
		return NULL;

	return WCHARToJString( pEnv, snapshotSetId.c_str() );
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_vssManager_1DeleteSnapshotSet
  (JNIEnv * pEnv, jclass, jlong vssManagerHandle, jstring snapshotSetId)
{
	IVcmVssMgr * pVssManager = (IVcmVssMgr *)vssManagerHandle;
	if (pVssManager == NULL)
		return -1;

	if (pVssManager->DeleteSnapshotSet( JStringToWString( pEnv, snapshotSetId ) ) != 0)
		return -1;

	return 0;
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_vssManager_1GetSnapshotSetByGuid
  (JNIEnv * pEnv, jclass, jlong vssManagerHandle, jstring snapshotSetId)
{
	IVcmVssMgr * pVssManager = (IVcmVssMgr *)vssManagerHandle;
	if (pVssManager == NULL)
		return 0;

	IVcmSnapshotSet * pSnapshotSet;

	if (pVssManager->GetSnapshotSetByGuid(
		JStringToWString( pEnv, snapshotSetId ), pSnapshotSet ) != 0)
		return 0;

	return (jlong)pSnapshotSet;
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_vssManager_1Release
  (JNIEnv *, jclass, jlong vssManagerHandle)
{
	IVcmVssMgr * pVssManager = (IVcmVssMgr *)vssManagerHandle;
	if (pVssManager == NULL)
		return;

	pVssManager->Release();
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_vcmSnapshotSet_1QuerySnapshotDeviceName
  (JNIEnv * pEnv, jclass, jlong snapshotSetHandle, jstring originalVolumeName)
{
	IVcmSnapshotSet * pSnapshotSet = (IVcmSnapshotSet *)snapshotSetHandle;
	if (pSnapshotSet == NULL)
		return NULL;

	wstring snapshotDeviceName;

	if (pSnapshotSet->QuerySnapshotDevName(
		JStringToWString( pEnv, originalVolumeName ), snapshotDeviceName ) != 0)
		return NULL;

	return WCHARToJString( pEnv, snapshotDeviceName.c_str() );
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_vcmSnapshotSet_1Release
  (JNIEnv *, jclass, jlong snapshotSetHandle)
{
	IVcmSnapshotSet * pSnapshotSet = (IVcmSnapshotSet *)snapshotSetHandle;
	if (pSnapshotSet == NULL)
		return;

	pSnapshotSet->Release();
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetIntactSessions
  (JNIEnv * pEnv, jclass clz, jstring sessionFolderPath, jobject sessionList)
{
	LPVOID handle = NULL;
	int * pSessionsBuffer = NULL;
	int sessionCount;

	__try
	{
		if (MVCM_OpenIntactSessionListHandle(
			JStringToWCHAR( pEnv, sessionFolderPath ), &handle ) != 0)
			return -1;

		if (MVCM_QueryIntactSessionList( handle, NULL, 0, &sessionCount ) != ERROR_MORE_DATA)
			return -1;

		pSessionsBuffer = new int[sessionCount];
		if (pSessionsBuffer == NULL)
			return -1;

		if (MVCM_QueryIntactSessionList(
			handle, pSessionsBuffer, sessionCount, &sessionCount ) != 0)
			return -1;

		for (int i = 0; i < sessionCount; i ++)
			AddIntToIntegerList( pEnv, sessionList, pSessionsBuffer[i] );

		return 0;
	}
	__finally
	{
		if (pSessionsBuffer != NULL)
			delete [] pSessionsBuffer;

		if (handle != NULL)
			MVCM_CloseIntactSessionListHandle( handle );
	}
}

//////////////////////////////////////////////////////////////////////////////

void ClearList( JNIEnv * pEnv, jobject listObject )
{
	jclass listClass = pEnv->GetObjectClass( listObject );
	jmethodID clearMethodID = pEnv->GetMethodID( listClass, "clear", "()V" );
	pEnv->CallVoidMethod( listObject, clearMethodID );

	if (listClass != NULL)
		pEnv->DeleteLocalRef( listClass );
}

//////////////////////////////////////////////////////////////////////////////
// Returns:
//
//	0	Succeed
//	1	Share name not found
//	-1	Error
//
int ConvertShareNameToLocalPath( LPCWSTR pszShareName, LPWSTR szBuffer, int cchBuffer, LPDWORD pdwErrorCode )
{
	NET_API_STATUS	status;
	SHARE_INFO_2 *	pShareInfo;

	status = NetShareGetInfo(
		NULL,					// local server
		(LPTSTR)pszShareName,	// net name
		2,						// SHARE_INFO_2
		(LPBYTE *)&pShareInfo );

	if (status == ERROR_SUCCESS)
	{
		_tcsncpy_s( szBuffer, cchBuffer, pShareInfo->shi2_path, _TRUNCATE );
		NetApiBufferFree( pShareInfo );
		return 0;
	}
	else if (status == NERR_NetNameNotFound)
	{
		return 1; // not found
	}
	else // error
	{
		if (pdwErrorCode != NULL)
			*pdwErrorCode = status;
		return -1;
	}
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetLocalPathOfShareName
  (JNIEnv * pEnv, jclass clz, jstring shareName, jobject localPathList, jobject errorCodeList)
{
	wchar_t *		pszShareName;
	wchar_t			szLocalPath[MAX_PATH + 1];
	NET_API_STATUS	status;
	int				nReturn;

	ClearList( pEnv, localPathList );
	ClearList( pEnv, errorCodeList );

	pszShareName = JStringToWCHAR( pEnv, shareName );
	nReturn = ConvertShareNameToLocalPath(
		pszShareName, szLocalPath, sizeof( szLocalPath ) / sizeof( wchar_t ), &status );

	if (nReturn == 0) // succeed
	{
		AddstringToList( pEnv, clz, szLocalPath, &localPathList );
	}
	else if (nReturn != 1) // error
	{
		AddIntToIntegerList( pEnv, errorCodeList, status );
	}

	return nReturn;
}

//////////////////////////////////////////////////////////////////////////////

#define REGKEY_D2DINSTALLPATH	(CST_REG_ROOT_L TEXT("\\InstallPath"))
#define REGVALUE_D2DINSTALLPATH	TEXT("Path")
#define ELEM_OF( array )		(sizeof( array ) / sizeof( array[0] ))

typedef bool (* PFN_GETRHASCENARIOSTATE)( const char* root_dir, int* state_index );
typedef bool (* PFN_ISSYNCREPLICATED)( const char* root_dir );
typedef bool (* PFN_SETTRCFILELOGPATH)( const char* trc_file, const char* log_path );

static HMODULE s_hRHAStateManagerDll = NULL;
static PFN_GETRHASCENARIOSTATE s_pfnGetRHAScenarioState = NULL;
static PFN_ISSYNCREPLICATED s_pfnIsSyncReplicated = NULL;

void SetLogPathForRHALibrary( HMODULE hDLL );

int GetModulePath( LPTSTR pszBuffer, int cchBuffer )
{
	TCHAR	szModuleName[MAX_PATH + 1];
	LPTSTR	pszLastSlash;

	if ((pszBuffer == NULL) || (cchBuffer <= 0))
		return -1;

	GetModuleFileName( g_hModule, szModuleName, ELEM_OF( szModuleName ) );
	pszLastSlash = _tcsrchr( szModuleName, TEXT('\\') );
	_tcsncpy_s( pszBuffer, cchBuffer, szModuleName, pszLastSlash - szModuleName + 1 );

	return 0;
}

int GetD2DInstallPath( LPTSTR pszBuffer, int cchBuffer )
{
	HKEY	hKey = NULL;
	DWORD	cbValueSize;
	LPBYTE	pValueData = NULL;
	int		nReturn = -1;

	if ((pszBuffer == NULL) || (cchBuffer <= 0))
		goto Exit;

	if (RegOpenKey( HKEY_LOCAL_MACHINE, REGKEY_D2DINSTALLPATH, &hKey ) != ERROR_SUCCESS)
		goto Exit;

	cbValueSize = 0;
	if (RegQueryValueEx(
		hKey, REGVALUE_D2DINSTALLPATH, NULL, NULL, NULL, &cbValueSize ) != ERROR_SUCCESS)
		goto Exit;

	pValueData = (LPBYTE)malloc( cbValueSize );
	if (pValueData == NULL)
		goto Exit;

	if (RegQueryValueEx(
		hKey, REGVALUE_D2DINSTALLPATH, NULL, NULL, pValueData, &cbValueSize ) != ERROR_SUCCESS)
		goto Exit;

	_tcsncpy_s( pszBuffer, cchBuffer, (LPCTSTR)pValueData, _TRUNCATE );

	nReturn = 0;

Exit:

	if (pValueData != NULL)
		free( pValueData );

	if (hKey != NULL)
		RegCloseKey( hKey );

	return nReturn;
}

int GetLogPath( LPTSTR pszBuffer, int cchBuffer )
{
	TCHAR	szPath[MAX_PATH + 1];
	int		cchPath, cchInstallPath;

	if ((pszBuffer == NULL) || (cchBuffer <= 0))
		return -1;

	cchPath = ELEM_OF( szPath );
	if (GetD2DInstallPath( szPath, cchPath ) != 0)
		return -1;

	cchInstallPath = _tcslen( szPath );
	if (szPath[cchInstallPath - 1] == TEXT('\\'))
		szPath[cchInstallPath - 1] = TEXT('\0');

	if (_stprintf_s( pszBuffer, cchBuffer, TEXT("%s\\Logs"), szPath ) == EINVAL)
		return -1;

	return 0;
}

int GetRHATrcFilePath( LPTSTR pszBuffer, int cchBuffer )
{
	TCHAR	szPath[MAX_PATH + 1];
	int		cchPath, cchInstallPath;

	if ((pszBuffer == NULL) || (cchBuffer <= 0))
		return -1;

	cchPath = ELEM_OF( szPath );
	if (GetModulePath( szPath, cchPath ) != 0)
		return -1;

	if (_stprintf_s( pszBuffer, cchBuffer,
		TEXT("%s\\RHA\\sync_stat.trc"), szPath ) == EINVAL)
		return -1;

	return 0;
}

BOOL LoadRHALibraries()
{
	TCHAR szDllPath[MAX_PATH + 1];
	int cchDllPath;
		
	cchDllPath = sizeof( szDllPath ) / sizeof( TCHAR );
	GetModulePath( szDllPath, cchDllPath );
	_tcscat_s( szDllPath, cchDllPath, TEXT("RHA\\stats_mng.dll") );

	SetLastError( 0 );
	HMODULE hDLL = LoadLibraryEx( szDllPath, NULL, LOAD_WITH_ALTERED_SEARCH_PATH );
	DWORD dwError = GetLastError();
	if (hDLL == NULL)
		return FALSE;

	PFN_GETRHASCENARIOSTATE pfn = (PFN_GETRHASCENARIOSTATE)
		GetProcAddress( hDLL, "get_scenario_state_index" );
	if (pfn == NULL)
		return FALSE;

	PFN_ISSYNCREPLICATED pfn2 = (PFN_ISSYNCREPLICATED)
		GetProcAddress( hDLL, "get_scenario_state_index" );
	if (pfn2 == NULL)
		return FALSE;

	SetLogPathForRHALibrary( hDLL );

	s_hRHAStateManagerDll = hDLL;
	s_pfnGetRHAScenarioState = pfn;
	s_pfnIsSyncReplicated = pfn2;

	return TRUE;
}

LPSTR GetUTF8String( LPCTSTR pszString )
{
	LPCWSTR	pszSrcString;
	LPSTR	pszDestString = NULL;
	LPWSTR	pszWString = NULL;
	int		cbRequired;

	if (sizeof( TCHAR ) == 1) // Ansi
	{
		cbRequired = MultiByteToWideChar(
			CP_ACP, 0, (LPCSTR)pszString, -1, NULL, 0 );

		pszWString = (LPWSTR)malloc( cbRequired );
		if (pszWString == NULL)
			goto Exit;

		if (MultiByteToWideChar(
			CP_ACP, 0, (LPCSTR)pszString, -1, pszWString, cbRequired ) == 0)
			goto Exit;

		pszSrcString = pszWString;
	}
	else if (sizeof( TCHAR ) == 2) // Unicode
	{
		pszSrcString = (LPCWSTR)pszString;
	}

	cbRequired = WideCharToMultiByte(
		CP_UTF8, 0, pszSrcString, -1, NULL, 0, NULL, NULL );

	pszDestString = (LPSTR)malloc( cbRequired );
	if (pszDestString == NULL)
		goto Exit;

	if (WideCharToMultiByte(
		CP_UTF8, 0, pszSrcString, -1, pszDestString, cbRequired,
		NULL, NULL ) == 0)
	{
		pszDestString = NULL;
		goto Exit;
	}

Exit:

	if (pszWString != NULL)
		free( pszWString );

	return pszDestString;
}

void FreeUTF8String( LPSTR pszString )
{
	if (pszString != NULL)
		free( pszString );
}

void SetLogPathForRHALibrary( HMODULE hDLL )
{
	TCHAR	szLogPath[MAX_PATH + 1];
	TCHAR	szTrcFilePath[MAX_PATH + 1];

	if (hDLL == NULL)
		return;

	PFN_SETTRCFILELOGPATH pfnSetTrcFileLogPath = (PFN_SETTRCFILELOGPATH)
		GetProcAddress( hDLL, "set_trc_file_log_path" );
	if (pfnSetTrcFileLogPath == NULL)
		return;

	if (GetLogPath( szLogPath, ELEM_OF( szLogPath ) ) != 0)
		return;

	if (GetRHATrcFilePath( szTrcFilePath, ELEM_OF( szTrcFilePath ) ) != 0)
		return;

	LPSTR pszUtf8LogPath = GetUTF8String( szLogPath );
	LPSTR pszUtf8TrcFilePath = GetUTF8String( szTrcFilePath );

	if ((pszUtf8LogPath != NULL) && (pszUtf8TrcFilePath != NULL))
		pfnSetTrcFileLogPath( pszUtf8TrcFilePath, pszUtf8LogPath );

	if (pszUtf8LogPath != NULL)
		FreeUTF8String( pszUtf8LogPath );

	if (pszUtf8TrcFilePath != NULL)
		FreeUTF8String( pszUtf8TrcFilePath );
}

PFN_GETRHASCENARIOSTATE GetRHAScenarioRetriever()
{
	if (s_pfnGetRHAScenarioState == NULL)
		LoadRHALibraries();

	return s_pfnGetRHAScenarioState;
}

PFN_ISSYNCREPLICATED GetRHASyncResultRetriever()
{
	if (s_pfnIsSyncReplicated == NULL)
		LoadRHALibraries();

	return s_pfnIsSyncReplicated;
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetRHAScenarioState
  (JNIEnv * pEnv, jclass, jstring rootPath, jobject resultCodeList)
{
	static const int RESULT_SUCCEED	= 0;
	static const int RESULT_FAILED	= -1;
	static const int STATE_UNKNOWN	= 3;

	int nResultCode = RESULT_FAILED;
	int nState = STATE_UNKNOWN;

	PFN_GETRHASCENARIOSTATE pfnGetRHAScenarioState = GetRHAScenarioRetriever();
	if (pfnGetRHAScenarioState == NULL)
		goto Exit;

	const char * pszRootPathInUTF8 = pEnv->GetStringUTFChars( rootPath, NULL );
	if (pszRootPathInUTF8 == NULL)
	{
		pEnv->ReleaseStringUTFChars( rootPath, pszRootPathInUTF8 );
		goto Exit;
	}

	bool bReturn = pfnGetRHAScenarioState( pszRootPathInUTF8, &nState );
	pEnv->ReleaseStringUTFChars( rootPath, pszRootPathInUTF8 );

	if (bReturn)
		nResultCode = RESULT_SUCCEED;

Exit:

	AddIntToIntegerList( pEnv, resultCodeList, nResultCode );
	return nState;
}

//////////////////////////////////////////////////////////////////////////////

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsRHASyncReplicated
  (JNIEnv * pEnv, jclass, jstring rootPath, jobject resultCodeList)
{
	static const int RESULT_SUCCEED	= 0;
	static const int RESULT_FAILED	= -1;

	int nResultCode = RESULT_FAILED;
	bool bIsSyncReplicated = FALSE;

	PFN_ISSYNCREPLICATED pfnIsSyncReplicated = GetRHASyncResultRetriever();
	if (pfnIsSyncReplicated == NULL)
		goto Exit;

	const char * pszRootPathInUTF8 = pEnv->GetStringUTFChars( rootPath, NULL );
	if (pszRootPathInUTF8 == NULL)
	{
		pEnv->ReleaseStringUTFChars( rootPath, pszRootPathInUTF8 );
		goto Exit;
	}
	
	bIsSyncReplicated = pfnIsSyncReplicated( pszRootPathInUTF8 );
	pEnv->ReleaseStringUTFChars( rootPath, pszRootPathInUTF8 );

	nResultCode = RESULT_SUCCEED;

Exit:

	AddIntToIntegerList( pEnv, resultCodeList, nResultCode );
	return bIsSyncReplicated;
}

// - end of file -
