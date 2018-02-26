// SetupLicenseDlg.cpp : implementation file
//

#include "stdafx.h"
#include "Uninstall.h"
#include "SetupSheet.h"
#include "MessagePage.h"
#include "ComAPI.h"

#ifndef UNICODE
#include <io.h>
#endif

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////
// CMessagePage property page

IMPLEMENT_DYNCREATE(CMessagePage, CSetupPage)

CMessagePage::CMessagePage() : CSetupPage(CMessagePage::IDD, 0, 0, 0)
{
	m_nNagivageTitleID = IDS_STRING_MESSAGE;
}

CMessagePage::~CMessagePage()
{
}

void CMessagePage::DoDataExchange(CDataExchange* pDX)
{
	CSetupPage::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CMessagePage)
	DDX_Control(pDX, IDC_STATIC_MSG_TOP, m_ctrMsgTopStatic);
	DDX_Control(pDX, IDC_STATIC_MSG_HEAD, m_ctrMsgHeadStatic);
	DDX_Control(pDX, IDC_STATIC_MSG_PIC, m_ctrMsgPicStatic);
	DDX_Control(pDX, IDC_STATIC_MSG_PIC2, m_ctrMsgPicStatic2);
	DDX_Control(pDX, IDC_STATIC_MSG_PIC3, m_ctrMsgPicStatic3);
	DDX_Control(pDX, IDC_STATIC_MSG_PIC4, m_ctrMsgPicStatic4);
	DDX_Control(pDX, IDC_STATIC_MSG_PIC5, m_ctrMsgPicStatic5);
	DDX_Control(pDX, IDC_STATIC_MSG_PIC6, m_ctrMsgPicStatic6);
	DDX_Control(pDX, IDC_STATIC_MSG_CONTENT, m_ctrMsgContentStatic);
	DDX_Control(pDX, IDC_STATIC_MSG_CONTENT2, m_ctrMsgContentStatic2);
	DDX_Control(pDX, IDC_STATIC_MSG_CONTENT3, m_ctrMsgContentStatic3);
	DDX_Control(pDX, IDC_STATIC_MSG_CONTENT4, m_ctrMsgContentStatic4);
	DDX_Control(pDX, IDC_STATIC_MSG_CONTENT5, m_ctrMsgContentStatic5);
	DDX_Control(pDX, IDC_STATIC_MSG_CONTENT6, m_ctrMsgContentStatic6);
	
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CMessagePage, CSetupPage)
	//{{AFX_MSG_MAP(CMessagePage)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CMessagePage message handlers

BOOL CMessagePage::OnInitDialog() 
{
	CSetupPage::OnInitDialog();	

	CRect rect(10,10,465,350);

	CWnd *pWnd = GetDlgItem(IDC_LIST_MSG);
	if (pWnd)
	{
		pWnd->ShowWindow(SW_HIDE);
		pWnd->GetWindowRect(&rect);
		ScreenToClient(rect);
	}

	//Create Image list
	m_ImageList.Create(16, 16, ILC_COLOR24|ILC_MASK, 4, 4);

	CBitmap bm;
	bm.LoadBitmap(IDB_MESSAGE_ICONS);
	m_ImageList.Add(&bm, RGB(236, 233, 216));

	m_ctrMsgTopStatic.SetFontBold(TRUE);

	SetStaticStyle();

	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}

void CMessagePage::SetStaticStyle()
{
	m_staticPicArray.RemoveAll();
	m_statiContentArray.RemoveAll();

	//static 1
	LONG style = GetWindowLong(m_ctrMsgPicStatic.GetSafeHwnd(), GWL_STYLE);
	style |= SS_ICON;  
	style |= SS_CENTERIMAGE;   
	SetWindowLong(m_ctrMsgPicStatic.GetSafeHwnd(),GWL_STYLE,style);
	m_staticPicArray.AddTail(&m_ctrMsgPicStatic);
	m_statiContentArray.AddTail(&m_ctrMsgContentStatic);

	//static 2
	style = GetWindowLong(m_ctrMsgPicStatic2.GetSafeHwnd(), GWL_STYLE);
	style |= SS_ICON;  
	style |= SS_CENTERIMAGE;   
	SetWindowLong(m_ctrMsgPicStatic2.GetSafeHwnd(),GWL_STYLE,style);
	m_staticPicArray.AddTail(&m_ctrMsgPicStatic2);
	m_statiContentArray.AddTail(&m_ctrMsgContentStatic2);

	//static 3
	style = GetWindowLong(m_ctrMsgPicStatic3.GetSafeHwnd(), GWL_STYLE);
	style |= SS_ICON;  
	style |= SS_CENTERIMAGE;   
	SetWindowLong(m_ctrMsgPicStatic3.GetSafeHwnd(),GWL_STYLE,style);
	m_staticPicArray.AddTail(&m_ctrMsgPicStatic3);
	m_statiContentArray.AddTail(&m_ctrMsgContentStatic3);

	//static 4
	style = GetWindowLong(m_ctrMsgPicStatic4.GetSafeHwnd(), GWL_STYLE);
	style |= SS_ICON;  
	style |= SS_CENTERIMAGE;   
	SetWindowLong(m_ctrMsgPicStatic4.GetSafeHwnd(),GWL_STYLE,style);
	m_staticPicArray.AddTail(&m_ctrMsgPicStatic4);
	m_statiContentArray.AddTail(&m_ctrMsgContentStatic4);

	//static 5
	style = GetWindowLong(m_ctrMsgPicStatic5.GetSafeHwnd(), GWL_STYLE);
	style |= SS_ICON;  
	style |= SS_CENTERIMAGE;   
	SetWindowLong(m_ctrMsgPicStatic5.GetSafeHwnd(),GWL_STYLE,style);
	m_staticPicArray.AddTail(&m_ctrMsgPicStatic5);
	m_statiContentArray.AddTail(&m_ctrMsgContentStatic5);

	//static 6
	style = GetWindowLong(m_ctrMsgPicStatic6.GetSafeHwnd(), GWL_STYLE);
	style |= SS_ICON;  
	style |= SS_CENTERIMAGE;   
	SetWindowLong(m_ctrMsgPicStatic6.GetSafeHwnd(),GWL_STYLE,style);
	m_staticPicArray.AddTail(&m_ctrMsgPicStatic6);
	m_statiContentArray.AddTail(&m_ctrMsgContentStatic6);
}

