
#pragma once

class CHyperlink : public CString
{
public:
	CHyperlink(LPCTSTR lpLink = NULL) : CString(lpLink) { }
	~CHyperlink() { }
	const CHyperlink& operator=(LPCTSTR lpsz)
	{
		CString::operator=(lpsz);
		return *this;
	}
	operator LPCTSTR() {
		return CString::operator LPCTSTR(); 
	}

	// Navigate the link. Use rundll32.exe
	BOOL Navigate()
	{
		return IsEmpty() ? NULL :
			((INT64)ShellExecute(0, _T("open"), *this, 0, 0, SW_SHOWNORMAL) > 32);
	}
};


class CStaticLink : public CStatic
{
public:
	CStaticLink(LPCTSTR lpLink = NULL, BOOL bDeleteOnDestroy=FALSE);
	~CStaticLink() {}

	// Use this if you want to subclass
	BOOL SubclassDlgItem(UINT nID, CWnd* pParent, LPCTSTR lpszLink=NULL)
	{
		m_link = lpszLink;

		return CStatic::SubclassDlgItem(nID, pParent);
	}

	BOOL Navigate(); // click

	// (GetWindowText) to get the target.
	COLORREF m_color;
	CHyperlink m_link;

	// Default colors you can change
	// These are global, so they're the same for all links.
	static COLORREF g_colorUnvisited;
	static COLORREF g_colorVisited;

	// Cursor used when mouse is on a link--you can set, or
	// it will default to the standard hand with pointing finger.
	// This is global, so it's the same for all links.
	static HCURSOR	 g_hCursorLink;

protected:
	CFont m_font;					// underline font for text control
	BOOL m_bDeleteOnDestroy;	// delete object when window destroyed?

	void DrawFocusRect();	// draw focus rectangle

	virtual void PostNcDestroy();

	// message handlers
	DECLARE_MESSAGE_MAP()
	afx_msg HBRUSH CtlColor(CDC* pDC, UINT nCtlColor);
	afx_msg LRESULT OnNcHitTest(CPoint point);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	afx_msg BOOL OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message);
	afx_msg UINT OnGetDlgCode();
	afx_msg void OnChar(UINT nChar, UINT nRepCnt, UINT nFlags);
	afx_msg void OnSetFocus(CWnd* pOldWnd);
	afx_msg void OnKillFocus(CWnd* pNewWnd);
	DECLARE_DYNAMIC(CStaticLink)
};
