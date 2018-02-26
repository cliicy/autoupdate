#include "stdafx.h"
#include "UpLib.h"
#include "DRCommonlib.h"
#include "brandname.h"
#include "DbgLog.h"
#include "XXmlNode.h"
#include <algorithm>

namespace PRODUTILS
{
	wstring GetProductHome(DWORD dwProd)
	{
		WCHAR szHomeDir[MAX_PATH] = { 0 };
		if (dwProd == ARCUPDATE_PRODUCT_AGENT)
			GetInstallPathByProduct(PRODUCT_D2D, szHomeDir, _ARRAYSIZE(szHomeDir));
		else if (dwProd == ARCUPDATE_PRODUCT_FULL)
			GetInstallPathByProduct(PRODUCT_CENTRAL, szHomeDir, _ARRAYSIZE(szHomeDir));
		else if (dwProd == ARCUPDATE_PRODUCT_GATEWAY)
			GetInstallPathByProduct(PRODUCT_CENTRAL, szHomeDir, _ARRAYSIZE(szHomeDir));

		return wstring(szHomeDir);
	}

	wstring GetUDPHome()
	{
		CDbgLog m_log;
		DWORD dwRet = 0;
		wstring strUDPHome = L"";

		CSysInfo sys;
		REGSAM sam = KEY_READ;
		if (sys.IsWow64())
			sam |= KEY_WOW64_64KEY;
		CRegistry reg;
		LONG lRet = reg.Open(CST_BRAND_REG_ROOT, HKEY_LOCAL_MACHINE, sam);
		if (lRet != 0)
		{
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to open registry key: %s", __WFUNCTION__, CST_BRAND_REG_ROOT);
			return strUDPHome;
		}
		reg.QueryStringValue(REG_K_ROOTPATH, strUDPHome);
		if (strUDPHome.empty())
		{
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to read registry value [%s] under [%s]", __WFUNCTION__, REG_K_ROOTPATH, CST_BRAND_REG_ROOT);
			reg.Close();
			return strUDPHome;
		}

		PATHUTILS::path_ensure_end_without_slash(strUDPHome);
		return strUDPHome;
	}

	wstring GetUpdateManagerHome()
	{
		DWORD dwRet = 0;
		wstring strUpdatemanager = L"";
		CDbgLog m_log;

		CSysInfo sys;
		REGSAM sam = KEY_READ;
		if (sys.IsWow64())
			sam |= KEY_WOW64_64KEY;
		CRegistry reg;
		LONG lRet = reg.Open(CST_BRAND_REG_ROOT, HKEY_LOCAL_MACHINE, sam);
		if (lRet != 0)
		{
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to open registry key: %s", __WFUNCTION__, CST_BRAND_REG_ROOT);
			return strUpdatemanager;
		}
		reg.QueryStringValue(REG_K_ROOTPATH, strUpdatemanager);
		if (strUpdatemanager.empty())
		{
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to read registry value [%s] under [%s]", __WFUNCTION__, REG_K_ROOTPATH, CST_BRAND_REG_ROOT);
			reg.Close();
			return strUpdatemanager;
		}
		strUpdatemanager = PATHUTILS::path_join(strUpdatemanager, DIR_UPDATE_MANAGER);
		return strUpdatemanager;
	}

	BOOL IsGatewayInstalled()
	{
		CDbgLog m_log;
		WCHAR szRegHome[MAX_PATH] = { 0 };
		GetRegRootPathByProduct(PRODUCT_CENTRAL, szRegHome, _ARRAYSIZE(szRegHome));
		if (wcslen(szRegHome) == 0)
		{
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to get reigisty root of product console", __WFUNCTION__);
			return FALSE;
		}

		CSysInfo sys;
		REGSAM sam = KEY_READ;
		if (sys.IsWow64())
			sam |= KEY_WOW64_64KEY;
		CRegistry reg;
		if (0 != reg.Open(szRegHome, HKEY_LOCAL_MACHINE, sam))
		{
			m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to open registry %s", __WFUNCTION__, szRegHome );
			return FALSE;
		}

		wstring strValue = L"";
		reg.QueryStringValue(REG_K_GATEWAYFLAG, strValue);
		if (STRUTILS::same_str(strValue, L"1"))
			return TRUE;
		return FALSE;
	}

