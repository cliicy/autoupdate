#pragma once
#include <vector>
#include "afxwin.h"
#include "EdgeAPM.h"


// CAPMSettingDlg dialog
using namespace std;
using namespace  EdgeAPM;

struct ServerInfoModel{
	CString			m_strServer;
	CString			m_strPort;
	ServerInfoModel();
};

struct APMSettingModel{
	int				m_nServerType;
	BOOL			m_bProxy;
	ServerInfoModel				m_proxyServer;
	BOOL			m_bProxyAuth;
	CString			m_strProxyUsername;
	CString			m_strProxyPwd;
	ServerInfoModel		m_stagingServer;
	APMSettingModel();
};

class CAPMSettingDlg : public CDialog
{
	DECLARE_DYNAMIC(CAPMSettingDlg)

public:
	CAPMSettingDlg(CWnd* pParent = NULL);   // standard constructor
	virtual ~CAPMSettingDlg();

// Dialog Data
	enum { IDD = IDD_SETTING };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	virtual BOOL OnInitDialog();
	CButton m_radioCaserver;
	CButton m_ckProxy;
	CButton m_ckProxyAuth;
	CEdit m_tbProxyServer;
	CEdit m_tbProxyUser;
	CEdit m_tbProxyPort;
	CEdit m_tbProxyPwd;
	CEdit m_tbStagingServer;
	CEdit m_tbStagingPort;

private:
	APMSettingModel		m_apmSettingModel;
	wstring				m_strIniFilePath;
	wstring				m_strD2DIniFilePath;
	CString             m_strCaption;
	void EnableCAServerControls(BOOL bEnable);
	void EnableStagingControls(BOOL bEnable);
	void EnableProxyControls(BOOL bEnable);
	void EnableProxyAuthControls(BOOL bEnable);
	APMSettingModel ConvertToModel(const APMSetting &apmSetting);
	ServerInfoModel ConvertToModel(const ServerInfo &serverInfo);
	APMSetting		ConvertFromModel(const APMSettingModel &model);
	ServerInfo		ConvertFromModel(const ServerInfoModel &model);
	BOOL			Validate();
	BOOL			ValidateServerName(const CString &strName);
	BOOL			ValidatePort(const CString &strPort);
public:
	afx_msg void OnBnClickedButton3();
	afx_msg void onBnClickedButtonCancel();
	afx_msg void OnBnClickedRadioCaserver();
	afx_msg void OnBnClickedRadioStagin();
	afx_msg void OnBnClickedCheck1();
	afx_msg void OnBnClickedCheckProxyAuth();
	HICON	m_hIcon;
	CStatic m_lblProxyServer;
	CStatic m_lblProxyPort;
	CStatic m_lblProxyUser;
	CStatic m_lblProxyPwd;
	CStatic m_lblStagingServer;
	CStatic m_lblStagingPort;
};