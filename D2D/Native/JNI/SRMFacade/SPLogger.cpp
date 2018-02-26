//////////////////////////////////////////////////////////////////////////
// Name: SPLogger.cpp
// Description: implement CSPLogger interface, which used for write logs
//
// Log Format:
// YYYY-MM-DD HH:mm:SS [Log_Level] - LogMessage\r\n
// Note: Log_Level indicate log type or level, enum item, including:
//		including:  LOG_LEVEL_FATAL: FATAL
//					LOG_LEVEL_ERROR: ERROR
//					LOG_LEVEL_WARN: WARNING
//					LOG_LEVEL_INFO: INFOMATION
//					LOG_LEVEL_DEBUG: DEBUG
// example: 2009-02-25 11:46:52 [1] - GetAuthUserRec_Interface username(adminsitrator)
//

#include "stdafx.h"
#include "SPLogger.h"
#include <strsafe.h>

//declare it
std::map<char*, CSPLogger*> CSPLogger::m_loggers;

const int g_nMaxFileSize = 1024*1024*8;  //8M


CSPLogger::CSPLogger()
{
	ZeroMemory(m_szLogFullPath, sizeof(m_szLogFullPath));
	ZeroMemory(m_szLogRootPath, sizeof(m_szLogRootPath));
	ZeroMemory(m_szLoggerModuleName, sizeof(m_szLoggerModuleName));
	ZeroMemory(m_szMutexName, sizeof(m_szMutexName));
	m_hMutex = NULL;

	m_nDefaultLoglevel = LOG_LEVEL_INFO; //default log level is INFO

	//set default module name, file path, full path, and Mutex name
	SetDefaultModuleAndPath(); 

	m_hMutex = CreateMutexW(NULL, FALSE, m_szMutexName);
	if (m_hMutex == NULL)
	{
		//create mutex fail
		OutputDebugStringA("Create SPLogger failed.");

		//avoid writing log fail or other problem, raise default log level
		//doesn't write any logs
		m_nDefaultLoglevel = LOG_LEVEL_FATAL+1;
	}

}

CSPLogger::~CSPLogger()
{
    ReleaseResouce();
}



//////////////////////////////////////////////////////////////////////////
// Set Default module name and file path
void CSPLogger::SetDefaultModuleAndPath()
{
	//Default Module Name is SPLogger_+(CurrentTime), such as SPLogger_200904231620
	wchar_t szCurrentTime[16] = {0};
	SYSTEMTIME tm;
	GetLocalTime(&tm);
	StringCchPrintfW(szCurrentTime,_countof(szCurrentTime), 
		L"%04d%02d%02d%02d%02d%02d", 
		tm.wYear, tm.wMonth, tm.wDay, tm.wHour, tm.wMinute, tm.wSecond);
	wcsncpy_s(m_szLoggerModuleName,_countof(m_szLoggerModuleName),
		L"SPLogger_", _TRUNCATE);
	wcsncat_s(m_szLoggerModuleName,_countof(m_szLoggerModuleName),
		szCurrentTime, _TRUNCATE);


	//Default log root path is %temp% path, if get failed, using "c:\\"
	ExpandEnvironmentStringsW(L"%TEMP%", m_szLogRootPath, _countof(m_szLogRootPath));
	if (wcslen(m_szLogRootPath)<=0){
		StringCchPrintfW(m_szLogRootPath, _countof(m_szLogRootPath), L"c:"); 
	}

	//Default Mutex name is same to default module name
	wcsncpy_s(m_szMutexName, _countof(m_szMutexName),
		m_szLoggerModuleName, _TRUNCATE);

	//default log full path
	wcsncpy_s(m_szLogFullPath,_countof(m_szLogFullPath), L"\\", _TRUNCATE);
	wcsncat_s(m_szLogFullPath,_countof(m_szLogFullPath), m_szLoggerModuleName, _TRUNCATE);
	wcsncat_s(m_szLogFullPath,_countof(m_szLogFullPath), L".", _TRUNCATE);
	wcsncat_s(m_szLogFullPath,_countof(m_szLogFullPath), LOG_EXTENTION, _TRUNCATE);

}



void CSPLogger::SetLoggerModule(wchar_t* pszLoggerModule)	
{
	if (pszLoggerModule==NULL || wcslen(pszLoggerModule)==0)
	{
		return;
	}
	ZeroMemory(m_szLoggerModuleName, sizeof(m_szLoggerModuleName));
	wcsncpy_s(m_szLoggerModuleName,_countof(m_szLoggerModuleName), 
				pszLoggerModule, _TRUNCATE);


	ZeroMemory(m_szLogFullPath, sizeof(m_szLogFullPath));
	StringCchPrintfW(m_szLogFullPath, _countof(m_szLogFullPath), 
		L"%s\\%s.%s", 
		m_szLogRootPath, m_szLoggerModuleName, LOG_EXTENTION);
}


