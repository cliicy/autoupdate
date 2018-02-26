#include "stdafx.h"
#include "FileMgrSession.h"
#include "PipeSessionSet.h"

CFileMgrSession::CFileMgrSession(HANDLE hPipe, CPipeSessionSet* pSet)
	: CPipeSessionBase(hPipe, pSet)
{
}


CFileMgrSession::~CFileMgrSession()
{
}

DWORD CFileMgrSession::Main()
{
	m_log.LogW(LL_INF, 0, L"%s: A new file manager session is started.", __WFUNCTION__);
	LONG lRet = 0;
	lRet = m_pipe.sendAck(REG_FILE_MGR_SESSION);
	if (lRet != 0)
		goto _EXIT;

	void* pReqParams = NULL;
	while (1)
	{
		SAFE_FREE(pReqParams);

		packet_req req;
		lRet = m_pipe.readReq(&req, &pReqParams);
		if (lRet != 0){
			break;
		}
		if (!pReqParams)
		{
			m_log.LogW(LL_ERR, lRet, L"%s: Invalid req, parameters is null", __WFUNCTION__);
			onInvalidParameter();
			continue;
		}

		switch (req.cmd)
		{
		case REQ_FILE_MGR_FIND_FILES:
		{
			wstring strPath((wchar_t*)pReqParams);
			replaceMacrosOfPath(strPath);
			onFindFiles(strPath);
			break;
		}
		case REQ_FILE_MGR_DELETE_FILE:
		{
			wstring strPath((wchar_t*)pReqParams);
			replaceMacrosOfPath(strPath);
			onDeleteFiles(strPath);
			break;
		}
		case REQ_FILE_MGR_DELETE_FOLDER:
		{
			wstring strPath((wchar_t*)pReqParams);
			replaceMacrosOfPath(strPath);
			onDeleteFolder(strPath);
			break;
		}
		case REQ_FILE_MGR_CREATE_FOLDER:
		{
			wstring strPath((wchar_t*)pReqParams);
			replaceMacrosOfPath(strPath);
			onCreateFolder(strPath);
			break;
		}
		case REQ_FILE_MGR_DOWNLOAD_FILE:
		{
			wstring strPath((wchar_t*)pReqParams);
			replaceMacrosOfPath(strPath);
			onDownloadFile(strPath);
			break;
		}
		case REQ_FILE_MGR_UPLOAD_FILE:
		{
			wstring strPath((wchar_t*)pReqParams);
			replaceMacrosOfPath(strPath);
			onUploadFile(strPath);
			break;
		}
		case REQ_FILE_MGR_QUERY_FILE_MD5:
		{
			wstring strPath((wchar_t*)pReqParams);
			replaceMacrosOfPath(strPath);
			onQueryMd5(strPath);
			break;
		}
		case REQ_FILE_MGR_RUN_COMMAND:
		{
			preq_param_run_command pCmd = (preq_param_run_command)pReqParams;
			wstring strCmd = pCmd->szCommandLine;
			replaceMacrosOfPath(strCmd);
			onRunCommand(strCmd, pCmd->nSync);
			break;
		}
		default:
		{
			onInvalidParameter();
			break;
		}
		}
	}
	SAFE_FREE(pReqParams);

_EXIT:
	m_log.LogW(LL_DET, 0, L"%s: Exit this session with error %d", __WFUNCTION__, lRet);
	m_pipe.Disconnect();
	if (m_pSessionSet)
		m_pSessionSet->RemoveSession(this);
	return (DWORD)lRet;
}

void CFileMgrSession::onRunCommand(const wstring& strCmd, int nSync)
{
	m_log.LogW(LL_DBG, 0, L"%s: Command = %s nSync = %d", __WFUNCTION__, strCmd.c_str(), nSync);
	LONG lRet = 0;

	STARTUPINFO si;
	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	si.dwFlags = STARTF_USESHOWWINDOW;
	si.wShowWindow = SW_HIDE;
	PROCESS_INFORMATION pi;
	BOOL bCreate = CreateProcessW(NULL,
		(LPWSTR)strCmd.c_str(),
		NULL,
		NULL,
		NULL,
		0,
		NULL,
		NULL,
		&si,
		&pi);
	if (!bCreate)
	{
		lRet = GetLastError();
	}
	else
	{
		if (nSync == 0)
		{
			CloseHandle(pi.hProcess);
			CloseHandle(pi.hThread);
		}
		else
		{
			::WaitForSingleObject(pi.hProcess, INFINITE);
			DWORD dwExitCode = 0;
			::GetExitCodeProcess(pi.hProcess, &dwExitCode);
			lRet = dwExitCode;
			CloseHandle(pi.hProcess);
			CloseHandle(pi.hThread);
		}
	}

	m_pipe.sendAck(REQ_FILE_MGR_RUN_COMMAND, lRet);
}

