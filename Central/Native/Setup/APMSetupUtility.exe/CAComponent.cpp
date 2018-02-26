#include "CAComponent.h"
#include "Golbals.h"
#include "EdgeSetupDefine.h"
#include "D2DComponent.h"
#include "CMComponent.h"
//#include "Adapter.h"
#include "APMSetupUtility.h"
//#include "MSXMLParserWrapper.h"
//#include "ARCSetupSvc.h"
#include <msi.h>

#pragma comment(lib, "msi.lib")

extern CUpdateCheckApp theApp;
#define INFAPPLICATIONS    _T("Applications")
#define APMINIPATH         _T("ApmIniPath")

CCAComponent::CCAComponent(void)
{
	m_strID = _T("");
	m_strProductCode = _T("");
	m_nMajor = 0;
	m_nMinor = 0;
	m_nBuild = 0;
	m_strInstallPath = _T("");
	m_strIniPath = _T("");
	hPipe = INVALID_HANDLE_VALUE;
	bServerRuningBeforeUpdate = FALSE;
}

CCAComponent::~CCAComponent(void)
{
	if (hPipe != INVALID_HANDLE_VALUE)
	{
		CloseHandle(hPipe);
	}
}

void CCAComponent::Initialize(LPCTSTR lpctFile, LPCTSTR lpctSection)
{
	TRACE(_T("CComponent::Initialize\n"));
	TCHAR szBuffer[MAX_PATH];
	memset(szBuffer, 0, sizeof(szBuffer));
	SetupGetPrivateProfileString(lpctSection, _T("IdName"), _T(""), szBuffer, MAX_PATH, lpctFile);
	if (_tcslen(szBuffer))
	{
		SetComponentId(szBuffer);
	}
	initProductInfo(lpctFile, lpctSection);
	memset(szBuffer, 0, sizeof(szBuffer));
	_tcscpy_s(szBuffer, _countof(szBuffer), m_strInstallPath);
	::PathRemoveFileSpec(szBuffer);
	::PathAppend(szBuffer, _T("Update Manager\\Config\\PMSettings.ini"));
	m_strIniPath = szBuffer;

	memset(szBuffer, 0, sizeof(szBuffer));
	SetupGetPrivateProfileString(GetComponentId(), APMINIPATH, _T(""), szBuffer, _countof(szBuffer), theApp.m_strInfFile);
}

void CCAComponent::initProductInfo(LPCTSTR lpctFile, LPCTSTR lpctSection)
{
	TCHAR szProductCode[MAX_PATH];
	TCHAR szRegVerLocBuffer[MAX_PATH];
	TCHAR szVerInfoBuffer[MAX_PATH];
	CStringArray strProduceCodeArray;
	DWORD dwSize;

	WriteLog(_T("Get product version, product code, installpath"));

	memset(szProductCode, 0, sizeof(szProductCode));
	SetupGetPrivateProfileString(lpctSection, SETUPINF_VALUE_PROCUCTCODE, _T(""), szProductCode, MAX_PATH, lpctFile);
	GetStringArray(szProductCode, _T(';'), strProduceCodeArray);

	for (int i = 0; i < strProduceCodeArray.GetCount(); i++)
	{
		if (INSTALLSTATE_DEFAULT == ::MsiQueryProductState(strProduceCodeArray.GetAt(i)))
		{
			m_strProductCode = strProduceCodeArray.GetAt(i);
			memset(szRegVerLocBuffer, 0, sizeof(szRegVerLocBuffer));
			SetupGetPrivateProfileString(lpctSection, SETUPINF_VALUE_REG_VERLOCATION, _T(""), szRegVerLocBuffer, MAX_PATH, lpctFile);
			if (_tcslen(szRegVerLocBuffer))
			{
				if (!InitInstallVersion(szRegVerLocBuffer))
				{
					memset(szVerInfoBuffer, 0, sizeof(szVerInfoBuffer));
					dwSize = _countof(szVerInfoBuffer);
					if (ERROR_SUCCESS == ::MsiGetProductInfo(strProduceCodeArray.GetAt(i), INSTALLPROPERTY_VERSIONSTRING, szVerInfoBuffer, &dwSize))
					{
						int nMajor = 0;
						int nMinor = 0;
						int nBuild = 0;
						int nMinorBuild = 0;
						_stscanf_s(szVerInfoBuffer, _T("%d.%d.%d.%d"), &nMajor, &nMinor, &nBuild, &nMinorBuild);
						if (m_nMajor==0)
						{
							m_nMajor = nMajor;
							m_nMinor = nMinor;
							m_nBuild = nBuild;
						}
					}
				}

				TCHAR szRegPathLocBuf[MAX_PATH];
				TCHAR szInstallDirBuffer[MAX_PATH];
				memset(szRegPathLocBuf, 0, sizeof(szRegPathLocBuf));
				SetupGetPrivateProfileString(lpctSection, SETUPINF_VALUE_REGPATHLOCATION, _T(""), szRegPathLocBuf, MAX_PATH, lpctFile);
				if (!InitInstallDir(szRegPathLocBuf))
				{
					dwSize = _countof(szInstallDirBuffer);
					if (ERROR_SUCCESS == ::MsiGetProductInfo(strProduceCodeArray.GetAt(i), INSTALLPROPERTY_INSTALLLOCATION, szInstallDirBuffer, &dwSize))
					{
						::PathRemoveBackslash(szInstallDirBuffer);
						m_strInstallPath = szInstallDirBuffer;
						//WriteLog(_T("Debug-----path:%s"), m_strInstallPath);
					}
				}
			}
			break;
		}
	}
}

