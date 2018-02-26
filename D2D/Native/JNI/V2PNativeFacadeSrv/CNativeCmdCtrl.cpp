// CNativeCmdCtrl.cpp : Implementation of CCNativeCmdCtrl

#include "stdafx.h"
#include <atlstr.h>
#include "V2PSrvDebugLog.h"
#include "ApiNameDef.h"
#include "D2DSwitch.h"
#include "SwitchDefines.h"
#include "CNativeCmdCtrl.h"

#if defined(_WIN64)
#define JRE_PATH L"TOMCAT\\JRE\\bin\\server"
#else
#define JRE_PATH L"TOMCAT\\JRE\\bin\\client"
#endif

DWORD AppendToEnvPath(TCHAR *appendPath)
{
	DWORD  dwPathBufSize = 0;
	size_t  appPathLen = 0;
	TCHAR  *pPathBuf = NULL;
	DWORD	pathBufSize = 0;
	DWORD  dwErrCode = 0;

	appPathLen = _tcslen(appendPath);

	dwPathBufSize = GetEnvironmentVariable(_T("Path"), NULL, 0);

	if (dwPathBufSize)
	{

		pathBufSize = (DWORD)(dwPathBufSize + appPathLen + 4);
		pPathBuf = (TCHAR *)malloc(pathBufSize * sizeof(TCHAR));
		if (!pPathBuf)
		{
			return 0xffffffff;
		}
		memset(pPathBuf, 0, pathBufSize * sizeof(TCHAR));

		_tcscat_s(pPathBuf, pathBufSize, appendPath);
		pPathBuf[appPathLen] = _T(';');

		GetEnvironmentVariable(_T("PATH"),
			pPathBuf + appPathLen + 1,
			dwPathBufSize + 1);

		if (!SetEnvironmentVariable(_T("Path"), pPathBuf))
		{
			dwErrCode = GetLastError();
		}

		if (pPathBuf)
			free(pPathBuf);
	}
	else
	{
		dwErrCode = GetLastError();
		return dwErrCode;
	}

	return dwErrCode;
}

static INT DebugGetJreDirectory(OUT CString & strJreDirectory)
{
	WCHAR JreDir[MAX_PATH] = { 0 };
	ULONG ulBytes = _countof(JreDir);
	LONG lRet = GetSwitchStringFromReg(SWT_AFBACKEND_KEYNAME_JREDIRECTORY, JreDir, &ulBytes);
	if (ERROR_SUCCESS == lRet)
	{
		strJreDirectory = JreDir;
	}
	return lRet;
}

