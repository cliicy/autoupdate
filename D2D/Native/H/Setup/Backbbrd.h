/////////////////////////////////////////////////////////////////////////////
// BACKBBRD.H	CBackgroundBillboard interface
// Copyright (C), 1995, Jetstream Software, Inc. All rights reserved.
//
// This software is protected by copyright law. Unauthorized reproduction
// or distribution of this program, or any portion of it, may result in
// severe civil or criminal penalties. For more information, contact:
// Jetstream Software, Inc. 218 Main Street, Suite 107, Kirkland, WA 98033
// (206) 827-9273 email:73677.3676@compuserve.com
/////////////////////////////////////////////////////////////////////////////

// IWGENERATED


#ifndef _BACKBBRD_H_
#define _BACKBBRD_H_

//////////////////////////////////////
// Includes, forwards
//////////////////////////////////////

#include "billbrd.h"	// for parent class CBillboard
class CXBitmap;


class CBackgroundBillboard : public CBillboard
{
//////////////////////////////////////
// Constructors, Destructors, Inits
//////////////////////////////////////

public:
	CBackgroundBillboard (UINT nResourceID,
						  UINT nBitmapResourceID,
						  BOOL bResizeClient = TRUE,
						  CWnd* pParent = NULL);


//////////////////////////////////////
// Message Handlers
//////////////////////////////////////

protected:
	// Generated message map functions
	//{{AFX_MSG(CBackgroundBillboard)
	virtual BOOL OnInitDialog();
	afx_msg void OnDestroy();
	afx_msg void OnSize(UINT nType, int cx, int cy);
	afx_msg HBRUSH OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor);
	afx_msg BOOL OnEraseBkgnd(CDC* pDC);
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()


//////////////////////////////////////
// Class data members
//////////////////////////////////////

protected:
	BOOL		m_bResizeClient;
	UINT		m_nBitmapID;
	CBrush		m_HollowBrush;
	CXBitmap*	m_pBitmap;
};


#endif	// !_BACKBBRD_H_

 

