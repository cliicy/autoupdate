#pragma once

#include "SetupPage.h"
#include "MessageListCtrl.h"

//max size of showing messages
#define MAX_MSG_COUNT  6

/////////////////////////////////////////////////////////////////////////////
// CMessagePage dialog

class CMessagePage : public CSetupPage
{
	DECLARE_DYNCREATE(CMessagePage)

// Construction
public:
	CMessagePage();
	~CMessagePage();

// Dialog Data
	//{{AFX_DATA(CMessagePage)
	enum { IDD = IDD_DIALOG_MSG };
	//}}AFX_DATA

protected:
	CImageList m_ImageList;

	CMessageListCtrl m_ctrMessageList;
// Overrides
	// ClassWizard generate virtual function overrides
	//{{AFX_VIRTUAL(CMessagePage)
	virtual BOOL OnInitDialog();
	virtual BOOL OnSetActive();
	virtual LRESULT OnWizardNext();
	virtual LRESULT OnWizardBack();
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL
	DECLARE_MESSAGE_MAP()

// Implementation

private:
	CStaticEx m_ctrMsgTopStatic;
	CStatic m_ctrMsgHeadStatic;
	CStatic m_ctrMsgPicStatic;
	CStatic m_ctrMsgPicStatic2;
	CStatic m_ctrMsgPicStatic3;
	CStatic m_ctrMsgPicStatic4;
	CStatic m_ctrMsgPicStatic5;
	CStatic m_ctrMsgPicStatic6;
	CStatic m_ctrMsgContentStatic;
	CStatic m_ctrMsgContentStatic2;
	CStatic m_ctrMsgContentStatic3;
	CStatic m_ctrMsgContentStatic4;
	CStatic m_ctrMsgContentStatic5;
	CStatic m_ctrMsgContentStatic6;

	CObList m_staticPicArray;
	CObList m_statiContentArray;

	void GetMessages();
	void ShowMessages();
	void SetStaticStyle();
	void InitMsgStaticWindows();
};
