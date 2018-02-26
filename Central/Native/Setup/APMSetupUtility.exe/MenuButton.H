#pragma once

#include <afxtempl.h>

// MenuButton.h : header file

//
enum MESSAGE_TYPE {MSG_TYPE_ERROR, MSG_TYPE_WARNING, MSG_TYPE_INFORMATION};

enum MESSAGE_ICON {ICON_ARROW, ICON_ERROR, ICON_WARNING, ICON_INFORMATION};

#define DEFAULT_BACK_COLOR RGB(192, 211, 252)
#define DEFAULT_HOT_COLOR RGB(168, 191, 251)

typedef struct _tagMessageItem
{
	MESSAGE_ICON uImageIndex;
	CString strMessage;
}MESSAGE_ITEM;


class CMenuButton : public CButton
{
public:

	CMenuButton();

	virtual ~CMenuButton();

	DECLARE_DYNAMIC( CMenuButton )

	void SetDrawFocusRect(BOOL bDrawFocusRect=TRUE);

	void SetStyle(BOOL bDrawArrow=TRUE);

	void AddMessage(MESSAGE_ITEM item);

	BOOL RemoveMessage(int nIndex);

	void RemoveAllMessage();

	inline INT_PTR GetMessageSize(){return m_aryMessages.GetSize();};

	virtual void DrawItem(LPDRAWITEMSTRUCT lpDrawItemStruct);

protected:
	//{{AFX_VIRTUAL(CMenuButton)
	virtual void PreSubclassWindow();
	//}}AFX_VIRTUAL


protected:

	BOOL m_bDrawFocusRect;

	COLORREF m_cBackground;

	CArray <MESSAGE_ITEM, MESSAGE_ITEM&> m_aryMessages;

	CImageList m_MessageTypeImages;

	int m_nICONcx, m_nICONcy;

	HICON m_hICONArrow;


	BOOL m_bMouseOverButton;

	//{{AFX_MSG(CMenuButton)
	afx_msg LRESULT OnMouseLeave(WPARAM, LPARAM);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg BOOL OnClicked();
	//}}AFX_MSG

	DECLARE_MESSAGE_MAP()
};