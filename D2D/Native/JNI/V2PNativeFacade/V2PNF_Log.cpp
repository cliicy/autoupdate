/*************************************************************************************

*  This program is an unpublished work fully protected by the United States

*  Copyright laws and is considered a trade secret belonging to CA International, Inc.

*  Copyright 2006 CA International, Inc

***************************************************************************************/
//

#include "stdafx.h"
#include "V2PNF_Log.h"

extern "C" USHORT WINAPI CstGetRegRootStringW(PTCHAR name, USHORT limit);
//modifid by zhepa02 at 2015-05-05, move the define to header file for reference
//#define WIN_PE L"SYSTEM\\CurrentControlSet\\Control\\MiniNT"
//modify end
CDbgLog g_DebugLog;

/*************************************************************************************
*  Name: Constructor
***************************************************************************************/

V2PNFlog::V2PNFlog()
{

	size_t ReturnValue = 0;
	TCHAR buffer[10];
	size_t sizeInWords = 10;

	maxLogSize = 250;
	
	memset(pathValue, 0, IS_KEY_LEN*sizeof(TCHAR));

	if(getD2DInstallPath(pathValue))
	{
		//return;
	}

	if(_tcslen(pathValue) > 0)
		_tcscat_s(pathValue,IS_KEY_LEN,_T("\\Logs"));
	else
		_tcscpy_s(pathValue,IS_KEY_LEN,_T("Log"));

	CreateDirectory(pathValue, NULL);

	memset(logFile, 0, IS_KEY_LEN*sizeof(TCHAR));
	
	_tcscpy_s(logFile, IS_KEY_LEN, pathValue);

	_tcscat_s(logFile,IS_KEY_LEN,_T("\\V2PNF.log"));	
}

/*************************************************************************************
*  Name: Destructor
***************************************************************************************/

V2PNFlog::~V2PNFlog()
{

}
//bccma01 taken from VmwareMgr
/*************************************************************************************
*  Name: int getD2DInstallPath
*  InOut:
*  TCHAR *PathValue
*  Description: Method to get the D2D installation path. Returns 0 on success and 1 on failure
***************************************************************************************/

int V2PNFlog::getD2DInstallPath(TCHAR *PathValue)
{
	HKEY hKey;
	TCHAR szDisplayName[IS_KEY_LEN];
	DWORD dwSize = sizeof(szDisplayName);
	DWORD dwType;
	bool bWinPE = FALSE;
	if(RegOpenKeyEx(HKEY_LOCAL_MACHINE, WIN_PE, 0, KEY_READ, &hKey) == ERROR_SUCCESS)
	{
		bWinPE = TRUE;		
	}
	else if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, IS_KEY_CA_Agent, 0, KEY_READ, &hKey) == ERROR_SUCCESS)		/* vatsa01, SECURITY_VULN, FALSE_POSITIVE */
	{
		if (RegQueryValueEx(hKey, IS_DISPLAY, NULL, &dwType,
			(LPBYTE)&szDisplayName, &dwSize) == ERROR_SUCCESS)
		{
			if(szDisplayName != NULL && wcslen(szDisplayName) > 0)
			{
				_tcscpy_s(PathValue,IS_KEY_LEN,szDisplayName);
			}
			else
			{
				return 1;
			}
		}
	}
	else
	{
		if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, IS_KEY_CA_Agent, 0, KEY_READ|KEY_WOW64_64KEY, &hKey) != ERROR_SUCCESS)		/* vatsa01, SECURITY_VULN, FALSE_POSITIVE */
		{

			return 1;
		}	

		if (RegQueryValueEx(hKey, IS_DISPLAY, NULL, &dwType,
			(LPBYTE)&szDisplayName, &dwSize) == ERROR_SUCCESS)
		{
			if(szDisplayName != NULL && wcslen(szDisplayName) > 0)
			{
				_tcscpy_s(PathValue,IS_KEY_LEN,szDisplayName);			
			}
			else
			{
				return 1;
			}
		}
	}
	if(bWinPE)
	{
		_tcscpy_s(PathValue,IS_KEY_LEN,_T(""));			
	}
	return 0;
}



/*************************************************************************************
*  Name: writeDebugLog
*  Input:
*  1. const TCHAR *fmt
*  2. Variables whose value to be logged ...
***************************************************************************************/

void V2PNFlog::writeDebugLog(const TCHAR *fmt, ...)
{
	va_list	ag;
	TCHAR	szbuf[2048] = _T("\0"), szDateTime[130];
	FILE	*fp = NULL;
	LONG	len;
	BOOL	bRevertToSelf = FALSE;

	__try
	{
		GetLocalDateTimeString(szDateTime);
		va_start(ag,fmt);
		wvsprintf(szbuf,fmt,ag);
		va_end(ag);

		fp = FOPEN(logFile, _T("a+"));

		if (fp == NULL)
		{
			fp = FOPEN(logFile, _T("a+"));

			if (fp == NULL)
			{
				return;
			}
		}

		len = _filelength(fp->_file);
		if (len > (LONG)maxLogSize*1024)
			_chsize(fp->_file,0);

		FPUTS(szDateTime, fp);
		FPUTS(szbuf, fp);
		FPUTS(_T("\n"), fp);
		fclose(fp);
		fp = NULL;
	}

	__except(1)
	{}
	
	if (fp) fclose(fp);
}

