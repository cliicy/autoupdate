#pragma once
#include "stdafx.h"
#include "AvailableBIUpdateDll.h"
#include <algorithm>
#include "..\include\UpdateError.h"
#include "DRCommonlib.h"

#define IDR_BIPATCHXML1                 109

static bool sortByOrder(PUP_POST_ACTION p1, PUP_POST_ACTION p2)
{
	if (!p1 && !p2) return true;
	if (p1 && !p2)	return false;
	if (!p1 && p2)	return true;
	return p1->dwOrder < p2->dwOrder;
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

static bool replaceMacrosOfPath(wstring& strPath)
{
	WCHAR szNewPath[1024] = { 0 };
	ExpandEnvironmentStrings(strPath.c_str(), szNewPath, _countof(szNewPath));
	strPath = wstring(szNewPath);
	return true;
}

static PUP_FILE_INFO xmlNodeToFileInfo(CXXmlNode* pNode)
{
	if (!pNode)
		return NULL;

	PUP_FILE_INFO pFi = new UP_FILE_INFO;
	pFi->dwFlag = pNode->GetAttributeDWORD(XML_Flags);
	pFi->strSrcURLOfFile = getChildNodeText(pNode, XML_SrcFileURL, L"");
	pFi->strDstFileName = getChildNodeText(pNode, XML_DstFilePath, L"");
	pFi->strMd5OfFile = getChildNodeText(pNode, XML_Checksum, L"");
	pFi->ullFileSize = _wtoi64(getChildNodeText(pNode, XML_Size, L"0").c_str());

	replaceMacrosOfPath(pFi->strDstFileName);
	return pFi;
}

static DWORD extractXmlFromDll(const wstring& strDllFile, const wstring& strXmlFile)
{
	CDbgLog m_log;

	if (!PATHUTILS::is_file_exist(strDllFile)){
		m_log.LogW(LL_ERR, 0, L"%s: Did not find file %s", __WFUNCTION__, strDllFile.c_str());
		return ERROR_FILE_NOT_FOUND;
	}

	DWORD dwRet = 0;
	HANDLE	hLocalXmlFile = INVALID_HANDLE_VALUE;
	hLocalXmlFile = ::CreateFile(strXmlFile.c_str(),
		GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE,
		NULL, CREATE_ALWAYS, FILE_FLAG_BACKUP_SEMANTICS, NULL);
	if (hLocalXmlFile == INVALID_HANDLE_VALUE)
	{
		dwRet = GetLastError();
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to create file: %s", __WFUNCTION__, strXmlFile.c_str());
		return dwRet;
	}
	//
	// extract the XML file from resource module
	//
	HMODULE hDLL = NULL;
	HGLOBAL hResData = NULL;
	do
	{
		hDLL = ::LoadLibrary(strDllFile.c_str());
		if (hDLL == NULL)
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to load module: %s", __WFUNCTION__, strDllFile.c_str());
			break;
		}

		HRSRC hXMLRes = ::FindResource(hDLL, MAKEINTRESOURCE(IDR_BIPATCHXML1), L"BIPATCHXML");
		if (hXMLRes == NULL)
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to extract XML from file: %s", __WFUNCTION__, strDllFile.c_str());
			break;
		}

		hResData = ::LoadResource(hDLL, hXMLRes);
		if (hResData == NULL)
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to LoadResource from file: %s", __WFUNCTION__, strDllFile.c_str());
			break;
		}

		LPVOID pData = ::LockResource(hResData);
		if (!pData)
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to LockResource from file: %s", __WFUNCTION__, strDllFile.c_str());
			break;
		}

		DWORD dwSize = ::SizeofResource(hDLL, hXMLRes);
		if (dwSize == 0)
		{
			dwRet = (DWORD)-1;
			m_log.LogW(LL_ERR, dwRet, L"%s: No XML resource 101 in file: %s", __WFUNCTION__, strDllFile.c_str());
			break;
		}

		DWORD dwWrote = 0;
		if (!::WriteFile(hLocalXmlFile, pData, dwSize, &dwWrote, NULL))
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to write file: %s", __WFUNCTION__, strDllFile.c_str());
			break;
		}

		if (hLocalXmlFile != INVALID_HANDLE_VALUE)
			::CloseHandle(hLocalXmlFile);
		hLocalXmlFile = INVALID_HANDLE_VALUE;
	} while (0);

	if (hLocalXmlFile != INVALID_HANDLE_VALUE)
	{
		::CloseHandle(hLocalXmlFile);
		hLocalXmlFile = INVALID_HANDLE_VALUE;
	}

	if (hResData != NULL)
	{
		::FreeResource(hResData);
		hResData = NULL;
	}

	if (hDLL != NULL)
	{
		::FreeLibrary(hDLL);
		hDLL = NULL;
	}

	return dwRet;
}

