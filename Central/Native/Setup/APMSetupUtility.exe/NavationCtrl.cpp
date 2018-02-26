// NavationCtrl.cpp : implementation file
//

#include "stdafx.h"
#include "NavationCtrl.h"
//#include "APMSetupUtility.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

#define SECTION_STATUS	_T("Status")
#define KEY_NUMBER		_T("Number")
#define KEY_ITEM		_T("Item")
/////////////////////////////////////////////////////////////////////////////
// CNavationCtrl dialog

CNavationCtrl::CNavationCtrl(CWnd* pParent /*=NULL*/)
: CDialog(CNavationCtrl::IDD, pParent)
{
	m_crBkColor = RGB(255, 255, 255);
	m_crTextBkColor = m_crBkColor;
	m_crTextColor = RGB(0,0,0);
	m_brush.CreateSolidBrush(m_crBkColor);

	m_bShowLinks = IsIEInstalled();

	m_strWebPageLink = _T("");
	m_strReadmeLink = _T("");

	LocadStatusFromRC();

	InitStatus();

	//{{AFX_DATA_INIT(CNavationCtrl)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}


void CNavationCtrl::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CNavationCtrl)
	DDX_Control(pDX, IDC_TREE_STATUS, m_ctrStatus);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CNavationCtrl, CDialog)
	//{{AFX_MSG_MAP(CNavationCtrl)
	ON_WM_CTLCOLOR()
	ON_WM_ERASEBKGND()
	//ON_STN_CLICKED(IDC_STATIC_WEBSITE, OnWebSite)
	//ON_STN_CLICKED(IDC_STATIC_README, OnReadMe)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()


BOOL CNavationCtrl::OnInitDialog() 
{
	CDialog::OnInitDialog();

	m_ctrStatus.SetBkColor(m_crBkColor);

	m_ctrStatus.SetTextColor(m_crTextColor);
	//m_ctrStatus.SetTextColor(RGB(255, 0, 0));

	m_ctrStatus.SetFont(GetFont());

	m_ctrWebSite.SubclassDlgItem(IDC_STATIC_WEBSITE, this, m_strWebPageLink);

	m_ctrReadMe.SubclassDlgItem(IDC_STATIC_README, this, m_strReadmeLink);

	ShowLinks(m_bShowLinks);
	
	//Create Image list
	m_imagePhase.Create(24, 24, ILC_COLOR24, 20, 40);

	CBitmap bitmap;
	bitmap.LoadBitmap(IDB_BITMAP_STATUS);
	m_imagePhase.Add(&bitmap, RGB(0, 0, 0));

	m_ctrStatus.SetImageList(&m_imagePhase, LVSIL_SMALL);
	
	CreateStatusTree();
	
	return TRUE;  // return TRUE unless you set the focus to a control
	// EXCEPTION: OCX Property Pages should return FALSE
}


HBRUSH CNavationCtrl::OnCtlColor(CDC* pDC, CWnd* pWnd, UINT nCtlColor)
{
	HBRUSH hbr = CDialog::OnCtlColor(pDC, pWnd, nCtlColor);

	TCHAR classname[MAX_PATH];
	if (::GetClassName(pWnd->m_hWnd, classname, MAX_PATH) == 0)
	{
		return hbr;
	}

	if (_tcsicmp(classname, WC_TABCONTROL) == 0)
	{
		return hbr;
	}
	
	if (_tcsicmp(classname, WC_EDIT) == 0)
		return hbr;
	
	if (_tcsicmp(classname, WC_COMBOBOX) == 0)
		return hbr;
	
	if (_tcsicmp(classname, WC_COMBOBOXEX) == 0)
		return hbr;
	
	if (_tcsicmp(classname, WC_LISTBOX) == 0)
		return hbr;
	
	if (_tcsicmp(classname, WC_TREEVIEW) == 0)
		return hbr;
	
	if (_tcsicmp(classname, WC_IPADDRESS) == 0)
		return hbr;
	
	pDC->SetBkColor(m_crBkColor);
	
	if ((HBRUSH)m_brush == NULL)
		m_brush.CreateSolidBrush(m_crBkColor);

	return (HBRUSH) m_brush;
}


