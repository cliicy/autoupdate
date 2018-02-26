#pragma once

#include "NavigatePropertyPage.h"
#include "NavationCtrl.h"
#include "MessagePanel.h"

/////////////////////////////////////////////////////////////////////////////
// CNavigatePropertySheet

enum NAVIGATE_STATUS {FINISH, DOING, PENDING};

#define ID_NAVIGATION_CTRL 0x2345

#define CNavigatePropertySheet_Base CPropertySheetEx

class CNavigatePropertySheet : public CNavigatePropertySheet_Base
{
	DECLARE_DYNAMIC(CNavigatePropertySheet)

// Construction
public:
  CNavigatePropertySheet();

  CNavigatePropertySheet(UINT nIDCaption, CWnd* pParentWnd = NULL,
	  UINT iSelectPage = 0, HBITMAP hbmWatermark = NULL,
	  HPALETTE hpalWatermark = NULL, HBITMAP hbmHeader = NULL);

  CNavigatePropertySheet(LPCTSTR pszCaption, CWnd* pParentWnd = NULL,
	  UINT iSelectPage = 0, HBITMAP hbmWatermark = NULL,
	  HPALETTE hpalWatermark = NULL, HBITMAP hbmHeader = NULL);

// Attributes
public:
	CNavationCtrl m_ctrNavigate;

	CRect m_rectPageDefault;

// Operations
public:
//  BOOL CheckKey(char cAccel);
// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CNavigatePropertySheet)
	virtual BOOL OnInitDialog();
	//}}AFX_VIRTUAL

// Implementation
public:
	virtual ~CNavigatePropertySheet();

	// Generated message map functions
protected:
	//{{AFX_MSG(CNavigatePropertySheet)
	afx_msg int OnCreate(LPCREATESTRUCT lpCreateStruct);
	afx_msg void OnSize(UINT nType, int cx, int cy);
	//}}AFX_MSG
  

	DECLARE_MESSAGE_MAP()

	void Init();

	void MoveChildWindows(int nDx, int nDy);

protected:
		
	int m_nWidth;

	BOOL m_bInited;

	CRect m_rectSheet;
	
	CRect m_rectPage;

	BOOL m_blShowNavigationBar;

	BOOL m_blShowMessagePanel;

	CMessagePanel m_ctrMessagePanel;

public:
	//Page functions
	void AddPage(CNavigatePropertyPage *pPage);

	inline CRect GetPageRect(){return m_rectPage;};

	//Status Panel functions
	void SetNavigationBarWidth(int nWidth);

	void HideNavigationBar();

	void ShowNavigationBar();

	inline BOOL IsShowNavigationBar(){return m_blShowNavigationBar;};

	inline int GetNavigationWidth(){return m_nWidth;};

	void SetCurrentStatusByHeaderTitleID(UINT nHeaderTitleID);

	void SetCurrentStatus(DWORD dwID);

	void EnableStatusByHeaderTitleID(UINT nHeaderTitleID, BOOL bEnalbe = TRUE, BOOL bUpdate = TRUE);

	void EnableStatus(DWORD dwID, BOOL bEnalbe = TRUE, BOOL bUpdate = TRUE);

	void EnableStatus(DWORD dwIDBegin, DWORD dwIDEnd, BOOL bEnalbe = TRUE, BOOL bUpdate = TRUE);

	ARRAY_STATUS* GetAllStatus();

	void SetAllStatus(ARRAY_STATUS* status);

	void GetStatusFromFile(CString strFileName);

	void SetStatusToFile(CString strFileName);

	//Message Panel functions
	void AddMessage(CString strMsg, MESSAGE_TYPE type);

	void RemoveAllMessage();

	void HideMessagePanel();

	void ShowMessagePanel();
};

/////////////////////////////////////////////////////////////////////////////