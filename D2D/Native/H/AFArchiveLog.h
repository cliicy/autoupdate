#pragma once

#include <string>

using namespace std;

// the logfile will be the <logfolderpath>\logfile+timestamp+jobnumber+count.log
// we will store [<logfolderpath>\logfile+timestamp+jobnumber] in basefilename
// and the [<logfolderpath>\logfile+timestamp+jobnumber+count.log] in currentfilename
typedef struct stAFARCHLog{
	wstring				szArchLogBaseFileName;		// logfile name set by the creating process
	wstring				szArchLogCurrentFileName;	// current used logfile name set by the creating process
	DWORD				dwLogFileCount;				// current logfile count
	DWORD				dwLogLevel;					// current log level to use
	DWORD				dwTotalLogLines;			// we will count this number and create a new file
	DWORD				dwMaxLogLinesPerFile;		// this can be set by the user by registry key
	BOOL				bLogInited;					// whether the log was initialised or not
	CRITICAL_SECTION	hArchLogCS;					// this will mutually exclude the operations from different threads
	FILE*				fpARCHLog;					// file pointer to the log file
}stAFARCHLogType;


// support for common logging for archive module - this will be used by Archive job [archive, restore, purge, catalogresync]
// not sure if webservice should use it or not yet


#define		AFARCH_LOG_LEVEL_DEBUG						1
#define		AFARCH_LOG_LEVEL_INFO						2
#define		AFARCH_LOG_LEVEL_WARN						3
#define		AFARCH_LOG_LEVEL_ERROR						4

#define		DEFAULT_AFARCH_LOG_LEVEL					AFARCH_LOG_LEVEL_INFO
#define		DEFAULT_AFARCH_MAX_LOG_LINES_PER_FILE		20000		// a new log file will be created every 2000 lines

extern "C" BOOL InitAFARCHLog(wstring szModuleName, wstring& nodeName, DWORD dwJobNumber);
extern "C" void DeInitAFARCHLog();
extern "C" void AFARCHLog(DWORD dwCurMsgLogLevel, TCHAR *szStrLogFormat, ...);
extern "C" void SetAFARCHLogParameters(DWORD dwLogLevel, DWORD dwMaxLogLinesPerFile);
extern "C" stAFARCHLogType* GetAFArchiveLogPtr();