BOOL CNavationCtrl::OnEraseBkgnd(CDC* pDC)
{
	CDialog::OnEraseBkgnd(pDC);
	
	CRect r;
	GetClientRect(&r);

	if ((HBRUSH)m_brush == NULL)
		m_brush.CreateSolidBrush(m_crBkColor);
	
	pDC->FillRect(&r, &m_brush);
	
	return TRUE;
}


void CNavationCtrl::OnOK()
{

}


void CNavationCtrl::OnCancel()
{

}

void CNavationCtrl::LocadStatusFromRC()
{
	CString strFormat, strText;
	m_aryStates.RemoveAll();

	//////////////////////////////////////////////////////////////////////////
	//Firse Level Phase
	STATE_ITEM item1;
	item1.dwID = PHASE_CHECKUPDATE;
	item1.bShow = FALSE;
	item1.nPhase = PHASE_PENDING;
	item1.nIDHeaderTitle = IDS_STRING_CHECKFORUPDATE_SUBTITLE;
	m_aryStates.Add(item1);
	//////////////////////////////////////////////////////////////////////////
}

void CNavationCtrl::InitStatus()
{
	INT_PTR nSize = m_aryStates.GetSize();

	for (INT_PTR i=0; i<nSize; i++)
	{
		if (m_aryStates[i].dwID == PHASE_CHECKUPDATE)
		{
			m_aryStates[i].bShow = TRUE;
		}
		else
		{
			m_aryStates[i].bShow = FALSE;
		}
	}

	if (nSize > 0)
	{
		m_aryStates[0].nPhase = PHASE_DOING;
	}
}


void CNavationCtrl::SetCurrentStatusByHeaderTitleID(UINT nIDHeaderTitle)
{
	INT_PTR i;
	INT_PTR nSize = m_aryStates.GetSize();
	BOOL blFind = FALSE;
	DWORD dwID = 0;

	for (i=0; i<nSize; i++)
	{
		if (m_aryStates[i].nIDHeaderTitle == nIDHeaderTitle)
		{
			blFind = TRUE;
			dwID = m_aryStates[i].dwID;
			break;
		}
	}

	if (blFind)
	{
		SetCurrentStatus(dwID);
	}
}


void CNavationCtrl::SetCurrentStatus(DWORD dwID)
{
	INT_PTR i;
	INT_PTR nSize = m_aryStates.GetSize();

	DWORD dwParent = GetParentID(dwID);

	for (i=0; i<nSize; i++)
	{
		if (m_aryStates[i].dwID < dwID && m_aryStates[i].dwID != dwParent)
		{
			m_aryStates[i].nPhase = PHASE_FINISH;
		}
		else if (m_aryStates[i].dwID == dwID || m_aryStates[i].dwID == dwParent)
		{
			m_aryStates[i].nPhase = PHASE_DOING;
			m_aryStates[i].bShow = TRUE;
		}
		else
		{
			m_aryStates[i].nPhase = PHASE_PENDING;
		}
	}

	ShowStatus();
}


void CNavationCtrl::EnableStatusByHeaderTitleID(UINT nIDHeaderTitle, BOOL bEnalbe, BOOL bUpdate)
{
	BOOL blFind = FALSE;

	INT_PTR nSize = m_aryStates.GetSize();

	for (INT_PTR i=0; i<nSize; i++)
	{
		if (m_aryStates[i].nIDHeaderTitle == nIDHeaderTitle)
		{
			m_aryStates[i].bShow = bEnalbe;

			blFind = TRUE;

			break;
		}
	}

	if (blFind && bUpdate)
	{
		ShowStatus();
	}
}


void CNavationCtrl::EnableStatus(DWORD dwID, BOOL bEnalbe, BOOL bUpdate)
{
	BOOL blFind = FALSE;

	INT_PTR nSize = m_aryStates.GetSize();

	for (INT_PTR i=0; i<nSize; i++)
	{
		if (m_aryStates[i].dwID == dwID)
		{
			m_aryStates[i].bShow = bEnalbe;

			blFind = TRUE;

			break;
		}
	}

	if (blFind && bUpdate)
	{
		ShowStatus();
	}
}

