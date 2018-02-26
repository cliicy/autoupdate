// Uninstall.h : main header file for the PROJECT_NAME application
//

#pragma once

#ifndef __AFXWIN_H__
	#error "include 'stdafx.h' before including this file for PCH"
#endif

#include "resource.h"		// main symbols

//////////////////////////////////////////////////////////////////////////
#define WM_CHANGEITEMSTATUS WM_USER + 100

#define WM_INSTALLFINISH	WM_USER + 101
#define WM_REMOVE_START		WM_USER + 102 

enum COMPONENT_TYPE{CAPRODUCT=1, SHARECOMPONENT=4};

#define UNINSTALL_SINGLESTON_FLAG _T("Global\\ARCSERVE_UNINSTALL_SINGLESTON_FLAG")

#define DEFAULT_PRODUCT_NAME _T("Arcserve Unified Data Protection")
#define REG_UDP _T("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\") DEFAULT_PRODUCT_NAME                                       

enum UNINSTALL_STATUS {M_STATUS_PENDING, M_STATUS_WORKING, M_STATUS_COMPLETED, M_STATUS_FAILED};

BOOL SetRebootFlagFile();

//For Platform ID
#define VER_PLATFORM_X86					0X000000000
#define VER_PLATFORM_X64					0X000000001
#define VER_PLATFORM_IA64					0X000000002

class CUninstallConfigure
{
public:
	CUninstallConfigure();

	~CUninstallConfigure();

	const CUninstallConfigure& operator = (const CUninstallConfigure& other);

	CUninstallConfigure(const CUninstallConfigure& other);

	BOOL IsUninstallSuccess(DWORD dwValue);

	BOOL IsUninstallNeedReboot(DWORD dwValue);

public:
	CString m_strShortName; //shortname is like RPSX64,D2DX86,D2DX64,CMX64
	CString m_strProductCode;
	CString m_strProductName;
	CString m_strProductVersion;
	CString m_strComponentName; //shortname is like Agent,Server,VCM, CM etc.
	CString m_strInstallPath;

	CStringArray m_strDependentFeatures;
	CStringArray m_strSharedComponents;
	CStringArray m_strPreUninstallComponents;
	CStringArray m_strRestartServices;
	CStringArray m_strStopServices;

	CString m_strExecutable;
	CString m_strCommandLine;
	CString m_strProductLogFile;
	CString m_strAfterUnisntall;
	CString m_strBeforeUnisntall;
	CString m_strVersionCheck;
	DWORD m_dwComponentType;
	int m_nUAServiceHandle;
	DWORD m_dwExitCode;

	BOOL m_blSelected;
	BOOL m_blNeedReboot;
	UNINSTALL_STATUS m_nStatus;
	int m_nEstInstallTime;
	INT64 m_nSize;

	CDWordArray m_arySuccessValues;
	CDWordArray m_aryRebootValues;
};

typedef CArray <CUninstallConfigure, CUninstallConfigure&> UNINSTALLCONFIGURE_ARRAY;

typedef struct _tagMsgItem
{
	UINT nIconIndex;
	CString strMsg;
}MSG_ITEM;

typedef struct _tagRestartSpecService
{
	CString strUninstallProducts;
	CString strCondition;
	CString strServiceName;
}RESTARTSPECSERVICE;

typedef struct _tagServiceItem
{
	BOOL bNeedRestartStart;
	CString strServiceName;
}SERVICEITEM;

//////////////////////////////////////////////////////////////////////////
class CUninstallApp : public CWinApp
{
public:
	CUninstallApp();
// Overrides
public:
	virtual BOOL InitInstance();
	virtual int ExitInstance();
// Implementation

	DECLARE_MESSAGE_MAP()

public:

	CStringArray m_strRestartSpecServices;

	CString m_strProgFilesPath32;
	CString m_strProgFilesPath64;

	CString m_strLogPath; // this folder without timestamp.
	CString m_strLogPathWithTime;  //this folder with timestamp if the input folder is empty
	CString m_strUinstallStatusFile;

