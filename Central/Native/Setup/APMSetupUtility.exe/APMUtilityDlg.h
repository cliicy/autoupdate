// APMUtilityDlg.h : header file
//

#pragma once
#include "afxcmn.h"
#include "resource.h"
#include "afxwin.h"

#include "EdgeAPM.h"

// CAPMUtilityDlg dialog
class CAPMUtilityDlg : public CDialog
{
// Construction
public:
	CAPMUtilityDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	enum { IDD = IDD_APMUTILITY_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support


// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	afx_msg LRESULT OnAPMStatusChanged(WPARAM wParam, LPARAM lParam);
	DECLARE_MESSAGE_MAP()
public:
	void UpdateNow(const EdgeAPM::APMSetting *pAPMSetting);
	CProgressCtrl m_ctrlProgress;	
	
	CStatic m_lblStep;

private:
	HANDLE m_hUpdateThread;
	UINT   m_nStep;		//current step  the update process going on
	HANDLE m_hCancelUpdateEvent;
	CString  m_strCaption;
	EdgeAPM::APMSetting m_apmSetting;

public:				
	HANDLE GetCancelUpdateEvent();
	void PostNcDestroy();
	void DestroyParentDlg();
	void DestroySelf();
	const EdgeAPM::APMSetting* GetApmSetting();
};
