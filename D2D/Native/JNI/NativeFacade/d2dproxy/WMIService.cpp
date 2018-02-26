#include "WMIService.h"
#include "DbgLog.h"

extern CDbgLog logObj;

HRESULT CWMIService::m_hrLastError = S_OK;
CWMIService::CWMIService(void)
{
	m_bInit = FALSE;
	m_bInitCom = FALSE;
    m_pLoc = NULL;
    m_pSvc = NULL;
}

CWMIService::~CWMIService(void)
{
    Uninitialize();
}

HRESULT	CWMIService::GetLastError( )
{
	return CWMIService::m_hrLastError;
}

void CWMIService::SetLastError( HRESULT hrLastError )
{
	CWMIService::m_hrLastError = hrLastError;
}

std::wstring  CWMIService::GetWMIErrorMsg( HRESULT hErr )
{
   WCHAR szBuf[1024] = {0};
   ::FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM | 
                   FORMAT_MESSAGE_IGNORE_INSERTS,
                   NULL, hErr, 0, szBuf, _ARRAYSIZE(szBuf) ,NULL );
   return szBuf;
}

BOOL CWMIService::Initialize( LPCWSTR wsNameSpace )
{
	if( m_bInit )
    {
		return TRUE;
    }

    // Initialize COM
	HRESULT hRes = CoInitializeEx(NULL, COINIT_MULTITHREADED);
    if (hRes == RPC_E_CHANGED_MODE)
    {
        // A previous call to CoInitializeEx specified the concurrency model for this thread as multi-thread apartment (MTA). 
        // This could also indicate that a change from neutral-threaded apartment to single-threaded apartment has occurred.
        // Don't call CoUninitialize in this case
        logObj.LogW(LL_WAR, 0, L"A previous call to CoInitializeEx specified the concurrency model for this thread as multi-thread apartment (MTA)");
        m_bInitCom = FALSE; 
    }
    else if( hRes == S_FALSE )
    {
        // To close the COM library gracefully, each successful call to CoInitialize or CoInitializeEx, 
        // including those that return S_FALSE, must be balanced by a corresponding call to CoUninitialize. 
        logObj.LogW(LL_WAR, 0, L"The COM library has already been initialized in this thread");
        m_bInitCom = TRUE;
    }
	else if(FAILED(hRes))
	{
		CWMIService::SetLastError(hRes);
		std::wstring strMsg = CWMIService::GetWMIErrorMsg(hRes);
        logObj.LogW(LL_ERR, 0, L"Failed to call CoInitialize. Err[%d], Msg[%s].", hRes, strMsg.c_str());
		return FALSE;
	}
    else 
    {
        m_bInitCom = TRUE;
    }

    // Set general COM security levels --------------------------
    // Note: If you are using Windows 2000, you need to specify -
    // the default authentication credentials for a user by using
    // a SOLE_AUTHENTICATION_LIST structure in the pAuthList ----
    // parameter of CoInitializeSecurity ------------------------
    hRes =  CoInitializeSecurity(
        NULL, 
        -1,                          // COM authentication
        NULL,                        // Authentication services
        NULL,                        // Reserved
        RPC_C_AUTHN_LEVEL_DEFAULT,   // Default authentication 
        RPC_C_IMP_LEVEL_IMPERSONATE, // Default Impersonation  
        NULL,                        // Authentication info
        EOAC_NONE,                   // Additional capabilities 
        NULL                         // Reserved
        );
	if(FAILED(hRes) && hRes != RPC_E_TOO_LATE)
	{
        CWMIService::SetLastError(hRes);
        std::wstring strMsg = CWMIService::GetWMIErrorMsg(hRes);
        logObj.LogW(LL_ERR, 0, L"Failed to call CoInitializeSecurity. Err[%d], Msg[%s].", hRes, strMsg.c_str());
		return FALSE;
	}
	
	// Obtain the initial locator to WMI -------------------------
    hRes = CoCreateInstance(
        CLSID_WbemLocator,             
        0, 
        CLSCTX_INPROC_SERVER, 
        IID_IWbemLocator, (LPVOID *) &m_pLoc);
	if(FAILED(hRes))
	{
		// Sometimes the environment is not ready, try again
		if(CO_E_SERVER_EXEC_FAILURE == hRes)
		{
			hRes = CoCreateInstance(
									CLSID_WbemLocator,             
									0, 
									CLSCTX_INPROC_SERVER, 
									IID_IWbemLocator, (LPVOID *) &m_pLoc);
		}		
		if(FAILED(hRes))
		{
			CWMIService::SetLastError(hRes);
			std::wstring strMsg = CWMIService::GetWMIErrorMsg(hRes);
            logObj.LogW(LL_ERR, 0, L"Failed to get interface IID_IWbemLocator. Err[%d], Msg[%s].", hRes, strMsg.c_str());
			return FALSE;
		}
	}

    // Connect to the requested namespace with the current user and obtain pointer pSvc to make IWbemServices calls.
    hRes = m_pLoc->ConnectServer(
         _bstr_t(wsNameSpace), // Object path of WMI namespace
         NULL,                    // User name. NULL = current user
         NULL,                    // User password. NULL = current
         0,                       // Locale. NULL indicates current
         NULL,                    // Security flags.
         0,                       // Authority (e.g. Kerberos)
         0,                       // Context object 
         &m_pSvc                    // pointer to IWbemServices proxy
         );
	if(FAILED(hRes))
	{
		CWMIService::SetLastError(hRes);
        std::wstring strMsg = CWMIService::GetWMIErrorMsg(hRes);
        logObj.LogW(LL_ERR, 0, L"Failed to ConnectServer to Namespace[%s]. Err[%d], Msg[%s].", wsNameSpace, hRes, strMsg.c_str());
		return FALSE;
	}
	
	// Set security levels on the proxy -------------------------
    hRes = CoSetProxyBlanket(
       m_pSvc,                        // Indicates the proxy to set
       RPC_C_AUTHN_WINNT,           // RPC_C_AUTHN_xxx
       RPC_C_AUTHZ_NONE,            // RPC_C_AUTHZ_xxx
       NULL,                        // Server principal name 
       RPC_C_AUTHN_LEVEL_CALL,      // RPC_C_AUTHN_LEVEL_xxx 
       RPC_C_IMP_LEVEL_IMPERSONATE, // RPC_C_IMP_LEVEL_xxx
       NULL,                        // client identity
       EOAC_NONE                    // proxy capabilities 
    );
	if(FAILED(hRes))
	{
		CWMIService::SetLastError(hRes);
        std::wstring strMsg = CWMIService::GetWMIErrorMsg(hRes);
        logObj.LogW(LL_ERR, 0, L"Failed to CoSetProxyBlanket. Err[%d], Msg[%s].", hRes, strMsg.c_str());
		return FALSE;
	}

	m_bInit = TRUE;
	return TRUE;
}