static PUP_POST_ACTION xmlNodeToActionItem(CXXmlNode* pNode)
{
	if (!pNode)
		return NULL;
	PUP_POST_ACTION pAi = new UP_POST_ACTION();
	if (STRUTILS::same_str(pNode->GetAttribute(XML_Sync), L"1"))
		pAi->bSync = TRUE;
	pAi->dwOrder = pNode->GetAttributeDWORD(XML_Order, 0);
	pAi->strCommand = getChildNodeText(pNode, XML_Command, L"");
	pAi->strWorkingDir = getChildNodeText(pNode, XML_WorkingDir, L"");
	replaceMacrosOfPath(pAi->strCommand);
	replaceMacrosOfPath(pAi->strWorkingDir);
	return pAi;
}

static wstring getLanguageName()
{
	switch (::GetSystemDefaultLangID())
	{
	case 0x404:  return L"CHT"; //cht
	case 0x804:  return L"CHS"; //chs
	case 0x40c:  return L"FRN"; //frn
	case 0x407:	 return L"GRM"; //grm
	case 0x410:	 return L"ITA"; //ita
	case 0x411:  return L"JPN"; //jpn
	case 0xc0a:  return L"SPA"; //spa
	case 0x416:  return L"PRB"; //prb
	default:	 return L"ENU"; //enu
	}
}

wstring BIgetDescNodeText()
{
	wstring sNodeText = L"Desc";
	LANGID langID = GetUILanguage(0);

	BYTE primaryLang = PRIMARYLANGID(langID);
	switch (primaryLang)
	{
	case LANG_CHINESE:
	{
		BYTE subLang = SUBLANGID(langID);
		if (SUBLANG_CHINESE_SIMPLIFIED == subLang)
		{
			sNodeText = L"Desc_CHS";
		}
		else
		{
			sNodeText = L"Desc_CHT";
		}
	}
	break;

	case LANG_FRENCH:
		sNodeText = L"Desc_FRN";
		break;

	case LANG_GERMAN:
		sNodeText = L"Desc_GRM";
		break;

	case LANG_ITALIAN:
		sNodeText = L"Desc_ITA";
		break;

	case LANG_JAPANESE:
		sNodeText = L"Desc_JPN";
		break;

	case LANG_PORTUGUESE:
		sNodeText = L"Desc_PRB";
		break;

	case LANG_SPANISH:
		sNodeText = L"Desc_SPA";
		break;
	}

	return sNodeText;
}

wstring BIgetUpdateURLNodeText()
{
	wstring sNodeText = L"UpdateURL";
	LANGID langID = GetUILanguage(0);

	BYTE primaryLang = PRIMARYLANGID(langID);
	switch (primaryLang)
	{
	case LANG_CHINESE:
	{
		BYTE subLang = SUBLANGID(langID);
		if (SUBLANG_CHINESE_SIMPLIFIED == subLang)
		{
			sNodeText = L"UpdateURL_CHS";
		}
		else
		{
			sNodeText = L"UpdateURL_CHT";
		}
	}
	break;

	case LANG_FRENCH:
		sNodeText = L"UpdateURL_FRN";
		break;

	case LANG_GERMAN:
		sNodeText = L"UpdateURL_GRM";
		break;

	case LANG_ITALIAN:
		sNodeText = L"UpdateURL_ITA";
		break;

	case LANG_JAPANESE:
		sNodeText = L"UpdateURL_JPN";
		break;

	case LANG_PORTUGUESE:
		sNodeText = L"UpdateURL_PRB";
		break;

	case LANG_SPANISH:
		sNodeText = L"UpdateURL_SPA";
		break;
	}

	return sNodeText;
}