CCAComponent *CCAComponent::CreateComponent(int nType)
{
	if (nType == D2D)
	{
		return new CD2DComponent;
	}
	else if (nType == CM)
	{
		return new CCMComponent;
	}

	return NULL;
}

BOOL CCAComponent::CheckInstallStatus()
{
	if (INSTALLSTATE_DEFAULT == ::MsiQueryProductState(m_strProductCode))
	{
		//WriteLog(_T("xxxxxxxxxxxxxxxxx %s"), m_strProductCode);
		return TRUE;
	}
	return FALSE;
}

BOOL CCAComponent::InitInstallDir(LPCTSTR lpctRegKey)
{
	if (lpctRegKey==NULL || _tcslen(lpctRegKey)==0)
		return FALSE;

	TCHAR szPath[MAX_PATH] = {0};
	DWORD dwSize;

	HKEY hKey = NULL;
	REGSAM regSam;

	if (Is64BitMachine(NULL))
		regSam = KEY_READ|KEY_WOW64_64KEY;
	else
		regSam = KEY_READ;

	if ( ERROR_SUCCESS == ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, lpctRegKey, 0, regSam, &hKey))
	{
		dwSize = sizeof(szPath);
		::RegQueryValueEx(hKey, REG_VALUE_PATH, 0, 0, (LPBYTE)szPath, &dwSize);
		::PathRemoveBackslash(szPath);
		m_strInstallPath = szPath;
		m_strInstallPath.Trim();
		::RegCloseKey(hKey);
	}

	return (!m_strInstallPath.IsEmpty());
}

BOOL CCAComponent::BackendExeRun()
{
	WriteLog(_T("begain to run Backend exe."));
	return TRUE;
}

DWORD CCAComponent::TestServerConnection(const APMSetting &apmSetting)
{
	return ERROR_SUCCESS;
}

DWORD CCAComponent::downloadPatch(HANDLE handle, Response& response)
{
	return ERROR_SUCCESS;
}

void CCAComponent::SetEnvPath()
{
	const CString envPath = _T("%path%;") + m_strInstallPath + _T("\\BIN");
	const CString strpath = _T("path");
	_tputenv_s(strpath, envPath);
}

DWORD CCAComponent::PatchInstall()
{
	return ERROR_SUCCESS;
}

