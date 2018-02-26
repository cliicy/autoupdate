
#ifndef _SPLASH_ABOUT_
#define _SPLASH_ABOUT_

// SplashAbout.h : header file
//
// This is the header file for the about box.
//

/////////////////////////////////////////////////////////////////////////////
//   The About box class

class CSplashAbout : public CWnd
{
// Construction
protected:
	CSplashAbout();

// Attributes:
public:
	CBitmap m_bitmap;
	BOOL	m_bChangeCursor;
	static CFont	m_font;
	static CButton  m_button;
	static CEdit	m_Edit;
	static CToolTipCtrl* m_pToolTip;
// Operations
public:
	static void EnableLicense(BOOL bEnable = TRUE);
	static void ShowSplashAbout(CWnd* pParentWnd = NULL);
	//virtual BOOL PreTranslateAppMessage(MSG* pMsg);

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CSplashAbout)
	public:
	virtual BOOL PreTranslateMessage(MSG* pMsg);
	//}}AFX_VIRTUAL

// Implementation
public:
	~CSplashAbout();
	virtual void PostNcDestroy();

protected:
	BOOL Create(CWnd* pParentWnd = NULL);
	void HideSplashAbout();
	static BOOL c_bShowLicense;
	static CSplashAbout* c_pSplashWnd;
	static CWnd*	c_pWnd;

private:
	HICON	m_hIcon;

// Generated message map functions
protected:
	//{{AFX_MSG(CSplashAbout)
	afx_msg int OnCreate(LPCREATESTRUCT lpCreateStruct);
	afx_msg void OnPaint();
    afx_msg HBRUSH OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg BOOL OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message);
	afx_msg void OnClickedOK();
	afx_msg void OnKillFocus(CWnd* pNewWnd);
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


#endif
