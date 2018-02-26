#include "..\stdafx.h" //new filter for update dns tool
#include "UpdateDNSTool.h"
#include "DebugLog-UpdateDNSTool.h"

#include "..\MyCrypto\EncryptPassword.h"
#include "..\MyCrypto\EncryptString.h"

namespace {
static TCHAR _R_N[] = TEXT("\r\n");
static TCHAR _R_N_NEW[] = TEXT("^&");
}

//CONST wstring & Arg - not quoted 
static VOID __AddOneArgHelper(wstring & CmdLine, CONST wstring & Arg, BOOL IsFirstArg)
{
	if (IsFirstArg)
	{
		CmdLine += L"\"";
	}
	else
	{
		CmdLine += L" \"";
	}
	CmdLine += Arg;
	CmdLine += L"\"";
}

//<huvfe01>2012-12-20 for server type
static VOID __AddOneArgCfgHelper(IN CONST wstring & Arg, OUT wstring & strCfg, BOOL bFirstArg)
{
    if (!bFirstArg)
    {
        strCfg += L",";
    }
    
    strCfg += Arg;
}

static VOID __FindArgsCfgHelper(IN wstring & strCfg, OUT vector<wstring> & Args)
{
    CString strCfgTemp = strCfg.c_str();
    INT nPos = 0;
    static CONST CString strSplitStr = TEXT(",");
    CString Arg;

    nPos = strCfgTemp.Find(strSplitStr.GetString());
    while (nPos > 0)
    {
        Arg = strCfgTemp.Left(nPos);
        Args.push_back(Arg.GetString());

        strCfgTemp = strCfgTemp.Right(strCfgTemp.GetLength() - (nPos + 1));

        nPos = strCfgTemp.Find(strSplitStr.GetString());
    }
    
    Args.push_back(strCfgTemp.GetString());
}

VOID DNSUpdaterCfgBuilder(IN CONST DNS_UPDATER_CFG_S& stUpdaterCfg, OUT wstring& strUpdaterCfg)
{
    CString strDnsSerserType;
    strDnsSerserType.Format(TEXT("%d"), stUpdaterCfg.nDnsSerserType);
    strUpdaterCfg = TEXT("\"");
    __AddOneArgCfgHelper(strDnsSerserType.GetString(),  strUpdaterCfg, TRUE);
    __AddOneArgCfgHelper(stUpdaterCfg.strUpdaterCmdLine, strUpdaterCfg, FALSE);
    strUpdaterCfg += TEXT("\"");
}

DWORD DNSUpdaterCfgParser(IN wstring& strUpdaterCfg, OUT DNS_UPDATER_CFG_S& stUpdaterCfg)
{
    wstring Arg;
    wstring strCfg = strUpdaterCfg;
    vector<wstring> Args;
    DWORD dwRet = ERROR_BAD_LENGTH;

    __FindArgsCfgHelper(strCfg,Args);

    if (DNS_UPDATER_CFG_ARG_COUNT == Args.size())
    {
        //DNS server type
        stUpdaterCfg.nDnsSerserType = DNS_SERVER_TYPE_INVALID;
        if (0 == Args[0].compare(TEXT("0")))
        {
            stUpdaterCfg.nDnsSerserType = DNS_SERVER_TYPE_WINDOWS;
        }
        else if (0 == Args[0].compare(TEXT("1")))
        {
            stUpdaterCfg.nDnsSerserType = DNS_SERVER_TYPE_BIND;
        }

        //Cmdline
        stUpdaterCfg.strUpdaterCmdLine = Args[1];

        dwRet = ERROR_SUCCESS;
    }

    return dwRet;
}

