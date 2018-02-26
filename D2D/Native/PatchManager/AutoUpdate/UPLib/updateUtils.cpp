#include "stdafx.h"
#include "UpLib.h"
#include "DRCommonlib.h"
#include "brandname.h"
#include "DbgLog.h"
#include "XXmlNode.h"
#include <algorithm>
#include "..\UpdateRes\ARCUpdateRes.h"
namespace UPUTILS
{
	static bool sortStagingServer(const UDP_STAGING_SVR &s1, const UDP_STAGING_SVR &s2)
	{
		return s1.nIndex < s2.nIndex;
	}

	static wstring getChildNodeText(CXXmlNode* pParent, const wstring& strChild, const wstring& strDefault)
	{
		if (!pParent)
			return strDefault;
		CXXmlNode* pChild = pParent->GetChildNode(strChild);
		if (pChild)
			return pChild->GetText();
		else
			return strDefault;
	}

	static CXXmlNode* updateChildNode(CXXmlNode* pParent, const wstring& strTag, const wstring& strText)
	{
		CXXmlNode* pChildNode = pParent->GetChildNode(strTag);
		if (!pChildNode)
		{
			pChildNode = CXXmlNode::CreateXmlNode(strTag);
			pParent->InsertNode(pChildNode, XXN_LAST);
		}
		if (!strText.empty())
		{
			pChildNode->SetText(strText);
		}
		return pChildNode;
	}

	static LANGID get_language_id()
	{
		//switch (::GetSystemDefaultLangID())
		//{
		//case 0x404:  return 0x404; //cht
		//case 0x804:  return 0x804; //chs
		//case 0x40c:  return 0x40c; //frn
		//case 0x407:	 return 0x407; //grm
		//case 0x410:	 return 0x410; //ita
		//case 0x411:  return 0x411; //jpn
		//case 0xc0a:  return 0xc0a; //spa
		//case 0x416:  return 0x416; //prb
		//default:	 return 0x409; //enu
		//}
		return GetUILanguage(0);
	}

	wstring GetDownloadHomeDirectory(DWORD dwProd)
	{
		wstring strUpdatemanager = PRODUTILS::GetUpdateManagerHome();
		switch (dwProd)
		{
		case ARCUPDATE_PRODUCT_AGENT:
			return PATHUTILS::path_join(strUpdatemanager, ARCUPDATE_SITE_ENGINE_UPDATES L"\\" ARCUPDATE_SITE_RELEASE_VERSION);
		case ARCUPDATE_PRODUCT_FULL:
			return PATHUTILS::path_join(strUpdatemanager, ARCUPDATE_SITE_FULL_UPDATES L"\\" ARCUPDATE_SITE_RELEASE_VERSION);
		case ARCUPDATE_PRODUCT_GATEWAY:
			return PATHUTILS::path_join(strUpdatemanager, ARCUPDATE_SITE_GATEWAY_UPDATES L"\\" ARCUPDATE_SITE_RELEASE_VERSION);
		case ARCUPDATE_PRODUCT_SELFUPDATE:
			return PATHUTILS::path_join(strUpdatemanager, ARCUPDATE_SITE_SELF_UPDATES L"\\" ARCUPDATE_SITE_RELEASE_VERSION);
		case ARCUPDATE_PRODUCT_PATCHUPDATE:
			return PATHUTILS::path_join(strUpdatemanager, ARCUPDATE_SITE_PATCH_UPDATES L"\\" ARCUPDATE_SITE_RELEASE_VERSION);
		default:
			return L"";
		}
	}

	//added by cliicy.luo
	wstring GetBIDownloadHomeDirectory(DWORD dwProd)
	{
		wstring strUpdatemanager = PRODUTILS::GetUpdateManagerHome();
		switch (dwProd)
		{
		case ARCUPDATE_PRODUCT_AGENT:
			return PATHUTILS::path_join(strUpdatemanager, ARCUPDATE_SITE_ENGINE_UPDATES L"\\" ARCUPDATE_SITE_RELEASE_VERSION BINARY_PATCH_DIR);
		case ARCUPDATE_PRODUCT_FULL:
			return PATHUTILS::path_join(strUpdatemanager, ARCUPDATE_SITE_FULL_UPDATES L"\\" ARCUPDATE_SITE_RELEASE_VERSION BINARY_PATCH_DIR);
		case ARCUPDATE_PRODUCT_GATEWAY:
			return PATHUTILS::path_join(strUpdatemanager, ARCUPDATE_SITE_GATEWAY_UPDATES L"\\" ARCUPDATE_SITE_RELEASE_VERSION BINARY_PATCH_DIR);
		case ARCUPDATE_PRODUCT_SELFUPDATE:
			return PATHUTILS::path_join(strUpdatemanager, ARCUPDATE_SITE_SELF_UPDATES L"\\" ARCUPDATE_SITE_RELEASE_VERSION BINARY_PATCH_DIR);
		case ARCUPDATE_PRODUCT_PATCHUPDATE:
			return PATHUTILS::path_join(strUpdatemanager, ARCUPDATE_SITE_PATCH_UPDATES L"\\" ARCUPDATE_SITE_RELEASE_VERSION BINARY_PATCH_DIR);
		default:
			return L"";
		}
	}
	//added by cliicy.luo

	DWORD GetDefaultUpdateServerInfo(DWORD dwProd, CA_UPDATE_SERVER_INFO& upSvrInfo)
	{
		CDbgLog m_log;
		DWORD dwRet = 0;
		m_log.LogW(LL_INF, 0, L"%s: oooo will get the default update server info by using code 83", __WFUNCTION__);
		upSvrInfo.strPathOnSource = L"";
		upSvrInfo.strServerName = L"";
		upSvrInfo.str_Binary_PathOnSource = L"";

		wstring strUpManager = PRODUTILS::GetUpdateManagerHome();
		wstring strXmlFile = PATHUTILS::path_join(strUpManager, XML_FILE_UPDATE_URLS);
		CXXmlNode* pRoot = CXXmlNode::LoadFromFile(strXmlFile);
		if (!pRoot)
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to load xml file: %s", __WFUNCTION__, strXmlFile.c_str());
			return dwRet;
		}

