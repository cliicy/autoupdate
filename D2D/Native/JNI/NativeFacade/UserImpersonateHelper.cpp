#include "StdAfx.h"
#include "UserImpersonateHelper.h"

#include "Iads.h"
#include "Adshlp.h"

extern CDbgLog logObj;
CUserImpersonateHelper::CUserImpersonateHelper(LPCWSTR pwszUserNameWithDomain, LPCWSTR pwszPassword)
{
	do{
		CDbgLog logObj(L"NativeFacade");
		m_hToken = NULL;
		m_isImp = false;
		m_errCode = S_OK;

		if (NULL == pwszUserNameWithDomain || NULL == pwszPassword)
		{
			m_errCode = E_INVALIDARG;
			break;
		}

		LPCWSTR pwszAdmin = NULL;
		LPCWSTR pwszDomain = NULL;

		wchar_t swzbuf[1024] = { 0 };
		memcpy(swzbuf, pwszUserNameWithDomain, wcslen(pwszUserNameWithDomain) * 2);
		wchar_t * pszFind;
		pszFind = wcschr(swzbuf, L'\\');

		if (pszFind)
		{
			if (IsBadReadPtr(pszFind, 2))
			{
				m_errCode = E_INVALIDARG;
				break;
			}

			pwszAdmin = pszFind + 1;
			pszFind[0] = 0;
			pwszDomain = swzbuf;
		}
		/*
		else
		{
			_bstr_t strDCName;
			if (0 == GetDomainName(strDCName))
			{
				pwszDomain = strDCName;
			}
		}
		*/

		HRESULT hr = S_OK;
		BOOL IsOpen;
		DWORD dwError = 0;
		BYTE * pszbuffer = NULL;

		OSVERSIONINFO osVer = { 0 };
		osVer.dwOSVersionInfoSize = sizeof(OSVERSIONINFO);
		BOOL bIsWin2008OrUp = (GetVersionEx(&osVer) && osVer.dwMajorVersion >= 6);

		DWORD dwLogonType = bIsWin2008OrUp ? LOGON32_LOGON_BATCH : LOGON32_LOGON_INTERACTIVE;

		IsOpen = LogonUserW(pwszAdmin, pwszDomain, pwszPassword,
			dwLogonType, LOGON32_PROVIDER_DEFAULT, &m_hToken);

		dwError = GetLastError();

		if (FALSE == IsOpen)
		{
			m_errCode = D2DCK_E_LOGON;
			logObj.LogW(LL_WAR,m_errCode, L"LogonUserW() failed 0x%x %ls %ls %ls", dwError, pwszAdmin, pwszDomain, pwszPassword);
			break;
		}

		m_isImp = ImpersonateLoggedOnUser(m_hToken);

		dwError = GetLastError();

		if (FALSE == m_isImp)
		{
			m_errCode = D2DCK_E_IMPERSONATE;
			logObj.LogW(LL_WAR, m_errCode, L"ImpersonateLoggedOnUser() failed 0x%x %ls %ls", dwError, pwszAdmin, pwszDomain);
			
			break;
		}
		
	} while (0);
}

HRESULT CUserImpersonateHelper::GetError(){
	return m_errCode;
}

HRESULT CUserImpersonateHelper::GetDomainName(_bstr_t & strDCname){
	IADs *pRoot;
	HRESULT hr;

	/*
	// Bind to the rootDSE object.
	hr = ADsOpenObject(L"LDAP://rootDSE",
		NULL,
		NULL,
		ADS_SECURE_AUTHENTICATION,
		__uuidof(IADs),
		(LPVOID*)&pRoot);
	if (SUCCEEDED(hr))
	{
		VARIANT var;

		VariantInit(&var);

		// Get the current domain DN. defaultNamingContext DC=ca,DC=com
		hr = pRoot->Get(L"defaultNamingContext", &var);

		if (SUCCEEDED(hr))
		{
			_bstr_t strName = var.bstrVal;
			if (strName.length()>0)
			{

				char * pszDC = strName;

				char *pszStant = NULL;
				char *pszEnd = NULL;

				int nLen = (int)strlen(pszDC);

				while (pszDC)
				{
					char szbuf[1024] = { 0 };
					char *pszStant = strchr(pszDC, '=');
					char *pszEnd = strchr(pszDC, ',');

					if (NULL == pszStant)
					{
						break;
					}

					if (NULL == pszStant&&NULL == pszEnd)
					{
						break;
					}
					if (pszEnd)
					{
						int nSublen = (pszEnd - pszStant) - 1;
						if (0 == nSublen) break;
						if (!IsBadReadPtr(pszStant + 1, nSublen))
							memcpy(szbuf, pszStant + 1, nSublen);
					}
					else
					{
						if (!IsBadReadPtr(pszStant + 1, 1))
							strcpy(szbuf, pszStant + 1);
					}

					if (strDCname.length()> 0) strDCname += ".";
					strDCname += szbuf;

					if (IsBadReadPtr(pszEnd + 1, 1))  break;
					pszDC = pszEnd + 1;



				}
			}
			VariantClear(&var);
		}

		pRoot->Release();
	}

	return hr;
	*/
	return S_OK;
}

CUserImpersonateHelper::~CUserImpersonateHelper()
{
	if (m_isImp)
	{
		RevertToSelf();
		m_isImp = false;
	}

	if (m_hToken)
	{
		CloseHandle(m_hToken);
		m_hToken = NULL;
	}
}
