#pragma once
#if (! defined UNIX) && (! defined LINUX)
#include <windows.h>
#include <string>
#include <sstream>
#include <atlstr.h>

#define LOGCFG_SM_NAME          L"Global\\LOGCFG_SM_NAME_C8AAE32A-9619-4685-B766-F003823BEEE3_%d"
#define LOGCFG_SM_NAME_LOCAL    L"Local\\LOGCFG_SM_NAME_C8AAE32A-9619-4685-B766-F003823BEEE3_%d"
#define LOGCFG_MUTEX_NAME       L"Global\\LOGCFG_MUTEX_NAME_C8AAE32A-9619-4685-B766-F003823BEEE3_%d"
#endif
//
// Defination of log level
//
#define LL_ERR		0		// Error.
#define LL_WAR		1		// Warning
#define LL_INF		2		// Infor
#define LL_DET		3		// Detailed
#define LL_DBG      4       // Debug log 

//
// the length of log buffer
//
#define  LOG_BUFFER_LEN		      1024	
#define _MAX_FILE_PATH			  512

#if (! defined UNIX) && (! defined LINUX)
/*
*   Interface IDBGLogOutput
*   Description: 
*       By default class "CDbgLog" will write debug log into file. 
*       But if want to redirect your log into some other places, like to socket, share memory...,
*       you can define a class to implement this interface and use "CDbgLog::SetOutput" to redirect it.
*/
class IDBGLogOutput
{
public:
	virtual void Release( ) = 0;

	virtual void OutputA( int nLevel, DWORD dwError,  LPCSTR lpszMsg ) = 0;

	virtual void OutputW( int nLevel, DWORD dwError, LPCWSTR lpszMsg ) = 0;

	virtual int  GetLogLevel( ) = 0;
};

/*
*   Class CDbgLog
*   Description: 
*       The purpose of this class is to -
*         - Put log module into one dedicated static library, so that each D2D binary can use it easily.
*         - Provide capability to consolidate debug log into one single file by EXE, Job....
*         - Provide capability to adjust log level programly
*/
class CDbgLog
{
public:
	/*
	*  The default constructor of CDbgLog.
	*  The output log file will be "<EXE Name>.Log" under "$D2D_HOME\Logs"
	*/
	CDbgLog( BOOL bUnicode=TRUE );

	/*
	*  The constructor of CDbgLog with a log file name.
	*  Parameters:
	*     lpszModuleName : The module name. It is better to use the project output binary name as the module name 
	*     lpszLogFilePath: It can be a full path name or a file name only.
	*  Exmaple:
	*     CDbgLog( L"AFCoreFunction.dll" )                 :  the output log file will be "$D2D_HOME\Logs\AFCoreFunction.dll.log"
	*     CDbgLog( L"AFCoreFunction.dll", L"C:\\Test.log" ):  the output log file will be "C:\Test.log"
	*/
	CDbgLog( LPCWSTR lpszModuleName, LPCWSTR lpszLogFilePath=NULL, BOOL bUnicode=TRUE );

	~CDbgLog( );

	/*
	*  Customize your own log output
	*/
	void SetOutput( IDBGLogOutput* pOutput );

	void LogA( int nLevel, DWORD dwError, LPCSTR lpszFormat, ... );

	void LogW( int nLevel, DWORD dwError, LPCWSTR lpszFormat, ... );

	void LogMessageA( int nLevel, DWORD dwError, LPCSTR lpszMessage );

	void LogMessageW( int nLevel, DWORD dwError, LPCWSTR lpszMessage );

public:
	/*
	*  Description:
	*      This function is used to consolidate all of logs into one single file in one EXE
	*  Parameters:
	*      lpszLogFileName: It can be a full path name or a file name only.
	*						If this parameter is NULL, it will use the defualt log output file
	*  Example:
	*      If you call this function in a backup job, like SetLogFileName(L"BackupJob.log"). 
	*      After calling this function, all of the  debug logs will output to "$D2D_HOME\Logs\BackupJob.log"
	*/
	static void SetGlobalLogFileName( LPCWSTR lpszLogFileName );


