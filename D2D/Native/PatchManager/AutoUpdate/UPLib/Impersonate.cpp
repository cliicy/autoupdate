#include "stdafx.h"
#include "UpLib.h"

CImpersonate::CImpersonate()
	: m_bOK(FALSE)
{
}

CImpersonate::~CImpersonate()
{
	if (m_bOK)
		RevertToSelf();
}

void CImpersonate::LogonOnWithUser(const wstring& strUserName, const wstring& strPassword)
{
	DWORD dwRet = 0;
	if (IsBadAccount(strUserName, strPassword))
	{
		m_log.LogW(LL_ERR, 0, L"%s: Invalid user name and password", __WFUNCTION__);
		return;
	}

	wstring user, domain;
	HANDLE hTok = NULL;
	GetStandardAccount(strUserName, domain, user);
	if (!LogonUser(user.c_str(), domain.c_str(), strPassword.c_str(),
		LOGON32_LOGON_NETWORK, LOGON32_PROVIDER_DEFAULT, &hTok))
	{
		dwRet = GetLastError();
		if (dwRet == 1326) // bad password
			SaveBadAccount(strUserName, strPassword);
		m_log.LogW(LL_DET, dwRet, L"%s: Fail to LogonUser, domain[%s], user[%s], so use current user context", __WFUNCTION__,
			domain.c_str(), user.c_str());
		return;
	}

	if (!ImpersonateLoggedOnUser(hTok))
	{
		dwRet = GetLastError();
		m_log.LogW(LL_ERR, dwRet, L"%s: Fail to ImpersonateLoggedOnUser, just use current user context", __WFUNCTION__);
	}

	CloseHandle(hTok);
	hTok = NULL;
	m_bOK = TRUE;
}

void CImpersonate::GetStandardAccount(const wstring &strUserName, wstring &strDomain, wstring &strUser)
{
	size_t pos = strUserName.find(L"\\");
	if (wstring::npos != pos)
	{
		strDomain = strUserName.substr(0, pos);
		strUser = strUserName.substr(pos + 1);
	}
	else
	{
		pos = strUserName.find(L"@");
		if (wstring::npos == pos)
		{
			strDomain = L".";
			strUser = strUserName;
		}
		else
		{
			strDomain.clear();
			strUser = strUserName;
		}
	}
}

BOOL CImpersonate::IsBadAccount(const wstring& strUsername, const wstring& strPassword)
{
	wstring strSigFile = PRODUTILS::GetUpdateManagerHome();
	strSigFile = PATHUTILS::path_join(strSigFile, L"\\Config\\Account.sig");
	HANDLE hFile = ::CreateFile(strSigFile.c_str(), GENERIC_READ, FILE_SHARE_READ, 0, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
	if (hFile == INVALID_HANDLE_VALUE)
		return FALSE;

	DWORD dwFileSize = ::GetFileSize(hFile, 0);
	if (dwFileSize == 0)
	{
		CloseHandle(hFile);
		hFile = INVALID_HANDLE_VALUE;
		::DeleteFile(strSigFile.c_str());
		return FALSE;
	}
	// allocate 1 MB memory to read content into buffer
	char* pBuf = (char*)malloc(dwFileSize + 2);
	if (!pBuf)
	{
		CloseHandle(hFile);
		m_log.LogW(LL_ERR, 0, L"%s: Failed to allocate %d buffer", __WFUNCTION__, dwFileSize);
		hFile = INVALID_HANDLE_VALUE;
		return FALSE;
	}

	DWORD dwRead = 0;
	ReadFile(hFile, pBuf, dwFileSize, &dwRead, NULL);
	CloseHandle(hFile);
	hFile = INVALID_HANDLE_VALUE;

	wstring str = strUsername + L"|" + strPassword;
	wstring strEncryptPwd = L"";
	ENCUTILS::EncryptToString(str.c_str(), strEncryptPwd);
	BOOL bBadPwd = FALSE;
	if (strEncryptPwd.length()*sizeof(WCHAR) == dwFileSize &&
		memcmp(strEncryptPwd.c_str(), pBuf, dwFileSize) == 0)
	{
		bBadPwd = TRUE;
	}
	free(pBuf);
	pBuf = NULL;

	if (!bBadPwd)
	{
		DeleteFile(strSigFile.c_str());
	}
	return bBadPwd;
}

DWORD CImpersonate::SaveBadAccount(const wstring& strUsername, const wstring& strPassword)
{
	wstring str = strUsername + L"|" + strPassword;
	wstring strEncryptPwd = L"";
	ENCUTILS::EncryptToString(str.c_str(), strEncryptPwd);
	if (strEncryptPwd.empty())
		return GetLastError();

	wstring strSigFile = PRODUTILS::GetUpdateManagerHome();
	strSigFile = PATHUTILS::path_join(strSigFile, L"\\Config\\Account.sig");
	HANDLE hFile = ::CreateFile(strSigFile.c_str(), GENERIC_WRITE, FILE_SHARE_READ, 0, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
	if (hFile == INVALID_HANDLE_VALUE)
		return FALSE;

	DWORD dwWrote = 0;
	::WriteFile(hFile, strEncryptPwd.c_str(), (DWORD)(strEncryptPwd.length()*sizeof(WCHAR)), &dwWrote, NULL);
	CloseHandle(hFile);
	hFile = INVALID_HANDLE_VALUE;

	return 0;
}
