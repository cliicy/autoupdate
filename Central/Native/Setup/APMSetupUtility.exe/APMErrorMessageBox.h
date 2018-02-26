#pragma once


// CAPMErrorMessageBox dialog

class CAPMErrorMessageBox : public CDialog
{
	DECLARE_DYNAMIC(CAPMErrorMessageBox)

public:
	CAPMErrorMessageBox(CWnd* pParent = NULL);   // standard constructor
	virtual ~CAPMErrorMessageBox();

// Dialog Data
	enum { IDD = IDD_APM_ERROR };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

	DECLARE_MESSAGE_MAP()
public:
	afx_msg void OnBnClickedOk();
};