void CWMIService::Uninitialize( )
{
   if (m_pSvc != NULL)
   {
       m_pSvc->Release();
       m_pSvc = NULL;
   }

   if (m_pLoc != NULL)
   {
       m_pLoc->Release();
       m_pLoc = NULL;
   }
        
   if( m_bInitCom)
   {
	  CoUninitialize( );
      m_bInitCom = FALSE;
   }
   m_bInit = FALSE;
}

BOOL CWMIService::DisableTSO( wstring& wsName )
{
    BOOL bSucceed = TRUE;
    if (!m_bInit)
    {
        logObj.LogW(LL_ERR, 0, L"DisableTSO:: WMI has not been initialized.");
        return FALSE;
    }

    IEnumWbemClassObject* pEnumerator = NULL;  
    HRESULT hRes = m_pSvc->ExecQuery(
        bstr_t("WQL"), 
        bstr_t("SELECT * FROM MSFT_NetAdapterLsoSettingData"),
        WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY, 
        NULL,
        &pEnumerator);
    if(FAILED(hRes))  
    {  
        CWMIService::SetLastError(hRes);
        std::wstring strMsg = CWMIService::GetWMIErrorMsg(hRes);
        logObj.LogW(LL_ERR, 0, L"Failed to select from MSFT_NetAdapterLsoSettingData. Err[%d], Msg[%s].", hRes, strMsg.c_str());
        return FALSE;
    }  

    // Final Next will return WBEM_S_FALSE
    DWORD dwIndex = 0;
    HRESULT hEnumRes = WBEM_S_NO_ERROR;
    while ( WBEM_S_NO_ERROR == hEnumRes )
    {
        ULONG ulReturned = 0;  
        IWbemClassObject* pClsObj = NULL;  

        hEnumRes = pEnumerator->Next(WBEM_INFINITE, 1, &pClsObj, &ulReturned);
        if(FAILED(hEnumRes) || pClsObj == NULL)  
        {
            if (hEnumRes != WBEM_S_FALSE)
            {
                logObj.LogW(LL_ERR, 0, L"Failed to enum next elem. NumReturned[%d].", ulReturned);
                bSucceed = FALSE;
            }
            break;  
        }

        VARIANT vtPath, vtIPv4Enabled, vtIPv6Enabled, vtV1IPv4Enabled;  
        VariantInit(&vtPath);  
        VariantInit(&vtIPv4Enabled);  
        VariantInit(&vtIPv6Enabled);  
        VariantInit(&vtV1IPv4Enabled);  
        do 
        {
            hRes = pClsObj->Get(L"__Path", 0, &vtPath, NULL, NULL);
            if(FAILED(hRes))  
            {  
                logObj.LogW(LL_ERR, 0, L"Object has no __Path!");
                bSucceed = FALSE;
                break;
            } 

            // Convert and compare adapter name
            BOOL bFound = FALSE;
            wstring wsObjPath;
            ConvertBSTR2Wstring(vtPath, wsObjPath);
            if (wsObjPath.find(wsName) == wstring::npos)
            {
                break;
            }

            // Check TSO status
            HRESULT hRes1 = pClsObj->Get(L"IPv4Enabled", 0, &vtIPv4Enabled, NULL, NULL);
            HRESULT hRes2 = pClsObj->Get(L"IPv6Enabled", 0, &vtIPv6Enabled, NULL, NULL);
            HRESULT hRes3 = pClsObj->Get(L"V1IPv4Enabled", 0, &vtV1IPv4Enabled, NULL, NULL);
            if (FAILED(hRes1) || FAILED(hRes2) || FAILED(hRes3))
            {  
                logObj.LogW(LL_ERR, 0, L"Failed to get attributes of Object[%d].", dwIndex);
                bSucceed = FALSE;
                break;
            } 

            // Find and execute disable method
            if (
                vtIPv4Enabled.boolVal == VARIANT_TRUE || 
                vtIPv6Enabled.boolVal == VARIANT_TRUE || 
                vtV1IPv4Enabled.boolVal == VARIANT_TRUE 
                )
            {
                IWbemClassObject* pResult = NULL;  
                hRes = m_pSvc->ExecMethod(vtPath.bstrVal, L"Disable", 0, NULL, NULL, &pResult, NULL);
                if(FAILED(hRes))  
                {
                    CWMIService::SetLastError(hRes);
                    std::wstring strMsg = CWMIService::GetWMIErrorMsg(hRes);
                    logObj.LogW(LL_ERR, 0, L"Unable to disable TSO. Err[%d], Msg[%s].", hRes, strMsg.c_str());
                    bSucceed = FALSE;
                    break;
                }

                // Check return value
                VARIANT vtRet;  
                VariantInit(&vtRet);  
                hRes = pResult->Get(L"ReturnValue", 0, &vtRet, NULL, 0);
                if(FAILED(hRes))  
                {
                    CWMIService::SetLastError(hRes);
                    std::wstring strMsg = CWMIService::GetWMIErrorMsg(hRes);
                    logObj.LogW(LL_ERR, 0, L"Unable to get return value of call. Err[%d], Msg[%s].", hRes, strMsg.c_str());
                    bSucceed = FALSE;
                }
                else if (vtRet.intVal != 0)
                {
                    logObj.LogW(LL_ERR, 0, L"Unable to get return value of call. Err[%d].", vtRet.intVal);
                    bSucceed = FALSE;
                }
                VariantClear(&vtRet);
                if (pResult != NULL)
                {
                    pResult->Release();  
                    pResult = NULL;
                }
            }
            else
            {
                logObj.LogW(LL_WAR, 0, L"TSO has already been disabled for NIC[%s].", wsName.c_str());
            }

        } while (FALSE);

        VariantClear(&vtV1IPv4Enabled);  
        VariantClear(&vtIPv6Enabled);  
        VariantClear(&vtIPv4Enabled);  
        VariantClear(&vtPath);  
        if (pClsObj != NULL)
        {
            pClsObj->Release();  
            pClsObj = NULL;
        }

        dwIndex++;
    }

    if (pEnumerator != NULL)
    {
        pEnumerator->Release();  
        pEnumerator = NULL;
    }

    return bSucceed;
}