// ----------------------------------------------------------------------------------------------
// function: GenerateBIUpdateStatusXMLFile
// desc : Generate 'status.xml' from given 'CAvailableBIUpdateDll.dll'
// ----------------------------------------------------------------------------------------------
DWORD GenerateBIUpdateStatusXMLFile(DWORD dwProduct, const wstring& strAvailableUpdateInfoDLL, const wstring& strTargetXMLFile)
{
	CDbgLog	logObj;
	DWORD dwRet = extractXmlFromDll(strAvailableUpdateInfoDLL, strTargetXMLFile);
	if (dwRet != 0){
		logObj.LogW(LL_ERR, dwRet, L"%s: Failed to extract XML from DLL: %s", __WFUNCTION__, strAvailableUpdateInfoDLL.c_str());
		return dwRet;
	}

	CXXmlNode* pUpdateInfo = CXXmlNode::LoadFromFile(strTargetXMLFile);
	if (!pUpdateInfo){
		logObj.LogW(LL_ERR, GetLastError(), L"%s: Failed to load xml file", __WFUNCTION__, strTargetXMLFile.c_str());
		return 87;
	}

	CXXmlNode* pStatusXml = NULL;
	do
	{
		pStatusXml = CXXmlNode::CreateXmlNode(XML_Product);
		pStatusXml->SetAttribute(XML_Name, L"1000");
		pStatusXml->SetAttribute(XML_SchemaVersion, L"1.0");

		//
		// set version info
		//
		CXXmlNode* pRelease = updateChildNode(pStatusXml, XML_Release, L"");
		wstring strVersion = getChildNodeText(pUpdateInfo, XML_Version, L"");
		UDP_VERSION_INFO ver; ZeroMemory(&ver, sizeof(ver));
		UPUTILS::VersionFromString(strVersion, ver);
		pRelease->SetAttributeDWORD(XML_MajorVersion, ver.dwMajor);
		pRelease->SetAttributeDWORD(XML_MinorVersion, ver.dwMinor);
		pRelease->SetAttributeDWORD(XML_ServicePack, ver.dwServicePack);
		pRelease->SetAttributeDWORD(XML_Code, dwProduct);
		wstring strDownloadedLocaltion = UPUTILS::GetBIDownloadHomeDirectory(dwProduct);

		//
		// set description and update readme
		//
		CXXmlNode* pDesc = pUpdateInfo->GetChildNode(XML_Desc);
		CXXmlNode* pReadme = pUpdateInfo->GetChildNode(XML_ReleaseNotes);
		CXXmlNode* pUpdateURL = pUpdateInfo->GetChildNode(XML_UpdateURL);

		wstring sDescFlag, sUpdateURLFlag;
		sDescFlag = BIgetDescNodeText();
		sUpdateURLFlag = BIgetUpdateURLNodeText();
		//
		// set update file and path...
		//
		CXXmlNode* pFilesNode = pUpdateInfo->GetChildNode(XML_UpdateFiles);
		CXXmlNode* pNode = NULL;
		if (pFilesNode)
		{

			pNode = pFilesNode->GetNode(XXN_FIRST);
			CXXmlNode* pPackage = NULL;
			int iNode = 0;
			while (pNode)
			{

				//add single node information by cliicy.luo

				wstring sPackageFlag = STRUTILS::fstr(L"%s%d", XML_Package, iNode);
				pPackage = updateChildNode(pRelease, sPackageFlag, L"");
				pPackage->SetAttribute(XML_Id, getChildNodeText(pNode, XML_Id, L""));
				pPackage->SetAttribute(XML_PublishedDate, getChildNodeText(pUpdateInfo, XML_PublishedDate, L""));

				//
				// set description and update readme
				//
				updateChildNode(pPackage, XML_Update, getChildNodeText(pNode, XML_DstFilePath, L""));
				ULONGLONG ullSizeInBytes = _wtoi64(getChildNodeText(pNode, XML_Size, L"0").c_str());
				ULONGLONG ullSizeInKB = ullSizeInBytes >> 10;
				updateChildNode(pPackage, XML_Size, STRUTILS::fstr(L"%I64d", ullSizeInKB));
				updateChildNode(pPackage, XML_Checksum, getChildNodeText(pNode, XML_Checksum, L""));
				
				wstring strNodeDownloadedLocaltion = PATHUTILS::path_join(strDownloadedLocaltion, getChildNodeText(pNode, XML_DstFilePath, L""));

				updateChildNode(pPackage, XML_Downloadedlocation, strNodeDownloadedLocaltion);

				updateChildNode(pPackage, XML_DownloadedOn, L"");
				updateChildNode(pPackage, XML_DownloadStatus, L"1");
				updateChildNode(pPackage, XML_AvailableStatus, L"1");
				updateChildNode(pPackage, XML_Path, L"");
				updateChildNode(pPackage, XML_UpdateBuild, STRUTILS::fstr(L"%d.%d", ver.dwBuild, ver.dwUpBuild));
				updateChildNode(pPackage, XML_UpdateVersionNumber, STRUTILS::fstr(L"%d", ver.dwUpdate));

				wstring strRebootRequired = getChildNodeText(pUpdateInfo, XML_RebootRequired, L"No");
				BOOL bReboot = STRUTILS::str2boolean(strRebootRequired, false);
				updateChildNode(pPackage, XML_RebootRequired, bReboot ? L"1" : L"0");
				updateChildNode(pPackage, XML_LastRebootableUpdateVersion, getChildNodeText(pUpdateInfo, XML_LastRebootableUpdateVersion, L"0"));
				updateChildNode(pPackage, XML_RequiredVersionOfAutoUpdate, getChildNodeText(pUpdateInfo, XML_RequiredVersionOfAutoUpdate, L"0"));

				int nInstallStatus = 1;
				UDP_VERSION_INFO localVer; ZeroMemory(&localVer, sizeof(localVer));
				if (UPUTILS::CompareUDPVersion(localVer, ver) < 0)
					updateChildNode(pPackage, XML_InstallStatus, L"-1");
				else
					updateChildNode(pPackage, XML_InstallStatus, L"1");

				updateChildNode(pPackage, XML_Dependency, getChildNodeText(pNode, XML_Dependency, L""));

				if (pDesc)
					updateChildNode(pPackage, XML_Desc, getChildNodeText(pUpdateInfo, sDescFlag, L""));

				if (pUpdateURL)
					updateChildNode(pPackage, XML_UpdateURL, getChildNodeText(pUpdateInfo, sUpdateURLFlag, L""));

				//add single node information by cliicy.luo
				iNode++;
				pNode = pNode->GetNode(XXN_NEXT);
			}
		}

		dwRet = pStatusXml->SaveToFile2(strTargetXMLFile);
		if (dwRet != 0){
			logObj.LogW(LL_ERR, dwRet, L"%s: Failed to save xml file %s", __WFUNCTION__, strTargetXMLFile.c_str());
		}
	} while (0);
	SAFE_DELETE(pUpdateInfo);
	SAFE_DELETE(pStatusXml);

	if (dwRet != 0)
		::DeleteFile(strTargetXMLFile.c_str());
	return dwRet;

}