void CSPLogger::SetLoggerRootPath(wchar_t* pszLoggerPath)
{
	if (pszLoggerPath==NULL || wcslen(pszLoggerPath)==0)
	{
		return;
	}
	ZeroMemory(m_szLogRootPath, sizeof(m_szLogRootPath));
	wcsncpy_s(m_szLogRootPath, _countof(m_szLogRootPath), 
		pszLoggerPath, _TRUNCATE);


	//reset logger file full path
	ZeroMemory(m_szLogFullPath, sizeof(m_szLogFullPath));
	StringCchPrintfW(m_szLogFullPath, _countof(m_szLogFullPath), 
		L"%s\\%s.%s", 
		m_szLogRootPath, m_szLoggerModuleName, LOG_EXTENTION);
}



//////////////////////////////////////////////////////////////////////////
// Get CSPLogger instance, which indicate log file path.
// All of log name and CSPLogger instance(including log path) have mapping, saved in m_loggers.
// If not exist in mapping, create new one;
// If has existed in mapping, return matting CSPLogger instance
//
CSPLogger* CSPLogger::getLogger()
{
	static CSPLogger instance;
	return &instance;
}

void CSPLogger::ReleaseResouce()
{
    CloseHandle(m_hMutex);
}


void CSPLogger::SetLoggerLevel(int nLogLevel)
{ 
	if (nLogLevel<=LOG_LEVEL_DEBUG && nLogLevel>=LOG_LEVEL_FATAL)
	{
		m_nDefaultLoglevel = nLogLevel; 
	}
}


void CSPLogger::WriteSPLoggers(int nLevel, char* pszFormatString,...)
{
	char szLevelNum[2] = {0};
	DWORD dwWaitResult = 0; 

	//parameters checking
	if (nLevel>LOG_LEVEL_DEBUG ||
		nLevel<LOG_LEVEL_FATAL ||
		pszFormatString == NULL)
	{
		return;
	}

	//checking default log level, if input log level is higher than m_nDefaultLoglevel, 
	//doesn't write logs
	if (nLevel > m_nDefaultLoglevel)
	{
		return;
	}

	_itoa_s(nLevel, szLevelNum, sizeof(szLevelNum), 10);

	//Request ownership of mutex.
	dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pszFormatString); 
			WriteDataA(szLevelNum, pszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

	// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
	// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}
}


void CSPLogger::WriteDataA(const char* szLevel, char* pszFormatString, va_list pVaList)
{
	// check log file's path and size
	CheckLogFile();

	// write time
	char szTmp[128] = {0};
	SYSTEMTIME tm;
	GetLocalTime(&tm);
	StringCchPrintfA(szTmp,_countof(szTmp), 
		"%04d-%02d-%02d %02d:%02d:%02d [%s] - ", 
		tm.wYear, tm.wMonth, tm.wDay, 
		tm.wHour, tm.wMinute, tm.wSecond,
		szLevel);


	//open file, create new one if not exist
	FILE* hFile = NULL;
	errno_t err = _wfopen_s(&hFile, m_szLogFullPath, L"a");
	if (err != 0)
	{
		return;
	}

    if (NeedPrintOnStandardOutput())
    {
        printf("%s", szTmp);
        vprintf(pszFormatString, pVaList);
    }

	//write format contents
	//va_list argList;
	//va_start(argList, pszFormatString);
	fprintf(hFile, "%s", szTmp);
	vfprintf(hFile, pszFormatString, pVaList);

	fprintf(hFile, "\n");
	//va_end( argList ); 

	fclose(hFile); //close it
}



void CSPLogger::WriteDataW(const wchar_t* szLevel, wchar_t* pszFormatString, va_list pVaList)
{
	// check log file's path and size
	CheckLogFile();

	wchar_t szTmp[128] = {0};
	SYSTEMTIME tm;
	GetLocalTime(&tm);
	StringCchPrintfW(szTmp,_countof(szTmp), 
		L"%04d-%02d-%02d %02d:%02d:%02d [%s] - ", 
		tm.wYear, tm.wMonth, tm.wDay, 
		tm.wHour, tm.wMinute, tm.wSecond,
		szLevel);


	//open file, create new one if not exist
	FILE* hFile = NULL;
	errno_t err = _wfopen_s(&hFile, m_szLogFullPath, L"a");
	if (err != 0)
	{
		return;
	}

    if (NeedPrintOnStandardOutput())
    {
        wprintf(L"%s", szTmp);
        vwprintf(pszFormatString, pVaList);
    }

	fwprintf(hFile, L"%s", szTmp);
	vfwprintf(hFile, pszFormatString, pVaList);
	fwprintf(hFile, L"\n");

	fclose(hFile); //close it

}



