#include "stdafx.h"
#include "UpLib.h"
#include "md5.h"
namespace PATHUTILS
{
	std::wstring home_dir()
	{
		wchar_t szPath[MAX_PATH] = { 0 };
		::GetModuleFileName(NULL, szPath, _ARRAYSIZE(szPath));
		wchar_t *ptr = wcsrchr(szPath, L'\\');
		if (!ptr)
			return L"";

		*ptr = 0;
		return std::wstring(szPath);
	}

	void path_ensure_end_with_slash(std::wstring& strPath)
	{
		wstring::reverse_iterator it = strPath.rbegin();
		if (it != strPath.rend() && *it != '\\')
			strPath += '\\';
	}

	void path_ensure_end_without_slash(std::wstring& strPath)
	{
		wstring::reverse_iterator it = strPath.rbegin();
		if (it != strPath.rend() && *it == '\\')
			strPath.erase(--strPath.end());
	}

	bool is_folder_exist(const std::wstring& strFolder)
	{
		DWORD dwAttrs = ::GetFileAttributes(strFolder.c_str());
		return (dwAttrs != INVALID_FILE_ATTRIBUTES) && ((dwAttrs&FILE_ATTRIBUTE_DIRECTORY) != 0);
	}

	bool is_file_exist(const std::wstring& strFile)
	{
		WIN32_FIND_DATA wfd;
		HANDLE hFile = ::FindFirstFile(strFile.c_str(), &wfd);
		if (hFile == INVALID_HANDLE_VALUE)
			return false;
		::FindClose(hFile);
		return true;
	}

	std::wstring file_name_of_path(const std::wstring& fullpath)
	{
		std::wstring::size_type pos = fullpath.find_last_of(L'\\');
		if (pos == std::wstring::npos)
			return fullpath;
		return fullpath.substr(pos + 1);
	}

	std::wstring folder_of_path(const std::wstring& fullpath)
	{
		std::wstring path = fullpath;

		std::wstring::size_type pos = path.find_last_of(L'\\');
		if (pos == std::wstring::npos)
			return L"";
		return path.substr(0, pos + 1);
	}

	std::wstring path_join(const std::wstring& parent, const std::wstring& sub)
	{
		if (sub.empty()) return parent;

		std::wstring sRes = parent;
		if (sub.at(0) == L'\\')
			path_ensure_end_without_slash(sRes);
		else
			path_ensure_end_with_slash(sRes);
		return sRes.append(sub);
	}

	bool create_folder(const std::wstring& strfolder, bool bRecursivly/*=true*/)
	{
		if (is_folder_exist(strfolder))
			return true;

		std::wstring strFullPath = strfolder;
		path_ensure_end_without_slash(strFullPath);
		if (CreateDirectory(strFullPath.c_str(), NULL)){
			return true;
		}
		else{
			if (GetLastError() == ERROR_INVALID_NAME)
				return false;
		}

		if (!bRecursivly)
			return false;

		std::wstring strBase = folder_of_path(strFullPath);
		if (strBase.empty())
			return false;

		if (!create_folder(strBase, bRecursivly))
			return false;

		if (!create_folder(strFullPath, bRecursivly))
			return false;

		return true;
	}

	void files_under_folder(const std::wstring& str_folder, const wstring& strFormat, std::vector<wstring>& vec_files, bool bFullPath/*=true*/)
	{
		if (str_folder.empty())
			return;

		std::wstring str_temp = path_join(str_folder, L"*");
		if (!strFormat.empty())
			str_temp = path_join(str_folder, strFormat);

		WIN32_FIND_DATA wfd;
		ZeroMemory(&wfd, sizeof(wfd));
		HANDLE hFind = ::FindFirstFile(str_temp.c_str(), &wfd);
		if (hFind == INVALID_HANDLE_VALUE)
			return;
		do
		{
			if (_wcsicmp(wfd.cFileName, L".") == 0 || _wcsicmp(wfd.cFileName, L"..") == 0)
				continue;
			if (0 != (wfd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY))
				continue;
			if (bFullPath)
				vec_files.push_back(path_join(str_folder, wfd.cFileName));
			else
				vec_files.push_back(wfd.cFileName);
		} while (::FindNextFile(hFind, &wfd));
		::FindClose(hFind);
	}

	void sub_folders(const std::wstring& strParentFullPath, std::vector<std::wstring>& vecSubFolders, bool bFullPath)
	{
		vecSubFolders.clear();

		if (strParentFullPath.empty())
			return;

		std::wstring str_temp = path_join(strParentFullPath, L"*");
		WIN32_FIND_DATA wfd;
		ZeroMemory(&wfd, sizeof(wfd));
		HANDLE hFind = ::FindFirstFile(str_temp.c_str(), &wfd);
		if (hFind == INVALID_HANDLE_VALUE)
			return;
		do
		{
			if (_wcsicmp(wfd.cFileName, L".") == 0 || _wcsicmp(wfd.cFileName, L"..") == 0)
				continue;
			if (0 == (wfd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY))
				continue;
			if (bFullPath)
				vecSubFolders.push_back(path_join(strParentFullPath, wfd.cFileName));
			else
				vecSubFolders.push_back(wfd.cFileName);

		} while (::FindNextFile(hFind, &wfd));
		::FindClose(hFind);
	}

