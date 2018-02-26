/////////////////////////////////////////////////////////////////////////////
// BABCrypt.h		
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#include "bebenabled.h"

#define PWDMAXLEN _MAX_PATH
#define ENPWDMAXLEN 6*_MAX_PATH

#ifdef __cplusplus
extern "C" {
#endif  /* __cplusplus */

int WINAPI ASET_BABEncrypt(TCHAR *pInData, unsigned long InDataLen, TCHAR *pOutData, unsigned long *pOutDataBufSize);
int WINAPI ASET_BABDecrypt(TCHAR *pInData, unsigned long InDataLen, TCHAR *pOutData, unsigned long *pOutDataBufSize);

int WINAPI ASET_BABEncrypt_NUMSTR(TCHAR *pInData, unsigned long InDataLen, TCHAR *pOutData, unsigned long *pOutDataBufSize);
int WINAPI ASET_BABDecrypt_NUMSTR(TCHAR *pInData, unsigned long InDataLen, TCHAR *pOutData, unsigned long *pOutDataBufSize);

BOOL WINAPI ASET_Is3DESEncrypt(LPCTSTR lpszICFFile);

#ifdef __cplusplus
}
#endif