void CSPLogger::CheckLogFile()
{
	//checking file directory, if the directory has exist, 
	//following function would fail
	CreateDirectoryW(m_szLogRootPath, NULL);

	//checking file size, if file size is larger than assigned one, rename it 
	//with a time stamp.
	WIN32_FILE_ATTRIBUTE_DATA attr;
	if(GetFileAttributesExW(m_szLogFullPath, GetFileExInfoStandard, &attr)!=0) //success get it
	{
		if (attr.nFileSizeHigh != 0 || attr.nFileSizeLow > g_nMaxFileSize)
		{
			wchar_t szBakFile[MAX_PATH] = {0};
			StringCchCopyW(szBakFile,_countof(szBakFile), m_szLogFullPath);
			size_t nLen = MAX_PATH;
			StringCchLengthW(szBakFile,_countof(szBakFile), &nLen);
			nLen -= 4;
			szBakFile[nLen] = 0;

			//time stamp
			wchar_t szTmp[128] = {0};
			SYSTEMTIME tm;
			GetLocalTime(&tm);
			StringCchPrintfW(szTmp,_countof(szTmp), 
				L"_%04d%02d%02d%02d%02d%02d", 
				tm.wYear, tm.wMonth, tm.wDay, 
				tm.wHour, tm.wMinute, tm.wSecond);

			StringCchCatW(szBakFile,_countof(szBakFile), szTmp);
			StringCchCatW(szBakFile,_countof(szBakFile), L".log");

			MoveFileExW(m_szLogFullPath, szBakFile, MOVEFILE_REPLACE_EXISTING | MOVEFILE_WRITE_THROUGH);
		}
	}
}


void CSPLogger::WriteFatal(char* pszFormatString,...)
{
	char szLevelNum[2] = {0};

	//parameters checking
	if (pszFormatString == NULL)
		return;

	_itoa_s(LOG_LEVEL_FATAL, szLevelNum, sizeof(szLevelNum), 10);

	//Request ownership of mutex.
	DWORD dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pszFormatString); 
			WriteDataA(szLevelNum, pszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

	// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
	// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}
}


void CSPLogger::WriteFatalW(wchar_t* pwszFormatString,...)
{
	wchar_t wszLevelNum[2] = {0};

	//parameters checking
	if (pwszFormatString == NULL)
		return;

	_itow_s(LOG_LEVEL_FATAL, wszLevelNum, sizeof(wszLevelNum), 10);

	//Request ownership of mutex.
	DWORD dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pwszFormatString); 
			WriteDataW(wszLevelNum, pwszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

		// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
		// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}
}



void CSPLogger::WriteError(char* pszFormatString,...)
{
	char szLevelNum[2] = {0};

	//parameters checking
	if (pszFormatString == NULL)
		return;

	//checking default log level, if input log level is higher than m_nDefaultLoglevel, 
	//doesn't write logs
	if (m_nDefaultLoglevel < LOG_LEVEL_ERROR)
		return;

	_itoa_s(LOG_LEVEL_ERROR, szLevelNum, sizeof(szLevelNum), 10);

	//Request ownership of mutex.
	DWORD dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pszFormatString); 
			WriteDataA(szLevelNum, pszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

		// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
		// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}
}



void CSPLogger::WriteErrorW(wchar_t* pwszFormatString,...)
{
	wchar_t wszLevelNum[2] = {0};

	//parameters checking
	if (pwszFormatString == NULL)
		return;

	//checking default log level, if input log level is higher than m_nDefaultLoglevel, 
	//doesn't write logs
	if (m_nDefaultLoglevel < LOG_LEVEL_ERROR)
		return;

	_itow_s(LOG_LEVEL_ERROR, wszLevelNum, sizeof(wszLevelNum), 10);

	//Request ownership of mutex.
	DWORD dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pwszFormatString); 
			WriteDataW(wszLevelNum, pwszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

		// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
		// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}
}


void CSPLogger::WriteWarning(char* pszFormatString,...)
{
	char szLevelNum[2] = {0};

	//parameters checking
	if (pszFormatString == NULL)
		return;

	//checking default log level, if input log level is higher than m_nDefaultLoglevel, 
	//doesn't write logs
	if (m_nDefaultLoglevel < LOG_LEVEL_WARN)
		return;

	_itoa_s(LOG_LEVEL_WARN, szLevelNum, sizeof(szLevelNum), 10);

	//Request ownership of mutex.
	DWORD dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pszFormatString); 
			WriteDataA(szLevelNum, pszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

		// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
		// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}
}



