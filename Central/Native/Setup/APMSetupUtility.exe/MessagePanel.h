#pragma once
// MessagePanel.h : header file
//
#include "MenuButton.H"

/////////////////////////////////////////////////////////////////////////////
// CMessagePanel CWnd

#define CMessagePanel_Base CWnd

class CMessagePanel : public CMessagePanel_Base
{
	// Construction
public:
	CMessagePanel();	//Default Constructor

	// Attributes
public:


	// Operations
public:
	void Init(CWnd* pParentWnd, CRect rect);

	COLORREF GetBackgroundColor() const	{return m_cBackground;}

	int GetHeight() const {return m_nHeight;}

	void SetBackgroundColor(const COLORREF color);

	void SetHeight(const int height);

	void AddMessage(CString strMsg, MESSAGE_TYPE type);

	void RemoveAllMessage();

	// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CMessagePanel)
	//}}AFX_VIRTUAL


	// Implementation
public:
	virtual ~CMessagePanel();

	CMenuButton m_ctrButton;

	// Generated message map functions
protected:
	//{{AFX_MSG(CMessagePanel)
	afx_msg BOOL OnEraseBkgnd(CDC* pDC);
	afx_msg void OnPaint();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()


	// Internal class functions
private:

	// Member Variables
protected:
	int m_nHeight;	//Height of Panel

	int m_nxEdge;			//System edge width
	int m_nyEdge;			//System edge height

	COLORREF m_cBackground;		//Background fill color
};