/////////////////////////////////////////////////////////////////////////////
// User.h		CCurrentUser Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _WIN95AGENT

#ifndef _USER
#define _USER

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CCurrentUser
#else
class __declspec(dllimport) CCurrentUser
#endif
{
private:
    CString m_sDomain;
    CString m_sUser;

public:
    // Constructor
    CCurrentUser();

    // Attributes
    LPCTSTR GetDomain()  {return m_sDomain;}
    LPCTSTR GetUser()    {return m_sUser;}

    static BOOL IsAdministrator();
    static BOOL GetUser(LPTSTR lpszName, LPDWORD lpdwSize);
    static BOOL GetDomain(LPTSTR lpszDomain, LPDWORD lpdwSize);
};
#endif

#endif	// _WIN95AGENT

