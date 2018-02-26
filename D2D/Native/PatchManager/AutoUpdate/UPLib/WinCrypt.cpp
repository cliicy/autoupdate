#include "stdafx.h"
#include "WinCrypt.h"
#include <atlenc.h>
#include "UpLib.h"

CWinCrypt::CWinCrypt()
{
	m_hProv = NULL;
	m_hHash = NULL;
	m_hKey = NULL;
	m_nProv = PROV_RSA_AES;
	m_nALG_ID = CALG_AES_256;
}

CWinCrypt::~CWinCrypt()
{
	Close();
}

int CWinCrypt::InitW(wchar_t* pszPassword)
{
	int nResult = 0;
	BOOL bResult = FALSE;

	do {
		if (NULL == pszPassword ||
			L'\0' == *pszPassword)
		{
			pszPassword = _T("Please input a valid password");
		}

		// Get provider.
		//bResult = CryptAcquireContext(&m_hProv,NULL,NULL,m_nProv,0);
		bResult = CryptAcquireContext(&m_hProv, NULL, NULL, m_nProv, CRYPT_VERIFYCONTEXT);
		if (!bResult)
		{
			if (GetLastError() == NTE_BAD_KEYSET)
			{
				bResult = CryptAcquireContext(&m_hProv, NULL, NULL, m_nProv, CRYPT_NEWKEYSET | CRYPT_VERIFYCONTEXT);
				if (!bResult)
				{
					nResult = -1;
					break;
				}
			}
			else
			{
				nResult = -1;
				break;
			}
		}

		// Get hash
		bResult = CryptCreateHash(m_hProv, CALG_MD5, NULL, 0, &m_hHash);
		if (!bResult)
		{
			nResult = -2;
			break;
		}

		bResult = CryptHashData(m_hHash, (BYTE *)pszPassword, (DWORD)_tcslen(pszPassword) * sizeof(TCHAR), 0);
		if (!bResult)
		{
			nResult = -3;
			break;
		}

		// get key
		bResult = CryptDeriveKey(m_hProv, m_nALG_ID, m_hHash, 0x01000000 | CRYPT_CREATE_SALT, &m_hKey);
		if (!bResult)
		{
			nResult = -4;
			break;
		}
	} while (0);


	return nResult;
}

int CWinCrypt::Close()
{
	if (NULL != m_hKey)
	{
		CryptDestroyKey(m_hKey);
	}
	if (NULL != m_hHash)
	{
		CryptDestroyHash(m_hHash);
	}
	if (NULL != m_hProv)
	{
		CryptReleaseContext(m_hProv, 0);
	}
	m_hKey = NULL;
	m_hHash = NULL;
	m_hProv = NULL;

	return 0;
}

int CWinCrypt::Encrypt(LPVOID pData, DWORD* pdwDataSize, DWORD dwBufSize, BOOL bFinal)
{
	int nResult = 0;
	BOOL bResult = FALSE;

	do {
		if (NULL == m_hKey
			|| NULL == pdwDataSize)
		{
			nResult = -1;
			break;
		}

		// encrypt
		bResult = CryptEncrypt(m_hKey, NULL, bFinal, 0, (BYTE*)pData, pdwDataSize, dwBufSize);
		if (!bResult)
		{
			nResult = -2;
			break;
		}

	} while (0);

	return nResult;
}

int CWinCrypt::Decrypt(LPVOID pData, DWORD* pdwDataSize, BOOL bFinal)
{
	int nResult = 0;
	BOOL bResult = FALSE;

	do {
		if (NULL == m_hKey
			|| NULL == pData
			|| NULL == pdwDataSize)
		{
			nResult = -1;
			break;
		}

		// decrypt
		bResult = CryptDecrypt(m_hKey, NULL, bFinal, 0, (BYTE*)pData, pdwDataSize);
		if (!bResult)
		{
			nResult = -5;
			break;
		}
	} while (0);

	return nResult;
}

int CWinCrypt::GetALG(DWORD* pnProv, DWORD* pnALG)
{
	if (NULL != pnProv)
	{
		*pnProv = m_nProv;
	}
	if (NULL != pnALG)
	{
		*pnALG = m_nALG_ID;
	}

	return 0;
}

int CWinCrypt::GetPasswordHash(LPVOID pvHash, DWORD* pdwHashBuf)
{
	int rc = 0;

	do
	{
		if (NULL == pvHash
			|| NULL == *pdwHashBuf)
		{
			rc = -1;
			break;
		}

		if (!CryptSignHash(m_hHash, AT_SIGNATURE, NULL, 0, (BYTE*)pvHash, pdwHashBuf))
		{
			rc = -11;
			break;
		}
	} while (0);

	return rc;
}