	/*
	*  Description:
	*      This function is used to full path file name for debug logs
	*  Parameters:
	*
	*      void
	*
	*  Example:
	*      If you call this function in a backup job after SetLogFileName(L"BackupJob.log"). 
	*      After calling this function, the result will be "$D2D_HOME\Logs\BackupJob.log"
	*/
	static LPCWSTR GetGlobalLogFileName(BOOL bFileNameOnly); //<sonmi01>2014-4-2 #108407: HBBU change debug log name in activity log

	/*
	*  Description:
	*      This function is used to adjust log level programly
	*  Parameters:
	*      lpszModuleName: It is the one used to consturct log object
	*	   nLevel:  The new log level. If it is -1, will use the default log level 2		
	*/
	static void SetLogLevel( LPCWSTR lpszModuleName, int nLevel );

	/*
	*  Description:
	*      This function is used to get log level of specified module
	*  Parameters:
	*      lpszModuleName: It is the one used to consturct log object
	*/
	static int GetLogLevel( LPCWSTR lpszModuleName );

	/*
	*  Description:
	*      This function is used get the default log folder.
	*  Remarks:
	*      If D2D is installed, the default log output folder is ..\Logs\
	*	   If D2D was not installed, the default log output folder $Application$\Log
	*/
	static DWORD GetDefaultLogFolder( LPWSTR pszLogPath, DWORD* pdwSizeInCharacters );

	/*
	*  Description:
	*	   Set log file name for a specified module.
	*  Remarks:
	*      By default, if an application defined the global log file name by "SetGlobalLogFileName"
	*      All modules of this application will output debug log to this file.
	*      But with this function, you can specifiy a module to output to a specified log file without conrolled
	*      by application
	*/
	static void SetModuleLogFileName( LPCWSTR lpszModuleName, LPCWSTR lpszLogFileName );

protected:
	CDbgLog( const CDbgLog& logObj );

	CDbgLog& operator =( const CDbgLog& logObj );

protected:
	IDBGLogOutput*	  m_pOutputFile;
	IDBGLogOutput*    m_pOutputSM; // write log to share memory
};

/*zxh
bellow is an log help class,please use it by macro definitions at the end of this file.
*/
template<typename stringType, typename StringStreamType>
class CDbgLogHelperCreator;

enum DbgLogHelperLogPolicy
{
	immediatelyLog
};

template<typename stringType, typename StringStreamType>
class CDbgLogHelper
{
	typedef CDbgLogHelperCreator<stringType, StringStreamType> CDbgLogHelperCreatorT;
	friend class CDbgLogHelperCreatorT;
private:
	CDbgLog&	m_logger;
	int			m_iLogLevel;
	DWORD		m_dwErrorCode;

private:
	stringType  m_sLogBuffer;
	stringType	m_sFunInfo;

protected:
	CDbgLogHelper(CDbgLog& logger, int iLogLevel, DWORD dwErrorCode, stringType sFunInfo)
		: m_logger(logger)
	{
		m_iLogLevel = iLogLevel;
		m_dwErrorCode = dwErrorCode;
		m_sFunInfo = sFunInfo;
	};

public:
	~CDbgLogHelper()
	{
		_doLog<stringType>();
	}

public:
	//please use this help class by macro definitions at the end of this file.

	template<typename valueType>
	CDbgLogHelper& operator<<(valueType value){
		StringStreamType ss;
		ss << value;
		m_sLogBuffer += ss.str();
		return (*this);
	}

	CDbgLogHelper& operator<<(const ATL::CString& value){
		StringStreamType ss;
		ss << value.GetString();
		m_sLogBuffer += ss.str();
		return (*this);
	}

	void operator<<(DbgLogHelperLogPolicy value){
		_doLog<stringType>();
	}

private:
	template<typename stringType>
	void _doLog()
	{

	}