		wstring strURL = L"";
		CXXmlNode* pProdNode = NULL;
		CXXmlNode* pChild = pRoot->GetNode(XXN_FIRST);
		CXXmlNode* pDefault = NULL;
		while (pChild)
		{
			if (!STRUTILS::same_str(pChild->GetTag(), XML_Product, false))
			{
				pChild = pChild->GetNode(XXN_NEXT);
				continue;
			}

			DWORD dwCode = pChild->GetAttributeDWORD(XML_Code);
			if (dwProd == dwCode)
			{
				pProdNode = pChild;
				break;
			}
			else if (dwCode == 0)
			{
				pDefault = pChild;
			}
			pChild = pChild->GetNode(XXN_NEXT);
		}

		CXXmlNode* pDownloadServer = (pProdNode == NULL) ? pDefault : pProdNode;

		if (!pDownloadServer)
		{
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Download server of produt [%d] was not defined in file [%s]", __WFUNCTION__, dwProd, strXmlFile.c_str());
			SAFE_DELETE(pRoot);
			return (DWORD)-1;
		}

		
		upSvrInfo.strServerName = getChildNodeText(pDownloadServer, XML_DownloadServer, L"");
		upSvrInfo.strPathOnSource = getChildNodeText(pDownloadServer, XML_PathOnSource, L"");
		upSvrInfo.str_Binary_PathOnSource = getChildNodeText(pDownloadServer, XML_PatchBI_OnSrc, L"");//added by cliicy.luo to single binaries updates
				