void CFileMgrSession::onFindFiles(const wstring& strFormat)
{
	m_log.LogW(LL_DBG, 0, L"%s: Format = %s", __WFUNCTION__, strFormat.c_str());
	LONG lRet = 0;
	WIN32_FIND_DATAW wfd;
	HANDLE hFind = ::FindFirstFile(strFormat.c_str(), &wfd);
	if (hFind == INVALID_HANDLE_VALUE){
		m_pipe.sendAck(REQ_FILE_MGR_FIND_FILES, GetLastError());
		return;
	}

	do
	{
		if (STRUTILS::same_str(wfd.cFileName, L".") || STRUTILS::same_str(wfd.cFileName, L".."))
			continue;
		lRet = m_pipe.sendAck(REQ_FILE_MGR_FIND_FILES, 0, sizeof(wfd), &wfd);
		if (lRet != 0)
			break;
		packet_ack ack;
		lRet = m_pipe.readAck(&ack);
		if (lRet != 0 || ack.errCode != 0)
			break;

	} while (::FindNextFile(hFind, &wfd));
	::FindClose(hFind);
	
	// send the last end packet.
	if (lRet == 0)
		m_pipe.sendAck(REQ_FILE_MGR_FIND_FILES);
}

void CFileMgrSession::onDeleteFiles(const wstring& strFile)
{
	m_log.LogW(LL_DBG, 0, L"%s: File = %s", __WFUNCTION__, strFile.c_str());
	m_pipe.sendAck(REQ_FILE_MGR_DELETE_FILE, _deleteFiles(strFile));
}

void CFileMgrSession::onQueryMd5(const wstring& strFile)
{
	m_log.LogW(LL_DBG, 0, L"%s: File = %s", __WFUNCTION__, strFile.c_str());
	if (!PATHUTILS::is_file_exist(strFile)){
		m_pipe.sendAck(REQ_FILE_MGR_QUERY_FILE_MD5, ERROR_FILE_NOT_FOUND);
		return;
	}
	
	wstring strMd5 = PATHUTILS::md5_of_file(strFile);
	DWORD dwSize = (DWORD)(strMd5.length() + 1)*sizeof(WCHAR);
	m_pipe.sendAck(REQ_FILE_MGR_QUERY_FILE_MD5, 0, dwSize, (void*)strMd5.c_str());
}

void CFileMgrSession::onDeleteFolder(const wstring& strFolder)
{
	m_log.LogW(LL_DBG, 0, L"%s: Folder = %s", __WFUNCTION__, strFolder.c_str());
	m_pipe.sendAck(REQ_FILE_MGR_DELETE_FOLDER, _deleteFolder(strFolder));
}

void CFileMgrSession::onCreateFolder(const wstring& strFolder)
{
	m_log.LogW(LL_DBG, 0, L"%s: Folder = %s", __WFUNCTION__, strFolder.c_str());
	m_pipe.sendAck(REQ_FILE_MGR_CREATE_FOLDER, _createFolder(strFolder));
}

void CFileMgrSession::onDownloadFile(const wstring& strFile)
{
	DWORD dwErr = 0;
	m_log.LogW(LL_DBG, 0, L"%s: File = %s", __WFUNCTION__, strFile.c_str());
	HANDLE hFile = ::CreateFile(strFile.c_str(), GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL,
		OPEN_EXISTING, 0, NULL);
	if (hFile == INVALID_HANDLE_VALUE){
		dwErr = GetLastError();
		m_log.LogW(LL_ERR, dwErr, L"%s: Failed to open file %s", __WFUNCTION__, strFile.c_str());
		m_pipe.sendAck(REQ_FILE_MGR_DOWNLOAD_FILE, dwErr);
		return;
	}

	char* pDataBuf = (char*)malloc(COM_BLOCK_DATA_SIZE);
	if (!pDataBuf){
		dwErr = ERROR_NOT_ENOUGH_MEMORY;
		m_log.LogW(LL_ERR, dwErr, L"%s: Failed to allocate buffer with size %d", __WFUNCTION__, COM_BLOCK_DATA_SIZE );
		m_pipe.sendAck(REQ_FILE_MGR_DOWNLOAD_FILE, dwErr);
		CloseHandle(hFile);
		return;
	}

	while (1)
	{
		ZeroMemory(pDataBuf, COM_BLOCK_DATA_SIZE);;
		DWORD dwRead = 0;
		if (!::ReadFile(hFile, pDataBuf, COM_BLOCK_DATA_SIZE, &dwRead, NULL)){
			dwErr = GetLastError();
			m_log.LogW(LL_ERR, dwErr, L"%s: Failed to read %d bytes data from file %s", __WFUNCTION__, COM_BLOCK_DATA_SIZE, strFile.c_str());
		}

		if (0 != m_pipe.sendAck(REQ_FILE_MGR_DOWNLOAD_FILE, dwErr, dwRead, pDataBuf))
			break;

		packet_ack ack;
		if (0 != m_pipe.readAck(&ack)){
			break;
		}

		if (dwErr != 0 || ack.errCode != 0 || dwRead < COM_BLOCK_DATA_SIZE)
			break;
	}
	CloseHandle(hFile);
	SAFE_FREE(pDataBuf);
}

