/////////////////////////////////////////////////////////////////////////////
// Crypt.h
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.
#include "BABCrypt.h"

#ifndef _CRYPT_H
#define _CRYPT_H

#ifdef __cplusplus
extern "C" {
#endif  /* __cplusplus */

void Crypt(LPVOID lpBuffer, ULONG uSize);

BOOL GetEncrytPsw(LPTSTR lpszPassword);
BOOL GetDecryptPsw(LPTSTR lpszPassword);

BOOL Encrypt(LPTSTR lpBuffer, DWORD &dwSize);
BOOL Decrypt(LPTSTR lpBuffer, DWORD &dwSize);

BOOL GetEncryptPsw3DES(LPCTSTR lpszPassword,LPTSTR lpszOut);
BOOL GetDecryptPsw3DES(LPCTSTR lpszPassword,LPTSTR lpszOut);

#ifdef __cplusplus
}
#endif

#endif