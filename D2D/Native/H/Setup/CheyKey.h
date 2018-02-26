/////////////////////////////////////////////////////////////////////////////
// CheyKey.h		CCheyKey Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _CHEYKEY
#define _CHEYKEY

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CCheyKey
#else
class __declspec(dllimport) CCheyKey
#endif
{
private:
    static const CString m_sCHEYKEY_INI;
    static const CString m_sARCserveNTInfo;
    static const CString m_sCDKey;
    static const CString m_sShowCDKeyDialog;

    CString m_sIniFile;
    TCHAR   m_szCDKey[32];
    BOOL    m_bShowCDKeyDialog;
    BOOL    m_bExists;
    BOOL    FindFile();

public:
    CCheyKey();

    LPCTSTR CDKey();
    BOOL    ShowCDKeyDialog();
};

#endif
