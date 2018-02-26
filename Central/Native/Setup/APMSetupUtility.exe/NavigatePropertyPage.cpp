#include "stdafx.h"
#include "NavigatePropertyPage.h"
#include "NavigatePropertySheet.h"

IMPLEMENT_DYNAMIC(CNavigatePropertyPage, CNavigatePropertyPage_Base)

CNavigatePropertyPage::CNavigatePropertyPage() : CNavigatePropertyPage_Base()
{
	m_nNagivageTitleID = 0;
}


CNavigatePropertyPage::CNavigatePropertyPage(UINT nIDTemplate, UINT nIDCaption/* = 0*/,
					  UINT nIDHeaderTitle/* = 0*/, UINT nIDHeaderSubTitle/* = 0*/) 
	: CNavigatePropertyPage_Base(nIDTemplate, nIDCaption, nIDHeaderTitle, nIDHeaderSubTitle)
{
	m_nNagivageTitleID = 0;
}


BOOL CNavigatePropertyPage::OnSetActive()
{
	BOOL blRet = CNavigatePropertyPage_Base::OnSetActive();

	CNavigatePropertySheet* pSheet = (CNavigatePropertySheet*)GetParent();
	ASSERT_KINDOF(CPropertySheetEx, pSheet);

	if ((m_psp.dwFlags & PSP_HIDEHEADER) == 0)
	{
		pSheet->ShowNavigationBar();
		MoveWindow(pSheet->GetPageRect());

		if (m_nNagivageTitleID > 0)
		{
			pSheet->SetCurrentStatusByHeaderTitleID(m_nNagivageTitleID);
		}
	}
	else
	{
		pSheet->HideNavigationBar();
		MoveWindow(pSheet->m_rectPageDefault);
	}
	//cxl
	pSheet->HideNavigationBar();
	//
	return blRet;
}
