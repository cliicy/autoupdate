/////////////////////////////////////////////////////////////////////////////
// CCMDLINE.H	Command line processor

/////////////////////////////////////////////////////////////////////////////

#ifndef _CCMDLINE_H_
#define _CCMDLINE_H_

const char chDefaultSwitch = _T('-');

#define REMOTE_INSTALL_SWITCH		_T("r")

#ifdef _SETUPCLS_DLL
class __declspec(dllexport) CCommandLineMap: protected CMapStringToString
#else
class __declspec(dllimport) CCommandLineMap: protected CMapStringToString
#endif

{
public:
	CCommandLineMap (LPCTSTR lpszCommandLine, TCHAR chSwitch = chDefaultSwitch);
	~CCommandLineMap ();
	
public:
	BOOL GetText (UINT nArgument, CString rText) const;
	BOOL GetSwitch (TCHAR chSwitch) const;
	BOOL GetSwitch (TCHAR chSwitch, CString& rSwitchText, LPCTSTR lpszDefault = NULL) const;
	BOOL GetSwitch (LPCTSTR lpszSwitch) const;
	BOOL GetSwitch (LPCTSTR lpszSwitch, CString& rSwitchText, LPCTSTR lpszDefault = NULL) const;

protected:
	void ProcessSwitchToken (LPCTSTR lpszToken);
	void TextKey (CString& Key, UINT nArgument) const;

#ifdef _DEBUG
public:
	static BOOL UnitTest();
#endif		

};
	
#endif	// _CMDLINE_H_