void CSPLogger::WriteWarningW(wchar_t* pwszFormatString,...)
{
	wchar_t wszLevelNum[2] = {0};

	//parameters checking
	if (pwszFormatString == NULL)
		return;

	//checking default log level, if input log level is higher than m_nDefaultLoglevel, 
	//doesn't write logs
	if (m_nDefaultLoglevel < LOG_LEVEL_WARN)
		return;

	_itow_s(LOG_LEVEL_WARN, wszLevelNum, sizeof(wszLevelNum), 10);

	//Request ownership of mutex.
	DWORD dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pwszFormatString); 
			WriteDataW(wszLevelNum, pwszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

		// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
		// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}
}


void CSPLogger::WriteInfo(char* pszFormatString,...)
{
	char szLevelNum[2] = {0};

	//parameters checking
	if (pszFormatString == NULL)
		return;

	//checking default log level, if input log level is higher than m_nDefaultLoglevel, 
	//doesn't write logs
	if (m_nDefaultLoglevel < LOG_LEVEL_INFO)
		return;

	_itoa_s(LOG_LEVEL_INFO, szLevelNum, sizeof(szLevelNum), 10);

	//Request ownership of mutex.
	DWORD dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pszFormatString); 
			WriteDataA(szLevelNum, pszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

		// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
		// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}
}


void CSPLogger::WriteInfoW(wchar_t* pwszFormatString,...)
{
	wchar_t wszLevelNum[2] = {0};

	//parameters checking
	if (pwszFormatString == NULL)
		return;

	//checking default log level, if input log level is higher than m_nDefaultLoglevel, 
	//doesn't write logs
	if (m_nDefaultLoglevel < LOG_LEVEL_INFO)
		return;

	_itow_s(LOG_LEVEL_INFO, wszLevelNum, _countof(wszLevelNum), 10);

	//Request ownership of mutex.
	DWORD dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pwszFormatString); 
			WriteDataW(wszLevelNum, pwszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

		// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
		// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}

}



void CSPLogger::WriteDebug(char* pszFormatString,...)
{
	char szLevelNum[2] = {0};

	//parameters checking
	if (pszFormatString == NULL) 
		return;

	//checking default log level, if input log level is higher than m_nDefaultLoglevel, 
	//doesn't write logs
	if (m_nDefaultLoglevel < LOG_LEVEL_DEBUG)
		return;

	_itoa_s(LOG_LEVEL_DEBUG, szLevelNum, sizeof(szLevelNum), 10);

	//Request ownership of mutex.
	DWORD dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pszFormatString); 
			WriteDataA(szLevelNum, pszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

		// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
		// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}
}



void CSPLogger::WriteDebugW(wchar_t* pwszFormatString,...)
{
	wchar_t wszLevelNum[2] = {0};

	//parameters checking
	if (pwszFormatString == NULL)
		return;

	//checking default log level, if input log level is higher than m_nDefaultLoglevel, 
	//doesn't write logs
	if (m_nDefaultLoglevel < LOG_LEVEL_DEBUG)
		return;

	_itow_s(LOG_LEVEL_DEBUG, wszLevelNum, _countof(wszLevelNum), 10);

	//Request ownership of mutex.
	DWORD dwWaitResult = WaitForSingleObject( 
		m_hMutex,  // handle to mutex
		5000L);   // five second time-out interval
	switch (dwWaitResult) 
	{
	case WAIT_OBJECT_0:
		// The thread got mutex ownership.
		__try {
			//write log contents
			va_list marker;
			va_start( marker, pwszFormatString); 
			WriteDataW(wszLevelNum, pwszFormatString, marker);
			va_end(marker);
		} 

		__finally { 
			// Release ownership of the mutex object.
			ReleaseMutex(m_hMutex);
		} 
		break;

		// Cannot get mutex ownership due to time-out.
	case WAIT_TIMEOUT: 
		// Got ownership of the abandoned mutex object.
	case WAIT_ABANDONED: 
		return; 
	}
}

void CSPLogger::WriteNewEmptyLine()
{
	// check log file's path and size
	CheckLogFile();

	//open file, create new one if not exist
	FILE* hFile = NULL;
	errno_t err = _wfopen_s(&hFile, m_szLogFullPath, L"a");
	if (err != 0)
	{
		return;
	}
	fprintf(hFile, "\n");
	fclose(hFile); //close it
}

BOOL CSPLogger::NeedPrintOnStandardOutput()
{
    wchar_t checkingPath[MAX_PATH] = {0};
    wcsncpy_s(checkingPath, _countof(checkingPath), m_szLogRootPath, _TRUNCATE);
    wcsncat_s(checkingPath, _countof(checkingPath), L"\\checking__", _TRUNCATE);

    FILE* hFile = NULL;
    errno_t err = _wfopen_s(&hFile, checkingPath, L"r");
    if (hFile != NULL)
    {
        fclose(hFile);
    }
    
    return (err == 0 );
}