void CNavationCtrl::EnableStatus(DWORD dwIDBegin, DWORD dwIDEnd, BOOL bEnalbe, BOOL bUpdate)
{
	INT_PTR nSize = m_aryStates.GetSize();

	for (INT_PTR i=0; i<nSize; i++)
	{
		if (m_aryStates[i].dwID >= dwIDBegin && m_aryStates[i].dwID <= dwIDEnd)
		{
			m_aryStates[i].bShow = bEnalbe;
		}
	}

	if (bUpdate)
	{
		ShowStatus();
	}
}


void CNavationCtrl::CreateStatusTree()
{
	// Create the Tree content
	TV_INSERTSTRUCT TreeItem;
	TV_INSERTSTRUCT curTreeItem;

	INT_PTR i, j;
	int nSize = m_aryStates.GetSize();

	//First Level
	WORD hWord, lWord;
	BYTE hByte, lByte;
	CString strHeaderTitle(_T(""));

	m_ctrStatus.DeleteAllItems();

	m_ctrStatus.SetImageList(&m_imagePhase, TVSIL_NORMAL);
	m_ctrStatus.SetImageList(&m_imagePhase, TVSIL_STATE);

	for (j=0; j<nSize; j++)
	{
		hWord = HIWORD(m_aryStates.GetAt(j).dwID);
		lWord = LOWORD(m_aryStates.GetAt(j).dwID);
		hByte = HIBYTE(hWord);
		lByte = LOBYTE(hWord);

		if (m_aryStates[j].bShow)
		{
			if (lWord == 0 && hByte > 0 && lByte == 0)
			{
				TreeItem.hParent = NULL; 
				TreeItem.hInsertAfter = TVI_LAST;
				TreeItem.itemex.mask = TVIF_IMAGE | TVIF_TEXT | TVIF_CHILDREN;
				TCHAR chText[MAX_PATH];
				memset(chText, 0, sizeof(chText));

				strHeaderTitle.LoadString(m_aryStates.GetAt(j).nIDHeaderTitle);
				_tcscpy_s(chText, _countof(chText), strHeaderTitle);

				int nImage;
				nImage = GetStatusImageIndex(m_aryStates.GetAt(j).nPhase);

				TreeItem.item.iImage = nImage;

				TreeItem.itemex.pszText = chText;
				TreeItem.itemex.cChildren = 1;

				HTREEITEM hRet = TreeItem.hParent = m_ctrStatus.InsertItem(&TreeItem);

				if (IMAGE_DOING == nImage)
				{
					m_ctrStatus.SetItemState(hRet, TVIS_BOLD, TVIS_BOLD);
				}
				else
				{
					m_ctrStatus.SetItemState(hRet, 0, TVIS_BOLD);
				}

				m_ctrStatus.SetItemData(hRet, DWORD_PTR(&m_aryStates.GetAt(j)));

				//////////////////////////////////////////////////////////////////////////
				//Second Level
				for (i=0; i<nSize; i++)
				{
					if (m_aryStates[i].bShow)
					{
						WORD hWord2 = HIWORD(m_aryStates.GetAt(i).dwID);
						WORD lWord2 = LOWORD(m_aryStates.GetAt(i).dwID);

						BYTE hByte2 = HIBYTE(hWord2);
						BYTE lByte2 = LOBYTE(hWord2);

						if (lWord2 == 0 && hByte == hByte2 && lByte2 > 0)
						{
							curTreeItem.itemex.mask = TVIF_IMAGE | TVIF_TEXT;
							curTreeItem.hParent = TreeItem.hParent; 
							curTreeItem.itemex.cChildren = 0;
							curTreeItem.hInsertAfter = TVI_LAST;

							nImage = GetStatusImageIndex(m_aryStates.GetAt(i).nPhase);

							curTreeItem.item.iImage = nImage;

							memset(chText, 0, sizeof(chText));

							strHeaderTitle.LoadString(m_aryStates.GetAt(i).nIDHeaderTitle);
							_tcscpy_s(chText, _countof(chText), strHeaderTitle);


							curTreeItem.itemex.pszText = chText;
							hRet = m_ctrStatus.InsertItem( &curTreeItem );

							if (IMAGE_DOING == nImage)
							{
								m_ctrStatus.SetItemState(hRet, TVIS_BOLD, TVIS_BOLD);
							}
							else
							{
								m_ctrStatus.SetItemState(hRet, 0, TVIS_BOLD);
							}

							m_ctrStatus.SetItemData(hRet, DWORD_PTR(&m_aryStates.GetAt(i)));
						}
					}
				}
			}
		}

		m_ctrStatus.Expand( TreeItem.hParent, TVE_EXPAND );
	}
}


