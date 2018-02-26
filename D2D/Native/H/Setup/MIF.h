/////////////////////////////////////////////////////////////////////////////
// MIF.h		CMIF Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _MIF
#define _MIF

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CMIF
#else
class __declspec(dllimport) CMIF
#endif
{
private:
    CString m_sFileName;
    CString m_sSerialNumber;

public:
    // Constructor
    CMIF(LPCTSTR lpszFileName, LPCTSTR lpszSerialNumber);
	CMIF(LPCTSTR lpszFileName);

	// Operations
	static BOOL Create(LPCTSTR lpszFileName, int iInstallType);
	BOOL Create(int iInstallType);
	static BOOL WriteStatus(LPCTSTR lpszFilename, LPCTSTR szMsg, BOOL bStatus);
	BOOL WriteStatus(LPCTSTR szMsg,BOOL bStatus);
	static BOOL WriteEndOfMIF(LPCTSTR lpszFilename);
	BOOL WriteEndOfMIF();

    // Operations
    BOOL Create();
    BOOL WriteStatus(int nStatus, LPCTSTR szMsg);

    static BOOL Create(LPCTSTR lpszFileName);
    static BOOL WriteStatus(LPCTSTR lpszFileName, LPCTSTR lpszSerialNumber, int nStatus, LPCTSTR szMsg);
};

#endif
