#pragma once
//#include <afxwin.h>
#include <windows.h>
#include <string>
#include <TCHAR.H>
#include <io.h>
//#include "ErrorCodes.h"
using namespace std;

#ifdef _EXPORTS_
#define APMAPI __declspec(dllexport)
#else
#define APMAPI __declspec(dllimport)
#endif

enum FILE_PROPERTY_NAME
{	
	FILE_CREATION_DATETIME = 0,
	FILE_MODIFIED_DATETIME,
	FILE_DRIVE,
	FILE_SIZE_BYTES
	
};

class CFileManager
{
public:
	CFileManager(wstring in_wsFileAbsolutePath);
	FILE * m_pFileHandle;
	struct _stat m_FileProperties;
	wstring m_wsFileAbsolutePath;
public:
	virtual  ~CFileManager(void);
	BOOL	  OpenFile(TCHAR * mode);
	BOOL	  CreateFile();
	INT		  WriteToFile(PBYTE  in_pBuffer,const ULONG &in_dwBytesToWrite);
	BOOL	  DeleteFile();
	BOOL	  SetFileOffset(const ULONG &in_dwOffsetToSeek );
	VOID	  Close(VOID);
	INT		  ReadFile(PBYTE  out_pBuffer,__int64 in_SizeToRead,__int64 & out_BytesRead);
	BOOL	  CFileManager::SetMode(wstring in_wsMode);
	TCHAR*    GetFileProperty(FILE_PROPERTY_NAME in_File_Property);
	BOOL	  SetFileProperties();
	BOOL	  IsFileAlreadyExists(ULONG *out_dwExistingLocalFileSize);
	static BOOL IsFileAlreadyExists(wstring in_wsFilePath);
	static TCHAR * GetFileProperty(FILE_PROPERTY_NAME in_File_Property ,TCHAR * in_pszFileAbsolutePath);
};
