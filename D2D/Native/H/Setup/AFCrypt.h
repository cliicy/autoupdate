#pragma once

int WINAPI AFDecryptData(void *pvData, DWORD* pnDataSize);

int WINAPI AFEncryptData(void *pvData, DWORD* pnDataSize, DWORD nBufSize);
/*
*Purpose: Encrypt string.
*@pszStr: [INPUT] input string which will be encrypted.
*@pszBuf: [OUTPUT] output buffer for encrypted string.
*@pBufLen: [INPUT, OUTPUT] For input, it is the length of output buffer in character.
                           For output, it is the length of the encrypted string in character.
*Note:
     If pszBuf is null or pBufLen is less than the length of encrypted string, pBufLen contains the length
     of encrypted string, and false will be returned. And GetLastError will return ERROR_NOT_ENOUGH_MEMORY
*/

BOOL WINAPI AFEncryptString(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen);

/*
*Purpose: Decrypt string.
*@pszStr: [INPUT] string which is encrpted.
*@pszBuf: [OUTPUT] string which is decrypted.
*@pBufLen: [INPUT, OUTPUT] For input, it is the length of the buffer in character.
                           For output, it is the length of decrypted string in character.
*Note:
     If pszBuf is null or pBufLen is less than the length of decrypted string, pBufLen contains the length
     of decrypted string, and false will be returned. And GetLastError will return ERROR_NOT_ENOUGH_MEMORY
*/
BOOL WINAPI AFDecryptString(const wchar_t *pszStr, wchar_t *pszBuf, DWORD *pBufLen);