// -------------------------------------------------------------------------------------------
// 
//
// helper funciton set 2
static int getEncHeader(CWinCrypt* pwc, PAF_ENC_HEADER pAFEncHeader, DWORD dwDataSize)
{
	int rc = 0;

	do
	{
		if (NULL == pwc
			|| NULL == pAFEncHeader)
		{
			rc = -1;
			break;
		}

		memset(pAFEncHeader, 0, sizeof(AF_ENC_HEADER));
		pAFEncHeader->nSignature = AF_ENC_SIGNATURE;
		pAFEncHeader->nVersion = AF_ENC_VERSION;
		pwc->GetALG(&pAFEncHeader->nProvider, &pAFEncHeader->nALG_ID);
		pAFEncHeader->nDataSize = dwDataSize;
		DWORD dwHashBuf = (DWORD)sizeof(pAFEncHeader->nKeyID);
		pwc->GetPasswordHash(pAFEncHeader->nKeyID, &dwHashBuf);

	} while (0);

	return rc;
}

static DWORD copyCharToWChar(const CHAR *ptr, WCHAR *wptr, int len, UINT uCodePage)
{
	int iSrc = 0;
	size_t uLen = 0;
	DWORD dwFlags = MB_PRECOMPOSED;

	//if len == 0, always return needed buffer len
	if (!wptr && len)
	{
		SetLastError(ERROR_INVALID_PARAMETER);
		return 0;
	}

	if (!ptr || !ptr[0])
	{
		if (wptr) *wptr = 0;
		SetLastError(ERROR_INVALID_PARAMETER);
		return 0;
	}

	if (uCodePage == 50220 || uCodePage == 50221 || uCodePage == 50222 || uCodePage == 50225 ||
		uCodePage == 50227 || uCodePage == 50229 || uCodePage == 52936 || uCodePage == 54936 ||
		(uCodePage >= 57002 && uCodePage <= 57011) ||
		uCodePage == 65000 || uCodePage == 65001 || uCodePage == 42)
	{
		dwFlags = 0;
	}

	//Need actual length of buffer
	uLen = (size_t)MultiByteToWideChar(uCodePage, dwFlags, ptr, -1, NULL, 0);

	if (len <= 0)
	{
		return (DWORD)uLen;
	}

	if ((size_t)len > uLen - 1)
	{
		iSrc = -1;
	}
	else
	{
		iSrc = len - 1;
		wptr[len - 1] = 0; //Make sure dest is 0 ended
	}

	return MultiByteToWideChar(uCodePage, dwFlags, ptr, iSrc, wptr, len);
}

static DWORD copyWCharToChar(const WCHAR *wptr, CHAR *ptr, int len, UINT uCodePage)
{
	int iSrc = 0;
	size_t uLen = 0;
	DWORD dwFlags = WC_COMPOSITECHECK | WC_SEPCHARS;

	//if len==0, always return needed buffer length
	if (!ptr && len)
	{
		SetLastError(ERROR_INVALID_PARAMETER);
		return 0;
	}

	if (!wptr || !wptr[0])
	{
		SetLastError(ERROR_INVALID_PARAMETER);
		if (ptr) *ptr = 0;
		return 0;
	}

	if (uCodePage == 50220 || uCodePage == 50221 || uCodePage == 50222 || uCodePage == 50225 ||
		uCodePage == 50227 || uCodePage == 50229 || uCodePage == 52936 || uCodePage == 54936 ||
		(uCodePage >= 57002 && uCodePage <= 57011) ||
		uCodePage == 65000 || uCodePage == 65001 || uCodePage == 42)
	{
		dwFlags = 0;
	}

	uLen = (size_t)WideCharToMultiByte(uCodePage, dwFlags, wptr, -1, NULL, 0, NULL, NULL);

	if (len <= 0)
	{
		return (DWORD)uLen;
	}

	if ((size_t)len > uLen - 1)
	{
		iSrc = -1;
	}
	else
	{
		iSrc = len - 1;
		ptr[len - 1] = 0; //Make sure dest is 0 ended
	}

	return WideCharToMultiByte(uCodePage, dwFlags, wptr, iSrc, ptr, len, NULL, NULL);
}