	std::wstring md5_of_file(const std::wstring& strFilePath)
	{
		HANDLE hFile = ::CreateFile(strFilePath.c_str(), GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
		if (hFile == INVALID_HANDLE_VALUE)
			return L"";

		MD5_CTX ctx;
		MD5Init(&ctx);
		DWORD dwSizeToRead = 2 * 1048576;
		DWORD dwRead = 0;
		PBYTE pBuf = (PBYTE)malloc(dwSizeToRead);
		while (TRUE)
		{
			dwRead = 0;
			ZeroMemory(pBuf, dwSizeToRead);
			if (!::ReadFile(hFile, pBuf, dwSizeToRead, &dwRead, NULL))
				break;
			if (dwRead == 0)
				break;
			MD5Update(&ctx, pBuf, dwRead);
		}

		unsigned char md5Out[256] = { 0 };
		MD5Final(md5Out, &ctx);
		CloseHandle(hFile);

		WCHAR szTemp[33] = { 0 };
		wstring strResult = L"";
		for (int i = 0; i < 16; i++)
		{
			swprintf_s(szTemp, _countof(szTemp), L"%02x", md5Out[i]);
			strResult += szTemp;
		}
		free(pBuf);
		return strResult;
	}

	ULONGLONG size_of_file(const std::wstring& strFile)
	{
		WIN32_FILE_ATTRIBUTE_DATA wfd;
		ZeroMemory(&wfd, sizeof(wfd));
		::GetFileAttributesEx(strFile.c_str(), GetFileExInfoStandard, &wfd);
		LARGE_INTEGER li;
		li.HighPart = wfd.nFileSizeHigh;
		li.LowPart = wfd.nFileSizeLow;
		return li.QuadPart;
	}

	std::wstring standlize_path(const std::wstring& strFullPath)
	{
		size_t sOffset = 0;
		if (strFullPath.length()>2 &&
			strFullPath.at(1) == L':'){
			sOffset = 2;
		}
		else if (strFullPath.length() > wcslen(L"\\\\?\\Volume{") &&
			STRUTILS::same_str(strFullPath.substr(0, wcslen(L"\\\\?\\Volume{")), L"\\\\?\\Volume{")){
			sOffset = wcslen(L"\\\\?\\Volume{");
		}
		else if (strFullPath.length() > wcslen(L"\\Device\\HarddiskVolume") &&
			STRUTILS::same_str(strFullPath.substr(0, wcslen(L"\\Device\\HarddiskVolume")), L"\\Device\\HarddiskVolume")){
			sOffset = wcslen(L"\\Device\\HarddiskVolume");
		}
		else if (strFullPath.length()>2 &&
			STRUTILS::same_str(strFullPath.substr(0, 2), L"\\\\")){
			sOffset = 2;
		}
		else{
			return strFullPath;
		}

		std::wstring strRoot = L"";
		std::wstring strSubPath = L"";
		std::wstring::size_type pos1 = strFullPath.find_first_of(L'\\', sOffset);
		std::wstring::size_type pos2 = strFullPath.find_first_of(L'/', sOffset);
		std::wstring::size_type pos = min(pos1, pos2);
		if (pos == std::wstring::npos)
			return strFullPath;
		else{
			strRoot = strFullPath.substr(0, pos);
			strSubPath = strFullPath.substr(pos);
		}

		if (strSubPath.find_first_of(L'/') == std::wstring::npos &&
			strSubPath.find(L"\\..\\") == std::wstring::npos &&
			strSubPath.find(L"\\.\\") == std::wstring::npos &&
			strSubPath.find(L"\\\\") == std::wstring::npos &&
			strSubPath.find(L"//") == std::wstring::npos)
			return strFullPath;

		strRoot.append(L"\\");
		STRUTILS::replace_str(strSubPath, L"/", L"\\");
		std::vector<std::wstring> pathEles;
		STRUTILS::split_str(strSubPath, L'\\', pathEles);
		if (pathEles.empty())
			return strRoot;

		for (size_t i = 0; i<pathEles.size();)
		{
			if (STRUTILS::same_str(pathEles[i], L".")){
				pathEles.erase(pathEles.begin() + i);
			}
			else if (STRUTILS::same_str(pathEles[i], L"..")){
				pathEles.erase(pathEles.begin() + i);
				if (i>0){
					pathEles.erase(pathEles.begin() + i - 1);
					i--;
				}
			}
			else{
				i++;
			}
		}

		for (size_t i = 0; i<pathEles.size(); i++){
			strRoot.append(pathEles[i]);
			if (i != pathEles.size() - 1)
				strRoot.append(L"\\");
		}

		if (!pathEles.empty() && strSubPath.find_last_of(L'\\') == strSubPath.length() - 1)
			strRoot.append(L"\\");

		return strRoot;
	}

}