void CNavationCtrl::ShowStatus()
{
	INT_PTR nSize = m_aryStates.GetSize();

	CString strHeaderTitle(_T(""));

	for (int i=0; i<nSize; i++)
	{
		HTREEITEM hItem = GetTreeItem(i);

		if (m_aryStates[i].bShow)
		{
			if (hItem)//Only Change Image
			{
				int nCurImage = GetStatusImageIndex(m_aryStates[i].nPhase);

				int nOldImage, nOldSelected;
				m_ctrStatus.GetItemImage(hItem, nOldImage, nOldSelected);

				if (nCurImage != nOldImage)
				{
					m_ctrStatus.SetItemImage(hItem, nCurImage, nCurImage);

					if (IMAGE_DOING == nCurImage)
					{
						m_ctrStatus.SetItemState(hItem, TVIS_BOLD, TVIS_BOLD);
					}
					else
					{
						m_ctrStatus.SetItemState(hItem, 0, TVIS_BOLD);
					}
				}
			}
			else//Need add a new item
			{
				DWORD dwID = m_aryStates[i].dwID;
				WORD hWord, lWord;
				BYTE hByte, lByte;

				hWord = HIWORD(dwID);
				lWord = LOWORD(dwID);
				hByte = HIBYTE(hWord);
				lByte = LOBYTE(hWord);

				//First Level
				if (lWord == 0 && hByte > 0 && lByte == 0)
				{
					BOOL bFindPrevious = FALSE;

					int n = 0;
					for (n=i-1; n>=0; n--)
					{
						if (m_aryStates[n].bShow)
						{
							bFindPrevious = TRUE;
							break;
						}
					}

					TV_INSERTSTRUCT TreeItem;
					TreeItem.hParent = NULL; 
					TreeItem.itemex.mask = TVIF_IMAGE | TVIF_TEXT | TVIF_CHILDREN; 

					if (bFindPrevious)
					{
						HTREEITEM hPreItem = GetTreeItem(n);
						TreeItem.hInsertAfter = hPreItem;
					}
					else
					{
						TreeItem.hInsertAfter = TVI_FIRST;
					}

					int nImage;

					nImage = GetStatusImageIndex(m_aryStates.GetAt(i).nPhase);

					TreeItem.item.iImage = nImage;


					TCHAR chText[MAX_PATH];
					memset(chText, 0, sizeof(chText));

					strHeaderTitle.LoadString(m_aryStates.GetAt(i).nIDHeaderTitle);
					_tcscpy_s(chText, _countof(chText), strHeaderTitle);

					TreeItem.itemex.pszText = chText;
					TreeItem.itemex.cChildren = 1;

					HTREEITEM hRet = m_ctrStatus.InsertItem(&TreeItem);

					m_ctrStatus.SetItemData(hRet, DWORD_PTR(&m_aryStates.GetAt(i)));

					if (IMAGE_DOING == nImage)
					{
						m_ctrStatus.SetItemState(hRet, TVIS_BOLD, TVIS_BOLD);
					}
					else
					{
						m_ctrStatus.SetItemState(hRet, 0, TVIS_BOLD);
					}

					m_ctrStatus.Expand(hRet, TVE_EXPAND);
				}
				else//Second Level
				{
					BOOL bFindPrevious = FALSE;

					DWORD dwParentID = GetParentID(dwID);
					int n = 0;
					for (n=i-1; n>=0; n--)
					{
						if (m_aryStates[n].bShow && m_aryStates[n].dwID > dwParentID && m_aryStates[n].dwID < dwID)
						{
							bFindPrevious = TRUE;
							break;
						}
					}

					TV_INSERTSTRUCT TreeSubItem;
					TreeSubItem.itemex.mask = TVIF_IMAGE | TVIF_TEXT;
					int nParentID = GetIndexFromID(dwParentID);
					HTREEITEM hParent = GetTreeItem(nParentID);
					TreeSubItem.hParent = hParent; 

					if (bFindPrevious)
					{
						HTREEITEM hPreItem = GetTreeItem(n);
						TreeSubItem.hInsertAfter = hPreItem;
					}
					else
					{
						TreeSubItem.hInsertAfter = TVI_FIRST;
					}

					int nImage;

					nImage = GetStatusImageIndex(m_aryStates.GetAt(i).nPhase);

					TreeSubItem.item.iImage = nImage;

					TCHAR chText[MAX_PATH];
					memset(chText, 0, sizeof(chText));
					strHeaderTitle.LoadString(m_aryStates.GetAt(i).nIDHeaderTitle);
					_tcscpy_s(chText, _countof(chText), strHeaderTitle);

					TreeSubItem.itemex.pszText = chText;
					TreeSubItem.itemex.cChildren = 0;

					HTREEITEM hSubRet = m_ctrStatus.InsertItem(&TreeSubItem);

					m_ctrStatus.SetItemData(hSubRet, DWORD_PTR(&m_aryStates.GetAt(i)));

					if (IMAGE_DOING == nImage)
					{
						m_ctrStatus.SetItemState(hSubRet, TVIS_BOLD, TVIS_BOLD);
					}
					else
					{
						m_ctrStatus.SetItemState(hSubRet, 0, TVIS_BOLD);
					}

					m_ctrStatus.Expand(m_ctrStatus.GetParentItem(hSubRet), TVE_EXPAND);
				}
			}
		}
		else//Need remove the item
		{
			if (hItem)
			{
				m_ctrStatus.DeleteItem(hItem);
			}
		}
	}
}


