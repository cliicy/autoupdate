#include "stdafx.h"
#include "UpLib.h"
#include <atlstr.h>

namespace STRUTILS
{
	#define STRING_BUFFER_LEN  1024
	std::wstring fstr(LPCWSTR pszFormat, ...)
	{
		std::wstring strRes = L"";

		va_list arg_ptr;
		va_start(arg_ptr, pszFormat);
		WCHAR szMessage[STRING_BUFFER_LEN] = { 0 };
		_vsnwprintf_s(szMessage, STRING_BUFFER_LEN, STRING_BUFFER_LEN - 1, pszFormat, arg_ptr);
		szMessage[STRING_BUFFER_LEN - 1] = 0;
		strRes = szMessage;

		va_end(arg_ptr);
		return strRes;
	}

	std::wstring construct_url(LPCWSTR pszFormat, ...)
	{
		std::wstring strRes = L"";

		va_list arg_ptr;
		va_start(arg_ptr, pszFormat);
		WCHAR szMessage[STRING_BUFFER_LEN] = { 0 };
		_vsnwprintf_s(szMessage, STRING_BUFFER_LEN, STRING_BUFFER_LEN - 1, pszFormat, arg_ptr);
		szMessage[STRING_BUFFER_LEN - 1] = 0;
		va_end(arg_ptr);

		CString str;
		if (_wcsnicmp(szMessage, L"http://", wcslen(L"http://")) == 0)
		{
			str = szMessage + wcslen(L"http://");
			str.Replace(L"//", L"/");
			strRes = L"http://";
			strRes.append(str.GetBuffer());
			return strRes;
		}
		else
		{
			str = szMessage;
			str.Replace(L"//", L"/");
			strRes = str.GetBuffer();
			return strRes;
		}
	}

	std::wstring str2wstr(const std::string& strA)
	{
		size_t nLen = strA.length() + 1;
		WCHAR *szWchar = new WCHAR[nLen];
		size_t nRet = MultiByteToWideChar(CP_ACP, 0, strA.c_str(), -1, szWchar, (int)nLen);
		if (nRet == 0)
		{
			ZeroMemory(szWchar, (nLen)*sizeof(WCHAR));
			delete[] szWchar;
			return L"";
		}

		if ((int)nRet <= nLen)
			szWchar[nLen - 1] = L'\0';
		else
			ZeroMemory(szWchar, (nLen)*sizeof(WCHAR));

		std::wstring strW(szWchar);
		delete[] szWchar;
		return strW;
	}

	std::string  wstr2str(const std::wstring& strW)
	{
		size_t nLen = strW.length() + 1;
		CHAR *szChar = new CHAR[nLen];
		size_t nRet = WideCharToMultiByte(CP_ACP, 0, strW.c_str(), -1, szChar, (int)nLen, 0, NULL);

		if (nRet == 0)
		{
			ZeroMemory(szChar, (nLen)*sizeof(CHAR));
			delete[] szChar;
			return "";
		}

		if ((int)nRet <= nLen)
			szChar[nLen - 1] = L'\0';
		else
			ZeroMemory(szChar, (nLen)*sizeof(CHAR));

		std::string strA(szChar);
		delete[] szChar;
		return strA;
	}

	bool same_str(const std::wstring& str1, const std::wstring& str2, bool bCaseSensitive/*=false*/)
	{
		if (bCaseSensitive)
			return wcscmp(str1.c_str(), str2.c_str()) == 0;
		else
			return _wcsicmp(str1.c_str(), str2.c_str()) == 0;
	}

	std::wstring guid2str(GUID guid)
	{
		std::wstring strGuid;
		RPC_WSTR pszGuid;
		::UuidToString(&guid, &pszGuid);
		strGuid = std::wstring((LPCWSTR)pszGuid);
		::RpcStringFree(&pszGuid);
		return strGuid;
	}

	void split_str(const std::wstring& str, wchar_t chSpliter, std::vector<wstring>& vecStrings)
	{
		std::wstring strSubStr = str;
		while (strSubStr.length() != 0)
		{
			std::wstring::size_type pos = strSubStr.find_first_of(chSpliter);
			if (pos != std::wstring::npos)
			{
				std::wstring s = strSubStr.substr(0, pos);
				strSubStr = strSubStr.substr(pos + 1);
				if (s.length() > 0)
					vecStrings.push_back(s);
			}
			else
			{
				vecStrings.push_back(strSubStr);
				break;
			}
		}
	}

	void replace_str(std::wstring& str, const std::wstring& strOld, const std::wstring& strNew)
	{
		if (strOld.empty() || str.empty())
			return;

		if (same_str(strOld, strNew, true))
			return;

		std::wstring::size_type pos = str.find(strOld, 0);
		while (pos != std::wstring::npos)
		{
			str.replace(str.begin() + pos, str.begin() + pos + strOld.length(), strNew.begin(), strNew.end());
			pos += strNew.length();
			pos = pos = str.find(strOld, pos);
		}
	}

	bool str2boolean(const wstring& str, bool bDefault )
	{
		if (str.empty())
			return bDefault;

		if (same_str(str, L"0") || same_str(str, L"false") || same_str(str, L"no") || same_str(str, L"not"))
			return false;
		return true;
	}

	void trim_str(std::wstring& str, wchar_t ch/*=L' '*/)
	{
		std::wstring::size_type pos = str.find_last_not_of(ch);
		if (pos != std::wstring::npos) {
			str.erase(pos + 1);
			pos = str.find_first_not_of(ch);
			if (pos != std::wstring::npos) str.erase(0, pos);
		}
		else
			str.erase(str.begin(), str.end());
	}

	void trim_str(std::string& str, char ch/*=L' '*/)
	{
		std::string::size_type pos = str.find_last_not_of(ch);
		if (pos != std::string::npos) {
			str.erase(pos + 1);
			pos = str.find_first_not_of(ch);
			if (pos != std::string::npos) str.erase(0, pos);
		}
		else
			str.erase(str.begin(), str.end());
	}

	void trim_str_right(std::wstring& str, wchar_t ch/*=L' '*/)
	{
		std::wstring::size_type pos = str.find_last_not_of(ch);
		if (pos != std::wstring::npos) {
			str.erase(pos + 1);
		}
		else
			str.erase(str.begin(), str.end());
	}

	void trim_str_right(std::string& str, char ch/*=L' '*/)
	{
		std::string::size_type pos = str.find_last_not_of(ch);
		if (pos != std::string::npos) {
			str.erase(pos + 1);
		}
		else
			str.erase(str.begin(), str.end());
	}

	void trim_str_left(std::wstring& str, wchar_t ch/*=L' '*/)
	{
		std::wstring::size_type pos = str.find_first_not_of(ch);
		if (pos != std::wstring::npos) {
			str.erase(0, pos);
		}
	}

	void trim_str_left(std::string& str, char ch/*=L' '*/)
	{
		std::string::size_type pos = str.find_first_not_of(ch);
		if (pos != std::string::npos) {
			str.erase(0, pos);
		}
	}
}