	DWORD GetProductVersion(DWORD dwProd, UDP_VERSION_INFO& prodVer)
	{
		CDbgLog  m_log;
		ZeroMemory(&prodVer, sizeof(prodVer));
		if (dwProd == ARCUPDATE_PRODUCT_AGENT)
		{
			WCHAR szRegHome[MAX_PATH] = { 0 };
			GetRegRootPathByProduct(PRODUCT_D2D, szRegHome, _ARRAYSIZE(szRegHome));
			if (wcslen(szRegHome) == 0)
			{
				m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to get reigisty root of product %d", __WFUNCTION__, dwProd);
				return (DWORD)-1;
			}
			wstring strRegKey = szRegHome;
			strRegKey += L"\\" REG_K_VERSION;

			CSysInfo sys;
			REGSAM sam = KEY_READ;
			if (sys.IsWow64())
				sam |= KEY_WOW64_64KEY;
			CRegistry reg;
			if (0 != reg.Open(strRegKey.c_str(), HKEY_LOCAL_MACHINE, sam))
			{
				m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to open registry %s", __WFUNCTION__, strRegKey.c_str());
				return ERROR_FILE_NOT_FOUND;
			}

			wstring strValue = L"";
			reg.QueryStringValue(REG_K_MAJOR, strValue);
			if (!strValue.empty())
			{
				prodVer.dwMajor = _wtoi(strValue.c_str());
				strValue = L"";
			}

			reg.QueryStringValue(REG_K_MINOR, strValue);
			if (!strValue.empty())
			{
				prodVer.dwMinor = _wtoi(strValue.c_str());
				strValue = L"";
			}

			reg.QueryStringValue(REG_K_SERVICE_PACK, strValue);
			if (!strValue.empty())
			{
				prodVer.dwServicePack = _wtoi(strValue.c_str());
				strValue = L"";
			}

			reg.QueryStringValue(REG_K_BUILD, strValue);
			if (!strValue.empty())
			{
				prodVer.dwBuild = _wtoi(strValue.c_str());
				strValue = L"";
			}

			reg.QueryStringValue(REG_K_UPDATE, strValue);
			if (!strValue.empty())
			{
				prodVer.dwUpdate = _wtoi(strValue.c_str());
				strValue = L"";
			}

			reg.QueryStringValue(REG_K_UPDATE_BUILD, strValue);
			if (!strValue.empty())
			{
				prodVer.dwUpBuild = _wtoi(strValue.c_str());
				strValue = L"";
			}
			return 0;
		}

		if (dwProd == ARCUPDATE_PRODUCT_FULL || dwProd == ARCUPDATE_PRODUCT_GATEWAY)
		{
			WCHAR szRegHome[MAX_PATH] = { 0 };
			GetRegRootPathByProduct(PRODUCT_CENTRAL, szRegHome, _ARRAYSIZE(szRegHome));
			if (wcslen(szRegHome) == 0)
			{
				m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to get reigisty root of product %d", __WFUNCTION__, dwProd);
				return (DWORD)-1;
			}

			wstring strRegKey = szRegHome;
			strRegKey += L"\\" REG_K_CONSOLE;

			CSysInfo sys;
			REGSAM sam = KEY_READ;
			if (sys.IsWow64())
				sam |= KEY_WOW64_64KEY;
			CRegistry reg;
			if (0 != reg.Open(strRegKey.c_str(), HKEY_LOCAL_MACHINE, sam))
			{
				m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to open registry %s", __WFUNCTION__, strRegKey.c_str());
				return ERROR_FILE_NOT_FOUND;
			}

			wstring strValue = L"";
			reg.QueryStringValue(REG_K_VERSION, strValue);
			std::vector<wstring> verEles;
			STRUTILS::split_str(strValue, L'.', verEles);
			if (verEles.size() > 0)
				prodVer.dwMajor = _wtoi(verEles[0].c_str());
			if (verEles.size() > 1)
				prodVer.dwMinor = _wtoi(verEles[1].c_str());
			if (verEles.size() > 2)
				prodVer.dwBuild = _wtoi(verEles[2].c_str());

			strValue = L"";
			reg.QueryStringValue(REG_K_UPDATE, strValue);
			if (!strValue.empty())
				prodVer.dwUpdate = _wtoi(strValue.c_str());

			strValue = L"";
			reg.QueryStringValue(REG_K_SERVICE_PACK, strValue);
			if (!strValue.empty())
				prodVer.dwServicePack = _wtoi(strValue.c_str());

			strValue = L"";
			reg.QueryStringValue(REG_K_UPDATE_BUILD, strValue);
			if (!strValue.empty())
				prodVer.dwUpBuild = _wtoi(strValue.c_str());

			return 0;
		}
		m_log.LogW(LL_ERR, -1, L"%s: Failed to get version info of product %d", __WFUNCTION__, dwProd);
		return (DWORD)-1;
	}

	BOOL IsAgentManagedByConsole()
	{
		wstring strAgentHome = GetProductHome(ARCUPDATE_PRODUCT_AGENT);
		wstring strCfgFile = PATHUTILS::path_join(strAgentHome, L"Configuration\\RegConfigPM.xml");
		if (PATHUTILS::is_file_exist(strCfgFile))
			return TRUE;
		return FALSE;
	}	
}