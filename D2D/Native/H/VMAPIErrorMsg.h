#pragma once

//#include "stdafx.h"

#include <atlbase.h>
#include <atlstr.h>

namespace NS_VMAPIErrorMsg
{
	static HRESULT GetErrorMessageFile(CString & strFullFilePath, LPCTSTR szFile, DWORD dwPid = 0, DWORD dwTid = 0)
	{
		HRESULT hr = S_OK;
		DWORD dwLastError = 0;

		if (0 == dwPid)
		{
			dwPid = GetProcessId(GetCurrentProcess());
		}
		if (0 == dwTid)
		{
			dwTid = GetCurrentThreadId();
		}

		do 
		{
			CONST INT BUFFER_SIZE = 1024;
			strFullFilePath.GetBuffer(BUFFER_SIZE);
			DWORD dwGetModuleFileName = GetModuleFileName(
				NULL,//__in_opt  HMODULE hModule,
				strFullFilePath.GetBuffer(),//__out     LPTSTR lpFilename,
				BUFFER_SIZE//__in      DWORD nSize
				);
			if (0 == dwGetModuleFileName)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}

			LPTSTR pch = _tcsrchr(strFullFilePath.GetBuffer(), TEXT('\\'));
			if (NULL == pch)
			{
				hr =E_FAIL;
				break;
			}

			pch[0] = 0;
			strFullFilePath.ReleaseBuffer();

			strFullFilePath.AppendFormat(TEXT("\\temp\\%s_pid%u_tid%u.err"), szFile, dwPid, dwTid);

		} while (FALSE);
		
		return hr;
	}
} //end namespace NS_VMAPIErrorMsg

class CVMAPIErrorMsg
{
	ULONG m_ResourceID; //<sonmi01>2011-5-31 ###???
	UINT64 m_Code;
	ULONGLONG m_nLengthBytes;
	CString m_strMessage;

public:
	CVMAPIErrorMsg()
	:m_Code(0),
	m_ResourceID(0)
	{
	}

	VOID SetResourceID(ULONG ResourceID) //<sonmi01>2011-5-31 ###???
	{
		m_ResourceID = ResourceID;
	}

	VOID Set(UINT64 Code, LPCTSTR pszMessage)
	{
		m_Code = Code;
		m_strMessage = pszMessage;
		m_nLengthBytes = m_strMessage.GetLength()*sizeof(TCHAR) + sizeof(TCHAR);
	}

	ULONG GetResourceID() //<sonmi01>2011-5-31 ###???
	{
		return m_ResourceID;
	}

	UINT64 GetErrorCode() CONST
	{
		return m_Code;
	}

	LPCTSTR GetErrorMessage() CONST
	{
		return m_strMessage.GetString();
	}
	
	HRESULT SaveAs(LPCTSTR pszFileName) CONST
	{
		HRESULT hr = S_OK;
		DWORD dwLastError = 0;

		do 
		{
			HANDLE hFile = CreateFile(
				pszFileName,//__in      LPCTSTR lpFileName,
				GENERIC_WRITE,//__in      DWORD dwDesiredAccess,
				0,//__in      DWORD dwShareMode,
				NULL,//__in_opt  LPSECURITY_ATTRIBUTES lpSecurityAttributes,
				CREATE_ALWAYS,//__in      DWORD dwCreationDisposition,
				0,//__in      DWORD dwFlagsAndAttributes,
				NULL//__in_opt  HANDLE hTemplateFile
				);
			if (INVALID_HANDLE_VALUE == hFile)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}

			CHandle ahFile(hFile); hFile = INVALID_HANDLE_VALUE;

			DWORD dwNumberOfBytesWritten = 0; //<sonmi01>2011-5-31 ###???
			BOOL bRet = WriteFile(
				ahFile,//__in         HANDLE hFile,
				&m_ResourceID,//__in         LPCVOID lpBuffer,
				sizeof(m_ResourceID),//__in         DWORD nNumberOfBytesToWrite,
				&dwNumberOfBytesWritten,//__out_opt    LPDWORD lpNumberOfBytesWritten,
				NULL//__inout_opt  LPOVERLAPPED lpOverlapped
				);
			if (!bRet)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}

