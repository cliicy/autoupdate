#pragma once

#define CNavigatePropertyPage_Base CPropertyPage

class CNavigatePropertyPage : public CNavigatePropertyPage_Base
{
	DECLARE_DYNAMIC(CNavigatePropertyPage)
public:
	CNavigatePropertyPage();
	CNavigatePropertyPage(UINT nIDTemplate, UINT nIDCaption = 0,
		UINT nIDHeaderTitle = 0, UINT nIDHeaderSubTitle = 0);

//	CNavigatePropertyPage(UINT nIDTemplate, UINT nIDCaption = 0);
//	CNavigatePropertyPage(LPCTSTR lpszTemplateName, UINT nIDCaption = 0);

	// ClassWizard generate virtual function overrides
	//{{AFX_VIRTUAL(CPage1)
	virtual BOOL OnSetActive();
	//}}AFX_VIRTUAL
protected:
	UINT m_nNagivageTitleID;
};