BOOL CCAComponent::LoadApmSetting(EdgeAPM::APMSettingModel & apmSetting)
{
	BOOL m_bExist = TRUE;
	DWORD dwSize = 6*MAX_PATH;
	apmSetting.m_nServerType = SetupGetPrivateProfileInt(_T("DownloadServer") , _T("ServerType"), 0, theApp.m_strIniFile);

	apmSetting.m_stagingServer.m_strServer.Empty();
	SetupGetPrivateProfileString(_T("StagingServer"), _T("ServerName"), _T(""), apmSetting.m_stagingServer.m_strServer.GetBuffer(dwSize), dwSize, theApp.m_strIniFile);
	apmSetting.m_stagingServer.m_strServer.ReleaseBuffer();
	if (apmSetting.m_stagingServer.m_strServer.IsEmpty())
	{
		m_bExist = FALSE;
	}

	apmSetting.m_stagingServer.m_strPort.Empty();
	SetupGetPrivateProfileString(_T("StagingServer"), _T("ServerPort"), _T(""), apmSetting.m_stagingServer.m_strPort.GetBuffer(dwSize), dwSize,  theApp.m_strIniFile);
	apmSetting.m_stagingServer.m_strPort.ReleaseBuffer();

	apmSetting.m_bProxy = SetupGetPrivateProfileInt(_T("ProxySettings") , _T("UseProxy"), 0, theApp.m_strIniFile);
	apmSetting.m_bProxyAuth = SetupGetPrivateProfileInt(_T("ProxySettings") , _T("ProxyRequireAuth"), 0, theApp.m_strIniFile);

	apmSetting.m_proxyServer.m_strServer.Empty();
	SetupGetPrivateProfileString(_T("ProxySettings"), _T("ProxyServer"), _T(""), apmSetting.m_proxyServer.m_strServer.GetBuffer(dwSize), dwSize,  theApp.m_strIniFile);
	apmSetting.m_proxyServer.m_strServer.ReleaseBuffer();

	apmSetting.m_proxyServer.m_strPort.Empty();
	SetupGetPrivateProfileString(_T("ProxySettings"), _T("ProxyPort"), _T(""), apmSetting.m_proxyServer.m_strPort.GetBuffer(dwSize), dwSize,  theApp.m_strIniFile);
	apmSetting.m_proxyServer.m_strPort.ReleaseBuffer();
	if (!m_bExist || apmSetting.m_proxyServer.m_strServer.IsEmpty())
	{
		return FALSE;
	}


	apmSetting.m_strProxyUsername.Empty();
	SetupGetPrivateProfileString(_T("ProxySettings"), _T("ProxyUserName"), _T(""), apmSetting.m_strProxyUsername.GetBuffer(dwSize), dwSize,  theApp.m_strIniFile);
	WriteLog(_T("Get apm setting from %s %s."), theApp.m_strIniFile, m_strInstallPath);
	apmSetting.m_strProxyUsername.ReleaseBuffer();

	CString strdecryPassword = _T("");
	SetupGetPrivateProfileString(_T("ProxySettings"), _T("ProxyPassword"), _T(""), strdecryPassword.GetBuffer(dwSize), dwSize,  theApp.m_strIniFile);
	strdecryPassword.ReleaseBuffer();
	if (!strdecryPassword.IsEmpty())
	{
		DecryptString(strdecryPassword, apmSetting.m_strProxyPwd, m_strInstallPath);
	}
	return FALSE;
}