//---------------------------------------------------------------------------------------------------------------
// class: CAvailableBIUpdateDll
// desc : The class to represent an update for UDP agent or consle. The content is got from "CAvailableBIUpdateDll.dll" or "CAvailableBIUpdateDll.dll.xml"
//---------------------------------------------------------------------------------------------------------------
CAvailableBIUpdateDll::CAvailableBIUpdateDll()
	: m_bRebootRequried(FALSE)
	, m_iLastRebootUpdate(-1)
	, m_dwRequiredVersionOfAutoUpdate(0)
{
	ZeroMemory(&m_version, sizeof(m_version));

}

CAvailableBIUpdateDll::~CAvailableBIUpdateDll()
{
	std::vector<PUP_FILE_INFO>::iterator it;
	for (it = m_vecFilesToDownload.begin(); it != m_vecFilesToDownload.end(); it++)
		delete (*it);
	m_vecFilesToDownload.clear();

	std::vector<PUP_POST_ACTION>::iterator itA;
	for (itA = m_vecActionsToRun.begin(); itA != m_vecActionsToRun.end(); itA++)
		delete (*itA);
	m_vecActionsToRun.clear();
}



void CAvailableBIUpdateDll::GetVersion(UDP_VERSION_INFO& version)
{
	version = m_version;
}


DWORD CAvailableBIUpdateDll::LoadFromDllFile(const wstring& strDllFile)
{
	m_log.LogW(LL_INF, 0, L"enter %s strDllFile=%s", __WFUNCTION__, strDllFile.c_str());

	wstring strXmlFile = strDllFile + L".xml";
	DWORD dwRet = extractXmlFromDll(strDllFile, strXmlFile);
	if (0 != dwRet){
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to extract XML from DLL %s", __WFUNCTION__, strDllFile.c_str());
		return dwRet;
	}
	dwRet = LoadFromXmlFile(strXmlFile);
	//::DeleteFile(strXmlFile.c_str()); //marked by cliicy.luo for debug
	return dwRet;
}

