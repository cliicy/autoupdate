#pragma once

#include "StatusCtrl.h"
#include "resource.h"
#include "StatLink.h"

#include <afxtempl.h>
// CNavationCtrl.h : header file
//
/////////////////////////////////////////////////////////////////////////////
// CNavationCtrl dialog
enum IMAGE_INDEX{IMAGE_FINISH = 0, IMAGE_DOING, IMAGE_PENDING, IMAGE_WRONG};
enum PHASE_INDEX{PHASE_FINISH = 0, PHASE_DOING, PHASE_PENDING, PHASE_WRONG};
enum LEVEL_INDEX{LEVEL_TOP = 0, LEVEL_FIRSET, LEVEL_SECOND, LEVEL_MAX};

enum PHASE_IDS
{
	PHASE_MIN = 0x00L,
	PHASE_1 = 0x01L,
	PHASE_2 = 0x02L,
	PHASE_3 = 0x03L,
	PHASE_4 = 0x04L,
	PHASE_MAX
};

enum PHASE_ID
{
	PHASE_COMPONENTS = PHASE_1 << 24,
	PHASE_MESSAGE = PHASE_2 << 24,
	PHASE_UNINSTALLATION = PHASE_3 << 24
};

typedef struct _tagStateItem
{
	UINT nIDHeaderTitle;
	DWORD dwID;
	BOOL bShow;
	PHASE_INDEX nPhase;
}STATE_ITEM;

typedef CArray <STATE_ITEM, STATE_ITEM&> ARRAY_STATUS;

class CNavationCtrl : public CDialog
{
	// Construction
public:
	CNavationCtrl(CWnd* pParent = NULL);   // standard constructor

	// Dialog Data
	//{{AFX_DATA(CNavationCtrl)
	enum { IDD = IDD_STATUS_PANEL};

	CStatusCtrl m_ctrStatus;

	ARRAY_STATUS m_aryStates;

	// NOTE: the ClassWizard will add data members here
	//}}AFX_DATA

	// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CNavationCtrl)
protected:
	virtual void OnOK();
	virtual void OnCancel();
	virtual BOOL OnInitDialog();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

	// Implementation
public:

	inline ARRAY_STATUS* GetAllStatus(){return &m_aryStates;};

	inline void SetAllStatus(ARRAY_STATUS* status)
	{
		m_aryStates.RemoveAll();
		m_aryStates.Append(*status);
	};

	void SetCurrentStatusByHeaderTitleID(UINT nIDHeaderTitle);

	void SetCurrentStatus(DWORD dwID);

	void EnableStatusByHeaderTitleID(UINT nHeaderTitle, BOOL bEnalbe = TRUE, BOOL bUpdate = TRUE);

	void EnableStatus(DWORD dwID, BOOL bEnalbe = TRUE, BOOL bUpdate = TRUE);

	void EnableStatus(DWORD dwIDBegin, DWORD dwIDEnd, BOOL bEnalbe = TRUE, BOOL bUpdate = TRUE);

	void ShowStatus();

	void RePositionControl();

	void GetStatusFromFile(CString strFileName);

	void SetStatusToFile(CString strFileName);

	void InitStatus();

private:
	void LocadStatusFromRC();

	int GetIndexFromID(DWORD dwID);

	DWORD GetParentID(DWORD dwID);

	int GetStatusImageIndex(PHASE_INDEX nStatus);

	void CreateStatusTree();

	HTREEITEM GetTreeItem(int nIndex);

	BOOL IsIEInstalled();

protected:
	CImageList m_imagePhase;
	CBrush m_brush;

	COLORREF m_crBkColor;
	COLORREF m_crTextBkColor;
	COLORREF m_crTextColor;

	// Generated message map functions
	//{{AFX_MSG(CNavationCtrl)
	afx_msg HBRUSH OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor);

	afx_msg BOOL OnEraseBkgnd(CDC* pDC);
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};