INT D2D_AddJrePath()
{
	TCHAR modulePath[512] = { 0 };
	int length = GetModuleFileName(NULL, modulePath, 512);
	if (length == 0)
	{
		return -1;
	}
	TCHAR* lastSlash = wcsrchr(modulePath, L'\\');
	if (lastSlash != NULL)
	{
		*lastSlash = 0;
		lastSlash = wcsrchr(modulePath, L'\\');
	}

	if (lastSlash != NULL)
	{
		CString strJreDirectory;
		DebugGetJreDirectory(strJreDirectory);

		if (strJreDirectory.GetLength() > 3)
		{
			return AppendToEnvPath(strJreDirectory.GetBuffer());
		}
		else
		{
			wcscpy_s(lastSlash + 1, 510 - (wcslen(modulePath) - wcslen(lastSlash)), JRE_PATH);
			return AppendToEnvPath(modulePath);
		}
	}
	return -1;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
static LPCWSTR V2PNATIVEFACADE_LIB_NAME = L"V2PNativeFacade.dll";
ApiNameDef(V2PNativeFacadeRPC_Proc);
ApiNameDef(V2PNativeFacadeInit);
ApiNameDef(V2PNativeFacadeInit_Ex);
ApiNameDef(V2PNativeFacadeExit);
ApiNameDef(getJVM);
ApiNameDef(readFileStream);
ApiNameDef(V2PNFSet_isSharedJVM); //<sonmi01>2015-9-6 ###???

void* CCNativeCmdCtrl::m_pJavaVM = NULL;
CCNativeCmdCtrl::pfn_V2PNativeFacadeInit CCNativeCmdCtrl::mfn_V2PNativeFacadeInit = NULL;
CCNativeCmdCtrl::pfn_V2PNativeFacadeInit_Ex CCNativeCmdCtrl::mfn_V2PNativeFacadeInit_Ex = NULL;
CCNativeCmdCtrl::pfn_V2PNativeFacadeExit CCNativeCmdCtrl::mfn_V2PNativeFacadeExit = NULL;
CCNativeCmdCtrl::pfn_V2PNativeFacadeRPC_Proc CCNativeCmdCtrl::mfn_V2PNativeFacadeRPC_Proc = NULL;
CCNativeCmdCtrl::pfn_getJVM CCNativeCmdCtrl::mfn_getJVM = NULL;
CCNativeCmdCtrl::pfn_readFileStream CCNativeCmdCtrl::mfn_readFileStream = NULL;
CCNativeCmdCtrl::pfn_V2PNFSet_isSharedJVM CCNativeCmdCtrl::mpfn_V2PNFSet_isSharedJVM = NULL; //<sonmi01>2015-9-6 ###???
HMODULE CCNativeCmdCtrl::m_hVMAPILib = NULL; /***INTERNAL***//***CLEANUP***/
V2PNF_HANDLE CCNativeCmdCtrl::m_hV2PNF = NULL; /***INTERNAL***/ /***CLEANUP***/

// CCNativeCmdCtrl
HRESULT CCNativeCmdCtrl::InitV2PNativeSrv()
{
	HRESULT hr = S_OK;
	DWORD LastError = 0;

	do 
	{
		D2D_AddJrePath();

		m_hVMAPILib = LoadLibrary(V2PNATIVEFACADE_LIB_NAME);
		if (NULL == m_hVMAPILib)
		{
			//LastError = WSAGetLastError();
			LastError = GetLastError();
			hr = HRESULT_FROM_WIN32(LastError);
			//D_SET_FISRT_STATUS(hr, LastError);
			//D_SET_FISRT_ERROR_MESSAGE(TEXT("")zzzzzz);
			//D_SET_LAST_STATUS(hr, LastError);
			//D_SET_LAST_ERROR_MESSAGE(TEXT("")zzzzzz);
			D2DDEBUGLOG(0, hr, TEXT(__FUNCTION__) TEXT("::LoadLibrary(%s) failed."), V2PNATIVEFACADE_LIB_NAME);
			break;
		}

		//////////////////////////////////////////////////////////////////////////
		mfn_V2PNativeFacadeInit = (pfn_V2PNativeFacadeInit)GetProcAddress(m_hVMAPILib, ApiName(V2PNativeFacadeInit));
		if (NULL == mfn_V2PNativeFacadeInit)
		{
			//LastError = WSAGetLastError();
			LastError = GetLastError();
			hr = HRESULT_FROM_WIN32(LastError);
			//D_SET_FISRT_STATUS(hr, LastError);
			//D_SET_FISRT_ERROR_MESSAGE(TEXT("")zzzzzz);
			//D_SET_LAST_STATUS(hr, LastError);
			//D_SET_LAST_ERROR_MESSAGE(TEXT("")zzzzzz);
			D2DDEBUGLOG(0, hr, TEXT(__FUNCTION__) TEXT("::GetProcAddress(%S) failed."), ApiName(V2PNativeFacadeInit));
			break;
		}


		//////////////////////////////////////////////////////////////////////////
		mfn_V2PNativeFacadeInit_Ex = (pfn_V2PNativeFacadeInit_Ex)GetProcAddress(m_hVMAPILib, ApiName(V2PNativeFacadeInit_Ex));
		if (NULL == mfn_V2PNativeFacadeInit_Ex)
		{
			//LastError = WSAGetLastError();
			LastError = GetLastError();
			hr = HRESULT_FROM_WIN32(LastError);
			//D_SET_FISRT_STATUS(hr, LastError);
			//D_SET_FISRT_ERROR_MESSAGE(TEXT("")zzzzzz);
			//D_SET_LAST_STATUS(hr, LastError);
			//D_SET_LAST_ERROR_MESSAGE(TEXT("")zzzzzz);
			D2DDEBUGLOG(0, hr, TEXT(__FUNCTION__) TEXT("::GetProcAddress(%S) failed."), ApiName(V2PNativeFacadeInit_Ex));
			break;
		}

		//////////////////////////////////////////////////////////////////////////
		mfn_V2PNativeFacadeExit = (pfn_V2PNativeFacadeExit)GetProcAddress(m_hVMAPILib, ApiName(V2PNativeFacadeExit));
		if (NULL == mfn_V2PNativeFacadeExit)
		{
			//LastError = WSAGetLastError();
			LastError = GetLastError();
			hr = HRESULT_FROM_WIN32(LastError);
			//D_SET_FISRT_STATUS(hr, LastError);
			//D_SET_FISRT_ERROR_MESSAGE(TEXT("")zzzzzz);
			//D_SET_LAST_STATUS(hr, LastError);
			//D_SET_LAST_ERROR_MESSAGE(TEXT("")zzzzzz);
			D2DDEBUGLOG(0, hr, TEXT(__FUNCTION__) TEXT("::GetProcAddress(%S) failed."), ApiName(V2PNativeFacadeExit));
			break;
		}

		//////////////////////////////////////////////////////////////////////////
		mfn_V2PNativeFacadeRPC_Proc = (pfn_V2PNativeFacadeRPC_Proc)GetProcAddress(m_hVMAPILib, ApiName(V2PNativeFacadeRPC_Proc));
		if (NULL == mfn_V2PNativeFacadeRPC_Proc)
		{
			//LastError = WSAGetLastError();
			LastError = GetLastError();
			hr = HRESULT_FROM_WIN32(LastError);
			//D_SET_FISRT_STATUS(hr, LastError);
			//D_SET_FISRT_ERROR_MESSAGE(TEXT("")zzzzzz);
			//D_SET_LAST_STATUS(hr, LastError);
			//D_SET_LAST_ERROR_MESSAGE(TEXT("")zzzzzz);
			D2DDEBUGLOG(0, hr, TEXT(__FUNCTION__) TEXT("::GetProcAddress(%S) failed."), ApiName(V2PNativeFacadeRPC_Proc));
			break;
		}

		//////////////////////////////////////////////////////////////////////////
		mfn_getJVM = (pfn_getJVM)GetProcAddress(m_hVMAPILib, ApiName(getJVM));
		if (NULL == mfn_getJVM)
		{
			//LastError = WSAGetLastError();
			LastError = GetLastError();
			hr = HRESULT_FROM_WIN32(LastError);
			//D_SET_FISRT_STATUS(hr, LastError);
			//D_SET_FISRT_ERROR_MESSAGE(TEXT("")zzzzzz);
			//D_SET_LAST_STATUS(hr, LastError);
			//D_SET_LAST_ERROR_MESSAGE(TEXT("")zzzzzz);
			D2DDEBUGLOG(0, hr, TEXT(__FUNCTION__) TEXT("::GetProcAddress(%S) failed."), ApiName(getJVM));
			break;
		}

        mfn_readFileStream = (pfn_readFileStream)GetProcAddress(m_hVMAPILib, ApiName(readFileStream));
        if (NULL == mfn_readFileStream)
        {
            //LastError = WSAGetLastError();
            LastError = GetLastError();
            hr = HRESULT_FROM_WIN32(LastError);
            //D_SET_FISRT_STATUS(hr, LastError);
            //D_SET_FISRT_ERROR_MESSAGE(TEXT("")zzzzzz);
            //D_SET_LAST_STATUS(hr, LastError);
            //D_SET_LAST_ERROR_MESSAGE(TEXT("")zzzzzz);
            D2DDEBUGLOG(0, hr, TEXT(__FUNCTION__) TEXT("::GetProcAddress(%S) failed."), ApiName(readFileStream));
            break;
        }

		//<sonmi01>2015-9-6 ###???
		mpfn_V2PNFSet_isSharedJVM = (pfn_V2PNFSet_isSharedJVM)GetProcAddress(m_hVMAPILib, ApiName(V2PNFSet_isSharedJVM));
		if (NULL == mpfn_V2PNFSet_isSharedJVM)
		{
			//LastError = WSAGetLastError();
			LastError = GetLastError();
			hr = HRESULT_FROM_WIN32(LastError);
			//D_SET_FISRT_STATUS(hr, LastError);
			//D_SET_FISRT_ERROR_MESSAGE(TEXT("")zzzzzz);
			//D_SET_LAST_STATUS(hr, LastError);
			//D_SET_LAST_ERROR_MESSAGE(TEXT("")zzzzzz);
			D2DDEBUGLOG(0, hr, TEXT(__FUNCTION__) TEXT("::GetProcAddress(%S) failed."), ApiName(V2PNFSet_isSharedJVM));
			break;
		}


		//init lib
		m_hV2PNF = mfn_V2PNativeFacadeInit(NULL, FALSE);
		if (NULL == m_hV2PNF)
		{
			LastError = ERROR_INVALID_HANDLE;
			hr = HRESULT_FROM_WIN32(LastError);
			D2DDEBUGLOG(0, hr, TEXT(__FUNCTION__) TEXT("::Init native facade failed."));
			break;
		}

		//get shared jvm
		m_pJavaVM = mfn_getJVM(m_hV2PNF);
		if (NULL == m_pJavaVM)
		{
			LastError = ERROR_NOT_READY;
			hr = HRESULT_FROM_WIN32(LastError);
			D2DDEBUGLOG(0, hr, TEXT(__FUNCTION__) TEXT("::Failed to get Java VM."));
			break;
		}

		//<sonmi01>2015-9-6 ###???
		mpfn_V2PNFSet_isSharedJVM(TRUE);

	} while (FALSE);
	
	return hr;
}

HRESULT CCNativeCmdCtrl::UnInitV2PNativeSrv()
{
	do 
	{
		if (m_hV2PNF)
		{
			if (mfn_V2PNativeFacadeExit) mfn_V2PNativeFacadeExit(m_hV2PNF);
			m_hV2PNF = NULL;
		}

		//////////////////////////////////////////////////////////////////////////
		if (m_hVMAPILib)
		{
			FreeLibrary(m_hVMAPILib);
			m_hVMAPILib = NULL;
		}
	} while (FALSE);

	return S_OK;
}

HRESULT STDMETHODCALLTYPE CCNativeCmdCtrl::Initialize()
{
	HRESULT hr = E_NOINTERFACE;

	if (mfn_V2PNativeFacadeInit_Ex && m_pJavaVM)
	{
		m_hV2PInstant = mfn_V2PNativeFacadeInit_Ex(m_pJavaVM, NULL, FALSE);
		if (!m_hV2PInstant)
		{
			hr = E_FAIL;
		}
		else
		{
			hr = S_OK;
		}
	}

	return hr;
}

HRESULT STDMETHODCALLTYPE CCNativeCmdCtrl::UnInitialize()
{
	if (m_hV2PInstant)
	{
		if (mfn_V2PNativeFacadeExit) mfn_V2PNativeFacadeExit(m_hV2PInstant);
		m_hV2PNF = NULL;
	}

	return S_OK;
}

HRESULT STDMETHODCALLTYPE CCNativeCmdCtrl::RPC_Invoke(unsigned long inputCmd, BSTR inputData, unsigned long* outputValue, BSTR* outputData)
{
	HRESULT hr = S_OK;



	if (m_hV2PInstant && mfn_V2PNativeFacadeRPC_Proc)
	{
		wstring strinputData = CString(inputData).GetString();
		wstring stroutputData;

		int nRet = mfn_V2PNativeFacadeRPC_Proc(m_hV2PInstant, inputCmd, strinputData, outputValue, stroutputData);
		if (nRet)
		{
			D2DDEBUGLOG(LL_INF, nRet, __WFUNCTION__ _T("::NativeClassRPCProc begin, Recved Cmd %u."), inputCmd);
			hr = E_FAIL;
		}
		else
		{
			CComBSTR bstrOut(stroutputData.c_str());
			*outputData = bstrOut.Detach();
		}
	}

	return hr;
}

HRESULT STDMETHODCALLTYPE CCNativeCmdCtrl::GetServerInfo(/* [out] */ unsigned long *processId, /* [out] */ unsigned long *threadId)
{
	if (processId)
	{
		*processId = GetCurrentProcessId();
	}

	if (threadId)
	{
		*threadId = GetCurrentThreadId();
	}

	return S_OK;
}

HRESULT STDMETHODCALLTYPE CCNativeCmdCtrl::SetClientInfo(/* [in] */ LONG cid, /* [in] */ BSTR cinfo)
{
	CString strcInfo(cinfo);

	D2DDEBUGLOG(LL_INF, 0, __WFUNCTION__ _T("::Client has connected to the server, cid=%u, cinfo=%s."), (ULONG)cid, strcInfo.GetString());

	return S_OK;
}

HRESULT STDMETHODCALLTYPE CCNativeCmdCtrl::readStream(
    /* [in] */ long long llOffset,
    /* [in] */ int lengthToRead,
    /* [size_is][out] */ byte *stream,
    /* [out] */ int *lengthRead,
    /* [out] */ int *ret)
{
    if (m_hV2PInstant && mfn_V2PNativeFacadeRPC_Proc)
    {
        int iRet = mfn_readFileStream((unsigned char *)stream, lengthToRead, lengthRead, llOffset, m_hV2PInstant);
        if (ret)
        {
            *ret = iRet;
        }
    }

    return S_OK;
}