	CString m_strInifile;
	CString m_strLogFile; // this is for uninstall.log to record the detail information
	CString m_strLogSetupFile; //this is for uninstall-history.log to record the history information

	/*
	Product Display Name
	Root Key:
	HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall\CA ARCserve Unified Data Protection                 //X64 OS
	HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\CA ARCserve Unified Data Protection                             //X86 OS

	Key Value:
	DisplayName   //String Type
	*/
	CString m_strProductDisplayName; 

	CString	m_strProductCode;

	CArray <CUninstallConfigure, CUninstallConfigure&> m_aryProducts;

	CArray <MSG_ITEM, MSG_ITEM&> m_aryMessages;

	//get the restart service list from configruation files
	CArray <RESTARTSPECSERVICE, RESTARTSPECSERVICE&> m_aryRestartSpecService;

	//record the service which need to be restarted truely
	CArray <SERVICEITEM, SERVICEITEM&> m_aryNeedRestartSpecService;

	void AddMessage(CString & strMsg, UINT nIconIndex);

	void AddMessage(UINT uID, UINT nIconIndex);

	void RemoveMessage(CString & strMsg);

	void RemoveMessage(UINT uID);

	DWORD LaunchProcess(LPTSTR pCmdLine, LPCTSTR pWorkingDir, DWORD &dwExitCode, DWORD dwMilliseconds=INFINITE, DWORD dwCreationFlags=0);

	void WriteLog(const TCHAR* pszFormat, ...);

	int GetRunningProcess(LPCTSTR lpctProcessName);

	BOOL IsMSIInstallerRunning();

	BOOL IsAllProductsUninstalled();

	BOOL IsProductsUninstalled(const CString &strProductList);

	BOOL IsProductSelected(const CString &strShortName);

	inline CString GetWorkingDir() const {return m_strWorkingDir;}

	inline BOOL IsUseDefaultProductName() const {return m_bUseDefaultProductName;}

	void RebootWinnt(DWORD dwReason = (SHTDN_REASON_MAJOR_APPLICATION | SHTDN_REASON_MINOR_INSTALLATION | SHTDN_REASON_FLAG_PLANNED));

	BOOL UninstallProducts(HWND hWnd, BOOL blWait);

	int GetIndexByShortName(const CString &strShortName);

	int GetIndexByProductName(const CString &lpctProdName);

	void GetStringArray(LPCTSTR lpctValue, TCHAR cSplitChar, CStringArray& data);

	void RemoveRebootFlag();

	inline BOOL IsNeedReboot() const {return m_bReboot;}

	inline void SetReboot(BOOL blReboot) {m_bReboot = blReboot;}

	inline void SetErrorCode(DWORD dwErrorCode) {m_dwErrorCode = dwErrorCode;}

	inline BOOL IsBlockCheck() const {return m_blBlockCheck;}

	inline void SetBlockCheck(BOOL blValue) {m_blBlockCheck = blValue;}

	void ExecPostUninstall();

	DWORD Get64BitOSType();

	void GetUAserviceStatus();

	void UninstallSummary();

	inline BOOL IsNeedHandleUAService() const {return m_bNeedHandleUAService;}
	
	BOOL IsAllowUninstallSharedComponent(LPCTSTR lpctSharedComponent);

	inline BOOL IsRemoveAll() const {return m_blRemoveAll;}

	inline void SetRemoveAll(BOOL blValue) {m_blRemoveAll = blValue;}

	inline BOOL Is64BitOS() const {return m_b64BitOS;}

	inline BOOL IsRollBack() const{return m_blRollback;};

	inline void Set64BitOS(BOOL blValue) {m_b64BitOS = blValue;}

	inline DWORD GetOSPlatform() const {return m_dwOSPlatform;}

	inline void SetOSPlatform(DWORD dwValue) {m_dwOSPlatform = dwValue;}

	BOOL GetNeedRestartSpecService(CStringArray& strServiceArray);