static VOID __DNSUpdaterBinToCmdLineHelper( CONST wstring & strExe, CONST DNSUpdaterCmdLine & Bin, wstring & CmdLine )
{
	__AddOneArgHelper(CmdLine, strExe, TRUE);

	if (Bin.dns.size())
	{
		__AddOneArgHelper(CmdLine, TEXT("-dns"), FALSE);
		__AddOneArgHelper(CmdLine, Bin.dns, FALSE);
	}

	if (Bin.hostname.size())
	{
		__AddOneArgHelper(CmdLine, TEXT("-hostname"), FALSE);
		__AddOneArgHelper(CmdLine, Bin.hostname, FALSE);
	}

	if (Bin.hostip.size())
	{
		__AddOneArgHelper(CmdLine, TEXT("-hostip"), FALSE);
		__AddOneArgHelper(CmdLine, Bin.hostip, FALSE);
	}

	if (Bin.ttl != -1)
	{
		__AddOneArgHelper(CmdLine, TEXT("-ttl"), FALSE);
		CString strTtl;
		strTtl.Format(TEXT("%d"), Bin.ttl);
		__AddOneArgHelper(CmdLine, strTtl.GetString(), FALSE);
	}

	if (Bin.username.size())
	{
		__AddOneArgHelper(CmdLine, TEXT("-username"), FALSE);
		__AddOneArgHelper(CmdLine, Bin.username, FALSE);
	}

	if (Bin.password.size())
	{
		__AddOneArgHelper(CmdLine, TEXT("-password"), FALSE);
		__AddOneArgHelper(CmdLine, Bin.password, FALSE);
	}

	if (Bin.keyfile.size())
	{
		__AddOneArgHelper(CmdLine, TEXT("-keyfile"), FALSE);
		__AddOneArgHelper(CmdLine, Bin.keyfile, FALSE);
	}
}

HRESULT DNSUpdaterSaveBinToIni( LPCTSTR pExe, CONST vector<DNSUpdaterCmdLine> & vecBin, LPCTSTR pIni, BOOL bencrypted )
{
	for (size_t ii = 0; ii < vecBin.size(); ++ ii)
	{
		wstring CmdLine;
		__DNSUpdaterBinToCmdLineHelper(pExe, vecBin[ii], CmdLine);

		CString strIniItem;
		if (bencrypted)
		{
			CString strPassWord;
			GetEncPassword(strPassWord);

			wstring EncrytedCmdLine;
			EncryptString(strPassWord.GetString(), CmdLine, EncrytedCmdLine);

			//strIniItem = TEXT("\"");
			strIniItem += EncrytedCmdLine.c_str();
			//strIniItem += TEXT("\"");
			strIniItem.Replace(_R_N, _R_N_NEW);
		}
		else
		{
			//strIniItem = TEXT("\"");
			strIniItem += CmdLine.c_str();
			//strIniItem += TEXT("\"");
		}

        //<huvfe01>2012-12-20 for server type
        wstring strUpdaterCfg;
        DNS_UPDATER_CFG_S stDNSUpdaterCfg;
        ZeroMemory(&stDNSUpdaterCfg, sizeof(DNS_UPDATER_CFG_S));
        stDNSUpdaterCfg.nDnsSerserType  = vecBin[ii].dnsServerType;
        stDNSUpdaterCfg.strUpdaterCmdLine = strIniItem.GetString();
        
		DNSUpdaterCfgBuilder(stDNSUpdaterCfg, strUpdaterCfg);
        

		CString strKey;
		strKey.Format(DNS_UPDATER_COMMAND_LINE_NAME, ii);
		
		BOOL bRet = WritePrivateProfileString(
			DNS_UPDATER_SECTION_NAME,//_In_  LPCTSTR lpAppName,
			strKey.GetString(),//_In_  LPCTSTR lpKeyName,
			strUpdaterCfg.c_str(),//_In_  LPCTSTR lpString,
			pIni//_In_  LPCTSTR lpFileName
			);
		DEBUG_LOG(TEXT("Write ini: [%s=%s], ret=%u"), strKey.GetString(), strIniItem.GetString(), bRet);
	}

	return 0;
}