void CNavationCtrl::RePositionControl()
{
	CRect rect, rectLink, rectReadMe, rectTree;

	GetClientRect(rect);

	m_ctrWebSite.GetWindowRect(rectLink);
	ScreenToClient(rectLink);

	m_ctrReadMe.GetWindowRect(rectReadMe);
	ScreenToClient(rectReadMe);

	m_ctrStatus.GetWindowRect(rectTree);
	ScreenToClient(rectTree);

	int nSpace = rect.bottom - rectReadMe.bottom - 8;

	rectReadMe.top += nSpace;
	rectReadMe.bottom += nSpace; 

	rectLink.top += nSpace;
	rectLink.bottom += nSpace; 

	rectTree.bottom = rectLink.top - 1;

	m_ctrWebSite.MoveWindow(rectLink, TRUE);
	m_ctrReadMe.MoveWindow(rectReadMe, TRUE);
	m_ctrStatus.MoveWindow(rectTree, TRUE);
}


void CNavationCtrl::GetStatusFromFile(CString strFileName)
{
	INT_PTR nSize = m_aryStates.GetSize();

	int nShowSize = ::GetPrivateProfileInt(SECTION_STATUS, KEY_NUMBER, 0, strFileName);

	CString strKey, strValue;

	for (int i=0; i<nShowSize; i++)
	{
		strKey.Format(_T("%s%d"), KEY_ITEM, i);

		int nValue = ::GetPrivateProfileInt(SECTION_STATUS, strKey, 0, strFileName);

		if (i >= 0 && i < nSize)
		{
			m_aryStates[i].bShow = (nValue != 0);
		}
	}
}


void CNavationCtrl::SetStatusToFile(CString strFileName)
{
	INT_PTR nSize = m_aryStates.GetSize();
	CString strKey, strValue;
	strValue.Format(_T("%d"), nSize);
	::WritePrivateProfileString(SECTION_STATUS, KEY_NUMBER, strValue, strFileName);

	for (INT_PTR i=0; i<nSize; i++)
	{
		strKey.Format(_T("%s%d"), KEY_ITEM, i);
		strValue.Format(_T("%d"), m_aryStates[i].bShow ? 1 : 0);

		::WritePrivateProfileString(SECTION_STATUS, strKey, strValue, strFileName);
	}
}


DWORD CNavationCtrl::GetParentID(DWORD dwID)
{
	return (dwID & 0xFF000000);
}