namespace ENCUTILS
{
	int EncryptData(void *pvData, DWORD* pnDataSize, DWORD nBufSize)
	{
		int rc = 0;
		void* pvTemp = NULL;

		do
		{
			if (NULL == pnDataSize)
			{
				rc = -1;
				break;
			}

			CWinCrypt wc;
			rc = wc.InitW(NULL);
			if (rc < 0)
			{
				rc = -11;
				break;
			}

			if (NULL == pvData)
			{
				rc = wc.Encrypt(NULL, pnDataSize, 0, TRUE);
				*pnDataSize += sizeof(AF_ENC_HEADER);
				break;
			}

			if (0 == *pnDataSize || *pnDataSize > nBufSize)
			{
				rc = -1;
				break;
			}

			DWORD nNeedSize = *pnDataSize;
			//DWORD nDataBufSize = (DWORD)(nBufSize - sizeof(AF_ENC_HEADER)); // lijle01 remove
			rc = wc.Encrypt(NULL, (DWORD*)&nNeedSize, 0, TRUE);
			if (rc < 0)
			{
				rc = -12;
				break;	// lijle01 add
			}
			if (nNeedSize + sizeof(AF_ENC_HEADER) > nBufSize)
			{
				*pnDataSize = nNeedSize + sizeof(AF_ENC_HEADER);
				rc = -16;
				break;
			}

			pvTemp = (void*) new char[nNeedSize];
			if (NULL == pvTemp)
			{
				rc = -13;
				break;
			}

			DWORD dwDataSize = *pnDataSize;
			memcpy(pvTemp, pvData, dwDataSize);
			rc = wc.Encrypt(pvTemp, &dwDataSize, nNeedSize, TRUE);
			if (rc < 0)
			{
				rc = -14;
				break;	// lijle01 add
			}

			PAF_ENC_HEADER pAFEncHeader = (PAF_ENC_HEADER)pvData;
			getEncHeader(&wc, pAFEncHeader, *pnDataSize);

			memcpy(pAFEncHeader + 1, pvTemp, dwDataSize);

			*pnDataSize = sizeof(AF_ENC_HEADER) + dwDataSize;

		} while (0);

		if (NULL != pvTemp)
		{
			delete[] pvTemp;
			pvTemp = NULL;
		}

		return rc;
	}

	int DecryptData(void *pvData, DWORD* pnDataSize)
	{
		int rc = 0;

		do
		{
			if (NULL == pvData
				|| NULL == pnDataSize)
			{
				rc = -1;
				break;
			}

			PAF_ENC_HEADER pAFEncHeader = (PAF_ENC_HEADER)pvData;
			DWORD nProv = 0;
			DWORD nALG = 0;
			CWinCrypt wc;

			// lijle01 add begin
			rc = wc.InitW(NULL);
			if (rc < 0)
			{
				rc = -11;
			}
			// lijle01 add end

			wc.GetALG(&nProv, &nALG);
			if (pAFEncHeader->nSignature != AF_ENC_SIGNATURE
				|| pAFEncHeader->nVersion != AF_ENC_VERSION
				|| pAFEncHeader->nProvider != nProv
				|| pAFEncHeader->nALG_ID != nALG)
			{
				rc = -2;
				break;
			}

			void* pvEncryptData = (void*)(pAFEncHeader + 1);
			DWORD dwDataSize = *pnDataSize - sizeof(AF_ENC_HEADER);
			rc = wc.Decrypt(pvEncryptData, &dwDataSize, TRUE);
			if (rc < 0)
			{
				rc = -11;
				break;
			}
			memcpy(pvData, pvEncryptData, dwDataSize);
			*pnDataSize = dwDataSize;

		} while (0);

		return rc;
	}


