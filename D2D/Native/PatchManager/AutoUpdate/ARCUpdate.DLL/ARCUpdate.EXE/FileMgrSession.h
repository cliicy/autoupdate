#pragma once
#include "..\include\UpLib.h"
#include "PipeSession.h"
#include <string>
#include <vector>
using namespace std;

class CFileMgrSession : public CPipeSessionBase
{
public:
	CFileMgrSession(HANDLE hPipe, CPipeSessionSet* pSet);

	virtual ~CFileMgrSession();

public:
	virtual DWORD Main();

protected:
	void onFindFiles(const wstring& strFormat );

	void onDeleteFiles(const wstring& strFile);

	void onQueryMd5(const wstring& strFile);
	
	void onDeleteFolder(const wstring& strFolder);
	
	void onCreateFolder(const wstring& strFolder);
	
	void onDownloadFile(const wstring& strFile);
	
	void onUploadFile(const wstring& strFile);

	void onRunCommand(const wstring& strCmd, int nSync);
	
	void onInvalidParameter();

protected:
	bool replaceMacrosOfPath(wstring& strPath);

protected:
	DWORD _deleteFiles(const wstring& strFile);
	
	DWORD _deleteFolder(const wstring& strFolder);
	
	DWORD _createFolder(const wstring& strFolder);
};