int CNavationCtrl::GetStatusImageIndex(PHASE_INDEX nStatus)
{
	int nImage = IMAGE_PENDING;

	switch (nStatus)
	{
	case PHASE_PENDING:
		nImage = IMAGE_PENDING;
		break;
	case PHASE_DOING:
		nImage = IMAGE_DOING;
		break;
	case PHASE_FINISH:
		nImage = IMAGE_FINISH;
		break;
	case PHASE_WRONG:
		nImage = IMAGE_WRONG;
		break;
	default:
		nImage = IMAGE_PENDING;
		break;
	}

	return nImage;
}


HTREEITEM CNavationCtrl::GetTreeItem(int nIndex)
{
	HTREEITEM hRoot = m_ctrStatus.GetRootItem();

	BOOL bFind = FALSE;

	INT_PTR nSize = m_aryStates.GetSize();
	if (nIndex < 0 || nIndex >= nSize)
	{
		return NULL;
	}

	while (hRoot)
	{
		STATE_ITEM *pItem = (STATE_ITEM *)m_ctrStatus.GetItemData(hRoot);

		if (pItem)
		{
			if (pItem->dwID == m_aryStates[nIndex].dwID)
			{
				bFind = TRUE;
				return hRoot;
				break;
			}
		}
		else
		{
			hRoot = m_ctrStatus.GetNextSiblingItem(hRoot);
			continue;
		}

		if (m_ctrStatus.ItemHasChildren(hRoot))
		{
			HTREEITEM hChild = m_ctrStatus.GetChildItem(hRoot);
			if (hChild)
			{
				pItem = (STATE_ITEM *)m_ctrStatus.GetItemData(hChild);
				if (pItem)
				{
					if (pItem->dwID == m_aryStates[nIndex].dwID)
					{
						return hChild;
					}
				}
				else
				{

				}

				HTREEITEM hSib = m_ctrStatus.GetNextSiblingItem(hChild);

				while (hSib)
				{
					pItem = (STATE_ITEM *)m_ctrStatus.GetItemData(hSib);
					if (pItem)
					{
						if (pItem->dwID == m_aryStates[nIndex].dwID)
						{
							return hSib;
						}
					}
					else
					{
						hSib = m_ctrStatus.GetNextSiblingItem(hSib);
						continue;
					}

					hSib = m_ctrStatus.GetNextSiblingItem(hSib);
				}
			}
		}

		hRoot = m_ctrStatus.GetNextSiblingItem(hRoot);
	}

	return NULL;
}


int CNavationCtrl::GetIndexFromID(DWORD dwID)
{
	INT_PTR nSize = m_aryStates.GetSize();
	for (int i=0; i<nSize; i++)
	{
		if (m_aryStates[i].dwID == dwID)
		{
			return i;
		}
	}

	return -1;
}


void CNavationCtrl::SetWebPageLink(CString strLink)
{
	m_strWebPageLink = strLink;

	m_ctrWebSite.m_link = strLink;
}


void CNavationCtrl::SetReadmeLink(CString strLink)
{
	m_strReadmeLink = strLink;

	m_ctrReadMe.m_link = strLink;
}


void CNavationCtrl::ShowLinks(BOOL bShow)
{
	m_bShowLinks = bShow;

	int nCmdShow = m_bShowLinks ? SW_SHOW : SW_HIDE;

	if (m_ctrWebSite.GetSafeHwnd())
	{
		m_ctrWebSite.ShowWindow(nCmdShow);
	}

	if (m_ctrReadMe.GetSafeHwnd())
	{
		m_ctrReadMe.ShowWindow(nCmdShow);
	}
}


BOOL CNavationCtrl::IsIEInstalled()
{
	BOOL bRet = FALSE;

	LPCTSTR lpctIEKey = _T("SOFTWARE\\Microsoft\\Internet Explorer");
	HKEY hKey;

	if (ERROR_SUCCESS == ::RegOpenKeyEx(HKEY_LOCAL_MACHINE, lpctIEKey, 0, KEY_READ, &hKey))
	{
		TCHAR szValue[MAX_PATH] = {0};
		DWORD dwSize = sizeof(szValue);

		if (ERROR_SUCCESS == ::RegQueryValueEx(hKey, _T("Version"), NULL, NULL, (LPBYTE)szValue, &dwSize))
		{
			bRet = TRUE;
		}

		::RegCloseKey(hKey);
	}

	return bRet;
}