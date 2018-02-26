#pragma once

typedef struct _Native_ASAG_CRC
{
	BOOL				CRCTableInitialized;
	BOOL				VerifyCRC;
	DWORD				CRCTable[256];
	DWORD				chkSum;

	VOID				BuildCRCTable();
	VOID				CSSCheckSum(PCHAR buffer, DWORD count);
	VOID				CSSCheckSum(PCHAR buffer, DWORD count, DWORD *pdwCRC);
	_Native_ASAG_CRC     ()    {CRCTableInitialized = FALSE;}
}	Native_ASAG_CRC;