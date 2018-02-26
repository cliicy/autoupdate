// CAMenu.h: interface for the CCAMenu class.
//
//////////////////////////////////////////////////////////////////////

#pragma once

// informations for menu items
#define MESSAGE_MAX_LEN MAX_PATH

class CItemInfo
{
public:
	HICON hIconNormal;
	HICON hIconSelect;
	TCHAR sText[MESSAGE_MAX_LEN];
};

class CCAMenu : public CMenu  
{
public:
	// construction / destruction
	CCAMenu();
	virtual ~CCAMenu();

	void SetWidth(int nWidth);

	inline int GetWidth(){return m_nWidth;};

	// general functions
	void DrawBgClr(CDC* pDC, CRect rect, BOOL bSelected);
	void DrawIcon(CDC* pDC, CRect rect, HICON hIconNormal, HICON hIconSelect, BOOL bSelected);
	void DrawText(CDC* pDC, CRect rect, CString sText);

	// virtual functions
	virtual void DrawItem(LPDRAWITEMSTRUCT);
	virtual void MeasureItem(LPMEASUREITEMSTRUCT lpMIS);

protected:
	int m_nWidth;
	int m_nHeight;
};
