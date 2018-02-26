//////////////////////////////////////////////////////////////////////////
// Name: SPLogger.h
//

#pragma once
#include <Windows.h>
#include <stdlib.h>

#pragma warning(push)
#pragma warning(disable:4005)
#pragma warning(pop)

#include <map>


#define		LOG_EXTENTION			L"log"


//module name is logger file name, no extension
#define SET_SPLOGGER_MODULE(logger) CSPLogger::getLogger()->SetLoggerModule(logger)
#define SET_SPLOGGER_LEVEL(DefaultLevel) CSPLogger::getLogger()->SetLoggerLevel(DefaultLevel)

//log root path, log file path, no "\"
#define SET_SPLOGGER_ROOTPATH(LogPath) CSPLogger::getLogger()->SetLoggerRootPath(LogPath)


#define LOG_FATAL	CSPLogger::getLogger()->WriteFatal
#define LOG_ERROR	CSPLogger::getLogger()->WriteError
#define LOG_WARN	CSPLogger::getLogger()->WriteWarning
#define LOG_INFO	CSPLogger::getLogger()->WriteInfo
#define LOG_DEBUG	CSPLogger::getLogger()->WriteDebug

#define LOG_FATALW	CSPLogger::getLogger()->WriteFatalW
#define LOG_ERRORW	CSPLogger::getLogger()->WriteErrorW
#define LOG_WARNW	CSPLogger::getLogger()->WriteWarningW
#define LOG_INFOW	CSPLogger::getLogger()->WriteInfoW
#define LOG_DEBUGW	CSPLogger::getLogger()->WriteDebugW

#define LOG_NEWLINE	CSPLogger::getLogger()->WriteNewEmptyLine

//log level
enum SP_LOG_LEVEL {
	LOG_LEVEL_FATAL,
	LOG_LEVEL_ERROR,
	LOG_LEVEL_WARN,
	LOG_LEVEL_INFO,
	LOG_LEVEL_DEBUG
};

class CSPLogger
{

protected:
	CSPLogger();
    ~CSPLogger();

public:

//#ifdef UNICODE
//#define WriteSPLoggers WriteSPLoggersW
//#else
//#define WriteSPLoggers WriteSPLoggersA
//#endif

	//////////////////////////////////////////////////////////////////////////
	// get logger file path and name, extension name is LOG_EXTENTION
	// Return CSPLogger instance pointer
	//
	static CSPLogger* getLogger();

	//////////////////////////////////////////////////////////////////////////
	// set default value 
	void SetDefaultModuleAndPath();

	//////////////////////////////////////////////////////////////////////////
	//set logger module
	void SetLoggerModule(wchar_t* pszLoggerModule);

	//////////////////////////////////////////////////////////////////////////
	//set logger root file path, no "\"
	void SetLoggerRootPath(wchar_t* pszLoggerPath);


	//////////////////////////////////////////////////////////////////////////
	// Write logs with SPLogger interface
	// parameters:
	//	nLevel[in]: log level, only log level is large or equal to default level(m_nDefaultLoglevel)
	//			the log content is permitted writing to log file
	//  pszFormatString/pwszFormatString[in]: log content
	//
	//void WriteSPLoggersW(int nLevel, wchar_t* pwszFormatString,...);
	void WriteSPLoggers(int nLevel, char* pszFormatString,...);


	//////////////////////////////////////////////////////////////////////////
	// Set default log level.
	// if not calling the interface to special SPLogger instance,
	// the default log level is LOG_LEVEL_DEBUG, write all logs.
	// **Refer to WriteSPLoggers()
	// Parameters: 
	//	nLogLevel: default log, such as LOG_LEVEL_DEBUG.
	//
	void SetLoggerLevel(int nLogLevel);


	void WriteFatal(char* pszFormatString,...);
	void WriteError(char* pszFormatString,...);
	void WriteWarning(char* pszFormatString,...);
	void WriteInfo(char* pszFormatString,...);
	void WriteDebug(char* pszFormatString,...);


	void WriteFatalW(wchar_t* pwszFormatString,...);
	void WriteErrorW(wchar_t* pwszFormatString,...);
	void WriteWarningW(wchar_t* pwszFormatString,...);
	void WriteInfoW(wchar_t* pwszFormatString,...);
	void WriteDebugW(wchar_t* pwszFormatString,...);

	void WriteNewEmptyLine();

    void ReleaseResouce();

private:

	//////////////////////////////////////////////////////////////////////////
	// Write data to logs
	// Parameters:
	//	szLevel[in]: log level string, corresponding to enum level, such as 1 or 2
	//  pwszFormatString/pszFormatString[in]: log contents
	//
	//void WriteDataW(const wchar_t* szLevel, wchar_t* pwszFormatString,...);
	void WriteDataA(const char* szLevel, char* pszFormatString, va_list pVaList);
	void WriteDataW(const wchar_t* szLevel, wchar_t* pszFormatString, va_list pVaList);

	//////////////////////////////////////////////////////////////////////////
	// Check log directory and log file size, rename old one and create a new one 
	// if file is larger than assigned size
	//
	void CheckLogFile();

    //////////////////////////////////////////////////////////////////////////
    // Print on standard output
    BOOL NeedPrintOnStandardOutput();

	int m_nDefaultLoglevel; 
	wchar_t m_szLogFullPath[MAX_PATH];
	wchar_t m_szLogRootPath[MAX_PATH];
	wchar_t m_szLoggerModuleName[128];  //no log extension
	static std::map<char*, CSPLogger*> m_loggers;

	wchar_t m_szMutexName[MAX_PATH];
	HANDLE m_hMutex;
};



//////////////////////////////////////////////////////////////////////////
//Example of using SPLogger
//
//CSPLogger::getLogger()->SetLoggerModule("caauthd")
//CSPLogger::getLogger("")->WriteSPLoggers(LOG_LEVEL_FATAL, "begin2...");
//CSPLogger::getLogger("")->SetLoggerLevel(LOG_LEVEL_INFO);
//CSPLogger::getLogger("")->WriteSPLoggers(LOG_LEVEL_DEBUG, "begin3...");
//
//
//SET_SPLOGGER_MODULE("atob");
//SET_SPLOGGER_LEVEL(LOG_LEVEL_WARN);
//LOG_FATAL("mainAuthServerThread LOG_ERROR(%d)", nLoop);
//LOG_WARN("mainAuthServerThread LOG_WARN(%s)", m_szListString);
//
//