#pragma once
#define _CRYPT32_
#include <Windows.h>
#include <objbase.h>
#include <wincrypt.h>


#define AF_ENC_SIGNATURE	0x4146454E
#define	AF_ENC_VERSION		0x20090710
typedef struct _tagAFEncHeader
{
	DWORD	nSignature;
	DWORD	nVersion;
	DWORD	nProvider;
	DWORD	nALG_ID;

	DWORD	nDataSize;
	DWORD	nReserved[2];
	DWORD	nKeySize;

	unsigned char	nKeyID[32];

	unsigned char	bReserved[64];
} AF_ENC_HEADER, *PAF_ENC_HEADER;

class CWinCrypt
{
public:
	CWinCrypt();
	virtual ~CWinCrypt();

public:
	int Close();
	int InitW(wchar_t* pszPassword);
	int CWinCrypt::Encrypt(LPVOID pData, DWORD* pdwDataSize, DWORD dwBufSize, BOOL bFinal = FALSE);
	int Decrypt(LPVOID pvData, DWORD* pdwDataSize, BOOL bFinal = FALSE);
	int GetALG(DWORD* pnProv, DWORD* pnALG);
	int GetPasswordHash(LPVOID pvHash, DWORD* pdwHashBuf);
protected:
	HCRYPTPROV m_hProv;
	HCRYPTHASH m_hHash;
	HCRYPTKEY m_hKey;
	DWORD m_nProv;
	DWORD m_nALG_ID;
};
