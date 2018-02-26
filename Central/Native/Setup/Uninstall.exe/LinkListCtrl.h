#pragma once

#include <map>
using namespace std;

// CLinkListCtrl
typedef struct {
	BOOL IsHoverOn;
	CString strURL;
	CString strTooltip;
} stURLTAG;

class CLinkListCtrl : public CListCtrl
{
	DECLARE_DYNAMIC(CLinkListCtrl)

public:
	CLinkListCtrl();
	virtual ~CLinkListCtrl();

protected:
	DECLARE_MESSAGE_MAP()

public:
	BOOL SetItemURL(int nItem, int nSubItem, CString strURL, CString strTooltip);
	void ClearItemURL(int nItem, int nSubItem);

protected:
	CRect GetTextRect(int nItem, int nSubItem);
	BOOL PtInText(CPoint pt, int nItem, int nSubItem);
	BOOL IsURL(int nItem, int nSubItem);
	void RedrawSubItem(int nItem, int nSubItem, BOOL IsHoverOn);

private:
	map<int, stURLTAG*> m_mapURL;
	CFont m_ftUnderline;
	CFont m_ftURL;
	CToolTipCtrl m_toolTip;
	
protected:
	virtual void PreSubclassWindow();

public:
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg void OnNMCustomdraw(NMHDR *pNMHDR, LRESULT *pResult);
	virtual BOOL PreTranslateMessage(MSG* pMsg);
};