		upSvrInfo.nPort = _wtoi(getChildNodeText(pDownloadServer, XML_Port, L"80").c_str());
		m_log.LogW(LL_INF, 0, L"%s: oooo load patch binary path: pathonsource=%s binary_onsource=%s", __WFUNCTION__, upSvrInfo.strPathOnSource.c_str(), upSvrInfo.str_Binary_PathOnSource.c_str());
		SAFE_DELETE(pRoot);
		return 0;
	}

	wstring GetUpdateCfgFile()
	{
		wstring strCfgFile = PRODUTILS::GetUpdateManagerHome();
		strCfgFile = PATHUTILS::path_join(strCfgFile, L"Config\\UpdateCfg.ini");
		return strCfgFile;
	}

	wstring GetUpdateSigFile()
	{
		wstring strCfgFile = PRODUTILS::GetUpdateManagerHome();
		strCfgFile = PATHUTILS::path_join(strCfgFile, L"Config\\arcserve.sig");
		return strCfgFile;
	}

	BOOL IgnoreSign()
	{
		int nV = ::GetPrivateProfileInt(L"Debug", L"IgnoreSign", 0, GetUpdateCfgFile().c_str());
		if (nV == 0)
			return FALSE;
		return TRUE;
	}

	int GetDownloadRetryCount()
	{
		int nV = ::GetPrivateProfileInt(L"Settings", L"Retry", 3, GetUpdateCfgFile().c_str());
		if (nV <= 0)
			nV = 3;
		return nV;
	}

	wstring GetUpdateSignature()
	{
		wstring strFile = GetUpdateSigFile();
		if (!PATHUTILS::is_file_exist(strFile))
			return L"";

		CDbgLog m_log;

		wstring strSignature = L"";
		char* pBuffer = NULL;
		HANDLE hFile = ::CreateFile(strFile.c_str(), GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
		do
		{
			if (hFile == INVALID_HANDLE_VALUE)
			{
				m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to open file %s", __WFUNCTION__, strFile.c_str());
				break;
			}

			DWORD dwFileSize = ::GetFileSize(hFile, NULL);
			if (dwFileSize == 0)
			{
				m_log.LogW(LL_ERR, GetLastError(), L"%s: Invalid file %s", __WFUNCTION__, strFile.c_str());
				break;
			}
			DWORD dwBufferSize = dwFileSize + 2;
			pBuffer = new char[dwBufferSize];
			if (!pBuffer)
			{
				m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to allocated buffer with size %d", __WFUNCTION__, dwBufferSize);
				break;
			}
			ZeroMemory(pBuffer, dwBufferSize);

			DWORD dwRead = 0;
			if (!::ReadFile(hFile, pBuffer, dwFileSize, &dwRead, NULL))
			{
				m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to read %d bytes from file %s", __WFUNCTION__, dwFileSize, strFile.c_str());
				break;
			}

			DWORD dwLen = dwFileSize;
			if (ENCUTILS::DecryptData(pBuffer, &dwLen) < 0)
			{
				m_log.LogW(LL_ERR, GetLastError(), L"%s: Fail to decrypt signature from file %s", __WFUNCTION__, strFile.c_str());
				break;
			}

			if (dwLen >= dwFileSize)
			{
				m_log.LogW(LL_ERR, 0, L"%s: dwLen >= dwFileSize", __WFUNCTION__);
				break;
			}

			char* pTemp = pBuffer + dwLen;
			ZeroMemory(pTemp, dwBufferSize - dwLen);
			strSignature = wstring((WCHAR*)pBuffer);
		} while (0);

		if (hFile != INVALID_HANDLE_VALUE)
			CloseHandle(hFile);

		if (pBuffer)
			delete[] pBuffer;

		return strSignature;
	}

	static void StatusXmlNode2UpdateVersion(CXXmlNode* pRoot, UDP_VERSION_INFO& ver)
	{
		//
		// get update version from xml
		//
		CDbgLog log;
		CXXmlNode* pReleaseNode = pRoot->GetNodeByPath(XML_NODEPATH_Release);
		if (!pReleaseNode)
		{
			log.LogW(LL_ERR, 0, L"%s: No release infor defined in xml file", __WFUNCTION__);
			return;
		}

		ver.dwMajor = pReleaseNode->GetAttributeDWORD(XML_MajorVersion);
		ver.dwMinor = pReleaseNode->GetAttributeDWORD(XML_MinorVersion);
		ver.dwServicePack = pReleaseNode->GetAttributeDWORD(XML_ServicePack);

		//
		// get update version number
		//
		CXXmlNode* pUpdateVersion = pRoot->GetNodeByPath(XML_NODEPATH_UpdateVersionNumber);
		if (pUpdateVersion)
			ver.dwUpdate = _wtoi(pUpdateVersion->GetText().c_str());

		//
		// get the update build
		//
		CXXmlNode* pBuild = pRoot->GetNodeByPath(XML_NODEPATH_UpdateBuild);
		if (pBuild)
		{
			wstring strBuild = pBuild->GetText();
			std::vector<wstring> eles;
			STRUTILS::split_str(strBuild, L'.', eles);
			if (eles.size()>0)
				ver.dwBuild = _wtoi(eles[0].c_str());
			if (eles.size()>1)
				ver.dwUpBuild = _wtoi(eles[1].c_str());
		}
	}

	static wstring _GetSelfUpdateStatusFile()
	{
		CDbgLog m_log;
		wstring strFile = PATHUTILS::path_join(GetDownloadHomeDirectory(ARCUPDATE_PRODUCT_SELFUPDATE), XML_FILE_STATUS);
		if (!PATHUTILS::is_file_exist(strFile))
			return strFile;

		//
		// if the update was installed already, set InstallStatus as 1
		//
		CXXmlNode* pRoot = CXXmlNode::LoadFromFile(strFile);
		if (!pRoot)
			return strFile;

		DWORD dwVersion = _wtoi(getChildNodeText(pRoot, XML_UpdateVersionNumber, L"0").c_str());
		wstring strStatus = getChildNodeText(pRoot, XML_InstallStatus, L"1");
		if (STRUTILS::same_str(strStatus, L"1"))
		{
			SAFE_DELETE(pRoot);
			return strFile;
		}

		if (dwVersion <= GetAutoupdateVersion())
		{
			updateChildNode(pRoot, XML_InstallStatus, L"1");
			pRoot->SaveToFile2(strFile);
		}

		SAFE_DELETE(pRoot);
		return strFile;
	}

	static wstring _GetPatchUpdateStatusFile()
	{
		wstring strFile = PATHUTILS::path_join(GetDownloadHomeDirectory(ARCUPDATE_PRODUCT_PATCHUPDATE), XML_FILE_STATUS);
		return strFile;
	}


	wstring GetUpdateStatusFile(DWORD dwProd)
	{
		if (dwProd == ARCUPDATE_PRODUCT_SELFUPDATE)
			return _GetSelfUpdateStatusFile();
		else if (dwProd == ARCUPDATE_PRODUCT_PATCHUPDATE)
			return _GetPatchUpdateStatusFile();

		//
		// get UDP Agent / Console status.xml file
		//
		CDbgLog m_log;
		wstring strFile = PATHUTILS::path_join(GetDownloadHomeDirectory(dwProd), XML_FILE_STATUS);
		if (!PATHUTILS::is_file_exist(strFile))
			return strFile;

		//
		// if the update was installed already, set InstallStatus as 1
		//
		CXXmlNode* pRoot = CXXmlNode::LoadFromFile(strFile);
		if (!pRoot)
			return strFile;

		CXXmlNode* pInstallStatus = pRoot->GetNodeByPath(XML_NODEPATH_InstallStatus);
		if (!pInstallStatus || STRUTILS::same_str(pInstallStatus->GetText(), L"1"))
		{
			SAFE_DELETE(pRoot);
			return strFile;
		}

		UDP_VERSION_INFO prodVer;
		if (0 != PRODUTILS::GetProductVersion(dwProd, prodVer))
		{
			SAFE_DELETE(pRoot);
			return strFile;
		}

		UDP_VERSION_INFO updateVer;
		StatusXmlNode2UpdateVersion(pRoot, updateVer);

		CXXmlNode* pUpdateNode = pRoot->GetNodeByPath(XML_NODEPATH_UpdateVersionNumber);

		BOOL bInstallNeeded = TRUE;
		if (updateVer.dwBuild != prodVer.dwBuild ||
			updateVer.dwMajor != prodVer.dwMajor ||
			updateVer.dwMinor != prodVer.dwMinor ||
			updateVer.dwServicePack != prodVer.dwServicePack)
		{
			// if the major build does not match, don't have to install this update.
			bInstallNeeded = FALSE;
		}
		else if (updateVer.dwUpdate < prodVer.dwUpdate)
		{
			// if the update number does not match, don't have to install this update
			bInstallNeeded = FALSE;
		}
		else if (updateVer.dwUpdate == prodVer.dwUpdate &&
			updateVer.dwUpBuild <= prodVer.dwUpBuild)
		{
			// if the update build number is larger than the build number on server. don't have to install this update
			bInstallNeeded = FALSE;
		}

		if (pUpdateNode && !bInstallNeeded)
		{
			pInstallStatus->SetText(L"1");
			pRoot->SaveToFile2(strFile);
		}

		SAFE_DELETE(pRoot);
		return strFile;
	}

	wstring GetUpdateSettingXmlFile(DWORD dwProd, BOOL bCreateIfNotExists/*=FALSE*/)
	{
		wstring strFile = PRODUTILS::GetUpdateManagerHome();
		strFile = PATHUTILS::path_join(strFile, L"Config");
		if (!PATHUTILS::is_folder_exist(strFile))
			::CreateDirectory(strFile.c_str(), NULL);

		wstring strSettingsFile = L"";
		BOOL bCreateDefaultFile = FALSE;
		if (dwProd == ARCUPDATE_PRODUCT_AGENT)
		{
			strSettingsFile = PATHUTILS::path_join(strFile, L"EngineUpdateSettings.xml");
			if (!PATHUTILS::is_file_exist(strSettingsFile) && !PRODUTILS::IsAgentManagedByConsole())
				bCreateDefaultFile = TRUE;
		}
		else if (dwProd == ARCUPDATE_PRODUCT_FULL)
		{
			strSettingsFile = PATHUTILS::path_join(strFile, L"FullUpdateSettings.xml");
			if (!PATHUTILS::is_file_exist(strSettingsFile))
				bCreateDefaultFile = TRUE;
		}
		else if (dwProd == ARCUPDATE_PRODUCT_GATEWAY)
		{
			strSettingsFile = PATHUTILS::path_join(strFile, L"FullUpdateSettings.xml");
			if (!PATHUTILS::is_file_exist(strSettingsFile))
				bCreateDefaultFile = TRUE;
		}

		if (bCreateDefaultFile && bCreateIfNotExists)
		{
			// create the default update settings
			UDP_UPDATE_SETTINGS updateSettings;
			UPUTILS::SaveUpdateSettingsToFile(strSettingsFile, updateSettings);
		}
		return strSettingsFile;
	}

	void ReadUpdateSettingsFromFile(const wstring& strFile, UDP_UPDATE_SETTINGS& updateSettings)
	{
		updateSettings.nServerType = 0;
		updateSettings.scheduler.bDisabled = TRUE;
		updateSettings.scheduler.nDay = 0;
		updateSettings.scheduler.nHour = 0;
		updateSettings.scheduler.nMinute = 0;
		updateSettings.ieProxy.bDefaultIEProxy = true;
		updateSettings.ieProxy.nProxyPort = 80;
		updateSettings.ieProxy.proxyPassword = L"";
		updateSettings.ieProxy.proxyServer = L"";
		updateSettings.ieProxy.proxyUserName = L"";
		updateSettings.vecStagingServers.clear();

		if (!PATHUTILS::is_file_exist(strFile))
			return;

		CXXmlNode* pRoot = CXXmlNode::LoadFromFile(strFile);
		if (!pRoot)
			return;

		CXXmlNode* pChild = pRoot->GetNode(XXN_FIRST);
		while (pChild)
		{
			wstring strTag = pChild->GetTag();
			if (STRUTILS::same_str(strTag, XML_ScheduleType)) //ScheduleType
			{
				if (STRUTILS::same_str(pChild->GetText(), L"true"))
					updateSettings.scheduler.bDisabled = FALSE;
			}
			else if (STRUTILS::same_str(strTag, XML_ScheduledHour)) // ScheduledHour
			{
				updateSettings.scheduler.nHour = _wtoi(pChild->GetText().c_str());
			}
			else if (STRUTILS::same_str(strTag, XML_ScheduledWeekDay)) // ScheduledWeekDay
			{
				updateSettings.scheduler.nDay = _wtoi(pChild->GetText().c_str());
			}
			else if (STRUTILS::same_str(strTag, XML_serverType)) // serverType
			{
				updateSettings.nServerType = _wtoi(pChild->GetText().c_str());
			}
			else if (STRUTILS::same_str(strTag, XML_StagingServers)) // StagingServers
			{
				UDP_STAGING_SVR stageServer;
				CXXmlNode* pT = pChild->GetChildNode(XML_StagingServer);
				if (pT)
					stageServer.strServerName = pT->GetText();

				pT = pChild->GetChildNode(XML_StagingServerPort);
				if (pT)
					stageServer.nPort = _wtoi(pT->GetText().c_str());

				pT = pChild->GetChildNode(XML_serverId);
				if (pT)
					stageServer.nIndex = _wtoi(pT->GetText().c_str());

				if (!stageServer.strServerName.empty())
					updateSettings.vecStagingServers.push_back(stageServer);
			}
			else if (STRUTILS::same_str(strTag, XML_ProxySettings)) // ProxySettings
			{
				CXXmlNode* pT = pChild->GetChildNode(XML_ProxyPassword);
				if (pT)
					ENCUTILS::DecryptFromString(pT->GetText().c_str(), updateSettings.ieProxy.proxyPassword);

				pT = pChild->GetChildNode(XML_ProxyUserName);
				if (pT)
					updateSettings.ieProxy.proxyUserName = pT->GetText();

				pT = pChild->GetChildNode(XML_ProxyServerName);
				if (pT)
					updateSettings.ieProxy.proxyServer = pT->GetText();

				pT = pChild->GetChildNode(XML_ProxyServerPort);
				if (pT)
					updateSettings.ieProxy.nProxyPort = _wtoi(pT->GetText().c_str());

				pT = pChild->GetChildNode(XML_useProxy);
				if (pT)
				{
					if (STRUTILS::same_str(pT->GetText(), L"true"))
						updateSettings.ieProxy.bDefaultIEProxy = FALSE;
					else
						updateSettings.ieProxy.bDefaultIEProxy = TRUE;
				}

				pT = pChild->GetChildNode(XML_proxyRequiresAuth);
				if (pT)
				{
					if (!STRUTILS::same_str(pT->GetText(), L"true"))
						updateSettings.ieProxy.proxyPassword = L"";
				}
			}

			pChild = pChild->GetNode(XXN_NEXT);
		}

		SAFE_DELETE(pRoot);

		std::sort(updateSettings.vecStagingServers.begin(), updateSettings.vecStagingServers.end(), sortStagingServer);
		return;
	}

	DWORD SaveUpdateSettingsToFile(const wstring& strFile, const UDP_UPDATE_SETTINGS& updateSettings)
	{
		CDbgLog logObj;
		logObj.LogW(LL_INF, 0, L"%s: Save update settings to file %s", __WFUNCTION__, strFile.c_str());
		CXXmlNode* pRoot = CXXmlNode::CreateXmlNode(XML_AutoUpdateSettings);

		wstring strText = STRUTILS::fstr(L"%d", updateSettings.nServerType);
		updateChildNode(pRoot, XML_serverType, strText);
		updateChildNode(pRoot, XML_ScheduleType, updateSettings.scheduler.bDisabled ? L"false" : L"true");

		strText = STRUTILS::fstr(L"%d", updateSettings.scheduler.nDay);
		updateChildNode(pRoot, XML_ScheduledWeekDay, strText);

		strText = STRUTILS::fstr(L"%d", updateSettings.scheduler.nHour);
		updateChildNode(pRoot, XML_ScheduledHour, strText);
		updateChildNode(pRoot, XML_iCAServerStatus, L"0");
		updateChildNode(pRoot, XML_backupsConfigured, L"false");

		CXXmlNode* pProxy = updateChildNode(pRoot, XML_ProxySettings, L"");
		updateChildNode(pProxy, XML_useProxy, updateSettings.ieProxy.bDefaultIEProxy ? L"false" : L"true");

		strText = STRUTILS::fstr(L"%d", updateSettings.ieProxy.nProxyPort);
		updateChildNode(pProxy, XML_ProxyServerPort, strText);
		updateChildNode(pProxy, XML_proxyRequiresAuth, updateSettings.ieProxy.proxyPassword.empty() ? L"false" : L"true");
		updateChildNode(pProxy, XML_ProxyServerName, updateSettings.ieProxy.proxyServer);
		ENCUTILS::EncryptToString(L"", strText);
		updateChildNode(pProxy, XML_ProxyPassword, strText);
		updateChildNode(pProxy, XML_ProxyUserName, updateSettings.ieProxy.proxyUserName);

		
		for (size_t i = 0; i < updateSettings.vecStagingServers.size(); i++){
			CXXmlNode* pStageServer = updateChildNode(pRoot, XML_StagingServers, L"");
			strText = STRUTILS::fstr(L"%d", i);
			updateChildNode(pStageServer, XML_serverId, strText);
			updateChildNode(pStageServer, XML_StagingServer, updateSettings.vecStagingServers[i].strServerName);
			strText = STRUTILS::fstr(L"%d", updateSettings.vecStagingServers[i].nPort);
			updateChildNode(pStageServer, XML_StagingServerPort, strText);
			updateChildNode(pStageServer, XML_stagingServerStatus, L"0");
		}

		DWORD dwRet = pRoot->SaveToFile2(strFile);
		if (dwRet != 0)
			logObj.LogW(LL_ERR, dwRet, L"%s: Failed to save update settings to file %s", __WFUNCTION__, strFile.c_str());
		SAFE_DELETE(pRoot);
		return dwRet;
	}

	wstring GetProductName(DWORD dwProd)
	{
		switch (dwProd)
		{
		case ARCUPDATE_PRODUCT_AGENT:
			return GetUpdateResourceString(ARCUPDATE_PRODUCT_NAME_ENGINE);
		case ARCUPDATE_PRODUCT_FULL:
			return GetUpdateResourceString(ARCUPDATE_PRODUCT_NAME_FULL);
		case ARCUPDATE_PRODUCT_GATEWAY:
			return GetUpdateResourceString(ARCUPDATE_PRODUCT_NAME_GATEWAY);
		case ARCUPDATE_PRODUCT_SELFUPDATE:
			return GetUpdateResourceString(ARCUPDATE_PRODUCT_NAME_UPDATE_MANAGER);
		case ARCUPDATE_PRODUCT_PATCHUPDATE:
			return GetUpdateResourceString(ARCUPDATE_PRODUCT_NAME_UDP_PATCH);
		default:
			return L"";
		}
	}

	DWORD GetAdminUserOfProduct(DWORD dwProduct, wstring& strUser, wstring& strPassword)
	{
		CDbgLog logObj;
		LONG lRet = 0;
		CSysInfo sys;
		REGSAM sam = KEY_READ;
		if (sys.IsWow64())
			sam |= KEY_WOW64_64KEY;
		CRegistry reg;
		char buf[514] = { 0 };
		do
		{
			WCHAR szRegHome[MAX_PATH] = { 0 };
			if (dwProduct == ARCUPDATE_PRODUCT_FULL)
			{
				GetRegRootPathByProduct(PRODUCT_CENTRAL, szRegHome, _ARRAYSIZE(szRegHome));
				if (wcslen(szRegHome) == 0)
				{
					logObj.LogW(LL_ERR, GetLastError(), L"%s: Failed to get reigisty root of product %d", __WFUNCTION__, dwProduct);
					return (DWORD)-1;
				}
			}
			else if (dwProduct == ARCUPDATE_PRODUCT_GATEWAY)
			{
				GetRegRootPathByProduct(PRODUCT_CENTRAL, szRegHome, _ARRAYSIZE(szRegHome));
				if (wcslen(szRegHome) == 0)
				{
					logObj.LogW(LL_ERR, GetLastError(), L"%s: Failed to get reigisty root of product %d", __WFUNCTION__, dwProduct);
					return (DWORD)-1;
				}
			}
			else
			{
				GetRegRootPathByProduct(PRODUCT_D2D, szRegHome, _ARRAYSIZE(szRegHome));
				if (wcslen(szRegHome) == 0)
				{
					logObj.LogW(LL_ERR, GetLastError(), L"%s: Failed to get reigisty root of product %d", __WFUNCTION__, dwProduct);
					return (DWORD)-1;
				}
			}

			lRet = reg.Open(szRegHome, HKEY_LOCAL_MACHINE, sam);
			if (lRet)
			{
				logObj.LogW(LL_ERR, lRet, L"%s: Cannot open[%s]", __WFUNCTION__, szRegHome);
				return lRet;
			}

			lRet = reg.QueryStringValue(L"AdminUser", strUser);
			if (lRet)
			{
				logObj.LogW(LL_ERR, lRet, L"%s: Fail to get user from registry[%s]", __WFUNCTION__, szRegHome);
				break;
			}

			DWORD dwType = REG_BINARY;
			DWORD dwLen = sizeof(buf);
			lRet = reg.QueryValue(L"AdminPassword", &dwType, buf, &dwLen);
			if (lRet || 0 == dwLen)
			{
				logObj.LogW(LL_ERR, lRet, L"%s: Fail to get encryped password from registry %s", __WFUNCTION__, szRegHome);
				strPassword = L"";
				lRet = 0;
				break;
			}
			else
			{

				if (ENCUTILS::DecryptData(buf, &dwLen) <0)
				{
					lRet = ERROR_BAD_ENVIRONMENT;
					logObj.LogW(LL_ERR, lRet, L"%s: Fail to Decrypt password", __WFUNCTION__);
					break;
				}

				// maoja01. Fortify scan.
				if (dwLen >= _countof(buf))
				{
					lRet = ERROR_BAD_ENVIRONMENT;
					logObj.LogW(LL_ERR, lRet, L"%s: dwLen >= _countof(buf)", __WFUNCTION__);
					break;
				}
				buf[dwLen] = 0;
				wchar_t wbuf[257] = { 0 };
				memcpy_s(wbuf, sizeof(wbuf), buf, dwLen);
				strPassword = wbuf;
			}

		} while (0);

		reg.Close();

		return lRet;
	}
	
	wstring	GetUpdateResourceString(DWORD dwMsgID, ...)
	{
		va_list pArgList = NULL;
		va_start(pArgList, dwMsgID);
		wstring strMsg = GetUpdateResourceStringEx(dwMsgID, &pArgList);
		va_end(pArgList);
		return strMsg;
	}

	wstring GetUpdateResourceStringEx(DWORD dwMsgID, va_list* pArgList/*=NULL*/)
	{
		wstring strResDll = PRODUTILS::GetUpdateManagerHome();
		strResDll = PATHUTILS::path_join(strResDll, L"UpdateRes.dll");
		HMODULE hResDll = ::LoadLibrary(strResDll.c_str());
		if (hResDll == NULL)
			return L"";

		wstring strRet = L"";
		LPWSTR pszMessage = NULL;
		::FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_HMODULE,
			hResDll,
			dwMsgID,
			get_language_id(),
			(LPWSTR)&pszMessage,
			0,
			pArgList);
		if (pszMessage)
		{
			strRet = pszMessage;
			::LocalFree(pszMessage);
		}
		::FreeLibrary(hResDll);
		return strRet;
	}
	
	DWORD GetLastAvailableSelfUpdate( DWORD& dwVersion, wstring& strFilePath)
	{
		CDbgLog logObj;
		wstring strStatusFile = GetUpdateStatusFile(ARCUPDATE_PRODUCT_SELFUPDATE);
		if (!PATHUTILS::is_file_exist(strStatusFile))
			return ERROR_FILE_NOT_FOUND;

		strFilePath = L"";
		DWORD dwRet = 0;
		CXXmlNode* pRoot = NULL;
		do
		{
			pRoot = CXXmlNode::LoadFromFile(strStatusFile);
			if (!pRoot)
			{
				logObj.LogW(LL_ERR, GetLastError(), L"%s: Failed to load file %s", __WFUNCTION__, strStatusFile.c_str());
				dwRet = ERROR_BAD_FORMAT;
			}

			wstring strVersion = getChildNodeText(pRoot, XML_UpdateVersionNumber, L"0");
			dwVersion = _wtoi(strVersion.c_str());
			strFilePath = getChildNodeText(pRoot, XML_Downloadedlocation, L"");
		} while (0);
		SAFE_DELETE(pRoot);
		return dwRet;
	}

	DWORD GetLastAvailableUpdateOfProduct(DWORD dwProduct, UDP_VERSION_INFO& version, wstring& strFilePath, PBOOL pbRebootRequired/*=NULL*/)
	{
		CDbgLog logObj;

		wstring strStatusFile = GetUpdateStatusFile(dwProduct);
		if (!PATHUTILS::is_file_exist(strStatusFile))
			return ERROR_FILE_NOT_FOUND;

		strFilePath = L"";
		DWORD dwRet = 0;
		CXXmlNode* pRoot = NULL;
		do
		{
			pRoot = CXXmlNode::LoadFromFile(strStatusFile);
			if (!pRoot)
			{
				logObj.LogW(LL_ERR, GetLastError(), L"%s: Failed to load file %s", __WFUNCTION__, strStatusFile.c_str());
				dwRet = ERROR_BAD_FORMAT;
			}

			CXXmlNode* pReleaseNode = pRoot->GetNodeByPath(XML_NODEPATH_Release);
			if (!pReleaseNode)
			{
				logObj.LogW(LL_ERR, 0, L"%s: No release infor defined in available update infor", __WFUNCTION__);
				dwRet = ERROR_BAD_FORMAT;
				break;
			}

			version.dwMajor = pReleaseNode->GetAttributeDWORD(XML_MajorVersion);
			version.dwMinor = pReleaseNode->GetAttributeDWORD(XML_MinorVersion);
			version.dwServicePack = pReleaseNode->GetAttributeDWORD(XML_ServicePack);

			//
			// get update version number
			//
			CXXmlNode* pUpdateVersion = pRoot->GetNodeByPath(XML_NODEPATH_UpdateVersionNumber);
			if (pUpdateVersion)
				version.dwUpdate = _wtoi(pUpdateVersion->GetText().c_str());

			//
			// get the update build
			//
			CXXmlNode* pBuild = pRoot->GetNodeByPath(XML_NODEPATH_UpdateBuild);
			if (pBuild)
			{
				wstring strBuild = pBuild->GetText();
				std::vector<wstring> eles;
				STRUTILS::split_str(strBuild, L'.', eles);
				if (eles.size() > 0)
					version.dwBuild = _wtoi(eles[0].c_str());
				if (eles.size() > 1)
					version.dwUpBuild = _wtoi(eles[1].c_str());
			}

			CXXmlNode* pNode = pRoot->GetNodeByPath(XML_NODEPATH_Downloadedlocation);
			strFilePath = L"";
			if (pNode)
				strFilePath = pNode->GetText();

			if (pbRebootRequired)
			{
				CXXmlNode* pNodeReboot = pRoot->GetNodeByPath(XML_NODEPATH_RebootRequired);
				if (pNodeReboot)
					(*pbRebootRequired) = STRUTILS::str2boolean(pNodeReboot->GetText(), FALSE);
				else
					(*pbRebootRequired) = FALSE;
			}
		} while (0);
		SAFE_DELETE(pRoot);

		if (strFilePath.empty())
		{
			logObj.LogW(LL_ERR, 0, L"%s: The downloaded file location was not defined. So delete file %s", __WFUNCTION__, strStatusFile.c_str());
			::DeleteFile(strStatusFile.c_str());
			dwRet = ERROR_FILE_NOT_FOUND;
		}

		if (!PATHUTILS::is_file_exist(strFilePath))
		{
			logObj.LogW(LL_ERR, 0, L"%s: The downloaded file %s was not found. So delete file %s", __WFUNCTION__, strFilePath.c_str(), strStatusFile.c_str());
			::DeleteFile(strStatusFile.c_str());
			dwRet = ERROR_FILE_NOT_FOUND;
		}

		return dwRet;
	}

	int CompareUDPVersion(const UDP_VERSION_INFO& v1, const UDP_VERSION_INFO& v2)
	{
		if (v1.dwMajor > v2.dwMajor)
			return 1;
		if (v1.dwMajor < v2.dwMajor)
			return -1;

		if (v1.dwMinor> v2.dwMinor)
			return 1;
		if (v1.dwMinor < v2.dwMinor)
			return -1;

		if (v1.dwServicePack > v2.dwServicePack)
			return 1;
		if (v1.dwServicePack < v2.dwServicePack)
			return -1;

		if (v1.dwBuild > v2.dwBuild)
			return 1;
		if (v1.dwBuild < v2.dwBuild)
			return -1;

		if (v1.dwUpdate > v2.dwUpdate)
			return 1;
		if (v1.dwUpdate < v2.dwUpdate)
			return -1;

		if (v1.dwUpBuild > v2.dwUpBuild)
			return 1;
		if (v1.dwUpBuild < v2.dwUpBuild)
			return -1;
		return 0;
	}

	DWORD GetAutoupdateVersion()
	{
		return ::GetPrivateProfileInt(L"Settings", L"AutoUpdateVersion", 0, UPUTILS::GetUpdateCfgFile().c_str());
	}

	DWORD GetUpdateInternalInfo(DWORD& dwUpdate, DWORD& dwBuild, DWORD& dwPatch, DWORD& dwSvrRole)
	{
		dwUpdate = 0;
		dwBuild = 0;
		dwSvrRole = 0;
		dwPatch = GetAutoupdateVersion();

		UDP_VERSION_INFO udpVer;
		DWORD dwRet = PRODUTILS::GetProductVersion(ARCUPDATE_PRODUCT_AGENT, udpVer);
		if (dwRet == 0)
		{
			dwSvrRole |= 1;
			dwUpdate = udpVer.dwUpdate;
			dwBuild = udpVer.dwUpBuild;
		}

		dwRet = PRODUTILS::GetProductVersion(ARCUPDATE_PRODUCT_FULL, udpVer);
		if (dwRet == 0)
		{
			dwSvrRole |= 2;
			dwUpdate = udpVer.dwUpdate;
			dwBuild = udpVer.dwUpBuild;
		}

		return 0;
	}

	wstring GetURLOfFileOnServer(DWORD dwProdCode, ARCUPDATE_SERVER_INFO* pSvrInf, const wstring& strFile, const wstring& strPathDir)
	{
		CDbgLog logObj;
		wstring strBaseURL = L"";
		if (pSvrInf->serverType == ARCUPDATE_SERVER_STAGE)
		{
			logObj.LogW(LL_INF, 0, L"%s: enter   %s", __WFUNCTION__, L"Stage server");
			if (dwProdCode == ARCUPDATE_PRODUCT_AGENT)
				strBaseURL = STRUTILS::construct_url(L"%s/%s/%s/%s", ARCUPDATE_SITE_ROOT, ARCUPDATE_SITE_ENGINE_UPDATES, strPathDir.c_str(), strFile.c_str());
			else if (dwProdCode == ARCUPDATE_PRODUCT_FULL)
				strBaseURL = STRUTILS::construct_url(L"%s/%s/%s/%s", ARCUPDATE_SITE_ROOT, ARCUPDATE_SITE_FULL_UPDATES, strPathDir.c_str(), strFile.c_str());
			else if (dwProdCode == ARCUPDATE_PRODUCT_GATEWAY)
				strBaseURL = STRUTILS::construct_url(L"%s/%s/%s/%s", ARCUPDATE_SITE_ROOT, ARCUPDATE_SITE_GATEWAY_UPDATES, strPathDir.c_str(), strFile.c_str());
			else if (dwProdCode == ARCUPDATE_PRODUCT_SELFUPDATE)
				strBaseURL = STRUTILS::construct_url(L"%s/%s/%s/%s", ARCUPDATE_SITE_ROOT, ARCUPDATE_SITE_SELF_UPDATES, strPathDir.c_str(), strFile.c_str());
			else if (dwProdCode == ARCUPDATE_PRODUCT_PATCHUPDATE)
				strBaseURL = STRUTILS::construct_url(L"%s/%s/%s/%s", ARCUPDATE_SITE_ROOT, ARCUPDATE_SITE_PATCH_UPDATES, strPathDir.c_str(), strFile.c_str());
		}
		else
		{
			logObj.LogW(LL_INF, 0, L"%s: enter   %s", __WFUNCTION__, L"no_Stage server");
			DWORD dwUpdate = 0; DWORD dwBuild = 0; DWORD dwPatch = 0; DWORD dwSvrRoles = 0;
			UPUTILS::GetUpdateInternalInfo(dwUpdate, dwBuild, dwPatch, dwSvrRoles);
			CA_UPDATE_SERVER_INFO caUpServer;
			UPUTILS::GetDefaultUpdateServerInfo(dwProdCode, caUpServer);
			strBaseURL = STRUTILS::construct_url(L"%s/%s&code=%d&update=%d&build=%d&patch=%d&svrrole=%d",
				caUpServer.strPathOnSource.c_str(), strFile.c_str(), dwProdCode, dwUpdate, dwBuild, dwPatch, dwSvrRoles);
		}
		//wcsncpy_s(pSvrInf->downloadPath, _countof(pSvrInf->downloadPath), strBaseURL.c_str(), _TRUNCATE);

		logObj.LogW(LL_INF, 0, L"%s: left server type:%d url=%s ", __WFUNCTION__, pSvrInf->serverType, strBaseURL.c_str());

		return strBaseURL;
	}



	wstring	VersionToString(const UDP_VERSION_INFO& vi)
	{
		return STRUTILS::fstr(L"%d.%d.%d.%d.%d.%d", vi.dwMajor, vi.dwMinor, vi.dwServicePack, vi.dwBuild, vi.dwUpdate, vi.dwUpBuild);
	}

	void VersionFromString(const wstring& strVersion, UDP_VERSION_INFO& vi)
	{
		ZeroMemory(&vi, sizeof(vi));
		vector<wstring> eles;
		STRUTILS::split_str(strVersion, L'.', eles);
		if (eles.size() > 0)
			vi.dwMajor = _wtoi(eles[0].c_str());
		if (eles.size() > 1)
			vi.dwMinor = _wtoi(eles[1].c_str());
		if (eles.size() > 2)
			vi.dwServicePack = _wtoi(eles[2].c_str());
		if (eles.size() > 3)
			vi.dwBuild = _wtoi(eles[3].c_str());
		if (eles.size() > 4)
			vi.dwUpdate = _wtoi(eles[4].c_str());
		if (eles.size() > 5)
			vi.dwUpBuild = _wtoi(eles[5].c_str());
	}

	//added by cliicy.luo
	wstring GetBIUpdateStatusFile(DWORD dwProd)
	{
		if (dwProd == ARCUPDATE_PRODUCT_SELFUPDATE)
			return _GetSelfUpdateStatusFile();
		else if (dwProd == ARCUPDATE_PRODUCT_PATCHUPDATE)
			return _GetPatchUpdateStatusFile();

		//
		// get UDP Agent / Console status.xml file
		//
		CDbgLog m_log;
		wstring strFile = PATHUTILS::path_join(GetBIDownloadHomeDirectory(dwProd), XML_FILE_STATUS);
		m_log.LogW(LL_INF, 0, L"%s: get status file=%s ", __WFUNCTION__, strFile.c_str());

		if (!PATHUTILS::is_file_exist(strFile))
			return strFile;

		//
		// if the update was installed already, set InstallStatus as 1
		//
		CXXmlNode* pRoot = CXXmlNode::LoadFromFile(strFile);
		if (!pRoot)
			return strFile;

		CXXmlNode* pInstallStatus = pRoot->GetNodeByPath(XML_NODEPATH_InstallStatus);
		if (!pInstallStatus || STRUTILS::same_str(pInstallStatus->GetText(), L"1"))
		{
			SAFE_DELETE(pRoot);
			return strFile;
		}

		UDP_VERSION_INFO prodVer;
		if (0 != PRODUTILS::GetProductVersion(dwProd, prodVer))
		{
			SAFE_DELETE(pRoot);
			return strFile;
		}

		UDP_VERSION_INFO updateVer;
		StatusXmlNode2UpdateVersion(pRoot, updateVer);

		CXXmlNode* pUpdateNode = pRoot->GetNodeByPath(XML_NODEPATH_UpdateVersionNumber);

		BOOL bInstallNeeded = TRUE;
		if (updateVer.dwBuild != prodVer.dwBuild ||
			updateVer.dwMajor != prodVer.dwMajor ||
			updateVer.dwMinor != prodVer.dwMinor ||
			updateVer.dwServicePack != prodVer.dwServicePack)
		{
			// if the major build does not match, don't have to install this update.
			bInstallNeeded = FALSE;
		}
		else if (updateVer.dwUpdate < prodVer.dwUpdate)
		{
			// if the update number does not match, don't have to install this update
			bInstallNeeded = FALSE;
		}
		else if (updateVer.dwUpdate == prodVer.dwUpdate &&
			updateVer.dwUpBuild <= prodVer.dwUpBuild)
		{
			// if the update build number is larger than the build number on server. don't have to install this update
			bInstallNeeded = FALSE;
		}

		if (pUpdateNode && !bInstallNeeded)
		{
			pInstallStatus->SetText(L"1");
			pRoot->SaveToFile2(strFile);
		}

		SAFE_DELETE(pRoot);
		return strFile;
	}
	//added by cliicy.luo
}