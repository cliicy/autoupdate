// ProdDetector.h : main header file for the ProdDetector DLL
//

#pragma once

#ifndef __AFXWIN_H__
	#error "include 'stdafx.h' before including this file for PCH"
#endif

#include "..\RCModules\Setup\ASetupRes.dll\resource.h"		// main symbols


// CProdDetectorApp
// See ProdDetector.cpp for the implementation of this class
//
#define PROD_NTAGENT		0
#define PROD_BASE			1
#define PROD_SQL			2
#define PROD_VMAGENT		3
#define PROD_BAOF			4
#define PROD_EXCH			5
#define PROD_DIAG			6
#define PROD_ORACLE			7
#define PROD_SP2003			8
#define PROD_SP2007			9
#define PROD_INFOMIX		10
#define PROD_SYBASE			11
#define PROD_DOMINO			12
#define PROD_SAP			13

DWORD GetProductStatus(LPTSTR lpMachine, DWORD dwProductID, CString & strInstallPath, CString & strMsg);
void InitRes(HINSTANCE hInst);

class CProdDetectorApp : public CWinApp
{
public:
	CProdDetectorApp();

// Overrides
public:
	virtual BOOL InitInstance();
	DECLARE_MESSAGE_MAP()
public:
	inline CString GetTempDir(){return m_sTempDir;};
	
private:
	CString m_sTempDir;
};