	BOOL EncryptToString(const wchar_t *pszStr, std::wstring& strOutput)
	{
		DWORD dwRet = 0;
		char *pEnBuf = NULL;
		size_t lenStr = 0;
		DWORD dwLen = 0;
		DWORD dwData = 0;

		if (pszStr && pszStr[0])
		{
			lenStr = wcslen(pszStr);

			dwLen = (DWORD)(lenStr * sizeof(wchar_t));

			dwData = (DWORD)(lenStr*sizeof(wchar_t));

			if (EncryptData(NULL, &dwLen, 0) < 0)
			{
				dwRet = GetLastError();
				SetLastError(dwRet);
				return FALSE;
			}

			pEnBuf = new char[dwLen];
			if (!pEnBuf)
			{
				dwRet = ERROR_NOT_ENOUGH_MEMORY;
				SetLastError(dwRet);
				return FALSE;
			}
			memset(pEnBuf, 0, dwLen);

			memcpy_s(pEnBuf, dwLen, pszStr, dwData);

			if (EncryptData(pEnBuf, &dwData, dwLen) < 0)
			{
				dwRet = GetLastError();
				//log.LogW(LL_ERR, dwRet, L"%s: Fail to AFEncryptData[%s]", __WFUNCTION__, pszBuf);
				SetLastError(dwRet);
				delete[] pEnBuf;
				pEnBuf = NULL;
				return FALSE;
			}
		}
		else
		{
			lenStr = MAX_PATH;
			dwLen = (DWORD)(lenStr * sizeof(wchar_t));
			dwData = (DWORD)(lenStr * sizeof(wchar_t));
			pEnBuf = new char[dwLen];
			if (!pEnBuf)
			{
				dwRet = ERROR_NOT_ENOUGH_MEMORY;
				SetLastError(dwRet);
				return FALSE;
			}
			memset(pEnBuf, 0, dwLen);
		}

		int iBufLen = Base64EncodeGetRequiredLength(dwData, 0);

		char * pBaseBuf = new char[iBufLen];
		if (!pBaseBuf)
		{
			dwRet = ERROR_NOT_ENOUGH_MEMORY;
			SetLastError(dwRet);
			delete[] pEnBuf;
			return FALSE;
		}
		memset(pBaseBuf, 0, iBufLen);

		if (!Base64Encode((const BYTE *)pEnBuf, dwData, pBaseBuf, &iBufLen, ATL_BASE64_FLAG_NOCRLF | ATL_BASE64_FLAG_NOPAD))
		{
			delete[] pEnBuf;
			delete[] pBaseBuf;
			return FALSE;
		}

		delete[] pEnBuf;
		pEnBuf = NULL;

		DWORD dwNeedLen = copyCharToWChar(pBaseBuf, NULL, 0, 0) + 1;
		WCHAR* pszOutBuf = new WCHAR[dwNeedLen];
		ZeroMemory(pszOutBuf, dwNeedLen*sizeof(WCHAR));

		copyCharToWChar(pBaseBuf, pszOutBuf, dwNeedLen, 0);
		delete[] pBaseBuf;
		pBaseBuf = NULL;

		strOutput = pszOutBuf;
		delete[] pszOutBuf;
		pszOutBuf = NULL;

		return TRUE;
	}

	//
	// decrypt a string by base64 encoded.
	//
	BOOL DecryptFromString(const wchar_t *pszStr, wstring& strOutput)
	{
		DWORD dwRet = 0;
		if (!pszStr || !pszStr[0])
		{
			dwRet = ERROR_INVALID_PARAMETER;
			SetLastError(dwRet);
			return FALSE;
		}

		size_t lenStr = wcslen(pszStr) + 1;
		DWORD dwStrLen = copyWCharToChar(pszStr, NULL, 0, 0) + 1;

		char *pBuf = new char[dwStrLen];
		if (!pBuf)
		{
			dwRet = ERROR_NOT_ENOUGH_MEMORY;
			SetLastError(dwRet);
			return FALSE;
		}

		memset(pBuf, 0, dwStrLen);

		copyWCharToChar(pszStr, pBuf, dwStrLen, 0);

		DWORD dwLen = (DWORD)Base64DecodeGetRequiredLength( (int)(strlen(pBuf)) ) + 1;

		char *pBaseBuf = new char[dwLen];
		if (!pBaseBuf)
		{
			dwRet = ERROR_NOT_ENOUGH_MEMORY;
			SetLastError(dwRet);
			delete[] pBuf;
			return FALSE;
		}

		if (!Base64Decode(pBuf, (int)(strlen(pBuf)), (BYTE *)pBaseBuf, (int *)&dwLen))
		{
			delete[] pBuf;
			delete[] pBaseBuf;
			return FALSE;
		}
		delete[] pBuf;

		if (pBaseBuf[0])
		{
			if (DecryptData(pBaseBuf, &dwLen) < 0)
			{
				dwRet = GetLastError();
				SetLastError(dwRet);
				delete[] pBaseBuf;
				return FALSE;
			}
		}

		DWORD dwSize = dwLen / sizeof(wchar_t) + 1;
		WCHAR* szOutBuf = new WCHAR[dwSize];
		ZeroMemory(szOutBuf, dwSize*sizeof(WCHAR));
		memcpy_s(szOutBuf, dwLen, pBaseBuf, dwLen);
		strOutput = szOutBuf;
		delete[] pBaseBuf;
		delete[] szOutBuf;
		return TRUE;
	}
}