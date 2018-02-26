/////////////////////////////////////////////////////////////////////////////
// Log.h		CLog Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _LOG
#define _LOG

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CLog
#else
class __declspec(dllimport) CLog
#endif
{
private:
    CString m_sFileName;

public:
    // Constructor
    CLog(LPCTSTR lpszFileName);

    // Operations
    BOOL Write(TCHAR *format,...);


    static BOOL Write(LPCTSTR lpszFileName, TCHAR *format,...);
	static BOOL WriteW(LPCWSTR lpszFileName, wchar_t *format,...);  //redbh03 
};
#endif