DWORD CAvailableBIUpdateDll::LoadFromXmlFile(const wstring& strXmlFile)
{
	m_log.LogW(LL_INF, 0, L"enter %s %s", __WFUNCTION__,strXmlFile.c_str());
	DWORD dwRet = 0;
	CXXmlNode* pRoot = CXXmlNode::LoadFromFile(strXmlFile);
	if (!pRoot)
	{
		m_log.LogW(LL_ERR, 0, L"%s: Failed to load file %s", __WFUNCTION__, strXmlFile.c_str());
		return (DWORD)-1;
	}

	//
	// get update version from xml
	//
	do
	{
		wstring strVersion = getChildNodeText(pRoot, XML_Version, L"");
		UPUTILS::VersionFromString(strVersion, m_version);
		m_bRebootRequried = STRUTILS::str2boolean(getChildNodeText(pRoot, XML_RebootRequired, L"false"), false);
		m_iLastRebootUpdate = _wtoi(getChildNodeText(pRoot, XML_LastRebootableUpdateVersion, L"0").c_str());
		m_dwRequiredVersionOfAutoUpdate = _wtoi(getChildNodeText(pRoot, XML_RequiredVersionOfAutoUpdate, L"0").c_str());

		//
		// get files to download
		//
		CXXmlNode* pFilesToDownload = pRoot->GetNodeByPath(XML_UpdateFiles);
		if (pFilesToDownload)
		{
			CXXmlNode* pFileNode = pFilesToDownload->GetNode(XXN_FIRST);
			while (pFileNode)
			{
				PUP_FILE_INFO pFile = xmlNodeToFileInfo(pFileNode);
				if (pFile) {
					m_vecFilesToDownload.push_back(pFile);
					m_log.LogW(LL_INF, 0, L"%s will download file %s", __WFUNCTION__,pFile->strSrcURLOfFile.c_str());
				}
				pFileNode = pFileNode->GetNode(XXN_NEXT);
			}
		}

		//
		// get the post download actions
		//
		CXXmlNode* pActions = pRoot->GetNodeByPath(XML_PostDownloadActions);
		if (pActions)
		{
			CXXmlNode* pActionNode = pActions->GetNode(XXN_FIRST);
			while (pActionNode)
			{
				PUP_POST_ACTION pAct = xmlNodeToActionItem(pActionNode);
				if (pAct)
					m_vecActionsToRun.push_back(pAct);
				pActionNode = pActionNode->GetNode(XXN_NEXT);
			}
		}
		std::sort(m_vecActionsToRun.begin(), m_vecActionsToRun.end(), sortByOrder);
	} while (0);

	return dwRet;
}

BOOL CAvailableBIUpdateDll::ValidateUpdate(const wstring& strBaseDir)
{
	m_log.LogW(LL_INF, 0, L"enter %s will validateupdate file %s", __WFUNCTION__, strBaseDir.c_str());

	std::vector<PUP_FILE_INFO>::iterator itf;
	for (itf = m_vecFilesToDownload.begin(); itf != m_vecFilesToDownload.end(); itf++){
		wstring strFile = PATHUTILS::path_join(strBaseDir, (*itf)->strDstFileName);
		m_log.LogW(LL_INF, 0, L"%s validateupdating file %s", __WFUNCTION__, strFile.c_str());

		if (!PATHUTILS::is_file_exist(strFile)){
			m_log.LogW(LL_ERR, 0, L"%s: File [%s] does not exist.", __WFUNCTION__, strFile.c_str());
			return FALSE;
		}

		wstring strMd5 = PATHUTILS::md5_of_file(strFile);
		if (!STRUTILS::same_str(strMd5, (*itf)->strMd5OfFile)){
			m_log.LogW(LL_ERR, 0, L"%s: File [%s] was damaged.", __WFUNCTION__, strFile.c_str());
			return FALSE;
		}
	}
	return TRUE;
}

void CAvailableBIUpdateDll::GetFilesToDownload(std::vector<PUP_FILE_INFO>& vecFiles)
{
	vecFiles.assign(m_vecFilesToDownload.begin(), m_vecFilesToDownload.end());
}

void CAvailableBIUpdateDll::GetPostDownloadActions(std::vector<PUP_POST_ACTION>& vecActions)
{
	vecActions.assign(m_vecActionsToRun.begin(), m_vecActionsToRun.end());
}

ULONGLONG CAvailableBIUpdateDll::GetSizeOfThisUpdate()
{
	ULONGLONG ullSize = 0;
	std::vector<PUP_FILE_INFO>::iterator it;
	for (it = m_vecFilesToDownload.begin(); it != m_vecFilesToDownload.end(); it++)
		ullSize += (*it)->ullFileSize;
	return ullSize;
}


