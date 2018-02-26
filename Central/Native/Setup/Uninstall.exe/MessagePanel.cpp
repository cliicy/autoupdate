#include "stdafx.h"
#include "MessagePanel.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

#define ID_MESSAGE_BUTTON	0X6799

#define DEFAULT_HEIGHT		28
/////////////////////////////////////////////////////////////////////////////

CMessagePanel::CMessagePanel()
{
	//Set default height
	m_nHeight = DEFAULT_HEIGHT;

	m_cBackground = ::GetSysColor(COLOR_WINDOW);

	m_nxEdge = ::GetSystemMetrics(SM_CXEDGE);

	//m_nyEdge=::GetSystemMetrics(SM_CYEDGE);
	m_nyEdge = 1;
}

CMessagePanel::~CMessagePanel()
{
}


BEGIN_MESSAGE_MAP(CMessagePanel, CMessagePanel_Base)
	//{{AFX_MSG_MAP(CMessagePanel)
	ON_WM_ERASEBKGND()
	ON_WM_PAINT()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()


BOOL CMessagePanel::OnEraseBkgnd(CDC* pDC) 
{
	return TRUE;

	//return CWnd::OnEraseBkgnd(pDC);
}

void CMessagePanel::OnPaint() 
{
	CPaintDC dc(this);	//Device context for painting
	CRect rect;		//General purpose rect
	CRect text;		//Rect for enclosing text
	int temp=0;		//Temp variable for right side of text rect

	GetClientRect(rect);

	dc.Draw3dRect(rect, GetSysColor(COLOR_BTNHIGHLIGHT), GetSysColor(COLOR_BTNSHADOW));

	dc.FillSolidRect(rect.left, rect.top, rect.Width(), rect.Height() - m_nxEdge,
		m_cBackground);

	// Do not call CWnd::OnPaint() for painting messages
}

void CMessagePanel::SetHeight(const int height)
{
	m_nHeight = height;

	if (NULL != m_hWnd)
	{
		Invalidate();
	}
}

//Set new background color
void CMessagePanel::SetBackgroundColor(const COLORREF color)
{
	m_cBackground = color;

	if (NULL != m_hWnd)
	{
		Invalidate();
	}
}


void CMessagePanel::Init(CWnd* pParentWnd, CRect rect)
{
	CWnd* pWnd = pParentWnd;	//Ptr to calling window

	if ((NULL == pWnd) || (NULL == pParentWnd->m_hWnd))
	{
		return;
	}

	CreateEx(NULL, NULL, NULL, WS_CHILD | WS_VISIBLE | WS_TABSTOP/* | WS_BORDER*/, 
		-1, -1, rect.Width(), GetHeight(), pWnd->m_hWnd, 0, 0);

	CRect rButton;
	GetClientRect(rButton);
	rButton.bottom -= 2;

	m_ctrButton.Create(_T("My button"), WS_CHILD|WS_VISIBLE|WS_TABSTOP|BS_PUSHBUTTON|BS_LEFT|BS_FLAT,
		rButton, this, ID_MESSAGE_BUTTON);
}

void CMessagePanel::AddMessage(CString strMsg, MESSAGE_TYPE type)
{
	MESSAGE_ITEM item;
	item.strMessage = strMsg;

	switch (type)
	{
	case MSG_TYPE_ERROR:
		item.uImageIndex = ICON_ERROR;
		break;
	case MSG_TYPE_WARNING:
		item.uImageIndex = ICON_WARNING;
		break;
	case MSG_TYPE_INFORMATION:
		item.uImageIndex = ICON_INFORMATION;
		break;
	default:
		item.uImageIndex = ICON_ERROR;
		break;
	}

	m_ctrButton.AddMessage(item);
}

void CMessagePanel::RemoveAllMessage()
{
	m_ctrButton.RemoveAllMessage();
}