void CMessagePage::InitMsgStaticWindows()
{
	POSITION posPic,posContent;
	//reset the head message for default
	CString strHead;
	strHead.LoadString(IDS_STRING_NO_MSG);
	m_ctrMsgHeadStatic.SetWindowText(strHead);

	//hide all message static windows
	for (INT_PTR i=0;i<m_staticPicArray.GetSize() && i<m_statiContentArray.GetSize(); i++)
	{
		posPic = m_staticPicArray.FindIndex(i);
		if(posPic == NULL)
		{
			break;
		}

		CStatic *pStatic = (CStatic*)m_staticPicArray.GetAt(posPic);

		if(pStatic != NULL)
		{
			pStatic->ShowWindow(SW_HIDE);
		}

		posContent = m_statiContentArray.FindIndex(i);
		if(posPic == NULL)
		{
			break;
		}

		CStatic *pContentStatic = (CStatic*)m_statiContentArray.GetAt(posContent);

		if(pContentStatic != NULL)
		{
			pContentStatic->ShowWindow(SW_HIDE);
		}
	}
}

BOOL CMessagePage::OnSetActive() 
{
	CSetupSheet *pSheet = (CSetupSheet *)GetParent();
	ASSERT_KINDOF(CPropertySheet, pSheet);

	CWnd *pWnd = pSheet->GetDlgItem(ID_WIZBACK);
	if (pWnd)
	{
		pWnd->EnableWindow(TRUE);
	}

	GetMessages();

	ShowMessages();

	return CSetupPage::OnSetActive();
}

void CMessagePage::ShowMessages()
{
	//first hide all message 
	InitMsgStaticWindows();

	INT_PTR nSize = theApp.m_aryMessages.GetSize();
	POSITION posPic,posContent;

	for (INT_PTR i=0; i<nSize && i<MAX_MSG_COUNT && i<m_staticPicArray.GetSize() && i<m_statiContentArray.GetSize(); i++)
	{
		posPic = m_staticPicArray.FindIndex(i);
		if(posPic == NULL)
		{
			break;
		}

		CStatic *pStatic = (CStatic*)m_staticPicArray.GetAt(posPic);

		if(pStatic == NULL)
		{
			break;
		}

		posContent = m_statiContentArray.FindIndex(i);
		if(posPic == NULL)
		{
			break;
		}

		CStatic *pContentStatic = (CStatic*)m_statiContentArray.GetAt(posContent);

		if(pContentStatic == NULL)
		{
			break;
		}

		if(i==0)
		{
			//reset the head message
			CString strHead;
			strHead.LoadString(IDS_STRING_MSG_HEAD);
			m_ctrMsgHeadStatic.SetWindowText(strHead);
		}

		//m_ctrMessageList.InsertItem(theApp.m_aryMessages[i].strMsg, theApp.m_aryMessages[i].nIconIndex, NORMAL_TEXT)
		pStatic->SetIcon(m_ImageList.ExtractIcon(theApp.m_aryMessages[i].nIconIndex));
		pStatic->ShowWindow(SW_SHOW);
		pContentStatic->SetWindowText(theApp.m_aryMessages[i].strMsg);
		pContentStatic->ShowWindow(SW_SHOW);
	}
}

void CMessagePage::GetMessages()
{
	theApp.m_aryMessages.RemoveAll();

	TCHAR szServiceName[MAX_PATH];
	CString strMsg;

	//Get the UA status.
	theApp.GetUAserviceStatus();
	if(theApp.IsNeedHandleUAService())
	{
		ZeroMemory(szServiceName,sizeof(szServiceName));
		GetServiceDisplayName(SERVICE_CASUNIVERSALAGENT,szServiceName,_countof(szServiceName));
		strMsg.Format(IDS_STRING_RESTART_SERVICE,szServiceName);
		theApp.AddMessage(strMsg, ICON_WARNING);
	}

	//get the restart service list
	theApp.GetNeedRestartSpecService(theApp.m_strRestartSpecServices);
	for(int i=0; i<theApp.m_strRestartSpecServices.GetCount();i++)
	{
		if(IsServiceStopped(theApp.m_strRestartSpecServices[i]))
		{
			//service doesn't start, no need show the message.
			theApp.WriteLog(_T("The service(%s) doesn't start, no need to add the restart message for it."),theApp.m_strRestartSpecServices[i]);
			continue;
		}
		else
		{
			//service doesn't start, no need show the message.
			theApp.WriteLog(_T("The service(%s) is running, and need add the restart message for it."),theApp.m_strRestartSpecServices[i]);
		}

		ZeroMemory(szServiceName,sizeof(szServiceName));
		GetServiceDisplayName(theApp.m_strRestartSpecServices[i],szServiceName,_countof(szServiceName));
		strMsg.Format(IDS_STRING_RESTART_SERVICE,szServiceName);
		theApp.AddMessage(strMsg, ICON_WARNING);
	}
	//end the restart service list
}

LRESULT CMessagePage::OnWizardNext() 
{
	return CSetupPage::OnWizardNext();
}

LRESULT CMessagePage::OnWizardBack() 
{
	return CSetupPage::OnWizardBack();
}