			dwNumberOfBytesWritten = 0;
			bRet = WriteFile(
				ahFile,//__in         HANDLE hFile,
				&m_Code,//__in         LPCVOID lpBuffer,
				sizeof(UINT64),//__in         DWORD nNumberOfBytesToWrite,
				&dwNumberOfBytesWritten,//__out_opt    LPDWORD lpNumberOfBytesWritten,
				NULL//__inout_opt  LPOVERLAPPED lpOverlapped
				);
			if (!bRet)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}

			dwNumberOfBytesWritten = 0;
			bRet = WriteFile(
				ahFile,//__in         HANDLE hFile,
				&m_nLengthBytes,//__in         LPCVOID lpBuffer,
				sizeof(ULONGLONG),//__in         DWORD nNumberOfBytesToWrite,
				&dwNumberOfBytesWritten,//__out_opt    LPDWORD lpNumberOfBytesWritten,
				NULL//__inout_opt  LPOVERLAPPED lpOverlapped
				);
			if (!bRet)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}

			dwNumberOfBytesWritten = 0;
			bRet = WriteFile(
				ahFile,//__in         HANDLE hFile,
				m_strMessage.GetString(),//__in         LPCVOID lpBuffer,
				m_nLengthBytes,//__in         DWORD nNumberOfBytesToWrite,
				&dwNumberOfBytesWritten,//__out_opt    LPDWORD lpNumberOfBytesWritten,
				NULL//__inout_opt  LPOVERLAPPED lpOverlapped
				);
			if (!bRet)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}

		} while (FALSE);

		return hr;
	}

	HRESULT LoadFrom(LPCTSTR pszFileName)
	{
		HRESULT hr = S_OK;
		DWORD dwLastError = 0;

		do 
		{
			HANDLE hFile = CreateFile(
				pszFileName,//__in      LPCTSTR lpFileName,
				GENERIC_READ,//__in      DWORD dwDesiredAccess,
				0,//__in      DWORD dwShareMode,
				NULL,//__in_opt  LPSECURITY_ATTRIBUTES lpSecurityAttributes,
				OPEN_EXISTING,//__in      DWORD dwCreationDisposition,
				0,//__in      DWORD dwFlagsAndAttributes,
				NULL//__in_opt  HANDLE hTemplateFile
				);
			if (INVALID_HANDLE_VALUE == hFile)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}

			CHandle ahFile(hFile); hFile = INVALID_HANDLE_VALUE;

			DWORD dwNumberOfBytesRead = 0;
			BOOL bRet = ReadFile(
				ahFile,//__in         HANDLE hFile,
				&m_ResourceID,//__in         LPCVOID lpBuffer,
				sizeof(ULONG),//__in         DWORD nNumberOfBytesToWrite,
				&dwNumberOfBytesRead,//__out_opt    LPDWORD lpNumberOfBytesWritten,
				NULL//__inout_opt  LPOVERLAPPED lpOverlapped
				);
			if (!bRet)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}


			dwNumberOfBytesRead = 0;
			bRet = ReadFile(
				ahFile,//__in         HANDLE hFile,
				&m_Code,//__in         LPCVOID lpBuffer,
				sizeof(UINT64),//__in         DWORD nNumberOfBytesToWrite,
				&dwNumberOfBytesRead,//__out_opt    LPDWORD lpNumberOfBytesWritten,
				NULL//__inout_opt  LPOVERLAPPED lpOverlapped
				);
			if (!bRet)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}

			dwNumberOfBytesRead = 0;
			bRet = ReadFile(
				ahFile,//__in         HANDLE hFile,
				&m_nLengthBytes,//__in         LPCVOID lpBuffer,
				sizeof(ULONGLONG),//__in         DWORD nNumberOfBytesToWrite,
				&dwNumberOfBytesRead,//__out_opt    LPDWORD lpNumberOfBytesWritten,
				NULL//__inout_opt  LPOVERLAPPED lpOverlapped
				);
			if (!bRet)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}

			dwNumberOfBytesRead = 0;
			m_strMessage.GetBuffer(m_nLengthBytes);
			bRet = ReadFile(
				ahFile,//__in         HANDLE hFile,
				m_strMessage.GetBuffer(),//__in         LPCVOID lpBuffer,
				m_nLengthBytes,//__in         DWORD nNumberOfBytesToWrite,
				&dwNumberOfBytesRead,//__out_opt    LPDWORD lpNumberOfBytesWritten,
				NULL//__inout_opt  LPOVERLAPPED lpOverlapped
				);
			m_strMessage.ReleaseBuffer();
			if (!bRet)
			{
				dwLastError = GetLastError();
				hr = HRESULT_FROM_WIN32(dwLastError);
				break;
			}

		} while (FALSE);

		return hr;
	}
};