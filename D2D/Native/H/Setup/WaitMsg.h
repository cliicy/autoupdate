#if !defined(AFX_WAITCREATEDLG_H__73DD9A34_F8B7_11D1_BD01_006097B72B24__INCLUDED_)
#define AFX_WAITCREATEDLG_H__73DD9A34_F8B7_11D1_BD01_006097B72B24__INCLUDED_

#if _MSC_VER >= 1000
#pragma once
#endif // _MSC_VER >= 1000
// WaitCreateDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CWaitCreateDlg dialog

class CWaitCreateDlg : public CDialog
{
// Construction
public:
	CWaitCreateDlg(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CWaitCreateDlg)
	enum { IDD = IDD_WAIT_CREATEDB };
		// NOTE: the ClassWizard will add data members here
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CWaitCreateDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CWaitCreateDlg)
		// NOTE: the ClassWizard will add member functions here
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Developer Studio will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_WAITCREATEDLG_H__73DD9A34_F8B7_11D1_BD01_006097B72B24__INCLUDED_)
