#include "stdafx.h"

#include <atlbase.h>
#include "MSXMLParserWrapper.h"
#include "EdgeUtility.h"
#include "Log.h"
#include "Golbals.h"
DWORD GetEdgeRootDir( wstring &strDir ){
	CRegKey		reg;
	long		iRet;
	WCHAR		szPath[MAX_PATH];
	ULONG		iPath = MAX_PATH;
	static wstring strRootDir;

	if( !strRootDir.empty() ){
		strDir = strRootDir;
		return 0;
	}

	iRet = reg.Open(HKEY_LOCAL_MACHINE, REG_EDGE_ROOT);
	if (iRet){
		WriteLog(L"fail to open registry key %s, %d", REG_EDGE_ROOT, iRet);
		return 1;
	}
	iRet = reg.QueryStringValue(REG_EDGE_PATH, szPath, &iPath);
	if (iRet){
		WriteLog(L"fail to get registry key [%s] value, %d", REG_EDGE_PATH, iRet);
		return 1;
	}

	strDir = wstring(szPath);
	return 0;
}

BOOL IsProductInstalled( int nProductId )
{
	CRegKey reg;
	DWORD res = 0;
	wstring strReg;

	GetRegRootKeyByProductId( nProductId, strReg );
	res = reg.Open(HKEY_LOCAL_MACHINE, strReg.c_str());
	if(res)
		return FALSE;
	else
		return TRUE;
}

DWORD GetRegRootKeyByProductId( int nProductId, wstring &strRegKey )
{
	DWORD dwRet = 0;
	switch( nProductId ){
			case APM_EDGE_COMMON:
				strRegKey = REG_EDGE_COMMON;
				break;
			case APM_EDGE_CM:
				strRegKey = REG_EDGE_CM;
				break;
			case APM_EDGE_VCM:
				strRegKey = REG_EDGE_VCM;
				break;
			case APM_EDGE_VSPHERE:
				strRegKey = REG_EDGE_VSPHERE;
				break;
			case APM_EDGE_REPORT:
				strRegKey = REG_EDGE_REPORT;
				break;
			case UDP_CONSOLE:
				strRegKey = REG_UDP_CONSOLE;
				break;
			default:
				strRegKey.clear();
				dwRet = 1;
				break;
	}

	return dwRet;
}

DWORD GetProductExtentionKey( int nProductId, const wstring &strKeyName, wstring &strValue )
{
	wstring strRegRoot;
	CRegKey reg;
	LONG	nRet;
	WCHAR	pszValue[MAX_PATH];
	ULONG	pnChars = MAX_PATH;

	strValue.clear();

	GetRegRootKeyByProductId(nProductId, strRegRoot);
	nRet = reg.Open( HKEY_LOCAL_MACHINE, strRegRoot.c_str() );
	if( nRet ){
		return -2;
	}

	nRet = reg.QueryStringValue( strKeyName.c_str(), pszValue, &pnChars );
	if( nRet ){
		return -2;
	}

	strValue = wstring(pszValue);
	return 0;
}

DWORD SetProductExtentionKey( int nProductId, const wstring &strKeyName, const wstring &strValue )
{
	wstring strRegRoot;
	CRegKey reg;
	LONG	nRet;
	ULONG	pnChars;

	GetRegRootKeyByProductId(nProductId, strRegRoot);
	nRet = reg.Open( HKEY_LOCAL_MACHINE, strRegRoot.c_str() );
	if( nRet ){
		return -2;
	}

	nRet = reg.SetStringValue( strKeyName.c_str(), strValue.c_str());
	if( nRet ){
		return -2;
	}

	return 0;
}
