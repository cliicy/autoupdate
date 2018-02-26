/*************************************************************************************

*  This program is an unpublished work fully protected by the United States

*  Copyright laws and is considered a trade secret belonging to CA International, Inc.

*  Copyright 2006 CA International, Inc

***************************************************************************************/

#ifndef __V2PNF_LOGGER_
#define __V2PNF_LOGGER_

#if _MSC_VER > 1000  
    #pragma once     
#endif 

#ifdef V2PNF_EXPORTS
    #define LIBSPEC __declspec(dllexport)
#else                                  
    #define LIBSPEC __declspec(dllimport)
#endif // VMMANAGER_EXPORTS 


#include <windows.h>
#include <stdio.h>
#include <tchar.h>
#include <stdlib.h>
#include <io.h>
#include <winnt.h>
#include "brandname.h"

//#ifndef PTCHAR
//typedef wchar_t *PTCHAR;
//#endif
//#ifndef TCHAR
//typedef wchar_t TCHAR;
//#endif

#ifdef V2PNF_EXPORTS
#define CAVMLOG_API __declspec(dllexport)
#else
#define CAVMLOG_API __declspec(dllimport)
#endif


#define MAX_PRINTF_OUTPUT               1152            // characters

//modifid by zhepa02 at 2015-05-05, move the define to header file for reference
#define WIN_PE L"SYSTEM\\CurrentControlSet\\Control\\MiniNT"
//modify end

#define FOPEN			_wfopen
#define FPUTS			fputws
#define SPRINTF			wsprintf
#define SBSPRINTF		wsprintfA

//#define FOPEN fopen
//#define FPUTS fputs
//#define SPRINTF sprintf

#define MaxLogSize		1024

#define _D_MDY          0
#define _D_DMY          1
#define _D_YMD          2

#define _T_12HR         0
#define _T_24HR         1

#define NULLCH		_T('\0')
#define IS_KEY_CA_Agent (CST_REG_ROOT_T _T("\\InstallPath"))
#define IS_DISPLAY _T("Path")
#define IS_KEY_LEN 256


#include "DbgLog.h"

extern CDbgLog g_DebugLog;

#define D2DDEBUGLOG(nLevel, dwError, szFormat, ...) g_DebugLog.LogW(nLevel, dwError, szFormat, __VA_ARGS__)
#define D2D_DEBUG_LOG_FILE(bFileNameOnly)			g_DebugLog.GetGlobalLogFileName(bFileNameOnly)

typedef struct  _INTL 
{
	UINT	iDate,
			iCentury,
			lzDay,
			lzMonth,
			iTime,
			lzHour;
	UINT	defaultiDate,
			defaultiCentury,
			defaultlzDay,
			defaultlzMonth,
			defaultiTime,
			defaultlzHour;
	TCHAR	sThousand[2],
			sDecimal[2],
			sDate[2],
			sTime[2],
			s1159[5],
			s2359[5];
	TCHAR	defaultsThousand[2],
			defaultsDecimal[2],
			defaultsDate[2],
			defaultsTime[2],
			defaults1159[5],
			defaults2359[5];
} INTL;

static  TCHAR   strIntl[]       = _T("intl");		
static  TCHAR   strCPIntl[]     = _T("Control Panel\\International");	
static  TCHAR   defaultstrCPIntl[] = _T(".DEFAULT\\Control Panel\\International");	

static  TCHAR   fmt2d[]         = _T("%2d"),
                fmt02d[]        = _T("%02d"),
                fmt04d[]        = _T("%04d"),
                fmtS[]          = _T("%s"),	
                fmtList[]       = _T("%u%s ");
                                   
static  TCHAR   strRatio[]      = _T(":1");	

static  TCHAR   fmtDate[32]     = _T("%2d%s%02d%s%02d"),
                fmt24Hr[32]     = _T("%2d%s%02d%s%02d"),
                fmt12Hr[32]     = _T("%2d%s%02d %s"),
                fmtms[32]		= _T("%2d%s%02d ");

static  TCHAR   defaultfmtDate[32]     = _T("%2d%s%02d%s%02d"),
                defaultfmt24Hr[32]     = _T("%2d%s%02d%s%02d"),
                defaultfmt12Hr[32]     = _T("%2d%s%02d %s");

static  TCHAR   strSec[16],
                strMin[16],
                strHr[16];

static  INTL    intl = { _D_MDY, FALSE, FALSE, FALSE, _T_12HR, FALSE,
						 _D_MDY, FALSE, FALSE, FALSE, _T_12HR, FALSE,
                         _T(","), _T("."), 
                         _T("/"), _T(":"), 
                         _T("AM"), _T("PM"),
                         _T(","), _T("."), 
                         _T("/"), _T(":"), 
                         _T("AM"), _T("PM") 
						};


class V2PNFlog {
private:
	HINSTANCE       hModule;
	HANDLE			logHandle;

	TCHAR	pathValue[IS_KEY_LEN]; 
	TCHAR   logFile[IS_KEY_LEN];  
	TCHAR   mountLogFile[IS_KEY_LEN];
	LONG	maxLogSize;		   
	LONG	maxMountLogSize;	
	TCHAR   RecoverVMlogFile[IS_KEY_LEN];
	void V2PNFlog::GetLogDate(PTCHAR szBuff);
	PTCHAR WINAPI V2PNFlog::_intl_dttos(PTCHAR string, short year, short month, short day);
	PTCHAR WINAPI V2PNFlog::_intl_tmtos(PTCHAR string, short hour, short minute, short second);
	PTCHAR WINAPI V2PNFlog::_intl_mstos(PTCHAR string, short minute, short second);

public:
	V2PNFlog();
	~V2PNFlog();
	void V2PNFlog::GetLocalDateTimeString(PTCHAR szBuff);
	void writeMsg(UINT strID, ...);					//used to write localized message
	void writeDebugLog(const TCHAR *msg, ...);		//used to write debug log messages
	int getD2DInstallPath(TCHAR *PathValue);		//get the agent installation path
		DWORD  WINAPI getMaxLogFiles(); 
};



#ifdef __cplusplus
extern "C" {	//start C scope
#endif

	LIBSPEC V2PNFlog* createLogObj();
	LIBSPEC void deleteLogObj(V2PNFlog *pObj);
	LIBSPEC void DebugLogWrapper(V2PNFlog *pObj, TCHAR *msg, ...);

#ifdef __cplusplus
}				//end C scope
#endif

#undef LIBSPEC
#endif //__V2PNF_LOGGER_
