// APMUtility.h : main header file for the PROJECT_NAME application
//

#pragma once

#ifndef __AFXWIN_H__
	#error "include 'stdafx.h' before including this file for PCH"
#endif

#include <string>
#include "resource.h"		// main symbols

using namespace std;

// CAPMUtilityApp:
// See APMUtility.cpp for the implementation of this class
//

class CAPMUtilityApp : public CWinApp
{
public:
	CAPMUtilityApp();

// Overrides
	public:
	virtual BOOL InitInstance();
	virtual int	 ExitInstance();

// Implementation

	DECLARE_MESSAGE_MAP()

private:
	HANDLE m_hUpdateProcess;
	wstring	m_strLanguageId;
	BOOL m_bRebootRequired;

public:
	wstring GetLanguageId();
	BOOL IsRebootRequired();
};

extern CAPMUtilityApp theApp;
extern bool g_bContinuousMode;