/*************************************************************************************
*  Name: GetLocalDateTimeString(PTCHAR szBuff)
*  Input:
*  1. PTCHAR szBuff
*  Description: Method to set date and time in specific format
***************************************************************************************/

void V2PNFlog::GetLocalDateTimeString(PTCHAR szBuff)
{
	SYSTEMTIME lpst;
	TCHAR szdate[64], sztime[64];

	memset(&lpst, 0, sizeof(lpst));
	GetLocalTime(&lpst);

	SPRINTF (szBuff, _T("%s %s  "),
		_intl_dttos(szdate, lpst.wYear, lpst.wMonth, lpst.wDay),		/* vatsa01, SECURITY_VULN */
		_intl_tmtos(sztime, lpst.wHour, lpst.wMinute, lpst.wSecond));	/* vatsa01, SECURITY_VULN */
}



void V2PNFlog::GetLogDate(PTCHAR szBuff)
{
	SYSTEMTIME lpst;

	memset(&lpst, 0, sizeof(lpst));
	GetLocalTime(&lpst);

	_intl_mstos (szBuff, lpst.wMinute, lpst.wSecond);		/* vatsa01, SECURITY_VULN */
}

PTCHAR WINAPI V2PNFlog::_intl_dttos(PTCHAR string, short year, short month, short day)
{
	if (intl.iCentury == 0)
		year %= 100;
	if (intl.iDate == _D_DMY)
		SPRINTF(string, fmtDate, day, (PTCHAR)intl.sDate, month, 
		(PTCHAR)intl.sDate, year);
	else if (intl.iDate == _D_YMD)
		SPRINTF(string, fmtDate, year, (PTCHAR)intl.sDate, month, 
		(PTCHAR)intl.sDate, day);
	else                          // MDY
		SPRINTF(string, fmtDate, month, (PTCHAR)intl.sDate, day, 
		(PTCHAR)intl.sDate, year);
	return string;
}

PTCHAR WINAPI V2PNFlog::_intl_tmtos(PTCHAR string, short hour, short minute, short second)
{
	BOOL  isPM;

	if (second < 0) 
	{
		if (intl.iTime == _T_24HR) 
		{
			SPRINTF(string, fmt24Hr, hour, (PTCHAR)intl.sTime, minute, 
				(PTCHAR)intl.sTime, 0); 
			string[5] = string[6] = string[7] = NULLCH;
		}
		else 
		{
			isPM = FALSE;
			if (hour == 0)
				hour = 12;
			else if (hour == 12)
				isPM = TRUE;
			else if (hour > 12) 
			{
				isPM = TRUE;
				hour -= 12;
			}
			SPRINTF(string, fmt12Hr, hour, (PTCHAR)intl.sTime, minute, 
				(isPM) ? (PTCHAR)intl.s2359 : (PTCHAR)intl.s1159); 
		}
	}
	else if (second > 59) 
	{
		SPRINTF(string, fmt24Hr, hour, (PTCHAR)intl.sTime, minute, 
			(PTCHAR)intl.sTime, 0); 
		string[5] = '\0';
	}
	else 
		SPRINTF(string, fmt24Hr, hour, (PTCHAR)intl.sTime, minute, 
		(PTCHAR)intl.sTime, second); 
	return string;
}

PTCHAR WINAPI V2PNFlog::_intl_mstos(PTCHAR string, short minute, short second)
{
	if (second < 0) 
		second = 0;

	if (second > 59) 
	{
		SPRINTF(string, fmtms, minute, (PTCHAR)intl.sTime, 0); 
	}
	else 
		SPRINTF(string, fmtms, minute, (PTCHAR)intl.sTime, second); 
	return string;
}

PTCHAR WINAPI _intl_default_tmtos(PTCHAR string, short hour, short minute, short second)
{
	BOOL  isPM;

	if (second < 0) 
	{
		if (intl.defaultiTime == _T_24HR) 
		{
			SPRINTF(string,defaultfmt24Hr,hour,intl.defaultsTime,minute,intl.defaultsTime,0); 
			string[5] = string[6] = string[7] = '\0';
		} 
		else 
		{
			isPM = FALSE;
			if (hour == 0)
				hour = 12;
			else if (hour == 12)
				isPM = TRUE;
			else if (hour > 12) 
			{
				isPM = TRUE;
				hour -= 12;
			}
			SPRINTF(string,defaultfmt12Hr,hour,intl.defaultsTime,minute,(isPM)?intl.defaults2359:intl.defaults1159); 
		}
	} 
	else if (second > 59) 
	{
		SPRINTF(string, defaultfmt24Hr, hour, (PTCHAR)intl.defaultsTime, minute, 
			(PTCHAR)intl.defaultsTime, 0); 
		string[5] = '\0';
	} 
	else 
		SPRINTF(string, defaultfmt24Hr, hour, (PTCHAR)intl.defaultsTime, minute, 
		(PTCHAR)intl.defaultsTime, second); 
	return string;
}

V2PNFlog* createLogObj()
{ 
	return new V2PNFlog();
}

void deleteLogObj(V2PNFlog *pObj)
{
	if(pObj)
		delete pObj;
}

void DebugLogWrapper(V2PNFlog *pObj, TCHAR *msg, ...)
{
	va_list ap;
	va_start(ap,msg);
	if(pObj)
		pObj->writeDebugLog(msg, ap);
	va_end(ap);
}