void CCAComponent::SaveApmSetting(EdgeAPM::APMSettingModel & apmSetting)
{
	//TCHAR szBuffer[10];
	//_itow_s(apmSetting.m_nServerType, szBuffer, sizeof(szBuffer), 10);
	SetupWritePrivateProfileInt(_T("DownloadServer"), _T("ServerType"), apmSetting.m_nServerType, theApp.m_strIniFile);
	if (!apmSetting.m_stagingServer.m_strServer.IsEmpty())
	{
		SetupWritePrivateProfileString(_T("StagingServer"), _T("ServerName"), apmSetting.m_stagingServer.m_strServer, theApp.m_strIniFile);
		SetupWritePrivateProfileString(_T("StagingServer"), _T("ServerPort"), apmSetting.m_stagingServer.m_strPort, theApp.m_strIniFile);
	}

	SetupWritePrivateProfileInt(_T("ProxySettings"), _T("UseProxy"), apmSetting.m_bProxy, theApp.m_strIniFile);
	SetupWritePrivateProfileString(_T("ProxySettings"), _T("ProxyServer"), apmSetting.m_proxyServer.m_strServer, theApp.m_strIniFile);
	SetupWritePrivateProfileString(_T("ProxySettings"), _T("ProxyPort"), apmSetting.m_proxyServer.m_strPort, theApp.m_strIniFile);
	SetupWritePrivateProfileInt(_T("ProxySettings"), _T("ProxyRequireAuth"), apmSetting.m_bProxyAuth, theApp.m_strIniFile);
	SetupWritePrivateProfileString(_T("ProxySettings"), _T("ProxyUserName"), apmSetting.m_strProxyUsername, theApp.m_strIniFile);
	CString strEncryPassword;
	if (!apmSetting.m_strProxyPwd)
	{
		EncryptString(apmSetting.m_strProxyPwd, strEncryPassword, m_strInstallPath);
		SetupWritePrivateProfileString(_T("ProxySettings"), _T("ProxyPassword"), strEncryPassword, theApp.m_strIniFile);
	}
}

void CCAComponent::KillPossibleProcesses()
{
	CArray<DWORD> pidAry;
	CStringArray m_strApplicationArray;
	TCHAR szBuffer[MAX_PATH*4];
	memset(szBuffer, 0, sizeof(szBuffer));
	TCHAR szApplicationPath[MAX_PATH];

	CStringArray strExeArray;
	SetupGetPrivateProfileString(GetComponentId(), INFAPPLICATIONS, _T(""), szBuffer, _countof(szBuffer), theApp.m_strInfFile);
	GetStringArray(szBuffer, _T(';'), m_strApplicationArray);

	for (int i = 0; i < m_strApplicationArray.GetCount(); i++)
	{
		memset(szApplicationPath, 0, sizeof(szApplicationPath));
		_tcscpy_s(szApplicationPath, _countof(szApplicationPath), m_strInstallPath);
		::PathAppend(szApplicationPath, m_strApplicationArray.GetAt(i));
		strExeArray.Add(szApplicationPath);
	}

	FindProcesses(strExeArray, pidAry);
	for (int j=0; j<pidAry.GetCount(); j++)
	{
		KillProcess(pidAry[j]);
	}
	//GetProcessByName(lpctProcessName, DWORD &npid);
}

BOOL CCAComponent::InitInstallVersion(LPCTSTR lpctRegKey)
{
	BOOL bRet = FALSE;

	if (lpctRegKey==NULL || _tcslen(lpctRegKey)==0)
		return FALSE;

	TCHAR szVersionInfo[MAX_PATH] = {0};
	DWORD dwSize;

	HKEY hKey = NULL;
	REGSAM regSam;

	if (Is64BitMachine(NULL))
		regSam = KEY_READ|KEY_WOW64_64KEY;
	else
		regSam = KEY_READ;

	if ( ERROR_SUCCESS == ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, lpctRegKey, 0, regSam, &hKey))
	{
		dwSize = sizeof(szVersionInfo);
		::RegQueryValueEx(hKey, REG_VALUE_VERSION, 0, 0, (LPBYTE)szVersionInfo, &dwSize);

		::RegCloseKey(hKey);
	}

	if (_tcslen(szVersionInfo)>0)
	{
		int nMinorBuild = 0;
		_stscanf_s(szVersionInfo, _T("%d.%d.%d.%d"), &m_nMajor, &m_nMinor, &m_nBuild, &nMinorBuild);

		if (m_nMajor>0)
			bRet = TRUE;
	}
	return bRet;
}

std::wstring CCAComponent::generateRequestId()
{
	static int id = 0;
	WCHAR buf[10];
	wstring strId;
	
	_itow(id++, buf, 10);
	strId = wstring(L"UICommand_") + buf;
	return strId;
}

void CCAComponent::DoExitThings()
{
	;
}

DWORD CCAComponent::HandleWindowsService()
{
	return ERROR_SUCCESS;
}