HRESULT DNSUpdaterLoadCommandLineFromIni( LPCTSTR pIni, OUT vector<DNS_UPDATER_CFG_S> & vecCfg, BOOL bEncrypted )
{
    TCHAR pBuffer[4096] = {0};
    CString strKeyName;

	for (INT ii = 0; ii < 1024; ++ii)
	{
		strKeyName.Format(DNS_UPDATER_COMMAND_LINE_NAME, ii);

		pBuffer[0] = 0;
		DWORD dwRet = GetPrivateProfileString(
			DNS_UPDATER_SECTION_NAME,//_In_   LPCTSTR lpAppName,
			strKeyName.GetString(),//_In_   LPCTSTR lpKeyName,
			TEXT(""),//_In_   LPCTSTR lpDefault,
			pBuffer,//_Out_  LPTSTR lpReturnedString,
			_countof(pBuffer),//_In_   DWORD nSize,
			pIni//_In_   LPCTSTR lpFileName
			);

        //<huvfe01>2012-12-20 for server type
        wstring strUpdaterCfg = pBuffer;
        DNS_UPDATER_CFG_S stDNSUpdaterCfg;
        ZeroMemory(&stDNSUpdaterCfg, sizeof(DNS_UPDATER_CFG_S));
        dwRet = DNSUpdaterCfgParser(strUpdaterCfg, stDNSUpdaterCfg);
        if (ERROR_SUCCESS != dwRet)
        {
            DEBUG_LOG(TEXT("Failed to parse configure file."));
            return E_FAIL;
        }

        CString strBuffer = stDNSUpdaterCfg.strUpdaterCmdLine.c_str();
		wstring PlainText;
		if (bEncrypted)
		{			
			strBuffer.Replace(_R_N_NEW, _R_N);

			CString strPassWord;
			GetEncPassword(strPassWord);

			DecryptString(strPassWord.GetString(), strBuffer.GetString(), PlainText);
		}
		else
		{
			PlainText = strBuffer.GetString(); //<huvfe01>2012-12-20 for server type
		}

		
		if (PlainText.size() < 5)
		{
			break;
		}

        stDNSUpdaterCfg.strUpdaterCmdLine = PlainText; //<huvfe01>2012-12-20 for server type

		vecCfg.push_back(stDNSUpdaterCfg);
	}

	return 0;
}

HRESULT DNSUpdaterExecCommands( CONST vector<DNS_UPDATER_CFG_S> & vecCfg )
{
	STARTUPINFO			si;
	PROCESS_INFORMATION pi;
	BOOL				bRet;
	CString				strCmdLine;
	DWORD				ProcessExitCode;

	HRESULT				hr = S_OK;
	DWORD				LastError = 0;

	for (size_t ii = 0; ii < vecCfg.size(); ++ii)
	{
		
		ZeroMemory( &si, sizeof(si) );
		
		si.cb = sizeof(si);
		ZeroMemory( &pi, sizeof(pi) );

        //<huvfe01>2012-12-20 for server type
        if (DNS_SERVER_TYPE_BIND == vecCfg[ii].nDnsSerserType)
        {
            DEBUG_LOG(TEXT("Update DNS information to Bind DNS server..."));
        }
        else if (DNS_SERVER_TYPE_WINDOWS == vecCfg[ii].nDnsSerserType)
        {
            DEBUG_LOG(TEXT("Update DNS information to Windows DNS server..."));
        }

		strCmdLine = vecCfg[ii].strUpdaterCmdLine.c_str(); //get a writable buffer
		bRet = CreateProcess(
			NULL,//_In_opt_     LPCTSTR lpApplicationName,
			strCmdLine.GetBuffer(),//_Inout_opt_  LPTSTR lpCommandLine,
			NULL,//_In_opt_     LPSECURITY_ATTRIBUTES lpProcessAttributes,
			NULL,//_In_opt_     LPSECURITY_ATTRIBUTES lpThreadAttributes,
			FALSE,//_In_         BOOL bInheritHandles,
			0,//_In_         DWORD dwCreationFlags,
			NULL,//_In_opt_     LPVOID lpEnvironment,
			NULL,//_In_opt_     LPCTSTR lpCurrentDirectory,
			&si,//_In_         LPSTARTUPINFO lpStartupInfo,
			&pi//_Out_        LPPROCESS_INFORMATION lpProcessInformation
			);
		if (!bRet)
		{
			LastError = GetLastError();
			hr = HRESULT_FROM_WIN32(LastError);
			DEBUG_LOG(TEXT("Execute command failure: [%s], hr=0x%08x"), strCmdLine.GetString(), hr);
		}
		else
		{
			CONST ULONG TIME_OUT = 1000 * 60 * 10; 
			WaitForSingleObject(pi.hProcess, TIME_OUT); //execute command one by one
			GetExitCodeProcess(pi.hProcess, &ProcessExitCode);
			DEBUG_LOG(TEXT("Execute command success: [%s], EixtCode=%d"), strCmdLine.GetString(), ProcessExitCode);
			CloseHandle(pi.hThread);
			CloseHandle(pi.hProcess);
		}
	}

	return hr;
}

