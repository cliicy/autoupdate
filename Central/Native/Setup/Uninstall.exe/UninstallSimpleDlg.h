
// UninstallSimpleDlg.h : header file
//

#pragma once


// CUninstallSimpleDlg dialog
class CUninstallSimpleDlg : public CDialog
{
// Construction
public:
	CUninstallSimpleDlg(CWnd* pParent = NULL);	// standard constructor
	BOOL IsUninstallFailed(){return m_bUnintallFailed;};
// Dialog Data
	enum { IDD = IDD_UNINSTALLSIMPLE_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support


// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()

	afx_msg void OnShowWindow(BOOL bShow, UINT nStatus);
	afx_msg LRESULT OnFinish(WPARAM wParam, LPARAM lParam);
	afx_msg void OnTimer(UINT_PTR nIDEvent);
	afx_msg LRESULT OnRemoveStart(WPARAM wParam, LPARAM lParam);
	afx_msg LRESULT OnChangeStatus(WPARAM wParam, LPARAM lParam);
private:
	BOOL m_bStart;
	BOOL m_bUnintallFailed;
	int m_nSteps;
	int m_nCurrentPos; //current position
	int m_nProductPos; //product progress postion
	CProgressCtrl m_ctrProgress;
};