	template<>
	void _doLog<std::string>()
	{
		if (m_sLogBuffer.empty())
		{
			return;
		}

		std::string sMsg = "[" + m_sFunInfo + "]" + m_sLogBuffer;
		m_logger.LogMessageA(m_iLogLevel, m_dwErrorCode, sMsg.c_str());
		m_sLogBuffer.clear();
	}

	template<>
	void _doLog<std::wstring>()
	{
		if (m_sLogBuffer.empty())
		{
			return;
		}

		std::wstring sMsg = L"[" + m_sFunInfo + L"]" + m_sLogBuffer;
		m_logger.LogMessageW(m_iLogLevel, m_dwErrorCode, sMsg.c_str());
		m_sLogBuffer.clear();
	}
};

template<typename stringType, typename StringStreamType>
class CDbgLogHelperCreator
{
public:
	static CDbgLogHelper<stringType, StringStreamType> CreateLogHelper(CDbgLog& logger, int iLogLevel, DWORD dwErrorCode, stringType sFunInfo)
	{
		CDbgLogHelper<stringType, StringStreamType> logHelper(logger, iLogLevel, dwErrorCode, sFunInfo);
		return logHelper;
	}
};

typedef CDbgLogHelperCreator<std::string, std::stringstream> CDbgLogHelperCreatorA;
typedef CDbgLogHelperCreator<std::wstring, std::wstringstream> CDbgLogHelperCreatorW;

#ifndef STRING
#define STRING(x) L##x
#endif 

#ifndef WSTRING
#define WSTRING(x) STRING(x)
#endif

#ifndef __WFUNCTION__
#define __WFUNCTION__ WSTRING(__FUNCTION__)
#endif

#define AutoLoggerA(logger, logLevel, errorCode) CDbgLogHelperCreatorA::CreateLogHelper(logger, logLevel, errorCode, __FUNCTION__)
#define AutoLoggerA_Dbg(logger, errorCode) CDbgLogHelperCreatorA::CreateLogHelper(logger, LL_DBG, errorCode, __FUNCTION__)
#define AutoLoggerA_Info(logger, errorCode) CDbgLogHelperCreatorA::CreateLogHelper(logger, LL_INF, errorCode, __FUNCTION__)
#define AutoLoggerA_War(logger, errorCode) CDbgLogHelperCreatorA::CreateLogHelper(logger, LL_WAR, errorCode, __FUNCTION__)
#define AutoLoggerA_Det(logger, errorCode) CDbgLogHelperCreatorA::CreateLogHelper(logger, LL_DET, errorCode, __FUNCTION__)
#define AutoLoggerA_Error(logger, errorCode) CDbgLogHelperCreatorA::CreateLogHelper(logger, LL_ERR, errorCode, __FUNCTION__)

#define AutoLoggerW(logger, logLevel, errorCode) CDbgLogHelperCreatorW::CreateLogHelper(logger, logLevel, errorCode, __WFUNCTION__)
#define AutoLoggerW_Dbg(logger, errorCode) CDbgLogHelperCreatorW::CreateLogHelper(logger, LL_DBG, errorCode, __WFUNCTION__)
#define AutoLoggerW_Info(logger, errorCode) CDbgLogHelperCreatorW::CreateLogHelper(logger, LL_INF, errorCode, __WFUNCTION__)
#define AutoLoggerW_War(logger, errorCode) CDbgLogHelperCreatorW::CreateLogHelper(logger, LL_WAR, errorCode, __WFUNCTION__)
#define AutoLoggerW_Det(logger, errorCode) CDbgLogHelperCreatorW::CreateLogHelper(logger, LL_DET, errorCode, __WFUNCTION__)
#define AutoLoggerW_Error(logger, errorCode) CDbgLogHelperCreatorW::CreateLogHelper(logger, LL_ERR, errorCode, __WFUNCTION__)

/*usage example

example 1:
AutoLoggerW(g_log, 1, 5) << L"test log" << 1 << 3.14 << immediatelyLog;		//it will do log when accept immediatelyLog

example 2:
{
	AutoLoggerW_Error(g_log, 6) << L"test log" << 2 << 6.14;				//it will do log when the object of CDbgLogHelper destory.
}
*/

#endif
