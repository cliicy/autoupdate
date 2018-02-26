// UpdateCheckDlg.h : header file
//

#pragma once

#include "SetupPage.h"
#include "resource.h"
#include "MyButton.h"
#include "EdgeAPM.h"
#include "StaticEx.h"
#include <vector>

using namespace std;
using namespace  EdgeAPM;

// CUpdateCheckPage dialog


class CUpdateCheckPage : public CSetupPage
{
	DECLARE_DYNCREATE(CUpdateCheckPage)

	// Construction
public:
	CUpdateCheckPage();
	~CUpdateCheckPage();

	// Dialog Data
	//{{AFX_DATA(CUpdateCheckPage)
	enum { IDD = IDD_SETUP_UPDATECHECK };
	//}}AFX_DATA
	
protected:

	// Overrides
	// ClassWizard generate virtual function overrides
	//{{AFX_VIRTUAL(CUpdateCheckPage)
	virtual BOOL OnQueryCancel();
	virtual BOOL OnInitDialog();
	virtual BOOL OnSetActive();
	virtual BOOL OnWizardFinish();
	virtual LRESULT OnWizardNext();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL
	afx_msg LRESULT OnAPMStatusChanged(WPARAM wParam, LPARAM lParam);

	// Generated message map functions
	//{{AFX_MSG(CUpdateCheckPage)
	//}}AFX_MSG

	DECLARE_MESSAGE_MAP()
public:
	const EdgeAPM::APMSetting* GetApmSetting();
	HANDLE GetCancelUpdateEvent();

public:
	afx_msg void OnBnClickedRadioCaServer();
	afx_msg void OnBnClickedCheckProxyAuth();
	afx_msg void OnBnClickedRadioStagin();
	afx_msg void ShowProxyAuthControls(BOOL bEnable);
	afx_msg void OnBnClickedRadioStagin2();
	afx_msg void OnBnClickedCaProxyServer();
	afx_msg void OnCheckingUpdate();
public:
	CButton m_radioCaserver;
	CButton m_checkbutton;
	CButton m_radioStagin;
	CButton m_radioStagin2;
	CStatic groupboxCaserver;
	CStatic groupboxStagin;
	CStatic m_lblupdatesdes;
	CButton m_ckProxy;
	CStaticEx m_tbtital;
	CStatic m_tbdes;
	CStatic m_lblProxyServer;
	CStatic m_lblStagingServer;
	CStatic m_lblStagingPort;
	CStatic m_lblProxyPort;
	CEdit m_tbProxyServer;
	CEdit m_tbProxyPort;
	CButton m_ckProxyAuth;
	CStatic m_lblProxyUser;
	CEdit m_tbProxyUser;
	CEdit m_tbProxyPwd;
	CEdit m_tbStagingServer;
	CEdit m_tbStagingPort;
	CStatic m_lblProxyPwd;
	CProgressCtrl m_ctrlProgress;
	CStatic m_tbprocess;
	//CTransparentPic  aa;

public:
	void SetRadioStatus(int nstatus)
	{
		RadioStatus = nstatus;
	}
	int GetRadioStatus()
	{
		return RadioStatus;
	}
	BOOL isProcessCtrlExist()
	{
		return m_bProCtrlExist;
	}
	void SetProcessCtrlExit(BOOL m_bexist)
	{
		m_bProCtrlExist = m_bexist;
	}
	BOOL Validate();
	EdgeAPM::ServerInfo ConvertFromModel( const ServerInfoModel &model );
	EdgeAPM::APMSetting ConvertFromModel( const APMSettingModel &model );
	DWORD ValidateServerName( const CString &strName );
	BOOL ValidatePort( const CString &strPort );
	Response response;
private:
	void EnableStagingControls( BOOL bEnable);
	void EnableProxyControls( BOOL bEnable );
	void EnableProxyAuthControls( BOOL bEnable );
	void EnableAllControls( BOOL bEnable );
public:
	EdgeAPM::APMSettingModel		m_apmSettingModel;
private:
	HANDLE m_hCancelUpdateEvent;
	EdgeAPM::APMSetting m_apmSetting;
	wstring				m_strIniFilePath;
	wstring				m_strD2DIniFilePath;
	int RadioStatus;
	BOOL m_bProCtrlExist;
};