	//when the depended product is selected, the related product depended on it will be selected for un-installation
	void DoProductSelectedWithDepend();

	CString GetProductName(const CString &strShortName);

	BOOL IsNeedRemoveSharedComponent(LPCTSTR lpctSharedComponent);

	BOOL IsNeedRestartService(LPCTSTR lpctService);

	BOOL RestartService(LPCTSTR lpctServie);

	BOOL DoStopService(LPCTSTR lpctServie);

	BOOL DoStartService(LPCTSTR lpctServie);

	BOOL IsDiskSpaceOK();

	//retry to get/set the data for GetPrivateProfile/SetPrivateProfile(BAOF probaly lock the inf file,so use this function to fix the problem)
	DWORD SetupGetPrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpDefault, LPTSTR lpReturnedString, DWORD nSize, LPCTSTR lpFileName);
	BOOL SetupWritePrivateProfileString(LPCTSTR lpAppName, LPCTSTR lpKeyName, LPCTSTR lpString, LPCTSTR lpFileName);
	UINT SetupGetPrivateProfileInt(LPCTSTR lpAppName, LPCTSTR lpKeyName, INT nDefault, LPCTSTR lpFileName);
	DWORD SetupGetPrivateProfileSection(LPCTSTR lpAppName,LPTSTR lpReturnedString,DWORD nSize,LPCTSTR lpFileName);
	//end for retry to get/set the data for GetPrivateProfile/SetPrivateProfile

private:

	BOOL ProcessCommandLine();

	BOOL GetModuleFilePath(LPTSTR lptPath, DWORD dwSize);

	BOOL IsOtherInstanceRunning();

	BOOL CreateRunningFlag();

	BOOL GetInstalledProducts();

	BOOL GetRestartSpecServices();

	BOOL SilentUninstallProducts();

	BOOL SimpleUIUninstallProducts();

	BOOL SelfDelete(BOOL bReboot);

	BOOL ProcessesRunningBlock();

	void RemoveReadOnlyAttibuteA(LPCSTR lpcFile);

	void GetDWordArray(LPCTSTR lpctValue, TCHAR cSplitChar, CDWordArray& data);

	BOOL MakeSurePathExists(LPCTSTR lpctPath, BOOL FilenameIncluded);

	void InitLogFileName(const CString &strLogPath = _T(""));

	UINT GetSplitDataList(const CString  &strListData, const CString &strSpliter, CStringArray& strArray);

	//this is for product name localization, because MSI only support a laugang. So we need get the localizatoin product name from uninstall RC dll if exists
	CString GetNewProductName(CString strProductNameKey);

	void InitUDPRootPath();
	void RemoveRegistry(CString strConfigureFile);
	void DeleteEmptyDirectories(LPCTSTR lpctDir);

private:

	CString m_strWorkingDir;
	CString m_strInputLogPath;
	CString m_strUDPRoot;

	BOOL m_bWriteHistory;
	BOOL m_blBlockCheck;
	BOOL m_blSilent;
	BOOL m_blMsiUI;
	BOOL m_blFullUI;
	BOOL m_blSimpleUI; //simple UI
	BOOL m_blRollback;
	BOOL m_blHelp;
	BOOL m_blProductCode;
	BOOL m_blRemoveAll;
	BOOL m_bReboot;
	BOOL m_bCanel;
	BOOL m_blExePost;//Post Uninstall is executed?
	BOOL m_bNeedRunPost;//Need run Post Uninstall?
	BOOL m_b64BitOS;
	BOOL m_bUseDefaultProductName;//Setup use the default product name (TRUE) or product name (FALSE) on Add/Remove panel
	BOOL m_bNeedHandleUAService; //Need handle UniAgent service
	HANDLE m_hProcessEvent;
	DWORD m_dwReturnCode;
	DWORD m_dwErrorCode;
	DWORD m_dwOSPlatform;
	char m_szBatFile[MAX_PATH];

	CBitmap m_bmpWatermark;
	CBitmap m_bmpHeader;
};

extern CUninstallApp theApp;