HRESULT DNSUpdaterCovertP2E( CONST vector<wstring> & CmdLines, LPCTSTR pEncryptedIni )
{
	CString strIniItem;

	for (size_t ii = 0; ii < CmdLines.size(); ++ ii)
	{
		CString strPassWord;
		GetEncPassword(strPassWord);

		wstring EncrytedCmdLine;
		EncryptString(strPassWord.GetString(), CmdLines[ii], EncrytedCmdLine);

		strIniItem = TEXT("\"");
		strIniItem += EncrytedCmdLine.c_str();
		strIniItem += TEXT("\"");
		strIniItem.Replace(_R_N, _R_N_NEW);

		CString strKey;
		strKey.Format(DNS_UPDATER_COMMAND_LINE_NAME, ii);

		BOOL bRet = WritePrivateProfileString(
			DNS_UPDATER_SECTION_NAME,//_In_  LPCTSTR lpAppName,
			strKey.GetString(),//_In_  LPCTSTR lpKeyName,
			strIniItem.GetString(),//_In_  LPCTSTR lpString,
			pEncryptedIni//_In_  LPCTSTR lpFileName
			);
		DEBUG_LOG(TEXT("Write ini: [%s=%s], ret=%u"), strKey.GetString(), strIniItem.GetString(), bRet);
	}

	return 0;
}

HRESULT DNSUpdaterCovertE2P( CONST vector<wstring> & CmdLines, LPCTSTR pPlainIni )
{
	for (size_t ii = 0; ii < CmdLines.size(); ++ ii)
	{
		//CString strBuffer = CmdLines[ii].c_str();
		//strBuffer.Replace(_R_N_NEW, _R_N);

		//CString strPassWord;
		//GetEncPassword(strPassWord);

		//wstring PlainText;
		//DecryptString(strPassWord.GetString(), strBuffer.GetString(), PlainText);

		CString strKey;
		strKey.Format(DNS_UPDATER_COMMAND_LINE_NAME, ii);

		CString strIniItem = TEXT("\"");
		strIniItem += CmdLines[ii].c_str();
		strIniItem += TEXT("\"");

		BOOL bRet = WritePrivateProfileString(
			DNS_UPDATER_SECTION_NAME,//_In_  LPCTSTR lpAppName,
			strKey.GetString(),//_In_  LPCTSTR lpKeyName,
			strIniItem.GetString(),//_In_  LPCTSTR lpString,
			pPlainIni//_In_  LPCTSTR lpFileName
			);
		DEBUG_LOG(TEXT("Write ini: [%s=%s], ret=%u"), strKey.GetString(), strIniItem.GetString(), bRet);
	}

	return 0;
}



