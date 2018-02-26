// NativeFacade.cpp : Defines the entry point for the DLL application.
//
//#define _WIN32_WINNT 0x0502
#include <windows.h>
#include "stdafx.h"
#include "afjob.h"
#include "AFFileStoreGlobals.h"
#include "AFFileCatalog.h"
#include "NativeFacade.h"
#include "AFCoreAPIInterface.h"
#include "AFCoreFunction.h"
#include "HaCommonFunc.h"
#include "com_ca_arcflash_webservice_jni_WSJNI.h"
#include "com_ca_ha_webservice_jni_HyperVJNI.h"
#include "com_ca_ha_webservice_jni_VMWareJNI.h"
#include "HAClientWrapper.h"
#include "HyperVRepCallBack.h"
#include "VMwareCallback.h"
#include "Catalog.h"
#include "JNIConv.h"
#include "Log.h"
#include "comutil.h"
#include "HypervInterface.h"
#include "HAVhdUtility.h"

using namespace HyperVManipulation;
using namespace HaVhdUtility;
#include "utils.h"
#include <atlstr.h>
#include <string>
#include "FlashDB.h"
#include <iostream>
#include <time.h>
#include <TCHAR.H>
#include "drcore.h"
#include "..\..\Virtualization\VirtualStandby\HaUtility\ComEnvironment.h"
#include "HAClientProxy.h"
#include "HAServerProxy.h"
#include "GRTMntManager.h"
#include "D2DSwitch.h"
#include "..\Common\JNIMessageID.h"
#include "InstantVM.h"
#include "ClusterMonitor.h"
#include "InstantVM.h"
#include "JobMonitor.h"
#include "UDPMessage.h"
#include "FileCopyCommonJNI.h"
#include<algorithm>
using namespace std;



#include "iAppBrowse.h"
//////////////////////////////////////////////////////////////////////////
#include "D2DProxyImpl.h"
#include <drcommonlib.h>
#include <dbglog.h>
//////////////////////////////////////////////////////////////////////////
#include "Native_ASAG_CRC.h"
#include "UserImpersonateHelper.h"
#ifdef _MANAGED
#pragma managed(push, off)
#endif

BOOL g_bImpersonate = FALSE;

extern CDbgLog logObj;

#define DO_IMPERSONATE(x, y) DWORD dwType = 0; AFGetBakDestDriveType(y, dwType); { x = AFImpersonate();if(0 == x) g_bImpersonate = TRUE;}
#define RETWITHREVERT() if(g_bImpersonate) {AFRevertToSelf(); g_bImpersonate=FALSE;}


typedef BOOL (WINAPI *PFN_GetDiskSizeToSend)(ST_CONN_INFO* pstRoot, int nStartSessNo, int nEndSessNo, 
                            const wchar_t* pwszD2DName, int nBackupDescType, __int64* pllSize);

typedef BOOL (WINAPI *PFN_CheckVHDMerging)(const wstring& wVHDFile, DWORD dwBeginSessNumber, DWORD dwEndSessNumber);

// chefr03, SMART_COPY_BITMAP
typedef int (WINAPI *PFN_CreateSessionBitmap)(const wchar_t* pwszSrcSessionDest, int nBackupDescType, const wchar_t* pwszDestSessionDest);
typedef int (WINAPI *PFN_DeleteSessionBitmap)(const wchar_t* pwszSessionDest, const wchar_t* pwszSessionName);
typedef int (WINAPI *PFN_GetSessionBitmapList)(const wchar_t* pwszSessionDest, vector<wstring>& vSessionBitmapList);
typedef int (WINAPI *PFN_DetectAndRemoveObsolteBitmap)(const wchar_t* pwszSessionDest, const wchar_t* pwszBeginSession, const wchar_t* pwszEndSession);
// chefr03, SMART_COPY_BITMAP

// lijwe02 check disk size >>
typedef int (WINAPI *PFN_CheckIfExistDiskLargerThan2T)(bool& bExist);
// << lijwe02

HMODULE g_hModule = NULL;
WCHAR g_homePath[MAX_PATH];



DWORD QueryVDDKRegKey();

#define _INNERFREE(p) {if (p) {free(p); p = NULL;}}

BOOL APIENTRY DllMain( HMODULE hModule,
					  DWORD  ul_reason_for_call,
					  LPVOID lpReserved
					  )
{
	switch( ul_reason_for_call ) 
    { 
        case DLL_PROCESS_ATTACH:
			{
				// Initialize once for each new process.
				g_hModule = hModule;
				setExceptionHandler();
				if ( GetModuleFileName(hModule, g_homePath, _countof(g_homePath)) > 0 )
				{
					PWCHAR dirPos = wcsrchr(g_homePath, L'\\');
					if ( dirPos != NULL )
					{
						*(dirPos + 1) = L'\0';
					}

					PWCHAR binPos = wcsstr(g_homePath, L"\\BIN\\");

					if(binPos == NULL)
					{
						binPos = wcsstr(g_homePath, L"\\bin\\");
					}

					if ( binPos != NULL  && (wcslen(g_homePath)-wcslen(L"\\bin\\") == (binPos - g_homePath)))
					{
						*(binPos + 1) = L'\0';
					}
				}
				else
				{
					g_homePath[0] = L'\0';
				}

				// ---------------------------------------------------------
				// for all debug log output to tomcat.exe_<pid>.log
				//
				WCHAR szLogFile[MAX_PATH] = {0};
				::GetModuleFileName( NULL, szLogFile, _ARRAYSIZE(szLogFile) );
				if( wcslen( szLogFile ) > 0 )
				{
					WCHAR* ptr = wcsrchr( szLogFile, L'\\' );
					if( ptr )
					{
						ptr++;
						if( wcslen(ptr) >= wcslen(L"tomcat") &&
							_wcsnicmp( ptr, L"tomcat", wcslen(L"tomcat") )==0 )
						{
							_snwprintf_s( szLogFile, _countof(szLogFile), _TRUNCATE, L"tomcat_%d.log", ::GetCurrentProcessId() );
							CDbgLog::SetGlobalLogFileName( szLogFile );
						}
					}
				}
				//
				// end
				// ---------------------------------------------------------
			}
			
         break;

        case DLL_THREAD_ATTACH:
         // Do thread-specific initialization.
            break;

        case DLL_THREAD_DETACH:
         // Do thread-specific cleanup.
            break;

        case DLL_PROCESS_DETACH:
			{
	         // Perform any necessary cleanup.
				revertExceptionHandler();
				CDbgLog dbgLog;
				dbgLog.LogW(LL_INF, 0, L"NativeFacade.dll was detached.");
	            break;
    		}
	}

	return TRUE;
}

#ifdef _MANAGED
#pragma managed(pop)
#endif

BOOL IsAdmin(HANDLE hToken)
{
	SID_IDENTIFIER_AUTHORITY NtAuthority = SECURITY_NT_AUTHORITY;
	PSID AdministratorsGroup; 
	BOOL b = AllocateAndInitializeSid(
		&NtAuthority,
		2,
		SECURITY_BUILTIN_DOMAIN_RID,
		DOMAIN_ALIAS_RID_ADMINS,
		0, 0, 0, 0, 0, 0,
		&AdministratorsGroup); 
	if(b) 
	{
		if (!CheckTokenMembership(hToken, AdministratorsGroup, &b)) 
		{
			b = FALSE;
		} 
		FreeSid(AdministratorsGroup); 
	}

	return(b);
}

DWORD MyLogonUser(LPTSTR lpszUsername,LPTSTR lpszDomain,LPTSTR lpszPassword, HANDLE* hToken)
{
	HANDLE hToken1;
	BOOL bRet =  LogonUser(
		lpszUsername,
		lpszDomain,
		lpszPassword,
		LOGON32_LOGON_BATCH,
		LOGON32_PROVIDER_DEFAULT,
		&hToken1
		);
	if(!bRet)
	{
		DWORD dwRet = GetLastError();
		if(dwRet == 1385)
		{
			bRet = TRUE;
		}
	}
	else
	{
		CloseHandle(hToken1);
	}

	if(bRet)
	{
		bRet = LogonUser(
			lpszUsername,
			lpszDomain,
			lpszPassword,
			LOGON32_LOGON_NETWORK,
			LOGON32_PROVIDER_DEFAULT,
			hToken
			);
	}
	return bRet;
}
int Validate(LPTSTR lpszUsername,LPTSTR lpszDomain,LPTSTR lpszPassword)
{
	int ret = 0;

	LPTSTR lpUserName = lpszUsername;
	LPTSTR lpDomain = lpszDomain;
	size_t iLen = _tcslen(lpUserName);
	if(iLen== 0)
	{
		return 1;
	}
	WCHAR szComputerName[MAX_COMPUTERNAME_LENGTH+1] = {0};
	BOOL bUpnFormat = FALSE;

	LPTSTR lpDomainName = NULL;
	for(size_t i=0; i<iLen; i++)
	{
		TCHAR cChar = lpszUsername[i];
		if((cChar == '\\') || (cChar == '/'))
		{
			//if the name format:domain\user, we always use the domain name 
			//and don't care lpszDomain is NULL or not
			lpDomainName = (LPTSTR)malloc(sizeof(TCHAR)*(i+1));
			ZeroMemory(lpDomainName, sizeof(TCHAR)*(i+1));
			_tcsncpy_s(lpDomainName, sizeof(TCHAR)*i, lpszUsername, i);
			if(i+1 < iLen)
			{
				lpUserName = lpszUsername + i+1;
			}
			lpDomain = lpDomainName;
			break;
		}
		else if(cChar == '@')
		{
			//if the user name is UPN format
			lpDomain = NULL;
			bUpnFormat = TRUE;
			break;
		}
	}

	LPTSTR lpOriginalDomain = NULL;
	if(!bUpnFormat)
	{

		DWORD dwSize = MAX_COMPUTERNAME_LENGTH+1;
		BOOL bRet = GetComputerNameW(szComputerName, &dwSize);
		if(lpDomain == NULL)
		{
			if(bRet)
			{
				lpDomain = szComputerName;
			}
			else
			{
				lpDomain = L".";
			}
		}
		else
		{
			if(wcslen(lpDomain)>MAX_COMPUTERNAME_LENGTH)
			{
				if(_wcsnicmp(lpDomain, szComputerName, MAX_COMPUTERNAME_LENGTH) == 0)
				{
					lpOriginalDomain = lpDomain;
					lpDomain = szComputerName;
				}
			}
		}
	}

	HANDLE hToken;
	BOOL isValidUser = MyLogonUser(
		lpUserName,
		lpDomain,
		lpszPassword,
		&hToken
		);

	if(!isValidUser)
	{	
		ret = 1;
		if(lpOriginalDomain)
		{
			isValidUser = MyLogonUser(
				lpUserName,
				lpOriginalDomain,
				lpszPassword,
				&hToken
				);
			if(!isValidUser)
			{
				ret = 1;
			}
			else
			{
				ret = 0;
			}
		}
	}

	if(isValidUser)
	{
		BOOL isAdmin = IsAdmin(hToken);	
		if(!isAdmin)
		{
			ret = 2;
		}
	}

	if(lpDomainName)
	{
		free(lpDomainName);
	}

	if(isValidUser &&hToken)
	{
		CloseHandle(hToken);
		hToken = NULL;

	}

	return ret;
}

void ThrowWSJNIException(JNIEnv * env, jstring message, jint code)
{
	jclass jWSJNICls = env->FindClass("com/ca/arcflash/webservice/jni/WSJNI");
	jmethodID methodID=env->GetStaticMethodID(jWSJNICls, "throwWSJNIException", "(Ljava/lang/String;I)V");
	env->CallStaticVoidMethod(jWSJNICls, methodID, message, code);
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_validate
(JNIEnv *env, jclass jobject, jstring username, jstring domainname, jstring password)
{
	wchar_t* sname = JStringToWCHAR(env, username);
	wchar_t* domain = JStringToWCHAR(env, domainname);
	wchar_t* pwd = JStringToWCHAR(env, password);
	int ret = Validate((LPTSTR)sname,(LPTSTR)domain,(LPTSTR)pwd);

	if(sname != NULL)
	{
		free(sname);
	}

	if(domain != NULL)
	{
		free(domain);
	}

	if(pwd != NULL)
	{
		free(pwd);
	}
	return ret;
}

//==================vss related begin==========================================
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_browseAppInforamtion
(JNIEnv *env, jclass jclz, jobject arr)
{	
	jclass class_ArrayList = env->GetObjectClass(arr);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	wchar_t* pBrowseInfo = NULL;
	DWORD dwBrowseInfoSize = 0;
	DWORD dwRet = BrowseAppInforamtion(&pBrowseInfo, &dwBrowseInfoSize, FALSE);
	if(dwRet == 0)
	{
		jstring ret = env->NewString((jchar*)pBrowseInfo, dwBrowseInfoSize);				
		env->CallBooleanMethod(arr, id_ArrayList_add,ret);
		ReleaseBrowseInformation(&pBrowseInfo);
	}
	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_BrowseVolumeInforamtion
(JNIEnv *env, jclass jclz, jobject arr, jboolean details, jstring backupdest)
{
	// 215706 - without impersonation, the calling of windows API GetVolumePathName is very slow,
	// that will cause web service timeout
	AFImpersonate();

	jclass class_ArrayList = env->GetObjectClass(arr);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
	wchar_t* pBrowseInfo = NULL;
	DWORD dwBrowseInfoSize = 0;
	wchar_t* pDestPath = NULL;
	if(backupdest != NULL)
		pDestPath = JStringToWCHAR(env, backupdest);

	DWORD dwRet = BrowseVolumeInforamtion(&pBrowseInfo, &dwBrowseInfoSize, FALSE, (BOOL)details, pDestPath);
	if(dwRet == 0)
	{
		jstring ret = env->NewString((jchar*)pBrowseInfo, dwBrowseInfoSize);
		env->CallBooleanMethod(arr, id_ArrayList_add,ret);
		ReleaseBrowseInformation(&pBrowseInfo);
	}

	if(pDestPath != NULL)
		free(pDestPath);

	return dwRet;

}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_browseAppInforamtion2File
(JNIEnv *env, jclass jclz, jstring filename)
{
	wchar_t* fname = JStringToWCHAR(env, filename);
	DWORD dwFnameChCnt = wcslen(fname) + 1;
	DWORD dwRet = BrowseAppInforamtion(&fname, &dwFnameChCnt, TRUE);
	if(fname != NULL)
	{
		free(fname);
	}
	return (jint)dwRet;
}
//==================vss related end==========================================

//==================catalog related begin==========================================

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GenerateIndexFiles
(JNIEnv*env, jclass jclz, jstring catalogName)
{
	wchar_t* catname = JStringToWCHAR(env, catalogName);
	DWORD ret = GenerateIndexFiles(catname);
	if(catname != NULL)
	{
		free(catname);
	}
	return JNI_OK;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_browseCatalogDetail
(JNIEnv *env, jclass jclz, jstring catalogName, jobject retArr)
{
	wchar_t* catname = JStringToWCHAR(env, catalogName);

	HANDLE h = OpenCatalogFile(catname);
	if(h != NULL) {
		UINT Cnt = 0; 

		// longNameID -1 means the first level under volume.
		PDetailW detail = GetChildren(h, -1, &Cnt);		 
		for(UINT i = 0; i < Cnt; i++) {						
			AddDetailW2List(env, &retArr, &detail[i]);
		}
		HeapFree(GetProcessHeap(), 0, detail);
		CloseCatalogFile(h);
	}
	if(catname != NULL)
	{
		free(catname);
	}
	return JNI_OK;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_browseCatalogChildren
(JNIEnv *env, jclass jclz, jstring catalogName, jlong longNameID, jobject retArr)
{	
	wchar_t* catname = JStringToWCHAR(env, catalogName);
	HANDLE h = OpenCatalogFile(catname);
	if(h != NULL) {
		UINT Cnt; 
		PDetailW detail = GetChildren(h, longNameID, &Cnt);		
		for(UINT i = 0; i < Cnt; i++) {				
			AddDetailW2List(env, &retArr, &detail[i]);				
		}
		HeapFree(GetProcessHeap(), 0, detail);
		CloseCatalogFile(h);
	}
	if(catname != NULL)
	{
		free(catname);
	}

	return 0;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_OpenCatalogFile
(JNIEnv *env, jclass clz, jstring catalogName)
{
	wchar_t* catname = JStringToWCHAR(env, catalogName);
	HANDLE h = OpenCatalogFile(catname);
	if(catname != NULL)
	{
		free(catname);
	}
	return (jlong)h;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetChildren
(JNIEnv *env, jclass clz, jlong jhandle, jlong longNameID, jobject retArr)
{	 
	HANDLE h = (HANDLE)jhandle;
	if(h!=NULL)
	{		
		PDetailW pchild = NULL;
		UINT Cnt = 0;

		pchild = GetChildren(h, longNameID, &Cnt);

		for(UINT i = 0; i < Cnt; i++) {				
			AddDetailW2List(env, &retArr, &pchild[i]);				
		}

		HeapFree(GetProcessHeap(), 0, pchild);
	}

	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetChildrenCount
(JNIEnv *env, jclass jclz, jlong jhandle, jlong jLogNameid, jobject jnCnt)
{
	HANDLE h = (HANDLE)jhandle;
	UINT ttlCatCnt;
	if(h != NULL)
	{
		ttlCatCnt = GetChildrenCount( h, jLogNameid );	
	}
	AddUINT2JRWLong(env, ttlCatCnt, &jnCnt);
	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetChildrenEx
(JNIEnv *env, jclass jclz, jlong jhandle, jlong longNameid, jint jnStart, jint jnRequest, jobject jnCnt, jobject retArr)
{
		UINT nFound;
		HANDLE h = (HANDLE)jhandle;
		if(h != NULL)
		{
			PDetailW pd = GetChildrenEx(h,longNameid, jnStart, jnRequest, &nFound);
			for(UINT i = 0; i < nFound; i++) {			
				AddDetailW2List(env, &retArr, &pd[i]);				
			}
			AddUINT2JRWLong(env, nFound, &jnCnt);

			if(pd!=NULL)
			{
				HeapFree(GetProcessHeap(), 0, pd);
			}
		}

		return JNI_OK;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetFullPath
(JNIEnv *env, jclass jclz, jlong jhandle, jlong pathId, jobject fullPath)
{
	HANDLE h = (HANDLE)jhandle;
	if(h != NULL)
	{
		wchar_t lpString[2*MAX_PATH+1] ={0};		
		wchar_t * ret = GetFullPath(h, pathId, lpString, _countof(lpString));
		AddstringToList(env, jclz, lpString, &fullPath);
	}

	return JNI_OK;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CloseCatalogFile
(JNIEnv *env, jclass clz, jlong jhandle)
{
	HANDLE h = (HANDLE)jhandle;
	if(h!=NULL)
	{
		CloseCatalogFile(h);
	}
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SearchCatalogFile
(JNIEnv *env, jclass clz, jstring destination, jstring sDir, jboolean bCaseSensitive, jboolean bIncludeSubDir, jstring pattern, jobject totalCnt)
{
	wchar_t* pDestination = JStringToWCHAR(env, destination);
	wchar_t* pDir = JStringToWCHAR(env, sDir);
	wchar_t* ptn = JStringToWCHAR(env, pattern);
	UINT ttlCatCnt;
	//SYSTEMTIME startDatetime;
	//memset(&startDatetime,0,sizeof(SYSTEMTIME));
	//SYSTEMTIME endDatetime;
	//memset(&endDatetime,0,sizeof(SYSTEMTIME));
	unsigned long begin_sesstime = 0;
	unsigned long end_sesstime = 0;
	HANDLE h = SearchCatalogFile(pDestination, begin_sesstime, end_sesstime, pDir, (BOOL)bCaseSensitive, (BOOL)bIncludeSubDir, ptn, &ttlCatCnt);

	if(pDestination != NULL)
	{
		free(pDestination);
		pDestination = NULL;
	}
	if(pDir != NULL)
	{
		free(pDir);
		pDir = NULL;
	}
	if(ptn != NULL)
	{
		free(ptn);
		ptn = NULL;
	}
	AddUINT2JRWLong(env, ttlCatCnt, &totalCnt);
	return (jlong)h;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_FindNextCatalogFile
(JNIEnv *env, jclass clz, jlong jhandle, jlong nRequest, jobject retArr, jobject jnFound, jobject jnCurrent)
{	 
	HANDLE h = (HANDLE)jhandle;
	if(h!=NULL)
	{
		PDetailW pd = NULL;	

		UINT nFound = 0;
		UINT nCur = FindNextCatalogFile(h, (UINT)nRequest,&pd, &nFound);	

		for(UINT i = 0; i < nFound; i++) {			
			AddDetailW2List(env, &retArr, &pd[i]);				
		}
		AddUINT2JRWLong(env, nCur, &jnCurrent);
		AddUINT2JRWLong(env, nFound, &jnFound);

		if(pd!=NULL)
		{
			HeapFree(GetProcessHeap(), 0, pd);
		}
	}

	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetD2DFileSizeByte
(JNIEnv *env, jclass clz, jstring strDirPath, jstring strUser, jstring strPwd, jstring d2dFile, jint jnStartSessNo, jint jnEndSessNo, jint jnBackupDescType)
{	 
	HMODULE hMountHACommondll = NULL; 
	__int64 fileSize=0;
	UINT nStartSessNo=0, nEndSessNo=0;
	UINT ret=0;

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if( hMountHACommondll == NULL )
	{
		return -1;
	}

    wchar_t* d2dFileName	= JStringToWCHAR(env, d2dFile);
	wchar_t* wszDirPath	= JStringToWCHAR(env, strDirPath);
	wchar_t* wszUser	= JStringToWCHAR(env, strUser);
	wchar_t* wszPwd		= JStringToWCHAR(env, strPwd);

	nStartSessNo	= jnStartSessNo;
	nEndSessNo		= jnEndSessNo;

	PFN_GetDiskSizeToSend pfnGetDiskSizeToSend = NULL;

	ST_CONN_INFO conn_info;
	conn_info.pwszDir	= wszDirPath; 
	conn_info.pwszUser	= wszUser;
	conn_info.pwszPwd	= wszPwd;

	pfnGetDiskSizeToSend = (PFN_GetDiskSizeToSend)GetProcAddress(hMountHACommondll, "GetDiskSizeToSend");
	ret = (*pfnGetDiskSizeToSend)(&conn_info,nStartSessNo, nEndSessNo, d2dFileName, jnBackupDescType, &fileSize);

    _INNERFREE(d2dFileName);
    _INNERFREE(wszDirPath);
    _INNERFREE(wszUser);
    _INNERFREE(wszPwd);

	if (hMountHACommondll != NULL)
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}

	if(0 != ret)
	{
		return -1;
	}
	else 
		return fileSize;
		
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetFindFullPath
(JNIEnv *env, jclass jclz, jlong jhandle, jlong pathId, jobject fullPath)
{
	HANDLE h = (HANDLE)jhandle;
	if(h != NULL)
	{
		wchar_t lpString[2*MAX_PATH+1] ={0};		
		wchar_t * ret = GetFindFullPath(h, pathId, lpString, _countof(lpString));
		AddstringToList(env, jclz, lpString, &fullPath);
	}

	return JNI_OK;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_FindCloseCatalogFile
(JNIEnv *env, jclass clz, jlong jhandle)
{
	HANDLE h = (HANDLE)jhandle;
	if(h != NULL)
	{
		FindCloseCatalogFile(h);
	}
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SearchCatalogFileEx
(JNIEnv *env, jclass clz, jstring catalogFileFolder, jstring sDir, jboolean bCaseSensitive, jboolean bIncludeSubDir, jstring pattern, jobject totalCnt)
{
	wchar_t* pDestination = JStringToWCHAR(env, catalogFileFolder);
	wchar_t* pDir = JStringToWCHAR(env, sDir);
	wchar_t* ptn = JStringToWCHAR(env, pattern);
	UINT ttlCatCnt;
	unsigned long begin_sesstime = 0;
	unsigned long end_sesstime = 0;
	HANDLE h = SearchCatalogFileEx(pDestination, begin_sesstime, end_sesstime, pDir, (BOOL)bCaseSensitive, (BOOL)bIncludeSubDir, ptn, &ttlCatCnt);

	if(pDestination != NULL)
	{
		free(pDestination);
		pDestination = NULL;
	}
	if(pDir != NULL)
	{
		free(pDir);
		pDir = NULL;
	}
	if(ptn != NULL)
	{
		free(ptn);
		ptn = NULL;
	}
	AddUINT2JRWLong(env, ttlCatCnt, &totalCnt);
	return (jlong)h;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_FindNextCatalogFileEx
(JNIEnv *env, jclass clz, jlong jhandle, jlong nRequest, jobject retArr, jobject jnFound, jobject jnCurrent)
{	 
	HANDLE h = (HANDLE)jhandle;
	if(h!=NULL)
	{
		PDetailW pd = NULL;	

		UINT nFound = 0;
		UINT nCur = FindNextCatalogFileEx(h, (UINT)nRequest,&pd, &nFound);	

		for(UINT i = 0; i < nFound; i++) {			
			AddDetailW2List(env, &retArr, &pd[i]);				
		}
		AddUINT2JRWLong(env, nCur, &jnCurrent);
		AddUINT2JRWLong(env, nFound, &jnFound);

		if(pd!=NULL)
		{
			HeapFree(GetProcessHeap(), 0, pd);
		}
	}

	return JNI_OK;
}
//==================catalog related end==========================================


//==================backup/Restore begin==========================================
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFBackup
(JNIEnv *env, jclass jclz, jobject bkpJS)
{
	AFJOBSCRIPT afJS;
	memset(&afJS, 0, sizeof(AFJOBSCRIPT));
	JJobScript2AFJOBSCRIPT(env, &afJS, &bkpJS);
	DWORD dwRet = AFBackup(&afJS, NULL, NULL);
	FreeAFJOBSCRIPT(&afJS);

	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFRestore
(JNIEnv *env, jclass clz,jobject jResJob)
{
	AFJOBSCRIPT afJS;
	memset(&afJS, 0, sizeof(AFJOBSCRIPT));
	JJobScript2AFJOBSCRIPT(env, &afJS, &jResJob);
	DWORD dwRet = AFRestore(&afJS, NULL, NULL);
	FreeAFJOBSCRIPT(&afJS);
	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCopy
(JNIEnv *env, jclass clz,jobject jCopyJob)
{
	AFJOBSCRIPT afJS;
	memset(&afJS, 0, sizeof(AFJOBSCRIPT));
	JJobScript2AFJOBSCRIPT(env, &afJS, &jCopyJob);	
	DWORD dwRet = AFCopy(&afJS, NULL, NULL);
	FreeAFJOBSCRIPT(&afJS);
	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFSetJobStatus
(JNIEnv *env, jclass clz, jobject jJS)
{
	JobStatus jobStatus;
	memset(&jobStatus, 0, sizeof(JobStatus));
	jJSToPJobStatus(env,&jobStatus, &jJS);
	DWORD dwRet = -1;// = AFSetJobStatus(IN &jobStatus);
	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetJobStatus
(JNIEnv *env, jclass clz, jobject jJS)
{	
	JobStatus jobStatus;
	memset(&jobStatus, 0, sizeof(JobStatus));
	DWORD dwRet = AFGetJobStatus(OUT &jobStatus);
	AddJobStatusTojJS(env, &jobStatus, &jJS);
	return dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetRecoveryPoint
(JNIEnv *env, jclass clz, jstring destination,jstring domain,jstring user,jstring pwd,jstring jStringBeginDate, jstring jStringEndDate, jobject list, jboolean jQueryDetail)
{
	wchar_t* pDest = JStringToWCHAR(env, destination);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);
	wchar_t* pBeginDate = JStringToWCHAR(env, jStringBeginDate);
	wchar_t* pEndDate = JStringToWCHAR(env, jStringEndDate);

	DWORD dwRet = 0;
	IRestorePoint *pIRest = NULL;

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

	dwRet = CreateIRestorePoint(info,&pIRest);

	if(!dwRet)
	{
		VRESTORE_POINT vRest;
		VRESTORE_POINT::iterator itrRest;
		VRESTORE_POINT_ITEM::iterator itrItem;

		pIRest->GetRestorePoints(pBeginDate, pEndDate,vRest, (BOOL)jQueryDetail);
		for(itrRest = vRest.begin(); itrRest != vRest.end(); itrRest++)
		{
			for(itrItem = itrRest->vRestPointItem.begin(); itrItem != itrRest->vRestPointItem.end(); itrItem++)
			{
				AddRestorePoint2List(env,&list,itrItem, (wchar_t*)itrRest->strDate.c_str());
			}
		}
		pIRest->Release();
	}else
	{
		wprintf(L"create restore point[%s] failed\n", destination);

	}

	if (pBeginDate != NULL)
	{	
		free(pBeginDate);
	}

	if (pEndDate != NULL)
	{
		free(pEndDate);
	}

	return dwRet;
}

/*
*  This API is used in ASBU copy to tape only.
*  The purpose of this API is to reduce disk IO, it only read backupinfodb.xml to get recovery points list.
*  The destination is like ...\<machine name>\
*/
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_ASBUGetRecoveryPoint
(JNIEnv *env, jclass clz, jstring destination, jstring jStringBeginDate, jstring jStringEndDate, jobject list, jboolean jQueryDetail)
{
	wchar_t* pDest = JStringToWCHAR(env, destination);
	wchar_t* pBeginDate = JStringToWCHAR(env, jStringBeginDate);
	wchar_t* pEndDate = JStringToWCHAR(env, jStringEndDate);

	DWORD dwRet = 0;
	IRestorePoint *pIRest = NULL;

	dwRet = ASBU_CreateIRestorePoint(pDest, &pIRest);

	if (!dwRet)
	{
		VRESTORE_POINT vRest;
		VRESTORE_POINT::iterator itrRest;
		VRESTORE_POINT_ITEM::iterator itrItem;

		pIRest->GetRestorePoints(pBeginDate, pEndDate, vRest, (BOOL)jQueryDetail);
		for (itrRest = vRest.begin(); itrRest != vRest.end(); itrRest++)
		{
			for (itrItem = itrRest->vRestPointItem.begin(); itrItem != itrRest->vRestPointItem.end(); itrItem++)
			{
				AddRestorePoint2List(env, &list, itrItem, (wchar_t*)itrRest->strDate.c_str());
			}
		}
		pIRest->Release();
	}
	else
	{
		wprintf(L"create restore point[%s] failed\n", destination);

	}

	if (pBeginDate != NULL)
	{
		free(pBeginDate);
	}

	if (pEndDate != NULL)
	{
		free(pEndDate);
	}

	if (pDest != NULL)
	{
		free(pDest);
	}

	return dwRet;
}


JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetBackupInfoSummary
(JNIEnv *env, jclass clz, jstring destination, jstring domain,jstring user,jstring pwd, jobject summary, jboolean onlyCapacity,jstring foldername)
{
	IBackupSumm *pSumm = NULL;
	wchar_t* pDest = JStringToWCHAR(env, destination);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);
	wchar_t* pFolder = JStringToWCHAR(env, foldername);

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

	DWORD dwRet = CreateIBackupSumm(info, &pSumm);  //first, create the interface
	if(dwRet)
	{
		wprintf(L"failed to CreateIBackupSumm[%d]\n", dwRet);
		return dwRet;
	}

	jclass class_JBackupInfoSummary = env->GetObjectClass(summary);

	if(!((BOOL)onlyCapacity)){		

		//Set Recovery Point Count
		jfieldID field_RecoveryPointCount	= env->GetFieldID(class_JBackupInfoSummary, "recoveryPointCount", "I");
		if(field_RecoveryPointCount != 0){
			env->SetIntField(summary, field_RecoveryPointCount, pSumm->GetTotalNumberOfRestorePoints());
		}

		jfieldID field_recoveryPointCount4Repeat = env->GetFieldID(class_JBackupInfoSummary, "recoveryPointCount4Repeat", "I");
		if(field_recoveryPointCount4Repeat != 0){
			env->SetIntField(summary, field_recoveryPointCount4Repeat, pSumm->GetTotalNumberOfRestorePointsByCategory(QJDTO_B_REAPEAT_BACKUP));
		}

		jfieldID field_recoveryPointCount4Day = env->GetFieldID(class_JBackupInfoSummary, "recoveryPointCount4Day", "I");
		if(field_recoveryPointCount4Day != 0){
			env->SetIntField(summary, field_recoveryPointCount4Day, pSumm->GetTotalNumberOfRestorePointsByCategory(QJDTO_B_DAILY_BACKUP));
		}

		jfieldID field_recoveryPointCount4Week = env->GetFieldID(class_JBackupInfoSummary, "recoveryPointCount4Week", "I");
		if(field_recoveryPointCount4Week != 0){
			env->SetIntField(summary, field_recoveryPointCount4Week, pSumm->GetTotalNumberOfRestorePointsByCategory(QJDTO_B_WEEKLY_BACKUP));
		}

		jfieldID field_recoveryPointCount4Month = env->GetFieldID(class_JBackupInfoSummary, "recoveryPointCount4Month", "I");
		if(field_recoveryPointCount4Month != 0){
			env->SetIntField(summary, field_recoveryPointCount4Month, pSumm->GetTotalNumberOfRestorePointsByCategory(QJDTO_B_MONTHLY_BACKUP));
		}


		jfieldID field_RecoverySetCount = env->GetFieldID(class_JBackupInfoSummary, "reocverySetCount", "I");
		env->SetIntField(summary, field_RecoverySetCount, pSumm->GetTotalNumberOfRestoreSet());

		VBACKUP_INFO vBakInfo;
		VBACKUP_INFO::iterator itrBakInfo;
		dwRet = pSumm->GetBackupInfoSumm(vBakInfo,pFolder);
	
		jclass class_JBackupInfo = env->FindClass("com/ca/arcflash/webservice/jni/model/JBackupInfo");	
		jmethodID jBackupInfo_constructor = env->GetMethodID(class_JBackupInfo, "<init>", "()V");
	
		jclass class_ArrayList = env->FindClass("java/util/List");
		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

		jfieldID field_BackupInfoList= env->GetFieldID(class_JBackupInfoSummary, "backupInfoList", "Ljava/util/List;");
		jobject backupInfoList = env->GetObjectField(summary,field_BackupInfoList);
		jfieldID field_PeriodFlag = env->GetFieldID(class_JBackupInfo, "periodRetentionFlag", "I");
		for(itrBakInfo = vBakInfo.begin(); itrBakInfo != vBakInfo.end(); itrBakInfo++)
		{
			jobject jBackupInfo = env->NewObject(class_JBackupInfo, jBackupInfo_constructor);

			SetStringValue2Field(env, jBackupInfo, "status", itrBakInfo->strStatus);
			SetStringValue2Field(env, jBackupInfo, "type", itrBakInfo->strType);
			SetStringValue2Field(env, jBackupInfo, "date", itrBakInfo->strDate);
			SetStringValue2Field(env, jBackupInfo, "time", itrBakInfo->strDetailTime);
			SetStringValue2Field(env, jBackupInfo, "size", itrBakInfo->strSize);			
			env->SetIntField(jBackupInfo, field_PeriodFlag, itrBakInfo->dwBKAdvSchFlag);			

			env->CallBooleanMethod(backupInfoList, id_ArrayList_add, jBackupInfo);
			if (jBackupInfo != NULL) 
			{
				env->DeleteLocalRef(jBackupInfo);
				jBackupInfo = NULL;
			}
		}

		if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
		if (class_JBackupInfo != NULL) env->DeleteLocalRef(class_JBackupInfo);
	}

	//Get destination capacity
	BACKUP_DEV_INFO backupDevInfo;
	dwRet = pSumm->GetBackupDevInfo(NULL,backupDevInfo);

	if(dwRet)
	{
		if (class_JBackupInfoSummary != NULL) env->DeleteLocalRef(class_JBackupInfoSummary);
		pSumm->Release();  //after use it, please don’t forget to release it.

		wprintf(L"failed to CreateIBackupSumm[%d]\n", dwRet);
		return dwRet;
	}

	jfieldID field_BackupDestination= env->GetFieldID(class_JBackupInfoSummary, "destinationInfo", "Lcom/ca/arcflash/webservice/jni/model/JBackupDestinationInfo;");
	jobject jDestinationInfo = env->GetObjectField(summary,field_BackupDestination);

	SetStringValue2Field(env, jDestinationInfo, "fullBackupSize", backupDevInfo.strFullDataSize);
	SetStringValue2Field(env, jDestinationInfo, "incrementalBackupSize", backupDevInfo.strIncDataSize);
	SetStringValue2Field(env, jDestinationInfo, "resyncBackupSize", backupDevInfo.strResyncDataSize);
	SetStringValue2Field(env, jDestinationInfo, "totalSize", backupDevInfo.strTotalSpace);
	SetStringValue2Field(env, jDestinationInfo, "totalFreeSize", backupDevInfo.strFreeSpace);
	SetStringValue2Field(env, jDestinationInfo, "catalogSize", backupDevInfo.strCatalogSize);

	if (class_JBackupInfoSummary != NULL) env->DeleteLocalRef(class_JBackupInfoSummary);
	pSumm->Release();  //after use it, please don’t forget to release it.
return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetProtectionInformation
(JNIEnv *env, jclass clz, jstring destination, jstring domain,jstring user,jstring pwd, jobject list)
{
	IBackupSumm *pSumm = NULL;
	wchar_t* pDest = JStringToWCHAR(env, destination);
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

	DWORD dwRet = CreateIBackupSumm(info, &pSumm);  //first, create the interface
	if(dwRet)
	{
		wprintf(L"failed to CreateIBackupSumm[%d]\n", dwRet);
		return dwRet;
	}

	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	jclass class_ProtectionInformation = env->FindClass("com/ca/arcflash/service/jni/model/JProtectionInfo");
	jmethodID jProtectionInfo_constructor = env->GetMethodID(class_ProtectionInformation, "<init>", "()V");
	
	jmethodID jmethod_setOsInfo = env->GetMethodID(class_ProtectionInformation, "setLatestOsInfo", "(Lcom/ca/arcflash/service/jni/model/JAgentOSInfo;)V");

	jclass class_JAgentOSInfo = env->FindClass("com/ca/arcflash/service/jni/model/JAgentOSInfo");
	jmethodID JAgentOSInfo_init = env->GetMethodID(class_JAgentOSInfo, "<init>", "()V");

	SUMM_FILTER filter;
	filter.dwType = BACKUP_TYPE_ALL;
	filter.dwStatus = BACKUP_STATUS_SUCCESS;

	VDATA_PROTECTION vProtect;
	VDATA_PROTECTION::iterator itrPrt;
	pSumm->GetDataProtectionSumm(filter, vProtect);

	NODE_OS_INFO osInfo;
	pSumm->GetNodeOSInfo(osInfo);
	logObj.LogW(LL_INF, 0, L"GetNodeOSInfo: dwAgentOSType=%d, dwAgentBackupType=%d, dwVMGuestOsType=%d, dwVMHypervisor=%d.",
		osInfo.dwAgentOSType, osInfo.dwAgentBackupType, osInfo.dwVMGuestOsType, osInfo.dwVMHypervisor);

	for (itrPrt = vProtect.begin(); itrPrt != vProtect.end(); itrPrt++)
	{
		jobject jProectionInfo = env->NewObject(class_ProtectionInformation, jProtectionInfo_constructor);
		jobject jNodeOsInfo = env->NewObject(class_JAgentOSInfo, JAgentOSInfo_init);

		NodeOSInfo2JAgentOSInfo(env, jNodeOsInfo, osInfo);
		env->CallVoidMethod(jProectionInfo, jmethod_setOsInfo, jNodeOsInfo);
		if (jNodeOsInfo != NULL) env->DeleteLocalRef(jNodeOsInfo);

		SetStringValue2Field(env, jProectionInfo, "type", itrPrt->strType);
		SetStringValue2Field(env, jProectionInfo, "count", itrPrt->strCount);
		SetStringValue2Field(env, jProectionInfo, "totalLogicalSize", itrPrt->strTotalLogicalSize);
		SetStringValue2Field(env, jProectionInfo, "totalSize", itrPrt->strTotalSize);
		SetStringValue2Field(env, jProectionInfo, "lastBackupTime", itrPrt->strLastBackupTime);

		env->CallBooleanMethod(list, id_ArrayList_add, jProectionInfo);
		if (jProectionInfo != NULL) env->DeleteLocalRef(jProectionInfo);
	}

	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_ProtectionInformation != NULL) env->DeleteLocalRef(class_ProtectionInformation);

	pSumm->Release();
	
	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetCopyProtectionInformation
(JNIEnv *env, jclass clz, jstring destination, jstring domain,jstring user,jstring pwd, jobject list)
{
	IBackupSumm *pSumm = NULL;
	wchar_t* pDest = JStringToWCHAR(env, destination);
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

	DWORD dwRet = CreateIBackupSumm(info, &pSumm);  //first, create the interface
	if(dwRet)
	{
		wprintf(L"failed to CreateIBackupSumm[%d]\n", dwRet);
		return dwRet;
	}

	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	jclass class_ProtectionInformation = env->FindClass("com/ca/arcflash/service/jni/model/JProtectionInfo");
	jmethodID jProtectionInfo_constructor = env->GetMethodID(class_ProtectionInformation, "<init>", "()V");
	
	SUMM_FILTER filter;
	filter.dwType = BACKUP_TYPE_ALL;
	filter.dwStatus = BACKUP_STATUS_SUCCESS;

	VDATA_PROTECTION vProtect;
	VDATA_PROTECTION::iterator itrPrt;
	pSumm->GetDataProtectionSumm(filter,vProtect);
	for(itrPrt = vProtect.begin(); itrPrt != vProtect.end(); itrPrt++)
	{
		jobject jProectionInfo = env->NewObject(class_ProtectionInformation, jProtectionInfo_constructor);

		SetStringValue2Field(env, jProectionInfo, "type", itrPrt->strType);
		SetStringValue2Field(env, jProectionInfo, "count", itrPrt->strCount);
		SetStringValue2Field(env, jProectionInfo, "totalSize", itrPrt->strTotalSize);
		SetStringValue2Field(env, jProectionInfo, "totalLogicalSize", itrPrt->strTotalLogicalSize);
		wstring lastSuccessfullTime;
		pSumm->GetLastSuccessfullCpyTime(lastSuccessfullTime);
		SetStringValue2Field(env, jProectionInfo, "lastBackupTime", lastSuccessfullTime);

		env->CallBooleanMethod(list, id_ArrayList_add, jProectionInfo);
		if (jProectionInfo != NULL) env->DeleteLocalRef(jProectionInfo);
	}

	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_ProtectionInformation != NULL) env->DeleteLocalRef(class_ProtectionInformation);

	pSumm->Release();
	
	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetRecentBakcupInfo
(JNIEnv *env, jclass clz, jstring destination, jstring domain,jstring user,jstring pwd, jint type, jint status, jint count, jobject list,jstring foldername)
{
	IBackupSumm *pSumm = NULL;
	wchar_t* pDest = JStringToWCHAR(env, destination);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);
	wchar_t* pFolder = JStringToWCHAR(env, foldername);

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

	DWORD dwRet = CreateIBackupSumm(info, &pSumm);  //first, create the interface
	if(dwRet)
	{
		wprintf(L"failed to CreateIBackupSumm[%d]\n", dwRet);
		return dwRet;
	}

	jclass class_JBackupInfo = env->FindClass("com/ca/arcflash/webservice/jni/model/JBackupInfo");	
	jmethodID jBackupInfo_constructor = env->GetMethodID(class_JBackupInfo, "<init>", "()V");	

	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	SUMM_FILTER filter;
	VBACKUP_INFO vBakInfo;
	VBACKUP_INFO::iterator itrBakInfo;

	/*Convert java backup status into cpp backup status*/
	jclass class_BackupType = env->FindClass("com/ca/arcflash/webservice/data/backup/BackupType");
	jfieldID fieldFull	= env->GetStaticFieldID(class_BackupType, "Full", "I");
	jfieldID fieldIncre	= env->GetStaticFieldID(class_BackupType, "Incremental", "I");
	jfieldID fieldResync = env->GetStaticFieldID(class_BackupType, "Resync", "I");

	jint fullType = env->GetStaticIntField(class_BackupType,fieldFull);
	jint increType = env->GetStaticIntField(class_BackupType,fieldIncre);
	jint resyncType = env->GetStaticIntField(class_BackupType,fieldResync);

	if(type == fullType){
		filter.dwType = BACKUP_TYPE_FULL;
	}else if(type == increType){
		filter.dwType = BACKUP_TYPE_INC;
	}else if(type == resyncType){
		filter.dwType = BACKUP_TYPE_RESYNC;	
	}else{
		filter.dwType = BACKUP_TYPE_ALL;
	}
	/*Convert end*/

	/*Convert java backup status into cpp backup status*/
	jclass class_BackupStatus = env->FindClass("com/ca/arcflash/webservice/data/backup/BackupStatus");
	jfieldID fieldFinished	= env->GetStaticFieldID(class_BackupStatus, "Finished", "I");
	jfieldID fieldFailed	= env->GetStaticFieldID(class_BackupStatus, "Failed", "I");
	jfieldID fieldActive = env->GetStaticFieldID(class_BackupStatus, "Active", "I");
	jfieldID fieldCanceled = env->GetStaticFieldID(class_BackupStatus, "Canceled", "I");
	jfieldID fieldCrashed = env->GetStaticFieldID(class_BackupStatus, "Crashed", "I");

	jint finishedStatus = env->GetStaticIntField(class_BackupStatus,fieldFinished);
	jint failedStatus = env->GetStaticIntField(class_BackupStatus,fieldFailed);
	jint activeStatus = env->GetStaticIntField(class_BackupStatus,fieldActive);
	jint canceldedStatus = env->GetStaticIntField(class_BackupStatus,fieldCanceled);
	jint crashedStatus = env->GetStaticIntField(class_BackupStatus,fieldCrashed);

	if(status == finishedStatus){
		filter.dwStatus = BACKUP_STATUS_SUCCESS;
	}else if(status == failedStatus){
		filter.dwStatus = BACKUP_STATUS_FAILED;
	}else if(status == activeStatus){
		filter.dwStatus = BACKUP_STATUS_ACTIVE;	
	}else if(status == canceldedStatus){
		filter.dwStatus = BACKUP_STATUS_CANCELED;		
	}else if(status == crashedStatus){
		filter.dwStatus = BACKUP_STATUS_CRASHED;		
	}else{
		filter.dwStatus = BACKUP_STATUS_ALL;	
	}
	/*Convert status end*/

	filter.strEndDate = L"";
	filter.strStartDate = L"";
	pSumm->GetRecentBackupInfo(filter,vBakInfo,count,pFolder);
	for(itrBakInfo = vBakInfo.begin(); itrBakInfo != vBakInfo.end(); itrBakInfo++)
	{
		jobject jBackupInfo = env->NewObject(class_JBackupInfo, jBackupInfo_constructor);

		SetStringValue2Field(env, jBackupInfo, "status", itrBakInfo->strStatus);
		SetStringValue2Field(env, jBackupInfo, "type", itrBakInfo->strType);
		SetStringValue2Field(env, jBackupInfo, "date", itrBakInfo->strDate);
		SetStringValue2Field(env, jBackupInfo, "time", itrBakInfo->strDetailTime);
		SetStringValue2Field(env, jBackupInfo, "logicalSize", itrBakInfo->strLogicalDataSize);
		SetStringValue2Field(env, jBackupInfo, "size", itrBakInfo->strSize);
		SetStringValue2Field(env, jBackupInfo, "name", itrBakInfo->strBackupName);
		SetStringValue2Field(env, jBackupInfo, "backupSessionID", itrBakInfo->strSessionID);
		SetStringValue2Field(env, jBackupInfo, "backupDest", itrBakInfo->strBakDest);

		env->CallBooleanMethod(list, id_ArrayList_add, jBackupInfo);
		if (jBackupInfo != NULL) env->DeleteLocalRef(jBackupInfo);
	}

	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_JBackupInfo != NULL) env->DeleteLocalRef(class_JBackupInfo);

	pSumm->Release();

	return 0;
}


JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetRecentBackupsByServerTime
(JNIEnv *env, jclass clz, jstring destination, jstring domain,jstring user,jstring pwd, jint type, jint status, jstring startdate, jstring enddate, jobject list,jstring foldername)
{
	IBackupSumm *pSumm = NULL;
	wchar_t* pDest = JStringToWCHAR(env, destination);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);
	wchar_t* pFolder = JStringToWCHAR(env, foldername);

	wchar_t* pBeginDate = JStringToWCHAR(env, startdate);
	wchar_t* pEndDate = JStringToWCHAR(env, enddate);

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

	DWORD dwRet = CreateIBackupSumm(info, &pSumm);  //first, create the interface
	if(dwRet)
	{
		if (pBeginDate != NULL)
		{	
			free(pBeginDate);
		}

		if (pEndDate != NULL)
		{
			free(pEndDate);
		}

		wprintf(L"failed to CreateIBackupSumm[%d]\n", dwRet);
		return dwRet;
	}

	jclass class_JBackupInfo = env->FindClass("com/ca/arcflash/webservice/jni/model/JBackupInfo");	
	jmethodID jBackupInfo_constructor = env->GetMethodID(class_JBackupInfo, "<init>", "()V");	

	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	SUMM_FILTER filter;
	VBACKUP_INFO vBakInfo;
	VBACKUP_INFO::iterator itrBakInfo;

	/*Convert java backup status into cpp backup status*/
	jclass class_BackupType = env->FindClass("com/ca/arcflash/webservice/data/backup/BackupType");
	jfieldID fieldFull	= env->GetStaticFieldID(class_BackupType, "Full", "I");
	jfieldID fieldIncre	= env->GetStaticFieldID(class_BackupType, "Incremental", "I");
	jfieldID fieldResync = env->GetStaticFieldID(class_BackupType, "Resync", "I");

	jint fullType = env->GetStaticIntField(class_BackupType,fieldFull);
	jint increType = env->GetStaticIntField(class_BackupType,fieldIncre);
	jint resyncType = env->GetStaticIntField(class_BackupType,fieldResync);

	if(type == fullType){
		filter.dwType = BACKUP_TYPE_FULL;
	}else if(type == increType){
		filter.dwType = BACKUP_TYPE_INC;
	}else if(type == resyncType){
		filter.dwType = BACKUP_TYPE_RESYNC;	
	}else{
		filter.dwType = BACKUP_TYPE_ALL;
	}
	/*Convert end*/

	/*Convert java backup status into cpp backup status*/
	jclass class_BackupStatus = env->FindClass("com/ca/arcflash/webservice/data/backup/BackupStatus");
	jfieldID fieldFinished	= env->GetStaticFieldID(class_BackupStatus, "Finished", "I");
	jfieldID fieldFailed	= env->GetStaticFieldID(class_BackupStatus, "Failed", "I");
	jfieldID fieldActive = env->GetStaticFieldID(class_BackupStatus, "Active", "I");
	jfieldID fieldCanceled = env->GetStaticFieldID(class_BackupStatus, "Canceled", "I");
	jfieldID fieldCrashed = env->GetStaticFieldID(class_BackupStatus, "Crashed", "I");

	jint finishedStatus = env->GetStaticIntField(class_BackupStatus,fieldFinished);
	jint failedStatus = env->GetStaticIntField(class_BackupStatus,fieldFailed);
	jint activeStatus = env->GetStaticIntField(class_BackupStatus,fieldActive);
	jint canceldedStatus = env->GetStaticIntField(class_BackupStatus,fieldCanceled);
	jint crashedStatus = env->GetStaticIntField(class_BackupStatus,fieldCrashed);

	if(status == finishedStatus){
		filter.dwStatus = BACKUP_STATUS_SUCCESS;
	}else if(status == failedStatus){
		filter.dwStatus = BACKUP_STATUS_FAILED;
	}else if(status == activeStatus){
		filter.dwStatus = BACKUP_STATUS_ACTIVE;	
	}else if(status == canceldedStatus){
		filter.dwStatus = BACKUP_STATUS_CANCELED;		
	}else if(status == crashedStatus){
		filter.dwStatus = BACKUP_STATUS_CRASHED;		
	}else{
		filter.dwStatus = BACKUP_STATUS_ALL;	
	}

	filter.strEndDate = pEndDate;
	filter.strStartDate = pBeginDate;

	/*Convert status end*/

	//filter.strEndDate = L"";
	//filter.strStartDate = L"";

	pSumm->GetRecentBackupInfo(filter,vBakInfo,-1,pFolder);

	jfieldID jfieldBackupSetFlag = env->GetFieldID(class_JBackupInfo, "backupSetFlag", "I");
	jfieldID jfieldPeriodRetentionFlag = env->GetFieldID(class_JBackupInfo, "periodRetentionFlag", "I");
	for(itrBakInfo = vBakInfo.begin(); itrBakInfo != vBakInfo.end(); itrBakInfo++)
	{
		jobject jBackupInfo = env->NewObject(class_JBackupInfo, jBackupInfo_constructor);

		SetStringValue2Field(env, jBackupInfo, "status", itrBakInfo->strStatus);
		SetStringValue2Field(env, jBackupInfo, "type", itrBakInfo->strType);
		SetStringValue2Field(env, jBackupInfo, "date", itrBakInfo->strDate);
		SetStringValue2Field(env, jBackupInfo, "time", itrBakInfo->strDetailTime);
		SetStringValue2Field(env, jBackupInfo, "size", itrBakInfo->strSize);
		SetStringValue2Field(env, jBackupInfo, "logicalSize", itrBakInfo->strLogicalDataSize);
		SetStringValue2Field(env, jBackupInfo, "name", itrBakInfo->strBackupName);
		SetStringValue2Field(env, jBackupInfo, "backupSessionID", itrBakInfo->strSessionID);
		SetStringValue2Field(env, jBackupInfo, "backupDest", itrBakInfo->strBakDest);
		SetLongValue2Field(env, jBackupInfo, "catalogFlag", itrBakInfo->dwCatalogFlag);
		env->SetIntField(jBackupInfo, jfieldBackupSetFlag, itrBakInfo->dwBKSetFlag);
		env->SetIntField(jBackupInfo, jfieldPeriodRetentionFlag, itrBakInfo->dwBKAdvSchFlag);

		env->CallBooleanMethod(list, id_ArrayList_add, jBackupInfo);
		if (jBackupInfo != NULL) env->DeleteLocalRef(jBackupInfo);
	}

	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_JBackupInfo != NULL) env->DeleteLocalRef(class_JBackupInfo);

	pSumm->Release();

	if (pBeginDate != NULL)
	{	
		free(pBeginDate);
	}

	if (pEndDate != NULL)
	{
		free(pEndDate);
	}

	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetBakcupInfo
(JNIEnv *env, jclass clz, jstring destination, jstring domain,jstring user,jstring pwd, jstring startdate, jstring enddate,jint count, jobject list)
{
	IBackupSumm *pSumm = NULL;
	wchar_t* pDest = JStringToWCHAR(env, destination);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);

	wchar_t* pBeginDate = JStringToWCHAR(env, startdate);
	wchar_t* pEndDate = JStringToWCHAR(env, enddate);

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
	
	DWORD dwRet = CreateIBackupSumm(info, &pSumm);  //first, create the interface
	if(dwRet)
	{
		wprintf(L"failed to CreateIBackupSumm[%d]\n", dwRet);
		if (pBeginDate != NULL)
		{	
			free(pBeginDate);
		}

		if (pEndDate != NULL)
		{
			free(pEndDate);
		}
		return dwRet;
	}

	jclass class_JBackupInfo = env->FindClass("com/ca/arcflash/webservice/jni/model/JBackupInfo");	
	jmethodID jBackupInfo_constructor = env->GetMethodID(class_JBackupInfo, "<init>", "()V");	

	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	SUMM_FILTER filter;
	VBACKUP_INFO vBakInfo;
	VBACKUP_INFO::iterator itrBakInfo;

	filter.dwStatus = BACKUP_STATUS_ALL;
	filter.dwType = BACKUP_TYPE_ALL;
	filter.strEndDate = pEndDate;
	filter.strStartDate = pBeginDate;


	pSumm->GetRecentBackupInfo(filter,vBakInfo,count);
	for(itrBakInfo = vBakInfo.begin(); itrBakInfo != vBakInfo.end(); itrBakInfo++)
	{
		jobject jBackupInfo = env->NewObject(class_JBackupInfo, jBackupInfo_constructor);

		SetStringValue2Field(env, jBackupInfo, "status", itrBakInfo->strStatus);
		SetStringValue2Field(env, jBackupInfo, "type", itrBakInfo->strType);
		SetStringValue2Field(env, jBackupInfo, "date", itrBakInfo->strDate);
		SetStringValue2Field(env, jBackupInfo, "time", itrBakInfo->strDetailTime);
		SetStringValue2Field(env, jBackupInfo, "size", itrBakInfo->strSize);
		SetStringValue2Field(env, jBackupInfo, "logicalSize", itrBakInfo->strLogicalDataSize);
		SetStringValue2Field(env, jBackupInfo, "name", itrBakInfo->strBackupName);

		env->CallBooleanMethod(list, id_ArrayList_add, jBackupInfo);
		if (jBackupInfo != NULL) env->DeleteLocalRef(jBackupInfo);
	}
	
	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
	if (class_JBackupInfo != NULL) env->DeleteLocalRef(class_JBackupInfo);
	
	pSumm->Release();
	if (pBeginDate != NULL)
	{	
		free(pBeginDate);
	}

	if (pEndDate != NULL)
	{
		free(pEndDate);
	}
	return 0;
}
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_LogActivityWithID
(JNIEnv *env, jclass clz, jlong level, jlong jobID, jlong resourceID, jobjectArray parameters)
{
	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_get = env->GetMethodID(class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jstring jstr1 = (jstring)env->GetObjectArrayElement(parameters,0);
	wchar_t* param1 = JStringToWCHAR(env, jstr1);

	jstring jstr2 = (jstring)env->GetObjectArrayElement(parameters,1);
	wchar_t* param2 = JStringToWCHAR(env, jstr2);

	jstring jstr3 = (jstring)env->GetObjectArrayElement(parameters,2);
	wchar_t* param3 = JStringToWCHAR(env, jstr3);

	jstring jstr4 = (jstring)env->GetObjectArrayElement(parameters,3);
	wchar_t* param4 = JStringToWCHAR(env, jstr4);

	jstring jstr5 = (jstring)env->GetObjectArrayElement(parameters,4);
	wchar_t* param5 = JStringToWCHAR(env, jstr5);

	DWORD dwMessageID = jni_get_mapped_messsage_id( resourceID );
	DWORD ret = LogActivityWithDetails( level, APT_D2D, AJT_COMMON, jobID, NULL, dwMessageID, param1, param2, param3, param4, param5);

	if (param1!=NULL)
		free(param1);
	if (param2!=NULL)
		free(param2);
	if (param3!=NULL)
		free(param3);
	if (param4!=NULL)
		free(param4);
	if (param5!=NULL)
		free(param5);
	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);

	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_LogActivity
(JNIEnv *env, jclass clz, jlong level, jlong resourceID, jobjectArray parameters)
{
	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_get = env->GetMethodID(class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jstring jstr1 = (jstring)env->GetObjectArrayElement(parameters,0);
	wchar_t* param1 = JStringToWCHAR(env, jstr1);

	jstring jstr2 = (jstring)env->GetObjectArrayElement(parameters,1);
	wchar_t* param2 = JStringToWCHAR(env, jstr2);

	jstring jstr3 = (jstring)env->GetObjectArrayElement(parameters,2);
	wchar_t* param3 = JStringToWCHAR(env, jstr3);

	jstring jstr4 = (jstring)env->GetObjectArrayElement(parameters,3);
	wchar_t* param4 = JStringToWCHAR(env, jstr4);

	jstring jstr5 = (jstring)env->GetObjectArrayElement(parameters,4);
	wchar_t* param5 = JStringToWCHAR(env, jstr5);

	wchar_t* param6 = NULL;
	jsize cnt = env->GetArrayLength(parameters);
	if( cnt>5 )
	{
		jstring jstr = (jstring)env->GetObjectArrayElement(parameters,5);
		param6 = JStringToWCHAR(env, jstr);
	}

	DWORD dwMessageID = jni_get_mapped_messsage_id( resourceID );
	DWORD ret = LogActivityWithDetails( level, APT_D2D, AJT_COMMON, 0, NULL, dwMessageID, param1, param2, param3, param4, param5, param6);

	if (param1!=NULL)
		free(param1);
	if (param2!=NULL)
		free(param2);
	if (param3!=NULL)
		free(param3);
	if (param4!=NULL)
		free(param4);
	if (param5!=NULL)
		free(param5);
	if (param6!=NULL)
		free(param6);

	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);

	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_VMLogActivityWithID
(JNIEnv *env, jclass clz, jlong level, jlong jobID, jlong resourceID, jobjectArray parameters, jstring vmIndentification)
{
	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_get = env->GetMethodID(class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jstring jstr1 = (jstring)env->GetObjectArrayElement(parameters,0);
	wchar_t* param1 = JStringToWCHAR(env, jstr1);

	jstring jstr2 = (jstring)env->GetObjectArrayElement(parameters,1);
	wchar_t* param2 = JStringToWCHAR(env, jstr2);

	jstring jstr3 = (jstring)env->GetObjectArrayElement(parameters,2);
	wchar_t* param3 = JStringToWCHAR(env, jstr3);

	jstring jstr4 = (jstring)env->GetObjectArrayElement(parameters,3);
	wchar_t* param4 = JStringToWCHAR(env, jstr4);

	jstring jstr5 = (jstring)env->GetObjectArrayElement(parameters,4);
	wchar_t* param5 = JStringToWCHAR(env, jstr5);

	wchar_t* vmInstanceUUID = JStringToWCHAR(env, vmIndentification); 

	DWORD dwMessageID = jni_get_mapped_messsage_id( resourceID );
	DWORD ret = LogActivityWithDetails( level, APT_D2D, AJT_COMMON, jobID, vmInstanceUUID, dwMessageID, param1, param2, param3, param4, param5);

	if (param1!=NULL)
		free(param1);
	if (param2!=NULL)
		free(param2);
	if (param3!=NULL)
		free(param3);
	if (param4!=NULL)
		free(param4);
	if (param5!=NULL)
		free(param5);
	if (vmInstanceUUID!=NULL)
		free(vmInstanceUUID);
	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);

	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_VMLogActivity
(JNIEnv *env, jclass clz, jlong level, jlong resourceID, jobjectArray parameters, jstring vmIndentification)
{
	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_get = env->GetMethodID(class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jstring jstr1 = (jstring)env->GetObjectArrayElement(parameters,0);
	wchar_t* param1 = JStringToWCHAR(env, jstr1);

	jstring jstr2 = (jstring)env->GetObjectArrayElement(parameters,1);
	wchar_t* param2 = JStringToWCHAR(env, jstr2);

	jstring jstr3 = (jstring)env->GetObjectArrayElement(parameters,2);
	wchar_t* param3 = JStringToWCHAR(env, jstr3);

	jstring jstr4 = (jstring)env->GetObjectArrayElement(parameters,3);
	wchar_t* param4 = JStringToWCHAR(env, jstr4);

	jstring jstr5 = (jstring)env->GetObjectArrayElement(parameters,4);
	wchar_t* param5 = JStringToWCHAR(env, jstr5);

	wchar_t* vmInstanceUUID = JStringToWCHAR(env, vmIndentification); 

	DWORD dwMessageID = jni_get_mapped_messsage_id( resourceID );
	DWORD ret = LogActivityWithDetails( level, APT_D2D, AJT_COMMON, 0, vmInstanceUUID, dwMessageID, param1, param2, param3, param4, param5);

	if (param1!=NULL)
		free(param1);
	if (param2!=NULL)
		free(param2);
	if (param3!=NULL)
		free(param3);
	if (param4!=NULL)
		free(param4);
	if (param5!=NULL)
		free(param5);
	if (vmInstanceUUID!=NULL)
		free(vmInstanceUUID);
	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);

	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_LogActivityWithDetailsEx
(JNIEnv *env, jclass clz, jobject logDetails, jlong resourceID, jobjectArray parameters)
{
	jclass class_ArrayList = env->FindClass("java/util/List");
	jmethodID id_ArrayList_get = env->GetMethodID(class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jstring jstr1 = (jstring)env->GetObjectArrayElement(parameters,0);
	wchar_t* param1 = JStringToWCHAR(env, jstr1);

	jstring jstr2 = (jstring)env->GetObjectArrayElement(parameters,1);
	wchar_t* param2 = JStringToWCHAR(env, jstr2);

	jstring jstr3 = (jstring)env->GetObjectArrayElement(parameters,2);
	wchar_t* param3 = JStringToWCHAR(env, jstr3);

	jstring jstr4 = (jstring)env->GetObjectArrayElement(parameters,3);
	wchar_t* param4 = JStringToWCHAR(env, jstr4);

	jstring jstr5 = (jstring)env->GetObjectArrayElement(parameters,4);
	wchar_t* param5 = JStringToWCHAR(env, jstr5);

	ACTLOG_DETAILS log_details;

	JActLogDetails2ACTLOG_DETAILS(env, log_details, logDetails);
	DWORD dwMessageID = jni_get_mapped_messsage_id( resourceID );
	DWORD ret = LogActivityWithDetails2(&log_details, dwMessageID, param1, param2, param3, param4, param5);

	if (param1!=NULL)
		free(param1);
	if (param2!=NULL)
		free(param2);
	if (param3!=NULL)
		free(param3);
	if (param4!=NULL)
		free(param4);
	if (param5!=NULL)
		free(param5);
	if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);

	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetLogActivity
(JNIEnv *env, jclass clz, jint startFrom, jint requestCount, jobject activityLogResult)
{
	ULONGLONG nCnt, ntotalCount = 0;
	PFLASHDB_ACTIVITY_LOG p = NULL;
	DWORD result = GetLog( APT_D2D, 0, startFrom, requestCount, &p, &nCnt, &ntotalCount, NULL);
	if( result == 0) 
	{
		jclass class_ArrayList = env->FindClass("java/util/List");
		jclass class_ActivityLogResult = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLogResult");	
		jclass class_ActivityLog = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLog");	

		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		jmethodID jActivityLog_constructor = env->GetMethodID(class_ActivityLog, "<init>", "()V");

		jfieldID field_totalCount = env->GetFieldID(class_ActivityLogResult, "totalCount", "J");
		jfieldID field_logs = env->GetFieldID(class_ActivityLogResult, "logs", "Ljava/util/List;");
		jfieldID field_message = env->GetFieldID(class_ActivityLog, "message", "Ljava/lang/String;");
		jfieldID field_time = env->GetFieldID(class_ActivityLog, "time", "Ljava/lang/String;");
		jfieldID field_level = env->GetFieldID(class_ActivityLog, "level", "J");
		jfieldID field_jobID = env->GetFieldID(class_ActivityLog, "jobID", "J");
		jobject jActivityLog = NULL;

		env->SetLongField(activityLogResult, field_totalCount, ntotalCount);
		jobject list = env->GetObjectField(activityLogResult,field_logs);

		WCHAR szTime[MAX_PATH] = {0};
		for(ULONGLONG i = 0; i < nCnt; i++) {
			jActivityLog = env->NewObject(class_ActivityLog, jActivityLog_constructor);

			jstring jstr = WCHARToJString(env, p[i].wszLogMessage);
			env->SetObjectField(jActivityLog, field_message, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			ZeroMemory( szTime, sizeof(szTime) );
			ulonglong_2_timestr( p[i].ullTimeUTC, szTime, _ARRAYSIZE(szTime) );
			jstr = WCHARToJString(env, szTime );
			env->SetObjectField(jActivityLog, field_time, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			env->SetLongField(jActivityLog, field_level, p[i].dwLogLevel);
			env->SetLongField(jActivityLog, field_jobID, p[i].ullJobID );

			env->CallBooleanMethod(list, id_ArrayList_add, jActivityLog);
			if (jActivityLog != NULL) env->DeleteLocalRef(jActivityLog);
		}

		if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
		if (class_ActivityLog != NULL) env->DeleteLocalRef(class_ActivityLog);		
		if (class_ActivityLogResult != NULL) env->DeleteLocalRef(class_ActivityLogResult);		
	}
	FreeLog(p);

	return result;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetJobLogActivity
(JNIEnv *env, jclass clz, jlong jobNo, jint startFrom, jint requestCount, jobject activityLogResult)
{
	ULONGLONG nCnt, ntotalCount = 0;
	PFLASHDB_ACTIVITY_LOG p = NULL;
	DWORD result = GetLog(APT_D2D, jobNo, startFrom, requestCount, &p, &nCnt, &ntotalCount, NULL);
	if( result == 0) 
	{
		jclass class_ArrayList = env->FindClass("java/util/List");
		jclass class_ActivityLogResult = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLogResult");	
		jclass class_ActivityLog = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLog");	

		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		jmethodID jActivityLog_constructor = env->GetMethodID(class_ActivityLog, "<init>", "()V");

		jfieldID field_totalCount = env->GetFieldID(class_ActivityLogResult, "totalCount", "J");
		jfieldID field_logs = env->GetFieldID(class_ActivityLogResult, "logs", "Ljava/util/List;");
		jfieldID field_message = env->GetFieldID(class_ActivityLog, "message", "Ljava/lang/String;");
		jfieldID field_time = env->GetFieldID(class_ActivityLog, "time", "Ljava/lang/String;");
		jfieldID field_level = env->GetFieldID(class_ActivityLog, "level", "J");
		jfieldID field_jobID = env->GetFieldID(class_ActivityLog, "jobID", "J");
		jobject jActivityLog = NULL;

		env->SetLongField(activityLogResult, field_totalCount, ntotalCount);
		jobject list = env->GetObjectField(activityLogResult,field_logs);

		WCHAR szTime[MAX_PATH] = {0};
		for(ULONGLONG i = 0; i < nCnt; i++) {
			jActivityLog = env->NewObject(class_ActivityLog, jActivityLog_constructor);

			jstring jstr = WCHARToJString(env, p[i].wszLogMessage);
			env->SetObjectField(jActivityLog, field_message, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			ZeroMemory( szTime, sizeof(szTime) );
			ulonglong_2_timestr( p[i].ullTimeUTC, szTime, _ARRAYSIZE(szTime) );
			jstr = WCHARToJString(env, szTime);
			env->SetObjectField(jActivityLog, field_time, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			env->SetLongField(jActivityLog, field_level, p[i].dwLogLevel);
			env->SetLongField(jActivityLog, field_jobID, p[i].ullJobID);

			env->CallBooleanMethod(list, id_ArrayList_add, jActivityLog);
			if (jActivityLog != NULL) env->DeleteLocalRef(jActivityLog);
		}

		if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
		if (class_ActivityLog != NULL) env->DeleteLocalRef(class_ActivityLog);		
		if (class_ActivityLogResult != NULL) env->DeleteLocalRef(class_ActivityLogResult);
	}

	FreeLog(p);
	
	return result;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DeleteLogActivity
(JNIEnv *env, jclass clz, jint year, jint month, jint day, jint hour, jint minute, jint second)
{
	SYSTEMTIME systime;
	memset(&systime, 0, sizeof(SYSTEMTIME));
	systime.wYear = year;
	systime.wMonth = month;
	systime.wDay = day;
	systime.wHour = hour;
	systime.wMinute = minute;
	systime.wSecond = second;
	return PruneLog( APT_D2D, systime, NULL);
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_updateSessionPasswordByGUID
(JNIEnv *env, jclass clz, jobjectArray jGUIDArray, jobjectArray jPwdArray)
{
	jclass jStringCls = env->FindClass("java/lang/String");
	int arrSize = env->GetArrayLength(jGUIDArray);
	int i = 0;
	int iRet = 0;

	for(i = 0; i < arrSize; i ++)
	{
		jstring jguid = (jstring)env->GetObjectArrayElement(jGUIDArray, i);
		jstring jpwd = (jstring)env->GetObjectArrayElement(jPwdArray, i);
		wstring guid = JStringToWString(env, jguid);
		wchar_t *pwd = JStringToWCHAR(env, jpwd);
		iRet = AFIUpdateSessionPasswordByGUID(guid, pwd, env->GetStringLength(jpwd));
		if(iRet != 0)
			return iRet;
	}

	return iRet;
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetSessionPasswordBySessionGuid
(JNIEnv *env, jclass clz, jobjectArray jGuidArray)
{
	jclass jStringCls = env->FindClass("java/lang/String");
	int arrSize = env->GetArrayLength(jGuidArray);
	jobjectArray jPwdArray = env->NewObjectArray(arrSize, jStringCls, NULL);
	vector<CSessPwdWrap> vecPwd;
	vector<CSessPwdWrap>::iterator itr;
	int i = 0;

	for (i = 0; i < arrSize; i++)
	{
		CSessPwdWrap pwdWrap;
		jstring jGuid = (jstring)env->GetObjectArrayElement(jGuidArray, i);
		wstring guid = JStringToWString(env, jGuid);
		pwdWrap.wsSessGUID = guid;
		vecPwd.push_back(pwdWrap);
	}
	AFIGetMultiSessPwdFromKeyMgmtDB(vecPwd);

	for (itr = vecPwd.begin(), i=0; i < arrSize && itr != vecPwd.end(); i++, itr++)
	{
		jstring jPwd = NULL;
		if( itr->bFound )
			jPwd = WCHARToJString(env, itr->wsSessPwd.c_str());
		else
			jPwd = NULL;
		env->SetObjectArrayElement(jPwdArray, i, (jobject)jPwd);
	}

	return jPwdArray;
}
//==================backup/Restore end==========================================


JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFEncryptString
(JNIEnv *env, jclass clz, jstring jstr)
{

	wchar_t* pszStr = JStringToWCHAR(env, jstr);	
	jstring retValue = NULL;
	DWORD pBufLen = 0;
	wchar_t *pszBuf = NULL;
	BOOL ret = AFEncryptString(pszStr, pszBuf, &pBufLen);
	pszBuf = new wchar_t[pBufLen];
	memset(pszBuf, 0, pBufLen * sizeof(wchar_t));
	if(pszBuf != NULL)
	{  
		ret = AFEncryptString(pszStr, pszBuf, &pBufLen);	
		retValue = WCHARToJString(env, pszBuf);
		delete[] pszBuf;
		//return retValue;
	}
	if (pszStr!=NULL)
	{
		free(pszStr);
	}
	return retValue;
}


JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFDecryptString
(JNIEnv *env, jclass clz, jstring jstr)
{	
	wchar_t* pszStr = JStringToWCHAR(env, jstr);	
	jstring retValue = NULL;
	DWORD pBufLen = 0;
	wchar_t *pszBuf = NULL;
	BOOL ret = AFDecryptString(pszStr, pszBuf, &pBufLen);
	pszBuf = new wchar_t[pBufLen];
	memset(pszBuf, 0, pBufLen * sizeof(wchar_t));
	if(pszBuf != NULL)
	{  
		ret = AFDecryptString(pszStr, pszBuf, &pBufLen);
		retValue = WCHARToJString(env, pszBuf);
		delete[] pszBuf;
		//return retValue;
	}
	if (pszStr!=NULL)
	{
		free(pszStr);
	}
	return retValue;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFEncryptStringEx
	(JNIEnv *env, jclass clz, jstring jstr)
{

	wchar_t* pszStr = JStringToWCHAR(env, jstr);	
	jstring retValue = NULL;
	DWORD pBufLen = 0;
	wchar_t *pszBuf = NULL;
	BOOL ret = AFEncryptStringEx(pszStr, pszBuf, &pBufLen);
	pszBuf = new wchar_t[pBufLen];
	memset(pszBuf, 0, pBufLen * sizeof(wchar_t));
	if(pszBuf != NULL)
	{  
		ret = AFEncryptStringEx(pszStr, pszBuf, &pBufLen);	
		retValue = WCHARToJString(env, pszBuf);
		delete[] pszBuf;
		//return retValue;
	}
	if (pszStr!=NULL)
	{
		free(pszStr);
	}
	return retValue;
}


JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFDecryptStringEx
	(JNIEnv *env, jclass clz, jstring jstr)
{	
	wchar_t* pszStr = JStringToWCHAR(env, jstr);	
	jstring retValue = NULL;
	DWORD pBufLen = 0;
	wchar_t *pszBuf = NULL;
	BOOL ret = AFDecryptStringEx(pszStr, pszBuf, &pBufLen);
	if (ret)
	{
		pszBuf = new wchar_t[pBufLen];
		memset(pszBuf, 0, pBufLen * sizeof(wchar_t));
		if(pszBuf != NULL)
		{  
			ret = AFDecryptStringEx(pszStr, pszBuf, &pBufLen);
			retValue = WCHARToJString(env, pszBuf);
			delete[] pszBuf;
			//return retValue;
		}
		if (pszStr!=NULL)
		{
			free(pszStr);
		}
		return retValue;
	} 
	else
	{
		return jstr;
	}
}

JNIEXPORT jbyteArray JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFEncryptBinary
(JNIEnv *env, jclass clz, jbyteArray inputArray)
{
	jsize iArraySize = env->GetArrayLength(inputArray);
	if (iArraySize < 1)
	{
		logObj.LogW(LL_ERR, 1, __WFUNCTION__ L"input array is empty");
		return NULL;
	}

	DWORD dwInputLen = iArraySize;
	DWORD dwOutputLen = iArraySize;
	AFEncryptData(NULL, &dwOutputLen, 0);
	if (dwOutputLen < 1)
	{
		logObj.LogW(LL_ERR, 2, __WFUNCTION__ L"output length is less than 1");
		return NULL;
	}

	BYTE* pOldBuff = (BYTE*)env->GetByteArrayElements(inputArray, 0);

	BYTE* pBuff = new BYTE[dwOutputLen];
	ZeroMemory(pBuff, dwOutputLen);
	memcpy_s(pBuff, dwOutputLen, pOldBuff, dwInputLen);

	if (AFEncryptData(pBuff, &dwInputLen, dwOutputLen) < 0)
	{
		logObj.LogW(LL_ERR, 3, L"%s: Fail to AFEncryptData", __WFUNCTION__);
		delete[] pBuff;
		return NULL;
	}
	else
	{
		jbyteArray outPutArray = env->NewByteArray(dwOutputLen);

		env->SetByteArrayRegion(outPutArray, 0, dwOutputLen, (jbyte*)pBuff);
		delete[] pBuff;

		return outPutArray;
	}
}

JNIEXPORT jbyteArray JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFDecryptBinary
(JNIEnv *env, jclass clz, jbyteArray inputArray)
{
	jsize iArraySize = env->GetArrayLength(inputArray);
	if (iArraySize < 1)
	{
		logObj.LogW(LL_ERR, 1, __WFUNCTION__ L"input array is empty");
		return NULL;
	}

	BYTE* pBuff = new BYTE[iArraySize];
	ZeroMemory(pBuff, iArraySize);

	BYTE* pOldBuff = (BYTE*)env->GetByteArrayElements(inputArray, 0);
	memcpy_s(pBuff, iArraySize, pOldBuff, iArraySize);

	DWORD dwLen = iArraySize;
	if (0 == AFDecryptData(pBuff, &dwLen))
	{
		jbyteArray outPutArray = env->NewByteArray(dwLen);

		env->SetByteArrayRegion(outPutArray, 0, dwLen, (jbyte*)pBuff);
		delete[] pBuff;
		return outPutArray;
	}
	else
	{
		logObj.LogW(2, LL_ERR, L"%s: Fail to Decrypt password", __WFUNCTION__);
		delete[] pBuff;
		return NULL;
	}
}

//==================Agent Deploy Start==========================================

JNIEnv *AdtTempEnv = NULL;
jclass AdtTempClz = NULL;
jobject AdtTempHandler = NULL;

int static __stdcall UpdateDeployStatus(const wchar_t* pServerName, // Server Name
			const UINT status,	 // Status: 1-7:
			const wchar_t* pMessage, // Status message
			const UINT percentage,
			const DWORD msgCode
			)
{
	if(AdtTempEnv == NULL || AdtTempClz == NULL || AdtTempHandler == NULL)
	{
		return 0;
	}

	// find IRemoteDeployCallback class
	jclass HandlerClass = AdtTempEnv->GetObjectClass(AdtTempHandler);
	jmethodID methodId = AdtTempEnv->GetMethodID(HandlerClass, "update", "(Lcom/ca/arcflash/webservice/jni/model/JDeployStatus;)V");

	// find JDeployStatus class
	jclass StatusClass = AdtTempEnv->FindClass("com/ca/arcflash/webservice/jni/model/JDeployStatus");
	jmethodID constructor = AdtTempEnv->GetMethodID(StatusClass, "<init>", "()V");
	jobject StatusObject = AdtTempEnv->NewObject(StatusClass, constructor);

	// set status
	jmethodID statusSetter = AdtTempEnv->GetMethodID(StatusClass, "setStatus", "(I)V");
	AdtTempEnv->CallVoidMethod(StatusObject, statusSetter, (jint)status);

	// set message
	jmethodID messageSetter = AdtTempEnv->GetMethodID(StatusClass, "setMessage", "(Ljava/lang/String;)V");
	jstring message = WCHARToJString(AdtTempEnv, (wchar_t*)pMessage);
	AdtTempEnv->CallVoidMethod(StatusObject, messageSetter, message);

	// set percentage
	jmethodID percentageSetter = AdtTempEnv->GetMethodID(StatusClass, "setPercentage", "(I)V");
	AdtTempEnv->CallVoidMethod(StatusObject, percentageSetter, percentage);

	// set msgCode
	jmethodID msgCodeSetter = AdtTempEnv->GetMethodID(StatusClass, "setMsgCode", "(J)V");
	AdtTempEnv->CallVoidMethod(StatusObject, msgCodeSetter, (jlong)msgCode);
	
	
	// call IRemoteDeployCallback.update() method
	AdtTempEnv->CallVoidMethod(AdtTempHandler, methodId, StatusObject);
	return 0;
}


typedef int (__stdcall *DeployStatusFunction)(const wchar_t*, // Server Name
										 const UINT,	 // Status: 1-7:
										 const wchar_t*, // Status message
										 const UINT,	 // Percentage: 0-100
										 const DWORD // msgCode
										 );

typedef UINT (__cdecl *StartToDeployFunction)(wchar_t* szwAFDomain, // Local Domain
										wchar_t* szwAFUserName,		// Local User
										wchar_t* szwAFPassword,		// Local Password
										wchar_t* szwUUID,	  // UUID from JAVA side
										wchar_t* szwServerName, // Remote server name
										wchar_t* szwUserName,   // Log on User with administrator privilege
										wchar_t* szwPassword,   // Password
										UINT iPort,       // Port number
              							wchar_t* szwInstallDir,
										BOOL bAutoStartRRService,
										BOOL bIsReboot,
										BOOL bResumedAndCheck,
										DeployStatusFunction); 


typedef UINT (__cdecl *PFNGetLocalPackage)();


JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetLocalPackage
  (JNIEnv *, jclass)
{
	int ret = -1;
	HMODULE hModule = LoadLibrary(L"AgentDeployTool.dll");
	if(hModule != NULL)
	{
		PFNGetLocalPackage getLocalPkg = (PFNGetLocalPackage)GetProcAddress(hModule, "GetLocalPackage");
		if(getLocalPkg != NULL)
		{
			ret = getLocalPkg();
		}
	}

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_TestADTConnectWithDriver
(JNIEnv *env, jclass clz, jstring jLocalDomain, jstring jLocalUser, jstring jLocalPassword, jstring jServerName, jstring jUsername, jstring jPassword, jstring jInstallPath, jboolean autoStartRRService, jboolean installDriver, jobject deployUpgrade)
{ 
	BSTR localDomain = NULL,localUser = NULL,localPwd = NULL,serverName = NULL,userName = NULL,pwd = NULL, installPath=NULL;

	JStringToBSTR(env, jLocalDomain, &localDomain);
	JStringToBSTR(env, jLocalUser, &localUser);
	JStringToBSTR(env, jLocalPassword, &localPwd);
	JStringToBSTR(env, jServerName, &serverName);
	JStringToBSTR(env, jUsername, &userName);
	JStringToBSTR(env, jPassword, &pwd);
	JStringToBSTR(env, jInstallPath, &installPath);

	HMODULE hModule = LoadLibrary(L"AgentDeployTool.dll");
	DWORD dwErr = 0;
	D2DREGCONF2 temp;
	memset(&temp, 0, sizeof(D2DREGCONF));
	std:wstring pwsUUID;
	temp.pwsUUID = &pwsUUID;

	if(hModule != NULL)
	{
		pfnTestConnect2 _pfnTestConnect = reinterpret_cast<BOOL(__cdecl *)(BSTR,           
			BSTR,        
			BSTR,      
			BSTR,   
			BSTR,
			BSTR,
			BSTR,
			BOOL,BOOL,PD2DREGCONF2,
			LPDWORD)>(GetProcAddress(hModule, "TestConnect2"));
		if(_pfnTestConnect != NULL){			
			_pfnTestConnect(localDomain, localUser,localPwd, serverName,userName, pwd, installPath,(BOOL)autoStartRRService,(BOOL)installDriver, &temp, &dwErr);
			if(dwErr==80061) 
				D2DREGCONF2JDeployUpgradeWithDriver(env,temp,deployUpgrade);
		}
		FreeLibrary(hModule);
	}

	::SysFreeString(localDomain);
	::SysFreeString(localUser);
	::SysFreeString(localPwd);
	::SysFreeString(serverName);
	::SysFreeString(userName);
	::SysFreeString(pwd);
	::SysFreeString(installPath);

	return dwErr;
}									


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_TestADTConnect
(JNIEnv *env, jclass clz, jstring jLocalDomain, jstring jLocalUser, jstring jLocalPassword, jstring jServerName, jstring jUsername, jstring jPassword, jstring jInstallPath, jboolean autoStartRRService,jobject deployUpgrade)
{ 
	BSTR localDomain = NULL,localUser = NULL,localPwd = NULL,serverName = NULL,userName = NULL,pwd = NULL, installPath=NULL;

	JStringToBSTR(env, jLocalDomain, &localDomain);
	JStringToBSTR(env, jLocalUser, &localUser);
	JStringToBSTR(env, jLocalPassword, &localPwd);
	JStringToBSTR(env, jServerName, &serverName);
	JStringToBSTR(env, jUsername, &userName);
	JStringToBSTR(env, jPassword, &pwd);
	JStringToBSTR(env, jInstallPath, &installPath);

	HMODULE hModule = LoadLibrary(L"AgentDeployTool.dll");
	DWORD dwErr = 0;
	D2DREGCONF temp;
	memset(&temp, 0, sizeof(D2DREGCONF));
	std:wstring pwsUUID;
	temp.pwsUUID = &pwsUUID;

	if(hModule != NULL)
	{
		pfnTestConnect _pfnTestConnect = reinterpret_cast<BOOL(__cdecl *)(BSTR,           
			BSTR,        
			BSTR,      
			BSTR,   
			BSTR,
			BSTR,
			BSTR,
			BOOL,PD2DREGCONF,
			LPDWORD)>(GetProcAddress(hModule, "TestConnect"));
		if(_pfnTestConnect != NULL){			
			_pfnTestConnect(localDomain, localUser,localPwd, serverName,userName, pwd, installPath,(BOOL)autoStartRRService,&temp, &dwErr);
			if(dwErr==80061) 
				D2DREGCONF2JDeployUpgrade(env,temp,deployUpgrade);
		}
		FreeLibrary(hModule);
	}

	::SysFreeString(localDomain);
	::SysFreeString(localUser);
	::SysFreeString(localPwd);
	::SysFreeString(serverName);
	::SysFreeString(userName);
	::SysFreeString(pwd);
	::SysFreeString(installPath);

	return dwErr;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_StartToDeployWithDriver
  (JNIEnv *env, jclass clz, jstring jLocalDomain, jstring jLocalUser, jstring jLocalPassword, 
	jstring jGuid, jstring jServerName, jstring jUsername, jstring jPassword, jint jPort, jstring jInstallDirectory, jboolean jAutoStartRRService, jboolean jReboot, jboolean jInstallDriver, jboolean jUseHttps, jboolean jResumedAndCheck, jobject jCallback)
{

	wchar_t* pszLocalDomain = JStringToWCHAR(env, jLocalDomain);
	wchar_t* pszLocalUser = JStringToWCHAR(env, jLocalUser);
	wchar_t* pszLocalPassword = JStringToWCHAR(env, jLocalPassword);
	wchar_t* pszGuid = JStringToWCHAR(env, jGuid);
	wchar_t* pszUserName = JStringToWCHAR(env, jUsername);
	wchar_t* pszServerName = JStringToWCHAR(env, jServerName);
	wchar_t* pszPassword = JStringToWCHAR(env, jPassword);
	wchar_t* pszInstallDirectory = JStringToWCHAR(env, jInstallDirectory);

	pfnStartToDeploy3 startToDeployFunc = NULL;

	HMODULE hModule = LoadLibrary(L"AgentDeployTool.dll");
	DWORD error = GetLastError();
	if(hModule != NULL)
	{
		startToDeployFunc = (pfnStartToDeploy3)GetProcAddress(hModule, "StartToDeploy3");
		if(startToDeployFunc != NULL)
		{
			AdtTempEnv = env;
			AdtTempClz = clz;
			AdtTempHandler = jCallback;
			
			(startToDeployFunc)(pszLocalDomain, pszLocalUser, pszLocalPassword, pszGuid, pszServerName, pszUserName, pszPassword, (UINT)jPort, pszInstallDirectory, (BOOL)jAutoStartRRService, (BOOL)jReboot, (BOOL)jInstallDriver, (BOOL)jResumedAndCheck, (BOOL)jUseHttps, (DeployStatusFunction)UpdateDeployStatus);

		}
	}

	if(pszLocalDomain != NULL)
	{
		free(pszLocalDomain);
		pszLocalDomain = NULL;
	}

	if(pszLocalUser != NULL)
	{
		free(pszLocalUser);
		pszLocalUser = NULL;
	}

	if(pszLocalPassword != NULL)
	{
		free(pszLocalPassword);
		pszLocalPassword = NULL;
	}

	if(pszGuid != NULL)
	{
		free(pszGuid);
		pszGuid = NULL;
	}

	if(pszServerName != NULL)
	{
		free(pszServerName);
		pszServerName = NULL;
	}

	if(pszUserName != NULL)
	{
		free(pszUserName);
		pszUserName = NULL;
	}

	if(pszPassword != NULL)
	{
		free(pszPassword);
		pszPassword = NULL;
	}

	if(pszInstallDirectory != NULL)
	{
		free(pszInstallDirectory);
		pszInstallDirectory = NULL;
	}

}


JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_StartToDeploy
  (JNIEnv *env, jclass clz, jstring jLocalDomain, jstring jLocalUser, jstring jLocalPassword, jstring jGuid, jstring jServerName, jstring jUsername, jstring jPassword, jint jPort, jstring jInstallDirectory, jboolean jAutoStartRRService, jboolean jReboot, jboolean jResumedAndCheck, jobject jCallback)
{

	wchar_t* pszLocalDomain = JStringToWCHAR(env, jLocalDomain);
	wchar_t* pszLocalUser = JStringToWCHAR(env, jLocalUser);
	wchar_t* pszLocalPassword = JStringToWCHAR(env, jLocalPassword);
	wchar_t* pszGuid = JStringToWCHAR(env, jGuid);
	wchar_t* pszServerName = JStringToWCHAR(env, jServerName);
	wchar_t* pszUserName = JStringToWCHAR(env, jUsername);
	wchar_t* pszPassword = JStringToWCHAR(env, jPassword);
	wchar_t* pszInstallDirectory = JStringToWCHAR(env, jInstallDirectory);

	pfnStartToDeploy startToDeployFunc = NULL;

	HMODULE hModule = LoadLibrary(L"AgentDeployTool.dll");
	DWORD error = GetLastError();
	if(hModule != NULL)
	{
		startToDeployFunc = (pfnStartToDeploy)GetProcAddress(hModule, "StartToDeploy");
		if(startToDeployFunc != NULL)
		{
			AdtTempEnv = env;
			AdtTempClz = clz;
			AdtTempHandler = jCallback;
			
			(startToDeployFunc)(pszLocalDomain, pszLocalUser, pszLocalPassword, pszGuid, pszServerName, pszUserName, pszPassword, (UINT)jPort, pszInstallDirectory, (BOOL)jAutoStartRRService, (BOOL)jReboot, (BOOL)jResumedAndCheck, (DeployStatusFunction)UpdateDeployStatus);

		}
	}

	if(pszLocalDomain != NULL)
	{
		free(pszLocalDomain);
		pszLocalDomain = NULL;
	}

	if(pszLocalUser != NULL)
	{
		free(pszLocalUser);
		pszLocalUser = NULL;
	}

	if(pszLocalPassword != NULL)
	{
		free(pszLocalPassword);
		pszLocalPassword = NULL;
	}

	if(pszGuid != NULL)
	{
		free(pszGuid);
		pszGuid = NULL;
	}

	if(pszServerName != NULL)
	{
		free(pszServerName);
		pszServerName = NULL;
	}

	if(pszUserName != NULL)
	{
		free(pszUserName);
		pszUserName = NULL;
	}

	if(pszPassword != NULL)
	{
		free(pszPassword);
		pszPassword = NULL;
	}

	if(pszInstallDirectory != NULL)
	{
		free(pszInstallDirectory);
		pszInstallDirectory = NULL;
	}

}
//==================Agent Deploy End============================================

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_BrowseFileFolderItem
(JNIEnv *env, jclass clz, jstring path, jstring domain, jstring user, jstring pwd, jobject retArr)
{
	wchar_t* pszPath = JStringToWCHAR(env, path);
	wchar_t* pszDomain = JStringToWCHAR(env, domain);
	wchar_t* pszUser = JStringToWCHAR(env, user);
	wchar_t* pszPwd = JStringToWCHAR(env, pwd);

	IFileListHandler *pList = NULL;

	DWORD dwRet = CreateIFileListHandler(&pList);
	if(dwRet)
	{
		wprintf(L"get handler failed[%d]\n", dwRet);		
	}
	else
	{	
		vector<FILE_INFO> vList;

		NET_CONN_INFO conn;
		ZeroMemory(&conn, sizeof(NET_CONN_INFO));

		if(pszDomain)
		{
			wcscpy_s(conn.szDomain, _countof(conn.szDomain), pszDomain);
		}
		if(pszUser){
			wcscpy_s(conn.szUsr, _countof(conn.szUsr), pszUser);
		}
		if(pszPwd){
			wcscpy_s(conn.szPwd, _countof(conn.szPwd), pszPwd);
		}
		if(pszPath)
		{
			wcscpy_s(conn.szDir, _countof(conn.szDir), pszPath);
		}

		int iNum = -1;
		dwRet = pList->GetFileList(vList, iNum, conn);
		if(dwRet)
		{
			wprintf(L"get file list failed[%d]\n", dwRet);
		}
		else
		{
			AddVecFileInfo2List(env, &retArr,vList);	
		}

		pList->Release();
	}

	if (pszPath != NULL)
	{
		free(pszPath);
		pszPath = NULL;
	}

	if (pszDomain != NULL)
	{
		free(pszDomain);
		pszDomain = NULL;
	}

	if (pszUser != NULL)
	{
		free(pszUser);
		pszUser = NULL;
	}

	if (pszPwd != NULL)
	{
		free(pszPwd);
		pszPwd = NULL;
	}
	return dwRet;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_BrowseFileFolderItemEx(
	JNIEnv *env, 
	jclass clz, 
	jstring path, 
	jint type,	// 0: both files / folder; 1: folders only; 2: files only
	jint maxCount,
	jstring domain, 
	jstring user, 
	jstring pwd, 
	jobject retArr
	)
{
	wchar_t* pszPath = JStringToWCHAR(env, path);
	wchar_t* pszDomain = JStringToWCHAR(env, domain);
	wchar_t* pszUser = JStringToWCHAR(env, user);
	wchar_t* pszPwd = JStringToWCHAR(env, pwd);

	IFileListHandler *pList = NULL;

	DWORD dwRet = CreateIFileListHandler(&pList);
	if(dwRet)
	{
		wprintf(L"get handler failed[%d]\n", dwRet);		
	}
	else
	{	
		vector<FILE_INFO> vList;

		NET_CONN_INFO conn;
		ZeroMemory(&conn, sizeof(NET_CONN_INFO));

		if(pszDomain)
		{
			wcscpy_s(conn.szDomain, _countof(conn.szDomain), pszDomain);
		}
		if(pszUser){
			wcscpy_s(conn.szUsr, _countof(conn.szUsr), pszUser);
		}
		if(pszPwd){
			wcscpy_s(conn.szPwd, _countof(conn.szPwd), pszPwd);
		}
		if(pszPath)
		{
			wcscpy_s(conn.szDir, _countof(conn.szDir), pszPath);
		}

		int iNum = maxCount;	// retrieve how many number of folders / files
		dwRet = pList->GetFileListEx(vList, type, iNum, conn);
		if(dwRet)
		{
			wprintf(L"get file list failed[%d]\n", dwRet);
		}
		else
		{
			AddVecFileInfo2List(env, &retArr,vList);	
		}

		pList->Release();
	}

	if (pszPath != NULL)
	{
		free(pszPath);
		pszPath = NULL;
	}

	if (pszDomain != NULL)
	{
		free(pszDomain);
		pszDomain = NULL;
	}

	if (pszUser != NULL)
	{
		free(pszUser);
		pszUser = NULL;
	}

	if (pszPwd != NULL)
	{
		free(pszPwd);
		pszPwd = NULL;
	}
	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFInitDestination
(JNIEnv *env, jclass clz, jobject jNewConn, jobject jOldConn, jlong dwBkpType, jboolean isCopy)
{
	NET_CONN_INFO newConn;	
	memset(&newConn,0,sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env,&jNewConn,newConn);

	NET_CONN_INFO oldConn;
	memset(&oldConn,0,sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env,&jOldConn,oldConn);

	PNET_CONN_INFO pOldConn = &oldConn;
	if(!oldConn.szDir) 
	{
		pOldConn = NULL;
	}

	DWORD dwRet = AFInitDestination(&newConn, pOldConn, (DWORD)dwBkpType, (BOOL)isCopy);

	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckFolderAccess
(JNIEnv *env, jclass clz, jobject jConn, jobject jArrFileinfo)
{
	NET_CONN_INFO connInfo;	
	memset(&connInfo,0,sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env,&jConn,connInfo);

   FILE_INFO fileInfo;
   memset(&fileInfo, 0, sizeof(FILE_INFO));

   DWORD dwRet = AFCheckFolderAccess(&connInfo, fileInfo); 
   if(dwRet)
   {
	   wprintf(L"get failed[%d]\n", dwRet);
   }
   else
   {
	   std::vector<FILE_INFO> vec;
	   vec.push_back(fileInfo);
	   AddVecFileInfo2List(env,&jArrFileinfo,vec);
   }

   return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFRetrieveSharedResource
(JNIEnv *env, jclass clz, jobject jConn, jobject retArr)
{

	NET_CONN_INFO connInfo;	
	memset(&connInfo,0,sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env,&jConn,connInfo);

	std::vector<std::wstring> vecShare;
	DWORD dwRet = AFRetrieveSharedResource(&connInfo, vecShare);

	if(dwRet)
	{
		wprintf(L"get failed[%d]\n", dwRet);
	}
	else
	{	  
		AddVecString2List(env,&retArr,vecShare);
	}

	return dwRet;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckCompressLevelChanged
(JNIEnv *env, jclass clz, jobject jConn, jint level){
	NET_CONN_INFO connInfo;	
	memset(&connInfo,0,sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env,&jConn,connInfo);

	return AFCheckVhdFormatChanged(&connInfo, level);
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckRecoveryVMJobExist
(JNIEnv *env, jclass clz, jstring vmName,jstring esxServerName){
	
	wchar_t* pVMName = JStringToWCHAR(env, vmName);

	wchar_t* pEsxServerName = JStringToWCHAR(env, esxServerName);
	
	BOOL ret = AFCheckRecoveryVMJobExist(pEsxServerName,pVMName);

	if(pVMName != NULL)
		free(pVMName);

	if(pEsxServerName != NULL)
		free(pEsxServerName);

	return ret;
}


JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckEncryptionAlgorithmAndKeyChanged
(JNIEnv *env, jclass clz, jobject jConn, jint algorithm, jstring key) {
	NET_CONN_INFO connInfo;
	memset(&connInfo, 0, sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env, &jConn, connInfo);
	ST_CRYPTO_INFO cryptoInfo;
	cryptoInfo.dwCryptoAlgType = algorithm;
	cryptoInfo.wsCryptoPwd = JStringToWString(env, key);
	return AFIsEncInfoChanged(connInfo, cryptoInfo);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_createJobMonitor
(JNIEnv *env, jclass clz, jlong shrMemId){

	IAFJobMonitor *pAFJM = NULL;
	JOB_MONITOR    aJM = {0};
	memset(&aJM, 0, sizeof(JOB_MONITOR));

	DWORD dwRet  = CreateIJobMonitor(shrMemId, &pAFJM);
	if(!dwRet)
	{		
		return (jlong)pAFJM;
	}

	return 0;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getJobMonitor
(JNIEnv *env, jclass clz, jlong address, jobject jobMonitor)
{
	//wprintf(L"Address:[%d]\n", address);
	IAFJobMonitor *pAFJM = (IAFJobMonitor *)address;
	JOB_MONITOR    aJM = {0};
	memset(&aJM, 0, sizeof(JOB_MONITOR));
	
	DWORD dwRead = 0;
	if(pAFJM)
	{		
		
		dwRead = pAFJM->Read(&aJM);
		//wprintf(L"dwRead[%d]\n", dwRead);
		if(!dwRead)
		{
			JOB_MONITOR2JJobMonitor(env, aJM,jobMonitor);
		}else
		{
			//wprintf(L"pAFJM->Read failed[%d]\n", dwRead);
			return dwRead;
		}
	}

	return dwRead;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_releaseJobMonitor
(JNIEnv *env, jclass clz, jlong address){

	IAFJobMonitor *pAFJM = (IAFJobMonitor *)address;
	if (pAFJM)
		DestroyIJobMonitor(&pAFJM);
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getJobID
(JNIEnv *env, jclass clz, jobject jobID){
	DWORD dwJobId = 0;
	BOOL returnValue = AFGetJobId(&dwJobId, 1);
	
	AddUINT2JRWLong(env, dwJobId, &jobID);

	return returnValue;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getJobIDs
(JNIEnv *env, jclass clz, jint count, jobject jobIdStart)
{
	DWORD dwJobId = 0;
	BOOL returnValue = AFGetJobId(&dwJobId, count);

	AddUINT2JRWLong(env, dwJobId, &jobIdStart);

	return returnValue;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getCurrentJobID
(JNIEnv *, jclass){
	DWORD returnValue = AFGetCurrentJobId();
	return returnValue;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_checkJobExist
(JNIEnv *env, jclass clz){
	return AFCheckJobExist();
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_checkBMRPerformed
(JNIEnv *env, jclass clz){
	return AFCheckBMRPerformed();
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_cancelJob
(JNIEnv *env, jclass clz, jlong jobID){
	return AFCancelJob(jobID);
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_updateThrottling
(JNIEnv *env, jclass clz, jlong jobID, jlong throttling)
{
	return AFUpdateThrottling(jobID, NULL, throttling);
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetErrorMsg
(JNIEnv *env, jclass clz, jlong errorCode)
{
	std::wstring errMsg;	

	AFGetErrorMsg((DWORD)errorCode,errMsg);
	
	jstring jstr = WCHARToJString(env,(wchar_t*)errMsg.c_str());
	return jstr;
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckDestValid
(JNIEnv *env, jclass clz, jstring dir)
{
	wchar_t* pDir = JStringToWCHAR(env, dir);
	DWORD returnValue = AFCheckDestValid(pDir);
	return returnValue;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetPathMaxLength
(JNIEnv *env, jclass)
{
	DWORD returnValue = 0;
	AFGetPathMaxLength(&returnValue);
	return returnValue;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetVSpherePathMaxLength
(JNIEnv *env, jclass)
{
	DWORD returnValue = 0;
	AFGetVSpherePathMaxLength(&returnValue);
	return returnValue;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckDestNeedHostName
(JNIEnv *env, jclass clz, jstring path, jstring serverName, jstring nodeId, jboolean bToCreateFolder, jobject retArr)
{
	wchar_t* pszPath   = JStringToWCHAR(env, path);
	wchar_t* pszNodeID = JStringToWCHAR(env, nodeId);

	if(NULL ==pszPath)
	{
		return E_INVALIDARG;
	}
	std::wstring strPath = pszPath;
	std::wstring strHostName = L"<NULL>";
	wchar_t* serverNameWStr = NULL;

	if(serverName == NULL)
	{
		strHostName = L"";
	}
	else
	{
		serverNameWStr = JStringToWCHAR(env, serverName);
		strHostName = serverNameWStr;
	}

	DWORD dwRet =0;
	try
	{
		dwRet = AFCheckDestNeedHostName(strPath, strHostName, NULL, pszNodeID, bToCreateFolder );
	}
	catch(...)
	{
		dwRet = E_FAIL;
		CDbgLog logObj(L"NativeFacade");
		logObj.LogW(LL_WAR, 0, L"WSJNI_AFCheckDestNeedHostName : Exception when invoking AFCheckDestNeedHostName()...");

	}
	 
	SAFE_FREE(pszPath);
	SAFE_FREE(pszNodeID);
	SAFE_FREE(serverNameWStr);

	if(dwRet == 0)
	{
		AddstringToList(env,clz,(wchar_t*)strHostName.c_str(),&retArr);
	}

	return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckDestNeedVMInfo
(JNIEnv *env, jclass clz, jstring path, jstring serverName, jobject retArr,jstring instanceUUID)
{
	wchar_t* pszPath = JStringToWCHAR(env, path);
	wchar_t* vmInstanceUUID = JStringToWCHAR(env, instanceUUID);
	std::wstring strPath = pszPath;
	std::wstring strHostName;
	wchar_t* serverNameWStr = NULL;

	if(serverName == NULL)
		strHostName = L"";
	else
	{
		serverNameWStr = JStringToWCHAR(env, serverName);
		strHostName = serverNameWStr;
	}

	DWORD dwRet = AFCheckDestNeedHostName(strPath, strHostName,vmInstanceUUID, NULL);

	if(pszPath != NULL)
	{
		free(pszPath);
	}

	if(serverNameWStr != NULL)
	{
		free(serverNameWStr);
	}
	
	if(vmInstanceUUID != NULL)
	{
		free(vmInstanceUUID);
	}

	if(dwRet == 0)
	{
		AddstringToList(env,clz,(wchar_t*)strHostName.c_str(),&retArr);
	}

	return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFSaveAdminAccount
  (JNIEnv *env, jclass clz, jobject account)
{
	return (jlong)AFSaveAdminAccount(env, account);
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckAdminAccountValid
  (JNIEnv *env, jclass clz, jobject account)
{
	return (jlong)CheckAdminAccountValid(env, account);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFReadAdminAccount
  (JNIEnv *env, jclass clz, jobject account)
{
	jclass class_account = env->GetObjectClass(account);

	jmethodID set_username = env->GetMethodID(class_account, "setUserName","(Ljava/lang/String;)V");
	jmethodID set_password = env->GetMethodID(class_account, "setPassword","(Ljava/lang/String;)V");

	std::wstring userName;
	std::wstring password;

	DWORD dwRet = AFReadAdminAccount(userName, password);

	if(dwRet == 0)
	{
	   jstring userNameStr = WCHARToJString(env, (wchar_t*)userName.c_str());	
	   jstring passwordStr = WCHARToJString(env, (wchar_t*)password.c_str());	
	
	   env->CallVoidMethod(account, set_username, userNameStr);
       env->CallVoidMethod(account, set_password, passwordStr);
	}
    if (class_account!= NULL) env->DeleteLocalRef(class_account);

	return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetBakDestDriveType
(JNIEnv *env, jclass clz, jstring dir)
{
	wchar_t* pDir = JStringToWCHAR(env, dir);
	NET_CONN_INFO connInfo;	
	memset(&connInfo,0,sizeof(NET_CONN_INFO));
	if(pDir != NULL)
	{
		wcscpy_s(connInfo.szDir, pDir);
		free(pDir);
	}

	DWORD destType = -1;
	DWORD returnValue = AFGetBakDestDriveType(connInfo, destType);

	if(returnValue == 0)
       return (jlong)destType;

	return -1;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCutConnection
(JNIEnv *env, jclass clz, jobject jConn, jboolean force)
{
   NET_CONN_INFO connInfo;	
   memset(&connInfo,0,sizeof(NET_CONN_INFO));
   JNetConnInfo2NET_CONN_INFO(env,&jConn,connInfo);

   DWORD dwRet = AFCutConnection(connInfo, (BOOL)force); 
   if(dwRet)
   {
	   wprintf(L"get failed[%d]\n", dwRet);
   }

   return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCreateDir
(JNIEnv *env, jclass clz, jstring parentdir, jstring subdir)
{
	wchar_t* pPath = JStringToWCHAR(env, parentdir);
	std::wstring strParentPath = pPath;

	wchar_t* subPath = JStringToWCHAR(env, subdir);
	std::wstring strSubPath = subPath;

    DWORD dwRet = AFCreateDir(strParentPath, strSubPath);
    
	if(pPath != NULL)
	{
		free(pPath);
	}

	if(subPath != NULL)
	{
		free(subPath);
	}

	if(dwRet)
    {
	   wprintf(L"get failed[%d]\n", dwRet);
    }

	return (jlong)dwRet;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckPathAccess
(JNIEnv *env, jclass clz, jobject jConn)
{
   NET_CONN_INFO connInfo;	
   memset(&connInfo,0,sizeof(NET_CONN_INFO));
   JNetConnInfo2NET_CONN_INFO(env,&jConn,connInfo);

   return AFCheckPathAccess(connInfo); 
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFSetThreshold
(JNIEnv *env, jclass clz, jlong threshold)
{
	DWORD dwRet = AFSetThreshold(threshold);
	return dwRet;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFTryToFindDest
(JNIEnv *env, jclass, jstring oldDestination)
{
	CDbgLog logXObj(L"NativeFacade");

	jstring jstrNewDestination =0;
	std::wstring newDestination;

	do
	{
		if(0== oldDestination)
		{
			logXObj.LogW(LL_WAR, 0, L"ikaka oldDestination in Java_com_ca_arcflash_webservice_jni_WSJNI_AFTryToFindDest...");
			break;
		}

		wchar_t* wstrOldDestination = JStringToWCHAR(env, oldDestination);

		if(NULL == wstrOldDestination )
		{  
			logXObj.LogW(LL_WAR, 0, L"ikaka empty path in Java_com_ca_arcflash_webservice_jni_WSJNI_AFTryToFindDest...");
			break;
		}

		AFTryToFindDest(wstrOldDestination,newDestination);

		if(wstrOldDestination != NULL)
		{
			free(wstrOldDestination);
		}

		jstring jstrNewDestination = WCHARToJString(env,(wchar_t*)newDestination.c_str() );

	}while(0);
	
	
	return jstrNewDestination;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckSQLAlternateLocation
(JNIEnv *env, jclass clz, jstring basePath, jstring instName, jstring dbName, jobject alterDestPath)
{
	wchar_t* basPathWchar = JStringToWCHAR(env, basePath);
	wchar_t* instNameWchar = JStringToWCHAR(env, instName);
	wchar_t* dbNameWchar = JStringToWCHAR(env, dbName);	
	std::wstring alterDestStr;

	long ret = AFCheckSQLAlternateLocation(basPathWchar, instNameWchar, dbNameWchar, alterDestStr);

	if(basPathWchar != NULL)
	{
		free(basPathWchar);
	}
	if(instNameWchar != NULL)
	{
		free(instNameWchar);
	}	
	if(dbNameWchar != NULL)
	{
		free(dbNameWchar);
	}

	if(ret >= 0)
	{
		AddstringToList(env,clz,(wchar_t*)alterDestStr.c_str(),&alterDestPath);
	}

	return (jlong)ret;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckBLILic
  (JNIEnv * env, jclass clz)
{
	BOOL hasBLILic = AFCheckBLILic();
	if(hasBLILic)
	{
		return JNI_TRUE;
	}
	return JNI_FALSE;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckDestContainRecoverPoint
(JNIEnv *env, jclass clz, jobject jConn)
{
	NET_CONN_INFO connInfo;	
	memset(&connInfo,0,sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env,&jConn,connInfo);
	
	return (jlong)AFCheckDestContainRecoverPoint(connInfo); 
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckDirPathValid
(JNIEnv *env, jclass clz, jobject jConn)
{
	NET_CONN_INFO connInfo;	
	memset(&connInfo,0,sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env,&jConn,connInfo);

	//wstring path = JStringToWString(env, connInfo.szDir);
	jlong ret;

	DWORD dwRet = AFCreateConnection(connInfo); 
	if(dwRet)
	{
		wprintf(L"get failed[%d]\n", dwRet);
		return (jint) GetLastError();
	} else {
		BOOL isExist = CheckFileFolderExist(connInfo.szDir);
		DWORD errorNumber = GetLastError();

		DWORD dwRet = AFCutConnection(connInfo); 
		if(dwRet)
		{
			wprintf(L"get failed[%d]\n", dwRet);
		}

		if(isExist)
		{
			return 0;
		} else {
			return -1;
		}
	}

	return (jint) GetLastError();
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckBaseLic
  (JNIEnv *, jclass)
{
	BOOL hasBLILic = AFCheckBaseLic();
	if(hasBLILic)
	{
		return JNI_TRUE;
	}
	return JNI_FALSE;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GatherExcludedFileListInWriter
(JNIEnv *env, jclass clz, jobject volumeList, jobject appList)
{
	jclass list_class = env->GetObjectClass(volumeList);
	jmethodID size = env->GetMethodID(list_class, "size", "()I");
	jmethodID get_method = env->GetMethodID(list_class, "get", "(I)Ljava/lang/Object;");
	
	jint listSize = env->CallIntMethod(volumeList, size);
	std::vector<std::wstring> volumeVector;
	for(int i = 0; i < listSize; i++)
	{
		jstring volume = (jstring)env->CallObjectMethod(volumeList, get_method, i);
		wchar_t* volume_WChar = JStringToWCHAR(env, volume);
		if(volume_WChar != NULL)
		{
			volumeVector.push_back(volume_WChar);
			free(volume_WChar);
		}		
	}

	ExcludedWriterVector excludedList;
	long ret = GatherExcludedFileListInWriter(volumeVector, excludedList);
	convertWriterVector2AppList(env, excludedList, appList);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetDestSizeInformation
(JNIEnv *env, jclass clz, jstring destpath, jobject destination)
{

	wchar_t* pDest = JStringToWCHAR(env, destpath);

	unsigned long long totalSize = 0;
	unsigned long long freeSize = 0;

	DWORD ret = GetDestSize(pDest, totalSize, freeSize);

	wchar_t wstrTotalSize[128] = {0};
	_i64tow_s(totalSize, wstrTotalSize, _countof(wstrTotalSize), 10);
	jstring jstrTotalSize = WCHARToJString(env, wstrTotalSize);

	wchar_t wstrFreeSize[128] = {0};
	_i64tow_s(freeSize, wstrFreeSize, _countof(wstrFreeSize), 10);
	jstring jstrFreeSize = WCHARToJString(env, wstrFreeSize);
	
	if(pDest != NULL){
		free(pDest);
	}

	jclass dest_class = env->GetObjectClass(destination);
	jmethodID totalSizeSetter = env->GetMethodID(dest_class,"setTotalSize","(Ljava/lang/String;)V");
	env->CallVoidMethod(destination,totalSizeSetter,jstrTotalSize);

	jmethodID freeSizeSetter = env->GetMethodID(dest_class,"setTotalFreeSize","(Ljava/lang/String;)V");
	env->CallVoidMethod(destination,freeSizeSetter,jstrFreeSize);
	
	return ret;
	
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCreateConnection
(JNIEnv *env, jclass clz, jobject jConn)
{
   NET_CONN_INFO connInfo;	
   memset(&connInfo,0,sizeof(NET_CONN_INFO));
   JNetConnInfo2NET_CONN_INFO(env,&jConn,connInfo);

   DWORD dwRet = AFCreateConnection(connInfo); 
   if(dwRet)
   {
	   wprintf(L"get failed[%d]\n", dwRet);
   }

   return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_RegenerateWriterMetadata
  (JNIEnv *env, jclass clz)
{
	 DWORD dwRet = GatherWriterMetadataForWriter();
	 return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetLocalDestVolumes
  (JNIEnv *env, jclass clz, jstring destpath, jobject localList)
{
	jclass list_class = env->GetObjectClass(localList);
	jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

	wchar_t* pDest = JStringToWCHAR(env, destpath);
	std::vector<std::wstring> vGuid;
	DWORD dwRet = AFGetLocalDestVolumes(pDest, vGuid);

	for(vector<std::wstring>::iterator volume = vGuid.begin(); volume != vGuid.end(); volume++)
	{
			jstring volume_JStr = WCHARToJString(env, (wchar_t*)volume->c_str());
			env->CallBooleanMethod(localList, addMethod, volume_JStr);
			if(volume_JStr != NULL)
				env->DeleteLocalRef(volume_JStr);
	}

	if(list_class != NULL)
		env->DeleteLocalRef(list_class);

	if(pDest != NULL)
		free(pDest);

	return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetBackupItem
(JNIEnv *env, jclass clz, jstring dest,jstring domain,jstring user,jstring pwd, jstring subPath, jobject jBkpItemList)
{
	wchar_t* pDest = JStringToWCHAR(env, dest);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);
	wchar_t* pSubPath = JStringToWCHAR(env, subPath);
	
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

	IRestorePoint *pIRest = NULL;
	DWORD dwRet = CreateIRestorePoint(info,&pIRest);

	if(!dwRet)
	{		
		VBACKUP_ITEM vBkpItem;
		dwRet = pIRest->GetBackupItem(info.szDir,pSubPath,vBkpItem);	
		if(vBkpItem.size() > 0)
		{
			VBACKUP_ITEM vBkpItem2Return;
			/**
			 Do not return Exchange 2013 writer, if it does not contain any database
			 for r16.5 backup, if all Exchange DBs are dismounted, we still create
			 catalog for Exchange writer but there is no DB for recovery. Here we hide
			 the Exchange 2013 writer from UI by not returning it to UI.
			*/
			for(VBACKUP_ITEM::iterator itr = vBkpItem.begin(); itr != vBkpItem.end(); itr++)
			{
				if(!isExchangeWriter(*itr) || hasExchangeDB(*itr))
				{
					vBkpItem2Return.push_back(*itr);
				}
			}
			AddVBACKUP_ITEM2List(env,jBkpItemList,vBkpItem2Return);
		}
		pIRest->Release();
	}else
	{
		wprintf(L"CreateIRestorePoint[%s] failed\n", dest);

	}

	if (pSubPath != NULL)
	{	
		free(pSubPath);
	}

	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetNetworkPathForMappedDrive
  (JNIEnv *env, jclass clz, jstring userName, jobject mapList)
{
	jclass mapListClass = env->GetObjectClass(mapList);
	jmethodID addMethodID = env->GetMethodID(mapListClass, "add", "(Ljava/lang/Object;)Z");

	jclass networkPathClass = env->FindClass("com/ca/arcflash/webservice/data/NetworkPath");
	jmethodID constructor = env->GetMethodID(networkPathClass, "<init>", "()V");
	jfieldID driverLetterField = env->GetFieldID(networkPathClass, "driverletter", "Ljava/lang/String;");
	jfieldID remotePathField = env->GetFieldID(networkPathClass, "remotePath", "Ljava/lang/String;");

	wchar_t* pDest = JStringToWCHAR(env, userName);
	std::vector<MAPPED_DRV_PATH> mapVector;	
	DWORD ret = AFGetNetworkPathForMappedDrive(pDest, mapVector);

	for(std::vector<MAPPED_DRV_PATH>::iterator driverMapPath = mapVector.begin(); driverMapPath != mapVector.end(); driverMapPath++)
	{
		jobject mapPathObject = env->NewObject(networkPathClass, constructor);
		jstring mnt = WCHARToJString(env, (wchar_t*)driverMapPath->strMnt.c_str());
		env->SetObjectField(mapPathObject, driverLetterField, mnt);
		if(mnt != NULL)
			env->DeleteLocalRef(mnt);

		jstring remotePath = WCHARToJString(env, (wchar_t*)driverMapPath->strPath.c_str());
		env->SetObjectField(mapPathObject, remotePathField, remotePath);
		if(remotePath != NULL)
			env->DeleteLocalRef(remotePath);

		env->CallBooleanMethod(mapList, addMethodID, mapPathObject);
		if(mapPathObject != NULL)
			env->DeleteLocalRef(mapPathObject);
	}

	if(pDest != NULL)
		free(pDest);

	return (jlong) ret;
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetLicenseInfo
  (JNIEnv *env, jclass clz, jobject jvolList)
{	
	LICENSE_INFO licInfo;
	ZeroMemory(&licInfo, sizeof(LICENSE_INFO));
	std::vector<std::wstring> vols;
	jList2Vector(env,jvolList,vols);
	
	AFGetLicenseInfo(vols, licInfo);

	jobject jlicInfo = LICENSE_INFO2JLicInfo(env,licInfo);

	return jlicInfo;
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetLicenseInfoEx
  (JNIEnv *env, jclass clz, jobject jvolList, jboolean bFilterLicInfo)
{	
	LICENSE_INFO licInfo;
	ZeroMemory(&licInfo, sizeof(LICENSE_INFO));
	std::vector<std::wstring> vols;
	jList2Vector(env,jvolList,vols);
	
	AFGetLicenseInfoEx(vols, licInfo, bFilterLicInfo ? TRUE : FALSE);

	jobject jlicInfo = LICENSE_INFO2JLicInfo(env,licInfo);

	return jlicInfo;
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetLicenseError
  (JNIEnv *env, jclass clz)
{	
	LICENSE_INFO licInfo;
	ZeroMemory(&licInfo, sizeof(LICENSE_INFO));

	AFGetLicenseErrorInfo(licInfo);

	jobject jlicInfo = LICENSE_INFO2JLicInfo(env,licInfo);

	return jlicInfo;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetLicenseStatus(JNIEnv *env, jclass clz, jboolean isStandalone)
{
	jlong jRet;
	jRet = (jlong) AFGetLicenseStatus( (!(BOOL)isStandalone) );

	return jRet;

	/*
	AGRT_PARENT Parent = {0};
	AGRT_ITEMS pvItem = {0};

	DWORD dwRet =0;
	DWORD dwSessNo = 1;
	DWORD nSubSess = 3;
	D2D_ENCRYPTION_INFO stEncry= {0};
	do 
	{ 
	Parent.size = sizeof(AGRT_PARENT);
	Parent.AppType = APP_GRT_AD;
	Parent.DataType = APP_DATA_AD_CHILD;
	Parent.dwSubSession = nSubSess;
	Parent.parent.AD_Parent.dwDNT = 2008;

	Parent.session.dwSessNo = dwSessNo;
	Parent.session.lpRootFolder = L"\\\\wanmi12-win7\\x$\\dst\\wanmi12-adgrt";
	Parent.session.lpUserName  = L"administrator";
	Parent.session.lpPassword  = L"1qaz2wsX";
	Parent.session.pEncryptInfo = &stEncry;

	dwRet = AppGetItems(&Parent, &pvItem);

	if(dwRet)
	{
	break;
	}

	for(int i=0; i < pvItem.nItemNum; i++)
	{
	{
	Parent.DataType = APP_DATA_AD_ATTr;
	Parent.parent.AD_Parent.dwDNT = pvItem.pvItems[i].item.ADItem.dwDnt;
	AGRT_ITEMS ATTrs = {0};
	dwRet = AppGetItems(&Parent, &ATTrs);

	logObj.LogW(LL_INF, 0, L"LIST OBJECT (%s:%d) ATTrs:",  pvItem.pvItems[i].item.ADItem.pszName, pvItem.pvItems[i].item.ADItem.dwDnt);
	for(int nATTr =0; nATTr < ATTrs.nItemNum; nATTr ++)
	{
	if(ATTrs.pvItems[nATTr].item.ADItem.pszName &&ATTrs.pvItems[nATTr].item.ADItem.pszValue)
	{

	CString strATTr;
	strATTr.Format(L"%s=%s", ATTrs.pvItems[nATTr].item.ADItem.pszName, ATTrs.pvItems[nATTr].item.ADItem.pszValue);
	logObj.LogW(LL_INF, 0, L"***%s", strATTr);
	}
	}

	if(ATTrs.nItemNum)
	{
	AppRleaseItems(&ATTrs);
	}
	}
	}


	} while (0);

	if(pvItem.nItemNum >0 )
	{
	AppRleaseItems(&pvItem);
	}
	*/
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckSessExist
  (JNIEnv *env, jclass clz, jstring sessSrcPath, jlong sessNum)
{
	wchar_t* pSessSrcPath = JStringToWCHAR(env, sessSrcPath);
	std::wstring strDest = pSessSrcPath;

	BOOL isSessExist = AFCheckSessExist(strDest, (DWORD)sessNum);

	if(pSessSrcPath != NULL) 
		free(pSessSrcPath);

	return (jboolean) isSessExist;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetMntFromPath
  (JNIEnv *env, jclass clz, jstring path, jobject retArr)
{
	jclass class_List = env->GetObjectClass(retArr);
	jmethodID addMethod = env->GetMethodID(class_List, "add", "(Ljava/lang/Object;)Z");

	wchar_t* pDest = JStringToWCHAR(env, path);
	std::vector<std::wstring> vMnt;
	DWORD dwRet = AFGetMntFromPath(pDest, vMnt);

	for(vector<std::wstring>::iterator mp = vMnt.begin(); mp != vMnt.end(); mp++)
	{
			jstring str = WCHARToJString(env, (wchar_t*)mp->c_str());
			env->CallBooleanMethod(retArr, addMethod, str);
			if(str != NULL)
				env->DeleteLocalRef(str);
	}

	if(class_List != NULL)
		env->DeleteLocalRef(class_List);

	if(pDest != NULL)
		free(pDest);

	return (jlong)dwRet;

}
extern DWORD WINAPI Native_CreateIARCFlashDev(IARCFlashDev **ppIARCFlashDev);

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetAllBackupDestinations
(JNIEnv *env, jclass klass, jstring destDir, jobject retList)
{	
	DWORD iRet;
	try{
		wstring wstrDest = Utility_JStringToWCHAR(env, destDir);

		IARCFlashDev *pIARCFlashDev = NULL;
		Native_CreateIARCFlashDev(&pIARCFlashDev);
		std::vector<std::wstring> vect;
		if(pIARCFlashDev!=NULL){
			iRet = pIARCFlashDev->GetAllBackupDestinationsEx(wstrDest,vect);

			pIARCFlashDev->Release();
		}
		AddVecString2List(env,&retList,vect);
	}
	catch(...){
		return -1;
	}
	return iRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetReplicatedSessions
(JNIEnv *env, jclass klass, jstring destDir, jobject retList, jstring serverName, jstring serverPort, jstring username,jstring password)
{	
	DWORD iRet;
	try{

		ST_HASRV_INFO stSrvInfo;
		memset(&stSrvInfo, 0, sizeof(ST_HASRV_INFO));
		wstring wstrDest = Utility_JStringToWCHAR(env, destDir);
		wstring wstrServerName ;
		wstring wstrServerport ;
		wstring wstrPwd ;
		wstring wstrUsr;

		if(serverName != NULL){
			wstrServerName = Utility_JStringToWCHAR(env,serverName);
			stSrvInfo.pwszName = wstrServerName.c_str();
		}

		if(serverPort != NULL){
			wstrServerport = Utility_JStringToWCHAR(env,serverPort);
			stSrvInfo.pwszPort = wstrServerport.c_str();
		}

		if(username != NULL){
			wstrUsr = Utility_JStringToWCHAR(env, username);
			stSrvInfo.pwszUser = wstrUsr.c_str();
		}

		if(password != NULL){
			wstrPwd = Utility_JStringToWCHAR(env, password);
			stSrvInfo.pwszPwd = wstrPwd.c_str();
		}


		wchar_t* sessionGuid= NULL;


		iRet = Native_HADT_GetLastRepSessInfo(&stSrvInfo, 
			wstrDest.c_str(),
			&sessionGuid);
		if(sessionGuid!=NULL){
			std::vector<std::wstring> vect;
			wstring wstSessionGuid = sessionGuid;
			vect.push_back(wstSessionGuid);
			AddVecString2List(env,&retList,vect);
			Native_HADT_FreeBuffer(sessionGuid);
			sessionGuid = NULL;
		}

	}
	catch(...){
		return -1;
	}
	return iRet;
}

//HyperVRep(HyperVRepParameterModel copyModel);
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_HyperVRep
(JNIEnv *env, jclass klass,jobject param)
{
	try{
		//const HA_JOBSCRIPT* pstJobScript,  IHADTCallback* pCallback
		HA_JOBSCRIPT jobScript;
		memset(&jobScript, 0, sizeof(HA_JOBSCRIPT));

		RepJobMonitor2RepJobScript(env,param,jobScript);

		HyperVRepCallBack * callback = new HyperVRepCallBack();
		callback->env = env;
		callback->vmuuid = jobScript.pwszVmInstID;
		long iret = Native_HADT_StartReplicaJobEx(&jobScript,callback);
		delete callback;
		callback = NULL;
		clear_jobscript(jobScript);
		return iret;
	}
	catch(...){
		//	ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in GetHostAdapterList."), 1);
		return -1;
	}
}

JNIEXPORT jlong  JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getFlashAdrConfigureXML(JNIEnv *env, jclass klass,jstring d2dBinLogsDir){
	wstring wstrD2dBinLogsDir = Utility_JStringToWCHAR(env, d2dBinLogsDir);

	DWORD dwResult = FlashDRCollectInfoForHA(wstrD2dBinLogsDir.c_str());
	if (dwResult)
	{
		ThrowWSJNIException(env, klass, env->NewStringUTF("Error occurs in FlashDRCollectInfoForHA."), dwResult);
		return dwResult;
	}

	return dwResult;	
}

JNIEXPORT jlong  JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getDiskSignatureFromGPTGuid(JNIEnv *env, jclass klass,jstring gptGUID){
	wstring wstrGptGUID = Utility_JStringToWCHAR(env, gptGUID);
	GUID guid ;
	DWORD dwResult = 0;
	dwResult = CLSIDFromString((wchar_t*)wstrGptGUID.c_str(),&guid);

	if (dwResult)
	{
		ThrowWSJNIException(env, klass, env->NewStringUTF("Error occurs in getDiskSignatureFromGPTGuid_CLSIDFromString."), dwResult);
		return dwResult;
	}
	Native_ASAG_CRC crc;
	DWORD diskSign;
	crc.CSSCheckSum((PCHAR)(&guid), sizeof(guid),&diskSign);
	return diskSign;	
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetHostAdapterList
(JNIEnv *env, jclass clazz)
{
	HaUtility::ComEnvironment::Instance().Start();
	map<wstring, wstring> adapterList = GetHostAdapterList();
	jobject objAdapters = CMapToJMap(env,clazz,adapterList);
	HaUtility::ComEnvironment::Instance().Shutdown();
	return objAdapters;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_EnableHostDHCP
(JNIEnv *env, jclass clazz, jstring adapterName)
{
	HaUtility::ComEnvironment::Instance().Start();
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	EnableHostDHCP(wstrAdapterName);
	HaUtility::ComEnvironment::Instance().Shutdown();
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_EnableHostStatic
(JNIEnv *env, jclass clazz, jstring adapterName, jobject ipAddresses, jobject vMasks)
{
	HaUtility::ComEnvironment::Instance().Start();
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	vector<wstring> vstrIpaddresses;
	vector<wstring> vstrMasks;

	JStrListToCVector(env,clazz,ipAddresses,vstrIpaddresses);
	JStrListToCVector(env,clazz,vMasks,vstrMasks);

	EnableHostStatic(wstrAdapterName,vstrIpaddresses,vstrMasks);
	HaUtility::ComEnvironment::Instance().Shutdown();

}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_EnableHostDNS
(JNIEnv *env, jclass clazz, jstring adapterName)
{
	HaUtility::ComEnvironment::Instance().Start();
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	EnableHostDNS(wstrAdapterName);
	HaUtility::ComEnvironment::Instance().Shutdown();
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SetHostDNSDomain
(JNIEnv *env, jclass clazz, jstring adapterName, jstring dnsDomain)
{
	HaUtility::ComEnvironment::Instance().Start();
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	wstring wstrDnsDomain = Utility_JStringToWCHAR(env, dnsDomain);
	SetHostDNSDomain(wstrAdapterName,wstrDnsDomain);
	HaUtility::ComEnvironment::Instance().Shutdown();
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SetHostGateways
(JNIEnv *env, jclass clazz, jstring adapterName, jobject gateways, jobject costMetrics)
{
	HaUtility::ComEnvironment::Instance().Start();
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	vector<wstring> vstrGateways;
	JStrListToCVector(env,clazz,gateways,vstrGateways);

	vector<unsigned short> uShortVec;
	JIntListToCVector(env,clazz,costMetrics,uShortVec);

	SetHostGateways(wstrAdapterName,vstrGateways,uShortVec);
	HaUtility::ComEnvironment::Instance().Shutdown();


}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SetHostDNSServerSearchOrder
(JNIEnv *env, jclass clazz, jstring adapterName, jobject vDNSServerSearchOrder)
{
	HaUtility::ComEnvironment::Instance().Start();
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	vector<wstring> vstrDnsServers;
	JStrListToCVector(env,clazz,vDNSServerSearchOrder,vstrDnsServers);
	SetHostDNSServerSearchOrder(wstrAdapterName,vstrDnsServers);
	HaUtility::ComEnvironment::Instance().Shutdown();

}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetOnlineDisksAndVolumes
(JNIEnv *env, jclass clazz,jobject vDisk,jobject vVolume)
{
	
	DWORD dwRet = 0;
	
	vector<SVolumeInfo> vecVolumes;
	vector<SVolumeInfo>::iterator itrVolume;

	vector<SDiskInfo> vecDisks;
	vector<SDiskInfo>::iterator itrDisk;

	dwRet = GetOnlineDisksAndVolumes(vecDisks,vecVolumes);
	if( dwRet )
	{
		ThrowWSJNIException(env, clazz, env->NewStringUTF("fail to call GetOnlineDisksAndVolumes"), dwRet);
		return NULL;
	}

	//Step 1: set the disk information
	//class and method for java class Disk
	jclass class_disk= env->FindClass("com/ca/arcflash/failover/model/Disk");
	jmethodID disk_setSize = env->GetMethodID(class_disk, "setSize", "(J)V");
	jmethodID disk_addExtent = env->GetMethodID(class_disk, "addDiskExtent", "(Lcom/ca/arcflash/failover/model/DiskExtent;)V");
	jmethodID disk_constructor = env->GetMethodID(class_disk, "<init>", "()V");
	jmethodID disk_setSignature = env->GetMethodID(class_disk, "setSignature", "(Ljava/lang/String;)V");
	jmethodID disk_setDiskGuid = env->GetMethodID(class_disk, "setDiskGuid", "(Ljava/lang/String;)V");
	jmethodID disk_setPartitionType = env->GetMethodID(class_disk, "setPartitionType", "(Ljava/lang/String;)V");
	jmethodID disk_setDiskNumber = env->GetMethodID(class_disk, "setDiskNumber", "(I)V");
	//class and method for java class DiskExtent
	jclass class_extent = env->FindClass("com/ca/arcflash/failover/model/DiskExtent");
	jmethodID extent_constructor = env->GetMethodID(class_extent, "<init>", "()V");
	jmethodID extent_setVolumeId = env->GetMethodID(class_extent, "setVolumeID", "(Ljava/lang/String;)V");
	jmethodID extent_setPartitionOffset = env->GetMethodID(class_extent, "setPartitionOffset", "(J)V");
	//class and method for java class Vector
	jclass class_vector = env->FindClass("java/util/Vector");
	jmethodID vector_contructor = env->GetMethodID(class_vector, "<init>", "()V");
	jmethodID vector_add = env->GetMethodID(class_vector, "add", "(Ljava/lang/Object;)Z");

	//construct the java vector to return
	if(vDisk == NULL)
	{
		vDisk = env->NewObject(class_vector, vector_contructor);
	}
	
	//fill the result vector
	for (itrDisk=vecDisks.begin(); itrDisk!=vecDisks.end(); itrDisk++)
	{
		vector<SDiskExtent>::iterator itrExt;
		jobject jDisk = env->NewObject(class_disk, disk_constructor);
		wchar_t buff[32];

		//set disk size
		env->CallVoidMethod(jDisk, disk_setSize, (*itrDisk).totalSize);
		//set disk signature for MBR
		_ui64tow_s(itrDisk->dwSignature, buff, 32, 10);
		env->CallVoidMethod(jDisk, disk_setSignature, WCHARToJString(env, buff));
		//set disk guid for GPT
		env->CallVoidMethod(jDisk, disk_setDiskGuid, WCHARToJString(env, itrDisk->diskGuid));
		//set disk NO
		env->CallVoidMethod(jDisk, disk_setDiskNumber, (jint)itrDisk->diskNo);
		//set partition style: mbr or gpt
		_itow_s(itrDisk->partitionStyle, buff, 32, 10);
		env->CallVoidMethod(jDisk, disk_setPartitionType, WCHARToJString(env, buff));

		//add disk extents to jDisk
		for (itrExt=(itrDisk->extents).begin(); itrExt!=(itrDisk->extents).end(); itrExt++)
		{
			jobject jExtent = env->NewObject(class_extent, extent_constructor);
			jstring jGuid = WCHARToJString(env, itrExt->volumeGuid.c_str());
			env->CallVoidMethod(jExtent, extent_setVolumeId, jGuid);
			env->CallVoidMethod(jExtent, extent_setPartitionOffset, itrExt->offset);

			env->CallVoidMethod(jDisk, disk_addExtent, jExtent);
		}

		//push element into vDisk
		env->CallBooleanMethod(vDisk, vector_add, jDisk);
	}


	//Step 2:set the volume information
	//class and method for java class Vector
	//jclass class_vector = env->FindClass("java/util/Vector");
	//jmethodID vector_contructor = env->GetMethodID(class_vector, "<init>", "()V");
	//jmethodID vector_add = env->GetMethodID(class_vector, "add", "(Ljava/lang/Object;)Z");
	//class and methord for java class Volume
	jclass class_volume = env->FindClass("com/ca/arcflash/failover/model/Volume");
	jmethodID volume_constructor = env->GetMethodID(class_volume, "<init>", "()V");
	jmethodID volume_setVolumeID = env->GetMethodID(class_volume, "setVolumeID", "(Ljava/lang/String;)V");
	jmethodID volume_setDriveLetter = env->GetMethodID(class_volume, "setDriveLetter", "(Ljava/lang/String;)V");
	jmethodID volume_setFlag = env->GetMethodID(class_volume, "setFlag", "(I)V");
	jmethodID volume_setDynamic = env->GetMethodID(class_volume, "setDynamic", "(Z)V");

	//construct the java vector to return
	if(vVolume == NULL)
	{
		vVolume = env->NewObject(class_vector, vector_contructor);
	}
	
	for (itrVolume=vecVolumes.begin(); itrVolume!=vecVolumes.end(); itrVolume++)
	{
		jobject jVolume = env->NewObject(class_volume, volume_constructor);
		jstring jGuid = WCHARToJString(env, itrVolume->guid);
		jstring jDriveLetter = NULL;
		if(!(itrVolume->driverLetter.empty()))
		{
			jDriveLetter = WCHARToJString(env, itrVolume->driverLetter);
			env->CallVoidMethod(jVolume, volume_setDriveLetter, jDriveLetter);
		}
		env->CallVoidMethod(jVolume, volume_setVolumeID, jGuid);
		env->CallVoidMethod(jVolume, volume_setFlag, itrVolume->ulFlags);
		env->CallVoidMethod(jVolume, volume_setDynamic, itrVolume->isDynamic);


		env->CallBooleanMethod(vVolume, vector_add, jVolume);
	}

	return 0;
}
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_HAIsHostOSGreaterEqual(
	JNIEnv *, jclass,
	jint dwMajor,
	jint dwMinor,
	jshort usServicePackMajor,
	jshort usServicePackMinor)
{
	BOOL bRet = FALSE;
	bRet = HA_IsHostOSGreaterEqual(dwMajor,dwMinor,usServicePackMajor,usServicePackMinor);
	return (jboolean)bRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CheckFolderCompressAttribute(
	JNIEnv *env, jclass,jstring folderPath)
{
	int iResult = 0;
	bool isEncrption = false, isCompress = false;
	wstring strFolderPath = Utility_JStringToWCHAR(env, folderPath);
	iResult = CheckFileCryptCmprsAttr(strFolderPath.c_str(), isCompress, isEncrption );
	if(iResult!=0){
		return -1;
	}
	if(isCompress)
	{
		iResult = iResult || (0x0001);
	}
	if(isEncrption){
		iResult = iResult || (0x0002);
	}
	return iResult;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CheckVolumeCompressAttribute(
	JNIEnv *env, jclass, jstring volumePath)
{
	int iResult = 0;
	bool isCompressVolume =false;
	wstring strVolumePath = Utility_JStringToWCHAR(env, volumePath);
	iResult = CheckIfFileOnCmprsVol(strVolumePath.c_str(),isCompressVolume);
	if(iResult!=0)
	{
		return -1;
	}
	if(isCompressVolume)
	{
		iResult = iResult || (0x0004);
	}
	return iResult;
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetOnlineDisks
(JNIEnv *env, jclass clazz)
{
	jobject vDisk = NULL;
	DWORD dwRet = 0;
	vector<SDiskInfo> vecDisks;
	vector<SDiskInfo>::iterator itrDisk;
	dwRet = GetOnlineDisks(vecDisks);
	if( dwRet )
	{
		ThrowWSJNIException(env, clazz, env->NewStringUTF("fail to call GetOnlineDisks"), dwRet);
		return NULL;
	}

	//class and method for java class Disk
	jclass class_disk= env->FindClass("com/ca/arcflash/failover/model/Disk");
	jmethodID disk_setSize = env->GetMethodID(class_disk, "setSize", "(J)V");
	jmethodID disk_addExtent = env->GetMethodID(class_disk, "addDiskExtent", "(Lcom/ca/arcflash/failover/model/DiskExtent;)V");
	jmethodID disk_constructor = env->GetMethodID(class_disk, "<init>", "()V");
	jmethodID disk_setSignature = env->GetMethodID(class_disk, "setSignature", "(Ljava/lang/String;)V");
	jmethodID disk_setDiskGuid = env->GetMethodID(class_disk, "setDiskGuid", "(Ljava/lang/String;)V");
	jmethodID disk_setPartitionType = env->GetMethodID(class_disk, "setPartitionType", "(Ljava/lang/String;)V");
	jmethodID disk_setDiskNumber = env->GetMethodID(class_disk, "setDiskNumber", "(I)V");
	//class and method for java class DiskExtent
	jclass class_extent = env->FindClass("com/ca/arcflash/failover/model/DiskExtent");
	jmethodID extent_constructor = env->GetMethodID(class_extent, "<init>", "()V");
	jmethodID extent_setVolumeId = env->GetMethodID(class_extent, "setVolumeID", "(Ljava/lang/String;)V");
	jmethodID extent_setPartitionOffset = env->GetMethodID(class_extent, "setPartitionOffset", "(J)V");
	//class and method for java class Vector
	jclass class_vector = env->FindClass("java/util/Vector");
	jmethodID vector_contructor = env->GetMethodID(class_vector, "<init>", "()V");
	jmethodID vector_add = env->GetMethodID(class_vector, "add", "(Ljava/lang/Object;)Z");
	
	//construct the java vector to return
	vDisk = env->NewObject(class_vector, vector_contructor);
	
	//fill the result vector
	for (itrDisk=vecDisks.begin(); itrDisk!=vecDisks.end(); itrDisk++)
	{
		vector<SDiskExtent>::iterator itrExt;
		jobject jDisk = env->NewObject(class_disk, disk_constructor);
		wchar_t buff[32];
		
		//set disk size
		env->CallVoidMethod(jDisk, disk_setSize, (*itrDisk).totalSize);
		//set disk signature for MBR
		_ui64tow_s(itrDisk->dwSignature, buff, 32, 10);
		env->CallVoidMethod(jDisk, disk_setSignature, WCHARToJString(env, buff));
		//set disk guid for GPT
		env->CallVoidMethod(jDisk, disk_setDiskGuid, WCHARToJString(env, itrDisk->diskGuid));
		//set disk NO
		env->CallVoidMethod(jDisk, disk_setDiskNumber, (jint)itrDisk->diskNo);
		//set partition style: mbr or gpt
		_itow_s(itrDisk->partitionStyle, buff, 32, 10);
		env->CallVoidMethod(jDisk, disk_setPartitionType, WCHARToJString(env, buff));

		//add disk extents to jDisk
		for (itrExt=(itrDisk->extents).begin(); itrExt!=(itrDisk->extents).end(); itrExt++)
		{
			jobject jExtent = env->NewObject(class_extent, extent_constructor);
			jstring jGuid = WCHARToJString(env, itrExt->volumeGuid.c_str());
			env->CallVoidMethod(jExtent, extent_setVolumeId, jGuid);
			env->CallVoidMethod(jExtent, extent_setPartitionOffset, itrExt->offset);
			
			env->CallVoidMethod(jDisk, disk_addExtent, jExtent);
		}

		//push element into vDisk
		env->CallBooleanMethod(vDisk, vector_add, jDisk);
	}

	return vDisk;
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetOnlineVolumes
(JNIEnv *env, jclass clazz)
{
	jobject vVolume = NULL;
	DWORD dwRet = 0;
	vector<SVolumeInfo> vecVolumes;
	vector<SVolumeInfo>::iterator itrVolume;
	dwRet = GetOnlineVolumes(vecVolumes);
	if( dwRet )
	{
		ThrowWSJNIException(env, clazz, env->NewStringUTF("fail to call GetOnlineVolumes"), dwRet);
		return NULL;
	}

	//class and method for java class Vector
	jclass class_vector = env->FindClass("java/util/Vector");
	jmethodID vector_contructor = env->GetMethodID(class_vector, "<init>", "()V");
	jmethodID vector_add = env->GetMethodID(class_vector, "add", "(Ljava/lang/Object;)Z");
	//class and methord for java class Volume
	jclass class_volume = env->FindClass("com/ca/arcflash/failover/model/Volume");
	jmethodID volume_constructor = env->GetMethodID(class_volume, "<init>", "()V");
	jmethodID volume_setVolumeID = env->GetMethodID(class_volume, "setVolumeID", "(Ljava/lang/String;)V");
	jmethodID volume_setDriveLetter = env->GetMethodID(class_volume, "setDriveLetter", "(Ljava/lang/String;)V");
	jmethodID volume_setFlag = env->GetMethodID(class_volume, "setFlag", "(I)V");
	jmethodID volume_setDynamic = env->GetMethodID(class_volume, "setDynamic", "(Z)V");

	//construct the java vector to return
	vVolume = env->NewObject(class_vector, vector_contructor);
	
	for (itrVolume=vecVolumes.begin(); itrVolume!=vecVolumes.end(); itrVolume++)
	{
		jobject jVolume = env->NewObject(class_volume, volume_constructor);
		jstring jGuid = WCHARToJString(env, itrVolume->guid);
		jstring jDriveLetter = NULL;
		if(!(itrVolume->driverLetter.empty()))
		{
			jDriveLetter = WCHARToJString(env, itrVolume->driverLetter);
			env->CallVoidMethod(jVolume, volume_setDriveLetter, jDriveLetter);
		}
		env->CallVoidMethod(jVolume, volume_setVolumeID, jGuid);
		env->CallVoidMethod(jVolume, volume_setFlag, itrVolume->ulFlags);
		env->CallVoidMethod(jVolume, volume_setDynamic, itrVolume->isDynamic);
		

		env->CallBooleanMethod(vVolume, vector_add, jVolume);
	}

	return vVolume;
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetHostNetworkConfig
(JNIEnv *env, jclass clazz)
{
	HaUtility::ComEnvironment::Instance().Start();

	jclass class_vector = env->FindClass("java/util/Vector");
	jmethodID vector_contructor = env->GetMethodID(class_vector, "<init>", "()V");
	jmethodID vector_add = env->GetMethodID(class_vector, "add", "(Ljava/lang/Object;)Z");
	jobject jVector = env->NewObject(class_vector, vector_contructor);

	map<wstring, wstring> adapterList = GetHostAdapterList();
	map<wstring,wstring>::iterator itrAdapter;
	for ( itrAdapter = adapterList.begin(); itrAdapter != adapterList.end(); itrAdapter++ )
	{
		wstring strAdapterId = itrAdapter->first;
		jclass class_networkconfig = env->FindClass("com/ca/arcflash/webservice/jni/model/JHostNetworkConfig");
		jmethodID method_initJHostNetworkConfig = env->GetMethodID(class_networkconfig, "<init>", "()V");
		jmethodID method_setNetworkAdapterName = env->GetMethodID(class_networkconfig, "setNetworkAdapterName", "(Ljava/lang/String;)V");
		jmethodID method_setIsDHCPEnabled = env->GetMethodID(class_networkconfig, "setIsDHCPEnabled", "(Z)V");
		jmethodID method_addGateway = env->GetMethodID(class_networkconfig, "addGateway", "(Ljava/lang/String;)V");
		jmethodID method_setIsAutoDnsEnabled = env->GetMethodID(class_networkconfig, "setIsAutoDnsEnabled", "(Z)V");
		jmethodID method_addIP = env->GetMethodID(class_networkconfig, "addIP", "(Ljava/lang/String;)V");
		jmethodID method_addMask = env->GetMethodID(class_networkconfig, "addMask", "(Ljava/lang/String;)V");
		jmethodID method_addDnsServer = env->GetMethodID(class_networkconfig, "addDnsServer", "(Ljava/lang/String;)V");
		jmethodID method_setMacAddress = env->GetMethodID(class_networkconfig, "setMacAddress", "(Ljava/lang/String;)V");

		bool isDhcpEnabled = true;
		bool isAutoDns = true;
		vector<wstring> vecIp;
		vector<wstring> vecMask;
		vector<wstring> vecGateway;
		vector<wstring> vecDnsServer;
		wstring strMac;
		vector<wstring>::iterator itr;
		jobject jConfig = env->NewObject(class_networkconfig, method_initJHostNetworkConfig);

		GetHostIsDHCPEnabed(strAdapterId, isDhcpEnabled);
		GetHostDNSServerSetting(strAdapterId, isAutoDns, vecDnsServer);
		GetHostIPSetting(strAdapterId, vecIp, vecMask);
		GetHostGatewaySetting(strAdapterId, vecGateway);
		GetHostMac(strAdapterId, strMac);

		env->CallVoidMethod(jConfig, method_setNetworkAdapterName, WCHARToJString(env, itrAdapter->second));
		env->CallVoidMethod(jConfig, method_setIsDHCPEnabled, isDhcpEnabled);
		env->CallVoidMethod(jConfig, method_setIsAutoDnsEnabled, isAutoDns);
		env->CallVoidMethod(jConfig, method_setMacAddress, WCHARToJString(env, strMac));
		for (itr=vecIp.begin(); itr!=vecIp.end(); itr++)
		{
			wstring ip = *itr;
			env->CallVoidMethod(jConfig, method_addIP, WCHARToJString(env, ip));
		}
		for( itr = vecMask.begin(); itr != vecMask.end(); itr++ )
		{
			wstring mask = *itr;
			env->CallVoidMethod(jConfig, method_addMask, WCHARToJString(env, mask) );
		}
		for ( itr = vecGateway.begin(); itr != vecGateway.end(); itr++ )
		{
			wstring gateway = *itr;
			env->CallVoidMethod(jConfig, method_addGateway, WCHARToJString(env, gateway));
		}
		for( itr = vecDnsServer.begin(); itr != vecDnsServer.end(); itr++ )
		{
			wstring dnsServer = *itr;
			env->CallVoidMethod(jConfig, method_addDnsServer, WCHARToJString(env, dnsServer));
		}

		env->CallBooleanMethod(jVector, vector_add, jConfig);

	}
	HaUtility::ComEnvironment::Instance().Shutdown();
	return jVector;
}

JNIEXPORT jshort JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetHostProcessorArchitectural
(JNIEnv *env, jclass clazz)
{
	HaUtility::ComEnvironment::Instance().Start();
	WORD cpuArc = GetHostCPUArchitecture();
	HaUtility::ComEnvironment::Instance().Shutdown();
	return cpuArc;
}


/////Vmware JNI are followed
JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_ConvertVHD2VMDK
(JNIEnv *env, jclass clazz, jstring afGuid, jstring vhdFileName, jstring moref, jstring hostname,
jstring username, jstring password, jstring diskURL, jint vddkPort, jobject exParams, jint diskType, jstring snapMoref,
jstring jobID, jlong blockSize, jint jnBackupDescType, jstring NetConnUserName, jstring NetConnPwd, jobject errorCodeList)
{
	try{
		DWORD result = 1;
		wstring wstr_afGuid = JStringToWString(env,afGuid);
		wstring wstr_VhdFileName = JStringToWString(env,vhdFileName);
		wstring wstr_moref = JStringToWString(env,moref);
		wstring wstr_hostname = JStringToWString(env,hostname);
		wstring wstr_username = JStringToWString(env,username);
		wstring wstr_password = JStringToWString(env,password);
		wstring wstr_diskURL = JStringToWString(env,diskURL);
		wstring wstr_jobid = JStringToWString(env,jobID);
		wstring wstr_snapmoref = JStringToWString(env,snapMoref);
		unsigned long ulErrorCode = 0;
		unsigned long d2dErrorCode = 0;
		wstring wstr_netConnUserName = JStringToWString(env, NetConnUserName);
		wstring wstr_netConnPwd = JStringToWString(env, NetConnPwd);

		VMDK_CONNECT_MORE_PARAMS moreParams;

		ConvertVMDKConnParams(env, exParams, moreParams);

		VMwareCallback * callback = new VMwareCallback(env,wstr_afGuid);

		result = ConvertVHD2VMDK(wstr_afGuid, wstr_VhdFileName,wstr_moref,wstr_hostname,wstr_username,
			wstr_password, wstr_diskURL, vddkPort, moreParams,diskType, wstr_snapmoref,
			wstr_jobid, blockSize, jnBackupDescType, wstr_netConnUserName, wstr_netConnPwd, ulErrorCode, d2dErrorCode, callback);
		
		AddLongToList(env,clazz,(jlong)ulErrorCode,&errorCodeList);
		AddLongToList(env,clazz,(jlong)d2dErrorCode,&errorCodeList);

		if(callback != NULL)
		{
			delete callback;
		}

		return (jlong) result;
	}
	catch(...)
	{
		return (jlong) 1;
	}
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_D2D2VmdkSmartCopy
(JNIEnv *env , jclass clazz,jstring afGuid ,jstring d2dBeginFile,jstring d2dEndFile, jstring moref, jstring hostname, 
 jstring username, jstring password, jstring diskURL,jint vddkPort,jobject exParams, jint diskType,
 jstring snapMoref, jstring jobID, jlong blockSize, jint jnBackupDescType, jstring NetConnUserName, jstring NetConnPwd, jobject errorCodeList)
{
	try{
		DWORD result = 1;
		wstring wstr_afGuid = JStringToWString(env,afGuid);
		wstring wstr_D2DBeginFile = JStringToWString(env,d2dBeginFile);
		wstring wstr_D2DEndFile = JStringToWString(env,d2dEndFile);
		wstring wstr_moref = JStringToWString(env,moref);
		wstring wstr_hostname = JStringToWString(env,hostname);
		wstring wstr_username = JStringToWString(env,username);
		wstring wstr_password = JStringToWString(env,password);
		wstring wstr_diskURL = JStringToWString(env,diskURL);
		wstring wstr_jobid = JStringToWString(env,jobID);
		wstring wstr_snapmoref = JStringToWString(env,snapMoref);
		unsigned long ulErrorCode = 0;
		unsigned long d2dErrorCode = 0;
		wstring wstr_netConnUserName = JStringToWString(env, NetConnUserName);
		wstring wstr_netConnPwd = JStringToWString(env, NetConnPwd);

		VMwareCallback * callback = new VMwareCallback(env,wstr_afGuid);

		VMDK_CONNECT_MORE_PARAMS moreParams;

		ConvertVMDKConnParams(env, exParams, moreParams);

		result = FacadeD2D2VmdkSmartCopy(wstr_afGuid, wstr_D2DBeginFile,wstr_D2DEndFile,wstr_moref,wstr_hostname,wstr_username,
										wstr_password, wstr_diskURL, vddkPort, moreParams,diskType, wstr_snapmoref, wstr_jobid,
										blockSize, jnBackupDescType, wstr_netConnUserName, wstr_netConnPwd, ulErrorCode, d2dErrorCode, callback);

		AddLongToList(env,clazz,(jlong)ulErrorCode,&errorCodeList);
		AddLongToList(env,clazz,(jlong)d2dErrorCode,&errorCodeList);

		if(callback != NULL)
		{
			delete callback;
		}

		return (jlong) result;
	}
	catch(...)
	{
		return (jlong) 1;
	}
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_UpdateDiskSigViaNBD
(JNIEnv *env , jclass clazz,jstring afGuid, jstring vhdFileName, jstring moref, jstring hostname, 
 jstring username, jstring password, jstring diskURL,jint vddkPort,jobject exParams, jint diskType,
 jstring snapMoref, jstring jobID, jint jnBackupDescType, jstring NetConnUserName, jstring NetConnPwd)
{
	try{
		DWORD result = 1;
		wstring wstr_VhdFileName = JStringToWString(env,vhdFileName);
		wstring wstr_moref = JStringToWString(env,moref);
		wstring wstr_hostname = JStringToWString(env,hostname);
		wstring wstr_username = JStringToWString(env,username);
		wstring wstr_password = JStringToWString(env,password);
		wstring wstr_diskURL = JStringToWString(env,diskURL);
		wstring wstr_jobid = JStringToWString(env,jobID);
		wstring wstr_snapmoref = JStringToWString(env,snapMoref);

		wstring wstr_netConnUserName = JStringToWString(env, NetConnUserName);
		wstring wstr_netConnPwd = JStringToWString(env, NetConnPwd);
		VMDK_CONNECT_MORE_PARAMS moreParams;

		ConvertVMDKConnParams(env, exParams, moreParams);

		result = FacadeUpdateDiskSigViaNBD(wstr_VhdFileName,wstr_moref,wstr_hostname,wstr_username,
			wstr_password, wstr_diskURL, vddkPort, moreParams, diskType, wstr_snapmoref, wstr_jobid, jnBackupDescType, wstr_netConnUserName, wstr_netConnPwd, NULL);

		return (jlong) result;
	}
	catch(...)
	{
		return (jlong) 1;
	}
}

//modifede by zhepa02 at 2015-04-09 to support vddk 6
//add the thumbPrint parameter
JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_GetVMDKSignature
(JNIEnv *env, jclass cls, jstring afguid, jstring esx, jstring user, jstring password, jstring morefId, jint port, jobject exParams, jstring snapMoref, jstring vmdkUrl, jstring JobID)
{
	wstring wstrAfguid = JStringToWString(env, afguid);
	wstring wstrEsx = JStringToWString(env, esx);
	wstring wstrUser = JStringToWString(env, user);
	wstring wstrPassword = JStringToWString(env, password);
	wstring wstrMorefId = JStringToWString(env, morefId);
	UINT32 uPort = port;
	wstring wstrUrl = JStringToWString(env, vmdkUrl);
	wstring wstrDiskGUID;
	wstring wstrSnapMoref = JStringToWString(env, snapMoref);
	wstring wstrJobID = JStringToWString(env, JobID);

	VMDK_CONNECT_MORE_PARAMS moreParams;

	ConvertVMDKConnParams(env, exParams, moreParams);

	//add by zhepa02 at 2015-04-09 to support vddk 6
	//wstring wstrThumbprint = JStringToWString(env, thumbprint);
	
	DWORD result = 0;
	//modified by zhepa02 at 2015-04-09 to support vddk 6
	result = FacadeGetVMDKSignature(wstrAfguid, wstrEsx, wstrUser, wstrPassword, wstrMorefId, uPort, moreParams, wstrSnapMoref, wstrUrl, wstrDiskGUID, wstrJobID);
	jstring jstrDiskGuid;
	if(result == 13)
	{
		wstring werror;
		werror = L"SAN NOT Supported";
		jstrDiskGuid = WCHARToJString(env,werror);
	}
	else
		jstrDiskGuid = WCHARToJString(env,wstrDiskGUID);

	return jstrDiskGuid;
}


JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_SetVMDKGeometry
(JNIEnv *env, jclass cls, jstring esx, jstring user, jstring password, jstring morefId, jint port, jobject exParams, jstring vmdkUrl, jlong volOffset, jstring JobID, jstring afguid)
{
	wstring wstrEsx = JStringToWString(env, esx);
	wstring wstrUser = JStringToWString(env, user);
	wstring wstrPassword = JStringToWString(env, password);
	wstring wstrMorefId = JStringToWString(env, morefId);
	UINT32 uPort = port;
	wstring wstrUrl = JStringToWString(env, vmdkUrl);
	wstring wstrDiskGUID;
	wstring wstrJobID = JStringToWString(env, JobID);
	wstring wstrAfguid = JStringToWString(env, afguid);
	long lvolOffset = volOffset;

	//add by zhepa02 at 2015-04-09 to support vddk 6
	//wstring wstrThumbprint = JStringToWString(env, thumbprint);

	VMDK_CONNECT_MORE_PARAMS moreParams;

	ConvertVMDKConnParams(env, exParams, moreParams);

	DWORD result = 0;
	result = FacadeSetVMDKGeometry(wstrEsx, wstrUser, wstrPassword, wstrMorefId, uPort, moreParams, wstrUrl, lvolOffset, wstrJobID, wstrAfguid);
	return (jlong)result;
}

//modified by zhepa02 at 2015-04-14 ,add the strThumbprint parameter.
JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_DoVMWareDriverInjection
  (JNIEnv *env, jclass clazz, jstring esxServer, jstring username, jstring password, jstring morefId, 
	jint adminPort, jobject exParams, jobject vmdkUrls,jobject volInfo,jstring hostname,jstring failovermode,
	jstring key,jstring value,jstring jobID, jstring afguid, jboolean isUEFI, jstring drzFilePath)
{
	try{
		wstring wstrEsx = JStringToWString(env, esxServer);	
		wstring wstrUsername = JStringToWString(env, username);
		wstring wstrPassword = JStringToWString(env, password);
		wstring wstrMorefId = JStringToWString(env, morefId);
		//wstring wstrBootVolume = JStringToWString(env, bootVolume);
		vector<wstring> vecVmdkUrls;
		JStrListToCVector(env,clazz,vmdkUrls,vecVmdkUrls);
		wstring wstrHostname = JStringToWString(env, hostname);
		wstring wstrFailoverMode = JStringToWString(env, failovermode);
		wstring wstrKey = JStringToWString(env, key);
		wstring wstrValue = JStringToWString(env, value);
		wstring wstrJOBId = JStringToWString(env, jobID);
		wstring wstrAfguid = JStringToWString(env, afguid);
		wstring wstrDrzFilePath = JStringToWString(env, drzFilePath);
		/*wstring wstrThumbprint = JStringToWString(env, thumbprint);*/

		VMDK_CONNECT_MORE_PARAMS moreParams;
		VMwareVolumeInfo volumeInfo = { 0 };

		ConvertVMDKConnParams(env, exParams, moreParams);
		ConvertVMwareVolumeInfo(env, volInfo, volumeInfo);

		DWORD result = 0;
		result = DoVMWareDriverInjection(wstrEsx, wstrUsername, wstrPassword, wstrMorefId, adminPort, moreParams, vecVmdkUrls,
			volumeInfo, wstrHostname, wstrFailoverMode, wstrKey, wstrValue, wstrJOBId, wstrAfguid, isUEFI, wstrDrzFilePath);
	
		return (jlong)result;
	}catch(...){
		return 1;
	}

}



JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_GetGuestID
  (JNIEnv *env, jclass clazz)
{
	try{
		wstring wstrGuestID;
		FacadeGetGuestID(wstrGuestID);
		jstring jstrGuestID;
		jstrGuestID = WCHARToJString(env,wstrGuestID);
		return jstrGuestID;
		
	}catch(...){
		return NULL;
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_InstallVMwareTools
  (JNIEnv *env, jclass clazz)
{
	try{
		FacadeInstallVMwareTools();
	}catch(...){
		
	}
}

JNIEXPORT jint JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_GetVDDKVersion
(JNIEnv *env, jclass clazz, jobject obj)
{
	DWORD dwMajorVersion=0, dwMinorVersion=0, dwSubVersion=0;
	DWORD dwResult=0;

	dwResult= FacadeGetVDDKVersion(dwMajorVersion, dwMinorVersion, dwSubVersion);
	jclass    class_FileVersion=env->GetObjectClass(obj);
	jmethodID method_setMajorVersion=env->GetMethodID(class_FileVersion,"setMajorVersion","(I)V");
	jmethodID method_setMinorVersion=env->GetMethodID(class_FileVersion,"setMinorVersion","(I)V");
	jmethodID method_setSubVersion =env->GetMethodID(class_FileVersion,"setSubVersion","(I)V");

	if((method_setMajorVersion==NULL)||(method_setMinorVersion==NULL)||
		(method_setSubVersion==NULL))
	{
		return 2;
	}

	env->CallVoidMethod(obj,method_setMajorVersion,(jint)dwMajorVersion);
	env->CallVoidMethod(obj,method_setMinorVersion,(jint)dwMinorVersion);
	env->CallVoidMethod(obj,method_setSubVersion,(jint)dwSubVersion);

	return (jint)dwResult;

}
JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_HALockD2DSessions
(JNIEnv *env, jclass cls, jstring sessionRoot, jint sessionBegin, jint sessionEnd, jboolean isForRemoteVCM, jobject resultCodeList)
{
	wstring wstrSessionRoot = JStringToWString(env,sessionRoot);
	long lSessionStart = sessionBegin;
	long lSessionEnd = sessionEnd;

	HANDLE lockHandle = NULL;
	DWORD result = 0;
	result = FacadeHA_LockD2DSessions(wstrSessionRoot.c_str(),lSessionStart,lSessionEnd,&lockHandle, (BOOL)isForRemoteVCM);
	AddIntToIntegerList( env, resultCodeList, result );
	return (jlong)lockHandle;
}
JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_HAUnlockD2DSessions
(JNIEnv *env, jclass cls, jlong handle)
{
	HANDLE lockHandle = NULL;
	DWORD result = 0;

	lockHandle = (HANDLE)handle;
	result = FacadeHA_UnlockD2DSessions(lockHandle);
	
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_HAUnlockCTF2
(JNIEnv *env, jclass cls, jlong handle)
{
	HANDLE lockHandle = NULL;
	DWORD result = 0;

	lockHandle = (HANDLE)handle;
	result = FacadeHA_UnlockCTF2(lockHandle);

	return (jlong)result;

}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetOfflinecopySize(JNIEnv *env, jclass klass, jstring rootPath)
{
	wchar_t* pDest = JStringToWCHAR(env, rootPath);
	__int64 llTotalSize;
	int dwRet = GetOffcopySize_HyperV(pDest, llTotalSize);

	if(pDest != NULL)
		free(pDest);

	if(dwRet != 0){
		ThrowWSJNIException(env, klass, env->NewStringUTF("Error occurs in GetOffcopySize."), dwRet);
		return -1;
	}

	return llTotalSize;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AdjustVolumeBootCodeForHyperV
  (JNIEnv *env, jclass, jstring vmuuid, jstring snapshotGuid)
{
	wchar_t* wstrVMuuid = JStringToWCHAR(env, vmuuid);
	wchar_t* wstrSnapshotGuid = JStringToWCHAR(env, snapshotGuid);

	int result = AdjustVolumeBootCode_HyperV(wstrVMuuid,wstrSnapshotGuid);

	if(wstrVMuuid != NULL)
		free(wstrVMuuid); //baide02 for mem leak

	if(wstrSnapshotGuid != NULL)
		free(wstrSnapshotGuid); //baide02 for mem leak

	return (jint)result;

}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CancelReplicationForHyperV(JNIEnv *env, jclass klass, jstring jobId)
{
	wchar_t * jobIdStr = JStringToWCHAR(env, jobId);
	
	Proc_HADT_CancelJob func = (Proc_HADT_CancelJob)DynGetProcAddress(L"HATransClientProxy.dll", "HADT_CancelJob");
	int ret = func(jobIdStr);

	if(jobIdStr != NULL)
		free(jobIdStr);	

	if(ret != 0)
		ThrowWSJNIException(env, klass, env->NewStringUTF("Error occurs in CancelReplicationForHyperV."), ret);

	return ret;	
	
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_StopHAServerProxy(JNIEnv *env, jclass klass)
{
	Proc_HADT_S_StopServer func = (Proc_HADT_S_StopServer)DynGetProcAddress(L"HATransServerProxy.dll", "HADT_S_StopServer");
	int ret = func();

	if(ret != 0)
		ThrowWSJNIException(env, klass, env->NewStringUTF("Error occurs in StopHAServerProxy."), ret);

	return ret;		
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CancelReplicationForVMware(JNIEnv *env, jclass klass, jstring jobId)
{
	try{
		wchar_t* jobIDStr = JStringToWCHAR(env, jobId);
		BOOL ret = CancelConversionVMware(jobIDStr);

		if(jobIDStr != NULL)
			free(jobIDStr);		

		return ret;
	}catch(...){
		
	}
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsHyperVRoleInstalled(JNIEnv *env, jclass klass)
{
	HANDLE hyperv_handle = OpenHyperVOperation(L"", L"", L"");
	bool isInstalled = hyperv_handle ? true : false;
	DelIHyperVOperation((IHypervOperation*)hyperv_handle);
	return isInstalled;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_OnlineDisks(JNIEnv *env, jclass klass)
{
	DWORD ret = 0;
	ret = AFOnlineDisks();
	return ret;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GenerateAdrconfigure
(JNIEnv *env, jclass klazz,jstring dest) 
{
	DWORD ret = NULL;
	wchar_t* wstrDest = NULL;
	wstrDest = JStringToWCHAR(env, dest);
	ret = FlashDRCollectInfoForHA(wstrDest);

	if(wstrDest != NULL)
		free(wstrDest);		

	return ret;

}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GenerateIVMAdrconfigure
(JNIEnv *env, jclass klazz,jstring dest,jstring vmuuid,jstring vmname) 
{
	wchar_t* wstrVMname = JStringToWCHAR(env, vmname);
	wchar_t* wstrVMuuid = JStringToWCHAR(env, vmuuid);
	wchar_t* wstrPath = JStringToWCHAR(env, dest);
	int nSize = 0;

	int ret = GetIVMAdrConfigLocalCopyPathForSnapNow_HyperV(wstrVMname, wstrVMuuid, wstrPath, nSize);

	if (wstrVMname != NULL)
		free((wstrVMname));

	if (wstrVMuuid != NULL)
		free(wstrVMuuid);

	if(ret != 0){
		ThrowWSJNIException(env, klazz, env->NewStringUTF("Error occurs in GenerateAdrInfoC."), ret);		
	}
	return ret;

}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GenerateAdrInfoC
	(JNIEnv *env, jclass klazz) 
{
	DWORD ret = NULL;
	wstring wstrDest;
	ret = FlashDRCollectBCDForHA(wstrDest); 
	if(ret != 0){
		ThrowWSJNIException(env, klazz, env->NewStringUTF("Error occurs in GenerateAdrInfoC."), ret);		
	}

	jstring strPath = WCHARToJString(env,wstrDest.c_str());
	return strPath;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetDrInfoLocalCopyPath
(JNIEnv *env, jclass clazz, jstring vmname, jstring vmuuid)
{
	wchar_t wszPath[255];
	memset(wszPath,0,sizeof(wszPath));
	
	wchar_t* wstrVMname = JStringToWCHAR(env,vmname);
	wchar_t* wstrVMuuid = JStringToWCHAR(env,vmuuid);
	int nSize = _countof(wszPath);

	int ret = GetDrInfoLocalCopyPathForSnapNow_HyperV(wstrVMname, wstrVMuuid,wszPath, nSize);

	if(wstrVMname != NULL)
		free((wstrVMname));

	if(wstrVMuuid != NULL)
		free(wstrVMuuid);

	if(ret != 0){
		ThrowWSJNIException(env, clazz, env->NewStringUTF("Error occurs in CancelReplicationForHyperV."), ret);		
	}

	jstring strPath = WCHARToJString(env,wszPath);

	return strPath;

}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetAdrInfoCLocalCopyPath
	(JNIEnv *env, jclass clazz, jstring vmname, jstring vmuuid)
{
	wchar_t wszPath[255];
	memset(wszPath,0,sizeof(wszPath));

	wchar_t* wstrVMname = JStringToWCHAR(env,vmname);
	wchar_t* wstrVMuuid = JStringToWCHAR(env,vmuuid);
	int nSize = _countof(wszPath);

	int ret = GetAdrInfoCLocalCopyPathForSnapNow_HyperV(wstrVMname, wstrVMuuid,wszPath, nSize);

	if(wstrVMname != NULL)
		free((wstrVMname));

	if(wstrVMuuid != NULL)
		free(wstrVMuuid);

	if(ret != 0){
		ThrowWSJNIException(env, clazz, env->NewStringUTF("Error occurs in CancelReplicationForHyperV."), ret);		
	}

	jstring strPath = WCHARToJString(env,wszPath);

	return strPath;

}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetDestSubRoot
(JNIEnv *env, jclass clazz, jobject javaParam)
{

	ST_HASRV_INFO* pstSrvInfo = new ST_HASRV_INFO();
	wstring wstrProductNode;
	wstring pwszLastDesRoot;
	HA_SRC_ITEM* pwszSrcItem = new HA_SRC_ITEM();
    ZeroMemory(pwszSrcItem, sizeof(HA_SRC_ITEM));
	wchar_t** ppwszDesRootList = NULL;
	int destCount = 0;
	
	wchar_t wszPath[255];
	memset(wszPath,0,sizeof(wszPath));
	int nLenInWord = _countof(wszPath);

	DWORD ret = ConstructGetSubRootParams(env,javaParam,pstSrvInfo,
				wstrProductNode,pwszLastDesRoot,pwszSrcItem,ppwszDesRootList,destCount);

	ret = Native_HADT_GetDestSubRoot(pstSrvInfo,wstrProductNode.c_str(),pwszSrcItem,pwszLastDesRoot.c_str(),
									 destCount,(const wchar_t**)ppwszDesRootList,wszPath,nLenInWord);


	if(pstSrvInfo != NULL)
	{
        if (pstSrvInfo->pwszName)
		    free((void*)pstSrvInfo->pwszName);
        if (pstSrvInfo->pwszPort)
		    free((void*)pstSrvInfo->pwszPort);
        if (pstSrvInfo->pwszUser)
		    free((void*)pstSrvInfo->pwszUser);
        if (pstSrvInfo->pwszPwd)
		    free((void*)pstSrvInfo->pwszPwd);
        delete pstSrvInfo;
        pstSrvInfo = NULL;
	}

	if(pwszSrcItem != NULL)
	{
        _INNERFREE(pwszSrcItem->pwszPath);
        _INNERFREE(pwszSrcItem->pwszSFUsername);
        _INNERFREE(pwszSrcItem->pwszSFPassword);
		delete(pwszSrcItem);
	}
	
	if(ppwszDesRootList != NULL)
	{
        for (int i = 0; i<destCount; i++)
        {
            _INNERFREE(ppwszDesRootList[i]);
        }
		delete[](ppwszDesRootList);
	}

	if(ret != 0){
		ThrowWSJNIException(env, clazz, env->NewStringUTF("Error occurs in GetDestSubRoot."), ret);		
	}

	jstring strPath = WCHARToJString(env,wszPath);

	return strPath;

}

// chefr03, SMART_COPY_BITMAP
//JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CreateSessionBitmap(JNIEnv *env, jclass clazz, jstring sessionDest)
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CreateSessionBitmap
  (JNIEnv *env, jclass, jstring srcSessionDest, jstring destSessionDest, jint jnBackupDescType)
{
	HMODULE hMountHACommondll = NULL; 
	UINT ret = 0;

	wchar_t* wszSrcSessionDest	= JStringToWCHAR(env, srcSessionDest);
	wchar_t* wszDestSessionDest	= JStringToWCHAR(env, destSessionDest);

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if (hMountHACommondll == NULL)	{
		return -1;
	}

	PFN_CreateSessionBitmap pfnCreateSessionBitmap = NULL;

	pfnCreateSessionBitmap = (PFN_CreateSessionBitmap)GetProcAddress(hMountHACommondll, "CreateSessionBitmap");
	ret = (*pfnCreateSessionBitmap)(wszSrcSessionDest, jnBackupDescType, wszDestSessionDest);

	if (hMountHACommondll != NULL) {
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}

    _INNERFREE(wszSrcSessionDest);
	_INNERFREE(wszDestSessionDest);

	return ret;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DeleteSessionBitmap(JNIEnv *env, jclass clazz, jstring sessionDest, jstring sessionName)
{
	HMODULE hMountHACommondll = NULL; 
	UINT ret = 0;

	wchar_t* wszSessionDest		= JStringToWCHAR(env, sessionDest);
	wchar_t* wszSessionName		= JStringToWCHAR(env, sessionName);

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if (hMountHACommondll == NULL)	{
		return -1;
	}

	PFN_DeleteSessionBitmap pfnDeleteSessionBitmap = NULL;

	pfnDeleteSessionBitmap = (PFN_DeleteSessionBitmap)GetProcAddress(hMountHACommondll, "DeleteSessionBitmap");
	ret = (*pfnDeleteSessionBitmap)(wszSessionDest, wszSessionName);

	if (hMountHACommondll != NULL) {
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}

    _INNERFREE(wszSessionDest);
    _INNERFREE(wszSessionName);

	return ret;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetSessionBitmapList(JNIEnv *env, jclass clazz, jstring sessionDest, jobject bitmapList)
{
	HMODULE hMountHACommondll = NULL; 
	UINT ret = 0;

	wchar_t* wszSessionDest		= JStringToWCHAR(env, sessionDest);
	vector<wstring>		vSessionBitmapList;

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if (hMountHACommondll == NULL)	{
		return -1;
	}

	PFN_GetSessionBitmapList pfnGetSessionBitmapList = NULL;

	pfnGetSessionBitmapList = (PFN_GetSessionBitmapList)GetProcAddress(hMountHACommondll, "GetSessionBitmapList");
	ret = (*pfnGetSessionBitmapList)(wszSessionDest, vSessionBitmapList);
	if (hMountHACommondll != NULL) {
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}

	if (ret == 0) {
		// Set vSessionBitmapList to bitmapList
		AddVecString2List(env, &bitmapList, vSessionBitmapList);
	}
    _INNERFREE(wszSessionDest);

	return ret;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DetectAndRemoveObsolteBitmap(JNIEnv *env, jclass clazz, jstring sessionDest, jstring beginSession, jstring endSession)
{
	HMODULE hMountHACommondll = NULL; 
	UINT ret = 0;

	wchar_t* wszSessionDest = JStringToWCHAR(env, sessionDest);
	wchar_t* wszBeginSession = JStringToWCHAR(env, beginSession);
	wchar_t* wszEndSession = JStringToWCHAR(env, endSession);

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if (hMountHACommondll == NULL)	
	{
		return -1;
	}

	PFN_DetectAndRemoveObsolteBitmap pfnDetectAndRemoveObsolteBitmap = NULL;
	pfnDetectAndRemoveObsolteBitmap = (PFN_DetectAndRemoveObsolteBitmap)GetProcAddress(hMountHACommondll, "DetectAndRemoveObsolteBitmap");
	if (pfnDetectAndRemoveObsolteBitmap == NULL)
	{
		return -1;
	}

	ret = (*pfnDetectAndRemoveObsolteBitmap)(wszSessionDest, wszBeginSession, wszEndSession);
	if (hMountHACommondll != NULL) 
	{
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}
	_INNERFREE(wszSessionDest);
	_INNERFREE(wszBeginSession);
	_INNERFREE(wszEndSession);
	return ret;
}
// chefr03, SMART_COPY_BITMAP

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetIpAddressFromDns(JNIEnv * env, jclass clazz, jstring hostname, jobject ipsList)
{
	wchar_t* pHostName = JStringToWCHAR(env, hostname);

	std::vector<std::wstring> ipVec;
	DWORD dwRet = GetIpAddressFromDns(pHostName, ipVec);

	if(dwRet != 0)
		return dwRet;

	jclass list_class = env->GetObjectClass(ipsList);
	jmethodID addMethod = env->GetMethodID(list_class, "add", "(Ljava/lang/Object;)Z");

	for(vector<std::wstring>::iterator ip = ipVec.begin(); ip != ipVec.end(); ip++)
	{
		jstring ip_JStr = WCHARToJString(env, (wchar_t*)ip->c_str());
		env->CallBooleanMethod(ipsList, addMethod, ip_JStr);
		if(ip_JStr != NULL)
			env->DeleteLocalRef(ip_JStr);
	}

	if(list_class != NULL)
		env->DeleteLocalRef(list_class);

	if(pHostName != NULL)
		free(pHostName);
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFValidateSessPassword
  (JNIEnv *env, jclass clz, jstring password, jstring destination, jlong sessionNum)
{
	wchar_t* pPassword = JStringToWCHAR(env, password);
	wchar_t* pDestination = JStringToWCHAR(env, destination);
	BOOL isValid = AFValidateSessPassword(pPassword, pDestination, sessionNum);
    _INNERFREE(pPassword);
    _INNERFREE(pDestination);
	
	return (jboolean) isValid;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFValidateSessPasswordByHash
  (JNIEnv *env, jclass clz, jstring password, jlong lpwdlen, jstring hashvalue, jlong lhashlen)
{
	wchar_t* pPassword = JStringToWCHAR(env, password);
	wchar_t* pHashvalue = JStringToWCHAR(env, hashvalue);
	BOOL isValid = AFValidateSessPasswordByHash(pPassword, lpwdlen, pHashvalue, lhashlen);
    _INNERFREE(pPassword);
    _INNERFREE(pHashvalue);
	
	return (jboolean) isValid;
}


/* !!! To Delete
#define MAX_MESSAGE_SIZE 16384
#define D2D_PM_COMMUNICATOR_DLL L"D2DPMCommunicator.dll"
#define D2D_PM_SUB_DIRECTORY_NAME L"Update Manager"
#define EDGE_PM_COMMUNICATOR_DLL L"EdgePMCommunicator.dll"
#define EDGE_PM_SUB_DIRECTORY_NAME L"Update Manager\\ArcApp"

enum {TYPE_D2D = 0, TYPE_EDGE = 1};

bool LoadD2DModuleAndGetProc(int type, const PCHAR procName, HMODULE &hModule, FARPROC &funcPtr)
{
	if ( hModule == NULL )
	{
		WCHAR modulePath[MAX_PATH] = {0};
		if ( wcslen(g_homePath) > 0 )
		{
			wcsncpy_s(modulePath, _countof(modulePath), g_homePath, _TRUNCATE);
			if(type == TYPE_D2D){
				wcsncat_s(modulePath, _countof(modulePath), D2D_PM_SUB_DIRECTORY_NAME, _TRUNCATE);
				wcsncat_s(modulePath, _countof(modulePath), L"\\", _TRUNCATE);
				wcsncat_s(modulePath, _countof(modulePath), D2D_PM_COMMUNICATOR_DLL, _TRUNCATE);
			}
			else if(type == TYPE_EDGE){
				wcsncat_s(modulePath, _countof(modulePath), EDGE_PM_SUB_DIRECTORY_NAME, _TRUNCATE);
				wcsncat_s(modulePath, _countof(modulePath), L"\\", _TRUNCATE);
				wcsncat_s(modulePath, _countof(modulePath), EDGE_PM_COMMUNICATOR_DLL, _TRUNCATE);
			}
		}
		else
		{	if(type == TYPE_D2D){
				wcsncpy_s(modulePath, _countof(modulePath), D2D_PM_COMMUNICATOR_DLL, _TRUNCATE);
			}else if(type == TYPE_EDGE){
				wcsncpy_s(modulePath, _countof(modulePath), EDGE_PM_COMMUNICATOR_DLL, _TRUNCATE);
			}
		}

	    hModule = LoadLibrary(modulePath);
		if ( hModule == NULL )
		{
			LogFile(_T("[LoadD2DModuleAndGetProc]Failed to load library."));
		    return false;
		}
	}

	funcPtr = GetProcAddress(hModule, procName);
	if ( funcPtr == NULL )
	{
	    LogFile(_T("[LoadD2DModuleAndGetProc]Failed to get procedure."));
		return false;
	}

	return true;
}

void FreeD2DModule(HMODULE hModule)
{
    if ( hModule != NULL )
	{
	    FreeLibrary(hModule);
	}
}
*

/*
* ----------------------------------------------------------------------------------------------
*                                   {{{ Update Manager Start
* ----------------------------------------------------------------------------------------------
*/
enum {TYPE_D2D = 0, TYPE_EDGE = 1};

// !!! To Delete
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CreateFile(
	JNIEnv *env, 
    jclass className, 
    jstring lpFileName, 
    jint dwDesiredAccess, 
    jint dwShareMode, 
    jint lpSecurityAttributes, 
    jint dwCreationDisposition, 
    jint dwFlagsAndAttributes, 
    jint hTemplateFile)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_CreateFileEx(env, 
     className, 
     lpFileName, 
     dwDesiredAccess, 
     dwShareMode, 
     lpSecurityAttributes, 
     dwCreationDisposition, 
     dwFlagsAndAttributes, 
     hTemplateFile,TYPE_D2D);
}

// !!! To Delete
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CreateFileEx
(	JNIEnv *env, 
    jclass className, 
    jstring lpFileName, 
    jint dwDesiredAccess, 
    jint dwShareMode, 
    jint lpSecurityAttributes, 
    jint dwCreationDisposition, 
    jint dwFlagsAndAttributes, 
    jint hTemplateFile,
	jint type)
{

	return -1;
/*
	HMODULE hModule = NULL;
	PMCreateFile pMCreateFile = NULL;

	bool load = LoadD2DModuleAndGetProc(type, "PMCreateFile", hModule, (FARPROC&)pMCreateFile);

	if ( !load )
	{
		FreeD2DModule(hModule);
	    return -1;
	}

	HANDLE hPipe = NULL;
	wchar_t* pNamedPipeName = JStringToWCHAR(env, lpFileName);
	hPipe = pMCreateFile(pNamedPipeName, GENERIC_WRITE|GENERIC_READ, FILE_SHARE_WRITE|FILE_SHARE_READ, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
	FreeD2DModule(hModule);

	if (INVALID_HANDLE_VALUE == hPipe)
	{
		return -1;
	}
	return (jint)hPipe;
*/
}

// !!! To Delete
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_ReadFile
	(JNIEnv *env,jclass className,jint hNamedPipe)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_ReadFileEx(env,className,hNamedPipe,TYPE_D2D);
}

// !!! To Delete
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_ReadFileEx
	(JNIEnv *env,jclass className,jint hNamedPipe,jint type)
{
	return NULL;
/*
	HMODULE hModule = NULL;
	PMReadFile pMReadFile = NULL;

	bool load = LoadD2DModuleAndGetProc(type, "PMReadFile", hModule, (FARPROC&)pMReadFile);

	if ( !load )
	{
		FreeD2DModule(hModule);
	    return WCHARToJString(env, L"");
	}

	WCHAR szResponseBuff[MAX_MESSAGE_SIZE] = {0};
	pMReadFile((HANDLE)hNamedPipe, szResponseBuff, _countof(szResponseBuff));
	FreeD2DModule(hModule);

	jstring jResponseBuff = WCHARToJString(env, szResponseBuff);
	return jResponseBuff;
*/
}

// !!! To Delete
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsPatchManagerRunning
	(JNIEnv *env,jclass className, jstring runningMutexName)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_IsPatchManagerRunningEx(env,className, runningMutexName,TYPE_D2D);
}

// !!! To Delete
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsPatchManagerRunningEx
	(JNIEnv *env,jclass className, jstring runningMutexName, jint type)
{	
	return AFIsUpdateServiceRunning();
}

// !!! To Delete
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_WriteFile
	( JNIEnv *env, jclass className, jint hNamedPipe, jstring in_MessageXML, jint nNumberOfBytesToWrite)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_WriteFileEx(env, className,  hNamedPipe, in_MessageXML, nNumberOfBytesToWrite,TYPE_D2D);
}

// !!! To Delete
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_WriteFileEx
  (JNIEnv *env, jclass className, jint hNamedPipe, jstring in_MessageXML, jint nNumberOfBytesToWrite, jint type)
{
	return 0;
/*
	HMODULE hModule = NULL;
	PMWriteFile pMWriteFile = NULL;

	bool load = LoadD2DModuleAndGetProc(type, "PMWriteFile", hModule, (FARPROC&)pMWriteFile);

	if ( !load )
	{
		FreeD2DModule(hModule);
	    return GetLastError();
	}

	wchar_t* pMessageXML = JStringToWCHAR(env, in_MessageXML);

	int ret = pMWriteFile((HANDLE)hNamedPipe, pMessageXML, (int)wcslen(pMessageXML)+10);
	FreeD2DModule(hModule);
	return ret;
*/
}

// !!! To Delete
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CloseFile
  (JNIEnv *env,jclass className, jint FileHandle)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_CloseFileEx(env, className, FileHandle,TYPE_D2D);
}

// !!! To Delete
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CloseFileEx
	(JNIEnv *env,jclass className, jint FileHandle, jint type)
{
	return FALSE;
/*
	HMODULE hModule = NULL;
	PMCloseFile pMCloseFile = NULL;

	bool load = LoadD2DModuleAndGetProc(type, "PMCloseFile", hModule, (FARPROC&)pMCloseFile);

	if ( !load )
	{
		FreeD2DModule(hModule);
	    return FALSE;
	}

	BOOL ret = pMCloseFile((HANDLE)FileHandle);
	FreeD2DModule(hModule);
	return ret;
*/
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_testDownloadServerConnection
	(JNIEnv *env, jclass className,jstring strDownloadURI, jint iServerType, 
	jstring strDownloadServer, jstring strDownloadServerPort, 
	jstring strProxyServerName, jstring strProxyPort, 
	jstring strProxyUserName, jstring strProxyPassword)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_testDownloadServerConnectionEx
		(env, className,strDownloadURI, iServerType, 
		strDownloadServer, strDownloadServerPort, 
		strProxyServerName, strProxyPort, 
		strProxyUserName, strProxyPassword,TYPE_D2D);
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_testDownloadServerConnectionEx
	(JNIEnv *env, jclass className,jstring strDownloadURI, jint iServerType, 
	jstring strDownloadServer, jstring strDownloadServerPort, 
	jstring strProxyServerName, jstring strProxyPort, 
	jstring strProxyUserName, jstring strProxyPassword, jint type)
{
	return NULL;

}

// !!! To Delete
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFRunPatchJob
  (JNIEnv *env,jclass className,jstring spatchURL)
{
	return 0;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsPatchManagerBusy
	(JNIEnv *env,jclass className, jstring busyMutexName)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_IsPatchManagerBusyEx(env,className, busyMutexName,TYPE_D2D);
}


JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsPatchManagerBusyEx
	(JNIEnv *env,jclass className, jstring busyMutexName, jint type)
{
	return AFIsUpdateBusy(type);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_checkUpdate
	( JNIEnv *env, jclass className )
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_checkUpdateEx( env, className, TYPE_D2D );
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_checkUpdateEx
	( JNIEnv *env, jclass className, jint type )
{
	return AFCheckUpdate(type);
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getUpdateStatusFile
  (JNIEnv *env, jclass className)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_getUpdateStatusFileEx( env, className, TYPE_D2D );
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getUpdateStatusFileEx
  (JNIEnv *env, jclass className, jint type)
{
	wstring strFile;
	AFGetUpdateStatusFile(type, strFile);
	jstring retValue = WCHARToJString( env, strFile.c_str() );
	return retValue;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getUpdateSettingsFile
  (JNIEnv *env, jclass className)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_getUpdateSettingsFileEx( env, className, TYPE_D2D );
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getUpdateSettingsFileEx
  (JNIEnv *env, jclass className, jint type)
{
	wstring strFile;
	AFGetUpdateSettingsFile(type, strFile);
	jstring retValue = WCHARToJString( env, strFile.c_str() );
	return retValue;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getUpdateErrorMessage
  (JNIEnv *env, jclass className, jint errorCode )
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_getUpdateErrorMessageEx( env, className, errorCode, TYPE_D2D );
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getUpdateErrorMessageEx
  (JNIEnv *env, jclass className, jint errorCode, jint type)
{
	wstring strErrorMessage;
	AFGetUpdateErrorMessage( type, errorCode, strErrorMessage );
	jstring retValue = WCHARToJString( env, strErrorMessage.c_str() );
	return retValue;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_testUpdateServerConnection
  (JNIEnv *env, jclass className, jint svrType, jstring strServer, jstring strServerPort, jstring strProxyServerName, jstring strProxyServerPort, jstring strProxyUsername, jstring strProxyPassword )
{
	logObj.LogW(LL_INF, 0, L"wawawa will call testUpdateServerConnectionEx");
	return Java_com_ca_arcflash_webservice_jni_WSJNI_testUpdateServerConnectionEx( env, className, svrType, strServer, strServerPort, strProxyServerName, strProxyServerPort, strProxyUsername, strProxyPassword, TYPE_D2D );//for test add BI
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_testUpdateServerConnectionEx
   (JNIEnv *env, jclass className, jint svrType, jstring strServer, jstring strServerPort, jstring strProxyServerName, jstring strProxyServerPort, jstring strProxyUsername, jstring strProxyPassword, jint type)
{
	wchar_t* szDownloadServer= JStringToWCHAR(env, strServer);
	wchar_t* szDownloadServerPort= JStringToWCHAR(env, strServerPort);
	wchar_t* szProxyServerName= JStringToWCHAR(env, strProxyServerName);
	wchar_t* szProxyPort= JStringToWCHAR(env, strProxyServerPort);
	wchar_t* szProxyUserName= JStringToWCHAR(env, strProxyUsername);
	wchar_t* szProxyPassword= JStringToWCHAR(env, strProxyPassword);

	wstring wsServer = (szDownloadServer==NULL ? L"" : szDownloadServer);
	wstring wsProxyServer = (szProxyServerName==NULL ? L"" : szProxyServerName);
	wstring wsProxyUsername = ( szProxyUserName==NULL ? L"" : szProxyUserName );
	wstring wsProxyPassword = ( szProxyPassword==NULL ? L"" : szProxyPassword );
	int nSvrPort = 80;
	if( szDownloadServerPort && wcslen(szDownloadServerPort)>0 )
		nSvrPort = _wtoi(szDownloadServerPort);
	int nProxyPort = 80;
	if( szProxyPort && wcslen(szProxyPort)>0 )
		nProxyPort = _wtoi( szProxyPort );

	SAFE_FREE( szDownloadServer );
	SAFE_FREE( szDownloadServerPort );
	SAFE_FREE( szProxyServerName );
	SAFE_FREE( szProxyPort );
	SAFE_FREE( szProxyUserName );
	SAFE_FREE( szProxyPassword );

	return AFTestUpdateServerConnection( type, svrType, wsServer, nSvrPort, wsProxyServer, nProxyPort, wsProxyUsername, wsProxyPassword );
}

//added by cliicy.luo
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_testBIUpdateServerConnection
(JNIEnv *env, jclass className, jint svrType, jstring strServer, jstring strServerPort, jstring strProxyServerName, jstring strProxyServerPort, jstring strProxyUsername, jstring strProxyPassword)
{
	logObj.LogW(LL_INF, 0, L"really will call testBIUpdateServerConnectionEx");
	return Java_com_ca_arcflash_webservice_jni_WSJNI_testBIUpdateServerConnectionEx(env, className, svrType, strServer, strServerPort, strProxyServerName, strProxyServerPort, strProxyUsername, strProxyPassword, TYPE_D2D);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_testBIUpdateServerConnectionEx
(JNIEnv *env, jclass className, jint svrType, jstring strServer, jstring strServerPort, jstring strProxyServerName, jstring strProxyServerPort, jstring strProxyUsername, jstring strProxyPassword, jint type)
{
	wchar_t* szDownloadServer = JStringToWCHAR(env, strServer);
	wchar_t* szDownloadServerPort = JStringToWCHAR(env, strServerPort);
	wchar_t* szProxyServerName = JStringToWCHAR(env, strProxyServerName);
	wchar_t* szProxyPort = JStringToWCHAR(env, strProxyServerPort);
	wchar_t* szProxyUserName = JStringToWCHAR(env, strProxyUsername);
	wchar_t* szProxyPassword = JStringToWCHAR(env, strProxyPassword);

	wstring wsServer = (szDownloadServer == NULL ? L"" : szDownloadServer);
	wstring wsProxyServer = (szProxyServerName == NULL ? L"" : szProxyServerName);
	wstring wsProxyUsername = (szProxyUserName == NULL ? L"" : szProxyUserName);
	wstring wsProxyPassword = (szProxyPassword == NULL ? L"" : szProxyPassword);
	int nSvrPort = 80;
	if (szDownloadServerPort && wcslen(szDownloadServerPort)>0)
		nSvrPort = _wtoi(szDownloadServerPort);
	int nProxyPort = 80;
	if (szProxyPort && wcslen(szProxyPort)>0)
		nProxyPort = _wtoi(szProxyPort);

	SAFE_FREE(szDownloadServer);
	SAFE_FREE(szDownloadServerPort);
	SAFE_FREE(szProxyServerName);
	SAFE_FREE(szProxyPort);
	SAFE_FREE(szProxyUserName);
	SAFE_FREE(szProxyPassword);

	return AFTestBIUpdateServerConnection(type, svrType, wsServer, nSvrPort, wsProxyServer, nProxyPort, wsProxyUsername, wsProxyPassword);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_installBIUpdate
(JNIEnv *env, jclass className)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_installBIUpdateEx(env, className, TYPE_D2D);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_installBIUpdateEx
(JNIEnv *env, jclass className, jint type)
{
	return AFInstallBIUpdate(type);
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getBIUpdateStatusFile
(JNIEnv *env, jclass className)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_getBIUpdateStatusFileEx(env, className, TYPE_D2D);
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getBIUpdateStatusFileEx
(JNIEnv *env, jclass className, jint type)
{
	wstring strFile;
	AFGetBIUpdateStatusFile(type, strFile);
	jstring retValue = WCHARToJString(env, strFile.c_str());
	return retValue;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_checkBIUpdate
(JNIEnv *env, jclass className)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_checkBIUpdateEx(env, className, TYPE_D2D);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_checkBIUpdateEx
(JNIEnv *env, jclass className, jint type)
{
	return AFCheckBIUpdate(type);
}

//added by cliicy.luo


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_installUpdate
  (JNIEnv *env, jclass className)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_installUpdateEx( env, className, TYPE_D2D );
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_installUpdateEx
  (JNIEnv *env, jclass className, jint type)
{
	return AFInstallUpdate(type);
}

/*
* ----------------------------------------------------------------------------------------------
*                                     Update Manager END }}}
* ----------------------------------------------------------------------------------------------
*/

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetLastError(JNIEnv *env,jclass className)
{
	DWORD errorNumber = GetLastError();
   return (jint) errorNumber;
}


void LogFile(TCHAR szMsg[])
{
	return;
	FILE *fp=NULL;
	fp = _tfopen(_T("C:\\1.txt"),_T("at,ccs=UTF-16LE"));
	if(fp)
	{
		_ftprintf(fp,_T("%s  %s  "),_T("11"),_T("12"));
		_ftprintf(fp,_T("	%s\n"),szMsg);
		fclose(fp);
	}
}

//For D2D data sync to Edge
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetCacheFile4Sync(JNIEnv *env, jclass clz)
{
	jstring  cacheFileName;
	wstring _cacheFileName = L"";
	DWORD dwRet;


	dwRet = GetCacheFile4Sync(_cacheFileName);


	if(dwRet == 0)
		cacheFileName = WCHARToJString(env, (wchar_t *)_cacheFileName.c_str());
	else
		cacheFileName = WCHARToJString(env, (wchar_t *)(L""));


	return cacheFileName;
}

//For D2D data sync to Edge
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DeleteCacheFile4Sync(JNIEnv *env, jclass clz)
{
	DWORD dwRet;


	dwRet = DeleteCacheFile4Sync();


	return 0;
}

//For D2D data sync to Edge
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetReSyncCacheFile(JNIEnv *env, jclass clz)
{
	std::wstring cacheFileName = L"";

	DWORD dwRet = GetReSyncCacheFile(cacheFileName);
	if(dwRet != 0)
		cacheFileName = L"ERROR";

	
	jstring jcacheFileName = WCHARToJString(env,(wchar_t*)cacheFileName.c_str());

	return jcacheFileName;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetD2DSysFolder(JNIEnv *env, jclass clz)
{
	std::wstring d2dSysFolder = L"";

	d2dSysFolder = GetD2DSysFolder();
	
	jstring jd2dSysFolder = WCHARToJString(env,(wchar_t*)d2dSysFolder.c_str());

	return jd2dSysFolder;
}

//For D2D active log sync to Edge
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetD2DActiveLogTransFileXML(JNIEnv *env, jclass clz)
{
	jstring  ActiveLogTransFileXML;
	ActiveLogTransFileXML = WCHARToJString(env, (wchar_t *)(L""));
	return ActiveLogTransFileXML;

	//
	// remove the logic for activity logs, it is legacy method not used in r5.0
	//
	/*
	wstring _ActiveLogTransFileXML = L"";
	DWORD dwRet;


	dwRet = GetD2DActiveLogTransFileXML(_ActiveLogTransFileXML);


	if(dwRet == 0)
		ActiveLogTransFileXML = WCHARToJString(env, (wchar_t *)_ActiveLogTransFileXML.c_str());
	else
		ActiveLogTransFileXML = WCHARToJString(env, (wchar_t *)(L""));


	return ActiveLogTransFileXML;
	*/
}

//For D2D active log sync to Edge
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetFullD2DActiveLogTransFileXML(JNIEnv *env, jclass clz)
{
	jstring  FullActiveLogTransFileXML;
	FullActiveLogTransFileXML = WCHARToJString(env, (wchar_t *)(L""));
	return FullActiveLogTransFileXML;
	//
	// remove the logic for activity logs, it is legacy method not used in r5.0
	//

	/*
	wstring _FullActiveLogTransFileXML = L"";
	DWORD dwRet;


	dwRet = GetFullD2DActiveLogTransFileXML(_FullActiveLogTransFileXML);


	if(dwRet == 0)
		FullActiveLogTransFileXML = WCHARToJString(env, (wchar_t *)_FullActiveLogTransFileXML.c_str());
	else
		FullActiveLogTransFileXML = WCHARToJString(env, (wchar_t *)(L""));


	return FullActiveLogTransFileXML;
	*/
}

//For D2D active log sync to Edge
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DelD2DActiveLogTransFileXML(JNIEnv *env, jclass clz)
{
	DWORD dwRet;


	dwRet = DelD2DActiveLogTransFileXML();


	return 0;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_cancelVMJob
(JNIEnv *env, jclass clz, jlong jobID,jstring vmIdentification){
	return AFCancelJob(jobID,JStringToWCHAR(env, vmIdentification));
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_createVMJobMonitor
(JNIEnv *env, jclass clz, jstring vmId){

	IAFJobMonitor *pAFJM = NULL;
	JOB_MONITOR    aJM = {0};
	memset(&aJM, 0, sizeof(JOB_MONITOR));
	wchar_t* vmIdentification = JStringToWCHAR(env, vmId);
	
	DWORD dwRet  = CreateIJobMonitorByName(vmIdentification, &pAFJM);
	
	if(vmIdentification != NULL)
		free(vmIdentification);

	if(!dwRet)
	{		
		return (jlong)pAFJM;
	}

	return 0;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetBackupVMList
  (JNIEnv *env, jclass clz, jstring path,jstring domain,jstring username,jstring password, jobject backupVMList)
{
	wchar_t* pDest = JStringToWCHAR(env, path);
	wchar_t* pUser = JStringToWCHAR(env, username);
	wchar_t* pPwd = JStringToWCHAR(env, password);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	
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

	VMInfoList vmList;
	IVMList *pList = NULL;
	DWORD dwRet = CreateIVMList(info,&pList);

	if(!dwRet){
		if(pList != NULL)
		{
			dwRet = pList->ReadVMList(info.szDir,vmList);
			if(vmList.size()>0){
				AddBackupVMToList(env,backupVMList,vmList);
			}
			pList->Release();
		}
	}

	return (jlong)dwRet;

}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetBackupVM
  (JNIEnv *env, jclass clz, jstring path,jstring domain,jstring username,jstring password, jobject backupVMList)
{
	wchar_t* pDest = JStringToWCHAR(env, path);
	wchar_t* pUser = JStringToWCHAR(env, username);
	wchar_t* pPwd = JStringToWCHAR(env, password);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	
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

	VMInfoList vmList;
	IVMList *pList = NULL;
	DWORD dwRet = CreateIVMList(info,&pList);

	if(!dwRet){
		if(pList != NULL)
		{
			dwRet = pList->ReadVMListPerVM(info.szDir,vmList);
			if(vmList.size()>0){
				AddBackupVMToList(env,backupVMList,vmList);
			}
			pList->Release();
		}
	}

	return (jlong)dwRet;

}

/*
* Class:     com_ca_arcflash_webservice_jni_WSJNI
* Method:    getVAppChildBackupVMsAndRecoveryPoints
* Signature: (Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/Map;)I
*/
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getVAppChildBackupVMsAndRecoveryPoints
  (JNIEnv *env, jclass clz, jstring vAppDest, jint vAppSessNum, jstring domain, jstring username, jstring password, jobject childVMs, jobject childRestPointsMap) //<huvfe01>2014-8-20 browse vApp childs
{
	wchar_t* pDest = JStringToWCHAR(env, vAppDest);
	wchar_t* pUser = JStringToWCHAR(env, username);
	wchar_t* pPwd = JStringToWCHAR(env, password);
	wchar_t* pDomain = JStringToWCHAR(env, domain);

	NET_CONN_INFO info;
	memset(&info, 0, sizeof(NET_CONN_INFO));

	if (pDest != NULL)
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

	DWORD dwRet = 0;
	IVMList *pList = NULL;
	IRestorePoint *pIRest = NULL;

	do 
	{
		vAppInfoXml vAppInfo;
		dwRet = AFGetBackupVAppInfo(info, (ULONG)vAppSessNum, vAppInfo);
		if (dwRet)
		{
			break;
		}

		//get child VMs and it restore point
		for (size_t ii = 0; ii < vAppInfo.vAppVMTotalInfoXml.size(); ii++)
		{
			CONST vAppVMSessionInfo & vAppVMSessionInfoVar = vAppInfo.vAppVMTotalInfoXml[ii].VMSessionInfo;

			CString strBackupRoot = vAppVMSessionInfoVar.BackupRoot;
			strBackupRoot.TrimRight(L"\\/");
			CONST CString & strSessGuid = vAppVMSessionInfoVar.SessionGUID;

			if (0 != vAppVMSessionInfoVar.JobStatusString.CompareNoCase(TOKEN_VALUE_TO_STR(JS_FINISHED)))
			{
				dwRet = ERROR_INVALID_STATE;
				break;
			}

			ZeroMemory(info.szDir, sizeof(info.szDir));
			wcscpy_s(info.szDir, strBackupRoot.GetString());

			if (NULL != pList)
			{
				pList->Release();
				pList = NULL;
			}

			if (NULL != pIRest)
			{
				pIRest->Release();
				pIRest = NULL;
			}
			
			dwRet = CreateIVMList(info, &pList);
			if ((!dwRet) && (NULL != pList))
			{
				//child vm
				VMInfoList vmList;
				dwRet = pList->ReadVMListPerVM(info.szDir, vmList);
				if (dwRet || 0 == vmList.size())
				{
					break;
				}

				//specific restore point				
				dwRet = CreateIRestorePoint(info, &pIRest);
				if (dwRet)
				{
					break;
				}

				RESTORE_POINT_ITEM restPoint;
				wstring strDate;
				BOOL bRet = pIRest->GetRestorePointByGuid(strSessGuid.GetString(), restPoint, strDate);
				if (!bRet)
				{
					dwRet = ERROR_NOT_FOUND;
					break;
				}

				////////////////////////////////////////////////////
				AddBackupVMToList(env, childVMs, vmList);

				////////////////////////////////////////////////////
				AddRestorePoint2JMap(env, &childRestPointsMap, &restPoint, vmList[0].instanceUuid.c_str(), strDate.c_str());
			}
		}

	} while (FALSE);

	if (NULL != pList)
	{
		pList->Release();
		pList = NULL;
	}

	if (NULL != pIRest)
	{
		pIRest->Release();
		pIRest = NULL;
	}

	return dwRet;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckVMDestination
  (JNIEnv *env, jclass clz, jstring path,jstring domain,jstring username,jstring password)
{
	wchar_t* pDest = JStringToWCHAR(env, path);
	wchar_t* pUser = JStringToWCHAR(env, username);
	wchar_t* pPwd = JStringToWCHAR(env, password);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	
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

	IVMList *pList = NULL;
	DWORD dwRet = CreateIVMList(info,&pList);

	if(!dwRet){
		if(pList != NULL)
		{
			BOOL ret = pList->CheckDestContainBackup(info.szDir);
			pList->Release();
			return ret;
		}
	}

	return (jboolean)0;

}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetBackupVMDisk
  (JNIEnv *env, jclass clz, jstring path,jstring subPath,jstring domain,jstring username,jstring password, jobject diskList)
{
	wchar_t* pDest = JStringToWCHAR(env, path);
	wchar_t* pSubPath = JStringToWCHAR(env, subPath);
	wchar_t* pUser = JStringToWCHAR(env, username);
	wchar_t* pPwd = JStringToWCHAR(env, password);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	
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

	VMDiskList vmDiskList;
	IDiskVolumeMap *pMap = NULL;
	DWORD dwRet = CreateIDiskVolumeMap(info,&pMap);

	if(!dwRet){
		if(pMap != NULL)
		{
			dwRet = pMap->GetDiskInfo(info.szDir,pSubPath,vmDiskList);
			if(vmDiskList.size()>0){
				AddBackupVMDiskToList(env,diskList,vmDiskList);
			}
			pMap->Release();
		}
	}

	if(pSubPath!=NULL){
		free(pSubPath);
	}
	return (jlong)dwRet;

} 

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetHyperVVMNetworkList
	(JNIEnv *env, jclass clz, jstring path,jstring subPath,jstring domain,jstring username,jstring password, jobject adapterList)
{
	using namespace VMPersistConfiguration;

	DWORD dwRet = 0;
	wchar_t* pDest    = JStringToWCHAR(env, path);
	wchar_t* pSubPath = JStringToWCHAR(env, subPath);
	wchar_t* pUser    = JStringToWCHAR(env, username);
	wchar_t* pPwd     = JStringToWCHAR(env, password);
	wchar_t* pDomain  = JStringToWCHAR(env, domain);

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

	vector<CHyperVVMNetworkAdapter> vecAdapters;
	IHyperVVMPersistConfParser* pConfParser = NULL;
	dwRet = CreateIHyperVMPersistConfParser(info, pSubPath, &pConfParser);

	if(!dwRet){
		if(pConfParser != NULL)
		{
			(VOID)pConfParser->GetAllNetworkAdapters(vecAdapters);
			if(vecAdapters.size()>0){
				AddHypervVMbackupNetworkAdaptersToList(env,adapterList,vecAdapters);
			}
			pConfParser->Release();
		}
	}

	if(pSubPath!=NULL){
		free(pSubPath);
	}
	return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetHyperVBackupVMDisk
	(JNIEnv *env, jclass clz, jstring path,jstring subPath,jstring domain,jstring username,jstring password, jobject diskList)
{
	DWORD dwRet = 0;

	wchar_t* pDest = JStringToWCHAR(env, path);
	wchar_t* pSubPath = JStringToWCHAR(env, subPath);
	wchar_t* pUser = JStringToWCHAR(env, username);
	wchar_t* pPwd = JStringToWCHAR(env, password);
	wchar_t* pDomain = JStringToWCHAR(env, domain);

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

	HyperVVMDiskList vmDiskList;
	IDiskVolumeMap *pMap = NULL;
	dwRet = CreateIDiskVolumeMap(info,&pMap);

	if(!dwRet){
		if(pMap != NULL)
		{
			dwRet = pMap->GetHyperVVMBackupDiskInfo(info.szDir,pSubPath,vmDiskList);
			if(vmDiskList.size()>0){
				AddHypervVMbackupDiskToList(env,diskList,vmDiskList);
			}
			pMap->Release();
		}
	}

	if(pSubPath!=NULL){
		free(pSubPath);
	}
	return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetBackupVMOriginalInfo
  (JNIEnv *env, jclass clz, jstring path,jint sessionNum,jstring domain,jstring username,jstring password, jobject originalInfo)
{
	wchar_t* pDest = JStringToWCHAR(env, path);
	wchar_t* pUser = JStringToWCHAR(env, username);
	wchar_t* pPwd = JStringToWCHAR(env, password);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	
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
	
	
	VMInfoXml vmInfo;
	DWORD dwRet = AFGetBackupSourceVMInfo(info,sessionNum,vmInfo);

	if(!dwRet){
		jclass class_JBackupVMOriginalInfo = env->FindClass("com/ca/arcflash/webservice/jni/model/JBackupVMOriginalInfo");	

		jfieldID field_esx = env->GetFieldID(class_JBackupVMOriginalInfo, "originalEsxServer", "Ljava/lang/String;");
		jfieldID field_vc = env->GetFieldID(class_JBackupVMOriginalInfo, "originalVcName", "Ljava/lang/String;");
		jfieldID field_resourcepool = env->GetFieldID(class_JBackupVMOriginalInfo, "originalResourcePool", "Ljava/lang/String;");

		jstring jstr = WCHARToJString(env, vmInfo.VM.vmESXHost);
		env->SetObjectField(originalInfo, field_esx, jstr);

		jstr = WCHARToJString(env, vmInfo.VCenter.ServerName);
		env->SetObjectField(originalInfo, field_vc, jstr);

		jstr = WCHARToJString(env, vmInfo.VM.vmResPool);
		env->SetObjectField(originalInfo, field_resourcepool, jstr);

		if (class_JBackupVMOriginalInfo != NULL) env->DeleteLocalRef(class_JBackupVMOriginalInfo);

	}
	return (jlong)dwRet;

}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetVMLogActivity
(JNIEnv *env, jclass clz, jint startFrom, jint requestCount, jobject activityLogResult,jstring vmUUID)
{
	ULONGLONG nCnt, ntotalCount = 0;
	PFLASHDB_ACTIVITY_LOG p = NULL;
	wchar_t* vm_uuid = JStringToWCHAR(env, vmUUID);
	DWORD result = GetLog( APT_D2D, 0, startFrom, requestCount, &p, &nCnt, &ntotalCount,vm_uuid);
	if( result == 0) 
	{
		jclass class_ArrayList = env->FindClass("java/util/List");
		jclass class_ActivityLogResult = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLogResult");	
		jclass class_ActivityLog = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLog");	

		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		jmethodID jActivityLog_constructor = env->GetMethodID(class_ActivityLog, "<init>", "()V");

		jfieldID field_totalCount = env->GetFieldID(class_ActivityLogResult, "totalCount", "J");
		jfieldID field_logs = env->GetFieldID(class_ActivityLogResult, "logs", "Ljava/util/List;");
		jfieldID field_message = env->GetFieldID(class_ActivityLog, "message", "Ljava/lang/String;");
		jfieldID field_time = env->GetFieldID(class_ActivityLog, "time", "Ljava/lang/String;");
		jfieldID field_level = env->GetFieldID(class_ActivityLog, "level", "J");
		jfieldID field_jobID = env->GetFieldID(class_ActivityLog, "jobID", "J");
		jobject jActivityLog = NULL;

		env->SetLongField(activityLogResult, field_totalCount, ntotalCount);
		jobject list = env->GetObjectField(activityLogResult,field_logs);

		WCHAR szTime[MAX_PATH] = {0};
		for(ULONGLONG i = 0; i < nCnt; i++) {
			jActivityLog = env->NewObject(class_ActivityLog, jActivityLog_constructor);

			jstring jstr = WCHARToJString(env, p[i].wszLogMessage);
			env->SetObjectField(jActivityLog, field_message, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			ZeroMemory( szTime, sizeof(szTime) );
			ulonglong_2_timestr( p[i].ullTimeUTC, szTime, _ARRAYSIZE(szTime) );
			jstr = WCHARToJString(env, szTime );
			env->SetObjectField(jActivityLog, field_time, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			env->SetLongField(jActivityLog, field_level, p[i].dwLogLevel);
			env->SetLongField(jActivityLog, field_jobID, p[i].ullJobID);

			env->CallBooleanMethod(list, id_ArrayList_add, jActivityLog);
			if (jActivityLog != NULL) env->DeleteLocalRef(jActivityLog);
		}

		if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
		if (class_ActivityLog != NULL) env->DeleteLocalRef(class_ActivityLog);		
		if (class_ActivityLogResult != NULL) env->DeleteLocalRef(class_ActivityLogResult);
	}

	FreeLog(p);

	if(vm_uuid!=NULL){
		free(vm_uuid);
	}

	return result;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetVMJobLogActivity
(JNIEnv *env, jclass clz, jlong jobNo, jint startFrom, jint requestCount, jobject activityLogResult,jstring vmUUID)
{
	ULONGLONG nCnt, ntotalCount = 0;
	PFLASHDB_ACTIVITY_LOG p = NULL;
	wchar_t* vm_uuid = JStringToWCHAR(env, vmUUID);
	DWORD result = GetLog( APT_D2D, jobNo, startFrom, requestCount, &p, &nCnt, &ntotalCount,vm_uuid);
	if( result == 0) 
	{
		jclass class_ArrayList = env->FindClass("java/util/List");
		jclass class_ActivityLogResult = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLogResult");	
		jclass class_ActivityLog = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLog");	

		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		jmethodID jActivityLog_constructor = env->GetMethodID(class_ActivityLog, "<init>", "()V");

		jfieldID field_totalCount = env->GetFieldID(class_ActivityLogResult, "totalCount", "J");
		jfieldID field_logs = env->GetFieldID(class_ActivityLogResult, "logs", "Ljava/util/List;");
		jfieldID field_message = env->GetFieldID(class_ActivityLog, "message", "Ljava/lang/String;");
		jfieldID field_time = env->GetFieldID(class_ActivityLog, "time", "Ljava/lang/String;");
		jfieldID field_level = env->GetFieldID(class_ActivityLog, "level", "J");
		jfieldID field_jobID = env->GetFieldID(class_ActivityLog, "jobID", "J");
		jobject jActivityLog = NULL;

		env->SetLongField(activityLogResult, field_totalCount, ntotalCount);
		jobject list = env->GetObjectField(activityLogResult,field_logs);

		WCHAR szTime[MAX_PATH] = {0};
		for(ULONGLONG i = 0; i < nCnt; i++) {
			jActivityLog = env->NewObject(class_ActivityLog, jActivityLog_constructor);

			jstring jstr = WCHARToJString(env, p[i].wszLogMessage);
			env->SetObjectField(jActivityLog, field_message, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			ZeroMemory( szTime, sizeof(szTime) );
			ulonglong_2_timestr( p[i].ullTimeUTC, szTime, _ARRAYSIZE(szTime) );
			jstr = WCHARToJString(env, szTime);
			env->SetObjectField(jActivityLog, field_time, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			env->SetLongField(jActivityLog, field_level, p[i].dwLogLevel);
			env->SetLongField(jActivityLog, field_jobID, p[i].ullJobID );

			env->CallBooleanMethod(list, id_ArrayList_add, jActivityLog);
			if (jActivityLog != NULL) env->DeleteLocalRef(jActivityLog);
		}

		if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
		if (class_ActivityLog != NULL) env->DeleteLocalRef(class_ActivityLog);		
		if (class_ActivityLogResult != NULL) env->DeleteLocalRef(class_ActivityLogResult);
	}
	
	FreeLog(p);

    if(vm_uuid!=NULL){
		free(vm_uuid);
	}
	return result;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetJobLogActivityForVM
(JNIEnv *env, jclass clz, jlong jobNo, jint startFrom, jint requestCount, jobject activityLogResult,jstring vmUUID)
{
	ULONGLONG nCnt, ntotalCount = 0;
	PFLASHDB_ACTIVITY_LOG p = NULL;
	wchar_t* vm_uuid = JStringToWCHAR(env, vmUUID);
	DWORD result = GetLogOfAgent( APT_D2D, jobNo, startFrom, requestCount, &p, &nCnt, &ntotalCount,vm_uuid);
	if( result == 0) 
	{
		jclass class_ArrayList = env->FindClass("java/util/List");
		jclass class_ActivityLogResult = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLogResult");	
		jclass class_ActivityLog = env->FindClass("com/ca/arcflash/service/jni/model/JActivityLog");	

		jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		jmethodID jActivityLog_constructor = env->GetMethodID(class_ActivityLog, "<init>", "()V");

		jfieldID field_totalCount = env->GetFieldID(class_ActivityLogResult, "totalCount", "J");
		jfieldID field_logs = env->GetFieldID(class_ActivityLogResult, "logs", "Ljava/util/List;");
		jfieldID field_message = env->GetFieldID(class_ActivityLog, "message", "Ljava/lang/String;");
		jfieldID field_time = env->GetFieldID(class_ActivityLog, "time", "Ljava/lang/String;");
		jfieldID field_level = env->GetFieldID(class_ActivityLog, "level", "J");
		jfieldID field_jobID = env->GetFieldID(class_ActivityLog, "jobID", "J");
		jobject jActivityLog = NULL;

		env->SetLongField(activityLogResult, field_totalCount, ntotalCount);
		jobject list = env->GetObjectField(activityLogResult,field_logs);

		WCHAR szTime[MAX_PATH] = {0};
		for(ULONGLONG i = 0; i < nCnt; i++) {
			jActivityLog = env->NewObject(class_ActivityLog, jActivityLog_constructor);

			jstring jstr = WCHARToJString(env, p[i].wszLogMessage);
			env->SetObjectField(jActivityLog, field_message, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			ZeroMemory( szTime, sizeof(szTime) );
			ulonglong_2_timestr( p[i].ullTimeUTC, szTime, _ARRAYSIZE(szTime) );
			jstr = WCHARToJString(env, szTime);
			env->SetObjectField(jActivityLog, field_time, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);

			env->SetLongField(jActivityLog, field_level, p[i].dwLogLevel);
			env->SetLongField(jActivityLog, field_jobID, p[i].ullJobID );

			env->CallBooleanMethod(list, id_ArrayList_add, jActivityLog);
			if (jActivityLog != NULL) env->DeleteLocalRef(jActivityLog);
		}

		if (class_ArrayList != NULL) env->DeleteLocalRef(class_ArrayList);
		if (class_ActivityLog != NULL) env->DeleteLocalRef(class_ActivityLog);		
		if (class_ActivityLogResult != NULL) env->DeleteLocalRef(class_ActivityLogResult);
	}
	
	FreeLog(p);

    if(vm_uuid!=NULL){
		free(vm_uuid);
	}
	return result;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DeleteVMLogActivity
(JNIEnv *env, jclass clz, jint year, jint month, jint day, jint hour, jint minute, jint second,jstring vmUUID)
{
	SYSTEMTIME systime;
	memset(&systime, 0, sizeof(SYSTEMTIME));
	systime.wYear = year;
	systime.wMonth = month;
	systime.wDay = day;
	systime.wHour = hour;
	systime.wMinute = minute;
	systime.wSecond = second;
	wchar_t* vm_uuid = JStringToWCHAR(env, vmUUID);
	DWORD result = PruneLog( APT_D2D, systime,vm_uuid);
	if(vm_uuid!=NULL){
		free(vm_uuid);
	}
	return result;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetVDDKVersion
(JNIEnv *env, jclass clz)
{
	ULONG  unVerHigh = 0;
	ULONG  unVerLow = 0;
	ULONG  unVerSub = 0;
	int result = AFGetVddkVersionAPI(&unVerHigh, &unVerLow, &unVerSub);
	wchar_t pswzRetVal[128]; 
	if(result==0){
		_snwprintf_s(pswzRetVal, _countof(pswzRetVal), _TRUNCATE, L"%u.%u.%u", unVerHigh, unVerLow, unVerSub);
	}else{
		_snwprintf_s(pswzRetVal, _countof(pswzRetVal), _TRUNCATE, L"%d", result);
	}
	//wchar_t* ret = L"1.2.1";
	return WCHARToJString(env,pswzRetVal);
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetVIXVersion
(JNIEnv *env, jclass clz)
{
	ULONG  unVerHigh = 0;
	ULONG  unVerLow = 0;
	ULONG  unVerSub = 0;
	int result = AFGetVixVersionAPI(&unVerHigh, &unVerLow, &unVerSub);
	wchar_t pswzRetVal[128]; 
	if(result==0){
		_snwprintf_s(pswzRetVal, _countof(pswzRetVal), _TRUNCATE, L"%u.%u.%u", unVerHigh, unVerLow, unVerSub);
	}else{
		_snwprintf_s(pswzRetVal, _countof(pswzRetVal), _TRUNCATE, L"%d", result);
	}
	//wchar_t* ret = L"1.2.1";
	return WCHARToJString(env,pswzRetVal);
}




JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_MsgOpenCatalogFile
  (JNIEnv *env, jclass clz, jstring catName)
{
	wchar_t* pCatName = JStringToWCHAR(env, catName);

	HANDLE h = MsgOpenCatalogFile(pCatName);
	if(pCatName != NULL)
		free(pCatName);

	return (jlong)h;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_MsgGetChildren
  (JNIEnv *env, jclass clz, jlong jhandle, jlong lowSelfid, jlong highSelfid, jobject retArrMsgRec)
{
	HANDLE h = (HANDLE)jhandle;
	if(h != NULL)
	{			
		UINT Cnt = 0;

		PMsgRecW pchild = MsgGetChildren(h, lowSelfid, highSelfid, &Cnt);		

		for(UINT i = 0; i < Cnt; i++) 
		{	
			AddMsgRecW2List(env, &retArrMsgRec, &pchild[i]);		
		}


		if(pchild != NULL)
		{
			ReleaseMsgRecW(pchild,Cnt);
		}

		//HeapFree(GetProcessHeap(), 0, pchild);
	}

	return JNI_OK;

}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_MsgGetChildrenEx
(JNIEnv *env, jclass jclz, jlong jhandle, jlong lowSelfid, jlong highSelfid, jint jnStart, jint jnRequest, jobject jnCnt, jobject retArrMsgRec)
{		
		HANDLE h = (HANDLE)jhandle;
		if(h != NULL)
		{	UINT Cnt = 0;
			PMsgRecW pchild = MsgGetChildrenEx(h, lowSelfid, highSelfid, (UINT)jnStart, (UINT)jnRequest, &Cnt);		

			for(UINT i = 0; i < Cnt; i++) 
			{	
				AddMsgRecW2List(env, &retArrMsgRec, &pchild[i]);		
			}

			AddUINT2JRWLong(env, Cnt, &jnCnt);

			if(pchild != NULL)
			{
				ReleaseMsgRecW(pchild,Cnt);
			}

			/*if(pchild!=NULL)
			{
				HeapFree(GetProcessHeap(), 0, pchild);
			}*/
		}

		return JNI_OK;
}


// start to get msg children with filter and paging and sorting
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_MsgGetChildrenByFilterFirst
(JNIEnv *env, jclass clz, jlong jhandle, jlong jnRequestIndex, jlong jlselfid, jlong jhselfid, jstring jqueryString, jlong jnRequest, jobject jnCnt, jobject jnTotal, jobject jretArrMsgRec)
{
	HANDLE h = (HANDLE)jhandle;

	if(h != NULL)
	{	
		UINT nCnt = 0;
		UINT nTotal = 0;
		wchar_t* pszQueryString = JStringToWCHAR(env, jqueryString);

		PMsgRecW pchild = MsgGetChildrenByFilterFirst(h, jlselfid, jhselfid, pszQueryString, (UINT)jnRequest, &nCnt,(int)jnRequestIndex, &nTotal);		

		for(UINT i = 0; i < nCnt; i++) 
		{	
			AddMsgRecW2List(env, &jretArrMsgRec, &pchild[i]);		
		}

		AddUINT2JRWLong(env, nCnt, &jnCnt);
		AddUINT2JRWLong(env, nTotal, &jnTotal);

		if (pszQueryString != NULL)
		{
			free(pszQueryString);
		}

		if(pchild != NULL)
		{
			ReleaseMsgRecW(pchild, nCnt);
		}
	}

	return JNI_OK;
}

// continue to get msg children with filter and paging and sorting
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_MsgGetChildrenByFilterNext
(JNIEnv *env, jclass clz, jlong jhandle, jlong jnRequestIndex, jlong jnStart, jlong jnRequest, jobject jnCnt, jobject jretArrMsgRec)
{
	HANDLE h = (HANDLE)jhandle;

	if(h != NULL)
	{	
		UINT nCnt = 0;

		PMsgRecW pchild = MsgGetChildrenByFilterNext(h, jnStart, (UINT)jnRequest, &nCnt, (int)jnRequestIndex);		

		for(UINT i = 0; i < nCnt; i++) 
		{	
			AddMsgRecW2List(env, &jretArrMsgRec, &pchild[i]);		
		}

		AddUINT2JRWLong(env, nCnt, &jnCnt);

		if(pchild != NULL)
		{
			ReleaseMsgRecW(pchild, nCnt);
		}
	}

	return JNI_OK;
}


JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetMsgCatalogPath
(JNIEnv *env, jclass clz, jstring dbIdentity, jstring backupDestination, jlong sessionNumber, jlong subSessionNumber)
{
	jstring msgCatalogPath;

	wchar_t* pszDBIdentity = JStringToWCHAR(env, dbIdentity);
	wchar_t* pszBackupDestination = JStringToWCHAR(env, backupDestination);
	wstring strMsgCatalogPath;

	long ret = GetMsgCatalogPath(strMsgCatalogPath, pszDBIdentity, pszBackupDestination, (DWORD)sessionNumber, (DWORD)subSessionNumber);	

	msgCatalogPath = WCHARToJString(env, (wchar_t*)strMsgCatalogPath.data());
	
	if(pszDBIdentity != NULL)
		free(pszDBIdentity);

	if(pszBackupDestination != NULL)
		free(pszBackupDestination);

	return msgCatalogPath;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_MsgGetChildrenCount
(JNIEnv *env, jclass jclz, jlong jhandle, jlong lowSelfid, jlong highSelfid, jobject jnCnt)
{
	HANDLE h = (HANDLE)jhandle;
	UINT ttlCatCnt = 0;
	if(h != NULL)
	{
		ttlCatCnt = MsgGetChildrenCount( h, lowSelfid, highSelfid);	

		// -1 means failed
		ttlCatCnt = ttlCatCnt == -1 ? 0 : ttlCatCnt;
	}
	AddUINT2JRWLong(env, ttlCatCnt, &jnCnt);
	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_D2DExCheckUser
(JNIEnv *env, jclass jclz, jstring domain, jstring user, jstring password)
{
	LONG ret = 0;

	ret = AOEInit();

	if(S_OK ==ret )
	{
		wchar_t* pszDomain = JStringToWCHAR(env, domain);
		wchar_t* pszUser = JStringToWCHAR(env, user);
		wchar_t* pszPassword = JStringToWCHAR(env, password);

		ret = D2DExCheckUserW(pszDomain, pszUser, pszPassword);

		if(pszDomain != NULL)
			free(pszDomain);

		if(pszUser != NULL)
			free(pszUser);

		if(pszPassword != NULL)
			free(pszPassword);
	} 

	return (jlong)ret;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_FindNextMsgCatalogFile
(JNIEnv *env, jclass clz, jlong jhandle, jlong nRequest, jobject retArr, jobject jnFound, jobject jnCurrent)
{	 
	HANDLE h = (HANDLE)jhandle;
	if(h != NULL)
	{
		PMsgSearchRecW pDetail = NULL;	

		UINT nFound = 0;
		UINT nCur = FindNextMsgCatalogFile(h, (UINT)nRequest, &pDetail, &nFound);	

		for(UINT i = 0; i < nFound; i++) {			
			AddPMsgSearchRecW2List(env, &retArr, &pDetail[i]);				
		}
		AddUINT2JRWLong(env, nCur, &jnCurrent);
		AddUINT2JRWLong(env, nFound, &jnFound);

		if(pDetail != NULL)
		{
			ReleaseMsgSearchRecW(pDetail,nFound);
		}
	}

	return JNI_OK;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AOEGetOrganizationName
	(JNIEnv *env, jclass clz, jstring userNameWithDomain, jstring password)
{		
	wchar_t* pwszUserNameWithDomain = JStringToWCHAR(env, userNameWithDomain);
	wchar_t* pwszPassword = JStringToWCHAR(env, password);
	std::wstring strExchOrgName = L"";

	do{
		CUserImpersonateHelper userImplHelper(pwszUserNameWithDomain, pwszPassword);
		HRESULT hResult = userImplHelper.GetError();
		if (hResult != S_OK)
			break;	

		hResult = AOEInit();
		if (hResult == S_OK)
		{
			EAOBJ_ORG_BASEINFO orgInfo;
			memset(&orgInfo, 0, sizeof(EAOBJ_ORG_BASEINFO));
			AOEGetOrgBaseinfo(&orgInfo);

			//orgInfo.nsize is always 0?
			if (/*orgInfo.nsize > 0 &&*/ orgInfo.pszOrgName != NULL)
			{
				strExchOrgName = orgInfo.pszOrgName;
				AOEFreeOrgBaseinfo(&orgInfo);
			}

			AOEUninit();
		}

	} while (0);

	if (pwszUserNameWithDomain != NULL)
		free(pwszUserNameWithDomain);

	if (pwszPassword != NULL)
		free(pwszPassword);

	jstring exchOrgName = WCHARToJString(env, (wchar_t*)strExchOrgName.c_str());
	return exchOrgName;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AOEGetServers
	(JNIEnv *env, jclass clz, jobject retArr, jstring userNameWithDomain, jstring password)
{	 
	wchar_t* pwszUserNameWithDomain = JStringToWCHAR(env, userNameWithDomain);
	wchar_t* pwszPassword = JStringToWCHAR(env, password);

	do{
		CUserImpersonateHelper userImplHelper(pwszUserNameWithDomain, pwszPassword);
		HRESULT hResult = userImplHelper.GetError();
		if (hResult != S_OK)
			break;
		hResult = AOEInit();

		if (hResult == S_OK)
		{
			EAOBJ_SERVERLIST serverList;
			memset(&serverList, 0, sizeof(EAOBJ_SERVERLIST));

			hResult = AOEGetServers(&serverList, 0);
			if (hResult == S_OK)
			{
				PEAOBJ_SERVER pServer = serverList.pServer;
				for (int i = 0; i < serverList.nCount && pServer != NULL; i++, pServer = pServer->pNext)
				{
					Add_AOEServer2List(env, &retArr, pServer);
				}

				AOEFreeServers(&serverList);
			}

			AOEUninit();
		}
	} while (0);

	if (pwszUserNameWithDomain != NULL)
		free(pwszUserNameWithDomain);

	if (pwszPassword != NULL)
		free(pwszPassword);

	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AEGetE15CASList
	(JNIEnv *env, jclass clz, jobject retArr, jstring userNameWithDomain, jstring password)
{	 

	wchar_t* pwszUserNameWithDomain = JStringToWCHAR(env, userNameWithDomain);
	wchar_t* pwszPassword = JStringToWCHAR(env, password);
	do{
		CUserImpersonateHelper userImplHelper(pwszUserNameWithDomain, pwszPassword);
		HRESULT hResult = userImplHelper.GetError();
		if (hResult != S_OK)
			break;
		hResult = AOEInit();

		if (hResult == S_OK)
		{
			EAOBJ_SERVERLIST serverList;
			memset(&serverList, 0, sizeof(EAOBJ_SERVERLIST));

			hResult = AOEGetServers(&serverList, 1);
			if (hResult == S_OK)
			{
				PEAOBJ_SERVER pServer = serverList.pServer;
				for (int i = 0; i < serverList.nCount && pServer != NULL; i++, pServer = pServer->pNext)
				{
					Add_AOEServer2List(env, &retArr, pServer);
				}

				AOEFreeServers(&serverList);
			}

			AOEUninit();
		}
	} while (0);

	if (pwszUserNameWithDomain != NULL)
		free(pwszUserNameWithDomain);

	if (pwszPassword != NULL)
		free(pwszPassword);
	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AEGetDefaultE15CAS(JNIEnv *env, jclass clz, jobject retATTr)
{
	std::wstring strName = L"";

	AEGetE15CAS(strName);
	AddstringToList(env, clz, (wchar_t *)strName.c_str() , &retATTr);

	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AESetDefaultE15CAS(JNIEnv *env, jclass clz, jstring casName)
{
	std::wstring strName =L"";
	wchar_t* pszName = JStringToWCHAR(env, casName);
	if(pszName)
	{
		strName = pszName;
		AESetE15CAS(strName);
	}
	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AOEGetStorageGroups
	(JNIEnv *env, jclass clz, jstring dn, jobject retArr, jstring userNameWithDomain, jstring password)
{	 

	wchar_t* pwszUserNameWithDomain = JStringToWCHAR(env, userNameWithDomain);
	wchar_t* pwszPassword = JStringToWCHAR(env, password);
	do{
		CUserImpersonateHelper userImplHelper(pwszUserNameWithDomain, pwszPassword);
		HRESULT hResult = userImplHelper.GetError();
		if (hResult != S_OK)
			break;
		hResult = AOEInit();

		if (hResult == S_OK)
		{
			wchar_t* pszDN = JStringToWCHAR(env, dn);

			ADOBJ_LIST exchangeItemList;
			memset(&exchangeItemList, 0, sizeof(ADOBJ_LIST));

			hResult = AOEGetSGroup(pszDN, &exchangeItemList);
			if (hResult == S_OK)
			{
				st_exchange_item* pExchangeItem = exchangeItemList.pobjList;
				for (int i = 0; i < exchangeItemList.nCount && pExchangeItem != NULL; i++, pExchangeItem = pExchangeItem->pNext)
				{
					Add_AOEExchangeItem2List(env, &retArr, pExchangeItem);
				}

				AOEFreeSGroup(&exchangeItemList);
			}

			if (pszDN != NULL)
			{
				free(pszDN);
			}

			AOEUninit();
		}
	}while(0);

	if (pwszUserNameWithDomain != NULL)
		free(pwszUserNameWithDomain);

	if (pwszPassword != NULL)
		free(pwszPassword);
	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AOEGetEDBs
	(JNIEnv *env, jclass clz, jstring dn, jobject retArr, jstring userNameWithDomain, jstring password)
{	 

	wchar_t* pwszUserNameWithDomain = JStringToWCHAR(env, userNameWithDomain);
	wchar_t* pwszPassword = JStringToWCHAR(env, password);
	do{
		CUserImpersonateHelper userImplHelper(pwszUserNameWithDomain, pwszPassword);
		HRESULT hResult = userImplHelper.GetError();
		if (hResult != S_OK)
			break;
		hResult = AOEInit();

		if (hResult == S_OK)
		{
			wchar_t* pszDN = JStringToWCHAR(env, dn);

			ADOBJ_LIST exchangeItemList;
			memset(&exchangeItemList, 0, sizeof(ADOBJ_LIST));

			hResult = AOEGetEDBs(pszDN, &exchangeItemList);
			if (hResult == S_OK)
			{
				st_exchange_item* pExchangeItem = exchangeItemList.pobjList;
				for (int i = 0; i < exchangeItemList.nCount && pExchangeItem != NULL; i++, pExchangeItem = pExchangeItem->pNext)
				{
					Add_AOEExchangeItem2List(env, &retArr, pExchangeItem);
				}

				AOEFreeEDBs(&exchangeItemList);
			}

			if (pszDN != NULL)
			{
				free(pszDN);
			}

			AOEUninit();
		}
	} while (0);

	if (pwszUserNameWithDomain != NULL)
		free(pwszUserNameWithDomain);

	if (pwszPassword != NULL)
		free(pwszPassword);
	return JNI_OK;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AOEGetMailboxes
	(JNIEnv *env, jclass clz, jstring dn, jobject retArr, jstring userNameWithDomain, jstring password)
{	 

	wchar_t* pwszUserNameWithDomain = JStringToWCHAR(env, userNameWithDomain);
	wchar_t* pwszPassword = JStringToWCHAR(env, password);
	do{
		CUserImpersonateHelper userImplHelper(pwszUserNameWithDomain, pwszPassword);
		HRESULT hResult = userImplHelper.GetError();
		if (hResult != S_OK)
			break;
		hResult = AOEInit();

		if (hResult == S_OK)
		{
			wchar_t* pszDN = JStringToWCHAR(env, dn);

			EAOBJ_MBLIST mailboxList;
			memset(&mailboxList, 0, sizeof(EAOBJ_MBLIST));

			hResult = AOEGetMailbox(pszDN, &mailboxList);
			if (hResult == S_OK)
			{
				PEAOBJ_MAILBOX pMailbox = mailboxList.pobjList;
				for (int i = 0; i < mailboxList.nCount && pMailbox != NULL; i++, pMailbox = pMailbox->pNext)
				{
					Add_AOEMailbox2List(env, &retArr, pMailbox);
				}

				AOEFreeMailbox(&mailboxList);
			}

			if (pszDN != NULL)
			{
				free(pszDN);
			}

			AOEUninit();
		}
	} while (0);

	if (pwszUserNameWithDomain != NULL)
		free(pwszUserNameWithDomain);

	if (pwszPassword != NULL)
		free(pwszPassword);
	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AOECheckServiceStatus
	(JNIEnv *env, jclass clz, jstring serviceName)
{

	long nResult = 0;

	wchar_t* pszServiceName = JStringToWCHAR(env, serviceName);

	BOOL bIsInstalled = FALSE;
	BOOL bIsRunning   = FALSE;

	HRESULT hRet = AOECheckServiceStatus(pszServiceName, &bIsInstalled, &bIsRunning);

	if (bIsInstalled)
	{
		nResult |= 0x01;
	}

	if (bIsRunning)
	{
		nResult |= 0x02;
	}

	if (pszServiceName != NULL)
	{
		free(pszServiceName);
	}

	return nResult;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AHDesktopFile
(JNIEnv *env, jclass clz, jstring backupDestination)
{
	wchar_t* pszBackupDestination = JStringToWCHAR(env, backupDestination);

	HRESULT hRet = AHDesktopFile(pszBackupDestination);

	if (pszBackupDestination != NULL)
	{
		free(pszBackupDestination);
	}
	return hRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCatalogJob
(JNIEnv *env, jclass jclz, jobject catalogJob)
{
	AFCATALOGJOB afJS;
	memset(&afJS, 0, sizeof(AFCATALOGJOB));

	JCatalogJob2AFCatalogJob(env, &afJS, &catalogJob);

	DWORD dwRet = AFCatalogJob(&afJS);

	FreeAFCatalogJob(&afJS);

	return dwRet;
}


JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFIsCatalogJobRunning
(JNIEnv *env, jclass clz, jstring jbackupDestination, jlong jsessionNumber, jlong jsubSessionNumber)
{
	wchar_t* pszBackupDestination = JStringToWCHAR(env, jbackupDestination);

	BOOL bResult = AFIsCatalogJobRunning(pszBackupDestination, jsessionNumber, jsubSessionNumber);

	if (pszBackupDestination != NULL)
	{
		free(pszBackupDestination);
	}

	return bResult;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFIsCatalogJobInQueue
	(JNIEnv *env, jclass clz, jlong jQueueType, jstring jbackupDestination, jlong jsessionNumber, jlong jsubSessionNumber, jstring vmInstanceUUID)
{
	wchar_t* pszBackupDestination = JStringToWCHAR(env, jbackupDestination);

	BOOL bResult = AFIsCatalogJobInQueue(jQueueType, pszBackupDestination, jsessionNumber, jsubSessionNumber);

	if (pszBackupDestination != NULL)
	{
		free(pszBackupDestination);
	}

	return bResult;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFIsCatalogAvailable
(JNIEnv *env, jclass clz, jlong queueType, jstring queueID, jstring rpsSvrIdentity) 
{
	wchar_t* pszQueueID = JStringToWCHAR(env, queueID);
	wchar_t* pszRpsSvrIdentity = JStringToWCHAR(env, rpsSvrIdentity);

	long bRet = 0;

	if(!pszRpsSvrIdentity) 
		bRet = AFIsCatalogAvailable(queueType, pszQueueID);
	else
		bRet = AFIsCatalogAvailable(queueType, pszQueueID, pszRpsSvrIdentity);

	if(pszQueueID) free(pszQueueID);
	if(pszRpsSvrIdentity) free(pszRpsSvrIdentity);

	return bRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCGRTSkipDisk
(JNIEnv *env, jclass clz, jstring jvolName, jobject jskipped)
{
	wchar_t* pwzVolName = JStringToWCHAR(env, jvolName);

	BOOL bSkipped = FALSE;

	// check if the volume should be skipped because if is a mounted disk
	DWORD dwRet = AFCGRTSkipDisk(pwzVolName, &bSkipped);

	if (dwRet == 0)
	{
		AddUINT2JRWLong(env, bSkipped, &jskipped);
	}

	if (pwzVolName != NULL)
	{
		free(pwzVolName);
	}

	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CheckSessVerByNo
(JNIEnv *env, jclass clz, jstring jdest, jlong jsessNum)
{
	wchar_t* pwszDest = JStringToWCHAR(env, jdest);

	// check if the session is an old session
	int iResult = 0;
	DWORD dwRet = CheckSessVerByNo(pwszDest, jsessNum, iResult);

	if (dwRet != 0)
	{
		iResult = 0;
	}

	if (pwszDest != NULL)
	{
		free(pwszDest);
	}

	return iResult;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_RebootSystem(JNIEnv *env, jclass clazz, jboolean force)
{
	bool bForce= (bool)force;
	DWORD dwRet = RebootSystem(bForce);
	if(dwRet)
		ThrowWSJNIException(env, clazz, env->NewStringUTF("fail to call RebootSystem"), dwRet);
	return ;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_FindHAConfigurationFileURL(JNIEnv *env, jclass klazz, jstring lastRepDest, jstring fileName)
{
	wchar_t* wstrLastRepDest = JStringToWCHAR(env, lastRepDest);
	wchar_t* wstrFileName = JStringToWCHAR(env, fileName);
	wstring fileSaveRoot;
	
	int ret = 0;

	ret = GetDirOfMarkFile_HyperV(wstrLastRepDest,wstrFileName,fileSaveRoot);
	
	if(wstrLastRepDest != NULL)
	{
		free(wstrLastRepDest); //baide02 for mem leak
	}

	if(wstrFileName != NULL)
	{
		free(wstrFileName); //baide02 for mem leak
	}

	if(ret != 0)
	{
		ThrowWSJNIException(env,klazz,env->NewStringUTF("Error occurs in GetDirOfMarkFile_HyperV."),ret);
	}

	jstring fileRoot = WCHARToJString(env,fileSaveRoot);

	return fileRoot;

}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_FindHAConfigurationFileURLByVMGUID(JNIEnv *env, jclass klazz, jstring lastRepDest, jstring fileName)
{
	wchar_t* wstrLastRepDest = JStringToWCHAR(env, lastRepDest);
	wchar_t* wstrFileName = JStringToWCHAR(env, fileName);
	wstring fileSaveRoot;

	int ret = 0;

	ret = GetDirOfMarkFileByVm_HyperV(wstrLastRepDest,wstrFileName,fileSaveRoot);

	if(wstrLastRepDest != NULL)
	{
		free(wstrLastRepDest); //baide02 for mem leak
	}

	if(wstrFileName != NULL)
	{
		free(wstrFileName); //baide02 for mem leak
	}

	if(ret != 0)
	{
		ThrowWSJNIException(env,klazz,env->NewStringUTF("Error occurs in GetDirOfMarkFileByVm_HyperV."),ret);
	}

	jstring fileRoot = WCHARToJString(env,fileSaveRoot);

	return fileRoot;
}
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsFirstD2DSyncCalled(JNIEnv *env, jclass clazz)
{
	BOOLEAN ret = IsFirstD2DSyncCalled();
	return (jboolean)ret;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_MarkFirstD2DSyncCalled(JNIEnv *env, jclass clazz)
{
	DWORD dwRet = MarkFirstD2DSyncCalled();
	return;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetCachedVmInfo4Trans(JNIEnv *env, jclass clazz)
{
	wstring rtnString = AFIGetCachedVmInfo4Trans();

	jstring retValue = NULL;
	retValue = WCHARToJString(env, rtnString.c_str());
	return retValue;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetAllVmInfo4Trans(JNIEnv *env, jclass clazz)
{
	wstring rtnString = AFIGetAllVmInfo4Trans();

	jstring retValue = NULL;
	retValue = WCHARToJString(env, rtnString.c_str());
	return retValue;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DeleteVmInfoTransFile(JNIEnv *env, jclass clazz)
{
	DWORD dwRet = AFIDeleteVmInfoTransFile();
	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DeleteAllVmInfoTransFile(JNIEnv *env, jclass clazz)
{
	DWORD dwRet = AFIDeleteAllVmInfoTransFile();
	return dwRet;
}


/* ARCHIVE */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFArchivePurge
(JNIEnv *env, jclass jclz, jobject in_archiveJobScript)
{
	return FileCopyCommonJNI::AFArchivePurge(env, jclz, in_archiveJobScript);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetArchivedVolumeHandle(JNIEnv *env, jclass clz, jstring strVolume)
{
	return FileCopyCommonJNI::GetArchivedVolumeHandle(env, clz, strVolume);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetArchiveChildrenCount(JNIEnv *env, jclass clz,jlong lVolumeHandle,jstring strCatalogPath, jobject childCount)
{
	return FileCopyCommonJNI::GetArchiveChildrenCount(env, clz, lVolumeHandle, strCatalogPath, childCount);
}
//public static native long browseArchiveCatalogChildren(
//			long inVolumeHandle, String inCatalogFilePath,List<JArchiveCatalogDetail> archiveCatalogItems);

//Respective C++ API      AFFILECATALOG_API DWORD  GetChildren(HANDLE VolumeHandle, PTCHAR szPath,vector <pCatalogChildItem> &versions);
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_browseArchiveCatalogChildren(JNIEnv *env, jclass clz,jlong lVolumeHandle,jstring inCatalogFilePath, jobject archiveCatalogItems)
{
	return FileCopyCommonJNI::browseArchiveCatalogChildren(env, clz, lVolumeHandle, inCatalogFilePath, archiveCatalogItems);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_browseArchiveCatalogChildrenEx(JNIEnv *env, jclass clz,jlong lVolumeHandle,jstring inCatalogFilePath,jlong in_lIndex,jlong in_lCount, jobject archiveCatalogItems)
{
	return FileCopyCommonJNI::browseArchiveCatalogChildrenEx(env, clz, lVolumeHandle, inCatalogFilePath, in_lIndex, in_lCount, archiveCatalogItems);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CanArchiveJobBeSubmitted(JNIEnv *env, jclass clz, jobject inout_jArchiveJob)
{
	return FileCopyCommonJNI::AFCanArchiveJobBeSubmitted(env, clz, inout_jArchiveJob);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CanArchiveSourceDeleteJobBeSubmitted(JNIEnv *env, jclass clz, jobject inout_jArchiveJob)
{
	return FileCopyCommonJNI::AFCanArchiveSourceDeleteJobBeSubmitted(env, clz, inout_jArchiveJob);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_searchArchiveCatalogChildren(JNIEnv *env, jclass clz,jstring inFileName,jstring inHostName,jstring inSearchpath,jobject inArchiveDestConfig,
																							   jlong in_lSearchOptions,jlong in_lIndex,jlong in_lCount, jobject archiveCatalogItems)
{
	return FileCopyCommonJNI::searchArchiveCatalogChildren(env, clz, inFileName, inHostName, inSearchpath, inArchiveDestConfig, in_lSearchOptions, in_lIndex, in_lCount, archiveCatalogItems);
}
void DescribeException(JNIEnv *env)
{
	if (env->ExceptionCheck())
	{
		jthrowable e = env->ExceptionOccurred();

		char buf[1024];
		strnset(buf, 0, 1024);

		// have to clear the exception before JNI will work again.
		env->ExceptionClear();

		jclass eclass = env->GetObjectClass(e);

		jmethodID mid = env->GetMethodID(eclass, "toString", "()Ljava/lang/String;");

		jstring jErrorMsg = (jstring) env->CallObjectMethod(e, mid);
		wchar_t* pMessage = JStringToWCHAR(env, jErrorMsg);
		WCHAR szMessage[1024] = {_T('\0')};
		wcscpy(szMessage, (const WCHAR *)pMessage);
		
		printf("\nException in finding the WebServiceClient class\n");
		return;
	}
}

//JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetPreviewFilesHandle(JNIEnv *env, jclass clz, jstring strfilePath,jobject jHandle,jobject jFilesCnt)
//{
//	wchar_t* szArchiveFilePath = JStringToWCHAR(env, strfilePath);
//	HANDLE hPreviewHandle;
//	DWORD dwFilesCount;
//	DWORD dwRet;
//	//DWORD dwRet = AFCreatePreviewFilesHandle(szArchiveFilePath,hPreviewHandle,dwFilesCount);
//
//	if(dwRet == 0)
//	{
//		AddUINT2JRWLong(env, dwFilesCount, &jFilesCnt);
//	}
//
//	return (jlong)hPreviewHandle;
//}
//
//JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_ReadPreviewFilesList(JNIEnv *env, jclass clz, jlong in_lHandle,jlong in_lIndex,jlong in_lCount,jobject archiveFilesList)
//{
//	wchar_t* szArchiveFilePath = JStringToWCHAR(env, strfilePath);
//	HANDLE hPreviewHandle;
//	DWORD dwFilesCount;
//
//	//DWORD dwRet = AFCreatePreviewFilesHandle(szArchiveFilePath,hPreviewHandle,dwFilesCount);
//
//	if(dwRet == 0)
//	{
//		AddUINT2JRWLong(env, dwFilesCount, &jFilesCnt);
//	}
//
//	return (jlong)hPreviewHandle;
//}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetArchiveJobsInfo(JNIEnv *env, jclass clz, jobject inout_jArchiveJob,jobject out_archiveJobsList)
{
	return FileCopyCommonJNI::GetArchiveJobsInfo(env, clz, inout_jArchiveJob, out_archiveJobsList);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetArchiveJobInfoCount(JNIEnv *env, jclass clz, jobject inout_jArchiveJob, jobject jobCount)
{
	return FileCopyCommonJNI::GetArchiveJobInfoCount(env, clz, inout_jArchiveJob, jobCount);
}
//API's to check the archive jobs status
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsArchiveJobRunning(JNIEnv *env, jclass clz)
{
	return FileCopyCommonJNI::IsFileCopyJobRunning2(env, clz, FileCopyCommonJNI::GetArchiveHostName(env, clz));
}
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsFileArchiveJobRunning(JNIEnv *env, jclass clz)
{
	return FileCopyCommonJNI::IsFileArchiveJobRunning2(env, clz, FileCopyCommonJNI::GetArchiveHostName(env, clz));
}
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsArchiveRestoreJobRunning(JNIEnv *env, jclass clz)
{
	return FileCopyCommonJNI::IsArchiveRestoreJobRunning2(env, clz, FileCopyCommonJNI::GetArchiveHostName(env, clz));
}
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsArchivePurgeJobRunning(JNIEnv *env, jclass clz)
{
	return FileCopyCommonJNI::IsArchivePurgeJobRunning2(env, clz, FileCopyCommonJNI::GetArchiveHostName(env, clz));
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsArchiveCatalogSyncJobRunning(JNIEnv *env, jclass clz)
{
	return FileCopyCommonJNI::IsArchiveCatalogSyncJobRunning2(env, clz, FileCopyCommonJNI::GetArchiveHostName(env, clz));
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsArchiveSourceDeleteJobRunning(JNIEnv *env, jclass clz)
{
	LogFile(L"IsArchiveSourceDeleteJobRunning start");
	BOOL bRet = FALSE;

	//Manorama: Add call to the api to figure out if a source delete job is running on this machine or not.

	if (bRet)
		LogFile(L"Archive Source Delete job running");
	else
		LogFile(L"Archive Source Delete job is not running");

	return bRet;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetArchiveDNSHostName(JNIEnv *env, jclass jclz)
{
	return FileCopyCommonJNI::GetArchiveDNSHostName(env, jclz);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetArchiveDestinationVolumes
(JNIEnv *env, jclass clz, jstring hostName, jobject jDestInfo, jobject volumeList)
{
	return FileCopyCommonJNI::AFGetArchiveDestinationVolumes(env, clz, hostName, jDestInfo, volumeList);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFArchive
(JNIEnv *env, jclass jclz, jobject in_archiveJobScript)
{
	return FileCopyCommonJNI::AFArchive(env, jclz, in_archiveJobScript);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFArchiveRestore
(JNIEnv *env, jclass jclz, jobject bkpJS)
{
	return FileCopyCommonJNI::AFArchiveRestore(env, jclz, bkpJS);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFArchiveCatalogSync
(JNIEnv *env, jclass jclz, jobject in_archiveCatalogSyncJobScript)
{
	return FileCopyCommonJNI::AFArchiveCatalogSync(env, jclz, in_archiveCatalogSyncJobScript);
}

// by khaga01
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFArchiveSourceDelete
(JNIEnv *env, jclass jclz, jobject in_archiveJobScript)
{
	LogFile(L"AFArchiveSourceDelete Start");
	DWORD dwRet = 0;
	AFARCHIVEJOBSCRIPT afArchiveJS;
	memset(&afArchiveJS, 0, sizeof(AFARCHIVEJOBSCRIPT));
	FileCopyCommonJNI::ArchiveJobScript2AFJOBSCRIPT(env, &afArchiveJS, &in_archiveJobScript);
	dwRet = AFArchiveSourceDelete(&afArchiveJS, NULL, NULL);
	TCHAR szErrMsg[MAX_PATH] = { _T('\0') };
	_stprintf(szErrMsg, L"AFArchiveSourceDelete returned dwRet [%d]", dwRet);
	LogFile(szErrMsg);
	FileCopyCommonJNI::FreeArchiveJobScript(&afArchiveJS);
	LogFile(L"AFArchiveSourceDelete End");
	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFSCheckCatalogExist
(JNIEnv *env, jclass clz, jstring jdest, jlong jsessNum, jobject jretAttray)
{
	wchar_t* pwszDest = JStringToWCHAR(env, jdest);

	CATALOG_INFO_LIST_EX catalog_info_list;
	DWORD dwRet = AFSCheckCatalogExistEx(pwszDest, jsessNum, catalog_info_list);

	if (dwRet == S_OK)
	{
		for (CATALOG_INFO_LIST_EX::iterator iter = catalog_info_list.begin(); iter != catalog_info_list.end();  iter++)
		{
			Add_CatalogInfo2List(env, &jretAttray, &(*iter));
		}	
	}

	if (pwszDest != NULL)
	{
		free(pwszDest);
	}

	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckGrtSession
(JNIEnv *env, jclass clz, jstring jdest, jlong jsessNum)
{
	long result = 0;
	wchar_t* pwszDest = JStringToWCHAR(env, jdest);

	GRT_SESSIONINFO grtSessionInfo = {0};	

	// compare the OS information between current machine and it where the session is backed up from 
	DWORD dwRet = AFCheckGrtSession(pwszDest, jsessNum, grtSessionInfo);

	if (dwRet == S_OK)
	{
		if (grtSessionInfo.isSameOStype && grtSessionInfo.isSameMajor)
		{
			result = 1;  // the server than generated the session and the current D2D server has the same OS
		}
		else
		{
			result = -1; // not same OS, cannot be restored
		}
	}

	if (pwszDest != NULL)
	{
		free(pwszDest);
	}

	return result;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_launchCatalogJob(JNIEnv * env, jclass cls, jlong id, jlong type, jstring username, jstring pass)
{
	HMODULE hMountCatalogMgrDll = NULL; 
	long ret = -1;

	wchar_t* name	= JStringToWCHAR(env, username);
	wchar_t* password	= JStringToWCHAR(env, pass);

	ret = AFStartCatalogGenerator(type, id, NULL, name, password, L"");

	if(name != NULL)
		free(name);

	if(password != NULL)
		free(password);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_launchVSphereCatalogJob(JNIEnv * env, jclass cls, jlong id, jlong type, jstring vmIndentification,jstring username, jstring pass)
{
	HMODULE hMountCatalogMgrDll = NULL; 
	long ret = -1;

	wchar_t* name	= JStringToWCHAR(env, username);
	wchar_t* password	= JStringToWCHAR(env, pass);
	wchar_t* vmInstanceUUID	= JStringToWCHAR(env, vmIndentification);

	ret = AFStartCatalogGenerator(type, id, NULL, name, password,vmInstanceUUID);

	if(name != NULL)
		free(name);

	if(password != NULL)
		free(password);

	if(vmInstanceUUID != NULL)
		free(vmInstanceUUID);

	return ret;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_addGRTCatalogInfo(JNIEnv *env, jclass cls, jstring backupDest, jstring username, jstring pass, jstring domain, 
	jlong sessionNum, jlong subSession, jstring sessionPass, jobject grtEdbList, jstring vmInstanceUUID)
{
	wchar_t* pDest = JStringToWCHAR(env, backupDest);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, username);
	wchar_t* pPwd = JStringToWCHAR(env, pass);
	wchar_t* sessionPwd = JStringToWCHAR(env, sessionPass);
	wchar_t* pVMInstanceUUID = JStringToWCHAR(env, vmInstanceUUID);
	DWORD ret = -1;

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
	//save grt catalog information
	vector<wstring> vectorGRTEdb;
	JStrListToCVector(env, cls,grtEdbList, vectorGRTEdb);
	ret = AFSaveJobScriptForExchGRT2(info, sessionNum, subSession, sessionPwd, pVMInstanceUUID, vectorGRTEdb);

	if(sessionPwd != NULL)
		free(sessionPwd);

	if(pVMInstanceUUID != NULL)
		free(pVMInstanceUUID);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFSaveJS4FSOndemand(JNIEnv *env, jclass cls, jstring backupDest, jstring username, jstring pass, jstring domain, jlong sessionNum, jstring vmInstanceID, jlong subSession, jstring sessionPass)
{
	wchar_t* pDest = JStringToWCHAR(env, backupDest);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, username);
	wchar_t* pPwd = JStringToWCHAR(env, pass);
	wchar_t* pVMInstanceID= JStringToWCHAR(env,vmInstanceID);
	wchar_t* sessionPwd = JStringToWCHAR(env, sessionPass);
	DWORD ret = -1;

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
	//save grt catalog information
	ret = AFSaveJS4FSOndemand(info, sessionNum, pVMInstanceID, subSession, sessionPwd);

	if(sessionPwd != NULL)
		free(sessionPwd);
	return ret;
}
//For D2D data sync to Edge
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetArchiveCacheFileName4Trans(JNIEnv *env, jclass clz)
{
	jstring  cacheFileName;
	wstring _cacheFileName = L"";
	DWORD dwRet;


	_cacheFileName = AFIGetArchiveCacheFileName4Trans();


	if(!_cacheFileName.empty())
		cacheFileName = WCHARToJString(env, (wchar_t *)_cacheFileName.c_str());
	else
		cacheFileName = WCHARToJString(env, (wchar_t *)(L""));


	return cacheFileName;
}

//For D2D data sync to Edge
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DeleteArchiveCacheFileTrans(JNIEnv *env, jclass clz)
{
	DWORD dwRet;


	dwRet = AFIDeleteArchiveCacheFileTrans();


	return 0;
}

/*scheduled export*/

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFEnabledScheduledExport(JNIEnv *env, jclass clazz, jboolean enabled) //<sonmi01>2014-7-29 ###???
{
	DWORD ret = -1;

	ret = AFEnableShExp((BOOL)enabled);

	return ret;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckShExpBackupNum(JNIEnv *env, jclass clazz, jint interval) //<sonmi01>2014-7-29 ###???
{
	BOOL ret = AFCheckShExpBackupNum(interval);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFAddSucceedBackupNum(JNIEnv *env, jclass clazz) //<sonmi01>2014-7-29 ###???
{
	DWORD ret = AFAddSucceedBackupNum();
	return ret;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFEnabledScheduledExportVM(JNIEnv *env, jclass clazz, jboolean enabled, jstring nodeInstanceId) //<sonmi01>2014-7-29 ###???
{
	DWORD ret = -1;

	WCHAR * pNodeInstanceId = JStringToWCHAR(env, nodeInstanceId);
	ret = AFEnableShExp((BOOL)enabled, pNodeInstanceId);
	free(pNodeInstanceId); pNodeInstanceId = NULL;

	return ret;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckShExpBackupNumVM(JNIEnv *env, jclass clazz, jint interval, jstring nodeInstanceId) //<sonmi01>2014-7-29 ###???
{
	WCHAR * pNodeInstanceId = JStringToWCHAR(env, nodeInstanceId);
	BOOL ret = AFCheckShExpBackupNum(interval, pNodeInstanceId);
	free(pNodeInstanceId); pNodeInstanceId = NULL;

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFAddSucceedBackupNumVM(JNIEnv *env, jclass clazz, jstring nodeInstanceId) //<sonmi01>2014-7-29 ###???
{
	WCHAR * pNodeInstanceId = JStringToWCHAR(env, nodeInstanceId);
	DWORD ret = AFAddSucceedBackupNum(pNodeInstanceId);
	free(pNodeInstanceId); pNodeInstanceId = NULL;
	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHyperVVMPathType(JNIEnv *env, jclass clazz, jstring vmPath)
{
	Proc_HADT_GetHypervVMPathType func = (Proc_HADT_GetHypervVMPathType)DynGetProcAddress(L"HATransClientProxy.dll", "HADT_GetHypervVMPathType");
	return func(JStringToWCHAR(env, vmPath));
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFIGetSessPwdFromKeyMgmtBySessNum(JNIEnv *env, jclass clz, jlong sessionNum, jstring destPath)
{
	WCHAR sessPwd[24];
	DWORD sessPwdLen = _countof(sessPwd);

	WCHAR *sessDestPath = JStringToWCHAR(env, destPath);

	DWORD ret = AFIGetSessPwdFromKeyMgmtBySessNum(sessPwd, &sessPwdLen, sessDestPath, sessionNum);
	
	if(ret == 0)
	{
		jstring pwd = WCHARToJString(env, sessPwd);
		return pwd;
	}

	return NULL;
}

/* START ARCHIVE:NEW Interfaces */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_verifyBucketName(JNIEnv *env, jclass clz, jobject jCloudDestInfo)
{
	return FileCopyCommonJNI::verifyBucketName(env, clz, jCloudDestInfo);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getCloudBuckets(JNIEnv *env, jclass clz, jobject jCloudDestInfo,jobject out_Buckets)
{
	return FileCopyCommonJNI::getCloudBuckets(env, clz, jCloudDestInfo, out_Buckets);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getCloudRegions(JNIEnv *env, jclass clz,jobject jCloudDestInfo,jobject out_CloudRegions)
{
	return FileCopyCommonJNI::getCloudRegions(env, clz, jCloudDestInfo, out_CloudRegions);
}
//madra04 modifications..
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_validateEncryptionSettings(JNIEnv *env, jclass clz,jobject in_archiveJobScript,jobject out_jErrorcode,jobject out_jCCIErrorCode)
{
	return FileCopyCommonJNI::validateEncryptionSettings(env, clz, in_archiveJobScript, out_jErrorcode, out_jCCIErrorCode);
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getRegionForBucket(JNIEnv *env, jclass clz,jobject jCloudDestInfo,jobject out_jrwRet)
{
	return FileCopyCommonJNI::getRegionForBucket(env, clz, jCloudDestInfo, out_jrwRet);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_testConnection(JNIEnv *env, jclass clz,jobject jCloudDestInfo)
{
	return FileCopyCommonJNI::testConnection(env, clz, jCloudDestInfo);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_testBIConnection(JNIEnv *env, jclass clz, jobject jCloudDestInfo)
{
	return FileCopyCommonJNI::testBIConnection(env, clz, jCloudDestInfo);
}

//archive catalog sync api 
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetLastArchiveCatalogUpdateTime(JNIEnv *env, jclass clz,jobject jDestInfo,jobject out_CatalogDetails)
{
	return FileCopyCommonJNI::GetLastArchiveCatalogUpdateTime(env, clz, jDestInfo, out_CatalogDetails);
}

/*VCM-VMware*/
//modified by zhepa02 at 2015-04-14 ,add the strThumbprint parameter.
JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_DriverInjectSingleVMDK
(JNIEnv *env, jclass klass, jstring esxServer, jstring username, jstring password, 
	jstring morefId,jint iPort, jobject exParams, jstring vmdkUrl,jstring hostname,jstring failovermode,
	jstring key,jstring value,jstring jobID, jstring afguid)
{
	
	wstring wstrEsx = JStringToWString(env, esxServer);	
	wstring wstrUsername = JStringToWString(env, username);
	wstring wstrPassword = JStringToWString(env, password);
	wstring wstrMorefId = JStringToWString(env, morefId);
	wstring wstrVmdkUrl = JStringToWString(env, vmdkUrl);

	wstring wstrHostname = JStringToWString(env, hostname);
	wstring wstrFailoverMode = JStringToWString(env, failovermode);
	wstring wstrKey = JStringToWString(env, key);
	wstring wstrValue = JStringToWString(env, value);
	wstring wstrJOBId = JStringToWString(env, jobID);
	wstring wstrAfguid = JStringToWString(env, afguid);
	//wstring wstrThumbprint = JStringToWString(env, thumbprint);		//modified by zhepa02 at 2015-04-14 ,add the strThumbprint parameter.

	VMDK_CONNECT_MORE_PARAMS moreParams;

	ConvertVMDKConnParams(env, exParams, moreParams);

	DWORD result = 0;
	result = DriverInjectSingleVMDK(wstrEsx, wstrUsername, wstrPassword, wstrMorefId, wstrHostname, wstrFailoverMode, iPort, moreParams, wstrVmdkUrl, wstrKey, wstrValue, wstrJOBId, wstrAfguid);

	return (jlong)result;
	
}


JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckFolderContainsBackup(JNIEnv *env, jclass clazz, jstring folder)
{
	wstring destFolder = JStringToWString(env, folder);
	IFileListHandler* fileHandler = NULL;

	if(CreateIFileListHandler(&fileHandler))
	{
		LogFile(L"Create FileListHandler failed for schedule export destination check");     
		return false;
	}
	BOOL bRet = fileHandler->CheckFolderContainBackups(destFolder);
	fileHandler->Release();
	return bRet;
	
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getActiveJobs(JNIEnv *env, jclass clz, jobject jretArray)
{
	vector<JOB_CONTEXT> jobs;
	DWORD dwRet = AFRetrieveActiveJobs(jobs);
	if(dwRet == 0)
	{
		jclass class_JJobContext = env->FindClass("com/ca/arcflash/webservice/jni/model/JJobContext");	
		jmethodID jJobContext_constructor = env->GetMethodID(class_JJobContext, "<init>", "()V");
	
		jclass class_ArrayList = env->GetObjectClass(jretArray);
		jmethodID addMethod = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");
		
		for(vector<JOB_CONTEXT>::iterator jobIter = jobs.begin(); jobIter != jobs.end(); jobIter ++)
		{	
			jobject jobContext = env->NewObject(class_JJobContext, jJobContext_constructor);
			//JOB_CONTEXT2JobContext(env, jobIter, jobContext, class_JJobContext);
			jmethodID setJobIDMethod = env->GetMethodID(class_JJobContext, "setDwJobId", "(J)V");
			jmethodID setQueueTypeMethod = env->GetMethodID(class_JJobContext, "setDwQueueType", "(J)V");
			jmethodID setJobTypeMethod = env->GetMethodID(class_JJobContext, "setDwJobType", "(J)V");
			jmethodID setProcessIDMethod = env->GetMethodID(class_JJobContext, "setDwProcessId", "(J)V");
			jmethodID setShrmemIDMethod = env->GetMethodID(class_JJobContext, "setDwJMShmId", "(J)V");
            jmethodID setLauncherInstanceUUIDMethod = env->GetMethodID(class_JJobContext, "setLauncherInstanceUUID", "(Ljava/lang/String;)V");
			jmethodID setExecuterInstanceUUIDMethod = env->GetMethodID(class_JJobContext, "setExecuterInstanceUUID", "(Ljava/lang/String;)V");
			jmethodID setDwLauncherMethod = env->GetMethodID(class_JJobContext, "setDwLauncher", "(J)V");
			jmethodID setDwPriorityMethod = env->GetMethodID(class_JJobContext, "setDwPriority", "(J)V");
			jmethodID setDwMasterJobIdMethod = env->GetMethodID(class_JJobContext, "setDwMasterJobId", "(J)V");
			jmethodID setGeneratedDestinationMethod = env->GetMethodID(class_JJobContext, "setGeneratedDestination", "(Ljava/lang/String;)V");
	
			env->CallVoidMethod(jobContext, setJobIDMethod, (jlong)jobIter->dwJobId);
			env->CallVoidMethod(jobContext, setQueueTypeMethod, (jlong)jobIter->dwQueueType);
			env->CallVoidMethod(jobContext, setJobTypeMethod, (jlong)jobIter->dwJobType);
			env->CallVoidMethod(jobContext, setProcessIDMethod, (jlong)jobIter->dwProcessId);
			env->CallVoidMethod(jobContext, setShrmemIDMethod, (jlong)jobIter->dwJMShmId);
			env->CallVoidMethod(jobContext, setDwLauncherMethod, (jlong)jobIter->ulJobAttribute);
			env->CallVoidMethod(jobContext, setLauncherInstanceUUIDMethod,WCHARToJString(env, jobIter->wstrLauncherInstanceUUID));
			env->CallVoidMethod(jobContext, setExecuterInstanceUUIDMethod,WCHARToJString(env, jobIter->wstrNodeName));
			env->CallVoidMethod(jobContext, setDwPriorityMethod, (jlong)jobIter->dwPriority);
			env->CallVoidMethod(jobContext, setDwMasterJobIdMethod, (jlong)jobIter->dwMasterJobId);
			env->CallVoidMethod(jobContext, setGeneratedDestinationMethod, WCHARToJString(env, jobIter->generatedDestination));

			env->CallBooleanMethod(jretArray, addMethod, jobContext);
		}

		if(class_JJobContext != NULL) env->DeleteLocalRef(class_JJobContext);
	}
	return dwRet;
}
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getVMwareToolStatus(JNIEnv *env, jclass clz){

	bool isInstalled;
	bool isRunning;
	int ret = CheckVMToolStatus(isInstalled, isRunning);
	if (ret)
	{
		ThrowWSJNIException(env, clz, env->NewStringUTF("Error occurs in CheckVMToolStatus."), ret);
		return -1;
	}

	jint installStatus = 0;
	if (isInstalled)
		installStatus += 1;

	if(isRunning)
		installStatus += 2;

	return installStatus;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getVMApplicationStatus(JNIEnv *env, jclass clz,jstring vmInstaceUUID,jobject appStatus){
	
	long ret = -1;

	wchar_t* vmInstanceUUID	= JStringToWCHAR(env, vmInstaceUUID);
	std::wstring stamp;
	
	vector<CVMWriterMetadataItem> appInfo;
	ret = AFGetVMAppInfo(stamp,appInfo,vmInstanceUUID);
	if(ret==0){
		ret = Convert2ApplicationStatus(env,appInfo,appStatus);	
	}

	if(vmInstanceUUID != NULL)
		free(vmInstanceUUID);

	return ret;
	
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetMntFromVolumeGuid(JNIEnv *env, jclass clz, jstring strVolumeGUID)
{
	long ret = -1;
	wstring volumeGUID = JStringToWString(env,strVolumeGUID);
	wstring mntPath;

	ret = AFGetMntFromVolumeGuid(volumeGUID,mntPath);
	if(ret == 0)
		return WCHARToJString(env,mntPath);
	else
		return strVolumeGUID;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetMntSess(JNIEnv *env, jclass clz, jobject sessionList)
{
	vector<MNT_SESS> sessions;
	long ret = AFGetMntSess(sessions);
	
	if(ret == 0)
	{
		ret = Convert2MountSession(env, sessionList, sessions);
	}

	return ret;

}




JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetFileCopyCatalogPathy(JNIEnv *env, jclass, jstring MachineName, jlong ProductType)

{
	return FileCopyCommonJNI::GetFileCopyCatalogPathy(env,0, MachineName, ProductType);
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_VMWareJNI_GetVDDKRegistryKey(JNIEnv *env,jclass, jstring keyName)
{
	wstring szKeyName = JStringToWString(env, keyName);
	return QueryVDDKRegKey((WCHAR*)szKeyName.c_str());
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckDestChainAccess(JNIEnv *env, jclass clazz, 
				jobject currentConn, jobject baseConn, jobject errConn, jboolean jbPrev)
{
	NET_CONN_INFO cCurrConn;	
	memset(&cCurrConn,0,sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env,&currentConn,cCurrConn);

	NET_CONN_INFO cBaseConn;
	memset(&cBaseConn,0,sizeof(NET_CONN_INFO));

	NET_CONN_INFO cErrConn;	
	memset(&cErrConn,0,sizeof(NET_CONN_INFO));

	BOOL ret = AFCheckDestChainAccess(cCurrConn, cBaseConn, cErrConn, jbPrev);
	if(!ret)
	{
		ret = ConnInfo2JConnInfo(env, baseConn, cBaseConn);
		ret = ConnInfo2JConnInfo(env, errConn, cErrConn);
	}

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFCheckUpdateNetConn
	(JNIEnv *env, jclass clz, jobject baseConn,jobject updateConn, jboolean jbPrev)
{
	NET_CONN_INFO cBaseConn;	
	memset(&cBaseConn,0,sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env,&baseConn,cBaseConn);

	NET_CONN_INFO cUpdateConn;
	memset(&cUpdateConn,0,sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env,&updateConn,cUpdateConn);
	//First connect to error destination with new connection information
	DWORD dwRet = 0;
	if(!AFCheckPathAccess(cBaseConn)) 
	{
		dwRet = AFCreateConnection(cBaseConn);
		if(dwRet)
		{
			wprintf(L"get failed[%d]\n", dwRet);
		}
	}
	
	dwRet = AFCreateConnection(cUpdateConn);
	if(dwRet)
	{
		wprintf(L"get failed[%d]\n", dwRet);
	}
	return AFCheckUpdateNetConn(cBaseConn, cUpdateConn, jbPrev);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFRetrieveConnections(JNIEnv *env, jclass clz, jobject retConns)
{
	vector<NET_CONN> cConns;
	
	DWORD dwRet = AFRetrieveConnections(cConns);
	if(dwRet == 0)
	{
		jclass class_netconn = env->FindClass("com/ca/arcflash/service/jni/model/JNetConnInfo");	
		jmethodID jConn_constructor = env->GetMethodID(class_netconn, "<init>", "()V");
	
		jclass class_ArrayList = env->GetObjectClass(retConns);
		jmethodID addMethod = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

		for(vector<NET_CONN>::iterator iter = cConns.begin(); iter != cConns.end(); iter ++)
		{
			jobject jConn = env->NewObject(class_netconn, jConn_constructor);
			jmethodID methodSetUser = env->GetMethodID(class_netconn, "setSzUsr", "(Ljava/lang/String;)V");
			jmethodID methodSetDest = env->GetMethodID(class_netconn, "setSzDir", "(Ljava/lang/String;)V");
			
			env->CallVoidMethod(jConn, methodSetUser,WCHARToJString(env, iter->strUser));
			env->CallVoidMethod(jConn, methodSetDest,WCHARToJString(env, iter->strConn));
			env->CallBooleanMethod(retConns, addMethod, jConn);
		}

		if(class_netconn != NULL) env->DeleteLocalRef(class_netconn);
	}

	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFMoveLogs(JNIEnv *env, jclass clz)
{
	return AFMoveLogs();
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFDeleteLicError(JNIEnv *env, jclass clz,jlong licCode)
{
	return AFDeleteLicError(licCode);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CreateEvent(JNIEnv* env, jclass this_class, jstring eventName, jboolean manualReset, jboolean initialState)
{
	wstring strEventName = JStringToWString(env, eventName);
	BOOL bManualReset = manualReset ? TRUE : FALSE;
	BOOL bInitialState = initialState ? TRUE : FALSE;
	DWORD error = 0;

	SECURITY_DESCRIPTOR sd = { 0 };
	::InitializeSecurityDescriptor(&sd, SECURITY_DESCRIPTOR_REVISION);
	::SetSecurityDescriptorDacl(&sd, TRUE, 0, FALSE);
	SECURITY_ATTRIBUTES sa = { 0 };
	sa.nLength = sizeof(SECURITY_ATTRIBUTES);
	sa.lpSecurityDescriptor = &sd;

	HANDLE handle = CreateEvent( &sa, bManualReset, bInitialState, strEventName.c_str() );
	return reinterpret_cast<jlong>(handle);
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SetEvent(JNIEnv* env, jclass this_class, jlong handle)
{
	BOOL ret = SetEvent( reinterpret_cast<HANDLE>(handle) );
	return static_cast<jint>(ret);
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_ResetEvent(JNIEnv* env, jclass this_class, jlong handle)
{
	BOOL ret = ResetEvent( reinterpret_cast<HANDLE>(handle) );
	return static_cast<jint>(ret);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_WaitForSingleObject(JNIEnv* env, jclass this_class, jlong handle, jlong milliSeconds )
{
	HANDLE objHandle = reinterpret_cast<HANDLE>(handle);
	DWORD dwMilliSeconds = static_cast<DWORD>(milliSeconds);
	return WaitForSingleObject( objHandle, dwMilliSeconds );
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_createMutex(JNIEnv* env, jclass this_class, jboolean initiallyOwned, jstring mutexName)
{
	wstring strMutexName = JStringToWString(env, mutexName);
	BOOL bInitialOwner = initiallyOwned ? TRUE : FALSE;

	HANDLE handle = CreateMutex(NULL, bInitialOwner, strMutexName.c_str());
	return reinterpret_cast<jlong>(handle);
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_releaseMutex(JNIEnv* env, jclass this_class, jlong handle)
{
	HANDLE hHandle = reinterpret_cast<HANDLE>(handle);
	BOOL bRelease = ReleaseMutex(hHandle);
	BOOL bClose = CloseHandle(hHandle);
	return bRelease && bClose;
}

void AddVecWriterInfo2List(JNIEnv *env, jobject list, std::vector<WriterInfo> &vList)
{
	jclass class_ArrayList = env->GetObjectClass(list);
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");	
	jclass class_JWriterInfo = env->FindClass("com/ca/arcflash/webservice/jni/model/JWriterInfo");
	jmethodID mid_JVolume_constructor = env->GetMethodID(class_JWriterInfo, "<init>", "()V");

	jfieldID field;
	jstring jstr;

	for(std::vector<WriterInfo>::iterator it = vList.begin(); it != vList.end(); it++)
	{		
		jobject jWriterInfo = env->NewObject(class_JWriterInfo, mid_JVolume_constructor);
		
		SetStringValue2Field(env, jWriterInfo, "wszWriterName", it->wszWriterName);
		SetStringValue2Field(env, jWriterInfo, "wszWriterID", it->wszWriterID);
		SetStringValue2Field(env, jWriterInfo, "wszInstanceName", it->wszInstanceName);
		SetStringValue2Field(env, jWriterInfo, "wszInstanceID", it->wszInstanceID);

		env->CallBooleanMethod(list, id_ArrayList_add, jWriterInfo);

		if (jWriterInfo != NULL) env->DeleteLocalRef(jWriterInfo);			
	}

	if (class_JWriterInfo != NULL) env->DeleteLocalRef(class_JWriterInfo);
	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getApplicationDetailsInESXVM
(JNIEnv* env, jclass this_class, jobject list, jstring esxServerName, jstring esxUsername, jstring esxPassword, jstring vmName, jstring vmVMX, jstring vmUsername, jstring vmPassword)
{
	wchar_t* pesxServerName	= JStringToWCHAR(env, esxServerName);
	wchar_t* pesxUsername	= JStringToWCHAR(env, esxUsername);
	wchar_t* pesxPassword	= JStringToWCHAR(env, esxPassword);
	wchar_t* pvmName		= JStringToWCHAR(env, vmName);
	wchar_t* pvmVMX			= JStringToWCHAR(env, vmVMX);
	wchar_t* pvmUsername	= JStringToWCHAR(env, vmUsername);
	wchar_t* pvmPassword	= JStringToWCHAR(env, vmPassword);

	vector<WriterInfo> vecAppDetails;
	LONG lRet = AFGetApplicationDetailsInESXVM(vecAppDetails, pesxServerName, pesxUsername, pesxPassword, pvmName, pvmVMX, pvmUsername, pvmPassword);
	if (lRet == 0)
	{
		AddVecWriterInfo2List(env, list, vecAppDetails);
	}

	return lRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getVMInformation
(JNIEnv* env, jclass this_class, jobject vmInfo, jstring esxServerName, jstring esxUsername, jstring esxPassword, jstring vmName, jstring vmVMX, jstring vmUsername, jstring vmPassword)
{
	wchar_t* pesxServerName	= JStringToWCHAR(env, esxServerName);
	wchar_t* pesxUsername	= JStringToWCHAR(env, esxUsername);
	wchar_t* pesxPassword	= JStringToWCHAR(env, esxPassword);
	wchar_t* pvmName		= JStringToWCHAR(env, vmName);
	wchar_t* pvmVMX			= JStringToWCHAR(env, vmVMX);
	wchar_t* pvmUsername	= JStringToWCHAR(env, vmUsername);
	wchar_t* pvmPassword	= JStringToWCHAR(env, vmPassword);

	VMInfo vminfo;
	memset(&vminfo, NULL, sizeof(vminfo));
	LONG lRet = AFGetESXVMInfo(vminfo, pesxServerName, pesxUsername, pesxPassword, pvmName, pvmVMX, pvmUsername, pvmPassword);
	if (lRet == 0)
	{
		SetStringValue2Field(env, vmInfo, "vmOSVersion", vminfo.wszvmOSVersion); 
		SetBoolValue2Field(env, vmInfo, "setSqlserverInstalled", vminfo.bSQLServerInstalled); 
		SetBoolValue2Field(env, vmInfo, "setExchangeInstalled", vminfo.bExchangeInstalled); 
		SetBoolValue2Field(env, vmInfo, "setHasDynamicDisk", vminfo.bHasDynamicDisk); 
		SetBoolValue2Field(env, vmInfo, "setHasStorageSpaces", vminfo.bHasStorageSpaces); 
	}

	return lRet;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    AFISetPreAllocSpacePercent
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFISetPreAllocSpacePercent
  (JNIEnv * env, jclass jcls, jlong prevalue)
{
	return AFISetPreAllocSpacePercent(prevalue);
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    AFIGetPreAllocSpacePercent
 * Signature: (Lcom/ca/arcflash/webservice/jni/model/JRWLong;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFIGetPreAllocSpacePercent
  (JNIEnv * env, jclass jcls, jobject retValue)
{
	DWORD dwValue = 0;
	DWORD dwRet = AFIGetPreAllocSpacePercent(dwValue);
	if(dwRet == 0){
		jclass jwordCls = env->GetObjectClass(retValue);
		jmethodID jm = env->GetMethodID(jwordCls, "setValue", "(J)V");
		env->CallVoidMethod(retValue, jm, dwValue);
	}
	return dwRet;
}


JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsFirmwareuEFI(JNIEnv *env, jclass clz)
{
	return IsFirmwareuEFI();
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetRestorePointPath
(JNIEnv *env, jclass clz, jstring dest,jstring domain,jstring user,jstring pwd, jstring subPath)
{
	wstring strSubPath = JStringToWString(env, subPath);
	
	DWORD dwRet = 0;
	IRestorePoint *pIRest = NULL;

	NET_CONN_INFO info = getConnectionInfo(env, dest, domain, user, pwd);

	wstring strSessionPath = L"";
	dwRet = CreateIRestorePoint(info,&pIRest);
	if(!dwRet)
	{
		pIRest->GetRestorePointPath(strSubPath,strSessionPath);
		pIRest->Release();
		
	}else
	{
		wprintf(L"create restore point[%s] failed\n", dest);

	}
	return WCHARToJString(env,strSessionPath);;
}



JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetAllMountedRecoveryPointItems(JNIEnv *env, jclass clz, jobject mountedItems)
{
	DWORD dwNum = AFGetAllMntInfoCount();
	if(dwNum == 0)
		return 0;

	AFMOUNTMGR::AFMOUNTINFO *pInfoList = new AFMOUNTMGR::AFMOUNTINFO[dwNum];
	DWORD dwRet = AFGetAllMntInfo(pInfoList, dwNum);
	if(dwRet == 0)
	{
		AddVMOUNT_ITEM2List(env, mountedItems, pInfoList, dwNum);
	}

	delete[] pInfoList;
	pInfoList = NULL;
	return dwRet;
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetMountedRecoveryPointItems(JNIEnv *env, jclass clz, jstring dest,jstring domain,jstring user,jstring pwd, jstring subPath,jobject mountedItems)
{
	NET_CONN_INFO info = getConnectionInfo(env,dest,domain, user,pwd);
	wstring strSubPath = JStringToWString(env, subPath);

	DWORD dwNum = AFGetMntInfoCount(info,strSubPath.c_str());
	if(dwNum == 0)
		return 0;

	AFMOUNTMGR::AFMOUNTINFO *pInfoList = new AFMOUNTMGR::AFMOUNTINFO[dwNum];
	DWORD dwRet = AFGetMntInfo(info, strSubPath.c_str(), pInfoList, dwNum);
	if(dwRet == 0)
	{
		AddVMOUNT_ITEM2List(env, mountedItems, pInfoList, dwNum);
	}

	delete[] pInfoList;
	pInfoList = NULL;
	return dwRet;
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_MountRecoveryPointItem(JNIEnv *env, jclass clz, jstring dest,jstring domain,jstring user,jstring pwd, jstring subPath,
																						 jstring volGUID,jint encryptionType,jstring encryptPassword, jstring mountPath)
{
	DWORD dwRet = 0;
	wstring strSubPath = JStringToWString(env, subPath);
	wstring strVolGuid = JStringToWString(env, volGUID);
	wstring strEncryptPassword = JStringToWString(env, encryptPassword);
	wstring strMountPath = JStringToWString(env, mountPath);

	NET_CONN_INFO info = getConnectionInfo(env,dest,domain, user,pwd);

	AFMOUNTMGR::MOUNT_PARAM mount_Param;
	memset(&mount_Param, 0 ,sizeof(AFMOUNTMGR::MOUNT_PARAM));
	mount_Param.pathInfo = info;
	wcscpy_s(mount_Param.szMnt, strMountPath.c_str());
	wcscpy_s(mount_Param.szSessSubPath, strSubPath.c_str());
	wcscpy_s(mount_Param.szVolGuidName, strVolGuid.c_str());
	PD2D_ENCRYPTION_INFO pEncryption_info = new D2D_ENCRYPTION_INFO;
	memset(pEncryption_info,0, sizeof(D2D_ENCRYPTION_INFO));
	pEncryption_info->uiCryptoType = (UINT16)encryptionType;
	wcscpy_s(pEncryption_info->szSessionPassword, strEncryptPassword.c_str());
	mount_Param.pEncryptInfo = pEncryption_info;

	dwRet = AFMountSession(&mount_Param, NULL);
	delete pEncryption_info;
	return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_MountRecoveryPointItemEx(JNIEnv *env, jclass clz, jobject jMntParams)
{
	jclass class_JMountParams			= env->FindClass("com/ca/arcflash/jni/common/JMountRecoveryPointParams");	
	jfieldID field_rpsHostname			= env->GetFieldID( class_JMountParams, "rpsHostname",		"Ljava/lang/String;" );
	jfieldID field_datastoreName		= env->GetFieldID( class_JMountParams, "datastoreName",		"Ljava/lang/String;" );
	jfieldID field_dest					= env->GetFieldID( class_JMountParams, "dest",				"Ljava/lang/String;" );
	jfieldID field_domain				= env->GetFieldID( class_JMountParams, "domain",			"Ljava/lang/String;" );
	jfieldID field_user					= env->GetFieldID( class_JMountParams, "user",				"Ljava/lang/String;" );	
	jfieldID field_pwd					= env->GetFieldID( class_JMountParams, "pwd",				"Ljava/lang/String;" );
	jfieldID field_subPath				= env->GetFieldID( class_JMountParams, "subPath",			"Ljava/lang/String;" );
	jfieldID field_volGUID				= env->GetFieldID( class_JMountParams, "volGUID",			"Ljava/lang/String;" );
	jfieldID field_encryptionType		= env->GetFieldID( class_JMountParams, "encryptionType",	"I" );
	jfieldID field_encryptPassword		= env->GetFieldID( class_JMountParams, "encryptPassword",	"Ljava/lang/String;" );
	jfieldID field_mountPath			= env->GetFieldID( class_JMountParams, "mountPath",			"Ljava/lang/String;" );

	wchar_t* pRpsHostname	= GetStringFromField( env, &jMntParams, field_rpsHostname );
	wchar_t* pDsName		= GetStringFromField( env, &jMntParams, field_datastoreName );
	wchar_t* pDest			= GetStringFromField( env, &jMntParams, field_dest );
	wchar_t* pDomain		= GetStringFromField( env, &jMntParams, field_domain );
	wchar_t* pUser			= GetStringFromField( env, &jMntParams, field_user );
	wchar_t* pPwd			= GetStringFromField( env, &jMntParams, field_pwd );
	wchar_t* pSubPath		= GetStringFromField( env, &jMntParams, field_subPath );
	wchar_t* pVolGuid		= GetStringFromField( env, &jMntParams, field_volGUID );
	wchar_t* pEncPwd		= GetStringFromField( env, &jMntParams, field_encryptPassword );
	wchar_t* pMountPath		= GetStringFromField( env, &jMntParams, field_mountPath );
	int nEncryptType = 0;
	if( field_encryptionType )
		nEncryptType = env->GetLongField( jMntParams, field_encryptionType );

	NET_CONN_INFO info; ZeroMemory( &info, sizeof(info) );
	if( pDest )
		wcscpy_s( info.szDir, pDest );
	if( pDomain )
		wcscpy_s( info.szDomain, pDomain );
	if( pUser )
		wcscpy_s( info.szUsr, pUser );
	if( pPwd )
		wcscpy_s( info.szPwd, pPwd );

	AFMOUNTMGR::MOUNT_PARAM mount_Param;
	memset(&mount_Param, 0 ,sizeof(AFMOUNTMGR::MOUNT_PARAM));
	mount_Param.pathInfo = info;
	if( pMountPath )
		wcscpy_s(mount_Param.szMnt, pMountPath);
	if( pSubPath )
		wcscpy_s(mount_Param.szSessSubPath, pSubPath );
	if( pVolGuid )
		wcscpy_s(mount_Param.szVolGuidName, pVolGuid);
	if( pRpsHostname )
		wcscpy_s(mount_Param.szRPSServer, pRpsHostname );
	if( pDsName )
		wcscpy_s(mount_Param.szDataStore, pDsName );


	PD2D_ENCRYPTION_INFO pEncryption_info = new D2D_ENCRYPTION_INFO;
	memset(pEncryption_info,0, sizeof(D2D_ENCRYPTION_INFO));
	pEncryption_info->uiCryptoType = (UINT16)nEncryptType;
	if( pEncPwd )
		wcscpy_s(pEncryption_info->szSessionPassword, pEncPwd);
    mount_Param.pEncryptInfo = pEncryption_info;
	
	DWORD dwRet = AFMountSession(&mount_Param, NULL);
	delete pEncryption_info;
	SAFE_FREE(pRpsHostname);
	SAFE_FREE(pDsName);
	SAFE_FREE(pDest);
	SAFE_FREE(pDomain);
	SAFE_FREE(pUser);
	SAFE_FREE(pPwd);
	SAFE_FREE(pSubPath);
	SAFE_FREE(pVolGuid);
	SAFE_FREE(pEncPwd);
	SAFE_FREE(pMountPath);

	return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DisMountRecoveryPointItem(JNIEnv *env, jclass clz, jstring mountPath,jint mountDiskSignature)
{
	DWORD dwRet = 0;
	wstring strMountPath = JStringToWString(env, mountPath);
	dwRet = AFDismount(strMountPath.c_str(), mountDiskSignature);
	return (jlong)dwRet;
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetAvailableMountDriveLetters(JNIEnv *env, jclass clz, jobject avaliableMountDriveLetters)
{
	DWORD dwRet = 0;
	std::vector<wstring> vecLetters;
	dwRet = getAvailableMountDriveLetters(vecLetters);
	AddVWSTRING_ITEM2List(env, avaliableMountDriveLetters, vecLetters);
	return dwRet;
}
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetWindowsTempDir(JNIEnv *env, jclass clz)
{
	DWORD dwRet = 0;
	wstring strTempPath = L"";
	
	dwRet = getWindowsTempDir(strTempPath);
	return WCHARToJString(env, strTempPath);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetMntInfoForVolume(JNIEnv *env, jclass clz,
				          jstring dest,jstring domain,jstring user,jstring pwd,jstring subPath,jstring volGUID, jobject mountVolInfo)
{
	wstring strMountVolumePath = L"";
	NET_CONN_INFO info = getConnectionInfo(env,dest,domain, user,pwd);
	wstring strSubPath = JStringToWString(env,subPath);
	wstring strVolumeGUID = JStringToWString(env,volGUID);

	DWORD dwNum = AFGetMntInfoForVolumeCount(info, strSubPath.c_str(), strVolumeGUID.c_str());
	if(dwNum == 0)
		return 0;

	AFMOUNTMGR::AFMOUNTINFO *pInfoList = new AFMOUNTMGR::AFMOUNTINFO[dwNum];
	DWORD dwRet = AFGetMntInfoForVolume(info,strSubPath.c_str(),strVolumeGUID.c_str(),pInfoList, dwNum);
	if(dwRet == 0)
	{
		AddVMOUNT_ITEM2List(env, mountVolInfo, pInfoList, dwNum);
	}

	delete[] pInfoList;
	pInfoList = NULL;

	return dwRet;
}
/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    CheckIfExistDiskLargerThan2T
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CheckIfExistDiskLargerThan2T
  (JNIEnv * env, jclass jcls){
	HMODULE hMountHACommondll = NULL; 
	UINT ret = 0;
	bool checkResult = false;

	hMountHACommondll = LoadLibrary( L"HaCommonFunc.dll" );
	if (hMountHACommondll == NULL)	{
		return checkResult;
	}

	PFN_CheckIfExistDiskLargerThan2T pfnCheckIfExistDiskLargerThan2T = NULL;

	pfnCheckIfExistDiskLargerThan2T = (PFN_CheckIfExistDiskLargerThan2T)GetProcAddress(hMountHACommondll, "CheckIfExistDiskLargerThan2T");
	ret = (*pfnCheckIfExistDiskLargerThan2T)(checkResult);

	if (hMountHACommondll != NULL) {
		FreeLibrary(hMountHACommondll);
		hMountHACommondll = NULL;
	}

	if (ret != 0) {
		ThrowWSJNIException(env, jcls, env->NewStringUTF("Error occurs in CheckIfExistDiskLargerThan2T."), ret);
		return checkResult;
	}

	return checkResult;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_StartD2dCallback(JNIEnv * env, jclass clz)
{
	CDbgLog logObj(L"NativeFacade");

	//
	XInit();
	logObj.LogW(LL_WAR, 0, L"ikaka XInit()...");
	//
	
	logObj.LogW(LL_WAR, 0, L"_WSJNI_StartD2dCallback: start()...");
	DWORD dwRet = 0;
	CD2DProxyImpl * pobjProxy =CD2DProxyImpl::GetInstance();
	if(pobjProxy)
	{
		pobjProxy->Init(env);
		pobjProxy->Run();
	}

	logObj.LogW(LL_WAR, 0, L"_WSJNI_StartD2dCallback: end()...");
	
	return (jlong)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_StopD2dCallback(JNIEnv * env, jclass clz)
{
	CDbgLog logObj(L"NativeFacade");
	//
	XUnInit();
	logObj.LogW(LL_WAR, 0, L"ikaka XUnInit()...");
	//
	logObj.LogW(LL_WAR, 0, L"WSJNI_StopD2dCallback: start()...");
	DWORD dwRet = 0;
	CD2DProxyImpl * pobjProxy =CD2DProxyImpl::GetInstance();
	if(pobjProxy)
	{
		pobjProxy->UnInit();
	}

	logObj.LogW(LL_WAR, 0, L"WSJNI_StopD2dCallback: end()...");
	return (jlong)dwRet;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_StartNICMonitor(JNIEnv * env, jclass clz)
{
	CDbgLog logObj(L"NativeFacade");

	logObj.LogW(LL_WAR, 0, L"Start routinue to monitor networ adapters");
	CNICMonitor* pNicMonitor = CNICMonitor::CreateInstance();
	pNicMonitor->Start( );
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_StopNICMonitor(JNIEnv * env, jclass clz)
{
	CDbgLog logObj(L"NativeFacade");
	
	logObj.LogW(LL_INF, 0, L"Stop network adapter monitor routine");

	CNICMonitor* pNicMonitor = CNICMonitor::CreateInstance();
	pNicMonitor->Stop( );
	CNICMonitor::DeleteInstance();

	logObj.LogW(LL_INF, 0, L"Network adapter monitor routine is stopped");
}


JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_StartClusterMonitor(JNIEnv *, jclass)
{
	CDbgLog logObj(L"NativeFacade");
	DWORD dwDisabled = 0;
	GetSwitchDWORDFromReg(SWT_WEBSERVICE_KEYNAME_DISABLECLUSTERMONITOR, dwDisabled, SWT_WEBSERVICE_MODULENAME, 0);
	if (dwDisabled != 0)
	{
		logObj.LogW(LL_WAR, 0, L"Cluster monitor is disabled.");
		return;
	}
	else
	{
		logObj.LogW(LL_WAR, 0, L"Start routinue to monitor cluster shared disks");
		ClusterMonitor* pClusMonitor = ClusterMonitor::CreateInstance();
		pClusMonitor->StartMonitor();
	}
}


JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_StopClusterMonitor(JNIEnv *, jclass)
{
	CDbgLog logObj(L"NativeFacade");
	DWORD dwDisabled = 0;
	GetSwitchDWORDFromReg(SWT_WEBSERVICE_KEYNAME_DISABLECLUSTERMONITOR, dwDisabled, SWT_WEBSERVICE_MODULENAME, 0);
	if (dwDisabled != 0)
	{
		logObj.LogW(LL_WAR, 0, L"Cluster monitor is disabled, do not have to stop it.");
		return;
	}
	else
	{
		logObj.LogW(LL_INF, 0, L"Stop cluster shared disk monitor routine");

		ClusterMonitor* pClusMonitor = ClusterMonitor::CreateInstance();
		pClusMonitor->StopMonitor();
		ClusterMonitor::DeleteInstance();

		logObj.LogW(LL_INF, 0, L"Cluster shared disk monitor routine is stopped");
	}
}

//bccma01 //20415776
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_UnMountVolume(JNIEnv *env, jclass jclz, jstring jsDiskSign, jlong jhandle)
{
	jint jRet = JNI_OK;

	wchar_t* diskSignature = JStringToWCHAR(env, jsDiskSign);

	HANDLE handle = HANDLE(jhandle);
	//20415776
	if(diskSignature)
	{
		jRet = UnMountVolume(handle,diskSignature);
	}

	if(handle)
	{
		jRet = ExitGRTMounter(handle);
	}

	if(diskSignature)
	{
		free(diskSignature);
		diskSignature = NULL;
	}

	return jRet;
}
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_browseFileChildren
(JNIEnv *env, jclass jclz,jstring volumeGUID, jstring filePath, jobject retArr)
{	
	wchar_t* szMountGUID = JStringToWCHAR(env, volumeGUID);
	wchar_t* filepath = JStringToWCHAR(env, filePath);
	
	UINT Cnt; 
	PDetailW detail = GetFileChildren(szMountGUID, filepath, &Cnt);	
	if(detail == NULL)
		return 0;
	for(UINT i = 0; i < Cnt; i++) {				
		AddDetailW2List(env, &retArr, &detail[i]);				
	}
	HeapFree(GetProcessHeap(), 0, detail);
	
	if(szMountGUID != NULL)
	{
		free(szMountGUID);
		szMountGUID = NULL;
	}
	if(filepath != NULL)
	{
		free(filepath);
		filepath = NULL;
	}
	return JNI_OK;
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetFileChildrenEx
(JNIEnv *env, jclass jclz,jstring volumeGUID, jstring filePath, jint jnStart, jint jnRequest ,jobject jnCnt, jobject retArr)
{	
	wchar_t* szMountGUID = JStringToWCHAR(env, volumeGUID);
	wchar_t* filepath = JStringToWCHAR(env, filePath);
	
	UINT Cnt; 
	BOOL bGetCount = FALSE;
	PDetailW detail = GetFileChildrenEx(szMountGUID, filepath, &Cnt,bGetCount,jnStart,jnRequest);	
	if(detail == NULL)
		return 0;

	for(UINT i = 0; i < Cnt; i++) {				
		AddDetailW2List(env, &retArr, &detail[i]);				
	}
	AddUINT2JRWLong(env, Cnt, &jnCnt);

	HeapFree(GetProcessHeap(), 0, detail);
	
	if(szMountGUID != NULL)
	{
		free(szMountGUID);
		szMountGUID = NULL;
	}
	if(filepath != NULL)
	{
		free(filepath);
		filepath = NULL;
	}
	return JNI_OK;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetFileChildrenCount(JNIEnv *env, jclass jclz, jstring mountVolGUID, jstring filePath, jobject jnCnt)
{
	wchar_t* szMountGUID = JStringToWCHAR(env, mountVolGUID);
	wchar_t* filepath = JStringToWCHAR(env, filePath);
	UINT Cnt = GetFileChildrenCount(szMountGUID, filepath);

	AddUINT2JRWLong(env, Cnt, &jnCnt);

	if(szMountGUID != NULL)
	{
		free(szMountGUID);
		szMountGUID = NULL;
	}
	if(filepath != NULL)
	{
		free(filepath);
		filepath = NULL;
	}
	
	return JNI_OK;
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_MountVolume(JNIEnv *env, jclass jclz, jstring userName, jstring passWord, jstring rootFolder, jlong sessionNumber,jstring volumeGUID, jobject jsVolMount, jstring encryptKey )
{	
	wchar_t* rootfolder = JStringToWCHAR(env, rootFolder);
	wchar_t* username = JStringToWCHAR(env, userName);
	wchar_t* password = JStringToWCHAR(env, passWord);
	wchar_t* volumeguid = JStringToWCHAR(env, volumeGUID);
	wchar_t* encryptkey = NULL;
	if(encryptKey != NULL)
		encryptkey = JStringToWCHAR(env, encryptKey);

	jlong jRet = JNI_ERR ;

	SESSION_INFO si = Convert2SessionInfo(rootfolder,username,password,sessionNumber);

	WCHAR szDiskSignature[MAX_PATH] ={0};
	WCHAR szMountGUID[MAX_PATH] = {0};	

	//if (gHandle)
	//	gHandle = NULL;
	//20415776
	HANDLE handle = InitGRTMounter();
	if(handle != NULL) 
	{
		DWORD dwRet	= MountVolume(handle,si,volumeguid, encryptkey, 0, 0, 0, NULL, szDiskSignature,szMountGUID);
		if(dwRet == ERROR_SUCCESS) 
		{
			if( (wcslen(szDiskSignature) >2) && (wcslen(szMountGUID) >2 ) )
			{
				Add2JMountPoint(env,szMountGUID,szDiskSignature,handle,jsVolMount);
				jRet = JNI_OK;
			}
			else
			{
				jRet =JNI_ERR;
			}
		}else {
			jRet = dwRet;
		}
	}
	
	if(rootfolder != NULL)
	{
		free(rootfolder);
		rootfolder = NULL;
	}
	if(username != NULL)
	{
		free(username);
		username = NULL;
	}
	if(password != NULL)
	{
		free(password);
		password = NULL;
	}
	if(volumeguid != NULL)
	{
		free(volumeguid);
		volumeguid = NULL;
	}
	return jRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetCatalogStatus(JNIEnv *env, jclass jclz, jstring jsessionPath)
{	
	wchar_t* sessionPath = JStringToWCHAR(env, jsessionPath);
	jint jRet = JNI_ERR ;

	DWORD dwRet = GetCatalogStatus(sessionPath);

	if(sessionPath != NULL)
	{
		free(sessionPath);
		sessionPath = NULL;
	}
	return (jint)dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SetCatalogStatus(JNIEnv *env, jclass jclz, jstring jbackupDest, jstring jsessionPath)
{	
	wchar_t* sessionPath = JStringToWCHAR(env, jsessionPath);
	wchar_t* backupDest = JStringToWCHAR(env, jbackupDest);
	jint jRet = JNI_ERR ;

	DWORD dwRet = SetCatalogStatus(backupDest, sessionPath);

	if(sessionPath != NULL)
	{
		free(sessionPath);
		sessionPath = NULL;
	}
	if(backupDest != NULL)
	{
		free(backupDest);
		backupDest = NULL;
	}
	return (jint)dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_UpdateCatalogJobScript(JNIEnv *env, jclass jclz, jstring jbackupDest, jstring juserName, 
		jstring jpassWord, jlong sessNumber, jstring jjobScript, jstring jvminstanceUUID)
{	
	wchar_t* userName = JStringToWCHAR(env, juserName);
	wchar_t* passWord = JStringToWCHAR(env, jpassWord);
	wchar_t* backupDest = JStringToWCHAR(env, jbackupDest);
	wchar_t* jobScript =  JStringToWCHAR(env, jjobScript);
	wchar_t* vmInstance = JStringToWCHAR(env, jvminstanceUUID);
	jint jRet = JNI_ERR ;

	SESSION_INFO si = Convert2SessionInfo(backupDest,userName,passWord,sessNumber);

	DWORD dwRet = UpdateCatalogJobScript(si, jobScript, vmInstance);

	if(userName != NULL)
	{
		free(userName);
		userName = NULL;
	}
	if(passWord != NULL)
	{
		free(passWord);
		passWord = NULL;
	}
	if(backupDest != NULL)
	{
		free(backupDest);
		backupDest = NULL;
	}
	if(jobScript != NULL)
	{
		free(jobScript);
		jobScript = NULL;
	}
	return (jint)dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_InitGRTMounter(JNIEnv *env, jclass jclz)
{
	HANDLE handle = InitGRTMounter();	
	return (jlong)handle;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_ExitGRTMounter(JNIEnv *env, jclass jclz, jlong jhandle)
{
	HANDLE handle = HANDLE(jhandle);
	jint jRet = ExitGRTMounter(handle);
	return jRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SearchMountPoint
(JNIEnv *env, jclass clz, jstring mntVolumeGUID, jstring backupDestination, jstring sDriveLetter,jstring sDir, jboolean bCaseSensitive, jboolean bIncludeSubDir, jstring pattern)
{
	wchar_t* pMntVolumeGUID = JStringToWCHAR(env, mntVolumeGUID);
	wchar_t* pDriveLetter = JStringToWCHAR(env, sDriveLetter);
	wchar_t* pDir = JStringToWCHAR(env, sDir);
	wchar_t* ptn = JStringToWCHAR(env, pattern);
	wchar_t* pbackupDestination = JStringToWCHAR(env, backupDestination);

	HANDLE h = SearchMountPoint(pbackupDestination,pMntVolumeGUID, pDriveLetter, ptn, pDir, (BOOL)bIncludeSubDir, (BOOL)bCaseSensitive);

	if(pMntVolumeGUID != NULL)
	{
		free(pMntVolumeGUID);
		pMntVolumeGUID = NULL;
	}
	if(pDir != NULL)
	{
		free(pDir);
		pDir = NULL;
	}
	if(ptn != NULL)
	{
		free(ptn);
		ptn = NULL;
	}	
	if(pDriveLetter != NULL)
	{
		free(pDriveLetter);
		pDriveLetter = NULL;
	}
	if(pbackupDestination != NULL)
	{
		free(pbackupDestination);
		pbackupDestination = NULL;
	}	
	return (jlong)h;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_FindNextSearchItems
(JNIEnv *env, jclass clz, jlong jhandle, jlong nRequest, jobject retArr, jobject jnFound)
{
	DWORD dwRet = 0;
	HANDLE h = (HANDLE)jhandle;
	if(h!=NULL)
	{
		PDetailW pd = NULL;	

		UINT nFound = 0;
		dwRet = FindNextSearchItems(h, (UINT)nRequest,&pd, &nFound);	

		for(UINT i = 0; i < nFound; i++) {			
			AddDetailW2List(env, &retArr, &pd[i]);				
		}		
		AddUINT2JRWLong(env, nFound, &jnFound);

		if(pd!=NULL)
		{
			HeapFree(GetProcessHeap(), 0, pd);
		}
	}
	return (jint)dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_FindCloseSearchItems
(JNIEnv *env, jclass clz, jlong jhandle)
{
	HANDLE h = (HANDLE)jhandle;
	if(h != NULL)
	{
		FindCloseSearchItems(h);
	}
	return JNI_OK;
}


JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetRegIntValue
(JNIEnv *env, jclass clz, jstring keyName, jstring valueName, jstring rootReg, jobject regValue)
{
	wchar_t* wsKeyName = JStringToWCHAR(env, keyName);
	wchar_t* wsValueName = JStringToWCHAR(env, valueName);
	wchar_t* wsRootReg = JStringToWCHAR(env, rootReg);
	
	if((wsRootReg == NULL)||(wcslen(wsRootReg)==0))
	{  
		if(wsRootReg != NULL)
		{
			free(wsRootReg);
			wsRootReg = NULL;
		}

		DWORD len = MAX_PATH*2;
		wsRootReg = (wchar_t *) malloc(len*sizeof(WCHAR));
		memset(wsRootReg,0,len*sizeof(WCHAR));
		wcscpy_s(wsRootReg,len, CST_REG_ROOT_L);
	}	

	DWORD dwValue = 0;
	DWORD dwRet = readRegDWORDValue(wsKeyName, wsValueName, wsRootReg, dwValue);
	if(dwRet == 0)
	{
		AddUINT2JRWLong(env,dwValue,&regValue);
	}

	if(wsKeyName != NULL){
		free(wsKeyName);
	}
	if(wsValueName != NULL){
		free(wsValueName);
	}
	if(wsRootReg != NULL )
	{
		free(wsRootReg);
	}

	return dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SetRegIntValue
(JNIEnv *env, jclass clz, jstring keyName, jstring valueName, jstring rootReg, jint regIntValue)
{
	jstring strRet = NULL;
	wchar_t* wsKeyName = JStringToWCHAR(env, keyName);
	wchar_t* wsValueName = JStringToWCHAR(env, valueName);
	wchar_t* wsRootReg = JStringToWCHAR(env, rootReg);

	if((wsRootReg == NULL)||(wcslen(wsRootReg)==0))
		wsRootReg = CST_REG_ROOT_L;

	DWORD dwValue = regIntValue;
	DWORD ret = writeRegDWORDValue(wsKeyName, wsValueName, wsRootReg, dwValue);

	if(wsKeyName != NULL){
		free(wsKeyName);
	}

	if(wsValueName != NULL){
		free(wsValueName);
	}
	if(wsRootReg != NULL)
	{
		free(wsRootReg);
	}
	return ret;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetRegMultiStringValue
(JNIEnv *env, jclass clz, jstring keyName, jstring valueName, jstring rootReg, jobject regArrayValue)
{
	wchar_t* wsKeyName = JStringToWCHAR(env, keyName);
	wchar_t* wsValueName = JStringToWCHAR(env, valueName);
	wchar_t* wsRootReg = JStringToWCHAR(env, rootReg);

	if((wsRootReg == NULL)||(wcslen(wsRootReg)==0))
		wsRootReg = CST_REG_ROOT_L;

	vector<wstring> vecValues;
	DWORD dwRet = readRegMultiStringValue(wsKeyName, wsValueName, wsRootReg, vecValues);
	if(dwRet == 0)
	{
		AddVWSTRING_ITEM2List(env,regArrayValue,vecValues);
	}

	if(wsKeyName != NULL){
		free(wsKeyName);
	}
	if(wsValueName != NULL){
		free(wsValueName);
	}
	if(wsRootReg != NULL)
	{
		free(wsRootReg);
	}

	return dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SetRegMultiStringValue
(JNIEnv *env, jclass clz, jstring keyName, jstring valueName, jstring rootReg, jobject regArrayValue)
{
	jstring strRet = NULL;
	wchar_t* wsKeyName = JStringToWCHAR(env, keyName);
	wchar_t* wsValueName = JStringToWCHAR(env, valueName);
	wchar_t* wsRootReg = JStringToWCHAR(env, rootReg);

	if((wsRootReg == NULL)||(wcslen(wsRootReg)==0))
		wsRootReg = CST_REG_ROOT_L;

	vector<wstring> vecValues;
	jList2Vector(env, regArrayValue, vecValues);
	DWORD ret = writeRegMultiStringValue(wsKeyName, wsValueName, wsRootReg, vecValues);

	if(wsKeyName != NULL){
		free(wsKeyName);
	}

	if(wsValueName != NULL){
		free(wsValueName);
	}
	if(wsRootReg != NULL)
	{
		free(wsRootReg);
	}
	return ret;
}

JNIEXPORT jbyteArray JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetRegBinaryValue
(JNIEnv *env, jclass clz, jstring rootReg, jstring valueName)
{
	wchar_t* wsValueName = JStringToWCHAR(env, valueName);
	wchar_t* wsRootReg = JStringToWCHAR(env, rootReg);

	if ((wsValueName == NULL) || (wcslen(wsValueName) == 0))
	{
		logObj.LogW(LL_ERR, 1, __WFUNCTION__ L"valueName is null or empty");
		return NULL;
	}
	else
	{
		logObj.LogW(LL_DBG, 0, __WFUNCTION__ L"valueName is %s", wsValueName);
	}

	if ((wsRootReg == NULL) || (wcslen(wsRootReg) == 0))
	{
		logObj.LogW(LL_ERR, 1, __WFUNCTION__ L"rootReg is null or empty");
		return NULL;
	}
	else
	{
		logObj.LogW(LL_DBG, 0, __WFUNCTION__ L"rootReg is %s", rootReg);
	}

	CRegistry reg;
	DWORD  dwRet = (DWORD)reg.Open(wsRootReg);
	if (ERROR_SUCCESS == dwRet)
	{
		ULONG ulRealByteSize = -1;
		//query the size
		reg.QueryValue(wsValueName, NULL, NULL, &ulRealByteSize);

		if (ulRealByteSize < 0)
		{
			logObj.LogW(LL_ERR, 1, __WFUNCTION__ L"QueryValue to get size failed");
			return NULL;
		}

		BYTE* pBuff = new BYTE[ulRealByteSize];
		ZeroMemory(pBuff, ulRealByteSize);

		DWORD dwQueryVal = reg.QueryValue(wsValueName, NULL, pBuff, &ulRealByteSize);
		if (ERROR_SUCCESS != dwQueryVal)
		{
			logObj.LogW(LL_ERR, 1, __WFUNCTION__ L"QueryValue failed");
			reg.Close();
			return NULL;
		}
		reg.Close();

		jbyteArray byteArray = env->NewByteArray(ulRealByteSize);
		env->SetByteArrayRegion(byteArray, 0, ulRealByteSize, (jbyte*)pBuff);
		delete[] pBuff;
		return byteArray;
	}
	else
	{
		logObj.LogW(LL_ERR, 3, __WFUNCTION__ L"open reg error");
		return NULL;
	}
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_SetRegBinaryValue
(JNIEnv *env, jclass clz, jstring rootReg, jstring valueName, jbyteArray byteArray)
{
	wchar_t* wsValueName = JStringToWCHAR(env, valueName);
	wchar_t* wsRootReg = JStringToWCHAR(env, rootReg);

	if ((wsValueName == NULL) || (wcslen(wsValueName) == 0))
	{
		logObj.LogW(LL_ERR, 1, __WFUNCTION__ L"valueName is null or empty");
		return -1;
	}
	else
	{
		logObj.LogW(LL_DBG, 0, __WFUNCTION__ L"valueName is %s", wsValueName);
	}

	if ((wsRootReg == NULL) || (wcslen(wsRootReg) == 0))
	{
		logObj.LogW(LL_ERR, 1, __WFUNCTION__ L"rootReg is null or empty");
		return -1;
	}
	else
	{
		logObj.LogW(LL_DBG, 0, __WFUNCTION__ L"rootReg is %s", rootReg);
	}

	CRegistry reg;
	DWORD  dwRet = (DWORD)reg.Open(wsRootReg);
	if (ERROR_SUCCESS == dwRet)
	{
		jsize iArraySize = env->GetArrayLength(byteArray);
		BYTE* pBuff = NULL;
		if (iArraySize > 0)
		{
			pBuff = (BYTE*)env->GetByteArrayElements(byteArray, 0);
		}

		ULONG ulRealByteSize = 0;
		dwRet = (DWORD)reg.SetValue(wsValueName, REG_BINARY, pBuff, iArraySize);
		reg.Close();
	}
	else
	{
		logObj.LogW(LL_ERR, 3, __WFUNCTION__ L"open reg error");
	}

	return dwRet;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetSymbolicLinkActualPath(JNIEnv * env, 
					jclass cls, jstring inFolderPath, jobject outErrorCode)
{
	HMODULE hMountCatalogMgrDll = NULL; 
	long dwRet = ERROR_SUCCESS;

	wstring wsFolderName	= JStringToWString(env, inFolderPath);
	wstring wsActualFolderName = L""; //wsFolderName;

	dwRet = AFGetActualPathName(wsFolderName, wsActualFolderName);
	AddUINT2JRWLong(env,dwRet,&outErrorCode);
	//outErrorCode = (jlong)dwRet;
	return WCHARToJString(env,wsActualFolderName);
	
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    isShowUpdate
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_isShowUpdate
  (JNIEnv * env, jclass jcls)
{
	DWORD dwValue;
	DWORD dwRet = readRegDWORDValue(L"Update Manager", L"HideAutoUpdate", CST_REG_ROOT_L, dwValue);
	if(dwRet == 0) 
	{
		return dwValue == 0;
	}else 
	{
		return true;
	}
}
/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    startMerge
 * Signature: (Lcom/ca/arcflash/webservice/jni/model/MergeJobScript;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_startMerge
  (JNIEnv *env, jclass cls, jobject mjs, jstring jsPath)
{
	CMergeJS mergeJS;
	JMergeJobScript2CMergeJS(env, mjs, mergeJS);

	WCHAR* cjspath = JStringToWCHAR(env, jsPath);
	AFISaveMergeJS(mergeJS, cjspath);
	long ret = AFIStartJob(cjspath);
	if(cjspath != NULL)
		free(cjspath);
	return ret;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    stopMerge
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_stopMerge
  (JNIEnv * env, jclass jcls, jlong jobId)
{
	return AFIStopMergeJob(jobId, EJT_MERGE);
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getMergeJobMonitor
 * Signature: (JLcom/ca/arcflash/webservice/data/merge/MergeJobMonitor;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getMergeJobMonitor
  (JNIEnv *env, jclass jcls, jlong address, jobject mjm)
{	
	IJobMonInterface *pJobM = (IJobMonInterface *)address;
	PST_MERGE_CTRL mergeJM = NULL;

	if(pJobM)
	{		
		mergeJM = pJobM->MergeJobMon();
		if(mergeJM){			
			MergeControl2JMergeJobMonitor(env, mergeJM, mjm);
			return 0;
		}
	}

	return -1;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    createMergeJobMonitor
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_createMergeJobMonitor
  (JNIEnv * env, jclass jcls, jlong jjobId)
{
	IJobMonInterface *pAFJM = NULL;
	DWORD dwRet = 0;
	pAFJM = AFICreateMergeJM(jjobId, &dwRet, EJT_MERGE);
	if(!dwRet)
	{		
		return (jlong)pAFJM;
	}

	return 0;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    releaseMergeJobMonitor
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_releaseMergeJobMonitor
  (JNIEnv * env, jclass jcls, jlong address)
{
	IJobMonInterface *pJobM = (IJobMonInterface *)address;
	AFIReleaseMergeJM(&pJobM);
	return 0;
}
/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    isMergeJobAvailable
 * Signature: (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_isMergeJobAvailable
  (JNIEnv * env, jclass jcls, jint retentionCount, jstring jdest, jstring jvmUUID, 
	jstring jDestUser, jstring jDestPwd)
{
	WCHAR* wdest = JStringToWCHAR(env, jdest);
	WCHAR* wvmuuid = JStringToWCHAR(env, jvmUUID);
	WCHAR* wdestuser = JStringToWCHAR(env, jDestUser);
	WCHAR* wdestPwd = JStringToWCHAR(env, jDestPwd);

	long ret = AFIIsMergeJobAvailable(retentionCount, wdest, wvmuuid, wdestuser, wdestPwd);

	if(wdest != NULL)
		free(wdest);
	if(wvmuuid != NULL)
		free(wvmuuid);
	if(wdestuser != NULL)
		free(wdestuser);
	if(wdestPwd != NULL)
		free(wdestPwd);
	
	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFIIsMergeJobAvailableEx
	(JNIEnv *env, jclass jclzz, jstring jdest, jint retentionCount, jint dailyCount, jint weeklyCount, jint monthlyCount, jstring jVMUUID, jstring ds4Replication, jstring jDestUser, jstring jDestPwd)
{
	WCHAR* wdest = JStringToWCHAR(env, jdest);
	WCHAR* wvmuuid = JStringToWCHAR(env, jVMUUID);
	//WCHAR* wDS4Replication = JStringToWCHAR(env, ds4Replication);
	WCHAR* wdestuser = JStringToWCHAR(env, jDestUser);
	WCHAR* wdestPwd = JStringToWCHAR(env, jDestPwd);

	long ret = AFIIsMergeJobAvailableEx(wdest,retentionCount,dailyCount, weeklyCount, monthlyCount,wvmuuid, NULL, wdestuser, wdestPwd);

	if(wdest != NULL)
		free(wdest);

	//if(wDS4Replication != NULL)
	//	free(wDS4Replication);

	if(wvmuuid != NULL)
		free(wvmuuid);

	if(wdestuser != NULL)
		free(wdestuser);

	if(wdestPwd != NULL)
		free(wdestPwd);
	
	return ret;
}

/*
* Class:     com_ca_arcflash_webservice_jni_WSJNI
* Method:    AFIIsMergeJobAvailableExt
* Signature: (Lcom/ca/arcflash/service/jni/model/JMergeData;)J
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFIIsMergeJobAvailableExt
	(JNIEnv *env, jclass jcls, jobject jMegeData)
{
	WCHAR* wDkupDest = NULL;
	WCHAR* wVmUUIDs = NULL;
	DWORD  retentionCount = 0;
	DWORD  dailyCount = 0;
	DWORD  weeklyCount = 0;
	DWORD  monthlyCount = 0;
	WCHAR* wDsForRep = 0; 
	WCHAR* wDestUser = NULL; 
	WCHAR* wDestPwd = NULL;
	DWORD  dwArchiveDailySel = 0;
	DWORD  dwArchiveSourceSelection = 0;
	LONGLONG  llArchiveConfigTime = 0;
	DWORD  dwRet = 0;

	// Get data from Java object start...
	jclass mergeClass = env->GetObjectClass(jMegeData);

	jmethodID getBkDest = env->GetMethodID(mergeClass, "getBackupDest", "()Ljava/lang/String;");
	jmethodID getRetCnt = env->GetMethodID(mergeClass, "getCustomRetentionCnt", "()I");
	jmethodID getDayCnt = env->GetMethodID(mergeClass, "getDailyCnt", "()I");
	jmethodID getWekCnt = env->GetMethodID(mergeClass, "getWeeklyCnt", "()I");
	jmethodID getMthCnt = env->GetMethodID(mergeClass, "getMonthlyCnt", "()I");
	jmethodID getVMGuid = env->GetMethodID(mergeClass, "getVmGUID", "()Ljava/lang/String;");
	jmethodID getDs4Rep = env->GetMethodID(mergeClass, "getDataStore4Replication", "()Ljava/lang/String;");
	jmethodID getBkUser = env->GetMethodID(mergeClass, "getBackupUser", "()Ljava/lang/String;");
	jmethodID getbkPawd = env->GetMethodID(mergeClass, "getBackupPassword", "()Ljava/lang/String;");
	jmethodID getCfTime = env->GetMethodID(mergeClass, "getArchiveConfigTime", "()J");
	jmethodID getSrcSel = env->GetMethodID(mergeClass, "getArchiveSourceSelection", "()J");
	jmethodID getDayArr = env->GetMethodID(mergeClass, "getArchiveDailySelectedDays", "()[Z");

	wDkupDest = JStringToWCHAR(env, (jstring)env->CallObjectMethod(jMegeData, getBkDest));
	wVmUUIDs = JStringToWCHAR(env, (jstring)env->CallObjectMethod(jMegeData, getVMGuid));
	wDsForRep = JStringToWCHAR(env, (jstring)env->CallObjectMethod(jMegeData, getDs4Rep));
	wDestUser = JStringToWCHAR(env, (jstring)env->CallObjectMethod(jMegeData, getBkUser));
	wDestPwd = JStringToWCHAR(env, (jstring)env->CallObjectMethod(jMegeData, getbkPawd));

	retentionCount = env->CallIntMethod(jMegeData, getRetCnt);
	dailyCount = env->CallIntMethod(jMegeData, getDayCnt);
	weeklyCount = env->CallIntMethod(jMegeData, getWekCnt);
	monthlyCount = env->CallIntMethod(jMegeData, getMthCnt);

	dwArchiveSourceSelection = (DWORD)env->CallLongMethod(jMegeData, getSrcSel);
	llArchiveConfigTime = (LONGLONG)env->CallLongMethod(jMegeData, getCfTime);

	jobject jBoolArrObj = env->CallObjectMethod(jMegeData, getDayArr);
	if (NULL != jBoolArrObj)
	{
		jbooleanArray* jData = reinterpret_cast<jbooleanArray*>(&jBoolArrObj);
		jboolean* bDaySels = env->GetBooleanArrayElements(*jData, NULL);
		if (NULL != bDaySels)
		{
			jsize nLen = env->GetArrayLength(*jData);
			for (jsize idx = 0; idx < nLen; idx++)
			{
				if (0 != bDaySels[idx])
					dwArchiveDailySel |= (1 << idx);		//bit0:bit6 <--> sun:sat
			}

			env->ReleaseBooleanArrayElements(*jData, bDaySels, 0);
		}
		else
		{
			dwRet = GetLastError();
			logObj.LogW(LL_WAR, dwRet, __WFUNCTION__ L"env->GetBooleanArrayElements() return NULL.");
		}
	}
	else
	{
		dwRet = GetLastError();
		logObj.LogW(LL_WAR, dwRet, __WFUNCTION__ L"JMergeData::getArchiveDailySelectedDays() return NULL.");
	}
	// Get data from Java object end.

	// call native method
	long ret = AFIIsMergeJobAvailableEx(wDkupDest, retentionCount, dailyCount, weeklyCount, monthlyCount, wVmUUIDs, wDsForRep, wDestUser, wDestPwd,
		dwArchiveDailySel, dwArchiveSourceSelection, llArchiveConfigTime);

	if (wDkupDest){ free(wDkupDest); wDkupDest = NULL; }
	if (wVmUUIDs) { free(wVmUUIDs); wVmUUIDs = NULL; }
	if (wDsForRep){ free(wDsForRep); wDsForRep = NULL; }
	if (wDestUser){ free(wDestUser); wDestUser = NULL; }
	if (wDestPwd) { free(wDestPwd); wDestPwd = NULL; }

	return ret;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    AFIRetrieveMergeJM
 * Signature: (Ljava/util/ArrayList;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFIRetrieveMergeJM
  (JNIEnv * env, jclass jcls, jobject jjobs)
{
	ActJobVector vecActiveJob;
	long ret = AFIRetrieveMergeJM(vecActiveJob);
	
	if(ret == 0) {
		ActiveMergeVector2JActiveMergeJobs(env, jjobs, vecActiveJob);
	}
	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetSessNumListForNextMerge
  (JNIEnv * env, jclass jcls, jint retentionCount, jstring jdest, jobject sessionList, 
	jstring jDestUser, jstring jDestPwd)
{
	WCHAR* wdest = JStringToWCHAR(env, jdest);
	WCHAR* wdestuser = JStringToWCHAR(env, jDestUser);
	WCHAR* wdestPwd = JStringToWCHAR(env, jDestPwd);
	vector<DWORD> sessions;
	
	long ret = AFIGetSessNumListForNextMerge(retentionCount, wdest, sessions, wdestuser, wdestPwd);

	if(ret == 0)
	{
		for(int i = 0; i< sessions.size(); i++)
		{
			int value = sessions[i];
			AddIntToIntegerList(env, sessionList, value );
		}
	}

	if(wdest != NULL)
		free(wdest);
	if(wdestuser != NULL)
		free(wdestuser);
	if(wdestPwd != NULL)
		free(wdestPwd);
	
	return ret;
}
/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    setBackupSetFlag
 * Signature: (Ljava/lang/String;JILjava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_setBackupSetFlag
  (JNIEnv * env, jclass jcls, jstring destination, jlong sessionNum, 
							jint flagValue, jstring vmInstanceUUID)
{
	WCHAR* wDest = JStringToWCHAR(env, destination);
	WCHAR* wUUID = JStringToWCHAR(env, vmInstanceUUID);
	
	long ret = AFISetBackupSetFlag(wDest, sessionNum, flagValue, wUUID);

	if(wDest != NULL)
	{
		free(wDest);
	}
	if(wUUID != NULL)
	{
		free(wUUID);
	}
	return ret;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    launchCatalogEx
 * Signature: (Lcom/ca/arcflash/webservice/jni/model/CatalogJobContext;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_launchCatalogEx
  (JNIEnv * env, jclass jcls, jobject jobj)
{
	jclass context = env->FindClass("com/ca/arcflash/webservice/jni/model/CatalogJobContext");
	jmethodID getIDM = env->GetMethodID(context, "getId", "()J");
	jmethodID getTypeM = env->GetMethodID(context, "getType", "()J");
	jmethodID getVmIndentification = env->GetMethodID(context, "getVmIndentification", "()Ljava/lang/String;");
	jmethodID getConnInfo = env->GetMethodID(context, "getConnInfo", "()Lcom/ca/arcflash/service/jni/model/JNetConnInfo;");
	jlong jobid = env->CallLongMethod(jobj, getIDM);
	jlong type = env->CallLongMethod(jobj, getTypeM);
	jstring vminstanceUUID = (jstring)env->CallObjectMethod(jobj, getVmIndentification);
	jobject jconn = env->CallObjectMethod(jobj, getConnInfo);
	NET_CONN_INFO conn;
	memset(&conn, 0, sizeof(NET_CONN_INFO));
	JNetConnInfo2NET_CONN_INFO(env, &jconn, conn);
	WCHAR* instanceUUID = NULL;
	if(vminstanceUUID != NULL)
		instanceUUID = JStringToWCHAR(env, vminstanceUUID);

	DWORD ret = AFStartCatalogGenerator(type, jobid, NULL, NULL, NULL, instanceUUID, &conn);
	if(instanceUUID != NULL)
	{
		free(instanceUUID);
	}
	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetSystemInfo
(JNIEnv *env, jclass clz, jobject jSystemInfo)
{
	D2D_SYSINFO sysInfo;
	int ret = AFCollectSystemInfo(&sysInfo);
	if(ret == 0){
		jclass class_jSystemInfo = env->GetObjectClass(jSystemInfo);

		jmethodID set_DedupInstalled = env->GetMethodID(class_jSystemInfo, "setDedupInstalled","(Z)V");
		env->CallVoidMethod(jSystemInfo, set_DedupInstalled, (jboolean)sysInfo.isDedupeInstalled);

		jmethodID set_win8 = env->GetMethodID(class_jSystemInfo, "setWin8","(Z)V");
		env->CallVoidMethod(jSystemInfo, set_win8, (jboolean)sysInfo.isWin8);

		jmethodID setReFsSupported = env->GetMethodID(class_jSystemInfo, "setReFsSupported","(Z)V");
		env->CallVoidMethod(jSystemInfo, setReFsSupported, (jboolean)sysInfo.isSupportedRefs);
		
		if (class_jSystemInfo != NULL) env->DeleteLocalRef(class_jSystemInfo);
	}
	return ret;
}

void AddItemToAdapterMapInfoVector( JNIEnv * pEnv, jobject item, LPARAM lParam )
{
	jclass classIPSettingDetail = pEnv->GetObjectClass( item );
	jmethodID midIsDhcp			= pEnv->GetMethodID( classIPSettingDetail,	"isDhcp",			"()Z" );
	jmethodID midGetWinsPrimary	= pEnv->GetMethodID( classIPSettingDetail,	"getWinsPrimary",	"()Ljava/lang/String;" );
	jmethodID midGetWinsSecond	= pEnv->GetMethodID( classIPSettingDetail,	"getWinsSecond",	"()Ljava/lang/String;" );
	jmethodID midGetDns			= pEnv->GetMethodID( classIPSettingDetail,	"getDns",			"()Ljava/util/List;" );
	jmethodID midGetGateways	= pEnv->GetMethodID( classIPSettingDetail,	"getGateways",		"()Ljava/util/List;" );
	jmethodID midGetIps			= pEnv->GetMethodID( classIPSettingDetail,	"getIps",			"()Ljava/util/List;" );
	jmethodID midGetSubnets		= pEnv->GetMethodID( classIPSettingDetail,	"getSubnets",		"()Ljava/util/List;" );

	Adapter_Map_InfoW ami;

	ami.replicate_mac			= false;
	ami.ip_use_dhcp				= pEnv->CallBooleanMethod( item, midIsDhcp ) == JNI_TRUE;
	ami.use_original_setting	= false;
	ami.wins_p					= JStringToWString( pEnv, (jstring)pEnv->CallObjectMethod( item, midGetWinsPrimary ) );
	ami.wins_s					= JStringToWString( pEnv, (jstring)pEnv->CallObjectMethod( item, midGetWinsSecond ) );

    if (0 == ami.wins_s.compare(L"0.0.0.0")) //<huvfe01>2012-12-26 for defect#160986 Fail to set WINS server
    {
        ami.wins_s = L"";
    }

	jobject list;

	list = pEnv->CallObjectMethod( item, midGetDns );
	GoThroughJavaList( pEnv, list, AddItemToWstringVector, (LPARAM)&ami.dns );

	list = pEnv->CallObjectMethod( item, midGetGateways );
	GoThroughJavaList( pEnv, list, AddItemToWstringVector, (LPARAM)&ami.gateways );

	list = pEnv->CallObjectMethod( item, midGetIps );
	GoThroughJavaList( pEnv, list, AddItemToWstringVector, (LPARAM)&ami.ips );

	list = pEnv->CallObjectMethod( item, midGetSubnets );
	GoThroughJavaList( pEnv, list, AddItemToWstringVector, (LPARAM)&ami.subnets );

	ami.ip_use_dhcp = (0 == ami.ips.size()); //<sonmi01>2012-10-19 ###???
	ami.dns_use_dhcp = (0 == ami.dns.size());

	vector<Adapter_Map_InfoW> * pAdapterMapInfoVector = (vector<Adapter_Map_InfoW> *)lParam;
	pAdapterMapInfoVector->push_back( ami );
}

void AddItemToDNSUpdaterCmdLineVector( JNIEnv * pEnv, jobject item, LPARAM lParam )
{
	jclass classIPSettingDetail = pEnv->GetObjectClass( item );
	jmethodID midGetDNS			= pEnv->GetMethodID( classIPSettingDetail,	"getDns",		"()Ljava/lang/String;" );
	jmethodID midGetHostname	= pEnv->GetMethodID( classIPSettingDetail,	"getHostname",	"()Ljava/lang/String;" );
	jmethodID midGetHostIP		= pEnv->GetMethodID( classIPSettingDetail,	"getHostIp",	"()Ljava/lang/String;" );
	jmethodID midGetTTL			= pEnv->GetMethodID( classIPSettingDetail,	"getTtl",		"()I" );
	jmethodID midGetUsername	= pEnv->GetMethodID( classIPSettingDetail,	"getUsername",	"()Ljava/lang/String;" );
	jmethodID midGetPassword	= pEnv->GetMethodID( classIPSettingDetail,	"getCredential",	"()Ljava/lang/String;" );
	jmethodID midGetKeyFile		= pEnv->GetMethodID( classIPSettingDetail,	"getKeyFile",	"()Ljava/lang/String;" );
    jmethodID midGetDnsServerType = pEnv->GetMethodID( classIPSettingDetail,	"getDnsServerType",	"()I" ); //<huvfe01>2012-12-20 for server type

	DNSUpdaterCmdLine ducl;

	ducl.dns		= JStringToWString( pEnv, (jstring)pEnv->CallObjectMethod( item, midGetDNS ) );
	ducl.hostname	= JStringToWString( pEnv, (jstring)pEnv->CallObjectMethod( item, midGetHostname ) );
	ducl.hostip		= JStringToWString( pEnv, (jstring)pEnv->CallObjectMethod( item, midGetHostIP ) );
	ducl.ttl		= (int)pEnv->CallObjectMethod( item, midGetTTL );
	ducl.username	= JStringToWString( pEnv, (jstring)pEnv->CallObjectMethod( item, midGetUsername ) );
	ducl.password	= JStringToWString( pEnv, (jstring)pEnv->CallObjectMethod( item, midGetPassword ) );
	ducl.keyfile	= JStringToWString( pEnv, (jstring)pEnv->CallObjectMethod( item, midGetKeyFile ) );
    ducl.dnsServerType = (int)pEnv->CallObjectMethod( item, midGetDnsServerType ); //<huvfe01>2012-12-20 for server type    
	vector<DNSUpdaterCmdLine> * pDNSUpdaterCmdLineVector = (vector<DNSUpdaterCmdLine> *)lParam;
	pDNSUpdaterCmdLineVector->push_back( ducl );
}

template <typename T> T noop(T t) { return t; }
void AddItemToVolumeDriverVector(JNIEnv * pEnv, jobject item, LPARAM lParam)
{
	jclass classIPSettingDetail = pEnv->GetObjectClass( item );
	jmethodID midGetDriverLetter = pEnv->GetMethodID( classIPSettingDetail,	"getDriveLetter", "()Ljava/lang/String;");
	jmethodID midGetGuidPath = pEnv->GetMethodID( classIPSettingDetail,	"getGuidPath", "()Ljava/lang/String;");
	std::pair<std::wstring, std::wstring> volumePair;

	std::wstring driverLetter = JStringToWString(pEnv, (jstring)pEnv->CallObjectMethod(item, midGetDriverLetter));
	std::wstring guidPath = JStringToWString(pEnv, (jstring)pEnv->CallObjectMethod(item, midGetGuidPath));
	std::vector<std::pair<std::wstring, std::wstring>>* pVector = (std::vector<std::pair<std::wstring, std::wstring>>*)lParam;
	if (driverLetter.size() == 1 && guidPath.size())
	{
		pVector->push_back(std::make_pair<std::wstring, std::wstring>(noop<std::wstring>(driverLetter), noop<std::wstring>(guidPath)));
	}
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    DoRVCMInjectService
 * Signature: (Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)J
 */
//modified by zhepa02 at 2015-04-14 ,add the strThumbprint parameter.
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DoRVCMInjectService(
	JNIEnv * pEnv,
	jclass,
	jstring	esxHostname,
	jint	esxPort,
	jobject exParams,
	jstring	username,
	jstring	password,
	jstring	moRefId,
	jobject volInfo,
	jstring	failoverMode,
	jobject	vmdkUrlList,
	jstring	iniFolderPath,
	jstring	jobId,
	jobject volumesOnNonBootOrSystemDisk,
	jboolean isX86,
	jstring scriptPath)
{
	wstring wstrEsxHostName		= JStringToWString( pEnv, esxHostname );
	UINT32 uPort				= esxPort;
	wstring wstrUsername		= JStringToWString( pEnv, username );
	wstring wstrPassword		= JStringToWString( pEnv, password );
	wstring wstrMoRefId			= JStringToWString( pEnv, moRefId );
	//wstring wstrBootVolume		= JStringToWString( pEnv, bootVolume );
	wstring wstrFailoverMode	= JStringToWString( pEnv, failoverMode );
	wstring wstrIniFolderPath	= JStringToWString( pEnv, iniFolderPath );
	wstring wstrJobId			= JStringToWString( pEnv, jobId );
	wstring wstrScriptPath = JStringToWString(pEnv, scriptPath);
	//wstring wstrThumbprint = JStringToWString(pEnv, thumbprint); //modified by zhepa02 at 2015-04-14 ,add the strThumbprint parameter.

	VMDK_CONNECT_MORE_PARAMS moreParams;
	VMwareVolumeInfo volumeInfo = { 0 };

	ConvertVMDKConnParams(pEnv, exParams, moreParams);
	ConvertVMwareVolumeInfo(pEnv, volInfo, volumeInfo);
	vector<wstring> vtrVmdkUrls;
	GoThroughJavaList( pEnv, vmdkUrlList, AddItemToWstringVector, (LPARAM)&vtrVmdkUrls );

	std::vector<std::pair<std::wstring, std::wstring>> vtrVolumeDrivers;
	if (volumesOnNonBootOrSystemDisk != NULL)
		GoThroughJavaList( pEnv, volumesOnNonBootOrSystemDisk, AddItemToVolumeDriverVector, (LPARAM)&vtrVolumeDrivers);

	//modified by zhepa02 at 2015-04-14 ,add the strThumbprint parameter.
	DWORD dwReturn = DoRVCMInjectService(
		wstrEsxHostName,
		wstrUsername,
		wstrPassword,
		wstrMoRefId,
		volumeInfo,
		wstrFailoverMode,
		uPort,
		moreParams,
		vtrVmdkUrls,
		wstrIniFolderPath,
		wstrJobId,
		vtrVolumeDrivers, 
		isX86,
		wstrScriptPath);

	return (jlong)dwReturn;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    DoRVCMInjectServiceForHyperV
 * Signature: (Ljava/lang/String;Ljava/util/List;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DoRVCMInjectServiceForHyperV(
	JNIEnv * pEnv,
	jclass,
	jstring vmWinDir,
	jstring winSystemDir,
	jobject volumesOnNonBootOrSystemDisk, 
	jboolean isX86,
	jstring scriptPath)
{
	wstring wstrVMWinDir = JStringToWString( pEnv, vmWinDir );
	wstring wstrWinSystemDir = JStringToWString( pEnv, winSystemDir );
	wstring wstrScriptPath = JStringToWString(pEnv, scriptPath);

	std::vector<std::pair<std::wstring, std::wstring>> vtrVolumeDrivers;
	GoThroughJavaList( pEnv, volumesOnNonBootOrSystemDisk, AddItemToVolumeDriverVector, (LPARAM)&vtrVolumeDrivers);

	DWORD dwReturn = DoRVCMInjectServiceForHyperV(
		wstrVMWinDir,
		wstrWinSystemDir,
		vtrVolumeDrivers,
		(bool)isX86,
		wstrScriptPath);

	return (jlong)dwReturn;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_BrowseVMFileFolderItem
(JNIEnv *env, jclass jclz, jstring path,jstring subPath,jint option,jobject jBackupVM,jobject retArr)
{
	
	IBrowseVMHierarchy *pList = NULL;

	DWORD dwRet = CreateIBrowseVMHierarchy(&pList);
	if(dwRet)
	{
		wprintf(L"get handler failed[%d]\n", dwRet);		
	}
	else
	{	
		wchar_t* pDestPath = NULL;
		wchar_t* pSubPath = NULL;
		if(path != NULL)
			pDestPath = JStringToWCHAR(env, path);

		if(subPath != NULL)
			pSubPath = JStringToWCHAR(env, subPath);

		jclass class_JBackupVM = env->FindClass("com/ca/arcflash/webservice/jni/model/JBackupVM");	

		jfieldID field_vmName	= env->GetFieldID(class_JBackupVM, "vmName", "Ljava/lang/String;");
		jfieldID field_username	= env->GetFieldID(class_JBackupVM, "username", "Ljava/lang/String;");
		jfieldID field_password	= env->GetFieldID(class_JBackupVM, "password", "Ljava/lang/String;");
		jfieldID field_instanceUUID	= env->GetFieldID(class_JBackupVM, "instanceUUID", "Ljava/lang/String;");
		jfieldID field_vmVMX	= env->GetFieldID(class_JBackupVM, "vmVMX", "Ljava/lang/String;");

		jfieldID field_esxServerName	= env->GetFieldID(class_JBackupVM, "esxServerName", "Ljava/lang/String;");
		jfieldID field_esxUsername	= env->GetFieldID(class_JBackupVM, "esxUsername", "Ljava/lang/String;");
		jfieldID field_esxPassword	= env->GetFieldID(class_JBackupVM, "esxPassword", "Ljava/lang/String;");
		jfieldID field_protocol	= env->GetFieldID(class_JBackupVM, "protocol", "Ljava/lang/String;");
		jfieldID field_port	= env->GetFieldID(class_JBackupVM, "port", "I");

		VM_BROWSEINFO vm_Browseinfo;

		vm_Browseinfo.strVM_Name = GetStringFromField(env,&jBackupVM,field_vmName);
		vm_Browseinfo.strVM_User = GetStringFromField(env,&jBackupVM,field_username);
		vm_Browseinfo.strVM_Pwd = GetStringFromField(env,&jBackupVM,field_password);
		vm_Browseinfo.strVM_VMXPath = GetStringFromField(env,&jBackupVM,field_vmVMX);
		vm_Browseinfo.strVMInstGUID = GetStringFromField(env,&jBackupVM,field_instanceUUID);

		vm_Browseinfo.strVMHost_Name = GetStringFromField(env,&jBackupVM,field_esxServerName);
		vm_Browseinfo.strVMHost_User = GetStringFromField(env,&jBackupVM,field_esxUsername);
		vm_Browseinfo.strVMHost_Pwd = GetStringFromField(env,&jBackupVM,field_esxPassword);
		vm_Browseinfo.VMHost_Port = env->GetIntField(jBackupVM,field_port);
		vm_Browseinfo.strParent = pDestPath;
		vm_Browseinfo.option = option;
		vm_Browseinfo.strTobeCreatedFolder = pSubPath;
		
		vector<FILE_INFO> vList;

		/*dwRet = AFVM_BrowseFolderUnderDir(vList, GetStringFromField(env,&jBackupVM,field_esxServerName), GetStringFromField(env,&jBackupVM,field_esxUsername),GetStringFromField(env,&jBackupVM,field_esxPassword),
			GetStringFromField(env,&jBackupVM,field_vmName),GetStringFromField(env,&jBackupVM,field_vmVMX),GetStringFromField(env,&jBackupVM,field_username),GetStringFromField(env,&jBackupVM,field_password),
			GetStringFromField(env,&jBackupVM,field_instanceUUID),pDestPath,1,NULL);*/
		dwRet = pList->Browse_VM_Folders(vList,vm_Browseinfo);
		
		if(dwRet)
		{
			wprintf(L"get handler failed[%d]\n", dwRet);		
		}else{
			if(option == 1)
				AddVecFileInfo2List(env, &retArr,vList);
		}

		pList->Release();

		if (class_JBackupVM != NULL) env->DeleteLocalRef(class_JBackupVM);	

		if(pDestPath != NULL)
			free(pDestPath);

		if(pSubPath != NULL)
			free(pSubPath);
	}
	

	return dwRet;

}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DeleteAllPendingFileCopyJobs(JNIEnv* env,jclass this_class,jstring strDest,jstring strDestDomain,jstring strDestUserName,jstring strDestPassword)
{
	return FileCopyCommonJNI::DeleteAllPendingFileCopyJobs(env, this_class, strDest, strDestDomain, strDestUserName, strDestPassword);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DisableFileCopy(JNIEnv* env, jclass this_class, jstring strDest, jstring strDestDomain, jstring strDestUserName, jstring strDestPassword)
{
	return FileCopyCommonJNI::DisableFileCopy(env, this_class, strDest, strDestDomain, strDestUserName, strDestPassword);
}
/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    AFIGetFullSess4Inc
 * Signature: (Lcom/ca/arcflash/webservice/jni/model/JRWLong;JLjava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFIGetFullSess4Inc
  (JNIEnv * env, jclass cls, jobject fullSessNumber, jlong increSessNumber, jstring backupDest)
{
	DWORD ret = 0;
	wchar_t* wbackupDest = JStringToWCHAR(env, backupDest);
	DWORD dwFullSessNumber = 0;
	ret = AFIGetFullSess4Inc(dwFullSessNumber, increSessNumber, wbackupDest);

	if(ret == 0 && dwFullSessNumber != 0)
	{
		DWORDTOJRWLong(env, dwFullSessNumber, fullSessNumber);
	}

	if(wbackupDest != NULL)
	{
		free(wbackupDest);	
	}
	
	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getMachineDetailFromBackupSession
(JNIEnv * env, jclass cls, jstring backupDest, jobject result)
{
	DWORD ret = 0;
	D2D_NODE_INFO_EX stNodeInfo;
	memset(&stNodeInfo, 0 , sizeof(stNodeInfo));

	// get machine detail from backup session folder
	LPCWSTR lpszBackupDest = JStringToWCHAR(env, backupDest);
	ret = AFGetD2DNodeInfo( lpszBackupDest, &stNodeInfo);

	// set return value
	if(ret == 0){
		// machineType
		jclass className = env->GetObjectClass(result);
		jmethodID setMachineTypeID = env->GetMethodID(className, "setMachineType", "(I)V");
		env->CallVoidMethod(result, setMachineTypeID, (jint)stNodeInfo.nodeType);
		// hostName
		SetStringValue2Field(env, result, "hostName", stNodeInfo.szHostName); 
		// hypervisorHostName
		SetStringValue2Field(env, result, "hypervisorHostName", stNodeInfo.szHyperVisorHostName); 

		jmethodID setOsMajorVersion = env->GetMethodID(className, "setOsMajorVersion", "(I)V");
		env->CallVoidMethod(result, setOsMajorVersion, (jint)stNodeInfo.dwOSMajorVersion);

		jmethodID setOsMinorVersion = env->GetMethodID(className, "setOsMinorVersion", "(I)V");
		env->CallVoidMethod(result, setOsMinorVersion, (jint)stNodeInfo.dwOSMinorVersion);

		jmethodID setOsProductType = env->GetMethodID(className, "setOsProductType", "(I)V");
		env->CallVoidMethod(result, setOsProductType, (jint)stNodeInfo.dwOSProductType);

		jmethodID setOsSuiteMask = env->GetMethodID(className, "setOsSuiteMask", "(I)V");
		env->CallVoidMethod(result, setOsSuiteMask, (jint)stNodeInfo.dwOSSuiteMask);

		jmethodID setHyperVisorNumberOfProcessors = env->GetMethodID(className, "setHyperVisorNumberOfProcessors", "(I)V");
		env->CallVoidMethod(result, setHyperVisorNumberOfProcessors, (jint)stNodeInfo.dwHyperVisorNumberOfProcessors);

		jmethodID setHyperVisorNumberOfLogicalProcessors = env->GetMethodID(className, "setHyperVisorNumberOfLogicalProcessors", "(I)V");
		env->CallVoidMethod(result, setHyperVisorNumberOfLogicalProcessors, (jint)stNodeInfo.dwHyperVisorNumberOfLogicalProcessors);

		jmethodID setNumberOfProcessors = env->GetMethodID(className, "setNumberOfProcessors", "(I)V");
		env->CallVoidMethod(result, setNumberOfProcessors, (jint)stNodeInfo.dwNumberOfProcessors);

		jmethodID setNumberOfLogicalProcessors = env->GetMethodID(className, "setNumberOfLogicalProcessors", "(I)V");
		env->CallVoidMethod(result, setNumberOfLogicalProcessors, (jint)stNodeInfo.dwNumberOfLogicalProcessors);
	}
       
	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getTickCount
  (JNIEnv *, jclass)
{
	return GetTickCount();
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getRPSDataStoreHashKey
 * Signature: (Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getRPSDataStoreHashKey
  (JNIEnv * env, jclass jcls, jstring bkDest, jint sessionNumber, jstring sessionPwd)
{
	wchar_t* wzBkDest = JStringToWCHAR(env, bkDest);
	wchar_t* wzSessionPwd = JStringToWCHAR(env, sessionPwd);
	wstring wsHashKey;
	long ret = AFIGetDataStoreHashKey(wsHashKey,wzBkDest, sessionNumber, wzSessionPwd);
	
	if(ret == 0)
	{
		return WCHARToJString(env, (wchar_t*)wsHashKey.c_str());
	}
	else
	{
		return NULL;
	}

	if(wzBkDest != NULL)
	{
		free(wzBkDest);
	}

	if(wzSessionPwd != NULL){
		free(wzSessionPwd);
	}
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_isCatalogExist
	(JNIEnv * env, jclass jcls, jstring backupDest, jint sessionNum)
{
	jboolean ret = false;
	wchar_t* wszDest = JStringToWCHAR(env, backupDest);
	ret = AFHasCatalogFiles(wszDest, sessionNum);
	if(wszDest != NULL)
		free(wszDest);
	return ret;
}


JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getSessPathByNo
  (JNIEnv * env, jclass jcls, jstring strCurDest, jint sessionNumber)
{
	wchar_t* CurDest  = JStringToWCHAR(env, strCurDest);
	std::wstring destStr, sessionPathStr;

	long ret = AFGetSessPathByNo(CurDest, sessionNumber, destStr, sessionPathStr);

	if(CurDest != NULL)
	{
		free(CurDest);
	}
	return WCHARToJString(env, (wchar_t*)sessionPathStr.c_str());
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFIVerifyDestUser
	(JNIEnv *env, jclass cls, jstring destination, jstring userName, jstring password)
{
	wchar_t* pwszDest = JStringToWCHAR(env, destination);
	wchar_t* pwszUser = JStringToWCHAR(env, userName);
	wchar_t* pwszPwd = JStringToWCHAR(env, password);

	DWORD dwRet = AFIVerifyDestUser(pwszDest, pwszUser, pwszPwd);

	if(pwszDest != NULL)
		free(pwszDest);
	if(pwszPwd != NULL)
		free(pwszPwd);
	if(pwszUser != NULL)
		free(pwszUser);

	return dwRet;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getIPList
	(JNIEnv * env, jclass jcls, jobject listObj)
{
	jclass listCls = env->GetObjectClass(listObj);
	jmethodID addMethod = env->GetMethodID(listCls, "add", "(Ljava/lang/Object;)Z");

	std::vector<NIC_INFO> vecNics;
	CNICMonitor::GetNicAdapters(vecNics, TRUE);

	for(int i = 0; i < vecNics.size(); i ++)
	{
		NIC_INFO nicInfo = vecNics.at(i);
		if(!nicInfo.bConnected)
			continue;
		for(int j = 0; j < nicInfo.ipAddrList.size(); j ++)
		{
			NIC_ADDR ip = nicInfo.ipAddrList.at(j);
			env->CallBooleanMethod(listObj, addMethod, WCHARToJString(env, (wchar_t*)ip.ipAddr.c_str()));
		}
	}
}

jlong OpenHypervHandle
	(JNIEnv * env, jclass klass, jstring serverName, jstring user, jstring pwd)
{
	wstring wstrServer = Utility_JStringToWCHAR(env, serverName);
	wstring wstrUser = Utility_JStringToWCHAR( env, user );
	wstring wstrPwd = L"";
	if(NULL != pwd)
		wstrPwd = Utility_JStringToWCHAR(env, pwd);
	IHypervOperation *pHyperv = OpenHyperVOperation( wstrServer, wstrUser, wstrPwd );
	return (jlong)pHyperv;
}

void CloseHypervHandle
	(JNIEnv * env, jclass klass, jlong handle)
{
	DelIHyperVOperation( (IHypervOperation*)handle );
}

static void AddVMLIST2VMList(IN const wstring& wstrServer, IN const wstring& strClusterName, IN HyperVCluster::IClusterOperation * pCluster, 
	IN wstring& strIntegrationServicesGuestInstallerVersion, IN const vector<IVirtualMachine*>& vVms, 
	IN JNIEnv * env, IN jclass class_hv, OUT jobject hvList)
{
	enum {VM_TYPE_UNKNOWN = 0, VM_TYPE_STANDALONE = 1, VM_TYPE_IN_CLUSTER = 2, VM_TYPE_IN_CSV};
	//query methods
	jmethodID hv_constructor = env->GetMethodID(class_hv, "<init>", "()V");
	jmethodID hv_setVmName = env->GetMethodID(class_hv, "setVmName", "(Ljava/lang/String;)V");
	jmethodID hv_setVmUUID = env->GetMethodID(class_hv, "setVmUuid", "(Ljava/lang/String;)V");
	jmethodID hv_setVmHostName = env->GetMethodID(class_hv, "setVmHostName", "(Ljava/lang/String;)V");
	jmethodID hv_setHypervisor = env->GetMethodID(class_hv, "setHypervisor", "(Ljava/lang/String;)V");
	jmethodID hv_setClusterName = env->GetMethodID(class_hv, "setClusterName", "(Ljava/lang/String;)V");
	jmethodID hv_setVmType = env->GetMethodID(class_hv, "setVmType", "(I)V");

	jmethodID hv_setVmGuestOS = env->GetMethodID(class_hv, "setVmGuestOS", "(Ljava/lang/String;)V");
	jmethodID hv_setVmPowerStatus = env->GetMethodID(class_hv, "setVmPowerStatus", "(I)V");
	jmethodID hv_setVmInteServiceSatus = env->GetMethodID(class_hv, "setVmInteServiceSatus", "(I)V");
	jmethodID hv_setIpList = env->GetMethodID(class_hv, "setIpList", "(Ljava/util/List;)V");

	//array
	jclass arrayListClass = env->FindClass("java/util/ArrayList");
	jmethodID arrayListConstr = env->GetMethodID(arrayListClass, "<init>", "()V");
	jmethodID listAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");

	//vm list
	vector<IVirtualMachine*>::const_iterator itr = vVms.begin();
	while( itr != vVms.end() )
	{
		jobject jhvInfo = env->NewObject(class_hv, hv_constructor);
		env->CallVoidMethod(jhvInfo, hv_setVmName, WCHARToJString(env, (*itr)->GetName().c_str()));
		env->CallVoidMethod(jhvInfo, hv_setVmUUID, WCHARToJString(env, (*itr)->GetGuid().c_str()));
		env->CallVoidMethod(jhvInfo, hv_setVmHostName, WCHARToJString(env, (*itr)->GetHostName().c_str()));
		env->CallVoidMethod(jhvInfo, hv_setHypervisor, WCHARToJString(env, wstrServer.c_str()));
		env->CallVoidMethod(jhvInfo, hv_setVmGuestOS, WCHARToJString(env, (*itr)->GetOSName().c_str()));
		env->CallVoidMethod(jhvInfo, hv_setVmPowerStatus, (*itr)->GetState());
		env->CallVoidMethod(jhvInfo, hv_setVmInteServiceSatus, (*itr)->GetIntegrationServicesStatus(strIntegrationServicesGuestInstallerVersion));

		//if vm is cluster resource or data folder resides on CSV
		int nVMtype = VM_TYPE_STANDALONE;
		if (NULL != pCluster)
		{
			wstring strGrpName;
			if ((pCluster->IsVMInCluster((*itr)->GetGuid(), strGrpName)))
			{
				nVMtype = VM_TYPE_IN_CLUSTER;
			} 
			else if((pCluster->IsPathOnCSV((*itr)->GetDataFolder())))
			{
				nVMtype = VM_TYPE_IN_CLUSTER;
			}

			env->CallVoidMethod(jhvInfo, hv_setClusterName, WCHARToJString(env, strClusterName.c_str()));
		}
		env->CallVoidMethod(jhvInfo, hv_setVmType, nVMtype);

		// VM IP address list
		vector<wstring> vIP;
		(*itr)->GetIPAddress(vIP);

		jobject jIPAddressList = env->NewObject(arrayListClass, arrayListConstr);

		AddVWSTRING_ITEM2List(env, jIPAddressList, vIP);

		env->CallVoidMethod(jhvInfo, hv_setIpList, jIPAddressList);

		if(jIPAddressList != NULL)
			env->DeleteLocalRef(jIPAddressList);
		env->CallBooleanMethod(hvList, listAddMethod, jhvInfo);

		itr++;
	}
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetVmList
	(JNIEnv * env, jclass klass, jstring serverName, jstring user, jstring pwd,  jboolean isSpecified )
{
	ULONG_PTR handle = OpenHypervHandle(env, klass, serverName, user, pwd); //<sonmi01>2014-5-6 #116609: Agent ¨C UDP Agent web service is getting stopped in build 1892
	if(0 == handle)
	{
		string msg = "fail to connect hyper-v server";
		ThrowWSJNIException( env, klass, env->NewStringUTF(msg.c_str()), 100);
		return NULL;
	}

	wstring wstrServer = Utility_JStringToWCHAR(env, serverName);
	wstring wstrUser = Utility_JStringToWCHAR( env, user );
	wstring wstrPwd = L"";
	if(NULL != pwd)
		wstrPwd = Utility_JStringToWCHAR(env, pwd);

	//query vminfo class
	jclass class_hv= env->FindClass("com/ca/arcflash/webservice/jni/model/JHypervVMInfo");
	jclass arrayListClass = env->FindClass("java/util/ArrayList");

	//output object
	jmethodID arrayListConstr = env->GetMethodID(arrayListClass, "<init>", "()V");
	jobject hvList = env->NewObject(arrayListClass, arrayListConstr);

	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HYPERV_NODE_TYPE_E enType = pHyperv->GetHyperVServerType();

	if ((HYPERV_TYPE_STANDALONE == enType) || 
		((isSpecified) && (HYPERV_TYPE_CLUSTER_PHYSICAL_NODE == enType)))
	{
		vector<IVirtualMachine*> vVms;
		DWORD dwResult = pHyperv->_GetVmList(vVms);
		if( dwResult )
		{
			ThrowWSJNIException(env, klass, env->NewStringUTF("Error occurs in GetVmList"), 200);
		}
		else
		{
			// host ic version
			wstring strIntegrationServicesGuestInstallerVersion;
			pHyperv->GetIntegrationServicesGuestInstallerVersion(strIntegrationServicesGuestInstallerVersion);

			HyperVCluster::IClusterOperation * pCluster = NULL;  // to set the VM type correctly, get pCluster and send to AddVMLIST2VMList
			if (HYPERV_TYPE_CLUSTER_PHYSICAL_NODE == enType)
			{
				pCluster = pHyperv->GetClusterOperation();
			}
			AddVMLIST2VMList(wstrServer, L"", pCluster/*NULL*/, strIntegrationServicesGuestInstallerVersion, vVms, env, class_hv, hvList);

			//release the vms
			vector<IVirtualMachine*>::iterator itr = vVms.begin();
			while( itr != vVms.end() )
			{
				DelIVirtualMachine(*itr);
				itr++;
			}
		}
	}
	else if ((HYPERV_TYPE_CLUSTER_PHYSICAL_NODE == enType) || 
		(HYPERV_TYPE_CLUSTER_VIRTUAL_NODE == enType))
	{
		vector<wstring> vecNodes;
		HyperVCluster::IClusterOperation * pCluster = pHyperv->GetClusterOperation();
		if (NULL == pCluster)
		{
			string msg = "fail to get cluster operation";
			ThrowWSJNIException( env, klass, env->NewStringUTF(msg.c_str()), 100);
		}
		else
		{
			pCluster->GetActiveClusterNodes(vecNodes);
			for (size_t ii = 0; ii < vecNodes.size(); ii++)
			{
				IHypervOperation * pClusterNode = OpenHyperVOperation(vecNodes[ii], wstrUser, wstrPwd);
				if (NULL == pClusterNode)
				{
					continue;
				}

				vector<IVirtualMachine*> vVms;
				pClusterNode->_GetVmList(vVms);

				// host ic version
				wstring strIntegrationServicesGuestInstallerVersion;
				pClusterNode->GetIntegrationServicesGuestInstallerVersion(strIntegrationServicesGuestInstallerVersion);

				wstring strClusterName;
				BOOL bVirtualNode = FALSE;
				DWORD dwError = pCluster->IsClusterVirtualNode(wstrServer, bVirtualNode);
				if(dwError == 0 && bVirtualNode)
				{
					strClusterName = wstrServer;
				}
				else
				{
					strClusterName = pCluster->GetClusterName();
				}

				AddVMLIST2VMList(vecNodes[ii], strClusterName, pCluster, strIntegrationServicesGuestInstallerVersion, vVms, env, class_hv, hvList);

				//release the vms
				vector<IVirtualMachine*>::iterator itr = vVms.begin();
				while( itr != vVms.end() )
				{
					DelIVirtualMachine(*itr);
					itr++;
				}

				DelIHyperVOperation(pClusterNode);
				pClusterNode = NULL;
			}
		}
	}

	CloseHypervHandle(env, klass, handle);
	return hvList;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetHypervInfoList
(JNIEnv * env, jclass klass, jstring serverName, jstring user, jstring pwd, jobject hypervInfo)
{
    CDbgLog logObj(L"NativeFacade");
	ULONG_PTR handle = OpenHypervHandle(env, klass, serverName, user, pwd);
	if (0 == handle)
	{
		DWORD dwLastError = GetLastError();

		logObj.LogW(LL_ERR, 0, L"Java_com_ca_arcflash_webservice_jni_WSJNI_GetHypervList : OpenHypervHandle failed, dwLastError = 0x%x.", dwLastError);

		if (dwLastError == WBEM_E_INVALID_NAMESPACE)
		{
			return GET_HYPERV_NOT_INSTALL_HYPERV_ROLE;
		}
		else if ((dwLastError == E_ACCESSDENIED) || (dwLastError == WBEM_E_ACCESS_DENIED))
		{
			return GET_HYPERV_ACCESS_DENIED;
		}
		else if (dwLastError == WIN_SERVER_UNAVAILABLE)
		{
			return GET_HYPERV_SERVER_UNAVAILABLE;
		}
		else
		{
			return GET_HYPERV_OTHER_ERROR;
		}
	}

	wstring wstrServer = Utility_JStringToWCHAR(env, serverName);
	wstring wstrUser = Utility_JStringToWCHAR(env, user);
	wstring wstrPwd = L"";
	if (NULL != pwd)
	{
		wstrPwd = Utility_JStringToWCHAR(env, pwd);
	}
		
	jclass class_hv = env->FindClass("com/ca/arcflash/webservice/jni/model/JHypervInfo");
	jclass arrayListClass = env->FindClass("java/util/ArrayList");
	jmethodID arrayListConstr = env->GetMethodID(arrayListClass, "<init>", "()V");
	jmethodID addMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");

	jmethodID hv_setHypervName = env->GetMethodID(class_hv, "setHypervName", "(Ljava/lang/String;)V");
	jmethodID hv_setCluster = env->GetMethodID(class_hv, "setCluster", "(Z)V");
	jmethodID hv_setNodeName = env->GetMethodID(class_hv, "setNodeName", "(Ljava/lang/String;)V");
	jmethodID hv_setCurrentHostServer = env->GetMethodID(class_hv, "setCurrentHostServer", "(Z)V");
	

	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HYPERV_NODE_TYPE_E enType = pHyperv->GetHyperVServerType();

	if (HYPERV_TYPE_STANDALONE == enType)
	{
		jobject jhvInfo = env->NewObject(class_hv, arrayListConstr);
		env->CallVoidMethod(jhvInfo, hv_setCluster, false);
		env->CallVoidMethod(jhvInfo, hv_setHypervName, serverName);
		env->CallVoidMethod(jhvInfo, hv_setNodeName, serverName);
		env->CallVoidMethod(jhvInfo, hv_setCurrentHostServer, false);
		env->CallBooleanMethod(hypervInfo, addMethod, jhvInfo);
	}
	else
	{
		HyperVCluster::IClusterOperation * pCluster = pHyperv->GetClusterOperation();
		if (NULL == pCluster)
		{
			logObj.LogW(LL_ERR, 0, L"Java_com_ca_arcflash_webservice_jni_WSJNI_GetHypervList : GetClusterOperation failed.");
			return GET_HYPERV_OTHER_ERROR;
		}
		else
		{
			vector<wstring> vecNodes;

			wstring strClusterName;
			BOOL bVirtualNode = FALSE;
			DWORD dwError = pCluster->IsClusterVirtualNode(wstrServer, bVirtualNode);
			if (dwError == 0 && bVirtualNode)
			{
				strClusterName = wstrServer;
			}
			else
			{
				strClusterName = pCluster->GetClusterName();
			}

			pCluster->GetClusterNodes(vecNodes);

			sort(vecNodes.begin(), vecNodes.end(), less<wstring>());

			wstring strCurrentHostServerNameInCluster = pCluster->GetClusterOnwerNode();

			for (size_t i = 0; i < vecNodes.size(); i++)
			{
				jobject jhvInfo = env->NewObject(class_hv, arrayListConstr);
				env->CallVoidMethod(jhvInfo, hv_setCluster, true);
				env->CallVoidMethod(jhvInfo, hv_setHypervName, WCHARToJString(env, strClusterName.c_str()));
				env->CallVoidMethod(jhvInfo, hv_setNodeName, WCHARToJString(env, vecNodes[i].c_str()));
				if (strCurrentHostServerNameInCluster == vecNodes[i])
				{
					env->CallVoidMethod(jhvInfo, hv_setCurrentHostServer, true);
				}
				else
				{
					env->CallVoidMethod(jhvInfo, hv_setCurrentHostServer, false);
				}
				
				env->CallBooleanMethod(hypervInfo, addMethod, jhvInfo);
			}
		}
	}

	CloseHypervHandle(env, klass, handle);
	return GET_HYPERV_SUCCESS;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_updateD2DJobHistory
	(JNIEnv *env, jclass jcls, jobject jJobHistory )
{
	PFLASHDB_JOB_HISTORY pHistory = new FLASHDB_JOB_HISTORY();
	ZeroMemory( pHistory, sizeof(FLASHDB_JOB_HISTORY) );
	JJobHistory2PFLASHDB_JOB_HISTORY( env, jJobHistory, pHistory );
	pHistory->dwStatus = JS_CRASHED;

	WCHAR szHostName[MAX_PATH] = {0};
	DWORD dwSize = _ARRAYSIZE(szHostName);
	::GetComputerName( szHostName, &dwSize );

	wstring strMyNodeID = L"";
	AFIGetUserSID( strMyNodeID );

	if( wcslen(pHistory->wszRunningNodeUUID)==0 )
		wcsncpy_s( pHistory->wszRunningNodeUUID, _ARRAYSIZE(pHistory->wszRunningNodeUUID), strMyNodeID.c_str(), _TRUNCATE );
	if( wcslen(pHistory->wszDisposeNodeUUID)==0 )
		wcsncpy_s( pHistory->wszDisposeNodeUUID, _ARRAYSIZE(pHistory->wszDisposeNodeUUID), strMyNodeID.c_str(), _TRUNCATE );

	if( wcslen(pHistory->wszRunningNode)==0 && _wcsicmp(pHistory->wszRunningNodeUUID,strMyNodeID.c_str())==0 )
		wcsncpy_s( pHistory->wszRunningNode, _ARRAYSIZE(pHistory->wszRunningNode), szHostName, _TRUNCATE );
	if( wcslen(pHistory->wszDisposeNode)==0 && _wcsicmp(pHistory->wszDisposeNodeUUID, strMyNodeID.c_str())==0 )
		wcsncpy_s( pHistory->wszDisposeNode, _ARRAYSIZE(pHistory->wszDisposeNode), szHostName, _TRUNCATE );

	StartNewJob( APT_D2D, pHistory );
	delete pHistory;
	pHistory = NULL;

	return 0;
}


JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_addMissedD2DJobHistory
	(JNIEnv *env, jclass jcls, jobject jJobHistory )
{
	PFLASHDB_JOB_HISTORY pHistory = new FLASHDB_JOB_HISTORY();
	ZeroMemory( pHistory, sizeof(FLASHDB_JOB_HISTORY) );
	JJobHistory2PFLASHDB_JOB_HISTORY( env, jJobHistory, pHistory );

	WCHAR szHostName[MAX_PATH] = {0};
	DWORD dwSize = _ARRAYSIZE(szHostName);
	::GetComputerName( szHostName, &dwSize );

	wstring strMyNodeID = L"";
	AFIGetUserSID( strMyNodeID );

	if( wcslen(pHistory->wszRunningNodeUUID)==0 )
		wcsncpy_s( pHistory->wszRunningNodeUUID, _ARRAYSIZE(pHistory->wszRunningNodeUUID), strMyNodeID.c_str(), _TRUNCATE );
	if( wcslen(pHistory->wszDisposeNodeUUID)==0 )
		wcsncpy_s( pHistory->wszDisposeNodeUUID, _ARRAYSIZE(pHistory->wszDisposeNodeUUID), strMyNodeID.c_str(), _TRUNCATE );

	if( wcslen(pHistory->wszRunningNode)==0 && _wcsicmp(pHistory->wszRunningNodeUUID,strMyNodeID.c_str())==0 )
		wcsncpy_s( pHistory->wszRunningNode, _ARRAYSIZE(pHistory->wszRunningNode), szHostName, _TRUNCATE );
	if( wcslen(pHistory->wszDisposeNode)==0 && _wcsicmp(pHistory->wszDisposeNodeUUID, strMyNodeID.c_str())==0 )
		wcsncpy_s( pHistory->wszDisposeNode, _ARRAYSIZE(pHistory->wszDisposeNode), szHostName, _TRUNCATE );

	StartNewJob( APT_D2D, pHistory );

	MarkJobEnd( APT_D2D, pHistory->ullJobId, pHistory->dwStatus );

	if( pHistory->dwJobType==AF_JOBTYPE_BACKUP || 
		pHistory->dwJobType==AF_JOBTYPE_BACKUP_VMWARE || 
		pHistory->dwJobType==AF_JOBTYPE_BACKUP_HYPERV )
	{
		BOOL bVMBackupJob = TRUE;
		if( _wcsicmp(pHistory->wszRunningNodeUUID, pHistory->wszDisposeNodeUUID)==0 )
			bVMBackupJob = FALSE;
		AFAddMissedBackupHistory( pHistory->ullUTCStartTime, pHistory->ullJobId, pHistory->dwJobMethod, 
			pHistory->wszJobName, bVMBackupJob ? pHistory->wszDisposeNodeUUID : NULL, pHistory );
	}

	delete pHistory;
	pHistory = NULL;

	return 0;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getD2DServerSID
	(JNIEnv * env, jclass jcls)
{
	wstring sid;
	DWORD ret = AFIGetUserSID(sid);
	if(ret == 0)
		return WCHARToJString(env, sid);
	else
		return NULL;
}

//
// mark the job as end for D2D job
// jobID:          the job ID
// jobStatus:      the job end status
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_markD2DJobEnd
       (JNIEnv *env, jclass jcls, jlong jobID, jlong jobStatus, jstring jobDetails)
{
	wchar_t* lpszJobDetails = JStringToWCHAR(env, jobDetails);

	DWORD ret = MarkJobEnd( APT_D2D, jobID, jobStatus, lpszJobDetails);

	if( lpszJobDetails )	free( lpszJobDetails);
	return ret;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getActivityLogPaths4Sync
 * Signature: (ILjava/lang/String;Ljava/util/ArrayList;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getActivityLogPaths4Sync
  (JNIEnv *env, jclass jcls, jint logType, jstring vmInstanceUUID, jobject pathsList)
{
	//For incremental sync
	wchar_t* vmUUID = JStringToWCHAR(env, vmInstanceUUID);
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;
	int ret = 0;
	if(logType == 1)
	{
		//for CPM
		ret = PrepareSyncLogOfCPM(APT_D2D, vmUUID, &logFiles, &count);
	}
	else if(logType == 2)
	{
		//for VCM
		ret = PrepareSyncLogOfVCM(APT_D2D, vmUUID, &logFiles, &count);
	}

	AddPathsToList(env, pathsList, logFiles, count);

	FreeSyncLogFiles(logFiles);

	if(vmUUID != NULL)
	{
		free(vmUUID);

	}
	return ret;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getRecoveryPoint4Sync
 */
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getRecoveryPoint4Sync
	(JNIEnv *env, jclass clz, jstring destination, jstring domain, jstring user,jstring pwd, jstring vmUUID, jboolean jbFullSync, jobject pathsList )
{
	wchar_t* pDest = JStringToWCHAR(env, destination);
	wchar_t* pDomain = JStringToWCHAR(env, domain);
	wchar_t* pUser = JStringToWCHAR(env, user);
	wchar_t* pPwd = JStringToWCHAR(env, pwd);
	wchar_t* pVmUUID = JStringToWCHAR(env, vmUUID);

	NET_CONN_INFO info;
	memset(&info, 0, sizeof(NET_CONN_INFO));

	if (pDest!=NULL)
		wcscpy_s(info.szDir, pDest);
	if (pDomain != NULL)
		wcscpy_s(info.szDomain, pDomain);
	if (pUser != NULL)
		wcscpy_s(info.szUsr, pUser);
	if (pPwd != NULL)
		wcscpy_s(info.szPwd, pPwd);

	//
	// generate recovery point sync info
	//
	AFIGenerateRecoveryPointSyncInfo( info, pVmUUID, jbFullSync );
	
	//
	// return the file list to web service.
	//
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;
	DATASYNC_ARGS dataSyncArgs;
	dataSyncArgs.dwSyncTo = SYNC_DATA_TO_CPM;
	dataSyncArgs.dwSyncFrom = SYNC_DATA_FROM_D2D;
	dataSyncArgs.dwSyncType = SYNC_DATA_RECOVERYPOINT_INFO;
	if( pVmUUID )
		wcsncpy_s( dataSyncArgs.szNodeUUID, _countof(dataSyncArgs.szNodeUUID), pVmUUID, _TRUNCATE ); 

	int ret = 0;
	ret = PrepareSyncData(dataSyncArgs, &logFiles, &count);
	AddPathsToList(env, pathsList, logFiles, count);
	FreeSyncLogFiles(logFiles);

	SAFE_FREE( pDest );
	SAFE_FREE( pDomain );
	SAFE_FREE( pUser );
	SAFE_FREE( pPwd );
	SAFE_FREE( pVmUUID );

	return ret;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getActivityLogPaths4Sync
 * Signature: (ILjava/lang/String;Ljava/util/ArrayList;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getActivityLogPaths4FullSync
  (JNIEnv *env, jclass jcls, jint logType, jstring vmInstanceUUID, jobject pathsList)
{
	//For incremental sync
	wchar_t* vmUUID = JStringToWCHAR(env, vmInstanceUUID);
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;
	int ret = 0;
	if(logType == 1)
	{
		//for CPM
		ret = PrepareFullSyncLogOfCPM(APT_D2D, vmUUID, &logFiles, &count);
	}
	else if(logType == 2)
	{
		//for VCM
		ret = PrepareFullSyncLogOfVCM(APT_D2D, vmUUID, &logFiles, &count);
	}

	AddPathsToList(env, pathsList, logFiles, count);

	FreeSyncLogFiles(logFiles);

	if(vmUUID != NULL)
	{
		free(vmUUID);

	}
	return ret;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getJobHistoryPaths4IncrementalSync
  (JNIEnv *env, jclass jcls, jobject pathsList)
{
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;
	int ret = 0;

	ret = PrepareSyncJobHistoryOfCPM(APT_D2D, &logFiles, &count);

	AddPathsToList(env, pathsList, logFiles, count);

	FreeSyncLogFiles(logFiles);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getJobHistoryPath4FullSync
	(JNIEnv *env, jclass jcls, jobject pathsList)
{
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;
	int ret = 0;

	ret = PrepareFullSyncJobHistoryOfCPM(APT_D2D, &logFiles, &count);

	AddPathsToList(env, pathsList, logFiles, count);
	
	FreeSyncLogFiles(logFiles);

	return ret;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getFilePaths4Sync
	(JNIEnv *env, jclass jcls, jlong jlSyncTo, jlong jlSyncFrom, jlong jlSyncType, jstring vmInstanceUUID, jobject pathsList)
{
	PSyncLogFiles logFiles = NULL;
	UINT count = 0;

	DATASYNC_ARGS dataSyncArgs;
	dataSyncArgs.dwSyncTo = jlSyncTo;
	dataSyncArgs.dwSyncFrom = jlSyncFrom;
	dataSyncArgs.dwSyncType = jlSyncType;

	wchar_t* vmUUID = JStringToWCHAR(env, vmInstanceUUID); 
	if(vmUUID)
	{
		wcsncpy_s( dataSyncArgs.szNodeUUID, _countof(dataSyncArgs.szNodeUUID), vmUUID, _TRUNCATE ); 
		free(vmUUID); 
	}

	int ret = 0;

	ret = PrepareSyncData(dataSyncArgs, &logFiles, &count);

	AddPathsToList(env, pathsList, logFiles, count);

	FreeSyncLogFiles(logFiles);

	return ret;
}


JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_isX86
	(JNIEnv *, jclass){
		return sizeof(void*) == 4 ? true : false;
}


JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getJobHistory
	(JNIEnv *env, jclass jcls, jlong start, jlong request, jobject jFilter, jobject jJobHistoryResult)
{
	PFLASHDB_JOB_HISTORY pHistories;
	ULONGLONG ullCnt = 0, ullTotal = 0;
	DWORD dwRet = 0;

	pHistories = (PFLASHDB_JOB_HISTORY)HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, sizeof(FLASHDB_JOB_HISTORY)*request);
	if(pHistories == NULL) 
	{
		CDbgLog dbgLog;
		dbgLog.LogW(LL_INF, 0, L"GetJobHistories cann't allocate memory for : %d job history", request);
	}

	if(jFilter != NULL) 
	{
		FLASHDB_JOB_HISTORY_FILTER_COL jobFilter;
		JFlashJobHistoryFilter2FLASHDB_JOB_HISTORY_FILTER_COL(env, jFilter, jobFilter);
		dwRet = GetJobHistories( APT_D2D, start, request, &jobFilter, &ullCnt, &ullTotal, &pHistories);
	} 
	else
	{
		dwRet = GetJobHistories( APT_D2D, start, request, NULL, &ullCnt, &ullTotal, &pHistories);
	}

	PFLASHDB_JOB_HISTORY2JFlashJobHistoryResult(env, pHistories, ullCnt, ullTotal, jJobHistoryResult);

	if(pHistories) HeapFree(GetProcessHeap(), 0, pHistories);

	return dwRet;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_ifJobHistoryExist
	(JNIEnv *env, jclass jcls, jint productType, jlong jobID, jint jobType, jstring agentNodeID)
{
	wchar_t* vmUUID = JStringToWCHAR(env, agentNodeID);

	BOOL returnValue = FALSE;
	PFLASHDB_JOB_HISTORY pHistory = new FLASHDB_JOB_HISTORY();
	ZeroMemory( pHistory, sizeof(FLASHDB_JOB_HISTORY) );
	DWORD dwRet = GetJobHistory(productType, jobID, pHistory );
	if( dwRet==0 )
	{
		if (pHistory->dwJobType == jobType)
		{
			if (pHistory->wszDisposeNodeUUID == NULL && vmUUID == NULL)
				returnValue = TRUE;
			else if (pHistory->wszDisposeNodeUUID != NULL && vmUUID != NULL)
			{
				if (_wcsicmp(pHistory->wszDisposeNodeUUID, vmUUID) == 0)
					returnValue = TRUE;
			}
		} 
	}

	delete pHistory;
	if(vmUUID != NULL)
		free(vmUUID);

	return returnValue;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_queryJobQueue
	(JNIEnv *env, jclass jcls, jlong jJobQType, jstring jJobQIdentity, 
	jobject jJobScriptList, jboolean jCreateJobQFolder, jstring jCatalogModeID)

{
	wchar_t *pwzJobQIdentity = JStringToWCHAR(env, jJobQIdentity);
	wchar_t *pwzCatalogModeID = JStringToWCHAR(env, jCatalogModeID);

	wstring wsJobQPath;
	WSVector vecJobScriptList;

	DWORD dwRet = AFIQueryJobQueue((E_QUEUE_TYPE)jJobQType, pwzJobQIdentity, &wsJobQPath, &vecJobScriptList, jCreateJobQFolder, pwzCatalogModeID);

	Convert2JRPSCatalogScriptInfo(env, wsJobQPath, vecJobScriptList, jJobScriptList);

	if(pwzJobQIdentity) free(pwzJobQIdentity);
	if(pwzCatalogModeID) free(pwzCatalogModeID);

	return dwRet;
}

JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHypervPFCDataConsistentStatus
  (JNIEnv *env, jclass jcls, jstring jVMUuid, jstring jVMUserName, jstring jVMPassword, jlong jHandle)
{
	IHypervOperation *pHyperV = ((IHypervOperation *)jHandle);
	if(NULL == pHyperV)
	{
		string msg = "Invalid IHypervOperation handle";
		ThrowWSJNIException( env, jcls, env->NewStringUTF(msg.c_str()), ERROR_INVALID_PARAMETER);
		return NULL;
	}

	wchar_t *pwszVMUuid     = JStringToWCHAR(env, jVMUuid);
	wchar_t *pwszVMUserName = JStringToWCHAR(env, jVMUserName);
	wchar_t *pwszVMPassword = JStringToWCHAR(env, jVMPassword);

	HYPERV_PFC_ITEMS status = {0};
	DWORD dwResult = pHyperV->PFCCheckVM(
		pwszVMUuid     == NULL? L"" : pwszVMUuid, 
		pwszVMUserName == NULL? L"" : pwszVMUserName, 
		pwszVMPassword == NULL? L"" : pwszVMPassword, status);

	if( dwResult )
	{
		ThrowWSJNIException(env, jcls, env->NewStringUTF("Error occurs in PFCDataConsistentStatus"), dwResult);
		return NULL;
	}

	jclass jclass_PFCStatus = env->FindClass("com/ca/arcflash/webservice/jni/model/JHypervPFCDataConsistencyStatus");
	jmethodID jmethodPFCStatusCons = env->GetMethodID(jclass_PFCStatus, "<init>", "()V");

	jmethodID jmethod_setHasNotSupportedFileSystem      = env->GetMethodID(jclass_PFCStatus, "setHasNotSupportedFileSystem", "(I)V");
	jmethodID jmethod_setHasNotSupportedDiskType        = env->GetMethodID(jclass_PFCStatus, "setHasNotSupportedDiskType", "(I)V");
	jmethodID jmethod_setIsIntegrationServiceInBadState = env->GetMethodID(jclass_PFCStatus, "setIsIntegrationServiceInBadState", "(I)V");
	jmethodID jmethod_setIsScopeSnapshotEnabled         = env->GetMethodID(jclass_PFCStatus, "setIsScopeSnapshotEnabled", "(I)V");
	jmethodID jmethod_setIsPhysicalHardDisk             = env->GetMethodID(jclass_PFCStatus, "setIsPhysicalHardDisk", "(I)V");
	jmethodID jmethod_setHasDiskOnRemoteShare             = env->GetMethodID(jclass_PFCStatus, "setHasDiskOnRemoteShare", "(I)V");
	jmethodID jmethod_setIsVMCredentialNotOK             = env->GetMethodID(jclass_PFCStatus, "setIsVMCredentialNotOK", "(I)V");
	jmethodID jmethod_setIsDataConsistencyNotSupported	= env->GetMethodID(jclass_PFCStatus, "setIsDataConsistencyNotSupported", "(I)V");
	jmethodID jmethod_setHasShadowStorageOnDifferentVolume = env->GetMethodID(jclass_PFCStatus, "setHasShadowStorageOnDifferentVolume", "(I)V");
	jmethodID jmethod_setHasStorageSpace = env->GetMethodID(jclass_PFCStatus, "setHasStorageSpace", "(I)V");

	jobject jobjPFCStatus = env->NewObject(jclass_PFCStatus, jmethodPFCStatusCons);

	env->CallVoidMethod(jobjPFCStatus, jmethod_setHasNotSupportedFileSystem, status.nHasNotSupportedFileSystem);
	env->CallVoidMethod(jobjPFCStatus, jmethod_setHasNotSupportedDiskType, status.nHasNotSupportedDiskType);
	env->CallVoidMethod(jobjPFCStatus, jmethod_setIsIntegrationServiceInBadState, status.nIsIntegrationServiceInBadState);
	env->CallVoidMethod(jobjPFCStatus, jmethod_setIsScopeSnapshotEnabled, status.nIsScopeSnapshotEnabled);
	env->CallVoidMethod(jobjPFCStatus, jmethod_setIsPhysicalHardDisk, status.nHasPhysicalHardDisk);
	env->CallVoidMethod(jobjPFCStatus, jmethod_setHasDiskOnRemoteShare, status.nHasDiskOnRemoteShare);
	env->CallVoidMethod(jobjPFCStatus, jmethod_setIsVMCredentialNotOK, status.nIsVMCredentialNotOK);
	env->CallVoidMethod(jobjPFCStatus, jmethod_setIsDataConsistencyNotSupported, status.nIsDataConsistencyNotSupported);
	env->CallVoidMethod(jobjPFCStatus, jmethod_setHasShadowStorageOnDifferentVolume, status.nHasShadowStorageOnDifferentVolume);
	env->CallVoidMethod(jobjPFCStatus, jmethod_setHasStorageSpace, status.nHasStorageSpace);

	if(pwszVMUuid != NULL) 	 free(pwszVMUuid);
	if(pwszVMUserName != NULL) free(pwszVMUserName);
	if(pwszVMPassword != NULL) free(pwszVMPassword);
	
	return jobjPFCStatus;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getSwitchIntFromFile
 * Signature: (Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getSwitchIntFromFile
	(JNIEnv *env, jclass className, jstring strAppName, jstring strKeyName, jint nDefault, jstring strFullPathOfFile)
{
	wchar_t *pwszAppName     = JStringToWCHAR(env, strAppName);
	wchar_t *pwszKeyName     = JStringToWCHAR(env, strKeyName);
	wchar_t *pwszFilepath    = JStringToWCHAR(env, strFullPathOfFile);
	int nRet = GetSwitchIntFromIni(pwszAppName, pwszKeyName, nDefault, pwszFilepath);
	SAFE_FREE(pwszAppName);
	SAFE_FREE(pwszKeyName);
	SAFE_FREE(pwszFilepath);
	return nRet;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getSwitchIntFromReg
 * Signature: (Ljava/lang/String;ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getSwitchIntFromReg
	(JNIEnv *env, jclass className, jstring strValueName, jint nDefault, jstring strSubkey)
{
	wchar_t *pwszValueName     = JStringToWCHAR(env, strValueName);
	wchar_t *pwszSubKey       = JStringToWCHAR(env, strSubkey);
	
	DWORD dwValue = nDefault;
	GetSwitchDWORDFromReg( pwszValueName, dwValue, pwszSubKey, nDefault );
	
	SAFE_FREE(pwszValueName);
	SAFE_FREE(pwszSubKey);
	return (jint)dwValue;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getSwitchStringFromFile
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getSwitchStringFromFile
  (JNIEnv *env, jclass className, jstring strAppName, jstring strKeyName, jstring strDefault, jstring strFullPathOfFile)
{
	wchar_t *pwszAppName     = JStringToWCHAR(env, strAppName);
	wchar_t *pwszKeyName     = JStringToWCHAR(env, strKeyName);
	wchar_t *pwszFilepath    = JStringToWCHAR(env, strFullPathOfFile);
	wchar_t *pwszDefault     = JStringToWCHAR(env, strDefault);

	WCHAR szRetValue[1024] = {0}; // assume 1024 is enough
	GetSwitchStringFromIni( pwszAppName, pwszKeyName, szRetValue, _ARRAYSIZE(szRetValue), pwszDefault, pwszFilepath );

	SAFE_FREE(pwszAppName);
	SAFE_FREE(pwszKeyName);
	SAFE_FREE(pwszFilepath);
	SAFE_FREE(pwszDefault);

	jstring retValue = WCHARToJString( env, szRetValue );
	return retValue;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getSwitchStringFromReg
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getSwitchStringFromReg
  (JNIEnv *env, jclass className, jstring strValueName, jstring strDefault, jstring strSubKey)
{
	wchar_t *pwszValueName    = JStringToWCHAR(env, strValueName);
	wchar_t *pwszSubKey       = JStringToWCHAR(env, strSubKey);
	wchar_t *pwszDefault      = JStringToWCHAR(env, strDefault);

	WCHAR szRetValue[1024] = {0}; // assume 1024 is enough
	DWORD dwSize = _ARRAYSIZE(szRetValue);
	GetSwitchStringFromReg( pwszValueName, szRetValue, &dwSize, pwszSubKey, pwszDefault );

	SAFE_FREE(pwszValueName);
	SAFE_FREE(pwszSubKey);
	SAFE_FREE(pwszDefault);

	jstring retValue = WCHARToJString( env, szRetValue );
	return retValue;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getMailAlertFiles
 * Signature: (Ljava/util/ArrayList;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getMailAlertFiles
  (JNIEnv *env, jclass className, jobject fileList)
{
	return Java_com_ca_arcflash_webservice_jni_WSJNI_getMailAlertFilesEx( env, className, TYPE_D2D, fileList );
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getSourceNodeSysInfo
 * Signature: (Ljava/util/ArrayList;)J
 */
JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getSourceNodeSysInfo
  (JNIEnv *env, jclass className, jobject sourceNodeSysInfo)
{
	jclass Class_SourceNodeSysInfo = env->FindClass("com/ca/arcflash/webservice/data/SourceNodeSysInfo");
	jfieldID majorField = env->GetFieldID(Class_SourceNodeSysInfo, "major", "J");
	jfieldID minorField = env->GetFieldID(Class_SourceNodeSysInfo, "minor", "J");
	jfieldID spMajorField = env->GetFieldID(Class_SourceNodeSysInfo, "spMajor", "J");
	jfieldID spMinorField = env->GetFieldID(Class_SourceNodeSysInfo, "spMinor", "J");
	jfieldID x86Field = env->GetFieldID(Class_SourceNodeSysInfo, "isX86", "Z");
	jfieldID UEFIField = env->GetFieldID(Class_SourceNodeSysInfo, "isUEFI", "Z");

	CWinVer winVer;
	CSysInfo sysInfo;

	env->SetLongField(sourceNodeSysInfo, majorField, winVer.GetMajVer());
	env->SetLongField(sourceNodeSysInfo, minorField, winVer.GetMinVer());
	env->SetLongField(sourceNodeSysInfo, spMajorField, winVer.GetSPMajVer());
	env->SetLongField(sourceNodeSysInfo, spMinorField, winVer.GetSPMinVer());
	env->SetBooleanField(sourceNodeSysInfo, x86Field, sysInfo.IsX86());

	CSystemInfo info;
	env->SetBooleanField(sourceNodeSysInfo, UEFIField, info.IsUEFIBoot());

	if (Class_SourceNodeSysInfo != NULL) env->DeleteLocalRef(Class_SourceNodeSysInfo);	
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getMailAlertFilesEx
 * Signature: (ILjava/util/ArrayList;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getMailAlertFilesEx
  (JNIEnv *env, jclass className, jint type, jobject fileList)
{
	std::vector<std::wstring> vecFiles;
	AFGetAlertMailFiles( type, vecFiles );
	jclass listClass = env->GetObjectClass(fileList);
	jmethodID addMethod = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");
	for( size_t i=0; i<vecFiles.size(); i++ )
	{
		jstring fPath = WCHARToJString(env, vecFiles[i].c_str() );
		env->CallBooleanMethod(fileList, addMethod, fPath);
	}
	return 0;
}


/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    browseHyperVHostFolder
 * Signature: (ILjava/lang/String;)Ljava/util/ArrayList;
 */
JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_browseHyperVHostFolder
  (JNIEnv *env, jclass jcls, jlong jHandle, jstring jParentFolder)
{
	IHypervOperation *pHyperV = ((IHypervOperation *)jHandle);
	if(NULL == pHyperV)
	{
		string msg = "Invalid IHypervOperation handle";
		ThrowWSJNIException( env, jcls, env->NewStringUTF(msg.c_str()), ERROR_INVALID_PARAMETER);
		return NULL;
	}

	wchar_t *pwszParentFolder    = JStringToWCHAR(env, jParentFolder);
	
	wstring strParentFolder;
	if (pwszParentFolder != NULL)
	{
		strParentFolder = pwszParentFolder;
	}

	vector<wstring> vChildFolders;
	DWORD dwResult = pHyperV->BrowseFolder(strParentFolder, vChildFolders);
	if( dwResult != 0 )
	{
		ThrowWSJNIException(env, jcls, env->NewStringUTF("Error occurs in IHypervOperation->BrowseFolder"), dwResult);
		return NULL;
	}

	jclass arrayListClass = env->FindClass("java/util/ArrayList");
	jmethodID arrayListConstr = env->GetMethodID(arrayListClass, "<init>", "()V");
	jmethodID listAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
	jobject jRetList = env->NewObject(arrayListClass, arrayListConstr);

	vector<wstring>::iterator itr = vChildFolders.begin();
	while( itr != vChildFolders.end() )
	{
		jstring jstr = WCHARToJString(env, (wchar_t*)itr->c_str());
		env->CallBooleanMethod(jRetList, listAddMethod, jstr);
		if ( jstr!=NULL) env->DeleteLocalRef(jstr);

		itr++;
	}	

	if (arrayListClass!=NULL) env->DeleteLocalRef(arrayListClass);

	if(pwszParentFolder != NULL) 	 free(pwszParentFolder);

	return jRetList;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    createHyperVHostFolder
 * Signature: (ILjava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_createHyperVHostFolder
  (JNIEnv *env, jclass jcls, jlong jHandle, jstring jPath, jstring jFolder)
{
	jboolean result = TRUE;
	IHypervOperation *pHyperV = ((IHypervOperation *)jHandle);
	if(NULL == pHyperV)
	{
		string msg = "Invalid IHypervOperation handle";
		ThrowWSJNIException( env, jcls, env->NewStringUTF(msg.c_str()), ERROR_INVALID_PARAMETER);
		return NULL;
	}

	wchar_t *pwszPath    = JStringToWCHAR(env, jPath);
	wchar_t *pwszFolder    = JStringToWCHAR(env, jFolder);

	wstring strPath;	
	if (pwszPath != NULL)
	{
		strPath = pwszPath;
	}

	wstring strFolder;
	if (pwszFolder != NULL)
	{
		strFolder = pwszFolder;
	}

	DWORD dwResult = pHyperV->CreateFolder(strPath, strFolder);
	if( dwResult != 0 )
	{
		ThrowWSJNIException(env, jcls, env->NewStringUTF("Error occurs in IHypervOperation->CreateFolder"), dwResult);
		return FALSE;
	}	

	if(pwszPath != NULL) 		 free(pwszPath);
	if(pwszFolder != NULL) 	 free(pwszFolder);

	return result;
}


/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getHyperVCPUSocketCount
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHyperVCPUSocketCount
  (JNIEnv *env, jclass jcls, jlong jHandle)
{
	jint result = 0;

	IHypervOperation *pHyperV = ((IHypervOperation *)jHandle);
	if(NULL == pHyperV)
	{
		string msg = "Invalid IHypervOperation handle";
		ThrowWSJNIException( env, jcls, env->NewStringUTF(msg.c_str()), ERROR_INVALID_PARAMETER);
		return NULL;
	}	

	UINT numberOfLogicalProcessors = 0; 
	UINT numberOfProcessors = 0;
	DWORD dwResult = pHyperV->GetNumberOfProcessors(numberOfLogicalProcessors, numberOfProcessors);
	if( dwResult != 0 )
	{
		ThrowWSJNIException(env, jcls, env->NewStringUTF("Error occurs in IHypervOperation->GetNumberOfProcessors"), dwResult);
		return NULL;
	}

	result = numberOfProcessors;

	return result;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getHyperVHostStorage
 * Signature: (I)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHyperVHostStorage
  (JNIEnv *env, jclass jcls, jlong jHandle)
{
	IHypervOperation *pHyperV = ((IHypervOperation *)jHandle);
	if(NULL == pHyperV)
	{
		string msg = "Invalid IHypervOperation handle";
		ThrowWSJNIException( env, jcls, env->NewStringUTF(msg.c_str()), ERROR_INVALID_PARAMETER);
		return NULL;
	}	

	vector<VOLUME_INFO> vVolumeInfoList;
	DWORD dwResult = pHyperV->GetVolumes(vVolumeInfoList);
	if( dwResult != 0 )
	{
		ThrowWSJNIException(env, jcls, env->NewStringUTF("Error occurs in IHypervOperation->GetVolumes"), dwResult);
		return NULL;
	}	

	// convert 
	jclass class_hv= env->FindClass("com/ca/arcflash/webservice/data/vsphere/HyperVHostStorage");
	jmethodID hv_constructor = env->GetMethodID(class_hv, "<init>", "()V");
	jmethodID hv_setDrive = env->GetMethodID(class_hv, "setDrive", "(Ljava/lang/String;)V");
	jmethodID hv_setPath = env->GetMethodID(class_hv, "setPath", "(Ljava/lang/String;)V");
	jmethodID hv_setFreeSize = env->GetMethodID(class_hv, "setFreeSize", "(J)V");
	jmethodID hv_setTotalSize = env->GetMethodID(class_hv, "setTotalSize", "(J)V");

	jclass arrayListClass = env->FindClass("java/util/ArrayList");
	jmethodID arrayListConstr = env->GetMethodID(arrayListClass, "<init>", "()V");
	jmethodID listAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
	jobject jRetList = env->NewObject(arrayListClass, arrayListConstr);

	vector<VOLUME_INFO>::iterator itr = vVolumeInfoList.begin();
	while( itr != vVolumeInfoList.end() )
	{
		jobject jhvInfo = env->NewObject(class_hv, hv_constructor);

		if (L'\0' != ((*itr).strDriveLetter)[0])
		{
			jstring jstr = WCHARToJString(env, (*itr).strDriveLetter);
			env->CallVoidMethod(jhvInfo, hv_setDrive, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);
		}

		if (L'\0' != ((*itr).strDriveName)[0])
		{
			jstring jstr = WCHARToJString(env, (*itr).strDriveName);
			env->CallVoidMethod(jhvInfo, hv_setPath, jstr);
			if ( jstr!=NULL) env->DeleteLocalRef(jstr);
		}

		env->CallVoidMethod(jhvInfo, hv_setFreeSize, (*itr).nFreeSpace);
		env->CallVoidMethod(jhvInfo, hv_setTotalSize, (*itr).nCapacity);

		env->CallBooleanMethod(jRetList, listAddMethod, jhvInfo);

		itr++;
	}

	if (arrayListClass!=NULL) env->DeleteLocalRef(arrayListClass);	

	return jRetList;
}


//<sonmi01>2014-1-23 ###???
namespace {

	static BOOL AFIsNumber( TCHAR ch )
	{
		return (ch >= TEXT('0') && ch <= TEXT('9'));
	}

	static INT AFToNumber( TCHAR ch )
	{
		return (INT)(ch - TEXT('0'));
	}

	static VOID AFExtractNumbers( LPCTSTR pStr, INT Length, vector<INT> & Numbers )
	{
		INT Index = 0;
		while (Index < Length)
		{
			while (Index < Length && !AFIsNumber(pStr[Index]))
			{
				++Index;
			}

			INT Number = 0;
			BOOL bFoundNumber = FALSE;
			while (Index < Length && AFIsNumber(pStr[Index]))
			{
				bFoundNumber = TRUE;
				Number = Number * 10 + AFToNumber(pStr[Index]);
				++Index;
			}

			if (bFoundNumber)
			{
				Numbers.push_back(Number);
			}
		}
	}
}

//<sonmi01>2014-1-22 ###???
// 0 - original hyper-v server version is equal to target ...
//<0 - less
//>0 - greater - print warning

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    CompareHyperVVersion
 * Signature: (ILjava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CompareHyperVVersion
  (JNIEnv *env, jclass klass, jlong handle, jstring sessionUsername, jstring sessionPwd, jstring sessionRootPath, jint sessionNumber)
{
	int nRet = 0;
	DWORD64 dwCookie64 = 0;
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	IHypervPersistInfo* pPersistVMDataInfo = NULL;
	LONG OriginalVersion = 0;
	LONG TargetVersion = 0;

	do{
		//check parameter
		if (NULL == pHyperv)
		{
			nRet = ERROR_INVALID_PARAMETER;
			ThrowWSJNIException(env, klass, env->NewStringUTF("Hyper-V operation handle is null."), nRet);
			break;
		}

		//connect to the backup destination
		wstring wstrsessionRootPath = Utility_JStringToWCHAR(env, sessionRootPath);	
		wstring wstrUsername = Utility_JStringToWCHAR(env, sessionUsername);
		wstring wstrPassword = Utility_JStringToWCHAR(env, sessionPwd);
		NMGR_EXTEND_OPTION stExtend = {0};
		stExtend.size = sizeof(NMGR_EXTEND_OPTION);
		stExtend.isNeedImpnt = TRUE;
		stExtend.Flags = 0;
		nRet = AFMgrDestInit(wstrsessionRootPath.c_str(), wstrUsername.c_str(), wstrPassword.c_str(), dwCookie64, FALSE, &stExtend);
		if (nRet)
		{
			ThrowWSJNIException(env, klass, env->NewStringUTF("cannot AFMgrDestInit."), nRet);
			break;
		}

		//create persist instance
		nRet = AFCreateInstanceHyperVPersistInfo(&pPersistVMDataInfo);
		if (nRet)
		{
			ThrowWSJNIException(env, klass, env->NewStringUTF("cannot AFCreateInstanceHyperVPersistInfo."), nRet);
			break;
		}

		nRet = pPersistVMDataInfo->UnSerialize(wstrsessionRootPath.c_str(), sessionNumber);
		if (nRet)
		{
			ThrowWSJNIException(env, klass, env->NewStringUTF("cannot UnSerialize HyperV persist info."), nRet);
			break;
		}

		wstring OriginalHyperVServerVesion = pPersistVMDataInfo->GetHyperVVersion();
		wstring TargetHyperVServerVesion = pHyperv->GetHyperVServerOsVersion();

		vector<INT> OriginalVersionNumbers;
		vector<INT> TargetVersionNumbers;
		AFExtractNumbers( OriginalHyperVServerVesion.c_str(), OriginalHyperVServerVesion.size(), OriginalVersionNumbers);
		AFExtractNumbers( TargetHyperVServerVesion.c_str(), TargetHyperVServerVesion.size(), TargetVersionNumbers );

		OriginalVersion = MAKELONG((OriginalVersionNumbers.size() >= 2? OriginalVersionNumbers[1] : 0), (OriginalVersionNumbers.size() >= 1? OriginalVersionNumbers[0] : 0));
		TargetVersion = MAKELONG((TargetVersionNumbers.size() >= 2? TargetVersionNumbers[1] : 0), (TargetVersionNumbers.size() >= 1? TargetVersionNumbers[0] : 0));
	}while(FALSE);

	if (pPersistVMDataInfo)
	{
		pPersistVMDataInfo->Release();
		pPersistVMDataInfo = NULL;
	}

	if (dwCookie64)
	{
		(VOID)AFMgrDestUnInit(dwCookie64);
		dwCookie64 = 0;
	}

	return (OriginalVersion - TargetVersion);
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getDateTimeFormat
 * Signature: (Lcom/ca/arcflash/webservice/data/DateFormat;)I
 */
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getDateTimeFormat	
  (JNIEnv *env, jclass jcls, jobject jDateTimeFormat)
{
	int bufLen = 50;
	LPWSTR pstrShortDate = new WCHAR[bufLen];	
	LPWSTR pstrShotTime = new WCHAR[bufLen];
	LPWSTR pstrLongTime = new WCHAR[bufLen];

	wmemset(pstrShortDate, 0, bufLen);	
	wmemset(pstrShotTime, 0, bufLen);
	wmemset(pstrLongTime, 0, bufLen);

	GetShortDatePattern(pstrShortDate, bufLen);		
	GetShortTimePattern(pstrShotTime, bufLen);
	GetLongTimePattern(pstrLongTime, bufLen);

	jclass class_DateFormat = env->FindClass("com/ca/arcflash/webservice/data/DateFormat");
	
	jfieldID field_shortTimeFormat = env->GetFieldID(class_DateFormat, "shortTimeFormat", "Ljava/lang/String;");	
	jfieldID field_timeFormat = env->GetFieldID(class_DateFormat, "timeFormat", "Ljava/lang/String;");
	jfieldID field_dateFormat = env->GetFieldID(class_DateFormat, "dateFormat", "Ljava/lang/String;");	

	jstring jstrShotTime = WCHARToJString(env, pstrShotTime);
	env->SetObjectField(jDateTimeFormat, field_shortTimeFormat, jstrShotTime);

	jstring jstrLongTime= WCHARToJString(env, pstrLongTime);
	env->SetObjectField(jDateTimeFormat, field_timeFormat, jstrLongTime);

	jstring jstrShortDate = WCHARToJString(env, pstrShortDate);
	env->SetObjectField(jDateTimeFormat, field_dateFormat, jstrShortDate);

	delete []pstrShortDate;
	pstrShortDate = NULL;

	delete []pstrShotTime;
	pstrShotTime = NULL;

	delete []pstrLongTime;
	pstrLongTime = NULL;

	return 0;
}

/*
* Class:     Java_com_ca_arcflash_webservice_jni_WSJNI
* Method:    getHyperVServerType
* Signature: (J)V
*/
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHyperVServerType
	(JNIEnv * env, jclass klass, jlong handle)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);

	HYPERV_NODE_TYPE_E enType = pHyperv->GetHyperVServerType();

	return (jint)enType;
}

/*
* Class:     Java_com_ca_arcflash_webservice_jni_WSJNI
* Method:    AFGetHyperVPhysicalName
* Signature: Ljava/lang/String
* Description: Get the hyper-v host of vm in cluster env
*/
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_AFGetHyperVPhysicalName
	(JNIEnv * env, jclass klass, jstring serverName, jstring user, jstring pwd, jstring vmGuid)
{
	wstring wstrServer = Utility_JStringToWCHAR(env, serverName);
	wstring wstrUser = Utility_JStringToWCHAR( env, user );
	wstring wstrPwd = Utility_JStringToWCHAR(env, pwd);
	wstring wstrGuid = Utility_JStringToWCHAR(env, vmGuid);

	IHypervOperation *pHyperv = NULL;
	jstring jvmHost = NULL;
	DWORD dwImp = AFImpersonate();

	do 
	{
		pHyperv = OpenHyperVOperation(wstrServer, wstrUser, wstrPwd);
		if (NULL == pHyperv)
		{
			string msg = "fail to connect hyper-v server";
			ThrowWSJNIException( env, klass, env->NewStringUTF(msg.c_str()), 0);		
			break;
		}

		HYPERV_NODE_TYPE_E enType = pHyperv->GetHyperVServerType();
		/*if (HYPERV_TYPE_STANDALONE == enType)
		{
			jvmHost = WCHARToJString(env, wstrServer);
			break;
		}*/

		if (HYPERV_TYPE_STANDALONE == enType || HYPERV_TYPE_CLUSTER_PHYSICAL_NODE == enType)
		{
			IVirtualMachine *pVm = pHyperv->GetVmByGuid(wstrGuid);
			if( NULL != pVm )
			{
				CAutoPtr<IVirtualMachine> spVm(pVm);
				jvmHost = WCHARToJString(env, wstrServer);
				break;
			}
		}

		HyperVCluster::IClusterOperation * pCluster = pHyperv->GetClusterOperation();
		if (NULL == pCluster)
		{
			ThrowWSJNIException(env, klass, env->NewStringUTF("Error occurs in GetClusterOperation."), ERROR_NOT_FOUND);
			break;
		}

		vector<wstring> vecNodes;
		DWORD dwRet = pCluster->GetActiveClusterNodes(vecNodes);
		if (dwRet)
		{
			ThrowWSJNIException(env, klass, env->NewStringUTF("Error occurs in GetActiveClusterNodes."), dwRet);
			break;
		}

		BOOL bFound = FALSE;
		for (size_t ii = 0; ii < vecNodes.size(); ii++)
		{
			IHypervOperation *pHypervNode = OpenHyperVOperation(vecNodes[ii], wstrUser, wstrPwd);
			if (NULL != pHypervNode)
			{
				IVirtualMachine *pVm = pHypervNode->GetVmByGuid(wstrGuid);
				if( NULL != pVm )
				{
					CAutoPtr<IVirtualMachine> spVm(pVm);
					jvmHost = WCHARToJString(env, vecNodes[ii]);
					bFound = TRUE;
				}
				DelIHyperVOperation(pHypervNode);
				if (bFound) break;
			}
		}

		if (!bFound)
		{
			wstring strGrp;
			BOOL bRet = pCluster->IsVMInCluster(wstrGuid, strGrp);
			if (bRet)
			{
				bFound = TRUE;
				wstring strVMOwner = pCluster->GetVMOwner(strGrp);
				jvmHost = WCHARToJString(env, strVMOwner);
				break;
			}
		}
	} while (FALSE);

	if (jvmHost == NULL)
	{
		ThrowWSJNIException(env, klass, env->NewStringUTF("Cannot find the VM by GUID from the Hyper-V"), -2);
	}

	if (NULL != pHyperv)
	{
		DelIHyperVOperation(pHyperv);
		pHyperv = NULL;
	}

	if (ERROR_SUCCESS == dwImp)
	{
		AFRevertToSelf();
	}

	return jvmHost;
}

/*
* Class:     Java_com_ca_arcflash_webservice_jni_WSJNI
* Method:    getHyperVClusterNodes
* Signature: (J)V
*/
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHyperVClusterNodes
	(JNIEnv * env, jclass klass, jlong handle, jobject jNodeList)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	if(NULL == pHyperv)
	{
		string msg = "Invalid IHypervOperation handle";
		ThrowWSJNIException( env, klass, env->NewStringUTF(msg.c_str()), ERROR_INVALID_PARAMETER);
		return ERROR_INVALID_PARAMETER;
	}

	HyperVCluster::IClusterOperation * pCluster = pHyperv->GetClusterOperation();
	if (NULL == pCluster)
	{
		ThrowWSJNIException(env, klass, env->NewStringUTF("Error occurs in GetClusterOperation."), ERROR_NOT_FOUND);
		return ERROR_NOT_FOUND;
	}

	vector<wstring> vecNodes;
	DWORD dwRet = pCluster->GetActiveClusterNodes(vecNodes);
	if (dwRet)
	{
		ThrowWSJNIException(env, klass, env->NewStringUTF("Error occurs in GetActiveClusterNodes."), dwRet);
		return dwRet;
	}

	return AddVecString2List(env,&jNodeList,vecNodes);
}


/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    getHyperVDefaultFolderOfVHD
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHyperVDefaultFolderOfVHD
 (JNIEnv *env, jclass jcls, jlong jHandle)
{
	IHypervOperation *pHyperV = ((IHypervOperation *)jHandle);
	if(NULL == pHyperV)
	{
		string msg = "Invalid IHypervOperation handle";
		ThrowWSJNIException( env, jcls, env->NewStringUTF(msg.c_str()), ERROR_INVALID_PARAMETER);
		return NULL;
	}	

	wstring strFolder;
	DWORD dwResult = pHyperV->GetDefaultFolderOfVHD(strFolder);
	if( dwResult != 0 )
	{
		ThrowWSJNIException(env, jcls, env->NewStringUTF("Failed to get default folder of VHD"), dwResult);
		return NULL;
	}

	jstring jFolder = WCHARToJString( env, strFolder.c_str());
	return jFolder;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    GetHyperVDefaultFolderOfVM
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetHyperVDefaultFolderOfVM
	(JNIEnv *env, jclass jcls, jlong jHandle)
{
	IHypervOperation *pHyperV = ((IHypervOperation *)jHandle);
	if(NULL == pHyperV)
	{
		string msg = "Invalid IHypervOperation handle";
		ThrowWSJNIException( env, jcls, env->NewStringUTF(msg.c_str()), ERROR_INVALID_PARAMETER);
		return NULL;
	}	

	wstring strFolder;
	DWORD dwResult = pHyperV->GetDefaultFolderOfVM(strFolder);
	if( dwResult != 0 )
	{
		ThrowWSJNIException(env, jcls, env->NewStringUTF("Failed to get default folder of VM"), dwResult);
		return NULL;
	}

	jstring jFolder = WCHARToJString( env, strFolder.c_str());
	return jFolder;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CheckVHDMerging
	(JNIEnv *env, jclass jcls, jstring strRootPath, jint beginSessNumber, jint endSessNumber)
{
	BOOL returnValue = FALSE;

	HMODULE hMountAFStordll = LoadLibrary( L"AFStor.dll" );
	if(hMountAFStordll == NULL)
		return returnValue;

	wchar_t* wszRootPath = JStringToWCHAR(env, strRootPath);
	PFN_CheckVHDMerging pfnCheckVHDMerging = (PFN_CheckVHDMerging)GetProcAddress(hMountAFStordll, "CheckVHDMerging");
	returnValue = (*pfnCheckVHDMerging)(wszRootPath, beginSessNumber, endSessNumber);

	if (hMountAFStordll != NULL)
	{
		FreeLibrary(hMountAFStordll);
		hMountAFStordll = NULL;
	}

	if(wszRootPath != NULL)
		free(wszRootPath);

	return returnValue;
}


//<sonmi01>2014-3-21 ###???
static void split_str( const std::wstring& str, wchar_t chSpliter, std::vector<std::wstring>& vecStrings )
{
	std::wstring strSubStr = str;
	while( strSubStr.length() != 0 )
	{
		std::wstring::size_type pos = strSubStr.find_first_of( chSpliter );
		if( pos != std::wstring::npos )
		{
			std::wstring s = strSubStr.substr( 0, pos );
			strSubStr = strSubStr.substr( pos+1 );				
			if( s.length()>0 )
				vecStrings.push_back( s );
		}
		else
		{
			vecStrings.push_back( strSubStr );
			break;
		}
	}
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getOSVersion(JNIEnv *env, jclass)
{
	CWinVer winVer;
	WCHAR osVersion[10] = { 0 };
	swprintf(osVersion, 10, L"%d.%d", winVer.GetMajVer(), winVer.GetMinVer());

	return WCHARToJString(env, osVersion);
}

static wstring AFBackupGetOSName(void)
{
	typedef void (WINAPI *PGNSI)(LPSYSTEM_INFO);

	PGNSI pGNSI = NULL;
	OSVERSIONINFOEX osvi = {0};
	SYSTEM_INFO si = {0};
	BOOL bOsVersionInfoEx = FALSE;

	wstring wstrOSName = L"Unrecognized OS";//the OS name

	// Try calling GetVersionEx using the OSVERSIONINFOEX structure.
	// If that fails, try using the OSVERSIONINFO structure.

	ZeroMemory(&osvi, sizeof(OSVERSIONINFOEX));
	osvi.dwOSVersionInfoSize = sizeof(OSVERSIONINFOEX);

	if( !(bOsVersionInfoEx = GetVersionEx ((OSVERSIONINFO *) &osvi)) )
	{
		osvi.dwOSVersionInfoSize = sizeof (OSVERSIONINFO);
		if (!GetVersionEx( (OSVERSIONINFO *) &osvi) ) 
			return wstrOSName;
	}

	// Call GetNativeSystemInfo if supported
	// or GetSystemInfo otherwise.
	pGNSI = (PGNSI) GetProcAddress(GetModuleHandleW(L"kernel32.dll"), "GetNativeSystemInfo");
	if(NULL != pGNSI)
		pGNSI(&si);
	else 
		GetSystemInfo(&si);

	//get the real version from register.Because for win 8.1,the version get from GetVersionEX is 6.2
	//But for win 8.1/2012R2, the real version is 6.3.
	wstring strSubKey = L"SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion";
	CRegistry reg;
	DWORD  dwRet = (DWORD)reg.Open( strSubKey.c_str() );
	if(ERROR_SUCCESS == dwRet)
	{
		wstring strVersion = L"";
		dwRet = (DWORD)reg.QueryStringValue( L"CurrentVersion", strVersion );
		reg.Close( );

		if(ERROR_SUCCESS == dwRet)
		{
			std::vector<wstring> eles;
			split_str( strVersion, L'.', eles );
			if( eles.size()>=1 )
				osvi.dwMajorVersion = _wtoi( eles[0].c_str() );
			if( eles.size()>=2 )
				osvi.dwMinorVersion = _wtoi( eles[1].c_str() );
		}
	}


	switch (osvi.dwPlatformId)
	{
		// Test for the Windows NT product family.
	case VER_PLATFORM_WIN32_NT:
		// Test for the specific product.
		if(osvi.dwMajorVersion == 6 && osvi.dwMinorVersion == 3)
		{
			if( osvi.wProductType == VER_NT_WORKSTATION )
				wstrOSName = L"Windows 8.1";
			else
				wstrOSName = L"Windows Server 2012 R2";
		}
		else if(osvi.dwMajorVersion == 6 && osvi.dwMinorVersion == 2)
		{
			if( osvi.wProductType == VER_NT_WORKSTATION )
				wstrOSName = L"Windows 8";
			else
				wstrOSName = L"Windows Server 2012";
		}
		else if ( osvi.dwMajorVersion == 6 && osvi.dwMinorVersion == 1 )
		{
			if( osvi.wProductType == VER_NT_WORKSTATION )
				wstrOSName = L"Windows 7";
			else
				wstrOSName = L"Windows Server 2008 R2";
		}
		else if ( osvi.dwMajorVersion == 6 && osvi.dwMinorVersion == 0 )
		{
			if( osvi.wProductType == VER_NT_WORKSTATION )
				wstrOSName = L"Windows Vista";
			else
				wstrOSName = L"Windows Server Longhorn";
		}
		else if ( osvi.dwMajorVersion == 5 && osvi.dwMinorVersion == 2 )
		{
			if( GetSystemMetrics(SM_SERVERR2) )
				wstrOSName = L"Microsoft Windows Server 2003 R2";
			else if ( osvi.wProductType == VER_NT_WORKSTATION &&
				si.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_AMD64)
			{
				wstrOSName = L"Microsoft Windows XP Professional x64 Edition";
			}
			else 
				wstrOSName = L"Microsoft Windows Server 2003";
		}
		else if ( osvi.dwMajorVersion == 5 && osvi.dwMinorVersion == 1 )
			wstrOSName = L"Microsoft Windows XP";
		else if ( osvi.dwMajorVersion == 5 && osvi.dwMinorVersion == 0 )
			wstrOSName = L"Microsoft Windows 2000";
		else if ( osvi.dwMajorVersion <= 4 )
			wstrOSName = L"Microsoft Windows NT";

		break;
		// Test for the Windows Me/98/95.
	case VER_PLATFORM_WIN32_WINDOWS:

		if (osvi.dwMajorVersion == 4 && osvi.dwMinorVersion == 0)
		{
			wstrOSName = L"Microsoft Windows 95";
			if (osvi.szCSDVersion[1]=='C' || osvi.szCSDVersion[1]=='B')
				wstrOSName += L" OSR2";
		} 
		else if (osvi.dwMajorVersion == 4 && osvi.dwMinorVersion == 10)
		{
			wstrOSName = L"Microsoft Windows 98";
			if ( osvi.szCSDVersion[1] == 'A' )
				wstrOSName += L" SE";
		} 
		else if (osvi.dwMajorVersion == 4 && osvi.dwMinorVersion == 90)
		{
			wstrOSName = L"Microsoft Windows Millennium Edition";
		}

		break;

	case VER_PLATFORM_WIN32s:
		wstrOSName = L"Microsoft Win32s";
		break;
	}

	return wstrOSName;

}


static BOOL Is64bitWindows()
{
	BOOL bRet = TRUE;
	DWORD LastError = 0;
	TCHAR Buffer[1024] = {0};
	UINT dwRet = GetSystemWow64Directory(
		Buffer,//_Out_  LPTSTR lpBuffer,
		_countof(Buffer)//_In_   UINT uSize
		);
	if ( 0 == dwRet)
	{
		LastError = GetLastError();
		if (ERROR_CALL_NOT_IMPLEMENTED == LastError)
		{
			bRet = FALSE;
		}
	}

	return bRet;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_IsOSCompatibleWithProxy
	(JNIEnv *, jclass, jstring, jstring, jstring, jstring)
{
	//for now, only to check local machine
	/*********************************************************************************
	This release was tested with (and supports) the following operating systems to perform proxy backup:
	Windows Server 2003 R2
	Windows Server 2008
	Windows Server 2008 R2
	Windows Server 2012
	Red Hat Enterprise Linux RHEL 5.9, 6.2, 6.3, and 6.4 (as of 17 JAN)
	SUSE Linux Enterprise Server SLES 10.4, 11.1, and 11.2.
	*********************************************************************************/
	static const LPCTSTR PROXY_OS[] = 
	{
		TEXT("Windows Server 2003 R2"),
		TEXT("Windows Server 2008"),
		TEXT("Windows Server 2008 R2"),
		TEXT("Windows Server 2012"),
		TEXT("Windows Server 2012 R2")
	};

	wstring osName = AFBackupGetOSName();

	jboolean bRet = FALSE;

	for (LONG ii = 0; ii < _countof(PROXY_OS); ++ ii)
	{
		if (wstring::npos != osName.find(PROXY_OS[ii]))
		{
			bRet = TRUE;
			break;
		}
	}
	
	return bRet;
}

/*
 * Class:     com_ca_arcflash_webservice_jni_WSJNI
 * Method:    GetAllDnsSuffixes
 * Signature: (Ljava/util/List;)J
 */
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetAllDnsSuffixes
		(JNIEnv *env, jclass jcls, jobject listObj)
{
	logObj.LogW(LL_DBG, 0, L"%s: Entering ...", __WFUNCTION__);

	jlong ret = 0;
	jclass listCls = env->GetObjectClass(listObj);
	jmethodID addMethod = env->GetMethodID(listCls, "add", "(Ljava/lang/Object;)Z");

	std::vector<wstring> dnsSuffixes;
	
	CNICMonitor::GetDnsSuffixes(dnsSuffixes);

	for(std::vector<wstring>::iterator itr = dnsSuffixes.begin(); itr != dnsSuffixes.end(); itr++)	
	{		
		logObj.LogW(LL_DBG, 0, L"\t%s: DNS suffix [%s]", __WFUNCTION__, itr->c_str());
		env->CallBooleanMethod(listObj, addMethod, WCHARToJString(env, *itr));
	}

	logObj.LogW(LL_DBG, 0, L"%s: Leaving ...", __WFUNCTION__);

	return 0;
}

/*
* Class:     com_ca_arcflash_webservice_jni_WSJNI
* Method:    getHyperVProtectionTypes
* Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/ca/arcflash/webservice/jni/model/JRWLong;Lcom/ca/arcflash/webservice/jni/model/JRWLong;)V
*/
DWORD ValidAccountInAllNode(vector<wstring> vecNodes, const wstring& strSeverName, const wstring strUsername, const wstring& strPassword, wstring& strProblemNode)
{
	DWORD dwRet = 0;
	IHypervOperation* pOperation = NULL;
	wstring strNode;

	for (size_t ii = 0; ii < vecNodes.size(); ii++)
	{
		strNode = vecNodes[ii];

		pOperation = OpenHyperVOperation(strNode, strUsername, strPassword);
		if(pOperation == NULL)
		{
			dwRet = GetLastError();
			logObj.LogW(LL_ERR, dwRet, L"\t%s: Failed to open Hyper-V operation on Hyper-V host %s", __WFUNCTION__, strNode.c_str());
			break;
		}

		if(pOperation->GetHyperVServerType() == HYPERV_TYPE_STANDALONE)
		{
			dwRet = -1;
			logObj.LogW(LL_ERR, dwRet, L"\t%s: Wrong server type on Hyper-V host %s", __WFUNCTION__, strNode.c_str());
			break;
		}

		DelIHyperVOperation(pOperation);
		pOperation = NULL;
	}

	if (NULL != pOperation)
	{
		DelIHyperVOperation(pOperation);
		pOperation = NULL;
	}

	if (dwRet != 0)
	{
		strProblemNode = strNode;
	}

	return dwRet;
}

JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHyperVProtectionTypes
(JNIEnv* env, jclass jcls, jstring serverName, jstring username, jstring password, jobject type, jobject errorCode, jobject hypervResult)
{
	enum ENUMProtectionType{PROTECTION_TYPE_UNKNOWN = 0, PROTECTION_TYPE_PRIVATE = 1, PROTECTION_TYPE_CLUSTER = 2};
	enum ENUMErrorCode{ERROR_CODE_SUCCESS = 0, ERROR_CODE_UNKNOWN = -1, ERROR_CODE_CONNECT_TO_SERVER = 1,
		ERROR_CODE_VALID_ACCOUNT_IN_OTHER_NODE, ERROR_CODE_CONNECT_TO_SERVER_CLUSTER, ERROR_CODE_CONNECT_LOCAL_ACCOUNT_FORMAT, ERROR_CODE_CONNECT_TO_SERVER_ACCESS_DENIED, 
		ERROR_CODE_VALID_ACCOUNT_IN_OTHER_NODE_ACCESS_DENIED, ERROR_CODE_SERVICE_NOT_INSTALLED
	};

	ULONG ulProtectionType = PROTECTION_TYPE_UNKNOWN;
	ENUMErrorCode eCode = ERROR_CODE_SUCCESS;
	IHypervOperation* pOperation = NULL;
	do
	{
		wstring strSeverName = JStringToWCHAR(env, serverName);
		wstring strUsername = JStringToWCHAR(env, username);
		wstring strPassword = JStringToWCHAR(env, password);
		pOperation = OpenHyperVOperation(strSeverName, strUsername, strPassword);
		if(pOperation == NULL)
		{
			DWORD dwLastError = GetLastError();
			if(dwLastError == HRESULT_FROM_WIN32(RPC_S_ACCESS_DENIED))
			{
				eCode = ERROR_CODE_CONNECT_TO_SERVER_ACCESS_DENIED;
			}
			else if (dwLastError == HRESULT_FROM_WIN32(WBEM_E_INVALID_NAMESPACE))
			{
				eCode = ERROR_CODE_SERVICE_NOT_INSTALLED;
			}
			else if(dwLastError == HRESULT_FROM_WIN32(RPC_S_SERVER_UNAVAILABLE))
			{
				eCode = ERROR_CODE_CONNECT_TO_SERVER;
			}
			else
			{
				eCode = ERROR_CODE_CONNECT_TO_SERVER;
			}

			logObj.LogW(LL_ERR, dwLastError, L"\t%s: Failed to open Hyper-V operation on host %s", __WFUNCTION__, strSeverName.c_str());
			break;
		}

		//standalone mode
		ulProtectionType = PROTECTION_TYPE_PRIVATE;
		if(pOperation->GetHyperVServerType() == HYPERV_TYPE_STANDALONE)
		{
			logObj.LogW(LL_INF, 0, L"\t%s: server type is private from Hyper-V operation", __WFUNCTION__);

			break;
		}

		//hyper-v is clustered
		if (pOperation->GetHyperVServerType() == HYPERV_TYPE_CLUSTER_VIRTUAL_NODE)
		{
			//user is using cluster name or ip to import VM
			logObj.LogW(LL_INF, 0, L"\t%s: server type is cluster virtual node", __WFUNCTION__);
			ulProtectionType = PROTECTION_TYPE_CLUSTER;
		}

		if(strUsername.find(L"\\") == wstring::npos)
		{
			eCode = ERROR_CODE_CONNECT_LOCAL_ACCOUNT_FORMAT;			
			logObj.LogW(LL_INF, 0, L"\t%s: Non-domain user account format", __WFUNCTION__);

			break;
		}

		HyperVCluster::IClusterOperation* pClusterOperation = pOperation->GetClusterOperation();
		if(pClusterOperation == NULL)
		{
			eCode = ERROR_CODE_CONNECT_TO_SERVER_CLUSTER;
			logObj.LogW(LL_ERR, -1, L"\t%s: Failed to open cluster operation on host %s", __WFUNCTION__, strSeverName.c_str());

			break;
		}

		vector<wstring> vecNodes;
		DWORD dwRet = pClusterOperation->GetActiveClusterNodes(vecNodes);
		if(dwRet != 0)
		{
			eCode = ERROR_CODE_CONNECT_TO_SERVER_CLUSTER;
			logObj.LogW(LL_ERR, dwRet, L"\t%s: Failed to get cluster nodes on Hyper-V host %s", __WFUNCTION__, strSeverName.c_str());

			break;
		}

		wstring strProblemNode;
		DWORD dwError = ValidAccountInAllNode(vecNodes, strSeverName, strUsername, strPassword, strProblemNode);
		if(dwError == 0)
		{
			//no error, protect the hyper-v with cluster mode
			ulProtectionType |= PROTECTION_TYPE_CLUSTER;
		}
		else if(dwError == HRESULT_FROM_WIN32(RPC_S_ACCESS_DENIED))
		{
			eCode = ERROR_CODE_VALID_ACCOUNT_IN_OTHER_NODE_ACCESS_DENIED;
		}
		else
		{
			eCode = ERROR_CODE_VALID_ACCOUNT_IN_OTHER_NODE;
		}

		jstring jstr = WCHARToJString(env, strProblemNode.c_str());

		jclass jHypervResult = env->GetObjectClass(hypervResult);
		jmethodID methodSetErrorHyperv = env->GetMethodID(jHypervResult, "setErrorHyperv", "(Ljava/lang/String;)V");
		env->CallVoidMethod(hypervResult, methodSetErrorHyperv, jstr);
		if (jHypervResult != NULL) env->DeleteLocalRef(jHypervResult);

		if (jstr != NULL) env->DeleteLocalRef(jstr);
	}while(0);

	if(pOperation)
	{
		DelIHyperVOperation(pOperation);
		pOperation = NULL;
	}

	AddUINT2JRWLong(env, ulProtectionType, &type);
	AddUINT2JRWLong(env, (ULONG)eCode, &errorCode);
}

#include "..\Virtualization\HostVMBackup\HyperVBackupInteractLib\NamedPipe.h"
#include "..\Virtualization\HostVMBackup\HyperVBackupInteractLib\ClusterAccessHint.h"
JNIEXPORT void JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_setClusterAccessHint
	(JNIEnv* env, jclass jcls, jstring serverName, jstring username, jstring password)
{
	wstring strServerName = JStringToWCHAR(env, serverName);
	wstring strUsername = JStringToWCHAR(env, username);
	wstring strPassword = JStringToWCHAR(env, password);

	IHypervOperation* pOperation = NULL;
	DWORD64 dwConnCookie = 0;
	BYTE* pHintBuffer = NULL;
	do
	{
		pOperation = OpenHyperVOperation(strServerName, strUsername, strPassword);
		if(pOperation == NULL)
		{
			DWORD dwLastError = GetLastError();
			logObj.LogW(LL_ERR, dwLastError, L"\t%s: Failed to open Hyper-V operation on host %s", __WFUNCTION__, strServerName.c_str());
			break;
		}

		HyperVCluster::IClusterOperation* pClusterOperation = pOperation->GetClusterOperation();
		if(pClusterOperation == NULL)
		{
			logObj.LogW(LL_ERR, -1, L"\t%s: Failed to open cluster operation on host %s", __WFUNCTION__, strServerName.c_str());

			break;
		}

		wstring strOwner = pClusterOperation->GetClusterOnwerNode();
		if(strOwner.empty())
		{
			logObj.LogW(LL_ERR, -1, L"\t%s: Failed to get cluster owner on host %s", __WFUNCTION__, strServerName.c_str());

			break;
		}

		wstring strOwnerShare = L"\\\\" + strOwner;
		NMGR_EXTEND_OPTION stExtend = {0};
		stExtend.size = sizeof(NMGR_EXTEND_OPTION);
		stExtend.isNeedImpnt = FALSE;
		stExtend.Flags = NMR_FLAG_GETUSER_FROM_LEGACY;
		DWORD dwError = AFMgrDestInit(strOwnerShare.c_str(), strUsername.c_str(), strPassword.c_str(), dwConnCookie, FALSE, &stExtend);
		if(dwError)
		{
			logObj.LogW(LL_ERR, dwError, L"\t%s: Failed to connect to owner %s", __WFUNCTION__, strOwner.c_str());

			break;
		}

		DWORD dwHintSize = 0;
		wstring strDomainName;
		wstring strPureUsername;
		wstring::size_type pos = strUsername.find(L"\\");
		if(pos != wstring::npos)
		{
			strDomainName = strUsername.substr(0, pos);
			strPureUsername = strUsername.substr(pos + 1, strUsername.size() - pos - 1);
		}
		else
		{
			strDomainName.empty();
			strPureUsername = strUsername;
		}

		dwError = CClusterAccessHint::ZipHint(strDomainName, strPureUsername, strPassword, &pHintBuffer, dwHintSize);
		if (dwError)
		{
			logObj.LogW(LL_ERR, dwError, L"\t%s: Failed to zip hint", __WFUNCTION__);

			break;
		}

		wstring strPipeName = strOwnerShare + L"\\";
		strPipeName += PLUGIN_REMOTE_AFFAIR_PIPE;
		CNamedPipe pipe(strPipeName.c_str());
		dwError = pipe.Connect();
		if(dwError)
		{
			logObj.LogW(LL_ERR, dwError, L"\t%s: Failed to connect to pipe %s", __WFUNCTION__, strPipeName.c_str());

			break;
		}

		CReportInfo hintReport;
		hintReport.m_nType = REPORT_TYPE_DATA_PLUGIN_ACCESS_HINT;
		hintReport.m_pReportData.reset(new CClusterAccessHintData(dwHintSize, pHintBuffer));
		dwError = pipe.Write(hintReport);
		if(dwError)
		{
			logObj.LogW(LL_ERR, dwError, L"\t%s: Failed to write to pipe %s", __WFUNCTION__, strPipeName.c_str());

			break;
		}
	}while(0);

	if(pHintBuffer)
	{
		delete[] pHintBuffer;
		pHintBuffer = NULL;
	}

	if (dwConnCookie)
	{
		AFMgrDestUnInit(dwConnCookie);
		dwConnCookie = 0;
	}

	if(pOperation)
	{
		DelIHyperVOperation(pOperation);
		pOperation = NULL;
	}
}

/*
* Class:     com_ca_arcflash_webservice_jni_WSJNI
* Method:    getClusterVirtualByPyhsicalNode
* Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;
*/
JNIEXPORT jobjectArray JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getClusterVirtualByPyhsicalNode
	(JNIEnv* env, jclass jcls, jstring serverName, jstring username, jstring password)
{
	wstring strSeverName = JStringToWCHAR(env, serverName);
	wstring strUsername = JStringToWCHAR(env, username);
	wstring strPassword = JStringToWCHAR(env, password);

	IHypervOperation* pOperation = NULL;
	wstring strVirtualName;
	do
	{
		pOperation = OpenHyperVOperation(strSeverName, strUsername, strPassword);
		if(pOperation == NULL)
		{
			logObj.LogW(LL_ERR, -1, L"\t%s: Failed to open Hyper-V operation on host %s", __WFUNCTION__, strSeverName.c_str());
			break;
		}

		HyperVCluster::IClusterOperation* pClusterOperation = pOperation->GetClusterOperation();
		if(pClusterOperation == NULL)
		{
			logObj.LogW(LL_ERR, -1, L"\t%s: Failed to open cluster operation on host %s", __WFUNCTION__, strSeverName.c_str());
			break;
		}

		strVirtualName = pClusterOperation->GetClusterName();
	}while(0);

	if(pOperation)
	{
		DelIHyperVOperation(pOperation);
		pOperation = NULL;
	}

	logObj.LogW(LL_INF, 1, L"\t%s: Cluster virtual name %s for node %s", __WFUNCTION__, strVirtualName.c_str(), strSeverName.c_str());

	jclass jStringCls = env->FindClass("java/lang/String");
	jstring jVirtualName = WCHARToJString(env, strVirtualName.c_str());
	if(jVirtualName)
	{
		jobjectArray jVirtualNameArray = env->NewObjectArray(1, jStringCls, NULL);
		env->SetObjectArrayElement(jVirtualNameArray, 0, (jobject)jVirtualName);

		return jVirtualNameArray;
	}
	else
	{
		return NULL;
	}
}

typedef DWORD(*PFN_StartInstantVM)(LPCWSTR instant_vm_config_path, DWORD startFlag);
typedef DWORD(*PFN_StopInstantVM)(LPCWSTR instant_vm_uuid, DWORD jobID, DWORD jobType);
typedef DWORD(*PFN_QueryInstantVM)(LPCWSTR instant_vm_uuid, IVM_JOB& vm_job);
typedef DWORD(*PFN_CleanShareMemory)(LPCWSTR instant_vm_uuid);
typedef DWORD(*PFN_StartHydration)(LPCWSTR instant_vm_uuid);
typedef DWORD(*PFN_StopHydration)(LPCWSTR instant_vm_uuid);
typedef bool(*PFN_IsAgentExist)(LPCWSTR instant_vm_uuid);

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_startInstantVM
		(JNIEnv *env, jclass jcls, jstring paraXMLPath, jint startFlag)
{
	WCHAR* wcXMLPath = JStringToWCHAR(env, paraXMLPath);
	HMODULE _hModule = NULL;
	DWORD ret = NO_ERROR;
	_hModule = ::LoadLibrary(L"InstantVMJob.dll");
	if (_hModule)
	{
		PFN_StartInstantVM startInstantVMFunc = (PFN_StartInstantVM)GetProcAddress(_hModule, "StartInstantVM");
		if (startInstantVMFunc)
			ret = startInstantVMFunc(wcXMLPath, startFlag);
		else
			logObj.LogW(LL_ERR, 0, L"Failed to get address of function StartInstantVM, error code = %d", GetLastError());
	}
	else
		logObj.LogW(LL_ERR, 0, L"Failed to load InstantVMJob.dll, error code = %d", GetLastError());

	if (ret)
		logObj.LogW(LL_ERR, 0, L"Failed to start instant vm, please see instant vm agent log, error code = %d", ret);

// 	if (_hModule)
// 	{
// 		::FreeLibrary(_hModule);
// 		_hModule = NULL;
// 	}

	return ret;
}

typedef bool(*PFN_PreCheckProxy)(PRE_CHECK_STRUCT& pre_check_struct, DWORD& pre_check_status, DWORD& err_code, std::vector<DWORD>& warning_list, std::wstring& errMsg);
/*
* Class:     com_ca_arcflash_webservice_jni_WSJNI
* Method:    isInstantVMProxyMeetRequirement
* Signature: (I)Ljava/lang/Object;
*/
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_isInstantVMProxyMeetRequirement
(JNIEnv *env, jclass jcls, jobject precheckProxyStruct, jobject jwarningList, jobject jerrMsg)
{
	PRE_CHECK_STRUCT preCheckStruct;
	jclass preCheckInstantVMProxyModelClass = env->FindClass("com/ca/arcflash/webservice/jni/PreCheckInstantVMProxyModel");

	jmethodID getVMConfigPath = env->GetMethodID(preCheckInstantVMProxyModelClass, "getVmConfigPath", "()Ljava/lang/String;");
	jstring jVMConfigPath = (jstring)env->CallObjectMethod(precheckProxyStruct, getVMConfigPath);
	StrCpy(preCheckStruct.vmConfigPath, JStringToWCHAR(env, jVMConfigPath));

	jmethodID getNode_uuid = env->GetMethodID(preCheckInstantVMProxyModelClass, "getNode_uuid", "()Ljava/lang/String;");
	jstring jNodeUUID = (jstring)env->CallObjectMethod(precheckProxyStruct, getNode_uuid);
	StrCpy(preCheckStruct.node_uuid, JStringToWCHAR(env, jNodeUUID));

	jmethodID getJobType = env->GetMethodID(preCheckInstantVMProxyModelClass, "getJob_type", "()J");
	jlong jJobType = env->CallLongMethod(precheckProxyStruct, getJobType);
	preCheckStruct.job_type = (long)jJobType;

	jmethodID getJobID = env->GetMethodID(preCheckInstantVMProxyModelClass, "getJobID", "()J");
	jlong jJobID = env->CallLongMethod(precheckProxyStruct, getJobID);
	preCheckStruct.jobID = (long)jJobID;

	jmethodID getHypervisorType = env->GetMethodID(preCheckInstantVMProxyModelClass, "getHypervisor_type", "()J");
	jlong jHypervisorType = env->CallLongMethod(precheckProxyStruct, getHypervisorType);
	preCheckStruct.hypervisor_type = (DWORD)jHypervisorType;

	jmethodID getCheckMask = env->GetMethodID(preCheckInstantVMProxyModelClass, "getCheckMask", "()I");
	jint checkMask = env->CallLongMethod(precheckProxyStruct, getCheckMask);
	preCheckStruct.check_mask = (DWORD)checkMask;

	jmethodID getIsExitsOnceError = env->GetMethodID(preCheckInstantVMProxyModelClass, "isExitsOnceError", "()Z");
	jboolean isExitsOnceError = env->CallLongMethod(precheckProxyStruct, getIsExitsOnceError);
	preCheckStruct.exits_once_error = (BOOL)isExitsOnceError;

	vector<DWORD> warning_list;
	DWORD proxy_check_status = 0;
	DWORD errCode = 0;
	HMODULE _hModule = ::LoadLibrary(L"InstantVMJob.dll");
	std::wstring errMsg = L"";
	if (_hModule)
	{
		bool preCheckProxyRet = true;
		PFN_PreCheckProxy preCheckProxy = (PFN_PreCheckProxy)GetProcAddress(_hModule, "isProxyMeetRequirement");
		if (preCheckProxy)
			preCheckProxyRet = preCheckProxy(preCheckStruct, proxy_check_status, errCode, warning_list, errMsg);
		else
			logObj.LogW(LL_ERR, GetLastError(), L"Failed to get address of function isProxyMeetRequirement, skip the check, error code = %d", GetLastError());

		::FreeLibrary(_hModule);

		if (!preCheckProxyRet)
		{
			jclass StringBuilder_Class = env->FindClass("java/lang/StringBuilder");
			jmethodID StringBuilder_append_Method = env->GetMethodID(StringBuilder_Class, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
			jstring jString = WCHARToJString(env, errMsg);
			env->CallObjectMethod(jerrMsg, StringBuilder_append_Method, jString);

			jmethodID setErrorBitMask = env->GetMethodID(preCheckInstantVMProxyModelClass, "setErrorMask", "(I)V");
			env->CallLongMethod(precheckProxyStruct, setErrorBitMask, preCheckStruct.error_mask);
			return (jlong)errCode;
		}
	}
	else
	{
		logObj.LogW(LL_ERR, GetLastError(), L"Failed to load library InstantVMJob.dll, error code = %d", GetLastError());
		WCHAR error_message[4096] = { 0 };
		std::wstring module = L"InstantVMJob.dll";

		LPWSTR messageBuffer = NULL;
		size_t size = FormatMessageW(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
			NULL, ::GetLastError(), MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), (LPWSTR)&messageBuffer, 0, NULL);

		std::wstring message = L"";
		if (messageBuffer)
		{
			message = messageBuffer;
			//Free the buffer.
			LocalFree(messageBuffer);
		}
		else
		{
			logObj.LogW(LL_ERR, GetLastError(), L"Failed to get the error message.");
		}


		UDPFormatMessageW(0, error_message, _countof(error_message), INSTANT_VM_ERROR_FAIL_LOAD_BINARY, (WCHAR*)module.c_str(), (WCHAR*)message.c_str());
		errCode = INSTANT_VM_ERROR_FAIL_LOAD_BINARY;
		errMsg = error_message;
		jclass StringBuilder_Class = env->FindClass("java/lang/StringBuilder");
		jmethodID StringBuilder_append_Method = env->GetMethodID(StringBuilder_Class, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
		jstring jString = WCHARToJString(env, errMsg);
		env->CallObjectMethod(jerrMsg, StringBuilder_append_Method, jString);
		return (jlong)errCode;
	}

	return 0;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_stopInstantVM
	(JNIEnv *env, jclass jcls, jstring ivm_job_uuid, jlong jobID, jint jobType, jboolean isDelete)
{
	wchar_t* wcIVM_job_UUID = JStringToWCHAR(env, ivm_job_uuid);

	HMODULE _hModule = NULL;
	DWORD ret = NO_ERROR;
	_hModule = ::LoadLibrary(L"InstantVMJob.dll");
	if (_hModule)
	{
		PFN_StopInstantVM stopInstantVMFunc = (PFN_StopInstantVM)GetProcAddress(_hModule, "StopInstantVM");
		if (stopInstantVMFunc)
			ret = stopInstantVMFunc((LPCWSTR)wcIVM_job_UUID, (DWORD)jobID, (DWORD)jobType);
		else
			logObj.LogW(LL_ERR, 0, L"Failed to get address of function StopInstantVM, error code = %d", GetLastError());
	}
	else
		logObj.LogW(LL_ERR, 0, L"Failed to load InstantVMJob.dll, error code = %d", GetLastError());

	if (ret)
		logObj.LogW(LL_ERR, 0, L"Failed to stop instant vm, please see instant vm agent log, error code = %d", ret);

// 	if (_hModule)
// 	{
// 		::FreeLibrary(_hModule);
// 		_hModule = NULL;
// 	}

	return 0;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_startHydration
(JNIEnv *env, jclass jcls, jstring ivm_job_uuid)
{
	wchar_t* wcIVM_job_UUID = JStringToWCHAR(env, ivm_job_uuid);

	HMODULE _hModule = NULL;
	DWORD ret = NO_ERROR;
	_hModule = ::LoadLibrary(L"InstantVMJob.dll");
	if (_hModule)
	{
		PFN_StartHydration startHydrationFunc = (PFN_StartHydration)GetProcAddress(_hModule, "StartHydration");
		if (startHydrationFunc)
			ret = startHydrationFunc((LPCWSTR)wcIVM_job_UUID);
		else
			logObj.LogW(LL_ERR, 0, L"Failed to get address of function StartHydration, error code = %d", GetLastError());
	}
	else
		logObj.LogW(LL_ERR, 0, L"Failed to load InstantVMJob.dll, error code = %d", GetLastError());

	if (ret)
		logObj.LogW(LL_ERR, 0, L"Failed to start hydration, please see instant vm agent log, error code = %d", ret);
	return 0;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_stopHydration
(JNIEnv *env, jclass jcls, jstring ivm_job_uuid)
{
	wchar_t* wcIVM_job_UUID = JStringToWCHAR(env, ivm_job_uuid);

	HMODULE _hModule = NULL;
	DWORD ret = NO_ERROR;
	_hModule = ::LoadLibrary(L"InstantVMJob.dll");
	if (_hModule)
	{
		PFN_StopHydration stopHydrationFunc = (PFN_StopHydration)GetProcAddress(_hModule, "StopHydration");
		if (stopHydrationFunc)
			ret = stopHydrationFunc((LPCWSTR)wcIVM_job_UUID);
		else
			logObj.LogW(LL_ERR, 0, L"Failed to get address of function StopHydration, error code = %d", GetLastError());
	}
	else
		logObj.LogW(LL_ERR, 0, L"Failed to load InstantVMJob.dll, error code = %d", GetLastError());

	if (ret)
		logObj.LogW(LL_ERR, 0, L"Failed to start hydration, please see instant vm agent log, error code = %d", ret);
	return 0;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_isIVMAgentExist(JNIEnv *env, jclass clz, jstring ivmJobUUID)
{
	wchar_t* wcIVM_job_UUID = JStringToWCHAR(env, ivmJobUUID);

	HMODULE _hModule = NULL;
	bool isExist = false;
	_hModule = ::LoadLibrary(L"InstantVMJob.dll");
	if (_hModule)
	{
		PFN_IsAgentExist isAgentExistFunc = (PFN_IsAgentExist)GetProcAddress(_hModule, "IsAgentExist");
		if (isAgentExistFunc)
			isExist = isAgentExistFunc((LPCWSTR)wcIVM_job_UUID);
		else
			logObj.LogW(LL_ERR, GetLastError(), L"Failed to get address of function IsAgentExist, error code = %d", GetLastError());
	}
	else
		logObj.LogW(LL_ERR, GetLastError(), L"Failed to load InstantVMJob.dll, error code = %d", GetLastError());

	return isExist;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CleanShareMemory(JNIEnv *env, jclass jcls, jstring ivm_job_uuid)
{
	wchar_t* wcIVM_job_UUID = JStringToWCHAR(env, ivm_job_uuid);

	HMODULE _hModule = NULL;
	DWORD ret = NO_ERROR;
	_hModule = ::LoadLibrary(L"InstantVMJob.dll");
	if (_hModule)
	{
		PFN_CleanShareMemory cleanShareMemory = (PFN_CleanShareMemory)GetProcAddress(_hModule, "CleanShareMemory");
		if (cleanShareMemory)
			ret = cleanShareMemory((LPCWSTR)wcIVM_job_UUID);
		else
			logObj.LogW(LL_ERR, 0, L"Failed to get address of function CleanShareMemory, error code = %d", GetLastError());
	}
	else
		logObj.LogW(LL_ERR, 0, L"Failed to load InstantVMJob.dll, error code = %d", GetLastError());

	if (ret)
		logObj.LogW(LL_ERR, 0, L"Failed to clean share memory, please see instant vm agent log, error code = %d", ret);

// 	if (_hModule)
// 	{
// 		::FreeLibrary(_hModule);
// 		_hModule = NULL;
// 	}

	return 0;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getInstantVMJobStatus(JNIEnv * env, jclass klass, jobject jStatus)
{
	jclass jobStatusClass = env->FindClass("com/ca/arcflash/webservice/jni/model/JIVMJobStatus");
	jmethodID getUUID = env->GetMethodID(jobStatusClass, "getIvmJobUUID", "()Ljava/lang/String;");
	jstring jIVMJobUUID = (jstring)env->CallObjectMethod(jStatus, getUUID);
	wchar_t* pIVMJobUUID = JStringToWCHAR(env, jIVMJobUUID);

	HMODULE _hModule = NULL;
	DWORD ret = NO_ERROR;
	_hModule = ::LoadLibrary(L"InstantVMJob.dll");
	IVM_JOB vm_job;
	vm_job.ivm_status = IVM_INIT;
	if (_hModule)
	{
		PFN_QueryInstantVM queryInstantVMFunc = (PFN_QueryInstantVM)GetProcAddress(_hModule, "QueryInstantVM");
		if (queryInstantVMFunc)
			ret = queryInstantVMFunc((LPCWSTR)pIVMJobUUID, vm_job);
		else
			logObj.LogW(LL_ERR, 0, L"Failed to get address of function QueryInstantVM, error code = %d", GetLastError());
	}
	else
		logObj.LogW(LL_ERR, 0, L"Failed to load InstantVMJob.dll, error code = %d", GetLastError());

	if (ret)
	{
		logObj.LogW(LL_ERR, 0, L"Failed to get instant vm status, error code = %d", ret);
		return ret;
	}


// 	if (_hModule)
// 	{
// 		::FreeLibrary(_hModule);
// 		_hModule = NULL;
// 	}

	// Set value to job monitor object
	jmethodID setJobPhase = env->GetMethodID(jobStatusClass, "setJobPhase", "(I)V");
	env->CallObjectMethod(jStatus, setJobPhase, vm_job.ivm_status);

	std::wstring vmDisplayName = vm_job.szInstantVMName;

	SetStringValue2Field(env, jStatus, "vmDisplayName", vmDisplayName);

	if (jobStatusClass != NULL) env->DeleteLocalRef(jobStatusClass);

	return 0;
}

unsigned int GetCrc32(char* InStr, unsigned int len)
{
	unsigned int Crc32Table[256];
	int i, j;
	unsigned int Crc;
	for (i = 0; i < 256; i++){
		Crc = i;
		for (j = 0; j < 8; j++){
			if (Crc & 1)
				Crc = (Crc >> 1) ^ 0xEDB88320;
			else
				Crc >>= 1;
		}
		Crc32Table[i] = Crc;
	}


	Crc = 0xffffffff;
	for (int i = 0; i<len; i++){
		Crc = (Crc >> 8) ^ Crc32Table[(Crc ^ InStr[i]) & 0xFF ];
	}

	Crc ^= 0xFFFFFFFF;
	return Crc;
}

typedef DWORD(*PFN_GETLASTSESSIONNOTOTAPE)(LPCWSTR lpBasePath, LPCWSTR lpUserName, LPCWSTR lpPassword, DWORD dwLastSesNo[], DWORD dwErrorSesNo[], DWORD dwRetryCount[], DWORD dwMaxRetryCount[], DWORD dwSize);

JNIEXPORT jintArray JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getLastArchiveToTapeSession(JNIEnv *env, jclass jcls, jstring backupDest, jstring userName, jstring passWord)
{
	WCHAR* wcBackupDest = JStringToWCHAR(env, backupDest);
	WCHAR* wcUserName = JStringToWCHAR(env, userName);
	WCHAR* wcPassWord = JStringToWCHAR(env, passWord);

	NET_CONN_INFO netConn = { 0 };
	
	DWORD dwLastSessToTape[4] = { -1, -1, -1, -1 };

	HMODULE _hModule = NULL;
	BOOL ret = FALSE;
	_hModule = ::LoadLibrary(L"afstor.dll");

	jintArray lastSesNo = env->NewIntArray(4);
	
	if (_hModule)
	{
		DWORD dwErrorSesNo[4], dwRetryCount[4], dwMaxRetryCount[4];

		PFN_GETLASTSESSIONNOTOTAPE getLastSessionNoToTape = (PFN_GETLASTSESSIONNOTOTAPE)GetProcAddress(_hModule, "GetLastSessionNoToTape");
		if (getLastSessionNoToTape)
			ret = getLastSessionNoToTape(wcBackupDest, wcUserName, wcPassWord, dwLastSessToTape, dwErrorSesNo, dwRetryCount, dwMaxRetryCount,4);
		else
			logObj.LogW(LL_ERR, 0, L"Failed to get address of function GetLastSessionNoToTape, error code = %d", GetLastError());

		::FreeLibrary(_hModule);
		if (ret)
		{
			for (int i = 0; i < 4; i++)
			{
				if (dwLastSessToTape[i] < dwErrorSesNo[i] && dwErrorSesNo[i] > 0)
				{
					if (dwRetryCount[i] >= dwMaxRetryCount[i])
					{
						dwLastSessToTape[i] = dwErrorSesNo[i];
					}
					else
					{
						dwLastSessToTape[i] = dwErrorSesNo[i] - 1;
					}
				}
			}

			WCHAR wzEvent[256], Temp[MAX_PATH];

			ULONG ulLen = wcslen(wcBackupDest);

			for (int i = 0; i < ulLen; i++)
			{
				if (wcBackupDest[i] >= L'A' && wcBackupDest[i] <= L'Z')
				{
					Temp[i] = wcBackupDest[i] - L'A' + L'a';
				}
				else
				{
					Temp[i] = wcBackupDest[i];
				}
			}

			Temp[ulLen] = 0;
			if (Temp[ulLen - 1] == L'\\')
			{
				Temp[ulLen - 1] = 0;
				ulLen--;
			}


			unsigned int CRC = GetCrc32((char*)Temp, ulLen * 2);

			for (int nSch = 0; nSch < 4; nSch++)
			{
				for (int i = dwLastSessToTape[nSch]; i < dwLastSessToTape[nSch] + 64; i++)
				{
					swprintf_s(wzEvent, _countof(wzEvent), L"Global\\%x_%010u", CRC, i);

					HANDLE hEvent = OpenEvent(EVENT_ALL_ACCESS, 0, wzEvent);
					if (hEvent != NULL)
					{
						CloseHandle(hEvent);

						CDbgLog logObj;

						logObj.LogW(LL_ERR, 0, L"The d2d session %d in path %s is backing up by client agent, ignore the trigger by force.", i, wcBackupDest);

						dwLastSessToTape[nSch] = -2;
					}
				}
			}
		}
		else
		{
			logObj.LogW(LL_ERR, 0, L"Failed to get the last session number to tape");
		}
	}
	else
		logObj.LogW(LL_ERR, 0, L"Failed to load afstor.dll, error code = %d", GetLastError());
	
	env->SetIntArrayRegion(lastSesNo, 0, 4, (const jint*)dwLastSessToTape);

	return lastSesNo;	
}

DWORD ValidateRemoteAdminAccountEx(LPCWSTR lpMachine, LPCWSTR lpUserName, LPCWSTR lpPassword, BOOL& bAdminGroupMember)
{
	DWORD			dwRet = ERROR_SUCCESS;

	{
		bAdminGroupMember = FALSE;

		if (!lpMachine || (wcslen(lpMachine) == 0))
			return ERROR_INVALID_PARAMETER;

		if (!lpUserName || (wcslen(lpUserName) == 0))
			return ERROR_INVALID_PARAMETER;

		//For IPv6
		wstring strMachine = lpMachine;
		if (strMachine.find(L':') != wstring::npos)
		{
			wstring::size_type pos = wstring::npos;
			pos = strMachine.find(L"::");
			while (pos != wstring::npos)
			{
				strMachine.replace(pos, 2, L":0:0:0:");
				pos = strMachine.find(L"::");
			}

			pos = strMachine.find(L":");
			while (pos != wstring::npos)
			{
				strMachine.replace(pos, 1, L"-");
				pos = strMachine.find(L":");
			}

			strMachine = strMachine + L".ipv6-literal.net";
		}

		wstring strRemoteName = wstring(L"\\\\") + strMachine + wstring(L"\\ADMIN$");
		wstring strUser = lpUserName;

		if (strUser.find(L"\\") == wstring::npos)
		{
			strUser = strMachine + L"\\" + strUser;
		}

		dwRet = AFIVerifyDestUser(strRemoteName.c_str(), strUser.c_str(), lpPassword);

		if (dwRet == ERROR_SUCCESS)
		{
			bAdminGroupMember = TRUE;
		}
	}

	return dwRet;
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_verifyHyperVAdminAccount
(JNIEnv *env, jclass jclz, jstring machine, jstring userName, jstring password, jboolean isCluster)
{
	logObj.LogW(LL_INF, 0, _TEXT(__FUNCTION__) L"::isCluster = %d", isCluster);

	UINT result = ERROR_SUCCESS;
	IHypervOperation *pHyperv = NULL;

	do
	{
		wstring wstrServer = JStringToWString(env, machine);
		wstring wstrUser = JStringToWString(env, userName);
		wstring wstrPwd = JStringToWString(env, password);

		logObj.LogW(LL_INF, 0, _TEXT(__FUNCTION__) L"::wstrServer = %s, wstrUser = %s", wstrServer.c_str(), wstrUser.c_str());

		if (wstrServer.empty() || wstrUser.empty())
		{
			result = ERROR_INVALID_PARAMETER;
			break;
		}

		// nodes to check
		vector<wstring> vecNodes;

		// check directly
		if (!isCluster)
		{
			vecNodes.push_back(wstrServer);  
		}
		// check as cluster
		else
		{
			pHyperv = OpenHyperVOperation(wstrServer, wstrUser, wstrPwd);
			if (NULL == pHyperv)
			{
				result = ERROR_ACCESS_DENIED;
				logObj.LogW(LL_ERR, 0, _TEXT(__FUNCTION__) L"Failed to OpenHyperVOperation, error code = %d", result);
				break;
			}

			HyperVCluster::IClusterOperation * pCluster = pHyperv->GetClusterOperation();
			if (NULL == pCluster)
			{
				result = ERROR_NOT_FOUND;
				logObj.LogW(LL_ERR, 0, _TEXT(__FUNCTION__) L"Failed to GetClusterOperation, error code = %d", result);
				break;
			}

			DWORD dwRet = pCluster->GetActiveClusterNodes(vecNodes);
			if (dwRet || vecNodes.empty())
			{
				result = ERROR_NOT_FOUND;
				logObj.LogW(LL_ERR, 0, _TEXT(__FUNCTION__) L"Failed to GetActiveClusterNodes, error code = %d", result);
				break;
			}

			logObj.LogW(LL_INF, 0, _TEXT(__FUNCTION__) L"cluster nodes count = %d", vecNodes.size());
		}

		// check the nodes
		for (vector<wstring>::iterator iter = vecNodes.begin(); iter != vecNodes.end(); iter++)
		{
			logObj.LogW(LL_INF, 0, _TEXT(__FUNCTION__) L"cluster node i = %s", iter->c_str());

			BOOL	bAdminGroupMember = FALSE;
			result = ValidateRemoteAdminAccountEx(iter->c_str(), wstrUser.c_str(), wstrPwd.c_str(), bAdminGroupMember);

			if ((result == ERROR_SUCCESS) && (!bAdminGroupMember))
			{
				result = ERROR_MEMBER_NOT_IN_GROUP;
			}

			if (result != ERROR_SUCCESS)
			{
				break;
			}
		}
	} while (false);

	if (pHyperv != NULL)
	{
		DelIHyperVOperation(pHyperv);
	}

	logObj.LogW(LL_INF, 0, _TEXT(__FUNCTION__) L"result = %d", result);
	return result;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHyperVServerOsVersion
(JNIEnv *env, jclass jcls, jlong jHandle)
{
	IHypervOperation *pHyperV = ((IHypervOperation *) jHandle);
	if (NULL == pHyperV)
	{
		string msg = "Invalid IHypervOperation handle";
		ThrowWSJNIException(env, jcls, env->NewStringUTF(msg.c_str()), ERROR_INVALID_PARAMETER);
		return NULL;
	}

	wstring version = pHyperV->GetHyperVServerOsVersion();

	jstring result = WCHARToJString(env, version.c_str());
	return result;
}

/*
* Class:     com_ca_arcflash_webservice_jni_WSJNI
* Method:    getHypervVMInfo
* Signature: (JLjava/lang/String;)Lcom/ca/arcflash/webservice/jni/model/JHypervVMInfo;
*/
JNIEXPORT jobject JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHypervVMInfo
(JNIEnv *env, jclass jcls, jlong jHandle, jstring vmGuid)
{
	jobject result = NULL;

	do
	{
		IHypervOperation *pHyperv = ((IHypervOperation *) jHandle);
		IVirtualMachine *pVm = NULL;
		wstring wstrGuid = JStringToWString(env, vmGuid);

		pVm = pHyperv->GetVmByGuid(wstrGuid);
		if (NULL == pVm)
		{
			ThrowWSJNIException(env, jcls, env->NewStringUTF("Cannot get VM info"), -1);
			break;
		}

		jclass arrayListClass = env->FindClass("java/util/ArrayList");
		jmethodID arrayListConstr = env->GetMethodID(arrayListClass, "<init>", "()V");
		jmethodID listAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");

		// host ic version
		wstring strIntegrationServicesGuestInstallerVersion;
		pHyperv->GetIntegrationServicesGuestInstallerVersion(strIntegrationServicesGuestInstallerVersion);

		jclass class_hv = env->FindClass("com/ca/arcflash/webservice/jni/model/JHypervVMInfo");
		jmethodID hv_constructor = env->GetMethodID(class_hv, "<init>", "()V");
		jmethodID hv_setVmName = env->GetMethodID(class_hv, "setVmName", "(Ljava/lang/String;)V");
		jmethodID hv_setVmUUID = env->GetMethodID(class_hv, "setVmUuid", "(Ljava/lang/String;)V");
		jmethodID hv_setVmHostName = env->GetMethodID(class_hv, "setVmHostName", "(Ljava/lang/String;)V");
		jmethodID hv_setVmGuestOS = env->GetMethodID(class_hv, "setVmGuestOS", "(Ljava/lang/String;)V");
		jmethodID hv_setVmPowerStatus = env->GetMethodID(class_hv, "setVmPowerStatus", "(I)V");
		jmethodID hv_setVmInteServiceSatus = env->GetMethodID(class_hv, "setVmInteServiceSatus", "(I)V");
		jmethodID hv_setIpList = env->GetMethodID(class_hv, "setIpList", "(Ljava/util/List;)V");

		jmethodID hv_setVmCpuNum = env->GetMethodID(class_hv, "setVmCpuNum", "(I)V");
		jmethodID hv_setVmMemoryMB = env->GetMethodID(class_hv, "setVmMemoryMB", "(I)V");

		jobject jhvInfo = env->NewObject(class_hv, hv_constructor);
		env->CallVoidMethod(jhvInfo, hv_setVmName, WCHARToJString(env, pVm->GetName().c_str()));
		env->CallVoidMethod(jhvInfo, hv_setVmUUID, WCHARToJString(env, pVm->GetGuid().c_str()));
		env->CallVoidMethod(jhvInfo, hv_setVmHostName, WCHARToJString(env, pVm->GetHostName().c_str()));
		env->CallVoidMethod(jhvInfo, hv_setVmGuestOS, WCHARToJString(env, pVm->GetOSName().c_str()));

		env->CallVoidMethod(jhvInfo, hv_setVmPowerStatus, pVm->GetState());
		env->CallVoidMethod(jhvInfo, hv_setVmInteServiceSatus, pVm->GetIntegrationServicesStatus(strIntegrationServicesGuestInstallerVersion));
		int vmMemory = 0;
		pVm->GetMemory(vmMemory);
		env->CallVoidMethod(jhvInfo, hv_setVmMemoryMB, vmMemory);

		int vmCpuNum = 0;
		pVm->GetCpuNum(vmCpuNum);
		env->CallVoidMethod(jhvInfo, hv_setVmCpuNum, vmCpuNum);

		// VM IP address list
		vector<wstring> vIP;
		pVm->GetIPAddress(vIP);
		jobject jIPAddressList = env->NewObject(arrayListClass, arrayListConstr);
		AddVWSTRING_ITEM2List(env, jIPAddressList, vIP);
		env->CallVoidMethod(jhvInfo, hv_setIpList, jIPAddressList);

		result = jhvInfo;

		if (arrayListClass != NULL) env->DeleteLocalRef(arrayListClass);
		if (class_hv != NULL) env->DeleteLocalRef(class_hv);
		if (jIPAddressList != NULL) env->DeleteLocalRef(jIPAddressList);

		DelIVirtualMachine(pVm);

	} while (false);
	

	return result;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_EncryptDNSPassword
(JNIEnv *env, jclass clz, jstring plainText)
{
	typedef long(*PFN_EncryptDNSPassword)(const std::wstring& password, std::wstring& encryptedPassword);
	HMODULE h = ::LoadLibrary(L"InstantVmComm.dll");
	
	if (h) 
	{
		wstring encryptedPassword = L"";
		PFN_EncryptDNSPassword pfn_encryptDNSPassword = (PFN_EncryptDNSPassword)(::GetProcAddress(h, "EncryptDNSPassword"));
		DWORD errCode = GetLastError();
		if (pfn_encryptDNSPassword)
		{
			errCode = pfn_encryptDNSPassword(JStringToWCHAR(env, plainText), encryptedPassword);
		}

		FreeLibrary(h);

		if (errCode)
			return NULL;
		else
			return WCHARToJString(env, encryptedPassword);
	}

	return NULL;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_DecryptDNSPassword
(JNIEnv *env, jclass clz, jstring plainText)
{
	typedef long(*PFN_DecryptDNSPassword)(const std::wstring& password, std::wstring& decryptedPassword);
	HMODULE h = ::LoadLibrary(L"InstantVmComm.dll");

	if (h)
	{
		wstring decryptedPassword = L"";
		PFN_DecryptDNSPassword pfn_decryptDNSPassword = (PFN_DecryptDNSPassword)(::GetProcAddress(h, "DecryptDNSPassword"));
		DWORD errCode = GetLastError();
		if (pfn_decryptDNSPassword)
		{
			errCode = pfn_decryptDNSPassword(JStringToWCHAR(env, plainText), decryptedPassword);
		}

		FreeLibrary(h);

		if (errCode)
			return NULL;
		else
			return WCHARToJString(env, decryptedPassword);
	}

	return NULL;
}

/*
* Class:     com_ca_arcflash_webservice_jni_WSJNI
* Method:    cancelGroupJob
* Signature: (JJLjava/util/List;)I
*/
JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_cancelGroupJob
(JNIEnv *env, jclass clz, jlong jobType, jlong jobID, jobject finishedChildJobIDs)
{
	jint result = -1;

	vector<DWORD> childJobIDs; // vector of child job IDs
	if (finishedChildJobIDs != NULL)
	{
		jclass jListClass = env->GetObjectClass(finishedChildJobIDs);
		jmethodID methodSize = env->GetMethodID(jListClass, "size", "()I");
		jmethodID methodGet = env->GetMethodID(jListClass, "get", "(I)Ljava/lang/Object;");
		jint sizeOfList = env->CallIntMethod(finishedChildJobIDs, methodSize);

		//populate vector
		jclass jLongClass = env->FindClass("java/lang/Long");
		for (jint i = 0; i < sizeOfList; i++)
		{
			jobject jLongObj = env->CallObjectMethod(finishedChildJobIDs, methodGet, i);
			jfieldID fieldID = env->GetFieldID(jLongClass, "value", "J");
			ULONG64 fieldValue = (ULONG64) env->GetLongField(jLongObj, fieldID);
			childJobIDs.push_back((DWORD)fieldValue);
		}
	}
	
	result = AFCancelJobEx((ULONG)jobType, (DWORD)jobID, childJobIDs);

	return result;
}


JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getDataSizesFromStorage
	(JNIEnv *env, jclass clz, jobject jRetArrLst, jstring strSrc, jstring usrName, jstring usrPwd)
{
	long lRetCode = 0;
	wchar_t* pszSource = JStringToWCHAR(env, strSrc);
	wchar_t* pszUsrPwd = JStringToWCHAR(env, usrPwd);
	wchar_t* pszUsrName = JStringToWCHAR(env, usrName);

	VBACKUP_NODES_SIZE vecBkSize;
	NET_CONN_INFO netConn = CreateNetConnInfo(pszSource, pszUsrName, pszUsrPwd);

	lRetCode = AFGetDataSizeFromShareFolder(netConn, vecBkSize);
	if (lRetCode)
	{
		logObj.LogW(LL_ERR, lRetCode, L"Get data size from share folder [%s] failed. EC:%#08x\n", netConn.szDir, lRetCode);
		lRetCode = -1;	// Error code not specified at now, 
	}
	else
	{
		VectorNodeSize2JArrayList(env, jRetArrLst, vecBkSize);
	}

	SAFE_FREE(pszSource);
	SAFE_FREE(pszUsrPwd);
	SAFE_FREE(pszUsrName);

	return lRetCode;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_isHyperVVmExist(JNIEnv *env, jclass clz, jstring guid, jlong handle)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, guid);

	return pHyperv->GetVmByGuid(wstrVmGuid) != NULL;
}



JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetLastArchiveCatalogUpdateTime2(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jobject jDestInfo, jobject out_CatalogDetails)
{
	return FileCopyCommonJNI::GetLastArchiveCatalogUpdateTime2(env, clz, catalogDirBasePath, jDestInfo, out_CatalogDetails);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_searchArchiveCatalogChildren2(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jstring inFileName, jstring inHostName, jstring inSearchpath, jobject inArchiveDestConfig,
	jlong in_lSearchOptions, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems)
{
	return FileCopyCommonJNI::searchArchiveCatalogChildren2(env, clz, catalogDirBasePath, inFileName, inHostName, inSearchpath, inArchiveDestConfig, in_lSearchOptions, in_lIndex, in_lCount, archiveCatalogItems);
}

//virtual DWORD OpenMachine(wstring& catalogDirBasePath, PTCHAR szHostName, wstring& destGUID, HANDLE& machineCatalogHandle) = 0;
//virtual DWORD GetVolumeList(HANDLE machineCatalogHandle, std::vector<PTCHAR> &VolumeList) = 0;
//virtual DWORD GetChildItemCount(HANDLE machineCatalogHandle, PTCHAR szPath, DWORD& count) = 0;
//virtual DWORD GetChildItems(HANDLE machineCatalogHandle, PTCHAR szPath, vector <pCatalogChildItem> &versions) = 0;
//virtual DWORD GetChildItemsEx(HANDLE machineCatalogHandle, PTCHAR szPath, vector <pCatalogChildItem> &versions, DWORD nStart, DWORD nReqItemCount) = 0;
//virtual void  CloseHandle(HANDLE machineCatalogHandle) = 0;

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_ArchiveOpenMachine(JNIEnv *env, jclass clz, jstring catalogDirBasePath, jstring hostName, jobject jDestInfo, jobject pMachineHandle)
{
	return FileCopyCommonJNI::ArchiveOpenMachine(env, clz, catalogDirBasePath, NULL, NULL, hostName, jDestInfo, pMachineHandle);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetArchiveVolumeList(JNIEnv *env, jclass clz, jlong pMachineHandle, jobject volumeList)
{
	return FileCopyCommonJNI::GetArchiveVolumeList(env, clz, pMachineHandle, volumeList);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_WSJNI_ArchiveOpenVolume(JNIEnv *env, jclass clz, jlong pMachineHandle, jstring strVolume)
{
	return FileCopyCommonJNI::ArchiveOpenVolume(env, clz, pMachineHandle, strVolume);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetArchiveChildItemCount(JNIEnv *env, jclass clz, jlong pMachineHandle, jstring strPath, jobject childCount)
{
	return FileCopyCommonJNI::GetArchiveChildItemCount(env, clz, pMachineHandle, strPath, childCount);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetArchiveChildItems(JNIEnv *env, jclass clz, jlong pMachineHandle, jstring strPath, jobject archiveCatalogItems)
{
	return FileCopyCommonJNI::GetArchiveChildItems(env, clz, pMachineHandle, strPath, archiveCatalogItems);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetArchiveChildItemsEx(JNIEnv *env, jclass clz, jlong pMachineHandle, jstring strPath, jlong in_lIndex, jlong in_lCount, jobject archiveCatalogItems)
{
	return FileCopyCommonJNI::GetArchiveChildItemsEx(env, clz, pMachineHandle, strPath, in_lIndex, in_lCount, archiveCatalogItems);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_WSJNI_ArchiveCloseVolume(JNIEnv *env, jclass clz, jlong pVolumeHandle)
{
	return FileCopyCommonJNI::ArchiveCloseVolume(env, clz, pVolumeHandle);
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_ArchiveCloseMachine(JNIEnv *env, jclass clz, jlong pMachineHandle)
{
	return FileCopyCommonJNI::ArchiveCloseMachine(env, clz, pMachineHandle);
}

JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getVolumeSize
(JNIEnv *env, jclass clz, jstring directoryName, jobject sizes)
{
	CDbgLog logObj(L"NativeFacade");
	wstring wszDirectoryName = JStringToWString(env, directoryName);
	wchar_t wszVolPath[MAX_PATH] = { 0 };
	if (!::GetVolumePathNameW(wszDirectoryName.c_str(), wszVolPath, MAX_PATH))
	{
		DWORD err = GetLastError();
		logObj.LogW(LL_ERR, err, L"Fail to call GetVolumePathNameW for directory %s.", wszDirectoryName.c_str());
		return err;
	}
	ULARGE_INTEGER freeBytesAvailable = { 0 };
	ULARGE_INTEGER totalNumberOfBytes = { 0 };
	ULARGE_INTEGER totalNumberOfFreeBytes = { 0 };
	if (!GetDiskFreeSpaceEx(wszVolPath, &freeBytesAvailable, &totalNumberOfBytes, &totalNumberOfFreeBytes))
	{
		DWORD err = GetLastError();
		logObj.LogW(LL_ERR, err, L"Fail to call GetDiskFreeSpaceEx for directory %s.", wszVolPath);
		return err;
	}
	AddLongToList(env, clz, (jlong)freeBytesAvailable.QuadPart, &sizes);
	AddLongToList(env, clz, (jlong)totalNumberOfBytes.QuadPart, &sizes);
	return NO_ERROR;
}
/* END */


JNIEXPORT jint JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_checkServiceState
(JNIEnv *env, jclass clz, jstring hostname, jstring serviceName, jstring userName, jstring password, jobject service)
{
	CDbgLog logObj(L"NativeFacade");
	INodeOperation * pNode = NULL;
	DWORD dwRet = 0;

	do 
	{
		wstring strHostName = JStringToWString(env, hostname);
		wstring strServiceName = JStringToWString(env, serviceName);
		wstring strUserName = JStringToWString(env, userName);
		wstring strPassword = JStringToWString(env, password);

		pNode = OpenNodeOperation(strHostName, strUserName, strPassword);
		if (NULL == pNode)
		{
			dwRet = ::GetLastError();
			logObj.LogW(LL_ERR, dwRet, L"Java_com_ca_arcflash_webservice_jni_WSJNI_checkServiceState::Failed to connect to %s with error %u.", strHostName.c_str(), dwRet);
			break;
		}

		WIN32_SERVICE_INFO_S ServiceInfo;
		ZeroMemory(&ServiceInfo, sizeof(WIN32_SERVICE_INFO_S));
		dwRet = pNode->GetWindowsServiceInfo(strServiceName, ServiceInfo);
		if (dwRet)
		{
			logObj.LogW(LL_ERR, dwRet, L"Java_com_ca_arcflash_webservice_jni_WSJNI_checkServiceState::Failed to get Service %s.", strServiceName.c_str());
			break;
		}

		jclass class_hv = env->GetObjectClass(service);
		jmethodID hv_setExist = env->GetMethodID(class_hv, "setExist", "(Z)V");
		jmethodID hv_setStarted = env->GetMethodID(class_hv, "setStarted", "(Z)V");
		jmethodID hv_setProcessId = env->GetMethodID(class_hv, "setProcessId", "(J)V");
		jmethodID hv_setStartMode = env->GetMethodID(class_hv, "setStartMode", "(Ljava/lang/String;)V");
		jmethodID hv_setState = env->GetMethodID(class_hv, "setState", "(Ljava/lang/String;)V");

		if (L'\0' != (ServiceInfo.szStartMode[0]))
		{
			jstring jstr = WCHARToJString(env, ServiceInfo.szStartMode);
			env->CallVoidMethod(service, hv_setStartMode, jstr);
			if (jstr != NULL) env->DeleteLocalRef(jstr);
		}

		if (L'\0' != (ServiceInfo.szState[0]))
		{
			jstring jstr = WCHARToJString(env, ServiceInfo.szState);
			env->CallVoidMethod(service, hv_setState, jstr);
			if (jstr != NULL) env->DeleteLocalRef(jstr);
		}

		env->CallVoidMethod(service, hv_setExist, true);
		env->CallVoidMethod(service, hv_setStarted, (ServiceInfo.bStarted ? true : false));
		env->CallVoidMethod(service, hv_setProcessId, ServiceInfo.ulProcessId);

	} while (FALSE);

	if (NULL != pNode)
	{
		DelNodeOperation(pNode);
		pNode = NULL;
	}

	return (jint)dwRet;
}

JNIEXPORT jboolean JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_CheckASBUAgent(JNIEnv *env, jclass clz)
{
	BOOL bInstallAgent = FALSE;
	int nPort = 6050;

	WSADATA wsdat;
	memset(&wsdat, 0, sizeof(wsdat));

	if (WSAStartup(MAKEWORD(2, 2), &wsdat))
		return FALSE;

	struct sockaddr_in sock_info;

	memset(&sock_info, 0, sizeof(sock_info));

	SOCKET s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (s == INVALID_SOCKET)
		return FALSE;

	sock_info.sin_family = AF_INET;
	sock_info.sin_addr.s_addr = inet_addr("127.0.0.1");
	sock_info.sin_port = htons(nPort);

	int ret = connect(s, (struct sockaddr*)&sock_info, sizeof(struct sockaddr));
	if (ret == SOCKET_ERROR)
	{
		bInstallAgent = FALSE;
	}
	else
	{
		bInstallAgent = TRUE;
	}

	closesocket(s);

	WSACleanup();

	if (bInstallAgent)
	{
		DWORD dwType;
		WCHAR wzValue[128] = L"";
		DWORD cb = _countof(wzValue);

		ret = RegGetValue(HKEY_LOCAL_MACHINE,
			L"SOFTWARE\\ComputerAssociates\\CA ARCServe Backup\\UniversalClientAgent\\Common\\",
			L"Version",
			RRF_RT_REG_SZ,
			&dwType,
			&wzValue,
			&cb
			);

		if (ret == ERROR_SUCCESS)
		{
			if (_wtof(wzValue) < 17.0)
			{
				bInstallAgent = FALSE;

				logObj.LogW(LL_ERR, 0, L"%s: client agent version[%s] is not match.\n", __FUNCTIONW__, wzValue);
			}
		}
	}

	return bInstallAgent;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_VDDismountResBrsVols(JNIEnv *, jclass, jboolean bForceDisMntAllResBrw)
{
	CDbgLog logObj(L"NativeFacade");
	logObj.LogW(LL_DBG, 0, L"call in Java_com_ca_arcflash_webservice_jni_WSJNI_VDDismountResBrsVols");

	HMODULE hDllModule = LoadLibrary(L"GRTMntBrowser.dll");
	if (NULL == hDllModule)
	{
		logObj.LogW(LL_ERR, GetLastError(), L"LoadLibrary:GRTMntBrowser.dll failed");
		return -1;
	}

	typedef DWORD(*FunPtr_UnMountRestoreBrowseVolumes)(BOOL);

	DWORD dwRet = 0;
	FunPtr_UnMountRestoreBrowseVolumes pFun = (FunPtr_UnMountRestoreBrowseVolumes)GetProcAddress(hDllModule, "UnMountRestoreBrowseVolumes");
	if (NULL == pFun)
	{
		dwRet = GetLastError();
		logObj.LogW(LL_ERR, dwRet, L"GetProcAddress:%s failed", L"UnMountRestoreBrowseVolumes");
	}
	else
	{
		dwRet = pFun((BOOL)bForceDisMntAllResBrw);
	}

	if (hDllModule)
	{
		FreeLibrary(hDllModule);
		hDllModule = NULL;
	}

	return dwRet;
}

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_VDUpateVolumeMountTimestamp(JNIEnv *env, jclass, jstring volumeID)
{
	DWORD dwRet = 0;
	CDbgLog logObj(L"NativeFacade");
	logObj.LogW(LL_DBG, 0, L"call in Java_com_ca_arcflash_webservice_jni_WSJNI_VDUpateVolumeExtData");

	wchar_t* pVolumeID = JStringToWCHAR(env, volumeID);
	if (NULL == pVolumeID)
	{
		logObj.LogW(LL_ERR, 111, L"JStringToWCHAR failed");
		return -1;
	}

	HMODULE hDllModule = LoadLibrary(L"GRTMntBrowser.dll");
	if (NULL == hDllModule)
	{
		logObj.LogW(LL_ERR, GetLastError(), L"LoadLibrary:GRTMntBrowser.dll failed");
		return -1;
	}

	typedef DWORD(*FunPtr_UpdateVolumeAccessTime)(LPWSTR);

	FunPtr_UpdateVolumeAccessTime pFunUpdateVolumeAccessTime = (FunPtr_UpdateVolumeAccessTime)GetProcAddress(hDllModule, "UpdateVolumeAccessTime");
	if (NULL == pFunUpdateVolumeAccessTime)
	{
		dwRet = GetLastError();
		logObj.LogW(LL_ERR, dwRet, L"GetProcAddress:%s failed", L"UpdateVolumeAccessTime");
	}
	else
	{
		dwRet = pFunUpdateVolumeAccessTime(pVolumeID);
	}

	if (hDllModule)
	{
		FreeLibrary(hDllModule);
		hDllModule = NULL;
	}

	return dwRet;
}

JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_GetDisplayLanguage(JNIEnv *env, jclass clz)
{
	CDbgLog logObj(L"NativeFacade");

	wstring strUILanguage = GetUILanguage(L"");
	logObj.LogW(LL_INF, 0, L"The display launguage being used is: %s", strUILanguage.c_str());

	jstring jstrUILanguage = WCHARToJString(env, (wchar_t *)strUILanguage.c_str());
	return jstrUILanguage;
}



JNIEXPORT jstring JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getHostFQDN(JNIEnv *env, jclass clz)
{
	DWORD dwRet = 0;
	wstring strHostFQDN;

	dwRet = AFGetHostFQDN(strHostFQDN);
	if (dwRet == 0 && strHostFQDN.length() > 0)	{
		return WCHARToJString(env, (wchar_t *)strHostFQDN.c_str());
	}

	return WCHARToJString(env, L"");
}