void CFileMgrSession::onUploadFile(const wstring& strFile)
{
	DWORD dwErr = 0;
	m_log.LogW(LL_DBG, 0, L"%s: File = %s", __WFUNCTION__, strFile.c_str());
	HANDLE hFile = ::CreateFile(strFile.c_str(), GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
	if (hFile == INVALID_HANDLE_VALUE){
		dwErr = GetLastError();
		m_log.LogW(LL_ERR, dwErr, L"%s: Failed to create file %s", __WFUNCTION__, strFile.c_str());
		m_pipe.sendAck(REQ_FILE_MGR_UPLOAD_FILE, dwErr);
		return;
	}

	do
	{
		dwErr = m_pipe.sendAck(REQ_FILE_MGR_UPLOAD_FILE, 0);
		if ( 0 != dwErr){
			m_log.LogW(LL_ERR, dwErr, L"%s: Failed send ack(REQ_FMGR_UPLOAD_FILE) to the client", __WFUNCTION__);
			break;
		}

		void* pDataBuf = NULL;
		while (1)
		{
			SAFE_FREE(pDataBuf);
			packet_ack ack;

			dwErr = m_pipe.readAck(&ack, &pDataBuf);
			if (dwErr != 0 || ack.errCode != 0){				
				m_log.LogW(LL_ERR, dwErr, L"%s: ack error. dwErr=%d, ack.errCode=%d", __WFUNCTION__, dwErr, ack.errCode);
				dwErr = (dwErr == 0) ? ack.errCode : dwErr;
				break;
			}

			if (ack.dataSize > 0)
			{
				DWORD dwWrote = 0;
				if (!::WriteFile(hFile, pDataBuf, ack.dataSize, &dwWrote, NULL)){
					dwErr = GetLastError();
					m_log.LogW(LL_ERR, dwErr, L"%s: Failed to write file %s", __WFUNCTION__, strFile.c_str());
				}
			}

			m_pipe.sendAck(REQ_FILE_MGR_UPLOAD_FILE, dwErr);
			
			if (dwErr != 0 || ack.dataSize < COM_BLOCK_DATA_SIZE)
				break;
		}
		SAFE_FREE(pDataBuf);
	} while (0);

	CloseHandle(hFile);

	if (dwErr != 0)
		::DeleteFile(strFile.c_str());
}

void CFileMgrSession::onInvalidParameter()
{
	LONG lRet = m_pipe.sendAck(REG_FILE_MGR_SESSION, ERROR_INVALID_PARAMETER);
}

DWORD CFileMgrSession::_deleteFiles(const wstring& strFile)
{
	WIN32_FIND_DATA wfd; ZeroMemory(&wfd, sizeof(wfd));
	HANDLE hFind = ::FindFirstFile(strFile.c_str(), &wfd);
	if (hFind == INVALID_HANDLE_VALUE)
		return 0;

	DWORD dwRet = 0;
	wstring strFolder = PATHUTILS::folder_of_path(strFile);
	do
	{
		if (STRUTILS::same_str(wfd.cFileName, L".") || STRUTILS::same_str(wfd.cFileName, L".."))
			continue;

		if (0 != (wfd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY))
			continue;

		wstring strFileToDel = PATHUTILS::path_join(strFolder, wfd.cFileName);
		::SetFileAttributes(strFileToDel.c_str(), FILE_ATTRIBUTE_NORMAL);
		if (!::DeleteFile(strFileToDel.c_str())){
			dwRet = GetLastError();
			break;
		}
	} while (FindNextFile(hFind, &wfd));

	FindClose(hFind);
	return dwRet;
}

DWORD CFileMgrSession::_deleteFolder(const wstring& strFolder)
{
	if (!PATHUTILS::is_folder_exist(strFolder))
		return 0;
	std::vector<wstring> vecFiles;
	PATHUTILS::files_under_folder(strFolder, L"", vecFiles, true);
	for (size_t i = 0; i < vecFiles.size(); i++){
		::SetFileAttributes(vecFiles[i].c_str(), FILE_ATTRIBUTE_NORMAL);
		if (!::DeleteFile(vecFiles[i].c_str()))
			return GetLastError();
	}

	std::vector<wstring> vecSubFolders;
	PATHUTILS::sub_folders(strFolder, vecSubFolders, true);
	for (size_t i = 0; i < vecSubFolders.size(); i++){
		DWORD dwRet = _deleteFolder(vecSubFolders[i]);
		if (dwRet != 0)
			return dwRet;
	}

	if (!::RemoveDirectory(strFolder.c_str())){
		return GetLastError();
	}
	return 0;
}

DWORD CFileMgrSession::_createFolder(const wstring& strFolder)
{
	if (!PATHUTILS::create_folder(strFolder, true))
		return GetLastError();
	return 0;
}

bool CFileMgrSession::replaceMacrosOfPath(wstring& strPath)
{
	WCHAR szNewPath[1024] = { 0 };
	ExpandEnvironmentStrings(strPath.c_str(), szNewPath, _countof(szNewPath));
	strPath = wstring(szNewPath);
	return true;
}