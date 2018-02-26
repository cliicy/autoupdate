// APMErrorMessageBox.cpp : implementation file
//

#include "stdafx.h"
#include "APMUtility.h"
#include "APMErrorMessageBox.h"


// CAPMErrorMessageBox dialog

IMPLEMENT_DYNAMIC(CAPMErrorMessageBox, CDialog)

CAPMErrorMessageBox::CAPMErrorMessageBox(CWnd* pParent /*=NULL*/)
	: CDialog(CAPMErrorMessageBox::IDD, pParent)
{

}

CAPMErrorMessageBox::~CAPMErrorMessageBox()
{
}

void CAPMErrorMessageBox::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
}


BEGIN_MESSAGE_MAP(CAPMErrorMessageBox, CDialog)
	ON_BN_CLICKED(IDOK, &CAPMErrorMessageBox::OnBnClickedOk)
END_MESSAGE_MAP()


// CAPMErrorMessageBox message handlers

void CAPMErrorMessageBox::OnBnClickedOk()
{
	// TODO: Add your control notification handler code here
	OnOK();
}
