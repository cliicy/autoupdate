#include "stdafx.h"

//////////////////////////////////////////////////////////////////////////
#ifndef BEGIN_BLOCK
#define BEGIN_BLOCK(LevelNumber)		do{ int __abc_xyz_##LevelNumber(LevelNumber); 
#define LEAVE_BLOCK(LevelNumber)		{(__abc_xyz_##LevelNumber);} break;
#define END_BLOCK(LevelNumber)			{(__abc_xyz_##LevelNumber);} }while(0);
#endif
//////////////////////////////////////////////////////////////////////////


#define	PRINT_DEBUG_LOG		_tprintf_s
#define __R_N				TEXT("\r\n")

static VOID __AppendTail(CString & str, TCHAR ch)
{
	str.TrimRight(ch);
	str += ch;
}

static HRESULT __CopyFilesFlat(LPCTSTR pSourceDir, LPCTSTR pDestDir, LPCTSTR pFilePattern)
{
	HRESULT hr = S_OK;
	DWORD LastError = 0;

	BEGIN_BLOCK(0);
	//////////////////////////////////////////////////////////////////////////
	CString strSourceDir = pSourceDir;
	__AppendTail(strSourceDir, TEXT('\\'));

	CString strDestDir = pDestDir;
	__AppendTail(strDestDir, TEXT('\\'));
		
	//////////////////////////////////////////////////////////////////////////
	WIN32_FIND_DATA FindFileData = {0};
	CString strFilePattern = strSourceDir;
	strFilePattern += pFilePattern;
	HANDLE hFind = FindFirstFile(
		strFilePattern.GetString(),//_In_   LPCTSTR lpFileName,
		&FindFileData//_Out_  LPWIN32_FIND_DATA lpFindFileData
		);
	if (INVALID_HANDLE_VALUE == hFind)
	{
		LastError = GetLastError();
		hr = HRESULT_FROM_WIN32(LastError);
		LEAVE_BLOCK(0);
	}

	//////////////////////////////////////////////////////////////////////////
	CString strSourceFile;
	CString strDestFile;
	BOOL bCopyFile = FALSE;
	do 
	{
		if (!(FILE_ATTRIBUTE_DIRECTORY  & FindFileData.dwFileAttributes))
		{
			strSourceFile = strSourceDir;
			strSourceFile += FindFileData.cFileName;

			strDestFile = strDestDir;
			strDestFile += FindFileData.cFileName;

			bCopyFile = CopyFile(
				strSourceFile.GetString(),//_In_  LPCTSTR lpExistingFileName,
				strDestFile.GetString(),//_In_  LPCTSTR lpNewFileName,
				FALSE//_In_  BOOL bFailIfExists
				);
			if (!bCopyFile)
			{
				LastError = GetLastError();
				hr = HRESULT_FROM_WIN32(LastError);
				PRINT_DEBUG_LOG(TEXT("CopyFile failed [%s] --> [%s], hr = 0x%08x") __R_N, strSourceFile.GetString(), strDestFile.GetString(), hr);
			}
			else
			{
				PRINT_DEBUG_LOG(TEXT("CopyFile [%s] --> [%s]") __R_N, strSourceFile.GetString(), strDestFile.GetString());
			}
		}

	} while (FindNextFile(hFind, &FindFileData));

	//////////////////////////////////////////////////////////////////////////
	FindClose(hFind);
	hFind = INVALID_HANDLE_VALUE;
	END_BLOCK(0);
	
	return hr;
}

HRESULT CopyFileTree(LPCTSTR pSourceDir, LPCTSTR pDestDir, LPCTSTR pFilePattern)
{
	HRESULT hr = S_OK;
	DWORD	LastError = 0;

	BEGIN_BLOCK(0);
	//////////////////////////////////////////////////////////////////////////
	hr = __CopyFilesFlat(pSourceDir, pDestDir, pFilePattern);

	//////////////////////////////////////////////////////////////////////////
	CString strSubSourceDirPattern = pSourceDir;
	__AppendTail(strSubSourceDirPattern, TEXT('\\'));
	strSubSourceDirPattern += TEXT('*');

	WIN32_FIND_DATA FindFileData = {0};
	HANDLE hFind = FindFirstFile(
		strSubSourceDirPattern.GetString(),//_In_   LPCTSTR lpFileName,
		&FindFileData//_Out_  LPWIN32_FIND_DATA lpFindFileData
		);
	if (INVALID_HANDLE_VALUE == hFind)
	{
		LastError = GetLastError();
		hr = HRESULT_FROM_WIN32(LastError);
		LEAVE_BLOCK(0);
	}

	//////////////////////////////////////////////////////////////////////////
	CString strSubSource;
	CString strSubDest;
	BOOL bCreateDirectory = FALSE;
	do 
	{
		if ((FILE_ATTRIBUTE_DIRECTORY & FindFileData.dwFileAttributes) &&
			_tcsicmp(TEXT("."), FindFileData.cFileName) && 
			_tcsicmp(TEXT(".."), FindFileData.cFileName))
		{
			strSubSource = pSourceDir;
			__AppendTail(strSubSource, TEXT('\\'));
			strSubSource += FindFileData.cFileName;

			strSubDest = pDestDir;
			__AppendTail(strSubDest, TEXT('\\'));
			strSubDest += FindFileData.cFileName;

			bCreateDirectory = CreateDirectory(strSubDest.GetString(), NULL);
			if (!bCreateDirectory)
			{
				LastError = GetLastError();
				hr = HRESULT_FROM_WIN32(LastError);
				if (ERROR_ALREADY_EXISTS == LastError)
				{
					bCreateDirectory = TRUE;
				}
				else
				{
					PRINT_DEBUG_LOG(TEXT("CreateDirectory failed [%s], hr = 0x%08x") __R_N, strSubDest.GetString(), hr);
				}
				
			}

			if (bCreateDirectory)
			{
				hr = CopyFileTree(strSubSource.GetString(), strSubDest.GetString(), pFilePattern);
			}
		}

	} while (FindNextFile(hFind, &FindFileData));

	//////////////////////////////////////////////////////////////////////////
	FindClose(hFind);
	hFind = INVALID_HANDLE_VALUE;
	END_BLOCK(0);

	return hr;
}