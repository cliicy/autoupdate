// BrwsFldDlg.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CBrowseFolderDialog dialog
#pragma once 

#define WM_FOCUS_PATHEDIT WM_USER+101

/////////////////////////////////////////////////////////////////////////////
// CPathEdit window
#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CPathEdit
#else
class __declspec(dllimport) CPathEdit
#endif 
: public CEdit
{
	// Construction
public:
	CPathEdit();
	CWnd* m_pWnd;

	// Attributes
public:

	// Operations
public:

	// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CPathEdit)
	//}}AFX_VIRTUAL

	// Implementation
public:
	virtual ~CPathEdit();

	// Generated message map functions
protected:
	//{{AFX_MSG(CPathEdit)
	afx_msg void OnSetFocus(CWnd* pOldWnd);
	//}}AFX_MSG

	DECLARE_MESSAGE_MAP()
};

//Setup Type define (come from mastersetup)
/*****	0	--- Local Setup		*****/
/*****	1	--- Remote Setup	*****/
/*****	2	--- Create ans file	*****/
/*****	4	--- Express setup	*****/
#define	IS_LOCAL_INSTALLATION				0
#define IS_REMOTE_SETUP						1
#define IS_CREATE_RESPONSE_FILE				2
#define IS_EXPRESS_SETUP					3

/*  Example for calling it
DWORD dwFlags = OFN_HIDEREADONLY | OFN_OVERWRITEPROMPT 
| OFN_EXPLORER | OFN_NODEREFERENCELINKS;

CString  m_sInstallPath ="C:\\Program Files\\CA\\BAB";
CBrowseFolderDialog folderDlg(TRUE, dwFlags);
CString targetPath = m_sInstallPath;
while (targetPath.GetLength()>3) 
{
	if (_access(targetPath,0) == 0) break;
	targetPath = targetPath.Left(targetPath.ReverseFind('\\'));
}
if (!targetPath.IsEmpty())
{
	folderDlg.m_ofn.lpstrInitialDir = targetPath;
}
folderDlg.m_sDefaultFolder = m_sInstallPath;

//Set MasterSetup RC file,if don't set it, it will show english string,not localizated string
folderDlg.m_sMSetupResDll = "..\\..\\Install\\1033\\MSetupRes.dll"; 

//set the selected language for installation(default is "1033") -- Fix issue 12292565
folderDlg.m_sLocalOSLID = "1033";

if (folderDlg.DoModal() == IDOK)
{
	m_sInstallPath = folderDlg.m_sFolderName;
}

*/
#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CBrowseFolderDialog
#else
class __declspec(dllimport) CBrowseFolderDialog
#endif
: public CFileDialog
{
	DECLARE_DYNAMIC(CBrowseFolderDialog)
public:
	CBrowseFolderDialog(BOOL bOpenFileDialog = TRUE, // TRUE for FileOpen, FALSE for FileSaveAs
		DWORD dwFlags = OFN_HIDEREADONLY | OFN_OVERWRITEPROMPT);
	~CBrowseFolderDialog();
// Attributes
public:
	CStringList m_listDisplayNames;    // list of actual items selected in listview
	CFont m_Font;
	//CPathEdit	m_PathEdit;
	CPathEdit m_PathEdit;
	CString		m_sFolderName;
	CString		m_sDefaultFolder;
	CString		m_sMSetupResDll;
	CString		m_sLocalOSLID;
	CString		m_sDlgTile;
	BOOL		isFirstChangeFolder;
	BOOL		isEditing ;
	int			m_nSetupType;

	TCHAR m_szBigBuffer[10000];

// Dialog Data
	//{{AFX_DATA(CBrowseFolderDialog)
//	enum { IDD = IDD_CUSTOM_FILE_DIALOG };
	CListBox	m_wndSelectionsList;
	BOOL	m_bExplorer;
	BOOL	m_bMulti;
	BOOL	m_bTemplate;
	//}}AFX_DATA

// Operations
	//Declare this as a friend
	friend LRESULT CALLBACK WindowProcNew(HWND hwnd,UINT message, WPARAM wParam, LPARAM lParam);
public:
	BOOL IsFileNameValid(LPCTSTR lpFileName);
	int MakeSurePathExists(LPCTSTR lpPath);
	int Touch(LPCTSTR lpPath, BOOL bValidate);
	BOOL OnTheOK();
	void GetLastFileName();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CBrowseFolderDialog)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL
	virtual void OnFolderChange();
	virtual void OnFileNameChange();
	
// Implementation
protected:
	BOOL m_bSelectMode; // TRUE when in "Selection" mode
	HINSTANCE m_hOldResource; //for old resource
	//{{AFX_MSG(CBrowseFolderDialog)
	virtual BOOL OnInitDialog();
	//}}AFX_MSG
	afx_msg LRESULT OnFocusPathEdit(WPARAM, LPARAM);
	DECLARE_MESSAGE_MAP()
};
